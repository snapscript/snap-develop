package org.snapscript.studio.index;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.extern.slf4j.Slf4j;

import org.snapscript.core.Context;
import org.snapscript.studio.common.FileAction;
import org.snapscript.studio.common.FileProcessor;
import org.snapscript.studio.common.FileReader;
import org.snapscript.studio.index.classpath.BootstrapClassPath;
import org.snapscript.studio.index.classpath.ClassPathSearcher;

@Slf4j
public class IndexScanner implements IndexDatabase {
   
   private static final Map<String, IndexNode> DEFAULT_IMPORTS = BootstrapClassPath.getDefaultImportNames();

   private final AtomicReference<IndexFileCache> reference;
   private final FileProcessor<IndexFile> processor;
   private final FileAction<IndexFile> action;
   private final IndexPathTranslator translator;
   private final ClassPathSearcher searcher;
   private final Indexer indexer;
   private final String project;
   private final File root;
   
   public IndexScanner(ClassLoader loader, Context context, Executor executor, File root, String project, String... prefixes) {
      this.reference = new AtomicReference<IndexFileCache>();
      this.searcher = new ClassPathSearcher(loader);
      this.translator = new IndexPathTranslator(prefixes);
      this.indexer = new Indexer(translator, this, context, executor, root);
      this.action = new CompileAction(indexer, root);
      this.processor = new FileProcessor<IndexFile>(action, executor);
      this.project = project;
      this.root = root;
   }

   @Override
   public Map<String, IndexFile> getFiles() throws Exception {
      IndexFileCache cache = reference.get();
      
      if(cache == null || cache.isExpired()) {
         String path = root.getCanonicalPath();
         Set<IndexFile> results = processor.process(project, path + "/**.snap"); // build all resources
   
         if(!results.isEmpty()) {
            Map<String, IndexFile> matches = new HashMap<String, IndexFile>();
            
            for(IndexFile file : results) {
               String resource = file.getRealPath();
               matches.put(resource, file);
            }
            cache = new IndexFileCache(matches);
         } else {
            cache = new IndexFileCache(Collections.EMPTY_MAP);
         }
         reference.set(cache);
      }
      return cache.getFiles();
   }
   
   @Override
   public Map<String, IndexNode> getTypeNodes() throws Exception {
      Map<String, IndexFile> files = getFiles();
      Collection<IndexFile> list = files.values();
      
      if(!files.isEmpty()) {
         Map<String, IndexNode> matches = new HashMap<String, IndexNode>();
         
         for(IndexFile file : list) {
            Map<String, IndexNode> nodes = file.getTypeNodes();
            Set<Entry<String, IndexNode>> entries = nodes.entrySet();
            
            for(Entry<String, IndexNode> entry : entries) {
               IndexNode node = entry.getValue();
               String name = node.getFullName();
               
               if(name != null) {
                  IndexType type = node.getType();
                  
                  if(!type.isImport()) {
                     matches.put(name, node);
                  }
               }
            }
         }
         matches.putAll(searcher.getTypeNodes()); // add project types and bootstrap types
         return Collections.unmodifiableMap(matches);
      }
      return searcher.getTypeNodes();
   }

   @Override
   public IndexNode getTypeNode(String typeName) throws Exception {
      Map<String, IndexNode> nodes = getTypeNodes();
      
      if(!nodes.isEmpty()) {
         return nodes.get(typeName);
      }
      return null;
   }
   
   @Override
   public Map<String, IndexNode> getTypeNodesMatching(String expression) throws Exception {
      return getTypeNodesMatching(expression, false);
   }
   
   @Override
   public Map<String, IndexNode> getTypeNodesMatching(String expression, boolean ignoreCase) throws Exception {
      Map<String, IndexNode> nodes = getTypeNodes();
      Set<Entry<String, IndexNode>> entries = nodes.entrySet();
      Pattern pattern = Pattern.compile(expression, ignoreCase ? Pattern.CASE_INSENSITIVE : 0);
      
      if(!nodes.isEmpty()) {
         Map<String, IndexNode> matches = new TreeMap<String, IndexNode>();
         
         for(Entry<String, IndexNode> entry : entries) {
            IndexNode node = entry.getValue();
            String name = node.getName();
            
            if(name != null) {
               String fullName = node.getFullName();
               IndexType type = node.getType();
               Matcher matcher = pattern.matcher(name);
               
               if(matcher.matches() && !type.isImport()) {
                  matches.put(fullName, node);
               }
            }
         }
         return matches;
      }
      return Collections.emptyMap();
   }

   @Override
   public IndexNode getDefaultImport(String module, String name) throws Exception {
      IndexNode node = getTypeNode(module + "." + name);
      
      if(node == null) {
         return BootstrapClassPath.getDefaultImportClasses().get(name);
      }
      return node;
   }

   @Override
   public IndexFile getFile(String resource, String source) throws Exception {
      return indexer.index(resource, source);
   }
   

   public Map<String, IndexNode> getNodesInScope(IndexNode node) {
      Map<String, IndexNode> scope = new HashMap<String, IndexNode>(DEFAULT_IMPORTS);
      Set<IndexNode> enclosing = new HashSet<IndexNode>();
      
      while(node != null) {
         Set<IndexNode> nodes = node.getNodes();
         
         for(IndexNode entry : nodes) {
            String name = entry.getName();
            IndexType type = entry.getType();
            Map<String, IndexNode> children = getChildNodes(entry);
            
            if(!type.isSuper()) {
               scope.put(name, entry);
               scope.putAll(children);
            }
         }
         IndexType type = node.getType();
         
         if(type.isType()) {
            enclosing.add(node);
         }
         node = node.getParent();
      }
      Map<String, IndexNode> inherited = getMemberNodes(enclosing); // get enclosing methods
      
      if(!inherited.isEmpty()) {
         scope.putAll(inherited);
      }
      return scope;
   }
   
   private Map<String, IndexNode> getMemberNodes(Set<IndexNode> enclosing) {
      Map<String, IndexNode> result = new HashMap<String, IndexNode>();
      
      try{
         Map<String, IndexNode> types = getTypeNodes();
         
         for(IndexNode entry : enclosing) {
            Set<IndexNode> members = getMemberNodes(entry, types);
            
            for(IndexNode member : members) {
               String name = member.getName();
               result.put(name, member);
            }
         }
      }catch(Throwable cause) {
         log.info("Could not get members", cause);
      }
      return result;
   }
   
   private static Set<IndexNode> getMemberNodes(IndexNode node,  Map<String, IndexNode> types) {
      Set<IndexNode> nodes = new HashSet<IndexNode>();
      Map<String, IndexNode> hierarchy = new HashMap<String, IndexNode>();
      
      try{
         collectHierarchy(node, types, hierarchy);
         
         for(IndexNode entry : hierarchy.values()) {
            Set<IndexNode> children = entry.getNodes();
            
            for(IndexNode child : children) {
               if(child.isPublic()) {
                  IndexType type = child.getType();
                  
                  if(type.isFunction() || type.isProperty()) {
                     nodes.add(child);
                  }
               }
            }
         }
      }catch(Throwable cause) {
         log.info("Could not get members", cause);
      }
      return nodes;
   }
   
   private static void collectHierarchy(IndexNode node, Map<String, IndexNode> types, Map<String, IndexNode> done) {
      try{
         String fullName = node.getFullName();
         
         if(done.put(fullName, node) == null) {
            Set<IndexNode> superAndInterfaces = getSuperTypeAndInterfaces(node);
            
            for(IndexNode baseNode : superAndInterfaces) {
               String baseNodeName = baseNode.getFullName();
               IndexNode realNode = types.get(baseNodeName);
               
               if(realNode == null) {
                  realNode = DEFAULT_IMPORTS.get(baseNodeName);
               }
               if(realNode == null) {
                  realNode = baseNode;
               }
               collectHierarchy(realNode, types, done);
            }
         }
      }catch(Throwable cause) {
         log.info("Could not get hierarchy", cause);
      }
   }
   
   private static Set<IndexNode> getSuperTypeAndInterfaces(IndexNode node) {
      Set<IndexNode> nodes = new HashSet<IndexNode>();
      
      try {
         Set<IndexNode> children = node.getNodes();
         
         for(IndexNode child : children) {
            IndexType type = child.getType();
            
            if(type.isSuper()) {
               nodes.add(child);
            }
         }
         return nodes; 
      }catch(Throwable cause) {
         log.info("Could not get super types", cause);
      }
      return nodes;
   }
   
   private static Map<String, IndexNode> getChildNodes(IndexNode node) {
      String name = node.getName();
      IndexType type = node.getType();
      
      if(type.isType()) {
         return getChildNodes(node, name);
      }
      return Collections.emptyMap();
   }
   
   private static Map<String, IndexNode> getChildNodes(IndexNode node, String prefix) {
      Set<IndexNode> nodes = node.getNodes(); 
      
      if(!nodes.isEmpty()) {
         Map<String, IndexNode> scope = new HashMap<String, IndexNode>();

         for(IndexNode entry : nodes) {
            IndexType type = entry.getType();
            String name = entry.getName();
            
            if(type.isConstructor()) {
               if(prefix == null) {
                  scope.put(name, entry);
               } else if(!name.startsWith(prefix)) {
                  scope.put(prefix + "." + name, entry); // i.e new SomeClass.InnerClass()
               } else {
                  scope.put(name, entry); // new SomeClass()
               }
            } else if(type.isType()) {
               Map<String, IndexNode> children = getChildNodes(entry, name);
               Collection<IndexNode> entries = children.values();
               
               for(IndexNode child : entries) {
                  String suffix = child.getName();
                  scope.put(prefix + "." + suffix, child);
               }
               scope.put(prefix + "." + name, entry);
            } 
         }
         return scope;
      }
      return Collections.emptyMap();
   }
   
   private static class IndexFileCache {
      
      private final Map<String, IndexFile> files;
      private final long created;
      private final long expiry;
      
      public IndexFileCache(Map<String, IndexFile> files) {
         this(files, 5000);
      }
      
      public IndexFileCache(Map<String, IndexFile> files, long expiry) {
         this.created = System.currentTimeMillis();
         this.files = files;
         this.expiry = expiry;
      }
      
      public Map<String, IndexFile> getFiles() {
         return files;
      }
      
      public boolean isExpired() {
         return (created + expiry) < System.currentTimeMillis();
      }
   }
   
   
   private static class CompileAction implements FileAction<IndexFile> {
   
      private final Indexer indexer;
      private final File root;
      
      public CompileAction(Indexer indexer, File root) {
         this.indexer = indexer;
         this.root = root;
      }
      
      @Override
      public IndexFile execute(String reference, File file) throws Exception {
         String rootPath = root.getCanonicalPath();
         String filePath = file.getCanonicalPath();
         String relativePath = filePath.replace(rootPath, "");
         String resourcePath = relativePath.replace(File.separatorChar, '/');
         
         if(!resourcePath.startsWith("/")) {
            resourcePath = "/" + resourcePath;
         }
         String source = FileReader.readText(file);
         
         return indexer.index(resourcePath, source);
      }
   }
}

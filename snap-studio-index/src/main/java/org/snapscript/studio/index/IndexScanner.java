package org.snapscript.studio.index;
import static org.snapscript.core.Reserved.IMPORT_FILE;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.Executor;

import org.snapscript.core.Context;
import org.snapscript.core.link.ImportPathResolver;
import org.snapscript.studio.common.FileAction;
import org.snapscript.studio.common.FileProcessor;
import org.snapscript.studio.common.FileReader;
import org.snapscript.studio.index.classpath.BootstrapClassPath;
import org.snapscript.studio.index.classpath.ClassIndexProcessor;
import org.snapscript.studio.index.classpath.ClassPathSearcher;

public class IndexScanner implements IndexDatabase {

   private final FileProcessor<IndexFile> processor;
   private final FileAction<IndexFile> action;
   private final IndexPathTranslator translator;
   private final ImportPathResolver resolver;
   private final ClassPathSearcher searcher;
   private final Indexer indexer;
   private final String project;
   private final File root;
   
   public IndexScanner(ClassLoader loader, Context context, Executor executor, File root, String project, String... prefixes) {
      this.searcher = new ClassPathSearcher(loader);
      this.translator = new IndexPathTranslator(prefixes);
      this.indexer = new Indexer(translator, this, context, executor, root);
      this.action = new CompileAction(indexer, root);
      this.processor = new FileProcessor<IndexFile>(action, executor);
      this.resolver = new ImportPathResolver(IMPORT_FILE);
      this.project = project;
      this.root = root;
   }

   @Override
   public Map<String, IndexFile> getFiles() throws Exception {
      String path = root.getCanonicalPath();
      Set<IndexFile> results = processor.process(project, path + "/**.snap"); // build all resources

      if(!results.isEmpty()) {
         Map<String, IndexFile> matches = new HashMap<String, IndexFile>();
         
         for(IndexFile file : results) {
            String resource = file.getRealPath();
            matches.put(resource, file);
         }
         return matches;
      }
      return Collections.emptyMap();
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
      Map<String, IndexNode> files = getTypeNodes();
      
      if(!files.isEmpty()) {
         return files.get(typeName);
      }
      return null;
   }
   
   @Override
   public Map<String, IndexNode> getTypeNodesMatching(String expression) throws Exception {
      Map<String, IndexNode> nodes = getTypeNodes();
      Set<Entry<String, IndexNode>> entries = nodes.entrySet();
      
      if(!nodes.isEmpty()) {
         Map<String, IndexNode> matches = new TreeMap<String, IndexNode>();
         
         for(Entry<String, IndexNode> entry : entries) {
            IndexNode node = entry.getValue();
            String name = node.getName();
            
            if(name != null) {
               String fullName = node.getFullName();
               IndexType type = node.getType();
               
               if(name.matches(expression) && !type.isImport()) {
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

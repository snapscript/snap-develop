package org.snapscript.develop.common;

import static org.snapscript.tree.Instruction.SCRIPT;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.snapscript.agent.log.ProcessLogger;
import org.snapscript.common.store.FileStore;
import org.snapscript.common.store.Store;
import org.snapscript.compile.Compiler;
import org.snapscript.compile.Executable;
import org.snapscript.compile.StoreContext;
import org.snapscript.compile.StringCompiler;
import org.snapscript.core.Context;
import org.snapscript.core.EmptyModel;
import org.snapscript.core.FilePathConverter;
import org.snapscript.core.Model;
import org.snapscript.core.Module;
import org.snapscript.core.ModuleRegistry;
import org.snapscript.core.Path;
import org.snapscript.core.PathConverter;
import org.snapscript.core.Scope;
import org.snapscript.core.ScopeMerger;
import org.snapscript.core.Type;
import org.snapscript.core.link.Package;
import org.snapscript.core.link.PackageDefinition;
import org.snapscript.core.link.PackageLinker;
import org.snapscript.develop.configuration.ClassPathExecutor;
import org.snapscript.develop.configuration.ConfigurationClassLoader;

public class ResourceTypeLoader {
   
   private static final String MODULE_NAME_PATTERN = "^[a-zA-Z0-9\\.]+\\.([a-zA-Z0-9]+)$";
   private static final String IMPORT_ALIAS_PATTERN = "^import\\s+(.*)\\s+as\\s+(.*);.*";
   private static final String IMPORT_PATTERN = "^import (.*);.*";
   
   private final PathConverter converter;
   private final ProcessLogger logger;
   private final Executor executor;
   
   public ResourceTypeLoader(ConfigurationClassLoader loader, ProcessLogger logger) {
      this.executor = new ClassPathExecutor(loader, 6);
      this.converter = new FilePathConverter();
      this.logger = logger;
   }
   
   public Map<String, TypeNode> compileSource(File root, String resource, String source) {
      return compileSource(root, resource, source, -1);
   }
   
   public Map<String, TypeNode> compileSource(File root, String resource, String source, int line) {
      return compileSource(root, resource, source, line, false);
   }
   
   public Map<String, TypeNode> compileSource(File root, String resource, String source, int line, boolean aliases) {
      Map<String, TypeNode> types = new HashMap<String, TypeNode>();
      Model model = new EmptyModel();
      Store store = new FileStore(root);
      Context context = new StoreContext(store, executor);
      Compiler compiler = new StringCompiler(context);
      ScopeMerger merger = new ScopeMerger(context);
      ModuleRegistry registry = context.getRegistry();
      String current = converter.createModule(resource);
      Path path = converter.createPath(resource);
      String lines[] = source.split("\\r?\\n");
      
      try {
         String lineSource = source;
         
         if(line != -1) {
            lineSource = excludeLine(lines, line);
         }
         PackageLinker linker = context.getLinker();
         Package library = linker.link(path, lineSource, SCRIPT.name);
         Scope scope = merger.merge(model, current, path);
         PackageDefinition definition = library.define(scope);
         
         definition.compile(scope, null); // this might be wrong, maybe null
      } catch(Exception e) {
         logger.info("Error compiling " + resource, e);
         
         try {
            String importSource = createImports(lines);
            Executable executable = compiler.compile(importSource);
            executable.execute();
         }catch(Exception fatal) {
            logger.info("Error compiling imports for " + resource, fatal);
         }
      }
      List<Module> modules = registry.getModules();
      
      for(Module imported : modules) {
        List<Type> accessible = imported.getTypes();
        String module = imported.getName();
        
        for(Type type : accessible) {
           String name = type.getName();

           if(name != null) {
              TypeNode value = new TypeNode(type, name);
              types.put(name, value);
           }
        }
        if(module != null){
           Pattern pattern = Pattern.compile(MODULE_NAME_PATTERN);
           Matcher matcher = pattern.matcher(module);
           String name = module;
           
           if(matcher.matches()) {
              name = matcher.group(1);
           }
           TypeNode value = new TypeNode(imported, name);
           types.put(module, value);
           types.put(name, value);
        }
      }
      if(aliases) {
         Map<String, String> imports = convertAliases(lines);
         Set<String> keys = imports.keySet();
         Module container = registry.getModule(current);
         
         for(String key : keys) {
            Type type = container.getType(key);
            
            if(type != null) {
               TypeNode value = new TypeNode(type, key);
               types.put(key, value);
            } else {
               Module module = container.getModule(key);
               
               if(module != null) {
                  TypeNode value = new TypeNode(module, key);
                  types.put(key, value);
               }
            }
         }
      }
      return types;
   }
   
   private Map<String, String> convertAliases(String[] lines) {
      Map<String, String> imports = new HashMap<String, String>();
      Pattern pattern = Pattern.compile(IMPORT_ALIAS_PATTERN);

      for(String line : lines) {
         String token = line.trim();
        
         if(token.startsWith("import ")) {
            Matcher matcher = pattern.matcher(token);
            
            if(matcher.matches()) {
               String type = matcher.group(1);
               String name = matcher.group(2);
               
               imports.put(name, type);
            }
         }
      }
      return imports;
   }
   
   private String excludeLine(String[] lines, int line) {
      StringBuilder builder = new StringBuilder();
      
      for(int i = 0; i < lines.length; i++){
         String token = lines[i];
         
         if(i != line) {
            builder.append(token);
            builder.append("\n");
         } else {
            builder.append("\n"); // empty line
         }
      }
      return builder.toString();
   }
   
   private String createImports(String[] lines) {
      List<String> imports = new ArrayList<String>();
      StringBuilder builder = new StringBuilder();
      Pattern pattern = Pattern.compile(IMPORT_PATTERN);

      for(String line : lines) {
         String token = line.trim();
        
         if(token.startsWith("import ")) {
            Matcher matcher = pattern.matcher(token);
            
            if(matcher.matches()) {
               String module = matcher.group(1);
               
               imports.add(module);
               builder.append(token);
               builder.append("\n");
            }
         }
      }
      return builder.toString();
   }
}
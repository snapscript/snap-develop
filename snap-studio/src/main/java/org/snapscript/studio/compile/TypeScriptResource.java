package org.snapscript.studio.compile;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.snapscript.studio.resource.Resource;
import org.snapscript.studio.resource.ResourceMatcher;

public class TypeScriptResource implements Resource {

   private final TypeScriptCompiler compiler;
   private final ResourceMatcher matcher;
   private final List<File> outputDirs;
   private final List<String> sourceFiles;
   private final File typescriptDir;
   
   public TypeScriptResource(TypeScriptCompiler compiler, ResourceMatcher matcher, File typescriptDir, List<File> outputDirs, List<String> sourceFiles) {
      this.compiler = compiler;
      this.typescriptDir = typescriptDir;
      this.outputDirs = outputDirs;
      this.sourceFiles = sourceFiles;
      this.matcher = matcher;
   }

   @Override
   public void handle(Request request, Response response) throws Throwable {
      if(typescriptDir.exists()) {
         for(File outputDir : outputDirs) {
            compiler.compile(typescriptDir, outputDir, sourceFiles);
         }
      }
      Resource resource = matcher.match(request, response);
      
      if(resource == null) {
         throw new IOException("Could not match " + request);
      }
      resource.handle(request, response);
   }

}
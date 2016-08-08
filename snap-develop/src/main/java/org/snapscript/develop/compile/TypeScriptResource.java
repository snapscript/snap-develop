package org.snapscript.develop.compile;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.snapscript.develop.http.resource.FileMatcher;
import org.snapscript.develop.http.resource.Resource;

public class TypeScriptResource implements Resource {

   private final TypeScriptCompiler compiler;
   private final FileMatcher matcher;
   private final List<File> outputDirs;
   private final File sourceDir;
   
   public TypeScriptResource(TypeScriptCompiler compiler, FileMatcher matcher, File sourceDir, List<File> outputDirs) {
      this.compiler = compiler;
      this.sourceDir = sourceDir;
      this.outputDirs = outputDirs;
      this.matcher = matcher;
   }

   @Override
   public void handle(Request request, Response response) throws Throwable {
      if(sourceDir.exists()) {
         for(File outputDir : outputDirs) {
            compiler.compile(sourceDir, outputDir);
         }
      }
      Resource resource = matcher.match(request, response);
      
      if(resource == null) {
         throw new IOException("Could not match " + request);
      }
      resource.handle(request, response);
   }

}

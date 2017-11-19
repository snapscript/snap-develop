package org.snapscript.studio.compile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.snapscript.core.Bug;
import org.snapscript.studio.common.resource.Resource;
import org.snapscript.studio.common.resource.ResourcePath;
import org.snapscript.studio.common.resource.display.DisplayResourceMatcher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@ResourcePath("/.*.js")
public class TypeScriptResource implements Resource {

   private final DisplayResourceMatcher matcher;
   private final TypeScriptCompiler compiler;
   private final List<String> outputDirs;
   private final List<String> sourceFiles;
   private final File typescriptDir;
   
   @Bug("more rubbish again")
   public TypeScriptResource(
         TypeScriptCompiler compiler, 
         DisplayResourceMatcher matcher, 
         @Value("${typescript.dir:src/main/resources/resource/ts}") File typescriptDir, 
         @Value("${output.dirs:src/main/resources/resource/js,target/classes/resource/js}") String[] outputDirs, 
         @Value("${source.files:src/main/resources/resource/js/*.js}") String[] sourceFiles) 
   {
      this.outputDirs = Arrays.asList(outputDirs);
      this.sourceFiles = Arrays.asList(sourceFiles);
      this.compiler = compiler;
      this.typescriptDir = typescriptDir;
      this.matcher = matcher;
   }

   @Override
   public void handle(Request request, Response response) throws Throwable {
      if(typescriptDir.getAbsoluteFile().exists()) {
         for(String outputDir : outputDirs) {
            compiler.compile(typescriptDir.getCanonicalFile(), 
                             new File(outputDir).getCanonicalFile(), 
                             sourceFiles);
         }
      }
      Resource resource = matcher.match(request, response);
      
      if(resource == null) {
         throw new IOException("Could not match " + request);
      }
      resource.handle(request, response);
   }

}
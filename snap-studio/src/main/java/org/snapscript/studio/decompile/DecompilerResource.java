package org.snapscript.studio.decompile;

import java.io.PrintStream;

import org.simpleframework.http.Path;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.snapscript.studio.common.resource.Resource;
import org.snapscript.studio.common.resource.ResourcePath;
import org.snapscript.studio.project.Workspace;
import org.snapscript.studio.project.decompile.Decompiler;
import org.springframework.stereotype.Component;

// /decompile/project/<jar-file>/<class-name>
@Component
@ResourcePath("/decompile/.*")
public class DecompilerResource implements Resource {

   private final Workspace workspace;
   
   public DecompilerResource(Workspace workspace) {
      this.workspace = workspace;
   }
   
   @Override
   public void handle(Request request, Response response) throws Throwable {
      Path path = request.getPath(); 
      String[] segments = path.getSegments();
      String className = path.getPath(segments.length -1); // remove leading slash
      String jarFile = path.getPath(2, segments.length-3);
      
      if(className.startsWith("/")) {
         className = className.substring(1);
      }
      if(className.endsWith(".java")) {
         int length = className.length();
         int chop = ".java".length();
         
         className = className.substring(0, length-chop);
      }
      if(jarFile.startsWith("/")) {
         jarFile = jarFile.substring(1);
      }
      Decompiler decompiler = workspace.getDecompiler();
      String source = decompiler.decompile(jarFile, className);
      PrintStream stream = response.getPrintStream();

      response.setContentType("text/plain");
      stream.print(source);
      stream.close();
   }

}

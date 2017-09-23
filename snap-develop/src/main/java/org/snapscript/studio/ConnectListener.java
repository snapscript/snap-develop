package org.snapscript.studio;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Collections;

import lombok.AllArgsConstructor;

import org.simpleframework.http.Path;
import org.snapscript.studio.command.CommandListener;
import org.snapscript.studio.command.ExecuteCommand;
import org.snapscript.studio.resource.project.Project;
import org.snapscript.studio.resource.project.ProjectBuilder;

@AllArgsConstructor
public class ConnectListener {

   private final ProjectBuilder builder;
   
   public void connect(CommandListener listener, Path path) {
      String script = CommandLineArgument.SCRIPT.getValue();
      
      if(script != null) {
         try {
            Project project = builder.createProject(path);
            File projectPath = project.getProjectPath();
            String projectName = project.getProjectName();
            File file = new File(projectPath, "/" + script);
            FileInputStream input = new FileInputStream(file);
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] chunk = new byte[1024];
            int count = 0;
            
            while((count = input.read(chunk))!=-1) {
               buffer.write(chunk, 0, count);
            }
            input.close();
            buffer.close();
            
            String source = buffer.toString("UTF-8");
            String system = System.getProperty("os.name");
            ExecuteCommand command = ExecuteCommand.builder()
                  .project(projectName)
                  .system(system)
                  .resource(script)
                  .source(source)
                  .breakpoints(Collections.EMPTY_MAP)
                  .build();
            
            listener.onExecute(command);
         } catch(Exception e) {
            e.printStackTrace();
         }
      }
   }
}
package org.snapscript.studio.resource.project;

import java.io.File;
import java.util.Set;

import org.simpleframework.http.Path;
import org.snapscript.common.thread.ThreadPool;
import org.snapscript.studio.common.FileAction;
import org.snapscript.studio.common.FileProcessor;
import org.snapscript.studio.common.FileReader;
import org.snapscript.studio.common.Problem;
import org.snapscript.studio.common.ProblemFinder;
import org.snapscript.studio.project.Project;
import org.snapscript.studio.project.Workspace;

public class ProjectProblemFinder {

   private final FileProcessor<Problem> processor;
   private final FileAction<Problem> action;
   private final Workspace workspace;
   
   public ProjectProblemFinder(Workspace workspace, ThreadPool pool) {
      this.action = new CompileAction(workspace);
      this.processor = new FileProcessor<Problem>(action, pool);
      this.workspace = workspace;
   }
   
   public Set<Problem> compileProject(Path path) throws Exception {
      Project project = workspace.createProject(path);
      String name = project.getProjectName();
      File directory = project.getProjectPath();
      String root = directory.getCanonicalPath();
      int length = root.length();
      
      if(root.endsWith("/")) {
         root = root.substring(0, length -1);
      }
      long start = System.currentTimeMillis();
      
      try {
         return processor.process(name, root + "/**.snap"); // build all resources
      } finally {
         long finish = System.currentTimeMillis();
         long duration = finish - start;
         
         if(workspace.getLogger().isTraceEnabled()) {
            workspace.getLogger().trace("Took " + duration + " ms to compile project " + name);
         }
      }
   }
   
   private static class CompileAction implements FileAction<Problem> {
   
      private final ProblemFinder finder;
      private final Workspace workspace;
      
      public CompileAction(Workspace workspace) {
         this.finder = new ProblemFinder();
         this.workspace = workspace;
      }
      
      @Override
      public Problem execute(String reference, File file) throws Exception {
         Project project = workspace.getProject(reference);
         String name = project.getProjectName();
         File root = project.getProjectPath();
         String rootPath = root.getCanonicalPath();
         String filePath = file.getCanonicalPath();
         String relativePath = filePath.replace(rootPath, "");
         String resourcePath = relativePath.replace(File.separatorChar, '/');
         
         if(!resourcePath.startsWith("/")) {
            resourcePath = "/" + resourcePath;
         }
         String source = FileReader.readText(file);
         
         if(workspace.getLogger().isTraceEnabled()) {
            workspace.getLogger().trace("Compiling " + resourcePath + " in project " + reference);
         }
         return finder.parse(name, resourcePath, source);
      }
   }
}
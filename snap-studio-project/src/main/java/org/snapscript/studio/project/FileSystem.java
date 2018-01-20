package org.snapscript.studio.project;

import java.io.File;

import org.simpleframework.http.Path;

public class FileSystem {

   private final Project project;
   
   public FileSystem(Project project) {
      this.project = project;
   }
   
   public File getFile(Path path) {
      String projectPath = path.getPath(2); // /<project-name>/<project-path> or /default/blah.snap
      File rootPath = project.getProjectPath();
      String realPath = projectPath.replace('/', File.separatorChar);
      return new File(rootPath, realPath);
   }
   
   public File getFile(String path) {
      File rootPath = project.getProjectPath();
      String realPath = path.replace('/', File.separatorChar);
      return new File(rootPath, realPath);
   }
   
   public void writeAsString(String path, String resource) throws Exception {
      byte[] octets = resource.getBytes("UTF-8");
      writeAsByteArray(path, octets);
   }
   
   public void writeAsByteArray(String path, byte[] resource) throws Exception {
      File rootPath = project.getProjectPath();
      FilePersister.writeAsByteArray(rootPath, path, resource);
   }
   
   public String readAsString(String path) throws Exception {
      File rootPath = project.getProjectPath();
      return FilePersister.readAsString(rootPath, path);
   }
   
   public byte[] readAsByteArray(String path) throws Exception {
      File rootPath = project.getProjectPath();
      return FilePersister.readAsByteArray(rootPath, path);
   }
   
   public FileData readFile(Path path) throws Exception {
      String projectPath = path.getPath(2); // /<project-name>/<project-path> or /default/blah.snap
      return readFile(projectPath);
   }
   
   public FileData readFile(String path) throws Exception {
      long time = System.currentTimeMillis();
      File rootPath = project.getProjectPath();
      String realPath = path.replace('/', File.separatorChar);
      File projectFile = new File(rootPath, realPath);
      
      if(projectFile.exists()) {
         return new FileData(this, path, projectFile, time);
      }          
      return new FileData(this, path, null, time);
   }
   
}
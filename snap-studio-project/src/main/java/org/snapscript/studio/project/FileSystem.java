package org.snapscript.studio.project;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.simpleframework.http.Path;
import org.snapscript.studio.common.ClassPathReader;

public class FileSystem {

   private final Project project;
   
   public FileSystem(Project project) {
      this.project = project;
   }
   
   public File getFile(Path path) {
      String projectPath = path.getPath(2); // /<project-name>/<project-path> or /default/blah.snap
      File sourcePath = project.getSourcePath();
      String realPath = projectPath.replace('/', File.separatorChar);
      return new File(sourcePath, realPath);
   }
   
   public File getFile(String path) {
      File sourcePath = project.getSourcePath();
      String realPath = path.replace('/', File.separatorChar);
      return new File(sourcePath, realPath);
   }
   
   public void writeAsString(String path, String resource) throws Exception {
      byte[] octets = resource.getBytes("UTF-8");
      writeAsByteArray(path, octets);
   }
   
   public void writeAsByteArray(String path, byte[] resource) throws Exception {
      File sourcePath = project.getSourcePath();
      String realPath = path.replace('/', File.separatorChar);
      File sourceFile = new File(sourcePath, realPath);
      FileOutputStream outputStream = new FileOutputStream(sourceFile);
      outputStream.write(resource);
      outputStream.close();
   }
   
   public String readAsString(String path) throws Exception {
      byte[] resource = readAsByteArray(path);
      return new String(resource, "UTF-8");
   }
   
   public byte[] readAsByteArray(String path) throws Exception {
      File sourcePath = project.getSourcePath();
      File rootPath = project.getProjectPath();
      String realPath = path.replace('/', File.separatorChar);
      File sourceFile = new File(sourcePath, realPath);
      File projectFile = new File(rootPath, realPath);
      ByteArrayOutputStream buffer = null;
      InputStream inputStream = null;
      
      if(sourceFile.exists()) {
         long length = sourceFile.length();
         buffer = new ByteArrayOutputStream((int)length);
         inputStream = new FileInputStream(sourceFile);
      } else if(projectFile.exists()) {
         long length = sourceFile.length();
         buffer = new ByteArrayOutputStream((int)length);
         inputStream = new FileInputStream(projectFile);
      } else {
         inputStream = ClassPathReader.class.getResourceAsStream(path);
      }
      byte[] chunk = new byte[8192];
      int count = 0;

      while((count = inputStream.read(chunk)) != -1) {
         buffer.write(chunk, 0, count);
      }
      inputStream.close();
      return buffer.toByteArray();
   }
   
   public FileData readFile(Path path) throws Exception {
      String projectPath = path.getPath(2); // /<project-name>/<project-path> or /default/blah.snap
      return readFile(projectPath);
   }
   
   public FileData readFile(String path) throws Exception {
      long time = System.currentTimeMillis();
      File sourcePath = project.getSourcePath();
      File rootPath = project.getProjectPath();
      String realPath = path.replace('/', File.separatorChar);
      File sourceFile = new File(sourcePath, realPath);
      File projectFile = new File(rootPath, realPath);
      
      if(sourceFile.exists()) {
         return new FileData(this, path, sourceFile, time);
      } else if(projectFile.exists()) {
         return new FileData(this, path, projectFile, time);
      }          
      return new FileData(this, path, null, time);
   }
   
}
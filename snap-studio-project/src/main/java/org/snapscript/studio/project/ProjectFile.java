package org.snapscript.studio.project;

import java.io.File;

public class ProjectFile {

   private final ProjectFileSystem fileSystem;
   private final String location;
   private final File file;
   private final long time;
   private String text;
   private byte[] data;
   
   public ProjectFile(ProjectFileSystem fileSystem, String location, File file, long time) {
      this.fileSystem = fileSystem;
      this.location = location;
      this.file = file;
      this.time = time;
   }
   
   public File getFile() {
      return file;
   }
   
   public byte[] getByteArray() {
      try {
         if(data == null) {
            data = fileSystem.readAsByteArray(location);
         }
      } catch(Exception e){
         throw new IllegalStateException("Could not encode " + location, e);
      }
      return data;
   }
   
   public String getString() {
      try {
         if(text == null) {
            text = fileSystem.readAsString(location);
         }
      } catch(Exception e){
         throw new IllegalStateException("Could not encode " + location, e);
      }
      return text;
   }
   
   public boolean isStale() {
      if(file != null) {
         return file.lastModified() > time;
      }
      return true;
   }
}
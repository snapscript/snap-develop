package org.snapscript.studio.find.file;

import java.io.File;

public class FileMatch implements Comparable<FileMatch> {

   private final String resource;
   private final String project;
   private final File file;
   private final String text;
   
   public FileMatch(String project, String resource, File file, String text) {
      this.resource = resource;
      this.project = project;
      this.file = file;
      this.text = text;
   }

   @Override
   public int compareTo(FileMatch other) {
      return resource.compareTo(other.resource);
   }
   
   public File getFile(){
      return file;
   }
   
   public String getText(){
      return text;
   }

   public String getProject() {
      return project;
   }
   
   public String getResource() {
      return resource;
   }

}
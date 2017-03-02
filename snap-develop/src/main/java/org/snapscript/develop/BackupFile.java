package org.snapscript.develop;

import java.io.File;
import java.util.Date;

public class BackupFile {

   private final File file;
   private final String path;
   private final Date date;
   private final String timeStamp;
   private final String project;
   
   public BackupFile(File file, String path, Date date, String timeStamp, String project) {
      this.timeStamp = timeStamp;
      this.project = project;
      this.file = file;
      this.path = path;
      this.date = date;
   }
   
   public String getProject(){
      return project;
   }
   
   public String getTimeStamp(){
      return timeStamp;
   }

   public File getFile() {
      return file;
   }

   public String getPath() {
      return path;
   }

   public Date getDate() {
      return date;
   }
}

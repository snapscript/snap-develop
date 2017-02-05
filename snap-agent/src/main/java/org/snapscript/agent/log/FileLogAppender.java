package org.snapscript.agent.log;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

public class FileLogAppender {

   private FileWriter appender;
   private PrintWriter writer;
   private File file;
   private boolean append;
   
   public FileLogAppender(File file) {
      this(file, true);
   }
   
   public FileLogAppender(File file, boolean append) {
      this.append = append;
      this.file = file;
   }
   
   public void append(Object text) throws Exception {
      append(text, null);
   }
   
   public void append(Object text, Throwable cause) throws Exception {
      if(!file.exists() || writer == null) {
         appender = new FileWriter(file, append);
         writer = new PrintWriter(appender);
      }
      writer.print(text);
     
      if(cause != null) {
         writer.print(": ");
         cause.printStackTrace(writer);
      } else {
         writer.println();
      }
      writer.flush();
   }
   
   public void close() throws Exception {
      if(writer != null) {
         writer.flush();
         writer.close();
         writer = null;
      }
   }
}

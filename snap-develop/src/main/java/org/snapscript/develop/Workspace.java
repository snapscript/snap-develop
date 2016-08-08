package org.snapscript.develop;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

public class Workspace {

   private final File root;
   
   public Workspace(File root){
      this.root = root;
   }
   
   public File create(String name) {
      File file = new File(root, name);
      
      try {
         File directory = file.getParentFile();
         
         if(!directory.exists()) {
            directory.mkdirs();
         }
         return file.getCanonicalFile();
      }catch(Exception e) {
         throw new IllegalStateException("Could not create directory " + file, e);
      }
   }
   
   public File create() {
      try {
         File directory = root.getCanonicalFile();
         
         if(!directory.exists()){
            if(!directory.mkdirs()) {
            throw new IllegalStateException("Could not build work directory " + directory);
            }
            File ignore = new File(directory, ".gitignore");
            OutputStream stream = new FileOutputStream(ignore);
            PrintStream print = new PrintStream(stream);
            print.println("/.temp/");
            print.close();
         }
         return directory;
      }catch(Exception e) {
         throw new IllegalStateException("Could not create directory " + root, e);
      }
   }
}

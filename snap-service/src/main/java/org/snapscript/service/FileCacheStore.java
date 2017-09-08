package org.snapscript.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

import org.snapscript.common.store.NotFoundException;
import org.snapscript.common.store.Store;
import org.snapscript.core.FilePathConverter;
import org.snapscript.core.Path;
import org.snapscript.core.PathConverter;

public class FileCacheStore implements Store {

   private static final long EXPIRY = TimeUnit.DAYS.toMillis(1);
   
   private PathConverter converter;
   private Store store;
   private Path script;
   private File temp;
   private String root;
   
   public FileCacheStore(Store store, Path script, String root) throws IOException {
      this.converter = new FilePathConverter();
      this.store = store;
      this.script = script;
      this.root = root;
   }

   @Override
   public InputStream getInputStream(String path) {
      CacheInputStream stream = getTempInputStream(path);
      
      if(stream == null) {
         try {
            InputStream remote = store.getInputStream(path);

            try {
               CacheOutputStream output = getTempOutputStream(path);
               
               if(output != null) {
                  try {
                     byte[] data = new byte[8192];
                     int count = 0;
                     
                     while((count = remote.read(data)) != -1){
                        output.write(data, 0, count);
                     }
                     System.out.println("downloaded: " + path + " to " + output.getFile());
                     output.close();
                  } finally {
                     remote.close();
                  }
                  return getTempInputStream(path);
               }
            }catch(Exception e) {
               throw new IllegalStateException("Could not process '" + path + "' from '" + root + "'", e);
            }
         } catch(NotFoundException e) {
            OutputStream output = getTempOutputStream(path); // save cache miss
            
            if(output != null) {
               try {
                  output.close();
               }catch(Exception ex) {
                  return null;
               }
            }
            throw e;
         }
      }
      System.out.println("Loading " + path + " from "+ stream.getFile());
      
      if(stream != null && stream.isFailure()) {
         throw new NotFoundException("Could not find resource '" + path + "' from '" + root + "'");
      }
      return stream;
   }

   @Override
   public OutputStream getOutputStream(String path) {
      return store.getOutputStream(path);
   }
   
   private CacheOutputStream getTempOutputStream(String path) {
      try {
         File file = getTempFile(path);
         File parent = file.getParentFile();
         
         if(!parent.exists()) {
            parent.mkdirs();
         }
         return new CacheOutputStream(new FileOutputStream(file), file);
      } catch(Exception e) {
         return null;
      }
   }
   
   private CacheInputStream getTempInputStream(String path) {
      try {
         File file = getTempFile(path);
         
         if(file.exists()) {
            long lastModified = System.currentTimeMillis();
            long currentTime = System.currentTimeMillis();
            
            if(lastModified + EXPIRY > currentTime) {
               InputStream stream = new FileInputStream(file);
               return new CacheInputStream(stream, file);
            }
            file.delete();
         }
      } catch(Exception e) {
         return null;
      }
      return null;
   }
   
   private File getTempFile(String path) {
      if(temp == null) {
         String directory = System.getProperty("java.io.tmpdir");
         String folder = script.getPath();
         String name = converter.createModule(folder);
         File file = new File(directory, name);
         
         if(!file.exists()) {
            file.mkdirs();
         }
         temp = file;
      }
      return new File(temp, path);
   }
   
   private static class CacheOutputStream extends FilterOutputStream {
      
      private final File file;
      
      public CacheOutputStream(OutputStream stream, File file) {
         super(stream);
         this.file = file;
      }
      
      public String getFile(){
         try {
            return file.getCanonicalPath();
         } catch(Exception e) {
            throw new IllegalStateException("Could not get file " + file);
         }
      }
      
      public boolean isFailure(){
         return file.length() <= 0;
      }
   }
   
   private static class CacheInputStream extends FilterInputStream {
      
      private final File file;
      
      public CacheInputStream(InputStream stream, File file) {
         super(stream);
         this.file = file;
      }
      
      public String getFile(){
         try {
            return file.getCanonicalPath();
         } catch(Exception e) {
            throw new IllegalStateException("Could not get file " + file);
         }
      }
      
      public boolean isFailure(){
         return file.length() <= 0;
      }
   }
   
   
}

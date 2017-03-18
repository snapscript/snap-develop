
package org.snapscript.develop.common;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileReader {

   public static String readText(File file) throws Exception {
      byte[] binary = readBinary(file);
      return new String(binary, "UTF-8");
   }
   
   public static byte[] readBinary(File file) throws Exception {
      if(file.exists() && file.isFile()) {
         InputStream source = new FileInputStream(file);
         ByteArrayOutputStream buffer = new ByteArrayOutputStream();
         byte[] data = new byte[1024];
         int count = 0;
         
         try {
            while((count = source.read(data)) != -1) {
               buffer.write(data, 0, count);
            }
            return buffer.toByteArray();
         } finally {
            source.close();
         }
      }
      throw new IOException("Resource "  + file + " is a directory");   
   }
}

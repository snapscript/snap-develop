package org.snapscript.develop.http.resource;

public class FileContent {
   
   private final String path;
   private final String type;
   private final String encoding;
   private final byte[] data;
   private final long duration;
   private final double compression;

   public FileContent(String path, String type, String encoding, byte[] data, long duration, double compression) {
      this.duration = duration;
      this.encoding = encoding;
      this.compression = compression;
      this.path = path;
      this.type = type;
      this.data = data;
   }
   
   public double getCompression() {
      return compression;
   }

   public long getDuration(){
      return duration;
   }

   public String getPath() {
      return path;
   }

   public String getType() {
      return type;
   }

   public String getEncoding() {
      return encoding;
   }

   public byte[] getData() {
      return data;
   }
}

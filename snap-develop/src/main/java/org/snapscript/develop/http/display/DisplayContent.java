/*
 * FileContent.java December 2016
 *
 * Copyright (C) 2016, Niall Gallagher <niallg@users.sf.net>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied. See the License for the specific language governing 
 * permissions and limitations under the License.
 */

package org.snapscript.develop.http.display;

public class DisplayContent {
   
   private final String path;
   private final String type;
   private final String encoding;
   private final byte[] data;
   private final long duration;
   private final double compression;

   public DisplayContent(String path, String type, String encoding, byte[] data, long duration, double compression) {
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

/*
 * WriteOutputEvent.java December 2016
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

package org.snapscript.agent.event;


public class WriteOutputEvent implements ProcessEvent {

   private final String process;
   private final byte[] data;
   private final int offset;
   private final int length;
   private final boolean flush;
   
   public WriteOutputEvent(Builder builder) {
      this.offset = builder.offset;
      this.length = builder.length;
      this.process = builder.process;
      this.data = builder.data;
      this.flush = builder.flush;
   }
   
   @Override
   public String getProcess() {
      return process;
   }
   
   public byte[] getData() {
      return data;
   }
   
   public int getLength() {
      return length;
   }
   
   public int getOffset() {
      return offset;
   }
   
   public boolean isFlush() {
      return flush;
   }
   
   public static class Builder {
      
      private String process;
      private byte[] data;
      private int offset;
      private int length;
      private boolean flush;
      
      public Builder(String process) {
         this.process = process;
      }

      public Builder withProcess(String process) {
         this.process = process;
         return this;
      }

      public Builder withData(byte[] data) {
         this.data = new byte[data.length];
         System.arraycopy(data, 0, this.data, 0, data.length);
         return this;
      }

      public Builder withOffset(int offset) {
         this.offset = offset;
         return this;
      }

      public Builder withLength(int length) {
         this.length = length;
         return this;
      }
      
      public Builder withFlush(boolean flush) {
         this.flush = flush;
         return this;
      }
      
      public WriteOutputEvent build(){
         return new WriteOutputEvent(this);
      }
   }
}

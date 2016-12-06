package org.snapscript.agent.event;

public class WriteErrorEvent implements ProcessEvent {

   private final String process;
   private final byte[] data;
   private final int offset;
   private final int length;
   
   public WriteErrorEvent(Builder builder) {
      this.offset = builder.offset;
      this.length = builder.length;
      this.process = builder.process;
      this.data = builder.data;
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
   
   public static class Builder {
      
      private String process;
      private byte[] data;
      private int offset;
      private int length;
      
      public Builder(String process) {
         this.process = process;
      }

      public Builder withProcess(String process) {
         this.process = process;
         return this;
      }

      public Builder withData(byte[] data) {
         this.data = data;
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
      
      public WriteErrorEvent build(){
         return new WriteErrorEvent(this);
      }

      
   }
}

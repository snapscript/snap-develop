package org.snapscript.agent.event;

public class MessageEnvelope {
   
   private final String process;
   private final byte[] data;
   private final int offset;
   private final int length;
   private final int code;
   
   public MessageEnvelope(String process, int code, byte[] data, int offset, int length) {
      this.offset = offset;
      this.length = length;
      this.process = process;
      this.data = data;
      this.code = code;
   }
   
   public int getCode() {
      return code;
   }
   
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
}

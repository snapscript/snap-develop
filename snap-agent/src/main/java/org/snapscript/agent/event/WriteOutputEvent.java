package org.snapscript.agent.event;

public class WriteOutputEvent implements ProcessEvent {

   private String process;
   private byte[] data;
   private int offset;
   private int length;
   
   public WriteOutputEvent(String process, byte[] data, int offset, int length) {
      this.offset = offset;
      this.length = length;
      this.process = process;
      this.data = data;
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
}

package org.snapscript.agent.event;

import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.OutputStream;

public class MessageEnvelopeWriter {

   private final DataOutputStream stream;
   private final Closeable closeable;
   
   public MessageEnvelopeWriter(OutputStream stream, Closeable closeable) {
      this.stream = new DataOutputStream(stream);
      this.closeable = closeable;
   }
   
   public synchronized void write(MessageEnvelope message) throws Exception {
      String agent = message.getProcess();
      byte[] array = message.getData();
      int length = message.getLength();
      int offset = message.getOffset();
      int type = message.getCode();
      
      stream.writeUTF(agent);
      stream.writeInt(type);
      stream.writeInt(length);
      stream.write(array, offset, length);
      stream.flush();
   }
   
   public synchronized void close() throws Exception {
      try {
         stream.flush();
      }finally {
         closeable.close();
      }
   }
}

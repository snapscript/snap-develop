package org.snapscript.agent.event;

import java.io.DataInputStream;
import java.io.InputStream;

public class MessageEnvelopReader {

   public final DataInputStream stream;
   
   public MessageEnvelopReader(InputStream stream) {
      this.stream = new DataInputStream(stream);
   }
   
   public synchronized MessageEnvelope read() throws Exception {
      String agent = stream.readUTF();
      int type = stream.readInt();
      int length = stream.readInt();
      byte[] array = new byte[length];
      
      stream.readFully(array);
      
      return new MessageEnvelope(agent, type, array, 0, length);
   }
}

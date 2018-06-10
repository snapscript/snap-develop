package org.snapscript.studio.cli.debug;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URI;

public class DebugRequestMarshaller {

   public DebugRequest readRequest(Socket socket) throws IOException {
      InputStream stream = socket.getInputStream();
      DataInputStream input = new DataInputStream(stream);
      String project = input.readUTF();
      String host = input.readUTF();  
      int port = input.readInt();
      
      return new DebugRequest(project, host, port);
   }
   
   public void writeRequest(Socket socket, DebugRequest request) throws IOException {
      OutputStream stream = socket.getOutputStream();
      DataOutputStream output = new DataOutputStream(stream);     
      String project = request.getProject();
      URI root = request.getTarget();
      String host = root.getHost();
      int port = root.getPort();
      
      output.writeUTF(project);
      output.writeUTF(host);
      output.writeInt(port);
      output.flush();
   }
}
package org.snapscript.agent.event.socket;

import java.io.PrintStream;

import junit.framework.TestCase;

import org.snapscript.agent.ProcessEventStream;
import org.snapscript.agent.event.ExitEvent;
import org.snapscript.agent.event.ProcessEventAdapter;
import org.snapscript.agent.event.ProcessEventChannel;
import org.snapscript.agent.event.ProcessEventType;
import org.snapscript.agent.event.RegisterEvent;
import org.snapscript.agent.event.WriteErrorEvent;
import org.snapscript.agent.log.ConsoleLog;
import org.snapscript.agent.log.ProcessLog;
import org.snapscript.agent.log.ProcessLogger;

public class SocketTest extends TestCase {

   private class DemoListener extends ProcessEventAdapter {
      
      private final String name;
      
      public DemoListener(String name) {
         this.name = name;
      }
      
      @Override
      public void onWriteError(ProcessEventChannel channel, WriteErrorEvent event) throws Exception {
         try {               
            byte[] array = event.getData();
            int length = event.getLength();
            int offset = event.getOffset();
            String text = new String(array, offset, length, "UTF-8");
            System.err.println("TEXT: ["+text+"]");
         } catch(Exception e) {
            e.printStackTrace();
         }
      }
      
      @Override
      public void onRegister(ProcessEventChannel channel, RegisterEvent event) throws Exception  {
         System.err.println("REGISTER: process=" + event.getProcess() + " name="+name);
         channel.send(new ExitEvent.Builder("exit["+name+"]="+event.getProcess())
            .withDuration(0L)
            .build());
      }
      
      @Override
      public void onExit(ProcessEventChannel channel, ExitEvent event) throws Exception {
         System.err.println("EXIT: process=" + event.getProcess() + " name="+name);
      }
   }
   
   public void testSocket() throws Exception {
      ProcessLog log = new ConsoleLog();
      ProcessLogger logger = new ProcessLogger(log);
      SocketEventServer server = new SocketEventServer(new DemoListener("server-listener"), logger, 3344);
      SocketEventClient client = new SocketEventClient(new DemoListener("client-listener"), logger);
      
      server.start();
      ProcessEventChannel channel = client.connect("localhost", 3344);
      
      for(int i = 0; i < 100; i++) {
         channel.send(new RegisterEvent.Builder("blah-" + i)
            .withSystem(System.getProperty("os.name"))
            .build());
      }
      ProcessEventStream stream = new ProcessEventStream(ProcessEventType.WRITE_ERROR, channel, System.err, "XXX");
      PrintStream printer = new PrintStream(stream, true, "UTF-8");
      printer.println("line-1");
      printer.println("line-2");
      Thread.sleep(100000);
   }
}

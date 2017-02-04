package org.snapscript.develop;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.snapscript.agent.ConsoleLogger;

public class ProcessConnectionPool {

   private final BlockingQueue<ProcessConnection> pool;
   private final ConsoleLogger logger;
   
   public ProcessConnectionPool(ConsoleLogger logger){
      this.pool = new LinkedBlockingQueue<ProcessConnection>();
      this.logger = logger;
   }
   
   public ProcessConnection acquire(long wait) {
      try {
         return pool.poll(wait, TimeUnit.MILLISECONDS); // take a process from the pool
      }catch(Exception e){
         logger.info("Could not acquire process", e);
      }
      return null;
   }
   
   public ProcessConnection acquire(long wait, String process) {
      try {
         if(process != null) {
            int count = 10 * pool.size();
            
            while(count-- > 0) {
               ProcessConnection connection = pool.poll(5, TimeUnit.MILLISECONDS); // take a process from the pool
               
               if(connection != null) {
                  String name = connection.getProcess();
                  
                  if(name.equals(process)) { 
                     return connection; // if there is a match then return it
                  }
                  pool.offer(connection);
               }
            }
         }
      }catch(Exception e){
         logger.info("Could not acquire process '" +process+ "'", e);
      }
      return acquire(wait);
   }
   
   public void register(ProcessConnection connection) {
      pool.offer(connection);
      
   }
   
   public void register(List<ProcessConnection> connections) {
      for(ProcessConnection connection : connections){
         register(connection);
      }
   }
   
   public boolean isEmpty(){
      return pool.isEmpty();
   }
   
   public int size() {
      return pool.size();
   }
}

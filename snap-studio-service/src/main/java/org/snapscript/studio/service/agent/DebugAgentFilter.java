package org.snapscript.studio.service.agent;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.concurrent.atomic.AtomicLong;

import org.snapscript.studio.service.ProcessNameFilter;
import org.springframework.stereotype.Component;

@Component
public class DebugAgentFilter implements ProcessNameFilter {
   
   private static final String PROCESS_PREFIX = "agent-";
   private static final String DATE_FORMAT = "ddHHmmss";
   private static final String PROCESS_PATTERN = PROCESS_PREFIX + "\\d+";
   
   private final AtomicLong counter;
   private final DateFormat format;
   
   public DebugAgentFilter(){
      this.format = new SimpleDateFormat(DATE_FORMAT);
      this.counter = new AtomicLong(1);
   }
   
   @Override
   public synchronized String generate() {
      long time = System.currentTimeMillis();
      long sequence = counter.getAndIncrement();
      String date = format.format(time);
      
      return String.format("%s%s%s", PROCESS_PREFIX, sequence, date);
   }

   @Override
   public synchronized boolean accept(String name) {
      if(name != null) {
         return name.matches(PROCESS_PATTERN);
      }
      return false;
   }
}
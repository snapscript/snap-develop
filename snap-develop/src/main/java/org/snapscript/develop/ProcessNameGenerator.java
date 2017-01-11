package org.snapscript.develop;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.concurrent.atomic.AtomicLong;

public class ProcessNameGenerator {
   
   private final AtomicLong counter;
   private final DateFormat format;
   
   public ProcessNameGenerator(){
      this.format = new SimpleDateFormat("ddHHmmss");
      this.counter = new AtomicLong(1);
   }
   
   public synchronized String generate() {
      long time = System.currentTimeMillis();
      long sequence = counter.getAndIncrement();
      String date = format.format(time);
      
      return String.format("agent-%s%s", sequence, date);
   }
}

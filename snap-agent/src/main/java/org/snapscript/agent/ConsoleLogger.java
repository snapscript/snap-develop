package org.snapscript.agent;

import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.snapscript.common.ThreadBuilder;

public class ConsoleLogger {
   
   private static final String TIME_FORMAT = "HH:mm:ss";
   private static final int EVENT_LIMIT = 10000;
   
   private final LogDispatcher dispatcher;
   private final DateFormatter formatter;
   private final PrintStream logger;
   private final boolean verbose;
   
   public ConsoleLogger() {
      this(false);
   }
   
   public ConsoleLogger(boolean verbose) {
      this.dispatcher = new LogDispatcher(EVENT_LIMIT);
      this.formatter = new DateFormatter(TIME_FORMAT);
      this.logger = System.out;
      this.verbose = verbose;
   }
   
   public void debug(String message) {
      if(verbose) {
         log(message);
      }
   }
   
   public void debug(String message, Throwable cause) {
      if(verbose) {
         log(message, cause);
      }
   }
   
   public void log(String message) {
      LogEvent event = new LogEvent(message, null);
      dispatcher.log(event);
   }
   
   public void log(String message, Throwable cause) {
      LogEvent event = new LogEvent(message, cause);
      dispatcher.log(event);
   }
   
   private class DateFormatter extends ThreadLocal<DateFormat> {
      
      private final String format;
      
      public DateFormatter(String format) {
         this.format = format;
      }
      
      @Override
      public DateFormat initialValue(){
         return new SimpleDateFormat(format);
      }
   }
   
   private class LogDispatcher implements Runnable {
      
      private final BlockingQueue<LogEvent> queue;
      private final ThreadFactory factory;
      private final AtomicBoolean active;
      
      public LogDispatcher(int capacity) {
         this.factory = new ThreadBuilder();
         this.queue = new ArrayBlockingQueue<LogEvent>(capacity);
         this.active = new AtomicBoolean(false);
      }
      
      public void log(LogEvent event) {
         try {
            if(active.compareAndSet(false, true)) {
               Thread thread = factory.newThread(this);
               thread.start();
            }
            queue.offer(event, 10000, TimeUnit.MILLISECONDS);
         }catch(Exception e) {
            throw new IllegalStateException("Could not log event", e);
         }
      }
      
      @Override
      public void run() {
         try {
            while(active.get()) {
               try {
                  LogEvent event = queue.poll(1000, TimeUnit.MILLISECONDS);
                  
                  if(event != null) {
                     event.run();
                  }
               } catch(Exception e) {
                  throw new IllegalStateException("Could not poll queue", e);
               }
            }
         } finally {
            active.set(false);
         }
      }
   }
   
   private class LogEvent implements Runnable {
      
      private final Throwable cause;
      private final String message;
      private final Thread thread;
      private final long time;
      
      public LogEvent(String message, Throwable cause) {
         this.time = System.currentTimeMillis();
         this.thread = Thread.currentThread();
         this.message = message;
         this.cause = cause;
      }
      
      @Override
      public void run() {
         String name = thread.getName();
         DateFormat format = formatter.get();
         String date = format.format(time);
         
         if(cause != null) {
            logger.print(date + " ["+name+"] " + message);
            
            if(verbose) {
               logger.print(": ");
               cause.printStackTrace(logger);
            } else {
               logger.println(": " + cause);
            }
         } else {
            logger.println(date + " ["+name+"] " + message);
         }
      }
      
   }
}

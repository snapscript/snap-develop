
package org.snapscript.agent.log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.snapscript.common.ThreadBuilder;

public class ProcessLogger {
   
   private static final String TIME_FORMAT = "HH:mm:ss";
   private static final int EVENT_LIMIT = 10000;
   
   private final LogDispatcher dispatcher;
   private final DateFormatter formatter;
   private final ProcessLog logger;
   private final LogLevel level;
   
   public ProcessLogger(ProcessLog logger) {
      this(logger, null);
   }
   
   public ProcessLogger(ProcessLog logger, String level) {
      this.dispatcher = new LogDispatcher(EVENT_LIMIT);
      this.formatter = new DateFormatter(TIME_FORMAT);
      this.level = LogLevel.resolveLevel(level);
      this.logger = logger;
   }
   
   public String getLevel() {
      return level.name();
   }
   
   public boolean isTrace() {
      return level.isTrace();
   }
   
   public boolean isDebug() {
      return level.isDebug();
   }
   
   public void trace(String message) {
      if(level.isTrace()) {
         log(message);
      }
   }
   
   public void trace(String message, Throwable cause) {
      if(level.isTrace()) {
         log(message, cause);
      }
   }
   
   public void debug(String message) {
      if(level.isDebug()) {
         log(message);
      }
   }
   
   public void debug(String message, Throwable cause) {
      if(level.isDebug()) {
         log(message, cause);
      }
   }
   
   public void info(String message) {
      if(level.isInfo()) {
         log(message);
      }
   }
   
   public void info(String message, Throwable cause) {
      if(level.isInfo()) {
         log(message, cause);
      }
   }
   
   private void log(String message) {
      LogEvent event = new LogEvent(message, null);
      dispatcher.log(event);
   }
   
   private void log(String message, Throwable cause) {
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
   
   private enum LogLevel {
      TRACE(0),
      DEBUG(1),
      INFO(2);
      
      private final int level;
      
      private LogLevel(int level){
         this.level = level;
      }
      
      public boolean isTrace(){
         return level <= TRACE.level;
      }
      
      public boolean isDebug(){
         return level <= DEBUG.level;
      }
      
      public boolean isInfo(){
         return level <= INFO.level;
      }
      
      public static LogLevel resolveLevel(String token){
         if(token != null) {
            String match = token.trim();
            LogLevel[] levels = values();
            
            for(LogLevel level : levels){
               String name = level.name();
               
               if(name.equalsIgnoreCase(match)) {
                  return level;
               }
            }
         }
         return LogLevel.INFO;
         
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
         
         if(message != null) {
            StringBuilder builder = new StringBuilder();
            
            builder.append(date);
            builder.append(" [");
            builder.append(name);
            builder.append("] ");
            builder.append(message);
            
            if(cause != null) {
               if(level.isTrace()) {
                  logger.log(builder, cause);
               } else {
                  builder.append(": ");
                  builder.append(cause);
                  logger.log(builder);
               }
            } else {
               logger.log(builder);
            }
         }
      }
      
   }
}

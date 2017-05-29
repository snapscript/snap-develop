package org.snapscript.develop.browser;

import java.io.File;

import org.snapscript.agent.log.ProcessLogger;

public class BrowserContext {

   private static final String ICON_PATH = "/resource/img/icon_large.png";
   
   private final ProcessLogger logger;
   private final File directory;
   private final String host;
   private final int port;
   private final boolean debug;
   
   public BrowserContext(Builder builder) {
      this.directory = builder.directory;
      this.logger = builder.logger;
      this.host = builder.host;
      this.debug = builder.debug;
      this.port = builder.port;
   }
   
   public String getIconPath() {
      return ICON_PATH;
   }

   public ProcessLogger getLogger() {
      return logger;
   }

   public String getTarget() {
      return String.format("http://%s:%s", host, port);
   }

   public String getHost() {
      return host;
   }

   public int getPort() {
      return port;
   }


   public File getDirectory() {
      return directory;
   }

   public boolean isDebug() {
      return debug;
   }

   public static class Builder {
      
      private ProcessLogger logger;
      private File directory;
      private String host;
      private int port;
      private boolean debug;

      public Builder() {
         super();
      }

      public Builder withLogger(ProcessLogger logger) {
         this.logger = logger;
         return this;
      }

      public Builder withHost(String host) {
         this.host = host;
         return this;
      }
      
      public Builder withPort(int port) {
         this.port = port;
         return this;
      }

      public Builder withDirectory(File directory) {
         this.directory = directory;
         return this;
      }

      public Builder withDebug(boolean debug) {
         this.debug = debug;
         return this;
      }
      
      public BrowserContext build() {
         return new BrowserContext(this);
      }
   }
}

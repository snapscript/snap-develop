package org.snapscript.develop.complete;

public class CompletionRequest {

   private String resource;
   private String source;
   private String prefix;
   private String complete;
   private int line;
   
   public CompletionRequest() {
      super();
   }

   public int getLine() {
      return line;
   }

   public void setLine(int line) {
      this.line = line;
   }

   public String getComplete() {
      return complete;
   }
   
   public void setComplete(String complete) {
      this.complete = complete;
   }

   public String getResource() {
      return resource;
   }

   public void setResource(String resource) {
      this.resource = resource;
   }

   public String getSource() {
      return source;
   }

   public void setSource(String source) {
      this.source = source;
   }

   public String getPrefix() {
      return prefix;
   }

   public void setPrefix(String prefix) {
      this.prefix = prefix;
   }
}

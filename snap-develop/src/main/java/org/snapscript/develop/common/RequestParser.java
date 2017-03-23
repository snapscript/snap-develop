package org.snapscript.develop.common;

import org.simpleframework.http.Request;

public class RequestParser {

   private final Request request;
   
   public RequestParser(Request request) {
      this.request = request;
   }
   
   public String getString(String name) {
      String value = request.getParameter(name);
      
      if(value == null) {
         throw new IllegalArgumentException("Could not find parameter " + name);
      }
      return value;
   }
   
   public boolean getBoolean(String name) {
      String value = getString(name);
      return Boolean.parseBoolean(value);
   }
   
   public int getInteger(String name) {
      String value = getString(name);
      return Integer.parseInt(value);
   }
   
   public double getDouble(String name) {
      String value = getString(name);
      return Double.parseDouble(value);
   }
}

package org.snapscript.studio.index.complete;

import java.util.Collections;
import java.util.Map;

public class CompletionResponse {

   private Map<String, String> tokens;
   private String details;
   
   public CompletionResponse() {
      this(Collections.EMPTY_MAP, null);
   }
   
   public CompletionResponse(Map<String, String> tokens, String details) {
      this.tokens = tokens;
      this.details = details;
   }

   public String getDetails() {
      return details;
   }

   public void setDetails(String details) {
      this.details = details;
   }

   public Map<String, String> getTokens() {
      return tokens;
   }

   public void setTokens(Map<String, String> tokens) {
      this.tokens = tokens;
   }
}
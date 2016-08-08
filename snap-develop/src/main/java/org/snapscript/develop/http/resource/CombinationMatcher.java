package org.snapscript.develop.http.resource;

import java.util.List;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;

public class CombinationMatcher implements ResourceMatcher {

   private final List<ResourceMatcher> matchers;
   private final Resource fallback;

   public CombinationMatcher(List<ResourceMatcher> matchers) {
      this(matchers, null);
   }
   
   public CombinationMatcher(List<ResourceMatcher> matchers, Resource fallback) {
      this.fallback = fallback;
      this.matchers = matchers;
   }

   @Override
   public Resource match(Request request, Response response) throws Exception {
      for (ResourceMatcher matcher : matchers) {
         Resource resource = matcher.match(request, response);

         if (resource != null) {
            return resource;
         }
      }
      return fallback;
   }

}

package org.snapscript.develop.find.text;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.snapscript.common.Cache;
import org.snapscript.common.LeastRecentlyUsedCache;
import org.snapscript.common.thread.ThreadPool;

public class TextMatchHistory {

   private final Cache<String, ProjectHistory> cache; // reduce the set of files to look at
   private final ThreadPool pool;
   
   public TextMatchHistory(ThreadPool pool) {
      this.cache = new LeastRecentlyUsedCache<String, ProjectHistory>();
      this.pool = pool;
   }
   
   public synchronized void saveMatches(TextMatchQuery query, Set<TextFile> matches) {
      String project = query.getProject();
      ProjectHistory history = cache.fetch(project);
      
      if(history == null) {
         history = new ProjectHistory(project);
         cache.cache(project, history);
      }
      ExpiryTask task = new ExpiryTask(query);
      
      pool.schedule(task, 10, TimeUnit.SECONDS); // clear the cache entry in 10 seconds
      history.put(query, matches);
   }
   
   public synchronized Set<TextFile> deleteMatches(TextMatchQuery query) {
      String project = query.getProject();
      ProjectHistory history = cache.fetch(project);
      
      if(history != null) {
         return history.remove(query);
      }
      return null;
   }
   
   public synchronized Set<TextFile> findMatches(TextMatchQuery query) {
      String project = query.getProject();
      ProjectHistory history = cache.fetch(project);
      Set<TextFile> files = null;
      
      if(history != null) {
         Set<TextMatchQuery> queries = history.keySet();
         int best = 0;
         
         for(TextMatchQuery next : queries) {
            int score = scoreMatch(query, next);
               
            if(score > best) {
               files = history.get(next);
               best = score;
            }
         }
      }
      return files;
   }
   
   private synchronized int scoreMatch(TextMatchQuery query, TextMatchQuery existing) {
      if(startsWith(query, existing)) {
         String expression = existing.getQuery();
         int length = expression.length();
         
         return length;
      }
      return -1;
   }

   private synchronized boolean startsWith(TextMatchQuery query, TextMatchQuery existing) {
      if(query.isCaseSensitive() == existing.isCaseSensitive()) {
         String longer = query.getQuery();
         String shorter = existing.getQuery();
         
         if(query.isCaseSensitive()) {
            String longerToken = longer.toLowerCase();
            String shorterToken = shorter.toLowerCase();
            
            return longerToken.startsWith(shorterToken); 
         }
         return longer.startsWith(shorter);
      }
      return false;
   }
   
   private class ExpiryTask implements Runnable {
      
      private final TextMatchQuery query;
      
      public ExpiryTask(TextMatchQuery query) {
         this.query = query;
      }
      
      @Override
      public void run() {
         deleteMatches(query);
      }
   }
   
   private class ProjectHistory {
      
      private final Cache<TextMatchQuery, Set<TextFile>> history; // reduce the set of files to look at
      private final String project;
      
      public ProjectHistory(String project) {
         this(project, 1000);
      }
      
      public ProjectHistory(String project, int capacity) {
         this.history = new LeastRecentlyUsedCache<TextMatchQuery, Set<TextFile>>(capacity);
         this.project = project;
      }
      
      public Set<TextMatchQuery> keySet() {
         return history.keySet();
      }
      
      public Set<TextFile> get(TextMatchQuery query) {
         return history.fetch(query);
      }
      
      public Set<TextFile> remove(TextMatchQuery query) {
         return history.take(query);
      }
      
      public void put(TextMatchQuery query, Set<TextFile> files) {
         history.cache(query, Collections.unmodifiableSet(files));
      }
      
      @Override
      public String toString(){
         return project;
      }
      
   }
}
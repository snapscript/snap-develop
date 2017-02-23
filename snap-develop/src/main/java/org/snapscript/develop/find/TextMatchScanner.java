/*
 * TextMatchScanner.java December 2016
 *
 * Copyright (C) 2016, Niall Gallagher <niallg@users.sf.net>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied. See the License for the specific language governing 
 * permissions and limitations under the License.
 */

package org.snapscript.develop.find;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.simpleframework.http.Path;
import org.snapscript.agent.log.ProcessLogger;
import org.snapscript.common.Cache;
import org.snapscript.common.LeastRecentlyUsedCache;
import org.snapscript.common.LeastRecentlyUsedMap.RemovalListener;
import org.snapscript.common.ThreadPool;
import org.snapscript.develop.http.project.Project;
import org.snapscript.develop.http.project.ProjectBuilder;

public class TextMatchScanner {
   
   private final Cache<String, Set<TextFile>> cache; // reduce the set of files to look at
   private final FileMatchScanner scanner;
   private final TextMatchFinder finder;
   private final ProjectBuilder builder;
   private final ProcessLogger logger;
   private final CacheCleaner cleaner;
   private final ThreadPool pool;
   private final Set<String> tokens; // what is available in cache
   
   public TextMatchScanner(ProjectBuilder builder, ProcessLogger logger, ThreadPool pool) {
      this.scanner = new FileMatchScanner(builder); // e.g *.snap, *.txt
      this.cleaner = new CacheCleaner();
      this.cache = new LeastRecentlyUsedCache<String, Set<TextFile>>(cleaner, 100);
      this.tokens = new CopyOnWriteArraySet<String>();
      this.finder = new TextMatchFinder(logger);
      this.builder = builder;
      this.logger = logger;
      this.pool = pool;
   }
   
   public List<TextMatch> scanFiles(final Path path, final String filePattern, final String expression) throws Exception {
      final String key = createKey(path, filePattern, expression);
      final Set<TextFile> files = findFiles(path, filePattern, expression);
      
      if(!files.isEmpty()) {
         final List<TextMatch> matches = new CopyOnWriteArrayList<TextMatch>();
         final Set<TextFile> success = new CopyOnWriteArraySet<TextFile>();
         final BlockingQueue<TextFile> finished = new LinkedBlockingQueue<TextFile>();
         
         for(final TextFile file : files) {
            pool.execute(new Runnable() {
               public void run() {
                  try {
                     List<TextMatch> match = finder.findText(file, expression);
                     
                     if(!match.isEmpty()) {
                        matches.addAll(match);
                        success.add(file);
                     }
                     //logger.debug("Searched " + file);
                     finished.offer(file);
                  }catch(Exception e) {
                     logger.debug("Error searching file " + file, e);
                  }
               }
            });
         }
         for(final TextFile file : files) {
            finished.take(); // wait for them all to finish
         }
         pool.schedule(new Runnable() { // crude but will clear cache
            public void run() {
               tokens.remove(key);
               cache.take(key);
            }
         }, 10, TimeUnit.SECONDS); // clear the cache entry in 10 seconds
         tokens.add(key);
         cache.cache(key, success);
         List<TextMatch> sorted = new ArrayList<TextMatch>(matches);
         Collections.sort(sorted);
         return sorted;
      }
      return Collections.emptyList();
   }
   
   private Set<TextFile> findFiles(Path path, String filePattern, String expression) throws Exception {
      String key = createKey(path, filePattern, expression); 
      Set<TextFile> files = null;
      int best = 0;
      
      for(String token : tokens) {
         if(key.startsWith(token)) {
            int length = token.length();
            
            if(length > best) {
               files = cache.fetch(token);
               best = length;
            }
         }
      }
      if(files == null) {
         return findAllFiles(path, filePattern, expression);
      }
      return files;
   }
   
   private Set<TextFile> findAllFiles(Path path, String filePattern, String expression) throws Exception {
      Set<FileMatch> filesFound = new LinkedHashSet<FileMatch>();
      Set<TextFile> textFiles = new LinkedHashSet<TextFile>();
      
      if(filePattern == null) {
         filePattern = "*.*";
      }
      String[] fileExpressions = filePattern.split(",");
      
      for(String fileExpression : fileExpressions) {
         String pathPattern = fileExpression.trim();
         
         if(!pathPattern.isEmpty()) {
            List<FileMatch> filesMatched = scanner.findAllFiles(path, pathPattern);
            
            for(FileMatch fileMatch : filesMatched) {
               filesFound.add(fileMatch);
            }
         }
      }
      for(FileMatch fileMatch : filesFound) {
         File file = fileMatch.getFile();
         String resourcePath = fileMatch.getResource();
         String projectName = fileMatch.getProject();
         TextFile projectFile = new TextFile(file, projectName, resourcePath);
         textFiles.add(projectFile);
      }
      return textFiles;
   }
   
   private String createKey(Path path, String filePattern, String expression) throws Exception {
      Project project = builder.createProject(path);
      String name = project.getProjectName();
      String token = expression.toLowerCase();
      
      return String.format("%s:%s:%s", name, filePattern, token);
   }
   
   private class CacheCleaner implements RemovalListener<String, Set<TextFile>> {

      @Override
      public void notifyRemoved(String key, Set<TextFile> value) {
         tokens.remove(key);
      }
   }
}

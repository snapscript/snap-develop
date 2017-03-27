

package org.snapscript.develop.find.text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.LinkedBlockingQueue;

import org.snapscript.agent.log.ProcessLogger;
import org.snapscript.common.ThreadPool;

public class TextMatchScanner {
   
   private final TextMatchHistory history; // what is available in cache
   private final TextFileScanner scanner;
   private final TextMatchFinder finder;
   private final ProcessLogger logger;
   private final ThreadPool pool;
   
   public TextMatchScanner(ProcessLogger logger, ThreadPool pool) {
      this.scanner = new TextFileScanner(); // e.g *.snap, *.txt
      this.history = new TextMatchHistory(pool);
      this.finder = new TextMatchFinder(logger);
      this.logger = logger;
      this.pool = pool;
   }
   
   public List<TextMatch> process(final TextMatchQuery query) throws Exception {
      if(query.isEnableReplace()) {
         return searchAndReplaceFiles(query);
      }
      return searchFiles(query);
   }
   
   private List<TextMatch> searchFiles(final TextMatchQuery query) throws Exception {
      final boolean caseSensitive = query.isCaseSensitive();
      final String expression = query.getQuery();
      final Set<TextFile> files = findFiles(query);
      
      if(!files.isEmpty()) {
         final List<TextMatch> matches = new CopyOnWriteArrayList<TextMatch>();
         final Set<TextFile> success = new CopyOnWriteArraySet<TextFile>();
         final BlockingQueue<TextFile> finished = new LinkedBlockingQueue<TextFile>();
         
         for(final TextFile file : files) {
            pool.execute(new Runnable() {
               public void run() {
                  try {
                     List<TextMatch> match = finder.findText(file, expression, caseSensitive);
                     
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
         history.saveMatches(query, success);
         List<TextMatch> sorted = new ArrayList<TextMatch>(matches);
         Collections.sort(sorted);
         return sorted;
      }
      return Collections.emptyList();
   }
   
   private List<TextMatch> searchAndReplaceFiles(final TextMatchQuery query) throws Exception {
      final boolean caseSensitive = query.isCaseSensitive();
      final String expression = query.getQuery();
      final String replace = query.getReplace();
      final Set<TextFile> files = findFiles(query);
      
      if(!files.isEmpty()) {
         final List<TextMatch> matches = new CopyOnWriteArrayList<TextMatch>();
         final Set<TextFile> success = new CopyOnWriteArraySet<TextFile>();
         final BlockingQueue<TextFile> finished = new LinkedBlockingQueue<TextFile>();
         
         for(final TextFile file : files) {
            pool.execute(new Runnable() {
               public void run() {
                  try {
                     List<TextMatch> match = finder.replaceText(file, expression, replace, caseSensitive);
                     
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
         history.saveMatches(query, success);
         List<TextMatch> sorted = new ArrayList<TextMatch>(matches);
         Collections.sort(sorted);
         return sorted;
      }
      return Collections.emptyList();
   }
   
   private Set<TextFile> findFiles(TextMatchQuery query) throws Exception {
      Set<TextFile> files = history.findMatches(query);
      
      if(files == null) {
         return scanner.findAllFiles(query);
      }
      return files;
   }
   

}

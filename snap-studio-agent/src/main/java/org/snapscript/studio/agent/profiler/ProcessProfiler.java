package org.snapscript.studio.agent.profiler;

import static org.snapscript.core.Reserved.SCRIPT_EXTENSION;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import org.snapscript.core.Path;
import org.snapscript.core.Scope;
import org.snapscript.core.trace.Trace;
import org.snapscript.studio.agent.debug.TraceAdapter;

public class ProcessProfiler extends TraceAdapter {
   
   private final Map<String, ResourceProfiler> profilers;
   private final Set<String> resources;
   
   public ProcessProfiler() {
      this.profilers = new ConcurrentHashMap<String, ResourceProfiler>();
      this.resources = new CopyOnWriteArraySet<String>();
   }

   public SortedSet<ProfileResult> lines(int size) {
      SortedSet<ProfileResult> results = new TreeSet<ProfileResult>();
      SortedSet<ProfileResult> reduced = new TreeSet<ProfileResult>();
      
      for(String resource : resources){
         ResourceProfiler profiler = profilers.get(resource);
         
         if(profiler != null) {
            profiler.collect(results, size);
         }
      }
      Iterator<ProfileResult> iterator = results.iterator();
      
      while(iterator.hasNext()) {
         if(size-- <= 0) {
            break;
         }
         ProfileResult result = iterator.next();
         
         if(result != null) {
            long duration = result.getTime();
            
            if(duration > 0) {
               reduced.add(result);
            }
         }
      }
      return reduced;
   }
   
   @Override
   public void traceBefore(Scope scope, Trace trace) {
      Path path = trace.getPath();
      String resource = path.getPath();
      ResourceProfiler profiler = profilers.get(resource);
      int line = trace.getLine();
      
      if(profiler == null) {
         String local = resource;
         
         if(!local.endsWith(SCRIPT_EXTENSION)) { // a.b.c
            local = local.replace('.', '/'); // a/b/c
            local = local + SCRIPT_EXTENSION; // a/b/c.snap
         }
         if(!local.startsWith("/")) {
            local = "/" + local; // /a/b/c.snap
         }
         if(resources.add(local)) {
            profiler = new ResourceProfiler(local);
            profilers.put(resource, profiler);
            resources.add(resource);
         }
      }
      if(profiler != null) { // eval(...) could be null
         profiler.enter(line);
      }
   }

   @Override
   public void traceAfter(Scope scope, Trace trace) {
      Path path = trace.getPath();
      String resource = path.getPath();
      ResourceProfiler profiler = profilers.get(resource);
      int line = trace.getLine();
      
      if(profiler != null) {
         profiler.exit(line);
      }
   }
}
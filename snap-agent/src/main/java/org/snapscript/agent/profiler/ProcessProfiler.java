package org.snapscript.agent.profiler;

import static org.snapscript.core.Reserved.SCRIPT_EXTENSION;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import org.snapscript.core.Module;
import org.snapscript.core.Scope;
import org.snapscript.core.trace.Trace;
import org.snapscript.core.trace.TraceListener;

public class ProcessProfiler implements TraceListener {
   
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
   public void before(Scope scope, Trace trace) {
      Module module = trace.getModule();
      String resource = module.getPath();
      ResourceProfiler profiler = profilers.get(resource);
      int line = trace.getLine();
      
      if(profiler == null) {
         String path = resource;
         
         if(!path.endsWith(SCRIPT_EXTENSION)) { // a.b.c
            path = path.replace('.', '/'); // a/b/c
            path = path + SCRIPT_EXTENSION; // a/b/c.snap
         }
         if(!path.startsWith("/")) {
            path = "/" + path; // /a/b/c.snap
         }
         if(resources.add(path)) {
            profiler = new ResourceProfiler(path);
            profilers.put(resource, profiler);
            resources.add(resource);
         }
      }
      if(profiler != null) { // eval(...) could be null
         profiler.enter(line);
      }
   }

   @Override
   public void after(Scope scope, Trace trace) {
      Module module = trace.getModule();
      String resource = module.getPath();
      ResourceProfiler profiler = profilers.get(resource);
      int line = trace.getLine();
      
      if(profiler != null) {
         profiler.exit(line);
      }
   }
}
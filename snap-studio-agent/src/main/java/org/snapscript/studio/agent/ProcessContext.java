package org.snapscript.studio.agent;

import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;

import org.snapscript.common.thread.ThreadPool;
import org.snapscript.compile.ResourceCompiler;
import org.snapscript.compile.StoreContext;
import org.snapscript.core.ResourceManager;
import org.snapscript.core.link.PackageLinker;
import org.snapscript.core.scope.EmptyModel;
import org.snapscript.core.scope.Model;
import org.snapscript.core.trace.TraceInterceptor;
import org.snapscript.studio.agent.debug.BreakpointMatcher;
import org.snapscript.studio.agent.debug.SuspendController;
import org.snapscript.studio.agent.profiler.ProcessProfiler;

public class ProcessContext {

   private final SuspendController controller;
   private final ResourceCompiler compiler;
   private final ProcessProfiler profiler;
   private final BreakpointMatcher matcher;
   private final StoreContext context;
   private final CountDownLatch latch;
   private final ProcessStore store;
   private final ProcessMode mode;
   private final Executor executor;
   private final Model model;
   private final String process;

   public ProcessContext(ProcessMode mode, URI root, String process) {
      this(mode, root, process, 10);
   }
   
   public ProcessContext(ProcessMode mode, URI root, String process, int threads) {
      this(mode, root, process, threads, 0);
   }
   
   public ProcessContext(ProcessMode mode, URI root, String process, int threads, int stack) {
      this.executor = new ThreadPool(threads < 5 ? 5 : threads, stack);
      this.latch = new CountDownLatch(1);
      this.store = new ProcessStore(root);
      this.context = new StoreContext(store, executor);
      this.compiler = new ResourceCompiler(context);
      this.controller = new SuspendController();
      this.matcher = new BreakpointMatcher();
      this.profiler = new ProcessProfiler();
      this.model = new EmptyModel();
      this.process = process;
      this.mode = mode;
   }
   
   public ProcessMode getMode() {
      return mode;
   }

   public CountDownLatch getLatch() {
      return latch;
   }

   public ResourceManager getManager(){
      return context.getManager();
   }
   
   public PackageLinker getLinker() {
      return context.getLinker();
   }
   
   public TraceInterceptor getInterceptor() {
      return context.getInterceptor();
   }
   
   public ResourceCompiler getCompiler() {
      return compiler;
   }
   
   public ProcessProfiler getProfiler() {
      return profiler;
   }
   
   public BreakpointMatcher getMatcher() {
      return matcher;
   }
   
   public SuspendController getController() {
      return controller;
   }
   
   public ProcessStore getStore() {
      return store;
   }
   
   public Model getModel() {
      return model;
   }
   
   public String getProcess() {
      return process;
   }
}
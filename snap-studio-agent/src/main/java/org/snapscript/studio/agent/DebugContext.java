package org.snapscript.studio.agent;

import java.util.concurrent.Executor;

import org.snapscript.common.thread.ThreadPool;
import org.snapscript.compile.ResourceCompiler;
import org.snapscript.compile.StoreContext;
import org.snapscript.core.Context;
import org.snapscript.core.ExpressionEvaluator;
import org.snapscript.core.ResourceManager;
import org.snapscript.core.link.PackageLinker;
import org.snapscript.core.scope.EmptyModel;
import org.snapscript.core.scope.Model;
import org.snapscript.core.trace.TraceInterceptor;
import org.snapscript.studio.agent.debug.BreakpointMatcher;
import org.snapscript.studio.agent.debug.SuspendController;
import org.snapscript.studio.agent.profiler.TraceProfiler;

public class DebugContext {

   private final SuspendController controller;
   private final ResourceCompiler compiler;
   private final TraceProfiler profiler;
   private final BreakpointMatcher matcher;
   private final ExecuteLatch latch;
   private final ProjectStore store;
   private final RunMode mode;
   private final Executor executor;
   private final Context context;
   private final Model model;   
   private final String process;
   private final String system;

   public DebugContext(RunMode mode, ProjectStore store, String process, String system) {
      this(mode, store, process, system, 10);
   }
   
   public DebugContext(RunMode mode, ProjectStore store, String process, String system, int threads) {
      this(mode, store, process, system, threads, 0);
   }
   
   public DebugContext(RunMode mode, ProjectStore store, String process, String system, int threads, int stack) {
      this.executor = new ThreadPool(threads < 5 ? 5 : threads, stack);
      this.latch = new ExecuteLatch(process, system);
      this.context = new StoreContext(store, executor);
      this.compiler = new ResourceCompiler(context);
      this.controller = new SuspendController();
      this.matcher = new BreakpointMatcher();
      this.profiler = new TraceProfiler();
      this.model = new EmptyModel();
      this.process = process;
      this.system = system;
      this.store = store;
      this.mode = mode;
   }
   
   public RunMode getMode() {
      return mode;
   }

   public ExecuteLatch getLatch() {
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
   
   public ExpressionEvaluator getEvaluator(){
      return context.getEvaluator();
   }
   
   public TraceProfiler getProfiler() {
      return profiler;
   }
   
   public BreakpointMatcher getMatcher() {
      return matcher;
   }
   
   public SuspendController getController() {
      return controller;
   }
   
   public ProjectStore getStore() {
      return store;
   }
   
   public Model getModel() {
      return model;
   }
   
   public String getSystem() {
      return system;
   }
   
   public String getProcess() {
      return process;
   }
}
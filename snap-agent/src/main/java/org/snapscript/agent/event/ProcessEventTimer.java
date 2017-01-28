package org.snapscript.agent.event;

import org.snapscript.agent.ConsoleLogger;

public class ProcessEventTimer implements ProcessEventListener {
   
   private final ProcessEventListener listener;
   private final ConsoleLogger logger;
   
   public ProcessEventTimer(ProcessEventListener listener, ConsoleLogger logger) {
      this.listener = listener;
      this.logger = logger;
   }

   @Override
   public void onExit(ProcessEventChannel channel, ExitEvent event) throws Exception {
      long start = System.currentTimeMillis();
      
      try {
         listener.onExit(channel, event);
      }finally {
         long finish = System.currentTimeMillis();
         long duration = finish - start;
         
         logger.debug("onExit(): took " + duration + " ms");
      }
   }

   @Override
   public void onExecute(ProcessEventChannel channel, ExecuteEvent event) throws Exception {
      long start = System.currentTimeMillis();
      
      try {
         listener.onExecute(channel, event);
      }finally {
         long finish = System.currentTimeMillis();
         long duration = finish - start;
         
         logger.debug("onExecute(): took " + duration + " ms");
      }
   }

   @Override
   public void onWriteError(ProcessEventChannel channel, WriteErrorEvent event) throws Exception {
      long start = System.currentTimeMillis();
      
      try {
         listener.onWriteError(channel, event);
      }finally {
         long finish = System.currentTimeMillis();
         long duration = finish - start;
         
         logger.debug("onWriteError(): took " + duration + " ms");
      }
   }

   @Override
   public void onWriteOutput(ProcessEventChannel channel, WriteOutputEvent event) throws Exception {
      long start = System.currentTimeMillis();
      
      try {
         listener.onWriteOutput(channel, event);
      }finally {
         long finish = System.currentTimeMillis();
         long duration = finish - start;
         
         logger.debug("onWriteOutput(): took " + duration + " ms");
      }
   }

   @Override
   public void onRegister(ProcessEventChannel channel, RegisterEvent event) throws Exception {
      long start = System.currentTimeMillis();
      
      try {
         listener.onRegister(channel, event);
      }finally {
         long finish = System.currentTimeMillis();
         long duration = finish - start;
         
         logger.debug("onRegister(): took " + duration + " ms");
      }
   }

   @Override
   public void onSyntaxError(ProcessEventChannel channel, SyntaxErrorEvent event) throws Exception {
      long start = System.currentTimeMillis();
      
      try {
         listener.onSyntaxError(channel, event);
      }finally {
         long finish = System.currentTimeMillis();
         long duration = finish - start;
         
         logger.debug("onSyntaxError(): took " + duration + " ms");
      }
   }

   @Override
   public void onScope(ProcessEventChannel channel, ScopeEvent event) throws Exception {
      long start = System.currentTimeMillis();
      
      try {
         listener.onScope(channel, event);
      }finally {
         long finish = System.currentTimeMillis();
         long duration = finish - start;
         
         logger.debug("onScope(): took " + duration + " ms");
      }
   }

   @Override
   public void onBreakpoints(ProcessEventChannel channel, BreakpointsEvent event) throws Exception {
      long start = System.currentTimeMillis();
      
      try {
         listener.onBreakpoints(channel, event);
      }finally {
         long finish = System.currentTimeMillis();
         long duration = finish - start;
         
         logger.debug("onBreakpoints(): took " + duration + " ms");
      }
   }

   @Override
   public void onBegin(ProcessEventChannel channel, BeginEvent event) throws Exception {
      long start = System.currentTimeMillis();
      
      try {
         listener.onBegin(channel, event);
      }finally {
         long finish = System.currentTimeMillis();
         long duration = finish - start;
         
         logger.debug("onBegin(): took " + duration + " ms");
      }
   }

   @Override
   public void onStep(ProcessEventChannel channel, StepEvent event) throws Exception {
      long start = System.currentTimeMillis();
      
      try {
         listener.onStep(channel, event);
      }finally {
         long finish = System.currentTimeMillis();
         long duration = finish - start;
         
         logger.debug("onStep(): took " + duration + " ms");
      }
   }

   @Override
   public void onBrowse(ProcessEventChannel channel, BrowseEvent event) throws Exception {
      long start = System.currentTimeMillis();
      
      try {
         listener.onBrowse(channel, event);
      }finally {
         long finish = System.currentTimeMillis();
         long duration = finish - start;
         
         logger.debug("onBrowse(): took " + duration + " ms");
      }
   }

   @Override
   public void onProfile(ProcessEventChannel channel, ProfileEvent event) throws Exception {
      long start = System.currentTimeMillis();
      
      try {
         listener.onProfile(channel, event);
      }finally {
         long finish = System.currentTimeMillis();
         long duration = finish - start;
         
         logger.debug("onProfile(): took " + duration + " ms");
      }
   }
   

   @Override
   public void onEvaluate(ProcessEventChannel channel, EvaluateEvent event) throws Exception {
      long start = System.currentTimeMillis();
      
      try {
         listener.onEvaluate(channel, event);
      }finally {
         long finish = System.currentTimeMillis();
         long duration = finish - start;
         
         logger.debug("onEvaluate(): took " + duration + " ms");
      }
   }
   
   @Override
   public void onFault(ProcessEventChannel channel, FaultEvent event) throws Exception {
      long start = System.currentTimeMillis();
      
      try {
         listener.onFault(channel, event);
      }finally {
         long finish = System.currentTimeMillis();
         long duration = finish - start;
         
         logger.debug("onFault(): took " + duration + " ms");
      }
   }

   @Override
   public void onPing(ProcessEventChannel channel, PingEvent event) throws Exception {
      long start = System.currentTimeMillis();
      
      try {
         listener.onPing(channel, event);
      }finally {
         long finish = System.currentTimeMillis();
         long duration = finish - start;
         
         logger.debug("onPing(): took " + duration + " ms");
      }
   }

   @Override
   public void onPong(ProcessEventChannel channel, PongEvent event) throws Exception {
      long start = System.currentTimeMillis();
      
      try {
         listener.onPong(channel, event);
      }finally {
         long finish = System.currentTimeMillis();
         long duration = finish - start;
         
         logger.debug("onPong(): took " + duration + " ms");
      }
   }

   @Override
   public void onClose(ProcessEventChannel channel) throws Exception {
      long start = System.currentTimeMillis();
      
      try {
         listener.onClose(channel);
      }finally {
         long finish = System.currentTimeMillis();
         long duration = finish - start;
         
         logger.debug("onClose(): took " + duration + " ms");
      }
   }
}

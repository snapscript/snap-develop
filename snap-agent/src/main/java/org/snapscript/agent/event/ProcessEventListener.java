/*
 * ProcessEventListener.java December 2016
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

package org.snapscript.agent.event;

public interface ProcessEventListener {
   void onExit(ProcessEventChannel channel, ExitEvent event) throws Exception;
   void onExecute(ProcessEventChannel channel, ExecuteEvent event) throws Exception;
   void onWriteError(ProcessEventChannel channel, WriteErrorEvent event) throws Exception;
   void onWriteOutput(ProcessEventChannel channel, WriteOutputEvent event) throws Exception;
   void onRegister(ProcessEventChannel channel, RegisterEvent event) throws Exception;
   void onSyntaxError(ProcessEventChannel channel, SyntaxErrorEvent event) throws Exception;
   void onScope(ProcessEventChannel channel, ScopeEvent event) throws Exception;
   void onBreakpoints(ProcessEventChannel channel, BreakpointsEvent event) throws Exception;
   void onBegin(ProcessEventChannel channel, BeginEvent event) throws Exception;
   void onStep(ProcessEventChannel channel, StepEvent event) throws Exception;
   void onBrowse(ProcessEventChannel channel, BrowseEvent event) throws Exception;
   void onProfile(ProcessEventChannel channel, ProfileEvent event) throws Exception;
   void onEvaluate(ProcessEventChannel channel, EvaluateEvent event) throws Exception;
   void onFault(ProcessEventChannel channel, FaultEvent event) throws Exception;
   void onPing(ProcessEventChannel channel, PingEvent event) throws Exception;
   void onPong(ProcessEventChannel channel, PongEvent event) throws Exception;
   void onClose(ProcessEventChannel channel) throws Exception;
}

/*
 * ProcessEventAdapter.java December 2016
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

public class ProcessEventAdapter implements ProcessEventListener {
   public void onExit(ProcessEventChannel channel, ExitEvent event) throws Exception {}
   public void onExecute(ProcessEventChannel channel, ExecuteEvent event) throws Exception {}
   public void onWriteError(ProcessEventChannel channel, WriteErrorEvent event) throws Exception {}
   public void onWriteOutput(ProcessEventChannel channel, WriteOutputEvent event) throws Exception {}
   public void onRegister(ProcessEventChannel channel, RegisterEvent event) throws Exception {}
   public void onSyntaxError(ProcessEventChannel channel, SyntaxErrorEvent event) throws Exception {}
   public void onScope(ProcessEventChannel channel, ScopeEvent event) throws Exception {}
   public void onBreakpoints(ProcessEventChannel channel, BreakpointsEvent event) throws Exception {}
   public void onBegin(ProcessEventChannel channel, BeginEvent event) throws Exception {}
   public void onStep(ProcessEventChannel channel, StepEvent event) throws Exception {}
   public void onBrowse(ProcessEventChannel channel, BrowseEvent event) throws Exception {}
   public void onProfile(ProcessEventChannel channel, ProfileEvent event) throws Exception {}
   public void onEvaluate(ProcessEventChannel channel, EvaluateEvent event) throws Exception {}
   public void onFault(ProcessEventChannel channel, FaultEvent event) throws Exception {}
   public void onPing(ProcessEventChannel channel, PingEvent event) throws Exception {}
   public void onPong(ProcessEventChannel channel, PongEvent event) throws Exception {}
   public void onClose(ProcessEventChannel channel) throws Exception {}

}

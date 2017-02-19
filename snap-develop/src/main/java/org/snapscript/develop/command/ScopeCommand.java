/*
 * ScopeCommand.java December 2016
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

package org.snapscript.develop.command;

import java.util.Map;

public class ScopeCommand implements Command {
   
   private Map<String, Map<String, String>> evaluation;
   private Map<String, Map<String, String>> variables;
   private String process;
   private String instruction;
   private String resource;
   private String status;
   private String thread;
   private String stack;
   private int change;
   private int depth;
   private int line;
   private int key;
   
   public ScopeCommand() {
      super();
   }

   public ScopeCommand(String process, Map<String, Map<String, String>> variables, Map<String, Map<String, String>> evaluation, String thread, String stack, String instruction, String status, String resource, int line, int depth, int key, int change) {
      this.process = process;
      this.variables = variables;
      this.instruction = instruction;
      this.evaluation = evaluation;
      this.change = change;
      this.thread = thread;
      this.resource = resource;
      this.status = status;
      this.stack = stack;
      this.depth = depth;
      this.line = line;
      this.key = key;
   }

   public Map<String, Map<String, String>> getVariables() {
      return variables;
   }

   public void setVariables(Map<String, Map<String, String>> variables) {
      this.variables = variables;
   }

   public Map<String, Map<String, String>> getEvaluation() {
      return evaluation;
   }

   public void setEvaluation(Map<String, Map<String, String>> evaluation) {
      this.evaluation = evaluation;
   }
   
   public String getProcess() {
      return process;
   }

   public void setProcess(String process) {
      this.process = process;
   }

   public String getInstruction() {
      return instruction;
   }

   public void setInstruction(String instruction) {
      this.instruction = instruction;
   }
  
   public String getStatus() {
      return status;
   }

   public void setStatus(String status) {
      this.status = status;
   }
   
   public int getChange() {
      return change;
   }

   public void setChange(int change) {
      this.change = change;
   }

   public String getResource() {
      return resource;
   }

   public void setResource(String resource) {
      this.resource = resource;
   }

   public String getThread() {
      return thread;
   }

   public void setThread(String thread) {
      this.thread = thread;
   }

   public String getStack() {
      return stack;
   }

   public void setStack(String stack) {
      this.stack = stack;
   }

   public int getDepth() {
      return depth;
   }

   public void setDepth(int depth) {
      this.depth = depth;
   }

   public int getLine() {
      return line;
   }

   public void setLine(int line) {
      this.line = line;
   }

   public int getKey() {
      return key;
   }

   public void setKey(int key) {
      this.key = key;
   }
}

/*
 * ProcessMode.java December 2016
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

package org.snapscript.agent;

public enum ProcessMode {
    SERVICE(false, false), // background long running
    SCRIPT(true, false), // terminates when script ends
    TASK(false, true); // stops ping when script ends

    private final boolean terminate;
    private final boolean detach;

    private ProcessMode(boolean terminate, boolean detach) {
       this.terminate = terminate;
       this.detach = detach;
    }

    public boolean isDetachRequired() {
       return detach;
    }

    public boolean isTerminateRequired(){
        return terminate;
    }
    
    public static ProcessMode resolveMode(String token) {
       ProcessMode[] modes = ProcessMode.values();
       
       for(ProcessMode mode : modes) {
          String name = mode.name();
          
          if(name.equalsIgnoreCase(token)) {
             return mode;
          }
       }
       return SCRIPT;
    }
}

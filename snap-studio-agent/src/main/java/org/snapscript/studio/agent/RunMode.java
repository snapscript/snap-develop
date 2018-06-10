package org.snapscript.studio.agent;

public enum RunMode {
    REMOTE(false, false), // background long running
    SERVICE(false, false), // background long running
    SCRIPT(true, false), // terminates when script ends
    TASK(false, true); // stops ping when script ends

    private final boolean terminate;
    private final boolean detach;

    private RunMode(boolean terminate, boolean detach) {
       this.terminate = terminate;
       this.detach = detach;
    }
    
    public boolean isRemoteAttachment(){
       return this == REMOTE;
    }

    public boolean isDetachRequired() {
       return detach;
    }

    public boolean isTerminateRequired(){
        return terminate;
    }
    
    public static RunMode resolveMode(String token) {
       RunMode[] modes = RunMode.values();
       
       for(RunMode mode : modes) {
          String name = mode.name();
          
          if(name.equalsIgnoreCase(token)) {
             return mode;
          }
       }
       return SCRIPT;
    }
}
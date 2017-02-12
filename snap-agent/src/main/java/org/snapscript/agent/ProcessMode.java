package org.snapscript.agent;

public enum ProcessMode {
    DETACHED(true),
    ATTACHED(false);

    private final boolean async;

    private ProcessMode(boolean async) {
        this.async = async;
    }

    public boolean isAsync(){
        return async;
    }
    
    public static ProcessMode resolveMode(String token) {
       ProcessMode[] modes = ProcessMode.values();
       
       for(ProcessMode mode : modes) {
          String name = mode.name();
          
          if(name.equalsIgnoreCase(token)) {
             return mode;
          }
       }
       return ATTACHED;
    }
}

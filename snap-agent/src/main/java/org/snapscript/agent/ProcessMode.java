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
}

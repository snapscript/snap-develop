package org.snapscript.studio.agent;

public interface ExecuteState { 
   ExecuteData getData();
   ExecuteStatus getStatus();
   String getProcess();
   String getSystem();
}

package org.snapscript.studio.agent.worker;

import java.net.URI;

import org.snapscript.studio.agent.ProcessAgent;
import org.snapscript.studio.agent.ProcessContext;
import org.snapscript.studio.agent.ProcessMode;
import org.snapscript.studio.agent.core.TerminateListener;
import org.snapscript.studio.agent.log.LogLevel;
import org.snapscript.studio.agent.worker.store.WorkerStore;

public class WorkerProcessExecutor {

   public void execute(WorkerCommandLine line) throws Exception {
      URI download = line.getDownloadURL();
      ProcessMode mode = line.getMode();
      String process = line.getName();
      LogLevel level = line.getLogLevel();
      String system = line.getSystem();
      
      WorkerStore store = new WorkerStore(download);
      Runnable listener = new TerminateListener(mode);
      ProcessContext context = new ProcessContext(mode, store, process, system);
      ProcessAgent agent = new ProcessAgent(context, level);
      
      agent.start(download, listener);
   }
}

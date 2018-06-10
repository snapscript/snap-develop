package org.snapscript.studio.service;

import org.snapscript.studio.project.config.ProcessConfiguration;

public interface ProcessLauncher {   
   ProcessDefinition launch(ProcessConfiguration configuration) throws Exception;
}
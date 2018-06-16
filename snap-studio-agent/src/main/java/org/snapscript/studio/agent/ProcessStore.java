package org.snapscript.studio.agent;

import org.snapscript.common.store.Store;

public interface ProcessStore extends Store{
   void update(String project);
}

package org.snapscript.studio.agent;

import org.snapscript.common.store.Store;

public interface ProjectStore extends Store{
   void update(String project);
}

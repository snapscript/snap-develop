package org.snapscript.studio.service;

import org.snapscript.studio.common.ProgressListener;

public interface SplashPanel extends ProgressListener {   
   void show(long duration);
   void hide();
}

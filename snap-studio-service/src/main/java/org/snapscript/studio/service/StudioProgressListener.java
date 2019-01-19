package org.snapscript.studio.service;

import org.springframework.boot.context.event.ApplicationStartingEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class StudioProgressListener implements ApplicationListener<ApplicationStartingEvent> {

   @Override
   public void onApplicationEvent(ApplicationStartingEvent event) {
      ProgressManager.getProgress().update("Loading components");
   }

}

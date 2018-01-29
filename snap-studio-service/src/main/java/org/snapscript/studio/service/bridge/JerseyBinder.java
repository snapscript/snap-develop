package org.snapscript.studio.service.bridge;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.springframework.stereotype.Component;

@Component
public class JerseyBinder extends AbstractBinder {

   @Override
   protected void configure() {
      bind(JerseyExample.class);
   }

}

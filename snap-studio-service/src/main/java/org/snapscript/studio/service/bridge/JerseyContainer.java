package org.snapscript.studio.service.bridge;

import java.lang.reflect.Constructor;
import java.net.URI;

import javax.ws.rs.core.Application;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.simple.SimpleContainer;
import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class JerseyContainer implements ApplicationContextAware {

   public static void main(String[] list) throws Exception {
      SpringApplication.run(JerseyContainer.class, list);
   }

   @Override
   public void setApplicationContext(ApplicationContext context) throws BeansException {
      try {
         ResourceConfig config = new ResourceConfig(JerseyExample.class);
         JerseyServiceLocatorFeature feature = new JerseyServiceLocatorFeature((ConfigurableApplicationContext)context);
         JerseyBinder binder = new JerseyBinder();
         URI address = new URI("http://localhost:8867");
         
         config.register(feature);
         config.register(binder);
         
         Constructor<SimpleContainer> constructor = SimpleContainer.class.getDeclaredConstructor(Application.class);
         constructor.setAccessible(true);
         SimpleContainer container = constructor.newInstance(config);
         JerseyServer.create(address, null, container, null);
      } catch(Exception e) {
         throw new IllegalStateException("Could not create container", e);
      }
   }
}

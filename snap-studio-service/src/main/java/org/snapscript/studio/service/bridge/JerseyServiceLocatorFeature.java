package org.snapscript.studio.service.bridge;

import javax.inject.Provider;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.ServiceLocatorProvider;
import org.jvnet.hk2.spring.bridge.api.SpringBridge;
import org.jvnet.hk2.spring.bridge.api.SpringIntoHK2Bridge;
import org.springframework.context.ConfigurableApplicationContext;

import com.google.common.base.Preconditions;

public class JerseyServiceLocatorFeature implements Feature, Provider<ServiceLocator> {

   private ConfigurableApplicationContext appContext;
   private ServiceLocator locator;

   public JerseyServiceLocatorFeature(ConfigurableApplicationContext appContext) {
       this.appContext = appContext;
   }

   @Override
   public ServiceLocator get() {
       return Preconditions.checkNotNull(locator, "Service locator is not yet available");
   }

   @Override
   public boolean configure(FeatureContext context) {
       locator =  ServiceLocatorProvider.getServiceLocator(context);

       SpringBridge.getSpringBridge().initializeSpringBridge(locator);
       SpringIntoHK2Bridge springBridge = locator.getService(SpringIntoHK2Bridge.class);
       springBridge.bridgeSpringBeanFactory(appContext.getBeanFactory());

       return true;
   }
}

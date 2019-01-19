package org.snapscript.studio.agent.runtime;

import static org.snapscript.studio.agent.runtime.RuntimeAttribute.HOST;

import java.net.InetAddress;

public class HostAddressValue implements RuntimeValue {

   @Override
   public String getName() {
      return HOST.name;
   }

   @Override
   public String getValue() {
      try {
         return InetAddress.getLocalHost().getCanonicalHostName();
      } catch(Exception e) {
         e.printStackTrace();
      }
      return null;
   }
}

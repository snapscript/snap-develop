package org.snapscript.studio.agent.runtime;

import static org.snapscript.studio.agent.runtime.RuntimeAttribute.OS;

public class OperatingSystemValue implements RuntimeValue {

   @Override
   public String getName() {
      return OS.name;
   }

   @Override
   public String getValue() {
      return System.getProperty("os.name");
   }
}

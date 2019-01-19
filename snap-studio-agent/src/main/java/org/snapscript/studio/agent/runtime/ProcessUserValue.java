package org.snapscript.studio.agent.runtime;

import static org.snapscript.studio.agent.runtime.RuntimeAttribute.USER;

public class ProcessUserValue implements RuntimeValue {

   @Override
   public String getName() {
      return USER.name;
   }

   @Override
   public String getValue() {
      return System.getProperty("user.name");
   }
}

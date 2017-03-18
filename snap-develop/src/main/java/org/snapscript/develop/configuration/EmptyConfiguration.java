
package org.snapscript.develop.configuration;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class EmptyConfiguration implements Configuration {
   
   public EmptyConfiguration() {
      super();
   }

   @Override
   public Map<String, String> getVariables() {
      return Collections.emptyMap();
   }

   @Override
   public List<File> getDependencies() {
      return Collections.emptyList();
   }

   @Override
   public List<String> getArguments() {
      return Collections.emptyList();
   }

}

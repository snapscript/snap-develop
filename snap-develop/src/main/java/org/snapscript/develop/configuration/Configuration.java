package org.snapscript.develop.configuration;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface Configuration {

   String PROJECT_FILE = ".project";
   String CLASSPATH_FILE = ".classpath";
   
   Map<String, String> getVariables();
   List<File> getDependencies();
   List<String> getArguments();
}

package org.snapscript.studio.project.config;

import java.util.Collections;
import java.util.Set;

public interface Dependency {
   String getGroupId();
   String getArtifactId();
   String getVersion(); 
   
   default Set<String> getExclusions() {
      return Collections.emptySet();
   }

   default String getDependencyKey(){
      String groupId = getGroupId();
      String artifactId = getArtifactId();
      
      return String.format("%s:%s", groupId, artifactId);
   }
   
   default String getDependencyFullName(){
      String groupId = getGroupId();
      String artifactId = getArtifactId();
      String version = getVersion();
      
      if(version != null) {
         return String.format("%s:%s:%s", groupId, artifactId, version);
      }
      return String.format("%s:%s", groupId, artifactId);
   }
}

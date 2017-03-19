
package org.snapscript.develop.command;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecuteCommand implements Command {

   private Map<String, Map<Integer, Boolean>> breakpoints;
   private String project;
   private String resource;
   private String system;
   private String source;
}

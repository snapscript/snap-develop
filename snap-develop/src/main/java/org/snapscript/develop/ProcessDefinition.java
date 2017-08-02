package org.snapscript.develop;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProcessDefinition {
   private final Process process;
   private final String name;
}
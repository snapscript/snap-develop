package org.snapscript.studio.service.core;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProcessDefinition {
   private final Process process;
   private final String name;
}
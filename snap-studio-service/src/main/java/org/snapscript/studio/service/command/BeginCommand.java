package org.snapscript.studio.service.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BeginCommand implements Command {

   private String resource;
   private String process;
   private long duration;
   private boolean debug;
}
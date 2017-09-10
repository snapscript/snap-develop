package org.snapscript.develop.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatusCommand implements Command {

   private String project;
   private String resource;
   private String process;
   private String system;
   private boolean focus;
   private boolean running;
   private boolean debug;
   private long totalMemory;
   private long usedMemory;
   private int threads;
   private long time;
}
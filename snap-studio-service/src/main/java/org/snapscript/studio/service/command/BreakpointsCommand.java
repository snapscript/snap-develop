package org.snapscript.studio.service.command;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BreakpointsCommand implements Command {

   protected Map<String, Map<Integer, Boolean>> breakpoints;
   protected String project;
}
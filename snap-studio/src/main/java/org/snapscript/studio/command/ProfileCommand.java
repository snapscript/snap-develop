package org.snapscript.studio.command;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.snapscript.studio.agent.profiler.ProfileResult;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileCommand implements Command {
   
   private Set<ProfileResult> results;
   private String process;
}
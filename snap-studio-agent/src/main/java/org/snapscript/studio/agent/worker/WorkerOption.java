package org.snapscript.studio.agent.worker;

import static java.util.Collections.EMPTY_LIST;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.snapscript.studio.agent.ProcessMode;
import org.snapscript.studio.agent.cli.CommandLineBuilder;
import org.snapscript.studio.agent.cli.CommandOption;
import org.snapscript.studio.agent.log.LogLevel;

public enum WorkerOption implements CommandOption {
   HOST("h", "host", "download host", ".+", String.class),
   PORT("p", "port", "download port", "\\d+", Integer.class),
   NAME("n", "name", "name of the process", ".+", String.class),
   LEVEL("l", "level", "log level", "(TRACE|DEBUG|INFO)", String.class, LogLevel.INFO),
   MODE("m", "mode", "run mode to use", "(SCRIPT|SERVICE)", String.class, ProcessMode.SCRIPT);

   public final Pattern pattern;
   public final String description;
   public final Object value;
   public final String name;
   public final String code;
   public final Class type;

   private WorkerOption(String code, String name, String description, String pattern, Class type) {
      this(code, name, description, pattern, type, null);
   }
   
   private WorkerOption(String code, String name, String description, String pattern, Class type, Object value) {
      this.pattern = Pattern.compile(pattern);
      this.description = description;
      this.value = value;
      this.name = name;
      this.code = code;
      this.type = type;
   }

   @Override
   public String getCode() {
      return code;
   }

   @Override
   public String getName() {
      return name;
   }

   @Override
   public String getDescription() {
      return description;
   }

   @Override
   public Object getDefault() {
      return value;
   }
   
   @Override
   public Pattern getPattern() {
      return pattern;
   }

   @Override
   public Class getType() {
      return type;
   }
   
   public static CommandLineBuilder getBuilder(){
      WorkerOption[] options = WorkerOption.values();
      
      if(options.length > 0) {
         List<WorkerOption> list = new ArrayList<WorkerOption>();
         CommandLineBuilder parser = new CommandLineBuilder(list);
         
         for(WorkerOption option : options) {
            list.add(option);
         }
         return parser;
      }
      return new CommandLineBuilder(EMPTY_LIST);
   }
}

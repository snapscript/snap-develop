package org.snapscript.studio.agent.worker;

import static java.util.Collections.EMPTY_LIST;

import java.util.ArrayList;
import java.util.List;

import org.snapscript.studio.agent.cli.CommandLineBuilder;
import org.snapscript.studio.agent.cli.CommandOption;
import org.snapscript.studio.agent.cli.CommandOptionParser;
import org.snapscript.studio.agent.local.LocalOption;

public enum WorkerOption implements CommandOption {
   HOST("h", "host", "download host", String.class),
   PORT("p", "port", "download port", Integer.class),
   NAME("n", "name", "name of the process", String.class),
   LEVEL("l", "level", "log level", String.class, "INFO"),
   MODE("m", "mode", "run mode to use", String.class, "SCRIPT");

   public final String description;
   public final String value;
   public final String name;
   public final String code;
   public final Class type;

   private WorkerOption(String code, String name, String description, Class type) {
      this(code, name, description, type, null);
   }
   
   private WorkerOption(String code, String name, String description, Class type, String value) {
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
   public String getDefault() {
      return value;
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

package org.snapscript.studio.agent.cli;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CommandLineBuilder {
   
   private final List<? extends CommandOption> options;
   private final CommandOptionParser parser;

   public CommandLineBuilder(List<? extends CommandOption> options) {
      this.parser = new CommandOptionParser(options);
      this.options = options;
   }
   
   public CommandLine build(String[] arguments) {
      Map<String, Object> map = new LinkedHashMap<String, Object>();
      List<String> values = new ArrayList<String>();
      
      for(String argument: arguments) {
         CommandValue value = parser.parse(argument);
         Object object = value.getValue();
         String name = value.getName();
         
         if(name != null) {
            map.put(name, object);
         } else {
            values.add((String)object);
         }
      }
      for(CommandOption option : options){
         String name = option.getName();
         
         if(!map.containsKey(name)) {
            String token = option.getDefault();
            
            if(token != null) {
               Class type = option.getType();
               Object value = parser.convert(token, type);
               
               map.put(name, value);
            }
         }
      }
      String[] remainder = values.toArray(new String[]{});
      return new CommandLine(options, map, remainder);
   }
}

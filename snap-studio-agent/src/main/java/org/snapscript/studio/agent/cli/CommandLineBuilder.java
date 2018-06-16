package org.snapscript.studio.agent.cli;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
      Set<String> done = new HashSet<String>();
      
      for(CommandOption option : options){
         String name = option.getName();
         String code = option.getCode();
         
         if(!done.add(name)) {
            throw new IllegalStateException("Option '" + name + "' declared twice");
         }
         if(!done.add(code)) {
            throw new IllegalStateException("Option '" + code + "' declared twice");
         }
      }
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

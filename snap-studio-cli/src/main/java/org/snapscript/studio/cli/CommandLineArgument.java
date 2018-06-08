package org.snapscript.studio.cli;

public enum CommandLineArgument {
   DIRECTORY("d", "directory", "specify directory to execute in"),
   URL("u", "url", "specify a URL to download sources from"),
   SCRIPT("s", "script", "script to execute"),
   EXPRESSION("e", "expression", "expression to evaluate"),
   CLASSPATH("cp", "classpath", "optional classpath file"),
   VERBOSE("v", "verbose", "enable verbose logging", "false"),
   CHECK("c", "check", "compile script only", "false");

   public final String description;
   public final String value;
   public final String name;
   public final String code;

   private CommandLineArgument(String code, String name, String description) {
      this(code, name, description, null);
   }
   
   private CommandLineArgument(String code, String name, String description, String value) {
      this.description = description;
      this.value = value;
      this.name = name;
      this.code = code;
   }
   
   public boolean isDirectory(){
      return this == DIRECTORY;
   }

   public boolean isScript(){
      return this == SCRIPT;
   }
   
   public boolean isExpression(){
      return this == EXPRESSION;
   }
   
   public boolean isClassPath(){
      return this == CLASSPATH;
   }

   public boolean isVerbose(){
      return this == VERBOSE;
   }

   public boolean isCheck(){
      return this == CHECK;
   }
   
   public boolean isURL(){
      return this == URL;
   }
   
   public static CommandLineArgument resolveArgument(String token){
      CommandLineArgument[] arguments = CommandLineArgument.values();
      
      for(CommandLineArgument argument : arguments) {
         if(argument.code.equalsIgnoreCase(token)) {
            return argument;
         }
         if(argument.name.equalsIgnoreCase(token)) {
            return argument;
         }
      }
      return null;
   }
}

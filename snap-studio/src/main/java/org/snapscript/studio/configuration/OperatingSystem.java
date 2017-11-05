package org.snapscript.studio.configuration;

public enum OperatingSystem {
   WINDOWS("explorer \"%{resource}\"", "cmd /c \"cd %{resource}\" & start cmd"),
   MAC("open \"%{resource}\"", "open -a Terminal \"%{resource}\""),
   LINUX("bash \"${resource}\"", "bash \"${resource}\"");

   private final String explore;
   private final String terminal;

   private OperatingSystem(String explore, String terminal) {
      this.explore = explore;
      this.terminal = terminal;
   }
   
   public String createExploreCommand(String resource) {
      return explore.replace("%{resource}", resource);
   }
   
   public String createTerminalCommand(String resource) {
      return terminal.replace("%{resource}", resource);
   }
   
   public static OperatingSystem resolveSystem() {
      OperatingSystem[] values = OperatingSystem.values();
      String system = System.getProperty("os.name");
      String token = system.toLowerCase();
      
      for(OperatingSystem os : values) {
         if(token.startsWith(os.name().toLowerCase())) {
            return os;
         }
      }
      return WINDOWS;
   }
}

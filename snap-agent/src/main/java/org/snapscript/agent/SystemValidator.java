package org.snapscript.agent;

import org.snapscript.core.Path;
import org.snapscript.core.link.PackageLinker;

public class SystemValidator {
   
   private static final String SOURCE =
   "class InternalTypeForScriptAgent {\n"+
   "   static const ARR = [\"a\",\"b\",\"c\"];\n"+
   "   var x;\n"+
   "   new(index){\n"+
   "      this.x=ARR[index];\n"+
   "   }\n"+
   "   dump(){\n"+
   "      println(x);\n"+
   "   }\n"+
   "}\n"+
   "var privateVariableInScriptAgent = new InternalTypeForScriptAgent(1);\n"+
   "privateVariableInScriptAgent.dump();\n"+
   "println(privateVariableInScriptAgent.x);\n"+
   "println(InternalTypeForScriptAgent.ARR);";
   
   private final ProcessContext context;
   
   public SystemValidator(ProcessContext context) {
      this.context = context;
   }

   public void validate() {
      PackageLinker linker = context.getLinker();
      
      try {
         linker.link(new Path("/internalPrivateScript.snap"), SOURCE, "script");
      }catch(Exception e) {
         e.printStackTrace();
      }
   }
}

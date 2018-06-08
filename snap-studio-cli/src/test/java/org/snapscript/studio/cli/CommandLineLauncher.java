package org.snapscript.studio.cli;

import org.snapscript.studio.cli.CommandLineInterpreter;

public class CommandLineLauncher {

   public static void main(String[] list) throws Exception {
//      ScriptService.main(
//            new String[] {
//            "--url=https://github.com/snapscript/snap-develop/raw/master/snap-develop/work/games",
//            "--script=mario.MarioGame",
//            "--verbose"});
      
      CommandLineInterpreter.main(
            new String[] {
            "--e=println(x)",
            "--x=10"});
   }
}

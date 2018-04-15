package org.snapscript.studio.service;

import org.snapscript.studio.cli.ScriptService;

public class ScriptLauncher {

   public static void main(String[] list) throws Exception {
//      ScriptService.main(
//            new String[] {
//            "--url=https://github.com/snapscript/snap-develop/raw/master/snap-develop/work/games",
//            "--script=mario.MarioGame",
//            "--verbose"});
      
      ScriptService.main(
            new String[] {
            "--e=println(x)",
            "--x=10"});
   }
}

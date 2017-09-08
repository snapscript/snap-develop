package org.snapscript.service;

public class ScriptLauncher {

   public static void main(String[] list) throws Exception {
      ScriptService.main(
            new String[] {
            "--root=https://github.com/snapscript/snap-develop/raw/master/snap-develop/work/games",
            "--script=mario.MarioGame"});
   }
}

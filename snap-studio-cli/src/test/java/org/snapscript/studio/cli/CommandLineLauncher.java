package org.snapscript.studio.cli;

import org.snapscript.studio.cli.CommandLineInterpreter;

public class CommandLineLauncher {

   public static void main(String[] list) throws Exception {
//      ScriptService.main(
//            new String[] {
//            "--url=https://github.com/snapscript/snap-develop/raw/master/snap-develop/work/games",
//            "--script=mario.MarioGame",
//            "--verbose"});
      
//      CommandLineInterpreter.main(
//            new String[] {
//            "--e=println(x)",
//            "--x=10"});
      
//      CommandLineInterpreter.main(
//            new String[] {
//            "--cp=c:/Work/development/snapscript/snap-release/../snap-develop/snap-studio/work/demo/physics/src;c:/Work/development/snapscript/snap-release/../snap-develop/snap-studio/work/demo/physics/assets/",      
//            "--s=wireframe/render3d.snap",
//            "--v=true"});     
      
      CommandLineInterpreter.main(
            new String[] {
            "--cp=../snap-studio/work/demo/games/src;../snap-studio/work/demo/games/assets/",      
            "--s=mario/MarioGame.snap",
            "--p=7799",
            "--v=true"});         
   }
}

package org.snapscript.studio.common.find;

import org.snapscript.studio.common.find.LiteralMatchEvaluator;
import org.snapscript.studio.common.find.MatchEvaluator;

import junit.framework.TestCase;

public class LiteralMatchEvaluatorTest extends TestCase {
   
   public void testLineMatcher() throws Exception {
      MatchEvaluator matcher = new LiteralMatchEvaluator("FileSystem", false, "#6495ed", "#ffffff", true);
      String result = matcher.match("this is a FileSystem and a filesystem ok");
      assertEquals("this is a <span style='background-color: #6495ed; color: #ffffff; font-weight: bold;'>FileSystem</span> and a <span style='background-color: #6495ed; color: #ffffff; font-weight: bold;'>filesystem</span> ok", result);
      
   }

}

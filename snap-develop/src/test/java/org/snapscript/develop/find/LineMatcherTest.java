package org.snapscript.develop.find;

import junit.framework.TestCase;

public class LineMatcherTest extends TestCase {
   
   public void testLineMatcher() throws Exception {
      MatchEvaluator matcher = new MatchEvaluator("FileSystem", "#6495ed", "#ffffff", true);
      String result = matcher.match("this is a FileSystem and a filesystem ok", false);
      assertEquals("this is a <span style='background-color: #6495ed; color: #ffffff; font-weight: bold;'>FileSystem</span> and a <span style='background-color: #6495ed; color: #ffffff; font-weight: bold;'>filesystem</span> ok", result);
      
   }

}

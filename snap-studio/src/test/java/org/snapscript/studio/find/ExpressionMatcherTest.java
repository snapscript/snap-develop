package org.snapscript.studio.find;

import org.snapscript.studio.find.ExpressionResolver;

import junit.framework.TestCase;

public class ExpressionMatcherTest extends TestCase {
   
   public void testMatcher() throws Exception {
      assertEquals(new ExpressionResolver("*ello").match("hello dolly"), "hello");
      assertEquals(new ExpressionResolver("*ello").match("this is hello dolly"), "this is hello");
      assertEquals(new ExpressionResolver("ello").match("this is hello dolly"), "ello");
      assertEquals(new ExpressionResolver("?ello").match("this is hello dolly"), "hello");
      assertEquals(new ExpressionResolver("llo*dol").match("this is hello dolly"), "llo dol");
      assertEquals(new ExpressionResolver("llo**dol").match("this is hello dolly"), "llo dol");
      assertEquals(new ExpressionResolver("*llo**dol").match("this is hello dolly"), "this is hello dol");
      assertEquals(new ExpressionResolver("*llo**dol*").match("this is hello dolly"), "this is hello dolly");
   }

}

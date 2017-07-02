package org.snapscript.develop.cglib;

import java.lang.reflect.Method;

import junit.framework.TestCase;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class CglibProxyTest extends TestCase {
   
   public static abstract class SampleClass {
   
      public String execute(String value) {
         return implementMe(value);
      }
      
      public abstract String implementMe(String value);
   }

   public void testMethodInterceptor() throws Exception {
      Enhancer enhancer = new Enhancer();
      enhancer.setSuperclass(SampleClass.class);
      enhancer.setCallback(new MethodInterceptor() {
        @Override
        public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
          if(method.getDeclaringClass() != Object.class && method.getReturnType() == String.class && method.getName().equals("implementMe")) {
            return "value=" + args[0];
          } 
          return proxy.invokeSuper(obj, args);
        }
      });
      SampleClass proxy = (SampleClass) enhancer.create();
      assertEquals("value=foo", proxy.execute("foo"));
      proxy.hashCode(); // Does not throw an exception or result in an endless loop.
    }
   
   // someMethod() {
   //
   //    x++; <--- this must be from scope.get("x")
   //
   // }
}

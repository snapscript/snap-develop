package org.snapscript.develop.reflect;


public interface Accessor {
   <T> T getValue(Object source);
   Class getType();
}

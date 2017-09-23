package org.snapscript.studio.reflect;


public interface Accessor {
   <T> T getValue(Object source);
   Class getType();
}
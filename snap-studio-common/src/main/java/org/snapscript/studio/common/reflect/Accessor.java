package org.snapscript.studio.common.reflect;


public interface Accessor {
   <T> T getValue(Object source);
   Class getType();
}
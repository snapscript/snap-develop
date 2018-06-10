package org.snapscript.studio.service;

public interface ProcessNameFilter {
   String generate();
   boolean accept(String name);
}
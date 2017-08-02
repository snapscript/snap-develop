package org.snapscript.develop.complete;

import java.util.Map;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TypeNodeReference {

   public static final String CLASS = "class";
   public static final String MODULE = "module";
   
   private final Map<String, Set<Integer>> functions;
   private final Set<String> properties;
   private final String name;
   private final String resource;
   private final String type;
}
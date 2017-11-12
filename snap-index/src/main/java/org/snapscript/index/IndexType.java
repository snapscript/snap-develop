package org.snapscript.index;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public enum IndexType {
   SCRIPT("script"),
   IMPORT("import", "script", "module"),
   FUNCTION("function", "script"), 
   CONSTRUCTOR("constructor", "class", "enum"), 
   MEMBER_FUNCTION("member-function", "module", "class", "trait", "enum"), 
   VARIABLE("variable", "script", "function", "member-function", "constructor", "compound"), 
   PROPERTY("property", "module", "class", "enum", "trait"), 
   CLASS("class", "script", "module", "class", "trait", "enum"),
   ENUM("enum", "script", "module", "class", "trait", "enum"),
   TRAIT("trait", "script", "module", "class", "trait", "enum"),
   MODULE("module", "script"),
   PARAMETER("parameter", "function", "member-function", "constructor"),
   COMPOUND("compound", "function", "member-function", "constructor", "compound");

   
   private final Set<IndexType> types;
   private final String[] parents;
   private final String name;
   
   private IndexType(String name, String... parents) {
      this.types = new HashSet<IndexType>();
      this.parents = parents;
      this.name = name;
   }
   
   public boolean isRoot() {
      return this == SCRIPT;
   }
   
   public boolean isImport() {
      return this == IMPORT;
   }
   
   public boolean isType() {
      return this == CLASS ||
              this == ENUM ||
              this == TRAIT ||
              this == MODULE ||
              this == IMPORT;
   }
   
   public boolean isConstructor(){
      return this == CONSTRUCTOR;
   }
   
   public boolean isFunction(){
      return this == FUNCTION ||
              this == CONSTRUCTOR ||
              this == MEMBER_FUNCTION;
   }
   
   public boolean isCompound(){
      return this == COMPOUND;
   }

   public boolean isLeaf() {
      return this == PARAMETER ||
              this == VARIABLE ||
              this == PROPERTY ||
              this == IMPORT;
   }
   
   public String getName() {
      return name;
   }
   
   public Set<IndexType> getParentTypes() {
      if(types.isEmpty()) {
         for(int i = 0; i < parents.length; i++) {
            IndexType type = TYPES.get(parents[i]);
         
            if(type == null) {
               throw new IllegalStateException("Invalid index " + parents[i] + " for " + this);
            }
            types.add(type);
         }
      }
      return types;
   }
   
   private static final Map<String, IndexType> TYPES = new LinkedHashMap<String, IndexType>();
   
   static {
      IndexType[] types = IndexType.values();
      
      for(IndexType type : types) {
         TYPES.put(type.name, type);
      }
   }
}

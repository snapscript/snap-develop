package org.snapscript.studio.index.compile;

import org.snapscript.studio.index.tree.ArrayConstraintIndex;
import org.snapscript.studio.index.tree.ClassConstructorIndex;
import org.snapscript.studio.index.tree.ClassDefinitionIndex;
import org.snapscript.studio.index.tree.CompoundStatementIndex;
import org.snapscript.studio.index.tree.DeclarationIndex;
import org.snapscript.studio.index.tree.EnumConstructorIndex;
import org.snapscript.studio.index.tree.EnumDefinitionIndex;
import org.snapscript.studio.index.tree.GenericConstraintIndex;
import org.snapscript.studio.index.tree.ImportIndex;
import org.snapscript.studio.index.tree.ImportStaticIndex;
import org.snapscript.studio.index.tree.MemberFieldDeclarationIndex;
import org.snapscript.studio.index.tree.MemberFunctionIndex;
import org.snapscript.studio.index.tree.ModuleDefinitionIndex;
import org.snapscript.studio.index.tree.ModuleFunctionIndex;
import org.snapscript.studio.index.tree.ModulePropertyIndex;
import org.snapscript.studio.index.tree.ParameterDeclarationIndex;
import org.snapscript.studio.index.tree.ScriptFunctionIndex;
import org.snapscript.studio.index.tree.ScriptIndex;
import org.snapscript.studio.index.tree.TraitConstraintIndex;
import org.snapscript.studio.index.tree.TraitDefinitionIndex;
import org.snapscript.studio.index.tree.TraitReferenceIndex;
import org.snapscript.studio.index.tree.TypeHierarchyIndex;
import org.snapscript.studio.index.tree.TypeReferencePartIndex;

public enum IndexInstruction {
   DECLARATION(DeclarationIndex.class, "declaration"),
   ARRAY_CONSTRAINT(ArrayConstraintIndex.class, "array-constraint"),
   GENERIC_CONSTRAINT(GenericConstraintIndex.class, "generic-constraint"),
   LIST_CONSTRAINT(GenericConstraintIndex.class, "list-constraint"),   
   SET_CONSTRAINT(GenericConstraintIndex.class, "set-constraint"),   
   MAP_CONSTRAINT(GenericConstraintIndex.class, "map-constraint"),     
   TRAIT_CONSTRAINT(TraitConstraintIndex.class, "trait-constraint"),
   PARAMETER(ParameterDeclarationIndex.class, "parameter-declaration"),
   COMPOUND_STATEMENT(CompoundStatementIndex.class, "compound-statement"),
   SCRIPT_FUNCTION(ScriptFunctionIndex.class, "script-function"),
   CLASS_HIERARCHY(TypeHierarchyIndex.class, "class-hierarchy"),
   CLASS_DEFINITION(ClassDefinitionIndex.class, "class-definition"),
   CLASS_FUNCTION(MemberFunctionIndex.class, "class-function"),
   CLASS_CONSTRUCTOR(ClassConstructorIndex.class, "class-constructor"),
   CLASS_FIELD_DECLARATION(MemberFieldDeclarationIndex.class, "class-field-declaration"),
   CLASS_REFERENCE_PART(TypeReferencePartIndex.class, "type-reference-part"),
   ENUM_HIERARCHY(TypeHierarchyIndex.class, "enum-hierarchy"),
   ENUM_DEFINITION(EnumDefinitionIndex.class, "enum-definition"),
   ENUM_FUNCTION(MemberFunctionIndex.class, "enum-function"),
   ENUM_CONSTRUCTOR(EnumConstructorIndex.class, "enum-constructor"),
   ENUM_FIELD_DECLARATION(MemberFieldDeclarationIndex.class, "enum-field-declaration"),
   TRAIT_HIERARCHY(TypeHierarchyIndex.class, "trait-hierarchy"),
   TRAIT_DEFINITION(TraitDefinitionIndex.class, "trait-definition"),   
   TRAIT_FUNCTION(MemberFunctionIndex.class, "trait-function"), 
   TRAIT_REFERENCE_PART(TypeReferencePartIndex.class, "trait-reference-part"),
   TRAIT_FIELD_DECLARATION(MemberFieldDeclarationIndex.class, "trait-field-declaration"),
   TRAIT_REFERENCE(TraitReferenceIndex.class, "trait-reference"),
   MODULE_DEFINITION(ModuleDefinitionIndex.class, "module-definition"),
   MODULE_FUNCTION(ModuleFunctionIndex.class, "module-function"),
   MODULE_PROPERTY(ModulePropertyIndex.class, "module-property"),
   IMPORT(ImportIndex.class, "import"),
   IMPORT_STATIC(ImportStaticIndex.class, "import-static"),
   SCRIPT(ScriptIndex.class, "script");
   
   public final Class type;
   public final String name;
   
   private IndexInstruction(Class type, String name){
      this.type = type;
      this.name = name;
   }

   public String getName(){
      return name;
   }
   
   public Class getType(){
      return type;
   }
}
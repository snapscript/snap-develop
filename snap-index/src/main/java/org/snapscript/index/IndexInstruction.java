package org.snapscript.index;

import org.snapscript.index.tree.ClassConstructorIndex;
import org.snapscript.index.tree.ClassDefinitionIndex;
import org.snapscript.index.tree.CompoundStatementIndex;
import org.snapscript.index.tree.DeclarationIndex;
import org.snapscript.index.tree.EnumConstructorIndex;
import org.snapscript.index.tree.EnumDefinitionIndex;
import org.snapscript.index.tree.ImportIndex;
import org.snapscript.index.tree.ImportStaticIndex;
import org.snapscript.index.tree.MemberFieldDeclarationIndex;
import org.snapscript.index.tree.MemberFunctionIndex;
import org.snapscript.index.tree.ModuleDefinitionIndex;
import org.snapscript.index.tree.ModuleFunctionIndex;
import org.snapscript.index.tree.ScriptFunctionIndex;
import org.snapscript.index.tree.ScriptIndex;
import org.snapscript.index.tree.TraitDefinitionIndex;
import org.snapscript.index.tree.TypeReferencePartIndex;

public enum IndexInstruction {
   DECLARATION(DeclarationIndex.class, "declaration"),
   COMPOUND_STATEMENT(CompoundStatementIndex.class, "compound-statement"),
   SCRIPT_FUNCTION(ScriptFunctionIndex.class, "script-function"),
   CLASS_DEFINITION(ClassDefinitionIndex.class, "class-definition"),
   CLASS_FUNCTION(MemberFunctionIndex.class, "class-function"),
   CLASS_CONSTRUCTOR(ClassConstructorIndex.class, "class-constructor"),
   CLASS_FIELD_DECLARATION(MemberFieldDeclarationIndex.class, "class-field-declaration"),
   CLASS_REFERENCE_PART(TypeReferencePartIndex.class, "type-reference-part"),
   ENUM_DEFINITION(EnumDefinitionIndex.class, "enum-definition"),
   ENUM_FUNCTION(MemberFunctionIndex.class, "enum-function"),
   ENUM_CONSTRUCTOR(EnumConstructorIndex.class, "enum-constructor"),
   ENUM_FIELD_DECLARATION(MemberFieldDeclarationIndex.class, "enum-field-declaration"),
   TRAIT_DEFINITION(TraitDefinitionIndex.class, "trait-definition"),   
   TRAIT_FUNCTION(MemberFunctionIndex.class, "trait-function"), 
   TRAIT_REFERENCE_PART(TypeReferencePartIndex.class, "trait-reference-part"),
   TRAIT_FIELD_DECLARATION(MemberFieldDeclarationIndex.class, "trait-field-declaration"),
   MODULE_DEFINITION(ModuleDefinitionIndex.class, "module-definition"),
   MODULE_FUNCTION(ModuleFunctionIndex.class, "module-function"),
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
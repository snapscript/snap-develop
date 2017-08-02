package org.snapscript.template.translate;

import simple.page.Workspace;
import simple.http.serve.FileContext;
import simple.http.serve.Context;
import java.io.IOException;
import java.io.FileReader;
import java.io.Writer;
import java.io.File;
import java.util.Stack;

/**
 * The <code>Builder</code> object is used to build a document definition
 * for the specified JSP source file. This performs the lexical analysis
 * and parsing of the JSP document, and constructs a definition. This
 * definition contains all details for the JSP source, including the 
 * package name, runtime language, source path, and code segments, which
 * can be used to generate a source file to compile.
 * 
 * @author Niall Gallagher 
 */ 
final class Builder {

   /**
    * Represents the workspace used to translate and compile sources.
    */ 
   private Workspace project;        

   /**
    * Constructor for the <code>Builder</code> object. This is used
    * to build a document definition for specified sources. This will
    * load all JSP source requested from the workspace source path.
    *
    * @param project this is the workspace used by the builder
    */ 
   public Builder(Workspace project) {
      this.project = project;           
   }        

   /**
    * This method is used to build the document definition for the
    * specified JSP source file. This must be given an absolute URI 
    * path, which references a file within the workspace source path.
    * This method creates the root JSP document definition.
    *
    * @param name this is the URI path reference to the source
    *
    * @return this returns the document definition for the source
    */ 
   public Definition build(String name) throws IOException {
      return build(name, new Definition(project, name));              
   }

   /**
    * This method is used to add to an existing document definition. 
    * This method is used to build the root JSP document and included
    * JSP sources. This can be given a relative URI path or an absolute
    * URI path. For example "../path/File.jsp" can be used. Once the
    * document and its includes have been evaluated the definition is
    * returned, which contains all code segments required by the JSP.
    * 
    * @param name this is the location of the source to be evaluated
    * @param source this is the document definition to be augmented 
    */ 
   public Definition build(String name, Definition source) throws IOException{
      Stack stack = source.getContext();

      if(!name.startsWith("/")) {
         name = "" + stack.peek() + name;
      }
      String path = project.getDirectory(name);
      File file = project.getSourceFile(name);

      try {
         source.addInclude(name);              
         stack.push(path);         
         return build(file, source);      
      } finally {
         stack.pop();
      }
   }   
   
   /**
    * This method is used to add to an existing document definition. 
    * This method is used to build the root JSP document and included
    * JSP sources. This can be given a relative URI path or an absolute
    * URI path. For example "../path/File.jsp" can be used. Once the
    * document and its includes have been evaluated the definition is
    * returned, which contains all code segments required by the JSP.
    * 
    * @param file this is the location of the source to be evaluated
    * @param source this is the document definition to be augmented 
    */   
   private Definition build(File file, Definition source) throws IOException {
      Processor out = new Processor(source, this);
      FileReader data = new FileReader(file);
      char[] text = new char[512];

      while(true) {
         int count = data.read(text);
         if(count < 0){
            break;                 
         }
         out.write(text,0,count);
      }
      out.close();           
      return source;
   }
   
}
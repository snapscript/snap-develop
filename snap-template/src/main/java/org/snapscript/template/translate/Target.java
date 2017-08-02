package org.snapscript.template.translate;

import simple.util.parse.ParseBuffer;
import simple.util.parse.Parser;
import simple.page.Workspace;

/**
 * The <code>Target</code> object is used to parse the JSP path name
 * so that a package and class name can be created. The translator 
 * uses the path of the JSP to construct class properties, for example
 * take the JSP path "/some/path/Source.jsp" this will be used to
 * create the package name "some.path" and the class "SourcePage"
 * 
 * @author Niall Gallagher
 */ 
class Target extends Parser {
        
   /**
    * This is the target is the fully qualified class name.
    */         
   private ParseBuffer target;

   /**
    * This represents the name of the class minus the package.
    */ 
   private ParseBuffer name;

   /**
    * This object is used to gather the characters for the package.
    */ 
   private ParseBuffer scope;

   /**
    * This is used to store the directory the JSP file is in.
    */ 
   private ParseBuffer path;

   /**
    * This is the name of the JSP project workspace object.
    */ 
   private String prefix;

   /**
    * Constructor for the <code>Target</code> object. This will create
    * several buffers used to store the attributes for the target
    * page class. These buffers are used to accumulate characters.
    *
    * @param project this is the JSP project workspace instance
    */    
   public Target(Workspace project) {
      this.target = new ParseBuffer();           
      this.name = new ParseBuffer();
      this.path = new ParseBuffer();
      this.scope = new ParseBuffer();
      this.prefix = project.getName();         
   }
   
   /**
    * Constructor for the <code>Target</code> object. This will create
    * several buffers used to store the attributes for the target
    * page class. These buffers are used to accumulate characters.
    *
    * @param path this is the target path string that is to be parsed
    * @param project this is the JSP project workspace instance
    */     
   public Target(Workspace project, String path) {
      this(project);
      parse(path);
   }

   /**
    * The provides the name of the resulting object. This is used by
    * the code generator to determine the correct class name and
    * constructor to use, and may be used by the source compiler.
    *
    * @return this returns the name of the source object generated
    */ 
   public String getName() {
      return name.toString();
   }

   /**
    * Provides the fully qualified package name for the resulting
    * object. This is required by the compiler, so that once the
    * generated source has been compiled it can later be loaded as a
    * class by the compiler class loader and instantiated. 
    *
    * @return this returns the fully qualified class name targeted
    */    
   public String getTarget() {
      return target.toString();
   }

   /**
    * This is used by the source code generator to determine the
    * package the target object is using. This ensures that a correct
    * name space is given to each source, which avoids collisions.
    *
    * @return this returns the package name for the source object
    */    
   public String getPackage() {
      return scope.toString();
   }

   /**
    * This acquires the directory the source object is generated into.
    * This allows the generator to create the appropriate directory,
    * before generating the resulting Java or Groovy object into that
    * directory before compilation can proceed. This is a URI path.
    *
    * @return the directory to generate the source object into
    */    
   public String getDirectory() {
      return path.toString();           
   }

   /**
    * This will construct a path using the targeted JSP path and the
    * JSP project workspace name, if one is provided. The path is
    * typically a resource that can be acquired from the workspace,
    * however the target is given a prefix of the workspace name. So
    * if the target is "/demo/File.jsp" the augmented target would
    * be "/example/demo/File.jsp" if the workspace name was "example".
    *
    * @param path this is the path to be parsed for targeted JSP
    */ 
   public void parse(String path) {
      if(prefix != null) {
         path = "/" + prefix+ path;              
      }           
      super.parse(path);
   }
   
   /**
    * This method is used to parse each individual part of the target
    * including the name, package, and directory of the source object.
    * Once this has completed each part of the target will be 
    * buffered within the individual buffers of this object.
    */ 
   protected void parse() {
      name();
      path();
      scope();
      target();
   }
   
   /**
    * This will clear the target tokens so that this can be reused
    * by the builder. In practice this method just satisfies the
    * contract of the token so that this object is not abstract.
    */      
   protected void init() {
      name.clear();
      scope.clear();
      path.clear();      
      target.clear();
      off =0;
   }

   /**
    * This will extract the directory specified in the target path.
    * This is basically all path segments up the, but not including,
    * the name of the JSP. For example "/some/path/File.jsp" this
    * will extract the value "/some/path/".
    */ 
   public void path() {
      if(buf[off] !='/'){
         path.append('/');
      }
      path.append(buf,off,count+1);              
   }

   /**
    * This is used to concatenate the package name and the class name
    * extracted from the target path. If there is no package, that is
    * if the file is within the root directory, no package is used.
    */ 
   public void target() {
      if(scope.length()>0){
         target.append(scope);              
         target.append(".");
      }           
      target.append(name);
   }

   /**
    * This is used to extract the name of the class from the name of
    * the JSP file. For example if the path was "/example/File.jsp"
    * then the resulting class name generated would be "FilePage".
    */ 
   public void name() {
      int len = 0;

      while(count-- > off) {
         if(buf[count] =='/'){
            break;
         }        
         if(buf[count] =='.'){
            len = 0;
         } else {
            len++;                  
         }
      }
      name.append(buf,count+1,len);    
      name.append("Page");
   }

   /**
    * This is used to extract the package name of the target. This is
    * basically the target directory with '/' characters replaced with
    * '.' characters. All '/' characters except for the root are 
    * converted to a '.' delimited list of the path segments.
    */ 
   public void scope() {
      int pos = count;
      int len = 0;

      while(pos-- > off) {
         if(buf[pos] =='/'){
            if(pos == off) {
               off++;
               break;                    
            }
            buf[pos] ='.';                 
         }  
         len++;         
      }      
      scope.append(buf,off,len);
   }
}
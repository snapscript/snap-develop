package org.snapscript.template.translate;

import simple.page.Workspace;
import java.util.ArrayList;
import java.util.Stack;               
import java.util.List;
import java.util.Date;
import java.io.File;

/**
 * A <code>Definition</code> object is used to gather information 
 * regarding a JSP source file. During translation, a JSP source file
 * undergoes a lexical analysis phase, where the file is broken up
 * into digestable tokens. These tokens are parsed and used in the
 * building phase of translation. As the building phase progresses
 * data is placed into a definition so that it can be used in a
 * generation step. For example, take the following token.
 * <pre>
 * 
 *    &lt;%@ page import=&quot;java.util.List,
 *                        java.util.Iterator,
 *                         java.util.Map&quot; %&gt;
 *
 * </pre>
 * When parsed, this generates a list of import packages, which will
 * be needed in the generation phase to compose the Java or Groovy
 * source file. So, during building, each import can be added to the
 * JSP <code>Definition</code>, where it can be later retrieved and
 * used by the source code generator. For more information on how
 * this object is used see the <code>Builder</code> object.
 * 
 * @author Niall Gallagher
 *
 * @see simple.page.translate.Builder
 */ 
class Definition implements Source {

   /**
    * Contains a list of libraries used by the root JSP source.
    */ 
   private ArrayList imports;

   /**
    * Contains a list of code and print statements to generate.
    */ 
   private ArrayList contents;

   /**
    * Contains the member functions and fields used by the page.
    */ 
   private ArrayList fields;   

   /**
    * Represents the workspace this object is generate within.
    */ 
   private Workspace project;

   /**
    * Contains information regarding the generated class file.
    */ 
   private Target target;

   /**
    * Keeps track of the current context of the evaluated JSP.
    */ 
   private Stack stack;

   /**
    * Keeps track of all included files used by the root JSP.
    */ 
   private Tracker files;

   /**
    * This specifies the runtime language used by the JSP page.
    */ 
   private String runtime;

   /**
    * This is the character encoding the page will write with.
    */ 
   private String charset;

   /**
    * This is the MIME type of the JSP page source contents. 
    */ 
   private String type;

   /**
    * Constructor for the <code>Definition</code> object. This is used
    * to create an object for collecting tokens parsed from the JSP
    * source file. This is used when translating the page into a Java
    * or Groovy JSP source. Information, such as the imorts, includes
    * code segments, runtime language, and so on. This object is used
    * in the translation and generation phase of the JSP processing.
    *
    * @param target this is the URI path to the translated JSP file
    */ 
   public Definition(Workspace project, String target) {
      this.target = new Target(project, target);           
      this.files = new Tracker(project);
      this.contents = new ArrayList();
      this.imports = new ArrayList();
      this.fields = new ArrayList();
      this.stack = new Stack();
      this.project = project;
   }

   /**
    * This provides a key component for the JSP engine, which is used
    * to determine when a JSP page or its includes have expired. This
    * allows background compilation, and ensures that pages can be
    * edited and deployed without restarting the server.
    *
    * @return this returns a monitor to the referenced JSP sources
    */
   public Reference getReference() {
      return files;           
   }

   /**
    * This is used to acquire the source file generated. This is used
    * by the generator to create the source file. This makes use of
    * the language token to identify the name of the source file. For
    * eample if the runtime language is Groovy the file extension 
    * would be ".groovy" and ".java" when using the Java language.
    * This will also contain the directory to be used for the source.
    * <p>
    * The source file name is typically extracted using the data
    * taken from the JSP source file. For example, the JSP source
    * file "/example/demo/Test.jsp" would translate to a source
    * file name "/example/demo/TestPage.java" if the language was
    * Java. For groovy the file extension would be ".groovy".
    * 
    * @return this returns the file system path for the source file
    */ 
   public File getSource() {
      return project.getBuildFile(target.getDirectory() +
         getName() +"."+ getLanguage());
   }     

   /**
    * This is the directory the JSP source file. This is used in 
    * the genration and compilation phase to determine if a
    * directory needs to be created to host the generated source.
    * The generation and build files, that is, the ".java" and
    * ".class" files use a mirror of the JSP directory.
    *
    * @return this returns the directory used by the JSP file
    */ 
   public File getDirectory() {
      return project.getBuildFile(
            target.getDirectory());           
   }
   
   /**
    * This is used to acqure the name of the generated class file. 
    * This is the name of the class as it appears within the source.
    * For example a class name such as <code>ExamplePage</code> can
    * be used when generating the source JSP "/demo/Example.jsp".
    * Typically this appends <code>Page</code> to the JSP file name,
    * however this feature may be overloaded to avoid collisions.
    * 
    * @return this returns the name of the class generated
    */ 
   public String getName() {
      return target.getName();
   }

   /**
    * This is the fully qualified class name of the generated class.
    * This contains the class name and package, in a "." seperated
    * string. Typically the package name is taken from the path of
    * the generated JSP source. For example "/demo/Example.jsp" will
    * have the fully qualified name <code>demo.ExamplePage</code>.
    * <p>
    * This is used in the compilation phase, so that the generated
    * class can be loaded with a class loader and then instantiated
    * and used to serve content for a request.
    *
    * @return this returns the fully qualified package name
    */ 
   public String getTarget() {
      return target.getTarget();
   }

   /**
    * This is used to acquire the package name for the class file.
    * The generator uses this to declare the package name of the
    * source file. This is required so that there is a namespace
    * for the generated page, which avoids name collisions. This 
    * is taken from the source JSP file, so "/a/b/c/Demo.jsp"
    * would contain the package <code>a.b.c</code>.
    *
    * @return this returns the package for the source generated
    */ 
   public String getPackage() {
      return target.getPackage();
   }
   
   /**
    * This is used to acquire a context for includes when the scope 
    * of the JSP changes. This allows relative include paths to be
    * used, for instance if a path such as "../File.jsp" was used it
    * would require the that the includers parent directory contains
    * that source file. This is fine for a single heirarchy of JSP
    * includes, however consider the case where the includes are.
    * <pre>
    * 
    *    &lt;%@ include file=&quot;../File.jsp&quot; %&gt;
    *    &lt;%@ include file=&quot;../Example.jsp&quot; %&gt;
    *
    * </pre> 
    * So above, the main source JSP includes "File.jsp" from its
    * parent directory. Following that "File.jsp" includes the file
    * "Example.jsp" from its parent directory. It is obvoius from 
    * this that "Example.jsp" is not in the root JSP files parent
    * directory, relative to the root it is "../../Example.jsp". So
    * context must change so includes can cascade well.
    * 
    * @return this returns a stack that remembers the JSP context
    */         
   public Stack getContext(){
      return stack;           
   }

   /**
    * This is used to acquire the imports specified within the JSP
    * file. The imports acquired from the <code>List</code> object
    * provided are used in the generation phase of translation. 
    * The source produced via translation must contain all imports
    * for objects used by the JSP. This will provide a unique set
    * of imports from the root JSP and all included JSP files.
    *
    * @return this returns the imports to be used in the source
    */            
   public List getImports(){
      return imports;           
   }

   /**
    * Add an import line to the document definition. The imports
    * added to the document definition should be fully complete.
    * This means requires the the building phase produces some
    * code that is used to generate the resulting source file.
    * <pre>
    *
    *    import java.util.List;
    *    import java.util.Iterator;
    *
    * </pre>
    * The above tokens are examples of import lines that may be
    * included as imports. This are directly usable by both the
    * Java and Groovy source files, without modification.
    *
    * @param library this is an import to be added to the source
    */ 
   public void addImport(String library){
      imports.add(library);           
   }

   /**
    * This retrieves the list of declaration blocks included within
    * the JSP source. Declarations are segments within the source 
    * that provide member fields and functions to the resulting Java
    * or Groovy file generated. Member functions and fields will be
    * accessable from the class to perform repetitive tasks.
    *
    * @return this provides a list of the declarations specified
    */ 
   public List getDeclarations(){
      return fields;           
   }

   /**
    * Add a declaration block to the document definition. This is
    * used to insert member methods and fields to the generated Java
    * or Groovy source file. Declarations are defined within the JSP
    * source file using the <code>&lt;%!</code> tag. For example.
    * <pre>
    * 
    *       public String escape(String url) {
    *          return new URLEncoder.encode(url, "UTF-8");
    *       } 
    *       
    * </pre>
    * The above example declaration block could be added to the 
    * document and later generated as part of the Java or Groovy 
    * source file, where it could be conviniently used.
    *
    * @param field this is used to add functions or fields
    */ 
   public void addDeclaration(String field){
      fields.add(field);           
   }

   /**
    * This is used to acquire the lines, which are to be used within
    * the main page method. The will provide code segments and print
    * statements, which are used to provide the contents for the 
    * resulting request. During translation the JSP unescaped text
    * is converted into <code>print</code> statements to that it
    * can be compiled in such a way that it emits the text. 
    *
    * @return pring statements and code segments for the body
    */ 
   public List getContents(){
      return contents;           
   }

   /**
    * Add a code or print statement to to the definition. Each block
    * of code added to the definition must be in order of appearnce,
    * and all must be valid Groovy and Java statements. JSP text is
    * typically escaped as a <code>print</code> statement. 
    * <pre>
    *
    *    out.print("%lt;b&gt;Example Text&lt;/b&gt;\n");
    *    
    * </pre>
    * The above statement is an example of a <code>print</code> 
    * statement generated from some JSP markup. All contents in the
    * provided list are in order of appearence.
    * 
    * @param token code statement or block to add to the definition 
    */   
   public void addContent(String token){
      contents.add(token);           
   }

   /**
    * This provides the list of JSP files that be been used to compose 
    * the resulting source. The included files are identified using
    * a URI path that references a file from the <code>Context</code>
    * used. This allows a cascading context to search for the file, 
    * such that the file can move and change context, allowing the
    * translator and compiler to initiate compilation again.
    *
    * @return this returns a list of URI paths for JSP includes
    */ 
   public List getIncludes(){
      return files.getIncludes();
   }

   /**
    * This is used to include other JSP files or text files, which 
    * can be translated and compiled into the resulting source. The
    * included file is monitored by the JSP engine so that if the
    * file changes it can be compiled back in to the source.
    * <pre> 
    * 
    *    &lt;%@ include file="../Relative.jsp" %&gt;
    *    &lt;%@ include file="/path/Absolute.jsp" %&gt;
    *    
    * </pre>
    * All included files must be absolute so that its details can
    * be retrieved using a <code>Context</code> object. This allows
    * the modification times for the source to be determined. 
    *
    * @param target an absolute path to the included JSP file
    */ 
   public void addInclude(String target){
      files.addInclude(target);           
   }

   /**
    * This is used to determine the language the JSP has been written
    * in. This can be set to "groovy" or "java" with the "language"
    * attribute for the "page" token. This is used by the generator
    * and compiler to determine how to create an compile the source.
    * <pre>
    *
    *    &lt;%@ page language="groovy" %&gt;
    * 
    * </pre> 
    * The above is an example of how the "language" attribute can
    * be used from within a JSP page. If this attribute is not
    * specified the runtime language defaults to Java.
    *
    * @return the runtime language used by the JSP source
    */ 
   public String getLanguage(){
      if(runtime == null) {
         return "java";              
      }           
      return runtime;           
   }

   /**
    * Specify the language that this page is using. Typically this
    * will be used if the runtime language is Groovy. If this is
    * not set the language defaults to Java. Once set the page can
    * be generated and compiled using the specified language. This
    * takes either "groovy" or "java" as this are supported.
    *
    * @param runtime this is the runtime language used by the JSP 
    */ 
   public void setLanguage(String runtime){
      this.runtime = runtime;           
   }

   /**
    * This is used to acquire the charset for the page. Typically
    * this will be null, and defaults to "UTF-8" within the 
    * generated page. The charset provided by this must be a Java
    * supported character encoding, for example "ISO-8858-1". 
    * <pre>
    *
    *    &lt;%@ page contentType="text/xhtml; charset=UTF-8" %&gt;
    * 
    * </pre>
    * The charset is extracted from the content type specification.
    * The "charset" attribute within the content type directive is
    * optional, and again if not specified it defaults to "UTF-8".
    * 
    * @return provides the charset specified or null for UTF-8
    */ 
   public String getCharset(){
      return charset;           
   } 

   /**
    * This is used to specify the character encoding used for the
    * page. The charset specified must be a valid Java encoding
    * such as UTF-8, ISO-8859-1, UCS2, and so on. If this has 
    * not been set the default character encoding is UTF-8.
    *
    * @param charset valid character encoding used for the JSP
    */            
   public void setCharset(String charset){
      this.charset = charset;           
   }

   /**
    * This is used to acquire the MIME type for the page. This
    * like the charset is taken from the "page" directive. If no
    * type is specified, that is, if this returns null, then the
    * file extension is used to acquire the MIME type.
    * <pre>
    *
    *    &lt;%@ page contentType="text/xhtml; charset=UTF-8" %&gt;
   * 
    * </pre>
    * The MIME type is extracted from the "contentType" attribute
    * within the page directive. This should be a valid type as
    * is acceptable within the HTTP Content-Type header.
    *
    * @return this returns null, or the MIME type of the JSP
    */ 
   public String getType() {
      return type;           
   }

   /**
    * This is used to specify the MIME type encoding used for the
    * JSP. This MIME type must be valid and acceptable as a value
    * for the HTTP Content-Type header. If no MIME type is 
    * specified, then the file extension is used to map the type.
    * 
    * @param type this is the token used to specify the type
    */            
   public void setType(String type){
      this.type = type;           
   }

   /**
    * This is used to provide the modification date for the source
    * translation. This is useful for debugging issues as it is 
    * possible to determine the version of the JSP source was used
    * to convert the page to either Groovy or Java source.
    *
    * @return this returns the date the source was translated
    */ 
   public Date getDate() {
      return new Date();           
   }   
}
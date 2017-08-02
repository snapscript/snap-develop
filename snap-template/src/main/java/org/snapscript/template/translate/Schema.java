package org.snapscript.template.translate;

import freemarker.template.Template;
import java.util.HashMap;
import java.util.Map;
import java.io.Writer;

/**
 * The <code>Schema</code> object is used to define an attribute set
 * that can be used to compose a JSP page. This is used to acquire all
 * details regarding a JSP document definition, it is provided to a
 * templating layer, where attributes are retrieved and used to compose
 * the resulting source, which can be Groovy or Java. 
 * <p>
 * The attributes that can be acquired from the document definition
 * are taken via this hash map from the Freemarker template. They
 * include the JSP imports, the content type, and the charset.
 *
 * @author Niall Gallagher
 *
 * @see simple.page.translate.Generator
 */ 
final class Schema extends HashMap {

   /**
    * This is the actual template used to render the source.
    */ 
   private Template template;

   /**
    * This is the source object associated with this schema.
    */
   private Definition source;
           
   /**
    * Constructor for the <code>Schema</code> object. This is used
    * to wrap the provided template, which will be used to render 
    * the attributes provided by the schema. All attributes are 
    * taken from the provided document definition.
    *
    * @param template this is the template used for rendering 
    * @param source this is the document definition to be used
    */ 
   public Schema(Template template, Definition source) {
      this.template = template;           
      this.source = source;
   }

   /**
    * This is used to acquire attributes from the source definition.
    * This object restricts the available attributes, this keeps the
    * template layer from having to query the document definition
    * directly, which may change and force the templates to change.
    * 
    * @param name this is the name of the attribute to acquire
    *
    * @return this is the attribute acquired from the definition
    */   
   public Object get(Object name) {
      if(name.equals("charset")) {
         return source.getCharset();              
      }
      if(name.equals("type")) {
         return source.getType();             
      }
      if(name.equals("contents")) {
         return source.getContents();              
      }
      if(name.equals("declarations")) {
         return source.getDeclarations();              
      }
      if(name.equals("imports")) {
         return source.getImports();              
      }
      if(name.equals("package")) {
         return source.getPackage();
      }
      if(name.equals("name")) {
         return source.getName();              
      }
      if(name.equals("date")) {
         return source.getDate();              
      }
      return null;
   }

   /**
    * This method will use this scheme instance to write a source file
    * to the given <code>Writer</code>. The template is given all the
    * data via the schema attribute names. Once the schema is given to
    * the template the template will query it for required attributes.
    * 
    * @param target this is the writer used to emit the source to
    */ 
   public void write(Writer target) throws Exception {
      template.process(this, target);
   }
}
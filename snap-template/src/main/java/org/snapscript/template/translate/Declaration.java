package org.snapscript.template.translate;

/**
 * The <code>Declaration</code> object is used to parse declarations
 * from the JSP source. Declarations are member functions and fields
 * that will be added at the head of the generates source object.
 * </pre>
 *
 *    &lt;%!
 *          declaration
 *    %&gt;
 * 
 * </pre>
 * The above declaration token is parsed by this object. This will
 * basically remove the beginning and end tags an insert the 
 * remainder, that is, the declaration, into the definition object.
 *
 * @author Niall Gallagher
 */ 
class Declaration extends Token {

   /**
    * This is used to collect the token parsed from the declaration.
    */        
   private TokenBuffer member;
       
   /**
    * Constructor for the <code>Declaration</code> token. This will
    * create a buffer, which can be used to accumulate the data
    * extracted from the supplied declaration token.
    */    
   public Declaration() {
      this.member = new TokenBuffer();           
   }
   
   /**
    * Constructor for the <code>Declaration</code> token. This will
    * create a buffer, which can be used to accumulate the data
    * extracted from the supplied declaration token before parsing.
    *
    * @param token this is the declaration token to be parsed
    */         
   public Declaration(String token) {
      this();
      parse(token);
   }        

   /**
    * This method will supply code to the document definition that
    * will allow a declaration to be used by the page. The data
    * inserted into the definition will generated in the class.
    *
    * @param source this is the source to push the code into
    * @param builder this is the builder driving the process
    */ 
   public void process(Definition source, Builder builder) {
      source.addDeclaration(member.text());
   }

   /**
    * This will clear the declaration token so that the parse can be 
    * reused by the builder. In practice this method just satisfies
    * the contract of the token so that this object is not abstract.
    */     
   protected void init() {
      member.clear();           
   }

   /**
    * This is a very simple parse method which basically extracts the
    * begining and end values from the token. For instance this will
    * remove "&lt;%!" and "%&gt;" from the token supplied. 
    */ 
   protected void parse() {
      if(skip("<%!")) {
         member.append(buf,off,count-5);              
      }
   }
}
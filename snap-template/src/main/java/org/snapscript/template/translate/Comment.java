package org.snapscript.template.translate;

/**
 * The <code>Comment</code> object is used to parse a JSP comment. 
 * This performs a very simple parsing of the JSP comment provided.
 * This is parsed so that it can be included into the source.
 * <pre>
 *
 *    &lt;%-- comment --%&gt;
 * 
 * </pre>
 * The above expression is parsed an inserted into the JSP source
 * as an embedded comment starting with "/*" comment tag.
 * 
 * @author Niall Gallagher
 */ 
class Comment extends Token {

   /**
    * This is used to collect the token parsed from the print.
    */         
   private TokenBuffer comment;        
      
   /**
    * Constructor for the <code>Comment</code> token. This will
    * create a buffer, which can be used to accumulate the data
    * extracted from the supplied print token.
    */   
   public Comment() {
      this.comment = new TokenBuffer();           
   }
   
   /**
    * Constructor for the <code>Comment</code> token. This will
    * create a buffer, which can be used to accumulate the data
    * extracted from the supplied print token before parsing.
    *
    * @param token this is the insert token to be parsed
    */    
   public Comment(String token) {
      this();
      parse(token);      
   }   

   /**
    * This method will supply code to the document definition that
    * will allow a comment to be printed by the page. The data
    * inserted into the definition will be displayed in the body.
    *
    * @param source this is the source to push the code into
    * @param builder this is the builder driving the process
    */ 
   public void process(Definition source, Builder builder){
      source.addContent("/*" + comment + "*/");
   }        

   /**
    * This will clear the comment token so that the parse can be reused
    * by the builder. In practice this method just satisfies the
    * contract of the token so that this object is not abstract.
    */     
   protected void init() {
      comment.clear();           
   }

   /**
    * This is a very simple parse method which basically extracts the
    * begining and end values from the token. For instance this will
    * remove "&lt;%--" and "--%&gt;" from the token supplied. 
    */ 
   protected void parse() {
      if(skip("<%--")) {
         while(off < count) {      
            if(skip("--%>")) {        
               break;
            }
            if(skip("%>")){
               break;                    
            }
            comment.append(buf[off++]);              
         }
      }           
   }
}
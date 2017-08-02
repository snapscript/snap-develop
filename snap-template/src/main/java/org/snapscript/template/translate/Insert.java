package org.snapscript.template.translate;

/**
 * The <code>Insert</code> object is used to parse the insert token
 * from the JSP. This will parse the token in such a way the data
 * from the model can be inserted into the JSP page. The following
 * token is parsed using this token implementation.
 * <pre>
 *
 *    &lt;%@ insert name="example" %&gt;
 * 
 * </pre>
 * This will push contents into the document definition for getting
 * the named attribute from the page model and displaying it. The 
 * result is that this token allows attributes to be printed easily.
 * 
 * @author Niall Gallagher
 */ 
class Insert extends Token {

   /**
    * This is used to collect the token parsed from the insert.
    */ 
   private TokenBuffer name;

   /**
    * Constructor for the <code>Insert</code> token. This will
    * create a buffer, which can be used to accumulate the data
    * extracted from the supplied insert token.
    */ 
   public Insert() {
      this.name = new TokenBuffer();           
   }           

   /**
    * Constructor for the <code>Insert</code> token. This will
    * create a buffer, which can be used to accumulate the data
    * extracted from the supplied insert token before parsing.
    *
    * @param token this is the insert token to be parsed
    */     
   public Insert(String token) {
      this();
      parse(token);
   }        
        
   /**
    * This method will supply code to the document definition that
    * will allow an attribute to be printed by the page. The data
    * inserted into the definition will be displayed in the body.
    *
    * @param source this is the source to push the code into
    * @param builder this is the builder driving the process
    */ 
   public void process(Definition source, Builder builder) {
      source.addContent("model.write(out, \""+name+"\");");   
   }

   /**
    * This will clear the name token so that the parse can be reused
    * by the builder. In practice this method just satisfies the
    * contract of the token so that this object is not abstract.
    */ 
   protected void init() {
      name.clear();           
   }

   /**
    * This does not verify the token type, instead this will seek 
    * the '=' character. Once the '=' character has been encountered
    * the name is extracted as a quoted string, for example "name".
    */ 
   protected void parse() {
      scrap();
      name();      
   }

   /**
    * This is a quick and dirty means of parsing the token. This 
    * will basically seek the '=' character such the offset of
    * the buffer is on the start of the name, like "[n]ame".
    */ 
   private void scrap() {
      while(off < count){
         char next = buf[off];
         
         if(next == '='){
            while(off < count) {
               next = buf[off++];                                 
               if(quote(next)) {
                  break;
               }              
            }
            break;            
         }        
         off++;      
      }           
   }

   /**
    * This method will read all characters up to a space or the
    * next quotation chatacter, for example "'" or '"' will be
    * considered a terminal. Once this has finished the name will
    * be stored in the internal name buffer for processing.
    */ 
   private void name() {
      while(off < count) {
         char next = buf[off++];              

         if(space(next)) {
            break;                 
         }else if(quote(next)){
            break;                 
         }
         name.append(next);
      }           
   }

   /**
    * This is used to determine when the start and end of the name
    * has been encountered. The terminals are '"' and '"', which 
    * are legal quotations within the JSP syntax.
    *
    * @param ch this is the character to be evaluated
    *
    * @return this returns true if the character is a quote
    */  
   private boolean quote(char ch) {
      return ch == '"' || ch == '\'';
   }   
}
package org.snapscript.template.translate;

import simple.util.parse.ParseBuffer;

/**
 * The <code>Parser</code> object is used to digest the tokens emitted
 * from the lexer. This will delegate the token parsing to objects
 * implementing the <code>Token</code> class. Each token emitted from
 * the lexical analysis phase is classified using a classification
 * object. Once classified a specific token type processes the text.
 * 
 * @author Niall Gallagher
 */
final class Parser extends ParseBuffer {

   /**
    * This is the document definition that is populated via parsing.
    */         
   private Definition source;
        
   /**
    * This is used to classify the tokens emitted from the lexer.
    */ 
   private Classifier factory;

   /**
    * This is used if recursive processing of a token is required.
    */ 
   private Builder builder;
   
   /**
    * Constructor for the <code>Parser</code> object. This requires
    * the document definition to be constructed as well as the builder
    * driving the process, the builder is required for the recursive
    * evaluation of included JSP sources via the include directive.
    *
    * @param source this is the document definition to populate
    * @param builder this is the object that builds the definition
    */ 
   public Parser(Definition source, Builder builder) {
      this.factory = new Classifier();           
      this.builder = builder;
      this.source = source;
   }

   /**
    * Each parser implementation must specify the boundaries of the
    * tokens it is prepared to classify and digest. This implementation
    * uses the standard JSP tags "&lt;%" and "%&gt;", however it can
    * just as easily use the PHP tag "&lt;?" and "?&gt;".
    *
    * @param lexer this is the lexer that emits the required tokens
    */ 
   public void begin(Lexer lexer){
      lexer.match("<%", "%>");
   }

   /**
    * This method is invoked by the lexer and is used to process an 
    * array of characters, which signifies the full text for a token.
    * The text provided to this method is guaranteed to contain either
    * a complete token, or plain text which is not a parsable token.
    *
    * @param text this contains only a single token or plain text
    */ 
   public void parse(char[] text) {
      parse(text, 0, text.length);
   }


   /**
    * This method is invoked by the lexer and is used to process an 
    * array of characters, which signifies the full text for a token.
    * The text provided to this method is guaranteed to contain either
    * a complete token, or plain text which is not a parsable token.
    *
    * @param text this contains only a single token or plain text
    * @param off this is the offset within the buffer to read from
    * @param len the number of characters in the buffer to evaluate
    */ 
   public void parse(char[] text, int off, int len) {
      if(len > 2) {
         if(text[off] == '<' && text[off+1] =='%'){
            if(count > 0 ) {
               process(buf, 0, count);
               clear();               
            }
            process(text, off, len);
            len = 0;
         }
      } 
      if(len > 0) {
         append(text, off, len);
      }         
   }

   /**
    * Once a full token has been passed to the parser this method is
    * invoked to classify and process the token. This requires that
    * a full token is provided, this unlike the </code>parse</code>
    * method does not buffer the plain text tokens.
    *
    * @param text this contains only a single token or plain text
    * @param off this is the offset within the buffer to read from
    * @param len the number of characters in the buffer to evaluate
    */ 
   private void process(char[] text, int off, int len) {
      String data = new String(text, off, len);           
      Token token = factory.getToken(data);           

      try {
         token.process(source, builder);
      }catch(Exception e) {
         e.printStackTrace();              
      }              
   }

   /**
    * Once lexical analysis has finished this method is invoked. This
    * will force any buffered plain text to be processed so that the
    * full JSP source is processed and used in the document definition. 
    */ 
   public void finish(){
      if(count > 0){
         process(buf, 0, count);
      }              
   }
}
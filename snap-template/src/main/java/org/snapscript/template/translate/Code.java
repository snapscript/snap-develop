package org.snapscript.template.translate;

import java.util.ArrayList;
import java.util.List;

/**
 * The <code>Code</code> object is used to parse the code directive
 * from the JSP source. This performs a very simple parsing routine,
 * it will basically strip the code block into individual lines and
 * remove the start and end block from the source text.
 * <pre>
 *
 *    &lt;% code %&gt;
 * 
 * </pre>
 * The above expression is parsed from the source. The code portion
 * of the token is the only text added to the document definition.
 * 
 * @author Niall Gallagher
 */ 
class Code extends Token {

   /**
    * This is used to collect the token parsed from the code.
    */           
   private TokenBuffer line;

   /**
    * This is used to accumulate the lines of the code block. 
    */ 
   private List code;
       
   /**
    * Constructor for the <code>Code</code> token. This will
    * create a buffer, which can be used to accumulate the data
    * extracted from the supplied code token.
    */     
   public Code() {
      this.line = new TokenBuffer();           
      this.code = new ArrayList();
   }
   
   /**
    * Constructor for the <code>Code</code> token. This will
    * create a buffer, which can be used to accumulate the data
    * extracted from the supplied code token before parsing.
    *
    * @param token this is the code token to be parsed
    */     
   public Code(String token) {
      this();
      parse(token);
   }        

   /**
    * This method will supply code to the document definition that
    * will allow a code to be used by the page. The data inserted 
    * into the definition will generated in the class.
    *
    * @param source this is the source to push the code into
    * @param builder this is the builder driving the process
    */    
   public void process(Definition source, Builder builder) {
      for(int i = 0; i < code.size(); i++){  
         String line = (String) code.get(i);     
         source.addContent(line);
      }              
   }

   /**
    * This will clear the code token so that the parse can be 
    * reused by the builder. In practice this method just satisfies
    * the contract of the token so that this object is not abstract.
    */    
   protected void init() {
      code.clear();           
      line.clear();
   }

   /**
    * This is a very simple parse method which basically extracts the
    * begining and end values from the token. For instance this will
    * remove "&lt;%" and "%&gt;" from the token supplied. Once the
    * beginning and end JSP tokens have been removed, each line of
    * the code block is inserted into a list of code lines.
    */    
   protected void parse() {
      if(skip("<%")) {
         count -= 2;
         while(off < count){
            line();
            insert();            
         }              
      }
   }

   /**
    * This method will extract a line from the code block. If there
    * is no new line or carrige return character then all of the
    * characters within the code block are consumed as a single 
    * line. This considers a carrige return and line feed sequence
    * to be a single new line delimiter. Also a single line feed
    * is considered to be a single line delimiter.
    */ 
   private void line() {
      int size = 0;
      
      while(off < count) {
         char next = buf[off++];
        
         if(next == '\r' || next == '\n') {
            if(off < count) {
               off += buf[off] == '\n' ? 1:0;
            }
            break;
         }             
         line.append(next);         
      }
   }

   /**
    * Once the characters for the code line have been collected this
    * is used to insert that line into the list of lines. If no line
    * was accumulated then this method will insert an empty string.
    * Once finished the line buffer is cleared for re-use.
    */ 
   private void insert() {
      code.add(line.text());              
      line.clear();
   }
}
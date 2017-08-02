package org.snapscript.template.translate;

/**
 * The <code>Print</code> object is used to process the JSP print
 * token. This performs a very simple parsing of the provided token.
 * The following token is evaluated by this token.
 * <pre>
 *
 *    &lt;%= expression %&gt;
 * 
 * </pre>
 * This above "expression" is wrapped within a print statement. 
 * This allows the generated page to print expression results.
 * 
 * @author Niall Gallagher
 */ 
class Print extends Token {
   
   /**
    * This is used to collect the token parsed from the print.
    */         
   private TokenBuffer print;

   /**
    * Constructor for the <code>Print</code> token. This will
    * create a buffer, which can be used to accumulate the data
    * extracted from the supplied print token.
    */ 
   public Print() {
      this.print = new TokenBuffer();           
   }

   /**
    * Constructor for the <code>Print</code> token. This will
    * create a buffer, which can be used to accumulate the data
    * extracted from the supplied print token before parsing.
    *
    * @param token this is the print token to be parsed
    */      
   public Print(String token) {
      this();
      parse(token);
   }
   
   /**
    * This method will supply code to the document definition that
    * will allow an expression to be printed by the page. The data
    * inserted into the definition will be displayed in the body.
    *
    * @param source this is the source to push the code into
    * @param builder this is the builder driving the process
    */ 
   public void process(Definition source, Builder builder) {
      if(print.length() > 0) {           
         source.addContent("out.print("+print+");");
      }         
   }
   
   /**
    * This will clear the print token so that the parse can be reused
    * by the builder. In practice this method just satisfies the
    * contract of the token so that this object is not abstract.
    */     
   protected void init() {
      print.clear();           
   }

   /**
    * This is a very simple parse method which basically extracts the
    * begining and end values from the token. For instance this will
    * remove "&lt;%=" and "%&gt;" from the token supplied. 
    */ 
   protected void parse() {
      if(skip("<%=")) {
         while(off < count) {
            if(skip("%>")) {
               break;
            }
            print.append(buf[off++]);            
         }              
      }           
   }
}
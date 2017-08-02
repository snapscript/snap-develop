package org.snapscript.template.translate;

import java.io.Writer;

/**
 * The <code>Processor</code> object is used to process a stream of
 * characters from a JSP source file. This performs the building 
 * process for the translator in a simple stream based manner, such
 * a process allows translators to cascade, such that multiple
 * syntaxes can be present in a single JSP source file.
 * 
 * @author Niall Gallagher
 *
 * @see simple.page.translate.Translator
 */
final class Processor extends Writer {

   /**
    * This breaks up the document into digestable parsable tokens.
    */ 
   private Tokenizer lexer;

   /**
    * This parses the tokens emitted from the lexical analysis.
    */ 
   private Parser parser;        

   /**
    * Constructor for the <code>Processor</code> object. This takes
    * the builder used to construct the document definition and the
    * document definition that accumulates the code segments.
    *
    * @param source this is the document definition to populate
    * @param builder this is the builder used to build the source
    */ 
   public Processor(Definition source, Builder builder) {
      this.parser = new Parser(source, builder);
      this.lexer = new Tokenizer(parser);      
      this.prepare(parser);
   }

   /**
    * Each parses can dynamically specify its own token types. This
    * enables it to digest the tokens emitted from lexical analysis.
    * Once this has been invoked the lexer is ready to receve the
    * soure for the JSP file.
    *
    * @param parser this is the parser used to digest the tokens
    */ 
   public void prepare(Parser parser) {
      parser.begin(lexer);
   }

   /**
    * This is used to write a character buffer to the lexer. The data
    * written to the lexer is analysed and used to emit tokens to 
    * the parse. The will delegate to the <code>append</code> method.
    * 
    * @param text this is some source text from the JSP file
    */ 
   public void write(char[] text){
      lexer.scan(text);
   }

   /**
    * This is used to write a character buffer to the lexer. The data
    * written to the lexer is analysed and used to emit tokens to 
    * the parse. The will delegate to the <code>append</code> method.
    * 
    * @param text this is some source text from the JSP file
    * @param off this is the offset within the text to read from
    * @param len this is the number of characters to consider
    */    
   public void write(char[] text, int off, int len){
      lexer.scan(text, off, len);
   }

   /**
    * This method is used to flush any buffered data to the lexer. The
    * current implementation of this method exists to fulfil the 
    * super class abstract method, as characters are not buffered here.
    */ 
   public void flush() {}

   /**
    * Once the JSP source has been written the processor must be closed
    * so that the lexer can perform a final emit of tokens to the
    * parse. If the close method is not invoked some JSP source may not
    * be flushed to the lexer, and thus may result in an incomplete JSP.
    */ 
   public void close(){
      parser.finish();
   }   
}
package org.snapscript.template.translate;

/**
 * The <code>Lexer</code> interface represents an object that can be
 * initialized with token matching patterns. This is used so that
 * a parser can be handed an arbitrary lexer implementation and be
 * able to tell that lexer the token types it would like to receive.
 * Typically this will be initialized with tokens such as those
 * used by PHP, JSP, and Ruby on Rails. For example, tokens such as
 * the JSP <code>&lt;%</code> and <code>%&gt;</code> could be used.
 * Such tokens would ensure the lexer emits JSP directives.
 * 
 * @author Niall Gallagher
 *
 * @see simple.page.translate.Tokenizer
 */
interface Lexer {  

   /**
    * This method tells the lexer how to extract the tokens
    * from the source document. This is given the opening and
    * closing tokens used to identify a segment. Typically
    * with languages such as JSP and PHP code segments are
    * opened with a delimeter like <code>&lt;%</code> for JSP
    * and <code>&lt;?php</code> for PHP. This method allows
    * the lexer to be configured to process such delimeters.
    *
    * @param start this is the opening token for a segment
    * @param finish this is the closing token for a segment
    */
   public void match(String start, String finish);

   /**
    * This method tells the lexer how to extract the tokens
    * from the source document. This is given the opening and
    * closing tokens used to identify a segment. Typically
    * with languages such as JSP and PHP code segments are
    * opened with a delimeter like <code>&lt;%</code> for JSP
    * and <code>&lt;?php</code> for PHP. This method allows
    * the lexer to be configured to process such delimeters.
    * <p>
    * With this <code>match</code> method a collection of
    * special characters can be specified. These characters
    * tell the lexer what it should allow whitespace to
    * surround for example take the HTML expressions below.
    * <pre>
    * 
    * &lt;   script language ='groovy' &gt;
    * &lt;script language='groovy'&gt;
    *
    * </pre>
    * The above two HTML expressions should be considered 
    * equals using the special characters <code>&lt;</code>,
    * <code>&gt;</code>, and <code>=</code>. 
    *
    * @param start this is the opening token for a segment
    * @param finish this is the closing token for a segment
    */  
   public void match(String start, String finish, String special);
}
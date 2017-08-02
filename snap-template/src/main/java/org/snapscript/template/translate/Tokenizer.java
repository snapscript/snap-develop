package org.snapscript.template.translate;

/**
 * The <code>Tokenizer</code> is used to extract valid tokens from
 * the stream of bytes given to it for scanning. Identifying the
 * tokens from the stream of input is done using delimiters to
 * specify the start and end of a valid token. For example take
 * the well known JSP syntax. A parsable segment typically opens
 * using the following token <code>&lt;%</code> and closes with
 * the <code>%&gt;</code>, as shown in the JSP text shown below.
 * <pre>
 *
 * &lt;%= new java.util.Date() %&gt;
 *
 * </pre>
 * This tokenizer can be used to extract HTML expressions and
 * other such formats by specifying the starting and ending of
 * the expression. For example the following HTML could be used
 * to specify the opening and closure of an valid token.
 * <pre>
 *
 * &lt;script language='groovy'&gt;
 *    java.util.Date();
 * &lt;/script&gt;
 *
 * </pre>
 * The above token will be identified using a case insensitive
 * match, and whitespace characters can be ignored, such that
 * the HTML does not have to be formatted correctly in order
 * for this tokenizer to extract the HTML as a valid token.
 *
 * @author Niall Gallagher
 */
final class Tokenizer implements Lexer {

   /**
    * Parses the tokens extracted from the bytes stream.
    */
   private Parser parser;

   /**
    * Identifies what characters are considered special.
    */
   private char[] special;

   /**
    * The sequence of characters that start a valid token.
    */
   private char[] start;

   /**
    * The sequence of characters that finish a valid token.
    */
   private char[] finish;

   /**
    * Buffers the scanned bytes so this can cascade easily.
    */
   private char[] buf;

   /**
    * The number of characters the internal buffer has.
    */
   private int capacity;

   /**
    * The current read offset within the active buffer.
    */
   private int off;

   /**
    * The number of valid characters within the buffer.
    */
   private int count;

   /**
    * This identifies the start of a valid token string.
    */
   private int mark;

   /**
    * This specifies the number of characters in the token.
    */
   private int size;

   /**
    * Constructor for the <code>Tokenizer</code> object. This
    * is used to scan a stream of bytes and pass any extracted
    * tokens from the stream to the <code>Parser</code>.
    *
    * @param parser the parser used to parse extracted tokens
    */
   public Tokenizer(Parser parser) {
      this.parser = parser;
   }

   /**
    * This method tells the lexer how to extract the tokens
    * from the source document. This is given the opening and
    * closing tokens used to identify a segment. Typically
    * with languages such as JSP and PHP code segments are
    * opened with a delimiter like <code>&lt;%</code> for JSP
    * and <code>&lt;?php</code> for PHP. This method allows
    * the lexer to be configured to process such delimiters.
    *
    * @param start this is the opening token for a segment
    * @param finish this is the closing token for a segment
    */
   public void match(String start, String finish) {
      match(start, finish, "");
   }

   /**
    * This method tells the lexer how to extract the tokens
    * from the source document. This is given the opening and
    * closing tokens used to identify a segment. Typically
    * with languages such as JSP and PHP code segments are
    * opened with a delimiter like <code>&lt;%</code> for JSP
    * and <code>&lt;?php</code> for PHP. This method allows
    * the lexer to be configured to process such delimiters.
    * <p>
    * With this <code>match</code> method a collection of
    * special characters can be specified. These characters
    * tell the lexer what it should allow whitespace to
    * surround. For example take the HTML expressions below.
    * <pre>
    *
    * &lt;   script language ='groovy' &gt;
    * &lt;script language='groovy'&gt;
    *
    * </pre>
    * The above two HTML expressions should be considered
    * equal using the special characters <code>&lt;</code>,
    * <code>&gt;</code>, and <code>=</code>.
    *
    * @param start this is the opening token for a segment
    * @param finish this is the closing token for a segment
    * @param special this is the set of special characters
    */
   public void match(String start, String finish, String special) {
      this.special = special.toCharArray();
      this.finish = finish.toCharArray();
      this.start  = start.toCharArray();
   }

   /**
    * The <code>skip</code> method is used to read the specified
    * text from the bytes within the current buffer. This will
    * perform a case insensitive comparison of the characters
    * from both sources. Also, this makes use of the collection
    * of special characters to ignore whitespace.
    *
    * @param text this is the text that is to be skipped
    *
    * @return this returns true if the text was fully skipped
    */
   private boolean skip(String text) {
      return skip(text.toCharArray());
   }

   /**
    * The <code>skip</code> method is used to read the specified
    * text from the bytes within the current buffer. This will
    * perform a case insensitive comparison of the characters
    * from both sources. Also, this makes use of the collection
    * of special characters to ignore whitespace.
    *
    * @param text this is the text that is to be skipped
    *
    * @return this returns true if the text was fully skipped
    */
   private boolean skip(char[] text) {
      int size = text.length;
      int seek = off;
      int scan = 0;

      if(off + size > count){
         return false;
      }
      for(int pos = 0; pos < size;) {
         char peek = buf[seek++];

         if(special(text[pos++])) {
            scan = pos;
         }
         if(pos > 1) {
            if(special(text[pos-2]))
               scan = pos;
         }
         while(seek < count &&scan >0){
            peek = buf[seek-1];

            if(equals(text[pos-1],peek)){
               scan =0;
            } else{
               if(space(peek)){
                  seek++;
               }else{
                  return false;
               }
            }
         }
         if(!equals(text[pos-1],peek)){
            return false;
         }
      }
      off = seek;
      return true;
   }

   /**
    * The <code>peek</code> method is used to determine if the
    * specified text can be fully or partially read from the
    * current buffer. This performs the same comparative checks
    * as the <code>skip</code> methods. However, the offset to
    * the current buffer remains unchanged if the peek is
    * successful. Also, not all of the text needs to be read
    * if the end of the stream of bytes is reached first.
    *
    * @param text this is the text that is to be examined
    *
    * @return this returns true if the text was fully examined
    */
   private boolean peek(String text) {
      return peek(text.toCharArray());
   }

   /**
    * The <code>peek</code> method is used to determine if the
    * specified text can be fully or partially read from the
    * current buffer. This performs the same comparative checks
    * as the <code>skip</code> methods. However, the offset to
    * the current buffer remains unchanged if the peek is
    * successful. Also, not all of the text needs to be read
    * if the end of the stream of bytes is reached first.
    *
    * @param text this is the text that is to be examined
    *
    * @return this returns true if the text was fully examined
    */
   private boolean peek(char[] text) {
      int size = text.length;
      int seek = off;
      int scan = 0;

      for(int pos = 0; pos < size;) {
         if(seek >= count) {
            return true;
         }
         char peek = buf[seek++];

         if(special(text[pos++])) {
            scan = pos;
         }
         if(pos > 1) {
            if(special(text[pos-2])){
               scan = pos;
            }
         }
         while(seek < count && scan >0){
            peek = buf[seek-1];

            if(equals(text[pos-1],peek)){
               scan =0;
            } else{
               if(space(peek)){
                  seek++;
               }else{
                  return false;
               }
            }
         }
         if(!equals(text[pos-1], peek)){
            return false;
         }
      }
      return true;
   }

   /**
    * Emits a token to the <code>Parser</code> so that it can
    * process it. This will write bytes from the internal buffer
    * that have been marked out during the scanning phase.
    */
   private void emit(){
      parser.parse(buf,mark, size);
   }

   /**
    * This will scan the provided bytes for tokens that should be
    * emitted to the <code>Parser</code>. The tokens emitted to
    * the parser object are either plain text tokens or valid
    * segments that require further processing by the parser.
    *
    * @param text this is the buffer that contains the bytes
    */
   public void scan(char[] text) {
      scan(text,0, text.length);
   }

   /**
    * This will scan the provided bytes for tokens that should be
    * emitted to the <code>Parser</code>. The tokens emitted to
    * the parser object are either plain text tokens or valid
    * segments that require further processing by the parser.
    *
    * @param text this is the buffer that contains the bytes
    * @param pos this is the offset within the buffer to read
    * @param len this is the number of bytes to use
    */
   public void scan(char[] text, int pos, int len) {
      if(len + count > capacity) {
         resize(len + count);
      }
      for(int i=0; i < len; i++){
         buf[count++] =text[pos +i];
      }
      process();
      reset();
   }

   /**
    * This will reset the offset pointer so that the next scan
    * will account for any segments that were not fully processed.
    * This also ensures that the size of the internal buffer does
    * not get any larger than the largest segment scanned.
    */
   private void reset(){
      if(mark < count) {
         count -= mark;

         for(int i = 0; i <count; i++){
            buf[i]= buf[mark++];
         }
         off=mark =0;
      }else {
         off= mark =
           count=0;
      }
   }

   /**
    * This is used to expand the capacity of the internal buffer.
    * Because the size of the segments within the source text can
    * vary, this is used to ensure that the maximum segment can
    * be stored before it is emitted to the <code>Parser</code>.
    *
    * @param size this is the minimum size the buffer should be
    */
   private void resize(int size) {
      if(capacity  < size) {
         char[] large =  new char[size];

         for(int i=0; i< capacity; i++) {
            large[i] = buf[i];
         }
         capacity = size;
         buf = large;
      }
   }

   /**
    * Once the bytes have been scanned in they are processed for
    * valid segments. This will determine if segments have been
    * encountered and will appropriately emit them to the
    * <code>Parser</code>. Data that is not contained within the
    * specified delimiters will be emitted as plain text tokens.
    */
   private void process() {
      while(off < count) {
         if(peek(start)) {
            if(segment()) {
               emit();
            } else {
               break;
            }
         }else {
            text();
            emit();
         }
         mark= off;
      }
   }

   /**
    * This will extract all plain text tokens from the source
    * text. If at any stage the opening delimiter of a segment
    * is encountered this will cease reading bytes and return.
    */
   private void text() {
      for(size = 0; off < count;) {
         if(peek(start)) {
            break;
         }
         size++;
         off++;
      }
   }

   /**
    * This is used to extract the segment from the source text.
    * This will first check to see if the opening token can
    * be read from the source text, if it can then this will try
    * to consume the contents of the segment until it reaches
    * the closing delimiter. If the closing delimiter cannot be
    * read from the contents of the buffer this returns false.
    * <p>
    * An added feature of this method is that Java style quoted
    * strings and comments will not be scanned for the closing
    * delimiter, which means that comments and strings do not
    * need to be given special attention.
    *
    * @return this returns true if a segment has been read
    */
   private boolean segment() {
      if(start()) {
         if(body()) {
            return finish();
         }
      }
      return false;
   }

   /**
    * This will check to see if the starting delimeter for the
    * segment can be read. If the segment start can is read this
    * will copy the start segment to the start of the token so
    * that unformatted HTML does not have to be interpreted by
    * by the <code>Parser</code> implementation.
    *
    * @return this returns true if the start token is read
    */
   private boolean start() {
      if(skip(start)) {
         int len = start.length;

         for(mark = off; len-- > 0;){
            buf[--mark] = start[len];
         }
         return true;
      }
      return false;
   }

   /**
    * This will attempt to read the body of the segment. If the
    * end delimiter for a segment can be read from the remaining
    * bytes then this will return true. This will ensure that 
    * the end delimiter cannot be read from a Java style quoted
    * string such as <code>" %&gt; "</code> for JSP tags. Also,
    * this will not read the end delimiter from a Java comment.
    *
    * @return this returns true if the end token is reached
    */
   private boolean body(){
      while(off < count) {
         if(comment()) {
            continue;
         }else if(quoted()){
            continue;
         }else {
            if(peek(finish)) {
               return true;
            }
            off++;
         }
      }
      return false; 
   }

   /**
    * This provides a similar function to the <code>start</code>
    * method which reads the opening delimiter for a segment. 
    * This will attempt to read the closing or end delimiter for
    * a segment. If it is read it is copied to the end of the
    * buffer so that the <code>Parser</code> implementation does
    * not have to deal with unformatted HTML tags.
    *
    * @return this returns true if the end token has been read    
    */ 
   private boolean finish(){
      int len = off - mark;

      if(skip(finish)) {
         int count = finish.length;
         int pos = 0;

         for(size = len; pos < count; pos++) {
            buf[mark + size++] = finish[pos];
         }
         return true;         
      }
      return false;
   }


   /**
    * This method will read a Java style comment from the source
    * text. This can read embedded comments that start with the
    * <code>/*</code> token and end with <code>*&#47;</code>.
    * Also single line comments beginning with <code>//</code>
    * are also accounted for. If the comment can not be fully
    * read from the source text this will return false.
    *
    * @return this returns true if a comment has been read
    */
   private boolean comment() {
      char peek = buf[off];

      if(count - off > 1 && peek =='/') {
         if(buf[off + 1] =='*') {
            while(off < count) {
               peek = buf[off];

               if(count - off < 2){
                  return false;
               }
               if(peek =='*'){
                  if(buf[off+1]=='/'){
                     off += 2;
                     return true;
                  }
               }
               off++;
            }
         } else if(buf[off + 1] == '/') {
            while(off < count) {
               peek = buf[off++];

               if(peek == '\n' || peek== '\r'){
                  return true;
               }
            }
         }
      }
      return false;
   }

   /**
    * This method is used to extract a Java style quoted string
    * from the template. This will ensure that the quoted string
    * can have escaped comments such that <code>\"</code> will
    * not evaluate to the end of the quoted string.
    *
    * @return this returns true if a quoted string is read
    */
   private boolean quoted() {
      if(off < count) {           
         if(buf[off++] == '"') {
            for(int slash = 0; off < count;) {
               if(buf[off++] == '\\') {
                  slash++;
               } else {
                  if(buf[off - 1] == '"'){
                     if(slash % 2 < 1)  
                        return true;                                 
                  }              
                  slash =0;         
               }                  
            }
         }else{
            off--;
        }
      }         
      return false;
   } 

   /**
    * This method is used to determine if the two characters are
    * equal in a case insensitive manner. This will make use of
    * the <code>Character.toLowerCase</code> for UCS-2 characters.
    *
    * @param one this is a character to be examined
    * @param two this is the second character to compare
    */
   private boolean equals(char one, char two) {
      return toLower(one) == toLower(two);
   }

   /**
    * This is used to determine if the given character is a special
    * character. The character is a special character is it was
    * specified to this object with the <code>match</code> method.
    *
    * @param ch this to be checked against the special characters
    *
    * @return true if the character is a special character
    */
   private boolean special(char ch) {
      int len = special.length;

      for(int pos = 0; pos < len; pos++) {
         if(equals(special[pos],ch))
            return true;
      }
      return false;
   }

   /**
    * This converts the provided character to lower case so that
    * a comparison can be made in a case insensitive manner. This
    * delegates to the <code>Character.toLowerCase</code> method.
    *
    * @param ch this is the character to convert to lower case
    *
    * @return the character equivalent in lower case format
    */
   private char toLower(char ch) {
      return Character.toLowerCase(ch);
   }

   /**
    * This is used to determine if a given UCS-2 character is a
    * space character. That is a whitespace character this sees
    * the, space, carriage return and line feed characters as
    * whitespace characters.
    *
    * @param ch the character that is being determined by this
    *
    * @return true if the character given is a space character
    */
   private boolean space(char ch) {
      switch(ch){
      case ' ': case '\t':
      case '\n': case '\r':
         return true;
      default:
         return false;
      }
   }
}
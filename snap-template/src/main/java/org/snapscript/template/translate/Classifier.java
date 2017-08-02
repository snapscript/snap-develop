package org.snapscript.template.translate;

import java.lang.reflect.Constructor;

/**
 * The <code>Classifier</code> object is used to classify tokens that
 * have been emitted from the lexical analysis phase. This will
 * examine the text within the token to determine the class of token.
 * Classification of the token results in a <code>Token</code> object
 * being created. This object is then used to process the data.
 * 
 * @author Niall Gallagher
 */ 
final class Classifier {

   /**
    * This method performs the classification of the token. This will
    * examine the structure of the token before classifing it. This
    * basically takes a look at the JSP tags beginning the token and
    * from the tags determines the class of token it represents.
    *
    * @param token this is the token text to be classified
    *
    * @return this returns a token to process the provided text
    */ 
   public Token getToken(String token) {
      if(token.startsWith("<%@ page")) {
         return getToken(Page.class, token);
      }
      if(token.startsWith("<%@ include")) {
         return getToken(Include.class, token);
      }
      if(token.startsWith("<%@ insert")) {
         return getToken(Insert.class, token);
      }
      if(token.startsWith("<%=")) {
         return getToken(Print.class, token);
      }
      if(token.startsWith("<%--")) {
         return getToken(Comment.class, token);
      }
      if(token.startsWith("<%!")) {
         return getToken(Declaration.class, token);
      }
      if(token.startsWith("<%")) {
         return getToken(Code.class, token);
      }
      return getToken(Text.class, token);
   }

   /**
    * This method is used to create a token of the specified type. The
    * class provided must extend the <code>Token</code> object. Once
    * the token has been created it can be processed, and used to 
    * build the document definition before the generation phase.
    *
    * @param type this is the class implementing the token type
    * @param token this is the text to be processed by the token
    *
    * @return this returns a token that can parse the text provided
    */ 
   public Token getToken(Class type, String token) {
      try {           
         Constructor factory = getConstructor(type);
         return (Token)factory.newInstance(new Object[]{token}); 
      }catch(Exception e){
         return null;              
      }              
   }

   /**
    * This is used to construct the token implementation using a well
    * known signature. The constructor signature must take a string
    * representing the token to be parsed. The <code>Class</code>
    * provided must be an implementation of the token type.
    *
    * @param type this is the clas implementing the token type
    *
    * @return this returns a factory method for creating a token
    */ 
   private Constructor getConstructor(Class type) throws Exception {
      Class[] list = new Class[]{String.class};
      return type.getDeclaredConstructor(list);
   }    
}

package org.snapscript.develop.complete;

import static org.snapscript.develop.complete.BraceCounter.CLOSE_BRACE;
import static org.snapscript.develop.complete.BraceCounter.OPEN_BRACE;
import static org.snapscript.develop.complete.HintToken.CLASS;
import static org.snapscript.develop.complete.HintToken.CONSTANT;
import static org.snapscript.develop.complete.HintToken.ENUMERATION;
import static org.snapscript.develop.complete.HintToken.MODULE;
import static org.snapscript.develop.complete.HintToken.TRAIT;
import static org.snapscript.develop.complete.HintToken.VARIABLE;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.snapscript.develop.common.TypeNode;
import org.snapscript.parse.GrammarIndexer;
import org.snapscript.parse.GrammarResolver;
import org.snapscript.parse.Line;
import org.snapscript.parse.SourceCode;
import org.snapscript.parse.SourceProcessor;
import org.snapscript.parse.Token;
import org.snapscript.parse.TokenIndexer;
import org.snapscript.parse.TokenType;

public class SourceContextExtractor {

   private final SourceProcessor processor;
   private final GrammarIndexer indexer;
   
   public SourceContextExtractor(GrammarResolver resolver, GrammarIndexer indexer) {
      this.processor = new SourceProcessor(100);
      this.indexer = indexer;
   }
   
   public SourceContext extractContext(Completion state) {
      BraceCounter counter = new BraceCounter();
      List<Token> tokens = new ArrayList<Token>();
      Map<String, TypeNode> types = state.getTypes();
      String source = state.getSource();
      String resource = state.getResource();
      String prefix = state.getPrefix();
      int line = state.getLine();
      
      if(!source.isEmpty()) {
         TokenIndexer indexer = createIndexer(source, resource);
         indexer.index(tokens);
      }
      int length = tokens.size();

      for(int i = 0; i < length; i++) {
         Token token = tokens.get(i);
         Object value = token.getValue();
         String text = String.valueOf(value);
         Line current = token.getLine();
         int number = current.getNumber();
         
         if(number > line) {
            String context = counter.getType();
            TypeNode type = types.get(context);
            Map<String, String> strings = counter.getTokens(prefix);
            
            return new SourceContext(type, strings);
         }
         if(text.equals(OPEN_BRACE) || text.equals(CLOSE_BRACE)) {
            counter.setBrace(text);
         }
         String type = HintTokenProcessor.getType(tokens, i);
      
         if(type.equals(CLASS) || type.equals(TRAIT) || type.equals(ENUMERATION) || type.equals(MODULE)) {
            counter.setType(text);
         } else {
            if(type.equals(CONSTANT) || type.equals(VARIABLE)) {
               int category = token.getType();
               
               if(category != TokenType.TEXT.mask) {
                  counter.addToken(text, type);
               }
            }
         }
      }
      Map<String, String> strings = counter.getTokens(prefix);
      return new SourceContext(null, strings);
   }
   
   private TokenIndexer createIndexer(String text, String resource) {
      char[] array = text.toCharArray();
      
      if(array.length > 0) {
         SourceCode source = processor.process(text);
         char[] original = source.getOriginal();
         char[] compress = source.getSource();
         short[] lines = source.getLines();
         short[]types = source.getTypes();
         
         return new TokenIndexer(indexer, resource, original, compress, lines, types);
      }
      return null;
   }
   
}

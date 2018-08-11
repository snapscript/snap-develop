package org.snapscript.studio.index;

import org.snapscript.parse.GrammarCompiler;
import org.snapscript.parse.GrammarIndexer;
import org.snapscript.parse.GrammarResolver;
import org.snapscript.parse.SourceProcessor;
import org.snapscript.parse.SyntaxNode;
import org.snapscript.parse.SyntaxParser;
import org.snapscript.studio.index.counter.TokenBraceCounter;
import org.snapscript.tree.Instruction;

public class SourceSanatizer {   

   private final SourceProcessor processor;
   private final GrammarIndexer indexer;
   private final SyntaxParser parser;
   
   public SourceSanatizer(GrammarResolver resolver, GrammarIndexer indexer, GrammarCompiler compiler) {
      this.processor = new SourceProcessor(100);
      this.parser = new SyntaxParser(resolver, indexer);
      this.indexer = indexer;
   }   
   
   public SyntaxNode sanatize(String script, String source) throws Exception {
      return sanatize(script, source, 4);
   }
   
   private SyntaxNode sanatize(String script, String source, int attempts) throws Exception {
      Exception problem = null;
      
      for(int i = 0; i < attempts; i++) {
         try {
            return parser.parse(script, source, Instruction.SCRIPT.name);
         } catch(Exception cause) {
            if(problem == null) {
               problem = cause;
            }
            int line = getLine(cause);
            
            if(line != -1) {
               try {
                  if(i + 1 >= attempts) {
                     source = getSourceRemoveAllLines(script, source, line);
                  } else {
                     source = getSourceWithoutLine(script, source, line);
                  }
               } catch(Exception fatal) {
                  throw problem;
               }
            }
         }
      }
      throw problem; 
   }
   
   private String getSourceWithoutLine(String script, String source, int line) {
      StringBuilder builder = new StringBuilder();
      String lines[] = source.split("\\r?\\n");
      int index = line -1;
      
      if(lines.length > index && lines[index].trim().equals("}")) {
         while(index >= 0) {
            int length = lines[--index].trim().length();
            
            if(length != 0) {
               break;
            }           
         }
      }           
      for(int j = 0; j < lines.length; j++) {
         if(j != index) {
            builder.append(lines[j]);
            builder.append("\n");
         }
      }
      return builder.toString();
   }
   
   private String getSourceRemoveAllLines(String script, String source, int line) {
      StringBuilder builder = new StringBuilder();
      String lines[] = source.split("\\r?\\n");
      int depth = getDepth(script, source, line);
      int index = line -1;
      
      if(lines.length > index && lines[index].trim().equals("}")) {
         while(index >= 0) {
            int length = lines[--index].trim().length();
            
            if(length != 0) {
               break;
            }           
         }
      }           
      for(int j = 0; j < index; j++) {     
         builder.append(lines[j]);
         builder.append("\n");
      }
      for(int j = 0; j < depth; j++) {
         builder.append("}");
      }
      return builder.toString();
   }
   
   private int getLine(Exception cause) {
      String message  = cause.getMessage();
      
      if(message.contains("at line ")) {
         try {
            int length = message.length();
            int lastIndex = message.lastIndexOf(" ");
            String token = message.substring(lastIndex + 1, length);
            
            return Integer.parseInt(token);
         }catch(Exception e){}
      }
      return -1;
   }
   
   private int getDepth(String script, String source, int line) {
      TokenBraceCounter counter = new TokenBraceCounter(indexer, processor, script, source);      
      return counter.getDepth(line);
   }
}

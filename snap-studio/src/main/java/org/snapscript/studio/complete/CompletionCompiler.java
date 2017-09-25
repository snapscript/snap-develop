package org.snapscript.studio.complete;

import static org.snapscript.core.Reserved.GRAMMAR_FILE;

import java.util.LinkedHashMap;
import java.util.Map;

import org.snapscript.agent.log.ProcessLogger;
import org.snapscript.parse.Grammar;
import org.snapscript.parse.GrammarCompiler;
import org.snapscript.parse.GrammarDefinition;
import org.snapscript.parse.GrammarIndexer;
import org.snapscript.parse.GrammarReader;
import org.snapscript.parse.GrammarResolver;
import org.snapscript.studio.configuration.ConfigurationClassLoader;

public class CompletionCompiler {
   
   private final Iterable<GrammarDefinition> definitions;
   private final Map<String, Grammar> grammars;
   private final GrammarCompiler compiler;
   private final GrammarResolver resolver;
   private final GrammarIndexer indexer;
   private final CompletionMatcher matcher;

   public CompletionCompiler(ConfigurationClassLoader loader, ProcessLogger logger) {
      this.grammars = new LinkedHashMap<String, Grammar>();      
      this.resolver = new GrammarResolver(grammars);
      this.indexer = new GrammarIndexer();
      this.matcher = new CompletionMatcher(resolver, indexer, loader, logger);      
      this.compiler = new GrammarCompiler(resolver, indexer);    
      this.definitions = new GrammarReader(GRAMMAR_FILE);
   } 

   public synchronized CompletionMatcher compile() {
      if(grammars.isEmpty()) {
         for(GrammarDefinition definition : definitions){
            String name = definition.getName();
            String value = definition.getDefinition();
            Grammar grammar = compiler.process(name, value);
            
            grammars.put(name, grammar);
         }
      }
      return matcher;
   }
}
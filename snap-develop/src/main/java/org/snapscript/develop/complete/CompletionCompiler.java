package org.snapscript.develop.complete;

import java.util.LinkedHashMap;
import java.util.Map;

import org.snapscript.agent.ConsoleLogger;
import org.snapscript.develop.configuration.ConfigurationClassLoader;
import org.snapscript.parse.Grammar;
import org.snapscript.parse.GrammarCompiler;
import org.snapscript.parse.GrammarIndexer;
import org.snapscript.parse.GrammarResolver;
import org.snapscript.parse.Syntax;

public class CompletionCompiler {
   
   private final Map<String, Grammar> grammars;
   private final GrammarCompiler compiler;
   private final GrammarResolver resolver;
   private final GrammarIndexer indexer;
   private final CompletionMatcher matcher;

   public CompletionCompiler(ConfigurationClassLoader loader, ConsoleLogger logger) {
      this.grammars = new LinkedHashMap<String, Grammar>();      
      this.resolver = new GrammarResolver(grammars);
      this.indexer = new GrammarIndexer();
      this.matcher = new CompletionMatcher(resolver, indexer, loader, logger);      
      this.compiler = new GrammarCompiler(resolver, indexer);      
   } 

   public synchronized CompletionMatcher compile() {
      if(grammars.isEmpty()) {
         Syntax[] language = Syntax.values();
         
         for(Syntax syntax : language) {
            String name = syntax.getName();
            String value = syntax.getGrammar();
            Grammar grammar = compiler.process(name, value);
            
            grammars.put(name, grammar);
         }
      }
      return matcher;
   }
}

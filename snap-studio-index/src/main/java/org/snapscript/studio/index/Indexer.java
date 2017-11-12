package org.snapscript.studio.index;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.snapscript.common.ArrayStack;
import org.snapscript.common.Stack;
import org.snapscript.common.thread.ThreadPool;
import org.snapscript.compile.assemble.OperationBuilder;
import org.snapscript.compile.assemble.OperationTraverser;
import org.snapscript.core.Context;
import org.snapscript.core.FilePathConverter;
import org.snapscript.core.Path;
import org.snapscript.parse.Grammar;
import org.snapscript.parse.GrammarCompiler;
import org.snapscript.parse.GrammarDefinition;
import org.snapscript.parse.GrammarIndexer;
import org.snapscript.parse.GrammarReader;
import org.snapscript.parse.GrammarResolver;
import org.snapscript.parse.SourceCode;
import org.snapscript.parse.SourceProcessor;
import org.snapscript.parse.SyntaxNode;
import org.snapscript.parse.SyntaxParser;
import org.snapscript.parse.Token;
import org.snapscript.parse.TokenIndexer;
import org.snapscript.tree.Instruction;
import org.snapscript.tree.OperationResolver;

public class Indexer {
   
   private static final String GRAMMAR_FILE = "grammar.txt";

   private final GrammarIndexer grammarIndexer;
   private final Map<String, Grammar> grammars;      
   private final GrammarResolver grammarResolver;
   private final GrammarCompiler grammarCompiler;  
   private final SourceProcessor sourceProcessor;
   private final FilePathConverter converter;
   private final SyntaxParser parser;
   private final GrammarReader reader;
   private final ThreadPool pool;

   public Indexer() {
      this(GRAMMAR_FILE);
   }
   
   public Indexer(String file) {
      this.pool = new ThreadPool(6);
      this.grammarIndexer = new GrammarIndexer();
      this.grammars = new LinkedHashMap<String, Grammar>();      
      this.grammarResolver = new GrammarResolver(grammars);
      this.grammarCompiler = new GrammarCompiler(grammarResolver, grammarIndexer);  
      this.sourceProcessor = new SourceProcessor(100);
      this.reader = new GrammarReader(GRAMMAR_FILE);
      this.parser = new SyntaxParser(grammarResolver, grammarIndexer);
      this.converter = new FilePathConverter();
      
      for(GrammarDefinition definition : reader){
         String name = definition.getName();
         String value = definition.getDefinition();
         Grammar grammar = grammarCompiler.process(name, value);
         
         grammars.put(name, grammar);
      }
   }
   
   public IndexFile index(Context context, String resource, String source) throws Exception {
      List<Token> tokens = new ArrayList<Token>();
      Stack<IndexNode> stack = new ArrayStack<IndexNode>();
      IndexBuilder listener = new IndexBuilder(stack);
      OperationBuilder builder = new IndexInstructionBuilder(listener, context, pool);
      OperationResolver resolver = new IndexInstructionResolver(context);
      OperationTraverser traverser = new OperationTraverser(builder, resolver);
      SyntaxNode node = parser.parse(resource, source, Instruction.SCRIPT.name);
      Path path = converter.createPath(resource);
      Object result = traverser.create(node, path);
      IndexNode top = stack.pop();
      
      if(!stack.isEmpty()) {
         throw new IllegalStateException("Syntax error in " + resource);
      }
      SourceCode code = sourceProcessor.process(source);
      char[] original = code.getOriginal();
      char[] compress = code.getSource();
      short[] lines = code.getLines();
      short[]types = code.getTypes();

      TokenIndexer tokenIndexer = new TokenIndexer(grammarIndexer, resource, original, compress, lines, types);
      tokenIndexer.index(tokens);
      return new IndexSearcher(top, tokens);
   }
   
   private static class IndexBuilder implements IndexListener {
      
      private final Stack<IndexNode> stack;
      
      public IndexBuilder(Stack<IndexNode> stack) {
         this.stack = stack;
      }

      @Override
      public void update(Index index) {
         IndexNode node = new IndexNode(index);
         IndexType type = index.getType();
         int line = index.getLine();
         
         while(!stack.isEmpty()) {
            IndexNode top = stack.peek();
            Set<IndexType> parents = top.getParentTypes();
            int offset = top.getIndex().getLine();
            
            if(offset < line) {
               break;
            } else if(parents.contains(type)) {
               if(node.getType().isFunction() && top.getType().isCompound()) {
                  Set<IndexNode> nodes = top.getNodes();
                  node.getNodes().addAll(nodes);            
               } else {
                  top.setParent(node);
                  node.getNodes().add(top);
               }
               stack.pop();      
            } else {
               break;
            }
         }
         stack.push(node);
      }
      
   }
}

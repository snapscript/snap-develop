package org.snapscript.studio.index;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;

import org.snapscript.common.ArrayStack;
import org.snapscript.common.Stack;
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
import org.snapscript.parse.Line;
import org.snapscript.parse.SourceCode;
import org.snapscript.parse.SourceProcessor;
import org.snapscript.parse.SyntaxNode;
import org.snapscript.parse.SyntaxParser;
import org.snapscript.parse.Token;
import org.snapscript.parse.TokenIndexer;
import org.snapscript.parse.TokenType;
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
   private final IndexPathTranslator translator;
   private final IndexDatabase database;
   private final SyntaxParser parser;
   private final GrammarReader reader;
   private final Executor executor;
   private final Context context;
   private final File root;

   public Indexer(IndexPathTranslator translator, IndexDatabase database, Context context, Executor executor, File root) {
      this(translator, database, context, executor, root, GRAMMAR_FILE);
   }
   
   public Indexer(IndexPathTranslator translator, IndexDatabase database, Context context, Executor executor, File root, String file) {
      this.grammarIndexer = new GrammarIndexer();
      this.grammars = new LinkedHashMap<String, Grammar>();      
      this.grammarResolver = new GrammarResolver(grammars);
      this.grammarCompiler = new GrammarCompiler(grammarResolver, grammarIndexer);  
      this.sourceProcessor = new SourceProcessor(100);
      this.reader = new GrammarReader(GRAMMAR_FILE);
      this.parser = new SyntaxParser(grammarResolver, grammarIndexer);
      this.converter = new FilePathConverter();
      this.translator = translator;
      this.database = database;
      this.executor = executor;
      this.context = context;
      this.root = root;
      
      for(GrammarDefinition definition : reader){
         String name = definition.getName();
         String value = definition.getDefinition();
         Grammar grammar = grammarCompiler.process(name, value);
         
         grammars.put(name, grammar);
      }
   }
   
   public IndexFile index(String resource, String source) throws Exception {
      String script = translator.getScriptPath(root, resource);
      File file = new File(root, resource);
      NodeBuilder listener = new NodeBuilder(database, resource);
      OperationBuilder builder = new IndexInstructionBuilder(listener, context, executor);
      OperationResolver resolver = new IndexInstructionResolver(context);
      OperationTraverser traverser = new OperationTraverser(builder, resolver);
      TokenBraceCounter counter = new TokenBraceCounter(grammarIndexer, sourceProcessor, script, source);
      SyntaxNode node = parser.parse(script, source, Instruction.SCRIPT.name);
      Path path = converter.createPath(script);
      Object result = traverser.create(node, path);
      IndexNode top = listener.build();

      return new IndexSearcher(database, counter, top, file, resource, script);
   }
   
   private static class TokenBraceCounter implements IndexBraceCounter {
      
      private final SourceProcessor sourceProcessor;
      private final GrammarIndexer grammarIndexer;
      private final List<Token> tokens;
      private final String resource;
      private final String source;
      
      public TokenBraceCounter(GrammarIndexer grammarIndexer, SourceProcessor sourceProcessor, String resource, String source) {
         this.tokens = new ArrayList<Token>();
         this.sourceProcessor = sourceProcessor;
         this.grammarIndexer = grammarIndexer;
         this.resource = resource;
         this.source = source;
      }
      
      @Override
      public int getDepth(int line) {
         List<Token> tokens = getTokens();
         
         if(!tokens.isEmpty()) {
            BraceStack stack = new BraceStack();
            
            for(Token token : tokens) {
               Line position = token.getLine();
               String path = position.getResource();
               
               try{
                  stack.update(token);
               }catch(Exception e){
                  throw new IllegalStateException("Unbalanced braces in " + path + " at line "+ line, e);
               }
            }
            return stack.depth(line);
         }
         return 0;
      }
      
      private List<Token> getTokens() {
         if(tokens.isEmpty()) {
            SourceCode code = sourceProcessor.process(source);
            char[] original = code.getOriginal();
            char[] compress = code.getSource();
            short[] lines = code.getLines();
            short[]types = code.getTypes();
            TokenIndexer tokenIndexer = new TokenIndexer(grammarIndexer, resource, original, compress, lines, types);
            tokenIndexer.index(tokens);
         }
         return tokens;
      }
   }
   
   private static class BraceStack {
      
      private final Stack<BraceNode> stack;
      private final BraceNode root;
      
      public BraceStack() {
         this.stack = new ArrayStack<BraceNode>();
         this.root = new BraceNode(null, null);
         this.stack.push(root);
      }
      
      public int depth(int line) {
         return root.depth(line);
      }
      
      public void update(Token token) {
         Object value = token.getValue();
         Line line = token.getLine();
         int size = stack.size();
         short type = token.getType();
         
         if((type & TokenType.LITERAL.mask) == TokenType.LITERAL.mask) {
            if(value.equals("{")){
               BraceNode current = stack.peek();
               BraceNode node = current.open(BraceType.COMPOUND, line, size);
   
               stack.push(node);
            }else if(value.equals("}")) {
               BraceNode current = stack.pop();
               BraceNode parent = current.getParent();
               int depth = parent.close(BraceType.COMPOUND, line);
               
               if(depth != 0) {
                  throw new IllegalStateException("Bracket not closed");
               }
            }else if(value.equals("(")) {
               open(BraceType.NORMAL, line);
            }else if(value.equals(")")) {
               close(BraceType.NORMAL, line);
            }else if(value.equals("[")) {
               open(BraceType.ARRAY, line);
            }else if(value.equals("]")) {
               close(BraceType.ARRAY, line);
            }
         }
      }
      
      private void open(BraceType type, Line line) {
         BraceNode node = stack.peek();
         int depth = stack.size();
         
         if(node == null) {
            throw new IllegalStateException("No node");
         }
         node.open(type, line, depth);
      }
      
      private void close(BraceType type, Line line) {
         BraceNode node = stack.peek();
         int depth = stack.size();
         
         if(depth == 0) {
            throw new IllegalStateException("Stack is empty");
         }
         node.close(type, line);
      }
   }
   
   private static class BraceNode {
      
      private List<BraceNode> children;
      private Stack<BraceNode> stack;
      private BraceNode parent;
      private BraceType type;
      private Line start;
      private Line finish;
      private int depth;
      
      public BraceNode(BraceNode parent, BraceType type) {
         this.children = new ArrayList<BraceNode>();
         this.stack = new ArrayStack<BraceNode>();
         this.parent = parent;
         this.type = type;
      }
      
      public BraceNode getParent(){
         return parent;
      }
      
      public BraceNode open(BraceType type, Line line, int depth) {
         BraceNode node = new BraceNode(this, type);
         
         stack.push(node);
         node.start = line;
         node.depth = depth;
         return node;
      }
      
      public int close(BraceType type, Line line) {
         BraceNode node = stack.pop();

         if(node == null) {
            throw new IllegalStateException("Unbalanced braces");
         }
         if(node.type != type) {
            throw new IllegalStateException("Unbalanced braces");
         }
         if(node.type == BraceType.COMPOUND) {
            children.add(node);
         }
         node.finish = line;
         return stack.size();
      }
      
      public int depth(int line) {
         if(enclose(line)) {
            for(BraceNode node : children) {
               int result = node.depth(line);
               
               if(result != -1) {
                  return result;
               }
            }
            return depth;
         }
         return -1;
      }
      
      private boolean enclose(int line) {
         if(start == null && finish == null) {
            return true;
         }
         int begin = start.getNumber();
         int end = finish.getNumber();
         
         return line >= begin && line <= end; 
      }
      
      public int size() {
         return stack.size();
      }
      
      @Override
      public String toString() {
         return String.valueOf(type);
      }
   }
   
   private static enum BraceType {
      ARRAY("[", "]"),
      COMPOUND("{", "}"),
      NORMAL("(", ")");
      
      private final String open;
      private final String close;
      
      private BraceType(String open, String close) {
         this.open = open;
         this.close = close;
      }
   }
   
   private static class NodeBuilder implements IndexListener {
      
      private final Stack<IndexFileNode> stack;
      private final IndexDatabase database;
      private final String resource;
      
      public NodeBuilder(IndexDatabase database, String resource) {
         this.stack = new ArrayStack<IndexFileNode>();
         this.database = database;
         this.resource = resource;
      }
      
      public IndexNode build() {
         IndexNode top = stack.pop();
         
         if(!stack.isEmpty()) {
            throw new IllegalStateException("Syntax error in " + resource);
         }
         return top;
      }

      @Override
      public void update(Index index) {
         IndexFileNode node = new IndexFileNode(database, index, resource);
         IndexType type = index.getType();
         int line = index.getLine();
         
         while(!stack.isEmpty()) {
            IndexFileNode top = stack.peek();
            Set<IndexType> parents = top.getParentTypes();
            int offset = top.getLine();
            
            if(offset < line) {
               break;
            }  
            if(parents.contains(type)) {
               if(node.getType().isFunction() && top.getType().isCompound()) {
                  Set<IndexNode> nodes = top.getNodes();
                  
                  top.setParent(node);
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

/*
 * DefaultTypeLoader.java December 2016
 *
 * Copyright (C) 2016, Niall Gallagher <niallg@users.sf.net>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied. See the License for the specific language governing 
 * permissions and limitations under the License.
 */

package org.snapscript.develop.common;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.Closeable;
import java.io.Console;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilePermission;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.FilterReader;
import java.io.FilterWriter;
import java.io.Flushable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.PushbackInputStream;
import java.io.PushbackReader;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.io.SequenceInputStream;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Currency;
import java.util.Date;
import java.util.Deque;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.Formattable;
import java.util.Formatter;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Observable;
import java.util.Observer;
import java.util.PriorityQueue;
import java.util.Properties;
import java.util.Queue;
import java.util.Random;
import java.util.RandomAccess;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.SimpleTimeZone;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;
import java.util.Vector;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

import org.snapscript.agent.log.ProcessLogger;
import org.snapscript.compile.StoreContext;
import org.snapscript.core.Context;
import org.snapscript.core.Type;
import org.snapscript.core.TypeLoader;
import org.snapscript.core.store.ClassPathStore;
import org.snapscript.core.store.Store;

public class DefaultTypeLoader {
   
   private static final Class[] DEFAULT_TYPES = new Class[] {
      String.class,
      System.class,
      Runtime.class,
      Thread.class,
      Runnable.class,
      Integer.class,
      Boolean.class,
      Number.class,
      Double.class,
      Float.class,
      Long.class,
      Byte.class,
      Short.class,
      Comparable.class,
      Math.class,
      Enum.class,
      Object.class,
      StringBuilder.class,
      Character.class,
      ThreadLocal.class,
      Process.class,
      ProcessBuilder.class,
      ClassLoader.class,
      IllegalStateException.class,
      IllegalArgumentException.class,
      Exception.class,
      Map.class,
      List.class,
      ConcurrentHashMap.class,
      ArrayDeque.class,
      ArrayList.class,
      Arrays.class,
      BitSet.class,
      Calendar.class,
      Collections.class,
      Currency.class,
      Date.class,
      EnumMap.class,
      EnumSet.class,
      Formatter.class,
      GregorianCalendar.class,
      HashMap.class,
      HashSet.class,
      Hashtable.class,
      IdentityHashMap.class,
      LinkedHashMap.class,
      LinkedHashSet.class,
      LinkedList.class,
      Locale.class,
      Observable.class,
      PriorityQueue.class,
      Properties.class,
      Random.class,
      ResourceBundle.class,
      Scanner.class,
      ServiceLoader.class,
      SimpleTimeZone.class,
      Stack.class,
      StringTokenizer.class,
      Timer.class,
      TimerTask.class,
      TimeZone.class,
      TreeMap.class,
      TreeSet.class,
      UUID.class,
      Vector.class,
      WeakHashMap.class,
      Collection.class,
      Comparator.class,
      Deque.class,
      Enumeration.class,
      Formattable.class,
      Iterator.class,
      List.class,
      ListIterator.class,
      Map.class,
      Map.Entry.class,
      NavigableMap.class,
      NavigableSet.class,
      Observer.class,
      Queue.class,
      RandomAccess.class,
      Set.class,
      SortedMap.class,
      SortedSet.class,
      IOException.class,
      BufferedInputStream.class,
      BufferedOutputStream.class,
      BufferedReader.class,
      BufferedWriter.class,
      ByteArrayInputStream.class,
      ByteArrayOutputStream.class,
      CharArrayReader.class,
      CharArrayWriter.class,
      Console.class,
      DataInputStream.class,
      DataOutputStream.class,
      File.class,
      FileDescriptor.class,
      FileInputStream.class,
      FileOutputStream.class,
      FilePermission.class,
      FileReader.class,
      FileWriter.class,
      FilterInputStream.class,
      FilterOutputStream.class,
      FilterReader.class,
      FilterWriter.class,
      InputStream.class,
      InputStreamReader.class,
      LineNumberReader.class,
      ObjectInputStream.class,
      ObjectOutputStream.class,
      OutputStream.class,
      OutputStreamWriter.class,
      PipedInputStream.class,
      PipedOutputStream.class,
      PipedReader.class,
      PipedWriter.class,
      PrintStream.class,
      PrintWriter.class,
      PushbackInputStream.class,
      PushbackReader.class,
      RandomAccessFile.class,
      Reader.class,
      SequenceInputStream.class,
      StreamTokenizer.class,
      StringReader.class,
      StringWriter.class,
      Writer.class,
      Closeable.class,
      FileFilter.class,
      FilenameFilter.class,
      FileNotFoundException.class,
      Flushable.class,
      EOFException.class
   };

   private final Map<String, TypeNode> cache;
   private final ProcessLogger logger;
   private final Context context;
   private final Store store;
   
   public DefaultTypeLoader(ProcessLogger logger) {
      this.cache = new ConcurrentHashMap<String, TypeNode>();
      this.store = new ClassPathStore();
      this.context = new StoreContext(store);
      this.logger = logger;
   }

   public Map<String, TypeNode> loadTypes() {
      Map<String, TypeNode> types = new HashMap<String, TypeNode>();
      Set<String> names = new HashSet<String>();
      
      for(Class real : DEFAULT_TYPES) {
         String name = real.getSimpleName();
         names.add(name);
      }
      try {
         TypeLoader loader = context.getLoader();
         int require = names.size();
         int actual = cache.size();
         
         if(actual < require) {
            for(Class real : DEFAULT_TYPES) {
               Type type = loader.loadType(real);
               String name = type.getName();
               
               if(name != null) {
                  TypeNode value = new TypeNode(type, name);
                  cache.put(name, value);
               }
            }
         }
         types.putAll(cache);   
      } catch(Exception e) {
         logger.info("Error creating default types", e);
      }
      return types;
   }
}

/*
 * ScopeNodeChecker.java December 2016
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

package org.snapscript.agent.debug;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.snapscript.core.PrimitivePromoter;

public class ScopeNodeChecker {

   private final PrimitivePromoter promoter;

   public ScopeNodeChecker() {
      this.promoter = new PrimitivePromoter();
   }

   public boolean isPrimitive(Class actual) {
      Class type = promoter.promote(actual);
      
      if (type == String.class) {
         return true;
      }else if (type == Integer.class) {
         return true;
      }else if (type == Double.class) {
         return true;
      }else if (type == Float.class) {
         return true;
      }else if (type == Long.class) {
         return true;
      }else if (type == BigInteger.class) {
         return true;
      }else if (type == BigDecimal.class) {
         return true;
      }else if (type == AtomicInteger.class) {
         return true;
      }else if (type == AtomicLong.class) {
         return true;
      }else if (type == AtomicBoolean.class) {
         return true;
      }else if (type == Boolean.class) {
         return true;
      }else if (type == Short.class) {
         return true;
      }else if (type == Character.class) {
         return true;
      }else if (type == Byte.class) {
         return true;
      }else if (type == Date.class) {
         return true;
      }else if (type == Locale.class) {
         return true;
      }else {
         Class parent = type.getSuperclass();
         
         if(parent != null) {
            if(parent.isEnum() || type.isEnum()) {
               return true;
            }
         }
      }
      return false;
   }
}

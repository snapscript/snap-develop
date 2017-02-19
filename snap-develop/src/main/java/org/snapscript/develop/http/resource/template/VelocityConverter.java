/*
 * VelocityConverter.java December 2016
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

package org.snapscript.develop.http.resource.template;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.snapscript.develop.http.resource.StackTraceError;

public class VelocityConverter {

   private final TemplateModel model;

   public VelocityConverter(TemplateModel model) {
      this.model = model;
   }

   public Object convert(Object value) {
      Transform transform = transform(value);

      if (transform != null) {
         return transform.transform(value);
      }
      return value;
   }

   private Transform transform(Object value) {
      if (value instanceof Date) {
         return new DateTransform();
      }
      if (value instanceof Throwable) {
         return new ExceptionTransform();
      }
      return null;
   }

   private interface Transform<T> {
      public Object transform(T value);
   }

   private class DateTransform implements Transform<Date> {

      @Override
      public String transform(Date date) {
         DateFormat format = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");

         if (date != null) {
            return format.format(date);
         }
         return null;
      }
   }

   private class ExceptionTransform implements Transform<Throwable> {

      @Override
      public StackTraceError transform(Throwable cause) {
         StackTraceError error = new StackTraceError();

         if (cause != null) {
            error.setCause(cause);
            return error;
         }
         return null;
      }
   }
}

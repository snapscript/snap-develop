
package org.snapscript.develop.resource.template;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.snapscript.develop.resource.StackTraceError;

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

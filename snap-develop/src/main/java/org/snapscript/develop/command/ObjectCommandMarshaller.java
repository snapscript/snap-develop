/*
 * ObjectCommandMarshaller.java December 2016
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

package org.snapscript.develop.command;

import com.google.gson.Gson;

public abstract class ObjectCommandMarshaller<T extends Command> implements CommandMarshaller<T> {

   private final CommandType type;
   private final Gson gson;
   
   public ObjectCommandMarshaller(CommandType type) {
      this.gson = new Gson();
      this.type = type;
   }

   @Override
   public T toCommand(String text) {
      int offset = text.indexOf(':');
      String json = text.substring(offset + 1);
      return (T)gson.fromJson(json, type.command);
   }

   @Override
   public String fromCommand(T command) {
      String json = gson.toJson(command);
      return type + ":" + json;
   }
}

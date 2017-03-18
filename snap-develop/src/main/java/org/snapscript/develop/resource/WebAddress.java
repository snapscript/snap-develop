/*
 * WebAddress.java December 2016
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

package org.snapscript.develop.resource;

import java.net.InetSocketAddress;

public class WebAddress {

   private final int port;
   
   public WebAddress(int port) {
      this.port = port;
   }
   
   public InetSocketAddress getLocalAddress() {
      try {
         return new InetSocketAddress(port);
      }catch(Exception e){
         return new InetSocketAddress(port);
      }
   }
   
   public InetSocketAddress getExternalAddress() {
      try {
         return new InetSocketAddress("localhost", port);
      }catch(Exception e){
         return new InetSocketAddress(port);
      }
   }
}

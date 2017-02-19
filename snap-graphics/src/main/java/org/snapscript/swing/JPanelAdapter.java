/*
 * JPanelAdapter.java December 2016
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

package org.snapscript.swing;

import java.awt.Graphics;

import javax.swing.JPanel;

public class JPanelAdapter extends JPanel {

   private final JPanelInterface panel;
   
   public JPanelAdapter(JPanelInterface panel) {
      this.panel = panel;
   }
   
   @Override
   public void update(Graphics g) {
      panel.update(this, g);
   }
   
   @Override
   public void paint(Graphics g) {
      panel.paint(this, g);
   }
}

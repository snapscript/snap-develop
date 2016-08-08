/*
 * Reference.java February 2006
 *
 * Copyright (C) 2006, Niall Gallagher <niallg@users.sf.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General 
 * Public License along with this library; if not, write to the 
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330, 
 * Boston, MA  02111-1307  USA
 */

package org.snapscript.template.translate;

/**
 * The <code>Reference</code> object is used to determine whether 
 * any of the files used to compose the JSP have changed. This will
 * allow background compilation of the sources. Once the files 
 * have expired the JSP sources will be translated and compiled
 * with the most up to date version.
 *  
 * @author Niall Gallagher
 */ 
public interface Reference {

   /**
    * This checks if the source or its includes have expired. This 
    * will check the JSP and its includes to determine if the 
    * source has changed in any way. For background compilation 
    * this method can be used to refresh the pages to ensure that
    * the most up to date code is active.
    *
    * @return this returns true if any of the JSP files changed
    */ 
   public boolean isModified();       
}

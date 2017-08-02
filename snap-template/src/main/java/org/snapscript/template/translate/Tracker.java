package org.snapscript.template.translate;

import simple.page.Workspace;
import java.util.ArrayList;
import java.util.List;
import java.io.File;

/**
 * The <code>Tracker</code> is used to keep track of files within
 * the project source directory. This allows the compiler to perform
 * background compilation, such that a translated and compiled JSP
 * file can be monitored for changes. Should a change occur the 
 * JSP can be translated and compiled with any modifications. This
 * also performs its checks in a way that allows multiple contexts
 * to be used. This allows JSP files to be overridden by simply
 * changing the context it lives within.
 * <p>
 * As well as allowing the compiler to determine when modifications
 * are made to the source JSP, this will allow the generator to
 * determine the JSP source files included within the root file.
 * This allows, informative comments to be embedded.
 * 
 * @author Niall Gallagher
 */ 
class Tracker implements Reference {

   /**
    * Provides the file system view for the JSP translation.
    */ 
   private Workspace project;

   /**
    * Keeps track of all modification times for included files.
    */ 
   private List history;
   
   /**
    * Maintains a list of the JSP source files that are used.
    */ 
   private List include;        

   /**
    * Constructor for the <code>Tracker</code> object. This is used 
    * to keep track of the JSP files and its includes. The project is
    * provided to allow this object to monitor modification times for
    * root JSP and the sources it includes.  
    *
    * @param project this is the workspace used to locate the files
    */  
   public Tracker(Workspace project) {
      this.include = new ArrayList();
      this.history = new ArrayList();
      this.project = project;      
   }

   /**
    * This provides the list of JSP files that be been used to compose 
    * the resulting source. The included files are identified using
    * a URI path that references a file from the <code>Context</code>
    * used. This allows a cascading context to search for the file, 
    * such that the file can move and change context, allowing the
    * translator and compiler to initiate compilation again.
    *
    * @return this returns a list of URI paths for JSP includes
    */ 
   public List getIncludes(){
      return include;           
   }

   /**
    * This is used to include other JSP files or text files, which 
    * can be translated and compiled into the resulting source. The
    * included file is monitored by the JSP engine so that if the
    * file changes it can be compiled back in to the source.
    * <p> 
    * All included files must be absolute so that its details can
    * be retrieved using a <code>Context</code> object. This allows
    * the modification times for the source to be determined. 
    *
    * @param path an absolute path to the included JSP file
    */ 
   public void addInclude(String path) {
      addInclude(path, getFile(path));
   }

   /**
    * This is used to include other JSP files or text files, which 
    * can be translated and compiled into the resulting source. The
    * included file is monitored by the JSP engine so that if the
    * file changes it can be compiled back in to the source.
    * <p> 
    * All included files must be absolute so that its details can
    * be retrieved using a <code>Context</code> object. This allows
    * the modification times for the source to be determined. 
    *
    * @param path an absolute path to the included JSP file
    * @param file this is an initial reference to the JSP file 
    */ 
   private void addInclude(String path, File file) {
      include.add(new Entry(path, file));
      history.add(path);      
   } 

   /**
    * This will acquire a <code>File</code> for the specified URI 
    * path. This is used so that if there are multiple contexts
    * then the file reference can change, thus allowing the JSP
    * to be translated and compiled from a new location.
    *
    * @param path this is the URI path used to reference the JSP
    *
    * @return this is the file reference pointing to the JSP
    */ 
   private File getFile(String path) {
      return project.getSourceFile(path);           
   }
   
   /**
    * This checks if the source or its includes have expired. This 
    * will check the JSP and its includes to determine if the 
    * source has changed in any way. For background compilation 
    * this method can be used to refresh the pages to ensure that
    * the most up to date code is active.
    *
    * @return this returns true if any of the JSP files changed
    */    
   public boolean isModified() {
      for(int i = 0; i < include.size(); i++) {
         Entry file = (Entry) include.get(i);
         
         if(file.isModified()) {
            return true;                 
         }
      }           
      return false;           
   }
   
   /**
    * This is used to determine whether the source files have
    * been deleted. This is useful when a page should be purged
    * from the system. A deletion of the file will allow memory
    * to be freed from the VM and ensures it does not resolve.
    *
    * @return true if any of the JSP files were deleted
    */   
   public boolean isDeleted() {
      for(int i = 0; i < include.size(); i++) {
         Entry file = (Entry) include.get(i);
         
         if(file.isDeleted()) {
            return true;                 
         }
      }           
      return false;           
   }

   /**
    * The <code>Entry</code> object is used to keep track of the JSP
    * file modification times. This ensures that should the source
    * change this will indicate it. This reacquires the file each 
    * time the <code>Reference.isModified</code> is invoked ensuring
    * that JSP can be housed in, or moved to, a separate context.
    * 
    * @see simple.page.Workspace
    */ 
   private class Entry {

      /**
       * This is the URI path used to reference the JSP source.
       */            
      public String path;

      /**
       * Keeps a time stamp for the modification time of the JSP.
       */ 
      public long stamp;

      /**
       * Allows deletion of the JSP can unload the page class.
       */ 
      public boolean exist;

      /**
       * Constructor for the <code>Entry</code> object. This is used
       * to keep track of the JSP sources, and ensures any changes
       * will allow the engine to translate and compile the source
       * with the most up to date version.
       *
       * @param path this is the URI path used to target the file
       * @param file this is used to capture a snapshot of the file
       */ 
      public Entry(String path, File file) {
         this.stamp = file.lastModified();
         this.exist = file.exists();
         this.path = path;
      }

      /**
       * This is used to determine whether the source JSP has been
       * modified. This allows each file used to compose the page
       * to be monitored to determine when to refresh the page.
       * This is also true if the file has been deleted.
       *
       * @return this returns true if the JSP file has changed
       */       
      public boolean isModified(){
         File file = getFile(path);
         
         if(exist != file.exists()){
            return true;
         }
         if(stamp != file.lastModified()){
            return true;
         }
         return false;
      }

      /**
       * This is used to determine whether the source JSP has been 
       * deleted. This allows the file to be purged from the system
       * if it has been deleted from the file system. If the JSP
       * file has changed context this will still return true. 
       *
       * @return this returns true if the file has been deleted
       */
      public boolean isDeleted(){
         File file = getFile(path);
         
         if(exist != file.exists()){
            return true;
         }
         return false;              
      }
   }
}
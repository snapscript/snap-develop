
module ThreadManager {
   
   var suspendedThreads = {};
   var threadEditorFocus = {};
   
   export function createThreads() {
      createRoute("BEGIN", startThreads, clearThreads);
      createRoute("SCOPE", updateThreads, clearVariables);
      createRoute("TERMINATE", deleteThreads);
      createRoute("EXIT", deleteThreads);
   }
   
   function startThreads(socket, type, text) {
      var message = JSON.parse(text);
      
      suspendedThreads = {};
      clearFocusThread();
      clearVariables();
      Profiler.clearProfiler();
      clearThreads();
      
      StatusPanel.showProcessStatus(message.resource, message.process);
   }
   
   function deleteThreads(socket, type, text) {
      var terminateProcess = text;
      
      if(threadEditorFocus != null && threadEditorFocus.process == terminateProcess) { // clear if it dies
         terminateThreads();
      }
   }
   
   export function terminateThreads() {
      suspendedThreads = {};
      clearFocusThread();
      FileEditor.clearEditorHighlights(); // this should be done in editor.js, i.e createRoute("EXIT" ... )
      clearVariables();
      clearThreads();
   }
   
   export function clearThreads() {
      clearFocusThread();
      w2ui['threads'].records = [];
      w2ui['threads'].refresh();
      $("#process").html("");
   }
   
   function updateThreads(socket, type, text) {
      var threadScope = JSON.parse(text);
      var editorData = FileEditor.loadEditor();
     
      if(isThreadFocusResumed(threadScope)) {
         clearFocusThread(); // clear focus as it is a resume
         updateThreadPanels(threadScope);
         FileEditor.clearEditorHighlights(); // the thread has resumed so clear highlights
      } else {
         if(threadEditorFocus.thread == threadScope.thread) { // has the thread been suspended
            if(isThreadFocusUpdateNew(threadScope)) {
               updateFocusedThread(threadScope); // something new happened so focus editor
               updateThreadPanels(threadScope);
            }
         } else if(threadEditorFocus.thread == null) {  // we have to focus the thread
            focusThread(threadScope);
            updateThreadPanels(threadScope);
         } else {
            var currentScope = suspendedThreads[threadScope.thread];
            
            if(isThreadScopeDifferent(currentScope, threadScope)) {
               updateThreadPanels(threadScope);
            }
         }
      }
      suspendedThreads[threadScope.thread] = threadScope;
      showThreadBreakpointLine(threadScope); // show breakpoint on editor
   } 
   
   function isThreadScopeDifferent(leftScope, rightScope) {
      if(leftScope != null && rightScope != null) {
         if(leftScope.thread != rightScope.thread) {
            return true;
         }
         if(leftScope.status != rightScope.status) {
            return true;
         }
         if(leftScope.resource != rightScope.resource) {
            return true;
         }
         if(leftScope.line != rightScope.line) {
            return true;
         }
         return false;
      }
      return leftScope != rightScope;
   }
   
   function showThreadBreakpointLine(threadScope) { // show breakpoint if focused
      var editorData = FileEditor.loadEditor();
      
      if(threadEditorFocus.thread == threadScope.thread) {
         if(editorData.resource.filePath == threadScope.resource && threadScope.status == 'SUSPENDED') {
            FileEditor.createEditorHighlight(threadScope.line, "threadHighlight");
         }
      }
   }
   
   function updateThreadPanels(threadScope){
      suspendedThreads[threadScope.thread] = threadScope; // N.B update suspended threads before rendering
      showThreads();
      showVariables();
   }
   
   function updateFocusedThread(threadScope) {
      if(isThreadFocusLineChange(threadScope)) { // has the update resulted in a new line or resource
         if(isThreadFocusResourceChange(threadScope)) { // do we need to update the editor with a new resource
            var resourcePathDetails = FileTree.createResourcePath(threadScope.resource);
         
            FileExplorer.openTreeFile(resourcePathDetails.resourcePath, function(){
               updateThreadFocus(threadScope);
               FileEditor.showEditorLine(threadScope.line);
            });
         } else {
            updateThreadFocus(threadScope);
            FileEditor.showEditorLine(threadScope.line);
         }
      } else {
         updateThreadFocus(threadScope); // record focus thread
      }
   }
   
   function focusThread(threadScope) {
      var editorData = FileEditor.loadEditor();
      
      if(editorData.resource.filePath != threadScope.resource) { // do we need to change resource on hit of breakpoint
         var resourcePathDetails = FileTree.createResourcePath(threadScope.resource);
      
         FileExplorer.openTreeFile(resourcePathDetails.resourcePath, function(){
            updateThreadFocus(threadScope);
            FileEditor.showEditorLine(threadScope.line);
         });
      } else {
         updateThreadFocus(threadScope);
         FileEditor.showEditorLine(threadScope.line);
      }
   }
   
   function isThreadFocusResumed(threadScope) {
      if(threadScope.thread == threadScope.thread) {
         return threadScope.status != 'SUSPENDED'; // the thread has resumed
      }
      return false;
   }
   
   function isThreadFocusUpdateNew(threadScope) { // have we got a new update
      if(threadEditorFocus.thread == threadScope.thread) { // is this a new update
         if(threadScope.status == 'SUSPENDED') {
            if(threadEditorFocus.key != threadScope.key) { // thread position change
               return true;
            }
            if(threadEditorFocus.change != threadScope.change) { // thread variables change, e.g browse
               return true;
            }
         }
      }
      return false;
   }
   
   function isThreadFocusPositionChange(threadScope) {
      return isThreadFocusLineChange(threadScope) || isThreadFocusResourceChange(threadScope);
   }
   
   function isThreadFocusLineChange(threadScope) {
      if(threadEditorFocus.thread == threadScope.thread) {
         return threadEditorFocus.line != threadScope.line; // hash the thread or focus line changed
      }
      return false;
   }
   
   function isThreadFocusResourceChange(threadScope) {
      if(threadEditorFocus.thread == threadScope.thread) {
         var editorData = FileEditor.loadEditor();
         return editorData.resource.filePath != threadScope.resource; // is there a need to update the editor
      }
      return false;
   }
   
   export function focusedThread() {
      if(threadEditorFocus.thread != null) {
         return suspendedThreads[threadEditorFocus.thread];
      }
      return null;
   }
   
   function clearFocusThread() {
      clearVariables(); // clear the browse tree
      threadEditorFocus = {
         thread: null, 
         process: null,
         resource: null, 
         change: -1,
         line: -1, 
         key: -1
      }; 
   }
   
   function updateThreadFocus(threadScope) {
      threadEditorFocus = {
            thread: threadScope.thread,
            process: threadScope.process,
            resource: threadScope.resource, 
            line: threadScope.line, 
            change: threadScope.change,
            key: threadScope.key
         }; 
   } 
   
   export function updateThreadFocusByName(threadName) {
      var threadScope = suspendedThreads[threadName];
      updateThreadFocus(threadScope);
   } 
   
   export function focusedThreadVariables() {
      if(threadEditorFocus.thread != null) {
         var threadScope = suspendedThreads[threadEditorFocus.thread];
         
         if(threadScope != null) {
            return threadScope.variables;
         }
      }
      return {};
   }
   
   export function focusedThreadEvaluation() {
      if(threadEditorFocus.thread != null) {
         var threadScope = suspendedThreads[threadEditorFocus.thread];
         
         if(threadScope != null) {
            return threadScope.evaluation;
         }
      }
      return {};
   }
   
   export function showThreads() {
      var editorData = FileEditor.loadEditor();
      var threadRecords = [];
      var threadIndex = 1;
      
      for (var threadName in suspendedThreads) {
         if (suspendedThreads.hasOwnProperty(threadName)) {
            var threadScope = suspendedThreads[threadName];
            var displayStyle = 'threadSuspended';
            var active = "&nbsp;<input type='radio'>";
            
            showThreadBreakpointLine(threadScope);
            
            if(threadScope.status != 'SUSPENDED') {
               displayStyle = 'threadRunning';
            } else {
               if(threadEditorFocus.thread == threadScope.thread) {
                  active = "&nbsp;<input type='radio' checked>";
               }
            }
            var displayName = "<div title='"+threadScope.stack+"' class='"+displayStyle+"'>"+threadName+"</div>";
            var resourcePathDetails = FileTree.createResourcePath(threadScope.resource);
            
            threadRecords.push({
               recid: threadIndex++,
               name: displayName,
               thread: threadName,
               status: threadScope.status,
               active: active,
               instruction: threadScope.instruction,
               variables: threadScope.variables,
               resource: threadScope.resource,
               key: threadScope.key,
               line: threadScope.line,
               script: resourcePathDetails.resourcePath
            });
         }
      }
      updateTableRecords(threadRecords, 'threads'); // update if changed only
   }
}

ModuleSystem.registerModule("threads", "Thread module: threads.js", ThreadManager.createThreads, [ "common", "socket", "explorer" ]);
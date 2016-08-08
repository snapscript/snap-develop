var suspendedThreads = {};
var threadEditorFocus = {};

function createThreads() {
   createRoute("BEGIN", startThreads, clearThreads);
   createRoute("SCOPE", updateThreads, clearVariables);
}

function startThreads(socket, type, text) {
   var message = JSON.parse(text);
   
   suspendedThreads = {};
   clearFocusThread();
   clearVariables();
   clearProfiler();
   clearThreads();
   $("#process").html("<i>&nbsp;RUNNING: " + message.resource + " ("+message.process+") "+message.duration+" milliseconds</i>");
}

function terminateThreads() {
   suspendedThreads = {};
   clearFocusThread();
   clearEditorHighlights(); // this should be done in editor.js, i.e createRoute("EXIT" ... )
   clearVariables();
   clearThreads();
}

function clearThreads() {
   clearFocusThread();
   w2ui['threads'].records = [];
   w2ui['threads'].refresh();
   $("#process").html("");
}

function updateThreads(socket, type, text) {
   var threadScope = JSON.parse(text);
   var editorData = loadEditor();
  
   if(isThreadFocusResumed(threadScope)) {
      clearFocusThread(); // clear focus as it is a resume
   } else {
      if(threadEditorFocus.thread == threadScope.thread) { // has the thread been suspended
         if(isThreadFocusUpdateNew(threadScope)) {
            updateFocusedThread(threadScope); // something new happened so focus editor
         }
      } else {
         if(threadEditorFocus.thread == null) {  // we have to focus the thread
            focusThread(threadScope);
         }
      }
   }
   suspendedThreads[threadScope.thread] = threadScope;
   showThreads();
   showVariables();
} 

function updateFocusedThread(threadScope) {
   if(isThreadFocusLineChange(threadScope)) { // has the update resulted in a new line or resource
      if(isThreadFocusResourceChange(threadScope)) { // do we need to update the editor with a new resource
         var resourcePathDetails = createResourcePath(threadScope.resource);
         
         openTreeFile(resourcePathDetails.resourcePath, function(){
            updateThreadFocus(threadScope);
            showEditorLine(threadScope.line);
         });
      } else {
         updateThreadFocus(threadScope);
         showEditorLine(threadScope.line);
      }
   } else {
      updateThreadFocus(threadScope); // record focus thread
   }
}

function focusThread(threadScope) {
   var editorData = loadEditor();
   
   if(editorData.resource.filePath != threadScope.resource) { // do we need to change resource on hit of breakpoint
      var resourcePathDetails = createResourcePath(threadScope.resource);
      
      openTreeFile(resourcePathDetails.resourcePath, function(){
         updateThreadFocus(threadScope);
         showEditorLine(threadScope.line);
      });
   } else {
      updateThreadFocus(threadScope);
      showEditorLine(threadScope.line);
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
         return threadEditorFocus.key != threadScope.key
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
      var editorData = loadEditor();
      return editorData.resource.filePath != threadScope.resource; // is there a need to update the editor
   }
   return false;
}

function focusedThread() {
   if(threadEditorFocus.thread != null) {
      return suspendedThreads[threadEditorFocus.thread];
   }
   return null;
}

function clearFocusThread() {
   threadEditorFocus = {
      thread: null, 
      resource: null, 
      line: -1, 
      key: -1
   }; 
}

function updateThreadFocus(threadScope) {
   threadEditorFocus = {
         thread: threadScope.thread, 
         resource: threadScope.resource, 
         line: threadScope.line, 
         key: threadScope.key
      }; 
} 

function focusedThreadVariables() {
   if(threadEditorFocus.thread != null) {
      var threadScope = suspendedThreads[threadEditorFocus.thread];
      
      if(threadScope != null) {
         return threadScope.variables;
      }
   }
   return {};
}

function showThreads() {
   var editorData = loadEditor();
   var threadRecords = [];
   var threadIndex = 1;
   
   for (var threadName in suspendedThreads) {
      if (suspendedThreads.hasOwnProperty(threadName)) {
         var threadScope = suspendedThreads[threadName];
         var displayStyle = 'threadSuspended';
         
         if(editorData.resource.filePath == threadScope.resource && threadScope.status == 'SUSPENDED') {
            createEditorHighlight(threadScope.line, "threadHighlight");
         }
         if(threadScope.status != 'SUSPENDED') {
            displayStyle = 'threadRunning';
         }
         var displayName = "<div title='"+threadScope.stack+"' class='"+displayStyle+"'>"+threadName+"</div>";
         var resourcePathDetails = createResourcePath(threadScope.resource);
         
         threadRecords.push({
            recid: threadIndex++,
            name: displayName,
            thread: threadName,
            status: threadScope.status,
            depth: threadScope.depth,
            instruction: threadScope.instruction,
            variables: threadScope.variables,
            resource: threadScope.resource,
            key: threadScope.key,
            line: threadScope.line,
            script: resourcePathDetails.resourcePath
         });
      }
   }
   w2ui['threads'].records = threadRecords;
   w2ui['threads'].refresh();
}

registerModule("threads", "Thread module: threads.js", createThreads, [ "common", "socket", "explorer" ]);
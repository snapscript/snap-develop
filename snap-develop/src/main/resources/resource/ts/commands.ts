
module Command {
   
   export function searchTypes() {
      DialogBuilder.createListDialog(function(text){
         var typesFound = findTypesMatching(text);
         var typeRows = [];
        
         for(var i = 0; i < typesFound.length; i++) {
            var debugToggle = ";debug";
            var locationPath = window.document.location.pathname;
            var locationHash = window.document.location.hash;
            var debug = locationPath.indexOf(debugToggle, locationPath.length - debugToggle.length) !== -1;
            var resourceLink = "/project/" + typesFound[i].project;
            
            if(debug) {
               resourceLink += debugToggle;
            }
            resourceLink += "#" + typesFound[i].resource;
            
            var typeCell = {
               text: typesFound[i].name,
               link: resourceLink,
               style: typesFound[i].type == 'module' ? 'moduleNode' : 'typeNode'
            };
            var resourceCell = {
               text: typesFound[i].resource,
               link: resourceLink,
               style: 'resourceNode'
            };
            typeRows.push([typeCell, resourceCell]);
         }
         return typeRows;
     }, "Search Types");
   }
   
   function findTypesMatching(text) {
      var response = [];
      
      jQuery.ajax({
         url: '/type/' + document.title + '?expression=' + text,
         success: function (typeMatches) {
            var sortedMatches = [];
            
            for (var typeMatch in typeMatches) {
               if (typeMatches.hasOwnProperty(typeMatch)) {
                  sortedMatches.push(typeMatch);
               }
            }
            sortedMatches.sort();
   
            for(var i = 0; i < sortedMatches.length; i++) {
               var typeMatch = sortedMatches[i];
               var typeReference = typeMatches[typeMatch];
               var typeEntry = {
                     name: typeReference.name,
                     resource: typeReference.resource,
                     type: typeReference.type,
                     project: document.title
               };
               response.push(typeEntry);
            }
         },
         async: false
      });
      return response;
   }
   
   export function exploreDirectory(resourcePath) {
      if(FileTree.isResourceFolder(resourcePath.filePath)) {
         var message = JSON.stringify({
            project : document.title,
            resource : resourcePath.filePath,
         });
         socket.send("EXPLORE:" + message);
      }
   }
   
   export function pingProcess() {
      if(isSocketOpen()) {
         socket.send("PING:" + document.title);
      }
   }
   
   export function renameFile(resourcePath) {
      var originalFile = resourcePath.filePath;
      
      DialogBuilder.renameFileTreeDialog(resourcePath, true, function(resourceDetails) {
         var message = JSON.stringify({
            project : document.title,
            from : originalFile,
            to: resourceDetails.filePath
         });
         socket.send("RENAME:" + message);
         renameEditorTab(resourcePath.resourcePath, resourceDetails.resourcePath); // rename tabs if open
      });
   }
   
   export function renameDirectory(resourcePath) {
      var originalPath = resourcePath.filePath;
      var directoryPath = FileTree.createResourcePath(originalPath + ".#"); // put a # in to trick in to thinking its a file
      
      DialogBuilder.renameDirectoryTreeDialog(directoryPath, true, function(resourceDetails) {
         var message = JSON.stringify({
            project : document.title,
            from : originalPath,
            to: resourceDetails.filePath
         });
         socket.send("RENAME:" + message);
      });
   }
   
   export function newFile(resourcePath) {
      DialogBuilder.newFileTreeDialog(resourcePath, true, function(resourceDetails) {
         if(!FileTree.isResourceFolder(resourceDetails.filePath)) {
            var message = JSON.stringify({
               project : document.title,
               resource : resourceDetails.filePath,
               source : "",
               directory: false,
               create: true
            });
            ProcessConsole.clearConsole();
            socket.send("SAVE:" + message);
            FileEditor.updateEditor("", resourceDetails.projectPath);
         }
      });
   }
   
   export function newDirectory(resourcePath) {
      DialogBuilder.newDirectoryTreeDialog(resourcePath, true, function(resourceDetails) {
         if(FileTree.isResourceFolder(resourceDetails.filePath)) {
            var message = JSON.stringify({
               project : document.title,
               resource : resourceDetails.filePath,
               source : "",
               directory: true,
               create: true
            });
            ProcessConsole.clearConsole();
            socket.send("SAVE:" + message);
         }
      });
   }
   
   export function saveFile() {
      saveFileWithAction(function(){}, true);
   }
   
   function saveFileWithAction(saveCallback, update) {
      var editorData = FileEditor.loadEditor();
      if (editorData.resource == null) {
         DialogBuilder.openTreeDialog(null, false, function(resourceDetails) {
            saveEditor(update);
            saveCallback();
         });
      } else {
         if (FileEditor.isEditorChanged()) {
            DialogBuilder.openTreeDialog(editorData.resource, true, function(resourceDetails) {
               saveEditor(update);
               saveCallback();
            });
         } else {
            ProcessConsole.clearConsole();
            saveCallback();
         }
      }
   }
   
   export function saveEditor(update) {
      var editorData = FileEditor.loadEditor();
      var editorPath = editorData.resource;
      
      if(editorPath != null) {
         var message = JSON.stringify({
            project : document.title,
            resource : editorPath.filePath,
            source : editorData.source,
            directory: false,
            create: false
         });
         ProcessConsole.clearConsole();
         socket.send("SAVE:" + message);
         
         if(update) { // should the editor be updated?
            FileEditor.updateEditor(editorData.source, editorPath.projectPath);
         }
      }
   }
   
   export function deleteFile(resourceDetails) {
      var editorData = FileEditor.loadEditor();
      if(resourceDetails == null && editorData.resource != null) {
         resourceDetails = editorData.resource;
      }
      if(resourceDetails != null) {
         var editorData = FileEditor.loadEditor();
         var editorResource = editorData.resource;
         var message = "Delete resource " + editorResource.filePath;
         
         Alerts.createConfirmAlert("Delete File", message, "Delete", "Cancel", 
               function(){
                  var message = JSON.stringify({
                     project : document.title,
                     resource : resourceDetails.filePath
                  });
                  ProcessConsole.clearConsole();
                  socket.send("DELETE:" + message);
                  
                  if(editorData.resource != null && editorData.resource.resourcePath == resourceDetails.resourcePath) { // delete focused file
                     FileEditor.resetEditor();
                  }
                  deleteEditorTab(resourceDetails.resourcePath); // rename tabs if open
               },
               function(){});
      }
   } 
   
   export function deleteDirectory(resourceDetails) {
      if(resourceDetails != null) {
         var message = JSON.stringify({
            project : document.title,
            resource : resourceDetails.filePath
         });
         ProcessConsole.clearConsole();
         socket.send("DELETE:" + message);
      }
   }
   
   export function runScript() {
      saveFileWithAction(function() {
         var editorData = FileEditor.loadEditor();
         var message = JSON.stringify({
            breakpoints : editorData.breakpoints,
            project : document.title,
            resource : editorData.resource.filePath,
            source : editorData.source
         });
         socket.send("EXECUTE:" + message);
      }, true); // save editor
   }
   
   export function updateScriptBreakpoints() {
      var editorData = FileEditor.loadEditor();
      var message = JSON.stringify({
         breakpoints : editorData.breakpoints,
         project : document.title,
      });
      socket.send("BREAKPOINTS:" + message);
   }
   
   export function stepOverScript() {
      var threadScope = ThreadManager.focusedThread();
      if(threadScope != null) {
         var message = JSON.stringify({
            thread: threadScope.thread,
            type: "STEP_OVER"
         });
         FileEditor.clearEditorHighlights();
         socket.send("STEP:" + message);
      }
   }
   
   export function stepInScript() {
      var threadScope = ThreadManager.focusedThread();
      if(threadScope != null) {
         var message = JSON.stringify({
            thread: threadScope.thread,
            type: "STEP_IN"
         });
         FileEditor.clearEditorHighlights();
         socket.send("STEP:" + message);
      }
   }
   
   export function stepOutScript() {
      var threadScope = ThreadManager.focusedThread();
      if(threadScope != null) {
         var message = JSON.stringify({
            thread: threadScope.thread,
            type: "STEP_OUT"
         });
         FileEditor.clearEditorHighlights(); 
         socket.send("STEP:" + message);
      }
   }
   
   export function resumeScript() {
      var threadScope = ThreadManager.focusedThread();
      if(threadScope != null) {
         var message = JSON.stringify({
            thread: threadScope.thread,
            type: "RUN"
         });
         FileEditor.clearEditorHighlights(); 
         socket.send("STEP:" + message);
      }
   }
   
   export function stopScript() {
      socket.send("STOP");
   }
   
   export function browseScriptVariables(variables) {
      var threadScope = ThreadManager.focusedThread();
      if(threadScope != null) {
         var message = JSON.stringify({
            thread: threadScope.thread,
            expand: variables
         });
         socket.send("BROWSE:" + message);
      }
   }
   
   export function browseScriptEvaluation(variables, expression, refresh) {
      var threadScope = ThreadManager.focusedThread();
      if (threadScope != null) {
          var message = JSON.stringify({
              thread: threadScope.thread,
              expression: expression,
              expand: variables,
              refresh: refresh
          });
          socket.send("EVALUATE:" + message);
      }
   }
   
   export function attachProcess(process) {
      var statusFocus = currentStatusFocus(); // what is the current focus
      var editorData = FileEditor.loadEditor();
      var message = JSON.stringify({
         process: process,
         breakpoints : editorData.breakpoints,
         project : document.title,
         focus: statusFocus != process // toggle the focus
      });
      socket.send("ATTACH:" + message); // attach to process
   }
   
   export function switchLayout() {
      var debugToggle = ";debug";
      var locationPath = window.document.location.pathname;
      var locationHash = window.document.location.hash;
      var debug = locationPath.indexOf(debugToggle, locationPath.length - debugToggle.length) !== -1;
      
      if(debug) {
         var remainingPath = locationPath.substring(0, locationPath.length - debugToggle.length);
         document.location = remainingPath + locationHash;
      } else {
         document.location = locationPath + debugToggle + locationHash;
      }
   }
   
   export function evaluateExpression() {
      DialogBuilder.evaluateExpressionDialog();
   }
   
   export function switchProject() {
      document.location="/";
   }
}

ModuleSystem.registerModule("commands", "Commands module: commands.js", null, [ "common", "editor", "tree", "threads" ]);
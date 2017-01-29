
function searchTypes() {
   createListDialog(function(text){
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

function exploreDirectory(resourcePath) {
   if(isResourceFolder(resourcePath.filePath)) {
      var message = JSON.stringify({
         project : document.title,
         resource : resourcePath.filePath,
      });
      socket.send("EXPLORE:" + message);
   }
}

function pingProcess() {
   if(isSocketOpen()) {
      socket.send("PING:" + document.title);
   }
}

function renameFile(resourcePath) {
   var originalFile = resourcePath.filePath;
   
   renameFileTreeDialog(resourcePath, true, function(resourceDetails) {
      var message = JSON.stringify({
         project : document.title,
         from : originalFile,
         to: resourceDetails.filePath
      });
      socket.send("RENAME:" + message);
   });
}

function renameDirectory(resourcePath) {
   var originalPath = resourcePath.filePath;
   var directoryPath = createResourcePath(originalPath + ".#"); // put a # in to trick in to thinking its a file
   
   renameDirectoryTreeDialog(directoryPath, true, function(resourceDetails) {
      var message = JSON.stringify({
         project : document.title,
         from : originalPath,
         to: resourceDetails.filePath
      });
      socket.send("RENAME:" + message);
   });
}

function newFile(resourcePath) {
   newFileTreeDialog(resourcePath, true, function(resourceDetails) {
      if(!isResourceFolder(resourceDetails.filePath)) {
         var message = JSON.stringify({
            project : document.title,
            resource : resourceDetails.filePath,
            source : "",
            directory: false,
            create: true
         });
         clearConsole();
         socket.send("SAVE:" + message);
         updateEditor("", resourceDetails.projectPath);
      }
   });
}

function newDirectory(resourcePath) {
   newDirectoryTreeDialog(resourcePath, true, function(resourceDetails) {
      if(isResourceFolder(resourceDetails.filePath)) {
         var message = JSON.stringify({
            project : document.title,
            resource : resourceDetails.filePath,
            source : "",
            directory: true,
            create: true
         });
         clearConsole();
         socket.send("SAVE:" + message);
      }
   });
}

function saveFile() {
   saveFileWithAction(function(){}, true);
}

function saveFileWithAction(saveCallback, update) {
   var editorData = loadEditor();
   if (editorData.resource == null) {
      openTreeDialog(null, false, function(resourceDetails) {
         saveEditor(update);
         saveCallback();
      });
   } else {
      if (isEditorChanged()) {
         openTreeDialog(editorData.resource, true, function(resourceDetails) {
            saveEditor(update);
            saveCallback();
         });
      } else {
         clearConsole();
         saveCallback();
      }
   }
}

function saveEditor(update) {
   var editorData = loadEditor();
   var editorPath = editorData.resource;
   
   if(editorPath != null) {
      var message = JSON.stringify({
         project : document.title,
         resource : editorPath.filePath,
         source : editorData.source,
         directory: false,
         create: false
      });
      clearConsole();
      socket.send("SAVE:" + message);
      
      if(update) { // should the editor be updated?
         updateEditor(editorData.source, editorPath.projectPath);
      }
   }
}

function deleteFile(resourceDetails) {
   var editorData = loadEditor();
   if(resourceDetails == null && editorData.resource != null) {
      resourceDetails = editorData.resource;
   }
   if(resourceDetails != null) {
      var editorData = loadEditor();
      var editorResource = editorData.resource;
      var message = "Delete resource " + editorResource.filePath;
      
      Alerts.createConfirmAlert("Delete File", message, "Delete", "Cancel", 
            function(){
               var message = JSON.stringify({
                  project : document.title,
                  resource : resourceDetails.filePath
               });
               clearConsole();
               socket.send("DELETE:" + message);
               
               if(editorData.resource != null && editorData.resource.resourcePath == resourceDetails.resourcePath) { // delete focused file
                  resetEditor();
               }
            },
            function(){});
   }
} 

function deleteDirectory(resourceDetails) {
   if(resourceDetails != null) {
      var message = JSON.stringify({
         project : document.title,
         resource : resourceDetails.filePath
      });
      clearConsole();
      socket.send("DELETE:" + message);
   }
}

function runScript() {
   var waitingProcessSystem = findStatusWaitingProcessSystem();
   saveFileWithAction(function() {
      var editorData = loadEditor();
      var message = JSON.stringify({
         breakpoints : editorData.breakpoints,
         project : document.title,
         resource : editorData.resource.filePath,
         system: waitingProcessSystem,
         source : editorData.source,
      });
      socket.send("EXECUTE:" + message);
   }, true); // save editor
}

function updateScriptBreakpoints() {
   var editorData = loadEditor();
   var message = JSON.stringify({
      breakpoints : editorData.breakpoints,
      project : document.title,
   });
   socket.send("BREAKPOINTS:" + message);
}

function stepOverScript() {
   var threadScope = focusedThread();
   if(threadScope != null) {
      var message = JSON.stringify({
         thread: threadScope.thread,
         type: "STEP_OVER"
      });
      clearEditorHighlights();
      socket.send("STEP:" + message);
   }
}

function stepInScript() {
   var threadScope = focusedThread();
   if(threadScope != null) {
      var message = JSON.stringify({
         thread: threadScope.thread,
         type: "STEP_IN"
      });
      clearEditorHighlights();
      socket.send("STEP:" + message);
   }
}

function stepOutScript() {
   var threadScope = focusedThread();
   if(threadScope != null) {
      var message = JSON.stringify({
         thread: threadScope.thread,
         type: "STEP_OUT"
      });
      clearEditorHighlights(); 
      socket.send("STEP:" + message);
   }
}

function resumeScript() {
   var threadScope = focusedThread();
   if(threadScope != null) {
      var message = JSON.stringify({
         thread: threadScope.thread,
         type: "RUN"
      });
      clearEditorHighlights(); 
      socket.send("STEP:" + message);
   }
}

function stopScript() {
   socket.send("STOP");
}

function browseScriptVariables(variables) {
   var threadScope = focusedThread();
   if(threadScope != null) {
      var message = JSON.stringify({
         thread: threadScope.thread,
         expand: variables
      });
      socket.send("BROWSE:" + message);
   }
}

function browseScriptEvaluation(variables, expression, refresh) {
   var threadScope = focusedThread();
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

function attachProcess(process) {
   var statusFocus = currentStatusFocus(); // what is the current focus
   var editorData = loadEditor();
   var message = JSON.stringify({
      process: process,
      breakpoints : editorData.breakpoints,
      project : document.title,
      focus: statusFocus != process // toggle the focus
   });
   socket.send("ATTACH:" + message); // attach to process
}

function switchLayout() {
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

function switchProject() {
   document.location="/";
}

ModuleSystem.registerModule("commands", "Commands module: commands.js", null, [ "common", "editor", "tree", "threads" ]);
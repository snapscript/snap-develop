
module Command {
   
   export function searchTypes() {
      DialogBuilder.createListDialog(function(text, ignoreMe, onComplete){
         findTypesMatching(text, function(typesFound) {
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
            onComplete(typeRows);
         });
     }, null, "Search Types");
   }
   
   function findTypesMatching(text, onComplete) {
      if(text) {
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
               var response = [];
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
               onComplete(response);
            },
            async: true
         });
      } else {
         onComplete([]);
      }
   }
   
   export function replaceTokenInFiles(matchText, searchCriteria, filePatterns) {
      findFilesWithText(matchText, filePatterns, searchCriteria, function(filesReplaced){
         var editorData = FileEditor.loadEditor();
         
         for(var i = 0; i < filesReplaced.length; i++) {
            var fileReplaced = filesReplaced[i];
            var fileReplacedResource = FileTree.createResourcePath("/resource/" + fileReplaced.project + "/" + fileReplaced.resource);
            
            if(editorData.resource.resourcePath == fileReplacedResource.resourcePath) {
               FileExplorer.openTreeFile(fileReplacedResource.resourcePath, function() {
                  //FileEditor.showEditorLine(record.line);  
               }); 
            }
         }
      });
   }
   
   export function searchFiles(filePatterns) {
      searchOrReplaceFiles(false, filePatterns);
   }
   
   export function searchAndReplaceFiles(filePatterns) {
      searchOrReplaceFiles(true, filePatterns);
   }
   
   function searchOrReplaceFiles(enableReplace, filePatterns) {
      if(!filePatterns) {
         filePatterns = '*.snap,*.properties,*.xml,*.txt,*.json';
      } 
      var searchFunction = DialogBuilder.createTextSearchOnlyDialog;
      
      if(enableReplace) {
         searchFunction = DialogBuilder.createTextSearchAndReplaceDialog;
      }
      searchFunction(function(text, fileTypes, searchCriteria, onComplete){
         findFilesWithText(text, fileTypes, searchCriteria, function(filesFound) { // don't replace in the search phase
            var fileRows = [];
           
            for(var i = 0; i < filesFound.length; i++) {
               var fileFound = filesFound[i];
               var debugToggle = ";debug";
               var locationPath = window.document.location.pathname;
               var locationHash = window.document.location.hash;
               var debug = locationPath.indexOf(debugToggle, locationPath.length - debugToggle.length) !== -1;
               var resourceLink = "/resource/" + fileFound.project + "/" + fileFound.resource;
               
               var resourceCell = {
                  text: fileFound.resource,
                  line: fileFound.line,
                  resource: resourceLink,
                  style: 'resourceNode'
               };
   //            var lineCell = {
   //               text: "&nbsp;line&nbsp;" + filesFound[i].line + "&nbsp;",
   //               link: resourceLink,
   //               style: ''
   //            };
               var textCell = {
                  text: fileFound.text,
                  line: fileFound.line,
                  resource: resourceLink,
                  style: 'textNode'
               };
               fileRows.push([resourceCell, /*lineCell, */textCell]);
            }
            return onComplete(fileRows);
         });
     }, filePatterns, "Search Files");
   }
   
   function findFilesWithText(text, fileTypes, searchCriteria, onComplete) {
      if(text && text.length > 1) {
         var searchUrl = '';
         
         searchUrl += '/find/' + document.title;
         searchUrl += '?expression=' + encodeURIComponent(text);
         searchUrl += '&pattern=' + encodeURIComponent(fileTypes);
         searchUrl += "&caseSensitive=" + encodeURIComponent(searchCriteria.caseSensitive);
         searchUrl += "&regularExpression=" + encodeURIComponent(searchCriteria.regularExpression);
         searchUrl += "&wholeWord=" + encodeURIComponent(searchCriteria.wholeWord);
         searchUrl += "&replace=" + encodeURIComponent(searchCriteria.replace);
         searchUrl += "&enableReplace=" + encodeURIComponent(searchCriteria.enableReplace);
         
         jQuery.ajax({
            url: searchUrl,
            success: function (filesMatched) {
               var response = [];
               
               for(var i = 0; i < filesMatched.length; i++) {
                  var fileMatch = filesMatched[i];
                  var typeEntry = {
                        resource: fileMatch.resource,
                        line: fileMatch.line,
                        project: document.title
                  };
                  response.push(fileMatch);   
               }
               onComplete(response);
            },
            async: true
         });
      }else {
         onComplete([]);
      }
   }
   
   export function findFileNames() {
      DialogBuilder.createListDialog(function(text, ignoreMe, onComplete){
         findFilesByName(text, function(filesFound) {
            var fileRows = [];
           
            for(var i = 0; i < filesFound.length; i++) {
               var fileFound = filesFound[i];
               var debugToggle = ";debug";
               var locationPath = window.document.location.pathname;
               var locationHash = window.document.location.hash;
               var debug = locationPath.indexOf(debugToggle, locationPath.length - debugToggle.length) !== -1;
               var resourceLink = "/project/" + fileFound.project;
               
               if (debug) {
                   resourceLink += debugToggle;
               }
               resourceLink += "#" + fileFound.resource;
               
               var resourceCell = {
                  text: fileFound.text,
                  link: resourceLink,
                  style: 'resourceNode'
               };
               fileRows.push([resourceCell]);
            }
            return onComplete(fileRows);
         });
     }, null, "Find Files");
   }
   
   function findFilesByName(text, onComplete) {
      if(text && text.length > 1) {
         jQuery.ajax({
            url: '/file/' + document.title + '?expression=' + text,
            success: function (filesMatched) {
               var response = [];
               
               for(var i = 0; i < filesMatched.length; i++) {
                  var fileMatch = filesMatched[i];
                  var typeEntry = {
                        resource: fileMatch.resource,
                        project: document.title
                  };
                  response.push(fileMatch);
               }
               onComplete(response);
            },
            async: true
         });
      } else {
         onComplete([]);
      }
   }
   
   export function exploreDirectory(resourcePath) {
      if(FileTree.isResourceFolder(resourcePath.filePath)) {
         var message = {
            project : document.title,
            resource : resourcePath.filePath,
         };
         EventBus.sendEvent("EXPLORE", message);
      }
   }
   
   export function folderExpand(resourcePath) {
      var message = {
         project: document.title,
         folder : resourcePath
      };
      EventBus.sendEvent("FOLDER_EXPAND", message);
   }
   
   export function folderCollapse(resourcePath) {
      var message = {
         project: document.title,
         folder : resourcePath
      };
      EventBus.sendEvent("FOLDER_COLLAPSE", message);
   }
   
   export function pingProcess() {
      if(EventBus.isSocketOpen()) {
         EventBus.sendEvent("PING", document.title);
      }
   }
   
   export function renameFile(resourcePath) {
      var originalFile = resourcePath.filePath;
      
      DialogBuilder.renameFileTreeDialog(resourcePath, true, function(resourceDetails) {
         var message = {
            project : document.title,
            from : originalFile,
            to: resourceDetails.filePath
         };
         EventBus.sendEvent("RENAME", message);
         Project.renameEditorTab(resourcePath.resourcePath, resourceDetails.resourcePath); // rename tabs if open
      });
   }
   
   export function renameDirectory(resourcePath) {
      var originalPath = resourcePath.filePath;
      var directoryPath = FileTree.createResourcePath(originalPath + ".#"); // put a # in to trick in to thinking its a file
      
      DialogBuilder.renameDirectoryTreeDialog(directoryPath, true, function(resourceDetails) {
         var message = {
            project : document.title,
            from : originalPath,
            to: resourceDetails.filePath
         };
         EventBus.sendEvent("RENAME", message);
      });
   }
   
   export function newFile(resourcePath) {
      DialogBuilder.newFileTreeDialog(resourcePath, true, function(resourceDetails) {
         if(!FileTree.isResourceFolder(resourceDetails.filePath)) {
            var message = {
               project : document.title,
               resource : resourceDetails.filePath,
               source : "",
               directory: false,
               create: true
            };
            ProcessConsole.clearConsole();
            EventBus.sendEvent("SAVE", message);
            FileEditor.updateEditor("", resourceDetails.projectPath);
         }
      });
   }
   
   export function newDirectory(resourcePath) {
      DialogBuilder.newDirectoryTreeDialog(resourcePath, true, function(resourceDetails) {
         if(FileTree.isResourceFolder(resourceDetails.filePath)) {
            var message = {
               project : document.title,
               resource : resourceDetails.filePath,
               source : "",
               directory: true,
               create: true
            };
            ProcessConsole.clearConsole();
            EventBus.sendEvent("SAVE", message);
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
         var message = {
            project : document.title,
            resource : editorPath.filePath,
            source : editorData.source,
            directory: false,
            create: false
         };
         ProcessConsole.clearConsole();
         EventBus.sendEvent("SAVE", message);
         
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
                  var message = {
                     project : document.title,
                     resource : resourceDetails.filePath
                  };
                  ProcessConsole.clearConsole();
                  EventBus.sendEvent("DELETE", message);
                  
                  if(editorData.resource != null && editorData.resource.resourcePath == resourceDetails.resourcePath) { // delete focused file
                     FileEditor.resetEditor();
                  }
                  Project.deleteEditorTab(resourceDetails.resourcePath); // rename tabs if open
               },
               function(){});
      }
   } 
   
   export function deleteDirectory(resourceDetails) {
      if(resourceDetails != null) {
         var message = {
            project : document.title,
            resource : resourceDetails.filePath
         };
         ProcessConsole.clearConsole();
         EventBus.sendEvent("DELETE", message);
      }
   }
   
   export function runScript() {
      saveFileWithAction(function() {
         var editorData = FileEditor.loadEditor();
         var message = {
            breakpoints : editorData.breakpoints,
            project : document.title,
            resource : editorData.resource.filePath,
            source : editorData.source
         };
         EventBus.sendEvent("EXECUTE", message);
      }, true); // save editor
   }
   
   export function updateScriptBreakpoints() {
      var editorData = FileEditor.loadEditor();
      var message = {
         breakpoints : editorData.breakpoints,
         project : document.title,
      };
      EventBus.sendEvent("BREAKPOINTS", message);
   }
   
   export function stepOverScript() {
      var threadScope = ThreadManager.focusedThread();
      if(threadScope != null) {
         var message = {
            thread: threadScope.thread,
            type: "STEP_OVER"
         };
         FileEditor.clearEditorHighlights();
         EventBus.sendEvent("STEP", message);
      }
   }
   
   export function stepInScript() {
      var threadScope = ThreadManager.focusedThread();
      if(threadScope != null) {
         var message = {
            thread: threadScope.thread,
            type: "STEP_IN"
         };
         FileEditor.clearEditorHighlights();
         EventBus.sendEvent("STEP", message);
      }
   }
   
   export function stepOutScript() {
      var threadScope = ThreadManager.focusedThread();
      if(threadScope != null) {
         var message = {
            thread: threadScope.thread,
            type: "STEP_OUT"
         };
         FileEditor.clearEditorHighlights(); 
         EventBus.sendEvent("STEP", message);
      }
   }
   
   export function resumeScript() {
      var threadScope = ThreadManager.focusedThread();
      if(threadScope != null) {
         var message = {
            thread: threadScope.thread,
            type: "RUN"
         };
         FileEditor.clearEditorHighlights(); 
         EventBus.sendEvent("STEP", message);
      }
   }
   
   export function stopScript() {
      EventBus.sendEvent("STOP");
   }
   
   export function browseScriptVariables(variables) {
      var threadScope = ThreadManager.focusedThread();
      if(threadScope != null) {
         var message = {
            thread: threadScope.thread,
            expand: variables
         };
         EventBus.sendEvent("BROWSE", message);
      }
   }
   
   export function browseScriptEvaluation(variables, expression, refresh) {
      var threadScope = ThreadManager.focusedThread();
      if (threadScope != null) {
          var message = {
              thread: threadScope.thread,
              expression: expression,
              expand: variables,
              refresh: refresh
          };
          EventBus.sendEvent("EVALUATE", message);
      }
   }
   
   export function attachProcess(process) {
      var statusFocus = DebugManager.currentStatusFocus(); // what is the current focus
      var editorData = FileEditor.loadEditor();
      var message = {
         process: process,
         breakpoints : editorData.breakpoints,
         project : document.title,
         focus: statusFocus != process // toggle the focus
      };
      EventBus.sendEvent("ATTACH", message); // attach to process
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
   
   export function updateDisplay(displayInfo) {
      if(EventBus.isSocketOpen()) {
         EventBus.sendEvent("DISPLAY_UPDATE", displayInfo); // update and save display
      }
   }
   
   export function evaluateExpression() {
      var threadScope = ThreadManager.focusedThread();
      if (threadScope != null) {
         var selectedText = FileEditor.getSelectedText();
         DialogBuilder.evaluateExpressionDialog(selectedText);
      }
   }
   
   export function refreshScreen() {
      setTimeout(function() {
         location.reload();
      }, 10);
   }
   
   export function switchProject() {
      document.location="/";
   }
}

ModuleSystem.registerModule("commands", "Commands module: commands.js", null, null, [ "common", "editor", "tree", "threads" ]);
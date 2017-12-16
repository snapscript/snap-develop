import * as $ from "jquery"
import {w2ui} from "w2ui"
import {FileTree} from "tree"
import {FileEditor} from "editor"
import {FileExplorer} from "explorer"

export module History {

   export function trackHistory() {
      $(window).on('hashchange', function() {
         updateEditorFromHistory();
      });
      updateEditorFromHistory(200);
   } 
   
   export function showFileHistory() {
      var editorData = FileEditor.loadEditor();
      var resource = editorData.resource.projectPath;
      $.ajax({
         url: '/history/' + document.title + '/' + resource,
         success: function (currentRecords) {
            var historyRecords = [];
            var historyIndex = 1;
            
            for (var i = 0; i < currentRecords.length; i++) {
               var currentRecord = currentRecords[i];
               var recordResource = FileTree.createResourcePath(currentRecord.path);
               
               historyRecords.push({ 
                  recid: historyIndex++,
                  resource: "<div class='historyPath'>" + recordResource.filePath + "</div>", // /blah/file.snap 
                  date: currentRecord.date,
                  time: currentRecord.timeStamp,
                  script: recordResource.resourcePath // /resource/<project>/blah/file.snap
               });
            }
            w2ui['history'].records = historyRecords;
            w2ui['history'].refresh();
         },
         async: true
      });
   }
   
   export function navigateForward() {
      window.history.forward();
   }
   
   export function navigateBackward() {
      var location = window.location.hash;
      var hashIndex = location.indexOf('#'); // if we are currently on a file
      
      if(hashIndex != -1) {
         window.history.back();
      }
   }
   
   function updateEditorFromHistory(){
      var location = window.location.hash;
      var hashIndex = location.indexOf('#');
      
      if(hashIndex != -1) {
         var resource = location.substring(hashIndex + 1);
         var resourceData = FileTree.createResourcePath(resource);
         var editorData = FileEditor.loadEditor();
         var editorResource = editorData.resource;
         
         if(editorResource == null || editorResource.resourcePath != resourceData.resourcePath) { // only if changed
            FileExplorer.openTreeFile(resourceData.resourcePath, function() {});
         }
      }
   }
}

//ModuleSystem.registerModule("history", "History module: history.js", null, History.trackHistory, [ "common", "editor" ]);
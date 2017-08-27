import * as $ from "jquery"
import * as w2ui from "w2ui"
import {ace} from "ace"
import {saveAs} from "filesaver"
import {Common} from "common"
import {EventBus} from "socket"
import {FileTree} from "tree"
import {FileEditor} from "editor"
import {Command} from "commands"
import {Alerts} from "alert"
 
export module FileExplorer {
   
   var treeVisible = false;
   
   export function showTree() {
      if (treeVisible == false) {
         window.setTimeout(reloadTree, 500);
         treeVisible = true;
      }
      EventBus.createRoute("RELOAD_TREE", reloadTree);
   
   }
   
   function reloadTree(socket, type, text) {
      FileTree.createTree("/" + document.title, "explorer", "explorerTree", "/.", false, handleTreeMenu, function(event, data) {
         if (!data.node.isFolder()) {
            openTreeFile(data.node.tooltip, function(){});
         }
      });
   }
   
   export function openTreeFile(resourcePath, afterLoad) {
      var filePath = resourcePath.toLowerCase();
      
      if(Common.stringEndsWith(filePath, ".json") || Common.stringEndsWith(filePath, ".js")) { // is it json or javascript
         $.get(resourcePath, function(response) {
            handleOpenTreeFile(resourcePath, afterLoad, response);
         }, "text").fail(function() {
            handleOpenTreeFile(resourcePath, afterLoad, "// Could not find " + filePath);
         });
      } else {
         $.get(resourcePath, function(response) {
            handleOpenTreeFile(resourcePath, afterLoad, response);
         }).fail(function() {
            handleOpenTreeFile(resourcePath, afterLoad, "// Could not find " + filePath);
         });
      }
   }
   
   export function openTreeHistoryFile(resourcePath, timeStamp, afterLoad) {
      var filePath = resourcePath.toLowerCase();
      var backupResourcePath = resourcePath.replace(/^\/resource/i, "/history");
      //var backupUrl = backupResourcePath + "?time=" + timeStamp;
      
      if(Common.stringEndsWith(filePath, ".json") || Common.stringEndsWith(filePath, ".js")) { // is it json or javascript
         $.ajax({
            url: backupResourcePath + "?time=" + timeStamp,
            type: "get",
            success: function (response) {
               handleOpenTreeFile(resourcePath, afterLoad, response);
            },
            error: function (response) {
               handleOpenTreeFile(resourcePath, afterLoad, "// Could not find " + filePath);
            },
            async: false
         });
      } else {
         $.ajax({
            url: backupResourcePath + "?time=" + timeStamp,
            type: "get",            
            success: function (response) {
               handleOpenTreeFile(resourcePath, afterLoad, response);
            },
            error: function (response) {
               handleOpenTreeFile(resourcePath, afterLoad, "// Could not find " + filePath);
            },
            async: false
         });
      }
   }
   
   function handleOpenTreeFile(resourcePath, afterLoad, response) {
      var mode = FileEditor.resolveEditorMode(resourcePath);
      
      if(mode == null) {
         var resourceBlob = new Blob([response], {type: "application/octet-stream"});
         var resourceFile = resourcePath.replace(/.*\//, "");
         
         saveAs(resourceBlob, resourceFile);
      } else {
         if(FileEditor.isEditorChanged()) {
            var editorData = FileEditor.loadEditor();
            var editorResource = editorData.resource;
            var message = "Save resource " + editorResource.filePath;
            
            Alerts.createConfirmAlert("File Changed", message, "Save", "Ignore", 
                  function(){
                     Command.saveEditor(true); // save the file
                  },
                  function(){
                     FileEditor.updateEditor(response, resourcePath);
                  });
         } else {
            FileEditor.updateEditor(response, resourcePath);
         }
      }
      afterLoad();
   }
   
   function handleTreeMenu(resourcePath, commandName, elementId, isDirectory) {
      if(commandName == "runScript") {
         openTreeFile(resourcePath.resourcePath, function(){
            Command.runScript();
         });
      } else if(commandName == "debugScript") {
         openTreeFile(resourcePath.resourcePath, function(){
            Command.debugScript();
         });
      }else if(commandName == "newFile") {
         Command.newFile(resourcePath);
      }else if(commandName == "newDirectory") {
         Command.newDirectory(resourcePath);
      }else if(commandName == "exploreDirectory") {
         Command.exploreDirectory(resourcePath);
      }else if(commandName == "renameFile") {
         if(isDirectory) {
            Command.renameDirectory(resourcePath);
         } else {
            Command.renameFile(resourcePath);
         }
      }else if(commandName == "saveFile") {
         openTreeFile(resourcePath.resourcePath, function(){
            Command.saveFile();
         });
      }else if(commandName == "deleteFile") {
         if(FileTree.isResourceFolder(resourcePath.resourcePath)) {
            Command.deleteDirectory(resourcePath);
         } else {
            Command.deleteFile(resourcePath);
         }
      }
   }
}
//ModuleSystem.registerModule("explorer", "Explorer module: explorer.js", null, FileExplorer.showTree, [ "common", "spinner", "tree", "commands" ]);
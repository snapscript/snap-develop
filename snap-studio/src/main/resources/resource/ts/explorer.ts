import * as $ from "jquery"
import * as w2ui from "w2ui"
import {ace} from "ace"
//import {saveAs} from "filesaver"
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
      
      if(isTextFile(filePath)) { // is it json or javascript
         //var type = header.getResponseHeader("content-type");
         
         $.ajax({
            url: resourcePath, 
            type: "get",
            dataType: 'text',
            success: function(response, status, header) {
               var responseObject = parseResponseMessage(resourcePath, resourcePath, header, response);
               handleOpenTreeFile(responseObject, afterLoad);
            },
            error: function(response) {
               var responseObject = parseResponseMessage(resourcePath, resourcePath, null, response);
               handleOpenTreeFile(responseObject, afterLoad);
            },
            async: false
         });
      } else {
         $.ajax({
            url: resourcePath,
            type: "get",
            success: function(response, status, header) {
               var responseObject = parseResponseMessage(resourcePath, resourcePath, header, response);
               handleOpenTreeFile(responseObject, afterLoad);
            },
            error: function(response) {
               var responseObject = parseResponseMessage(resourcePath, resourcePath, null, response);
               handleOpenTreeFile(responseObject, afterLoad);
            },
            async: false
         });
      }
   }
   
   export function openTreeHistoryFile(resourcePath, timeStamp, afterLoad) {
      var filePath = resourcePath.toLowerCase();
      var backupResourcePath = resourcePath.replace(/^\/resource/i, "/history");
      //var backupUrl = backupResourcePath + "?time=" + timeStamp;
      
      if(isTextFile(filePath)) { // is it json or javascript
         var downloadURL = backupResourcePath + "?time=" + timeStamp;
         $.ajax({
            url: downloadURL,
            type: "get",
            dataType: 'text',
            success: function (response, status, header) {
               var responseObject = parseResponseMessage(resourcePath, downloadURL, header, response);
               handleOpenTreeFile(responseObject, afterLoad);
            },
            error: function (response) {
               var responseObject = parseResponseMessage(resourcePath, downloadURL, null, response);
               handleOpenTreeFile(responseObject, afterLoad);
            },
            async: false
         });
      } else {
         var downloadURL = backupResourcePath + "?time=" + timeStamp;
         $.ajax({
            url: downloadURL,
            type: "get",            
            success: function (response, status, header) {
               var responseObject = parseResponseMessage(resourcePath, downloadURL, header, response);
               handleOpenTreeFile(responseObject, afterLoad);
            },
            error: function (response) {
               var responseObject = parseResponseMessage(resourcePath, downloadURL, null, response);
               handleOpenTreeFile(responseObject, afterLoad);
            },
            async: false
         });
      }
   }
   
   function parseResponseMessage(resourcePath, downloadURL, responseHeader, responseEntity) {
      var lastModified = new Date().getTime();
      var contentType = "application/octet-stream";
      
      if(responseHeader && responseEntity) {
         var contentTypeHeader = responseHeader.getResponseHeader("content-type");
         var lastModifiedHeader = responseHeader.getResponseHeader("last-modified");
         
         if(lastModifiedHeader) {
            lastModified = new Date(lastModifiedHeader).getTime();
         }
         if(contentTypeHeader) {
            contentType = contentTypeHeader;
         }
         return {
            resourcePath: resourcePath,
            contentType: contentType,
            lastModified: lastModified,
            responseEntity: responseEntity,
            downloadURL: downloadURL
         };
      }
      return {
         resourcePath: resourcePath,
         contentType: "text/plain",
         lastModified: lastModified,
         responseEntity: "// Count not find " + path,
         downloadURL: downloadURL
      };
   }
   
   function handleOpenTreeFile(responseObject, afterLoad) {
      //console.log(responseObject);
      
      if(isImageFileType(responseObject.contentType)) {
         handleOpenFileInNewTab(responseObject.downloadURL);
      } else if(isBinaryFileType(responseObject.contentType)) {
         handleDownloadFile(responseObject.downloadURL);
      } else {
         var mode = FileEditor.resolveEditorMode(responseObject.resourcePath);
         
//         if(FileEditor.isEditorChanged()) {
//            var editorData = FileEditor.loadEditor();
//            var editorResource = editorData.resource;
//            var message = "Save resource " + editorResource.filePath;
//            
//            Alerts.createConfirmAlert("File Changed", message, "Save", "Ignore", 
//                  function(){
//                     Command.saveEditor(true); // save the file
//                  },
//                  function(){
//                     FileEditor.updateEditor(response, resourcePath);
//                  });
//         } else {
//            FileEditor.updateEditor(response, resourcePath);
//         }
         FileEditor.updateEditor(responseObject.responseEntity, responseObject.resourcePath, responseObject.lastModified);
         console.log("OPEN: " + responseObject.resourcePath)
      }
      afterLoad();
   }
   
   function handleOpenFileInNewTab(downloadURL) {
      var newTab = window.open(downloadURL, '_blank');
      newTab.focus();
    }
   
   function handleDownloadFile(downloadURL) {
      window.location.href = downloadURL;
    }
   
   function isTextFile(filePath) {
      return Common.stringEndsWith(filePath, ".json") || 
              Common.stringEndsWith(filePath, ".js") || 
              Common.stringEndsWith(filePath, ".xml") ||
              Common.stringEndsWith(filePath, ".project") ||
              Common.stringEndsWith(filePath, ".classpath") ||
              Common.stringEndsWith(filePath, ".index");
   }
   
   function isImageFileType(contentType) {
      if(contentType) {
         if(Common.stringStartsWith(contentType, "image")) {
            return true;
         }
      }
      return false;
   }
   
   function isBinaryFileType(contentType) {
      if(contentType) {
         if(contentType == "application/json") {
            return false;
         }
         if(contentType == "application/x-javascript") {
            return false;
         }
         if(Common.stringStartsWith(contentType, "application")) {
            return true;
         }
         if(Common.stringStartsWith(contentType, "image")) {
            return true;
         }
         if(Common.stringStartsWith(contentType, "text")) {
            return false;
         }
         return true; // unknown
      }
      return false;
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
      }else if(commandName == "openTerminal") {
         Command.openTerminal(resourcePath);
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
import * as $ from "jquery"
import * as fancytree from "fancytree"
import {Common} from "common"
import {Command} from "commands"

export class FilePath {
   resourcePath: string; // /resource/<project>/blah/script.snap
   projectPath: string; // /blah/script.snap
   projectDirectory: string; // /blah
   filePath: string; // /blah/script.snap
   fileName: string; // script.snap
   fileDirectory: string; // /blah
   originalPath: string;
   
   constructor(resourcePath: string, projectPath: string, projectDirectory: string, filePath: string, fileName: string, fileDirectory: string, originalPath: string) {
      this.resourcePath = resourcePath;
      this.projectPath = projectPath;
      this.projectDirectory = projectDirectory;
      this.filePath = filePath;
      this.fileName = fileName;
      this.fileDirectory = fileDirectory;
      this.originalPath = originalPath;
   }
}

export module FileTree {
   
   export function createTree(treePath, element, id, expandPath, foldersOnly, treeMenuHandler, clickCallback) { // #explorer
      createTreeOfDepth(treePath, element, id, expandPath, foldersOnly, treeMenuHandler, clickCallback, 10000); // large random depth
   }
   
   export function createTreeOfDepth(treePath, element, id, expandPath, foldersOnly, treeMenuHandler, clickCallback, depth) { // #explorer
      $(document).ready(function() {
         var project = document.title;
         var requestPath = '/tree' + treePath + "?id=" + id + "&folders=" + foldersOnly + "&depth=" + depth;
         
         if(expandPath != null) {
            requestPath += "&expand="+expandPath;
         }
         $.ajax({
            url: requestPath,
            success: function (response) {
               $('#' + element).html(response);
               showFancyTree(id, !foldersOnly, treeMenuHandler, clickCallback); // show the fancy tree
            },
            async: true
         });

      });
   }
   
   export function showTreeNode(id, treePath) {
      if(id && treePath) {
         if(treePath.resourcePath) {
            showNodeAndScroll(id, treePath.resourcePath);
            showNodeAndScroll(id, treePath.resourcePath); // do it twice
         }
      }
   }
   
   function showNodeAndScroll(treeId, nodeId) {
      var container = document.getElementById("browseParent");
      var tree = $("#" + treeId).fancytree("getTree");
      
      if(tree && (typeof tree.getNodeByKey === "function")) { // make sure the function exists
         var treeNode = tree.getNodeByKey(nodeId);
   
         if(treeNode) {
            if(treeNode.li && container) {
               if(!Common.isChildElementVisible(container, treeNode.li)) {
                  container.scrollTop = 0; // reset the scroll for better calculation
                  container.scrollTop = Common.calculateScrollOffset(container, treeNode.li);
               }
            }
            treeNode.setActive();
         }
      }
   }
   
   function showFancyTree(id, dragAndDrop, treeMenuHandler, clickCallback) {
       // using default options
       // https://github.com/mar10/fancytree/blob/master/demo/sample-events.html
       $('#' + id).fancytree({
         //autoScroll: true,
         //extensions: dragAndDrop ? ["dnd"] : [],
         click : clickCallback,
         expand: function(event, data) {
            if(typeof Command !== 'undefined') {
               Command.folderExpand(data.node.key);
               setTimeout(function() {
                  addTreeMenuHandler(id, treeMenuHandler);
                  addDragAndDropHandlers(id);
               }, 10);
            }
         },
         collapse: function(event, data) {
            if(typeof Command !== 'undefined') {
               Command.folderCollapse(data.node.key);
               setTimeout(function() {
                  addTreeMenuHandler(id, treeMenuHandler);
                  addDragAndDropHandlers(id);
               }, 10);
            }
         },
         init: function(event, data, flag) {
            addTreeMenuHandler(id, treeMenuHandler);
            addDragAndDropHandlers(id);
         }  
      });

   }
   
   function addDragAndDropHandlers(id) {
      var explorerTree = document.getElementById(id);
      var folders = Common.getElementsByClassName(explorerTree, 'fancytree-folder');
      
      for(var i = 0; i < folders.length; i++) {
         let child = folders[i];
         
         $(child).on("dragenter", function(event) {
            $(child).find('.fancytree-title').addClass("treeFolderDragOver");
         }).on("dragleave", function(event) {
            $(child).find('.fancytree-title').removeClass("treeFolderDragOver");
         }).on("drop", function (event) {
            var folderElement = $(child).find('.fancytree-title');
            var dataTransfer = event.target.dataTransfer || event.originalEvent.dataTransfer;
            var target = event.target || event.currentTarger;
            var fromPath = dataTransfer.getData("resource");
            var folderPath = $(folderElement).attr("title");
            
            $(folderElement).removeClass("treeFolderDragOver");
            event.stopPropagation();
            event.preventDefault();
            
            if(fromPath) {
               var toPath = {
                     resource: folderPath,
                     folder: isTreeNodeFolder(target)
               }
               handleNodeDroppedOverFolder(event, JSON.parse(fromPath), toPath);
            }else {
               handleFileDroppedOverFolder(event, folderPath);
            }
        }).on('dragover',function(event){
            event.preventDefault();
        });
        updateNodesAsDraggable(explorerTree);
      }  
   }
   
   function updateNodesAsDraggable(nodeElement) {
      var childNodes = Common.getElementsByClassName(nodeElement, 'fancytree-node');
      
      for(var i = 0; i < childNodes.length; i++) {
         let childNode = childNodes[i];
         
         if(childNode){
            childNode.setAttribute("draggable", "true");
            $(childNode).on('dragstart',function(event){
               var dataTransfer = event.target.dataTransfer || event.originalEvent.dataTransfer
               var target = event.target || event.currentTarger;
               var titleNodes = Common.getElementsByClassName(childNode, 'fancytree-title');
               
               if(titleNodes && titleNodes.length > 0) {
                  var titleNode = titleNodes[0];
                  
                  dataTransfer.setData("resource", JSON.stringify({
                     resource: titleNode.getAttribute("title"),
                     folder: isTreeNodeFolder(target) // this does not work
                  }));
               }
            })
         }
      }  
   }
   
   function addTreeMenuHandler(id, treeMenuHandler) {
      if(treeMenuHandler != null) {
         $("#" + id).contextmenu({
              delegate: "span.fancytree-title",
              menu: [
                  {title: "&nbsp;New", uiIcon: "menu-new", children: [
                     {title: "&nbsp;File", cmd: "newFile", uiIcon: "menu-new"},
                     {title: "&nbsp;Directory", cmd: "newDirectory", uiIcon: "menu-new"}
                     ]},              
                  {title: "&nbsp;Save", cmd: "saveFile", uiIcon: "menu-save"}, 
                  {title: "&nbsp;Rename", cmd: "renameFile", uiIcon: "menu-rename"},                       
                  {title: "&nbsp;Delete", cmd: "deleteFile", uiIcon: "menu-trash", disabled: false },
                  {title: "&nbsp;Run", cmd: "runScript", uiIcon: "menu-run"},
                  {title: "&nbsp;Debug", cmd: "debugScript", uiIcon: "menu-debug"},
                  {title: "&nbsp;Explore", cmd: "exploreDirectory", uiIcon: "menu-explore"},
                  {title: "&nbsp;Terminal", cmd: "openTerminal", uiIcon: "menu-terminal"} //,               
                  //{title: "----"},
                  //{title: "Edit", cmd: "edit", uiIcon: "ui-icon-pencil", disabled: true },
                  //{title: "Delete", cmd: "delete", uiIcon: "ui-icon-trash", disabled: true }
                  ],
              beforeOpen: function(event, ui) {
                var node = $.ui.fancytree.getNode(ui.target);
                node.setActive();
                var $menu = ui.menu,
                $target = ui.target,
                extraData = ui.extraData; // passed when menu was opened by call to open()
  
                ui.menu.zIndex( $(event.target).zIndex() + 2000);
              },
              select: function(event, ui) {
                var node = $.ui.fancytree.getNode(ui.target);
                var resourcePath = createResourcePath(node.tooltip);
                var commandName = ui.cmd;
                var elementId = ui.key;
                
                treeMenuHandler(resourcePath, commandName, elementId, node.isFolder());
              }
         });         
     }
   }

   function handleFileDroppedOverFolder(dropEvent, folderPath) {
      var droppedFiles = dropEvent.target.files || dropEvent.originalEvent.dataTransfer.files || dropEvent.dataTransfer.files;

      if(droppedFiles) {
         // process all File objects
         for (var i = 0; i < droppedFiles.length; i++) {
            var droppedFile = droppedFiles[i];
            
            if(isAdvancedFileUpload()) {
               console.log("file="+droppedFile.name+" folder=" + folderPath);
               
               var reader = new FileReader();
               
               reader.onload = function (event) {
                  var encodedFile = encodeFileArrayBufferAsBase64(event.target.result);
                  Command.uploadFileTo(droppedFile.name, folderPath, encodedFile);
               };
               reader.readAsArrayBuffer(droppedFile);
            } 
         }
      }
   }
   
   function handleNodeDroppedOverFolder(dropEvent, fromPath, toPath) {
      Command.dragAndDropFile(fromPath, toPath);
   }
   
   function isTreeNodeFolder(nodeElement) {
      var folders = $(nodeElement).filter('.fancytree-folder');
      if(folders) {
         return folders.length > 0;
      }
      return false;
   }
   
   function encodeFileArrayBufferAsBase64(fileAsArrayBuffer) {
      var binary = '';
      var bytes = new Uint8Array(fileAsArrayBuffer);
      var length = bytes.byteLength;
      for (var i = 0; i < length; i++) {
          binary += String.fromCharCode(bytes[ i ]);
      }
      return window.btoa(binary);
   }
   
   function isAdvancedFileUpload() {
      var div = document.createElement('div');
      return (('draggable' in div) || ('ondragstart' in div && 'ondrop' in div)) && 'FormData' in window && 'FileReader' in window;
   }
   
   export function isResourceFolder(path) {
      if(!Common.stringEndsWith(path, "/")) {
         var parts = path.split(".");
         
         if(path.length === 1 || (parts[0] === "" && parts.length === 2)) {
             return true;
         }
         var extension = parts.pop();
         var slash = extension.indexOf('/');
         
         return slash >= 0;
      }
      return true;
   }
   
   export function cleanResourcePath(path) {
      if(path != null) {
         var cleanPath = path.replace(/\/+/, "/").replace(/\.#/, ""); // replace // with /
         
         while(cleanPath.indexOf("//") != -1) {
            cleanPath = cleanPath.replace("//", "/"); // remove double slashes like /x/y//z.snap
         }
         if(Common.stringEndsWith(cleanPath, "/")) {
            cleanPath = cleanPath.substring(0,cleanPath.length-1);
         }
         return cleanPath;
      }
      return null;
   }
   
   export function createResourcePath(path: string) { 
      var resourcePathPrefix = "/resource/" + document.title + "/";
      var resourcePathRoot = "/resource/" + document.title;
      
      while(path.indexOf("//") != -1) {
         path = path.replace("//", "/"); // remove double slashes like /x/y//z.snap
      }
      if(path == resourcePathRoot || path == resourcePathPrefix) { // its the root /
         var currentPathDetails = {
            resourcePath: resourcePathPrefix, // /resource/<project>/blah/script.snap
            projectPath: "/", // /blah/script.snap
            projectDirectory: "/", // /blah
            filePath: "/", // /blah/script.snap
            fileName: null, // script.snap
            fileDirectory: "/", // /blah
            originalPath: path
         };
         var currentPathText = JSON.stringify(currentPathDetails);
         //console.log("FileTree.createResourcePath(" + path + "): " + currentPathText);
         return currentPathDetails;
      }
      //console.log("FileTree.createResourcePath(" + path + ")");
      
      if(!path.indexOf("/") == 0) {  // script.snap
         path = "/" + path; // /snap.script
      }
      if(!path.indexOf(resourcePathPrefix) == 0) { // /resource/<project>/(<file-path>)
         path = "/resource/" + document.title + path;
      }
      var isFolder = isResourceFolder(path); // /resource/<project>/blah/
      var pathSegments = path.split("/"); // [0="", 1="resource", 2="<project>", 3="blah", 4="script.snap"]
      var currentResourcePath = "/resource/" + document.title;
      var currentProjectPath = "";
      var currentProjectDirectory = "";   
      var currentFileName = null;
      var currentFilePath = "";
      var currentFileDirectory = "";
      
      for(var i = 3; i < pathSegments.length; i++) { 
         currentResourcePath += "/" + pathSegments[i];
         currentProjectPath += "/" + pathSegments[i];
         currentFilePath += "/" + pathSegments[i];
      }
      if(isFolder) { // /resource/<project>/blah/
         var currentFileName = pathSegments[pathSegments.length - 1];
         
         if(pathSegments.length > 3) {
            for(var i = 3; i < pathSegments.length; i++) { 
               currentProjectDirectory += "/" + pathSegments[i];
               currentFileDirectory += "/" + pathSegments[i];
            }
         } else {
            currentFileDirectory = "/";
         }
      } else { // /resource/<project>/blah/script.snap
         var currentFileName = pathSegments[pathSegments.length - 1];
         
         if(pathSegments.length > 4) {
            for(var i = 3; i < pathSegments.length - 1; i++) { 
               currentProjectDirectory += "/" + pathSegments[i];
               currentFileDirectory += "/" + pathSegments[i];
            }
         } else {
            currentFileDirectory = "/";
         }
      }
      return new FilePath(
         cleanResourcePath(currentResourcePath), // /resource/<project>/blah/script.snap
         cleanResourcePath(currentProjectPath), // /blah/script.snap
         cleanResourcePath(currentProjectDirectory == "" ? "/" : currentProjectDirectory), // /blah
         cleanResourcePath(currentFilePath), // /blah/script.snap
         cleanResourcePath(currentFileName), // script.snap
         cleanResourcePath(currentFileDirectory), // /blah
         path
      };
   }
}

//ModuleSystem.registerModule("tree", "Tree module: tree.js", null, null, [ "common" ]);
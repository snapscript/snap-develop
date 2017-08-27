import * as $ from "jquery"
import * as fancytree from "fancytree"
import {Common} from "common"
import {Command} from "commands"

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
      var dnd = {
         autoExpandMS: 400,
         focusOnClick: true,
         preventVoidMoves: true, // Prevent dropping nodes 'before self', etc.
         preventRecursiveMoves: true, // Prevent dropping nodes on own descendants
         dragStart: function(node, data) {
           /** This function MUST be defined to enable dragging for the tree.
            *  Return false to cancel dragging of node.
            */
           return true;
         },
         dragEnter: function(node, data) {
           /** data.otherNode may be null for non-fancytree droppables.
            *  Return false to disallow dropping on node. In this case
            *  dragOver and dragLeave are not called.
            *  Return 'over', 'before, or 'after' to force a hitMode.
            *  Return ['before', 'after'] to restrict available hitModes.
            *  Any other return value will calc the hitMode from the cursor position.
            */
           // Prevent dropping a parent below another parent (only sort
           // nodes under the same parent)
           /* if(node.parent !== data.otherNode.parent){
             return false;
           }
           // Don't allow dropping *over* a node (would create a child)
           return ["before", "after"];
           */
            if(typeof Command !== 'undefined') {
               return Command.isDragAndDropFilePossible({
                  name: data.otherNode.key,
                  folder: data.otherNode.folder == true
               }, {
                  name: node.key,
                  folder: node.folder == true
               });
            }
            return false;
         },
         dragDrop: function(node, data) {
           /** This function MUST be defined to enable dropping of items on
            *  the tree.
            */
           //data.otherNode.moveTo(node, data.hitMode); 
            
            if(typeof Command !== 'undefined') {
               Command.dragAndDropFile({
                  name: data.otherNode.key,
                  folder: data.otherNode.folder == true
               }, {
                  name: node.key,
                  folder: node.folder == true
               });
            }
         }
       };
       
       // using default options
       $('#' + id).fancytree({
         //autoScroll: true,
         extensions: dragAndDrop ? ["dnd"] : [],
         click : clickCallback,
         expand: function(event, data) {
            if(typeof Command !== 'undefined') {
               Command.folderExpand(data.node.key);
            }
         },
         collapse: function(event, data) {
            if(typeof Command !== 'undefined') {
               Command.folderCollapse(data.node.key);
            }
         },
         dnd: (dragAndDrop ? dnd : null)         
      });
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
                   {title: "&nbsp;Explore", cmd: "exploreDirectory", uiIcon: "menu-explore"} //,              
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
   
   export function createResourcePath(path) { 
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
      var currentPathDetails = {
         resourcePath: cleanResourcePath(currentResourcePath), // /resource/<project>/blah/script.snap
         projectPath: cleanResourcePath(currentProjectPath), // /blah/script.snap
         projectDirectory: cleanResourcePath(currentProjectDirectory == "" ? "/" : currentProjectDirectory), // /blah
         filePath: cleanResourcePath(currentFilePath), // /blah/script.snap
         fileName: cleanResourcePath(currentFileName), // script.snap
         fileDirectory: cleanResourcePath(currentFileDirectory), // /blah
         originalPath: path
      };
      var currentPathText = JSON.stringify(currentPathDetails);
      //console.log("FileTree.createResourcePath(" + path + "): " + currentPathText);
      return currentPathDetails;
   }
}

//ModuleSystem.registerModule("tree", "Tree module: tree.js", null, null, [ "common" ]);
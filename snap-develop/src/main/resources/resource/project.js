
function changeProjectFont(){
   var fontFamily = document.getElementById("fontFamily");
   var fontSize = document.getElementById("fontSize");
   
   if(fontSize != null && fontFamily != null) {
      FileEditor.updateEditorFont(fontFamily.options[fontFamily.selectedIndex].value, fontSize.options[fontSize.selectedIndex].value);
      ProcessConsole.updateConsoleFont(fontFamily.options[fontFamily.selectedIndex].value, fontSize.options[fontSize.selectedIndex].value);
   }
}

function changeEditorTheme(){
   var editorTheme = document.getElementById("editorTheme");
   
   if(editorTheme != null) {
      FileEditor.setEditorTheme("ace/theme/" + editorTheme.options[editorTheme.selectedIndex].value.toLowerCase());
   }
}

function toggleFullScreen() {
   var perspective = determineProjectLayout();

   if (perspective == "debug") {
      var topPanel = w2ui['debugEditorLayout'].get("top");
      var bottomPanel = w2ui['debugEditorLayout'].get("bottom");
      
      if(topPanel.hidden || bottomPanel.hidden) {
         w2ui['debugEditorLayout'].show("top");
         w2ui['debugEditorLayout'].show("bottom"); 
      } else {
         w2ui['debugEditorLayout'].hide("top");
         w2ui['debugEditorLayout'].hide("bottom"); 
      }
   } else {
      var leftPanel = w2ui['exploreMainLayout'].get("left");
      var bottomPanel = w2ui['exploreEditorLayout'].get("bottom");
      
      if(leftPanel.hidden || bottomPanel.hidden) {
         w2ui['exploreMainLayout'].show("left", true);
         w2ui['exploreEditorLayout'].show("bottom");
      } else {
         w2ui['exploreMainLayout'].hide("left", true);
         w2ui['exploreEditorLayout'].hide("bottom");
      }
   }
}

function applyProjectTheme() {
   $.get("/display/"+document.title, function(displayInfo) {
      //var theme = JSON.parse(response);
      if(displayInfo.fontName != null && displayInfo.fontSize != null) {
         var fontFamily = document.getElementById("fontFamily");
         var fontSize = document.getElementById("fontSize");
         var editorTheme = document.getElementById("editorTheme");
         
         if(fontSize != null) {
            fontSize.value = displayInfo.fontSize + "px";
         }
         if(fontFamily != null) {
            fontFamily.value = displayInfo.fontName;
         }   
         if(editorTheme != null && displayInfo.themeName != null) {
            editorTheme.value = displayInfo.themeName;
         }
         if(displayInfo.consoleCapacity != null) {
            ProcessConsole.updateConsoleCapacity(Math.max(displayInfo.consoleCapacity, 5000)); // don't allow stupidly small size
         }
         if(displayInfo.logoImage != null) {
            var toolbarRow =  document.getElementById("toolbarRow"); // this is pretty rubbish, but it works!
            
            toolbarRow.insertCell(0).innerHTML = "<div class='toolbarSeparator'></div>";
            toolbarRow.insertCell(0).innerHTML = "&nbsp;";
            toolbarRow.insertCell(0).innerHTML = "&nbsp;";
            toolbarRow.insertCell(0).innerHTML = "<div><img style='height: 25px; margin-top: -1px;' src='" + displayInfo.logoImage + "'></div>"; // /img/logo_grey_shade.png
         }
      }
      changeProjectFont();// update the fonts
      changeEditorTheme(); // change editor theme
   });
}

function showBrowseTreeContent(containsBrowse) { // hack to render tree
   if(containsBrowse) {
      // move the explorer
      var newParent = document.getElementById('browseParent');
      var oldParent = document.getElementById('browseParentHidden');
   
      if(oldParent != null && newParent != null){
         while (oldParent.childNodes.length > 0) {
             newParent.appendChild(oldParent.childNodes[0]);
         }
      }
   }
}

function hideBrowseTreeContent(containsBrowse) { // hack to render tree
   if(containsBrowse) {
      // move the explorer
      var newParent = document.getElementById('browseParentHidden');
      var oldParent = document.getElementById('browseParent');
   
      if(oldParent != null && newParent != null){
         while (oldParent.childNodes.length > 0) {
             newParent.appendChild(oldParent.childNodes[0]);
         }
      }
   }
}

function showEditorContent(containsEditor) { // hack to render editor
   if(containsEditor) {
      // move the explorer
      var newParent = document.getElementById('editParent');
      var oldParent = document.getElementById('editParentHidden');
   
      if(oldParent != null && newParent != null){
         while (oldParent.childNodes.length > 0) {
             newParent.appendChild(oldParent.childNodes[0]);
         }
      }
      updateEditorTabName();
   }
}

function updateEditorTabName() {
   var editorData = FileEditor.loadEditor();
   var editorFileName = document.getElementById("editFileName");
   
   if(editorFileName != null){
      var editorData = FileEditor.loadEditor();
      
      if(editorData != null && editorData.resource != null) {
         editorFileName.innerHTML = "<span title='" + editorData.resource.resourcePath +"'>&nbsp;" + editorData.resource.fileName + "&nbsp;</span>";
      }
   }
}

function findActiveEditorLayout() {
   var tabs = w2ui['exploreEditorTabLayout'];

   if(tabs == null) {
      return w2ui['debugEditorTabLayout'];
   }
   return tabs;
}

function findActiveEditorTabLayout() {
   var tabs = findActiveEditorLayout();
   
   if(tabs != null) {
      return tabs.panels[0].tabs;
   }
   return null;
}

function deleteEditorTab(resource) {
   var layout = findActiveEditorLayout();
   var tabs = findActiveEditorTabLayout();
   
   if(tabs != null && resource != null) {
      var removeTab = tabs.get(resource);
      
      if(removeTab.closable) {
         tabs.remove(resource); // remove the tab
         
         if(removeTab.active) {
            activateAnyEditorTab(resource); // if it was active then activate another
         }
      }
   }
}

function renameEditorTab(from, to) {
   var layout = findActiveEditorLayout();
   var tabs = findActiveEditorTabLayout();
   var editorData = FileEditor.loadEditor();
   
   if(tabs != null && from != null && to != null) {
      var tabList = tabs.tabs;
      var count = 0;
      
      for(var i = 0; i < tabList.length; i++) {
         var nextTab = tabList[i];
         
         if(nextTab != null && nextTab.id == from) {
            var newTab = JSON.parse(JSON.stringify(nextTab)); // clone the tab
            var toPath = FileTree.createResourcePath(to);
            var fromPath = FileTree.createResourcePath(from);

            tabs.remove(nextTab.id); // remove the tab

            if(nextTab.active) {
               FileExplorer.openTreeFile(toPath.resourcePath, function(){}); // browse style makes no difference here
            } else {
               var fileNameReplace = new RegExp(fromPath.fileName, "g");
               var filePathReplace = new RegExp(fromPath.resourcePath, "g");
               
               newTab.caption = newTab.caption.replace(fileNameReplace, toPath.fileName).replace(filePathReplace, toPath.resourcePath); // rename the tab
               newTab.text = newTab.text.replace(fileNameReplace, toPath.fileName).replace(filePathReplace, toPath.resourcePath); // rename the tab
               newTab.id = toPath.resourcePath;
               tabs.add(newTab);
            }
            break;
         }
      }
   }
}

function createEditorTab() {
   var layout = findActiveEditorLayout();
   var tabs = findActiveEditorTabLayout();
   var editorData = FileEditor.loadEditor();
   
   if(tabs != null && editorData != null && editorData.resource != null) {
      var tabList = tabs.tabs;
      var tabResources = {};
      
      for(var i = 0; i < tabList.length; i++) {
         var nextTab = tabList[i];
         
         if(nextTab != null && nextTab.id != 'editTab') {
            tabResources[nextTab.id] = {
               id : nextTab.id,
               caption : nextTab.caption.replace('id="editFileName"', "").replace("id='editFileName'", ""),
               content : "",
               closable: true,
               active: false
            }
         }
      }
      tabResources[editorData.resource.resourcePath] = { 
         id : editorData.resource.resourcePath,
         caption : "<div class='editTab' id='editFileName'><span title='" + editorData.resource.resourcePath +"'>&nbsp;" + editorData.resource.fileName + "&nbsp;</span></div>",
         content : "<div style='overflow: scroll; font-family: monospace;' id='edit'><div id='editParent'></div></div>",
         closable: true,
         active: true
      };
      var sortedNames = [];
      var sortedTabs = [];
      
      for (var tabResource in tabResources) {
         if (tabResources.hasOwnProperty(tabResource)) {
            sortedNames.push(tabResource); // add a '.' to ensure dot notation sorts e.g x.y.z
         }
      }
      sortedNames.sort();
      
      for(var i = 0; i < sortedNames.length; i++) {
         var tabResource = sortedNames[i];
         var nextTab = tabResources[tabResource];
         
         nextTab.closable = sortedNames.length > 1; // if only one tab make sure it cannot be closed
         sortedTabs[i] = nextTab;
      }
      tabs.tabs = sortedTabs;
      tabs.active = editorData.resource.resourcePath;
      activateTab(editorData.resource.resourcePath, layout.name, false, true, ""); // browse style makes no difference here
   }
}

function activateAnyEditorTab(resourcePathDeleted) {
   var layout = findActiveEditorLayout();
   var tabs = findActiveEditorTabLayout();
   
   if(tabs != null) {
      var tabList = tabs.tabs;

      for(var i = 0; i < tabList.length; i++) {
         var nextTab = tabList[i];
         
         if(nextTab != null && nextTab.id == resourcePathDeleted) {
            nextTab.id = 'editTab'; // make sure not to enable, bit of a hack
            nextTab.closable = true;
         }
      }
      for(var i = 0; i < tabList.length; i++) {
         var nextTab = tabList[i];
         
         if(nextTab != null && nextTab.id != 'editTab') {
            tabs.active = nextTab.id;
            tabs.closable = false;
            FileExplorer.openTreeFile(nextTab.id, function(){}); // browse style makes no difference here
            break;
         }
      }
   }
}

function hideEditorContent(containsEditor) { // hack to render editor
   if(containsEditor) {
      // move the editor
      var newParent = document.getElementById('editParentHidden');
      var oldParent = document.getElementById('editParent');
   
      if(oldParent != null && newParent != null){
         while (oldParent.childNodes.length > 0) {
             newParent.appendChild(oldParent.childNodes[0]);
         }
      }
   }
}

function determineProjectLayout() {
   var debugToggle = ";debug";
   var locationPath = window.document.location.pathname;
   var locationHash = window.document.location.hash;
   var debug = locationPath.indexOf(debugToggle, locationPath.length - debugToggle.length) !== -1;
   
   if(debug) {
      return "debug";
   }
   return "explore";
}

function createMainLayout() {
   var perspective = determineProjectLayout();
   
   if (perspective == "debug") {
      createDebugLayout(); // show debug layout
   } else {
      createExploreLayout();
   }
   startResizePoller(); // dynamically resize the editor
}

function startResizePoller() { // because w2ui onResize not working
   var editorWidth = 0;
   var editorHeight = 0;
   
   setInterval(function() {
      var editorElement = document.getElementById("editor");
      
      if(editorElement != null) {
         var currentWidth = editorElement.offsetWidth;
         var currentHeight = editorElement.offsetHeight;
         
         if(editorWidth != currentWidth || editorHeight != currentHeight) {
            editorWidth = currentWidth;
            editorHeight = currentHeight;
            FileEditor.resizeEditor();
         }
      }
   }, 100);
}

function createExploreLayout() {

   // $('#topLayer').spin({ lines: 10, length: 30, width: 20, radius: 40 });

   // -- LAYOUT
   var pstyle = 'background-color: #F5F6F7; overflow: hidden;';
      
   $('#mainLayout').w2layout({
      name : 'exploreMainLayout',
      padding : 0,
      panels : [ {
         type : 'top',
         size : '40px',
         resizable : false,
         style : pstyle
      }, {
         type : 'left',
         size : '20%',
         resizable : true,
         style : pstyle      
      },{
         type : 'right',
         size : '20%',
         resizable : true,
         hidden: true,
         style : pstyle
      },{
         type : 'main',
         size : '80%',
         resizable : true,
         style : pstyle
      } , {
         type : 'bottom',
         size : '25px',
         resizable : false,
         style : pstyle,
         content : createBottomStatusContent()
      } ]
   });

   $('').w2layout({
      name : 'exploreEditorLayout',
      padding : 0,
      panels : [ {
         type : 'main',
         size : '60%',
         resizable : true,
         overflow: 'auto',
         style : pstyle + 'border-bottom: 0px;'
      }, {
         type : 'bottom',
         size : '40%',
         overflow: 'auto',         
         resizable : true,
         style : pstyle + 'border-top: 0px;'
      } ]
   });
   
   $('').w2layout({
      name : 'exploreEditorTabLayout',
      padding : 0,
      panels : [ {
         type : 'main',
         size : '100%',
         style : pstyle + 'border-top: 0px;',
         resizable : false,
         name : 'editTabs',
         tabs : {
            active : 'editTab',
            tabs : [ {
               id : 'editTab',
               caption : '<div class="editTab" id="editFileName">Edit</div>',
               content : "<div style='overflow: scroll; font-family: monospace;' id='edit'><div id='editParent'></div></div>",
               closable: true 
            } ],
            onClick : function(event) {
               FileExplorer.openTreeFile(event.target, function(){});
            },
            onClose : function(event) {
               activateAnyEditorTab(event.target);
            }
         }
      } ]
   });

   $('').w2layout({
      name : 'exploreLeftTabLayout',
      padding : 0,
      panels : [ {
         type : 'main',
         size : '100%',
         style : pstyle + 'border-top: 0px;',
         resizable : false,
         name : 'tabs',
         tabs : {
            active : 'browseTab',
            tabs : [ {
               id : 'browseTab',
               caption : '<div class="browseTab">Browse</div>',
               content : "<div style='overflow: scroll; font-family: monospace;' id='browse'><div id='browseParent'><div id='explorer'></div></div></div>",
               closable: false 
            } ],
            onClick : function(event) {
               activateTab(event.target, "exploreLeftTabLayout", true, false, "style='right: 0px;'");
            }
         }
      } ]
   });
   
   $('').w2layout({
      name : 'exploreBottomTabLayout',
      padding : 0,
      panels : [ {
         type : 'main',
         size : '100%',
         style : pstyle + 'border-top: 0px;',
         resizable : false,
         name : 'tabs',
         tabs : {
            active : 'consoleTab',
            tabs : [ {
               id : 'consoleTab',
               caption : '<div class="consoleTab">Console</div>',
               closable: false
            }, {
               id : 'problemsTab',
               caption : '<div class="problemsTab">Problems</div>',
               closable: false
            }, {
               id : 'breakpointsTab',
               caption : '<div class="breakpointsTab">Breakpoints</div>',
               closable: false
            }, {
               id : 'threadsTab',
               caption : '<div class="threadTab">Threads</div>',
               closable: false
            }, {
               id : 'variablesTab',
               caption : '<div class="variableTab">Variables</div>',
               closable: false
            }, {
               id : 'profilerTab',
               caption : '<div class="profilerTab">Profiler</div>',
               closable: false
            }, {
               id : 'debugTab',
               caption : '<div class="debugTab">Debug&nbsp;&nbsp;</div>',
               closable: false
            } ],
            onClose: function(event) {
               console.log(event);
            },
            onClick : function(event) {
               activateTab(event.target, "exploreBottomTabLayout", false, false, "style='right: 0px;'");
            }
         }
      } ]
   });

   createTopMenuBar(); // menu bar at top
   createProblemsTab();
   createVariablesTab();
   createProfilerTab();
   createBreakpointsTab();
   createDebugTab();
   createThreadsTab();
   
   w2ui['exploreMainLayout'].content('top', w2ui['topLayout']);
   w2ui['exploreMainLayout'].content('left', w2ui['exploreLeftTabLayout']);
   w2ui['exploreMainLayout'].content('main', w2ui['exploreEditorLayout']);
   w2ui['exploreEditorLayout'].content('main', w2ui['exploreEditorTabLayout']);
   w2ui['exploreEditorLayout'].content('bottom', w2ui['exploreBottomTabLayout']);
   w2ui['exploreEditorTabLayout'].refresh();
   w2ui['exploreBottomTabLayout'].refresh();
   w2ui['exploreLeftTabLayout'].refresh();

   setTimeout(function() {
      applyProjectTheme();
      activateTab("consoleTab", "exploreBottomTabLayout", false, false, "style='right: 0px;'"); 
      activateTab("browseTab", "exploreLeftTabLayout", true, false, "style='right: 0px;'"); 
      activateTab("editTab", "exploreEditorTabLayout", false, true, "style='right: 0px;'"); 
   }, 300); // update theme
}


function createDebugLayout() {

   // $('#topLayer').spin({ lines: 10, length: 30, width: 20, radius: 40 });

   // -- LAYOUT
   var pstyle = 'background-color: #F5F6F7; overflow: hidden;';
   
   $('#mainLayout').w2layout({
      name : 'debugMainLayout',
      padding : 0,
      panels : [ {
         type : 'top',
         size : '40px',
         resizable : false,
         style : pstyle
      }, {
         type : 'main',
         size : '80%',
         resizable : true,
         style : pstyle
      } , {
         type : 'bottom',
         size : '25px',
         resizable : false,
         style : pstyle,
         content : createBottomStatusContent()
      } ]
   });

   $('').w2layout({
      name : 'debugEditorLayout',
      padding : 0,
      panels : [ {
         type : 'top',  
         size : '25%',
         overflow: 'auto',         
         resizable : true,
         style : pstyle + 'border-top: 0px;'
      }, {
         type : 'main',
         size : '50%',
         resizable : true,
         overflow: 'auto',
         style : pstyle + 'border-bottom: 0px;'      
      }, {
         type : 'bottom',
         size : '25%',
         overflow: 'auto',         
         resizable : true,
         style : pstyle + 'border-top: 0px;'
      } ]
   });
   
   $('').w2layout({
      name : 'debugEditorTabLayout',
      padding : 0,
      panels : [ {
         type : 'main',
         size : '100%',
         style : pstyle + 'border-top: 0px;',
         resizable : false,
         name : 'editTabs',
         tabs : {
            active : 'editTab',
            tabs : [ {
               id : 'editTab',
               caption : '<div class="editTab" id="editFileName">Edit</div>',
               content : "<div style='overflow: scroll; font-family: monospace;' id='edit'><div id='editParent'></div></div>",
               closable: true 
            } ],
            onClick : function(event) {
               FileExplorer.openTreeFile(event.target, function(){});
            },
            onClose : function(event) {
               activateAnyEditorTab(event.target);
            }
         }
      } ]
   });
   
   $('').w2layout({
      name : 'debugTopTabSplit',
      padding : 0,
      panels : [ {
         type : 'left',  
         size : '50%',
         overflow: 'auto',         
         resizable : true,
         style : pstyle + 'border-top: 0px;'
      }, {
         type : 'main',
         size : '50%',
         resizable : true,
         overflow: 'auto',
         style : pstyle + 'border-bottom: 0px;'
      } ]
   });

   $('').w2layout({
      name : 'debugLeftTabLayout',
      padding : 0,
      panels : [ {
         type : 'main',
         size : '100%',
         style : pstyle + 'border-top: 0px;',
         resizable : false,
         name : 'tabs',
         tabs : {
            active : 'debugTab',
            tabs : [ {
               id : 'debugTab',
               caption : '<div class="debugTab">Debug&nbsp;&nbsp;</div>',
               closable: false
            }, {
               id : 'threadsTab',
               caption : '<div class="threadTab">Threads</div>',
               closable: false
            },  {
               id : 'browseTab',
               caption : '<div class="browseTab">Browse</div>',
               content : "<div style='overflow: scroll; font-family: monospace;' id='browse'><div id='browseParent'></div></div>",
               closable: false 
            } ],
            onClick : function(event) {
               activateTab(event.target, "debugLeftTabLayout", true, false, "");
            }
         }
      } ]
   });
   
   $('').w2layout({
      name : 'debugRightTabLayout',
      padding : 0,
      panels : [ {
         type : 'main',
         size : '100%',
         style : pstyle + 'border-top: 0px;',
         resizable : false,
         name : 'tabs',
         tabs : {
            active : 'variablesTab',
            tabs : [ {
               id : 'variablesTab',
               caption : '<div class="variableTab">Variables</div>',
               closable: false
            }, {
               id : 'breakpointsTab',
               caption : '<div class="breakpointsTab">Breakpoints</div>',
               closable: false
            } ],
            onClick : function(event) {
               activateTab(event.target, "debugRightTabLayout", false, false, "");
            }
         }
      } ]
   });
   
   $('').w2layout({
      name : 'debugBottomTabLayout',
      padding : 0,
      panels : [ {
         type : 'main',
         size : '100%',
         style : pstyle + 'border-top: 0px;',
         resizable : false,
         name : 'tabs',
         tabs : {
            active : 'consoleTab',
            tabs : [ {
               id : 'consoleTab',
               caption : '<div class="consoleTab">Console</div>'
            }, {
               id : 'problemsTab',
               caption : '<div class="problemsTab">Problems</div>'
            }, {
               id : 'profilerTab',
               caption : '<div class="profilerTab">Profiler</div>'
            } ],
            onClick : function(event) {
               activateTab(event.target, "debugBottomTabLayout", false, false, "");
            }
         }
      } ]
   });
   
   createTopMenuBar(); // menu bar at top
   createProblemsTab();
   createVariablesTab();
   createProfilerTab();
   createBreakpointsTab();
   createDebugTab();
   createThreadsTab();
   
   w2ui['debugMainLayout'].content('top', w2ui['topLayout']);
   w2ui['debugMainLayout'].content('main', w2ui['debugEditorLayout']);
   w2ui['debugEditorLayout'].content('main', w2ui['debugEditorTabLayout']);
   w2ui['debugEditorLayout'].content('top', w2ui['debugTopTabSplit']);
   w2ui['debugTopTabSplit'].content('left', w2ui['debugLeftTabLayout']);
   w2ui['debugTopTabSplit'].content('main', w2ui['debugRightTabLayout']);
   w2ui['debugEditorLayout'].content('bottom', w2ui['debugBottomTabLayout']);  
   w2ui['debugEditorTabLayout'].refresh();
   w2ui['debugTopTabSplit'].refresh();
   w2ui['debugLeftTabLayout'].refresh();
   w2ui['debugRightTabLayout'].refresh();   
   w2ui['debugBottomTabLayout'].refresh();
   
   setTimeout(function() {
      applyProjectTheme();
      activateTab("debugTab", "debugLeftTabLayout", true, false, "");
      activateTab("variablesTab", "debugRightTabLayout", false, false, "");   
      activateTab("consoleTab", "debugBottomTabLayout", false, false, "");  
      activateTab("editTab", "debugEditorTabLayout", false, true, "");  
   }, 300); // update theme
   
   
}

function createBottomStatusContent() {
   return "<div id='status'>"+
      "  <table width='100%' height='100%'>"+
      "  <tr>"+
      "    <td width='50%' align='left'><p id='process'></p></td>"+
      "    <td width='50%' align='right'><p id='currentFile'></p></td>"+
      "  </tr>"+
      "  </table>"+
      "</div>"
}

function createTopMenuBar(){
   var pstyle = 'background-color: #fafafa; overflow: hidden;';
   $('#topLayout').w2layout(
         {
            name : 'topLayout',
            padding : 0,
            panels : [
               {
                  type : 'left',
                  size : '40%',
                  style : pstyle,
                  content : "<div class='toolbarTop'>"
                        + "<table border='0'>"
                        + "<tr id='toolbarRow'>"                         
                        + "   <td>"
                        + "      <table id='toolbarNormal'>"
                        + "      <tr>"
                        + "         <td><div id='newFile' onclick='Command.newFile(null)' title='New File&nbsp;&nbsp;&nbsp;Ctrl+N'></div></td>"                           
                        + "         <td><div id='saveFile' onclick='Command.saveFile(null)' title='Save File&nbsp;&nbsp;&nbsp;Ctrl+S'></div></td>" 
                        + "         <td><div id='deleteFile' onclick='Command.deleteFile(null)' title='Delete File'></div></td>"   
                        + "         <td><div id='searchTypes' onclick='Command.searchTypes()' title='Search Types&nbsp;&nbsp;&nbsp;Ctrl+Shift+S'></div></td>"                             
                        + "         <td><div id='runScript' onclick='Command.runScript()' title='Run Script&nbsp;&nbsp;&nbsp;Ctrl+R'></div></td>" 
                        + "      </tr>"
                        + "      </table>"
                        + "   </td>" 
                        + "   <td><div class='toolbarSeparator'></div></td>"
                        + "   <td>"
                        + "      <table id='toolbarDebug'>"
                        + "      <tr>"
                        + "         <td><div id='stopScript' onclick='Command.stopScript()' title='Stop Script'></div></td>" 
                        + "         <td><div id='resumeScript' onclick='Command.resumeScript()' title='Resume Script'></div></td>" 
                        + "         <td><div id='stepInScript' onclick='Command.stepInScript()' title='Step In'></div></td>" 
                        + "         <td><div id='stepOutScript' onclick='Command.stepOutScript()' title='Step Out'></div></td>" 
                        + "         <td><div id='stepOverScript' onclick='Command.stepOverScript()' title='Step Over'></div></td>" 
                        + "         <td><div id='evaluateExpression' onclick='Command.evaluateExpression()' title='Evaluate Expression'></div></td>"                         
                        + "      </tr>"
                        + "      </table>"
                        + "   </td>"
                        + "</tr>"
                        + "</table>" 
                        + "</div>"
               }, {
                  type : 'main',
                  size : '10%',
                  style : pstyle,
                  content : "<div class='toolbarTop'></div>"
               }, {
                  type : 'right',
                  size : '50%',
                  style : pstyle,
                  content : "<div class='toolbarTop'>"+
                            "<table border='0' width='100%' cellpadding='0'>"+
                            "<tr>"+
                            "   <td  width='100%'></td>"+
                            "   <td>"+
                            "        <select class='styledSelect' id='editorTheme' size='1' onchange='changeEditorTheme()'>\n"+
                            "          <option value='ambiance'>&nbsp;Ambiance</option>\n"+
                            "          <option value='chaos'>&nbsp;Chaos</option>\n"+
                            "          <option value='chrome'>&nbsp;Chrome</option>\n"+
                            "          <option value='clouds_midnight'>&nbsp;Clouds Midnight</option>\n"+
                            "          <option value='clouds'>&nbsp;Clouds</option>\n"+
                            "          <option value='cobalt'>&nbsp;Cobalt</option>\n"+
                            "          <option value='crimson_editor'>&nbsp;Crimson Editor</option>\n"+
                            "          <option value='dawn'>&nbsp;Dawn</option>\n"+
                            "          <option value='textmate' selected='selected'>&nbsp;Default</option>\n"+                               
                            "          <option value='dreamweaver'>&nbsp;Dreamweaver</option>\n"+
                            "          <option value='eclipse'>&nbsp;Eclipse</option>\n"+
                            "          <option value='github'>&nbsp;GitHub</option>\n"+
                            "          <option value='kuroir'>&nbsp;Kuroir</option>\n"+
                            "          <option value='merbivore_soft'>&nbsp;Merbivore Soft</option>\n"+
                            "          <option value='merbivore'>&nbsp;Merbivore</option>\n"+
                            "          <option value='mono_industrial'>&nbsp;Mono Industrial</option>\n"+
                            "          <option value='monokai'>&nbsp;Monokai</option>\n"+
                            "          <option value='solarized_dark'>&nbsp;Solarized Dark</option>\n"+
                            "          <option value='solarized_light'>&nbsp;Solarized Light</option>\n"+
                            "          <option value='sqlserver'>&nbsp;SQL Server</option>\n"+
                            "          <option value='terminal'>&nbsp;Terminal</option>\n"+
                            "          <option value='textmate'>&nbsp;TextMate</option>\n"+
                            "          <option value='twilight'>&nbsp;Twilight</option>\n"+
                            "          <option value='vibrant_ink'>&nbsp;Vibrant Ink</option>\n"+  
                            "          <option value='xcode'>&nbsp;XCode</option>\n"+                            
                            "        </select>\n"+
                            "   </td>"+  
                            "   <td>&nbsp;&nbsp;</td>"+                              
                            "   <td>"+
                            "        <select class='styledSelect' id='fontFamily' size='1' onchange='changeProjectFont()'>\n"+
                            "          <option value='Consolas' selected='selected'>&nbsp;Consolas</option>\n"+
                            "          <option value='Lucida Console'>&nbsp;Lucida Console</option>\n"+
                            "          <option value='Courier New'>&nbsp;Courier New</option>\n"+       
                            "          <option value='Courier'>&nbsp;Courier</option>\n"+    
                            "          <option value='Menlo'>&nbsp;Menlo</option>\n"+                              
                            "          <option value='Monaco'>&nbsp;Monaco</option>\n"+   
                            "        </select>\n"+
                            "   </td>"+  
                            "   <td>&nbsp;&nbsp;</td>"+  
                            "   <td>"+
                            "        <select class='styledSelect' id='fontSize' size='1' onchange='changeProjectFont()'>\n"+
                            "          <option value='10px'>&nbsp;10px</option>\n"+
                            "          <option value='11px'>&nbsp;11px</option>\n"+
                            "          <option value='12px'>&nbsp;12px</option>\n"+
                            "          <option value='13px'>&nbsp;13px</option>\n"+
                            "          <option value='14px' selected='selected'>&nbsp;14px</option>\n"+
                            "          <option value='16px'>&nbsp;16px</option>\n"+
                            "          <option value='18px'>&nbsp;18px</option>\n"+
                            "          <option value='20px'>&nbsp;20px</option>\n"+
                            "          <option value='24px'>&nbsp;24px</option>\n"+
                            "        </select>\n"+
                            "   </td>"+
                            "   <td>&nbsp;&nbsp;</td>"+  
                            "   <td><div id='toolbarResize' title='Full Screen' onclick='toggleFullScreen()'></div></td>"+                               
                            "   <td><div id='toolbarSwitchLayout' title='Switch Layout' onclick='Command.switchLayout()'></div></td>"+                                
                            "   <td><div id='toolbarSwitchProject' title='Switch Project' onclick='Command.switchProject()'></div></td>"+     
                            "   <td>&nbsp;&nbsp;</td>"+                                 
                            "</tr>"+
                            "</table>"+
                            "</div>"
               } ]
         });
}

function createProblemsTab(){
   $().w2grid({
      name : 'problems',
      columns : [ {
         field : 'description',
         caption : 'Description',
         size : '45%',
         sortable : true,
         resizable : true
      },{
         field : 'location',
         caption : 'Location',
         size : '10%',
         sortable : true,
         resizable : true
      }, {
         field : 'resource',
         caption : 'Resource',
         size : '45%',
         sortable : true,
         resizable : true
      },  ],
      onClick : function(event) {
         var grid = this;
         event.onComplete = function() {
            var sel = grid.getSelection();
            if (sel.length == 1) {
               var record = grid.get(sel[0]);
               FileExplorer.openTreeFile(record.script, function() {
                  FileEditor.showEditorLine(record.line);  
               });
            }
            grid.selectNone();
            grid.refresh();
         }
      }
   });
}

function createVariablesTab(){
   $().w2grid({
      recordTitles: false, // show tooltips
      name : 'variables',
      columns : [ {
         field : 'name',
         caption : 'Name',
         size : '30%',
         sortable : false
      }, {
         field : 'value',
         caption : 'Value',
         size : '40%',
         sortable : false
      }, {
         field : 'type',
         caption : 'Type',
         size : '30%'
      } ],
      onClick : function(event) {
         var grid = this;
         event.onComplete = function() {
            var sel = grid.getSelection();
            if (sel.length == 1) {
               var record = grid.get(sel[0]);
               toggleExpandVariable(record.path);
            }
            grid.selectNone();
            grid.refresh();
         }
      }
   });
}

function createProfilerTab(){
   $().w2grid({
      name : 'profiler',
      columns : [ {
         field : 'resource',
         caption : 'Resource',
         size : '40%',
         sortable : false
      }, {
         field : 'percentage',
         caption : 'Percentage',
         size : '15%'
      },{
         field : 'line',
         caption : 'Line',
         size : '15%'
      }, {
         field : 'count',
         caption : 'Count',
         size : '10%'
      }, {
         field : 'duration',
         caption : 'Duration',
         size : '10%',
         sortable : false
      },{
         field : 'average',
         caption : 'Average',
         size : '10%',
         sortable : false
      }],
      onClick : function(event) {
         var grid = this;
         event.onComplete = function() {
            var sel = grid.getSelection();
            if (sel.length == 1) {
               var record = grid.get(sel[0]);
               FileExplorer.openTreeFile(record.script, function() {
                  FileEditor.showEditorLine(record.line);  
               }); 
            }
            grid.selectNone();
            grid.refresh();
         }
      }
   });
}

function createBreakpointsTab(){
   $().w2grid({
      name : 'breakpoints',
      columns : [ 
       {
         field : 'name',
         caption : 'Resource',
         size : '60%',
         sortable : true,
         resizable : true
      },{
         field : 'location',
         caption : 'Location',
         size : '40%',
         sortable : true,
         resizable : true
      } ],
      onClick : function(event) {
         var grid = this;
         event.onComplete = function() {
            var sel = grid.getSelection();
            if (sel.length == 1) {
               var record = grid.get(sel[0]);
               FileExplorer.openTreeFile(record.script, function() {
                  FileEditor.showEditorLine(record.line);  
               }); 
            }
            grid.selectNone();
            grid.refresh();
         }
      }
   });
}

function createThreadsTab(){
   $().w2grid({
      name : 'threads',
      columns : [ {
         field : 'name',
         caption : 'Thread',
         size : '25%',
         sortable : true,
         resizable : true
      }, {
         field : 'status',
         caption : 'Status',
         size : '10%',
         sortable : true,
         resizable : true
      }, {
         field : 'instruction',
         caption : 'Instruction',
         size : '15%',
         sortable : true,
         resizable : true
      },{
         field : 'resource',
         caption : 'Resource',
         size : '30%',
         sortable : true,
         resizable : true
      },{
         field : 'line',
         caption : 'Line',
         size : '10%',
         sortable : true,
         resizable : true
      },{
         field : 'active',
         caption : 'Active',
         size : '10%',
         sortable : false,
         resizable : true
      },],
      onClick : function(event) {
         var grid = this;
         event.onComplete = function() {
            var sel = grid.getSelection();
            if (sel.length == 1) {
               var record = grid.get(sel[0]);
               FileExplorer.openTreeFile(record.script, function(){
                  ThreadManager.updateThreadFocusByName(record.thread);
                  FileEditor.showEditorLine(record.line);  
                  ThreadManager.showThreads();
               });
            }
            grid.selectNone();
            grid.refresh();
         }
      }
   });
}

function createDebugTab(){
   $().w2grid({
      name : 'debug',
      columns : [ 
       {
         field : 'name',
         caption : 'Process',
         size : '20%',
         sortable : true,
         resizable : true
      }, {
         field : 'system',
         caption : 'System',
         size : '20%',
         sortable : true,
         resizable : true
      }, {
         field : 'status',
         caption : 'Status',
         size : '20%',
         sortable : true,
         resizable : true
      },{
         field : 'resource',
         caption : 'Resource',
         size : '30%',
         sortable : true,
         resizable : true
      },{
         field : 'active',
         caption : 'Focus',
         size : '10%',
         sortable : false,
         resizable : true
      } ],
      onClick : function(event) {
         var grid = this;
         event.onComplete = function() {
            var sel = grid.getSelection();
            if (sel.length == 1) {
               var record = grid.get(sel[0]);
               
               if(record.running) {
                  FileExplorer.openTreeFile(record.script, function() {
                     Command.attachProcess(record.process);
                  });
               } else {
                  Command.attachProcess(record.process);
               }
            }
            grid.selectNone();
            grid.refresh();
         }
      }
   });
}

function activateTab(tabName, layoutName, containsBrowse, containsEditor, browseStyle) {
   hideBrowseTreeContent(containsBrowse); // hide tree
   hideEditorContent(containsEditor); // hide tree
   
   if (tabName == 'consoleTab') {
      w2ui[layoutName].content('main', "<div style='overflow: scroll; font-family: monospace;' id='console'></div>");
      w2ui[layoutName].refresh();
      ProcessConsole.showConsole();
   } else if (tabName == 'problemsTab') {
      w2ui[layoutName].content('main', "<div style='overflow: scroll; font-family: monospace;' id='problems'></div>");
      w2ui[layoutName].refresh();
      $('#problems').w2render('problems');
      showProblems();
   } else if (tabName == 'breakpointsTab') {
      w2ui[layoutName].content('main', "<div style='overflow: scroll; font-family: monospace;' id='breakpoints'></div>");
      w2ui[layoutName].refresh();
      $('#breakpoints').w2render('breakpoints');
      FileEditor.showEditorBreakpoints();
   } else if(tabName == 'threadsTab'){
      w2ui[layoutName].content('main', "<div style='overflow: scroll; font-family: monospace;' id='threads'></div>");
      w2ui[layoutName].refresh();
      $('#threads').w2render('threads');
      ThreadManager.showThreads();
   } else if(tabName == 'variablesTab'){
      w2ui[layoutName].content('main', "<div style='overflow: scroll; font-family: monospace;' id='variables'></div>");
      w2ui[layoutName].refresh();
      $('#variables').w2render('variables');
      showVariables();
   } else if(tabName == 'profilerTab'){
      w2ui[layoutName].content('main', "<div style='overflow: scroll; font-family: monospace;' id='profiler'></div>");
      w2ui[layoutName].refresh();
      $('#profiler').w2render('profiler');
      showVariables();
   } else if(tabName == 'browseTab'){
      w2ui[layoutName].content('main', "<div style='overflow: hidden; font-family: monospace;' id='browse'><div id='browseParent' "+browseStyle+"></div></div>");
      w2ui[layoutName].refresh();
      $('#browse').w2render('browse');
      showBrowseTreeContent(containsBrowse); // hack to move tree
   } else if(tabName == 'debugTab'){
      w2ui[layoutName].content('main', "<div style='overflow: scroll; font-family: monospace;' id='debug'></div>");
      w2ui[layoutName].refresh();
      $('#debug').w2render('debug');
      showStatus();
   } else { // editor is always the default as it contains file names
      w2ui[layoutName].content('main', "<div style='overflow: scroll; font-family: monospace;' id='edit'><div id='editParent'></div></div>");
      w2ui[layoutName].refresh();
      $('#edit').w2render('edit');
      showEditorContent(containsEditor);
   }
}


ModuleSystem.registerModule("project", "Project module: project.js", createMainLayout, [ "common", "socket", "console", "problem", "editor", "spinner", "tree", "threads" ]);

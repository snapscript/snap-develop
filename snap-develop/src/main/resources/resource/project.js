
function changeProjectFont(){
   var fontFamily = document.getElementById("fontFamily");
   var fontSize = document.getElementById("fontSize");
   
   if(fontSize != null && fontFamily != null) {
      updateEditorFont(fontFamily.options[fontFamily.selectedIndex].value, fontSize.options[fontSize.selectedIndex].value);
      updateConsoleFont(fontFamily.options[fontFamily.selectedIndex].value, fontSize.options[fontSize.selectedIndex].value);
   }
}

function applyProjectTheme() {
   $.get("/display/"+document.title, function(theme) {
      //var theme = JSON.parse(response);
      if(theme.font != null && theme.size != null) {
         var fontFamily = document.getElementById("fontFamily");
         var fontSize = document.getElementById("fontSize");
         
         if(fontSize != null) {
            fontSize.value = theme.size + "px";
         }
         if(fontFamily != null) {
            fontFamily.value = theme.font;
         }   
      }
      changeProjectFont();// update the fonts
   });
}

function createMainLayout() {
   var locationPath = window.document.location.pathname;
   var debugToggle = ";debug";
   var remainingPath = locationPath.slice(-debugToggle.length);
   var debug = remainingPath == debugToggle; // true
   
   if(debug) {
      createDebugLayout(); // show debug layout
   } else {
      createExploreLayout();
   }
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
         style : pstyle,
         content : createExplorerContent() // explorer tree         
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

   var pstyle = 'background-color: #F5F6F7; overflow: hidden;';
   $('').w2layout({
      name : 'exploreEditorLayout',
      padding : 0,
      panels : [ {
         type : 'top',
         size : '60%',
         resizable : true,
         overflow: 'auto',
         style : pstyle + 'border-bottom: 0px;',
         content : createEditorContent(),
         onResizing: function(target, data) {
//            var topWidth = w2ui['editorLayout'].get('top').width;
//            var topHeight = w2ui['editorLayout'].get('top').height;
//            var mainWidth = w2ui['editorLayout'].get('main').width;
//            var mainHeight = w2ui['editorLayout'].get('main').height;
//            
//            console.log("top: "+topWidth+"x"+topHeight);
//            console.log("main: "+mainWidth+"x"+mainHeight);       
         }
      }, {
         type : 'main',
         size : '40%',
         overflow: 'auto',         
         resizable : true,
         style : pstyle + 'border-top: 0px;'
      } ]
   });

   
   $('').w2layout({
      name : 'exploreTabLayout',
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
               caption : '<div class="errorTab">Problems</div>'
            }, {
               id : 'breakpointsTab',
               caption : '<div class="breakpointsTab">Breakpoints</div>'
            }, {
               id : 'threadsTab',
               caption : '<div class="threadTab">Threads</div>'
            }, {
               id : 'variablesTab',
               caption : '<div class="variableTab">Variables</div>'
            }, {
               id : 'profilerTab',
               caption : '<div class="profilerTab">Profiler</div>'
            }, {
               id : 'debugTab',
               caption : '<div class="debugTab">Debug&nbsp;&nbsp;</div>'
            } ],
            onClick : function(event) {
               activateTab(event.target, "exploreTabLayout");
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
   w2ui['exploreMainLayout'].content('main', w2ui['exploreEditorLayout']);
   w2ui['exploreEditorLayout'].content('main', w2ui['exploreTabLayout']);
   w2ui['exploreTabLayout'].refresh();

   setTimeout(function() {
      applyProjectTheme();
      activateTab("consoleTab", "exploreTabLayout"); 
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
         type : 'left',
         size : '20%',
         resizable : true,
         hidden: true,
         style : pstyle,
         content: createExplorerContent()
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
         style : pstyle + 'border-bottom: 0px;',
         content : createEditorContent(),
         onResizing: function(target, data) {
//            var topWidth = w2ui['debugEditorLayout'].get('top').width;
//            var topHeight = w2ui['debugEditorLayout'].get('top').height;
//            var mainWidth = w2ui['debugEditorLayout'].get('main').width;
//            var mainHeight = w2ui['debugEditorLayout'].get('main').height;
//            
//            console.log("top: "+topWidth+"x"+topHeight);
//            console.log("main: "+mainWidth+"x"+mainHeight);       
         }         
      }, {
         type : 'bottom',
         size : '25%',
         overflow: 'auto',         
         resizable : true,
         style : pstyle + 'border-top: 0px;'
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
            active : 'threadsTab',
            tabs : [  {
               id : 'threadsTab',
               caption : '<div class="threadTab">Threads</div>'
            }, {
               id : 'debugTab',
               caption : '<div class="debugTab">Debug&nbsp;&nbsp;</div>'
            }/*, {
               id : 'browseTab',
               caption : '<div class="browseTab">Browse</div>',
               content : "<div style='overflow: scroll; font-family: monospace;' id='browse'>" + createExplorerContent() + "</div>" 
            } */],
            onClick : function(event) {
               activateTab(event.target, "debugLeftTabLayout");
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
               caption : '<div class="variableTab">Variables</div>'
            }, {
               id : 'breakpointsTab',
               caption : '<div class="breakpointsTab">Breakpoints</div>'
            } ],
            onClick : function(event) {
               activateTab(event.target, "debugRightTabLayout");
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
               id : 'errorTab',
               caption : '<div class="errorTab">Problems</div>'
            }, {
               id : 'profilerTab',
               caption : '<div class="profilerTab">Profiler</div>'
            } ],
            onClick : function(event) {
               activateTab(event.target, "debugBottomTabLayout");
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
   w2ui['debugEditorLayout'].content('top', w2ui['debugTopTabSplit']);
   w2ui['debugTopTabSplit'].content('left', w2ui['debugLeftTabLayout']);
   w2ui['debugTopTabSplit'].content('main', w2ui['debugRightTabLayout']);
   w2ui['debugEditorLayout'].content('bottom', w2ui['debugBottomTabLayout']);  
   w2ui['debugTopTabSplit'].refresh();
   w2ui['debugLeftTabLayout'].refresh();
   w2ui['debugRightTabLayout'].refresh();   
   w2ui['debugBottomTabLayout'].refresh();
   
   setTimeout(function() {
      applyProjectTheme();
      activateTab("threadsTab", "debugLeftTabLayout");
      activateTab("variablesTab", "debugRightTabLayout");   
      activateTab("consoleTab", "debugBottomTabLayout");  
   }, 300); // update theme
   
   
}

function createExplorerContent() {
   return "<div id='explorer'></div>";
}

function createEditorContent() {
   return "<div id='editor'></div>";
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
   var pstyle = 'background-color: #F5F6F7; overflow: hidden;';
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
                        + "<tr>"
                        + "   <td><div id='toolbarLogoContainer'><img style='height: 25px; margin-top: -1px;' src='/img/logo_grey_shade.png'></div></td>"
                        + "   <td><div class='toolbarSeparator'></div></td>"                           
                        + "   <td>"
                        + "      <table id='toolbarNormal'>"
                        + "      <tr>"
                        + "         <td><div id='newFile' onclick='newFile(null)' title='New File&nbsp;&nbsp;&nbsp;Ctrl+N'></div></td>"                           
                        + "         <td><div id='saveFile' onclick='saveFile(null)' title='Save File&nbsp;&nbsp;&nbsp;Ctrl+S'></div></td>" 
                        + "         <td><div id='deleteFile' onclick='deleteFile(null)' title='Delete File'></div></td>"   
                        + "         <td><div id='searchTypes' onclick='searchTypes()' title='Search Types&nbsp;&nbsp;&nbsp;Ctrl+Shift+S'></div></td>"                             
                        + "         <td><div id='runScript' onclick='runScript()' title='Run Script&nbsp;&nbsp;&nbsp;Ctrl+R'></div></td>" 
                        + "      </tr>"
                        + "      </table>"
                        + "   </td>" 
                        + "   <td><div class='toolbarSeparator'></div></td>"
                        + "   <td>"
                        + "      <table id='toolbarDebug'>"
                        + "      <tr>"
                        + "         <td><div id='stopScript' onclick='stopScript()' title='Stop Script'></div></td>" 
                        + "         <td><div id='resumeScript' onclick='resumeScript()' title='Resume Script'></div></td>" 
                        + "         <td><div id='stepInScript' onclick='stepInScript()' title='Step In'></div></td>" 
                        + "         <td><div id='stepOutScript' onclick='stepOutScript()' title='Step Out'></div></td>" 
                        + "         <td><div id='stepOverScript' onclick='stepOverScript()' title='Step Over'></div></td>" 
                        + "      </tr>"
                        + "      </table>"
                        + "   </td>"
                        + "</tr>"
                        + "</table>" 
                        + "</div>"
               }, {
                  type : 'main',
                  size : '20%',
                  style : pstyle,
                  content : "<div class='toolbarTop'></div>"
               }, {
                  type : 'right',
                  size : '40%',
                  style : pstyle,
                  content : "<div class='toolbarTop'>"+
                            "<table border='0' width='100%' cellpadding='0'>"+
                            "<tr>"+
                            "   <td  width='100%'></td>"+
                            "   <td>"+
                            "        <select class='styledSelect' id='fontFamily' size='1' onchange='changeProjectFont()'>\n"+
                            "          <option value='Consolas' selected='selected'>&nbsp;Consolas</option>\n"+
                            "          <option value='Lucida Console'>&nbsp;Lucida Console</option>\n"+
                            "          <option value='Courier New'>&nbsp;Courier New</option>\n"+       
                            "          <option value='Courier'>&nbsp;Courier</option>\n"+                                 
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
                            "   <td><div id='toolbarSwitch' onclick='switchProject()'></div></td>"+     
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
               openTreeFile(record.script, function(){}); // open resource
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
         size : '15%'
      }, {
         field : 'duration',
         caption : 'Duration',
         size : '15%',
         sortable : false
      }],
      onClick : function(event) {
         var grid = this;
         event.onComplete = function() {
            var sel = grid.getSelection();
            if (sel.length == 1) {
               var record = grid.get(sel[0]);
               openTreeFile(record.script, function() {
                  showEditorLine(record.line);  
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
               openTreeFile(record.script, function() {
                  showEditorLine(record.line);  
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
               openTreeFile(record.script, function(){
                  updateThreadFocus(record.thread, record.line, record.key);
                  showEditorLine(record.line);  
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
         caption : 'Active',
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
                  openTreeFile(record.script, function() {
                     attachProcess(record.process);
                  });
               }
            }
            grid.selectNone();
            grid.refresh();
         }
      }
   });
}

function activateTab(tabName, layoutName) {
   if (tabName == 'consoleTab') {
      w2ui[layoutName].content('main', "<div style='overflow: scroll; font-family: monospace;' id='console'></div>");
      w2ui[layoutName].refresh();
      showConsole();
   } else if (tabName == 'problemsTab') {
      w2ui[layoutName].content('main', "<div style='overflow: scroll; font-family: monospace;' id='problems'></div>");
      w2ui[layoutName].refresh();
      $('#problems').w2render('problems');
      showProblems();
   } else if (tabName == 'breakpointsTab') {
      w2ui[layoutName].content('main', "<div style='overflow: scroll; font-family: monospace;' id='breakpoints'></div>");
      w2ui[layoutName].refresh();
      $('#breakpoints').w2render('breakpoints');
      showEditorBreakpoints();
   } else if(tabName == 'threadsTab'){
      w2ui[layoutName].content('main', "<div style='overflow: scroll; font-family: monospace;' id='threads'></div>");
      w2ui[layoutName].refresh();
      $('#threads').w2render('threads');
      showThreads();
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
      w2ui[layoutName].content('main', "<div style='overflow: scroll; font-family: monospace;' id='browse'>" + createExplorerContent() + "</div>");
      w2ui[layoutName].refresh();
      $('#browse').w2render('browse');
   } else {
      w2ui[layoutName].content('main', "<div style='overflow: scroll; font-family: monospace;' id='debug'></div>");
      w2ui[layoutName].refresh();
      $('#debug').w2render('debug');
      showStatus();
   }
}


registerModule("project", "Project module: project.js", createMainLayout, [ "common", "socket", "console", "problem", "editor", "spinner", "tree", "threads" ]);

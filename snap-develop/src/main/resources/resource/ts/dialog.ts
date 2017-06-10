import * as $ from "jquery"
import {w2ui, w2popup} from "w2ui"
import {Common} from "common"
import {Command} from "commands"
import {VariableManager} from "variables"
import {FileExplorer} from "explorer"
import {FileEditor} from "editor"
import {FileTree} from "tree"
 
export module DialogBuilder {
   
   export function openTreeDialog(resourceDetails, foldersOnly, saveCallback) {
      if (resourceDetails != null) {
         createProjectDialog(resourceDetails, foldersOnly, saveCallback, "Save Changes");
      } else {
         createProjectDialog(resourceDetails, foldersOnly, saveCallback, "Save As");
      }
   }
   
   export function renameFileTreeDialog(resourceDetails, foldersOnly, saveCallback){
      createProjectDialog(resourceDetails, foldersOnly, saveCallback, "Rename File");
   }
   
   export function renameDirectoryTreeDialog(resourceDetails, foldersOnly, saveCallback){
      createProjectDialog(resourceDetails, foldersOnly, saveCallback, "Rename Directory");
   }
   
   export function newFileTreeDialog(resourceDetails, foldersOnly, saveCallback){
      createProjectDialog(resourceDetails, foldersOnly, saveCallback, "New File");
   }
   
   export function newDirectoryTreeDialog(resourceDetails, foldersOnly, saveCallback){
      createProjectDialog(resourceDetails, foldersOnly, saveCallback, "New Directory");
   }
   
   export function evaluateExpressionDialog(expressionToEvaluate){
      createEvaluateDialog(expressionToEvaluate, "Evaluate Expression");
   }
   
   function createProjectDialog(resourceDetails, foldersOnly, saveCallback, dialogTitle) {
      createTreeDialog(resourceDetails, foldersOnly, saveCallback, dialogTitle, "/" +document.title)
   }
   
   function createTreeDialog(resourceDetails, foldersOnly, saveCallback, dialogTitle, treePath) {
      var dialogExpandPath = "/";
   
      if (resourceDetails != null) {
         dialogExpandPath = resourceDetails.projectDirectory; // /src/blah
      }
      var dialogBody = createFileSelectionDialogLayout(dialogExpandPath, '');
      w2popup.open({
         title : dialogTitle,
         body : dialogBody.content, 
         buttons : '<button id="dialogSave" class="btn dialogButton">Save</button><button id="dialogCancel" class="btn dialogButton">Cancel</button>',
         width : 500,
         height : 400,
         overflow : 'hidden',
         color : '#999',
         speed : '0.3',
         opacity : '0.8',
         modal : true,
         showClose : true,
         showMax : true,
         onOpen : function(event) {
            setTimeout(function() {
               dialogBody.init();
               var element = document.getElementById('dialogPath');
               
               element.contentEditable = true;
               element.focus();
            }, 200);
         },
         onClose : function(event) {
            console.log('close');
         },
         onMax : function(event) {
            console.log('max');
            $(window).trigger('resize');
         },
         onMin : function(event) {
            console.log('min');
            $(window).trigger('resize');
         },
         onKeydown : function(event) {
            console.log('keydown');
         }
      });
      $("#dialogSave").click(function() {
         var originalDialogFileName = $('#dialogPath').html();
         var originalDialogFolder = $('#dialogFolder').html();
         var dialogPathName = FileTree.cleanResourcePath(originalDialogFileName);
         var dialogFolder = FileTree.cleanResourcePath(originalDialogFolder);
         var dialogProjectPath = dialogFolder + "/" + dialogPathName; // /src/blah/script.snap
         var dialogPathDetails = FileTree.createResourcePath(dialogProjectPath); 
         
         saveCallback(dialogPathDetails);
         w2popup.close();
      });
      $("#dialogCancel").click(function() {
         w2popup.close();
      });
      if (resourceDetails != null) {
         $('#dialogFolder').html(FileTree.cleanResourcePath(resourceDetails.projectDirectory)); // /src/blah
         $('#dialogPath').html(FileTree.cleanResourcePath(resourceDetails.fileName)); // script.snap
      }
      FileTree.createTree(treePath, "dialog", "dialogTree", dialogExpandPath, foldersOnly, null, function(event, data) {
         var selectedFileDetails = FileTree.createResourcePath(data.node.tooltip);
   
         if (data.node.isFolder()) {
            $('#dialogFolder').html(FileTree.cleanResourcePath(selectedFileDetails.projectDirectory));
            //$('#dialogPath').html(""); // DO NOT CLEAR THE PATH INPUT
         } else {
            $('#dialogFolder').html(FileTree.cleanResourcePath(selectedFileDetails.projectDirectory)); // /src/blah
            $('#dialogPath').html(FileTree.cleanResourcePath(selectedFileDetails.fileName)); // file.snap
         }
      });
   }
   
   export function createTreeOpenDialog(openCallback, closeCallback, dialogTitle, buttonText, treePath) {
      var completeFunction = function() {
         var originalDialogFolder = $('#dialogPath').html();
         var dialogFolder = FileTree.cleanResourcePath(originalDialogFolder); // clean up path
         var dialogPathDetails = FileTree.createResourcePath(dialogFolder); 
         var selectedDirectory = dialogPathDetails.projectDirectory;
         
         if(selectedDirectory.indexOf("/") == 0) {
            selectedDirectory = selectedDirectory.substring(1);
         }
         openCallback(dialogPathDetails, selectedDirectory);
      };
      var dialogBody = createFileFolderSelectionDialogLayout();
      
      w2popup.open({
         title : dialogTitle,
         body : dialogBody.content,
         buttons : '<button id="dialogSave" class="btn dialogButton">'+buttonText+'</button>',
         width : 500,
         height : 400,
         overflow : 'hidden',
         color : '#999',
         speed : '0.3',
         opacity : '0.8',
         modal : true,
         showClose : true,
         showMax : true,
         onOpen : function(event) {
            setTimeout(function() {
               dialogBody.init();
               var element = document.getElementById('dialogPath');
               
               element.contentEditable = true;
               element.focus();
            }, 200);
         },
         onClose : function(event) { 
            closeCallback(); // this should probably be a parameter
         },
         onMax : function(event) {
            console.log('max');
            $(window).trigger('resize');
         },
         onMin : function(event) {
            console.log('min');
            $(window).trigger('resize');
         },
         onKeydown : function(event) {
            console.log('keydown');
         }
      });
      $("#dialogSave").click(function() {
         completeFunction();
         w2popup.close();
      });
      FileTree.createTreeOfDepth(treePath, "dialog", "dialogTree", "/" + document.title, true, null, function(event, data) {
         var selectedFileDetails = FileTree.createResourcePath(data.node.tooltip);
         var selectedDirectory = selectedFileDetails.projectDirectory;
         
         if(selectedDirectory.indexOf("/") == 0) {
            selectedDirectory = selectedDirectory.substring(1);
         }
         $('#dialogPath').html(FileTree.cleanResourcePath(selectedDirectory));
      }, 2);  
   }       
        
   export function createListDialog(listFunction, patternList, dialogTitle) { // listFunction(token): [a, b, c]
      var dialogBody = createListDialogLayout();
      w2popup.open({
         title : dialogTitle,
         body : dialogBody.content,
         buttons : '<button id="dialogCancel" class="btn dialogButton">Cancel</button>',
         width : 800,
         height : 400, 
         overflow : 'hidden',
         color : '#999',
         speed : '0.3',
         opacity : '0.8',
         modal : true,
         showClose : true,
         showMax : true,
         onOpen : function(event) {
            setTimeout(function() {
               dialogBody.init();
               $('#dialogPath').on('change keyup paste', function() {
                  var expressionText = $("#dialogPath").html();
                  var expressionPattern = null;
                  
                  if(patternList) {
                     expressionPattern = $("#dialogFolder").html();
                     expressionPattern = Common.clearHtml(expressionPattern);
                  }
                  if(expressionText) {
                     expressionText = Common.clearHtml(expressionText);
                  } 
                  listFunction(expressionText, expressionPattern, function(list) {
                     var content = createDialogListTable(list);
                     
                     if(content.content){
                        $("#dialog").html(content.content);
                     }else {
                        $("#dialog").html('');
                     }
                     // this is kind of crap, but we need to be sure the html is rendered before binding
                     if(content.init) {
                        setTimeout(content.init, 100); // register the init function to run 
                     }
                  });
               });
               var element = document.getElementById('dialogPath');
               
               element.contentEditable = true;
               element.focus();
            }, 200);
         },
         onMax : function(event) {
            console.log('max');
            $(window).trigger('resize');
         },
         onMin : function(event) {
            console.log('min');
            $(window).trigger('resize');
         }
      });
      $("#dialogSave").click(function() {
         w2popup.close();
      });
      $("#dialogCancel").click(function() {
         w2popup.close();
      });
   }
   
   export function createTextSearchOnlyDialog(listFunction, fileFilterPatterns, dialogTitle) { // listFunction(token): [a, b, c]
      var dialogBody = createTextSearchOnlyDialogLayout(fileFilterPatterns, '');
      var executeSearch = function() {
         var expressionText = $("#searchText").html();
         var searchCriteria = {
               caseSensitive: isCheckboxSelected("inputCaseSensitive"),
               regularExpression: isCheckboxSelected("inputRegularExpression"),
               wholeWord: isCheckboxSelected("inputWholeWord")
            };
         var expressionPattern = null;
         
         if(fileFilterPatterns) {
            expressionPattern = $("#fileFilterPatterns").html();
            expressionPattern = Common.clearHtml(expressionPattern);
         }
         if(expressionText) {
            expressionText = Common.clearHtml(expressionText);
         } 
         listFunction(expressionText, expressionPattern, searchCriteria, function(list) {
            var content = createDialogListTable(list);
            
            if(content.content){
               $("#dialog").html(content.content);
            }else {
               $("#dialog").html('');
            }
            // this is kind of crap, but we need to be sure the html is rendered before binding
            if(content.init) {
               setTimeout(content.init, 100); // register the init function to run 
            }
         });
      };
      w2popup.open({
         title : dialogTitle,
         body : dialogBody.content,
         buttons : '<button id="dialogCancel" class="btn dialogButton">Cancel</button>',
         width : 800,
         height : 400, 
         overflow : 'hidden',
         color : '#999',
         speed : '0.3',
         opacity : '0.8',
         modal : true,
         showClose : true,
         showMax : true,
         onOpen : function(event) {
            setTimeout(function() {
               dialogBody.init();
               $('#searchText').on('change keyup paste', executeSearch);
//               $('#inputCaseSensitive').change(executeSearch);
//               $('#inputRegularExpression').change(executeSearch);
//               $('#inputWholeWord').change(executeSearch);
               var element = document.getElementById('searchText');
               
               element.contentEditable = true;
               element.focus();
            }, 200);
         },
         onMax : function(event) {
            console.log('max');
            $(window).trigger('resize');
         },
         onMin : function(event) {
            console.log('min');
            $(window).trigger('resize');
         }
      });
      $("#dialogSave").click(function() {
         w2popup.close();
      });
      $("#dialogCancel").click(function() {
         w2popup.close();
      });
   }  
   
   export function createTextSearchAndReplaceDialog(listFunction, fileFilterPatterns, dialogTitle) { // listFunction(token): [a, b, c]
      var dialogBody = createTextSearchAndReplaceDialogLayout(fileFilterPatterns, '');
      var executeSearch = function() {
         var expressionText = $("#searchText").html();
         var searchCriteria = {
               caseSensitive: isCheckboxSelected("inputCaseSensitive"),
               regularExpression: isCheckboxSelected("inputRegularExpression"),
               wholeWord: isCheckboxSelected("inputWholeWord")
            };
         var expressionPattern = null;
         
         if(fileFilterPatterns) {
            expressionPattern = $("#fileFilterPatterns").html();
            expressionPattern = Common.clearHtml(expressionPattern);
         }
         if(expressionText) {
            expressionText = Common.clearHtml(expressionText);
         } 
         listFunction(expressionText, expressionPattern, searchCriteria, function(list) {
            var content = createDialogListTable(list);
            
            if(content.content){
               $("#dialog").html(content.content);
            }else {
               $("#dialog").html('');
            }
            // this is kind of crap, but we need to be sure the html is rendered before binding
            if(content.init) {
               setTimeout(content.init, 100); // register the init function to run 
            }
         });
      };
      w2popup.open({
         title : dialogTitle,
         body : dialogBody.content,
         buttons : '<button id="dialogSave" class="btn dialogButton">Replace</button><button id="dialogCancel" class="btn dialogButton">Cancel</button>',
         width : 800,
         height : 400, 
         overflow : 'hidden',
         color : '#999',
         speed : '0.3',
         opacity : '0.8',
         modal : true,
         showClose : true,
         showMax : true,
         onOpen : function(event) {
            setTimeout(function() {
               dialogBody.init();
               $('#searchText').on('change keyup paste', executeSearch);
//               $('#inputCaseSensitive').change(executeSearch);
//               $('#inputRegularExpression').change(executeSearch);
//               $('#inputWholeWord').change(executeSearch);
               var element = document.getElementById('searchText');
               
               element.contentEditable = true;
               element.focus();
            }, 200);
         },
         onMax : function(event) {
            console.log('max');
            $(window).trigger('resize');
         },
         onMin : function(event) {
            console.log('min');
            $(window).trigger('resize');
         }
      });
      $("#dialogSave").click(function() {
         var searchText = $("#searchText").html();
         var replaceText = $("#replaceText").html();
         var filePatterns = $("#fileFilterPatterns").html();
         var searchCriteria = {
               caseSensitive: isCheckboxSelected("inputCaseSensitive"),
               regularExpression: isCheckboxSelected("inputRegularExpression"),
               wholeWord: isCheckboxSelected("inputWholeWord"),
               enableReplace: true,
               replace: replaceText
            };
         
         Command.replaceTokenInFiles(searchText, searchCriteria, filePatterns);
         w2popup.close();
      });
      $("#dialogCancel").click(function() {
         w2popup.close();
      });
   }
   
   function createEvaluateDialog(inputText, dialogTitle) { 
      var dialogBody = createGridDialogLayout(inputText ? Common.escapeHtml(inputText) : '');
      w2popup.open({
         title : dialogTitle,
         body : dialogBody.content,
         buttons : '<button id="dialogSave" class="btn dialogButton">Evaluate</button>',
         width : 700,
         height : 400,
         overflow : 'hidden',
         color : '#999',
         speed : '0.3',
         opacity : '0.8',
         modal : false,
         showClose : true,
         showMax : true,
         onOpen : function(event) {
            setTimeout(function() {
               dialogBody.init(); // bind the functions
               $('#dialog').w2grid({
                  recordTitles: false, // show tooltips
                  name : 'evaluation',
                  columns : [ {
                     field : 'name',
                     caption : 'Name',
                     size : '40%',
                     sortable : false
                  }, {
                     field : 'value',
                     caption : 'Value',
                     size : '30%',
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
                           var text = $("#dialogPath").html();
                           var expression = Common.clearHtml(text);
                           
                           VariableManager.toggleExpandEvaluation(record.path, expression);
                        }
                        grid.selectNone();
                        grid.refresh();
                     }
                  }               
               }); 
               setTimeout(function() {
                  VariableManager.showVariables();
               }, 200);
            }, 200);
         },
         onClose : function(event) {
            w2ui['evaluation'].destroy(); // destroy grid so you can recreate it
            //$("#dialog").remove(); // delete the element
            VariableManager.clearEvaluation();
            Command.browseScriptEvaluation([], "", true); // clear the variables
         },
         onMax : function(event) {
            event.onComplete = function() {
               w2ui['evaluation'].refresh(); // resize
            }
            $(window).trigger('resize');
         },
         onMin : function(event) {
            event.onComplete = function() {
               w2ui['evaluation'].refresh(); // resize
            }
            $(window).trigger('resize');
         },
         onKeydown : function(event) {
            console.log('keydown');
         }
      });
      $("#dialogSave").click(function() {
         var text = $("#dialogPath").html();
         var expression = Common.clearHtml(text);
         
         Command.browseScriptEvaluation([], expression, true); // clear the variables
      });
   }
   
   function createDialogListTable(list) {
      var content = "<table id='dialogListTable' class='dialogListTable' width='100%'>";
      var selectedIndex = selectedIndexOfDialogListTable();
      var mouseOverFunctions = {};
      var clickFunctions = {};
      
      for(var i = 0; i < list.length; i++) {
         var dialogListEntryId = "dialogListEntry" + i;
         var row = list[i];
         
         content += "<tr ";
         
         if(i == selectedIndex) {
            content+= " class='dialogListTableRowSelected' ";
         }
         content += " id='" + dialogListEntryId + "'>";

         mouseOverFunctions[i] = function(rowId) {
            var selectedIndex = selectedIndexOfDialogListTable();
            selectDialogListTableRow(selectedIndex, rowId);
         };
         for(var j = 0; j < row.length; j++) {
            const cell = row[j];
            const entryId = "listEntry_" + i + "_" + j;
            
            content += "<td width='50%'><div id='" + entryId + "' class='";
            content += cell.style;
            content += "'>";
            content += cell.text;
            content += "</div></td>";
            
            clickFunctions[i] = function() { // this is a little rubbish, we are overriding the row click
               if(cell.line) {
                  return submitDialogListResource(cell.resource, cell.line);
               } else {
                  return submitDialogListResource(cell.link);
               }
            }
         }
         content += "<td>&nbsp;&nbsp;&nbsp;&nbsp;</td>"; 
         content += "</tr>";
      }
      content +="</table>";
      return {
         content: content,
         init: function() {
            // initialize all functions
            for (var clickFunctionId in clickFunctions) {
               if (clickFunctions.hasOwnProperty(clickFunctionId)) {
                  const clickFunction = clickFunctions[clickFunctionId];
                  const rowId = clickFunctionId;
                  
                  $('#dialogListEntry' + rowId).on('click', function(e) {
                     return clickFunction();
                  });
               }
            }
            for (var mouseOverFunctionId in mouseOverFunctions) {
               if (mouseOverFunctions.hasOwnProperty(mouseOverFunctionId)) {
                  const mouseOverFunction = mouseOverFunctions[mouseOverFunctionId];
                  const rowId = mouseOverFunctionId;
                  
                  $('#dialogListEntry' + rowId).on('mouseenter', function(e) {
                     return mouseOverFunction(rowId);
                  });
               }
            }
         }
      };
   }
   
   function navigateDialogListTable(event) {
      if(isKeyReturn(event)) {
         var selectedRowIndex = selectedIndexOfDialogListTable();
         
         if(selectedRowIndex >= 0) {
            $('#dialogListEntry' + selectedRowIndex).click(); // click on return
         }
      } else if(isKeyDown(event)) {
         navigateDialogListTableDown();
      } else if(isKeyUp(event)) {
         navigateDialogListTableUp();
      }
   }
   
   function navigateDialogListTableDown() {
      var selectedRowIndex = selectedIndexOfDialogListTable();
      
      if(selectedRowIndex == -1) {
         selectDialogListTableRow(selectedRowIndex, 0);
      } else {
         selectDialogListTableRow(selectedRowIndex, selectedRowIndex + 1);
      }
   }
   
   function navigateDialogListTableUp() {
      var selectedRowIndex = selectedIndexOfDialogListTable();
      
      if(selectedRowIndex == -1) {
         selectDialogListTableRow(selectedRowIndex, 0);
      } else {
         selectDialogListTableRow(selectedRowIndex, selectedRowIndex - 1);
      }
   }
   
   function selectDialogListTableRow(selectedRowIndex, nextRowIndex) {
      var selectedRow = document.getElementById("dialogListEntry" + selectedRowIndex);
      var nextRow = document.getElementById("dialogListEntry" + nextRowIndex);
      
      if(nextRow) {
         var container = document.getElementById("dialog");
         var offsetY = calculateScrollOffset(container, nextRow);
         
         console.log("offset: " + offsetY)

         if(selectedRow) {
            selectedRow.className = "";
         }
         container.scrollTop = container.scrollTop + offsetY;
         nextRow.className = "dialogListTableRowSelected";
      }
   }
   
   function selectedIndexOfDialogListTable() {
      var table = document.getElementById("dialogListTable");
      
      if(table) {
         var dialogRows = table.rows;
      
         for(var i = 0; i < dialogRows.length; i++) {
            var dialogRow = dialogRows[i];
            
            if(dialogRow) {
               if(dialogRow.classList.contains("dialogListTableRowSelected")){
                  var rowIndex = dialogRow.id.replace("dialogListEntry", "");
                  return parseInt(rowIndex);
               }
            }
         }
      }
      return -1;
   }
   
   function createGridDialogLayout(inputText) {
      if(!inputText) {
         inputText = '';
      }
      return {
         content: '<div id="dialogContainerBig">'+
                  '   <div id="dialog" class="dialog"></div>'+
                  '</div>'+
                  '<div id="dialogPath" contenteditable="true">' + inputText + '</div>',
                  
         init: function() {
            $('#dialogPath').on('keydown', function(e) {
               navigateDialogListTable(e);
               return submitDialog(e);
            });
            $('#dialogPath').on('click', function(e) {
               return focusDialogInput('dialogPath');
            }); 
         }        
      };
   }
   
   function createListDialogLayout() {
      return {
         content: '<div id="dialogContainerBig">'+
                  '   <div id="dialog" class="dialog"></div>'+
                  '</div>'+
                  '<div id="dialogPath" contenteditable="true"></div>',
                  
         init: function() {
            $('#dialogPath').on('keydown', function(e) {
               navigateDialogListTable(e);
               return submitDialog(e);
            });
            $('#dialogPath').on('click', function(e) {
               return focusDialogInput('dialogPath');
            }); 
         }
      };                  
   }
   
   function createFileFolderSelectionDialogLayout() {
      return {
         content: '<div id="dialogContainerBig">\n'+
                  '   <div id="dialog" class="dialogTree"></div>\n'+
                  '</div>\n'+
                  '<div id="dialogPath" contenteditable="true"></div>',
                
         init: function() {
            $('#dialogPath').on('keydown', function(e) {
               navigateDialogListTable(e);
               return submitDialog(e);
            });
            $('#dialogPath').on('click', function(e) {
               return focusDialogInput('dialogPath');
            }); 
         }
      };

   }
   
   function createFileSelectionDialogLayout(selectedFileFolder, selectedFile) {
      if(!selectedFileFolder) {
         selectedFileFolder = '';
      }
      if(!selectedFile) {
         selectedFile = '';
      }
      return {
         content: '<div id="dialogContainer">\n'+
                  '   <div id="dialog" class="dialogTree"></div>\n'+
                  '</div>\n'+
                  '<div id="dialogFolder">'+selectedFileFolder+'</div>\n'+
                  '<div id="dialogPath" contenteditable="true">'+selectedFile+'</div>',
                  
         init: function() {
            $('#dialogPath').on('keydown', function(e) {
               navigateDialogListTable(e);
               return submitDialog(e);
            });
            $('#dialogPath').on('click', function(e) {
               return focusDialogInput('dialogPath');
            });
         }
      };                    

   }
   
   function createTextSearchOnlyDialogLayout(fileFilterPatterns, searchText) {
      if(!searchText) {
         searchText = '';
      }
      return {
         content: '<div id="dialogContainer">\n'+
                  '   <div id="dialog" class="dialog"></div>\n'+
                  '</div>\n'+
                  '<div id="fileFilterPatterns" class="searchFileFilterInputBox" contenteditable="true"">'+fileFilterPatterns+'</div>\n'+
                  '<div id="searchText" class="searchValueInputBox" contenteditable="true"">'+searchText+'</div>\n'+
                  '<div class="searchCheckBoxPanel">\n'+
                  '   <table border="0" cellspacing="5">\n'+
                  '      <tr id="inputCaseSensitiveRow">\n'+
                  '         <td><input type="checkbox" name="caseSensitive" id="inputCaseSensitive"><label></label>&nbsp;&nbsp;Case sensitive</td>\n'+
                  '      </tr>\n'+
                  '      <tr><td height="5px"></td></tr>\n'+
                  '      <tr id="inputRegularExpressionRow">\n'+
                  '         <td><input type="checkbox" name="regex" id="inputRegularExpression"><label></label>&nbsp;&nbsp;Regular expression</td>\n'+
                  '      </tr>\n'+
                  '      <tr><td height="5px"></td></tr>\n'+
                  '      <!--tr id="inputWholeWordRow">\n'+
                  '         <td><input type="checkbox" name="wholeWord" id="inputWholeWord"><label></label>&nbsp;&nbsp;Whole word</td>\n'+
                  '      </tr-->\n'+                 
                  '   </table>\n'+  
                  '</div>',
                  
         init: function() {
            $('#fileFilterPatterns').on('click', function(e) {
               return focusDialogInput('fileFilterPatterns');
            });
            $('#searchText').on('keydown', function(e) {
               navigateDialogListTable(e);
               return submitDialog(e);
            });
            $('#searchText').on('click', function(e) {
               return focusDialogInput('searchText');
            });
            $('#dialog').on('keydown', function(e) {
               navigateDialogListTable(e);
               return submitDialog(e);
            });
            $('#inputCaseSensitiveRow').on('click', function(e) {
               return toggleCheckboxSelection('inputCaseSensitive');
            });
            $('#inputRegularExpressionRow').on('click', function(e) {
               return toggleCheckboxSelection('inputRegularExpression');
            });
//                     $('#inputWholeWordRow').on('click', function(e) {
//                        return toggleCheckboxSelection('inputWholeWord');
//                     });
         }
      };                            
   }
   
   function createTextSearchAndReplaceDialogLayout(fileFilterPatterns, searchText) {
      if(!searchText) {
         searchText = '';
      }
      return { 
         content: '<div id="dialogContainerSmall">\n'+
                  '   <div id="dialog" class="dialog"></div>\n'+
                  '</div>\n'+
                  '<div id="fileFilterPatterns" class="searchAndReplaceFileFilterInputBox" contenteditable="true"">'+fileFilterPatterns+'</div>\n'+
                  '<div id="searchText" class="searchAndReplaceValueInputBox" contenteditable="true">'+searchText+'</div>\n'+
                  '<div id="replaceText" class="searchAndReplaceInputBox" contenteditable="true"></div>\n'+
                  '<div class="searchAndReplaceCheckBoxPanel">\n'+
                  '   <table border="0" cellspacing="5">\n'+
                  '      <tr id="inputCaseSensitiveRow">\n'+
                  '         <td><input type="checkbox" name="caseSensitive" id="inputCaseSensitive"><label></label>&nbsp;&nbsp;Case sensitive</td>\n'+
                  '      </tr>\n'+
                  '      <tr><td height="5px"></td></tr>\n'+
                  '      <tr id="inputRegularExpressionRow">\n'+
                  '         <td><input type="checkbox" name="regex" id="inputRegularExpression"><label></label>&nbsp;&nbsp;Regular expression</td>\n'+
                  '      </tr>\n'+
                  '      <tr><td height="5px"></td></tr>\n'+
                  '      <!--tr id="inputWholeWordRow">\n'+
                  '         <td><input type="checkbox" name="wholeWord" id="inputWholeWord"><label></label>&nbsp;&nbsp;Whole word</td>\n'+
                  '      </tr-->\n'+                 
                  '   </table>\n'+  
                  '</div>',
                  
         init: function() {
            $('#fileFilterPatterns').on('click', function(e) {
               return focusDialogInput('fileFilterPatterns');
            });
            $('#searchText').on('keydown', function(e) {
               navigateDialogListTable(e);
               return submitDialog(e);
            });
            $('#searchText').on('click', function(e) {
               return focusDialogInput('searchText');
            });
            $('#replaceText').on('keydown', function(e) { // really??
               navigateDialogListTable(e);
               return submitDialog(e);
            });
            $('#replaceText').on('click', function(e) {
               return focusDialogInput('replaceText');
            });
            $('#dialog').on('keydown', function(e) {
               navigateDialogListTable(e);
               return submitDialog(e);
            });
            $('#inputCaseSensitiveRow').on('click', function(e) {
               return toggleCheckboxSelection('inputCaseSensitive');
            });
            $('#inputRegularExpressionRow').on('click', function(e) {
               return toggleCheckboxSelection('inputRegularExpression');
            });
//            $('#inputWholeWordRow').on('click', function(e) {
//               return toggleCheckboxSelection('inputWholeWord');
//            });
         }
      };
   }
    
   function submitDialogListResource(resource, line) {
      $("#dialogCancel").click(); // force the click
      
      if(line) {
         FileExplorer.openTreeFile(resource, function() {
            window.setTimeout(function() {
               FileEditor.showEditorLine(line);
            }, 100); // delay focus on line, some bug here that needs a delay 
         });
      }else {
         location.href = resource;
      }
      return false
   }
   
   function focusDialogInput(name) {
      document.getElementById(name).contentEditable = true;
      document.getElementById(name).focus();
      document.getElementById(name).focus();
      return true;
   }

   function calculateScrollOffset(parentElement, childElement) {
       var childRect = childElement.getBoundingClientRect();
       var parentRect = parentElement.getBoundingClientRect();
       var topOfChildRect = childRect.top;
       var topOfParentRect = parentRect.top;
       
       if(topOfChildRect < topOfParentRect) {
          return topOfChildRect - topOfParentRect;
       }
       var bottomOfChildRect = childRect.top + childRect.height;
       //var bottomOfParentRect = parentRect.top + parentRect.height;
       var bottomOfParentRect = parentRect.top + parentElement.clientHeight;
       
       if(bottomOfChildRect > bottomOfParentRect) {
          return bottomOfChildRect - bottomOfParentRect;
       } 
       return 0;
   }
   
   function isCheckboxSelected(input) {
      var inputField = document.getElementById(input);
      
      if(inputField) {
         return inputField.checked;
      }
      return false;
   }
   
   function toggleCheckboxSelection(input) {
      var inputField = document.getElementById(input);
      
      if(inputField) {
         inputField.checked = !inputField.checked;
      }
      return false;
   }
   
   function isKeyUp(e) {
      var evt = e || window.event
      return evt.keyCode === 38;
   }
   
   function isKeyDown(e) {
      var evt = e || window.event
      return evt.keyCode === 40;
   }
   
   function isKeyReturn(e) {
      var evt = e || window.event
      return evt.keyCode === 13;
   }
   
   function submitDialog(e) {
      var evt = e || window.event
      // "e" is the standard behavior (FF, Chrome, Safari, Opera),
      // while "window.event" (or "event") is IE's behavior
      if(isKeyReturn(e)) {
         $("#dialogSave").click(); // force the click
          return false
      } 
   }
}

//ModuleSystem.registerModule("dialog", "Dialog module: dialog.js", null, null, [ "common", "tree" ]);

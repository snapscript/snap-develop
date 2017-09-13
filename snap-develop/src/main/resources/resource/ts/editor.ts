import * as $ from "jquery"
import {md5} from "md5"
import {ace} from "ace"
import {w2ui, w2popup} from "w2ui"
import {Common} from "common"
import {EventBus} from "socket"
import {ProcessConsole} from "console"
import {ProblemManager} from "problem"
import {LoadSpinner} from "spinner"
import {FileTree} from "tree"
import {ThreadManager} from "threads"
import {History} from "history"
import {VariableManager} from "variables"
import {Project} from "project"
import {StatusPanel} from "status"
import {KeyBinder} from "keys"
import {Command} from "commands"

export module FileEditor {

   var editorBreakpoints = {}; // spans multiple resources
   var editorMarkers = {};
   var editorResource = null;
   var editorText = null;
   var editorTheme = null;
   var editorCurrentTokens = {}; // current editor hyperlinks
   var editorFocusToken = null; // token to focus on editor load
   var editorHistory = {};
   
   export function createEditor() {
      console.log("FileEditor::createEditor()");
      showEditor();
      EventBus.createTermination(clearEditorHighlights); // create callback
   }
   
   export function clearEditorHighlights() {
      console.log("FileEditor::clearEditorHighlights()");
      var editor = ace.edit("editor");
      var session = editor.getSession();
       
      for ( var editorLine in editorMarkers) {
         if (editorMarkers.hasOwnProperty(editorLine)) {
            var marker = editorMarkers[editorLine];
            
            if(marker != null) {
               session.removeMarker(marker);
            }
         }
      }
      editorMarkers = {};
   }
   
   export function showEditorLine(line) {
      console.log("FileEditor::showEditorLine("+line+")");
      var editor = ace.edit("editor");
      
      editor.resize(true);
      
      if(line > 1) {
         editor.scrollToLine(line - 1, true, true, function () {})
      } else {
         editor.scrollToLine(0, true, true, function () {})
      }
   }
   
   function clearEditorHighlight(line) {
      console.log("FileEditor::clearEditorHighlight("+line+")");
      var editor = ace.edit("editor");
      var session = editor.getSession();
      var marker = editorMarkers[line];
      
      if(marker != null) {
         session.removeMarker(marker);
      }
   }
   
   export function createEditorHighlight(line, css) {
      console.log("FileEditor::createEditorHighlight("+line+", "+css+")");
      var editor = ace.edit("editor");
      var Range = ace.require('ace/range').Range;
      var session = editor.getSession();
   
      // clearEditorHighlight(line);
      clearEditorHighlights(); // clear all highlights in editor
      
      // session.addMarker(new Range(from, 0, to, 1), "errorMarker",
      // "fullLine");
      var marker = session.addMarker(new Range(line - 1, 0, line - 1, 1), css, "fullLine");
      editorMarkers[line] = marker;
   }
   
   export function findAndReplaceTextInEditor(){
      console.log("FileEditor::findAndReplaceTextInEditor()");
      var editorData = loadEditor();
      Command.searchAndReplaceFiles(editorData.resource.projectPath);
   }
   
   export function findTextInEditor() {
      console.log("FileEditor::findTextInEditor()");
      var editorData = loadEditor();
      Command.searchFiles(editorData.resource.projectPath);
// Alerts.createPromptAlert("Find Text", "Find", "Cancel", function(textToFind)
// {
// var editor = ace.edit("editor");
// var session = editor.getSession();
// // var matchesFound = {};
// var range = editor.find(textToFind,{
// backwards: false,
// wrap: true,
// caseSensitive: false,
// wholeWord: false,
// regExp: false
// });
//         
// // while(range) {
// // var rangeKey = JSON.stringify(range);
// //
// // if(!matchesFound.hasOwnProperty(rangeKey)) {
// // matchesFound[rangeKey] = true;
// session.addMarker(range, "findHighlight", "background"); //
// "background"|"text"|"fullLine"
// // range = editor.findNext();
// // } else {
// // break;
// // }
// // }
// //editor.findNext();
// //editor.findPrevious();
// });
   }
   
   export function addEditorKeyBinding(keyBinding, actionFunction) {
      console.log("FileEditor::addEditorKeyBinding("+keyBinding+", "+actionFunction+")");
      var editor = ace.edit("editor");

      editor.commands.addCommand({
           name : keyBinding.editor,
           bindKey : {
              win : keyBinding.editor,
              mac : keyBinding.editor
           },
           exec : function(editor) {
              if(actionFunction) { 
                 actionFunction();
              }
           }
      });
   }
   
//   
// export function indentCurrentLine() {
// var editor = ace.edit("editor");
// editor.indent();
// }
//   
// export function commentSelection() {
// var editor = ace.edit("editor");
// editor.toggleCommentLines();
// }
//   
// export function moveCursorUp() {
// moveCursorTo(-1, 0);
// }
//   
// export function moveCursorDown() {
// moveCursorTo(1, 0);
// }
//   
// export function moveCursorLeft() {
// moveCursorTo(0, -1);
// }
//   
// export function moveCursorRight() {
// moveCursorTo(0, 1);
// }
//   
// function moveCursorTo(rowChange, columnChange) {
// var editor = ace.edit("editor");
// var cursorPosition = editor.getCursorPosition();
// var currentRow = cursorPosition.row;
// var currentColumn = cursorPosition.column;
// var maxRow = editor.session.getLength() - 1
// var maxColumn = editor.session.getLine(currentColumn).length // or simply
// Infinity
// var nextRow = currentRow + rowChange;
// var nextColumn = currentColumn + columnChange;
//      
// if(nextRow <= maxRow && /*nextColumn <= maxColumn &&*/ nextRow >= 0 &&
// nextColumn >= 0) {
// editor.selection.moveTo(nextRow, nextColumn);
// }
// }
   
   function clearEditorBreakpoint(row) {
      console.log("FileEditor::clearEditorBreakpoint("+row+")");
      var editor = ace.edit("editor");
      var session = editor.getSession();
      var breakpoints = session.getBreakpoints();
      var remove = false;
   
      for ( var breakpoint in breakpoints) {
         session.clearBreakpoint(row);
      }
      showEditorBreakpoints();
   }
   
   function clearEditorBreakpoints() {
      console.log("FileEditor::clearEditorBreakpoints()");
      var editor = ace.edit("editor");
      var session = editor.getSession();
      var breakpoints = session.getBreakpoints();
      var remove = false;
   
      for ( var breakpoint in breakpoints) {
         session.clearBreakpoint(row);
      }
   }
   
   export function showEditorBreakpoints() {
      console.log("FileEditor::showEditorBreakpoints()");
      var breakpointRecords = [];
      var breakpointIndex = 1;
   
      for ( var filePath in editorBreakpoints) {
         if (editorBreakpoints.hasOwnProperty(filePath)) {
            var breakpoints = editorBreakpoints[filePath];
   
            for ( var lineNumber in breakpoints) {
               if (breakpoints.hasOwnProperty(lineNumber)) {
                  if (breakpoints[lineNumber] == true) {
                     var resourcePathDetails = FileTree.createResourcePath(filePath);
                     var displayName = "<div class='breakpointEnabled'>"+resourcePathDetails.projectPath+"</div>";
                     
                     breakpointRecords.push({
                        recid: breakpointIndex++,
                        name: displayName,
                        location : "Line " + lineNumber,
                        resource : resourcePathDetails.projectPath,
                        line: parseInt(lineNumber),
                        script : resourcePathDetails.resourcePath
                     });
                  }
               }
            }
         }
      }
      w2ui['breakpoints'].records = breakpointRecords;
      w2ui['breakpoints'].refresh();
      Command.updateScriptBreakpoints(); // update the breakpoints
   }
   
   function setEditorBreakpoint(row, value) {
      console.log("FileEditor::setEditorBreakpoint("+row+", "+value+")");
      if (editorResource != null) {
         var editor = ace.edit("editor");
         var session = editor.getSession();
         var resourceBreakpoints = editorBreakpoints[editorResource.filePath];
         var line = parseInt(row);
   
         if (value) {
            session.setBreakpoint(line);
         } else {
            session.clearBreakpoint(line);
         }
         if (resourceBreakpoints == null) {
            resourceBreakpoints = {};
            editorBreakpoints[editorResource.filePath] = resourceBreakpoints;
         }
         resourceBreakpoints[line + 1] = value;
      }
      showEditorBreakpoints();
   }
   
   function toggleEditorBreakpoint(row) {
      console.log("FileEditor::toggleEditorBreakpoint("+row+")");
      if (editorResource != null) {
         var editor = ace.edit("editor");
         var session = editor.getSession();
         var resourceBreakpoints = editorBreakpoints[editorResource.filePath];
         var breakpoints = session.getBreakpoints();
         var remove = false;
   
         for ( var breakpoint in breakpoints) {
            if (breakpoint == row) {
               remove = true;
               break;
            }
         }
         if (remove) {
            session.clearBreakpoint(row);
         } else {
            session.setBreakpoint(row);
         }
         var line = parseInt(row);
   
         if (resourceBreakpoints == null) {
            resourceBreakpoints = {};
            resourceBreakpoints[line + 1] = true;
            editorBreakpoints[editorResource.filePath] = resourceBreakpoints;
         } else {
            if (resourceBreakpoints[line + 1] == true) {
               resourceBreakpoints[line + 1] = false;
            } else {
               resourceBreakpoints[line + 1] = true;
            }
         }
      }
      showEditorBreakpoints();
   }
   
   export function resizeEditor() {
      console.log("FileEditor::resizeEditor()");
      var editor = ace.edit("editor");
      var width = document.getElementById('editor').offsetWidth;
      var height = document.getElementById('editor').offsetHeight;
      
      console.log("Resize editor " + width + "x" + height);
      editor.setAutoScrollEditorIntoView(true);
      editor.resize(true);
      // editor.focus();
   }
   
   export function resetEditor() {
      console.log("FileEditor::resetEditor()");
      var editor = ace.edit("editor");
      var session = editor.getSession();
   
      editorMarkers = {};
      editorResource = null;
      editor.setReadOnly(false);
      session.setValue(editorText, 1);
      $("#currentFile").html("");
   }
   
   function clearEditor() {
      console.log("FileEditor::clearEditor()");
      var editor = ace.edit("editor");
      var session = editor.getSession();
   
      for ( var editorMarker in session.$backMarkers) {
         session.removeMarker(editorMarker);
      }
      var breakpoints = session.getBreakpoints();
      var remove = false;
   
      for ( var breakpoint in breakpoints) {
         session.clearBreakpoint(breakpoint);
      }
      $("#currentFile").html("");
   }
   
   export function loadEditor() {
      console.log("FileEditor::loadEditor()");
      var editor = ace.edit("editor");
      var editorHistory = loadEditorHistory();
      var text = editor.getValue();
   
      return {
         breakpoints : editorBreakpoints,
         resource : editorResource,
         history : editorHistory,
         source : text
      };
   }
   
   function loadEditorHistory() {
      console.log("FileEditor::loadEditorHistory()");
      var editor = ace.edit("editor");
      var session = editor.getSession();
      var manager = session.getUndoManager();
      var undoStack = $.extend(true, {}, manager.$undoStack);
      var redoStack = $.extend(true, {}, manager.$redoStack);

      return {
         undoStack: undoStack,
         redoStack: redoStack,
         dirtyCounter: manager.dirtyCounter
      }
   }
   
   function encodeEditorText(text, resource) {
      console.log("FileEditor::encodeEditorText("+text.length+", "+resource+")");
      if(text) {
         var token = resource.toLowerCase();
         
         if(Common.stringEndsWith(token, ".json")) {
            try {
                var object = JSON.parse(text);
                return JSON.stringify(object, null, 3);
            }catch(e) {
              return text;
            }
         }
         return text;
      }
      return "";
   }
   
   export function resolveEditorMode(resource) {
      console.log("FileEditor::resolveEditorMode("+resource+")");
      var token = resource.toLowerCase();
      
      if(Common.stringEndsWith(token, ".snap")) {
         return "ace/mode/snapscript";
      }
      if(Common.stringEndsWith(token, ".xml")) {
         return "ace/mode/xml";
      }
      if(Common.stringEndsWith(token, ".json")) {
         return "ace/mode/json";
      }
      if(Common.stringEndsWith(token, ".sql")) {
         return "ace/mode/sql";
      }
      if(Common.stringEndsWith(token, ".js")) {
         return "ace/mode/javascript";
      }
      if(Common.stringEndsWith(token, ".html")) {
         return "ace/mode/html";
      }
      if(Common.stringEndsWith(token, ".htm")) {
         return "ace/mode/html";
      }
      if(Common.stringEndsWith(token, ".txt")) {
         return "ace/mode/text";
      }
      if(Common.stringEndsWith(token, ".properties")) {
         return "ace/mode/properties";
      }
      if(Common.stringEndsWith(token, ".gitignore")) {
         return "ace/mode/text";
      }
      if(Common.stringEndsWith(token, ".project")) {
         return "ace/mode/xml";
      }
      if(Common.stringEndsWith(token, ".classpath")) {
         return "ace/mode/xml";
      }
      return "ace/mode/text";
   }
   
   function indexEditorTokens(text, resource) { // create dynamic hyperlinks
      console.log("FileEditor::indexEditorTokens("+text.length+", "+resource+")");
      var token = resource.toLowerCase();
      var functionRegex = /(function|static|public|private|abstract|override|)\s+([a-z][a-zA-Z0-9]*)\s*\(/g;
      var variableRegex = /(var|const)\s+([a-z][a-zA-Z0-9]*)/g;
      var classRegex = /(class|trait|enum)\s+([A-Z][a-zA-Z0-9]*)/g;
      var importRegex = /import\s+([a-z][a-zA-Z0-9\.]*)\.([A-Z][a-zA-Z]*)/g;
      var tokenList = {};
      
      if(Common.stringEndsWith(token, ".snap")) {
         var lines = text.split(/\r?\n/);
         
         for(var i = 0; i < lines.length; i++) {
            var line = lines[i];
            
            indexEditorLine(line, i+1, functionRegex, tokenList, ["%s("], false);
            indexEditorLine(line, i+1, variableRegex, tokenList, ["%s.", "%s=", "%s =", "%s<", "%s <", "%s>", "%s >", "%s!", "%s !", "%s-", "%s -", "%s+", "%s +", "%s*", "%s *", "%s%", "%s %", "%s/", "%s /"], false);     
            indexEditorLine(line, i+1, importRegex, tokenList, ["new %s(", "%s.", ":%s", ": %s", "extends %s", "with %s", "extends  %s", "with  %s", ".%s;", " as %s", "%s["], true);  
            indexEditorLine(line, i+1, classRegex, tokenList, ["new %s(", "%s.", ":%s", ": %s", "extends %s", "with %s", "extends  %s", "with  %s", ".%s;", " as %s", "%s["], false); 
         }
      }
      editorCurrentTokens = tokenList; // keep these tokens for indexing
      
      if(editorFocusToken != null) {
         var focusToken = editorCurrentTokens[editorFocusToken];
         
         if(focusToken != null) {
            setTimeout(function() { // delay to allow the editor to complete
                                    // loading
               showEditorLine(focusToken.line);  // focus on the line there
                                                   // was a token
            }, 100);
            editorFocusToken = null; // clear for next open
         }
      }
   }
   
   function indexEditorLine(line, number, expression, tokenList, templates, external) {
      console.log("FileEditor::indexEditorLine("+line+", "+number+", "+expression+", "+tokenList+", "+templates+", "+external+")");
      expression.lastIndex = 0; // you have to reset regex to its start
                                 // position
      var tokens = expression.exec(line);
   
      if(tokens != null && tokens.length >0){
         var resourceToken = tokens[1]; // only for 'import' which is external
         var indexToken = tokens[2];
         
         for(var i = 0; i < templates.length; i++) {
            var template = templates[i];
            var indexKey = template.replace("%s", indexToken);
            
            if(external) { // 
               tokenList[indexKey] = {
                  resource: "/" + resourceToken.replace(".", "/") + ".snap",
                  line: number // save the line number
               };
            }else {
               tokenList[indexKey] = {
                  resource: null,
                  line: number // save the line number
               };
            }
         }
      }
   }
   
   function saveEditorHistory() {
      console.log("FileEditor::saveEditorHistory()");
      var editorData = loadEditor();
      
      if(editorData.resource && editorData.source) {
         var md5Hash = md5(editorData.source);
         
         editorHistory[editorData.resource.resourcePath] = {
            hash: md5Hash,
            history: editorData.history
         };
      }
   }
   
   function createEditorUndoManager(session, text, resource) {
      console.log("FileEditor::createEditorUndoManager("+(typeof session)+", "+text.length+", "+resource+")");
      var manager = new ace.UndoManager();
      
      if(text && resource) {
         var editorResource = FileTree.createResourcePath(resource);
         var history = editorHistory[editorResource.resourcePath];
         
         if(history) {
            var md5Hash = md5(text);
            
            if(history.hash == md5Hash) {
               var undoStack = history.history.undoStack;
               var redoStack = history.history.redoStack;
               
               for (var undoEntry in undoStack) {
                  if (undoStack.hasOwnProperty(undoEntry)) {
                     manager.$undoStack[undoEntry] = undoStack[undoEntry];
                  }
               }
               for (var redoEntry in redoStack) {
                  if (redoStack.hasOwnProperty(redoEntry)) {
                     manager.$redoStack[redoEntry] = undoStack[redoEntry];
                  }
               }
               manager.$doc = session;
               manager.dirtyCounter = history.history.dirtyCounter;
            } else {
               editorHistory[editorResource.resourcePath] = null;
            }
         }  
      }
      session.setUndoManager(manager); // reset undo history
   }
   
   export function updateEditor(text, resource) {
      console.log("FileEditor::updateEditor("+text.length+", "+resource+")");
      var editor = ace.edit("editor");
      var session = editor.getSession();
      var currentMode = session.getMode();
      var actualMode = resolveEditorMode(resource);
      
      text = encodeEditorText(text, resource); // change JSON conversion
      
      saveEditorHistory(); // save any existing history
      
      if(actualMode != currentMode) {
         session.setMode({
            path: actualMode,
            v: Date.now() 
         })
      }
      editor.setReadOnly(false);
      editor.setValue(text, 1);
      createEditorUndoManager(session, text, resource); // restore any existing history
      
      clearEditor();
      scrollEditorToTop();
      editorResource = FileTree.createResourcePath(resource);
      editorMarkers = {};
      editorText = text;
      window.location.hash = editorResource.projectPath; // update # anchor
      ProblemManager.highlightProblems(); // higlight problems on this resource
      
      if (resource != null) {
         var breakpoints = editorBreakpoints[editorResource.filePath];
   
         if (breakpoints != null) {
            for ( var lineNumber in breakpoints) {
               if (breakpoints.hasOwnProperty(lineNumber)) {
                  if (breakpoints[lineNumber] == true) {
                     setEditorBreakpoint(lineNumber - 1, true);
                  }
               }
            }
         }
      }
      indexEditorTokens(text, resource); // create some tokens we can link to dynamically
      Project.createEditorTab(); // update the tab name
      History.showFileHistory(); // update the history
      StatusPanel.showActiveFile(editorResource.projectPath);  
      FileEditor.showEditorFileInTree();
   }
   
   export function showEditorFileInTree() {
      console.log("FileEditor::showEditorFileInTree()");
      var editorData = loadEditor();
      var resourcePath = editorData.resource;
      
      FileTree.showTreeNode('explorerTree', resourcePath);
   }
   
   export function getSelectedText() {
      console.log("FileEditor::getSelectedText()");
      var editor = ace.edit("editor");
      return editor.getSelectedText();
   }
   
   export function isEditorChanged() {
      console.log("FileEditor::isEditorChanged()");
      if(editorResource != null) {
         var editor = ace.edit("editor");
         var text = editor.getValue();
      
         return text != editorText;
      }
      return false;
   }
   
   function scrollEditorToTop() {
      console.log("FileEditor::scrollEditorToTop()");
      var editor = ace.edit("editor");
      var session = editor.getSession();
      session.setScrollTop(0);
   }
   
   function createEditorAutoComplete() {
      console.log("FileEditor::createEditorAutoComplete()");
      return {
         getCompletions: function createAutoComplete(editor, session, pos, prefix, callback) {
             if (prefix.length === 0) { 
                callback(null, []); 
                return; 
             }
             var text = editor.getValue();
             var line = editor.session.getLine(pos.row);
             var complete = line.substring(0, pos.column - prefix.length);
             var message = JSON.stringify({
                resource: editorResource.projectPath,
                line: pos.row,
                complete: complete,
                source: text,
                prefix: prefix
             });
             $.ajax({
                contentType: 'application/json',
                data: message,
                dataType: 'json',
                success: function(response){
                   var tokens = response.tokens;
                   var length = tokens.length;
                   var suggestions = [];
                   
                   for(var token in tokens) {
                      if (tokens.hasOwnProperty(token)) {
                         var type = tokens[token];
                         suggestions.push({name: token, value: token, score: 300, meta: type });
                      }
                   }
                   callback(null, suggestions);
                },
                error: function(){
                    console.log("Completion control failed");
                },
                processData: false,
                type: 'POST',
                url: '/complete/' + document.title
            });
         }
      }
   }
   
   export function formatEditorSource() {
      console.log("FileEditor::formatEditorSource()");
      var editor = ace.edit("editor");
      var text = editor.getValue();
      $.ajax({
         contentType: 'text/plain',
         data: text,
         success: function(result){
            editor.setReadOnly(false);
            editor.setValue(result, 1);
         },
         error: function(){
             console.log("Format failed");
         },
         processData: false,
         type: 'POST',
         url: '/format/' + document.title
     });
   }
   
// function registerEditorBindings() {
// var editor = ace.edit("editor");
// editor.commands.addCommand({
// name : 'run',
// bindKey : {
// win : 'Ctrl-R',
// mac : 'Command-R'
// },
// exec : function(editor) {
// Command.runScript();
// },
// readOnly : true
// // false if this command should not apply in readOnly mode
// });
// editor.commands.addCommand({
// name : 'save',
// bindKey : {
// win : 'Ctrl-S',
// mac : 'Command-S'
// },
// exec : function(editor) {
// Command.saveFile();
// },
// readOnly : true
// // false if this command should not apply in readOnly mode
// });
// editor.commands.addCommand({
// name : 'new',
// bindKey : {
// win : 'Ctrl-N',
// mac : 'Command-N'
// },
// exec : function(editor) {
// Command.newFile(null);
// },
// readOnly : true
// // false if this command should not apply in readOnly mode
// });
// editor.commands.addCommand({
// name : 'format',
// bindKey : {
// win : 'Ctrl-Shift-F',
// mac : 'Command-Shift-F'
// },
// exec : function(editor) {
// formatEditorSource();
// },
// readOnly : true
// // false if this command should not apply in readOnly mode
// });
// editor.commands.addCommand({
// name: 'find',
// bindKey: {
// win: 'Ctrl-Shift-S',
// mac: 'Command-Shift-S'
// },
// exec: function (editor) {
// Command.searchTypes();
// },
// readOnly: true
// });
// }
   
   export function setEditorTheme(theme) {
      console.log("FileEditor::setEditorTheme("+theme+")");
      if(theme != null){
         var editor = ace.edit("editor");
         
         if(editor != null) {
            editor.setTheme(theme);
         }
         editorTheme = theme;
      }
   }
   
//   export function undoEditorChange() {
//      var editor = ace.edit("editor");
//      editor.getSession().getUndoManager().undo(true);
//   }
//   
//   export function redoEditorChange() {
//      var editor = ace.edit("editor");
//      editor.getSession().getUndoManager().redo(true);
//   }
   
   export function showEditor() {
      console.log("FileEditor::showEditor()");
      var editor = ace.edit("editor");
      var autoComplete = createEditorAutoComplete();
      
      if(editorTheme != null) {
         editor.setTheme(editorTheme);
      }
      editor.completers = [autoComplete];
      // setEditorTheme("eclipse"); // set the default to eclipse
      
      editor.getSession().setMode("ace/mode/snapscript");
      editor.getSession().setTabSize(3);
      editor.setReadOnly(false);
      editor.setAutoScrollEditorIntoView(true);
      editor.getSession().setUseSoftTabs(true);
      
      editor.commands.removeCommand("replace"); // Ctrl-H
      editor.commands.removeCommand("find");    // Ctrl-F
      editor.commands.removeCommand("expandToMatching"); // Ctrl-Shift-M
      editor.commands.removeCommand("expandtoline"); // Ctrl-Shift-L
      
      // ################# DISABLE KEY BINDINGS ######################
      // editor.keyBinding.setDefaultHandler(null); // disable all keybindings
      // and allow Mousetrap to do it
      // #############################################################
      
      editor.setShowPrintMargin(false);
      editor.setOptions({
         enableBasicAutocompletion: true
      });
      editor.on("guttermousedown", function(e) {
         var target = e.domEvent.target;
         if (target.className.indexOf("ace_gutter-cell") == -1) {
            return;
         }
         if (!editor.isFocused()) {
            return;
         }
         if (e.clientX > 25 + target.getBoundingClientRect().left) {
            return;
         }
         var row = e.getDocumentPosition().row;
         // should be a getBreakpoints but does not seem to be there!!
         toggleEditorBreakpoint(row);
         e.stop()
      });
      
      //
      // THIS IS THE LINKS
      //
      
      //createEditorLinks(editor, validEditorLink, openEditorLink); // link.js
      KeyBinder.bindKeys(); // register key bindings
      // registerEditorBindings();
      Project.changeProjectFont(); // project.js update font
      scrollEditorToTop();
      LoadSpinner.finish();
      
      // JavaFX has a very fast scroll speed
      if(typeof java !== 'undefined') {
         editor.setScrollSpeed(0.05); // slow down if its Java FX
      }
   }
   
   function validEditorLink(string, col) { // see link.js
      console.log("FileEditor::validEditorLink("+string+", "+col+")"); // (http://jsbin.com/jehopaja/4/edit?html,output)
      if(KeyBinder.isControlPressed()) {
         var tokenPatterns = [
            "\\.[A-Z][a-zA-Z0-9]*;", // import type
            "\\sas\\s+[A-Z][a-zA-Z0-9]*;", // import alias
            "[a-zA-Z][a-zA-Z0-9]*\\s*\\.", // variable or type reference
            "[a-z][a-zA-Z0-9]*\\s*[=|<|>|!|\-|\+|\*|\\/|%]", // variable
                                                               // operation
            "new\\s+[A-Z][a-zA-Z0-9]*\\s*\\(", // constructor call
            "[a-zA-Z][a-zA-Z0-9]*\\s*\\(", // function or constructor call
            "[A-Z][a-zA-Z0-9]*\\s*\\[", // type array reference
            ":\\s*[A-Z][a-zA-Z0-9]*", // type constraint
            "extends\\s+[A-Z][a-zA-Z0-9]*", // super class
            "with\\s+[A-Z][a-zA-Z0-9]*" // implements trait
         ];
         for(var i = 0; i < tokenPatterns.length; i++) { 
            var regExp = new RegExp(tokenPatterns[i], 'g'); // WE SHOULD CACHE
                                                            // THE REGEX FOR
                                                            // PERFORMANCE
            var matchFound = null;
            regExp.lastIndex = 0; // you have to reset regex to its start
                                    // position
            
            string.replace(regExp, function(str) {
                var offset = arguments[arguments.length - 2];
                var length = str.length;
                if (offset <= col && offset + length >= col) {
                   var indexToken = editorCurrentTokens[str];
                   
                   if(indexToken != null) {
                      matchFound = {
                         start: offset,
                         value: str
                      };
                   }
                }
            });
            if(matchFound != null) {
               return matchFound;
            }
         }
      }
      return null;
   }
   
   function openEditorLink(event) {
      console.log("FileEditor::openEditorLink("+event+")"); 
      var editor = ace.edit("editor");
      if(KeyBinder.isControlPressed()) {
         var indexToken = editorCurrentTokens[event.value];
         
         if(indexToken != null) {
            if(indexToken.resource != null) {
               editorFocusToken = event.value;
               window.location.hash = indexToken.resource;
            }else {
               showEditorLine(indexToken.line); 
            }
            // alert("Editor open ["+event.value+"] @ "+line);
         }
      }
   }
   
   export function updateEditorFont(fontFamily, fontSize) {
      console.log("FileEditor::updateEditorFont("+fontFamily+", " + fontSize +")"); 
      var editor = ace.edit("editor");
      var autoComplete = createEditorAutoComplete();
      
      editor.completers = [autoComplete];
      editor.setOptions({
         enableBasicAutocompletion: true,
         fontFamily: "'"+fontFamily+"',monospace",
         fontSize: fontSize
      });
   }
}

//ModuleSystem.registerModule("editor", "Editor module: editor.js", null, FileEditor.createEditor, [ "common", "spinner", "tree" ]);
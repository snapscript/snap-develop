var editorBreakpoints = {}; // spans multiple resources
var editorMarkers = {};
var editorResource = null;
var editorText = null;

function createEditor() {
   window.setTimeout(showEditor, 400);
   createTermination(clearEditorHighlights); // create callback
}

function clearEditorHighlights() {
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

function showEditorLine(line) {
   var editor = ace.edit("editor");
   
   editor.resize(true);
   
   if(line > 1) {
      editor.scrollToLine(line - 1, true, true, function () {})
   } else {
      editor.scrollToLine(0, true, true, function () {})
   }
}

function clearEditorHighlight(line) {
   var editor = ace.edit("editor");
   var session = editor.getSession();
   var marker = editorMarkers[line];
   
   if(marker != null) {
      session.removeMarker(marker);
   }
}

function createEditorHighlight(line, css) {
   var editor = ace.edit("editor");
   var Range = ace.require('ace/range').Range;
   var session = editor.getSession();

   clearEditorHighlight(line);
   // session.addMarker(new Range(from, 0, to, 1), "errorMarker", "fullLine");
   var marker = session.addMarker(new Range(line - 1, 0, line - 1, 1), css, "fullLine");
   editorMarkers[line] = marker;
}

function clearEditorBreakpoint(row) {
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
   var editor = ace.edit("editor");
   var session = editor.getSession();
   var breakpoints = session.getBreakpoints();
   var remove = false;

   for ( var breakpoint in breakpoints) {
      session.clearBreakpoint(row);
   }
}

function showEditorBreakpoints() {
   var breakpointRecords = [];
   var breakpointIndex = 1;

   for ( var filePath in editorBreakpoints) {
      if (editorBreakpoints.hasOwnProperty(filePath)) {
         var breakpoints = editorBreakpoints[filePath];

         for ( var lineNumber in breakpoints) {
            if (breakpoints.hasOwnProperty(lineNumber)) {
               if (breakpoints[lineNumber] == true) {
                  var resourcePathDetails = createResourcePath(filePath);
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
   updateScriptBreakpoints(); // update the breakpoints
}

function setEditorBreakpoint(row, value) {
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

function resetEditor() {
   var editor = ace.edit("editor");
   var session = editor.getSession();

   editorMarkers = {};
   editorResource = null;
   editor.setReadOnly(true);
   session.setValue(editorText, 1);
   $("#currentFile").html("");
}

function clearEditor() {
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

function loadEditor() {
   var editor = ace.edit("editor");
   var text = editor.getValue();

   return {
      breakpoints : editorBreakpoints,
      resource : editorResource,
      source : text
   };
}

function encodeEditorText(text, resource) {
   var token = resource.toLowerCase();
   
   if(token.endsWith(".json")) {
      try {
          var object = JSON.parse(text);
          return JSON.stringify(object, null, 3);
      }catch(e) {
        return text;
      }
   }
   return text;
}

function resolveEditorMode(resource) {
   var token = resource.toLowerCase();
   
   if(token.endsWith(".snap")) {
      return "ace/mode/snapscript";
   }
   if(token.endsWith(".xml")) {
      return "ace/mode/xml";
   }
   if(token.endsWith(".json")) {
      return "ace/mode/json";
   }
   if(token.endsWith(".sql")) {
      return "ace/mode/sql";
   }
   if(token.endsWith(".js")) {
      return "ace/mode/javascript";
   }
   if(token.endsWith(".html")) {
      return "ace/mode/html";
   }
   if(token.endsWith(".htm")) {
      return "ace/mode/html";
   }
   if(token.endsWith(".txt")) {
      return "ace/mode/text";
   }
   if(token.endsWith(".properties")) {
      return "ace/mode/properties";
   }
   if(token.endsWith(".gitignore")) {
      return "ace/mode/text";
   }
   if(token.endsWith(".project")) {
      return "ace/mode/xml";
   }
   if(token.endsWith(".classpath")) {
      return "ace/mode/xml";
   }
   return null;
}

function updateEditor(text, resource) {
   var editor = ace.edit("editor");
   var session = editor.getSession();
   var currentMode = session.getMode();
   var actualMode = resolveEditorMode(resource);
   var text = encodeEditorText(text, resource); // change JSON conversion
   
   if(actualMode != currentMode) {
      session.setMode({
         path: actualMode,
         v: Date.now() 
      })
   }
   var manager = new ace.UndoManager();

   editor.setReadOnly(false);
   editor.setValue(text, 1);
   editor.getSession().setUndoManager(manager); // clear undo history
   clearEditor();
   scrollEditorToTop();
   editorResource = createResourcePath(resource);
   editorMarkers = {};
   editorText = text;
   window.location.hash = editorResource.projectPath; // update # anchor
   highlightProblems(); // higlight problems on this resource
   
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
   $("#currentFile").html("File:&nbsp;"+editorResource.projectPath+"&nbsp;&nbsp;");
}

function isEditorChanged() {
   if(editorResource != null) {
      var editor = ace.edit("editor");
      var text = editor.getValue();
   
      return text != editorText;
   }
   return false;
}

function scrollEditorToTop() {
   var editor = ace.edit("editor");
   var session = editor.getSession();
   session.setScrollTop(0);
}

function createEditorAutoComplete() {
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

function formatEditorSource() {
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

function registerEditorBindings() {
   var editor = ace.edit("editor");
   editor.commands.addCommand({
      name : 'run',
      bindKey : {
         win : 'Ctrl-R',
         mac : 'Command-R'
      },
      exec : function(editor) {
         runScript();
      },
      readOnly : true
   // false if this command should not apply in readOnly mode
   });
   editor.commands.addCommand({
      name : 'save',
      bindKey : {
         win : 'Ctrl-S',
         mac : 'Command-S'
      },
      exec : function(editor) {
         saveFile();
      },
      readOnly : true
   // false if this command should not apply in readOnly mode
   });
   editor.commands.addCommand({
      name : 'new',
      bindKey : {
         win : 'Ctrl-N',
         mac : 'Command-N'
      },
      exec : function(editor) {
         newFile(null);
      },
      readOnly : true
   // false if this command should not apply in readOnly mode
   });
   editor.commands.addCommand({
      name : 'format',
      bindKey : {
         win : 'Ctrl-Shift-F',
         mac : 'Command-Shift-F'
      },
      exec : function(editor) {
         formatEditorSource();
      },
      readOnly : true
   // false if this command should not apply in readOnly mode
   });
   editor.commands.addCommand({
       name: 'find',
       bindKey: {
           win: 'Ctrl-Shift-S',
           mac: 'Command-Shift-S'
       },
       exec: function (editor) {
            searchTypes();
       },
       readOnly: true
   });    
}

function showEditor() {
   var langTools = ace.require("ace/ext/language_tools");
   var editor = ace.edit("editor");
   var autoComplete = createEditorAutoComplete();
   
   editor.completers = [autoComplete];
   editor.getSession().setMode("ace/mode/snapscript");
   editor.getSession().setTabSize(3);
   editor.setReadOnly(true);
   editor.getSession().setUseSoftTabs(true);
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
   registerEditorBindings();
   changeProjectFont(); // project.js update font
   scrollEditorToTop();
   finishedLoading();
}

function updateEditorFont(fontFamily, fontSize) {
   var langTools = ace.require("ace/ext/language_tools");
   var editor = ace.edit("editor");
   var autoComplete = createEditorAutoComplete();
   
   editor.completers = [autoComplete];
   editor.setOptions({
      enableBasicAutocompletion: true,
      fontFamily: fontFamily,
      fontSize: fontSize
    });
}

registerModule("editor", "Editor module: editor.js", createEditor, [ "common", "spinner", "tree" ]);
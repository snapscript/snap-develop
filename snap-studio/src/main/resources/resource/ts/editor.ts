import * as $ from "jquery"
import {md5} from "md5"
import {ace} from "ace"
import {w2ui, w2popup} from "w2ui"
import {Common} from "common"
import {EventBus} from "socket"
import {ProcessConsole} from "console"
import {ProblemManager} from "problem"
import {LoadSpinner} from "spinner"
import {FileTree, FilePath} from "tree"
import {ThreadManager} from "threads"
import {History} from "history"
import {VariableManager} from "variables"
import {Project} from "project"
import {StatusPanel} from "status"
import {KeyBinder} from "keys"
import {Command} from "commands"
import {FileResource} from "explorer"

/**
 * Contains the state for the Ace editor and is a singleton instance
 * that exists as soon as the editor is created.
 */
export class FileEditorView {
   
   editorBreakpoints = {}; // spans multiple resources
   editorMarkers = {};
   editorResource: FilePath = null;
   editorText: string = null;
   editorLastModified = null;
   editorTheme: string = null;
   editorCurrentTokens = {}; // current editor hyperlinks
   editorFocusToken = null; // token to focus on editor load
   editorHistory = {}; // store all editor context
   editorPanel = null; // this actual editor
   
   constructor(editorPanel: any) {
      this.editorLastModified = new Date().getTime();
      this.editorPanel = editorPanel;
   }
   
   init() {
      KeyBinder.bindKeys(); // register key bindings
      Project.changeProjectFont(); // project.js update font
      FileEditor.scrollEditorToPosition();
      FileEditor.updateProjectTabOnChange(); // listen to change
   }
}

export class FileEditorHistory {

   private _undoState: FileEditorUndoState;
   private _position: FileEditorPosition;
   private _lastModified: number;
   private _buffer: string; // save the buffer if it has changed
   private _hash: string;
   
   constructor(lastModified: number, undoState: FileEditorUndoState, position: FileEditorPosition, buffer: string, hash: string) {
      this._lastModified = lastModified;
      this._undoState = undoState;
      this._position = position;
      this._buffer = buffer;
      this._hash = hash;
   }
   
   public getPosition(): FileEditorPosition {
      return this._position;
   }
   
   public getUndoState(): FileEditorUndoState {
      return this._undoState;
   }
   
   public getLastModified(): number {
      return this._lastModified;
   }
   
   public getBuffer(): string {
      return this._buffer;
   }
   
   public getHash(): string {
      return this._hash;
   }
}

export class FileEditorUndoState {

   private _undoStack: any;
   private _redoStack: any;
   private _dirtyCounter: any;

   constructor(undoStack: any, redoStack: any, dirtyCounter: any) {
      this._undoStack = undoStack;
      this._redoStack = redoStack;
      this._dirtyCounter = dirtyCounter;
   }
   
   public getUndoStack(): any {
      return this._undoStack;
   }
   
   public getRedoStack(): any {
      return this._redoStack;
   }
   
   public getDirtyCounter(): any {
      return this._dirtyCounter;
   }
}

export class FileEditorState {
   
   private _undoState : FileEditorUndoState;
   private _position: FileEditorPosition;
   private _lastModified: number;
   private _breakpoints: any;
   private _resource : FilePath;
   private _source : string;
   
   constructor(lastModified: number, breakpoints: any, resource: FilePath, undoState: FileEditorUndoState, position: FileEditorPosition, source: string) {
      this._lastModified = lastModified;
      this._breakpoints = breakpoints;
      this._resource = resource;
      this._undoState = undoState;
      this._position = position;
      this._source = source;
   }
   
   public isStateValid(): boolean {
      return this._resource && (this._source != null && this._source != "");      
   }
   
   public getResource(): FilePath {
      return this._resource;
   }
   
   public getPosition(): FileEditorPosition {
      return this._position;
   }
   
   public getUndoState(): FileEditorUndoState {
      return this._undoState;
   }
   
   public getLastModified(): number {
      return this._lastModified;
   }
   
   public getBreakpoints(): any {
      return this._breakpoints;
   }   
   
   public getSource(): string {
      return this._source;
   }
  
}

export class FileEditorPosition {
   
   private _row: number;
   private _column: number;
   private _scroll: number;

   constructor(row: number, column: number, scroll: number) {
      this._row = row;
      this._column = column;
      this._scroll = scroll;
   }
   
   public getRow(): number {
      return this._row;
   }
   
   public getColumn(): number {
      return this._column;
   }
   
   public getScroll(): number {
      return this._scroll;
   }
}


/**
 * Groups all the editor functions and creates the FileEditorView that
 * contains the state of the editor session. 
 */
export module FileEditor {

   var editorView: FileEditorView = null;
   
   export function createEditor() {
      editorView = showEditor();
      editorView.init();
      EventBus.createTermination(clearEditorHighlights); // create callback
   }
   
   export function clearEditorHighlights() {
      var session = editorView.editorPanel.getSession();
       
      for (var editorLine in editorView.editorMarkers) {
         if (editorView.editorMarkers.hasOwnProperty(editorLine)) {
            var marker = editorView.editorMarkers[editorLine];
            
            if(marker != null) {
               session.removeMarker(marker);
            }
         }
      }
      editorView.editorMarkers = {};
   }
   
   export function showEditorLine(line) {
      var editor = editorView.editorPanel;
      
      editorView.editorPanel.resize(true);
      
      if(line > 1) {
         editorView.editorPanel.scrollToLine(line - 1, true, true, function () {})
         editorView.editorPanel.gotoLine(line); // move the cursor
      } else {
         editorView.editorPanel.scrollToLine(0, true, true, function () {})
      }
      editorView.editorPanel.focus();
   }
   
   function clearEditorHighlight(line) {
      var session = editorView.editorPanel.getSession();
      var marker = editorView.editorMarkers[line];
      
      if(marker != null) {
         session.removeMarker(marker);
      }
   }
   
   export function createEditorHighlight(line, css) {
      var Range = ace.require('ace/range').Range;
      var session = editorView.editorPanel.getSession();
   
      // clearEditorHighlight(line);
      clearEditorHighlights(); // clear all highlights in editor

      var marker = session.addMarker(new Range(line - 1, 0, line - 1, 1), css, "fullLine");
      editorView.editorMarkers[line] = marker;
   }
   
   export function createMultipleEditorHighlights(lines, css) {
      var Range = ace.require('ace/range').Range;
      var session = editorView.editorPanel.getSession();
   
      // clearEditorHighlight(line);
      clearEditorHighlights(); // clear all highlights in editor

      for(var i = 0; i < lines.length; i++) {
         var line = lines[i];
         var marker = session.addMarker(new Range(line - 1, 0, line - 1, 1), css, "fullLine");
         editorView.editorMarkers[line] = marker;
      }
   }
   
   export function findAndReplaceTextInEditor(){
      const state: FileEditorState = currentEditorState();
      const resource: FilePath = state.getResource();
   
      Command.searchAndReplaceFiles(resource.getProjectPath());
   }
   
   export function findTextInEditor() {
      const state: FileEditorState = currentEditorState();
      const resource: FilePath = state.getResource();
   
      Command.searchFiles(resource.getProjectPath());
   }
   
   export function addEditorKeyBinding(keyBinding, actionFunction) {
      editorView.editorPanel.commands.addCommand({
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
   
   function clearEditorBreakpoint(row) {
      var session = editorView.editorPanel.getSession();
      var breakpoints = session.getBreakpoints();
      var remove = false;
   
      for(var breakpoint in breakpoints) {
         session.clearBreakpoint(row);
      }
      showEditorBreakpoints();
   }
   
   function clearEditorBreakpoints() {
      var session = editorView.editorPanel.getSession();
      var breakpoints = session.getBreakpoints();
      var remove = false;
   
      for(var breakpoint in breakpoints) {
         session.clearBreakpoint(breakpoint); // XXX is this correct
      }
   }
   
   export function showEditorBreakpoints() {
      var breakpointRecords = [];
      var breakpointIndex = 1;
   
      for(var filePath in editorView.editorBreakpoints) {
         if(editorView.editorBreakpoints.hasOwnProperty(filePath)) {
            var breakpoints = editorView.editorBreakpoints[filePath];
   
            for(var lineNumber in breakpoints) {
               if (breakpoints.hasOwnProperty(lineNumber)) {
                  if (breakpoints[lineNumber] == true) {
                     var resourcePathDetails: FilePath = FileTree.createResourcePath(filePath);
                     var displayName = "<div class='breakpointEnabled'>"+resourcePathDetails.getProjectPath()+"</div>";
                     
                     breakpointRecords.push({
                        recid: breakpointIndex++,
                        name: displayName,
                        location : "Line " + lineNumber,
                        resource : resourcePathDetails.getProjectPath(),
                        line: parseInt(lineNumber),
                        script : resourcePathDetails.getResourcePath()
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
      if (editorView.editorResource != null) {
         var session = editorView.editorPanel.getSession();
         var resourceBreakpoints = editorView.editorBreakpoints[editorView.editorResource.getFilePath()];
         var line = parseInt(row);
   
         if (value) {
            session.setBreakpoint(line);
         } else {
            session.clearBreakpoint(line);
         }
         if (resourceBreakpoints == null) {
            resourceBreakpoints = {};
            editorView.editorBreakpoints[editorView.editorResource.getFilePath()] = resourceBreakpoints;
         }
         resourceBreakpoints[line + 1] = value;
      }
      showEditorBreakpoints();
   }
   
   function toggleEditorBreakpoint(row) {
      if (editorView.editorResource != null) {
         var session = editorView.editorPanel.getSession();
         var resourceBreakpoints = editorView.editorBreakpoints[editorView.editorResource.getFilePath()];
         var breakpoints = session.getBreakpoints();
         var remove = false;
   
         for(var breakpoint in breakpoints) {
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
            editorView.editorBreakpoints[editorView.editorResource.getFilePath()] = resourceBreakpoints;
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
      var width = document.getElementById('editor').offsetWidth;
      var height = document.getElementById('editor').offsetHeight;
      
      console.log("Resize editor " + width + "x" + height);
      editorView.editorPanel.setAutoScrollEditorIntoView(true);
      editorView.editorPanel.resize(true);
      // editor.focus();
   }
   
   export function resetEditor() {
      var session = editorView.editorPanel.getSession();
   
      clearEditorHighlights();
      editorView.editorResource = null;
      editorView.editorPanel.setReadOnly(false);
      session.setValue(editorView.editorText, 1);
      $("#currentFile").html("");
   }
   
   function clearEditor() {
      var session = editorView.editorPanel.getSession();
   
      for(var editorMarker in session.$backMarkers) { // what is this???
         session.removeMarker(editorMarker);
      }
      clearEditorHighlights(); // clear highlighting
      
      var breakpoints = session.getBreakpoints();
      var remove = false;
   
      for(var breakpoint in breakpoints) {
         session.clearBreakpoint(breakpoint);
      }
      $("#currentFile").html("");
   }
   
   export function currentEditorState(): FileEditorState {
      var editorHistory: FileEditorUndoState = currentEditorUndoState();
      var editorPosition: FileEditorPosition = currentEditorPosition();
      var editorText = currentEditorText();
      
      return new FileEditorState(
         editorView.editorLastModified,
         editorView.editorBreakpoints,
         editorView.editorResource,
         editorHistory,
         editorPosition,
         editorText
      );
   }
   
   function currentEditorText(): string{
      return editorView.editorPanel.getValue();
   }
   
   function currentEditorPosition(): FileEditorPosition {
      var scrollTop = editorView.editorPanel.getSession().getScrollTop();
      var editorCursor = editorView.editorPanel.selection.getCursor();

      if(editorCursor) {
         return new FileEditorPosition(
               editorCursor.row,
               editorCursor.column,
               scrollTop
         );
      }
      return new FileEditorPosition( 
         null,
         null,
         scrollTop
      );
   }
   
   function currentEditorUndoState(): FileEditorUndoState {
      var session = editorView.editorPanel.getSession();
      var manager = session.getUndoManager();
      var undoStack = $.extend(true, {}, manager.$undoStack);
      var redoStack = $.extend(true, {}, manager.$redoStack);

      return new FileEditorUndoState(
         undoStack,
         redoStack,
         manager.dirtyCounter
      );
   }
   
   function encodeEditorText(text, resource) {
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
      if(Common.stringEndsWith(token, ".pl")) {
         return "ace/mode/perl";
      }
      if(Common.stringEndsWith(token, ".kt")) {
         return "ace/mode/kotlin";
      }
      if(Common.stringEndsWith(token, ".js")) {
         return "ace/mode/javascript";
      }
      if(Common.stringEndsWith(token, ".ts")) {
         return "ace/mode/typescript";
      }
      if(Common.stringEndsWith(token, ".java")) {
         return "ace/mode/java";
      }  
      if(Common.stringEndsWith(token, ".groovy")) {
         return "ace/mode/groovy";
      }  
      if(Common.stringEndsWith(token, ".py")) {
         return "ace/mode/python";
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
         return "ace/mode/text";
      }
      return "ace/mode/text";
   }
   
   function saveEditorHistory() {
      var editorState: FileEditorState = currentEditorState();
      
      if(editorState.isStateValid()) {
         var source = editorState.getSource();
         var md5Hash = md5(source);
         var currentText = editorView.editorPanel.getValue();
         var saveText = isEditorChanged() ? currentText : null; // keep text if changed
         
         editorView.editorHistory[editorState.getResource().getResourcePath()] = new FileEditorHistory(
            editorState.getLastModified(),
            editorState.getUndoState(),
            editorState.getPosition(),
            saveText, // save the buffer if it has changed
            md5Hash
         );
      }
   }
   
   function createEditorUndoManager(session, text: string, resource: string) {
      var manager = new ace.UndoManager();
      
      if(text && resource) {
         var editorResource: FilePath = FileTree.createResourcePath(resource);
         var history = editorView.editorHistory[editorResource.getResourcePath()];
         
         if(history) {
            var md5Hash = md5(text);
            
            if(history.hash == md5Hash) {
               var undoState: FileEditorUndoState = history.getUndoState();
               var undoStack = undoState.getUndoStack();
               var redoStack = undoState.getRedoStack();
               
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
               manager.dirtyCounter = undoState.getDirtyCounter();
            } else {
               editorView.editorHistory[editorResource.getResourcePath()] = null;
            }
         }  
      }
      session.setUndoManager(manager); // reset undo history
   }
   
   export function clearSavedEditorBuffer(resource: string) {
      var editorResource: FilePath = FileTree.createResourcePath(resource);
      var history = editorView.editorHistory[editorResource.getResourcePath()];
      
      if(history) {
         history.lastModified = -1;
         history.buffer = null; // clear the buffer
         updateEditorTabMarkForResource(editorResource.getResourcePath()); // remove the *
      }
   } 
   
   export function loadSavedEditorBuffer(resource: string) {
      var editorResource: FilePath = FileTree.createResourcePath(resource);
      
      if(isEditorResourcePath(editorResource.getResourcePath())) {
         var editorState: FileEditorState = currentEditorState();
         return {
            buffer: editorState.getSource(), // if its the current buffer then return it
            lastModified: editorState.getLastModified()
         };
      }
      var history = editorView.editorHistory[editorResource.getResourcePath()];
      
      if(history) {
         return {
            buffer: history.getBuffer(), // if its the current buffer then return it
            lastModified: history.getLastModified()
         };     
      }      
      return null;
   }
   
   function resolveEditorTextToUse(fileResource: FileResource) {
      console.log("resource=[" + fileResource.getResourcePath().getResourcePath() + "] modified=[" + fileResource.getTimeStamp() + "] length=[" + fileResource.getFileLength() + "]");
      
      var encodedText = encodeEditorText(fileResource.getFileContent(), fileResource.getResourcePath().getResourcePath()); // change JSON conversion
      var savedHistoryBuffer = loadSavedEditorBuffer(fileResource.getResourcePath().getResourcePath()); // load saved buffer

      if(savedHistoryBuffer && savedHistoryBuffer.buffer && savedHistoryBuffer.lastModified > fileResource.getLastModified()) {
         console.log("LOAD FROM HISTORY diff=[" + (savedHistoryBuffer.lastModified - fileResource.getLastModified()) + "]");
         return savedHistoryBuffer.buffer;
      }
      console.log("IGNORE HISTORY: ", savedHistoryBuffer);
      return encodedText;
   }
   
   /**
    * When a file is loaded from disk in to the editor it will check 
    * whether this file is a historical file or an actual file. If
    * the file is the actual file a new history item is created. This
    * will allow the editor to store history from the point of load
    * up to the point it is saved. The followingrules are observed.
    * 
    * a) If a loaded buffer is the same as the one in the buffer then
    *    the history can be maintained and the update is ignored. Two
    *    files are the same if they have the same content.
    *    
    * b) If the loaded buffer is the same as when the history started
    *    and also has the same timestamp as when the history started
    *    then the update can be ignored.
    *    
    * c) If there are no modifications to the current buffer and the
    *    incoming file is different then the history is discarded.
    *    
    * d) If there are modifications to the current buffer and the 
    *    incoming buffer is different then the user is prompted to 
    *    determine if they want to overwrite the buffer. If the accept
    *    the incoming file the history is discarded.         
    * 
    * Finally all breakpoints and errors are applied to the editor
    * that relate to the current file.
    * 
    * @param fileResource this is the incoming file    
    */
   export function updateEditor(fileResource: FileResource) { // why would you ever ignore an update here?
      var resourcePath: FilePath = fileResource.getResourcePath();
      var realText: string = fileResource.getFileContent();
      var textToDisplay = resolveEditorTextToUse(fileResource);
      var session = editorView.editorPanel.getSession();
      var currentMode = session.getMode();
      var actualMode = resolveEditorMode(resourcePath.getResourcePath());

      saveEditorHistory(); // save any existing history
      
      if(actualMode != currentMode) {
         session.setMode({
            path: actualMode,
            v: Date.now() 
         })
      }
      editorView.editorPanel.setReadOnly(false);
      editorView.editorPanel.setValue(textToDisplay, 1);
      createEditorUndoManager(session, textToDisplay, resourcePath.getResourcePath()); // restore any existing history
      clearEditor();
      editorView.editorResource = resourcePath;
      editorView.editorText = realText; // save the real text NOT the text to display
      window.location.hash = editorView.editorResource.getProjectPath(); // update # anchor
      ProblemManager.highlightProblems(); // higlight problems on this resource
      
      if (resourcePath != null && editorView.editorResource) {
         var breakpoints = editorView.editorBreakpoints[editorView.editorResource.getFilePath()];
   
         if (breakpoints != null) {
            for(var lineNumber in breakpoints) {
               if (breakpoints.hasOwnProperty(lineNumber)) {
                  if (breakpoints[lineNumber] == true) {
                     setEditorBreakpoint(parseInt(lineNumber) - 1, true);
                  }
               }
            }
         }
      }
      Project.createEditorTab(); // update the tab name
      History.showFileHistory(); // update the history
      StatusPanel.showActiveFile(editorView.editorResource.getProjectPath());  
      FileEditor.showEditorFileInTree();
      scrollEditorToPosition();
      updateEditorTabMark(); // add a * to the name if its not in sync
   }
   
   export function showEditorFileInTree() {
      var editorState: FileEditorState = currentEditorState();
      var resourcePath: FilePath = editorState.getResource();
      
      FileTree.showTreeNode('explorerTree', resourcePath);
   }
   
   export function getCurrentLineForEditor() {
      return editorView.editorPanel.getSelectionRange().start.row;
   }
   
   export function getSelectedText() {
      return editorView.editorPanel.getSelectedText();
   }
   
   export function isEditorChanged() {
      if(editorView.editorResource != null) {
         var text = editorView.editorPanel.getValue();
      
         return text != editorView.editorText;
      }
      return false;
   }
   
   export function isEditorChangedForPath(resource) {
      if(isEditorResourcePath(resource)) {
         return isEditorChanged();
      }
      var savedHistoryBuffer = loadSavedEditorBuffer(resource); // load saved buffer
      
      if(savedHistoryBuffer.buffer) {
         return true;
      }
      return false;
   }
   
   function isEditorResourcePath(resource) {
      if(editorView.editorResource != null) {
         if(editorView.editorResource.getResourcePath() == resource) {
            return true;
         }
      }
      return false;
   }
   
   export function scrollEditorToPosition() {
      var session = editorView.editorPanel.getSession();
      
      if(editorView.editorResource && editorView.editorResource.getResourcePath()) {
         var editorHistory: FileEditorHistory = editorView.editorHistory[editorView.editorResource.getResourcePath()];
         
         if(editorHistory && editorHistory.getPosition()) {
            const position: FileEditorPosition = editorHistory.getPosition();
            const scroll: number = position.getScroll();
            const row: number = position.getRow();
            const column: number = position.getColumn();
            
            if(row >= 0 && column >= 0) {
               editorView.editorPanel.selection.moveTo(row, column);
            } else {
               editorView.editorPanel.gotoLine(1);
            }
            session.setScrollTop(scroll); 
         } else {
            editorView.editorPanel.gotoLine(1); 
            session.setScrollTop(0);
         }
      }else {
         editorView.editorPanel.gotoLine(1);// required for focus
         session.setScrollTop(0); 
      }
      editorView.editorPanel.focus();
   }
   
   export function updateProjectTabOnChange() {
      editorView.editorPanel.on("input", function() {
         editorView.editorLastModified = new Date().getTime();
         updateEditorTabMark(); // on input then you update star
     });
   }
   
   function updateEditorTabMark() {
      updateEditorTabMarkForResource(editorView.editorResource.getResourcePath());
   }
   
   function updateEditorTabMarkForResource(resource) {
      Project.markEditorTab(resource, isEditorChangedForPath(resource));
   }
   
   function createEditorAutoComplete() {
      return {
         getCompletions: function createAutoComplete(editor, session, pos, prefix, callback) {
//             if (prefix.length === 0) { 
//                callback(null, []); 
//                return; 
//             }
             var text = editor.getValue();
             var line = editor.session.getLine(pos.row);
             var resource = editorView.editorResource.getProjectPath();
             var complete = line.substring(0, pos.column);
             var message = JSON.stringify({
                resource: resource,
                line: pos.row + 1,
                complete: complete,
                source: text,
                prefix: prefix
             });
             $.ajax({
                contentType: 'application/json',
                data: message,
                dataType: 'json',
                success: function(response){
                   var expression = response.expression;
                   var dotIndex = Math.max(0, expression.lastIndexOf('.') + 1);                   
                   var tokens = response.tokens;
                   var length = tokens.length;
                   var suggestions = [];
                   
                   for(var token in tokens) {
                      if (tokens.hasOwnProperty(token)) {
                         var type = tokens[token];
                         
                         if(Common.stringStartsWith(token, expression)) {
                            token = token.substring(dotIndex);
                         }
                         suggestions.push({className: 'autocomplete_' + type, token: token, value: token, score: 300, meta: type });
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
   
   // XXX this should be in commands
   export function formatEditorSource() {
      var text: string = editorView.editorPanel.getValue();
      var path: string = editorView.editorResource.getFilePath();
      
      $.ajax({
         contentType: 'text/plain',
         data: text,
         success: function(result){
            editorView.editorPanel.setReadOnly(false);
            editorView.editorPanel.setValue(result, 1);
         },
         error: function(){
             console.log("Format failed");
         },
         processData: false,
         type: 'POST',
         url: '/format/' + document.title + path
     });
   }
   
   export function setEditorTheme(theme) {
      if(theme != null){
         if(editorView.editorPanel != null) {
            editorView.editorPanel.setTheme(theme);
         }
         editorView.editorTheme = theme;
      }
   }
   
   function showEditor(): FileEditorView {
      var editor = ace.edit("editor");
      var autoComplete = createEditorAutoComplete();

      editor.completers = [autoComplete];
      // setEditorTheme("eclipse"); // set the default to eclipse
      
      editor.getSession().setMode("ace/mode/snapscript");
      editor.getSession().setTabSize(3);
      
      editor.setReadOnly(false);
      editor.setAutoScrollEditorIntoView(true);
      editor.getSession().setUseSoftTabs(true);
      //editor.setKeyboardHandler("ace/keyboard/vim");
      
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
      
      // JavaFX has a very fast scroll speed
      if(typeof java !== 'undefined') {
         editor.setScrollSpeed(0.05); // slow down if its Java FX
      }
      return new FileEditorView(editor);
   }
   
   function validEditorLink(string, col) { // see link.js (http://jsbin.com/jehopaja/4/edit?html,output)
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
                   var indexToken = editorView.editorCurrentTokens[str];
                   
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
      if(KeyBinder.isControlPressed()) {
         var indexToken = editorView.editorCurrentTokens[event.value];
         
         if(indexToken != null) {
            if(indexToken.resource != null) {
               editorView.editorFocusToken = event.value;
               window.location.hash = indexToken.resource;
            }else {
               showEditorLine(indexToken.line); 
            }
            // alert("Editor open ["+event.value+"] @ "+line);
         }
      }
   }
   
   export function updateEditorFont(fontFamily, fontSize) {
      var autoComplete = createEditorAutoComplete();
      
      editorView.editorPanel.completers = [autoComplete];
      editorView.editorPanel.setOptions({
         enableBasicAutocompletion: true,
         fontFamily: "'"+fontFamily+"',monospace",
         fontSize: fontSize
      });
   }
}

//ModuleSystem.registerModule("editor", "Editor module: editor.js", null, FileEditor.createEditor, [ "common", "spinner", "tree" ]);
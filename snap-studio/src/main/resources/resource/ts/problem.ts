import * as $ from "jquery"
import {w2ui, w2popup} from "w2ui"
import {Common} from "common"
import {EventBus} from "socket"
import {FileTree, FilePath} from "tree"
import {FileEditor, FileEditorState} from "editor"
import {Project} from "project"

export module ProblemManager {
   
   class ProblemItem {
   
      private _resource: FilePath;
      private _line: number;
      private _message: string;
      private _project: string;
      private _time: number;
   
      constructor(resource: FilePath, line: number, message: string, project: string, time: number) {
         this._resource = resource;
         this._line = line;
         this._message = message;
         this._project = project;
         this._time = time;
      }
      
      public isExpired(): boolean {
         return this._time + 100000 > Common.currentTime()
      }
      
      public getKey(): string {
         return this._resource + ":" + this._line;
      }
      
      public getResource(): FilePath {
         return this._resource;
      }
      
      public getLine(): number {
         return this._line;
      }
      
      public getMessage(): string {
         return this._message;
      }
      
      public getProject(): string {
         return this._project;
      }
      
      public getTime(): number {
         return this._time;
      }
   }
   
   
   var currentProblems = {};
   
   export function registerProblems() {
   	EventBus.createRoute('PROBLEM', updateProblems);
      setInterval(refreshProblems, 1000); // refresh the problems systems every 1 second
   }
   
   function refreshProblems() {
      var timeMillis = Common.currentTime();
      var activeProblems = {};
      var expiryCount = 0;
      
      for (var problemKey in currentProblems) {
         if (currentProblems.hasOwnProperty(problemKey)) {
            var problemItem: ProblemItem = currentProblems[problemKey];
            
            if(problemItem != null) {
               if(problemItem.isExpired()) {
                  activeProblems[problemKey] = problemItem;
               } else {
                  expiryCount++;
               }
            }
         }
      }
      currentProblems = activeProblems; // reset refreshed statuses
      
      if(expiryCount > 0) {
         showProblems(); // something expired!
      }
   }
   
   export function showProblems() {
      var problemRecords = [];
      var problemIndex = 1;
      
      for (var problemKey in currentProblems) {
         if (currentProblems.hasOwnProperty(problemKey)) {
            var problemItem: ProblemItem = currentProblems[problemKey];
            
         	if(problemItem != null) {
         	   problemRecords.push({ 
         	      recid: problemIndex++,
         		   line: problemItem.getLine(),
         		   location: "Line " + problemItem.getLine(), 
                  resource: problemItem.getResource().getFilePath(), // /blah/file.snap 
                  description: problemItem.getMessage(), 
                  project: problemItem.getProject(), 
                  script: problemItem.getResource().getResourcePath() // /resource/<project>/blah/file.snap
               });
         	}
         }
      }
      if(Common.updateTableRecords(problemRecords, 'problems')) {
         Project.showProblemsTab(); // focus the problems tab
      }
   }
   
   function clearProblems() {
   	var problems = w2ui['problems'];
   	
   	currentProblems = {};
      FileEditor.clearEditorHighlights();
       
   	if(problems != null) {
   	    problems.records = [];
   	    problems.refresh();
   	}
   }
   
   export function highlightProblems(){
      var editorState: FileEditorState = FileEditor.currentEditorState();
      var editorResource: FilePath = editorState.getResource();
      
      if(editorResource != null) {
         var highlightUpdates = [];
         
         //FileEditor.clearEditorHighlights(); this makes breakpoints jitter
         for (var problemKey in currentProblems) {
            if (currentProblems.hasOwnProperty(problemKey)) {
               if(Common.stringStartsWith(problemKey, editorResource.getResourcePath())) {
                  var problemItem: ProblemItem = currentProblems[problemKey];
                  
                  if(problemItem != null) {
                     FileEditor.clearEditorHighlights(); // clear if the resource is focused
                     highlightUpdates.push(problemItem.getLine());
                  } else {
                     FileEditor.clearEditorHighlights(); // clear if the resource is focused
                  }
               } else {
                  console.log("Clear highlights in " + editorResource);
                  FileEditor.clearEditorHighlights(); // clear if the resource is focused
               }
            }
         }
         if(highlightUpdates.length > 0) {
            FileEditor.createMultipleEditorHighlights(highlightUpdates, "problemHighlight");
         }
      }
   }
   
   function updateProblems(socket, type, text) {
   	var problems = w2ui['problems'];
   	var message = JSON.parse(text);
   	var resourcePath: FilePath = FileTree.createResourcePath(message.resource);   	
   	var problemItem: ProblemItem = new ProblemItem(
            resourcePath,
   	      message.line,
   	      "<div class='errorDescription'>"+message.description+"</div>",
   	      message.project,
   	      message.time
   	);
   	if(problemItem.getLine() >= 0) {
   	   currentProblems[problemItem.getKey()] = problemItem;
   	} else {
         for (var problemKey in currentProblems) {
            if (currentProblems.hasOwnProperty(problemKey)) {
               if(Common.stringStartsWith(problemKey, resourcePath.getResourcePath())) {
                  currentProblems[problemKey] = null;
               }
            }
         }
   	}
   	showProblems();
   	highlightProblems(); // highlight the problems
   }
}

//ModuleSystem.registerModule("problem", "Problem module: problem.js", null, ProblemManager.registerProblems, ["common", "socket"]);
import * as $ from "jquery"
import {w2ui, w2popup} from "w2ui"
import {Common} from "common"
import {EventBus} from "socket"
import {FileTree} from "tree"
import {FileEditor} from "editor"

export module ProblemManager {
   
   var currentProblems = {};
   
   export function registerProblems() {
   	EventBus.createRoute('PROBLEM', updateProblems);
      setInterval(refreshProblems, 1000); // refresh the problems systems every 1 second
   }
   
   function refreshProblems() {
      var timeMillis = Common.currentTime();
      var activeProblems = {};
      var expiryCount = 0;
      
      for (var currentProblem in currentProblems) {
         if (currentProblems.hasOwnProperty(currentProblem)) {
            var problemInfo = currentProblems[currentProblem];
            
            if(problemInfo != null) {
               if(problemInfo.time + 100000 > timeMillis) {
                  activeProblems[currentProblem] = problemInfo;
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
      
      for (var currentProblem in currentProblems) {
         if (currentProblems.hasOwnProperty(currentProblem)) {
            var problemInfo = currentProblems[currentProblem];
            
         	if(problemInfo != null) {
         	   problemRecords.push({ 
         	      recid: problemIndex++,
         		   line: problemInfo.line,
         		   location: "Line " + problemInfo.line, 
                  resource: problemInfo.resource.filePath, // /blah/file.snap 
                  description: problemInfo.message, 
                  project: problemInfo.project, 
                  script: problemInfo.resource.resourcePath // /resource/<project>/blah/file.snap
               });
         	}
         }
      }
      Common.updateTableRecords(problemRecords, 'problems');
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
      var editorData = FileEditor.loadEditor();
      var editorResource = editorData.resource;
      
      if(editorResource != null) {
         var highlightUpdates = [];
         
         //FileEditor.clearEditorHighlights(); this makes breakpoints jitter
         for (var currentProblem in currentProblems) {
            if (currentProblems.hasOwnProperty(currentProblem)) {
               if(Common.stringStartsWith(currentProblem, editorResource.resourcePath)) {
                  var problemInfo = currentProblems[currentProblem];
                  
                  if(problemInfo != null) {
                     FileEditor.clearEditorHighlights(); // clear if the resource is focused
                     highlightUpdates.push(problemInfo.line);
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
   	var resourcePath = FileTree.createResourcePath(message.resource);
   	var problemInfo = {
   	      line: message.line,
   	      message: "<div class='errorDescription'>"+message.description+"</div>",
   	      resource: resourcePath,
   	      project: message.project,
   	      time: message.time
   	};
   	if(problemInfo.line >= 0) {
   	   currentProblems[resourcePath.resourcePath + ":" + problemInfo.line] = problemInfo;
   	} else {
         for (var currentProblem in currentProblems) {
            if (currentProblems.hasOwnProperty(currentProblem)) {
               if(Common.stringStartsWith(currentProblem, resourcePath.resourcePath)) {
                  currentProblems[currentProblem] = null;
               }
            }
         }
   	}
   	showProblems();
   	highlightProblems(); // highlight the problems
   }
}

//ModuleSystem.registerModule("problem", "Problem module: problem.js", null, ProblemManager.registerProblems, ["common", "socket"]);
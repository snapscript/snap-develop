var threadVariables = {};
var threadEvaluation = {}

function toggleExpandVariable(name) {
   var threadScope = focusedThread();
   var expandPath = name + ".*"; // this ensures they sort in sequence with '.' notation, e.g blah.foo.*
   var removePrefix = name + ".";
   
   if(threadScope != null) {
      var variablesPaths = threadVariables[threadScope.thread];
      
      if(variablesPaths == null) {
         variablesPaths = [];
         threadVariables[threadScope.thread] = variablesPaths;
      }
      var removePaths = [];
      
      for(var i = 0; i< variablesPaths.length; i++) {
         var currentPath = variablesPaths[i];
         
         if(currentPath.startsWith(removePrefix)) {
            removePaths.push(currentPath); // remove variable
         }
      }
      for(var i = 0; i< removePaths.length; i++) {
         var removePath = removePaths[i];
         var removeIndex = variablesPaths.indexOf(removePath);

         if(removeIndex != -1) {
            variablesPaths.splice(removeIndex, 1); // remove variable
         }
      }
      if(removePaths.length == 0) {
         variablesPaths.push(expandPath); // add variable
      }
      browseScriptVariables(variablesPaths);
   }
}

function toggleExpandEvaluation(name, expression) {
   var threadScope = focusedThread();
   var expandPath = name + ".*"; // this ensures they sort in sequence with '.' notation, e.g blah.foo.*
   var removePrefix = name + ".";
   
   if(threadScope != null) {
      var variablesPaths = threadEvaluation[threadScope.thread];
      
      if(variablesPaths == null) {
         variablesPaths = [];
         threadEvaluation[threadScope.thread] = variablesPaths;
      }
      var removePaths = [];
      
      for(var i = 0; i< variablesPaths.length; i++) {
         var currentPath = variablesPaths[i];
         
         if(currentPath.startsWith(removePrefix)) {
            removePaths.push(currentPath); // remove variable
         }
      }
      for(var i = 0; i< removePaths.length; i++) {
         var removePath = removePaths[i];
         var removeIndex = variablesPaths.indexOf(removePath);

         if(removeIndex != -1) {
            variablesPaths.splice(removeIndex, 1); // remove variable
         }
      }
      if(removePaths.length == 0) {
         variablesPaths.push(expandPath); // add variable
      }
      browseScriptEvaluation(variablesPaths, expression);
   }
}

function showVariables() {
   var localVariables = focusedThreadVariables();
   var evaluationVariables = focusedThreadEvaluation();
   
   showVariablesGrid(localVariables, 'variables');
   showVariablesGrid(evaluationVariables, 'evaluation');
}

function showVariablesGrid(threadVariables, gridName) {
   var sortedNames = [];
   var variableRecords = [];
   var variableIndex = 1;
   
   for (var variableName in threadVariables) {
      if (threadVariables.hasOwnProperty(variableName)) {
         sortedNames.push(variableName); // add a '.' to ensure dot notation sorts e.g x.y.z
      }
   }
   sortedNames.sort();
   
   for(var i = 0; i < sortedNames.length; i++) {
      var variableName = sortedNames[i];
      var variable = threadVariables[variableName];
      var variableExpandable = "" + variable.expandable;
      var displayStyle = "variableLeaf";

      if(variableExpandable == "true") {
         displayStyle = "variableNode";
      }
      var displayValue = "<div class='variableData'>"+escapeHtml(variable.value)+"</div>";
      var displayName = "<div title='"+escapeHtml(variable.description)+"' style='padding-left: " + 
         (variable.depth * 20)+ 
         "px;'><div class='"+displayStyle+
         "'>"+variable.name+"</div></div>";
      
      variableRecords.push({
         recid: variableIndex++,
         path: variableName,
         name: displayName,
         value: variable.value,
         type: variable.type,
         expandable: variableExpandable == "true"
         //depth: variable.depth // seems to cause issues?
      });
   }
   var variableGrid = w2ui[gridName];
   
   if(variableGrid != null) {
      variableGrid.records = variableRecords;
      variableGrid.refresh();
   }
}

function clearEvaluation() {
   threadEvaluation = {};
//   w2ui['evaluation'].records = [];
//   w2ui['evaluation'].refresh();
}

function clearVariables() {
   threadVariables = {};
   w2ui['variables'].records = [];
   w2ui['variables'].refresh();
}

registerModule("variables", "Variables module: variables.js", null, [ "common" ]);
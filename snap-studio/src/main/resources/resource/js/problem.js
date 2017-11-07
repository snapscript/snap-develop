define(["require", "exports", "w2ui", "common", "socket", "tree", "editor"], function (require, exports, w2ui_1, common_1, socket_1, tree_1, editor_1) {
    "use strict";
    var ProblemManager;
    (function (ProblemManager) {
        var currentProblems = {};
        function registerProblems() {
            socket_1.EventBus.createRoute('PROBLEM', updateProblems);
            setInterval(refreshProblems, 1000); // refresh the problems systems every 1 second
        }
        ProblemManager.registerProblems = registerProblems;
        function refreshProblems() {
            var timeMillis = common_1.Common.currentTime();
            var activeProblems = {};
            var expiryCount = 0;
            for (var currentProblem in currentProblems) {
                if (currentProblems.hasOwnProperty(currentProblem)) {
                    var problemInfo = currentProblems[currentProblem];
                    if (problemInfo != null) {
                        if (problemInfo.time + 10000 > timeMillis) {
                            activeProblems[currentProblem] = problemInfo;
                        }
                        else {
                            expiryCount++;
                        }
                    }
                }
            }
            currentProblems = activeProblems; // reset refreshed statuses
            if (expiryCount > 0) {
                showProblems(); // something expired!
            }
        }
        function showProblems() {
            var problemRecords = [];
            var problemIndex = 1;
            for (var currentProblem in currentProblems) {
                if (currentProblems.hasOwnProperty(currentProblem)) {
                    var problemInfo = currentProblems[currentProblem];
                    if (problemInfo != null) {
                        problemRecords.push({
                            recid: problemIndex++,
                            line: problemInfo.line,
                            location: "Line " + problemInfo.line,
                            resource: problemInfo.resource.filePath,
                            description: problemInfo.message,
                            project: problemInfo.project,
                            script: problemInfo.resource.resourcePath // /resource/<project>/blah/file.snap
                        });
                    }
                }
            }
            common_1.Common.updateTableRecords(problemRecords, 'problems');
        }
        ProblemManager.showProblems = showProblems;
        function clearProblems() {
            var problems = w2ui_1.w2ui['problems'];
            currentProblems = {};
            editor_1.FileEditor.clearEditorHighlights();
            if (problems != null) {
                problems.records = [];
                problems.refresh();
            }
        }
        function highlightProblems() {
            var editorData = editor_1.FileEditor.loadEditor();
            var editorResource = editorData.resource;
            if (editorResource != null) {
                //FileEditor.clearEditorHighlights(); this makes breakpoints jitter
                if (currentProblems.hasOwnProperty(editorResource.resourcePath)) {
                    var problemInfo = currentProblems[editorResource.resourcePath];
                    if (problemInfo != null) {
                        editor_1.FileEditor.clearEditorHighlights(); // clear if the resource is focused
                        editor_1.FileEditor.createEditorHighlight(problemInfo.line, "problemHighlight");
                    }
                    else {
                        editor_1.FileEditor.clearEditorHighlights(); // clear if the resource is focused
                    }
                }
                else {
                    editor_1.FileEditor.clearEditorHighlights(); // clear if the resource is focused
                }
            }
        }
        ProblemManager.highlightProblems = highlightProblems;
        function updateProblems(socket, type, text) {
            var problems = w2ui_1.w2ui['problems'];
            var message = JSON.parse(text);
            var resourcePath = tree_1.FileTree.createResourcePath(message.resource);
            var problemInfo = {
                line: message.line,
                message: "<div class='errorDescription'>" + message.description + "</div>",
                resource: resourcePath,
                project: message.project,
                time: message.time
            };
            if (problemInfo.line >= 0) {
                currentProblems[resourcePath.resourcePath] = problemInfo;
            }
            else {
                currentProblems[resourcePath.resourcePath] = null;
            }
            showProblems();
            highlightProblems(); // highlight the problems
        }
    })(ProblemManager = exports.ProblemManager || (exports.ProblemManager = {}));
});
//ModuleSystem.registerModule("problem", "Problem module: problem.js", null, ProblemManager.registerProblems, ["common", "socket"]); 

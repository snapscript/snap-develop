define(["require", "exports", "w2ui", "common", "socket", "tree", "editor", "project"], function (require, exports, w2ui_1, common_1, socket_1, tree_1, editor_1, project_1) {
    "use strict";
    var ProblemManager;
    (function (ProblemManager) {
        var ProblemItem = (function () {
            function ProblemItem(resource, line, message, project, time) {
                this._resource = resource;
                this._line = line;
                this._message = message;
                this._project = project;
                this._time = time;
            }
            ProblemItem.prototype.isExpired = function () {
                return this._time + 100000 > common_1.Common.currentTime();
            };
            ProblemItem.prototype.getKey = function () {
                return this._resource + ":" + this._line;
            };
            ProblemItem.prototype.getResource = function () {
                return this._resource;
            };
            ProblemItem.prototype.getLine = function () {
                return this._line;
            };
            ProblemItem.prototype.getMessage = function () {
                return this._message;
            };
            ProblemItem.prototype.getProject = function () {
                return this._project;
            };
            ProblemItem.prototype.getTime = function () {
                return this._time;
            };
            return ProblemItem;
        }());
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
            for (var problemKey in currentProblems) {
                if (currentProblems.hasOwnProperty(problemKey)) {
                    var problemItem = currentProblems[problemKey];
                    if (problemItem != null) {
                        if (problemItem.isExpired()) {
                            activeProblems[problemKey] = problemItem;
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
            for (var problemKey in currentProblems) {
                if (currentProblems.hasOwnProperty(problemKey)) {
                    var problemItem = currentProblems[problemKey];
                    if (problemItem != null) {
                        problemRecords.push({
                            recid: problemIndex++,
                            line: problemItem.getLine(),
                            location: "Line " + problemItem.getLine(),
                            resource: problemItem.getResource().getFilePath(),
                            description: problemItem.getMessage(),
                            project: problemItem.getProject(),
                            script: problemItem.getResource().getResourcePath() // /resource/<project>/blah/file.snap
                        });
                    }
                }
            }
            if (common_1.Common.updateTableRecords(problemRecords, 'problems')) {
                project_1.Project.showProblemsTab(); // focus the problems tab
            }
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
            var editorState = editor_1.FileEditor.currentEditorState();
            var editorResource = editorState.getResource();
            if (editorResource != null) {
                var highlightUpdates = [];
                //FileEditor.clearEditorHighlights(); this makes breakpoints jitter
                for (var problemKey in currentProblems) {
                    if (currentProblems.hasOwnProperty(problemKey)) {
                        if (common_1.Common.stringStartsWith(problemKey, editorResource.getResourcePath())) {
                            var problemItem = currentProblems[problemKey];
                            if (problemItem != null) {
                                editor_1.FileEditor.clearEditorHighlights(); // clear if the resource is focused
                                highlightUpdates.push(problemItem.getLine());
                            }
                            else {
                                editor_1.FileEditor.clearEditorHighlights(); // clear if the resource is focused
                            }
                        }
                        else {
                            console.log("Clear highlights in " + editorResource);
                            editor_1.FileEditor.clearEditorHighlights(); // clear if the resource is focused
                        }
                    }
                }
                if (highlightUpdates.length > 0) {
                    editor_1.FileEditor.createMultipleEditorHighlights(highlightUpdates, "problemHighlight");
                }
            }
        }
        ProblemManager.highlightProblems = highlightProblems;
        function updateProblems(socket, type, text) {
            var problems = w2ui_1.w2ui['problems'];
            var message = JSON.parse(text);
            var resourcePath = tree_1.FileTree.createResourcePath(message.resource);
            var problemItem = new ProblemItem(resourcePath, message.line, "<div class='errorDescription'>" + message.description + "</div>", message.project, message.time);
            if (problemItem.getLine() >= 0) {
                currentProblems[problemItem.getKey()] = problemItem;
            }
            else {
                for (var problemKey in currentProblems) {
                    if (currentProblems.hasOwnProperty(problemKey)) {
                        if (common_1.Common.stringStartsWith(problemKey, resourcePath.getResourcePath())) {
                            currentProblems[problemKey] = null;
                        }
                    }
                }
            }
            showProblems();
            highlightProblems(); // highlight the problems
        }
    })(ProblemManager = exports.ProblemManager || (exports.ProblemManager = {}));
});
//ModuleSystem.registerModule("problem", "Problem module: problem.js", null, ProblemManager.registerProblems, ["common", "socket"]); 

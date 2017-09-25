define(["require", "exports", "jquery", "w2ui", "socket", "common", "tree", "editor", "variables", "explorer", "profiler", "status"], function (require, exports, $, w2ui_1, socket_1, common_1, tree_1, editor_1, variables_1, explorer_1, profiler_1, status_1) {
    "use strict";
    var ThreadManager;
    (function (ThreadManager) {
        var suspendedThreads = {};
        var threadEditorFocus = {};
        function createThreads() {
            socket_1.EventBus.createRoute("BEGIN", startThreads, clearThreads);
            socket_1.EventBus.createRoute("SCOPE", updateThreads, variables_1.VariableManager.clearVariables);
            socket_1.EventBus.createRoute("TERMINATE", deleteThreads);
            socket_1.EventBus.createRoute("EXIT", deleteThreads);
        }
        ThreadManager.createThreads = createThreads;
        function startThreads(socket, type, text) {
            var message = JSON.parse(text);
            suspendedThreads = {};
            clearFocusThread();
            variables_1.VariableManager.clearVariables();
            profiler_1.Profiler.clearProfiler();
            clearThreads();
            status_1.StatusPanel.showProcessStatus(message.resource, message.process, message.debug);
        }
        function deleteThreads(socket, type, text) {
            var terminateProcess = text;
            if (threadEditorFocus != null && threadEditorFocus.process == terminateProcess) {
                terminateThreads();
            }
        }
        function terminateThreads() {
            suspendedThreads = {};
            clearFocusThread();
            editor_1.FileEditor.clearEditorHighlights(); // this should be done in editor.js, i.e EventBus.createRoute("EXIT" ... )
            variables_1.VariableManager.clearVariables();
            clearThreads();
        }
        ThreadManager.terminateThreads = terminateThreads;
        function clearThreads() {
            clearFocusThread();
            w2ui_1.w2ui['threads'].records = [];
            w2ui_1.w2ui['threads'].refresh();
            $("#process").html("");
        }
        ThreadManager.clearThreads = clearThreads;
        function updateThreads(socket, type, text) {
            var threadScope = JSON.parse(text);
            var editorData = editor_1.FileEditor.loadEditor();
            if (isThreadFocusResumed(threadScope)) {
                clearFocusThread(); // clear focus as it is a resume
                updateThreadPanels(threadScope);
                editor_1.FileEditor.clearEditorHighlights(); // the thread has resumed so clear highlights
            }
            else {
                if (threadEditorFocus.thread == threadScope.thread) {
                    if (isThreadFocusUpdateNew(threadScope)) {
                        updateFocusedThread(threadScope); // something new happened so focus editor
                        updateThreadPanels(threadScope);
                    }
                }
                else if (threadEditorFocus.thread == null) {
                    focusThread(threadScope);
                    updateThreadPanels(threadScope);
                }
                else {
                    var currentScope = suspendedThreads[threadScope.thread];
                    if (isThreadScopeDifferent(currentScope, threadScope)) {
                        updateThreadPanels(threadScope);
                    }
                }
            }
            suspendedThreads[threadScope.thread] = threadScope;
            showThreadBreakpointLine(threadScope); // show breakpoint on editor
        }
        function isThreadScopeDifferent(leftScope, rightScope) {
            if (leftScope != null && rightScope != null) {
                if (leftScope.thread != rightScope.thread) {
                    return true;
                }
                if (leftScope.status != rightScope.status) {
                    return true;
                }
                if (leftScope.resource != rightScope.resource) {
                    return true;
                }
                if (leftScope.line != rightScope.line) {
                    return true;
                }
                return false;
            }
            return leftScope != rightScope;
        }
        function showThreadBreakpointLine(threadScope) {
            var editorData = editor_1.FileEditor.loadEditor();
            if (threadEditorFocus.thread == threadScope.thread) {
                if (editorData.resource.filePath == threadScope.resource && threadScope.status == 'SUSPENDED') {
                    editor_1.FileEditor.createEditorHighlight(threadScope.line, "threadHighlight");
                }
            }
        }
        function updateThreadPanels(threadScope) {
            suspendedThreads[threadScope.thread] = threadScope; // N.B update suspended threads before rendering
            showThreads();
            variables_1.VariableManager.showVariables();
        }
        function updateFocusedThread(threadScope) {
            if (isThreadFocusLineChange(threadScope)) {
                if (isThreadFocusResourceChange(threadScope)) {
                    var resourcePathDetails = tree_1.FileTree.createResourcePath(threadScope.resource);
                    explorer_1.FileExplorer.openTreeFile(resourcePathDetails.resourcePath, function () {
                        updateThreadFocus(threadScope);
                        editor_1.FileEditor.showEditorLine(threadScope.line);
                    });
                }
                else {
                    updateThreadFocus(threadScope);
                    editor_1.FileEditor.showEditorLine(threadScope.line);
                }
            }
            else {
                updateThreadFocus(threadScope); // record focus thread
            }
        }
        function focusThread(threadScope) {
            var editorData = editor_1.FileEditor.loadEditor();
            if (editorData.resource.filePath != threadScope.resource) {
                var resourcePathDetails = tree_1.FileTree.createResourcePath(threadScope.resource);
                explorer_1.FileExplorer.openTreeFile(resourcePathDetails.resourcePath, function () {
                    updateThreadFocus(threadScope);
                    editor_1.FileEditor.showEditorLine(threadScope.line);
                });
            }
            else {
                updateThreadFocus(threadScope);
                editor_1.FileEditor.showEditorLine(threadScope.line);
            }
        }
        function isThreadFocusResumed(threadScope) {
            if (threadScope.thread == threadScope.thread) {
                return threadScope.status != 'SUSPENDED'; // the thread has resumed
            }
            return false;
        }
        function isThreadFocusUpdateNew(threadScope) {
            if (threadEditorFocus.thread == threadScope.thread) {
                if (threadScope.status == 'SUSPENDED') {
                    if (threadEditorFocus.key != threadScope.key) {
                        return true;
                    }
                    if (threadEditorFocus.change != threadScope.change) {
                        return true;
                    }
                }
            }
            return false;
        }
        function isThreadFocusPositionChange(threadScope) {
            return isThreadFocusLineChange(threadScope) || isThreadFocusResourceChange(threadScope);
        }
        function isThreadFocusLineChange(threadScope) {
            if (threadEditorFocus.thread == threadScope.thread) {
                return threadEditorFocus.line != threadScope.line; // hash the thread or focus line changed
            }
            return false;
        }
        function isThreadFocusResourceChange(threadScope) {
            if (threadEditorFocus.thread == threadScope.thread) {
                var editorData = editor_1.FileEditor.loadEditor();
                return editorData.resource.filePath != threadScope.resource; // is there a need to update the editor
            }
            return false;
        }
        function focusedThread() {
            if (threadEditorFocus.thread != null) {
                return suspendedThreads[threadEditorFocus.thread];
            }
            return null;
        }
        ThreadManager.focusedThread = focusedThread;
        function clearFocusThread() {
            variables_1.VariableManager.clearVariables(); // clear the browse tree
            threadEditorFocus = {
                thread: null,
                process: null,
                resource: null,
                change: -1,
                line: -1,
                key: -1
            };
        }
        function updateThreadFocus(threadScope) {
            threadEditorFocus = {
                thread: threadScope.thread,
                process: threadScope.process,
                resource: threadScope.resource,
                line: threadScope.line,
                change: threadScope.change,
                key: threadScope.key
            };
            updateThreadPanels(threadScope); // update the thread variables etc..
        }
        function updateThreadFocusByName(threadName) {
            var threadScope = suspendedThreads[threadName];
            updateThreadFocus(threadScope);
        }
        ThreadManager.updateThreadFocusByName = updateThreadFocusByName;
        function focusedThreadVariables() {
            if (threadEditorFocus.thread != null) {
                var threadScope = suspendedThreads[threadEditorFocus.thread];
                if (threadScope != null) {
                    return threadScope.variables;
                }
            }
            return {};
        }
        ThreadManager.focusedThreadVariables = focusedThreadVariables;
        function focusedThreadEvaluation() {
            if (threadEditorFocus.thread != null) {
                var threadScope = suspendedThreads[threadEditorFocus.thread];
                if (threadScope != null) {
                    return threadScope.evaluation;
                }
            }
            return {};
        }
        ThreadManager.focusedThreadEvaluation = focusedThreadEvaluation;
        function showThreads() {
            var editorData = editor_1.FileEditor.loadEditor();
            var threadRecords = [];
            var threadIndex = 1;
            for (var threadName in suspendedThreads) {
                if (suspendedThreads.hasOwnProperty(threadName)) {
                    var threadScope = suspendedThreads[threadName];
                    var displayStyle = 'threadSuspended';
                    var active = "&nbsp;<input type='radio'>";
                    showThreadBreakpointLine(threadScope);
                    if (threadScope.status != 'SUSPENDED') {
                        displayStyle = 'threadRunning';
                    }
                    else {
                        if (threadEditorFocus.thread == threadScope.thread) {
                            active = "&nbsp;<input type='radio' checked>";
                        }
                    }
                    var displayName = "<div title='" + threadScope.stack + "' class='" + displayStyle + "'>" + threadName + "</div>";
                    var resourcePathDetails = tree_1.FileTree.createResourcePath(threadScope.resource);
                    threadRecords.push({
                        recid: threadIndex++,
                        name: displayName,
                        thread: threadName,
                        status: threadScope.status,
                        active: active,
                        instruction: threadScope.instruction,
                        variables: threadScope.variables,
                        resource: threadScope.resource,
                        key: threadScope.key,
                        line: threadScope.line,
                        script: resourcePathDetails.resourcePath
                    });
                }
            }
            common_1.Common.updateTableRecords(threadRecords, 'threads'); // update if changed only
        }
        ThreadManager.showThreads = showThreads;
    })(ThreadManager = exports.ThreadManager || (exports.ThreadManager = {}));
});
//ModuleSystem.registerModule("threads", "Thread module: threads.js", null, ThreadManager.createThreads, [ "common", "socket", "explorer" ]); 

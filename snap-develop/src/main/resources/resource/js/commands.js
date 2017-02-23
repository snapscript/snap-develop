var Command;
(function (Command) {
    function searchTypes() {
        DialogBuilder.createListDialog(function (text) {
            var typesFound = findTypesMatching(text);
            var typeRows = [];
            for (var i = 0; i < typesFound.length; i++) {
                var debugToggle = ";debug";
                var locationPath = window.document.location.pathname;
                var locationHash = window.document.location.hash;
                var debug = locationPath.indexOf(debugToggle, locationPath.length - debugToggle.length) !== -1;
                var resourceLink = "/project/" + typesFound[i].project;
                if (debug) {
                    resourceLink += debugToggle;
                }
                resourceLink += "#" + typesFound[i].resource;
                var typeCell = {
                    text: typesFound[i].name,
                    link: resourceLink,
                    style: typesFound[i].type == 'module' ? 'moduleNode' : 'typeNode'
                };
                var resourceCell = {
                    text: typesFound[i].resource,
                    link: resourceLink,
                    style: 'resourceNode'
                };
                typeRows.push([typeCell, resourceCell]);
            }
            return typeRows;
        }, null, "Search Types");
    }
    Command.searchTypes = searchTypes;
    function findTypesMatching(text) {
        var response = [];
        jQuery.ajax({
            url: '/type/' + document.title + '?expression=' + text,
            success: function (typeMatches) {
                var sortedMatches = [];
                for (var typeMatch in typeMatches) {
                    if (typeMatches.hasOwnProperty(typeMatch)) {
                        sortedMatches.push(typeMatch);
                    }
                }
                sortedMatches.sort();
                for (var i = 0; i < sortedMatches.length; i++) {
                    var typeMatch = sortedMatches[i];
                    var typeReference = typeMatches[typeMatch];
                    var typeEntry = {
                        name: typeReference.name,
                        resource: typeReference.resource,
                        type: typeReference.type,
                        project: document.title
                    };
                    response.push(typeEntry);
                }
            },
            async: false
        });
        return response;
    }
    function searchFiles() {
        DialogBuilder.createListDialog(function (text, fileTypes) {
            var filesFound = findFilesWithText(text, fileTypes);
            var fileRows = [];
            for (var i = 0; i < filesFound.length; i++) {
                var fileFound = filesFound[i];
                var debugToggle = ";debug";
                var locationPath = window.document.location.pathname;
                var locationHash = window.document.location.hash;
                var debug = locationPath.indexOf(debugToggle, locationPath.length - debugToggle.length) !== -1;
                var resourceLink = "/resource/" + fileFound.project + "/" + fileFound.resource;
                var resourceCell = {
                    text: fileFound.resource,
                    line: fileFound.line,
                    resource: resourceLink,
                    style: 'resourceNode'
                };
                //            var lineCell = {
                //               text: "&nbsp;line&nbsp;" + filesFound[i].line + "&nbsp;",
                //               link: resourceLink,
                //               style: ''
                //            };
                var textCell = {
                    text: fileFound.text,
                    line: fileFound.line,
                    resource: resourceLink,
                    style: 'textNode'
                };
                fileRows.push([resourceCell, textCell]);
            }
            return fileRows;
        }, '*.snap,*.properties,*.xml,*.txt,*.json', "Search Files");
    }
    Command.searchFiles = searchFiles;
    function findFilesWithText(text, fileTypes) {
        var response = [];
        if (text && text.length > 1) {
            jQuery.ajax({
                url: '/find/' + document.title + '?expression=' + text + '&pattern=' + fileTypes,
                success: function (filesMatched) {
                    for (var i = 0; i < filesMatched.length; i++) {
                        var fileMatch = filesMatched[i];
                        var typeEntry = {
                            resource: fileMatch.resource,
                            line: fileMatch.line,
                            project: document.title
                        };
                        response.push(fileMatch);
                    }
                },
                async: false
            });
        }
        return response;
    }
    function findFileNames() {
        DialogBuilder.createListDialog(function (text) {
            var filesFound = findFilesByName(text);
            var fileRows = [];
            for (var i = 0; i < filesFound.length; i++) {
                var fileFound = filesFound[i];
                var debugToggle = ";debug";
                var locationPath = window.document.location.pathname;
                var locationHash = window.document.location.hash;
                var debug = locationPath.indexOf(debugToggle, locationPath.length - debugToggle.length) !== -1;
                var resourceLink = "/project/" + fileFound.project;
                if (debug) {
                    resourceLink += debugToggle;
                }
                resourceLink += "#" + fileFound.resource;
                var resourceCell = {
                    text: fileFound.text,
                    link: resourceLink,
                    style: 'resourceNode'
                };
                fileRows.push([resourceCell]);
            }
            return fileRows;
        }, null, "Find Files");
    }
    Command.findFileNames = findFileNames;
    function findFilesByName(text) {
        var response = [];
        if (text && text.length > 1) {
            jQuery.ajax({
                url: '/file/' + document.title + '?expression=' + text,
                success: function (filesMatched) {
                    for (var i = 0; i < filesMatched.length; i++) {
                        var fileMatch = filesMatched[i];
                        var typeEntry = {
                            resource: fileMatch.resource,
                            project: document.title
                        };
                        response.push(fileMatch);
                    }
                },
                async: false
            });
        }
        return response;
    }
    function exploreDirectory(resourcePath) {
        if (FileTree.isResourceFolder(resourcePath.filePath)) {
            var message = JSON.stringify({
                project: document.title,
                resource: resourcePath.filePath
            });
            socket.send("EXPLORE:" + message);
        }
    }
    Command.exploreDirectory = exploreDirectory;
    function pingProcess() {
        if (isSocketOpen()) {
            socket.send("PING:" + document.title);
        }
    }
    Command.pingProcess = pingProcess;
    function renameFile(resourcePath) {
        var originalFile = resourcePath.filePath;
        DialogBuilder.renameFileTreeDialog(resourcePath, true, function (resourceDetails) {
            var message = JSON.stringify({
                project: document.title,
                from: originalFile,
                to: resourceDetails.filePath
            });
            socket.send("RENAME:" + message);
            renameEditorTab(resourcePath.resourcePath, resourceDetails.resourcePath); // rename tabs if open
        });
    }
    Command.renameFile = renameFile;
    function renameDirectory(resourcePath) {
        var originalPath = resourcePath.filePath;
        var directoryPath = FileTree.createResourcePath(originalPath + ".#"); // put a # in to trick in to thinking its a file
        DialogBuilder.renameDirectoryTreeDialog(directoryPath, true, function (resourceDetails) {
            var message = JSON.stringify({
                project: document.title,
                from: originalPath,
                to: resourceDetails.filePath
            });
            socket.send("RENAME:" + message);
        });
    }
    Command.renameDirectory = renameDirectory;
    function newFile(resourcePath) {
        DialogBuilder.newFileTreeDialog(resourcePath, true, function (resourceDetails) {
            if (!FileTree.isResourceFolder(resourceDetails.filePath)) {
                var message = JSON.stringify({
                    project: document.title,
                    resource: resourceDetails.filePath,
                    source: "",
                    directory: false,
                    create: true
                });
                ProcessConsole.clearConsole();
                socket.send("SAVE:" + message);
                FileEditor.updateEditor("", resourceDetails.projectPath);
            }
        });
    }
    Command.newFile = newFile;
    function newDirectory(resourcePath) {
        DialogBuilder.newDirectoryTreeDialog(resourcePath, true, function (resourceDetails) {
            if (FileTree.isResourceFolder(resourceDetails.filePath)) {
                var message = JSON.stringify({
                    project: document.title,
                    resource: resourceDetails.filePath,
                    source: "",
                    directory: true,
                    create: true
                });
                ProcessConsole.clearConsole();
                socket.send("SAVE:" + message);
            }
        });
    }
    Command.newDirectory = newDirectory;
    function saveFile() {
        saveFileWithAction(function () { }, true);
    }
    Command.saveFile = saveFile;
    function saveFileWithAction(saveCallback, update) {
        var editorData = FileEditor.loadEditor();
        if (editorData.resource == null) {
            DialogBuilder.openTreeDialog(null, false, function (resourceDetails) {
                saveEditor(update);
                saveCallback();
            });
        }
        else {
            if (FileEditor.isEditorChanged()) {
                DialogBuilder.openTreeDialog(editorData.resource, true, function (resourceDetails) {
                    saveEditor(update);
                    saveCallback();
                });
            }
            else {
                ProcessConsole.clearConsole();
                saveCallback();
            }
        }
    }
    function saveEditor(update) {
        var editorData = FileEditor.loadEditor();
        var editorPath = editorData.resource;
        if (editorPath != null) {
            var message = JSON.stringify({
                project: document.title,
                resource: editorPath.filePath,
                source: editorData.source,
                directory: false,
                create: false
            });
            ProcessConsole.clearConsole();
            socket.send("SAVE:" + message);
            if (update) {
                FileEditor.updateEditor(editorData.source, editorPath.projectPath);
            }
        }
    }
    Command.saveEditor = saveEditor;
    function deleteFile(resourceDetails) {
        var editorData = FileEditor.loadEditor();
        if (resourceDetails == null && editorData.resource != null) {
            resourceDetails = editorData.resource;
        }
        if (resourceDetails != null) {
            var editorData = FileEditor.loadEditor();
            var editorResource = editorData.resource;
            var message = "Delete resource " + editorResource.filePath;
            Alerts.createConfirmAlert("Delete File", message, "Delete", "Cancel", function () {
                var message = JSON.stringify({
                    project: document.title,
                    resource: resourceDetails.filePath
                });
                ProcessConsole.clearConsole();
                socket.send("DELETE:" + message);
                if (editorData.resource != null && editorData.resource.resourcePath == resourceDetails.resourcePath) {
                    FileEditor.resetEditor();
                }
                deleteEditorTab(resourceDetails.resourcePath); // rename tabs if open
            }, function () { });
        }
    }
    Command.deleteFile = deleteFile;
    function deleteDirectory(resourceDetails) {
        if (resourceDetails != null) {
            var message = JSON.stringify({
                project: document.title,
                resource: resourceDetails.filePath
            });
            ProcessConsole.clearConsole();
            socket.send("DELETE:" + message);
        }
    }
    Command.deleteDirectory = deleteDirectory;
    function runScript() {
        saveFileWithAction(function () {
            var editorData = FileEditor.loadEditor();
            var message = JSON.stringify({
                breakpoints: editorData.breakpoints,
                project: document.title,
                resource: editorData.resource.filePath,
                source: editorData.source
            });
            socket.send("EXECUTE:" + message);
        }, true); // save editor
    }
    Command.runScript = runScript;
    function updateScriptBreakpoints() {
        var editorData = FileEditor.loadEditor();
        var message = JSON.stringify({
            breakpoints: editorData.breakpoints,
            project: document.title
        });
        socket.send("BREAKPOINTS:" + message);
    }
    Command.updateScriptBreakpoints = updateScriptBreakpoints;
    function stepOverScript() {
        var threadScope = ThreadManager.focusedThread();
        if (threadScope != null) {
            var message = JSON.stringify({
                thread: threadScope.thread,
                type: "STEP_OVER"
            });
            FileEditor.clearEditorHighlights();
            socket.send("STEP:" + message);
        }
    }
    Command.stepOverScript = stepOverScript;
    function stepInScript() {
        var threadScope = ThreadManager.focusedThread();
        if (threadScope != null) {
            var message = JSON.stringify({
                thread: threadScope.thread,
                type: "STEP_IN"
            });
            FileEditor.clearEditorHighlights();
            socket.send("STEP:" + message);
        }
    }
    Command.stepInScript = stepInScript;
    function stepOutScript() {
        var threadScope = ThreadManager.focusedThread();
        if (threadScope != null) {
            var message = JSON.stringify({
                thread: threadScope.thread,
                type: "STEP_OUT"
            });
            FileEditor.clearEditorHighlights();
            socket.send("STEP:" + message);
        }
    }
    Command.stepOutScript = stepOutScript;
    function resumeScript() {
        var threadScope = ThreadManager.focusedThread();
        if (threadScope != null) {
            var message = JSON.stringify({
                thread: threadScope.thread,
                type: "RUN"
            });
            FileEditor.clearEditorHighlights();
            socket.send("STEP:" + message);
        }
    }
    Command.resumeScript = resumeScript;
    function stopScript() {
        socket.send("STOP");
    }
    Command.stopScript = stopScript;
    function browseScriptVariables(variables) {
        var threadScope = ThreadManager.focusedThread();
        if (threadScope != null) {
            var message = JSON.stringify({
                thread: threadScope.thread,
                expand: variables
            });
            socket.send("BROWSE:" + message);
        }
    }
    Command.browseScriptVariables = browseScriptVariables;
    function browseScriptEvaluation(variables, expression, refresh) {
        var threadScope = ThreadManager.focusedThread();
        if (threadScope != null) {
            var message = JSON.stringify({
                thread: threadScope.thread,
                expression: expression,
                expand: variables,
                refresh: refresh
            });
            socket.send("EVALUATE:" + message);
        }
    }
    Command.browseScriptEvaluation = browseScriptEvaluation;
    function attachProcess(process) {
        var statusFocus = DebugManager.currentStatusFocus(); // what is the current focus
        var editorData = FileEditor.loadEditor();
        var message = JSON.stringify({
            process: process,
            breakpoints: editorData.breakpoints,
            project: document.title,
            focus: statusFocus != process // toggle the focus
        });
        socket.send("ATTACH:" + message); // attach to process
    }
    Command.attachProcess = attachProcess;
    function switchLayout() {
        var debugToggle = ";debug";
        var locationPath = window.document.location.pathname;
        var locationHash = window.document.location.hash;
        var debug = locationPath.indexOf(debugToggle, locationPath.length - debugToggle.length) !== -1;
        if (debug) {
            var remainingPath = locationPath.substring(0, locationPath.length - debugToggle.length);
            document.location = remainingPath + locationHash;
        }
        else {
            document.location = locationPath + debugToggle + locationHash;
        }
    }
    Command.switchLayout = switchLayout;
    function evaluateExpression() {
        var threadScope = ThreadManager.focusedThread();
        if (threadScope != null) {
            var selectedText = FileEditor.getSelectedText();
            DialogBuilder.evaluateExpressionDialog(selectedText);
        }
    }
    Command.evaluateExpression = evaluateExpression;
    function switchProject() {
        document.location = "/";
    }
    Command.switchProject = switchProject;
})(Command || (Command = {}));
ModuleSystem.registerModule("commands", "Commands module: commands.js", null, ["common", "editor", "tree", "threads"]);

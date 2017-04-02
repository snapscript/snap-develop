var Command;
(function (Command) {
    function searchTypes() {
        DialogBuilder.createListDialog(function (text, ignoreMe, onComplete) {
            findTypesMatching(text, function (typesFound) {
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
                onComplete(typeRows);
            });
        }, null, "Find Types");
    }
    Command.searchTypes = searchTypes;
    function findTypesMatching(text, onComplete) {
        if (text) {
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
                    var response = [];
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
                    onComplete(response);
                },
                async: true
            });
        }
        else {
            onComplete([]);
        }
    }
    function replaceTokenInFiles(matchText, searchCriteria, filePatterns) {
        findFilesWithText(matchText, filePatterns, searchCriteria, function (filesReplaced) {
            var editorData = FileEditor.loadEditor();
            for (var i = 0; i < filesReplaced.length; i++) {
                var fileReplaced = filesReplaced[i];
                var fileReplacedResource = FileTree.createResourcePath("/resource/" + fileReplaced.project + "/" + fileReplaced.resource);
                if (editorData.resource.resourcePath == fileReplacedResource.resourcePath) {
                    FileExplorer.openTreeFile(fileReplacedResource.resourcePath, function () {
                        //FileEditor.showEditorLine(record.line);  
                    });
                }
            }
        });
    }
    Command.replaceTokenInFiles = replaceTokenInFiles;
    function searchFiles(filePatterns) {
        searchOrReplaceFiles(false, filePatterns);
    }
    Command.searchFiles = searchFiles;
    function searchAndReplaceFiles(filePatterns) {
        searchOrReplaceFiles(true, filePatterns);
    }
    Command.searchAndReplaceFiles = searchAndReplaceFiles;
    function searchOrReplaceFiles(enableReplace, filePatterns) {
        if (!filePatterns) {
            filePatterns = '*.snap,*.properties,*.xml,*.txt,*.json';
        }
        var searchFunction = DialogBuilder.createTextSearchOnlyDialog;
        if (enableReplace) {
            searchFunction = DialogBuilder.createTextSearchAndReplaceDialog;
        }
        searchFunction(function (text, fileTypes, searchCriteria, onComplete) {
            findFilesWithText(text, fileTypes, searchCriteria, function (filesFound) {
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
                return onComplete(fileRows);
            });
        }, filePatterns, enableReplace ? "Replace Text" : "Find Text");
    }
    function findFilesWithText(text, fileTypes, searchCriteria, onComplete) {
        if (text && text.length > 1) {
            var searchUrl = '';
            searchUrl += '/find/' + document.title;
            searchUrl += '?expression=' + encodeURIComponent(text);
            searchUrl += '&pattern=' + encodeURIComponent(fileTypes);
            searchUrl += "&caseSensitive=" + encodeURIComponent(searchCriteria.caseSensitive);
            searchUrl += "&regularExpression=" + encodeURIComponent(searchCriteria.regularExpression);
            searchUrl += "&wholeWord=" + encodeURIComponent(searchCriteria.wholeWord);
            searchUrl += "&replace=" + encodeURIComponent(searchCriteria.replace);
            searchUrl += "&enableReplace=" + encodeURIComponent(searchCriteria.enableReplace);
            jQuery.ajax({
                url: searchUrl,
                success: function (filesMatched) {
                    var response = [];
                    for (var i = 0; i < filesMatched.length; i++) {
                        var fileMatch = filesMatched[i];
                        var typeEntry = {
                            resource: fileMatch.resource,
                            line: fileMatch.line,
                            project: document.title
                        };
                        response.push(fileMatch);
                    }
                    onComplete(response);
                },
                async: true
            });
        }
        else {
            onComplete([]);
        }
    }
    function findFileNames() {
        DialogBuilder.createListDialog(function (text, ignoreMe, onComplete) {
            findFilesByName(text, function (filesFound) {
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
                return onComplete(fileRows);
            });
        }, null, "Find Files");
    }
    Command.findFileNames = findFileNames;
    function findFilesByName(text, onComplete) {
        if (text && text.length > 1) {
            jQuery.ajax({
                url: '/file/' + document.title + '?expression=' + text,
                success: function (filesMatched) {
                    var response = [];
                    for (var i = 0; i < filesMatched.length; i++) {
                        var fileMatch = filesMatched[i];
                        var typeEntry = {
                            resource: fileMatch.resource,
                            project: document.title
                        };
                        response.push(fileMatch);
                    }
                    onComplete(response);
                },
                async: true
            });
        }
        else {
            onComplete([]);
        }
    }
    function exploreDirectory(resourcePath) {
        if (FileTree.isResourceFolder(resourcePath.filePath)) {
            var message = {
                project: document.title,
                resource: resourcePath.filePath
            };
            EventBus.sendEvent("EXPLORE", message);
        }
    }
    Command.exploreDirectory = exploreDirectory;
    function folderExpand(resourcePath) {
        var message = {
            project: document.title,
            folder: resourcePath
        };
        EventBus.sendEvent("FOLDER_EXPAND", message);
    }
    Command.folderExpand = folderExpand;
    function folderCollapse(resourcePath) {
        var message = {
            project: document.title,
            folder: resourcePath
        };
        EventBus.sendEvent("FOLDER_COLLAPSE", message);
    }
    Command.folderCollapse = folderCollapse;
    function pingProcess() {
        if (EventBus.isSocketOpen()) {
            EventBus.sendEvent("PING", document.title);
        }
    }
    Command.pingProcess = pingProcess;
    function renameFile(resourcePath) {
        var originalFile = resourcePath.filePath;
        DialogBuilder.renameFileTreeDialog(resourcePath, true, function (resourceDetails) {
            var message = {
                project: document.title,
                from: originalFile,
                to: resourceDetails.filePath
            };
            EventBus.sendEvent("RENAME", message);
            Project.renameEditorTab(resourcePath.resourcePath, resourceDetails.resourcePath); // rename tabs if open
        });
    }
    Command.renameFile = renameFile;
    function renameDirectory(resourcePath) {
        var originalPath = resourcePath.filePath;
        var directoryPath = FileTree.createResourcePath(originalPath + ".#"); // put a # in to trick in to thinking its a file
        DialogBuilder.renameDirectoryTreeDialog(directoryPath, true, function (resourceDetails) {
            var message = {
                project: document.title,
                from: originalPath,
                to: resourceDetails.filePath
            };
            EventBus.sendEvent("RENAME", message);
        });
    }
    Command.renameDirectory = renameDirectory;
    function newFile(resourcePath) {
        DialogBuilder.newFileTreeDialog(resourcePath, true, function (resourceDetails) {
            if (!FileTree.isResourceFolder(resourceDetails.filePath)) {
                var message = {
                    project: document.title,
                    resource: resourceDetails.filePath,
                    source: "",
                    directory: false,
                    create: true
                };
                ProcessConsole.clearConsole();
                EventBus.sendEvent("SAVE", message);
                FileEditor.updateEditor("", resourceDetails.projectPath);
            }
        });
    }
    Command.newFile = newFile;
    function newDirectory(resourcePath) {
        DialogBuilder.newDirectoryTreeDialog(resourcePath, true, function (resourceDetails) {
            if (FileTree.isResourceFolder(resourceDetails.filePath)) {
                var message = {
                    project: document.title,
                    resource: resourceDetails.filePath,
                    source: "",
                    directory: true,
                    create: true
                };
                ProcessConsole.clearConsole();
                EventBus.sendEvent("SAVE", message);
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
            var message = {
                project: document.title,
                resource: editorPath.filePath,
                source: editorData.source,
                directory: false,
                create: false
            };
            ProcessConsole.clearConsole();
            EventBus.sendEvent("SAVE", message);
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
                var message = {
                    project: document.title,
                    resource: resourceDetails.filePath
                };
                ProcessConsole.clearConsole();
                EventBus.sendEvent("DELETE", message);
                if (editorData.resource != null && editorData.resource.resourcePath == resourceDetails.resourcePath) {
                    FileEditor.resetEditor();
                }
                Project.deleteEditorTab(resourceDetails.resourcePath); // rename tabs if open
            }, function () { });
        }
    }
    Command.deleteFile = deleteFile;
    function deleteDirectory(resourceDetails) {
        if (resourceDetails != null) {
            var message = {
                project: document.title,
                resource: resourceDetails.filePath
            };
            ProcessConsole.clearConsole();
            EventBus.sendEvent("DELETE", message);
        }
    }
    Command.deleteDirectory = deleteDirectory;
    function runScript() {
        saveFileWithAction(function () {
            var editorData = FileEditor.loadEditor();
            var message = {
                breakpoints: editorData.breakpoints,
                project: document.title,
                resource: editorData.resource.filePath,
                source: editorData.source
            };
            EventBus.sendEvent("EXECUTE", message);
        }, true); // save editor
    }
    Command.runScript = runScript;
    function updateScriptBreakpoints() {
        var editorData = FileEditor.loadEditor();
        var message = {
            breakpoints: editorData.breakpoints,
            project: document.title
        };
        EventBus.sendEvent("BREAKPOINTS", message);
    }
    Command.updateScriptBreakpoints = updateScriptBreakpoints;
    function stepOverScript() {
        var threadScope = ThreadManager.focusedThread();
        if (threadScope != null) {
            var message = {
                thread: threadScope.thread,
                type: "STEP_OVER"
            };
            FileEditor.clearEditorHighlights();
            EventBus.sendEvent("STEP", message);
        }
    }
    Command.stepOverScript = stepOverScript;
    function stepInScript() {
        var threadScope = ThreadManager.focusedThread();
        if (threadScope != null) {
            var message = {
                thread: threadScope.thread,
                type: "STEP_IN"
            };
            FileEditor.clearEditorHighlights();
            EventBus.sendEvent("STEP", message);
        }
    }
    Command.stepInScript = stepInScript;
    function stepOutScript() {
        var threadScope = ThreadManager.focusedThread();
        if (threadScope != null) {
            var message = {
                thread: threadScope.thread,
                type: "STEP_OUT"
            };
            FileEditor.clearEditorHighlights();
            EventBus.sendEvent("STEP", message);
        }
    }
    Command.stepOutScript = stepOutScript;
    function resumeScript() {
        var threadScope = ThreadManager.focusedThread();
        if (threadScope != null) {
            var message = {
                thread: threadScope.thread,
                type: "RUN"
            };
            FileEditor.clearEditorHighlights();
            EventBus.sendEvent("STEP", message);
        }
    }
    Command.resumeScript = resumeScript;
    function stopScript() {
        EventBus.sendEvent("STOP");
    }
    Command.stopScript = stopScript;
    function browseScriptVariables(variables) {
        var threadScope = ThreadManager.focusedThread();
        if (threadScope != null) {
            var message = {
                thread: threadScope.thread,
                expand: variables
            };
            EventBus.sendEvent("BROWSE", message);
        }
    }
    Command.browseScriptVariables = browseScriptVariables;
    function browseScriptEvaluation(variables, expression, refresh) {
        var threadScope = ThreadManager.focusedThread();
        if (threadScope != null) {
            var message = {
                thread: threadScope.thread,
                expression: expression,
                expand: variables,
                refresh: refresh
            };
            EventBus.sendEvent("EVALUATE", message);
        }
    }
    Command.browseScriptEvaluation = browseScriptEvaluation;
    function attachProcess(process) {
        var statusFocus = DebugManager.currentStatusFocus(); // what is the current focus
        var editorData = FileEditor.loadEditor();
        var message = {
            process: process,
            breakpoints: editorData.breakpoints,
            project: document.title,
            focus: statusFocus != process // toggle the focus
        };
        EventBus.sendEvent("ATTACH", message); // attach to process
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
    function updateDisplay(displayInfo) {
        if (EventBus.isSocketOpen()) {
            EventBus.sendEvent("DISPLAY_UPDATE", displayInfo); // update and save display
        }
    }
    Command.updateDisplay = updateDisplay;
    function evaluateExpression() {
        var threadScope = ThreadManager.focusedThread();
        if (threadScope != null) {
            var selectedText = FileEditor.getSelectedText();
            DialogBuilder.evaluateExpressionDialog(selectedText);
        }
    }
    Command.evaluateExpression = evaluateExpression;
    function refreshScreen() {
        setTimeout(function () {
            location.reload();
        }, 10);
    }
    Command.refreshScreen = refreshScreen;
    function switchProject() {
        document.location = "/";
    }
    Command.switchProject = switchProject;
})(Command || (Command = {}));
ModuleSystem.registerModule("commands", "Commands module: commands.js", null, null, ["common", "editor", "tree", "threads"]);

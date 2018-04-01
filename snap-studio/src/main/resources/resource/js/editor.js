define(["require", "exports", "jquery", "md5", "ace", "w2ui", "common", "socket", "problem", "spinner", "tree", "history", "project", "status", "keys", "commands"], function (require, exports, $, md5_1, ace_1, w2ui_1, common_1, socket_1, problem_1, spinner_1, tree_1, history_1, project_1, status_1, keys_1, commands_1) {
    "use strict";
    /**
     * Contains the state for the Ace editor and is a singleton instance
     * that exists as soon as the editor is created.
     */
    var FileEditorView = (function () {
        function FileEditorView(editorPanel, editorTheme) {
            this.editorBreakpoints = {}; // spans multiple resources
            this.editorMarkers = {};
            this.editorResource = null;
            this.editorText = null;
            this.editorTheme = null;
            this.editorCurrentTokens = {}; // current editor hyperlinks
            this.editorFocusToken = null; // token to focus on editor load
            this.editorHistory = {}; // store all editor context
            this.editorPanel = null; // this actual editor
            this.editorPanel = editorPanel;
            this.editorTheme = editorTheme;
        }
        FileEditorView.prototype.init = function () {
            keys_1.KeyBinder.bindKeys(); // register key bindings
            project_1.Project.changeProjectFont(); // project.js update font
            FileEditor.scrollEditorToPosition();
            FileEditor.updateProjectTabOnChange(); // listen to change
            spinner_1.LoadSpinner.finish();
        };
        return FileEditorView;
    }());
    exports.FileEditorView = FileEditorView;
    /**
     * Groups all the editor functions and creates the FileEditorView that
     * contains the state of the editor session.
     */
    var FileEditor;
    (function (FileEditor) {
        var editorView = null;
        function createEditor() {
            editorView = showEditor();
            editorView.init();
            socket_1.EventBus.createTermination(clearEditorHighlights); // create callback
        }
        FileEditor.createEditor = createEditor;
        function clearEditorHighlights() {
            var session = editorView.editorPanel.getSession();
            for (var editorLine in editorView.editorMarkers) {
                if (editorView.editorMarkers.hasOwnProperty(editorLine)) {
                    var marker = editorView.editorMarkers[editorLine];
                    if (marker != null) {
                        session.removeMarker(marker);
                    }
                }
            }
            editorView.editorMarkers = {};
        }
        FileEditor.clearEditorHighlights = clearEditorHighlights;
        function showEditorLine(line) {
            var editor = editorView.editorPanel;
            editorView.editorPanel.resize(true);
            if (line > 1) {
                editorView.editorPanel.scrollToLine(line - 1, true, true, function () { });
                editorView.editorPanel.gotoLine(line); // move the cursor
            }
            else {
                editorView.editorPanel.scrollToLine(0, true, true, function () { });
            }
            editorView.editorPanel.focus();
        }
        FileEditor.showEditorLine = showEditorLine;
        function clearEditorHighlight(line) {
            var session = editorView.editorPanel.getSession();
            var marker = editorView.editorMarkers[line];
            if (marker != null) {
                session.removeMarker(marker);
            }
        }
        function createEditorHighlight(line, css) {
            var Range = ace_1.ace.require('ace/range').Range;
            var session = editorView.editorPanel.getSession();
            // clearEditorHighlight(line);
            clearEditorHighlights(); // clear all highlights in editor
            var marker = session.addMarker(new Range(line - 1, 0, line - 1, 1), css, "fullLine");
            editorView.editorMarkers[line] = marker;
        }
        FileEditor.createEditorHighlight = createEditorHighlight;
        function createMultipleEditorHighlights(lines, css) {
            var Range = ace_1.ace.require('ace/range').Range;
            var session = editorView.editorPanel.getSession();
            // clearEditorHighlight(line);
            clearEditorHighlights(); // clear all highlights in editor
            for (var i = 0; i < lines.length; i++) {
                var line = lines[i];
                var marker = session.addMarker(new Range(line - 1, 0, line - 1, 1), css, "fullLine");
                editorView.editorMarkers[line] = marker;
            }
        }
        FileEditor.createMultipleEditorHighlights = createMultipleEditorHighlights;
        function findAndReplaceTextInEditor() {
            var editorData = loadEditor();
            commands_1.Command.searchAndReplaceFiles(editorData.resource.projectPath);
        }
        FileEditor.findAndReplaceTextInEditor = findAndReplaceTextInEditor;
        function findTextInEditor() {
            var editorData = loadEditor();
            commands_1.Command.searchFiles(editorData.resource.projectPath);
        }
        FileEditor.findTextInEditor = findTextInEditor;
        function addEditorKeyBinding(keyBinding, actionFunction) {
            editorView.editorPanel.commands.addCommand({
                name: keyBinding.editor,
                bindKey: {
                    win: keyBinding.editor,
                    mac: keyBinding.editor
                },
                exec: function (editor) {
                    if (actionFunction) {
                        actionFunction();
                    }
                }
            });
        }
        FileEditor.addEditorKeyBinding = addEditorKeyBinding;
        function clearEditorBreakpoint(row) {
            var session = editorView.editorPanel.getSession();
            var breakpoints = session.getBreakpoints();
            var remove = false;
            for (var breakpoint in breakpoints) {
                session.clearBreakpoint(row);
            }
            showEditorBreakpoints();
        }
        function clearEditorBreakpoints() {
            var session = editorView.editorPanel.getSession();
            var breakpoints = session.getBreakpoints();
            var remove = false;
            for (var breakpoint in breakpoints) {
                session.clearBreakpoint(row);
            }
        }
        function showEditorBreakpoints() {
            var breakpointRecords = [];
            var breakpointIndex = 1;
            for (var filePath in editorView.editorBreakpoints) {
                if (editorView.editorBreakpoints.hasOwnProperty(filePath)) {
                    var breakpoints = editorView.editorBreakpoints[filePath];
                    for (var lineNumber in breakpoints) {
                        if (breakpoints.hasOwnProperty(lineNumber)) {
                            if (breakpoints[lineNumber] == true) {
                                var resourcePathDetails = tree_1.FileTree.createResourcePath(filePath);
                                var displayName = "<div class='breakpointEnabled'>" + resourcePathDetails.projectPath + "</div>";
                                breakpointRecords.push({
                                    recid: breakpointIndex++,
                                    name: displayName,
                                    location: "Line " + lineNumber,
                                    resource: resourcePathDetails.projectPath,
                                    line: parseInt(lineNumber),
                                    script: resourcePathDetails.resourcePath
                                });
                            }
                        }
                    }
                }
            }
            w2ui_1.w2ui['breakpoints'].records = breakpointRecords;
            w2ui_1.w2ui['breakpoints'].refresh();
            commands_1.Command.updateScriptBreakpoints(); // update the breakpoints
        }
        FileEditor.showEditorBreakpoints = showEditorBreakpoints;
        function setEditorBreakpoint(row, value) {
            if (editorView.editorResource != null) {
                var session = editorView.editorPanel.getSession();
                var resourceBreakpoints = editorView.editorBreakpoints[editorView.editorResource.filePath];
                var line = parseInt(row);
                if (value) {
                    session.setBreakpoint(line);
                }
                else {
                    session.clearBreakpoint(line);
                }
                if (resourceBreakpoints == null) {
                    resourceBreakpoints = {};
                    editorView.editorBreakpoints[editorView.editorResource.filePath] = resourceBreakpoints;
                }
                resourceBreakpoints[line + 1] = value;
            }
            showEditorBreakpoints();
        }
        function toggleEditorBreakpoint(row) {
            if (editorView.editorResource != null) {
                var session = editorView.editorPanel.getSession();
                var resourceBreakpoints = editorView.editorBreakpoints[editorView.editorResource.filePath];
                var breakpoints = session.getBreakpoints();
                var remove = false;
                for (var breakpoint in breakpoints) {
                    if (breakpoint == row) {
                        remove = true;
                        break;
                    }
                }
                if (remove) {
                    session.clearBreakpoint(row);
                }
                else {
                    session.setBreakpoint(row);
                }
                var line = parseInt(row);
                if (resourceBreakpoints == null) {
                    resourceBreakpoints = {};
                    resourceBreakpoints[line + 1] = true;
                    editorView.editorBreakpoints[editorView.editorResource.filePath] = resourceBreakpoints;
                }
                else {
                    if (resourceBreakpoints[line + 1] == true) {
                        resourceBreakpoints[line + 1] = false;
                    }
                    else {
                        resourceBreakpoints[line + 1] = true;
                    }
                }
            }
            showEditorBreakpoints();
        }
        function resizeEditor() {
            var width = document.getElementById('editor').offsetWidth;
            var height = document.getElementById('editor').offsetHeight;
            console.log("Resize editor " + width + "x" + height);
            editorView.editorPanel.setAutoScrollEditorIntoView(true);
            editorView.editorPanel.resize(true);
            // editor.focus();
        }
        FileEditor.resizeEditor = resizeEditor;
        function resetEditor() {
            var session = editorView.editorPanel.getSession();
            clearEditorHighlights();
            editorView.editorResource = null;
            editorView.editorPanel.setReadOnly(false);
            session.setValue(editorView.editorText, 1);
            $("#currentFile").html("");
        }
        FileEditor.resetEditor = resetEditor;
        function clearEditor() {
            var session = editorView.editorPanel.getSession();
            for (var editorMarker in session.$backMarkers) {
                session.removeMarker(editorView.editorMarker);
            }
            var breakpoints = session.getBreakpoints();
            var remove = false;
            for (var breakpoint in breakpoints) {
                session.clearBreakpoint(breakpoint);
            }
            $("#currentFile").html("");
        }
        function loadEditor() {
            var editorHistory = loadEditorHistory();
            var editorPosition = loadEditorPosition();
            var editorText = loadEditorText();
            return {
                breakpoints: editorView.editorBreakpoints,
                resource: editorView.editorResource,
                history: editorHistory,
                position: editorPosition,
                source: editorText
            };
        }
        FileEditor.loadEditor = loadEditor;
        function loadEditorText() {
            return editorView.editorPanel.getValue();
        }
        function loadEditorPosition() {
            var scrollTop = editorView.editorPanel.getSession().getScrollTop();
            var editorCursor = editorView.editorPanel.selection.getCursor();
            if (editorCursor) {
                return {
                    row: editorCursor.row,
                    column: editorCursor.column,
                    scroll: scrollTop
                };
            }
            return {
                scroll: scrollTop,
                row: null,
                column: null
            };
        }
        function loadEditorHistory() {
            var session = editorView.editorPanel.getSession();
            var manager = session.getUndoManager();
            var undoStack = $.extend(true, {}, manager.$undoStack);
            var redoStack = $.extend(true, {}, manager.$redoStack);
            return {
                undoStack: undoStack,
                redoStack: redoStack,
                dirtyCounter: manager.dirtyCounter
            };
        }
        function encodeEditorText(text, resource) {
            if (text) {
                var token = resource.toLowerCase();
                if (common_1.Common.stringEndsWith(token, ".json")) {
                    try {
                        var object = JSON.parse(text);
                        return JSON.stringify(object, null, 3);
                    }
                    catch (e) {
                        return text;
                    }
                }
                return text;
            }
            return "";
        }
        function resolveEditorMode(resource) {
            var token = resource.toLowerCase();
            if (common_1.Common.stringEndsWith(token, ".snap")) {
                return "ace/mode/snapscript";
            }
            if (common_1.Common.stringEndsWith(token, ".xml")) {
                return "ace/mode/xml";
            }
            if (common_1.Common.stringEndsWith(token, ".json")) {
                return "ace/mode/json";
            }
            if (common_1.Common.stringEndsWith(token, ".sql")) {
                return "ace/mode/sql";
            }
            if (common_1.Common.stringEndsWith(token, ".pl")) {
                return "ace/mode/perl";
            }
            if (common_1.Common.stringEndsWith(token, ".kt")) {
                return "ace/mode/kotlin";
            }
            if (common_1.Common.stringEndsWith(token, ".js")) {
                return "ace/mode/javascript";
            }
            if (common_1.Common.stringEndsWith(token, ".ts")) {
                return "ace/mode/typescript";
            }
            if (common_1.Common.stringEndsWith(token, ".java")) {
                return "ace/mode/java";
            }
            if (common_1.Common.stringEndsWith(token, ".groovy")) {
                return "ace/mode/groovy";
            }
            if (common_1.Common.stringEndsWith(token, ".py")) {
                return "ace/mode/python";
            }
            if (common_1.Common.stringEndsWith(token, ".html")) {
                return "ace/mode/html";
            }
            if (common_1.Common.stringEndsWith(token, ".htm")) {
                return "ace/mode/html";
            }
            if (common_1.Common.stringEndsWith(token, ".txt")) {
                return "ace/mode/text";
            }
            if (common_1.Common.stringEndsWith(token, ".properties")) {
                return "ace/mode/properties";
            }
            if (common_1.Common.stringEndsWith(token, ".gitignore")) {
                return "ace/mode/text";
            }
            if (common_1.Common.stringEndsWith(token, ".project")) {
                return "ace/mode/xml";
            }
            if (common_1.Common.stringEndsWith(token, ".classpath")) {
                return "ace/mode/text";
            }
            return "ace/mode/text";
        }
        FileEditor.resolveEditorMode = resolveEditorMode;
        function saveEditorHistory() {
            var editorData = loadEditor();
            if (editorData.resource && editorData.source) {
                var md5Hash = md5_1.md5(editorData.source);
                var currentText = editorView.editorPanel.getValue();
                var saveText = isEditorChanged() ? currentText : null; // keep text if changed
                editorView.editorHistory[editorData.resource.resourcePath] = {
                    hash: md5Hash,
                    history: editorData.history,
                    position: editorData.position,
                    buffer: saveText // save the buffer if it has changed
                };
            }
        }
        function createEditorUndoManager(session, text, resource) {
            var manager = new ace_1.ace.UndoManager();
            if (text && resource) {
                var editorResource = tree_1.FileTree.createResourcePath(resource);
                var history = editorView.editorHistory[editorResource.resourcePath];
                if (history) {
                    var md5Hash = md5_1.md5(text);
                    if (history.hash == md5Hash) {
                        var undoStack = history.history.undoStack;
                        var redoStack = history.history.redoStack;
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
                        manager.dirtyCounter = history.history.dirtyCounter;
                    }
                    else {
                        editorView.editorHistory[editorResource.resourcePath] = null;
                    }
                }
            }
            session.setUndoManager(manager); // reset undo history
        }
        function clearSavedEditorBuffer(resource) {
            var editorResource = tree_1.FileTree.createResourcePath(resource);
            var history = editorView.editorHistory[editorResource.resourcePath];
            if (history) {
                history.buffer = null; // clear the buffer
                updateEditorTabMarkForResource(editorResource.resourcePath); // remove the *
            }
        }
        FileEditor.clearSavedEditorBuffer = clearSavedEditorBuffer;
        function loadSavedEditorBuffer(resource) {
            var editorResource = tree_1.FileTree.createResourcePath(resource);
            if (isEditorResourcePath(editorResource.resourcePath)) {
                var editorData = loadEditor();
                return editorData.source; // if its the current buffer then return it
            }
            var history = editorView.editorHistory[editorResource.resourcePath];
            if (history) {
                return history.buffer;
            }
            return null;
        }
        FileEditor.loadSavedEditorBuffer = loadSavedEditorBuffer;
        function updateEditor(text, resource) {
            var session = editorView.editorPanel.getSession();
            var currentMode = session.getMode();
            var actualMode = resolveEditorMode(resource);
            var encodedText = text; // encodeEditorText(text, resource); // change JSON conversion
            var savedHistoryBuffer = loadSavedEditorBuffer(resource); // load saved buffer
            var textToDisplay = encodedText;
            if (savedHistoryBuffer) {
                textToDisplay = savedHistoryBuffer;
            }
            saveEditorHistory(); // save any existing history
            if (actualMode != currentMode) {
                session.setMode({
                    path: actualMode,
                    v: Date.now()
                });
            }
            editorView.editorPanel.setReadOnly(false);
            editorView.editorPanel.setValue(textToDisplay, 1);
            createEditorUndoManager(session, textToDisplay, resource); // restore any existing history
            clearEditor();
            editorView.editorResource = tree_1.FileTree.createResourcePath(resource);
            editorView.editorText = encodedText; // save the real text NOT the text to display
            window.location.hash = editorView.editorResource.projectPath; // update # anchor
            problem_1.ProblemManager.highlightProblems(); // higlight problems on this resource
            if (resource != null && editorView.editorResource) {
                var breakpoints = editorView.editorBreakpoints[editorView.editorResource.filePath];
                if (breakpoints != null) {
                    for (var lineNumber in breakpoints) {
                        if (breakpoints.hasOwnProperty(lineNumber)) {
                            if (breakpoints[lineNumber] == true) {
                                setEditorBreakpoint(lineNumber - 1, true);
                            }
                        }
                    }
                }
            }
            project_1.Project.createEditorTab(); // update the tab name
            history_1.History.showFileHistory(); // update the history
            status_1.StatusPanel.showActiveFile(editorView.editorResource.projectPath);
            FileEditor.showEditorFileInTree();
            scrollEditorToPosition();
            updateEditorTabMark(); // add a * to the name if its not in sync
        }
        FileEditor.updateEditor = updateEditor;
        function showEditorFileInTree() {
            var editorData = loadEditor();
            var resourcePath = editorData.resource;
            tree_1.FileTree.showTreeNode('explorerTree', resourcePath);
        }
        FileEditor.showEditorFileInTree = showEditorFileInTree;
        function getCurrentLineForEditor() {
            return editorView.editorPanel.getSelectionRange().start.row;
        }
        FileEditor.getCurrentLineForEditor = getCurrentLineForEditor;
        function getSelectedText() {
            return editorView.editorPanel.getSelectedText();
        }
        FileEditor.getSelectedText = getSelectedText;
        function isEditorChanged() {
            if (editorView.editorResource != null) {
                var text = editorView.editorPanel.getValue();
                return text != editorView.editorText;
            }
            return false;
        }
        FileEditor.isEditorChanged = isEditorChanged;
        function isEditorChangedForPath(resource) {
            if (isEditorResourcePath(resource)) {
                return isEditorChanged();
            }
            var savedHistoryBuffer = loadSavedEditorBuffer(resource); // load saved buffer
            if (savedHistoryBuffer) {
                return true;
            }
            return false;
        }
        FileEditor.isEditorChangedForPath = isEditorChangedForPath;
        function isEditorResourcePath(resource) {
            if (editorView.editorResource != null) {
                if (editorView.editorResource.resourcePath == resource) {
                    return true;
                }
            }
            return false;
        }
        function scrollEditorToPosition() {
            var session = editorView.editorPanel.getSession();
            if (editorView.editorResource && editorView.editorResource.resourcePath) {
                var editorHistory = editorView.editorHistory[editorView.editorResource.resourcePath];
                if (editorHistory && editorHistory.position) {
                    var editorScroll = editorHistory.position.scroll;
                    var editorRow = editorHistory.position.row;
                    var editorColumn = editorHistory.position.column;
                    if (editorRow >= 0 && editorColumn >= 0) {
                        editorView.editorPanel.selection.moveTo(editorRow, editorColumn);
                    }
                    else {
                        editorView.editorPanel.gotoLine(1);
                    }
                    session.setScrollTop(editorScroll);
                }
                else {
                    editorView.editorPanel.gotoLine(1);
                    session.setScrollTop(0);
                }
            }
            else {
                editorView.editorPanel.gotoLine(1); // required for focus
                session.setScrollTop(0);
            }
            editorView.editorPanel.focus();
        }
        FileEditor.scrollEditorToPosition = scrollEditorToPosition;
        function updateProjectTabOnChange() {
            editorView.editorPanel.on("input", function () {
                updateEditorTabMark(); // on input then you update star
            });
        }
        FileEditor.updateProjectTabOnChange = updateProjectTabOnChange;
        function updateEditorTabMark() {
            updateEditorTabMarkForResource(editorView.editorResource.resourcePath);
        }
        function updateEditorTabMarkForResource(resource) {
            project_1.Project.markEditorTab(resource, isEditorChangedForPath(resource));
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
                    var resource = editorView.editorResource.projectPath;
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
                        success: function (response) {
                            var tokens = response.tokens;
                            var length = tokens.length;
                            var suggestions = [];
                            for (var token in tokens) {
                                if (tokens.hasOwnProperty(token)) {
                                    var type = tokens[token];
                                    suggestions.push({ className: 'autocomplete_' + type, name: token, value: token, score: 300, meta: type });
                                }
                            }
                            callback(null, suggestions);
                        },
                        error: function () {
                            console.log("Completion control failed");
                        },
                        processData: false,
                        type: 'POST',
                        url: '/complete/' + document.title
                    });
                }
            };
        }
        function formatEditorSource() {
            var text = editorView.editorPanel.getValue();
            var path = editorView.editorResource.filePath;
            $.ajax({
                contentType: 'text/plain',
                data: text,
                success: function (result) {
                    editorView.editorPanel.setReadOnly(false);
                    editorView.editorPanel.setValue(result, 1);
                },
                error: function () {
                    console.log("Format failed");
                },
                processData: false,
                type: 'POST',
                url: '/format/' + document.title + path
            });
        }
        FileEditor.formatEditorSource = formatEditorSource;
        function setEditorTheme(theme) {
            if (theme != null) {
                if (editorView.editorPanel != null) {
                    editorView.editorPanel.setTheme(theme);
                }
                editorView.editorTheme = theme;
            }
        }
        FileEditor.setEditorTheme = setEditorTheme;
        function showEditor() {
            var editor = ace_1.ace.edit("editor");
            var autoComplete = createEditorAutoComplete();
            editor.completers = [autoComplete];
            // setEditorTheme("eclipse"); // set the default to eclipse
            editor.getSession().setMode("ace/mode/snapscript");
            editor.getSession().setTabSize(3);
            editor.setReadOnly(false);
            editor.setAutoScrollEditorIntoView(true);
            editor.getSession().setUseSoftTabs(true);
            editor.commands.removeCommand("replace"); // Ctrl-H
            editor.commands.removeCommand("find"); // Ctrl-F
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
            editor.on("guttermousedown", function (e) {
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
                e.stop();
            });
            //
            // THIS IS THE LINKS
            //
            // JavaFX has a very fast scroll speed
            if (typeof java !== 'undefined') {
                editor.setScrollSpeed(0.05); // slow down if its Java FX
            }
            return new FileEditorView(editor, editorTheme);
        }
        function validEditorLink(string, col) {
            if (keys_1.KeyBinder.isControlPressed()) {
                var tokenPatterns = [
                    "\\.[A-Z][a-zA-Z0-9]*;",
                    "\\sas\\s+[A-Z][a-zA-Z0-9]*;",
                    "[a-zA-Z][a-zA-Z0-9]*\\s*\\.",
                    "[a-z][a-zA-Z0-9]*\\s*[=|<|>|!|\-|\+|\*|\\/|%]",
                    // operation
                    "new\\s+[A-Z][a-zA-Z0-9]*\\s*\\(",
                    "[a-zA-Z][a-zA-Z0-9]*\\s*\\(",
                    "[A-Z][a-zA-Z0-9]*\\s*\\[",
                    ":\\s*[A-Z][a-zA-Z0-9]*",
                    "extends\\s+[A-Z][a-zA-Z0-9]*",
                    "with\\s+[A-Z][a-zA-Z0-9]*" // implements trait
                ];
                for (var i = 0; i < tokenPatterns.length; i++) {
                    var regExp = new RegExp(tokenPatterns[i], 'g'); // WE SHOULD CACHE
                    // THE REGEX FOR
                    // PERFORMANCE
                    var matchFound = null;
                    regExp.lastIndex = 0; // you have to reset regex to its start
                    // position
                    string.replace(regExp, function (str) {
                        var offset = arguments[arguments.length - 2];
                        var length = str.length;
                        if (offset <= col && offset + length >= col) {
                            var indexToken = editorCurrentTokens[str];
                            if (indexToken != null) {
                                matchFound = {
                                    start: offset,
                                    value: str
                                };
                            }
                        }
                    });
                    if (matchFound != null) {
                        return matchFound;
                    }
                }
            }
            return null;
        }
        function openEditorLink(event) {
            if (keys_1.KeyBinder.isControlPressed()) {
                var indexToken = editorView.editorCurrentTokens[event.value];
                if (indexToken != null) {
                    if (indexToken.resource != null) {
                        editorView.editorFocusToken = event.value;
                        window.location.hash = indexToken.resource;
                    }
                    else {
                        showEditorLine(indexToken.line);
                    }
                }
            }
        }
        function updateEditorFont(fontFamily, fontSize) {
            var autoComplete = createEditorAutoComplete();
            editorView.editorPanel.completers = [autoComplete];
            editorView.editorPanel.setOptions({
                enableBasicAutocompletion: true,
                fontFamily: "'" + fontFamily + "',monospace",
                fontSize: fontSize
            });
        }
        FileEditor.updateEditorFont = updateEditorFont;
    })(FileEditor = exports.FileEditor || (exports.FileEditor = {}));
});
//ModuleSystem.registerModule("editor", "Editor module: editor.js", null, FileEditor.createEditor, [ "common", "spinner", "tree" ]); 

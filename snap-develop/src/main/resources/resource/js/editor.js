var editorBreakpoints = {}; // spans multiple resources
var editorMarkers = {};
var editorResource = null;
var editorText = null;
var editorTheme = null;
var editorCurrentTokens = {}; // current editor hyperlinks
var editorFocusToken = null; // token to focus on editor load
function createEditor() {
    window.setTimeout(showEditor, 400);
    createTermination(clearEditorHighlights); // create callback
}
function clearEditorHighlights() {
    var editor = ace.edit("editor");
    var session = editor.getSession();
    for (var editorLine in editorMarkers) {
        if (editorMarkers.hasOwnProperty(editorLine)) {
            var marker = editorMarkers[editorLine];
            if (marker != null) {
                session.removeMarker(marker);
            }
        }
    }
    editorMarkers = {};
}
function showEditorLine(line) {
    var editor = ace.edit("editor");
    editor.resize(true);
    if (line > 1) {
        editor.scrollToLine(line - 1, true, true, function () { });
    }
    else {
        editor.scrollToLine(0, true, true, function () { });
    }
}
function clearEditorHighlight(line) {
    var editor = ace.edit("editor");
    var session = editor.getSession();
    var marker = editorMarkers[line];
    if (marker != null) {
        session.removeMarker(marker);
    }
}
function createEditorHighlight(line, css) {
    var editor = ace.edit("editor");
    var Range = ace.require('ace/range').Range;
    var session = editor.getSession();
    //clearEditorHighlight(line);
    clearEditorHighlights(); // clear all highlights in editor
    // session.addMarker(new Range(from, 0, to, 1), "errorMarker", "fullLine");
    var marker = session.addMarker(new Range(line - 1, 0, line - 1, 1), css, "fullLine");
    editorMarkers[line] = marker;
}
function clearEditorBreakpoint(row) {
    var editor = ace.edit("editor");
    var session = editor.getSession();
    var breakpoints = session.getBreakpoints();
    var remove = false;
    for (var breakpoint in breakpoints) {
        session.clearBreakpoint(row);
    }
    showEditorBreakpoints();
}
function clearEditorBreakpoints() {
    var editor = ace.edit("editor");
    var session = editor.getSession();
    var breakpoints = session.getBreakpoints();
    var remove = false;
    for (var breakpoint in breakpoints) {
        session.clearBreakpoint(row);
    }
}
function showEditorBreakpoints() {
    var breakpointRecords = [];
    var breakpointIndex = 1;
    for (var filePath in editorBreakpoints) {
        if (editorBreakpoints.hasOwnProperty(filePath)) {
            var breakpoints = editorBreakpoints[filePath];
            for (var lineNumber in breakpoints) {
                if (breakpoints.hasOwnProperty(lineNumber)) {
                    if (breakpoints[lineNumber] == true) {
                        var resourcePathDetails = createResourcePath(filePath);
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
    w2ui['breakpoints'].records = breakpointRecords;
    w2ui['breakpoints'].refresh();
    updateScriptBreakpoints(); // update the breakpoints
}
function setEditorBreakpoint(row, value) {
    if (editorResource != null) {
        var editor = ace.edit("editor");
        var session = editor.getSession();
        var resourceBreakpoints = editorBreakpoints[editorResource.filePath];
        var line = parseInt(row);
        if (value) {
            session.setBreakpoint(line);
        }
        else {
            session.clearBreakpoint(line);
        }
        if (resourceBreakpoints == null) {
            resourceBreakpoints = {};
            editorBreakpoints[editorResource.filePath] = resourceBreakpoints;
        }
        resourceBreakpoints[line + 1] = value;
    }
    showEditorBreakpoints();
}
function toggleEditorBreakpoint(row) {
    if (editorResource != null) {
        var editor = ace.edit("editor");
        var session = editor.getSession();
        var resourceBreakpoints = editorBreakpoints[editorResource.filePath];
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
            editorBreakpoints[editorResource.filePath] = resourceBreakpoints;
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
    var editor = ace.edit("editor");
    var width = document.getElementById('editor').offsetWidth;
    var height = document.getElementById('editor').offsetHeight;
    console.log("Resize editor " + width + "x" + height);
    editor.setAutoScrollEditorIntoView(true);
    editor.resize(true);
    //editor.focus();
}
function resetEditor() {
    var editor = ace.edit("editor");
    var session = editor.getSession();
    editorMarkers = {};
    editorResource = null;
    editor.setReadOnly(true);
    session.setValue(editorText, 1);
    $("#currentFile").html("");
}
function clearEditor() {
    var editor = ace.edit("editor");
    var session = editor.getSession();
    for (var editorMarker in session.$backMarkers) {
        session.removeMarker(editorMarker);
    }
    var breakpoints = session.getBreakpoints();
    var remove = false;
    for (var breakpoint in breakpoints) {
        session.clearBreakpoint(breakpoint);
    }
    $("#currentFile").html("");
}
function loadEditor() {
    var editor = ace.edit("editor");
    var text = editor.getValue();
    return {
        breakpoints: editorBreakpoints,
        resource: editorResource,
        source: text
    };
}
function encodeEditorText(text, resource) {
    var token = resource.toLowerCase();
    if (token.endsWith(".json")) {
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
function resolveEditorMode(resource) {
    var token = resource.toLowerCase();
    if (token.endsWith(".snap")) {
        return "ace/mode/snapscript";
    }
    if (token.endsWith(".xml")) {
        return "ace/mode/xml";
    }
    if (token.endsWith(".json")) {
        return "ace/mode/json";
    }
    if (token.endsWith(".sql")) {
        return "ace/mode/sql";
    }
    if (token.endsWith(".js")) {
        return "ace/mode/javascript";
    }
    if (token.endsWith(".html")) {
        return "ace/mode/html";
    }
    if (token.endsWith(".htm")) {
        return "ace/mode/html";
    }
    if (token.endsWith(".txt")) {
        return "ace/mode/text";
    }
    if (token.endsWith(".properties")) {
        return "ace/mode/properties";
    }
    if (token.endsWith(".gitignore")) {
        return "ace/mode/text";
    }
    if (token.endsWith(".project")) {
        return "ace/mode/xml";
    }
    if (token.endsWith(".classpath")) {
        return "ace/mode/xml";
    }
    return null;
}
function indexEditorTokens(text, resource) {
    var token = resource.toLowerCase();
    var functionRegex = /(function|static|public|private|abstract|override|)\s+([a-z][a-zA-Z0-9]*)\s*\(/g;
    var variableRegex = /(var|const)\s+([a-z][a-zA-Z0-9]*)/g;
    var classRegex = /(class|trait|enum)\s+([A-Z][a-zA-Z0-9]*)/g;
    var importRegex = /import\s+([a-z][a-zA-Z0-9\.]*)\.([A-Z][a-zA-Z]*)/g;
    var tokenList = {};
    if (token.endsWith(".snap")) {
        var lines = text.split(/\r?\n/);
        for (var i = 0; i < lines.length; i++) {
            var line = lines[i];
            indexEditorLine(line, i + 1, functionRegex, tokenList, ["%s("], false);
            indexEditorLine(line, i + 1, variableRegex, tokenList, ["%s.", "%s=", "%s =", "%s<", "%s <", "%s>", "%s >", "%s!", "%s !", "%s-", "%s -", "%s+", "%s +", "%s*", "%s *", "%s%", "%s %", "%s/", "%s /"], false);
            indexEditorLine(line, i + 1, importRegex, tokenList, ["new %s(", "%s.", ":%s", ": %s", "extends %s", "with %s", "extends  %s", "with  %s", ".%s;", " as %s", "%s["], true);
            indexEditorLine(line, i + 1, classRegex, tokenList, ["new %s(", "%s.", ":%s", ": %s", "extends %s", "with %s", "extends  %s", "with  %s", ".%s;", " as %s", "%s["], false);
        }
    }
    editorCurrentTokens = tokenList; // keep these tokens for indexing
    if (editorFocusToken != null) {
        var focusToken = editorCurrentTokens[editorFocusToken];
        if (focusToken != null) {
            setTimeout(function () {
                showEditorLine(focusToken.line); // focus on the line there was a token
            }, 100);
            editorFocusToken = null; // clear for next open
        }
    }
}
function indexEditorLine(line, number, expression, tokenList, templates, external) {
    expression.lastIndex = 0; // you have to reset regex to its start position
    var tokens = expression.exec(line);
    if (tokens != null && tokens.length > 0) {
        var resourceToken = tokens[1]; // only for 'import' which is external
        var indexToken = tokens[2];
        for (var i = 0; i < templates.length; i++) {
            var template = templates[i];
            var indexKey = template.replace("%s", indexToken);
            if (external) {
                tokenList[indexKey] = {
                    resource: "/" + resourceToken.replace(".", "/") + ".snap",
                    line: number // save the line number
                };
            }
            else {
                tokenList[indexKey] = {
                    resource: null,
                    line: number // save the line number
                };
            }
        }
    }
}
function updateEditor(text, resource) {
    var editor = ace.edit("editor");
    var session = editor.getSession();
    var currentMode = session.getMode();
    var actualMode = resolveEditorMode(resource);
    var text = encodeEditorText(text, resource); // change JSON conversion
    if (actualMode != currentMode) {
        session.setMode({
            path: actualMode,
            v: Date.now()
        });
    }
    var manager = new ace.UndoManager();
    editor.setReadOnly(false);
    editor.setValue(text, 1);
    editor.getSession().setUndoManager(manager); // clear undo history
    clearEditor();
    scrollEditorToTop();
    editorResource = createResourcePath(resource);
    editorMarkers = {};
    editorText = text;
    window.location.hash = editorResource.projectPath; // update # anchor
    highlightProblems(); // higlight problems on this resource
    if (resource != null) {
        var breakpoints = editorBreakpoints[editorResource.filePath];
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
    indexEditorTokens(text, resource); // create some tokens we can link to dynamically
    $("#currentFile").html("File:&nbsp;" + editorResource.projectPath + "&nbsp;&nbsp;");
}
function isEditorChanged() {
    if (editorResource != null) {
        var editor = ace.edit("editor");
        var text = editor.getValue();
        return text != editorText;
    }
    return false;
}
function scrollEditorToTop() {
    var editor = ace.edit("editor");
    var session = editor.getSession();
    session.setScrollTop(0);
}
function createEditorAutoComplete() {
    return {
        getCompletions: function createAutoComplete(editor, session, pos, prefix, callback) {
            if (prefix.length === 0) {
                callback(null, []);
                return;
            }
            var text = editor.getValue();
            var line = editor.session.getLine(pos.row);
            var complete = line.substring(0, pos.column - prefix.length);
            var message = JSON.stringify({
                resource: editorResource.projectPath,
                line: pos.row,
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
                            suggestions.push({ name: token, value: token, score: 300, meta: type });
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
    var editor = ace.edit("editor");
    var text = editor.getValue();
    $.ajax({
        contentType: 'text/plain',
        data: text,
        success: function (result) {
            editor.setReadOnly(false);
            editor.setValue(result, 1);
        },
        error: function () {
            console.log("Format failed");
        },
        processData: false,
        type: 'POST',
        url: '/format/' + document.title
    });
}
function registerEditorBindings() {
    var editor = ace.edit("editor");
    editor.commands.addCommand({
        name: 'run',
        bindKey: {
            win: 'Ctrl-R',
            mac: 'Command-R'
        },
        exec: function (editor) {
            runScript();
        },
        readOnly: true
    });
    editor.commands.addCommand({
        name: 'save',
        bindKey: {
            win: 'Ctrl-S',
            mac: 'Command-S'
        },
        exec: function (editor) {
            saveFile();
        },
        readOnly: true
    });
    editor.commands.addCommand({
        name: 'new',
        bindKey: {
            win: 'Ctrl-N',
            mac: 'Command-N'
        },
        exec: function (editor) {
            newFile(null);
        },
        readOnly: true
    });
    editor.commands.addCommand({
        name: 'format',
        bindKey: {
            win: 'Ctrl-Shift-F',
            mac: 'Command-Shift-F'
        },
        exec: function (editor) {
            formatEditorSource();
        },
        readOnly: true
    });
    editor.commands.addCommand({
        name: 'find',
        bindKey: {
            win: 'Ctrl-Shift-S',
            mac: 'Command-Shift-S'
        },
        exec: function (editor) {
            searchTypes();
        },
        readOnly: true
    });
}
function setEditorTheme(theme) {
    if (theme != null) {
        var editor = ace.edit("editor");
        if (editor != null) {
            editor.setTheme(theme);
        }
        editorTheme = theme;
    }
}
function showEditor() {
    var langTools = ace.require("ace/ext/language_tools");
    var editor = ace.edit("editor");
    var autoComplete = createEditorAutoComplete();
    if (editorTheme != null) {
        editor.setTheme(editorTheme);
    }
    editor.completers = [autoComplete];
    editor.getSession().setMode("ace/mode/snapscript");
    editor.getSession().setTabSize(3);
    editor.setReadOnly(true);
    editor.setAutoScrollEditorIntoView(true);
    editor.getSession().setUseSoftTabs(true);
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
    createEditorLinks(editor, validEditorLink, openEditorLink); // link.js
    registerEditorBindings();
    changeProjectFont(); // project.js update font
    scrollEditorToTop();
    LoadSpinner.finish();
}
function validEditorLink(string, col) {
    var tokenPatterns = [
        "\\.[A-Z][a-zA-Z0-9]*;",
        "\\sas\\s+[A-Z][a-zA-Z0-9]*;",
        "[a-zA-Z][a-zA-Z0-9]*\\s*\\.",
        "[a-z][a-zA-Z0-9]*\\s*[=|<|>|!|\-|\+|\*|\\/|%]",
        "new\\s+[A-Z][a-zA-Z0-9]*\\s*\\(",
        "[a-zA-Z][a-zA-Z0-9]*\\s*\\(",
        "[A-Z][a-zA-Z0-9]*\\s*\\[",
        ":\\s*[A-Z][a-zA-Z0-9]*",
        "extends\\s+[A-Z][a-zA-Z0-9]*",
        "with\\s+[A-Z][a-zA-Z0-9]*" // implements trait
    ];
    for (var i = 0; i < tokenPatterns.length; i++) {
        var regExp = new RegExp(tokenPatterns[i], 'g'); // WE SHOULD CACHE THE REGEX FOR PERFORMANCE
        var matchFound = null;
        regExp.lastIndex = 0; // you have to reset regex to its start position
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
    return null;
}
function openEditorLink(event) {
    var indexToken = editorCurrentTokens[event.value];
    if (indexToken != null) {
        if (indexToken.resource != null) {
            editorFocusToken = event.value;
            window.location.hash = indexToken.resource;
        }
        else {
            showEditorLine(indexToken.line);
        }
    }
}
function updateEditorFont(fontFamily, fontSize) {
    var langTools = ace.require("ace/ext/language_tools");
    var editor = ace.edit("editor");
    var autoComplete = createEditorAutoComplete();
    editor.completers = [autoComplete];
    editor.setOptions({
        enableBasicAutocompletion: true,
        fontFamily: fontFamily,
        fontSize: fontSize
    });
}
ModuleSystem.registerModule("editor", "Editor module: editor.js", createEditor, ["common", "spinner", "tree"]);

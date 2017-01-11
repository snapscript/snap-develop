var currentProblems = {};
function registerProblems() {
    createRoute('PROBLEM', updateProblems);
    setInterval(refreshProblems, 1000); // refresh the problems systems every 1 second
}
function refreshProblems() {
    var timeMillis = currentTime();
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
    for (var currentProblem in currentProblems) {
        if (currentProblems.hasOwnProperty(currentProblem)) {
            var problemInfo = currentProblems[currentProblem];
            if (problemInfo != null) {
                problemRecords.push({
                    recid: 1,
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
    w2ui['problems'].records = problemRecords;
    w2ui['problems'].refresh();
}
function clearProblems() {
    var problems = w2ui['problems'];
    currentProblems = {};
    clearEditorHighlights();
    if (problems != null) {
        problems.records = [];
        problems.refresh();
    }
}
function highlightProblems() {
    var editorData = loadEditor();
    var editorResource = editorData.resource;
    if (editorResource != null) {
        //clearEditorHighlights(); this makes breakpoints jitter
        if (currentProblems.hasOwnProperty(editorResource.resourcePath)) {
            var problemInfo = currentProblems[editorResource.resourcePath];
            if (problemInfo != null) {
                clearEditorHighlights(); // clear if the resource is focused
                createEditorHighlight(problemInfo.line, "problemHighlight");
            }
        }
    }
}
function updateProblems(socket, type, text) {
    var problems = w2ui['problems'];
    var message = JSON.parse(text);
    var resourcePath = createResourcePath(message.resource);
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
ModuleSystem.registerModule("problem", "Problem module: problem.js", registerProblems, ["common", "socket"]);

var DebugManager;
(function (DebugManager) {
    var statusProcesses = {};
    var statusFocus = null;
    function createStatus() {
        createRoute("STATUS", createStatusProcess, clearStatus); // status of processes
        createRoute("TERMINATE", terminateStatusProcess); // clear focus
        createRoute("EXIT", terminateStatusProcess);
        setInterval(refreshStatusProcesses, 1000); // refresh the status systems every 1 second
    }
    DebugManager.createStatus = createStatus;
    function refreshStatusProcesses() {
        var timeMillis = currentTime();
        var activeProcesses = {};
        var expiryCount = 0;
        for (var statusProcess in statusProcesses) {
            if (statusProcesses.hasOwnProperty(statusProcess)) {
                var statusProcessInfo = statusProcesses[statusProcess];
                if (statusProcessInfo != null) {
                    if (statusProcessInfo.time + 10000 > timeMillis) {
                        activeProcesses[statusProcess] = statusProcessInfo;
                    }
                    else {
                        expiryCount++;
                    }
                }
            }
        }
        statusProcesses = activeProcesses; // reset refreshed statuses
        if (expiryCount > 0) {
            showStatus(); // something expired!
        }
        Command.pingProcess(); // this will force a STATUS event
    }
    function terminateStatusProcess(socket, type, text) {
        if (text != null) {
            statusProcesses[text] = null;
        }
        if (statusFocus == text) {
            suspendedThreads = {};
            currentFocusThread = null;
            ThreadManager.terminateThreads();
            clearStatusFocus();
        }
        showStatus();
    }
    function createStatusProcess(socket, type, text) {
        var message = JSON.parse(text);
        var process = message.process;
        var processResource = message.resource;
        var processFocus = "" + message.focus;
        var processSystem = message.system;
        var processSystemTime = message.time;
        var processProject = message.project;
        var processRunning = message.running == "true";
        statusProcesses[process] = {
            resource: processResource,
            system: processSystem,
            time: processSystemTime,
            running: processRunning,
            focus: processFocus,
            project: processProject
        };
        if (processFocus == "true") {
            updateStatusFocus(process);
        }
        else {
            if (statusFocus == process) {
                clearStatusFocus(); // we are focus = false
            }
        }
        showStatus();
    }
    function isCurrentStatusFocusRunning() {
        if (statusFocus) {
            var statusProcessInfo = statusProcesses[statusProcess];
            if (statusProcessInfo) {
                return statusProcessInfo.resource != null;
            }
        }
        return false;
    }
    DebugManager.isCurrentStatusFocusRunning = isCurrentStatusFocusRunning;
    function currentStatusFocus() {
        return statusFocus;
    }
    DebugManager.currentStatusFocus = currentStatusFocus;
    function updateStatusFocus(process) {
        var statusInfo = statusProcesses[process];
        if (statusInfo != null && statusInfo.resource != null) {
            var statusResourcePath = FileTree.createResourcePath(statusInfo.resource);
            $("#toolbarDebug").css('opacity', '1.0');
            $("#toolbarDebug").css('filter', 'alpha(opacity=100)'); // msie
            StatusPanel.showProcessStatus(statusInfo.resource, process);
        }
        else {
            $("#toolbarDebug").css('opacity', '0.4');
            $("#toolbarDebug").css('filter', 'alpha(opacity=40)'); // msie
            $("#process").html("");
            FileEditor.clearEditorHighlights(); // focus lost so clear breakpoints
        }
        if (statusFocus != process) {
            Profiler.clearProfiler(); // profiler does not apply
            ThreadManager.clearThreads(); // race condition here
            VariableManager.clearVariables();
        }
        ProcessConsole.updateConsoleFocus(process); // clear console if needed
        statusFocus = process;
    }
    function clearStatusFocus() {
        statusFocus = null;
        ThreadManager.clearThreads(); // race condition here
        VariableManager.clearVariables();
        //   clearProfiler();
        //   clearConsole();
        $("#toolbarDebug").css('opacity', '0.4');
        $("#toolbarDebug").css('filter', 'alpha(opacity=40)'); // msie
        $("#process").html("");
    }
    function clearStatus() {
        statusProcesses = {};
        statusFocus = null;
        w2ui['debug'].records = [];
        w2ui['debug'].refresh();
    }
    function showStatus() {
        var statusRecords = [];
        var statusIndex = 1;
        for (var statusProcess in statusProcesses) {
            if (statusProcesses.hasOwnProperty(statusProcess)) {
                var statusProcessInfo = statusProcesses[statusProcess];
                if (statusProcessInfo != null) {
                    var statusProject = statusProcessInfo.project;
                    if (statusProject == document.title || statusProject == null) {
                        var displayName = "<div class='debugIdleRecord'>" + statusProcess + "</div>";
                        var status = "WAITING";
                        var active = "&nbsp;<input type='radio'><label></label>";
                        var resourcePath = "";
                        var running = false;
                        if (statusFocus == statusProcess) {
                            active = "&nbsp;<input type='radio' checked><label></label>";
                        }
                        if (statusProcessInfo.resource != null) {
                            var resourcePathDetails = FileTree.createResourcePath(statusProcessInfo.resource);
                            if (statusFocus == statusProcess) {
                                displayName = "<div class='debugFocusRecord'>" + statusProcess + "</div>";
                                status = "DEBUGGING";
                            }
                            else {
                                displayName = "<div class='debugRecord'>" + statusProcess + "</div>";
                                status = "RUNNING";
                            }
                            resourcePath = resourcePathDetails.resourcePath;
                            running = true;
                        }
                        statusRecords.push({
                            recid: statusIndex++,
                            name: displayName,
                            active: active,
                            process: statusProcess,
                            status: status,
                            running: running,
                            system: statusProcessInfo.system,
                            resource: statusProcessInfo.resource,
                            focus: statusFocus == statusProcess,
                            script: resourcePath
                        });
                    }
                    else {
                        console.log("Ignoring process " + statusProcess + " as it belongs to " + statusProject);
                    }
                }
            }
        }
        updateTableRecords(statusRecords, 'debug'); // update if changed only
    }
    DebugManager.showStatus = showStatus;
})(DebugManager || (DebugManager = {}));
ModuleSystem.registerModule("debug", "Debug module: debug.js", null, DebugManager.createStatus, ["common", "socket", "tree", "threads"]);

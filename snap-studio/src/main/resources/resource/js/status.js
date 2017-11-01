define(["require", "exports", "jquery", "tree"], function (require, exports, $, tree_1) {
    "use strict";
    var StatusPanel;
    (function (StatusPanel) {
        function showProcessStatus(resource, agent, debug) {
            var resourcePath = tree_1.FileTree.createResourcePath(resource);
            var processFile = resourcePath.fileName;
            var statusClass = "statusPanelRunning";
            var processDetail = "";
            if (debug) {
                statusClass = "statusPanelDebugging";
            }
            processDetail += "<table border='0'>\n";
            processDetail += "<tr>\n";
            processDetail += "<td><div class='" + statusClass + "'></div></td>\n";
            processDetail += "<td>" + processFile + "</td>\n";
            processDetail += "</tr>";
            processDetail += "</table>";
            $("#process").html(processDetail); // ("+message.process+") "+message.duration+" milliseconds</i>");
        }
        StatusPanel.showProcessStatus = showProcessStatus;
        function showActiveFile(resource) {
            var resourcePath = tree_1.FileTree.createResourcePath(resource);
            var pathSegments = resourcePath.projectPath.split("/");
            var pathBreadcrumb = "";
            pathBreadcrumb += "<table border='0'>\n";
            pathBreadcrumb += "<tr>\n";
            pathBreadcrumb += "<td><div class='treeIndexFolder'></div><td>\n";
            pathBreadcrumb += "<td>" + document.title + "</td>\n";
            for (var i = 0; i < pathSegments.length; i++) {
                var segment = pathSegments[i];
                if (segment.length > 0) {
                    pathBreadcrumb += "<td><div class='";
                    if (segment.indexOf(".") != -1) {
                        pathBreadcrumb += "treeFile";
                    }
                    else {
                        pathBreadcrumb += "treeFileFolder";
                    }
                    pathBreadcrumb += "'></div>";
                    pathBreadcrumb += "</td>\n<td style='white-space: nowrap;'>";
                    pathBreadcrumb += segment;
                    pathBreadcrumb += "</td>\n";
                }
            }
            pathBreadcrumb += "</table>";
            $("#currentFile").html(pathBreadcrumb);
        }
        StatusPanel.showActiveFile = showActiveFile;
    })(StatusPanel = exports.StatusPanel || (exports.StatusPanel = {}));
});
//ModuleSystem.registerModule("status", "Status panel module: status.js", null, null, []); 

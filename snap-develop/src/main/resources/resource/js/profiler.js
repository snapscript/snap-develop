define(["require", "exports", "w2ui", "socket", "tree"], function (require, exports, w2ui_1, socket_1, tree_1) {
    "use strict";
    var Profiler;
    (function (Profiler) {
        function startProfiler() {
            socket_1.EventBus.createRoute("PROFILE", updateProfiler, clearProfiler);
        }
        Profiler.startProfiler = startProfiler;
        function updateProfiler(socket, type, text) {
            var profileResult = JSON.parse(text);
            var profileRecords = profileResult.results;
            var profilerRecords = [];
            var profilerWidths = [];
            var profilerIndex = 1;
            var totalTime = 0;
            for (var i = 0; i < profileRecords.length; i++) {
                totalTime += profileRecords[i].time;
            }
            for (var i = 0; i < profileRecords.length; i++) {
                var recordTime = profileRecords[i].time;
                if (recordTime > 0) {
                    var percentageTime = (recordTime / totalTime) * 100;
                    var percentage = parseInt(percentageTime);
                    profilerWidths[i] = percentage;
                }
            }
            for (var i = 0; i < profileRecords.length; i++) {
                var profileRecord = profileRecords[i];
                var resourcePath = tree_1.FileTree.createResourcePath(profileRecord.resource);
                var displayName = "<div class='profilerRecord'>" + resourcePath.projectPath + "</div>";
                var percentageBar = "<div style='padding: 2px;'><div style='height: 10px; background: #ed6761; width: " + profilerWidths[i] + "%;'></div></div>";
                var averageTime = (profileRecord.count / profileRecord.time) / 1000; // average time in seconds
                profilerRecords.push({
                    recid: profilerIndex++,
                    resource: displayName,
                    percentage: percentageBar,
                    duration: profileRecord.time,
                    line: profileRecord.line,
                    count: profileRecord.count,
                    average: averageTime.toFixed(5),
                    script: resourcePath.resourcePath
                });
            }
            //console.log(text);
            w2ui_1.w2ui['profiler'].records = profilerRecords;
            w2ui_1.w2ui['profiler'].refresh();
        }
        function clearProfiler() {
            w2ui_1.w2ui['profiler'].records = [];
            w2ui_1.w2ui['profiler'].refresh();
        }
        Profiler.clearProfiler = clearProfiler;
    })(Profiler = exports.Profiler || (exports.Profiler = {}));
});
//ModuleSystem.registerModule("profiler", "Profiler module: profiler.js", null, Profiler.startProfiler, [ "common", "socket" ]); 

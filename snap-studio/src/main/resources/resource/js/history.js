define(["require", "exports", "jquery", "w2ui", "tree", "editor", "explorer"], function (require, exports, $, w2ui_1, tree_1, editor_1, explorer_1) {
    "use strict";
    var History;
    (function (History) {
        function trackHistory() {
            $(window).on('hashchange', function () {
                updateEditorFromHistory();
            });
            updateEditorFromHistory(200);
        }
        History.trackHistory = trackHistory;
        function showFileHistory() {
            var editorData = editor_1.FileEditor.loadEditor();
            var resource = editorData.resource.projectPath;
            $.ajax({
                url: '/history/' + document.title + '/' + resource,
                success: function (currentRecords) {
                    var historyRecords = [];
                    var historyIndex = 1;
                    for (var i = 0; i < currentRecords.length; i++) {
                        var currentRecord = currentRecords[i];
                        var recordResource = tree_1.FileTree.createResourcePath(currentRecord.path);
                        historyRecords.push({
                            recid: historyIndex++,
                            resource: "<div class='historyPath'>" + recordResource.filePath + "</div>",
                            date: currentRecord.date,
                            time: currentRecord.timeStamp,
                            script: recordResource.resourcePath // /resource/<project>/blah/file.snap
                        });
                    }
                    w2ui_1.w2ui['history'].records = historyRecords;
                    w2ui_1.w2ui['history'].refresh();
                },
                async: true
            });
        }
        History.showFileHistory = showFileHistory;
        function navigateForward() {
            window.history.forward();
        }
        History.navigateForward = navigateForward;
        function navigateBackward() {
            window.history.back();
        }
        History.navigateBackward = navigateBackward;
        function updateEditorFromHistory() {
            var location = window.location.hash;
            var hashIndex = location.indexOf('#');
            if (hashIndex != -1) {
                var resource = location.substring(hashIndex + 1);
                var resourceData = tree_1.FileTree.createResourcePath(resource);
                var editorData = editor_1.FileEditor.loadEditor();
                var editorResource = editorData.resource;
                if (editorResource == null || editorResource.resourcePath != resourceData.resourcePath) {
                    explorer_1.FileExplorer.openTreeFile(resourceData.resourcePath, function () { });
                }
            }
        }
    })(History = exports.History || (exports.History = {}));
});
//ModuleSystem.registerModule("history", "History module: history.js", null, History.trackHistory, [ "common", "editor" ]); 

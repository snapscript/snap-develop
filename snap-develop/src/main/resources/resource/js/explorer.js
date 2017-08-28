define(["require", "exports", "jquery", "common", "socket", "tree", "editor", "commands", "alert"], function (require, exports, $, common_1, socket_1, tree_1, editor_1, commands_1, alert_1) {
    "use strict";
    var FileExplorer;
    (function (FileExplorer) {
        var treeVisible = false;
        function showTree() {
            if (treeVisible == false) {
                window.setTimeout(reloadTree, 500);
                treeVisible = true;
            }
            socket_1.EventBus.createRoute("RELOAD_TREE", reloadTree);
        }
        FileExplorer.showTree = showTree;
        function reloadTree(socket, type, text) {
            tree_1.FileTree.createTree("/" + document.title, "explorer", "explorerTree", "/.", false, handleTreeMenu, function (event, data) {
                if (!data.node.isFolder()) {
                    openTreeFile(data.node.tooltip, function () { });
                }
            });
        }
        function openTreeFile(resourcePath, afterLoad) {
            var filePath = resourcePath.toLowerCase();
            if (common_1.Common.stringEndsWith(filePath, ".json") || common_1.Common.stringEndsWith(filePath, ".js")) {
                //var type = header.getResponseHeader("content-type");
                $.ajax({
                    url: resourcePath,
                    type: "get",
                    dataType: 'text',
                    success: function (response, status, header) {
                        var contentType = header.getResponseHeader("content-type");
                        handleOpenTreeFile(resourcePath, afterLoad, response, contentType, resourcePath);
                    },
                    error: function (response) {
                        var type = header.getResponseHeader("content-type");
                        handleOpenTreeFile(resourcePath, afterLoad, "// Could not find " + filePath, "text/plain", resourcePath);
                    },
                    async: false
                });
            }
            else {
                $.ajax({
                    url: resourcePath,
                    type: "get",
                    success: function (response, status, header) {
                        var contentType = header.getResponseHeader("content-type");
                        handleOpenTreeFile(resourcePath, afterLoad, response, contentType, resourcePath);
                    },
                    error: function (response) {
                        handleOpenTreeFile(resourcePath, afterLoad, "// Could not find " + filePath, "text/plain", resourcePath);
                    },
                    async: false
                });
            }
        }
        FileExplorer.openTreeFile = openTreeFile;
        function openTreeHistoryFile(resourcePath, timeStamp, afterLoad) {
            var filePath = resourcePath.toLowerCase();
            var backupResourcePath = resourcePath.replace(/^\/resource/i, "/history");
            //var backupUrl = backupResourcePath + "?time=" + timeStamp;
            if (common_1.Common.stringEndsWith(filePath, ".json") || common_1.Common.stringEndsWith(filePath, ".js")) {
                var downloadURL = backupResourcePath + "?time=" + timeStamp;
                $.ajax({
                    url: downloadURL,
                    type: "get",
                    dataType: 'text',
                    success: function (response, status, header) {
                        var contentType = header.getResponseHeader("content-type");
                        handleOpenTreeFile(resourcePath, afterLoad, response, contentType, downloadURL);
                    },
                    error: function (response) {
                        handleOpenTreeFile(resourcePath, afterLoad, "// Could not find " + filePath, "text/plain", downloadURL);
                    },
                    async: false
                });
            }
            else {
                var downloadURL = backupResourcePath + "?time=" + timeStamp;
                $.ajax({
                    url: downloadURL,
                    type: "get",
                    success: function (response, status, header) {
                        var contentType = header.getResponseHeader("content-type");
                        handleOpenTreeFile(resourcePath, afterLoad, response, contentType, downloadURL);
                    },
                    error: function (response) {
                        handleOpenTreeFile(resourcePath, afterLoad, "// Could not find " + filePath, "text/plain", downloadURL);
                    },
                    async: false
                });
            }
        }
        FileExplorer.openTreeHistoryFile = openTreeHistoryFile;
        function handleOpenTreeFile(resourcePath, afterLoad, response, contentType, downloadURL) {
            if (isImageFileType(contentType)) {
                handleOpenFileInNewTab(downloadURL);
            }
            else if (isBinaryFileType(contentType)) {
                handleDownloadFile(downloadURL);
            }
            else {
                var mode = editor_1.FileEditor.resolveEditorMode(resourcePath);
                if (editor_1.FileEditor.isEditorChanged()) {
                    var editorData = editor_1.FileEditor.loadEditor();
                    var editorResource = editorData.resource;
                    var message = "Save resource " + editorResource.filePath;
                    alert_1.Alerts.createConfirmAlert("File Changed", message, "Save", "Ignore", function () {
                        commands_1.Command.saveEditor(true); // save the file
                    }, function () {
                        editor_1.FileEditor.updateEditor(response, resourcePath);
                    });
                }
                else {
                    editor_1.FileEditor.updateEditor(response, resourcePath);
                }
            }
            afterLoad();
        }
        function handleOpenFileInNewTab(downloadURL) {
            var newTab = window.open(downloadURL, '_blank');
            newTab.focus();
        }
        function handleDownloadFile(downloadURL) {
            window.location.href = downloadURL;
        }
        function isImageFileType(contentType) {
            if (contentType) {
                if (common_1.Common.stringStartsWith(contentType, "image")) {
                    return true;
                }
            }
            return false;
        }
        function isBinaryFileType(contentType) {
            if (contentType) {
                if (contentType == "application/json") {
                    return false;
                }
                if (contentType == "application/x-javascript") {
                    return false;
                }
                if (common_1.Common.stringStartsWith(contentType, "application")) {
                    return true;
                }
                if (common_1.Common.stringStartsWith(contentType, "image")) {
                    return true;
                }
                if (common_1.Common.stringStartsWith(contentType, "text")) {
                    return false;
                }
                return true; // unknown
            }
            return false;
        }
        function handleTreeMenu(resourcePath, commandName, elementId, isDirectory) {
            if (commandName == "runScript") {
                openTreeFile(resourcePath.resourcePath, function () {
                    commands_1.Command.runScript();
                });
            }
            else if (commandName == "debugScript") {
                openTreeFile(resourcePath.resourcePath, function () {
                    commands_1.Command.debugScript();
                });
            }
            else if (commandName == "newFile") {
                commands_1.Command.newFile(resourcePath);
            }
            else if (commandName == "newDirectory") {
                commands_1.Command.newDirectory(resourcePath);
            }
            else if (commandName == "exploreDirectory") {
                commands_1.Command.exploreDirectory(resourcePath);
            }
            else if (commandName == "renameFile") {
                if (isDirectory) {
                    commands_1.Command.renameDirectory(resourcePath);
                }
                else {
                    commands_1.Command.renameFile(resourcePath);
                }
            }
            else if (commandName == "saveFile") {
                openTreeFile(resourcePath.resourcePath, function () {
                    commands_1.Command.saveFile();
                });
            }
            else if (commandName == "deleteFile") {
                if (tree_1.FileTree.isResourceFolder(resourcePath.resourcePath)) {
                    commands_1.Command.deleteDirectory(resourcePath);
                }
                else {
                    commands_1.Command.deleteFile(resourcePath);
                }
            }
        }
    })(FileExplorer = exports.FileExplorer || (exports.FileExplorer = {}));
});
//ModuleSystem.registerModule("explorer", "Explorer module: explorer.js", null, FileExplorer.showTree, [ "common", "spinner", "tree", "commands" ]); 

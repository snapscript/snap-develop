var DialogBuilder;
(function (DialogBuilder) {
    function openTreeDialog(resourceDetails, foldersOnly, saveCallback) {
        if (resourceDetails != null) {
            createProjectDialog(resourceDetails, foldersOnly, saveCallback, "Save Changes");
        }
        else {
            createProjectDialog(resourceDetails, foldersOnly, saveCallback, "Save As");
        }
    }
    DialogBuilder.openTreeDialog = openTreeDialog;
    function renameFileTreeDialog(resourceDetails, foldersOnly, saveCallback) {
        createProjectDialog(resourceDetails, foldersOnly, saveCallback, "Rename File");
    }
    DialogBuilder.renameFileTreeDialog = renameFileTreeDialog;
    function renameDirectoryTreeDialog(resourceDetails, foldersOnly, saveCallback) {
        createProjectDialog(resourceDetails, foldersOnly, saveCallback, "Rename Directory");
    }
    DialogBuilder.renameDirectoryTreeDialog = renameDirectoryTreeDialog;
    function newFileTreeDialog(resourceDetails, foldersOnly, saveCallback) {
        createProjectDialog(resourceDetails, foldersOnly, saveCallback, "New File");
    }
    DialogBuilder.newFileTreeDialog = newFileTreeDialog;
    function newDirectoryTreeDialog(resourceDetails, foldersOnly, saveCallback) {
        createProjectDialog(resourceDetails, foldersOnly, saveCallback, "New Directory");
    }
    DialogBuilder.newDirectoryTreeDialog = newDirectoryTreeDialog;
    function evaluateExpressionDialog(expressionToEvaluate) {
        createGridDialog(function (x) { return []; }, expressionToEvaluate, "Evaluate Expression");
    }
    DialogBuilder.evaluateExpressionDialog = evaluateExpressionDialog;
    function createProjectDialog(resourceDetails, foldersOnly, saveCallback, dialogTitle) {
        createTreeDialog(resourceDetails, foldersOnly, saveCallback, dialogTitle, "/" + document.title);
    }
    function createTreeDialog(resourceDetails, foldersOnly, saveCallback, dialogTitle, treePath) {
        var dialogExpandPath = "/";
        if (resourceDetails != null) {
            dialogExpandPath = resourceDetails.projectDirectory; // /src/blah
        }
        w2popup.open({
            title: dialogTitle,
            body: createDialogLayout('', dialogExpandPath, false),
            buttons: '<button id="dialogSave" class="btn dialogButton">Save</button><button id="dialogCancel" class="btn dialogButton">Cancel</button>',
            width: 500,
            height: 400,
            overflow: 'hidden',
            color: '#999',
            speed: '0.3',
            opacity: '0.8',
            modal: true,
            showClose: true,
            showMax: true,
            onOpen: function (event) {
                setTimeout(function () {
                    var element = document.getElementById('dialogPath');
                    element.contentEditable = true;
                    element.focus();
                }, 200);
            },
            onClose: function (event) {
                console.log('close');
            },
            onMax: function (event) {
                console.log('max');
            },
            onMin: function (event) {
                console.log('min');
            },
            onKeydown: function (event) {
                console.log('keydown');
            }
        });
        $("#dialogSave").click(function () {
            var originalDialogFileName = $('#dialogPath').html();
            var originalDialogFolder = $('#dialogFolder').html();
            var dialogPathName = FileTree.cleanResourcePath(originalDialogFileName);
            var dialogFolder = FileTree.cleanResourcePath(originalDialogFolder);
            var dialogProjectPath = dialogFolder + "/" + dialogPathName; // /src/blah/script.snap
            var dialogPathDetails = FileTree.createResourcePath(dialogProjectPath);
            saveCallback(dialogPathDetails);
            w2popup.close();
        });
        $("#dialogCancel").click(function () {
            w2popup.close();
        });
        if (resourceDetails != null) {
            $('#dialogFolder').html(FileTree.cleanResourcePath(resourceDetails.projectDirectory)); // /src/blah
            $('#dialogPath').html(FileTree.cleanResourcePath(resourceDetails.fileName)); // script.snap
        }
        FileTree.createTree(treePath, "dialog", "dialogTree", dialogExpandPath, foldersOnly, null, function (event, data) {
            var selectedFileDetails = FileTree.createResourcePath(data.node.tooltip);
            if (data.node.isFolder()) {
                $('#dialogFolder').html(FileTree.cleanResourcePath(selectedFileDetails.projectDirectory));
                $('#dialogPath').html("");
            }
            else {
                $('#dialogFolder').html(FileTree.cleanResourcePath(selectedFileDetails.projectDirectory)); // /src/blah
                $('#dialogPath').html(FileTree.cleanResourcePath(selectedFileDetails.fileName)); // file.snap
            }
        });
    }
    function createTreeOpenDialog(openCallback, closeCallback, dialogTitle, buttonText, treePath) {
        var completeFunction = function () {
            var originalDialogFolder = $('#dialogPath').html();
            var dialogFolder = FileTree.cleanResourcePath(originalDialogFolder); // clean up path
            var dialogPathDetails = FileTree.createResourcePath(dialogFolder);
            var selectedDirectory = dialogPathDetails.projectDirectory;
            if (selectedDirectory.indexOf("/") == 0) {
                selectedDirectory = selectedDirectory.substring(1);
            }
            openCallback(dialogPathDetails, selectedDirectory);
        };
        w2popup.open({
            title: dialogTitle,
            body: createDialogLayout('', null, null),
            buttons: '<button id="dialogSave" class="btn dialogButton">' + buttonText + '</button>',
            width: 500,
            height: 400,
            overflow: 'hidden',
            color: '#999',
            speed: '0.3',
            opacity: '0.8',
            modal: true,
            showClose: true,
            showMax: true,
            onOpen: function (event) {
                setTimeout(function () {
                    var element = document.getElementById('dialogPath');
                    element.contentEditable = true;
                    element.focus();
                }, 200);
            },
            onClose: function (event) {
                closeCallback(); // this should probably be a parameter
            },
            onMax: function (event) {
                console.log('max');
            },
            onMin: function (event) {
                console.log('min');
            },
            onKeydown: function (event) {
                console.log('keydown');
            }
        });
        $("#dialogSave").click(function () {
            completeFunction();
            w2popup.close();
        });
        FileTree.createTreeOfDepth(treePath, "dialog", "dialogTree", "/" + document.title, true, null, function (event, data) {
            var selectedFileDetails = FileTree.createResourcePath(data.node.tooltip);
            var selectedDirectory = selectedFileDetails.projectDirectory;
            if (selectedDirectory.indexOf("/") == 0) {
                selectedDirectory = selectedDirectory.substring(1);
            }
            $('#dialogPath').html(FileTree.cleanResourcePath(selectedDirectory));
        }, 2);
    }
    DialogBuilder.createTreeOpenDialog = createTreeOpenDialog;
    function createListDialog(listFunction, patternList, dialogTitle) {
        w2popup.open({
            title: dialogTitle,
            body: createDialogLayout('', patternList, true),
            buttons: '<button id="dialogSave" class="btn dialogButton">Cancel</button>',
            width: 800,
            height: 400,
            overflow: 'hidden',
            color: '#999',
            speed: '0.3',
            opacity: '0.8',
            modal: true,
            showClose: true,
            showMax: true,
            onOpen: function (event) {
                setTimeout(function () {
                    $('#dialogPath').on('change keyup paste', function () {
                        var expressionText = $("#dialogPath").html();
                        var expressionPattern = null;
                        if (patternList) {
                            expressionPattern = $("#dialogFolder").html();
                            expressionPattern = clearHtml(expressionPattern);
                        }
                        if (expressionText) {
                            expressionText = clearHtml(expressionText);
                        }
                        listFunction(expressionText, expressionPattern, function (list) {
                            var content = "<table class='dialogListTable' width='100%'>";
                            for (var i = 0; i < list.length; i++) {
                                var row = list[i];
                                content += "<tr>";
                                for (var j = 0; j < row.length; j++) {
                                    var cell = row[j];
                                    content += "<td width='50%'><div class='";
                                    content += cell.style;
                                    content += "' onclick='return DialogBuilder.submitDialogListResource";
                                    if (cell.line) {
                                        content += "(\"";
                                        content += cell.resource;
                                        content += "\", ";
                                        content += cell.line;
                                        content += ")";
                                    }
                                    else {
                                        content += "(\""; // ("link")
                                        content += cell.link;
                                        content += "\")";
                                    }
                                    content += "'>";
                                    content += cell.text;
                                    content += "</div></td>";
                                }
                                content += "<td>&nbsp;&nbsp;&nbsp;&nbsp;</td>";
                                content += "</tr>";
                            }
                            content += "</table>";
                            $("#dialog").html(content);
                        });
                    });
                    var element = document.getElementById('dialogPath');
                    element.contentEditable = true;
                    element.focus();
                }, 200);
            },
            onClose: function (event) {
                console.log('close');
            },
            onMax: function (event) {
                console.log('max');
            },
            onMin: function (event) {
                console.log('min');
            },
            onKeydown: function (event) {
                console.log('keydown');
            }
        });
        $("#dialogSave").click(function () {
            w2popup.close();
        });
        $("#dialogCancel").click(function () {
            w2popup.close();
        });
    }
    DialogBuilder.createListDialog = createListDialog;
    function createGridDialog(listFunction, inputText, dialogTitle) {
        inputText = (inputText ? escapeHtml(inputText) : '');
        w2popup.open({
            title: dialogTitle,
            body: createDialogLayout(inputText, null, false),
            buttons: '<button id="dialogSave" class="btn dialogButton">Evaluate</button>',
            width: 700,
            height: 400,
            overflow: 'hidden',
            color: '#999',
            speed: '0.3',
            opacity: '0.8',
            modal: false,
            showClose: true,
            showMax: true,
            onOpen: function (event) {
                setTimeout(function () {
                    $('#dialog').w2grid({
                        recordTitles: false,
                        name: 'evaluation',
                        columns: [{
                                field: 'name',
                                caption: 'Name',
                                size: '40%',
                                sortable: false
                            }, {
                                field: 'value',
                                caption: 'Value',
                                size: '30%',
                                sortable: false
                            }, {
                                field: 'type',
                                caption: 'Type',
                                size: '30%'
                            }],
                        onClick: function (event) {
                            var grid = this;
                            event.onComplete = function () {
                                var sel = grid.getSelection();
                                if (sel.length == 1) {
                                    var record = grid.get(sel[0]);
                                    var text = $("#dialogPath").html();
                                    var expression = clearHtml(text);
                                    toggleExpandEvaluation(record.path, expression);
                                }
                                grid.selectNone();
                                grid.refresh();
                            };
                        }
                    });
                    setTimeout(function () {
                        showVariables();
                    }, 200);
                }, 200);
            },
            onClose: function (event) {
                w2ui['evaluation'].destroy(); // destroy grid so you can recreate it
                //$("#dialog").remove(); // delete the element
                clearEvaluation();
                Command.browseScriptEvaluation([], "", true); // clear the variables
            },
            onMax: function (event) {
                event.onComplete = function () {
                    w2ui['evaluation'].refresh(); // resize
                };
            },
            onMin: function (event) {
                event.onComplete = function () {
                    w2ui['evaluation'].refresh(); // resize
                };
            },
            onKeydown: function (event) {
                console.log('keydown');
            }
        });
        $("#dialogSave").click(function () {
            var text = $("#dialogPath").html();
            var expression = clearHtml(text);
            Command.browseScriptEvaluation([], expression, true); // clear the variables
        });
    }
    function createDialogLayout(firstInputText, secondInputText, secondInputEditable) {
        if (secondInputText) {
            var clickEvent = '';
            if (secondInputEditable) {
                clickEvent = ' onclick="this.contentEditable=\'true\';"';
            }
            if (!secondInputText) {
                secondInputText = '';
            }
            if (!firstInputText) {
                firstInputText = '';
            }
            return '<div id="dialogContainer">' +
                '   <div id="dialog"></div>' +
                '</div>' +
                '<div id="dialogFolder" ' + clickEvent + '>' + secondInputText + '</div>' +
                '<div id="dialogPath" onkeydown="return DialogBuilder.submitDialog(event);" onclick="this.contentEditable=\'true\';">' + firstInputText + '</div>';
        }
        if (firstInputText) {
            return '<div id="dialogContainerBig">' +
                '   <div id="dialog"></div>' +
                '</div>' +
                '<div id="dialogPath" onkeydown="return DialogBuilder.submitDialog(event);" onclick="this.contentEditable=\'true\';">' + firstInputText + '</div>';
        }
        return '<div id="dialogContainerBig">' +
            '   <div id="dialog"></div>' +
            '</div>' +
            '<div id="dialogPath" onkeydown="return DialogBuilder.submitDialog(event);" onclick="this.contentEditable=\'true\';"></div>';
    }
    function submitDialogListResource(resource, line) {
        $("#dialogSave").click(); // force the click
        if (line) {
            FileExplorer.openTreeFile(resource, function () {
                window.setTimeout(function () {
                    FileEditor.showEditorLine(line);
                }, 100); // delay focus on line, some bug here that needs a delay 
            });
        }
        else {
            location.href = resource;
        }
        return false;
    }
    DialogBuilder.submitDialogListResource = submitDialogListResource;
    function submitDialog(e) {
        var evt = e || window.event;
        // "e" is the standard behavior (FF, Chrome, Safari, Opera),
        // while "window.event" (or "event") is IE's behavior
        if (evt.keyCode === 13) {
            $("#dialogSave").click(); // force the click
            // Do something
            // You can disable the form submission this way:
            return false;
        }
    }
    DialogBuilder.submitDialog = submitDialog;
})(DialogBuilder || (DialogBuilder = {}));
ModuleSystem.registerModule("dialog", "Dialog module: dialog.js", null, ["common", "tree"]);

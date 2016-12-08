function openAlertDialog(message) {
    w2popup.open({
        title: 'Alert',
        body: '<div class="dialog">' +
            '    <div style="style="text-align: center">' +
            '       <div style="display: inline-block;">' + message + '</div>' +
            '    </div>' +
            '</div>',
        buttons: '<button class="btn" onclick="w2popup.close();">Close</button>',
        width: 500,
        height: 300,
        overflow: 'hidden',
        color: '#333',
        speed: '0.3',
        opacity: '0.8',
        modal: true,
        showClose: true,
        showMax: true,
        onOpen: function (event) {
            console.log('open');
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
}
function openTreeDialog(resourceDetails, foldersOnly, saveCallback) {
    if (resourceDetails != null) {
        createProjectDialog(resourceDetails, foldersOnly, saveCallback, "Save Changes");
    }
    else {
        createProjectDialog(resourceDetails, foldersOnly, saveCallback, "Save As");
    }
}
function renameFileTreeDialog(resourceDetails, foldersOnly, saveCallback) {
    createProjectDialog(resourceDetails, foldersOnly, saveCallback, "Rename File");
}
function renameDirectoryTreeDialog(resourceDetails, foldersOnly, saveCallback) {
    createProjectDialog(resourceDetails, foldersOnly, saveCallback, "Rename Directory");
}
function newFileTreeDialog(resourceDetails, foldersOnly, saveCallback) {
    createProjectDialog(resourceDetails, foldersOnly, saveCallback, "New File");
}
function newDirectoryTreeDialog(resourceDetails, foldersOnly, saveCallback) {
    createProjectDialog(resourceDetails, foldersOnly, saveCallback, "New Directory");
}
function evaluateExpressionDialog() {
    createGridDialog(function (x) { return []; }, "Evaluate Expression");
}
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
        body: '<div id="dialogContainer">' +
            '   <div id="dialog"></div>' +
            '</div>' +
            '<div id="dialogFolder">' + dialogExpandPath + '</div>' +
            '<div id="dialogFile" onkeydown="return submitDialog(event);" onclick="this.contentEditable=\'true\';"></div>',
        buttons: '<button id="dialogSave" class="btn">Save</button><button id="dialogCancel" class="btn">Cancel</button>',
        width: 500,
        height: 400,
        overflow: 'hidden',
        color: '#333',
        speed: '0.3',
        opacity: '0.8',
        modal: true,
        showClose: true,
        showMax: true,
        onOpen: function (event) {
            setTimeout(function () {
                var element = document.getElementById('dialogFile');
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
        var originalDialogFileName = $('#dialogFile').html();
        var originalDialogFolder = $('#dialogFolder').html();
        var dialogFileName = cleanResourcePath(originalDialogFileName);
        var dialogFolder = cleanResourcePath(originalDialogFolder);
        var dialogProjectPath = dialogFolder + "/" + dialogFileName; // /src/blah/script.snap
        var dialogPathDetails = createResourcePath(dialogProjectPath);
        saveCallback(dialogPathDetails);
        w2popup.close();
    });
    $("#dialogCancel").click(function () {
        w2popup.close();
    });
    if (resourceDetails != null) {
        $('#dialogFolder').html(cleanResourcePath(resourceDetails.projectDirectory)); // /src/blah
        $('#dialogFile').html(cleanResourcePath(resourceDetails.fileName)); // script.snap
    }
    createTree(treePath, "dialog", "dialogTree", dialogExpandPath, foldersOnly, null, function (event, data) {
        var selectedFileDetails = createResourcePath(data.node.tooltip);
        if (data.node.isFolder()) {
            $('#dialogFolder').html(cleanResourcePath(selectedFileDetails.projectDirectory));
            $('#dialogFile').html("");
        }
        else {
            $('#dialogFolder').html(cleanResourcePath(selectedFileDetails.projectDirectory)); // /src/blah
            $('#dialogFile').html(cleanResourcePath(selectedFileDetails.fileName)); // file.snap
        }
    });
}
function createTreeOpenDialog(openCallback, closeCallback, dialogTitle, buttonText, treePath) {
    var completeFunction = function () {
        var originalDialogFolder = $('#dialogPath').html();
        var dialogFolder = cleanResourcePath(originalDialogFolder); // clean up path
        var dialogPathDetails = createResourcePath(dialogFolder);
        var selectedDirectory = dialogPathDetails.projectDirectory;
        if (selectedDirectory.startsWith("/")) {
            selectedDirectory = selectedDirectory.substring(1);
        }
        openCallback(dialogPathDetails, selectedDirectory);
    };
    w2popup.open({
        title: dialogTitle,
        body: '<div id="dialogContainerBig">' +
            '   <div id="dialog"></div>' +
            '</div>' +
            '<div id="dialogPath" onkeydown="return submitDialog(event);" onclick="this.contentEditable=\'true\';"></div>',
        buttons: '<button id="dialogSave" class="btn">' + buttonText + '</button>',
        width: 500,
        height: 400,
        overflow: 'hidden',
        color: '#333',
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
    createTreeOfDepth(treePath, "dialog", "dialogTree", "/" + document.title, true, null, function (event, data) {
        var selectedFileDetails = createResourcePath(data.node.tooltip);
        var selectedDirectory = selectedFileDetails.projectDirectory;
        if (selectedDirectory.startsWith("/")) {
            selectedDirectory = selectedDirectory.substring(1);
        }
        $('#dialogPath').html(cleanResourcePath(selectedDirectory));
    }, 2);
}
function createListDialog(listFunction, dialogTitle) {
    w2popup.open({
        title: dialogTitle,
        body: '<div id="dialogContainerBig">' +
            '   <div id="dialog"></div>' +
            '</div>' +
            '<div id="dialogPath" onkeydown="return submitDialog(event);" onclick="this.contentEditable=\'true\';"></div>',
        buttons: '<button id="dialogSave" class="btn">Cancel</button>',
        width: 600,
        height: 400,
        overflow: 'hidden',
        color: '#333',
        speed: '0.3',
        opacity: '0.8',
        modal: true,
        showClose: true,
        showMax: true,
        onOpen: function (event) {
            setTimeout(function () {
                $('#dialogPath').on('change keyup paste', function () {
                    var text = $("#dialogPath").html();
                    var expression = text.replace("<br>", "");
                    var list = listFunction(expression);
                    var content = "<table class='dialogListTable' width='100%'>";
                    for (var i = 0; i < list.length; i++) {
                        var row = list[i];
                        content += "<tr>";
                        for (var j = 0; j < row.length; j++) {
                            var cell = row[j];
                            content += "<td width='50%'><div class='";
                            content += cell.style;
                            content += "' onclick='return submitDialogListResource(\"";
                            content += cell.link;
                            content += "\")'>";
                            content += cell.text;
                            content += "</div></td>";
                        }
                        content += "<td>&nbsp;&nbsp;&nbsp;&nbsp;</td>";
                        content += "</tr>";
                    }
                    content += "</table>";
                    $("#dialog").html(content);
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
function createGridDialog(listFunction, dialogTitle) {
    w2popup.open({
        title: dialogTitle,
        body: '<div id="dialogContainerBig">' +
            '   <div id="dialog"></div>' +
            '</div>' +
            '<div id="dialogPath" onkeydown="return submitDialog(event);" onclick="this.contentEditable=\'true\';"></div>',
        buttons: '<button id="dialogSave" class="btn">Evaluate</button>',
        width: 700,
        height: 400,
        overflow: 'hidden',
        color: '#333',
        speed: '0.0',
        opacity: '0.0',
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
                                var expression = text.replace("<br>", "");
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
            browseScriptEvaluation([], "", true); // clear the variables
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
        var expression = text.replace("<br>", "");
        browseScriptEvaluation([], expression, true); // clear the variables
    });
}
function submitDialogListResource(resource) {
    location.href = resource;
    $("#dialogSave").click(); // force the click
    return false;
}
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
registerModule("dialog", "Dialog module: dialog.js", null, ["common", "tree"]);

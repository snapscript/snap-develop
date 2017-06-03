define(["require", "exports", "jquery", "w2ui", "./console", "./problem", "./editor", "./tree", "./threads", "./history", "./variables", "./explorer", "./commands", "./debug"], function (require, exports, $, w2ui_1, console_1, problem_1, editor_1, tree_1, threads_1, history_1, variables_1, explorer_1, commands_1, debug_1) {
    "use strict";
    var Project;
    (function (Project) {
        var currentDisplayInfo = {};
        var doubleClickTimes = {};
        function createMainLayout() {
            var perspective = determineProjectLayout();
            if (perspective == "debug") {
                createDebugLayout(); // show debug layout
            }
            else {
                createExploreLayout();
            }
            $(window).trigger('resize'); // force a redraw after w2ui
        }
        Project.createMainLayout = createMainLayout;
        function startMainLayout() {
            var perspective = determineProjectLayout();
            if (perspective == "debug") {
                startDebugLayout(); // show debug layout
            }
            else {
                startExploreLayout();
            }
            startResizePoller(); // dynamically resize the editor
            attachClickEvents();
        }
        Project.startMainLayout = startMainLayout;
        function attachClickEvents() {
            $('#toolbarNavigateBack').on('click', function (e) {
                history_1.History.navigateBackward();
                e.preventDefault();
            });
            $('#toolbarNavigateForward').on('click', function (e) {
                history_1.History.navigateForward();
                e.preventDefault();
            });
            $('#editorTheme').on('click', function (e) {
                Project.changeEditorTheme();
                e.preventDefault();
            });
            $('#fontFamily').on('click', function (e) {
                Project.changeProjectFont();
                e.preventDefault();
            });
            $('#fontSize').on('click', function (e) {
                Project.changeProjectFont();
                e.preventDefault();
            });
            $('#newFile').on('click', function (e) {
                commands_1.Command.newFile(null);
                e.preventDefault();
            });
            $('#saveFile').on('click', function (e) {
                commands_1.Command.saveFile(null);
                e.preventDefault();
            });
            $('#deleteFile').on('click', function (e) {
                commands_1.Command.deleteFile(null);
                e.preventDefault();
            });
            $('#searchTypes').on('click', function (e) {
                commands_1.Command.searchTypes();
                e.preventDefault();
            });
            $('#runScript').on('click', function (e) {
                commands_1.Command.runScript();
                e.preventDefault();
            });
            $('#stopScript').on('click', function (e) {
                commands_1.Command.stopScript();
                e.preventDefault();
            });
            $('#resumeScript').on('click', function (e) {
                commands_1.Command.resumeScript();
                e.preventDefault();
            });
            $('#stepInScript').on('click', function (e) {
                commands_1.Command.stepInScript();
                e.preventDefault();
            });
            $('#stepOutScript').on('click', function (e) {
                commands_1.Command.stepOutScript();
                e.preventDefault();
            });
            $('#stepOverScript').on('click', function (e) {
                commands_1.Command.stepOverScript();
                e.preventDefault();
            });
            $('#evaluateExpression').on('click', function (e) {
                commands_1.Command.evaluateExpression();
                e.preventDefault();
            });
        }
        function determineProjectLayout() {
            var debugToggle = ";debug";
            var locationPath = window.document.location.pathname;
            var locationHash = window.document.location.hash;
            var debug = locationPath.indexOf(debugToggle, locationPath.length - debugToggle.length) !== -1;
            if (debug) {
                return "debug";
            }
            return "explore";
        }
        function startResizePoller() {
            var editorWidth = 0;
            var editorHeight = 0;
            setInterval(function () {
                var editorElement = document.getElementById("editor");
                if (editorElement != null) {
                    var currentWidth = editorElement.offsetWidth;
                    var currentHeight = editorElement.offsetHeight;
                    if (editorWidth != currentWidth || editorHeight != currentHeight) {
                        editorWidth = currentWidth;
                        editorHeight = currentHeight;
                        editor_1.FileEditor.resizeEditor();
                    }
                }
            }, 100);
        }
        function changeProjectFont() {
            var fontFamily = document.getElementById("fontFamily");
            var fontSize = document.getElementById("fontSize");
            if (fontSize != null && fontFamily != null) {
                var fontSizeOption = fontSize.options[fontSize.selectedIndex];
                var fontFamilyOption = fontFamily.options[fontFamily.selectedIndex];
                var fontSizeValue = fontSizeOption.value;
                var fontFamilyValue = fontFamilyOption.value;
                editor_1.FileEditor.updateEditorFont(fontFamilyValue, fontSizeValue);
                console_1.ProcessConsole.updateConsoleFont(fontFamilyValue, fontSizeValue);
                var displayInfo = currentProjectDisplay();
                commands_1.Command.updateDisplay(displayInfo);
            }
        }
        Project.changeProjectFont = changeProjectFont;
        function changeEditorTheme() {
            var editorTheme = document.getElementById("editorTheme");
            if (editorTheme != null) {
                var themeOption = editorTheme.options[editorTheme.selectedIndex];
                var themeName = themeOption.value.toLowerCase();
                editor_1.FileEditor.setEditorTheme("ace/theme/" + themeName);
                var displayInfo = currentProjectDisplay();
                commands_1.Command.updateDisplay(displayInfo);
                if (isProjectThemeChange(displayInfo.themeName)) {
                    commands_1.Command.refreshScreen(); // refresh the whole screen
                }
            }
        }
        Project.changeEditorTheme = changeEditorTheme;
        function toggleFullScreen() {
            var perspective = determineProjectLayout();
            if (perspective == "debug") {
                var topPanel = w2ui_1.w2ui['debugEditorLayout'].get("top");
                var bottomPanel = w2ui_1.w2ui['debugEditorLayout'].get("bottom");
                if (topPanel.hidden || bottomPanel.hidden) {
                    w2ui_1.w2ui['debugEditorLayout'].show("top");
                    w2ui_1.w2ui['debugEditorLayout'].show("bottom");
                }
                else {
                    w2ui_1.w2ui['debugEditorLayout'].hide("top");
                    w2ui_1.w2ui['debugEditorLayout'].hide("bottom");
                }
            }
            else {
                var leftPanel = w2ui_1.w2ui['exploreMainLayout'].get("left");
                var bottomPanel = w2ui_1.w2ui['exploreEditorLayout'].get("bottom");
                if (leftPanel.hidden || bottomPanel.hidden) {
                    w2ui_1.w2ui['exploreMainLayout'].show("left", true);
                    w2ui_1.w2ui['exploreEditorLayout'].show("bottom");
                }
                else {
                    w2ui_1.w2ui['exploreMainLayout'].hide("left", true);
                    w2ui_1.w2ui['exploreEditorLayout'].hide("bottom");
                }
            }
        }
        Project.toggleFullScreen = toggleFullScreen;
        function isProjectThemeChange(name) {
            if (currentDisplayInfo) {
                return currentDisplayInfo.themeName != name.toLowerCase(); // if they are not the same
            }
            return false;
        }
        function currentProjectDisplay() {
            var fontFamily = document.getElementById("fontFamily");
            var fontSize = document.getElementById("fontSize");
            var editorTheme = document.getElementById("editorTheme");
            return {
                consoleCapacity: 50000,
                themeName: editorTheme.value.toLowerCase().trim(),
                fontSize: fontSize.value.toLowerCase().replace("px", "").trim(),
                fontName: fontFamily.value
            };
        }
        function applyProjectTheme() {
            $.get("/display/" + document.title, function (displayInfo) {
                currentDisplayInfo = displayInfo; // save display info
                if (displayInfo.fontName != null && displayInfo.fontSize != null) {
                    var fontFamily = document.getElementById("fontFamily");
                    var fontSize = document.getElementById("fontSize");
                    var editorTheme = document.getElementById("editorTheme");
                    if (fontSize != null) {
                        fontSize.value = displayInfo.fontSize + "px";
                    }
                    if (fontFamily != null) {
                        fontFamily.value = displayInfo.fontName;
                    }
                    if (editorTheme != null && displayInfo.themeName != null) {
                        editorTheme.value = displayInfo.themeName;
                    }
                    if (displayInfo.consoleCapacity != null) {
                        console_1.ProcessConsole.updateConsoleCapacity(Math.max(displayInfo.consoleCapacity, 5000)); // don't allow stupidly small size
                    }
                    if (displayInfo.logoImage != null) {
                        var toolbarRow = document.getElementById("toolbarRow"); // this is pretty rubbish, but it works!
                        toolbarRow.insertCell(0).innerHTML = "<div class='toolbarSeparator'></div>";
                        toolbarRow.insertCell(0).innerHTML = "&nbsp;";
                        toolbarRow.insertCell(0).innerHTML = "&nbsp;";
                        toolbarRow.insertCell(0).innerHTML = "<div><img style='height: 25px; margin-top: -1px;' src='" + displayInfo.logoImage + "'></div>"; // /img/logo_grey_shade.png
                    }
                }
                changeProjectFont(); // update the fonts
                changeEditorTheme(); // change editor theme
            });
        }
        function showBrowseTreeContent(containsBrowse) {
            if (containsBrowse) {
                // move the explorer
                var newParent = document.getElementById('browseParent');
                var oldParent = document.getElementById('browseParentHidden');
                if (oldParent != null && newParent != null) {
                    while (oldParent.childNodes.length > 0) {
                        newParent.appendChild(oldParent.childNodes[0]);
                    }
                }
            }
        }
        function hideBrowseTreeContent(containsBrowse) {
            if (containsBrowse) {
                // move the explorer
                var newParent = document.getElementById('browseParentHidden');
                var oldParent = document.getElementById('browseParent');
                if (oldParent != null && newParent != null) {
                    while (oldParent.childNodes.length > 0) {
                        newParent.appendChild(oldParent.childNodes[0]);
                    }
                }
            }
        }
        function showEditorContent(containsEditor) {
            if (containsEditor) {
                // move the explorer
                var newParent = document.getElementById('editParent');
                var oldParent = document.getElementById('editParentHidden');
                if (oldParent != null && newParent != null) {
                    while (oldParent.childNodes.length > 0) {
                        newParent.appendChild(oldParent.childNodes[0]);
                    }
                }
                updateEditorTabName();
            }
        }
        function clickOnTab(name, doubleClickFunction) {
            var currentTime = new Date().getTime();
            var previousTime = doubleClickTimes[name];
            if (previousTime) {
                if ((currentTime - previousTime) < 200) {
                    doubleClickFunction();
                }
            }
            doubleClickTimes[name] = currentTime;
        }
        Project.clickOnTab = clickOnTab;
        function updateEditorTabName() {
            var editorData = editor_1.FileEditor.loadEditor();
            var editorFileName = document.getElementById("editFileName");
            if (editorFileName != null) {
                var editorData = editor_1.FileEditor.loadEditor();
                if (editorData != null && editorData.resource != null) {
                    editorFileName.innerHTML = "<span title='" + editorData.resource.resourcePath + "'>&nbsp;" + editorData.resource.fileName + "&nbsp;</span>";
                }
            }
        }
        function findActiveEditorLayout() {
            var tabs = w2ui_1.w2ui['exploreEditorTabLayout'];
            if (tabs == null) {
                return w2ui_1.w2ui['debugEditorTabLayout'];
            }
            return tabs;
        }
        function findActiveEditorTabLayout() {
            var tabs = findActiveEditorLayout();
            if (tabs != null) {
                return tabs.panels[0].tabs;
            }
            return null;
        }
        function deleteEditorTab(resource) {
            var layout = findActiveEditorLayout();
            var tabs = findActiveEditorTabLayout();
            if (tabs != null && resource != null) {
                var removeTab = tabs.get(resource);
                if (removeTab.closable) {
                    tabs.remove(resource); // remove the tab
                    if (removeTab.active) {
                        activateAnyEditorTab(resource); // if it was active then activate another
                    }
                }
            }
        }
        Project.deleteEditorTab = deleteEditorTab;
        function renameEditorTab(from, to) {
            var layout = findActiveEditorLayout();
            var tabs = findActiveEditorTabLayout();
            var editorData = editor_1.FileEditor.loadEditor();
            if (tabs != null && from != null && to != null) {
                var tabList = tabs.tabs;
                var count = 0;
                for (var i = 0; i < tabList.length; i++) {
                    var nextTab = tabList[i];
                    if (nextTab != null && nextTab.id == from) {
                        var newTab = JSON.parse(JSON.stringify(nextTab)); // clone the tab
                        var toPath = tree_1.FileTree.createResourcePath(to);
                        var fromPath = tree_1.FileTree.createResourcePath(from);
                        tabs.remove(nextTab.id); // remove the tab
                        if (nextTab.active) {
                            explorer_1.FileExplorer.openTreeFile(toPath.resourcePath, function () { }); // browse style makes no difference here
                        }
                        else {
                            var fileNameReplace = new RegExp(fromPath.fileName, "g");
                            var filePathReplace = new RegExp(fromPath.resourcePath, "g");
                            newTab.caption = newTab.caption.replace(fileNameReplace, toPath.fileName).replace(filePathReplace, toPath.resourcePath); // rename the tab
                            newTab.text = newTab.text.replace(fileNameReplace, toPath.fileName).replace(filePathReplace, toPath.resourcePath); // rename the tab
                            newTab.id = toPath.resourcePath;
                            tabs.add(newTab);
                        }
                        break;
                    }
                }
            }
        }
        Project.renameEditorTab = renameEditorTab;
        function createEditorTab() {
            var layout = findActiveEditorLayout();
            var tabs = findActiveEditorTabLayout();
            var editorData = editor_1.FileEditor.loadEditor();
            if (tabs != null && editorData != null && editorData.resource != null) {
                var tabList = tabs.tabs;
                var tabResources = {};
                for (var i = 0; i < tabList.length; i++) {
                    var nextTab = tabList[i];
                    if (nextTab != null && nextTab.id != 'editTab') {
                        tabResources[nextTab.id] = {
                            id: nextTab.id,
                            caption: nextTab.caption.replace('id="editFileName"', "").replace("id='editFileName'", ""),
                            content: "",
                            closable: true,
                            active: false
                        };
                    }
                }
                tabResources[editorData.resource.resourcePath] = {
                    id: editorData.resource.resourcePath,
                    caption: "<div class='editTab' id='editFileName'><span title='" + editorData.resource.resourcePath + "'>&nbsp;" + editorData.resource.fileName + "&nbsp;</span></div>",
                    content: "<div style='overflow: scroll; font-family: monospace;' id='edit'><div id='editParent'></div></div>",
                    closable: true,
                    active: true
                };
                var sortedNames = [];
                var sortedTabs = [];
                for (var tabResource in tabResources) {
                    if (tabResources.hasOwnProperty(tabResource)) {
                        sortedNames.push(tabResource); // add a '.' to ensure dot notation sorts e.g x.y.z
                    }
                }
                sortedNames.sort();
                for (var i = 0; i < sortedNames.length; i++) {
                    var tabResource = sortedNames[i];
                    var nextTab = tabResources[tabResource];
                    nextTab.closable = sortedNames.length > 1; // if only one tab make sure it cannot be closed
                    sortedTabs[i] = nextTab;
                }
                tabs.tabs = sortedTabs;
                tabs.active = editorData.resource.resourcePath;
                activateTab(editorData.resource.resourcePath, layout.name, false, true, ""); // browse style makes no difference here
                // this is pretty rubbish, it would be good if there was a promise after redraw/repaint
                setTimeout(function () {
                    $('#editFileName').on('click', function (e) {
                        Project.clickOnTab(editorData.resource.resourcePath, Project.toggleFullScreen);
                        e.preventDefault();
                    });
                }, 100);
            }
        }
        Project.createEditorTab = createEditorTab;
        function activateAnyEditorTab(resourcePathDeleted) {
            var layout = findActiveEditorLayout();
            var tabs = findActiveEditorTabLayout();
            if (tabs != null) {
                var tabList = tabs.tabs;
                for (var i = 0; i < tabList.length; i++) {
                    var nextTab = tabList[i];
                    if (nextTab != null && nextTab.id == resourcePathDeleted) {
                        nextTab.id = 'editTab'; // make sure not to enable, bit of a hack
                        nextTab.closable = true;
                    }
                }
                for (var i = 0; i < tabList.length; i++) {
                    var nextTab = tabList[i];
                    if (nextTab != null && nextTab.id != 'editTab') {
                        tabs.active = nextTab.id;
                        tabs.closable = false;
                        explorer_1.FileExplorer.openTreeFile(nextTab.id, function () { }); // browse style makes no difference here
                        break;
                    }
                }
            }
        }
        function hideEditorContent(containsEditor) {
            if (containsEditor) {
                // move the editor
                var newParent = document.getElementById('editParentHidden');
                var oldParent = document.getElementById('editParent');
                if (oldParent != null && newParent != null) {
                    while (oldParent.childNodes.length > 0) {
                        newParent.appendChild(oldParent.childNodes[0]);
                    }
                }
            }
        }
        function createExploreLayout() {
            // $('#topLayer').spin({ lines: 10, length: 30, width: 20, radius: 40 });
            // -- LAYOUT
            var pstyle = 'background-color: ${PROJECT_BACKGROUND_COLOR}; overflow: hidden;';
            $('#mainLayout').w2layout({
                name: 'exploreMainLayout',
                padding: 0,
                panels: [{
                        type: 'top',
                        size: '40px',
                        resizable: false,
                        style: pstyle
                    }, {
                        type: 'left',
                        size: '20%',
                        resizable: true,
                        style: pstyle
                    }, {
                        type: 'right',
                        size: '20%',
                        resizable: true,
                        hidden: true,
                        style: pstyle
                    }, {
                        type: 'main',
                        size: '80%',
                        resizable: true,
                        style: pstyle
                    }, {
                        type: 'bottom',
                        size: '25px',
                        resizable: false,
                        style: pstyle,
                        content: createBottomStatusContent()
                    }]
            });
            $('').w2layout({
                name: 'exploreEditorLayout',
                padding: 0,
                panels: [{
                        type: 'main',
                        size: '60%',
                        resizable: true,
                        overflow: 'auto',
                        style: pstyle + 'border-bottom: 0px;'
                    }, {
                        type: 'bottom',
                        size: '40%',
                        overflow: 'auto',
                        resizable: true,
                        style: pstyle + 'border-top: 0px;'
                    }]
            });
            $('').w2layout({
                name: 'exploreEditorTabLayout',
                padding: 0,
                panels: [{
                        type: 'main',
                        size: '100%',
                        style: pstyle + 'border-top: 0px;',
                        resizable: false,
                        name: 'editTabs',
                        tabs: {
                            active: 'editTab',
                            tabs: [{
                                    id: 'editTab',
                                    caption: '<div class="editTab" id="editFileName">...</div>',
                                    content: "<div style='overflow: scroll; font-family: monospace;' id='edit'><div id='editParent'></div></div>",
                                    closable: true
                                }],
                            onClick: function (event) {
                                explorer_1.FileExplorer.openTreeFile(event.target, function () { });
                            },
                            onClose: function (event) {
                                activateAnyEditorTab(event.target);
                            }
                        }
                    }]
            });
            $('').w2layout({
                name: 'exploreLeftTabLayout',
                padding: 0,
                panels: [{
                        type: 'main',
                        size: '100%',
                        style: pstyle + 'border-top: 0px;',
                        resizable: false,
                        name: 'tabs',
                        tabs: {
                            active: 'browseTab',
                            right: '<div id="navigateToTreeArrow" onclick="FileEditor.showEditorFileInTree()"></div>',
                            tabs: [{
                                    id: 'browseTab',
                                    caption: '<div class="browseTab">Project&nbsp;</div>',
                                    content: "<div style='overflow: scroll; font-family: monospace;' id='browse'><div id='browseParent'><div id='explorer'></div></div></div>",
                                    closable: false
                                }],
                            onClick: function (event) {
                                activateTab(event.target, "exploreLeftTabLayout", true, false, "style='right: 0px;'");
                            }
                        }
                    }]
            });
            $('').w2layout({
                name: 'exploreBottomTabLayout',
                padding: 0,
                panels: [{
                        type: 'main',
                        size: '100%',
                        style: pstyle + 'border-top: 0px;',
                        resizable: false,
                        name: 'tabs',
                        tabs: {
                            active: 'consoleTab',
                            tabs: [{
                                    id: 'consoleTab',
                                    caption: '<div class="consoleTab">Console</div>',
                                    closable: false
                                }, {
                                    id: 'problemsTab',
                                    caption: '<div class="problemsTab">Problems</div>',
                                    closable: false
                                }, {
                                    id: 'breakpointsTab',
                                    caption: '<div class="breakpointsTab">Breakpoints</div>',
                                    closable: false
                                }, {
                                    id: 'threadsTab',
                                    caption: '<div class="threadTab">Threads</div>',
                                    closable: false
                                }, {
                                    id: 'variablesTab',
                                    caption: '<div class="variableTab">Variables</div>',
                                    closable: false
                                }, {
                                    id: 'profilerTab',
                                    caption: '<div class="profilerTab">Profiler</div>',
                                    closable: false
                                }, {
                                    id: 'debugTab',
                                    caption: '<div class="debugTab">Debug&nbsp;&nbsp;</div>',
                                    closable: false
                                }, {
                                    id: 'historyTab',
                                    caption: '<div class="historyTab">History&nbsp;&nbsp;</div>',
                                    closable: false
                                }],
                            onClose: function (event) {
                                console.log(event);
                            },
                            onClick: function (event) {
                                activateTab(event.target, "exploreBottomTabLayout", false, false, "style='right: 0px;'");
                            }
                        }
                    }]
            });
            createTopMenuBar(); // menu bar at top
            createProblemsTab();
            createVariablesTab();
            createProfilerTab();
            createBreakpointsTab();
            createDebugTab();
            createThreadsTab();
            createHistoryTab();
            w2ui_1.w2ui['exploreMainLayout'].content('top', w2ui_1.w2ui['topLayout']);
            w2ui_1.w2ui['exploreMainLayout'].content('left', w2ui_1.w2ui['exploreLeftTabLayout']);
            w2ui_1.w2ui['exploreMainLayout'].content('main', w2ui_1.w2ui['exploreEditorLayout']);
            w2ui_1.w2ui['exploreEditorLayout'].content('main', w2ui_1.w2ui['exploreEditorTabLayout']);
            w2ui_1.w2ui['exploreEditorLayout'].content('bottom', w2ui_1.w2ui['exploreBottomTabLayout']);
            w2ui_1.w2ui['exploreEditorTabLayout'].refresh();
            w2ui_1.w2ui['exploreBottomTabLayout'].refresh();
            w2ui_1.w2ui['exploreLeftTabLayout'].refresh();
        }
        function startExploreLayout() {
            applyProjectTheme();
            activateTab("consoleTab", "exploreBottomTabLayout", false, false, "style='right: 0px;'");
            activateTab("browseTab", "exploreLeftTabLayout", true, false, "style='right: 0px;'");
            activateTab("editTab", "exploreEditorTabLayout", false, true, "style='right: 0px;'");
            openDefaultResource();
        }
        function createDebugLayout() {
            // $('#topLayer').spin({ lines: 10, length: 30, width: 20, radius: 40 });
            // -- LAYOUT
            var pstyle = 'background-color: ${PROJECT_BACKGROUND_COLOR}; overflow: hidden;';
            $('#mainLayout').w2layout({
                name: 'debugMainLayout',
                padding: 0,
                panels: [{
                        type: 'top',
                        size: '40px',
                        resizable: false,
                        style: pstyle
                    }, {
                        type: 'main',
                        size: '80%',
                        resizable: true,
                        style: pstyle
                    }, {
                        type: 'bottom',
                        size: '25px',
                        resizable: false,
                        style: pstyle,
                        content: createBottomStatusContent()
                    }]
            });
            $('').w2layout({
                name: 'debugEditorLayout',
                padding: 0,
                panels: [{
                        type: 'top',
                        size: '25%',
                        overflow: 'auto',
                        resizable: true,
                        style: pstyle + 'border-top: 0px;'
                    }, {
                        type: 'main',
                        size: '50%',
                        resizable: true,
                        overflow: 'auto',
                        style: pstyle + 'border-bottom: 0px;'
                    }, {
                        type: 'bottom',
                        size: '25%',
                        overflow: 'auto',
                        resizable: true,
                        style: pstyle + 'border-top: 0px;'
                    }]
            });
            $('').w2layout({
                name: 'debugEditorTabLayout',
                padding: 0,
                panels: [{
                        type: 'main',
                        size: '100%',
                        style: pstyle + 'border-top: 0px;',
                        resizable: false,
                        name: 'editTabs',
                        tabs: {
                            active: 'editTab',
                            tabs: [{
                                    id: 'editTab',
                                    caption: '<div class="editTab" id="editFileName">...</div>',
                                    content: "<div style='overflow: scroll; font-family: monospace;' id='edit'><div id='editParent'></div></div>",
                                    closable: true
                                }],
                            onClick: function (event) {
                                explorer_1.FileExplorer.openTreeFile(event.target, function () { });
                            },
                            onClose: function (event) {
                                activateAnyEditorTab(event.target);
                            }
                        }
                    }]
            });
            $('').w2layout({
                name: 'debugTopTabSplit',
                padding: 0,
                panels: [{
                        type: 'left',
                        size: '50%',
                        overflow: 'auto',
                        resizable: true,
                        style: pstyle + 'border-top: 0px;'
                    }, {
                        type: 'main',
                        size: '50%',
                        resizable: true,
                        overflow: 'auto',
                        style: pstyle + 'border-bottom: 0px;'
                    }]
            });
            $('').w2layout({
                name: 'debugLeftTabLayout',
                padding: 0,
                panels: [{
                        type: 'main',
                        size: '100%',
                        style: pstyle + 'border-top: 0px;',
                        resizable: false,
                        name: 'tabs',
                        tabs: {
                            active: 'debugTab',
                            tabs: [{
                                    id: 'debugTab',
                                    caption: '<div class="debugTab">Debug&nbsp;&nbsp;</div>',
                                    closable: false
                                }, {
                                    id: 'threadsTab',
                                    caption: '<div class="threadTab">Threads</div>',
                                    closable: false
                                }, {
                                    id: 'browseTab',
                                    caption: '<div class="browseTab">Project</div>',
                                    content: "<div style='overflow: scroll; font-family: monospace;' id='browse'><div id='browseParent'></div></div>",
                                    closable: false
                                }],
                            onClick: function (event) {
                                activateTab(event.target, "debugLeftTabLayout", true, false, "");
                            }
                        }
                    }]
            });
            $('').w2layout({
                name: 'debugRightTabLayout',
                padding: 0,
                panels: [{
                        type: 'main',
                        size: '100%',
                        style: pstyle + 'border-top: 0px;',
                        resizable: false,
                        name: 'tabs',
                        tabs: {
                            active: 'variablesTab',
                            tabs: [{
                                    id: 'variablesTab',
                                    caption: '<div class="variableTab">Variables</div>',
                                    closable: false
                                }, {
                                    id: 'breakpointsTab',
                                    caption: '<div class="breakpointsTab">Breakpoints</div>',
                                    closable: false
                                }],
                            onClick: function (event) {
                                activateTab(event.target, "debugRightTabLayout", false, false, "");
                            }
                        }
                    }]
            });
            $('').w2layout({
                name: 'debugBottomTabLayout',
                padding: 0,
                panels: [{
                        type: 'main',
                        size: '100%',
                        style: pstyle + 'border-top: 0px;',
                        resizable: false,
                        name: 'tabs',
                        tabs: {
                            active: 'consoleTab',
                            tabs: [{
                                    id: 'consoleTab',
                                    caption: '<div class="consoleTab">Console</div>'
                                }, {
                                    id: 'problemsTab',
                                    caption: '<div class="problemsTab">Problems</div>'
                                }, {
                                    id: 'profilerTab',
                                    caption: '<div class="profilerTab">Profiler</div>'
                                }, {
                                    id: 'historyTab',
                                    caption: '<div class="historyTab">History&nbsp;&nbsp;</div>',
                                    closable: false
                                }],
                            onClick: function (event) {
                                activateTab(event.target, "debugBottomTabLayout", false, false, "");
                            }
                        }
                    }]
            });
            createTopMenuBar(); // menu bar at top
            createProblemsTab();
            createVariablesTab();
            createProfilerTab();
            createBreakpointsTab();
            createDebugTab();
            createThreadsTab();
            createHistoryTab();
            w2ui_1.w2ui['debugMainLayout'].content('top', w2ui_1.w2ui['topLayout']);
            w2ui_1.w2ui['debugMainLayout'].content('main', w2ui_1.w2ui['debugEditorLayout']);
            w2ui_1.w2ui['debugEditorLayout'].content('main', w2ui_1.w2ui['debugEditorTabLayout']);
            w2ui_1.w2ui['debugEditorLayout'].content('top', w2ui_1.w2ui['debugTopTabSplit']);
            w2ui_1.w2ui['debugTopTabSplit'].content('left', w2ui_1.w2ui['debugLeftTabLayout']);
            w2ui_1.w2ui['debugTopTabSplit'].content('main', w2ui_1.w2ui['debugRightTabLayout']);
            w2ui_1.w2ui['debugEditorLayout'].content('bottom', w2ui_1.w2ui['debugBottomTabLayout']);
            w2ui_1.w2ui['debugEditorTabLayout'].refresh();
            w2ui_1.w2ui['debugTopTabSplit'].refresh();
            w2ui_1.w2ui['debugLeftTabLayout'].refresh();
            w2ui_1.w2ui['debugRightTabLayout'].refresh();
            w2ui_1.w2ui['debugBottomTabLayout'].refresh();
        }
        function startDebugLayout() {
            applyProjectTheme();
            activateTab("debugTab", "debugLeftTabLayout", true, false, "");
            activateTab("variablesTab", "debugRightTabLayout", false, false, "");
            activateTab("consoleTab", "debugBottomTabLayout", false, false, "");
            activateTab("editTab", "debugEditorTabLayout", false, true, "");
            openDefaultResource();
        }
        function createBottomStatusContent() {
            return "<div id='status'>" +
                "  <table width='100%' height='100%'>" +
                "  <tr>" +
                "    <td width='50%' align='left'><div id='process'></div></td>" +
                "    <td width='50%' align='right'><div id='currentFile'></div></td>" +
                "  </tr>" +
                "  </table>" +
                "</div>";
        }
        function createTopMenuBar() {
            var pstyle = 'background-color: ${PROJECT_MENU_COLOR}; overflow: hidden;';
            $('#topLayout').w2layout({
                name: 'topLayout',
                padding: 0,
                panels: [
                    {
                        type: 'left',
                        size: '40%',
                        style: pstyle,
                        content: "<div class='toolbarTop'>"
                            + "<table border='0'>"
                            + "<tr id='toolbarRow'>"
                            + "   <td>"
                            + "      <table id='toolbarNormal'>"
                            + "      <tr>"
                            + "         <td><div id='newFile' title='New File&nbsp;&nbsp;&nbsp;Ctrl+N'></div></td>"
                            + "         <td><div id='saveFile' title='Save File&nbsp;&nbsp;&nbsp;Ctrl+S'></div></td>"
                            + "         <td><div id='deleteFile' title='Delete File'></div></td>"
                            + "         <td><div id='searchTypes' title='Search Types&nbsp;&nbsp;&nbsp;Ctrl+Shift+S'></div></td>"
                            + "         <td><div id='runScript' title='Run Script&nbsp;&nbsp;&nbsp;Ctrl+R'></div></td>"
                            + "      </tr>"
                            + "      </table>"
                            + "   </td>"
                            + "   <td><div class='toolbarSeparator'></div></td>"
                            + "   <td>"
                            + "      <table id='toolbarDebug'>"
                            + "      <tr>"
                            + "         <td><div id='stopScript' title='Stop Script'></div></td>"
                            + "         <td><div id='resumeScript' title='Resume Script&nbsp;&nbsp;&nbsp;F8'></div></td>"
                            + "         <td><div id='stepInScript' title='Step In&nbsp;&nbsp;&nbsp;F5'></div></td>"
                            + "         <td><div id='stepOutScript' title='Step Out&nbsp;&nbsp;&nbsp;F7'></div></td>"
                            + "         <td><div id='stepOverScript' title='Step Over&nbsp;&nbsp;&nbsp;F6'></div></td>"
                            + "         <td><div id='evaluateExpression' title='Evaluate Expression&nbsp;&nbsp;&nbsp;Ctrl+Shift+E'></div></td>"
                            + "      </tr>"
                            + "      </table>"
                            + "   </td>"
                            + "</tr>"
                            + "</table>"
                            + "</div>"
                    }, {
                        type: 'main',
                        size: '10%',
                        style: pstyle,
                        content: "<div class='toolbarTop'></div>"
                    }, {
                        type: 'right',
                        size: '50%',
                        style: pstyle,
                        content: "<div class='toolbarTop'>" +
                            "<table border='0' width='100%' cellpadding='0'>" +
                            "<tr>" +
                            "   <td  width='100%'></td>" +
                            "   <td><div id='toolbarNavigateBack' title='Navigate Back'></div></td>" +
                            "   <td><div id='toolbarNavigateForward' title='Navigate Forward'></div></td>" +
                            "   <td>&nbsp;&nbsp;</td>" +
                            "   <td>" +
                            "        <select class='styledSelect' id='editorTheme' size='1'>\n" +
                            "          <option value='chrome'>&nbsp;Chrome</option>\n" +
                            "          <option value='eclipse' selected='selected'>&nbsp;Eclipse</option>\n" +
                            "          <option value='github'>&nbsp;GitHub</option>\n" +
                            "          <option value='monokai'>&nbsp;Monokai</option>\n" +
                            "          <option value='terminal'>&nbsp;Terminal</option>\n" +
                            "          <option value='textmate'>&nbsp;TextMate</option>\n" +
                            "          <option value='twilight'>&nbsp;Twilight</option>\n" +
                            "          <option value='vibrant_ink'>&nbsp;Vibrant Ink</option>\n" +
                            "          <option value='xcode'>&nbsp;XCode</option>\n" +
                            "        </select>\n" +
                            "   </td>" +
                            "   <td>&nbsp;&nbsp;</td>" +
                            "   <td>" +
                            "        <select class='styledSelect' id='fontFamily' size='1'>\n" +
                            "          <option value='Consolas' selected='selected'>&nbsp;Consolas</option>\n" +
                            "          <option value='Lucida Console'>&nbsp;Lucida Console</option>\n" +
                            "          <option value='Courier New'>&nbsp;Courier New</option>\n" +
                            "          <option value='Courier'>&nbsp;Courier</option>\n" +
                            "          <option value='Menlo'>&nbsp;Menlo</option>\n" +
                            "          <option value='Monaco'>&nbsp;Monaco</option>\n" +
                            "        </select>\n" +
                            "   </td>" +
                            "   <td>&nbsp;&nbsp;</td>" +
                            "   <td>" +
                            "        <select class='styledSelect' id='fontSize' size='1'>\n" +
                            "          <option value='10px'>&nbsp;10px</option>\n" +
                            "          <option value='11px'>&nbsp;11px</option>\n" +
                            "          <option value='12px'>&nbsp;12px</option>\n" +
                            "          <option value='13px'>&nbsp;13px</option>\n" +
                            "          <option value='14px' selected='selected'>&nbsp;14px</option>\n" +
                            "          <option value='16px'>&nbsp;16px</option>\n" +
                            "          <option value='18px'>&nbsp;18px</option>\n" +
                            "          <option value='20px'>&nbsp;20px</option>\n" +
                            "          <option value='24px'>&nbsp;24px</option>\n" +
                            "        </select>\n" +
                            "   </td>" +
                            "   <td>&nbsp;&nbsp;</td>" +
                            "   <td><div id='toolbarResize' title='Full Screen&nbsp;&nbsp;&nbsp;Ctrl+M'></div></td>" +
                            "   <td><div id='toolbarSwitchLayout' title='Switch Layout&nbsp;&nbsp;&nbsp;Ctrl+L'></div></td>" +
                            "   <td><div id='toolbarSwitchProject' title='Switch Project&nbsp;&nbsp;&nbsp;Ctrl+P'></div></td>" +
                            "   <td>&nbsp;&nbsp;</td>" +
                            "</tr>" +
                            "</table>" +
                            "</div>"
                    }]
            });
        }
        function createProblemsTab() {
            $().w2grid({
                name: 'problems',
                columns: [{
                        field: 'description',
                        caption: 'Description',
                        size: '45%',
                        sortable: true,
                        resizable: true
                    }, {
                        field: 'location',
                        caption: 'Location',
                        size: '10%',
                        sortable: true,
                        resizable: true
                    }, {
                        field: 'resource',
                        caption: 'Resource',
                        size: '45%',
                        sortable: true,
                        resizable: true
                    }],
                onClick: function (event) {
                    var grid = this;
                    event.onComplete = function () {
                        var sel = grid.getSelection();
                        if (sel.length == 1) {
                            var record = grid.get(sel[0]);
                            explorer_1.FileExplorer.openTreeFile(record.script, function () {
                                editor_1.FileEditor.showEditorLine(record.line);
                            });
                        }
                        grid.selectNone();
                        grid.refresh();
                    };
                }
            });
        }
        function createHistoryTab() {
            $().w2grid({
                name: 'history',
                columns: [{
                        field: 'resource',
                        caption: 'Resource',
                        size: '50%',
                        sortable: false,
                        resizable: true
                    }, {
                        field: 'date',
                        caption: 'Date',
                        size: '50%',
                        sortable: true,
                        resizable: true
                    }],
                onClick: function (event) {
                    var grid = this;
                    event.onComplete = function () {
                        var sel = grid.getSelection();
                        if (sel.length == 1) {
                            var record = grid.get(sel[0]);
                            explorer_1.FileExplorer.openTreeHistoryFile(record.script, record.time, function () {
                                editor_1.FileEditor.showEditorLine(record.line);
                            });
                        }
                        grid.selectNone();
                        grid.refresh();
                    };
                }
            });
        }
        function createVariablesTab() {
            $().w2grid({
                recordTitles: false,
                name: 'variables',
                columns: [{
                        field: 'name',
                        caption: 'Name',
                        size: '30%',
                        sortable: false
                    }, {
                        field: 'value',
                        caption: 'Value',
                        size: '40%',
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
                            variables_1.VariableManager.toggleExpandVariable(record.path);
                        }
                        grid.selectNone();
                        grid.refresh();
                    };
                }
            });
        }
        function createProfilerTab() {
            $().w2grid({
                name: 'profiler',
                columns: [{
                        field: 'resource',
                        caption: 'Resource',
                        size: '40%',
                        sortable: false
                    }, {
                        field: 'percentage',
                        caption: 'Percentage',
                        size: '15%'
                    }, {
                        field: 'line',
                        caption: 'Line',
                        size: '15%'
                    }, {
                        field: 'count',
                        caption: 'Count',
                        size: '10%'
                    }, {
                        field: 'duration',
                        caption: 'Duration',
                        size: '10%',
                        sortable: false
                    }, {
                        field: 'average',
                        caption: 'Average',
                        size: '10%',
                        sortable: false
                    }],
                onClick: function (event) {
                    var grid = this;
                    event.onComplete = function () {
                        var sel = grid.getSelection();
                        if (sel.length == 1) {
                            var record = grid.get(sel[0]);
                            explorer_1.FileExplorer.openTreeFile(record.script, function () {
                                editor_1.FileEditor.showEditorLine(record.line);
                            });
                        }
                        grid.selectNone();
                        grid.refresh();
                    };
                }
            });
        }
        function createBreakpointsTab() {
            $().w2grid({
                name: 'breakpoints',
                columns: [
                    {
                        field: 'name',
                        caption: 'Resource',
                        size: '60%',
                        sortable: true,
                        resizable: true
                    }, {
                        field: 'location',
                        caption: 'Location',
                        size: '40%',
                        sortable: true,
                        resizable: true
                    }],
                onClick: function (event) {
                    var grid = this;
                    event.onComplete = function () {
                        var sel = grid.getSelection();
                        if (sel.length == 1) {
                            var record = grid.get(sel[0]);
                            explorer_1.FileExplorer.openTreeFile(record.script, function () {
                                editor_1.FileEditor.showEditorLine(record.line);
                            });
                        }
                        grid.selectNone();
                        grid.refresh();
                    };
                }
            });
        }
        function createThreadsTab() {
            $().w2grid({
                name: 'threads',
                columns: [{
                        field: 'name',
                        caption: 'Thread',
                        size: '25%',
                        sortable: true,
                        resizable: true
                    }, {
                        field: 'status',
                        caption: 'Status',
                        size: '10%',
                        sortable: true,
                        resizable: true
                    }, {
                        field: 'instruction',
                        caption: 'Instruction',
                        size: '15%',
                        sortable: true,
                        resizable: true
                    }, {
                        field: 'resource',
                        caption: 'Resource',
                        size: '30%',
                        sortable: true,
                        resizable: true
                    }, {
                        field: 'line',
                        caption: 'Line',
                        size: '10%',
                        sortable: true,
                        resizable: true
                    }, {
                        field: 'active',
                        caption: 'Active',
                        size: '10%',
                        sortable: false,
                        resizable: true
                    }],
                onClick: function (event) {
                    var grid = this;
                    event.onComplete = function () {
                        var sel = grid.getSelection();
                        if (sel.length == 1) {
                            var record = grid.get(sel[0]);
                            explorer_1.FileExplorer.openTreeFile(record.script, function () {
                                threads_1.ThreadManager.updateThreadFocusByName(record.thread);
                                editor_1.FileEditor.showEditorLine(record.line);
                                threads_1.ThreadManager.showThreads();
                            });
                        }
                        grid.selectNone();
                        grid.refresh();
                    };
                }
            });
        }
        function createDebugTab() {
            $().w2grid({
                name: 'debug',
                columns: [
                    {
                        field: 'name',
                        caption: 'Process',
                        size: '20%',
                        sortable: true,
                        resizable: true
                    }, {
                        field: 'system',
                        caption: 'System',
                        size: '20%',
                        sortable: true,
                        resizable: true
                    }, {
                        field: 'status',
                        caption: 'Status',
                        size: '20%',
                        sortable: true,
                        resizable: true
                    }, {
                        field: 'resource',
                        caption: 'Resource',
                        size: '30%',
                        sortable: true,
                        resizable: true
                    }, {
                        field: 'active',
                        caption: 'Focus',
                        size: '10%',
                        sortable: false,
                        resizable: true
                    }],
                onClick: function (event) {
                    var grid = this;
                    event.onComplete = function () {
                        var sel = grid.getSelection();
                        if (sel.length == 1) {
                            var record = grid.get(sel[0]);
                            if (record.running) {
                                explorer_1.FileExplorer.openTreeFile(record.script, function () {
                                    commands_1.Command.attachProcess(record.process);
                                });
                            }
                            else {
                                commands_1.Command.attachProcess(record.process);
                            }
                        }
                        grid.selectNone();
                        grid.refresh();
                    };
                }
            });
        }
        function activateTab(tabName, layoutName, containsBrowse, containsEditor, browseStyle) {
            hideBrowseTreeContent(containsBrowse); // hide tree
            hideEditorContent(containsEditor); // hide tree
            if (tabName == 'consoleTab') {
                w2ui_1.w2ui[layoutName].content('main', "<div style='overflow: scroll; font-family: monospace;' id='console'></div>");
                w2ui_1.w2ui[layoutName].refresh();
                console_1.ProcessConsole.showConsole();
            }
            else if (tabName == 'problemsTab') {
                w2ui_1.w2ui[layoutName].content('main', "<div style='overflow: scroll; font-family: monospace;' id='problems'></div>");
                w2ui_1.w2ui[layoutName].refresh();
                $('#problems').w2render('problems');
                problem_1.ProblemManager.showProblems();
            }
            else if (tabName == 'breakpointsTab') {
                w2ui_1.w2ui[layoutName].content('main', "<div style='overflow: scroll; font-family: monospace;' id='breakpoints'></div>");
                w2ui_1.w2ui[layoutName].refresh();
                $('#breakpoints').w2render('breakpoints');
                editor_1.FileEditor.showEditorBreakpoints();
            }
            else if (tabName == 'threadsTab') {
                w2ui_1.w2ui[layoutName].content('main', "<div style='overflow: scroll; font-family: monospace;' id='threads'></div>");
                w2ui_1.w2ui[layoutName].refresh();
                $('#threads').w2render('threads');
                threads_1.ThreadManager.showThreads();
            }
            else if (tabName == 'variablesTab') {
                w2ui_1.w2ui[layoutName].content('main', "<div style='overflow: scroll; font-family: monospace;' id='variables'></div>");
                w2ui_1.w2ui[layoutName].refresh();
                $('#variables').w2render('variables');
                variables_1.VariableManager.showVariables();
            }
            else if (tabName == 'profilerTab') {
                w2ui_1.w2ui[layoutName].content('main', "<div style='overflow: scroll; font-family: monospace;' id='profiler'></div>");
                w2ui_1.w2ui[layoutName].refresh();
                $('#profiler').w2render('profiler');
                variables_1.VariableManager.showVariables();
            }
            else if (tabName == 'browseTab') {
                w2ui_1.w2ui[layoutName].content('main', "<div style='overflow: hidden; font-family: monospace;' id='browse'><div id='browseParent' " + browseStyle + "></div></div>");
                w2ui_1.w2ui[layoutName].refresh();
                $('#browse').w2render('browse');
                showBrowseTreeContent(containsBrowse); // hack to move tree
            }
            else if (tabName == 'debugTab') {
                w2ui_1.w2ui[layoutName].content('main', "<div style='overflow: scroll; font-family: monospace;' id='debug'></div>");
                w2ui_1.w2ui[layoutName].refresh();
                $('#debug').w2render('debug');
                debug_1.DebugManager.showStatus();
            }
            else if (tabName == 'historyTab') {
                w2ui_1.w2ui[layoutName].content('main', "<div style='overflow: scroll; font-family: monospace;' id='history'></div>");
                w2ui_1.w2ui[layoutName].refresh();
                $('#history').w2render('history');
                history_1.History.showFileHistory();
            }
            else {
                w2ui_1.w2ui[layoutName].content('main', "<div style='overflow: scroll; font-family: monospace;' id='edit'><div id='editParent'></div></div>");
                w2ui_1.w2ui[layoutName].refresh();
                $('#edit').w2render('edit');
                showEditorContent(containsEditor);
            }
        }
        function openDefaultResource() {
            var location = window.location.hash;
            var hashIndex = location.indexOf('#');
            if (hashIndex == -1) {
                $.ajax({
                    url: '/default/' + document.title,
                    success: function (defaultResource) {
                        explorer_1.FileExplorer.openTreeFile(defaultResource, function () { });
                    },
                    async: true
                });
            }
        }
    })(Project = exports.Project || (exports.Project = {}));
});
//ModuleSystem.registerModule("project", "Project module: project.js", Project.createMainLayout, Project.startMainLayout, [ "common", "socket", "console", "problem", "editor", "spinner", "tree", "threads" ]);

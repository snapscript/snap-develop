define(["require", "exports", "jquery", "w2ui", "common", "console", "problem", "editor", "tree", "threads", "history", "variables", "explorer", "commands", "debug", "keys"], function (require, exports, $, w2ui_1, common_1, console_1, problem_1, editor_1, tree_1, threads_1, history_1, variables_1, explorer_1, commands_1, debug_1, keys_1) {
    "use strict";
    var Project;
    (function (Project) {
        var currentDisplayInfo = {};
        var doubleClickTimes = {};
        function createMainLayout(setupFunction, startFunction) {
            var perspective = determineProjectLayout();
            if (perspective == "debug") {
                createDebugLayout(function () {
                    console.log("Performing setup for debug layout");
                    setupFunction(); // setup stuff
                    activateDebugLayout();
                    startResizePoller(); // dynamically resize the editor
                    attachClickEvents();
                    startFunction(); // start everything
                });
            }
            else {
                createExploreLayout(function () {
                    console.log("Performing setup for explore layout");
                    setupFunction(); // setup stuff
                    activateExploreLayout();
                    startResizePoller(); // dynamically resize the editor
                    attachClickEvents();
                    startFunction(); // start everything
                });
            }
            $(window).trigger('resize'); // force a redraw after w2ui
        }
        Project.createMainLayout = createMainLayout;
        function showProblemsTab() {
            var perspective = determineProjectLayout();
            if (perspective == "debug") {
                w2ui_1.w2ui['debugBottomTabLayout_main_tabs'].click('problemsTab');
            }
            else {
                w2ui_1.w2ui['exploreBottomTabLayout_main_tabs'].click('problemsTab');
            }
        }
        Project.showProblemsTab = showProblemsTab;
        function attachClickEvents() {
            $('#toolbarResize').on('click', function (e) {
                toggleFullScreen();
                e.preventDefault();
            });
            $('#toolbarSwitchLayout').on('click', function (e) {
                commands_1.Command.switchLayout();
                e.preventDefault();
            });
            $('#toolbarSwitchProject').on('click', function (e) {
                commands_1.Command.switchProject();
                e.preventDefault();
            });
            $('#toolbarNavigateBack').on('click', function (e) {
                history_1.History.navigateBackward();
                e.preventDefault();
            });
            $('#toolbarNavigateForward').on('click', function (e) {
                history_1.History.navigateForward();
                e.preventDefault();
            });
            $('#editorTheme').on('change', function (e) {
                changeEditorTheme();
                e.preventDefault();
            });
            $('#fontFamily').on('change', function (e) {
                changeProjectFont();
                e.preventDefault();
            });
            $('#fontSize').on('change', function (e) {
                changeProjectFont();
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
            $('#debugScript').on('click', function (e) {
                commands_1.Command.debugScript();
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
            $('#navigateToTreeArrow').on('click', function (e) {
                editor_1.FileEditor.showEditorFileInTree();
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
                var location = window.location.hash;
                var hashIndex = location.indexOf('#');
                if (hashIndex == -1) {
                    showEditorHelpContent(containsEditor);
                }
                else {
                    showEditorFileContent(containsEditor);
                }
            }
        }
        function showEditorFileContent(containsEditor) {
            var newParent = document.getElementById('editParent');
            var oldParent = document.getElementById('editParentHidden');
            var newParentInfo = "editParent=" + (newParent == null);
            var oldParentInfo = "editParentHidden=" + (oldParent == null);
            console.log("Project.showEditorFileContent(" + containsEditor + "): '" + newParentInfo + "' '" + oldParentInfo + "'");
            if (oldParent != null && newParent != null) {
                $("#help").remove();
                while (oldParent.childNodes.length > 0) {
                    newParent.appendChild(oldParent.childNodes[0]);
                }
            }
            updateEditorTabName();
        }
        function showEditorHelpContent(containsEditor) {
            var newParent = document.getElementById('editParent');
            var editorFileName = document.getElementById("editFileName");
            var newParentInfo = "editParent=" + (newParent == null);
            var editorFileNameInfo = "editFileName=" + (editorFileName == null);
            console.log("Project.showEditorHelpContent(" + containsEditor + "): '" + newParentInfo + "' '" + editorFileNameInfo + "'");
            if (newParent != null && editorFileName != null) {
                var keyBindings = keys_1.KeyBinder.getKeyBindings();
                var content = "";
                content += "<div id='help'>";
                content += "<div id='keyBindings'>";
                content += "<table border='0'>";
                for (var keyBinding in keyBindings) {
                    if (keyBindings.hasOwnProperty(keyBinding)) {
                        var description = keyBindings[keyBinding];
                        content += "<tr>";
                        content += "<td><div class='helpBullet'></div></td>";
                        content += "<td align='left'>&nbsp;&nbsp;" + keyBinding + "</td>";
                        content += "<td align='left'>&nbsp;&nbsp;&nbsp;&nbsp;" + description + "</td>";
                        content += "</td>";
                    }
                }
                content += "</table>";
                content += "</div>";
                content += "</div>";
                $("#editParent").html(content);
            }
            updateEditorTabName();
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
            var editorFileName = document.getElementById("editFileName");
            if (editorFileName != null) {
                var editorState = editor_1.FileEditor.currentEditorState();
                if (editorState != null && editorState.getResource() != null) {
                    editorFileName.innerHTML = "<span title='" + editorState.getResource().getResourcePath() + "'>&nbsp;" + editorState.getResource().getFileName() + "&nbsp;</span>";
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
        function closeEditorTab() {
            var data = editor_1.FileEditor.currentEditorState();
            if (data.getResource()) {
                var tabs = findActiveEditorTabLayout();
                if (tabs.tabs.length > 1) {
                    closeEditorTabForPath(data.getResource().getResourcePath());
                    deleteEditorTab(data.getResource().getResourcePath());
                }
            }
        }
        Project.closeEditorTab = closeEditorTab;
        function deleteEditorTab(resource) {
            var layout = findActiveEditorLayout();
            var tabs = findActiveEditorTabLayout();
            if (tabs != null && resource != null) {
                var removeTab = tabs.get(resource);
                if (removeTab && removeTab.closable) {
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
                            explorer_1.FileExplorer.openTreeFile(toPath.getResourcePath(), function () { }); // browse style makes no difference here
                        }
                        else {
                            var fileNameReplace = new RegExp(fromPath.getFileName(), "g");
                            var filePathReplace = new RegExp(fromPath.getResourcePath(), "g");
                            newTab.caption = newTab.caption.replace(fileNameReplace, toPath.getFileName()).replace(filePathReplace, toPath.getResourcePath()); // rename the tab
                            newTab.text = newTab.text.replace(fileNameReplace, toPath.getFileName()).replace(filePathReplace, toPath.getResourcePath()); // rename the tab
                            newTab.id = toPath.getResourcePath();
                            tabs.add(newTab);
                        }
                        break;
                    }
                }
            }
        }
        Project.renameEditorTab = renameEditorTab;
        function markEditorTab(name, isModified) {
            var layout = findActiveEditorLayout();
            var tabs = findActiveEditorTabLayout();
            if (tabs != null && name != null) {
                var tabList = tabs.tabs;
                var count = 0;
                for (var i = 0; i < tabList.length; i++) {
                    var nextTab = tabList[i];
                    if (nextTab != null && nextTab.id == name) {
                        var tabPath = tree_1.FileTree.createResourcePath(name);
                        var tabFromName = (isModified ? "" : "*") + tabPath.getFileName();
                        var tabToName = (isModified ? "*" : "") + tabPath.getFileName();
                        var isAlreadyModified = common_1.Common.stringContains(nextTab.text, "*" + tabPath.getFileName());
                        if (isModified != isAlreadyModified) {
                            nextTab.caption = common_1.Common.stringReplaceText(nextTab.caption, tabFromName, tabToName);
                            nextTab.text = common_1.Common.stringReplaceText(nextTab.text, tabFromName, tabToName);
                            tabs.refresh();
                        }
                        break;
                    }
                }
            }
        }
        Project.markEditorTab = markEditorTab;
        function createEditorTab() {
            var layout = findActiveEditorLayout();
            var tabs = findActiveEditorTabLayout();
            var editorState = editor_1.FileEditor.currentEditorState();
            if (tabs != null && editorState != null && editorState.getResource() != null) {
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
                tabResources[editorState.getResource().getResourcePath()] = {
                    id: editorState.getResource().getResourcePath(),
                    caption: "<div class='editTab' id='editFileName'><span title='" + editorState.getResource().getResourcePath() + "'>&nbsp;" + editorState.getResource().getFileName() + "&nbsp;</span></div>",
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
                tabs.active = editorState.getResource().getResourcePath();
                activateTab(editorState.getResource().getResourcePath(), layout.name, false, true, ""); // browse style makes no difference here
                // this is pretty rubbish, it would be good if there was a promise after redraw/repaint
                setTimeout(function () {
                    $('#editFileName').on('click', function (e) {
                        clickOnTab(editorState.getResource().getResourcePath(), toggleFullScreen);
                        e.preventDefault();
                    });
                }, 100);
            }
        }
        Project.createEditorTab = createEditorTab;
        function closeEditorTabForPath(resourcePathToClose) {
            if (editor_1.FileEditor.isEditorChangedForPath(resourcePathToClose)) {
                var currentBuffer = editor_1.FileEditor.loadSavedEditorBuffer(resourcePathToClose);
                var editorResource = tree_1.FileTree.createResourcePath(resourcePathToClose);
                commands_1.Command.saveEditorOnClose(currentBuffer.buffer, editorResource); // save the file;
                console.log("CLOSE: " + resourcePathToClose);
            }
            else {
                console.log("CLOSE: " + resourcePathToClose);
            }
            activateAnyEditorTab(resourcePathToClose); // activate some other tab
        }
        function activateAnyEditorTab(resourcePathDeleted) {
            var layout = findActiveEditorLayout();
            var tabs = findActiveEditorTabLayout();
            if (tabs != null) {
                var tabList = tabs.tabs;
                var wasDeleted = false;
                for (var i = 0; i < tabList.length; i++) {
                    var nextTab = tabList[i];
                    if (nextTab != null && nextTab.id == resourcePathDeleted) {
                        nextTab.id = 'editTab'; // make sure not to enable, bit of a hack
                        nextTab.closable = true;
                        nextTab.active = false;
                        wasDeleted = true;
                    }
                }
                if (wasDeleted) {
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
        }
        function hideEditorContent(containsEditor) {
            if (containsEditor) {
                // move the editor
                var newParent = document.getElementById('editParentHidden');
                var oldParent = document.getElementById('editParent');
                var newParentInfo = "editParentHidden=" + (newParent == null);
                var oldParentInfo = "editParent=" + (oldParent == null);
                console.log("Project.hideEditorContent(" + containsEditor + "): '" + newParentInfo + "' '" + oldParentInfo + "'");
                if (oldParent != null && newParent != null) {
                    while (oldParent.childNodes.length > 0) {
                        newParent.appendChild(oldParent.childNodes[0]);
                    }
                }
            }
        }
        function createExploreLayout(startFunction) {
            var layoutEvents = ["createExploreLayout"];
            var layoutEventListener = common_1.Common.createSimpleStateMachineFunction("createExploreLayout", function () {
                console.log("Explore layout fully rendered");
                startFunction();
            }, layoutEvents, 200);
            var pstyle = 'background-color: ${PROJECT_BACKGROUND_COLOR}; overflow: hidden;';
            var leftStyle = pstyle + " margin-top: 32px; border-top: 1px solid ${PROJECT_BORDER_COLOR};";
            createExploreMainLayout(pstyle, layoutEventListener, layoutEvents);
            createExploreEditorLayout(pstyle, layoutEventListener, layoutEvents);
            createExploreEditorTabLayout(pstyle, layoutEventListener, layoutEvents);
            createExploreLeftTabLayout(pstyle, layoutEventListener, layoutEvents);
            createExploreBottomTabLayout(pstyle, layoutEventListener, layoutEvents);
            validateLayout(layoutEvents, ["createExploreLayout"]);
            createTopMenuBar(); // menu bar at top
            createProblemsTab();
            createVariablesTab();
            createProfilerTab();
            createBreakpointsTab();
            createDebugTab();
            createThreadsTab();
            createHistoryTab();
            w2ui_1.w2ui['exploreMainLayout'].content('top', w2ui_1.w2ui['topLayout']);
            //w2ui['exploreMainLayout'].content('left', '<table cellpadding="2"><tr><td><span id="leftProjectRoot"></span></td><tr><tr><td><span id="leftDirectory"></span></td><tr><tr><td></td><tr></table>');
            //w2ui['exploreMainLayout'].content('left', '<div style="border: dotted 1px ${PROJECT_BORDER_COLOR}; padding: 1px; margin-top: 10px; margin-left: 5px;"><table cellpadding="2"><tr><td>&nbsp;</td><tr><tr><td><!--span id="leftProjectRoot"></span--></td></tr></table></div>');      
            w2ui_1.w2ui['exploreMainLayout'].content('left', w2ui_1.w2ui['exploreLeftTabLayout']);
            w2ui_1.w2ui['exploreMainLayout'].content('main', w2ui_1.w2ui['exploreEditorLayout']);
            w2ui_1.w2ui['exploreEditorLayout'].content('main', w2ui_1.w2ui['exploreEditorTabLayout']);
            w2ui_1.w2ui['exploreEditorLayout'].content('bottom', w2ui_1.w2ui['exploreBottomTabLayout']);
            w2ui_1.w2ui['exploreEditorTabLayout'].refresh();
            w2ui_1.w2ui['exploreBottomTabLayout'].refresh();
            w2ui_1.w2ui['exploreLeftTabLayout'].refresh();
            layoutEventListener("createExploreLayout"); // this allows the whole thing to initiate
        }
        function createExploreMainLayout(layoutStyle, layoutEventListener, layoutEvents) {
            layoutEvents.push("exploreMainLayout");
            $('#mainLayout').w2layout({
                name: 'exploreMainLayout',
                padding: 0,
                panels: [{
                        type: 'top',
                        size: '40px',
                        resizable: false,
                        style: layoutStyle
                    }, {
                        type: 'left',
                        size: '25%',
                        resizable: true,
                        style: layoutStyle
                    }, {
                        type: 'right',
                        size: '0%',
                        resizable: true,
                        hidden: true,
                        style: layoutStyle
                    }, {
                        type: 'main',
                        size: '75%',
                        resizable: true,
                        style: layoutStyle
                    }, {
                        type: 'bottom',
                        size: '25px',
                        resizable: false,
                        style: layoutStyle,
                        content: createBottomStatusContent()
                    }],
                onRender: function (event) {
                    layoutEventListener("exploreMainLayout");
                }
            });
        }
        function createExploreEditorLayout(layoutStyle, layoutEventListener, layoutEvents) {
            layoutEvents.push("exploreEditorLayout");
            $('').w2layout({
                name: 'exploreEditorLayout',
                padding: 0,
                panels: [{
                        type: 'main',
                        size: '60%',
                        resizable: true,
                        overflow: 'auto',
                        style: layoutStyle + 'border-bottom: 0px;'
                    }, {
                        type: 'bottom',
                        size: '40%',
                        overflow: 'auto',
                        resizable: true,
                        style: layoutStyle + 'border-top: 0px;'
                    }],
                onRender: function (event) {
                    layoutEventListener("exploreEditorLayout");
                }
            });
        }
        function createExploreEditorTabLayout(layoutStyle, layoutEventListener, layoutEvents) {
            layoutEvents.push("exploreEditorTabLayout");
            layoutEvents.push("exploreEditorTabLayout#tabs");
            $('').w2layout({
                name: 'exploreEditorTabLayout',
                padding: 0,
                panels: [{
                        type: 'main',
                        size: '100%',
                        style: layoutStyle + 'border-top: 0px;',
                        resizable: false,
                        name: 'editTabs',
                        tabs: {
                            active: 'editTab',
                            tabs: [{
                                    id: 'editTab',
                                    caption: '<div class="helpTab" id="editFileName">Welcome</div>',
                                    content: "<div style='overflow: scroll; font-family: monospace;' id='edit'><div id='editParent'></div></div>",
                                    closable: false
                                }],
                            onClick: function (event) {
                                explorer_1.FileExplorer.openTreeFile(event.target, function () {
                                    editor_1.FileEditor.showEditorFileInTree();
                                });
                            },
                            onClose: function (event) {
                                closeEditorTabForPath(event.target);
                            },
                            onRender: function (event) {
                                layoutEventListener("exploreEditorTabLayout#tabs");
                            }
                        }
                    }],
                onRender: function (event) {
                    layoutEventListener("exploreEditorTabLayout");
                }
            });
        }
        function createExploreLeftTabLayout(layoutStyle, layoutEventListener, layoutEvents) {
            layoutEvents.push("exploreLeftTabLayout");
            layoutEvents.push("exploreLeftTabLayout#tabs");
            $('').w2layout({
                name: 'exploreLeftTabLayout',
                padding: 0,
                panels: [{
                        type: 'main',
                        size: '100%',
                        style: layoutStyle + 'border-top: 0px;',
                        resizable: false,
                        name: 'tabs',
                        tabs: {
                            active: 'browseTab',
                            right: '<div id="navigateToTreeArrow"></div>',
                            tabs: [{
                                    id: 'browseTab',
                                    caption: '<div class="browseTab">Project&nbsp;</div>',
                                    content: "<div style='overflow: scroll; font-family: monospace;' id='browse'><div id='browseParent'><div id='explorer'></div></div></div>",
                                    closable: false
                                }],
                            onClick: function (event) {
                                activateTab(event.target, "exploreLeftTabLayout", true, false, "style='right: 0px;'");
                            },
                            onRender: function (event) {
                                layoutEventListener("exploreLeftTabLayout#tabs");
                            }
                        }
                    }],
                onRender: function (event) {
                    layoutEventListener("exploreLeftTabLayout");
                }
            });
        }
        function createExploreBottomTabLayout(layoutStyle, layoutEventListener, layoutEvents) {
            layoutEvents.push("exploreBottomTabLayout");
            layoutEvents.push("exploreBottomTabLayout#tabs");
            $('').w2layout({
                name: 'exploreBottomTabLayout',
                padding: 0,
                panels: [{
                        type: 'main',
                        size: '100%',
                        style: layoutStyle + 'border-top: 0px;',
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
                            },
                            onRender: function (event) {
                                layoutEventListener("exploreBottomTabLayout#tabs");
                            }
                        }
                    }],
                onRender: function (event) {
                    layoutEventListener("exploreBottomTabLayout");
                }
            });
        }
        function activateExploreLayout() {
            applyProjectTheme();
            activateTab("consoleTab", "exploreBottomTabLayout", false, false, "style='right: 0px;'");
            activateTab("browseTab", "exploreLeftTabLayout", true, false, "style='right: 0px;'");
            activateTab("editTab", "exploreEditorTabLayout", false, true, "style='right: 0px;'");
        }
        function createDebugLayout(startFunction) {
            var layoutEvents = ["createDebugLayout"];
            var layoutEventListener = common_1.Common.createSimpleStateMachineFunction("createDebugLayout", function () {
                console.log("Debug layout fully rendered");
                startFunction();
            }, layoutEvents, 200);
            var pstyle = 'background-color: ${PROJECT_BACKGROUND_COLOR}; overflow: hidden;';
            createDebugMainLayout(pstyle, layoutEventListener, layoutEvents);
            createEebugEditorLayout(pstyle, layoutEventListener, layoutEvents);
            createDebugEditorTabLayout(pstyle, layoutEventListener, layoutEvents);
            createDebugTopTabSplit(pstyle, layoutEventListener, layoutEvents);
            createDebugLeftTabLayout(pstyle, layoutEventListener, layoutEvents);
            createDebugRightTabLayout(pstyle, layoutEventListener, layoutEvents);
            createDebugBottomTabLayout(pstyle, layoutEventListener, layoutEvents);
            validateLayout(layoutEvents, ["createExploreLayout"]);
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
            w2ui_1.w2ui['debugEditorLayout'].content('top', w2ui_1.w2ui['debugTopTabSplit']);
            w2ui_1.w2ui['debugTopTabSplit'].content('left', w2ui_1.w2ui['debugLeftTabLayout']);
            w2ui_1.w2ui['debugTopTabSplit'].content('main', w2ui_1.w2ui['debugRightTabLayout']);
            w2ui_1.w2ui['debugEditorLayout'].content('bottom', w2ui_1.w2ui['debugBottomTabLayout']);
            w2ui_1.w2ui['debugEditorLayout'].content('main', w2ui_1.w2ui['debugEditorTabLayout']);
            w2ui_1.w2ui['debugEditorTabLayout'].refresh();
            w2ui_1.w2ui['debugTopTabSplit'].refresh();
            w2ui_1.w2ui['debugLeftTabLayout'].refresh();
            w2ui_1.w2ui['debugRightTabLayout'].refresh();
            w2ui_1.w2ui['debugBottomTabLayout'].refresh();
            layoutEventListener("createDebugLayout"); // this allows the whole thing to initiate
        }
        function createDebugMainLayout(layoutStyle, layoutEventListener, layoutEvents) {
            layoutEvents.push("debugMainLayout");
            $('#mainLayout').w2layout({
                name: 'debugMainLayout',
                padding: 0,
                panels: [{
                        type: 'top',
                        size: '40px',
                        resizable: false,
                        style: layoutStyle
                    }, {
                        type: 'main',
                        size: '80%',
                        resizable: true,
                        style: layoutStyle
                    }, {
                        type: 'bottom',
                        size: '25px',
                        resizable: false,
                        style: layoutStyle,
                        content: createBottomStatusContent()
                    }],
                onRender: function (event) {
                    layoutEventListener("debugMainLayout");
                }
            });
        }
        function createEebugEditorLayout(layoutStyle, layoutEventListener, layoutEvents) {
            layoutEvents.push("debugEditorLayout");
            $('').w2layout({
                name: 'debugEditorLayout',
                padding: 0,
                panels: [{
                        type: 'top',
                        size: '25%',
                        overflow: 'auto',
                        resizable: true,
                        style: layoutStyle + 'border-top: 0px;'
                    }, {
                        type: 'main',
                        size: '50%',
                        resizable: true,
                        overflow: 'auto',
                        style: layoutStyle + 'border-bottom: 0px;'
                    }, {
                        type: 'bottom',
                        size: '25%',
                        overflow: 'auto',
                        resizable: true,
                        style: layoutStyle + 'border-top: 0px;'
                    }],
                onRender: function (event) {
                    layoutEventListener("debugEditorLayout");
                }
            });
        }
        function createDebugEditorTabLayout(layoutStyle, layoutEventListener, layoutEvents) {
            layoutEvents.push("debugEditorTabLayout");
            layoutEvents.push("debugEditorTabLayout#tabs");
            $('').w2layout({
                name: 'debugEditorTabLayout',
                padding: 0,
                panels: [{
                        type: 'main',
                        size: '100%',
                        style: layoutStyle + 'border-top: 0px;',
                        resizable: false,
                        name: 'editTabs',
                        tabs: {
                            active: 'editTab',
                            tabs: [{
                                    id: 'editTab',
                                    caption: '<div class="helpTab" id="editFileName">Welcome</div>',
                                    content: "<div style='overflow: scroll; font-family: monospace;' id='edit'><div id='editParent'></div></div>",
                                    closable: false
                                }],
                            onClick: function (event) {
                                explorer_1.FileExplorer.openTreeFile(event.target, function () {
                                    editor_1.FileEditor.showEditorFileInTree();
                                });
                            },
                            onClose: function (event) {
                                closeEditorTabForPath(event.target);
                            },
                            onRender: function (event) {
                                layoutEventListener("debugEditorTabLayout#tabs");
                            }
                        }
                    }],
                onRender: function (event) {
                    layoutEventListener("debugEditorTabLayout");
                }
            });
        }
        function createDebugTopTabSplit(layoutStyle, layoutEventListener, layoutEvents) {
            layoutEvents.push("debugTopTabSplit");
            $('').w2layout({
                name: 'debugTopTabSplit',
                padding: 0,
                panels: [{
                        type: 'left',
                        size: '50%',
                        overflow: 'auto',
                        resizable: true,
                        style: layoutStyle + 'border-top: 0px;'
                    }, {
                        type: 'main',
                        size: '50%',
                        resizable: true,
                        overflow: 'auto',
                        style: layoutStyle + 'border-bottom: 0px;'
                    }],
                onRender: function (event) {
                    layoutEventListener("debugTopTabSplit");
                }
            });
        }
        function createDebugLeftTabLayout(layoutStyle, layoutEventListener, layoutEvents) {
            layoutEvents.push("debugLeftTabLayout");
            layoutEvents.push("debugLeftTabLayout#tabs");
            $('').w2layout({
                name: 'debugLeftTabLayout',
                padding: 0,
                panels: [{
                        type: 'main',
                        size: '100%',
                        style: layoutStyle + 'border-top: 0px;',
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
                            },
                            onRender: function (event) {
                                layoutEventListener("debugLeftTabLayout#tabs");
                            }
                        }
                    }],
                onRender: function (event) {
                    layoutEventListener("debugLeftTabLayout");
                }
            });
        }
        function createDebugRightTabLayout(layoutStyle, layoutEventListener, layoutEvents) {
            layoutEvents.push("debugRightTabLayout");
            layoutEvents.push("debugRightTabLayout#tabs");
            $('').w2layout({
                name: 'debugRightTabLayout',
                padding: 0,
                panels: [{
                        type: 'main',
                        size: '100%',
                        style: layoutStyle + 'border-top: 0px;',
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
                            },
                            onRender: function (event) {
                                layoutEventListener("debugRightTabLayout#tabs");
                            }
                        }
                    }],
                onRender: function (event) {
                    layoutEventListener("debugRightTabLayout");
                }
            });
        }
        function createDebugBottomTabLayout(layoutStyle, layoutEventListener, layoutEvents) {
            layoutEvents.push("debugBottomTabLayout");
            layoutEvents.push("debugBottomTabLayout#tabs");
            $('').w2layout({
                name: 'debugBottomTabLayout',
                padding: 0,
                panels: [{
                        type: 'main',
                        size: '100%',
                        style: layoutStyle + 'border-top: 0px;',
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
                            },
                            onRender: function (event) {
                                layoutEventListener("debugBottomTabLayout#tabs");
                            }
                        }
                    }],
                onRender: function (event) {
                    layoutEventListener("debugBottomTabLayout");
                }
            });
        }
        function activateDebugLayout() {
            applyProjectTheme();
            activateTab("debugTab", "debugLeftTabLayout", true, false, "");
            activateTab("variablesTab", "debugRightTabLayout", false, false, "");
            activateTab("consoleTab", "debugBottomTabLayout", false, false, "");
            activateTab("editTab", "debugEditorTabLayout", false, true, "");
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
        function validateLayout(layoutEvents, ignoreEvents) {
            for (var i = 0; i < layoutEvents.length; i++) {
                var layoutEvent = layoutEvents[i];
                var ignoreIndex = ignoreEvents.indexOf(layoutEvent);
                if (ignoreIndex == -1) {
                    var tabIndex = layoutEvent.indexOf("#tab");
                    if (tabIndex == -1) {
                        var layout = w2ui_1.w2ui[layoutEvent];
                        if (!layout || !layout.panels) {
                            console.warn("Layout '" + layoutEvent + "' was not registered");
                        }
                    }
                    else {
                        var parentLayoutEvent = layoutEvent.substring(0, tabIndex);
                        var layout = w2ui_1.w2ui[parentLayoutEvent];
                        if (!layout || !layout.panels) {
                            console.warn("Layout '" + parentLayoutEvent + "' was not registered");
                        }
                        else {
                            var layoutTabName = parentLayoutEvent + "_main_tabs";
                            var mainPanel = layout.panels.filter(function (layoutPanel) {
                                return layoutPanel && layoutPanel.type == 'main'; // its always on main
                            });
                            if (mainPanel.length == 0 || mainPanel[0].tabs.name != layoutTabName) {
                                console.warn("Layout '" + parentLayoutEvent + "' does not have any tabs");
                            }
                        }
                    }
                }
            }
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
                            + "         <td><div id='debugScript' title='Debug Script&nbsp;&nbsp;&nbsp;Ctrl+B'></div></td>"
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
                        sortable: true
                    }, {
                        field: 'percentage',
                        caption: 'Percentage',
                        size: '15%',
                        sortable: true
                    }, {
                        field: 'line',
                        caption: 'Line',
                        size: '15%'
                    }, {
                        field: 'count',
                        caption: 'Count',
                        size: '10%',
                        sortable: true
                    }, {
                        field: 'duration',
                        caption: 'Duration',
                        size: '10%',
                        sortable: true
                    }, {
                        field: 'average',
                        caption: 'Average',
                        size: '10%',
                        sortable: true
                    }],
                sortData: [
                    { field: 'percentage', direction: 'dsc' }
                ],
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
    })(Project = exports.Project || (exports.Project = {}));
});
//ModuleSystem.registerModule("project", "Project module: project.js", Project.createMainLayout, Project.startMainLayout, [ "common", "socket", "console", "problem", "editor", "spinner", "tree", "threads" ]);

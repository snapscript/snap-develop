define(["require", "exports", "jquery", "mousetrap", "common", "editor", "commands", "project"], function (require, exports, $, Mousetrap, common_1, editor_1, commands_1, project_1) {
    "use strict";
    var KeyBinder;
    (function (KeyBinder) {
        var MAX_PRESS_REPEAT = 250; // 250 milliseconds
        var pressTimes = {};
        var controlPressed = false;
        function bindKeys() {
            disableBrowserKeys();
            createKeyBinding("ctrl n", true, function () {
                commands_1.Command.newFile(null);
            });
            createKeyBinding("ctrl s", true, function () {
                commands_1.Command.saveFile(null);
            });
            createKeyBinding("ctrl shift s", true, function () {
                commands_1.Command.searchTypes();
            });
            createKeyBinding("ctrl tab", true, function () {
                editor_1.FileEditor.formatEditorSource();
            });
            createKeyBinding("ctrl shift e", true, function () {
                commands_1.Command.evaluateExpression();
            });
            createKeyBinding("ctrl shift m", true, function () {
                project_1.Project.toggleFullScreen();
            });
            createKeyBinding("ctrl shift l", true, function () {
                commands_1.Command.switchLayout();
            });
            createKeyBinding("ctrl shift p", true, function () {
                commands_1.Command.switchProject();
            });
            createKeyBinding("ctrl shift g", true, function () {
                commands_1.Command.findFileNames();
            });
            createKeyBinding("ctrl shift h", true, function () {
                commands_1.Command.searchAndReplaceFiles();
            });
            createKeyBinding("ctrl shift f", true, function () {
                commands_1.Command.searchFiles();
            });
            createKeyBinding("ctrl h", true, function () {
                editor_1.FileEditor.findAndReplaceTextInEditor();
            });
            createKeyBinding("ctrl f", true, function () {
                editor_1.FileEditor.findTextInEditor();
            });
            //      createKeyBinding("ctrl c", true, function() {
            //         console.log("COPY BUFFER");
            //      });
            //      createKeyBinding("ctrl v", true, function() {
            //         console.log("PASTE BUFFER");
            //      });
            //      createKeyBinding("ctrl x", true, function() {
            //         console.log("CUT BUFFER");
            //      });
            createKeyDownBinding("ctrl", false, function () {
                controlPressed = true;
            });
            createKeyUpBinding("ctrl", false, function () {
                controlPressed = false;
            });
            //      createKeyBinding("up", false, function() {
            //         FileEditor.moveCursorUp();
            //      });
            //      createKeyBinding("down", false, function() {
            //         FileEditor.moveCursorDown();
            //      });
            //      createKeyBinding("left", false, function() {
            //         FileEditor.moveCursorLeft();
            //      });
            //      createKeyBinding("right", false, function() {
            //         FileEditor.moveCursorRight();
            //      });
            //      createKeyBinding("tab", true, function() {
            //         FileEditor.indentCurrentLine();
            //      });
            //      createKeyBinding("ctrl /", true, function() {
            //         FileEditor.commentSelection();
            //      });
            //      createKeyBinding("ctrl z", true, function() {
            //         FileEditor.undoEditorChange();
            //      });
            //      createKeyBinding("ctrl y", true, function() {
            //         FileEditor.redoEditorChange();
            //      });
            createKeyBinding("ctrl r", true, function () {
                commands_1.Command.runScript();
            });
            createKeyBinding("f8", true, function () {
                console.log("F8");
                commands_1.Command.resumeScript();
            });
            createKeyBinding("f5", true, function () {
                console.log("F5");
                commands_1.Command.stepInScript();
            });
            createKeyBinding("f7", true, function () {
                console.log("F7");
                commands_1.Command.stepOutScript();
            });
            createKeyBinding("f6", true, function () {
                console.log("F6");
                commands_1.Command.stepOverScript();
            });
        }
        KeyBinder.bindKeys = bindKeys;
        function isControlPressed() {
            return controlPressed;
        }
        KeyBinder.isControlPressed = isControlPressed;
        function disableBrowserKeys() {
            $(window).keydown(function (event) {
                if (event.ctrlKey) {
                    event.preventDefault();
                }
            });
        }
        function parseKeyBinding(name) {
            var keyParts = name.split(/\s+/);
            var keyBindingParts = [];
            for (var i = 0; i < keyParts.length; i++) {
                var keyPart = keyParts[i];
                if (common_1.Common.isMacintosh() && keyPart == 'ctrl') {
                    keyPart = 'command';
                }
                keyBindingParts[i] = keyPart.charAt(0).toUpperCase() + keyPart.slice(1);
            }
            var editorKeyBinding = keyBindingParts.join("-");
            var globalKeyBinding = keyBindingParts.join("+").toLowerCase();
            return {
                editor: editorKeyBinding,
                global: globalKeyBinding
            };
        }
        function createKeyBinding(name, preventDefault, pressAction) {
            var keyBinding = parseKeyBinding(name);
            //      var editor = ace.edit("editor");
            //       
            //      console.log(keyBinding.editor);
            //      editor.commands.addCommand({
            //           name : name,
            //           bindKey : {
            //               win : keyBinding.editor,
            //               mac : keyBinding.editor
            //           },
            //           exec : function(editor) {
            //              if(pressAction) { 
            //                 pressAction();
            //              }
            //           }
            //      });
            editor_1.FileEditor.addEditorKeyBinding(keyBinding, pressAction);
            Mousetrap.bindGlobal(keyBinding.global, function (e) {
                if (pressAction) {
                    pressAction();
                }
                return !preventDefault;
            });
        }
        function createKeyDownBinding(name, preventDefault, pressAction) {
            var keyBinding = parseKeyBinding(name);
            Mousetrap.bindGlobal(keyBinding.global, function (e) {
                if (pressAction) {
                    pressAction();
                }
                return !preventDefault;
            }, 'keydown');
        }
        function createKeyUpBinding(name, preventDefault, pressAction) {
            var keyBinding = parseKeyBinding(name);
            Mousetrap.bindGlobal(keyBinding.global, function (e) {
                if (pressAction) {
                    pressAction();
                }
                return !preventDefault;
            }, 'keyup');
        }
    })(KeyBinder = exports.KeyBinder || (exports.KeyBinder = {}));
});
//ModuleSystem.registerModule("keys", "Key binder: key.js", null, KeyBinder.bindKeys, [ "common", "spinner", "tree", "commands", "editor" ]); 

var KeyBinder;
(function (KeyBinder) {
    function bindKeys() {
        var listener = new window.keypress.Listener();
        var keyBindings = [
            createKeyBinding("ctrl n", function () {
                Command.newFile(null);
            }),
            createKeyBinding("ctrl s", function () {
                Command.saveFile(null);
            }),
            createKeyBinding("ctrl shift s", function () {
                Command.searchTypes();
            }),
            createKeyBinding("ctrl r", function () {
                Command.runScript();
            }),
            createKeyBinding("f8", function () {
                Command.resumeScript();
            }),
            createKeyBinding("f5", function () {
                Command.stepInScript();
            }),
            createKeyBinding("f7", function () {
                Command.stepOutScript();
            }),
            createKeyBinding("f6", function () {
                Command.stepOverScript();
            }),
            createKeyBinding("ctrl shift f", function () {
                FileEditor.formatEditorSource();
            }),
            createKeyBinding("ctrl shift e", function () {
                Command.evaluateExpression();
            })];
        ;
        listener.register_many(keyBindings);
    }
    KeyBinder.bindKeys = bindKeys;
    function parseKeyBinding(name) {
        var keyParts = name.split(/\s+/);
        var keyBindingParts = [];
        for (var i = 0; i < keyParts.length; i++) {
            keyBindingParts[i] = keyParts[i].charAt(0).toUpperCase() + keyParts[i].slice(1);
        }
        var keyBinding = keyBindingParts.join("-");
        return keyBinding;
    }
    function createKeyBinding(name, pressAction, releaseAction) {
        var editor = ace.edit("editor");
        var keyBinding = parseKeyBinding(name);
        //console.log(keyBinding);
        editor.commands.addCommand({
            name: name,
            bindKey: {
                win: keyBinding,
                mac: keyBinding
            },
            exec: function (editor) {
                if (pressAction) {
                    pressAction();
                }
            }
        });
        return {
            keys: name,
            prevent_default: false,
            is_exclusive: true,
            on_keydown: function () {
                if (pressAction) {
                    pressAction();
                }
            },
            on_keyup: function (e) {
                if (releaseAction) {
                    releaseAction();
                }
            }
        };
    }
})(KeyBinder || (KeyBinder = {}));
ModuleSystem.registerModule("keys", "Key binder: key.js", KeyBinder.bindKeys, ["common", "spinner", "tree", "commands", "editor"]);

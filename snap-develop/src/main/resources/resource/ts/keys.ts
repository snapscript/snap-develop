
module KeyBinder {

   const MAX_PRESS_REPEAT = 250; // 250 milliseconds
   const pressTimes = {};

   export function bindKeys() {
      var listener = new window.keypress.Listener();
      var keyBindings = [
         createKeyBinding("ctrl n", false, function() {
            Command.newFile(null);
         }),
         createKeyBinding("ctrl s", false, function() {
            Command.saveFile(null);
         }),
         createKeyBinding("ctrl shift s", false, function() {
            Command.searchTypes();
         }),
         createKeyBinding("ctrl r", false, function() {
            Command.runScript();
         }),
         createKeyBinding("f8", true, function() {
            console.log("F8");
            Command.resumeScript();
         }),
         createKeyBinding("f5", true, function() {
            console.log("F5");
            Command.stepInScript();
         }),
         createKeyBinding("f7", true, function() {
            console.log("F7");
            Command.stepOutScript();
         }),
         createKeyBinding("f6", true, function() {
            console.log("F6");
            Command.stepOverScript();
         }),
         createKeyBinding("ctrl shift f", false, function() {
            FileEditor.formatEditorSource();
         }),
         createKeyBinding("ctrl shift e", false, function() {
            Command.evaluateExpression();
         })];
      ];
      listener.register_many(keyBindings);
   }
   
   function parseKeyBinding(name) {
      var keyParts = name.split(/\s+/);
      var keyBindingParts = [];
      
      for(var i = 0; i < keyParts.length; i++) {
         keyBindingParts[i] = keyParts[i].charAt(0).toUpperCase() + keyParts[i].slice(1);
      }
      var keyBinding = keyBindingParts.join("-");
      return keyBinding;
   }
   
   function createKeyBinding(name, preventDefault, pressAction, releaseAction) {
      var keyBinding = parseKeyBinding(name);
      
      if(!preventDefault) {
         var editor = ace.edit("editor");
         //console.log(keyBinding);
         editor.commands.addCommand({
             name : name,
             bindKey : {
                win : keyBinding,
                mac : keyBinding
             },
             exec : function(editor) {
                if(pressAction) { 
                   pressAction();
                }
             }
         });
      }
      return {
         keys: name,
         prevent_default: preventDefault,
         prevent_repeat: true,
         is_exclusive: true,
         on_keydown: function() {
            if(pressAction) { 
               var previousTime = pressTimes[keyBinding];
               var currentTime = new Date().getTime();
               
               if(!previousTime || previousTime + MAX_PRESS_REPEAT < currentTime) { // prevent repeats
                  pressAction();
                  pressTimes[keyBinding] = currentTime;
               }
            }
         },
         on_keyup: function(e) {
            if(releaseAction) {
               releaseAction();
            }
         }
     };
   }
}

ModuleSystem.registerModule("keys", "Key binder: key.js", KeyBinder.bindKeys, [ "common", "spinner", "tree", "commands", "editor" ]);
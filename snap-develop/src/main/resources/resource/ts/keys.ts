
module KeyBinder {

   const MAX_PRESS_REPEAT = 250; // 250 milliseconds
   const pressTimes = {};

   export function bindKeys() {
      createKeyBinding("ctrl n", false, function() {
         Command.newFile(null);
      });
      createKeyBinding("ctrl s", false, function() {
         Command.saveFile(null);
      });
      createKeyBinding("ctrl shift s", false, function() {
         Command.searchTypes();
      });
      createKeyBinding("ctrl shift h", false, function() {
         Command.searchFiles();
      });
      createKeyBinding("ctrl r", false, function() {
         Command.runScript();
      });
      createKeyBinding("f8", true, function() {
         console.log("F8");
         Command.resumeScript();
      });
      createKeyBinding("f5", true, function() {
         console.log("F5");
         Command.stepInScript();
      });
      createKeyBinding("f7", true, function() {
         console.log("F7");
         Command.stepOutScript();
      });
      createKeyBinding("f6", true, function() {
         console.log("F6");
         Command.stepOverScript();
      });
      createKeyBinding("ctrl shift f", false, function() {
         FileEditor.formatEditorSource();
      });
      createKeyBinding("ctrl shift e", false, function() {
         Command.evaluateExpression();
      });
   }
   
   function parseKeyBinding(name) {
      var keyParts = name.split(/\s+/);
      var keyBindingParts = [];
      
      for(var i = 0; i < keyParts.length; i++) {
         keyBindingParts[i] = keyParts[i].charAt(0).toUpperCase() + keyParts[i].slice(1);
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
      
       var editor = ace.edit("editor");
       // console.log(keyBinding);
       editor.commands.addCommand({
           name : name,
           bindKey : {
              win : keyBinding.editor,
              mac : keyBinding.editor
           },
           exec : function(editor) {
              if(pressAction) { 
                 pressAction();
              }
           }
      });
      Mousetrap.bind(keyBinding.global, function(e) {
         if(pressAction) {
            pressAction();
         }
         return false;
      });
   }
}

ModuleSystem.registerModule("keys", "Key binder: key.js", KeyBinder.bindKeys, [ "common", "spinner", "tree", "commands", "editor" ]);
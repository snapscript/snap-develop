
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
      createKeyBinding("ctrl shift g", false, function() {
         Command.findFileNames();
      });
      createKeyBinding("ctrl shift f", false, function() {
         FileEditor.formatEditorSource();
      });
      createKeyBinding("ctrl shift e", false, function() {
         Command.evaluateExpression();
      });
      createKeyBinding("ctrl shift m", false, function() {
         Project.toggleFullScreen();
      });
      createKeyBinding("ctrl shift l", false, function() {
         Command.switchLayout()
      });
      createKeyBinding("ctrl shift p", false, function() {
         Command.switchProject()
      });
      createKeyBinding("ctrl z", false, function() {
         FileEditor.undoEditorChange();
      });
      createKeyBinding("ctrl y", false, function() {
         FileEditor.redoEditorChange();
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
   }
   
   function parseKeyBinding(name) {
      var keyParts = name.split(/\s+/);
      var keyBindingParts = [];
      
      for(var i = 0; i < keyParts.length; i++) {
         var keyPart = keyParts[i];
         
         if(isMacintosh() && keyPart == 'ctrl') {
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
//              win : keyBinding.editor,
//              mac : keyBinding.editor
//           },
//           exec : function(editor) {
//              if(pressAction) { 
//                 pressAction();
//              }
//           }
//      });
      Mousetrap.bindGlobal(keyBinding.global, function(e) {
         if(pressAction) {
            pressAction();
         }
         return false;
      });
   }
}

ModuleSystem.registerModule("keys", "Key binder: key.js", KeyBinder.bindKeys, [ "common", "spinner", "tree", "commands", "editor" ]);
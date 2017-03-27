
module KeyBinder {

   const MAX_PRESS_REPEAT = 250; // 250 milliseconds
   const pressTimes = {};
   var controlPressed = false;

   export function bindKeys() {
      disableBrowserKeys();
      
      createKeyBinding("ctrl n", true, function() {
         Command.newFile(null);
      });
      createKeyBinding("ctrl s", true, function() {
         Command.saveFile(null);
      });
      createKeyBinding("ctrl shift s", true, function() {
         Command.searchTypes();
      });
      createKeyBinding("ctrl tab", true, function() {
         FileEditor.formatEditorSource();
      });
      createKeyBinding("ctrl shift e", true, function() {
         Command.evaluateExpression();
      });
      createKeyBinding("ctrl shift m", true, function() {
         Project.toggleFullScreen();
      });
      createKeyBinding("ctrl shift l", true, function() {
         Command.switchLayout()
      });
      createKeyBinding("ctrl shift p", true, function() {
         Command.switchProject();
      });
      createKeyBinding("ctrl shift g", true, function() {
         Command.findFileNames();
      });
      createKeyBinding("ctrl shift h", true, function() {
         Command.searchAndReplaceFiles();
      });
      createKeyBinding("ctrl shift f", true, function() {
         Command.searchFiles();
      });
      createKeyBinding("ctrl h", true, function() {
         FileEditor.findAndReplaceTextInEditor();
      });
      createKeyBinding("ctrl f", true, function() {
         FileEditor.findTextInEditor();
      });
      createKeyDownBinding("ctrl", false, function() {
         controlPressed = true;
      });
      createKeyUpBinding("ctrl", false, function() {
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
      createKeyBinding("ctrl r", true, function() {
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
   
   export function isControlPressed() {
      return controlPressed;
   }
   
   function disableBrowserKeys() {
      $(window).keydown(function(event) {
         if(event.ctrlKey) { 
           event.preventDefault(); 
         }
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
//               win : keyBinding.editor,
//               mac : keyBinding.editor
//           },
//           exec : function(editor) {
//              if(pressAction) { 
//                 pressAction();
//              }
//           }
//      });
      FileEditor.addEditorKeyBinding(keyBinding, pressAction);
      Mousetrap.bindGlobal(keyBinding.global, function(e) {
         if(pressAction) {
            pressAction();
         }
         return !preventDefault;
      });
   }
   
   function createKeyDownBinding(name, preventDefault, pressAction) {
      var keyBinding = parseKeyBinding(name);      

      Mousetrap.bindGlobal(keyBinding.global, function(e) {
         if(pressAction) {
            pressAction();
         }
         return !preventDefault;
      }, 'keydown');
   }
   
   function createKeyUpBinding(name, preventDefault, pressAction) {
      var keyBinding = parseKeyBinding(name);      

      Mousetrap.bindGlobal(keyBinding.global, function(e) {
         if(pressAction) {
            pressAction();
         }
         return !preventDefault;
      }, 'keyup');
   }
}

ModuleSystem.registerModule("keys", "Key binder: key.js", KeyBinder.bindKeys, [ "common", "spinner", "tree", "commands", "editor" ]);
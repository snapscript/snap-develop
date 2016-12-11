
module ModuleSystem {
   
   var modules = {};
   var starting = [];
   var started = [];
   
   /**
    * This is used to register modules such they can start in order
    * or dependency. This allows multiple modules to be registered without
    * the need to tie them together.
    * 
    * @param name the name of the module
    * @param description a description of the module
    * @param method the module initialization method
    * @param dependencies the modules it depends on
    */
   export function registerModule(name, description, method, dependencies) {
      var start = method;
      
      if(start == null) {
         start = function() {};
      }
      var module = {
          name: name,
          description: description,
          start: start,
          dependencies: dependencies
      }   
      modules[name] = module;
   }
   
   export function loadModules() {
      var errors = checkModules();
      
      if(errors.length > 0) {
         for(var i = 0; i < errors.length; i++) {
            console.log(errors[i]);
         }
      } else {
         for(var name in modules) {
            if(modules.hasOwnProperty(name)) {
               var module = modules[name];
               var description = module.description;
               var count = module.dependencies;
               
               if(count == 0) {
                  starting[name] = false;
                  started[name] = true;
                  console.log("Starting independent module '" + name + "' (" + description + ")");
                  module.start(); /* start independent module */               
               } else {
                  starting[name] = false;
                  started[name] = false;
               }          
            }
         }
         for(var name in modules) {
            if(modules.hasOwnProperty(name)) {
               var module = modules[name];           
               
               if(!started[name]) {
                  loadModule(module);
               }
            }
         }
      }
   }
      
   function loadModule(module) {
      var dependencies = module.dependencies;
      var description = module.description;
      var name = module.name;
      
      if(started[name]) {
         return; /* already started */
      }   
      if(!starting[name]) { /* ignore circular dependencies */
         starting[name] = true;
      
         for(var i = 0; i < dependencies.length; i++) {
            var dependency = dependencies[i];
            var child = modules[dependency];
            
            loadModule(child);
         }
         console.log("Starting module '" + name + "' (" + description + ")");
         module.start(); /* start once all dependencies are started */
         started[name] = true;
      }
   }
   
   function checkModules() {
      var dependencies = {};
      var required = [];
      var missing = [];
      var loaded = [];
      var errors = [];
      
      for(var name in modules) { /* build dependency graph */
         if(modules.hasOwnProperty(name)) {
            var module = modules[name];
            var requires = module['dependencies'];
            
            if(requires != null) {
               var count = requires.length;
               
               for(var i = 0; i < count; i++) {       
                  var require = requires[i];            
                  var dependency = dependencies[require];
                  
                  if(dependency == undefined) {
                     dependency = {
                        name: require,
                        modules: []
                     }
                     dependencies[require] = dependency;
                  }
                  dependency.modules.push(module);
                  required.push(require);
               }            
            }
            loaded.push(name);
         }
      }   
      for(var i = 0; i < required.length; i++) {
         var name = required[i];
         var count = 0;
         
         for(var j = 0; j < loaded.length; j++) {         
            if(required[i] == loaded[j]) {
               count++;
            }
         }
         if(count == 0) {
            missing.push(name);
         }
      }
      for(var i = 0; i < missing.length; i++) {
         var name = missing[i];
         var dependency = dependencies[name];
         var message = "Missing dependency '" + name + "', required by ";      
        
         for(var j = 0; j < dependency.modules.length; j++) {
            if(j > 0) {
               message += ", ";
            }
            var module = dependency.modules[j];
           
            message += "'"
            message += module.name;
            message += "' (";
            message += module.description;
            message += ")";
         }
         errors.push(message);
      }
      return errors;
   }
}
window.addEventListener("load", ModuleSystem.loadModules, false);
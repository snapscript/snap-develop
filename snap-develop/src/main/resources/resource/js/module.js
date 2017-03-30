var ModuleSystem;
(function (ModuleSystem) {
    var modules = {};
    /**
     * This is used to register modules such they can start in order
     * or dependency. This allows multiple modules to be registered without
     * the need to tie them together.
     *
     * @param name the name of the module
     * @param description a description of the module
     * @param prepare this is a function for preparing the module
     * @param start the function for starting the module
     * @param dependencies the modules it depends on
     */
    function registerModule(name, description, prepare, start, dependencies) {
        if (start == null) {
            start = function (module) { };
        }
        if (prepare == null) {
            prepare = function (module) { };
        }
        var module = {
            name: name,
            description: description,
            prepare: prepare,
            start: start,
            dependencies: dependencies
        };
        modules[name] = module;
    }
    ModuleSystem.registerModule = registerModule;
    function loadModules() {
        var errors = checkModules();
        if (errors.length > 0) {
            for (var i = 0; i < errors.length; i++) {
                console.log(errors[i]);
            }
        }
        else {
            processModules({
                phase: "PREPARE",
                processed: {},
                processing: {},
                process: function (module) {
                    if (module.prepare) {
                        module.prepare();
                    }
                }
            });
            setTimeout(function () {
                processModules({
                    phase: "START",
                    processed: {},
                    processing: {},
                    process: function (module) {
                        if (module.start) {
                            module.start();
                        }
                    }
                });
            }, 300);
        }
    }
    ModuleSystem.loadModules = loadModules;
    function processModules(context) {
        for (var name in modules) {
            if (modules.hasOwnProperty(name)) {
                if (!context.processed[name]) {
                    var module = modules[name];
                    var description = module.description;
                    var count = module.dependencies;
                    if (count == 0) {
                        context.processing[name] = false;
                        context.processed[name] = true;
                        console.log("[" + context.phase + "] independent module '" + name + "' (" + description + ")");
                        context.process(module); /* process independent module */
                    }
                    else {
                        context.processing[name] = false;
                        context.processed[name] = false;
                    }
                }
            }
        }
        for (var name in modules) {
            if (modules.hasOwnProperty(name)) {
                var module = modules[name];
                if (!context.processed[name]) {
                    processModule(context, module);
                }
            }
        }
    }
    function processModule(context, module) {
        var dependencies = module.dependencies;
        var description = module.description;
        var name = module.name;
        if (context.processed[name]) {
            return; /* already processed */
        }
        if (!context.processing[name]) {
            context.processing[name] = true;
            for (var i = 0; i < dependencies.length; i++) {
                var dependency = dependencies[i];
                var child = modules[dependency];
                processModule(context, child);
            }
            console.log("[" + context.phase + "] module '" + name + "' (" + description + ")");
            context.process(module); /* process once all dependencies are processed */
            context.processed[name] = true;
        }
    }
    function checkModules() {
        var dependencies = {};
        var required = [];
        var missing = [];
        var loaded = [];
        var errors = [];
        for (var name in modules) {
            if (modules.hasOwnProperty(name)) {
                var module = modules[name];
                var requires = module['dependencies'];
                if (requires != null) {
                    var count = requires.length;
                    for (var i = 0; i < count; i++) {
                        var require = requires[i];
                        var dependency = dependencies[require];
                        if (dependency == undefined) {
                            dependency = {
                                name: require,
                                modules: []
                            };
                            dependencies[require] = dependency;
                        }
                        dependency.modules.push(module);
                        required.push(require);
                    }
                }
                loaded.push(name);
            }
        }
        for (var i = 0; i < required.length; i++) {
            var name = required[i];
            var count = 0;
            for (var j = 0; j < loaded.length; j++) {
                if (required[i] == loaded[j]) {
                    count++;
                }
            }
            if (count == 0) {
                missing.push(name);
            }
        }
        for (var i = 0; i < missing.length; i++) {
            var name = missing[i];
            var dependency = dependencies[name];
            var message = "Missing dependency '" + name + "', required by ";
            for (var j = 0; j < dependency.modules.length; j++) {
                if (j > 0) {
                    message += ", ";
                }
                var module = dependency.modules[j];
                message += "'";
                message += module.name;
                message += "' (";
                message += module.description;
                message += ")";
            }
            errors.push(message);
        }
        return errors;
    }
})(ModuleSystem || (ModuleSystem = {}));
window.addEventListener("load", ModuleSystem.loadModules, false);

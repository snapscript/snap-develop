var consoleWindow = {};
var consoleCapacity = 5000;
var consoleProcess = null;
function registerConsole() {
    createRoute("BEGIN", createConsole);
    createRoute('PRINT_ERROR', updateConsole);
    createRoute('PRINT_OUTPUT', updateConsole);
    createRoute('TERMINATE', terminateConsole); // clear focus
    createRoute('EXIT', terminateConsole);
    setInterval(showConsole, 200); // prevents reflow overload when console is busy
}
function updateConsoleFont(fontFamily, fontSize) {
    var consoleElement = document.getElementById("console");
    if (consoleElement != null) {
        consoleElement.style.fontFamily = fontFamily;
        consoleElement.style.fontSize = fontSize;
    }
}
function updateConsoleCapacity(maxCapacity) {
    consoleCapacity = maxCapacity;
}
function terminateConsole(socket, type, text) {
    var terminateProcess = text;
    if (consoleProcess == terminateProcess) {
        showConsole();
    }
    if (consoleWindow[terminateProcess] != null) {
        delete consoleWindow[terminateProcess]; // remove the console process
    }
}
function clearConsole() {
    var consoleElement = document.getElementById("console");
    if (consoleElement != null) {
        document.getElementById("console").innerHTML = "";
    }
    consoleProcess = null;
}
function showConsole() {
    var consoleElement = document.getElementById("console");
    var consoleText = null;
    var previous = null;
    if (consoleElement != null && consoleProcess != null) {
        var currentText = consoleElement.innerHTML;
        var consoleData = consoleWindow[consoleProcess]; // is ther an update?
        if (consoleData != null && (currentText == "" || consoleData.update == true)) {
            consoleData.update = false; // clear the update
            for (var i = 0; i < consoleData.list.length; i++) {
                var next = consoleData.list[i];
                if (previous == null) {
                    if (next.error) {
                        consoleText = "<span class='consoleError'>" + next.text;
                    }
                    else {
                        consoleText = "<span class='consoleNormal'>" + next.text;
                    }
                    previous = next.error;
                }
                else if (next.error != previous) {
                    consoleText += "</span>";
                    if (next.error) {
                        consoleText += "<span class='consoleError'>" + next.text;
                    }
                    else {
                        consoleText += "<span class='consoleNormal'>" + next.text;
                    }
                    previous = next.error;
                }
                else {
                    consoleText += next.text;
                }
            }
            if (consoleText != null) {
                consoleText += "</span>";
                consoleElement.innerHTML = consoleText;
                consoleElement.scrollTop = consoleElement.scrollHeight;
            }
        }
    }
}
function updateConsoleFocus(processToFocus) {
    if (consoleProcess != processToFocus) {
        clearConsole();
        consoleProcess = processToFocus;
        showConsole();
    }
}
function createConsole(socket, type, value) {
    var message = JSON.parse(value);
    var newProcess = message.process;
    var consoleData = consoleWindow[newProcess];
    consoleWindow[newProcess] = {
        list: [],
        size: 0,
        update: true
    };
    updateConsoleFocus(newProcess);
}
function updateConsole(socket, type, value) {
    var offset = value.indexOf(':');
    var updateProcess = value.substring(0, offset);
    var updateText = value.substring(offset + 1);
    var node = {
        error: type == 'PRINT_ERROR',
        text: updateText
    };
    var consoleData = consoleWindow[updateProcess];
    if (consoleData == null) {
        consoleData = {
            list: [],
            size: 0,
            update: true
        };
        consoleWindow[updateProcess] = consoleData;
    }
    consoleData.list.push(node); // put at the end, i.e index consoleWindow.length - 1
    consoleData.size += updateText.length; // update the size of the console
    while (consoleData.list.length > 3 && consoleData.size > consoleCapacity) {
        var removeNode = consoleData.list.shift(); // remove from the start, i.e index 0
        if (removeNode != null) {
            consoleData.size -= removeNode.text.length;
        }
    }
    consoleData.update = true;
}
registerModule("console", "Console module: console.js", registerConsole, ["common", "socket"]);

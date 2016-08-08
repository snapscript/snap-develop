var consoleWindow = [];
var consoleUpdate = false;
var consoleCapacity = 5000;
var consoleProcess = null;
function registerConsole() {
    createRoute('PRINT_ERROR', updateConsole);
    createRoute('PRINT_OUTPUT', updateConsole);
    setInterval(showConsole, 200); // prevents reflow overload when console is busy
}
function updateConsoleFont(fontFamily, fontSize) {
    var consoleElement = document.getElementById("console");
    if (consoleElement != null) {
        consoleElement.style.fontFamily = fontFamily;
        consoleElement.style.fontSize = fontSize;
    }
}
function clearConsole() {
    var consoleElement = document.getElementById("console");
    if (consoleElement != null) {
        document.getElementById("console").innerHTML = "";
    }
    consoleProcess = null;
    consoleWindow = [];
}
function showConsole() {
    var consoleElement = document.getElementById("console");
    var consoleText = null;
    var previous = null;
    if (consoleElement != null) {
        var currentText = consoleElement.innerHTML;
        if (currentText == "" || consoleUpdate) {
            consoleUpdate = false;
            for (var i = 0; i < consoleWindow.length; i++) {
                var next = consoleWindow[i];
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
function updateConsoleFocus(updateProcess) {
    if (updateProcess != consoleProcess) {
        clearConsole();
    }
    consoleProcess = updateProcess;
}
function updateConsole(socket, type, value) {
    var offset = value.indexOf(':');
    var updateProcess = value.substring(0, offset);
    var updateText = value.substring(offset + 1);
    var node = {
        error: type == 'PRINT_ERROR',
        process: updateProcess,
        text: updateText
    };
    updateConsoleFocus(updateProcess); // focus on only one process
    consoleWindow.push(node); // put at the end, i.e index consoleWindow.length - 1
    if (consoleWindow.length > consoleCapacity) {
        consoleWindow.shift(); // remove from the start, i.e index 0
    }
    consoleProcess = updateProcess;
    consoleUpdate = true;
}
registerModule("console", "Console module: console.js", registerConsole, ["common", "socket"]);

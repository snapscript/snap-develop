define(["require", "exports", "socket"], function (require, exports, socket_1) {
    "use strict";
    var Alerts;
    (function (Alerts) {
        function registerAlerts() {
            socket_1.EventBus.createRoute('ALERT', createAlert);
        }
        Alerts.registerAlerts = registerAlerts;
        function createAlert(socket, type, object) {
            var message = JSON.parse(object);
            var text = message.message;
            w2alert('<table border="0" width="100%">' +
                '  <tr>' +
                '    <td>&nbsp;&nbsp</td>' +
                '    <td align="right"><img src="${IMAGE_FOLDER}/warning.png" height="20px"></td>' +
                '    <td align="left"><div class="alertText">' + text + '</div></td>' +
                '  </tr>' +
                '</table>');
        }
        function createConfirmAlert(title, message, yesButton, noButton, yesCallback, noCallback) {
            var text = '<table border="0" width="100%">' +
                '  <tr>' +
                '    <td>&nbsp;&nbsp</td>' +
                '    <td align="right"><img src="${IMAGE_FOLDER}/warning.png" height="20px"></td>' +
                '    <td align="left"><div class="alertText">' + message + '</div></td>' +
                '  </tr>' +
                '</table>';
            var options = {
                msg: text,
                title: title,
                width: 450,
                height: 220,
                yes_text: yesButton,
                yes_class: 'btn dialogButton',
                yes_style: '',
                yes_callBack: yesCallback,
                no_text: noButton,
                no_class: 'btn dialogButton',
                no_style: '',
                no_callBack: noCallback,
                callBack: null // common callBack
            };
            w2confirm(options);
        }
        Alerts.createConfirmAlert = createConfirmAlert;
        function createPromptAlert(title, yesButton, noButton, yesCallback) {
            var text = '<table border="0" width="100%">' +
                '  <tr>' +
                '    <td>&nbsp;&nbsp</td>' +
                '    <td align="right"><img src="${IMAGE_FOLDER}/search_glass.png" height="20px"></td>' +
                '    <td>&nbsp;&nbsp</td>' +
                '    <td align="left"><input id="textToSearchFor" type="text" name="token" width="180"></td>' +
                '  </tr>' +
                '</table>';
            var findCallback = function () {
                var element = document.getElementById("textToSearchFor");
                if (element && yesCallback) {
                    yesCallback(element.value);
                    yesCallback = null;
                }
            };
            var cancelCallback = function () { };
            var focusCallback = function () {
                var element = document.getElementById("textToSearchFor");
                if (element) {
                    element.focus();
                }
            };
            var options = {
                msg: text,
                title: title,
                width: 450,
                height: 220,
                yes_text: yesButton,
                yes_class: 'btn dialogButton',
                yes_style: '',
                yes_callBack: findCallback,
                no_text: noButton,
                no_class: 'btn dialogButton',
                no_style: '',
                no_callBack: cancelCallback,
                callBack: findCallback // common callBack
            };
            w2confirm(options);
            focusCallback();
        }
        Alerts.createPromptAlert = createPromptAlert;
    })(Alerts = exports.Alerts || (exports.Alerts = {}));
});
//ModuleSystem.registerModule("alert", "Alert module: alert.js", null, Alerts.registerAlerts, ["common", "socket"]); 

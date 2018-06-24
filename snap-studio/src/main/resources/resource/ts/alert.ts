import {w2ui} from "w2ui"
import {EventBus} from "socket"
import {Common} from "common"

export module Alerts {
   
   export function registerAlerts() {
      EventBus.createRoute('ALERT', createAlert);
   }
   
   function createAlert(socket, type, object) {
      let message = JSON.parse(object);
      let text = message.message;
      
      w2alert('<table border="0" width="100%">'+
              '  <tr>'+
              '    <td>&nbsp;&nbsp</td>'+
              '    <td align="right"><img src="${IMAGE_FOLDER}/warning.png" height="20px"></td>'+
              '    <td align="left"><div class="alertText">'+text+'</div></td>'+
              '  </tr>'+
              '</table>');
   
   }
   
   export function createConfirmAlert(title, message, yesButton, noButton, yesCallback, noCallback) {
      let text = '<table border="0" width="100%">'+
                  '  <tr>'+
                  '    <td>&nbsp;&nbsp</td>'+
                  '    <td align="right"><img src="${IMAGE_FOLDER}/warning.png" height="20px"></td>'+
                  '    <td align="left"><div class="alertText">'+message+'</div></td>'+
                  '  </tr>'+
                  '</table>';
      let options = {
            msg          : text,
            title        : title,
            width        : 450,       // width of the dialog
            height       : 220,       // height of the dialog
            yes_text     : yesButton,     // text for yes button
            yes_class    : 'btn dialogButton',        // class for yes button
            yes_style    : '',        // style for yes button
            yes_callBack : yesCallback,      // callBack for yes button
            no_text      : noButton,      // text for no button
            no_class     : 'btn dialogButton',        // class for no button
            no_style     : '',        // style for no button
            no_callBack  : noCallback,      // callBack for no button
            callBack     : null       // common callBack
        };
      w2confirm(options);
   }
   
   export function createRunPromptAlert(title, placeholder, yesButton, noButton, yesCallback) {
      createIconPromptAlert(title, placeholder, yesButton, noButton, yesCallback, "${IMAGE_FOLDER}/run.png", "Arguments");
   }
   
   export function createDebugPromptAlert(title, placeholder, yesButton, noButton, yesCallback) {
      createIconPromptAlert(title, placeholder, yesButton, noButton, yesCallback, "${IMAGE_FOLDER}/debug.png", "Arguments");
   }

   export function createRemoteDebugPromptAlert(title, placeholder, yesButton, noButton, yesCallback) {
      createIconPromptAlert(title, placeholder, yesButton, noButton, yesCallback, "${IMAGE_FOLDER}/debug.png", "Address");
   }
   
   function createIconPromptAlert(title, placeholder, yesButton, noButton, yesCallback, iconFile, textLabel) {
      let text = '<table border="0" width="100%">'+
      '  <tr>'+
      '    <td>&nbsp;&nbsp</td>'+
      '    <td align="right"><img src="' + iconFile + '" height="20px"></td>'+
      '    <td>&nbsp;</td>'+
      '    <td align="left">' + textLabel + '</td>'+      
      '    <td>&nbsp;</td>'+
      '    <td align="left"><input id="textToSearchFor" type="text" placeholder="' + placeholder + '" name="token" width="180"></td>'+
      '  </tr>'+
      '</table>';
      
      let findCallback = function() {
         let element: HTMLElement = document.getElementById("textToSearchFor");
         
         if(element && yesCallback) {
            yesCallback(element.value);
            yesCallback = null;
         }
      };
      let cancelCallback = function(){};
      let focusCallback = function(){
         let element: HTMLElement = document.getElementById("textToSearchFor");
         
         if(element) {
            element.focus();
         }
      };
      let options = {
         msg          : text,
         title        : title,
         width        : 450,       // width of the dialog
         height       : 220,       // height of the dialog
         yes_text     : yesButton,     // text for yes button
         yes_class    : 'btn dialogButton',        // class for yes button
         yes_style    : '',        // style for yes button
         yes_callBack : findCallback,      // callBack for yes button
         no_text      : noButton,      // text for no button
         no_class     : 'btn dialogButton',        // class for no button
         no_style     : '',        // style for no button
         no_callBack  : cancelCallback      // callBack for no button
      };
      w2confirm(options);
      focusCallback();
   }
}
//ModuleSystem.registerModule("alert", "Alert module: alert.js", null, Alerts.registerAlerts, ["common", "socket"]);
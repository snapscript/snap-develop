
module Alerts {
   
   export function registerAlerts() {
      createRoute('ALERT', createAlert);
   }
   
   function createAlert(socket, type, text) {
      var message = JSON.parse(text);
      var text = message.message;
      
      w2alert('<table border="0" width="100%">'+
              '  <tr>'+
              '    <td>&nbsp;&nbsp</td>'+
              '    <td align="right"><img src="/img/warning.png" height="20px"></td>'+
              '    <td align="left">&nbsp;&nbsp'+text+'</td>'+
              '  </tr>'+
              '</table>');
   
   }
   
   export function createConfirmAlert(title, message, yesButton, noButton, yesCallback, noCallback) {
      var text = '<table border="0" width="100%">'+
                  '  <tr>'+
                  '    <td>&nbsp;&nbsp</td>'+
                  '    <td align="right"><img src="/img/warning.png" height="20px"></td>'+
                  '    <td align="left">&nbsp;&nbsp'+message+'</td>'+
                  '  </tr>'+
                  '</table>';
      var options = {
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
}
ModuleSystem.registerModule("alert", "Alert module: alert.js", Alerts.registerAlerts, ["common", "socket"]);
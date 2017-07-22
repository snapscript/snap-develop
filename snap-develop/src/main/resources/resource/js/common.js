define(["require", "exports"], function (require, exports) {
    "use strict";
    var Common;
    (function (Common) {
        function openDialog(address, name, width, height) {
            var time = currentTime();
            var handle = window.open(address, name + time, 'height=' + height + ',width=' + width);
            if (handle != undefined) {
                if (handle.focus) {
                    handle.resizeTo(width, height);
                    handle.focus();
                }
            }
            return false;
        }
        Common.openDialog = openDialog;
        function extractParameter(name) {
            var source = name.replace(/[\[]/, "\\\[").replace(/[\]]/, "\\\]");
            var expression = "[\\?&]" + source + "=([^&#]*)";
            var regex = new RegExp(expression);
            var results = regex.exec(window.location.href);
            if (results == null) {
                return "";
            }
            return results[1];
        }
        Common.extractParameter = extractParameter;
        function decodeValue(value) {
            if (value.length > 0) {
                var result = '@' + value; // ensure we do not reference larger parent string
                var text = result.substring(2);
                if (value.charAt(0) == '<') {
                    var encoded = text.toString();
                    var decoded = '';
                    for (var i = 0; i < encoded.length; i += 2) {
                        var next = encoded.substr(i, 2);
                        var decimal = parseInt(next, 16);
                        decoded += String.fromCharCode(decimal);
                    }
                    return decoded;
                }
                return text;
            }
            return null;
        }
        Common.decodeValue = decodeValue;
        function updateTableRecords(update, name) {
            var current = w2ui[name].records; // find the table
            if (update.length == current.length) {
                var different = false;
                for (var i = 0; i < update.length; i++) {
                    var currentRow = current[i];
                    var updateRow = update[i];
                    if (currentRow.length != updateRow.length) {
                        different = true;
                        break;
                    }
                    for (var currentColumn in currentRow) {
                        if (currentRow.hasOwnProperty(currentColumn)) {
                            if (!updateRow.hasOwnProperty(currentColumn)) {
                                different = true;
                                break;
                            }
                            var currentCell = currentRow[currentColumn];
                            var updateCell = updateRow[currentColumn];
                            if (currentCell != updateCell) {
                                different = true;
                                break;
                            }
                        }
                    }
                }
                if (different) {
                    w2ui[name].records = update;
                    w2ui[name].refresh();
                }
            }
            else {
                w2ui[name].records = update;
                w2ui[name].refresh();
            }
        }
        Common.updateTableRecords = updateTableRecords;
        function isChildElementVisible(parentElement, childElement) {
            var childRect = childElement.getBoundingClientRect();
            var parentRect = parentElement.getBoundingClientRect();
            var topOfChildRect = childRect.top;
            var topOfParentRect = parentRect.top;
            var bottomOfChildRect = childRect.top + childRect.height;
            var bottomOfParentRect = parentRect.top + parentElement.clientHeight;
            return topOfChildRect > topOfParentRect && bottomOfChildRect < bottomOfParentRect;
        }
        Common.isChildElementVisible = isChildElementVisible;
        function calculateScrollOffset(parentElement, childElement) {
            var childRect = childElement.getBoundingClientRect();
            var parentRect = parentElement.getBoundingClientRect();
            var topOfChildRect = childRect.top;
            var topOfParentRect = parentRect.top;
            if (topOfChildRect < topOfParentRect) {
                return topOfChildRect - topOfParentRect;
            }
            var bottomOfChildRect = childRect.top + childRect.height;
            //var bottomOfParentRect = parentRect.top + parentRect.height;
            var bottomOfParentRect = parentRect.top + parentElement.clientHeight;
            if (bottomOfChildRect > bottomOfParentRect) {
                return bottomOfChildRect - bottomOfParentRect;
            }
            return 0;
        }
        Common.calculateScrollOffset = calculateScrollOffset;
        function stringEndsWith(text, token) {
            if (text && token) {
                return text.slice(-token.length) == token;
            }
            return false;
        }
        Common.stringEndsWith = stringEndsWith;
        function isMacintosh() {
            return navigator.platform.indexOf('Mac') > -1;
        }
        Common.isMacintosh = isMacintosh;
        function isWindows() {
            return navigator.platform.indexOf('Win') > -1;
        }
        Common.isWindows = isWindows;
        function escapeHtml(text) {
            return text
                .replace(/&/g, "&amp;")
                .replace(/</g, "&lt;")
                .replace(/>/g, "&gt;")
                .replace(/"/g, "&quot;")
                .replace(/'/g, "&#039;");
        }
        Common.escapeHtml = escapeHtml;
        function clearHtml(text) {
            return text
                .replace(/<br>/g, "")
                .replace(/&quot;/g, "\"")
                .replace(/&lt;/g, "<")
                .replace(/&gt;/g, ">")
                .replace(/&nbsp;/g, "")
                .replace(/&amp;/g, "&");
        }
        Common.clearHtml = clearHtml;
        function currentTime() {
            var date = new Date();
            return date.getTime();
        }
        Common.currentTime = currentTime;
    })(Common = exports.Common || (exports.Common = {}));
});
//ModuleSystem.registerModule("common", "Common module: common.js", null, null, []); 

var LinkIndexer;
(function (LinkIndexer) {
    var FUNCTION_REGEX = /(function|static|public|private|abstract|override|)\s+([a-z][a-zA-Z0-9]*)\s*\(/g;
    var VARIABLE_REGEX = /(var|const)\s+([a-z][a-zA-Z0-9]*)/g;
    var TYPE_REGEX = /(class|trait|enum)\s+([A-Z][a-zA-Z0-9]*)/g;
    var IMPORT_REGEX = /import\s+([a-z][a-zA-Z0-9\.]*)\.([A-Z][a-zA-Z]*)/g;
    var FUNCTION_TEMPLATES = ["%s("];
    var VARIABLE_TEMPLATES = ["%s.", "%s=", "%s =", "%s<", "%s <", "%s>", "%s >", "%s!", "%s !", "%s-", "%s -", "%s+", "%s +", "%s*", "%s *", "%s%", "%s %", "%s/", "%s /"];
    var TYPE_TEMPLATES = ["new %s(", "%s.", ":%s", ": %s", "extends %s", "with %s", "extends  %s", "with  %s", ".%s;", " as %s", "%s["];
    var IMPORT_TEMPLATES = ["new %s(", "%s.", ":%s", ": %s", "extends %s", "with %s", "extends  %s", "with  %s", ".%s;", " as %s", "%s["];
    var tokens = [];
    function indexEditorTokens(text, resource) {
        var token = resource.toLowerCase();
        var tokenList = {};
        if (token.endsWith(".snap")) {
            var lines = text.split(/\r?\n/);
            for (var i = 0; i < lines.length; i++) {
                var line = lines[i];
                indexEditorLine(line, i + 1, FUNCTION_REGEX, tokenList, FUNCTION_TEMPLATES, false);
                indexEditorLine(line, i + 1, VARIABLE_REGEX, tokenList, VARIABLE_TEMPLATES, false);
                indexEditorLine(line, i + 1, IMPORT_REGEX, tokenList, IMPORT_TEMPLATES, true);
                indexEditorLine(line, i + 1, TYPE_REGEX, tokenList, TYPE_TEMPLATES, false);
            }
        }
        editorCurrentTokens = tokenList; // keep these tokens for indexing
        if (editorFocusToken != null) {
            var focusToken = editorCurrentTokens[editorFocusToken];
            if (focusToken != null) {
                setTimeout(function () {
                    FileEditor.showEditorLine(focusToken.line); // focus on the line there was a token
                }, 100);
                editorFocusToken = null; // clear for next open
            }
        }
    }
    function indexEditorLine(line, number, expression, tokenList, templates, external) {
        expression.lastIndex = 0; // you have to reset regex to its start position
        var tokens = expression.exec(line);
        if (tokens != null && tokens.length > 0) {
            var resourceToken = tokens[1]; // only for 'import' which is external
            var indexToken = tokens[2];
            for (var i = 0; i < templates.length; i++) {
                var template = templates[i];
                var indexKey = template.replace("%s", indexToken);
                if (external) {
                    tokenList[indexKey] = {
                        resource: "/" + resourceToken.replace(".", "/") + ".snap",
                        line: number // save the line number
                    };
                }
                else {
                    tokenList[indexKey] = {
                        resource: null,
                        line: number // save the line number
                    };
                }
            }
        }
    }
})(LinkIndexer || (LinkIndexer = {}));
function createEditorLinks(editor, customMatchFunction, customOpenFunction) {
    aceDefine("hoverlink", [], function (require, exports, module) {
        "use strict";
        var oop = require("ace/lib/oop");
        var event = require("ace/lib/event");
        var Range = require("ace/range").Range;
        var EventEmitter = require("ace/lib/event_emitter").EventEmitter;
        var HoverLink = function (editor) {
            if (editor.hoverLink)
                return;
            editor.hoverLink = this;
            this.editor = editor;
            this.update = this.update.bind(this);
            this.onMouseMove = this.onMouseMove.bind(this);
            this.onMouseOut = this.onMouseOut.bind(this);
            this.onClick = this.onClick.bind(this);
            event.addListener(editor.renderer.scroller, "mousemove", this.onMouseMove);
            event.addListener(editor.renderer.content, "mouseout", this.onMouseOut);
            event.addListener(editor.renderer.content, "click", this.onClick);
        };
        (function () {
            oop.implement(this, EventEmitter);
            this.token = {};
            this.range = new Range();
            this.update = function () {
                this.$timer = null;
                var editor = this.editor;
                var renderer = editor.renderer;
                var canvasPos = renderer.scroller.getBoundingClientRect();
                var offset = (this.x + renderer.scrollLeft - canvasPos.left - renderer.$padding) / renderer.characterWidth;
                var row = Math.floor((this.y + renderer.scrollTop - canvasPos.top) / renderer.lineHeight);
                var col = Math.round(offset);
                var screenPos = {
                    row: row,
                    column: col,
                    side: offset - col > 0 ? 1 : -1
                };
                var session = editor.session;
                var docPos = session.screenToDocumentPosition(screenPos.row, screenPos.column);
                var selectionRange = editor.selection.getRange();
                if (!selectionRange.isEmpty()) {
                    if (selectionRange.start.row <= row && selectionRange.end.row >= row)
                        return this.clear();
                }
                var line = editor.session.getLine(docPos.row);
                if (docPos.column == line.length) {
                    var clippedPos = editor.session.documentToScreenPosition(docPos.row, docPos.column);
                    if (clippedPos.column != screenPos.column) {
                        return this.clear();
                    }
                }
                var token = this.findLink(docPos.row, docPos.column);
                this.link = token;
                if (!token) {
                    return this.clear();
                }
                this.isOpen = true;
                editor.renderer.setCursorStyle("pointer");
                session.removeMarker(this.marker);
                this.range = new Range(token.row, token.start, token.row, token.start + token.value.length);
                this.marker = session.addMarker(this.range, "ace_link_marker", "text", true);
            };
            this.clear = function () {
                if (this.isOpen) {
                    this.link = null;
                    this.editor.session.removeMarker(this.marker);
                    this.editor.renderer.setCursorStyle("");
                    this.isOpen = false;
                }
            };
            this.onClick = function () {
                if (this.link) {
                    this.link.editor = this.editor;
                    this._signal("open", this.link);
                    this.clear();
                }
            };
            this.findLink = function (row, column) {
                var editor = this.editor;
                var session = editor.session;
                var line = session.getLine(row);
                var match = customMatchFunction(line, column);
                if (!match)
                    return;
                match.row = row;
                return match;
            };
            this.onMouseMove = function (e) {
                if (this.editor.$mouseHandler.isMousePressed) {
                    if (!this.editor.selection.isEmpty())
                        this.clear();
                    return;
                }
                this.x = e.clientX;
                this.y = e.clientY;
                this.update();
            };
            this.onMouseOut = function (e) {
                this.clear();
            };
            this.destroy = function () {
                this.onMouseOut();
                event.removeListener(this.editor.renderer.scroller, "mousemove", this.onMouseMove);
                event.removeListener(this.editor.renderer.content, "mouseout", this.onMouseOut);
                delete this.editor.hoverLink;
            };
        }).call(HoverLink.prototype);
        exports.HoverLink = HoverLink;
    });
    HoverLink = aceRequire("hoverlink").HoverLink;
    editor.hoverLink = new HoverLink(editor);
    editor.hoverLink.on("open", function (event) {
        customOpenFunction(event);
    });
}

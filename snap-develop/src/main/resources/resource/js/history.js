function trackHistory() {
    $(window).on('hashchange', function () {
        updateEditorFromHistory();
    });
    setTimeout(updateEditorFromHistory, 200);
}
function updateEditorFromHistory() {
    var location = window.location.hash;
    var hashIndex = location.indexOf('#');
    if (hashIndex != -1) {
        var resource = location.substring(hashIndex + 1);
        var resourceData = createResourcePath(resource);
        var editorData = loadEditor();
        var editorResource = editorData.resource;
        if (editorResource == null || editorResource.resourcePath != resourceData.resourcePath) {
            openTreeFile(resourceData.resourcePath, function () {
                var editor = ace.edit("editor"); // XXX this is the wrong place for this!!
                editor.setReadOnly(false); // make sure its editable
            });
        }
    }
}
registerModule("history", "History module: history.js", trackHistory, ["common", "editor"]);

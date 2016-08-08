var spinnerHiding = false;
var spinner = null;
function finishedLoading() {
    if (spinnerHiding == false) {
        var func = function () {
            hideSpinner();
        };
        window.setTimeout(func, 1000);
    }
}
function showSpinner() {
    if (spinnerHiding == true) {
        spinnerHiding = false;
        document.getElementById("overlay").style.visibility = 'visible';
    }
}
function hideSpinner() {
    if (spinnerHiding == false) {
        window.setTimeout(function () {
            spinnerHiding = true;
            document.getElementById("overlay").style.visibility = 'hidden';
        }, 1000);
    }
}
function createSpinner() {
    var opts = {
        lines: 13,
        length: 30,
        width: 15,
        radius: 40,
        corners: 1,
        rotate: 0,
        direction: 1,
        color: '#ffffff',
        speed: 1,
        trail: 60,
        shadow: false,
        hwaccel: false,
        className: 'spinner',
        zIndex: 2e9,
        top: '50%',
        left: '50%' // Left position relative to parent
    };
    var target = document.getElementById('spin');
    spinner = new Spinner(opts).spin(target);
}
registerModule("spinner", "Spinner module: spinner.js", createSpinner, []);

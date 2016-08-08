function showSplash() {
	$(".meter > span").each(function() {
		$(this)
			.data("origWidth", $(this).width())
			.width(0)
			.animate({
				width: $(this).data("origWidth")
			}, 3000);
	});
};

registerModule("splash", "Splash screen module: splash.js", showSplash, []);
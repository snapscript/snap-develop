
module Splash {

   export function showSplash() {
   	$(".meter > span").each(function() {
   		$(this)
   			.data("origWidth", $(this).width())
   			.width(0)
   			.animate({
   				width: $(this).data("origWidth")
   			}, 3000);
   	});
   };
}

ModuleSystem.registerModule("splash", "Splash screen module: splash.js", null, Splash.showSplash, []);
package org.snapscript.studio.browser;

import lombok.extern.slf4j.Slf4j;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.snapscript.studio.ui.swt.Chromium;

@Slf4j
public class ChromiumApplication {

   public static void launch(BrowserContext context) {
      Display display = new Display();
      Shell shell = new Shell(display);
      FillLayout layout = new FillLayout();

      shell.setLayout(layout);

      Chromium browser = new Chromium(shell, SWT.NONE);
      
      try {
         shell.setText(context.getDirectory().getCanonicalPath());
      }catch(Exception e){
         log.info("Could not set title", e);
      }
      browser.setUrl(context.getTarget());
      shell.setSize(1200, 800);
      shell.setBackground(new Color(display, 0x66, 0x69, 0x70));
      shell.open();

      while (!shell.isDisposed()) {
         if (!display.readAndDispatch()) {
            display.sleep();
         }
      }
      display.dispose();
   }
}

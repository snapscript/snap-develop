package org.snapscript.studio.service;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.snapscript.studio.common.ClassPathReader;

@Slf4j
public class SplashScreen {   

   private static final SplashPanel INSTANCE = createPanel(
         "Please wait ...",         
         "resource/img/logo.png",
         "0xffffff",
         "0x505050"
   );   
   
   public static SplashPanel getPanel() {
      return INSTANCE;
   }
   
   private static SplashPanel createPanel(final String resource, final String background, final String foreground, final String message) {
      final CompletableFuture<SplashPanel> future = new CompletableFuture<SplashPanel>();
      final SplashPanelController controller = new SplashPanelController(future);      
      
      try {
         UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
         SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
               try {
                  SplashPanel panel = createSplashScreen(resource, background, foreground, message);
                  
                  future.complete(panel);
                  controller.start();
               }catch(Exception e){
                  log.info("Could not create splash dialog", e);
               }
            }
         });
      } catch(Exception e) {
         log.info("Could not create splash panel", e);
      }
      return controller;
   }
   
   private static class SplashPanelController implements SplashPanel, Runnable {
      
      private final Future<SplashPanel> panel;
      private final BlockingQueue<Runnable> tasks;
      private final AtomicBoolean active;
      private final AtomicLong expiry;
      private final Thread thread;
      
      public SplashPanelController(Future<SplashPanel> panel) {
         this.tasks = new ArrayBlockingQueue<Runnable>(1000);
         this.active = new AtomicBoolean();
         this.thread = new Thread(this);
         this.expiry = new AtomicLong();
         this.panel = panel;
      }
      
      @Override
      public void update(final String message) {
         tasks.offer(new Runnable() {
            @Override
            public void run() {
               try {         
                  panel.get(4, TimeUnit.SECONDS).update(message);
               } catch(Exception e) {
                  log.debug("Could not update splash panel", e);
               }
            }
         });
      }
      
      @Override
      public void show(final long duration) {
         long time = System.currentTimeMillis();
         
         expiry.set(time + duration);
         tasks.offer(new Runnable() {
            @Override
            public void run() {
               try {         
                  panel.get(4, TimeUnit.SECONDS).show(duration);
               } catch(Exception e) {
                  log.debug("Could not show splash panel", e);
               }
            }
         });
      }
      
      @Override
      public void hide() {
         tasks.offer(new Runnable() {
            @Override
            public void run() {
               try {         
                  panel.get(4, TimeUnit.SECONDS).hide();
               } catch(Exception e) {
                  log.debug("Could not hide splash panel", e);
               }
            }
         });
      }
      
      @Override
      public void run() {
         try {
            while(active.get()) {
               Runnable task = tasks.poll(1, TimeUnit.SECONDS);
               
               if(task != null) {
                  task.run();
               }
               long time = System.currentTimeMillis();
               long threshold = expiry.get();
               
               if(time > threshold) {
                  active.set(false);
               }               
            }
         } catch(Exception e){
            log.debug("Could not process splash panel events", e);                           
         } finally{
            active.set(false);
         }
         
      }
      
      public void start() {
         if(active.compareAndSet(false, true)) {
            thread.start();
         }
      }
   }
   
   @AllArgsConstructor
   private static class SplashDialog implements SplashPanel {
      
      private final JDialog dialog;
      private final JLabel label;
      
      @Override
      public void update(String message) {
         label.setText(message);
         label.invalidate();
      }
      
      @Override
      public void show(long duration) {
         dialog.setVisible(true);
      }
      
      @Override
      public void hide() {
         dialog.setVisible(false);
      }
   }

   private static SplashPanel createSplashScreen(String message, String resource, String background, String foreground) throws Exception {
       JDialog dialog = new JDialog((Frame) null);
       BufferedImage image = loadLogo(resource);
       JPanel panel = createPanel(image, background, foreground, 600, 400);
       JLabel label = createLabel(message, foreground);
       
       panel.add(label);
       dialog.setModal(false);
       dialog.setUndecorated(true);
       dialog.add(panel);
       dialog.pack();
       dialog.setLocationRelativeTo(null);      
       
       return new SplashDialog(dialog, label);
       
   }
   
   private static JLabel createLabel(String message, String color) {
      JLabel text = new JLabel(message);
      
      text.setForeground(Color.decode(color));
      text.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));  
      return text;
   }
   
   private static JPanel createPanel(BufferedImage image, String background, String border, int width, int height) {
      JPanel panel = new JPanel();
      JPanel spacer = new JPanel();
      ImageIcon icon = new ImageIcon(image);      
      JLabel label = new JLabel(icon);
      BorderLayout layout = new BorderLayout();
      
      label.setSize(width, height);
      label.setLayout(layout); 
      spacer.setSize(10, 10);
      spacer.setBackground(Color.decode(background));
      panel.setBackground(Color.decode(background));
      BoxLayout verticalLayout = new BoxLayout(panel, BoxLayout.Y_AXIS);
      panel.setLayout(verticalLayout);
      panel.setBorder(BorderFactory.createLineBorder(Color.decode(border)));
      panel.add(spacer);
      panel.add(label); 
      return panel;
   }
   
   private static BufferedImage loadLogo(String resource) {
      try {
         InputStream data = ClassPathReader.findResourceAsStream(resource);        
         return ImageIO.read(data);
      } catch(Exception e) {
         log.info("Could not load image {}", resource, e);
      }
      return null;
   }
}

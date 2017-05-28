
package org.snapscript.develop.command;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.simpleframework.http.Path;
import org.simpleframework.http.socket.FrameChannel;
import org.snapscript.agent.log.ProcessLogger;
import org.snapscript.develop.BackupManager;
import org.snapscript.develop.ProcessManager;
import org.snapscript.develop.common.Problem;
import org.snapscript.develop.common.ProblemFinder;
import org.snapscript.develop.resource.display.DisplayDefinition;
import org.snapscript.develop.resource.display.DisplayPersister;
import org.snapscript.develop.resource.project.ProjectProblemFinder;
import org.snapscript.develop.resource.tree.TreeContext;
import org.snapscript.develop.resource.tree.TreeContextManager;

public class CommandListener {
   
   private final DisplayPersister displayPersister;
   private final CommandEventForwarder forwarder;
   private final ProjectProblemFinder problemFinder;
   private final TreeContextManager treeManager;
   private final CommandFilter commandFilter;
   private final CommandClient commandClient;
   private final ProcessManager processManager;
   private final ProcessLogger processLogger;
   private final ProblemFinder finder;
   private final BackupManager backupManager;
   private final String cookie;
   private final String project;
   private final File root;
   private final Path path;
   
   public CommandListener(
         ProcessManager processManager, 
         ProjectProblemFinder problemFinder, 
         DisplayPersister displayPersister,
         FrameChannel frameChannel, 
         ProcessLogger processLogger, 
         BackupManager backupManager, 
         TreeContextManager treeManager, 
         Path path, 
         File root, 
         String project, 
         String cookie) 
   {
      this.commandFilter = new CommandFilter();
      this.commandClient = new CommandClient(frameChannel, project);
      this.forwarder = new CommandEventForwarder(commandClient, commandFilter, processLogger, project);
      this.finder = new ProblemFinder();
      this.displayPersister = displayPersister;
      this.treeManager = treeManager;
      this.problemFinder = problemFinder;
      this.backupManager = backupManager;
      this.processLogger = processLogger;
      this.processManager = processManager;
      this.project = project;
      this.cookie = cookie;
      this.root = root;
      this.path = path;
   }

   public void onExplore(ExploreCommand command) {
      String resource = command.getResource();
      
      try {
         if(resource != null) {
            File file = new File(root, "/" + resource);
            
            if(file.isDirectory()) {
               file = file.getParentFile();
            }
            String path = file.getCanonicalPath();
            boolean exists = file.exists();
            boolean directory = file.isDirectory();
            
            if(exists && directory) {
               List<String> arguments = new ArrayList<String>();
               
               arguments.add("explorer");
               arguments.add(path);

               ProcessBuilder builder = new ProcessBuilder(arguments);
               builder.start();
            }
         }
      } catch(Exception e) {
         processLogger.info("Error exploring directory " + resource, e);
      }
   }
   
   public void onSave(SaveCommand command) {
      String resource = command.getResource();
      String source = command.getSource();
      
      try {
         if(!command.isDirectory()) {
            Problem problem = finder.parse(project, resource, source);
            File file = new File(root, "/" + resource);
            boolean exists = file.exists();
            
            if(exists) {
               backupManager.backupFile(file, project);
            }
            if(command.isCreate() && exists) {
               commandClient.sendAlert(resource, "Resource " + resource + " already exists");
            } else {
               backupManager.saveFile(file, source);
               
               if(problem == null) {
                  commandClient.sendSyntaxError(resource, "", 0, -1); // clear problem
               } else {
                  String description = problem.getDescription();
                  int line = problem.getLine();
                  long time = System.currentTimeMillis();
                  
                  commandClient.sendSyntaxError(resource, description, time, line);
               }
               if(!exists) {
                  commandClient.sendReloadTree();
               }
            } 
         } else {
            File file = new File(root, "/"+resource);
            
            if(!file.exists()) {
               file.mkdirs();
               commandClient.sendReloadTree();
            }
         }
      } catch(Exception e) {
         processLogger.info("Error saving " + resource, e);
      }
   }
   
   public void onRename(RenameCommand command) {
      Boolean dragAndDrop = command.getDragAndDrop();
      String from = command.getFrom();
      String to = command.getTo();
      
      try {
         if(Boolean.TRUE.equals(dragAndDrop)) {
            processLogger.info("Drag and drop from: " + from + " to: " + to);
         }
         File fromFile = new File(root, "/" + from);
         File toFile = new File(root, "/" + to); 
         
         if(!fromFile.equals(root)) { // don't rename root
            boolean fromExists = fromFile.exists();
            boolean toExists = toFile.exists();
            
            if(!fromExists) {
               commandClient.sendAlert(from, "Resource " + from + " does not exist");
            } else {
               if(toExists) {
                  commandClient.sendAlert(to, "Resource " + to + " already exists");
               } else {
                  if(fromFile.renameTo(toFile)){
                     commandClient.sendReloadTree();
                  } else {
                     commandClient.sendAlert(from, "Could not rename " + from + " to " + to);
                  }
               }
            }
         } 
      } catch(Exception e) {
         processLogger.info("Error renaming " + from, e);
      }
   }   
   
   public void onExecute(ExecuteCommand command) {
      String resource = command.getResource();
      String source = command.getSource();
      
      try {
         Problem problem = finder.parse(project, resource, source);
         
         if(problem == null) {
            File file = new File(root, "/" + resource);
            boolean exists = file.exists();
            
            if(exists) {
               backupManager.backupFile(file, project);
            }
            backupManager.saveFile(file, source);
            commandClient.sendSyntaxError(resource, "", 0, -1); // clear problem
            processManager.register(forwarder); // make sure we are registered
            processManager.execute(command, commandFilter); 
         } else {
            String description = problem.getDescription();
            int line = problem.getLine();
            long time = System.currentTimeMillis();
            
            commandClient.sendSyntaxError(resource, description, time, line);
         }
      } catch(Exception e) {
         processLogger.info("Error executing " + resource, e);
      }
   }
   
   public void onAttach(AttachCommand command) {
      String process = command.getProcess();
      
      try {
         String focus = commandFilter.getFocus();
         
         if(focus == null) { // not focused
            if(command.isFocus()) {
               commandFilter.setFocus(process);
            }
         } else if(process.equals(focus)) { // focused
            if(command.isFocus()) {
               commandFilter.setFocus(process); // accept messages from this process
            } else {
               commandFilter.clearFocus(); // clear the focus
            }
         } else {
            if(command.isFocus()) {
               commandFilter.setFocus(process);
            }
         }
         processManager.attach(command, process);
         processManager.register(forwarder); // make sure we are registered
      } catch(Exception e) {
         processLogger.info("Error attaching to process " + process, e);
      }
   }
   
   public void onStep(StepCommand command) {
      String thread = command.getThread();
      String focus = commandFilter.getFocus();
            
      try {
         if(focus != null) {
            processManager.step(command, focus);
         }
      } catch(Exception e) {
         processLogger.info("Error stepping through " + thread +" in process " + focus, e);
      }
   }
   
   public void onDelete(DeleteCommand command) {
      String resource = command.getResource();
      
      try {
         File file = new File(root, "/" + resource);
         
         if(!file.equals(root)) { // don't delete root
            boolean exists = file.exists();
            
            if(exists) {
               backupManager.backupFile(file, project);
               
               if(file.isDirectory()) {
                  
               }
               file.delete();
               commandClient.sendReloadTree();
            }
         }
      } catch(Exception e) {
         processLogger.info("Error deleting " + resource, e);
      }
   }
   
   public void onBreakpoints(BreakpointsCommand command) {
      String focus = commandFilter.getFocus();
      
      try {
         if(focus != null) {
            processManager.breakpoints(command, focus);
         }
      } catch(Exception e){
         processLogger.info("Error setting breakpoints for process " + focus, e);
      }
   }
   
   public void onBrowse(BrowseCommand command) {
      String focus = commandFilter.getFocus();
      
      try {
         if(focus != null) {
            processManager.browse(command, focus);
         }
      } catch(Exception e) {
         processLogger.info("Error browsing variables for process " + focus, e);
      }
   }
   
   public void onEvaluate(EvaluateCommand command) {
      String focus = commandFilter.getFocus();
      
      try {
         if(focus != null) {
            processManager.evaluate(command, focus);
         }
      } catch(Exception e) {
         processLogger.info("Error browsing variables for process " + focus, e);
      }
   }
   
   public void onFolderExpand(FolderExpandCommand command) {
      String focus = commandFilter.getFocus();
      String folder = command.getFolder();
      String project = command.getProject();
      
      try {
         TreeContext context = treeManager.getContext(root, project, cookie);
         
         if(context != null) {
            processLogger.info("Expand folder: " + folder);
            context.folderExpand(folder);
         }
      } catch(Exception e) {
         processLogger.info("Error stopping process " + focus, e);
      }
   }
   
   public void onFolderCollapse(FolderCollapseCommand command) {
      String focus = commandFilter.getFocus();
      String folder = command.getFolder();
      String project = command.getProject();
      
      try {
         TreeContext context = treeManager.getContext(root, project, cookie);
         
         if(context != null) {
            processLogger.info("Collapse folder: " + folder);
            context.folderCollapse(folder);
         }
      } catch(Exception e) {
         processLogger.info("Error stopping process " + focus, e);
      }
   }
   
   public void onDisplayUpdate(DisplayUpdateCommand command) {
      int fontSize = command.getFontSize();
      String fontName = command.getFontName();
      String themeName = command.getThemeName();
      
      try {
         DisplayDefinition definition = displayPersister.readDefinition();
         
         if(definition != null) {
            if(fontName != null) {
               definition.setFontName(fontName);
            }
            if(fontSize > 0) {
               definition.setFontSize(fontSize);
            }
            if(themeName != null) {
               definition.setThemeName(themeName);
            }
            displayPersister.saveDefinition(definition);
         }
      } catch(Exception e) {
         processLogger.info("Error saving definition", e);
      }
   }
   
   public void onStop(StopCommand command) {
      String focus = commandFilter.getFocus();
      
      try {
         if(focus != null) {
            processManager.stop(focus);
            commandClient.sendProcessTerminate(focus);
            commandFilter.clearFocus();
         }
      } catch(Exception e) {
         processLogger.info("Error stopping process " + focus, e);
      }
   }
   
   public void onPing(PingCommand command) {
      String focus = commandFilter.getFocus();
      
      try {
         if(focus != null) {
            long time = System.currentTimeMillis();
            
            if(!processManager.ping(focus, time)) {
               commandClient.sendProcessTerminate(focus);
               commandFilter.clearFocus();
            }
         }
         processManager.register(forwarder); // make sure we are registered
      } catch(Exception e) {
         processLogger.info("Error pinging process " + focus, e);
      }
   }
   
   public void onPing() {
      String focus = commandFilter.getFocus();
      
      try {
         if(focus != null) {
            long time = System.currentTimeMillis();
            
            if(!processManager.ping(focus, time)) {
               commandClient.sendProcessTerminate(focus);
               commandFilter.clearFocus();
            }
         }
         processManager.register(forwarder); // make sure we are registered
         Set<Problem> problems = problemFinder.compileProject(path);
//         Map<String, TypeNode> nodes = loader.compileProject(path);
//         Set<String> names = nodes.keySet();
//         int index = 0;
//         
//         for(String name : names) {
//            String[] pair = name.split(":");
//            TypeNode node = nodes.get(name);
//            index++;
//            
//            if(node.isModule()) {
//               logger.log(index + " module " + pair[0] + " --> '" + pair[1] + "'");
//            } else {
//               logger.log(index + " class " + pair[0] + " --> '" + pair[1] + "'");
//            }
//         }
         for(Problem problem : problems) {
            String description = problem.getDescription();
            String path = problem.getResource();
            int line = problem.getLine();
            long time = System.currentTimeMillis();
            
            commandClient.sendSyntaxError(path,description,  time, line);
         }
      } catch(Exception e) {
         processLogger.info("Error pinging process " + focus, e);
      }
   }
   
   public void onClose() {
      try {
         //client.sendProcessTerminate();
         processManager.remove(forwarder);
      } catch(Exception e) {
         processLogger.info("Error removing listener", e);
      }
   }
}

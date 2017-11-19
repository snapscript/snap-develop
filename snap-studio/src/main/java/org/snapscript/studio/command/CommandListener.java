package org.snapscript.studio.command;

import java.io.File;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicLong;

import org.simpleframework.common.encode.Base64Encoder;
import org.simpleframework.http.Path;
import org.simpleframework.http.socket.FrameChannel;
import org.slf4j.Logger;
import org.snapscript.common.command.CommandBuilder;
import org.snapscript.common.command.Console;
import org.snapscript.studio.common.Problem;
import org.snapscript.studio.common.ProblemFinder;
import org.snapscript.studio.common.resource.display.DisplayDefinition;
import org.snapscript.studio.common.resource.display.DisplayPersister;
import org.snapscript.studio.configuration.OperatingSystem;
import org.snapscript.studio.configuration.ProjectConfiguration;
import org.snapscript.studio.core.BackupManager;
import org.snapscript.studio.core.ProcessManager;
import org.snapscript.studio.resource.project.Project;
import org.snapscript.studio.resource.project.ProjectProblemFinder;
import org.snapscript.studio.resource.tree.TreeContext;
import org.snapscript.studio.resource.tree.TreeContextManager;

public class CommandListener {
   
   private final DisplayPersister displayPersister;
   private final CommandEventForwarder forwarder;
   private final ProjectProblemFinder problemFinder;
   private final TreeContextManager treeManager;
   private final CommandFilter commandFilter;
   private final CommandClient commandClient;
   private final ProcessManager processManager;
   private final Logger processLogger;
   private final ProblemFinder finder;
   private final BackupManager backupManager;
   private final AtomicLong lastModified;
   private final Project project;
   private final String projectName;
   private final String cookie;
   private final Path path;
   private final File root;
   
   public CommandListener(
         ProcessManager processManager, 
         ProjectProblemFinder problemFinder, 
         DisplayPersister displayPersister,
         FrameChannel frameChannel, 
         Logger processLogger, 
         BackupManager backupManager, 
         TreeContextManager treeManager, 
         Project project,
         Path path, 
         String cookie) 
   {
      this.commandFilter = new CommandFilter();
      this.commandClient = new CommandClient(frameChannel, project);
      this.forwarder = new CommandEventForwarder(commandClient, commandFilter, processLogger, project);
      this.lastModified = new AtomicLong(project.getModificationTime());
      this.finder = new ProblemFinder();
      this.projectName = project.getProjectName();
      this.root = project.getSourcePath();
      this.displayPersister = displayPersister;
      this.treeManager = treeManager;
      this.problemFinder = problemFinder;
      this.backupManager = backupManager;
      this.processLogger = processLogger;
      this.processManager = processManager;
      this.project = project;
      this.cookie = cookie;
      this.path = path;
   }

   public void onExplore(ExploreCommand command) {
      String resource = command.getResource();
      
      try {
         if(resource != null) {
            File file = new File(root, "/" + resource);
            CommandBuilder builder = new CommandBuilder();
            
            if(!file.isDirectory()) {
               file = file.getParentFile();
            }
            String path = file.getCanonicalPath();
            boolean exists = file.exists();
            boolean directory = file.isDirectory();
            OperatingSystem os = OperatingSystem.resolveSystem();
            
            if(exists && directory) {
               if(!command.isTerminal()) {
                  String expression = os.createExploreCommand(path);
                  Callable<Console> task = builder.create(expression);
                  
                  processLogger.info("Executing: " + expression);
                  task.call();
               } else {
                  String expression = os.createTerminalCommand(path);
                  Callable<Console> task = builder.create(expression);
                  
                  processLogger.info("Executing: " + expression);
                  task.call();
               }
            }
         }
      } catch(Exception e) {
         processLogger.info("Error exploring directory " + resource, e);
      }
   }
   
   public void onUpload(UploadCommand command) {
      Boolean dragAndDrop = command.getDragAndDrop();
      String to = command.getTo();
      String name = command.getName();
      String project = command.getProject();
      
      try {
         if(Boolean.TRUE.equals(dragAndDrop)) {
            processLogger.info("Drag and drop file: " + name + " to: " + to);
         }
         File file = new File(root, to);
         boolean exists = file.exists();
         
         if(exists) {
            backupManager.backupFile(file, project);
         }
         String text = command.getData();
         char[] source = text.toCharArray();
         byte[] data = Base64Encoder.decode(source);
               
         backupManager.saveFile(file, data);
            
         if(!exists) {
            onReload();
         }
      } catch(Exception e) {
         processLogger.info("Error saving " + to, e);
      }
   }
   
   public void onSave(SaveCommand command) {
      String resource = command.getResource();
      String source = command.getSource();
      
      try {
         if(!command.isDirectory()) {
            Problem problem = finder.parse(projectName, resource, source);
            File file = new File(root, "/" + resource);
            boolean exists = file.exists();
            
            if(exists) {
               backupManager.backupFile(file, projectName);
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
                  onReload();
               }
            } 
         } else {
            File file = new File(root, "/"+resource);
            
            if(!file.exists()) {
               file.mkdirs();
               onReload();
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
         
         if(!fromFile.equals(root) && toFile.getParentFile().isDirectory()) { // don't rename root
            boolean fromExists = fromFile.exists();
            boolean toExists = toFile.exists();
            
            if(!fromExists) {
               commandClient.sendAlert(from, "Resource " + from + " does not exist");
            } else {
               if(toExists) {
                  commandClient.sendAlert(to, "Resource " + to + " already exists");
               } else {
                  if(fromFile.renameTo(toFile)){
                     onReload();
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
         Problem problem = finder.parse(projectName, resource, source);
         
         if(problem == null) {
            File file = new File(root, "/" + resource);
            boolean exists = file.exists();
            
            if(exists) {
               backupManager.backupFile(file, projectName);
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
               backupManager.backupFile(file, projectName);
               
               if(file.isDirectory()) {
                  
               }
               file.delete();
               onReload();
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
         TreeContext context = treeManager.getContext(root, project, cookie, true);
         
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
         TreeContext context = treeManager.getContext(root, project, cookie, true);
         
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
         long projectModification = project.getModificationTime();
         long previousModification = lastModified.get();
         
         if(previousModification < projectModification) {
            onReload();
         }
         try {
            project.getDependencies();
         } catch(Exception e) {
            String message = e.getMessage();
            long time = System.currentTimeMillis();
            
            commandClient.sendDependencyError(ProjectConfiguration.PROJECT_FILE, message, time, 1);
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
   
   public void onReload() {
      try {
         lastModified.set(project.getModificationTime());
         commandClient.sendReloadTree();
      } catch(Exception e) {
         processLogger.info("Error reloading tree", e);
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
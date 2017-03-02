/*
 * BackupManager.java December 2016
 *
 * Copyright (C) 2016, Niall Gallagher <niallg@users.sf.net>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied. See the License for the specific language governing 
 * permissions and limitations under the License.
 */

package org.snapscript.develop;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.snapscript.agent.log.ProcessLogger;
import org.snapscript.develop.http.project.Project;
import org.snapscript.develop.http.project.ProjectBuilder;

public class BackupManager {
   
   private static final String BACKUP_FOLDER = ".backup";
   private static final String DATE_FORMAT = "yyyy_MM_dd_HH_mm_ss_SSS";
   private static final String DATE_PATTERN = "^.*\\.\\d\\d\\d\\d_\\d\\d_\\d\\d_\\d\\d_\\d\\d_\\d\\d_\\d\\d\\d$";
   private static final long BACKUP_EXPIRY = 5 * 24 * 60 * 60 * 1000;
   
   private final ProjectBuilder builder;
   private final ProcessLogger logger;
   private final Workspace workspace;
   private final DateFormat format;
   
   public BackupManager(ProjectBuilder builder, ProcessLogger logger, Workspace workspace) {
      this.format = new SimpleDateFormat(DATE_FORMAT);
      this.builder = builder;
      this.workspace = workspace;
      this.logger = logger;
   }
   
   public synchronized void backupFile(File file, String project) {
      if(file.exists()) {
         if(file.isFile()) {
            if(acceptFile(file, project)) {
               File backupFile = createBackupFile(file, project);
               File backupDirectory = backupFile.getParentFile();
               
               if(!backupDirectory.exists()) {
                  backupDirectory.mkdirs();
               }
               cleanBackups(file, project);
               copyFile(file, backupFile);
            }
         } else {
            File[] files = file.listFiles();
            
            for(File entry : files) {
               backupFile(entry, project);
            }
         }
      }
   }
   
   private synchronized File createBackupFile(File file, String project) {
      long time = System.currentTimeMillis();
      File backupRoot = workspace.create(BACKUP_FOLDER);
      Project proj = builder.getProject(project);
      
      if(proj == null) {
         throw new IllegalArgumentException("Project " + project + " does not exist");
      }
      File root = proj.getProjectPath();
      String extension = format.format(time);
      String relative = relative(root, file);
      String timestampFile = String.format("%s/%s.%s", project, relative, extension);

      return new File(backupRoot, timestampFile);
   }
   
   private synchronized boolean acceptFile(File file, String project) {
      if(file.isFile() && file.exists()) {
         File latestBackup = findLatestBackup(file, project);
         
         if(latestBackup != null) {
            byte[] backupDigest = digestFile(latestBackup);
            byte[] fileDigest = digestFile(file);
            
            return !MessageDigest.isEqual(backupDigest, fileDigest);
         }
         return true;
      }
      return false;
   }
   
   private synchronized void cleanBackups(File file, String project) {
      List<BackupFile> backupFiles = findAllBackups(file, project);
      int backupCount = backupFiles.size();
      
      if(backupCount > 1) {
         for(BackupFile backupFile : backupFiles) {
            if(backupFile.getFile().exists()) {
               long lastModified = backupFile.getFile().lastModified();
               long time = System.currentTimeMillis();
               
               if(lastModified + BACKUP_EXPIRY < time) {
                  deleteFile(backupFile.getFile());
               }
            }
         }
      }
   }
   
   private synchronized File findLatestBackup(File file, String project) {
      try {
         List<BackupFile> backupFiles = findAllBackups(file, project);
         Iterator<BackupFile> backupIterator = backupFiles.iterator();
         
         if(backupIterator.hasNext()) {
            return backupIterator.next().getFile();
         }
      } catch(Exception e) {
         logger.info("Could not find backup from " + file, e);
      }
      return null;
   }
   
   public synchronized List<BackupFile> findAllBackups(File file, String project) {
      try {
         List<BackupFile> backupHistory = new ArrayList<BackupFile>();
         Map<Long, BackupFile> timeStampFiles = new TreeMap<Long, BackupFile>();
         File backupFile = createBackupFile(file, project);
         File backupDirectory = backupFile.getParentFile();
         File[] list = backupDirectory.listFiles();
         Project proj = builder.getProject(project);
         
         if(proj == null) {
            throw new IllegalArgumentException("Project " + project + " does not exist");
         }
         File root = proj.getProjectPath();
         String rootPath = root.getCanonicalPath();
         String matchName = file.getName();
         
         for(File entry : list) {
            String name = entry.getName();
            
            if(name.matches(DATE_PATTERN) && name.startsWith(matchName) && entry.exists()) {
               int index = name.lastIndexOf(".");
               int length = name.length();
               String timeStamp = name.substring(index + 1, length);
               Date date = format.parse(timeStamp);
               long time = date.getTime();
               String fullFile = file.getCanonicalPath();
               String relativeFile = fullFile.replace(rootPath, "").replace(File.separatorChar, '/');
               String projectPath = relativeFile.startsWith("/") ? relativeFile : ("/" + relativeFile);
               BackupFile backupData = new BackupFile(entry, projectPath, date, timeStamp, project);
               
               timeStampFiles.put(time, backupData);
            }
         }
         Set<Long> timeStamps = timeStampFiles.keySet();
         
         for(Long timeStamp : timeStamps) {
            BackupFile timeStampFile = timeStampFiles.get(timeStamp);
            backupHistory.add(timeStampFile);
         }
         Collections.reverse(backupHistory);
         return backupHistory;
      } catch(Exception e) {
         logger.info("Could not find backup from " + file, e);
      }
      return Collections.emptyList();
   }
   
   public synchronized void copyFile(File from, File to) {
      try {
         FileInputStream input = new FileInputStream(from);
         FileOutputStream output = new FileOutputStream(to);
         byte[] buffer = new byte[1024];
         int count = 0;
         
         while((count = input.read(buffer))!=-1){
            output.write(buffer, 0, count);
         }
         input.close();
         output.close();
      } catch(Exception e) {
         logger.info("Could not backup " + from + " to " + to);
      }
   }
   
   public synchronized void deleteFile(File file) {
      try {
         if(file.exists()) {
            if(file.isDirectory()) {
               File[] files = file.listFiles();
               
               for(File entry : files) {
                  if(entry.isDirectory()) {
                     deleteFile(entry);
                  } else {
                     if(entry.exists()) {
                        entry.delete();
                     }
                  }
               }
            } else {
               file.delete();
            }
         }
      } catch(Exception e) {
         logger.info("Could not delete " + file);
      }
   }
   
   public synchronized byte[] digestFile(File file) {
      try {
         MessageDigest digest = MessageDigest.getInstance("MD5");
         FileInputStream input = new FileInputStream(file);
         byte[] buffer = new byte[1024];
         int count = 0;
         
         while((count = input.read(buffer))!=-1){
            digest.update(buffer, 0, count);
         }
         input.close();
         return digest.digest();
      } catch(Exception e) {
         logger.info("Could not get MD5 digest of " + file);
      }
      return new byte[]{};
   }
   
   public synchronized void saveFile(File file, String content) {
      try {
         FileOutputStream out = new FileOutputStream(file);
         OutputStreamWriter encoder = new OutputStreamWriter(out, "UTF-8");
         
         encoder.write(content);
         encoder.close();
      } catch(Exception e) {
         logger.info("Could not save " + file);
      }
   }
   
   private synchronized String relative(File root, File file) {
      return root.toURI().relativize(file.toURI()).getPath();
   }
}

package org.snapscript.studio.project.maven;

import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.sonatype.aether.RepositoryEvent;
import org.sonatype.aether.RepositoryListener;

@Slf4j
public class ConsoleRepositoryListener implements RepositoryListener {

   public ConsoleRepositoryListener() {
      super();
   }

   @Override
   public void artifactDeployed(RepositoryEvent event) {
      log.debug("Deployed " + event.getArtifact() + " to " + event.getRepository());
   }

   @Override
   public void artifactDeploying(RepositoryEvent event) {
      log.debug("Deploying " + event.getArtifact() + " to " + event.getRepository());
   }

   @Override
   public void artifactDescriptorInvalid(RepositoryEvent event) {
      log.debug("Invalid artifact descriptor for " + event.getArtifact() + ": " + event.getException().getMessage());
   }

   @Override
   public void artifactDescriptorMissing(RepositoryEvent event) {
      log.debug("Missing artifact descriptor for " + event.getArtifact());
   }

   @Override
   public void artifactInstalled(RepositoryEvent event) {
      log.debug("Installed " + event.getArtifact() + " to " + event.getFile());
   }

   @Override
   public void artifactInstalling(RepositoryEvent event) {
      log.debug("Installing " + event.getArtifact() + " to " + event.getFile());
   }

   @Override
   public void artifactResolved(RepositoryEvent event) {
      log.debug("Resolved artifact " + event.getArtifact() + " from " + event.getRepository());
   }

   @Override
   public void artifactDownloading(RepositoryEvent event) {
      log.debug("Downloading artifact " + event.getArtifact() + " from " + event.getRepository());
   }

   @Override
   public void artifactDownloaded(RepositoryEvent event) {
      log.debug("Downloaded artifact " + event.getArtifact() + " from " + event.getRepository());
   }

   @Override
   public void artifactResolving(RepositoryEvent event) {
      log.debug("Resolving artifact " + event.getArtifact());
   }

   @Override
   public void metadataDeployed(RepositoryEvent event) {
      log.debug("Deployed " + event.getMetadata() + " to " + event.getRepository());
   }

   @Override
   public void metadataDeploying(RepositoryEvent event) {
      log.debug("Deploying " + event.getMetadata() + " to " + event.getRepository());
   }

   @Override
   public void metadataInstalled(RepositoryEvent event) {
      log.debug("Installed " + event.getMetadata() + " to " + event.getFile());
   }

   @Override
   public void metadataInstalling(RepositoryEvent event) {
      log.debug("Installing " + event.getMetadata() + " to " + event.getFile());
   }

   @Override
   public void metadataInvalid(RepositoryEvent event) {
      log.debug("Invalid metadata " + event.getMetadata());
   }

   @Override
   public void metadataResolved(RepositoryEvent event) {
      log.debug("Resolved metadata " + event.getMetadata() + " from " + event.getRepository());
   }

   @Override
   public void metadataResolving(RepositoryEvent event) {
      log.debug("Resolving metadata " + event.getMetadata() + " from " + event.getRepository());
   }

   @Override
   public void metadataDownloading(RepositoryEvent event) {
      log.debug("Metadata downloading " + event.getMetadata() + " from " + event.getRepository());
   }

   @Override
   public void metadataDownloaded(RepositoryEvent event) {
      log.debug("Metadata downloaded " + event.getMetadata() + " from " + event.getRepository());
   }

}
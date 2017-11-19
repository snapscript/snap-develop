package org.snapscript.studio.maven;

import org.slf4j.Logger;
import org.sonatype.aether.RepositoryEvent;
import org.sonatype.aether.RepositoryListener;

public class ConsoleRepositoryListener implements RepositoryListener {

   private Logger logger;

   public ConsoleRepositoryListener(Logger logger) {
      this.logger = logger;
   }

   @Override
   public void artifactDeployed(RepositoryEvent event) {
      logger.debug("Deployed " + event.getArtifact() + " to " + event.getRepository());
   }

   @Override
   public void artifactDeploying(RepositoryEvent event) {
      logger.debug("Deploying " + event.getArtifact() + " to " + event.getRepository());
   }

   @Override
   public void artifactDescriptorInvalid(RepositoryEvent event) {
      logger.debug("Invalid artifact descriptor for " + event.getArtifact() + ": " + event.getException().getMessage());
   }

   @Override
   public void artifactDescriptorMissing(RepositoryEvent event) {
      logger.debug("Missing artifact descriptor for " + event.getArtifact());
   }

   @Override
   public void artifactInstalled(RepositoryEvent event) {
      logger.debug("Installed " + event.getArtifact() + " to " + event.getFile());
   }

   @Override
   public void artifactInstalling(RepositoryEvent event) {
      logger.debug("Installing " + event.getArtifact() + " to " + event.getFile());
   }

   @Override
   public void artifactResolved(RepositoryEvent event) {
      logger.debug("Resolved artifact " + event.getArtifact() + " from " + event.getRepository());
   }

   @Override
   public void artifactDownloading(RepositoryEvent event) {
      logger.debug("Downloading artifact " + event.getArtifact() + " from " + event.getRepository());
   }

   @Override
   public void artifactDownloaded(RepositoryEvent event) {
      logger.debug("Downloaded artifact " + event.getArtifact() + " from " + event.getRepository());
   }

   @Override
   public void artifactResolving(RepositoryEvent event) {
      logger.debug("Resolving artifact " + event.getArtifact());
   }

   @Override
   public void metadataDeployed(RepositoryEvent event) {
      logger.debug("Deployed " + event.getMetadata() + " to " + event.getRepository());
   }

   @Override
   public void metadataDeploying(RepositoryEvent event) {
      logger.debug("Deploying " + event.getMetadata() + " to " + event.getRepository());
   }

   @Override
   public void metadataInstalled(RepositoryEvent event) {
      logger.debug("Installed " + event.getMetadata() + " to " + event.getFile());
   }

   @Override
   public void metadataInstalling(RepositoryEvent event) {
      logger.debug("Installing " + event.getMetadata() + " to " + event.getFile());
   }

   @Override
   public void metadataInvalid(RepositoryEvent event) {
      logger.debug("Invalid metadata " + event.getMetadata());
   }

   @Override
   public void metadataResolved(RepositoryEvent event) {
      logger.debug("Resolved metadata " + event.getMetadata() + " from " + event.getRepository());
   }

   @Override
   public void metadataResolving(RepositoryEvent event) {
      logger.debug("Resolving metadata " + event.getMetadata() + " from " + event.getRepository());
   }

   @Override
   public void metadataDownloading(RepositoryEvent event) {
      logger.debug("Metadata downloading " + event.getMetadata() + " from " + event.getRepository());
   }

   @Override
   public void metadataDownloaded(RepositoryEvent event) {
      logger.debug("Metadata downloaded " + event.getMetadata() + " from " + event.getRepository());
   }

}
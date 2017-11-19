package org.snapscript.studio.image;

import static org.simpleframework.http.Protocol.CONTENT_TYPE;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageInputStream;

import org.simpleframework.http.Path;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.Status;
import org.snapscript.common.Cache;
import org.snapscript.common.LeastRecentlyUsedCache;
import org.snapscript.core.Bug;
import org.snapscript.studio.common.resource.Content;
import org.snapscript.studio.common.resource.ContentTypeResolver;
import org.snapscript.studio.common.resource.FileResolver;
import org.snapscript.studio.common.resource.Resource;
import org.snapscript.studio.common.resource.ResourcePath;
import org.snapscript.studio.core.Workspace;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@ResourcePath("/img/theme/.*.png")
public class ImageScaleResource implements Resource {

   private final Cache<String, ScaledImage> scaledImages;
   private final ContentTypeResolver typeResolver;
   private final FileResolver fileResolver;
   private final Workspace workspace;
   private final int height;

   @Bug("crapola!")
   public ImageScaleResource(ContentTypeResolver typeResolver, FileResolver fileResolver, Workspace workspace, @Value("${image.scale:40}") int height) {
      this.scaledImages = new LeastRecentlyUsedCache<String, ScaledImage>(200);
      this.typeResolver = typeResolver;
      this.fileResolver = fileResolver;
      this.workspace = workspace;
      this.height = height;
   }
   
   @Override
   public void handle(Request request, Response response) throws Exception {
      Path path = request.getPath();
      String target = path.getPath();
      ScaledImage image = getImage(target);
      OutputStream output = response.getOutputStream();
      String type = image.getType();
      byte[] data = image.getData();
      int height = image.getHeight();
      int width = image.getWidth();
      
      if(workspace.getLogger().isTraceEnabled()) {
         workspace.getLogger().trace(path + " scaled=" + width + "x" + height);
      }
      response.setStatus(Status.OK);
      response.setValue(CONTENT_TYPE, type);
      output.write(data);
      output.close();
   }
   
   
   private ScaledImage getImage(String path) throws Exception {
      ScaledImage image = scaledImages.fetch(path);
      
      if(image == null) {
         image = getScaledImage(path);
         scaledImages.cache(path, image);
      }
      return image;
   }
   
   private ScaledImage getScaledImage(String path) throws Exception {
      String contentType = typeResolver.resolveType(path);
      ImageType imageType = ImageType.resolveByType(contentType);
      ImageWriter imageWriter = imageType.getImageWriter();
      ByteArrayOutputStream output = new ByteArrayOutputStream();

      imageWriter.setOutput(ImageIO.createImageOutputStream(output));

      BufferedImage originalImage = getOriginalImage(path);
      BufferedImage scaledImage = ImageScaler.scaleHeight(originalImage, height);
      
      imageWriter.write(scaledImage);
      output.close();
      
      byte[] data = output.toByteArray();
      int height = scaledImage.getHeight();
      int width = scaledImage.getWidth();
      
      return new ScaledImage(contentType, data, width, height);
   }
   
   private BufferedImage getOriginalImage(String path) throws Exception {
      Content content = fileResolver.resolveContent(path);
      InputStream contentStream = content.getInputStream();
      
      try {
         String contentType = typeResolver.resolveType(path);
         ImageType imageType = ImageType.resolveByType(contentType);
         ImageReader imageReader = imageType.getImageReader();
         ImageInputStream imageStream = ImageIO.createImageInputStream(contentStream);
         
         imageReader.setInput(imageStream);
   
         return imageReader.read(0);
      }finally {
         contentStream.close();
      }
   }
   
   private static class ScaledImage {
      
      private final int height;
      private final int width;
      private final String type;
      private final byte[] data;
      
      public ScaledImage(String type, byte[] data, int width, int height){
         this.width = width;
         this.height = height;
         this.type = type;
         this.data = data;
      }
      
      public int getWidth(){
         return width;
      }
      
      public int getHeight(){
         return height;
      }
      
      public byte[] getData(){
         return data;
      }
      
      public String getType(){
         return type;
      }
   }
}
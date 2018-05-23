package com.sun.xml.internal.messaging.saaj.soap;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.datatransfer.DataFlavor;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.ActivationDataFlavor;
import javax.activation.DataContentHandler;
import javax.activation.DataSource;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

public class ImageDataContentHandler
  extends Component
  implements DataContentHandler
{
  protected static final Logger log = Logger.getLogger("com.sun.xml.internal.messaging.saaj.soap", "com.sun.xml.internal.messaging.saaj.soap.LocalStrings");
  private DataFlavor[] flavor;
  
  public ImageDataContentHandler()
  {
    String[] arrayOfString = ImageIO.getReaderMIMETypes();
    flavor = new DataFlavor[arrayOfString.length];
    for (int i = 0; i < arrayOfString.length; i++) {
      flavor[i] = new ActivationDataFlavor(Image.class, arrayOfString[i], "Image");
    }
  }
  
  public DataFlavor[] getTransferDataFlavors()
  {
    return (DataFlavor[])Arrays.copyOf(flavor, flavor.length);
  }
  
  public Object getTransferData(DataFlavor paramDataFlavor, DataSource paramDataSource)
    throws IOException
  {
    for (int i = 0; i < flavor.length; i++) {
      if (flavor[i].equals(paramDataFlavor)) {
        return getContent(paramDataSource);
      }
    }
    return null;
  }
  
  public Object getContent(DataSource paramDataSource)
    throws IOException
  {
    return ImageIO.read(new BufferedInputStream(paramDataSource.getInputStream()));
  }
  
  public void writeTo(Object paramObject, String paramString, OutputStream paramOutputStream)
    throws IOException
  {
    try
    {
      BufferedImage localBufferedImage = null;
      if ((paramObject instanceof BufferedImage))
      {
        localBufferedImage = (BufferedImage)paramObject;
      }
      else if ((paramObject instanceof Image))
      {
        localBufferedImage = render((Image)paramObject);
      }
      else
      {
        log.log(Level.SEVERE, "SAAJ0520.soap.invalid.obj.type", new String[] { paramObject.getClass().toString() });
        throw new IOException("ImageDataContentHandler requires Image object, was given object of type " + paramObject.getClass().toString());
      }
      ImageWriter localImageWriter = null;
      Iterator localIterator = ImageIO.getImageWritersByMIMEType(paramString);
      if (localIterator.hasNext()) {
        localImageWriter = (ImageWriter)localIterator.next();
      }
      if (localImageWriter != null)
      {
        ImageOutputStream localImageOutputStream = null;
        localImageOutputStream = ImageIO.createImageOutputStream(paramOutputStream);
        localImageWriter.setOutput(localImageOutputStream);
        localImageWriter.write(localBufferedImage);
        localImageWriter.dispose();
        localImageOutputStream.close();
      }
      else
      {
        log.log(Level.SEVERE, "SAAJ0526.soap.unsupported.mime.type", new String[] { paramString });
        throw new IOException("Unsupported mime type:" + paramString);
      }
    }
    catch (Exception localException)
    {
      log.severe("SAAJ0525.soap.cannot.encode.img");
      throw new IOException("Unable to encode the image to a stream " + localException.getMessage());
    }
  }
  
  private BufferedImage render(Image paramImage)
    throws InterruptedException
  {
    MediaTracker localMediaTracker = new MediaTracker(this);
    localMediaTracker.addImage(paramImage, 0);
    localMediaTracker.waitForAll();
    BufferedImage localBufferedImage = new BufferedImage(paramImage.getWidth(null), paramImage.getHeight(null), 1);
    Graphics2D localGraphics2D = localBufferedImage.createGraphics();
    localGraphics2D.drawImage(paramImage, 0, 0, null);
    localGraphics2D.dispose();
    return localBufferedImage;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\messaging\saaj\soap\ImageDataContentHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
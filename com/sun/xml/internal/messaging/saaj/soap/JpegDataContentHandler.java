package com.sun.xml.internal.messaging.saaj.soap;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.datatransfer.DataFlavor;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import javax.activation.ActivationDataFlavor;
import javax.activation.DataContentHandler;
import javax.activation.DataSource;
import javax.imageio.ImageIO;

public class JpegDataContentHandler
  extends Component
  implements DataContentHandler
{
  public static final String STR_SRC = "java.awt.Image";
  
  public JpegDataContentHandler() {}
  
  public DataFlavor[] getTransferDataFlavors()
  {
    DataFlavor[] arrayOfDataFlavor = new DataFlavor[1];
    try
    {
      arrayOfDataFlavor[0] = new ActivationDataFlavor(Class.forName("java.awt.Image"), "image/jpeg", "JPEG");
    }
    catch (Exception localException)
    {
      System.out.println(localException);
    }
    return arrayOfDataFlavor;
  }
  
  public Object getTransferData(DataFlavor paramDataFlavor, DataSource paramDataSource)
  {
    if ((paramDataFlavor.getMimeType().startsWith("image/jpeg")) && (paramDataFlavor.getRepresentationClass().getName().equals("java.awt.Image")))
    {
      InputStream localInputStream = null;
      BufferedImage localBufferedImage = null;
      try
      {
        localInputStream = paramDataSource.getInputStream();
        localBufferedImage = ImageIO.read(localInputStream);
      }
      catch (Exception localException)
      {
        System.out.println(localException);
      }
      return localBufferedImage;
    }
    return null;
  }
  
  public Object getContent(DataSource paramDataSource)
  {
    InputStream localInputStream = null;
    BufferedImage localBufferedImage = null;
    try
    {
      localInputStream = paramDataSource.getInputStream();
      localBufferedImage = ImageIO.read(localInputStream);
    }
    catch (Exception localException) {}
    return localBufferedImage;
  }
  
  public void writeTo(Object paramObject, String paramString, OutputStream paramOutputStream)
    throws IOException
  {
    if (!paramString.equals("image/jpeg")) {
      throw new IOException("Invalid content type \"" + paramString + "\" for ImageContentHandler");
    }
    if (paramObject == null) {
      throw new IOException("Null object for ImageContentHandler");
    }
    try
    {
      BufferedImage localBufferedImage = null;
      if ((paramObject instanceof BufferedImage))
      {
        localBufferedImage = (BufferedImage)paramObject;
      }
      else
      {
        Image localImage = (Image)paramObject;
        MediaTracker localMediaTracker = new MediaTracker(this);
        localMediaTracker.addImage(localImage, 0);
        localMediaTracker.waitForAll();
        if (localMediaTracker.isErrorAny()) {
          throw new IOException("Error while loading image");
        }
        localBufferedImage = new BufferedImage(localImage.getWidth(null), localImage.getHeight(null), 1);
        Graphics2D localGraphics2D = localBufferedImage.createGraphics();
        localGraphics2D.drawImage(localImage, 0, 0, null);
      }
      ImageIO.write(localBufferedImage, "jpeg", paramOutputStream);
    }
    catch (Exception localException)
    {
      throw new IOException("Unable to run the JPEG Encoder on a stream " + localException.getMessage());
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\messaging\saaj\soap\JpegDataContentHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
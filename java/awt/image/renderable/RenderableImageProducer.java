package java.awt.image.renderable;

import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.ImageConsumer;
import java.awt.image.ImageProducer;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.util.Enumeration;
import java.util.Vector;

public class RenderableImageProducer
  implements ImageProducer, Runnable
{
  RenderableImage rdblImage;
  RenderContext rc;
  Vector ics = new Vector();
  
  public RenderableImageProducer(RenderableImage paramRenderableImage, RenderContext paramRenderContext)
  {
    rdblImage = paramRenderableImage;
    rc = paramRenderContext;
  }
  
  public synchronized void setRenderContext(RenderContext paramRenderContext)
  {
    rc = paramRenderContext;
  }
  
  public synchronized void addConsumer(ImageConsumer paramImageConsumer)
  {
    if (!ics.contains(paramImageConsumer)) {
      ics.addElement(paramImageConsumer);
    }
  }
  
  public synchronized boolean isConsumer(ImageConsumer paramImageConsumer)
  {
    return ics.contains(paramImageConsumer);
  }
  
  public synchronized void removeConsumer(ImageConsumer paramImageConsumer)
  {
    ics.removeElement(paramImageConsumer);
  }
  
  public synchronized void startProduction(ImageConsumer paramImageConsumer)
  {
    addConsumer(paramImageConsumer);
    Thread localThread = new Thread(this, "RenderableImageProducer Thread");
    localThread.start();
  }
  
  public void requestTopDownLeftRightResend(ImageConsumer paramImageConsumer) {}
  
  public void run()
  {
    RenderedImage localRenderedImage;
    if (rc != null) {
      localRenderedImage = rdblImage.createRendering(rc);
    } else {
      localRenderedImage = rdblImage.createDefaultRendering();
    }
    ColorModel localColorModel = localRenderedImage.getColorModel();
    Raster localRaster = localRenderedImage.getData();
    SampleModel localSampleModel = localRaster.getSampleModel();
    DataBuffer localDataBuffer = localRaster.getDataBuffer();
    if (localColorModel == null) {
      localColorModel = ColorModel.getRGBdefault();
    }
    int i = localRaster.getMinX();
    int j = localRaster.getMinY();
    int k = localRaster.getWidth();
    int m = localRaster.getHeight();
    Enumeration localEnumeration = ics.elements();
    ImageConsumer localImageConsumer;
    while (localEnumeration.hasMoreElements())
    {
      localImageConsumer = (ImageConsumer)localEnumeration.nextElement();
      localImageConsumer.setDimensions(k, m);
      localImageConsumer.setHints(30);
    }
    int[] arrayOfInt1 = new int[k];
    int i2 = localSampleModel.getNumBands();
    int[] arrayOfInt2 = new int[i2];
    for (int i1 = 0; i1 < m; i1++)
    {
      for (int n = 0; n < k; n++)
      {
        localSampleModel.getPixel(n, i1, arrayOfInt2, localDataBuffer);
        arrayOfInt1[n] = localColorModel.getDataElement(arrayOfInt2, 0);
      }
      localEnumeration = ics.elements();
      while (localEnumeration.hasMoreElements())
      {
        localImageConsumer = (ImageConsumer)localEnumeration.nextElement();
        localImageConsumer.setPixels(0, i1, k, 1, localColorModel, arrayOfInt1, 0, k);
      }
    }
    localEnumeration = ics.elements();
    while (localEnumeration.hasMoreElements())
    {
      localImageConsumer = (ImageConsumer)localEnumeration.nextElement();
      localImageConsumer.imageComplete(3);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\image\renderable\RenderableImageProducer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
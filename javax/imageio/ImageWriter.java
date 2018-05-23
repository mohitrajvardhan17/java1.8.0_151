package javax.imageio;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.imageio.event.IIOWriteProgressListener;
import javax.imageio.event.IIOWriteWarningListener;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.ImageWriterSpi;

public abstract class ImageWriter
  implements ImageTranscoder
{
  protected ImageWriterSpi originatingProvider = null;
  protected Object output = null;
  protected Locale[] availableLocales = null;
  protected Locale locale = null;
  protected List<IIOWriteWarningListener> warningListeners = null;
  protected List<Locale> warningLocales = null;
  protected List<IIOWriteProgressListener> progressListeners = null;
  private boolean abortFlag = false;
  
  protected ImageWriter(ImageWriterSpi paramImageWriterSpi)
  {
    originatingProvider = paramImageWriterSpi;
  }
  
  public ImageWriterSpi getOriginatingProvider()
  {
    return originatingProvider;
  }
  
  public void setOutput(Object paramObject)
  {
    if (paramObject != null)
    {
      ImageWriterSpi localImageWriterSpi = getOriginatingProvider();
      if (localImageWriterSpi != null)
      {
        Class[] arrayOfClass = localImageWriterSpi.getOutputTypes();
        int i = 0;
        for (int j = 0; j < arrayOfClass.length; j++) {
          if (arrayOfClass[j].isInstance(paramObject))
          {
            i = 1;
            break;
          }
        }
        if (i == 0) {
          throw new IllegalArgumentException("Illegal output type!");
        }
      }
    }
    output = paramObject;
  }
  
  public Object getOutput()
  {
    return output;
  }
  
  public Locale[] getAvailableLocales()
  {
    return availableLocales == null ? null : (Locale[])availableLocales.clone();
  }
  
  public void setLocale(Locale paramLocale)
  {
    if (paramLocale != null)
    {
      Locale[] arrayOfLocale = getAvailableLocales();
      int i = 0;
      if (arrayOfLocale != null) {
        for (int j = 0; j < arrayOfLocale.length; j++) {
          if (paramLocale.equals(arrayOfLocale[j]))
          {
            i = 1;
            break;
          }
        }
      }
      if (i == 0) {
        throw new IllegalArgumentException("Invalid locale!");
      }
    }
    locale = paramLocale;
  }
  
  public Locale getLocale()
  {
    return locale;
  }
  
  public ImageWriteParam getDefaultWriteParam()
  {
    return new ImageWriteParam(getLocale());
  }
  
  public abstract IIOMetadata getDefaultStreamMetadata(ImageWriteParam paramImageWriteParam);
  
  public abstract IIOMetadata getDefaultImageMetadata(ImageTypeSpecifier paramImageTypeSpecifier, ImageWriteParam paramImageWriteParam);
  
  public abstract IIOMetadata convertStreamMetadata(IIOMetadata paramIIOMetadata, ImageWriteParam paramImageWriteParam);
  
  public abstract IIOMetadata convertImageMetadata(IIOMetadata paramIIOMetadata, ImageTypeSpecifier paramImageTypeSpecifier, ImageWriteParam paramImageWriteParam);
  
  public int getNumThumbnailsSupported(ImageTypeSpecifier paramImageTypeSpecifier, ImageWriteParam paramImageWriteParam, IIOMetadata paramIIOMetadata1, IIOMetadata paramIIOMetadata2)
  {
    return 0;
  }
  
  public Dimension[] getPreferredThumbnailSizes(ImageTypeSpecifier paramImageTypeSpecifier, ImageWriteParam paramImageWriteParam, IIOMetadata paramIIOMetadata1, IIOMetadata paramIIOMetadata2)
  {
    return null;
  }
  
  public boolean canWriteRasters()
  {
    return false;
  }
  
  public abstract void write(IIOMetadata paramIIOMetadata, IIOImage paramIIOImage, ImageWriteParam paramImageWriteParam)
    throws IOException;
  
  public void write(IIOImage paramIIOImage)
    throws IOException
  {
    write(null, paramIIOImage, null);
  }
  
  public void write(RenderedImage paramRenderedImage)
    throws IOException
  {
    write(null, new IIOImage(paramRenderedImage, null, null), null);
  }
  
  private void unsupported()
  {
    if (getOutput() == null) {
      throw new IllegalStateException("getOutput() == null!");
    }
    throw new UnsupportedOperationException("Unsupported write variant!");
  }
  
  public boolean canWriteSequence()
  {
    return false;
  }
  
  public void prepareWriteSequence(IIOMetadata paramIIOMetadata)
    throws IOException
  {
    unsupported();
  }
  
  public void writeToSequence(IIOImage paramIIOImage, ImageWriteParam paramImageWriteParam)
    throws IOException
  {
    unsupported();
  }
  
  public void endWriteSequence()
    throws IOException
  {
    unsupported();
  }
  
  public boolean canReplaceStreamMetadata()
    throws IOException
  {
    if (getOutput() == null) {
      throw new IllegalStateException("getOutput() == null!");
    }
    return false;
  }
  
  public void replaceStreamMetadata(IIOMetadata paramIIOMetadata)
    throws IOException
  {
    unsupported();
  }
  
  public boolean canReplaceImageMetadata(int paramInt)
    throws IOException
  {
    if (getOutput() == null) {
      throw new IllegalStateException("getOutput() == null!");
    }
    return false;
  }
  
  public void replaceImageMetadata(int paramInt, IIOMetadata paramIIOMetadata)
    throws IOException
  {
    unsupported();
  }
  
  public boolean canInsertImage(int paramInt)
    throws IOException
  {
    if (getOutput() == null) {
      throw new IllegalStateException("getOutput() == null!");
    }
    return false;
  }
  
  public void writeInsert(int paramInt, IIOImage paramIIOImage, ImageWriteParam paramImageWriteParam)
    throws IOException
  {
    unsupported();
  }
  
  public boolean canRemoveImage(int paramInt)
    throws IOException
  {
    if (getOutput() == null) {
      throw new IllegalStateException("getOutput() == null!");
    }
    return false;
  }
  
  public void removeImage(int paramInt)
    throws IOException
  {
    unsupported();
  }
  
  public boolean canWriteEmpty()
    throws IOException
  {
    if (getOutput() == null) {
      throw new IllegalStateException("getOutput() == null!");
    }
    return false;
  }
  
  public void prepareWriteEmpty(IIOMetadata paramIIOMetadata1, ImageTypeSpecifier paramImageTypeSpecifier, int paramInt1, int paramInt2, IIOMetadata paramIIOMetadata2, List<? extends BufferedImage> paramList, ImageWriteParam paramImageWriteParam)
    throws IOException
  {
    unsupported();
  }
  
  public void endWriteEmpty()
    throws IOException
  {
    if (getOutput() == null) {
      throw new IllegalStateException("getOutput() == null!");
    }
    throw new IllegalStateException("No call to prepareWriteEmpty!");
  }
  
  public boolean canInsertEmpty(int paramInt)
    throws IOException
  {
    if (getOutput() == null) {
      throw new IllegalStateException("getOutput() == null!");
    }
    return false;
  }
  
  public void prepareInsertEmpty(int paramInt1, ImageTypeSpecifier paramImageTypeSpecifier, int paramInt2, int paramInt3, IIOMetadata paramIIOMetadata, List<? extends BufferedImage> paramList, ImageWriteParam paramImageWriteParam)
    throws IOException
  {
    unsupported();
  }
  
  public void endInsertEmpty()
    throws IOException
  {
    unsupported();
  }
  
  public boolean canReplacePixels(int paramInt)
    throws IOException
  {
    if (getOutput() == null) {
      throw new IllegalStateException("getOutput() == null!");
    }
    return false;
  }
  
  public void prepareReplacePixels(int paramInt, Rectangle paramRectangle)
    throws IOException
  {
    unsupported();
  }
  
  public void replacePixels(RenderedImage paramRenderedImage, ImageWriteParam paramImageWriteParam)
    throws IOException
  {
    unsupported();
  }
  
  public void replacePixels(Raster paramRaster, ImageWriteParam paramImageWriteParam)
    throws IOException
  {
    unsupported();
  }
  
  public void endReplacePixels()
    throws IOException
  {
    unsupported();
  }
  
  public synchronized void abort()
  {
    abortFlag = true;
  }
  
  protected synchronized boolean abortRequested()
  {
    return abortFlag;
  }
  
  protected synchronized void clearAbortRequest()
  {
    abortFlag = false;
  }
  
  public void addIIOWriteWarningListener(IIOWriteWarningListener paramIIOWriteWarningListener)
  {
    if (paramIIOWriteWarningListener == null) {
      return;
    }
    warningListeners = ImageReader.addToList(warningListeners, paramIIOWriteWarningListener);
    warningLocales = ImageReader.addToList(warningLocales, getLocale());
  }
  
  public void removeIIOWriteWarningListener(IIOWriteWarningListener paramIIOWriteWarningListener)
  {
    if ((paramIIOWriteWarningListener == null) || (warningListeners == null)) {
      return;
    }
    int i = warningListeners.indexOf(paramIIOWriteWarningListener);
    if (i != -1)
    {
      warningListeners.remove(i);
      warningLocales.remove(i);
      if (warningListeners.size() == 0)
      {
        warningListeners = null;
        warningLocales = null;
      }
    }
  }
  
  public void removeAllIIOWriteWarningListeners()
  {
    warningListeners = null;
    warningLocales = null;
  }
  
  public void addIIOWriteProgressListener(IIOWriteProgressListener paramIIOWriteProgressListener)
  {
    if (paramIIOWriteProgressListener == null) {
      return;
    }
    progressListeners = ImageReader.addToList(progressListeners, paramIIOWriteProgressListener);
  }
  
  public void removeIIOWriteProgressListener(IIOWriteProgressListener paramIIOWriteProgressListener)
  {
    if ((paramIIOWriteProgressListener == null) || (progressListeners == null)) {
      return;
    }
    progressListeners = ImageReader.removeFromList(progressListeners, paramIIOWriteProgressListener);
  }
  
  public void removeAllIIOWriteProgressListeners()
  {
    progressListeners = null;
  }
  
  protected void processImageStarted(int paramInt)
  {
    if (progressListeners == null) {
      return;
    }
    int i = progressListeners.size();
    for (int j = 0; j < i; j++)
    {
      IIOWriteProgressListener localIIOWriteProgressListener = (IIOWriteProgressListener)progressListeners.get(j);
      localIIOWriteProgressListener.imageStarted(this, paramInt);
    }
  }
  
  protected void processImageProgress(float paramFloat)
  {
    if (progressListeners == null) {
      return;
    }
    int i = progressListeners.size();
    for (int j = 0; j < i; j++)
    {
      IIOWriteProgressListener localIIOWriteProgressListener = (IIOWriteProgressListener)progressListeners.get(j);
      localIIOWriteProgressListener.imageProgress(this, paramFloat);
    }
  }
  
  protected void processImageComplete()
  {
    if (progressListeners == null) {
      return;
    }
    int i = progressListeners.size();
    for (int j = 0; j < i; j++)
    {
      IIOWriteProgressListener localIIOWriteProgressListener = (IIOWriteProgressListener)progressListeners.get(j);
      localIIOWriteProgressListener.imageComplete(this);
    }
  }
  
  protected void processThumbnailStarted(int paramInt1, int paramInt2)
  {
    if (progressListeners == null) {
      return;
    }
    int i = progressListeners.size();
    for (int j = 0; j < i; j++)
    {
      IIOWriteProgressListener localIIOWriteProgressListener = (IIOWriteProgressListener)progressListeners.get(j);
      localIIOWriteProgressListener.thumbnailStarted(this, paramInt1, paramInt2);
    }
  }
  
  protected void processThumbnailProgress(float paramFloat)
  {
    if (progressListeners == null) {
      return;
    }
    int i = progressListeners.size();
    for (int j = 0; j < i; j++)
    {
      IIOWriteProgressListener localIIOWriteProgressListener = (IIOWriteProgressListener)progressListeners.get(j);
      localIIOWriteProgressListener.thumbnailProgress(this, paramFloat);
    }
  }
  
  protected void processThumbnailComplete()
  {
    if (progressListeners == null) {
      return;
    }
    int i = progressListeners.size();
    for (int j = 0; j < i; j++)
    {
      IIOWriteProgressListener localIIOWriteProgressListener = (IIOWriteProgressListener)progressListeners.get(j);
      localIIOWriteProgressListener.thumbnailComplete(this);
    }
  }
  
  protected void processWriteAborted()
  {
    if (progressListeners == null) {
      return;
    }
    int i = progressListeners.size();
    for (int j = 0; j < i; j++)
    {
      IIOWriteProgressListener localIIOWriteProgressListener = (IIOWriteProgressListener)progressListeners.get(j);
      localIIOWriteProgressListener.writeAborted(this);
    }
  }
  
  protected void processWarningOccurred(int paramInt, String paramString)
  {
    if (warningListeners == null) {
      return;
    }
    if (paramString == null) {
      throw new IllegalArgumentException("warning == null!");
    }
    int i = warningListeners.size();
    for (int j = 0; j < i; j++)
    {
      IIOWriteWarningListener localIIOWriteWarningListener = (IIOWriteWarningListener)warningListeners.get(j);
      localIIOWriteWarningListener.warningOccurred(this, paramInt, paramString);
    }
  }
  
  protected void processWarningOccurred(int paramInt, String paramString1, String paramString2)
  {
    if (warningListeners == null) {
      return;
    }
    if (paramString1 == null) {
      throw new IllegalArgumentException("baseName == null!");
    }
    if (paramString2 == null) {
      throw new IllegalArgumentException("keyword == null!");
    }
    int i = warningListeners.size();
    for (int j = 0; j < i; j++)
    {
      IIOWriteWarningListener localIIOWriteWarningListener = (IIOWriteWarningListener)warningListeners.get(j);
      Locale localLocale = (Locale)warningLocales.get(j);
      if (localLocale == null) {
        localLocale = Locale.getDefault();
      }
      ClassLoader localClassLoader = (ClassLoader)AccessController.doPrivileged(new PrivilegedAction()
      {
        public Object run()
        {
          return Thread.currentThread().getContextClassLoader();
        }
      });
      ResourceBundle localResourceBundle = null;
      try
      {
        localResourceBundle = ResourceBundle.getBundle(paramString1, localLocale, localClassLoader);
      }
      catch (MissingResourceException localMissingResourceException1)
      {
        try
        {
          localResourceBundle = ResourceBundle.getBundle(paramString1, localLocale);
        }
        catch (MissingResourceException localMissingResourceException2)
        {
          throw new IllegalArgumentException("Bundle not found!");
        }
      }
      String str = null;
      try
      {
        str = localResourceBundle.getString(paramString2);
      }
      catch (ClassCastException localClassCastException)
      {
        throw new IllegalArgumentException("Resource is not a String!");
      }
      catch (MissingResourceException localMissingResourceException3)
      {
        throw new IllegalArgumentException("Resource is missing!");
      }
      localIIOWriteWarningListener.warningOccurred(this, paramInt, str);
    }
  }
  
  public void reset()
  {
    setOutput(null);
    setLocale(null);
    removeAllIIOWriteWarningListeners();
    removeAllIIOWriteProgressListeners();
    clearAbortRequest();
  }
  
  public void dispose() {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\imageio\ImageWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
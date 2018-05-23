package javax.imageio;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import javax.imageio.event.IIOReadProgressListener;
import javax.imageio.event.IIOReadUpdateListener;
import javax.imageio.event.IIOReadWarningListener;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;

public abstract class ImageReader
{
  protected ImageReaderSpi originatingProvider;
  protected Object input = null;
  protected boolean seekForwardOnly = false;
  protected boolean ignoreMetadata = false;
  protected int minIndex = 0;
  protected Locale[] availableLocales = null;
  protected Locale locale = null;
  protected List<IIOReadWarningListener> warningListeners = null;
  protected List<Locale> warningLocales = null;
  protected List<IIOReadProgressListener> progressListeners = null;
  protected List<IIOReadUpdateListener> updateListeners = null;
  private boolean abortFlag = false;
  
  protected ImageReader(ImageReaderSpi paramImageReaderSpi)
  {
    originatingProvider = paramImageReaderSpi;
  }
  
  public String getFormatName()
    throws IOException
  {
    return originatingProvider.getFormatNames()[0];
  }
  
  public ImageReaderSpi getOriginatingProvider()
  {
    return originatingProvider;
  }
  
  public void setInput(Object paramObject, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (paramObject != null)
    {
      int i = 0;
      if (originatingProvider != null)
      {
        Class[] arrayOfClass = originatingProvider.getInputTypes();
        for (int j = 0; j < arrayOfClass.length; j++) {
          if (arrayOfClass[j].isInstance(paramObject))
          {
            i = 1;
            break;
          }
        }
      }
      else if ((paramObject instanceof ImageInputStream))
      {
        i = 1;
      }
      if (i == 0) {
        throw new IllegalArgumentException("Incorrect input type!");
      }
      seekForwardOnly = paramBoolean1;
      ignoreMetadata = paramBoolean2;
      minIndex = 0;
    }
    input = paramObject;
  }
  
  public void setInput(Object paramObject, boolean paramBoolean)
  {
    setInput(paramObject, paramBoolean, false);
  }
  
  public void setInput(Object paramObject)
  {
    setInput(paramObject, false, false);
  }
  
  public Object getInput()
  {
    return input;
  }
  
  public boolean isSeekForwardOnly()
  {
    return seekForwardOnly;
  }
  
  public boolean isIgnoringMetadata()
  {
    return ignoreMetadata;
  }
  
  public int getMinIndex()
  {
    return minIndex;
  }
  
  public Locale[] getAvailableLocales()
  {
    if (availableLocales == null) {
      return null;
    }
    return (Locale[])availableLocales.clone();
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
  
  public abstract int getNumImages(boolean paramBoolean)
    throws IOException;
  
  public abstract int getWidth(int paramInt)
    throws IOException;
  
  public abstract int getHeight(int paramInt)
    throws IOException;
  
  public boolean isRandomAccessEasy(int paramInt)
    throws IOException
  {
    return false;
  }
  
  public float getAspectRatio(int paramInt)
    throws IOException
  {
    return getWidth(paramInt) / getHeight(paramInt);
  }
  
  public ImageTypeSpecifier getRawImageType(int paramInt)
    throws IOException
  {
    return (ImageTypeSpecifier)getImageTypes(paramInt).next();
  }
  
  public abstract Iterator<ImageTypeSpecifier> getImageTypes(int paramInt)
    throws IOException;
  
  public ImageReadParam getDefaultReadParam()
  {
    return new ImageReadParam();
  }
  
  public abstract IIOMetadata getStreamMetadata()
    throws IOException;
  
  public IIOMetadata getStreamMetadata(String paramString, Set<String> paramSet)
    throws IOException
  {
    return getMetadata(paramString, paramSet, true, 0);
  }
  
  private IIOMetadata getMetadata(String paramString, Set paramSet, boolean paramBoolean, int paramInt)
    throws IOException
  {
    if (paramString == null) {
      throw new IllegalArgumentException("formatName == null!");
    }
    if (paramSet == null) {
      throw new IllegalArgumentException("nodeNames == null!");
    }
    IIOMetadata localIIOMetadata = paramBoolean ? getStreamMetadata() : getImageMetadata(paramInt);
    if (localIIOMetadata != null)
    {
      if ((localIIOMetadata.isStandardMetadataFormatSupported()) && (paramString.equals("javax_imageio_1.0"))) {
        return localIIOMetadata;
      }
      String str = localIIOMetadata.getNativeMetadataFormatName();
      if ((str != null) && (paramString.equals(str))) {
        return localIIOMetadata;
      }
      String[] arrayOfString = localIIOMetadata.getExtraMetadataFormatNames();
      if (arrayOfString != null) {
        for (int i = 0; i < arrayOfString.length; i++) {
          if (paramString.equals(arrayOfString[i])) {
            return localIIOMetadata;
          }
        }
      }
    }
    return null;
  }
  
  public abstract IIOMetadata getImageMetadata(int paramInt)
    throws IOException;
  
  public IIOMetadata getImageMetadata(int paramInt, String paramString, Set<String> paramSet)
    throws IOException
  {
    return getMetadata(paramString, paramSet, false, paramInt);
  }
  
  public BufferedImage read(int paramInt)
    throws IOException
  {
    return read(paramInt, null);
  }
  
  public abstract BufferedImage read(int paramInt, ImageReadParam paramImageReadParam)
    throws IOException;
  
  public IIOImage readAll(int paramInt, ImageReadParam paramImageReadParam)
    throws IOException
  {
    if (paramInt < getMinIndex()) {
      throw new IndexOutOfBoundsException("imageIndex < getMinIndex()!");
    }
    BufferedImage localBufferedImage = read(paramInt, paramImageReadParam);
    ArrayList localArrayList = null;
    int i = getNumThumbnails(paramInt);
    if (i > 0)
    {
      localArrayList = new ArrayList();
      for (int j = 0; j < i; j++) {
        localArrayList.add(readThumbnail(paramInt, j));
      }
    }
    IIOMetadata localIIOMetadata = getImageMetadata(paramInt);
    return new IIOImage(localBufferedImage, localArrayList, localIIOMetadata);
  }
  
  public Iterator<IIOImage> readAll(Iterator<? extends ImageReadParam> paramIterator)
    throws IOException
  {
    ArrayList localArrayList1 = new ArrayList();
    int i = getMinIndex();
    processSequenceStarted(i);
    for (;;)
    {
      ImageReadParam localImageReadParam = null;
      if ((paramIterator != null) && (paramIterator.hasNext()))
      {
        localObject = paramIterator.next();
        if (localObject != null) {
          if ((localObject instanceof ImageReadParam)) {
            localImageReadParam = (ImageReadParam)localObject;
          } else {
            throw new IllegalArgumentException("Non-ImageReadParam supplied as part of params!");
          }
        }
      }
      Object localObject = null;
      try
      {
        localObject = read(i, localImageReadParam);
      }
      catch (IndexOutOfBoundsException localIndexOutOfBoundsException)
      {
        break;
      }
      ArrayList localArrayList2 = null;
      int j = getNumThumbnails(i);
      if (j > 0)
      {
        localArrayList2 = new ArrayList();
        for (int k = 0; k < j; k++) {
          localArrayList2.add(readThumbnail(i, k));
        }
      }
      IIOMetadata localIIOMetadata = getImageMetadata(i);
      IIOImage localIIOImage = new IIOImage((RenderedImage)localObject, localArrayList2, localIIOMetadata);
      localArrayList1.add(localIIOImage);
      i++;
    }
    processSequenceComplete();
    return localArrayList1.iterator();
  }
  
  public boolean canReadRaster()
  {
    return false;
  }
  
  public Raster readRaster(int paramInt, ImageReadParam paramImageReadParam)
    throws IOException
  {
    throw new UnsupportedOperationException("readRaster not supported!");
  }
  
  public boolean isImageTiled(int paramInt)
    throws IOException
  {
    return false;
  }
  
  public int getTileWidth(int paramInt)
    throws IOException
  {
    return getWidth(paramInt);
  }
  
  public int getTileHeight(int paramInt)
    throws IOException
  {
    return getHeight(paramInt);
  }
  
  public int getTileGridXOffset(int paramInt)
    throws IOException
  {
    return 0;
  }
  
  public int getTileGridYOffset(int paramInt)
    throws IOException
  {
    return 0;
  }
  
  public BufferedImage readTile(int paramInt1, int paramInt2, int paramInt3)
    throws IOException
  {
    if ((paramInt2 != 0) || (paramInt3 != 0)) {
      throw new IllegalArgumentException("Invalid tile indices");
    }
    return read(paramInt1);
  }
  
  public Raster readTileRaster(int paramInt1, int paramInt2, int paramInt3)
    throws IOException
  {
    if (!canReadRaster()) {
      throw new UnsupportedOperationException("readTileRaster not supported!");
    }
    if ((paramInt2 != 0) || (paramInt3 != 0)) {
      throw new IllegalArgumentException("Invalid tile indices");
    }
    return readRaster(paramInt1, null);
  }
  
  public RenderedImage readAsRenderedImage(int paramInt, ImageReadParam paramImageReadParam)
    throws IOException
  {
    return read(paramInt, paramImageReadParam);
  }
  
  public boolean readerSupportsThumbnails()
  {
    return false;
  }
  
  public boolean hasThumbnails(int paramInt)
    throws IOException
  {
    return getNumThumbnails(paramInt) > 0;
  }
  
  public int getNumThumbnails(int paramInt)
    throws IOException
  {
    return 0;
  }
  
  public int getThumbnailWidth(int paramInt1, int paramInt2)
    throws IOException
  {
    return readThumbnail(paramInt1, paramInt2).getWidth();
  }
  
  public int getThumbnailHeight(int paramInt1, int paramInt2)
    throws IOException
  {
    return readThumbnail(paramInt1, paramInt2).getHeight();
  }
  
  public BufferedImage readThumbnail(int paramInt1, int paramInt2)
    throws IOException
  {
    throw new UnsupportedOperationException("Thumbnails not supported!");
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
  
  static List addToList(List paramList, Object paramObject)
  {
    if (paramList == null) {
      paramList = new ArrayList();
    }
    paramList.add(paramObject);
    return paramList;
  }
  
  static List removeFromList(List paramList, Object paramObject)
  {
    if (paramList == null) {
      return paramList;
    }
    paramList.remove(paramObject);
    if (paramList.size() == 0) {
      paramList = null;
    }
    return paramList;
  }
  
  public void addIIOReadWarningListener(IIOReadWarningListener paramIIOReadWarningListener)
  {
    if (paramIIOReadWarningListener == null) {
      return;
    }
    warningListeners = addToList(warningListeners, paramIIOReadWarningListener);
    warningLocales = addToList(warningLocales, getLocale());
  }
  
  public void removeIIOReadWarningListener(IIOReadWarningListener paramIIOReadWarningListener)
  {
    if ((paramIIOReadWarningListener == null) || (warningListeners == null)) {
      return;
    }
    int i = warningListeners.indexOf(paramIIOReadWarningListener);
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
  
  public void removeAllIIOReadWarningListeners()
  {
    warningListeners = null;
    warningLocales = null;
  }
  
  public void addIIOReadProgressListener(IIOReadProgressListener paramIIOReadProgressListener)
  {
    if (paramIIOReadProgressListener == null) {
      return;
    }
    progressListeners = addToList(progressListeners, paramIIOReadProgressListener);
  }
  
  public void removeIIOReadProgressListener(IIOReadProgressListener paramIIOReadProgressListener)
  {
    if ((paramIIOReadProgressListener == null) || (progressListeners == null)) {
      return;
    }
    progressListeners = removeFromList(progressListeners, paramIIOReadProgressListener);
  }
  
  public void removeAllIIOReadProgressListeners()
  {
    progressListeners = null;
  }
  
  public void addIIOReadUpdateListener(IIOReadUpdateListener paramIIOReadUpdateListener)
  {
    if (paramIIOReadUpdateListener == null) {
      return;
    }
    updateListeners = addToList(updateListeners, paramIIOReadUpdateListener);
  }
  
  public void removeIIOReadUpdateListener(IIOReadUpdateListener paramIIOReadUpdateListener)
  {
    if ((paramIIOReadUpdateListener == null) || (updateListeners == null)) {
      return;
    }
    updateListeners = removeFromList(updateListeners, paramIIOReadUpdateListener);
  }
  
  public void removeAllIIOReadUpdateListeners()
  {
    updateListeners = null;
  }
  
  protected void processSequenceStarted(int paramInt)
  {
    if (progressListeners == null) {
      return;
    }
    int i = progressListeners.size();
    for (int j = 0; j < i; j++)
    {
      IIOReadProgressListener localIIOReadProgressListener = (IIOReadProgressListener)progressListeners.get(j);
      localIIOReadProgressListener.sequenceStarted(this, paramInt);
    }
  }
  
  protected void processSequenceComplete()
  {
    if (progressListeners == null) {
      return;
    }
    int i = progressListeners.size();
    for (int j = 0; j < i; j++)
    {
      IIOReadProgressListener localIIOReadProgressListener = (IIOReadProgressListener)progressListeners.get(j);
      localIIOReadProgressListener.sequenceComplete(this);
    }
  }
  
  protected void processImageStarted(int paramInt)
  {
    if (progressListeners == null) {
      return;
    }
    int i = progressListeners.size();
    for (int j = 0; j < i; j++)
    {
      IIOReadProgressListener localIIOReadProgressListener = (IIOReadProgressListener)progressListeners.get(j);
      localIIOReadProgressListener.imageStarted(this, paramInt);
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
      IIOReadProgressListener localIIOReadProgressListener = (IIOReadProgressListener)progressListeners.get(j);
      localIIOReadProgressListener.imageProgress(this, paramFloat);
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
      IIOReadProgressListener localIIOReadProgressListener = (IIOReadProgressListener)progressListeners.get(j);
      localIIOReadProgressListener.imageComplete(this);
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
      IIOReadProgressListener localIIOReadProgressListener = (IIOReadProgressListener)progressListeners.get(j);
      localIIOReadProgressListener.thumbnailStarted(this, paramInt1, paramInt2);
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
      IIOReadProgressListener localIIOReadProgressListener = (IIOReadProgressListener)progressListeners.get(j);
      localIIOReadProgressListener.thumbnailProgress(this, paramFloat);
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
      IIOReadProgressListener localIIOReadProgressListener = (IIOReadProgressListener)progressListeners.get(j);
      localIIOReadProgressListener.thumbnailComplete(this);
    }
  }
  
  protected void processReadAborted()
  {
    if (progressListeners == null) {
      return;
    }
    int i = progressListeners.size();
    for (int j = 0; j < i; j++)
    {
      IIOReadProgressListener localIIOReadProgressListener = (IIOReadProgressListener)progressListeners.get(j);
      localIIOReadProgressListener.readAborted(this);
    }
  }
  
  protected void processPassStarted(BufferedImage paramBufferedImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int[] paramArrayOfInt)
  {
    if (updateListeners == null) {
      return;
    }
    int i = updateListeners.size();
    for (int j = 0; j < i; j++)
    {
      IIOReadUpdateListener localIIOReadUpdateListener = (IIOReadUpdateListener)updateListeners.get(j);
      localIIOReadUpdateListener.passStarted(this, paramBufferedImage, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7, paramArrayOfInt);
    }
  }
  
  protected void processImageUpdate(BufferedImage paramBufferedImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int[] paramArrayOfInt)
  {
    if (updateListeners == null) {
      return;
    }
    int i = updateListeners.size();
    for (int j = 0; j < i; j++)
    {
      IIOReadUpdateListener localIIOReadUpdateListener = (IIOReadUpdateListener)updateListeners.get(j);
      localIIOReadUpdateListener.imageUpdate(this, paramBufferedImage, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramArrayOfInt);
    }
  }
  
  protected void processPassComplete(BufferedImage paramBufferedImage)
  {
    if (updateListeners == null) {
      return;
    }
    int i = updateListeners.size();
    for (int j = 0; j < i; j++)
    {
      IIOReadUpdateListener localIIOReadUpdateListener = (IIOReadUpdateListener)updateListeners.get(j);
      localIIOReadUpdateListener.passComplete(this, paramBufferedImage);
    }
  }
  
  protected void processThumbnailPassStarted(BufferedImage paramBufferedImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int[] paramArrayOfInt)
  {
    if (updateListeners == null) {
      return;
    }
    int i = updateListeners.size();
    for (int j = 0; j < i; j++)
    {
      IIOReadUpdateListener localIIOReadUpdateListener = (IIOReadUpdateListener)updateListeners.get(j);
      localIIOReadUpdateListener.thumbnailPassStarted(this, paramBufferedImage, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7, paramArrayOfInt);
    }
  }
  
  protected void processThumbnailUpdate(BufferedImage paramBufferedImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int[] paramArrayOfInt)
  {
    if (updateListeners == null) {
      return;
    }
    int i = updateListeners.size();
    for (int j = 0; j < i; j++)
    {
      IIOReadUpdateListener localIIOReadUpdateListener = (IIOReadUpdateListener)updateListeners.get(j);
      localIIOReadUpdateListener.thumbnailUpdate(this, paramBufferedImage, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramArrayOfInt);
    }
  }
  
  protected void processThumbnailPassComplete(BufferedImage paramBufferedImage)
  {
    if (updateListeners == null) {
      return;
    }
    int i = updateListeners.size();
    for (int j = 0; j < i; j++)
    {
      IIOReadUpdateListener localIIOReadUpdateListener = (IIOReadUpdateListener)updateListeners.get(j);
      localIIOReadUpdateListener.thumbnailPassComplete(this, paramBufferedImage);
    }
  }
  
  protected void processWarningOccurred(String paramString)
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
      IIOReadWarningListener localIIOReadWarningListener = (IIOReadWarningListener)warningListeners.get(j);
      localIIOReadWarningListener.warningOccurred(this, paramString);
    }
  }
  
  protected void processWarningOccurred(String paramString1, String paramString2)
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
      IIOReadWarningListener localIIOReadWarningListener = (IIOReadWarningListener)warningListeners.get(j);
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
      localIIOReadWarningListener.warningOccurred(this, str);
    }
  }
  
  public void reset()
  {
    setInput(null, false, false);
    setLocale(null);
    removeAllIIOReadUpdateListeners();
    removeAllIIOReadProgressListeners();
    removeAllIIOReadWarningListeners();
    clearAbortRequest();
  }
  
  public void dispose() {}
  
  protected static Rectangle getSourceRegion(ImageReadParam paramImageReadParam, int paramInt1, int paramInt2)
  {
    Rectangle localRectangle1 = new Rectangle(0, 0, paramInt1, paramInt2);
    if (paramImageReadParam != null)
    {
      Rectangle localRectangle2 = paramImageReadParam.getSourceRegion();
      if (localRectangle2 != null) {
        localRectangle1 = localRectangle1.intersection(localRectangle2);
      }
      int i = paramImageReadParam.getSubsamplingXOffset();
      int j = paramImageReadParam.getSubsamplingYOffset();
      x += i;
      y += j;
      width -= i;
      height -= j;
    }
    return localRectangle1;
  }
  
  protected static void computeRegions(ImageReadParam paramImageReadParam, int paramInt1, int paramInt2, BufferedImage paramBufferedImage, Rectangle paramRectangle1, Rectangle paramRectangle2)
  {
    if (paramRectangle1 == null) {
      throw new IllegalArgumentException("srcRegion == null!");
    }
    if (paramRectangle2 == null) {
      throw new IllegalArgumentException("destRegion == null!");
    }
    paramRectangle1.setBounds(0, 0, paramInt1, paramInt2);
    paramRectangle2.setBounds(0, 0, paramInt1, paramInt2);
    int i = 1;
    int j = 1;
    int k = 0;
    int m = 0;
    if (paramImageReadParam != null)
    {
      Rectangle localRectangle1 = paramImageReadParam.getSourceRegion();
      if (localRectangle1 != null) {
        paramRectangle1.setBounds(paramRectangle1.intersection(localRectangle1));
      }
      i = paramImageReadParam.getSourceXSubsampling();
      j = paramImageReadParam.getSourceYSubsampling();
      k = paramImageReadParam.getSubsamplingXOffset();
      m = paramImageReadParam.getSubsamplingYOffset();
      paramRectangle1.translate(k, m);
      width -= k;
      height -= m;
      paramRectangle2.setLocation(paramImageReadParam.getDestinationOffset());
    }
    if (x < 0)
    {
      n = -x * i;
      x += n;
      width -= n;
      x = 0;
    }
    if (y < 0)
    {
      n = -y * j;
      y += n;
      height -= n;
      y = 0;
    }
    int n = (width + i - 1) / i;
    int i1 = (height + j - 1) / j;
    width = n;
    height = i1;
    if (paramBufferedImage != null)
    {
      Rectangle localRectangle2 = new Rectangle(0, 0, paramBufferedImage.getWidth(), paramBufferedImage.getHeight());
      paramRectangle2.setBounds(paramRectangle2.intersection(localRectangle2));
      if (paramRectangle2.isEmpty()) {
        throw new IllegalArgumentException("Empty destination region!");
      }
      int i2 = x + n - paramBufferedImage.getWidth();
      if (i2 > 0) {
        width -= i2 * i;
      }
      int i3 = y + i1 - paramBufferedImage.getHeight();
      if (i3 > 0) {
        height -= i3 * j;
      }
    }
    if ((paramRectangle1.isEmpty()) || (paramRectangle2.isEmpty())) {
      throw new IllegalArgumentException("Empty region!");
    }
  }
  
  protected static void checkReadParamBandSettings(ImageReadParam paramImageReadParam, int paramInt1, int paramInt2)
  {
    int[] arrayOfInt1 = null;
    int[] arrayOfInt2 = null;
    if (paramImageReadParam != null)
    {
      arrayOfInt1 = paramImageReadParam.getSourceBands();
      arrayOfInt2 = paramImageReadParam.getDestinationBands();
    }
    int i = arrayOfInt1 == null ? paramInt1 : arrayOfInt1.length;
    int j = arrayOfInt2 == null ? paramInt2 : arrayOfInt2.length;
    if (i != j) {
      throw new IllegalArgumentException("ImageReadParam num source & dest bands differ!");
    }
    int k;
    if (arrayOfInt1 != null) {
      for (k = 0; k < arrayOfInt1.length; k++) {
        if (arrayOfInt1[k] >= paramInt1) {
          throw new IllegalArgumentException("ImageReadParam source bands contains a value >= the number of source bands!");
        }
      }
    }
    if (arrayOfInt2 != null) {
      for (k = 0; k < arrayOfInt2.length; k++) {
        if (arrayOfInt2[k] >= paramInt2) {
          throw new IllegalArgumentException("ImageReadParam dest bands contains a value >= the number of dest bands!");
        }
      }
    }
  }
  
  protected static BufferedImage getDestination(ImageReadParam paramImageReadParam, Iterator<ImageTypeSpecifier> paramIterator, int paramInt1, int paramInt2)
    throws IIOException
  {
    if ((paramIterator == null) || (!paramIterator.hasNext())) {
      throw new IllegalArgumentException("imageTypes null or empty!");
    }
    if (paramInt1 * paramInt2 > 2147483647L) {
      throw new IllegalArgumentException("width*height > Integer.MAX_VALUE!");
    }
    BufferedImage localBufferedImage = null;
    ImageTypeSpecifier localImageTypeSpecifier = null;
    if (paramImageReadParam != null)
    {
      localBufferedImage = paramImageReadParam.getDestination();
      if (localBufferedImage != null) {
        return localBufferedImage;
      }
      localImageTypeSpecifier = paramImageReadParam.getDestinationType();
    }
    if (localImageTypeSpecifier == null)
    {
      Object localObject1 = paramIterator.next();
      if (!(localObject1 instanceof ImageTypeSpecifier)) {
        throw new IllegalArgumentException("Non-ImageTypeSpecifier retrieved from imageTypes!");
      }
      localImageTypeSpecifier = (ImageTypeSpecifier)localObject1;
    }
    else
    {
      int i = 0;
      while (paramIterator.hasNext())
      {
        localObject2 = (ImageTypeSpecifier)paramIterator.next();
        if (((ImageTypeSpecifier)localObject2).equals(localImageTypeSpecifier))
        {
          i = 1;
          break;
        }
      }
      if (i == 0) {
        throw new IIOException("Destination type from ImageReadParam does not match!");
      }
    }
    Rectangle localRectangle = new Rectangle(0, 0, 0, 0);
    Object localObject2 = new Rectangle(0, 0, 0, 0);
    computeRegions(paramImageReadParam, paramInt1, paramInt2, null, localRectangle, (Rectangle)localObject2);
    int j = x + width;
    int k = y + height;
    return localImageTypeSpecifier.createBufferedImage(j, k);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\imageio\ImageReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
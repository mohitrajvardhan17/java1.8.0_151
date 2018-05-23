package java.awt;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.awt.image.SunWritableRaster;
import sun.util.logging.PlatformLogger;
import sun.util.logging.PlatformLogger.Level;

public final class SplashScreen
{
  private BufferedImage image;
  private final long splashPtr;
  private static boolean wasClosed = false;
  private URL imageURL;
  private static SplashScreen theInstance = null;
  private static final PlatformLogger log = PlatformLogger.getLogger("java.awt.SplashScreen");
  
  SplashScreen(long paramLong)
  {
    splashPtr = paramLong;
  }
  
  public static SplashScreen getSplashScreen()
  {
    synchronized (SplashScreen.class)
    {
      if (GraphicsEnvironment.isHeadless()) {
        throw new HeadlessException();
      }
      if ((!wasClosed) && (theInstance == null))
      {
        AccessController.doPrivileged(new PrivilegedAction()
        {
          public Void run()
          {
            System.loadLibrary("splashscreen");
            return null;
          }
        });
        long l = _getInstance();
        if ((l != 0L) && (_isVisible(l))) {
          theInstance = new SplashScreen(l);
        }
      }
      return theInstance;
    }
  }
  
  public void setImageURL(URL paramURL)
    throws NullPointerException, IOException, IllegalStateException
  {
    checkVisible();
    URLConnection localURLConnection = paramURL.openConnection();
    localURLConnection.connect();
    int i = localURLConnection.getContentLength();
    InputStream localInputStream = localURLConnection.getInputStream();
    byte[] arrayOfByte1 = new byte[i];
    int j = 0;
    for (;;)
    {
      int k = localInputStream.available();
      if (k <= 0) {
        k = 1;
      }
      if (j + k > i)
      {
        i = j * 2;
        if (j + k > i) {
          i = k + j;
        }
        byte[] arrayOfByte2 = arrayOfByte1;
        arrayOfByte1 = new byte[i];
        System.arraycopy(arrayOfByte2, 0, arrayOfByte1, 0, j);
      }
      int m = localInputStream.read(arrayOfByte1, j, k);
      if (m < 0) {
        break;
      }
      j += m;
    }
    synchronized (SplashScreen.class)
    {
      checkVisible();
      if (!_setImageData(splashPtr, arrayOfByte1)) {
        throw new IOException("Bad image format or i/o error when loading image");
      }
      imageURL = paramURL;
    }
  }
  
  private void checkVisible()
  {
    if (!isVisible()) {
      throw new IllegalStateException("no splash screen available");
    }
  }
  
  public URL getImageURL()
    throws IllegalStateException
  {
    synchronized (SplashScreen.class)
    {
      checkVisible();
      if (imageURL == null) {
        try
        {
          String str1 = _getImageFileName(splashPtr);
          String str2 = _getImageJarName(splashPtr);
          if (str1 != null) {
            if (str2 != null) {
              imageURL = new URL("jar:" + new File(str2).toURL().toString() + "!/" + str1);
            } else {
              imageURL = new File(str1).toURL();
            }
          }
        }
        catch (MalformedURLException localMalformedURLException)
        {
          if (log.isLoggable(PlatformLogger.Level.FINE)) {
            log.fine("MalformedURLException caught in the getImageURL() method", localMalformedURLException);
          }
        }
      }
      return imageURL;
    }
  }
  
  public Rectangle getBounds()
    throws IllegalStateException
  {
    synchronized (SplashScreen.class)
    {
      checkVisible();
      float f = _getScaleFactor(splashPtr);
      Rectangle localRectangle = _getBounds(splashPtr);
      assert (f > 0.0F);
      if ((f > 0.0F) && (f != 1.0F)) {
        localRectangle.setSize((int)(localRectangle.getWidth() / f), (int)(localRectangle.getHeight() / f));
      }
      return localRectangle;
    }
  }
  
  public Dimension getSize()
    throws IllegalStateException
  {
    return getBounds().getSize();
  }
  
  public Graphics2D createGraphics()
    throws IllegalStateException
  {
    synchronized (SplashScreen.class)
    {
      checkVisible();
      if (image == null)
      {
        Dimension localDimension = _getBounds(splashPtr).getSize();
        image = new BufferedImage(width, height, 2);
      }
      float f = _getScaleFactor(splashPtr);
      Graphics2D localGraphics2D = image.createGraphics();
      assert (f > 0.0F);
      if (f <= 0.0F) {
        f = 1.0F;
      }
      localGraphics2D.scale(f, f);
      return localGraphics2D;
    }
  }
  
  public void update()
    throws IllegalStateException
  {
    BufferedImage localBufferedImage;
    synchronized (SplashScreen.class)
    {
      checkVisible();
      localBufferedImage = image;
    }
    if (localBufferedImage == null) {
      throw new IllegalStateException("no overlay image available");
    }
    ??? = localBufferedImage.getRaster().getDataBuffer();
    if (!(??? instanceof DataBufferInt)) {
      throw new AssertionError("Overlay image DataBuffer is of invalid type == " + ???.getClass().getName());
    }
    int i = ((DataBuffer)???).getNumBanks();
    if (i != 1) {
      throw new AssertionError("Invalid number of banks ==" + i + " in overlay image DataBuffer");
    }
    if (!(localBufferedImage.getSampleModel() instanceof SinglePixelPackedSampleModel)) {
      throw new AssertionError("Overlay image has invalid sample model == " + localBufferedImage.getSampleModel().getClass().getName());
    }
    SinglePixelPackedSampleModel localSinglePixelPackedSampleModel = (SinglePixelPackedSampleModel)localBufferedImage.getSampleModel();
    int j = localSinglePixelPackedSampleModel.getScanlineStride();
    Rectangle localRectangle = localBufferedImage.getRaster().getBounds();
    int[] arrayOfInt = SunWritableRaster.stealData((DataBufferInt)???, 0);
    synchronized (SplashScreen.class)
    {
      checkVisible();
      _update(splashPtr, arrayOfInt, x, y, width, height, j);
    }
  }
  
  public void close()
    throws IllegalStateException
  {
    synchronized (SplashScreen.class)
    {
      checkVisible();
      _close(splashPtr);
      image = null;
      markClosed();
    }
  }
  
  static void markClosed()
  {
    synchronized (SplashScreen.class)
    {
      wasClosed = true;
      theInstance = null;
    }
  }
  
  public boolean isVisible()
  {
    synchronized (SplashScreen.class)
    {
      return (!wasClosed) && (_isVisible(splashPtr));
    }
  }
  
  private static native void _update(long paramLong, int[] paramArrayOfInt, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5);
  
  private static native boolean _isVisible(long paramLong);
  
  private static native Rectangle _getBounds(long paramLong);
  
  private static native long _getInstance();
  
  private static native void _close(long paramLong);
  
  private static native String _getImageFileName(long paramLong);
  
  private static native String _getImageJarName(long paramLong);
  
  private static native boolean _setImageData(long paramLong, byte[] paramArrayOfByte);
  
  private static native float _getScaleFactor(long paramLong);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\SplashScreen.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package sun.awt.image.codec;

import com.sun.image.codec.jpeg.ImageFormatException;
import com.sun.image.codec.jpeg.JPEGDecodeParam;
import com.sun.image.codec.jpeg.JPEGImageDecoder;
import java.awt.Point;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.security.AccessController;
import java.security.PrivilegedAction;

public class JPEGImageDecoderImpl
  implements JPEGImageDecoder
{
  private static final Class InputStreamClass = InputStream.class;
  private JPEGDecodeParam param = null;
  private InputStream input = null;
  private WritableRaster aRas = null;
  private BufferedImage aBufImg = null;
  private ColorModel cm = null;
  private boolean unpack = false;
  private boolean flip = false;
  
  public JPEGImageDecoderImpl(InputStream paramInputStream)
  {
    if (paramInputStream == null) {
      throw new IllegalArgumentException("InputStream is null.");
    }
    input = paramInputStream;
    initDecoder(InputStreamClass);
  }
  
  public JPEGImageDecoderImpl(InputStream paramInputStream, JPEGDecodeParam paramJPEGDecodeParam)
  {
    this(paramInputStream);
    setJPEGDecodeParam(paramJPEGDecodeParam);
  }
  
  public JPEGDecodeParam getJPEGDecodeParam()
  {
    if (param != null) {
      return (JPEGDecodeParam)param.clone();
    }
    return null;
  }
  
  public void setJPEGDecodeParam(JPEGDecodeParam paramJPEGDecodeParam)
  {
    param = ((JPEGDecodeParam)paramJPEGDecodeParam.clone());
  }
  
  public synchronized InputStream getInputStream()
  {
    return input;
  }
  
  public synchronized Raster decodeAsRaster()
    throws ImageFormatException
  {
    try
    {
      param = readJPEGStream(input, param, false);
    }
    catch (IOException localIOException)
    {
      System.out.println("Can't open input Stream" + localIOException);
      localIOException.printStackTrace();
    }
    return aRas;
  }
  
  public synchronized BufferedImage decodeAsBufferedImage()
    throws ImageFormatException
  {
    try
    {
      param = readJPEGStream(input, param, true);
    }
    catch (IOException localIOException)
    {
      System.out.println("Can't open input Stream" + localIOException);
      localIOException.printStackTrace();
    }
    return aBufImg;
  }
  
  private native void initDecoder(Class paramClass);
  
  private synchronized native JPEGDecodeParam readJPEGStream(InputStream paramInputStream, JPEGDecodeParam paramJPEGDecodeParam, boolean paramBoolean)
    throws IOException, ImageFormatException;
  
  private void readTables()
    throws IOException
  {
    try
    {
      param = readJPEGStream(input, null, false);
    }
    catch (ImageFormatException localImageFormatException)
    {
      localImageFormatException.printStackTrace();
    }
  }
  
  private int getDecodedColorModel(int paramInt, boolean paramBoolean)
    throws ImageFormatException
  {
    int[] arrayOfInt1 = { 8 };
    int[] arrayOfInt2 = { 8, 8, 8 };
    int[] arrayOfInt3 = { 8, 8, 8, 8 };
    cm = null;
    unpack = false;
    flip = false;
    if (!paramBoolean) {
      return paramInt;
    }
    switch (paramInt)
    {
    case 1: 
      cm = new ComponentColorModel(ColorSpace.getInstance(1003), arrayOfInt1, false, false, 1, 0);
      return paramInt;
    case 5: 
      cm = new ComponentColorModel(ColorSpace.getInstance(1002), arrayOfInt2, false, false, 1, 0);
      return paramInt;
    case 10: 
      cm = new ComponentColorModel(ColorSpace.getInstance(1002), arrayOfInt3, true, false, 3, 0);
      return paramInt;
    case 2: 
    case 3: 
      unpack = true;
      cm = new DirectColorModel(24, 16711680, 65280, 255);
      return 2;
    case 8: 
    case 9: 
      flip = true;
    case 6: 
    case 7: 
      unpack = true;
      cm = new DirectColorModel(ColorSpace.getInstance(1000), 32, 16711680, 65280, 255, -16777216, false, 3);
      return 6;
    }
    throw new ImageFormatException("Can't construct a BufferedImage for given COLOR_ID");
  }
  
  private Object allocateDataBuffer(int paramInt1, int paramInt2, int paramInt3)
  {
    Object localObject;
    if (unpack)
    {
      int[] arrayOfInt;
      if (paramInt3 == 3)
      {
        arrayOfInt = new int[] { 16711680, 65280, 255 };
        aRas = Raster.createPackedRaster(3, paramInt1, paramInt2, arrayOfInt, new Point(0, 0));
      }
      else if (paramInt3 == 4)
      {
        arrayOfInt = new int[] { 16711680, 65280, 255, -16777216 };
        aRas = Raster.createPackedRaster(3, paramInt1, paramInt2, arrayOfInt, new Point(0, 0));
      }
      else
      {
        throw new ImageFormatException("Can't unpack with anything other than 3 or 4 components");
      }
      localObject = ((DataBufferInt)aRas.getDataBuffer()).getData();
    }
    else
    {
      aRas = Raster.createInterleavedRaster(0, paramInt1, paramInt2, paramInt3, new Point(0, 0));
      localObject = ((DataBufferByte)aRas.getDataBuffer()).getData();
    }
    if (cm != null) {
      aBufImg = new BufferedImage(cm, aRas, true, null);
    }
    return localObject;
  }
  
  static
  {
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Void run()
      {
        System.loadLibrary("jpeg");
        return null;
      }
    });
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\image\codec\JPEGImageDecoderImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
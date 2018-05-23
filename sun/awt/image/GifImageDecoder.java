package sun.awt.image;

import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

public class GifImageDecoder
  extends ImageDecoder
{
  private static final boolean verbose = false;
  private static final int IMAGESEP = 44;
  private static final int EXBLOCK = 33;
  private static final int EX_GRAPHICS_CONTROL = 249;
  private static final int EX_COMMENT = 254;
  private static final int EX_APPLICATION = 255;
  private static final int TERMINATOR = 59;
  private static final int TRANSPARENCYMASK = 1;
  private static final int INTERLACEMASK = 64;
  private static final int COLORMAPMASK = 128;
  int num_global_colors;
  byte[] global_colormap;
  int trans_pixel = -1;
  IndexColorModel global_model;
  Hashtable props = new Hashtable();
  byte[] saved_image;
  IndexColorModel saved_model;
  int global_width;
  int global_height;
  int global_bgpixel;
  GifFrame curframe;
  private static final int normalflags = 30;
  private static final int interlaceflags = 29;
  private short[] prefix = new short['က'];
  private byte[] suffix = new byte['က'];
  private byte[] outCode = new byte['ခ'];
  
  public GifImageDecoder(InputStreamImageSource paramInputStreamImageSource, InputStream paramInputStream)
  {
    super(paramInputStreamImageSource, paramInputStream);
  }
  
  private static void error(String paramString)
    throws ImageFormatException
  {
    throw new ImageFormatException(paramString);
  }
  
  private int readBytes(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    for (;;)
    {
      if (paramInt2 > 0) {
        try
        {
          int i = input.read(paramArrayOfByte, paramInt1, paramInt2);
          if (i >= 0)
          {
            paramInt1 += i;
            paramInt2 -= i;
          }
        }
        catch (IOException localIOException) {}
      }
    }
    return paramInt2;
  }
  
  private static final int ExtractByte(byte[] paramArrayOfByte, int paramInt)
  {
    return paramArrayOfByte[paramInt] & 0xFF;
  }
  
  private static final int ExtractWord(byte[] paramArrayOfByte, int paramInt)
  {
    return paramArrayOfByte[paramInt] & 0xFF | (paramArrayOfByte[(paramInt + 1)] & 0xFF) << 8;
  }
  
  public void produceImage()
    throws IOException, ImageFormatException
  {
    try
    {
      readHeader();
      int i = 0;
      int j = 0;
      int k = -1;
      int m = 0;
      int n = -1;
      int i1 = 0;
      int i2 = 0;
      while (!aborted)
      {
        int i3;
        switch (i3 = input.read())
        {
        case 33: 
          switch (i3 = input.read())
          {
          case 249: 
            byte[] arrayOfByte1 = new byte[6];
            if (readBytes(arrayOfByte1, 0, 6) != 0) {
              return;
            }
            if ((arrayOfByte1[0] != 4) || (arrayOfByte1[5] != 0)) {
              return;
            }
            n = ExtractWord(arrayOfByte1, 2) * 10;
            if ((n > 0) && (i2 == 0))
            {
              i2 = 1;
              ImageFetcher.startingAnimation();
            }
            m = arrayOfByte1[1] >> 2 & 0x7;
            if ((arrayOfByte1[1] & 0x1) != 0) {
              trans_pixel = ExtractByte(arrayOfByte1, 4);
            } else {
              trans_pixel = -1;
            }
            break;
          case 254: 
          case 255: 
          default: 
            int i4 = 0;
            String str = "";
            for (;;)
            {
              int i5 = input.read();
              if (i5 <= 0) {
                break;
              }
              byte[] arrayOfByte2 = new byte[i5];
              if (readBytes(arrayOfByte2, 0, i5) != 0) {
                return;
              }
              if (i3 == 254)
              {
                str = str + new String(arrayOfByte2, 0);
              }
              else if (i3 == 255)
              {
                if (i4 != 0) {
                  if ((i5 == 3) && (arrayOfByte2[0] == 1))
                  {
                    if (i1 != 0)
                    {
                      ExtractWord(arrayOfByte2, 1);
                    }
                    else
                    {
                      k = ExtractWord(arrayOfByte2, 1);
                      i1 = 1;
                    }
                  }
                  else {
                    i4 = 0;
                  }
                }
                if ("NETSCAPE2.0".equals(new String(arrayOfByte2, 0))) {
                  i4 = 1;
                }
              }
            }
            if (i3 == 254) {
              props.put("comment", str);
            }
            if ((i4 == 0) || (i2 != 0)) {
              break;
            }
            i2 = 1;
            ImageFetcher.startingAnimation();
            break;
          case -1: 
            return;
          }
          break;
        case 44: 
          if (i2 == 0) {
            input.mark(0);
          }
          try
          {
            if (!readImage(i == 0, m, n)) {
              return;
            }
          }
          catch (Exception localException)
          {
            return;
          }
          j++;
          i++;
          break;
        case -1: 
        default: 
          if (j == 0) {
            return;
          }
          break;
        }
        if ((k == 0) || (k-- >= 0))
        {
          try
          {
            if (curframe != null)
            {
              curframe.dispose();
              curframe = null;
            }
            input.reset();
            saved_image = null;
            saved_model = null;
            j = 0;
          }
          catch (IOException localIOException)
          {
            return;
          }
        }
        else
        {
          imageComplete(3, true);
          return;
        }
      }
    }
    finally
    {
      close();
    }
  }
  
  private void readHeader()
    throws IOException, ImageFormatException
  {
    byte[] arrayOfByte = new byte[13];
    if (readBytes(arrayOfByte, 0, 13) != 0) {
      throw new IOException();
    }
    if ((arrayOfByte[0] != 71) || (arrayOfByte[1] != 73) || (arrayOfByte[2] != 70)) {
      error("not a GIF file.");
    }
    global_width = ExtractWord(arrayOfByte, 6);
    global_height = ExtractWord(arrayOfByte, 8);
    int i = ExtractByte(arrayOfByte, 10);
    if ((i & 0x80) == 0)
    {
      num_global_colors = 2;
      global_bgpixel = 0;
      global_colormap = new byte[6];
      global_colormap[0] = (global_colormap[1] = global_colormap[2] = 0);
      global_colormap[3] = (global_colormap[4] = global_colormap[5] = -1);
    }
    else
    {
      num_global_colors = (1 << (i & 0x7) + 1);
      global_bgpixel = ExtractByte(arrayOfByte, 11);
      if (arrayOfByte[12] != 0) {
        props.put("aspectratio", "" + (ExtractByte(arrayOfByte, 12) + 15) / 64.0D);
      }
      global_colormap = new byte[num_global_colors * 3];
      if (readBytes(global_colormap, 0, num_global_colors * 3) != 0) {
        throw new IOException();
      }
    }
    input.mark(Integer.MAX_VALUE);
  }
  
  private static native void initIDs();
  
  private native boolean parseImage(int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean, int paramInt5, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, IndexColorModel paramIndexColorModel);
  
  private int sendPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, byte[] paramArrayOfByte, ColorModel paramColorModel)
  {
    if (paramInt2 < 0)
    {
      paramInt4 += paramInt2;
      paramInt2 = 0;
    }
    if (paramInt2 + paramInt4 > global_height) {
      paramInt4 = global_height - paramInt2;
    }
    if (paramInt4 <= 0) {
      return 1;
    }
    int i;
    int k;
    if (paramInt1 < 0)
    {
      i = -paramInt1;
      paramInt3 += paramInt1;
      k = 0;
    }
    else
    {
      i = 0;
      k = paramInt1;
    }
    if (k + paramInt3 > global_width) {
      paramInt3 = global_width - k;
    }
    if (paramInt3 <= 0) {
      return 1;
    }
    int j = i + paramInt3;
    int m = paramInt2 * global_width + k;
    int n = curframe.disposal_method == 1 ? 1 : 0;
    if ((trans_pixel >= 0) && (!curframe.initialframe))
    {
      int i2;
      if ((saved_image != null) && (paramColorModel.equals(saved_model)))
      {
        i1 = i;
        while (i1 < j)
        {
          i2 = paramArrayOfByte[i1];
          if ((i2 & 0xFF) == trans_pixel) {
            paramArrayOfByte[i1] = saved_image[m];
          } else if (n != 0) {
            saved_image[m] = i2;
          }
          i1++;
          m++;
        }
      }
      else
      {
        i1 = -1;
        i2 = 1;
        int i3 = i;
        while (i3 < j)
        {
          int i4 = paramArrayOfByte[i3];
          if ((i4 & 0xFF) == trans_pixel)
          {
            if (i1 >= 0)
            {
              i2 = setPixels(paramInt1 + i1, paramInt2, i3 - i1, 1, paramColorModel, paramArrayOfByte, i1, 0);
              if (i2 == 0) {
                break;
              }
            }
            i1 = -1;
          }
          else
          {
            if (i1 < 0) {
              i1 = i3;
            }
            if (n != 0) {
              saved_image[m] = i4;
            }
          }
          i3++;
          m++;
        }
        if (i1 >= 0) {
          i2 = setPixels(paramInt1 + i1, paramInt2, j - i1, 1, paramColorModel, paramArrayOfByte, i1, 0);
        }
        return i2;
      }
    }
    else if (n != 0)
    {
      System.arraycopy(paramArrayOfByte, i, saved_image, m, paramInt3);
    }
    int i1 = setPixels(k, paramInt2, paramInt3, paramInt4, paramColorModel, paramArrayOfByte, i, 0);
    return i1;
  }
  
  private boolean readImage(boolean paramBoolean, int paramInt1, int paramInt2)
    throws IOException
  {
    if ((curframe != null) && (!curframe.dispose()))
    {
      abort();
      return false;
    }
    long l = 0L;
    byte[] arrayOfByte1 = new byte['ă'];
    if (readBytes(arrayOfByte1, 0, 10) != 0) {
      throw new IOException();
    }
    int i = ExtractWord(arrayOfByte1, 0);
    int j = ExtractWord(arrayOfByte1, 2);
    int k = ExtractWord(arrayOfByte1, 4);
    int m = ExtractWord(arrayOfByte1, 6);
    if ((k == 0) && (global_width != 0)) {
      k = global_width - i;
    }
    if ((m == 0) && (global_height != 0)) {
      m = global_height - j;
    }
    boolean bool1 = (arrayOfByte1[8] & 0x40) != 0;
    IndexColorModel localIndexColorModel = global_model;
    if ((arrayOfByte1[8] & 0x80) != 0)
    {
      n = 1 << (arrayOfByte1[8] & 0x7) + 1;
      arrayOfByte2 = new byte[n * 3];
      arrayOfByte2[0] = arrayOfByte1[9];
      if (readBytes(arrayOfByte2, 1, n * 3 - 1) != 0) {
        throw new IOException();
      }
      if (readBytes(arrayOfByte1, 9, 1) != 0) {
        throw new IOException();
      }
      if (trans_pixel >= n)
      {
        n = trans_pixel + 1;
        arrayOfByte2 = grow_colormap(arrayOfByte2, n);
      }
      localIndexColorModel = new IndexColorModel(8, n, arrayOfByte2, 0, false, trans_pixel);
    }
    else if ((localIndexColorModel == null) || (trans_pixel != localIndexColorModel.getTransparentPixel()))
    {
      if (trans_pixel >= num_global_colors)
      {
        num_global_colors = (trans_pixel + 1);
        global_colormap = grow_colormap(global_colormap, num_global_colors);
      }
      localIndexColorModel = new IndexColorModel(8, num_global_colors, global_colormap, 0, false, trans_pixel);
      global_model = localIndexColorModel;
    }
    if (paramBoolean)
    {
      if (global_width == 0) {
        global_width = k;
      }
      if (global_height == 0) {
        global_height = m;
      }
      setDimensions(global_width, global_height);
      setProperties(props);
      setColorModel(localIndexColorModel);
      headerComplete();
    }
    if ((paramInt1 == 1) && (saved_image == null))
    {
      saved_image = new byte[global_width * global_height];
      if ((m < global_height) && (localIndexColorModel != null))
      {
        n = (byte)localIndexColorModel.getTransparentPixel();
        if (n >= 0)
        {
          arrayOfByte2 = new byte[global_width];
          for (i1 = 0; i1 < global_width; i1++) {
            arrayOfByte2[i1] = n;
          }
          setPixels(0, 0, global_width, j, localIndexColorModel, arrayOfByte2, 0, 0);
          setPixels(0, j + m, global_width, global_height - m - j, localIndexColorModel, arrayOfByte2, 0, 0);
        }
      }
    }
    int n = bool1 ? 29 : 30;
    setHints(n);
    curframe = new GifFrame(this, paramInt1, paramInt2, curframe == null, localIndexColorModel, i, j, k, m);
    byte[] arrayOfByte2 = new byte[k];
    int i1 = ExtractByte(arrayOfByte1, 9);
    if (i1 >= 12) {
      return false;
    }
    boolean bool2 = parseImage(i, j, k, m, bool1, i1, arrayOfByte1, arrayOfByte2, localIndexColorModel);
    if (!bool2) {
      abort();
    }
    return bool2;
  }
  
  public static byte[] grow_colormap(byte[] paramArrayOfByte, int paramInt)
  {
    byte[] arrayOfByte = new byte[paramInt * 3];
    System.arraycopy(paramArrayOfByte, 0, arrayOfByte, 0, paramArrayOfByte.length);
    return arrayOfByte;
  }
  
  static
  {
    NativeLibLoader.loadLibraries();
    initIDs();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\image\GifImageDecoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
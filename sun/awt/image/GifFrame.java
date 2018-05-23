package sun.awt.image;

import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;

class GifFrame
{
  private static final boolean verbose = false;
  private static IndexColorModel trans_model;
  static final int DISPOSAL_NONE = 0;
  static final int DISPOSAL_SAVE = 1;
  static final int DISPOSAL_BGCOLOR = 2;
  static final int DISPOSAL_PREVIOUS = 3;
  GifImageDecoder decoder;
  int disposal_method;
  int delay;
  IndexColorModel model;
  int x;
  int y;
  int width;
  int height;
  boolean initialframe;
  
  public GifFrame(GifImageDecoder paramGifImageDecoder, int paramInt1, int paramInt2, boolean paramBoolean, IndexColorModel paramIndexColorModel, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    decoder = paramGifImageDecoder;
    disposal_method = paramInt1;
    delay = paramInt2;
    model = paramIndexColorModel;
    initialframe = paramBoolean;
    x = paramInt3;
    y = paramInt4;
    width = paramInt5;
    height = paramInt6;
  }
  
  private void setPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, ColorModel paramColorModel, byte[] paramArrayOfByte, int paramInt5, int paramInt6)
  {
    decoder.setPixels(paramInt1, paramInt2, paramInt3, paramInt4, paramColorModel, paramArrayOfByte, paramInt5, paramInt6);
  }
  
  public boolean dispose()
  {
    if (decoder.imageComplete(2, false) == 0) {
      return false;
    }
    if (delay > 0) {
      try
      {
        Thread.sleep(delay);
      }
      catch (InterruptedException localInterruptedException)
      {
        return false;
      }
    } else {
      Thread.yield();
    }
    int i = decoder.global_width;
    int j = decoder.global_height;
    if (x < 0)
    {
      width += x;
      x = 0;
    }
    if (x + width > i) {
      width = (i - x);
    }
    if (width <= 0)
    {
      disposal_method = 0;
    }
    else
    {
      if (y < 0)
      {
        height += y;
        y = 0;
      }
      if (y + height > j) {
        height = (j - y);
      }
      if (height <= 0) {
        disposal_method = 0;
      }
    }
    switch (disposal_method)
    {
    case 3: 
      byte[] arrayOfByte1 = decoder.saved_image;
      IndexColorModel localIndexColorModel = decoder.saved_model;
      if (arrayOfByte1 != null) {
        setPixels(x, y, width, height, localIndexColorModel, arrayOfByte1, y * i + x, i);
      }
      break;
    case 2: 
      int k;
      if (model.getTransparentPixel() < 0)
      {
        model = trans_model;
        if (model == null)
        {
          model = new IndexColorModel(8, 1, new byte[4], 0, true);
          trans_model = model;
        }
        k = 0;
      }
      else
      {
        k = (byte)model.getTransparentPixel();
      }
      byte[] arrayOfByte2 = new byte[width];
      int m;
      if (k != 0) {
        for (m = 0; m < width; m++) {
          arrayOfByte2[m] = k;
        }
      }
      if (decoder.saved_image != null) {
        for (m = 0; m < i * j; m++) {
          decoder.saved_image[m] = k;
        }
      }
      setPixels(x, y, width, height, model, arrayOfByte2, 0, 0);
      break;
    case 1: 
      decoder.saved_model = model;
    }
    return true;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\image\GifFrame.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
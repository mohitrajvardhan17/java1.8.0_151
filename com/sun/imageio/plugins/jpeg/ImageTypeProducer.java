package com.sun.imageio.plugins.jpeg;

import javax.imageio.ImageTypeSpecifier;

class ImageTypeProducer
{
  private ImageTypeSpecifier type = null;
  boolean failed = false;
  private int csCode;
  private static final ImageTypeProducer[] defaultTypes = new ImageTypeProducer[12];
  
  public ImageTypeProducer(int paramInt)
  {
    csCode = paramInt;
  }
  
  public ImageTypeProducer()
  {
    csCode = -1;
  }
  
  public synchronized ImageTypeSpecifier getType()
  {
    if ((!failed) && (type == null)) {
      try
      {
        type = produce();
      }
      catch (Throwable localThrowable)
      {
        failed = true;
      }
    }
    return type;
  }
  
  public static synchronized ImageTypeProducer getTypeProducer(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= 12)) {
      return null;
    }
    if (defaultTypes[paramInt] == null) {
      defaultTypes[paramInt] = new ImageTypeProducer(paramInt);
    }
    return defaultTypes[paramInt];
  }
  
  protected ImageTypeSpecifier produce()
  {
    switch (csCode)
    {
    case 1: 
      return ImageTypeSpecifier.createFromBufferedImageType(10);
    case 2: 
      return ImageTypeSpecifier.createInterleaved(JPEG.JCS.sRGB, JPEG.bOffsRGB, 0, false, false);
    case 6: 
      return ImageTypeSpecifier.createPacked(JPEG.JCS.sRGB, -16777216, 16711680, 65280, 255, 3, false);
    case 5: 
      if (JPEG.JCS.getYCC() != null) {
        return ImageTypeSpecifier.createInterleaved(JPEG.JCS.getYCC(), JPEG.bandOffsets[2], 0, false, false);
      }
      return null;
    case 10: 
      if (JPEG.JCS.getYCC() != null) {
        return ImageTypeSpecifier.createInterleaved(JPEG.JCS.getYCC(), JPEG.bandOffsets[3], 0, true, false);
      }
      return null;
    }
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\imageio\plugins\jpeg\ImageTypeProducer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
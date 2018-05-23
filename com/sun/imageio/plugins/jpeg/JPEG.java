package com.sun.imageio.plugins.jpeg;

import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.image.ColorModel;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.plugins.jpeg.JPEGHuffmanTable;
import javax.imageio.plugins.jpeg.JPEGQTable;

public class JPEG
{
  public static final int TEM = 1;
  public static final int SOF0 = 192;
  public static final int SOF1 = 193;
  public static final int SOF2 = 194;
  public static final int SOF3 = 195;
  public static final int DHT = 196;
  public static final int SOF5 = 197;
  public static final int SOF6 = 198;
  public static final int SOF7 = 199;
  public static final int JPG = 200;
  public static final int SOF9 = 201;
  public static final int SOF10 = 202;
  public static final int SOF11 = 203;
  public static final int DAC = 204;
  public static final int SOF13 = 205;
  public static final int SOF14 = 206;
  public static final int SOF15 = 207;
  public static final int RST0 = 208;
  public static final int RST1 = 209;
  public static final int RST2 = 210;
  public static final int RST3 = 211;
  public static final int RST4 = 212;
  public static final int RST5 = 213;
  public static final int RST6 = 214;
  public static final int RST7 = 215;
  public static final int RESTART_RANGE = 8;
  public static final int SOI = 216;
  public static final int EOI = 217;
  public static final int SOS = 218;
  public static final int DQT = 219;
  public static final int DNL = 220;
  public static final int DRI = 221;
  public static final int DHP = 222;
  public static final int EXP = 223;
  public static final int APP0 = 224;
  public static final int APP1 = 225;
  public static final int APP2 = 226;
  public static final int APP3 = 227;
  public static final int APP4 = 228;
  public static final int APP5 = 229;
  public static final int APP6 = 230;
  public static final int APP7 = 231;
  public static final int APP8 = 232;
  public static final int APP9 = 233;
  public static final int APP10 = 234;
  public static final int APP11 = 235;
  public static final int APP12 = 236;
  public static final int APP13 = 237;
  public static final int APP14 = 238;
  public static final int APP15 = 239;
  public static final int COM = 254;
  public static final int DENSITY_UNIT_ASPECT_RATIO = 0;
  public static final int DENSITY_UNIT_DOTS_INCH = 1;
  public static final int DENSITY_UNIT_DOTS_CM = 2;
  public static final int NUM_DENSITY_UNIT = 3;
  public static final int ADOBE_IMPOSSIBLE = -1;
  public static final int ADOBE_UNKNOWN = 0;
  public static final int ADOBE_YCC = 1;
  public static final int ADOBE_YCCK = 2;
  public static final String vendor = "Oracle Corporation";
  public static final String version = "0.5";
  static final String[] names = { "JPEG", "jpeg", "JPG", "jpg" };
  static final String[] suffixes = { "jpg", "jpeg" };
  static final String[] MIMETypes = { "image/jpeg" };
  public static final String nativeImageMetadataFormatName = "javax_imageio_jpeg_image_1.0";
  public static final String nativeImageMetadataFormatClassName = "com.sun.imageio.plugins.jpeg.JPEGImageMetadataFormat";
  public static final String nativeStreamMetadataFormatName = "javax_imageio_jpeg_stream_1.0";
  public static final String nativeStreamMetadataFormatClassName = "com.sun.imageio.plugins.jpeg.JPEGStreamMetadataFormat";
  public static final int JCS_UNKNOWN = 0;
  public static final int JCS_GRAYSCALE = 1;
  public static final int JCS_RGB = 2;
  public static final int JCS_YCbCr = 3;
  public static final int JCS_CMYK = 4;
  public static final int JCS_YCC = 5;
  public static final int JCS_RGBA = 6;
  public static final int JCS_YCbCrA = 7;
  public static final int JCS_YCCA = 10;
  public static final int JCS_YCCK = 11;
  public static final int NUM_JCS_CODES = 12;
  static final int[][] bandOffsets = { { 0 }, { 0, 1 }, { 0, 1, 2 }, { 0, 1, 2, 3 } };
  static final int[] bOffsRGB = { 2, 1, 0 };
  public static final float DEFAULT_QUALITY = 0.75F;
  
  public JPEG() {}
  
  static boolean isNonStandardICC(ColorSpace paramColorSpace)
  {
    boolean bool = false;
    if (((paramColorSpace instanceof ICC_ColorSpace)) && (!paramColorSpace.isCS_sRGB()) && (!paramColorSpace.equals(ColorSpace.getInstance(1001))) && (!paramColorSpace.equals(ColorSpace.getInstance(1003))) && (!paramColorSpace.equals(ColorSpace.getInstance(1004))) && (!paramColorSpace.equals(ColorSpace.getInstance(1002)))) {
      bool = true;
    }
    return bool;
  }
  
  static boolean isJFIFcompliant(ImageTypeSpecifier paramImageTypeSpecifier, boolean paramBoolean)
  {
    ColorModel localColorModel = paramImageTypeSpecifier.getColorModel();
    if (localColorModel.hasAlpha()) {
      return false;
    }
    int i = paramImageTypeSpecifier.getNumComponents();
    if (i == 1) {
      return true;
    }
    if (i != 3) {
      return false;
    }
    if (paramBoolean)
    {
      if (localColorModel.getColorSpace().getType() == 5) {
        return true;
      }
    }
    else if (localColorModel.getColorSpace().getType() == 3) {
      return true;
    }
    return false;
  }
  
  static int transformForType(ImageTypeSpecifier paramImageTypeSpecifier, boolean paramBoolean)
  {
    int i = -1;
    ColorModel localColorModel = paramImageTypeSpecifier.getColorModel();
    switch (localColorModel.getColorSpace().getType())
    {
    case 6: 
      i = 0;
      break;
    case 5: 
      i = paramBoolean ? 1 : 0;
      break;
    case 3: 
      i = 1;
      break;
    case 9: 
      i = paramBoolean ? 2 : -1;
    }
    return i;
  }
  
  static float convertToLinearQuality(float paramFloat)
  {
    if (paramFloat <= 0.0F) {
      paramFloat = 0.01F;
    }
    if (paramFloat > 1.0F) {
      paramFloat = 1.0F;
    }
    if (paramFloat < 0.5F) {
      paramFloat = 0.5F / paramFloat;
    } else {
      paramFloat = 2.0F - paramFloat * 2.0F;
    }
    return paramFloat;
  }
  
  static JPEGQTable[] getDefaultQTables()
  {
    JPEGQTable[] arrayOfJPEGQTable = new JPEGQTable[2];
    arrayOfJPEGQTable[0] = JPEGQTable.K1Div2Luminance;
    arrayOfJPEGQTable[1] = JPEGQTable.K2Div2Chrominance;
    return arrayOfJPEGQTable;
  }
  
  static JPEGHuffmanTable[] getDefaultHuffmanTables(boolean paramBoolean)
  {
    JPEGHuffmanTable[] arrayOfJPEGHuffmanTable = new JPEGHuffmanTable[2];
    if (paramBoolean)
    {
      arrayOfJPEGHuffmanTable[0] = JPEGHuffmanTable.StdDCLuminance;
      arrayOfJPEGHuffmanTable[1] = JPEGHuffmanTable.StdDCChrominance;
    }
    else
    {
      arrayOfJPEGHuffmanTable[0] = JPEGHuffmanTable.StdACLuminance;
      arrayOfJPEGHuffmanTable[1] = JPEGHuffmanTable.StdACChrominance;
    }
    return arrayOfJPEGHuffmanTable;
  }
  
  public static class JCS
  {
    public static final ColorSpace sRGB = ColorSpace.getInstance(1000);
    private static ColorSpace YCC = null;
    private static boolean yccInited = false;
    
    public JCS() {}
    
    /* Error */
    public static ColorSpace getYCC()
    {
      // Byte code:
      //   0: getstatic 33	com/sun/imageio/plugins/jpeg/JPEG$JCS:yccInited	Z
      //   3: ifne +34 -> 37
      //   6: sipush 1002
      //   9: invokestatic 36	java/awt/color/ColorSpace:getInstance	(I)Ljava/awt/color/ColorSpace;
      //   12: putstatic 34	com/sun/imageio/plugins/jpeg/JPEG$JCS:YCC	Ljava/awt/color/ColorSpace;
      //   15: iconst_1
      //   16: putstatic 33	com/sun/imageio/plugins/jpeg/JPEG$JCS:yccInited	Z
      //   19: goto +18 -> 37
      //   22: astore_0
      //   23: iconst_1
      //   24: putstatic 33	com/sun/imageio/plugins/jpeg/JPEG$JCS:yccInited	Z
      //   27: goto +10 -> 37
      //   30: astore_1
      //   31: iconst_1
      //   32: putstatic 33	com/sun/imageio/plugins/jpeg/JPEG$JCS:yccInited	Z
      //   35: aload_1
      //   36: athrow
      //   37: getstatic 34	com/sun/imageio/plugins/jpeg/JPEG$JCS:YCC	Ljava/awt/color/ColorSpace;
      //   40: areturn
      // Local variable table:
      //   start	length	slot	name	signature
      //   22	1	0	localIllegalArgumentException	IllegalArgumentException
      //   30	6	1	localObject	Object
      // Exception table:
      //   from	to	target	type
      //   6	15	22	java/lang/IllegalArgumentException
      //   6	15	30	finally
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\imageio\plugins\jpeg\JPEG.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
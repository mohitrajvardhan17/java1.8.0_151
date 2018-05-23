package com.sun.imageio.plugins.bmp;

public class BMPCompressionTypes
{
  private static final String[] compressionTypeNames = { "BI_RGB", "BI_RLE8", "BI_RLE4", "BI_BITFIELDS", "BI_JPEG", "BI_PNG" };
  
  public BMPCompressionTypes() {}
  
  static int getType(String paramString)
  {
    for (int i = 0; i < compressionTypeNames.length; i++) {
      if (compressionTypeNames[i].equals(paramString)) {
        return i;
      }
    }
    return 0;
  }
  
  static String getName(int paramInt)
  {
    return compressionTypeNames[paramInt];
  }
  
  public static String[] getCompressionTypes()
  {
    return (String[])compressionTypeNames.clone();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\imageio\plugins\bmp\BMPCompressionTypes.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
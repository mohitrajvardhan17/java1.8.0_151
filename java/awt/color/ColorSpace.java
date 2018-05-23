package java.awt.color;

import java.io.Serializable;

public abstract class ColorSpace
  implements Serializable
{
  static final long serialVersionUID = -409452704308689724L;
  private int type;
  private int numComponents;
  private transient String[] compName = null;
  private static ColorSpace sRGBspace;
  private static ColorSpace XYZspace;
  private static ColorSpace PYCCspace;
  private static ColorSpace GRAYspace;
  private static ColorSpace LINEAR_RGBspace;
  public static final int TYPE_XYZ = 0;
  public static final int TYPE_Lab = 1;
  public static final int TYPE_Luv = 2;
  public static final int TYPE_YCbCr = 3;
  public static final int TYPE_Yxy = 4;
  public static final int TYPE_RGB = 5;
  public static final int TYPE_GRAY = 6;
  public static final int TYPE_HSV = 7;
  public static final int TYPE_HLS = 8;
  public static final int TYPE_CMYK = 9;
  public static final int TYPE_CMY = 11;
  public static final int TYPE_2CLR = 12;
  public static final int TYPE_3CLR = 13;
  public static final int TYPE_4CLR = 14;
  public static final int TYPE_5CLR = 15;
  public static final int TYPE_6CLR = 16;
  public static final int TYPE_7CLR = 17;
  public static final int TYPE_8CLR = 18;
  public static final int TYPE_9CLR = 19;
  public static final int TYPE_ACLR = 20;
  public static final int TYPE_BCLR = 21;
  public static final int TYPE_CCLR = 22;
  public static final int TYPE_DCLR = 23;
  public static final int TYPE_ECLR = 24;
  public static final int TYPE_FCLR = 25;
  public static final int CS_sRGB = 1000;
  public static final int CS_LINEAR_RGB = 1004;
  public static final int CS_CIEXYZ = 1001;
  public static final int CS_PYCC = 1002;
  public static final int CS_GRAY = 1003;
  
  protected ColorSpace(int paramInt1, int paramInt2)
  {
    type = paramInt1;
    numComponents = paramInt2;
  }
  
  public static ColorSpace getInstance(int paramInt)
  {
    ICC_Profile localICC_Profile;
    ColorSpace localColorSpace;
    switch (paramInt)
    {
    case 1000: 
      synchronized (ColorSpace.class)
      {
        if (sRGBspace == null)
        {
          localICC_Profile = ICC_Profile.getInstance(1000);
          sRGBspace = new ICC_ColorSpace(localICC_Profile);
        }
        localColorSpace = sRGBspace;
      }
      break;
    case 1001: 
      synchronized (ColorSpace.class)
      {
        if (XYZspace == null)
        {
          localICC_Profile = ICC_Profile.getInstance(1001);
          XYZspace = new ICC_ColorSpace(localICC_Profile);
        }
        localColorSpace = XYZspace;
      }
      break;
    case 1002: 
      synchronized (ColorSpace.class)
      {
        if (PYCCspace == null)
        {
          localICC_Profile = ICC_Profile.getInstance(1002);
          PYCCspace = new ICC_ColorSpace(localICC_Profile);
        }
        localColorSpace = PYCCspace;
      }
      break;
    case 1003: 
      synchronized (ColorSpace.class)
      {
        if (GRAYspace == null)
        {
          localICC_Profile = ICC_Profile.getInstance(1003);
          GRAYspace = new ICC_ColorSpace(localICC_Profile);
          sun.java2d.cmm.CMSManager.GRAYspace = GRAYspace;
        }
        localColorSpace = GRAYspace;
      }
      break;
    case 1004: 
      synchronized (ColorSpace.class)
      {
        if (LINEAR_RGBspace == null)
        {
          localICC_Profile = ICC_Profile.getInstance(1004);
          LINEAR_RGBspace = new ICC_ColorSpace(localICC_Profile);
          sun.java2d.cmm.CMSManager.LINEAR_RGBspace = LINEAR_RGBspace;
        }
        localColorSpace = LINEAR_RGBspace;
      }
      break;
    default: 
      throw new IllegalArgumentException("Unknown color space");
    }
    return localColorSpace;
  }
  
  public boolean isCS_sRGB()
  {
    return this == sRGBspace;
  }
  
  public abstract float[] toRGB(float[] paramArrayOfFloat);
  
  public abstract float[] fromRGB(float[] paramArrayOfFloat);
  
  public abstract float[] toCIEXYZ(float[] paramArrayOfFloat);
  
  public abstract float[] fromCIEXYZ(float[] paramArrayOfFloat);
  
  public int getType()
  {
    return type;
  }
  
  public int getNumComponents()
  {
    return numComponents;
  }
  
  public String getName(int paramInt)
  {
    if ((paramInt < 0) || (paramInt > numComponents - 1)) {
      throw new IllegalArgumentException("Component index out of range: " + paramInt);
    }
    if (compName == null) {
      switch (type)
      {
      case 0: 
        compName = new String[] { "X", "Y", "Z" };
        break;
      case 1: 
        compName = new String[] { "L", "a", "b" };
        break;
      case 2: 
        compName = new String[] { "L", "u", "v" };
        break;
      case 3: 
        compName = new String[] { "Y", "Cb", "Cr" };
        break;
      case 4: 
        compName = new String[] { "Y", "x", "y" };
        break;
      case 5: 
        compName = new String[] { "Red", "Green", "Blue" };
        break;
      case 6: 
        compName = new String[] { "Gray" };
        break;
      case 7: 
        compName = new String[] { "Hue", "Saturation", "Value" };
        break;
      case 8: 
        compName = new String[] { "Hue", "Lightness", "Saturation" };
        break;
      case 9: 
        compName = new String[] { "Cyan", "Magenta", "Yellow", "Black" };
        break;
      case 11: 
        compName = new String[] { "Cyan", "Magenta", "Yellow" };
        break;
      case 10: 
      default: 
        String[] arrayOfString = new String[numComponents];
        for (int i = 0; i < arrayOfString.length; i++) {
          arrayOfString[i] = ("Unnamed color component(" + i + ")");
        }
        compName = arrayOfString;
      }
    }
    return compName[paramInt];
  }
  
  public float getMinValue(int paramInt)
  {
    if ((paramInt < 0) || (paramInt > numComponents - 1)) {
      throw new IllegalArgumentException("Component index out of range: " + paramInt);
    }
    return 0.0F;
  }
  
  public float getMaxValue(int paramInt)
  {
    if ((paramInt < 0) || (paramInt > numComponents - 1)) {
      throw new IllegalArgumentException("Component index out of range: " + paramInt);
    }
    return 1.0F;
  }
  
  static boolean isCS_CIEXYZ(ColorSpace paramColorSpace)
  {
    return paramColorSpace == XYZspace;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\color\ColorSpace.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
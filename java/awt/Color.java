package java.awt;

import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;
import java.beans.ConstructorProperties;
import java.io.Serializable;

public class Color
  implements Paint, Serializable
{
  public static final Color white = new Color(255, 255, 255);
  public static final Color WHITE = white;
  public static final Color lightGray = new Color(192, 192, 192);
  public static final Color LIGHT_GRAY = lightGray;
  public static final Color gray = new Color(128, 128, 128);
  public static final Color GRAY = gray;
  public static final Color darkGray = new Color(64, 64, 64);
  public static final Color DARK_GRAY = darkGray;
  public static final Color black = new Color(0, 0, 0);
  public static final Color BLACK = black;
  public static final Color red = new Color(255, 0, 0);
  public static final Color RED = red;
  public static final Color pink = new Color(255, 175, 175);
  public static final Color PINK = pink;
  public static final Color orange = new Color(255, 200, 0);
  public static final Color ORANGE = orange;
  public static final Color yellow = new Color(255, 255, 0);
  public static final Color YELLOW = yellow;
  public static final Color green = new Color(0, 255, 0);
  public static final Color GREEN = green;
  public static final Color magenta = new Color(255, 0, 255);
  public static final Color MAGENTA = magenta;
  public static final Color cyan = new Color(0, 255, 255);
  public static final Color CYAN = cyan;
  public static final Color blue = new Color(0, 0, 255);
  public static final Color BLUE = blue;
  int value;
  private float[] frgbvalue = null;
  private float[] fvalue = null;
  private float falpha = 0.0F;
  private ColorSpace cs = null;
  private static final long serialVersionUID = 118526816881161077L;
  private static final double FACTOR = 0.7D;
  
  private static native void initIDs();
  
  private static void testColorValueRange(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int i = 0;
    String str = "";
    if ((paramInt4 < 0) || (paramInt4 > 255))
    {
      i = 1;
      str = str + " Alpha";
    }
    if ((paramInt1 < 0) || (paramInt1 > 255))
    {
      i = 1;
      str = str + " Red";
    }
    if ((paramInt2 < 0) || (paramInt2 > 255))
    {
      i = 1;
      str = str + " Green";
    }
    if ((paramInt3 < 0) || (paramInt3 > 255))
    {
      i = 1;
      str = str + " Blue";
    }
    if (i == 1) {
      throw new IllegalArgumentException("Color parameter outside of expected range:" + str);
    }
  }
  
  private static void testColorValueRange(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    int i = 0;
    String str = "";
    if ((paramFloat4 < 0.0D) || (paramFloat4 > 1.0D))
    {
      i = 1;
      str = str + " Alpha";
    }
    if ((paramFloat1 < 0.0D) || (paramFloat1 > 1.0D))
    {
      i = 1;
      str = str + " Red";
    }
    if ((paramFloat2 < 0.0D) || (paramFloat2 > 1.0D))
    {
      i = 1;
      str = str + " Green";
    }
    if ((paramFloat3 < 0.0D) || (paramFloat3 > 1.0D))
    {
      i = 1;
      str = str + " Blue";
    }
    if (i == 1) {
      throw new IllegalArgumentException("Color parameter outside of expected range:" + str);
    }
  }
  
  public Color(int paramInt1, int paramInt2, int paramInt3)
  {
    this(paramInt1, paramInt2, paramInt3, 255);
  }
  
  @ConstructorProperties({"red", "green", "blue", "alpha"})
  public Color(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    value = ((paramInt4 & 0xFF) << 24 | (paramInt1 & 0xFF) << 16 | (paramInt2 & 0xFF) << 8 | (paramInt3 & 0xFF) << 0);
    testColorValueRange(paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public Color(int paramInt)
  {
    value = (0xFF000000 | paramInt);
  }
  
  public Color(int paramInt, boolean paramBoolean)
  {
    if (paramBoolean) {
      value = paramInt;
    } else {
      value = (0xFF000000 | paramInt);
    }
  }
  
  public Color(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    this((int)(paramFloat1 * 255.0F + 0.5D), (int)(paramFloat2 * 255.0F + 0.5D), (int)(paramFloat3 * 255.0F + 0.5D));
    testColorValueRange(paramFloat1, paramFloat2, paramFloat3, 1.0F);
    frgbvalue[0] = paramFloat1;
    frgbvalue[1] = paramFloat2;
    frgbvalue[2] = paramFloat3;
    falpha = 1.0F;
  }
  
  public Color(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    this((int)(paramFloat1 * 255.0F + 0.5D), (int)(paramFloat2 * 255.0F + 0.5D), (int)(paramFloat3 * 255.0F + 0.5D), (int)(paramFloat4 * 255.0F + 0.5D));
    frgbvalue[0] = paramFloat1;
    frgbvalue[1] = paramFloat2;
    frgbvalue[2] = paramFloat3;
    falpha = paramFloat4;
  }
  
  public Color(ColorSpace paramColorSpace, float[] paramArrayOfFloat, float paramFloat)
  {
    int i = 0;
    String str = "";
    int j = paramColorSpace.getNumComponents();
    fvalue = new float[j];
    for (int k = 0; k < j; k++) {
      if ((paramArrayOfFloat[k] < 0.0D) || (paramArrayOfFloat[k] > 1.0D))
      {
        i = 1;
        str = str + "Component " + k + " ";
      }
      else
      {
        fvalue[k] = paramArrayOfFloat[k];
      }
    }
    if ((paramFloat < 0.0D) || (paramFloat > 1.0D))
    {
      i = 1;
      str = str + "Alpha";
    }
    else
    {
      falpha = paramFloat;
    }
    if (i != 0) {
      throw new IllegalArgumentException("Color parameter outside of expected range: " + str);
    }
    frgbvalue = paramColorSpace.toRGB(fvalue);
    cs = paramColorSpace;
    value = (((int)(falpha * 255.0F) & 0xFF) << 24 | ((int)(frgbvalue[0] * 255.0F) & 0xFF) << 16 | ((int)(frgbvalue[1] * 255.0F) & 0xFF) << 8 | ((int)(frgbvalue[2] * 255.0F) & 0xFF) << 0);
  }
  
  public int getRed()
  {
    return getRGB() >> 16 & 0xFF;
  }
  
  public int getGreen()
  {
    return getRGB() >> 8 & 0xFF;
  }
  
  public int getBlue()
  {
    return getRGB() >> 0 & 0xFF;
  }
  
  public int getAlpha()
  {
    return getRGB() >> 24 & 0xFF;
  }
  
  public int getRGB()
  {
    return value;
  }
  
  public Color brighter()
  {
    int i = getRed();
    int j = getGreen();
    int k = getBlue();
    int m = getAlpha();
    int n = 3;
    if ((i == 0) && (j == 0) && (k == 0)) {
      return new Color(n, n, n, m);
    }
    if ((i > 0) && (i < n)) {
      i = n;
    }
    if ((j > 0) && (j < n)) {
      j = n;
    }
    if ((k > 0) && (k < n)) {
      k = n;
    }
    return new Color(Math.min((int)(i / 0.7D), 255), Math.min((int)(j / 0.7D), 255), Math.min((int)(k / 0.7D), 255), m);
  }
  
  public Color darker()
  {
    return new Color(Math.max((int)(getRed() * 0.7D), 0), Math.max((int)(getGreen() * 0.7D), 0), Math.max((int)(getBlue() * 0.7D), 0), getAlpha());
  }
  
  public int hashCode()
  {
    return value;
  }
  
  public boolean equals(Object paramObject)
  {
    return ((paramObject instanceof Color)) && (((Color)paramObject).getRGB() == getRGB());
  }
  
  public String toString()
  {
    return getClass().getName() + "[r=" + getRed() + ",g=" + getGreen() + ",b=" + getBlue() + "]";
  }
  
  public static Color decode(String paramString)
    throws NumberFormatException
  {
    Integer localInteger = Integer.decode(paramString);
    int i = localInteger.intValue();
    return new Color(i >> 16 & 0xFF, i >> 8 & 0xFF, i & 0xFF);
  }
  
  public static Color getColor(String paramString)
  {
    return getColor(paramString, null);
  }
  
  public static Color getColor(String paramString, Color paramColor)
  {
    Integer localInteger = Integer.getInteger(paramString);
    if (localInteger == null) {
      return paramColor;
    }
    int i = localInteger.intValue();
    return new Color(i >> 16 & 0xFF, i >> 8 & 0xFF, i & 0xFF);
  }
  
  public static Color getColor(String paramString, int paramInt)
  {
    Integer localInteger = Integer.getInteger(paramString);
    int i = localInteger != null ? localInteger.intValue() : paramInt;
    return new Color(i >> 16 & 0xFF, i >> 8 & 0xFF, i >> 0 & 0xFF);
  }
  
  public static int HSBtoRGB(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    int i = 0;
    int j = 0;
    int k = 0;
    if (paramFloat2 == 0.0F)
    {
      i = j = k = (int)(paramFloat3 * 255.0F + 0.5F);
    }
    else
    {
      float f1 = (paramFloat1 - (float)Math.floor(paramFloat1)) * 6.0F;
      float f2 = f1 - (float)Math.floor(f1);
      float f3 = paramFloat3 * (1.0F - paramFloat2);
      float f4 = paramFloat3 * (1.0F - paramFloat2 * f2);
      float f5 = paramFloat3 * (1.0F - paramFloat2 * (1.0F - f2));
      switch ((int)f1)
      {
      case 0: 
        i = (int)(paramFloat3 * 255.0F + 0.5F);
        j = (int)(f5 * 255.0F + 0.5F);
        k = (int)(f3 * 255.0F + 0.5F);
        break;
      case 1: 
        i = (int)(f4 * 255.0F + 0.5F);
        j = (int)(paramFloat3 * 255.0F + 0.5F);
        k = (int)(f3 * 255.0F + 0.5F);
        break;
      case 2: 
        i = (int)(f3 * 255.0F + 0.5F);
        j = (int)(paramFloat3 * 255.0F + 0.5F);
        k = (int)(f5 * 255.0F + 0.5F);
        break;
      case 3: 
        i = (int)(f3 * 255.0F + 0.5F);
        j = (int)(f4 * 255.0F + 0.5F);
        k = (int)(paramFloat3 * 255.0F + 0.5F);
        break;
      case 4: 
        i = (int)(f5 * 255.0F + 0.5F);
        j = (int)(f3 * 255.0F + 0.5F);
        k = (int)(paramFloat3 * 255.0F + 0.5F);
        break;
      case 5: 
        i = (int)(paramFloat3 * 255.0F + 0.5F);
        j = (int)(f3 * 255.0F + 0.5F);
        k = (int)(f4 * 255.0F + 0.5F);
      }
    }
    return 0xFF000000 | i << 16 | j << 8 | k << 0;
  }
  
  public static float[] RGBtoHSB(int paramInt1, int paramInt2, int paramInt3, float[] paramArrayOfFloat)
  {
    if (paramArrayOfFloat == null) {
      paramArrayOfFloat = new float[3];
    }
    int i = paramInt1 > paramInt2 ? paramInt1 : paramInt2;
    if (paramInt3 > i) {
      i = paramInt3;
    }
    int j = paramInt1 < paramInt2 ? paramInt1 : paramInt2;
    if (paramInt3 < j) {
      j = paramInt3;
    }
    float f3 = i / 255.0F;
    float f2;
    if (i != 0) {
      f2 = (i - j) / i;
    } else {
      f2 = 0.0F;
    }
    float f1;
    if (f2 == 0.0F)
    {
      f1 = 0.0F;
    }
    else
    {
      float f4 = (i - paramInt1) / (i - j);
      float f5 = (i - paramInt2) / (i - j);
      float f6 = (i - paramInt3) / (i - j);
      if (paramInt1 == i) {
        f1 = f6 - f5;
      } else if (paramInt2 == i) {
        f1 = 2.0F + f4 - f6;
      } else {
        f1 = 4.0F + f5 - f4;
      }
      f1 /= 6.0F;
      if (f1 < 0.0F) {
        f1 += 1.0F;
      }
    }
    paramArrayOfFloat[0] = f1;
    paramArrayOfFloat[1] = f2;
    paramArrayOfFloat[2] = f3;
    return paramArrayOfFloat;
  }
  
  public static Color getHSBColor(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    return new Color(HSBtoRGB(paramFloat1, paramFloat2, paramFloat3));
  }
  
  public float[] getRGBComponents(float[] paramArrayOfFloat)
  {
    float[] arrayOfFloat;
    if (paramArrayOfFloat == null) {
      arrayOfFloat = new float[4];
    } else {
      arrayOfFloat = paramArrayOfFloat;
    }
    if (frgbvalue == null)
    {
      arrayOfFloat[0] = (getRed() / 255.0F);
      arrayOfFloat[1] = (getGreen() / 255.0F);
      arrayOfFloat[2] = (getBlue() / 255.0F);
      arrayOfFloat[3] = (getAlpha() / 255.0F);
    }
    else
    {
      arrayOfFloat[0] = frgbvalue[0];
      arrayOfFloat[1] = frgbvalue[1];
      arrayOfFloat[2] = frgbvalue[2];
      arrayOfFloat[3] = falpha;
    }
    return arrayOfFloat;
  }
  
  public float[] getRGBColorComponents(float[] paramArrayOfFloat)
  {
    float[] arrayOfFloat;
    if (paramArrayOfFloat == null) {
      arrayOfFloat = new float[3];
    } else {
      arrayOfFloat = paramArrayOfFloat;
    }
    if (frgbvalue == null)
    {
      arrayOfFloat[0] = (getRed() / 255.0F);
      arrayOfFloat[1] = (getGreen() / 255.0F);
      arrayOfFloat[2] = (getBlue() / 255.0F);
    }
    else
    {
      arrayOfFloat[0] = frgbvalue[0];
      arrayOfFloat[1] = frgbvalue[1];
      arrayOfFloat[2] = frgbvalue[2];
    }
    return arrayOfFloat;
  }
  
  public float[] getComponents(float[] paramArrayOfFloat)
  {
    if (fvalue == null) {
      return getRGBComponents(paramArrayOfFloat);
    }
    int i = fvalue.length;
    float[] arrayOfFloat;
    if (paramArrayOfFloat == null) {
      arrayOfFloat = new float[i + 1];
    } else {
      arrayOfFloat = paramArrayOfFloat;
    }
    for (int j = 0; j < i; j++) {
      arrayOfFloat[j] = fvalue[j];
    }
    arrayOfFloat[i] = falpha;
    return arrayOfFloat;
  }
  
  public float[] getColorComponents(float[] paramArrayOfFloat)
  {
    if (fvalue == null) {
      return getRGBColorComponents(paramArrayOfFloat);
    }
    int i = fvalue.length;
    float[] arrayOfFloat;
    if (paramArrayOfFloat == null) {
      arrayOfFloat = new float[i];
    } else {
      arrayOfFloat = paramArrayOfFloat;
    }
    for (int j = 0; j < i; j++) {
      arrayOfFloat[j] = fvalue[j];
    }
    return arrayOfFloat;
  }
  
  public float[] getComponents(ColorSpace paramColorSpace, float[] paramArrayOfFloat)
  {
    if (cs == null) {
      cs = ColorSpace.getInstance(1000);
    }
    float[] arrayOfFloat1;
    if (fvalue == null)
    {
      arrayOfFloat1 = new float[3];
      arrayOfFloat1[0] = (getRed() / 255.0F);
      arrayOfFloat1[1] = (getGreen() / 255.0F);
      arrayOfFloat1[2] = (getBlue() / 255.0F);
    }
    else
    {
      arrayOfFloat1 = fvalue;
    }
    float[] arrayOfFloat2 = cs.toCIEXYZ(arrayOfFloat1);
    float[] arrayOfFloat3 = paramColorSpace.fromCIEXYZ(arrayOfFloat2);
    if (paramArrayOfFloat == null) {
      paramArrayOfFloat = new float[arrayOfFloat3.length + 1];
    }
    for (int i = 0; i < arrayOfFloat3.length; i++) {
      paramArrayOfFloat[i] = arrayOfFloat3[i];
    }
    if (fvalue == null) {
      paramArrayOfFloat[arrayOfFloat3.length] = (getAlpha() / 255.0F);
    } else {
      paramArrayOfFloat[arrayOfFloat3.length] = falpha;
    }
    return paramArrayOfFloat;
  }
  
  public float[] getColorComponents(ColorSpace paramColorSpace, float[] paramArrayOfFloat)
  {
    if (cs == null) {
      cs = ColorSpace.getInstance(1000);
    }
    float[] arrayOfFloat1;
    if (fvalue == null)
    {
      arrayOfFloat1 = new float[3];
      arrayOfFloat1[0] = (getRed() / 255.0F);
      arrayOfFloat1[1] = (getGreen() / 255.0F);
      arrayOfFloat1[2] = (getBlue() / 255.0F);
    }
    else
    {
      arrayOfFloat1 = fvalue;
    }
    float[] arrayOfFloat2 = cs.toCIEXYZ(arrayOfFloat1);
    float[] arrayOfFloat3 = paramColorSpace.fromCIEXYZ(arrayOfFloat2);
    if (paramArrayOfFloat == null) {
      return arrayOfFloat3;
    }
    for (int i = 0; i < arrayOfFloat3.length; i++) {
      paramArrayOfFloat[i] = arrayOfFloat3[i];
    }
    return paramArrayOfFloat;
  }
  
  public ColorSpace getColorSpace()
  {
    if (cs == null) {
      cs = ColorSpace.getInstance(1000);
    }
    return cs;
  }
  
  public synchronized PaintContext createContext(ColorModel paramColorModel, Rectangle paramRectangle, Rectangle2D paramRectangle2D, AffineTransform paramAffineTransform, RenderingHints paramRenderingHints)
  {
    return new ColorPaintContext(getRGB(), paramColorModel);
  }
  
  public int getTransparency()
  {
    int i = getAlpha();
    if (i == 255) {
      return 1;
    }
    if (i == 0) {
      return 2;
    }
    return 3;
  }
  
  static
  {
    Toolkit.loadLibraries();
    if (!GraphicsEnvironment.isHeadless()) {
      initIDs();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\Color.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
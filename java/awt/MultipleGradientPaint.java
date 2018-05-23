package java.awt;

import java.awt.geom.AffineTransform;
import java.awt.image.ColorModel;
import java.lang.ref.SoftReference;
import java.util.Arrays;

public abstract class MultipleGradientPaint
  implements Paint
{
  final int transparency;
  final float[] fractions;
  final Color[] colors;
  final AffineTransform gradientTransform;
  final CycleMethod cycleMethod;
  final ColorSpaceType colorSpace;
  ColorModel model;
  float[] normalizedIntervals;
  boolean isSimpleLookup;
  SoftReference<int[][]> gradients;
  SoftReference<int[]> gradient;
  int fastGradientArraySize;
  
  MultipleGradientPaint(float[] paramArrayOfFloat, Color[] paramArrayOfColor, CycleMethod paramCycleMethod, ColorSpaceType paramColorSpaceType, AffineTransform paramAffineTransform)
  {
    if (paramArrayOfFloat == null) {
      throw new NullPointerException("Fractions array cannot be null");
    }
    if (paramArrayOfColor == null) {
      throw new NullPointerException("Colors array cannot be null");
    }
    if (paramCycleMethod == null) {
      throw new NullPointerException("Cycle method cannot be null");
    }
    if (paramColorSpaceType == null) {
      throw new NullPointerException("Color space cannot be null");
    }
    if (paramAffineTransform == null) {
      throw new NullPointerException("Gradient transform cannot be null");
    }
    if (paramArrayOfFloat.length != paramArrayOfColor.length) {
      throw new IllegalArgumentException("Colors and fractions must have equal size");
    }
    if (paramArrayOfColor.length < 2) {
      throw new IllegalArgumentException("User must specify at least 2 colors");
    }
    float f1 = -1.0F;
    for (float f2 : paramArrayOfFloat)
    {
      if ((f2 < 0.0F) || (f2 > 1.0F)) {
        throw new IllegalArgumentException("Fraction values must be in the range 0 to 1: " + f2);
      }
      if (f2 <= f1) {
        throw new IllegalArgumentException("Keyframe fractions must be increasing: " + f2);
      }
      f1 = f2;
    }
    int i = 0;
    ??? = 0;
    ??? = paramArrayOfFloat.length;
    int m = 0;
    if (paramArrayOfFloat[0] != 0.0F)
    {
      i = 1;
      ???++;
      m++;
    }
    if (paramArrayOfFloat[(paramArrayOfFloat.length - 1)] != 1.0F)
    {
      ??? = 1;
      ???++;
    }
    fractions = new float[???];
    System.arraycopy(paramArrayOfFloat, 0, fractions, m, paramArrayOfFloat.length);
    colors = new Color[???];
    System.arraycopy(paramArrayOfColor, 0, colors, m, paramArrayOfColor.length);
    if (i != 0)
    {
      fractions[0] = 0.0F;
      colors[0] = paramArrayOfColor[0];
    }
    if (??? != 0)
    {
      fractions[(??? - 1)] = 1.0F;
      colors[(??? - 1)] = paramArrayOfColor[(paramArrayOfColor.length - 1)];
    }
    colorSpace = paramColorSpaceType;
    cycleMethod = paramCycleMethod;
    gradientTransform = new AffineTransform(paramAffineTransform);
    int n = 1;
    for (int i1 = 0; i1 < paramArrayOfColor.length; i1++) {
      n = (n != 0) && (paramArrayOfColor[i1].getAlpha() == 255) ? 1 : 0;
    }
    transparency = (n != 0 ? 1 : 3);
  }
  
  public final float[] getFractions()
  {
    return Arrays.copyOf(fractions, fractions.length);
  }
  
  public final Color[] getColors()
  {
    return (Color[])Arrays.copyOf(colors, colors.length);
  }
  
  public final CycleMethod getCycleMethod()
  {
    return cycleMethod;
  }
  
  public final ColorSpaceType getColorSpace()
  {
    return colorSpace;
  }
  
  public final AffineTransform getTransform()
  {
    return new AffineTransform(gradientTransform);
  }
  
  public final int getTransparency()
  {
    return transparency;
  }
  
  public static enum ColorSpaceType
  {
    SRGB,  LINEAR_RGB;
    
    private ColorSpaceType() {}
  }
  
  public static enum CycleMethod
  {
    NO_CYCLE,  REFLECT,  REPEAT;
    
    private CycleMethod() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\MultipleGradientPaint.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
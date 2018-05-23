package java.awt;

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;

final class RadialGradientPaintContext
  extends MultipleGradientPaintContext
{
  private boolean isSimpleFocus = false;
  private boolean isNonCyclic = false;
  private float radius;
  private float centerX;
  private float centerY;
  private float focusX;
  private float focusY;
  private float radiusSq;
  private float constA;
  private float constB;
  private float gDeltaDelta;
  private float trivial;
  private static final float SCALEBACK = 0.99F;
  private static final int SQRT_LUT_SIZE = 2048;
  private static float[] sqrtLut = new float['ࠁ'];
  
  RadialGradientPaintContext(RadialGradientPaint paramRadialGradientPaint, ColorModel paramColorModel, Rectangle paramRectangle, Rectangle2D paramRectangle2D, AffineTransform paramAffineTransform, RenderingHints paramRenderingHints, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float[] paramArrayOfFloat, Color[] paramArrayOfColor, MultipleGradientPaint.CycleMethod paramCycleMethod, MultipleGradientPaint.ColorSpaceType paramColorSpaceType)
  {
    super(paramRadialGradientPaint, paramColorModel, paramRectangle, paramRectangle2D, paramAffineTransform, paramRenderingHints, paramArrayOfFloat, paramArrayOfColor, paramCycleMethod, paramColorSpaceType);
    centerX = paramFloat1;
    centerY = paramFloat2;
    focusX = paramFloat4;
    focusY = paramFloat5;
    radius = paramFloat3;
    isSimpleFocus = ((focusX == centerX) && (focusY == centerY));
    isNonCyclic = (paramCycleMethod == MultipleGradientPaint.CycleMethod.NO_CYCLE);
    radiusSq = (radius * radius);
    float f1 = focusX - centerX;
    float f2 = focusY - centerY;
    double d = f1 * f1 + f2 * f2;
    if (d > radiusSq * 0.99F)
    {
      float f3 = (float)Math.sqrt(radiusSq * 0.99F / d);
      f1 *= f3;
      f2 *= f3;
      focusX = (centerX + f1);
      focusY = (centerY + f2);
    }
    trivial = ((float)Math.sqrt(radiusSq - f1 * f1));
    constA = (a02 - centerX);
    constB = (a12 - centerY);
    gDeltaDelta = (2.0F * (a00 * a00 + a10 * a10) / radiusSq);
  }
  
  protected void fillRaster(int[] paramArrayOfInt, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    if ((isSimpleFocus) && (isNonCyclic) && (isSimpleLookup)) {
      simpleNonCyclicFillRaster(paramArrayOfInt, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
    } else {
      cyclicCircularGradientFillRaster(paramArrayOfInt, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
    }
  }
  
  private void simpleNonCyclicFillRaster(int[] paramArrayOfInt, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    float f1 = a00 * paramInt3 + a01 * paramInt4 + constA;
    float f2 = a10 * paramInt3 + a11 * paramInt4 + constB;
    float f3 = gDeltaDelta;
    paramInt2 += paramInt5;
    int i = gradient[fastGradientArraySize];
    for (int j = 0; j < paramInt6; j++)
    {
      float f4 = (f1 * f1 + f2 * f2) / radiusSq;
      float f5 = 2.0F * (a00 * f1 + a10 * f2) / radiusSq + f3 / 2.0F;
      for (int k = 0; (k < paramInt5) && (f4 >= 1.0F); k++)
      {
        paramArrayOfInt[(paramInt1 + k)] = i;
        f4 += f5;
        f5 += f3;
      }
      while ((k < paramInt5) && (f4 < 1.0F))
      {
        int m;
        if (f4 <= 0.0F)
        {
          m = 0;
        }
        else
        {
          float f6 = f4 * 2048.0F;
          int n = (int)f6;
          float f7 = sqrtLut[n];
          float f8 = sqrtLut[(n + 1)] - f7;
          f6 = f7 + (f6 - n) * f8;
          m = (int)(f6 * fastGradientArraySize);
        }
        paramArrayOfInt[(paramInt1 + k)] = gradient[m];
        f4 += f5;
        f5 += f3;
        k++;
      }
      while (k < paramInt5)
      {
        paramArrayOfInt[(paramInt1 + k)] = i;
        k++;
      }
      paramInt1 += paramInt2;
      f1 += a01;
      f2 += a11;
    }
  }
  
  private void cyclicCircularGradientFillRaster(int[] paramArrayOfInt, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    double d1 = -radiusSq + centerX * centerX + centerY * centerY;
    float f1 = a00 * paramInt3 + a01 * paramInt4 + a02;
    float f2 = a10 * paramInt3 + a11 * paramInt4 + a12;
    float f3 = 2.0F * centerY;
    float f4 = -2.0F * centerX;
    int i = paramInt1;
    int j = paramInt5 + paramInt2;
    for (int k = 0; k < paramInt6; k++)
    {
      float f11 = a01 * k + f1;
      float f12 = a11 * k + f2;
      for (int m = 0; m < paramInt5; m++)
      {
        double d7;
        double d8;
        if (f11 == focusX)
        {
          d7 = focusX;
          d8 = centerY;
          d8 += (f12 > focusY ? trivial : -trivial);
        }
        else
        {
          double d5 = (f12 - focusY) / (f11 - focusX);
          double d6 = f12 - d5 * f11;
          double d2 = d5 * d5 + 1.0D;
          double d3 = f4 + -2.0D * d5 * (centerY - d6);
          double d4 = d1 + d6 * (d6 - f3);
          float f6 = (float)Math.sqrt(d3 * d3 - 4.0D * d2 * d4);
          d7 = -d3;
          d7 += (f11 < focusX ? -f6 : f6);
          d7 /= 2.0D * d2;
          d8 = d5 * d7 + d6;
        }
        float f9 = f11 - focusX;
        f9 *= f9;
        float f10 = f12 - focusY;
        f10 *= f10;
        float f7 = f9 + f10;
        f9 = (float)d7 - focusX;
        f9 *= f9;
        f10 = (float)d8 - focusY;
        f10 *= f10;
        float f8 = f9 + f10;
        float f5 = (float)Math.sqrt(f7 / f8);
        paramArrayOfInt[(i + m)] = indexIntoGradientsArrays(f5);
        f11 += a00;
        f12 += a10;
      }
      i += j;
    }
  }
  
  static
  {
    for (int i = 0; i < sqrtLut.length; i++) {
      sqrtLut[i] = ((float)Math.sqrt(i / 2048.0F));
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\RadialGradientPaintContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package java.awt;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;

final class LinearGradientPaintContext
  extends MultipleGradientPaintContext
{
  private float dgdX;
  private float dgdY;
  private float gc;
  
  LinearGradientPaintContext(LinearGradientPaint paramLinearGradientPaint, ColorModel paramColorModel, Rectangle paramRectangle, Rectangle2D paramRectangle2D, AffineTransform paramAffineTransform, RenderingHints paramRenderingHints, Point2D paramPoint2D1, Point2D paramPoint2D2, float[] paramArrayOfFloat, Color[] paramArrayOfColor, MultipleGradientPaint.CycleMethod paramCycleMethod, MultipleGradientPaint.ColorSpaceType paramColorSpaceType)
  {
    super(paramLinearGradientPaint, paramColorModel, paramRectangle, paramRectangle2D, paramAffineTransform, paramRenderingHints, paramArrayOfFloat, paramArrayOfColor, paramCycleMethod, paramColorSpaceType);
    float f1 = (float)paramPoint2D1.getX();
    float f2 = (float)paramPoint2D1.getY();
    float f3 = (float)paramPoint2D2.getX();
    float f4 = (float)paramPoint2D2.getY();
    float f5 = f3 - f1;
    float f6 = f4 - f2;
    float f7 = f5 * f5 + f6 * f6;
    float f8 = f5 / f7;
    float f9 = f6 / f7;
    dgdX = (a00 * f8 + a10 * f9);
    dgdY = (a01 * f8 + a11 * f9);
    gc = ((a02 - f1) * f8 + (a12 - f2) * f9);
  }
  
  protected void fillRaster(int[] paramArrayOfInt, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    float f1 = 0.0F;
    int i = paramInt1 + paramInt5;
    float f2 = dgdX * paramInt3 + gc;
    for (int j = 0; j < paramInt6; j++)
    {
      for (f1 = f2 + dgdY * (paramInt4 + j); paramInt1 < i; f1 += dgdX) {
        paramArrayOfInt[(paramInt1++)] = indexIntoGradientsArrays(f1);
      }
      paramInt1 += paramInt2;
      i = paramInt1 + paramInt5;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\LinearGradientPaintContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
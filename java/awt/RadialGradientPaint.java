package java.awt;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.awt.geom.Point2D.Float;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;
import java.beans.ConstructorProperties;

public final class RadialGradientPaint
  extends MultipleGradientPaint
{
  private final Point2D focus;
  private final Point2D center;
  private final float radius;
  
  public RadialGradientPaint(float paramFloat1, float paramFloat2, float paramFloat3, float[] paramArrayOfFloat, Color[] paramArrayOfColor)
  {
    this(paramFloat1, paramFloat2, paramFloat3, paramFloat1, paramFloat2, paramArrayOfFloat, paramArrayOfColor, MultipleGradientPaint.CycleMethod.NO_CYCLE);
  }
  
  public RadialGradientPaint(Point2D paramPoint2D, float paramFloat, float[] paramArrayOfFloat, Color[] paramArrayOfColor)
  {
    this(paramPoint2D, paramFloat, paramPoint2D, paramArrayOfFloat, paramArrayOfColor, MultipleGradientPaint.CycleMethod.NO_CYCLE);
  }
  
  public RadialGradientPaint(float paramFloat1, float paramFloat2, float paramFloat3, float[] paramArrayOfFloat, Color[] paramArrayOfColor, MultipleGradientPaint.CycleMethod paramCycleMethod)
  {
    this(paramFloat1, paramFloat2, paramFloat3, paramFloat1, paramFloat2, paramArrayOfFloat, paramArrayOfColor, paramCycleMethod);
  }
  
  public RadialGradientPaint(Point2D paramPoint2D, float paramFloat, float[] paramArrayOfFloat, Color[] paramArrayOfColor, MultipleGradientPaint.CycleMethod paramCycleMethod)
  {
    this(paramPoint2D, paramFloat, paramPoint2D, paramArrayOfFloat, paramArrayOfColor, paramCycleMethod);
  }
  
  public RadialGradientPaint(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float[] paramArrayOfFloat, Color[] paramArrayOfColor, MultipleGradientPaint.CycleMethod paramCycleMethod)
  {
    this(new Point2D.Float(paramFloat1, paramFloat2), paramFloat3, new Point2D.Float(paramFloat4, paramFloat5), paramArrayOfFloat, paramArrayOfColor, paramCycleMethod);
  }
  
  public RadialGradientPaint(Point2D paramPoint2D1, float paramFloat, Point2D paramPoint2D2, float[] paramArrayOfFloat, Color[] paramArrayOfColor, MultipleGradientPaint.CycleMethod paramCycleMethod)
  {
    this(paramPoint2D1, paramFloat, paramPoint2D2, paramArrayOfFloat, paramArrayOfColor, paramCycleMethod, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform());
  }
  
  @ConstructorProperties({"centerPoint", "radius", "focusPoint", "fractions", "colors", "cycleMethod", "colorSpace", "transform"})
  public RadialGradientPaint(Point2D paramPoint2D1, float paramFloat, Point2D paramPoint2D2, float[] paramArrayOfFloat, Color[] paramArrayOfColor, MultipleGradientPaint.CycleMethod paramCycleMethod, MultipleGradientPaint.ColorSpaceType paramColorSpaceType, AffineTransform paramAffineTransform)
  {
    super(paramArrayOfFloat, paramArrayOfColor, paramCycleMethod, paramColorSpaceType, paramAffineTransform);
    if (paramPoint2D1 == null) {
      throw new NullPointerException("Center point must be non-null");
    }
    if (paramPoint2D2 == null) {
      throw new NullPointerException("Focus point must be non-null");
    }
    if (paramFloat <= 0.0F) {
      throw new IllegalArgumentException("Radius must be greater than zero");
    }
    center = new Point2D.Double(paramPoint2D1.getX(), paramPoint2D1.getY());
    focus = new Point2D.Double(paramPoint2D2.getX(), paramPoint2D2.getY());
    radius = paramFloat;
  }
  
  public RadialGradientPaint(Rectangle2D paramRectangle2D, float[] paramArrayOfFloat, Color[] paramArrayOfColor, MultipleGradientPaint.CycleMethod paramCycleMethod)
  {
    this(new Point2D.Double(paramRectangle2D.getCenterX(), paramRectangle2D.getCenterY()), 1.0F, new Point2D.Double(paramRectangle2D.getCenterX(), paramRectangle2D.getCenterY()), paramArrayOfFloat, paramArrayOfColor, paramCycleMethod, MultipleGradientPaint.ColorSpaceType.SRGB, createGradientTransform(paramRectangle2D));
    if (paramRectangle2D.isEmpty()) {
      throw new IllegalArgumentException("Gradient bounds must be non-empty");
    }
  }
  
  private static AffineTransform createGradientTransform(Rectangle2D paramRectangle2D)
  {
    double d1 = paramRectangle2D.getCenterX();
    double d2 = paramRectangle2D.getCenterY();
    AffineTransform localAffineTransform = AffineTransform.getTranslateInstance(d1, d2);
    localAffineTransform.scale(paramRectangle2D.getWidth() / 2.0D, paramRectangle2D.getHeight() / 2.0D);
    localAffineTransform.translate(-d1, -d2);
    return localAffineTransform;
  }
  
  public PaintContext createContext(ColorModel paramColorModel, Rectangle paramRectangle, Rectangle2D paramRectangle2D, AffineTransform paramAffineTransform, RenderingHints paramRenderingHints)
  {
    paramAffineTransform = new AffineTransform(paramAffineTransform);
    paramAffineTransform.concatenate(gradientTransform);
    return new RadialGradientPaintContext(this, paramColorModel, paramRectangle, paramRectangle2D, paramAffineTransform, paramRenderingHints, (float)center.getX(), (float)center.getY(), radius, (float)focus.getX(), (float)focus.getY(), fractions, colors, cycleMethod, colorSpace);
  }
  
  public Point2D getCenterPoint()
  {
    return new Point2D.Double(center.getX(), center.getY());
  }
  
  public Point2D getFocusPoint()
  {
    return new Point2D.Double(focus.getX(), focus.getY());
  }
  
  public float getRadius()
  {
    return radius;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\RadialGradientPaint.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
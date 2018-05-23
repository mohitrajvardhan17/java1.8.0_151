package java.awt;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.awt.geom.Point2D.Float;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;
import java.beans.ConstructorProperties;

public final class LinearGradientPaint
  extends MultipleGradientPaint
{
  private final Point2D start;
  private final Point2D end;
  
  public LinearGradientPaint(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float[] paramArrayOfFloat, Color[] paramArrayOfColor)
  {
    this(new Point2D.Float(paramFloat1, paramFloat2), new Point2D.Float(paramFloat3, paramFloat4), paramArrayOfFloat, paramArrayOfColor, MultipleGradientPaint.CycleMethod.NO_CYCLE);
  }
  
  public LinearGradientPaint(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float[] paramArrayOfFloat, Color[] paramArrayOfColor, MultipleGradientPaint.CycleMethod paramCycleMethod)
  {
    this(new Point2D.Float(paramFloat1, paramFloat2), new Point2D.Float(paramFloat3, paramFloat4), paramArrayOfFloat, paramArrayOfColor, paramCycleMethod);
  }
  
  public LinearGradientPaint(Point2D paramPoint2D1, Point2D paramPoint2D2, float[] paramArrayOfFloat, Color[] paramArrayOfColor)
  {
    this(paramPoint2D1, paramPoint2D2, paramArrayOfFloat, paramArrayOfColor, MultipleGradientPaint.CycleMethod.NO_CYCLE);
  }
  
  public LinearGradientPaint(Point2D paramPoint2D1, Point2D paramPoint2D2, float[] paramArrayOfFloat, Color[] paramArrayOfColor, MultipleGradientPaint.CycleMethod paramCycleMethod)
  {
    this(paramPoint2D1, paramPoint2D2, paramArrayOfFloat, paramArrayOfColor, paramCycleMethod, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform());
  }
  
  @ConstructorProperties({"startPoint", "endPoint", "fractions", "colors", "cycleMethod", "colorSpace", "transform"})
  public LinearGradientPaint(Point2D paramPoint2D1, Point2D paramPoint2D2, float[] paramArrayOfFloat, Color[] paramArrayOfColor, MultipleGradientPaint.CycleMethod paramCycleMethod, MultipleGradientPaint.ColorSpaceType paramColorSpaceType, AffineTransform paramAffineTransform)
  {
    super(paramArrayOfFloat, paramArrayOfColor, paramCycleMethod, paramColorSpaceType, paramAffineTransform);
    if ((paramPoint2D1 == null) || (paramPoint2D2 == null)) {
      throw new NullPointerException("Start and end points must benon-null");
    }
    if (paramPoint2D1.equals(paramPoint2D2)) {
      throw new IllegalArgumentException("Start point cannot equalendpoint");
    }
    start = new Point2D.Double(paramPoint2D1.getX(), paramPoint2D1.getY());
    end = new Point2D.Double(paramPoint2D2.getX(), paramPoint2D2.getY());
  }
  
  public PaintContext createContext(ColorModel paramColorModel, Rectangle paramRectangle, Rectangle2D paramRectangle2D, AffineTransform paramAffineTransform, RenderingHints paramRenderingHints)
  {
    paramAffineTransform = new AffineTransform(paramAffineTransform);
    paramAffineTransform.concatenate(gradientTransform);
    if ((fractions.length == 2) && (cycleMethod != MultipleGradientPaint.CycleMethod.REPEAT) && (colorSpace == MultipleGradientPaint.ColorSpaceType.SRGB))
    {
      boolean bool = cycleMethod != MultipleGradientPaint.CycleMethod.NO_CYCLE;
      return new GradientPaintContext(paramColorModel, start, end, paramAffineTransform, colors[0], colors[1], bool);
    }
    return new LinearGradientPaintContext(this, paramColorModel, paramRectangle, paramRectangle2D, paramAffineTransform, paramRenderingHints, start, end, fractions, colors, cycleMethod, colorSpace);
  }
  
  public Point2D getStartPoint()
  {
    return new Point2D.Double(start.getX(), start.getY());
  }
  
  public Point2D getEndPoint()
  {
    return new Point2D.Double(end.getX(), end.getY());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\LinearGradientPaint.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
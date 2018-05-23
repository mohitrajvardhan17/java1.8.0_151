package java.awt;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;
import java.beans.ConstructorProperties;

public class GradientPaint
  implements Paint
{
  Point2D.Float p1;
  Point2D.Float p2;
  Color color1;
  Color color2;
  boolean cyclic;
  
  public GradientPaint(float paramFloat1, float paramFloat2, Color paramColor1, float paramFloat3, float paramFloat4, Color paramColor2)
  {
    if ((paramColor1 == null) || (paramColor2 == null)) {
      throw new NullPointerException("Colors cannot be null");
    }
    p1 = new Point2D.Float(paramFloat1, paramFloat2);
    p2 = new Point2D.Float(paramFloat3, paramFloat4);
    color1 = paramColor1;
    color2 = paramColor2;
  }
  
  public GradientPaint(Point2D paramPoint2D1, Color paramColor1, Point2D paramPoint2D2, Color paramColor2)
  {
    if ((paramColor1 == null) || (paramColor2 == null) || (paramPoint2D1 == null) || (paramPoint2D2 == null)) {
      throw new NullPointerException("Colors and points should be non-null");
    }
    p1 = new Point2D.Float((float)paramPoint2D1.getX(), (float)paramPoint2D1.getY());
    p2 = new Point2D.Float((float)paramPoint2D2.getX(), (float)paramPoint2D2.getY());
    color1 = paramColor1;
    color2 = paramColor2;
  }
  
  public GradientPaint(float paramFloat1, float paramFloat2, Color paramColor1, float paramFloat3, float paramFloat4, Color paramColor2, boolean paramBoolean)
  {
    this(paramFloat1, paramFloat2, paramColor1, paramFloat3, paramFloat4, paramColor2);
    cyclic = paramBoolean;
  }
  
  @ConstructorProperties({"point1", "color1", "point2", "color2", "cyclic"})
  public GradientPaint(Point2D paramPoint2D1, Color paramColor1, Point2D paramPoint2D2, Color paramColor2, boolean paramBoolean)
  {
    this(paramPoint2D1, paramColor1, paramPoint2D2, paramColor2);
    cyclic = paramBoolean;
  }
  
  public Point2D getPoint1()
  {
    return new Point2D.Float(p1.x, p1.y);
  }
  
  public Color getColor1()
  {
    return color1;
  }
  
  public Point2D getPoint2()
  {
    return new Point2D.Float(p2.x, p2.y);
  }
  
  public Color getColor2()
  {
    return color2;
  }
  
  public boolean isCyclic()
  {
    return cyclic;
  }
  
  public PaintContext createContext(ColorModel paramColorModel, Rectangle paramRectangle, Rectangle2D paramRectangle2D, AffineTransform paramAffineTransform, RenderingHints paramRenderingHints)
  {
    return new GradientPaintContext(paramColorModel, p1, p2, paramAffineTransform, color1, color2, cyclic);
  }
  
  public int getTransparency()
  {
    int i = color1.getAlpha();
    int j = color2.getAlpha();
    return (i & j) == 255 ? 1 : 3;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\GradientPaint.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
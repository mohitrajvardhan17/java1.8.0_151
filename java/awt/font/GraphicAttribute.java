package java.awt.font;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Float;

public abstract class GraphicAttribute
{
  private int fAlignment;
  public static final int TOP_ALIGNMENT = -1;
  public static final int BOTTOM_ALIGNMENT = -2;
  public static final int ROMAN_BASELINE = 0;
  public static final int CENTER_BASELINE = 1;
  public static final int HANGING_BASELINE = 2;
  
  protected GraphicAttribute(int paramInt)
  {
    if ((paramInt < -2) || (paramInt > 2)) {
      throw new IllegalArgumentException("bad alignment");
    }
    fAlignment = paramInt;
  }
  
  public abstract float getAscent();
  
  public abstract float getDescent();
  
  public abstract float getAdvance();
  
  public Rectangle2D getBounds()
  {
    float f = getAscent();
    return new Rectangle2D.Float(0.0F, -f, getAdvance(), f + getDescent());
  }
  
  public Shape getOutline(AffineTransform paramAffineTransform)
  {
    Object localObject = getBounds();
    if (paramAffineTransform != null) {
      localObject = paramAffineTransform.createTransformedShape((Shape)localObject);
    }
    return (Shape)localObject;
  }
  
  public abstract void draw(Graphics2D paramGraphics2D, float paramFloat1, float paramFloat2);
  
  public final int getAlignment()
  {
    return fAlignment;
  }
  
  public GlyphJustificationInfo getJustificationInfo()
  {
    float f = getAdvance();
    return new GlyphJustificationInfo(f, false, 2, f / 3.0F, f / 3.0F, false, 1, 0.0F, 0.0F);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\font\GraphicAttribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
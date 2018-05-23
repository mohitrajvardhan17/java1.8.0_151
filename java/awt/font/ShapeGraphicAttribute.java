package java.awt.font;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Float;

public final class ShapeGraphicAttribute
  extends GraphicAttribute
{
  private Shape fShape;
  private boolean fStroke;
  public static final boolean STROKE = true;
  public static final boolean FILL = false;
  private Rectangle2D fShapeBounds;
  
  public ShapeGraphicAttribute(Shape paramShape, int paramInt, boolean paramBoolean)
  {
    super(paramInt);
    fShape = paramShape;
    fStroke = paramBoolean;
    fShapeBounds = fShape.getBounds2D();
  }
  
  public float getAscent()
  {
    return (float)Math.max(0.0D, -fShapeBounds.getMinY());
  }
  
  public float getDescent()
  {
    return (float)Math.max(0.0D, fShapeBounds.getMaxY());
  }
  
  public float getAdvance()
  {
    return (float)Math.max(0.0D, fShapeBounds.getMaxX());
  }
  
  public void draw(Graphics2D paramGraphics2D, float paramFloat1, float paramFloat2)
  {
    paramGraphics2D.translate((int)paramFloat1, (int)paramFloat2);
    try
    {
      if (fStroke == true) {
        paramGraphics2D.draw(fShape);
      } else {
        paramGraphics2D.fill(fShape);
      }
    }
    finally
    {
      paramGraphics2D.translate(-(int)paramFloat1, -(int)paramFloat2);
    }
  }
  
  public Rectangle2D getBounds()
  {
    Rectangle2D.Float localFloat = new Rectangle2D.Float();
    localFloat.setRect(fShapeBounds);
    if (fStroke == true)
    {
      width += 1.0F;
      height += 1.0F;
    }
    return localFloat;
  }
  
  public Shape getOutline(AffineTransform paramAffineTransform)
  {
    return paramAffineTransform == null ? fShape : paramAffineTransform.createTransformedShape(fShape);
  }
  
  public int hashCode()
  {
    return fShape.hashCode();
  }
  
  public boolean equals(Object paramObject)
  {
    try
    {
      return equals((ShapeGraphicAttribute)paramObject);
    }
    catch (ClassCastException localClassCastException) {}
    return false;
  }
  
  public boolean equals(ShapeGraphicAttribute paramShapeGraphicAttribute)
  {
    if (paramShapeGraphicAttribute == null) {
      return false;
    }
    if (this == paramShapeGraphicAttribute) {
      return true;
    }
    if (fStroke != fStroke) {
      return false;
    }
    if (getAlignment() != paramShapeGraphicAttribute.getAlignment()) {
      return false;
    }
    return fShape.equals(fShape);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\font\ShapeGraphicAttribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
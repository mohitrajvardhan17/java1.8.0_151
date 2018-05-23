package java.awt.font;

import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Float;

public final class GlyphMetrics
{
  private boolean horizontal;
  private float advanceX;
  private float advanceY;
  private Rectangle2D.Float bounds;
  private byte glyphType;
  public static final byte STANDARD = 0;
  public static final byte LIGATURE = 1;
  public static final byte COMBINING = 2;
  public static final byte COMPONENT = 3;
  public static final byte WHITESPACE = 4;
  
  public GlyphMetrics(float paramFloat, Rectangle2D paramRectangle2D, byte paramByte)
  {
    horizontal = true;
    advanceX = paramFloat;
    advanceY = 0.0F;
    bounds = new Rectangle2D.Float();
    bounds.setRect(paramRectangle2D);
    glyphType = paramByte;
  }
  
  public GlyphMetrics(boolean paramBoolean, float paramFloat1, float paramFloat2, Rectangle2D paramRectangle2D, byte paramByte)
  {
    horizontal = paramBoolean;
    advanceX = paramFloat1;
    advanceY = paramFloat2;
    bounds = new Rectangle2D.Float();
    bounds.setRect(paramRectangle2D);
    glyphType = paramByte;
  }
  
  public float getAdvance()
  {
    return horizontal ? advanceX : advanceY;
  }
  
  public float getAdvanceX()
  {
    return advanceX;
  }
  
  public float getAdvanceY()
  {
    return advanceY;
  }
  
  public Rectangle2D getBounds2D()
  {
    return new Rectangle2D.Float(bounds.x, bounds.y, bounds.width, bounds.height);
  }
  
  public float getLSB()
  {
    return horizontal ? bounds.x : bounds.y;
  }
  
  public float getRSB()
  {
    return horizontal ? advanceX - bounds.x - bounds.width : advanceY - bounds.y - bounds.height;
  }
  
  public int getType()
  {
    return glyphType;
  }
  
  public boolean isStandard()
  {
    return (glyphType & 0x3) == 0;
  }
  
  public boolean isLigature()
  {
    return (glyphType & 0x3) == 1;
  }
  
  public boolean isCombining()
  {
    return (glyphType & 0x3) == 2;
  }
  
  public boolean isComponent()
  {
    return (glyphType & 0x3) == 3;
  }
  
  public boolean isWhitespace()
  {
    return (glyphType & 0x4) == 4;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\font\GlyphMetrics.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
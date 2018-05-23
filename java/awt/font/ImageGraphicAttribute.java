package java.awt.font;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Float;

public final class ImageGraphicAttribute
  extends GraphicAttribute
{
  private Image fImage;
  private float fImageWidth;
  private float fImageHeight;
  private float fOriginX;
  private float fOriginY;
  
  public ImageGraphicAttribute(Image paramImage, int paramInt)
  {
    this(paramImage, paramInt, 0.0F, 0.0F);
  }
  
  public ImageGraphicAttribute(Image paramImage, int paramInt, float paramFloat1, float paramFloat2)
  {
    super(paramInt);
    fImage = paramImage;
    fImageWidth = paramImage.getWidth(null);
    fImageHeight = paramImage.getHeight(null);
    fOriginX = paramFloat1;
    fOriginY = paramFloat2;
  }
  
  public float getAscent()
  {
    return Math.max(0.0F, fOriginY);
  }
  
  public float getDescent()
  {
    return Math.max(0.0F, fImageHeight - fOriginY);
  }
  
  public float getAdvance()
  {
    return Math.max(0.0F, fImageWidth - fOriginX);
  }
  
  public Rectangle2D getBounds()
  {
    return new Rectangle2D.Float(-fOriginX, -fOriginY, fImageWidth, fImageHeight);
  }
  
  public void draw(Graphics2D paramGraphics2D, float paramFloat1, float paramFloat2)
  {
    paramGraphics2D.drawImage(fImage, (int)(paramFloat1 - fOriginX), (int)(paramFloat2 - fOriginY), null);
  }
  
  public int hashCode()
  {
    return fImage.hashCode();
  }
  
  public boolean equals(Object paramObject)
  {
    try
    {
      return equals((ImageGraphicAttribute)paramObject);
    }
    catch (ClassCastException localClassCastException) {}
    return false;
  }
  
  public boolean equals(ImageGraphicAttribute paramImageGraphicAttribute)
  {
    if (paramImageGraphicAttribute == null) {
      return false;
    }
    if (this == paramImageGraphicAttribute) {
      return true;
    }
    if ((fOriginX != fOriginX) || (fOriginY != fOriginY)) {
      return false;
    }
    if (getAlignment() != paramImageGraphicAttribute.getAlignment()) {
      return false;
    }
    return fImage.equals(fImage);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\font\ImageGraphicAttribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package java.awt;

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;

public class TexturePaint
  implements Paint
{
  BufferedImage bufImg;
  double tx;
  double ty;
  double sx;
  double sy;
  
  public TexturePaint(BufferedImage paramBufferedImage, Rectangle2D paramRectangle2D)
  {
    bufImg = paramBufferedImage;
    tx = paramRectangle2D.getX();
    ty = paramRectangle2D.getY();
    sx = (paramRectangle2D.getWidth() / bufImg.getWidth());
    sy = (paramRectangle2D.getHeight() / bufImg.getHeight());
  }
  
  public BufferedImage getImage()
  {
    return bufImg;
  }
  
  public Rectangle2D getAnchorRect()
  {
    return new Rectangle2D.Double(tx, ty, sx * bufImg.getWidth(), sy * bufImg.getHeight());
  }
  
  public PaintContext createContext(ColorModel paramColorModel, Rectangle paramRectangle, Rectangle2D paramRectangle2D, AffineTransform paramAffineTransform, RenderingHints paramRenderingHints)
  {
    if (paramAffineTransform == null) {
      paramAffineTransform = new AffineTransform();
    } else {
      paramAffineTransform = (AffineTransform)paramAffineTransform.clone();
    }
    paramAffineTransform.translate(tx, ty);
    paramAffineTransform.scale(sx, sy);
    return TexturePaintContext.getContext(bufImg, paramAffineTransform, paramRenderingHints, paramRectangle);
  }
  
  public int getTransparency()
  {
    return bufImg.getColorModel().getTransparency();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\TexturePaint.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
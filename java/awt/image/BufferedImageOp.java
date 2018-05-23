package java.awt.image;

import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public abstract interface BufferedImageOp
{
  public abstract BufferedImage filter(BufferedImage paramBufferedImage1, BufferedImage paramBufferedImage2);
  
  public abstract Rectangle2D getBounds2D(BufferedImage paramBufferedImage);
  
  public abstract BufferedImage createCompatibleDestImage(BufferedImage paramBufferedImage, ColorModel paramColorModel);
  
  public abstract Point2D getPoint2D(Point2D paramPoint2D1, Point2D paramPoint2D2);
  
  public abstract RenderingHints getRenderingHints();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\image\BufferedImageOp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
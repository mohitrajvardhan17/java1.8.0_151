package java.awt.image.renderable;

import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;

public class RenderContext
  implements Cloneable
{
  RenderingHints hints;
  AffineTransform usr2dev;
  Shape aoi;
  
  public RenderContext(AffineTransform paramAffineTransform, Shape paramShape, RenderingHints paramRenderingHints)
  {
    hints = paramRenderingHints;
    aoi = paramShape;
    usr2dev = ((AffineTransform)paramAffineTransform.clone());
  }
  
  public RenderContext(AffineTransform paramAffineTransform)
  {
    this(paramAffineTransform, null, null);
  }
  
  public RenderContext(AffineTransform paramAffineTransform, RenderingHints paramRenderingHints)
  {
    this(paramAffineTransform, null, paramRenderingHints);
  }
  
  public RenderContext(AffineTransform paramAffineTransform, Shape paramShape)
  {
    this(paramAffineTransform, paramShape, null);
  }
  
  public RenderingHints getRenderingHints()
  {
    return hints;
  }
  
  public void setRenderingHints(RenderingHints paramRenderingHints)
  {
    hints = paramRenderingHints;
  }
  
  public void setTransform(AffineTransform paramAffineTransform)
  {
    usr2dev = ((AffineTransform)paramAffineTransform.clone());
  }
  
  public void preConcatenateTransform(AffineTransform paramAffineTransform)
  {
    preConcetenateTransform(paramAffineTransform);
  }
  
  @Deprecated
  public void preConcetenateTransform(AffineTransform paramAffineTransform)
  {
    usr2dev.preConcatenate(paramAffineTransform);
  }
  
  public void concatenateTransform(AffineTransform paramAffineTransform)
  {
    concetenateTransform(paramAffineTransform);
  }
  
  @Deprecated
  public void concetenateTransform(AffineTransform paramAffineTransform)
  {
    usr2dev.concatenate(paramAffineTransform);
  }
  
  public AffineTransform getTransform()
  {
    return (AffineTransform)usr2dev.clone();
  }
  
  public void setAreaOfInterest(Shape paramShape)
  {
    aoi = paramShape;
  }
  
  public Shape getAreaOfInterest()
  {
    return aoi;
  }
  
  public Object clone()
  {
    RenderContext localRenderContext = new RenderContext(usr2dev, aoi, hints);
    return localRenderContext;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\image\renderable\RenderContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package java.awt.image.renderable;

import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.RenderedImage;
import java.util.Vector;

public class RenderableImageOp
  implements RenderableImage
{
  ParameterBlock paramBlock;
  ContextualRenderedImageFactory myCRIF;
  Rectangle2D boundingBox;
  
  public RenderableImageOp(ContextualRenderedImageFactory paramContextualRenderedImageFactory, ParameterBlock paramParameterBlock)
  {
    myCRIF = paramContextualRenderedImageFactory;
    paramBlock = ((ParameterBlock)paramParameterBlock.clone());
  }
  
  public Vector<RenderableImage> getSources()
  {
    return getRenderableSources();
  }
  
  private Vector getRenderableSources()
  {
    Vector localVector = null;
    if (paramBlock.getNumSources() > 0)
    {
      localVector = new Vector();
      for (int i = 0; i < paramBlock.getNumSources(); i++)
      {
        Object localObject = paramBlock.getSource(i);
        if (!(localObject instanceof RenderableImage)) {
          break;
        }
        localVector.add((RenderableImage)localObject);
      }
    }
    return localVector;
  }
  
  public Object getProperty(String paramString)
  {
    return myCRIF.getProperty(paramBlock, paramString);
  }
  
  public String[] getPropertyNames()
  {
    return myCRIF.getPropertyNames();
  }
  
  public boolean isDynamic()
  {
    return myCRIF.isDynamic();
  }
  
  public float getWidth()
  {
    if (boundingBox == null) {
      boundingBox = myCRIF.getBounds2D(paramBlock);
    }
    return (float)boundingBox.getWidth();
  }
  
  public float getHeight()
  {
    if (boundingBox == null) {
      boundingBox = myCRIF.getBounds2D(paramBlock);
    }
    return (float)boundingBox.getHeight();
  }
  
  public float getMinX()
  {
    if (boundingBox == null) {
      boundingBox = myCRIF.getBounds2D(paramBlock);
    }
    return (float)boundingBox.getMinX();
  }
  
  public float getMinY()
  {
    if (boundingBox == null) {
      boundingBox = myCRIF.getBounds2D(paramBlock);
    }
    return (float)boundingBox.getMinY();
  }
  
  public ParameterBlock setParameterBlock(ParameterBlock paramParameterBlock)
  {
    ParameterBlock localParameterBlock = paramBlock;
    paramBlock = ((ParameterBlock)paramParameterBlock.clone());
    return localParameterBlock;
  }
  
  public ParameterBlock getParameterBlock()
  {
    return paramBlock;
  }
  
  public RenderedImage createScaledRendering(int paramInt1, int paramInt2, RenderingHints paramRenderingHints)
  {
    double d1 = paramInt1 / getWidth();
    double d2 = paramInt2 / getHeight();
    if (Math.abs(d1 / d2 - 1.0D) < 0.01D) {
      d1 = d2;
    }
    AffineTransform localAffineTransform = AffineTransform.getScaleInstance(d1, d2);
    RenderContext localRenderContext = new RenderContext(localAffineTransform, paramRenderingHints);
    return createRendering(localRenderContext);
  }
  
  public RenderedImage createDefaultRendering()
  {
    AffineTransform localAffineTransform = new AffineTransform();
    RenderContext localRenderContext = new RenderContext(localAffineTransform);
    return createRendering(localRenderContext);
  }
  
  public RenderedImage createRendering(RenderContext paramRenderContext)
  {
    Object localObject = null;
    RenderContext localRenderContext = null;
    ParameterBlock localParameterBlock = (ParameterBlock)paramBlock.clone();
    Vector localVector1 = getRenderableSources();
    try
    {
      if (localVector1 != null)
      {
        Vector localVector2 = new Vector();
        for (int i = 0; i < localVector1.size(); i++)
        {
          localRenderContext = myCRIF.mapRenderContext(i, paramRenderContext, paramBlock, this);
          RenderedImage localRenderedImage = ((RenderableImage)localVector1.elementAt(i)).createRendering(localRenderContext);
          if (localRenderedImage == null) {
            return null;
          }
          localVector2.addElement(localRenderedImage);
        }
        if (localVector2.size() > 0) {
          localParameterBlock.setSources(localVector2);
        }
      }
      return myCRIF.create(paramRenderContext, localParameterBlock);
    }
    catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException) {}
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\image\renderable\RenderableImageOp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
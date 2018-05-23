package sun.java2d.opengl;

import java.awt.Composite;
import sun.java2d.InvalidPipeException;
import sun.java2d.SunGraphics2D;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.GraphicsPrimitive;
import sun.java2d.loops.GraphicsPrimitiveMgr;
import sun.java2d.loops.SurfaceType;
import sun.java2d.pipe.BufferedMaskFill;

class OGLMaskFill
  extends BufferedMaskFill
{
  static void register()
  {
    GraphicsPrimitive[] arrayOfGraphicsPrimitive = { new OGLMaskFill(SurfaceType.AnyColor, CompositeType.SrcOver), new OGLMaskFill(SurfaceType.OpaqueColor, CompositeType.SrcNoEa), new OGLMaskFill(SurfaceType.GradientPaint, CompositeType.SrcOver), new OGLMaskFill(SurfaceType.OpaqueGradientPaint, CompositeType.SrcNoEa), new OGLMaskFill(SurfaceType.LinearGradientPaint, CompositeType.SrcOver), new OGLMaskFill(SurfaceType.OpaqueLinearGradientPaint, CompositeType.SrcNoEa), new OGLMaskFill(SurfaceType.RadialGradientPaint, CompositeType.SrcOver), new OGLMaskFill(SurfaceType.OpaqueRadialGradientPaint, CompositeType.SrcNoEa), new OGLMaskFill(SurfaceType.TexturePaint, CompositeType.SrcOver), new OGLMaskFill(SurfaceType.OpaqueTexturePaint, CompositeType.SrcNoEa) };
    GraphicsPrimitiveMgr.register(arrayOfGraphicsPrimitive);
  }
  
  protected OGLMaskFill(SurfaceType paramSurfaceType, CompositeType paramCompositeType)
  {
    super(OGLRenderQueue.getInstance(), paramSurfaceType, paramCompositeType, OGLSurfaceData.OpenGLSurface);
  }
  
  protected native void maskFill(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, byte[] paramArrayOfByte);
  
  protected void validateContext(SunGraphics2D paramSunGraphics2D, Composite paramComposite, int paramInt)
  {
    OGLSurfaceData localOGLSurfaceData;
    try
    {
      localOGLSurfaceData = (OGLSurfaceData)surfaceData;
    }
    catch (ClassCastException localClassCastException)
    {
      throw new InvalidPipeException("wrong surface data type: " + surfaceData);
    }
    OGLContext.validateContext(localOGLSurfaceData, localOGLSurfaceData, paramSunGraphics2D.getCompClip(), paramComposite, null, paint, paramSunGraphics2D, paramInt);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\opengl\OGLMaskFill.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
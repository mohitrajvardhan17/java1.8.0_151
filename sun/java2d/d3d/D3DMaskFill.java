package sun.java2d.d3d;

import java.awt.Composite;
import sun.java2d.InvalidPipeException;
import sun.java2d.SunGraphics2D;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.GraphicsPrimitive;
import sun.java2d.loops.GraphicsPrimitiveMgr;
import sun.java2d.loops.SurfaceType;
import sun.java2d.pipe.BufferedMaskFill;

class D3DMaskFill
  extends BufferedMaskFill
{
  static void register()
  {
    GraphicsPrimitive[] arrayOfGraphicsPrimitive = { new D3DMaskFill(SurfaceType.AnyColor, CompositeType.SrcOver), new D3DMaskFill(SurfaceType.OpaqueColor, CompositeType.SrcNoEa), new D3DMaskFill(SurfaceType.GradientPaint, CompositeType.SrcOver), new D3DMaskFill(SurfaceType.OpaqueGradientPaint, CompositeType.SrcNoEa), new D3DMaskFill(SurfaceType.LinearGradientPaint, CompositeType.SrcOver), new D3DMaskFill(SurfaceType.OpaqueLinearGradientPaint, CompositeType.SrcNoEa), new D3DMaskFill(SurfaceType.RadialGradientPaint, CompositeType.SrcOver), new D3DMaskFill(SurfaceType.OpaqueRadialGradientPaint, CompositeType.SrcNoEa), new D3DMaskFill(SurfaceType.TexturePaint, CompositeType.SrcOver), new D3DMaskFill(SurfaceType.OpaqueTexturePaint, CompositeType.SrcNoEa) };
    GraphicsPrimitiveMgr.register(arrayOfGraphicsPrimitive);
  }
  
  protected D3DMaskFill(SurfaceType paramSurfaceType, CompositeType paramCompositeType)
  {
    super(D3DRenderQueue.getInstance(), paramSurfaceType, paramCompositeType, D3DSurfaceData.D3DSurface);
  }
  
  protected native void maskFill(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, byte[] paramArrayOfByte);
  
  protected void validateContext(SunGraphics2D paramSunGraphics2D, Composite paramComposite, int paramInt)
  {
    D3DSurfaceData localD3DSurfaceData;
    try
    {
      localD3DSurfaceData = (D3DSurfaceData)surfaceData;
    }
    catch (ClassCastException localClassCastException)
    {
      throw new InvalidPipeException("wrong surface data type: " + surfaceData);
    }
    D3DContext.validateContext(localD3DSurfaceData, localD3DSurfaceData, paramSunGraphics2D.getCompClip(), paramComposite, null, paint, paramSunGraphics2D, paramInt);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\d3d\D3DMaskFill.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
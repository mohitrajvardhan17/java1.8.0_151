package sun.java2d.opengl;

import java.awt.Composite;
import sun.font.GlyphList;
import sun.java2d.SunGraphics2D;
import sun.java2d.loops.GraphicsPrimitive;
import sun.java2d.pipe.BufferedTextPipe;
import sun.java2d.pipe.RenderQueue;

class OGLTextRenderer
  extends BufferedTextPipe
{
  OGLTextRenderer(RenderQueue paramRenderQueue)
  {
    super(paramRenderQueue);
  }
  
  protected native void drawGlyphList(int paramInt1, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, int paramInt2, float paramFloat1, float paramFloat2, long[] paramArrayOfLong, float[] paramArrayOfFloat);
  
  protected void validateContext(SunGraphics2D paramSunGraphics2D, Composite paramComposite)
  {
    OGLSurfaceData localOGLSurfaceData = (OGLSurfaceData)surfaceData;
    OGLContext.validateContext(localOGLSurfaceData, localOGLSurfaceData, paramSunGraphics2D.getCompClip(), paramComposite, null, paint, paramSunGraphics2D, 0);
  }
  
  OGLTextRenderer traceWrap()
  {
    return new Tracer(this);
  }
  
  private static class Tracer
    extends OGLTextRenderer
  {
    Tracer(OGLTextRenderer paramOGLTextRenderer)
    {
      super();
    }
    
    protected void drawGlyphList(SunGraphics2D paramSunGraphics2D, GlyphList paramGlyphList)
    {
      GraphicsPrimitive.tracePrimitive("OGLDrawGlyphs");
      super.drawGlyphList(paramSunGraphics2D, paramGlyphList);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\opengl\OGLTextRenderer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
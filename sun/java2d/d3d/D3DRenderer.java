package sun.java2d.d3d;

import java.awt.Paint;
import java.awt.geom.Path2D.Float;
import sun.java2d.InvalidPipeException;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;
import sun.java2d.loops.GraphicsPrimitive;
import sun.java2d.pipe.BufferedRenderPipe;
import sun.java2d.pipe.ParallelogramPipe;
import sun.java2d.pipe.RenderBuffer;
import sun.java2d.pipe.RenderQueue;
import sun.java2d.pipe.SpanIterator;

class D3DRenderer
  extends BufferedRenderPipe
{
  D3DRenderer(RenderQueue paramRenderQueue)
  {
    super(paramRenderQueue);
  }
  
  protected void validateContext(SunGraphics2D paramSunGraphics2D)
  {
    int i = paint.getTransparency() == 1 ? 1 : 0;
    D3DSurfaceData localD3DSurfaceData;
    try
    {
      localD3DSurfaceData = (D3DSurfaceData)surfaceData;
    }
    catch (ClassCastException localClassCastException)
    {
      throw new InvalidPipeException("wrong surface data type: " + surfaceData);
    }
    D3DContext.validateContext(localD3DSurfaceData, localD3DSurfaceData, paramSunGraphics2D.getCompClip(), composite, null, paint, paramSunGraphics2D, i);
  }
  
  protected void validateContextAA(SunGraphics2D paramSunGraphics2D)
  {
    int i = 0;
    D3DSurfaceData localD3DSurfaceData;
    try
    {
      localD3DSurfaceData = (D3DSurfaceData)surfaceData;
    }
    catch (ClassCastException localClassCastException)
    {
      throw new InvalidPipeException("wrong surface data type: " + surfaceData);
    }
    D3DContext.validateContext(localD3DSurfaceData, localD3DSurfaceData, paramSunGraphics2D.getCompClip(), composite, null, paint, paramSunGraphics2D, i);
  }
  
  void copyArea(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    rq.lock();
    try
    {
      int i = surfaceData.getTransparency() == 1 ? 1 : 0;
      D3DSurfaceData localD3DSurfaceData;
      try
      {
        localD3DSurfaceData = (D3DSurfaceData)surfaceData;
      }
      catch (ClassCastException localClassCastException)
      {
        throw new InvalidPipeException("wrong surface data type: " + surfaceData);
      }
      D3DContext.validateContext(localD3DSurfaceData, localD3DSurfaceData, paramSunGraphics2D.getCompClip(), composite, null, null, null, i);
      rq.ensureCapacity(28);
      buf.putInt(30);
      buf.putInt(paramInt1).putInt(paramInt2).putInt(paramInt3).putInt(paramInt4);
      buf.putInt(paramInt5).putInt(paramInt6);
    }
    finally
    {
      rq.unlock();
    }
  }
  
  protected native void drawPoly(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt1, boolean paramBoolean, int paramInt2, int paramInt3);
  
  D3DRenderer traceWrap()
  {
    return new Tracer(this);
  }
  
  private class Tracer
    extends D3DRenderer
  {
    private D3DRenderer d3dr;
    
    Tracer(D3DRenderer paramD3DRenderer)
    {
      super();
      d3dr = paramD3DRenderer;
    }
    
    public ParallelogramPipe getAAParallelogramPipe()
    {
      final ParallelogramPipe localParallelogramPipe = d3dr.getAAParallelogramPipe();
      new ParallelogramPipe()
      {
        public void fillParallelogram(SunGraphics2D paramAnonymousSunGraphics2D, double paramAnonymousDouble1, double paramAnonymousDouble2, double paramAnonymousDouble3, double paramAnonymousDouble4, double paramAnonymousDouble5, double paramAnonymousDouble6, double paramAnonymousDouble7, double paramAnonymousDouble8, double paramAnonymousDouble9, double paramAnonymousDouble10)
        {
          GraphicsPrimitive.tracePrimitive("D3DFillAAParallelogram");
          localParallelogramPipe.fillParallelogram(paramAnonymousSunGraphics2D, paramAnonymousDouble1, paramAnonymousDouble2, paramAnonymousDouble3, paramAnonymousDouble4, paramAnonymousDouble5, paramAnonymousDouble6, paramAnonymousDouble7, paramAnonymousDouble8, paramAnonymousDouble9, paramAnonymousDouble10);
        }
        
        public void drawParallelogram(SunGraphics2D paramAnonymousSunGraphics2D, double paramAnonymousDouble1, double paramAnonymousDouble2, double paramAnonymousDouble3, double paramAnonymousDouble4, double paramAnonymousDouble5, double paramAnonymousDouble6, double paramAnonymousDouble7, double paramAnonymousDouble8, double paramAnonymousDouble9, double paramAnonymousDouble10, double paramAnonymousDouble11, double paramAnonymousDouble12)
        {
          GraphicsPrimitive.tracePrimitive("D3DDrawAAParallelogram");
          localParallelogramPipe.drawParallelogram(paramAnonymousSunGraphics2D, paramAnonymousDouble1, paramAnonymousDouble2, paramAnonymousDouble3, paramAnonymousDouble4, paramAnonymousDouble5, paramAnonymousDouble6, paramAnonymousDouble7, paramAnonymousDouble8, paramAnonymousDouble9, paramAnonymousDouble10, paramAnonymousDouble11, paramAnonymousDouble12);
        }
      };
    }
    
    protected void validateContext(SunGraphics2D paramSunGraphics2D)
    {
      d3dr.validateContext(paramSunGraphics2D);
    }
    
    public void drawLine(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      GraphicsPrimitive.tracePrimitive("D3DDrawLine");
      d3dr.drawLine(paramSunGraphics2D, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void drawRect(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      GraphicsPrimitive.tracePrimitive("D3DDrawRect");
      d3dr.drawRect(paramSunGraphics2D, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    protected void drawPoly(SunGraphics2D paramSunGraphics2D, int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt, boolean paramBoolean)
    {
      GraphicsPrimitive.tracePrimitive("D3DDrawPoly");
      d3dr.drawPoly(paramSunGraphics2D, paramArrayOfInt1, paramArrayOfInt2, paramInt, paramBoolean);
    }
    
    public void fillRect(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      GraphicsPrimitive.tracePrimitive("D3DFillRect");
      d3dr.fillRect(paramSunGraphics2D, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    protected void drawPath(SunGraphics2D paramSunGraphics2D, Path2D.Float paramFloat, int paramInt1, int paramInt2)
    {
      GraphicsPrimitive.tracePrimitive("D3DDrawPath");
      d3dr.drawPath(paramSunGraphics2D, paramFloat, paramInt1, paramInt2);
    }
    
    protected void fillPath(SunGraphics2D paramSunGraphics2D, Path2D.Float paramFloat, int paramInt1, int paramInt2)
    {
      GraphicsPrimitive.tracePrimitive("D3DFillPath");
      d3dr.fillPath(paramSunGraphics2D, paramFloat, paramInt1, paramInt2);
    }
    
    protected void fillSpans(SunGraphics2D paramSunGraphics2D, SpanIterator paramSpanIterator, int paramInt1, int paramInt2)
    {
      GraphicsPrimitive.tracePrimitive("D3DFillSpans");
      d3dr.fillSpans(paramSunGraphics2D, paramSpanIterator, paramInt1, paramInt2);
    }
    
    public void fillParallelogram(SunGraphics2D paramSunGraphics2D, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6, double paramDouble7, double paramDouble8, double paramDouble9, double paramDouble10)
    {
      GraphicsPrimitive.tracePrimitive("D3DFillParallelogram");
      d3dr.fillParallelogram(paramSunGraphics2D, paramDouble1, paramDouble2, paramDouble3, paramDouble4, paramDouble5, paramDouble6, paramDouble7, paramDouble8, paramDouble9, paramDouble10);
    }
    
    public void drawParallelogram(SunGraphics2D paramSunGraphics2D, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6, double paramDouble7, double paramDouble8, double paramDouble9, double paramDouble10, double paramDouble11, double paramDouble12)
    {
      GraphicsPrimitive.tracePrimitive("D3DDrawParallelogram");
      d3dr.drawParallelogram(paramSunGraphics2D, paramDouble1, paramDouble2, paramDouble3, paramDouble4, paramDouble5, paramDouble6, paramDouble7, paramDouble8, paramDouble9, paramDouble10, paramDouble11, paramDouble12);
    }
    
    public void copyArea(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
    {
      GraphicsPrimitive.tracePrimitive("D3DCopyArea");
      d3dr.copyArea(paramSunGraphics2D, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\d3d\D3DRenderer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
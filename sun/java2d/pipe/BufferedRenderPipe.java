package sun.java2d.pipe;

import java.awt.Polygon;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D.Float;
import java.awt.geom.Ellipse2D.Float;
import java.awt.geom.Path2D.Float;
import java.awt.geom.RoundRectangle2D.Float;
import sun.java2d.SunGraphics2D;
import sun.java2d.loops.ProcessPath;
import sun.java2d.loops.ProcessPath.DrawHandler;

public abstract class BufferedRenderPipe
  implements PixelDrawPipe, PixelFillPipe, ShapeDrawPipe, ParallelogramPipe
{
  ParallelogramPipe aapgrampipe = new AAParallelogramPipe(null);
  static final int BYTES_PER_POLY_POINT = 8;
  static final int BYTES_PER_SCANLINE = 12;
  static final int BYTES_PER_SPAN = 16;
  protected RenderQueue rq;
  protected RenderBuffer buf;
  private BufferedDrawHandler drawHandler;
  
  public BufferedRenderPipe(RenderQueue paramRenderQueue)
  {
    rq = paramRenderQueue;
    buf = paramRenderQueue.getBuffer();
    drawHandler = new BufferedDrawHandler();
  }
  
  public ParallelogramPipe getAAParallelogramPipe()
  {
    return aapgrampipe;
  }
  
  protected abstract void validateContext(SunGraphics2D paramSunGraphics2D);
  
  protected abstract void validateContextAA(SunGraphics2D paramSunGraphics2D);
  
  public void drawLine(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int i = transX;
    int j = transY;
    rq.lock();
    try
    {
      validateContext(paramSunGraphics2D);
      rq.ensureCapacity(20);
      buf.putInt(10);
      buf.putInt(paramInt1 + i);
      buf.putInt(paramInt2 + j);
      buf.putInt(paramInt3 + i);
      buf.putInt(paramInt4 + j);
    }
    finally
    {
      rq.unlock();
    }
  }
  
  public void drawRect(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    rq.lock();
    try
    {
      validateContext(paramSunGraphics2D);
      rq.ensureCapacity(20);
      buf.putInt(11);
      buf.putInt(paramInt1 + transX);
      buf.putInt(paramInt2 + transY);
      buf.putInt(paramInt3);
      buf.putInt(paramInt4);
    }
    finally
    {
      rq.unlock();
    }
  }
  
  public void fillRect(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    rq.lock();
    try
    {
      validateContext(paramSunGraphics2D);
      rq.ensureCapacity(20);
      buf.putInt(20);
      buf.putInt(paramInt1 + transX);
      buf.putInt(paramInt2 + transY);
      buf.putInt(paramInt3);
      buf.putInt(paramInt4);
    }
    finally
    {
      rq.unlock();
    }
  }
  
  public void drawRoundRect(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    draw(paramSunGraphics2D, new RoundRectangle2D.Float(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6));
  }
  
  public void fillRoundRect(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    fill(paramSunGraphics2D, new RoundRectangle2D.Float(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6));
  }
  
  public void drawOval(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    draw(paramSunGraphics2D, new Ellipse2D.Float(paramInt1, paramInt2, paramInt3, paramInt4));
  }
  
  public void fillOval(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    fill(paramSunGraphics2D, new Ellipse2D.Float(paramInt1, paramInt2, paramInt3, paramInt4));
  }
  
  public void drawArc(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    draw(paramSunGraphics2D, new Arc2D.Float(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, 0));
  }
  
  public void fillArc(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    fill(paramSunGraphics2D, new Arc2D.Float(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, 2));
  }
  
  protected void drawPoly(final SunGraphics2D paramSunGraphics2D, final int[] paramArrayOfInt1, final int[] paramArrayOfInt2, final int paramInt, final boolean paramBoolean)
  {
    if ((paramArrayOfInt1 == null) || (paramArrayOfInt2 == null)) {
      throw new NullPointerException("coordinate array");
    }
    if ((paramArrayOfInt1.length < paramInt) || (paramArrayOfInt2.length < paramInt)) {
      throw new ArrayIndexOutOfBoundsException("coordinate array");
    }
    if (paramInt < 2) {
      return;
    }
    if ((paramInt == 2) && (!paramBoolean))
    {
      drawLine(paramSunGraphics2D, paramArrayOfInt1[0], paramArrayOfInt2[0], paramArrayOfInt1[1], paramArrayOfInt2[1]);
      return;
    }
    rq.lock();
    try
    {
      validateContext(paramSunGraphics2D);
      int i = paramInt * 8;
      int j = 20 + i;
      if (j <= buf.capacity())
      {
        if (j > buf.remaining()) {
          rq.flushNow();
        }
        buf.putInt(12);
        buf.putInt(paramInt);
        buf.putInt(paramBoolean ? 1 : 0);
        buf.putInt(transX);
        buf.putInt(transY);
        buf.put(paramArrayOfInt1, 0, paramInt);
        buf.put(paramArrayOfInt2, 0, paramInt);
      }
      else
      {
        rq.flushAndInvokeNow(new Runnable()
        {
          public void run()
          {
            drawPoly(paramArrayOfInt1, paramArrayOfInt2, paramInt, paramBoolean, paramSunGraphics2DtransX, paramSunGraphics2DtransY);
          }
        });
      }
    }
    finally
    {
      rq.unlock();
    }
  }
  
  protected abstract void drawPoly(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt1, boolean paramBoolean, int paramInt2, int paramInt3);
  
  public void drawPolyline(SunGraphics2D paramSunGraphics2D, int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt)
  {
    drawPoly(paramSunGraphics2D, paramArrayOfInt1, paramArrayOfInt2, paramInt, false);
  }
  
  public void drawPolygon(SunGraphics2D paramSunGraphics2D, int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt)
  {
    drawPoly(paramSunGraphics2D, paramArrayOfInt1, paramArrayOfInt2, paramInt, true);
  }
  
  public void fillPolygon(SunGraphics2D paramSunGraphics2D, int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt)
  {
    fill(paramSunGraphics2D, new Polygon(paramArrayOfInt1, paramArrayOfInt2, paramInt));
  }
  
  protected void drawPath(SunGraphics2D paramSunGraphics2D, Path2D.Float paramFloat, int paramInt1, int paramInt2)
  {
    rq.lock();
    try
    {
      validateContext(paramSunGraphics2D);
      drawHandler.validate(paramSunGraphics2D);
      ProcessPath.drawPath(drawHandler, paramFloat, paramInt1, paramInt2);
    }
    finally
    {
      rq.unlock();
    }
  }
  
  protected void fillPath(SunGraphics2D paramSunGraphics2D, Path2D.Float paramFloat, int paramInt1, int paramInt2)
  {
    rq.lock();
    try
    {
      validateContext(paramSunGraphics2D);
      drawHandler.validate(paramSunGraphics2D);
      drawHandler.startFillPath();
      ProcessPath.fillPath(drawHandler, paramFloat, paramInt1, paramInt2);
      drawHandler.endFillPath();
    }
    finally
    {
      rq.unlock();
    }
  }
  
  private native int fillSpans(RenderQueue paramRenderQueue, long paramLong1, int paramInt1, int paramInt2, SpanIterator paramSpanIterator, long paramLong2, int paramInt3, int paramInt4);
  
  protected void fillSpans(SunGraphics2D paramSunGraphics2D, SpanIterator paramSpanIterator, int paramInt1, int paramInt2)
  {
    rq.lock();
    try
    {
      validateContext(paramSunGraphics2D);
      rq.ensureCapacity(24);
      int i = fillSpans(rq, buf.getAddress(), buf.position(), buf.capacity(), paramSpanIterator, paramSpanIterator.getNativeIterator(), paramInt1, paramInt2);
      buf.position(i);
    }
    finally
    {
      rq.unlock();
    }
  }
  
  public void fillParallelogram(SunGraphics2D paramSunGraphics2D, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6, double paramDouble7, double paramDouble8, double paramDouble9, double paramDouble10)
  {
    rq.lock();
    try
    {
      validateContext(paramSunGraphics2D);
      rq.ensureCapacity(28);
      buf.putInt(22);
      buf.putFloat((float)paramDouble5);
      buf.putFloat((float)paramDouble6);
      buf.putFloat((float)paramDouble7);
      buf.putFloat((float)paramDouble8);
      buf.putFloat((float)paramDouble9);
      buf.putFloat((float)paramDouble10);
    }
    finally
    {
      rq.unlock();
    }
  }
  
  public void drawParallelogram(SunGraphics2D paramSunGraphics2D, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6, double paramDouble7, double paramDouble8, double paramDouble9, double paramDouble10, double paramDouble11, double paramDouble12)
  {
    rq.lock();
    try
    {
      validateContext(paramSunGraphics2D);
      rq.ensureCapacity(36);
      buf.putInt(15);
      buf.putFloat((float)paramDouble5);
      buf.putFloat((float)paramDouble6);
      buf.putFloat((float)paramDouble7);
      buf.putFloat((float)paramDouble8);
      buf.putFloat((float)paramDouble9);
      buf.putFloat((float)paramDouble10);
      buf.putFloat((float)paramDouble11);
      buf.putFloat((float)paramDouble12);
    }
    finally
    {
      rq.unlock();
    }
  }
  
  public void draw(SunGraphics2D paramSunGraphics2D, Shape paramShape)
  {
    Object localObject1;
    if (strokeState == 0)
    {
      if (((paramShape instanceof Polygon)) && (transformState < 3))
      {
        localObject1 = (Polygon)paramShape;
        drawPolygon(paramSunGraphics2D, xpoints, ypoints, npoints);
        return;
      }
      int i;
      int j;
      if (transformState <= 1)
      {
        if ((paramShape instanceof Path2D.Float)) {
          localObject1 = (Path2D.Float)paramShape;
        } else {
          localObject1 = new Path2D.Float(paramShape);
        }
        i = transX;
        j = transY;
      }
      else
      {
        localObject1 = new Path2D.Float(paramShape, transform);
        i = 0;
        j = 0;
      }
      drawPath(paramSunGraphics2D, (Path2D.Float)localObject1, i, j);
    }
    else if (strokeState < 3)
    {
      localObject1 = LoopPipe.getStrokeSpans(paramSunGraphics2D, paramShape);
      try
      {
        fillSpans(paramSunGraphics2D, (SpanIterator)localObject1, 0, 0);
      }
      finally
      {
        ((ShapeSpanIterator)localObject1).dispose();
      }
    }
    else
    {
      fill(paramSunGraphics2D, stroke.createStrokedShape(paramShape));
    }
  }
  
  public void fill(SunGraphics2D paramSunGraphics2D, Shape paramShape)
  {
    Object localObject1;
    int i;
    int j;
    if (strokeState == 0)
    {
      if (transformState <= 1)
      {
        if ((paramShape instanceof Path2D.Float)) {
          localObject1 = (Path2D.Float)paramShape;
        } else {
          localObject1 = new Path2D.Float(paramShape);
        }
        i = transX;
        j = transY;
      }
      else
      {
        localObject1 = new Path2D.Float(paramShape, transform);
        i = 0;
        j = 0;
      }
      fillPath(paramSunGraphics2D, (Path2D.Float)localObject1, i, j);
      return;
    }
    if (transformState <= 1)
    {
      localObject1 = null;
      i = transX;
      j = transY;
    }
    else
    {
      localObject1 = transform;
      i = j = 0;
    }
    ShapeSpanIterator localShapeSpanIterator = LoopPipe.getFillSSI(paramSunGraphics2D);
    try
    {
      Region localRegion = paramSunGraphics2D.getCompClip();
      localShapeSpanIterator.setOutputAreaXYXY(localRegion.getLoX() - i, localRegion.getLoY() - j, localRegion.getHiX() - i, localRegion.getHiY() - j);
      localShapeSpanIterator.appendPath(paramShape.getPathIterator((AffineTransform)localObject1));
      fillSpans(paramSunGraphics2D, localShapeSpanIterator, i, j);
    }
    finally
    {
      localShapeSpanIterator.dispose();
    }
  }
  
  private class AAParallelogramPipe
    implements ParallelogramPipe
  {
    private AAParallelogramPipe() {}
    
    public void fillParallelogram(SunGraphics2D paramSunGraphics2D, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6, double paramDouble7, double paramDouble8, double paramDouble9, double paramDouble10)
    {
      rq.lock();
      try
      {
        validateContextAA(paramSunGraphics2D);
        rq.ensureCapacity(28);
        buf.putInt(23);
        buf.putFloat((float)paramDouble5);
        buf.putFloat((float)paramDouble6);
        buf.putFloat((float)paramDouble7);
        buf.putFloat((float)paramDouble8);
        buf.putFloat((float)paramDouble9);
        buf.putFloat((float)paramDouble10);
      }
      finally
      {
        rq.unlock();
      }
    }
    
    public void drawParallelogram(SunGraphics2D paramSunGraphics2D, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6, double paramDouble7, double paramDouble8, double paramDouble9, double paramDouble10, double paramDouble11, double paramDouble12)
    {
      rq.lock();
      try
      {
        validateContextAA(paramSunGraphics2D);
        rq.ensureCapacity(36);
        buf.putInt(16);
        buf.putFloat((float)paramDouble5);
        buf.putFloat((float)paramDouble6);
        buf.putFloat((float)paramDouble7);
        buf.putFloat((float)paramDouble8);
        buf.putFloat((float)paramDouble9);
        buf.putFloat((float)paramDouble10);
        buf.putFloat((float)paramDouble11);
        buf.putFloat((float)paramDouble12);
      }
      finally
      {
        rq.unlock();
      }
    }
  }
  
  private class BufferedDrawHandler
    extends ProcessPath.DrawHandler
  {
    private int scanlineCount;
    private int scanlineCountIndex;
    private int remainingScanlines;
    
    BufferedDrawHandler()
    {
      super(0, 0, 0);
    }
    
    void validate(SunGraphics2D paramSunGraphics2D)
    {
      Region localRegion = paramSunGraphics2D.getCompClip();
      setBounds(localRegion.getLoX(), localRegion.getLoY(), localRegion.getHiX(), localRegion.getHiY(), strokeHint);
    }
    
    public void drawLine(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      rq.ensureCapacity(20);
      buf.putInt(10);
      buf.putInt(paramInt1);
      buf.putInt(paramInt2);
      buf.putInt(paramInt3);
      buf.putInt(paramInt4);
    }
    
    public void drawPixel(int paramInt1, int paramInt2)
    {
      rq.ensureCapacity(12);
      buf.putInt(13);
      buf.putInt(paramInt1);
      buf.putInt(paramInt2);
    }
    
    private void resetFillPath()
    {
      buf.putInt(14);
      scanlineCountIndex = buf.position();
      buf.putInt(0);
      scanlineCount = 0;
      remainingScanlines = (buf.remaining() / 12);
    }
    
    private void updateScanlineCount()
    {
      buf.putInt(scanlineCountIndex, scanlineCount);
    }
    
    public void startFillPath()
    {
      rq.ensureCapacity(20);
      resetFillPath();
    }
    
    public void drawScanline(int paramInt1, int paramInt2, int paramInt3)
    {
      if (remainingScanlines == 0)
      {
        updateScanlineCount();
        rq.flushNow();
        resetFillPath();
      }
      buf.putInt(paramInt1);
      buf.putInt(paramInt2);
      buf.putInt(paramInt3);
      scanlineCount += 1;
      remainingScanlines -= 1;
    }
    
    public void endFillPath()
    {
      updateScanlineCount();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\pipe\BufferedRenderPipe.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
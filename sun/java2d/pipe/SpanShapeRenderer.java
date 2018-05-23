package sun.java2d.pipe;

import java.awt.BasicStroke;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;
import sun.java2d.loops.FillRect;
import sun.java2d.loops.RenderLoops;

public abstract class SpanShapeRenderer
  implements ShapeDrawPipe
{
  static final RenderingEngine RenderEngine = ;
  public static final int NON_RECTILINEAR_TRANSFORM_MASK = 48;
  
  public SpanShapeRenderer() {}
  
  public void draw(SunGraphics2D paramSunGraphics2D, Shape paramShape)
  {
    if ((stroke instanceof BasicStroke))
    {
      ShapeSpanIterator localShapeSpanIterator = LoopPipe.getStrokeSpans(paramSunGraphics2D, paramShape);
      try
      {
        renderSpans(paramSunGraphics2D, paramSunGraphics2D.getCompClip(), paramShape, localShapeSpanIterator);
      }
      finally
      {
        localShapeSpanIterator.dispose();
      }
    }
    else
    {
      fill(paramSunGraphics2D, stroke.createStrokedShape(paramShape));
    }
  }
  
  public void fill(SunGraphics2D paramSunGraphics2D, Shape paramShape)
  {
    if (((paramShape instanceof Rectangle2D)) && ((transform.getType() & 0x30) == 0))
    {
      renderRect(paramSunGraphics2D, (Rectangle2D)paramShape);
      return;
    }
    Region localRegion = paramSunGraphics2D.getCompClip();
    ShapeSpanIterator localShapeSpanIterator = LoopPipe.getFillSSI(paramSunGraphics2D);
    try
    {
      localShapeSpanIterator.setOutputArea(localRegion);
      localShapeSpanIterator.appendPath(paramShape.getPathIterator(transform));
      renderSpans(paramSunGraphics2D, localRegion, paramShape, localShapeSpanIterator);
    }
    finally
    {
      localShapeSpanIterator.dispose();
    }
  }
  
  public abstract Object startSequence(SunGraphics2D paramSunGraphics2D, Shape paramShape, Rectangle paramRectangle, int[] paramArrayOfInt);
  
  public abstract void renderBox(Object paramObject, int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  public abstract void endSequence(Object paramObject);
  
  public void renderRect(SunGraphics2D paramSunGraphics2D, Rectangle2D paramRectangle2D)
  {
    double[] arrayOfDouble = { paramRectangle2D.getX(), paramRectangle2D.getY(), paramRectangle2D.getWidth(), paramRectangle2D.getHeight() };
    arrayOfDouble[2] += arrayOfDouble[0];
    arrayOfDouble[3] += arrayOfDouble[1];
    if ((arrayOfDouble[2] <= arrayOfDouble[0]) || (arrayOfDouble[3] <= arrayOfDouble[1])) {
      return;
    }
    transform.transform(arrayOfDouble, 0, arrayOfDouble, 0, 2);
    double d;
    if (arrayOfDouble[2] < arrayOfDouble[0])
    {
      d = arrayOfDouble[2];
      arrayOfDouble[2] = arrayOfDouble[0];
      arrayOfDouble[0] = d;
    }
    if (arrayOfDouble[3] < arrayOfDouble[1])
    {
      d = arrayOfDouble[3];
      arrayOfDouble[3] = arrayOfDouble[1];
      arrayOfDouble[1] = d;
    }
    int[] arrayOfInt = { (int)arrayOfDouble[0], (int)arrayOfDouble[1], (int)arrayOfDouble[2], (int)arrayOfDouble[3] };
    Rectangle localRectangle = new Rectangle(arrayOfInt[0], arrayOfInt[1], arrayOfInt[2] - arrayOfInt[0], arrayOfInt[3] - arrayOfInt[1]);
    Region localRegion = paramSunGraphics2D.getCompClip();
    localRegion.clipBoxToBounds(arrayOfInt);
    if ((arrayOfInt[0] >= arrayOfInt[2]) || (arrayOfInt[1] >= arrayOfInt[3])) {
      return;
    }
    Object localObject = startSequence(paramSunGraphics2D, paramRectangle2D, localRectangle, arrayOfInt);
    if (localRegion.isRectangular())
    {
      renderBox(localObject, arrayOfInt[0], arrayOfInt[1], arrayOfInt[2] - arrayOfInt[0], arrayOfInt[3] - arrayOfInt[1]);
    }
    else
    {
      SpanIterator localSpanIterator = localRegion.getSpanIterator(arrayOfInt);
      while (localSpanIterator.nextSpan(arrayOfInt)) {
        renderBox(localObject, arrayOfInt[0], arrayOfInt[1], arrayOfInt[2] - arrayOfInt[0], arrayOfInt[3] - arrayOfInt[1]);
      }
    }
    endSequence(localObject);
  }
  
  public void renderSpans(SunGraphics2D paramSunGraphics2D, Region paramRegion, Shape paramShape, ShapeSpanIterator paramShapeSpanIterator)
  {
    Object localObject1 = null;
    int[] arrayOfInt = new int[4];
    try
    {
      paramShapeSpanIterator.getPathBox(arrayOfInt);
      Rectangle localRectangle = new Rectangle(arrayOfInt[0], arrayOfInt[1], arrayOfInt[2] - arrayOfInt[0], arrayOfInt[3] - arrayOfInt[1]);
      paramRegion.clipBoxToBounds(arrayOfInt);
      if ((arrayOfInt[0] >= arrayOfInt[2]) || (arrayOfInt[1] >= arrayOfInt[3])) {
        return;
      }
      paramShapeSpanIterator.intersectClipBox(arrayOfInt[0], arrayOfInt[1], arrayOfInt[2], arrayOfInt[3]);
      localObject1 = startSequence(paramSunGraphics2D, paramShape, localRectangle, arrayOfInt);
      spanClipLoop(localObject1, paramShapeSpanIterator, paramRegion, arrayOfInt);
    }
    finally
    {
      if (localObject1 != null) {
        endSequence(localObject1);
      }
    }
  }
  
  public void spanClipLoop(Object paramObject, SpanIterator paramSpanIterator, Region paramRegion, int[] paramArrayOfInt)
  {
    if (!paramRegion.isRectangular()) {
      paramSpanIterator = paramRegion.filter(paramSpanIterator);
    }
    while (paramSpanIterator.nextSpan(paramArrayOfInt))
    {
      int i = paramArrayOfInt[0];
      int j = paramArrayOfInt[1];
      renderBox(paramObject, i, j, paramArrayOfInt[2] - i, paramArrayOfInt[3] - j);
    }
  }
  
  public static class Composite
    extends SpanShapeRenderer
  {
    CompositePipe comppipe;
    
    public Composite(CompositePipe paramCompositePipe)
    {
      comppipe = paramCompositePipe;
    }
    
    public Object startSequence(SunGraphics2D paramSunGraphics2D, Shape paramShape, Rectangle paramRectangle, int[] paramArrayOfInt)
    {
      return comppipe.startSequence(paramSunGraphics2D, paramShape, paramRectangle, paramArrayOfInt);
    }
    
    public void renderBox(Object paramObject, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      comppipe.renderPathTile(paramObject, null, 0, paramInt3, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void endSequence(Object paramObject)
    {
      comppipe.endSequence(paramObject);
    }
  }
  
  public static class Simple
    extends SpanShapeRenderer
    implements LoopBasedPipe
  {
    public Simple() {}
    
    public Object startSequence(SunGraphics2D paramSunGraphics2D, Shape paramShape, Rectangle paramRectangle, int[] paramArrayOfInt)
    {
      return paramSunGraphics2D;
    }
    
    public void renderBox(Object paramObject, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      SunGraphics2D localSunGraphics2D = (SunGraphics2D)paramObject;
      SurfaceData localSurfaceData = localSunGraphics2D.getSurfaceData();
      loops.fillRectLoop.FillRect(localSunGraphics2D, localSurfaceData, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void endSequence(Object paramObject) {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\pipe\SpanShapeRenderer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
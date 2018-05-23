package sun.java2d.pipe;

import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D.Float;
import java.awt.geom.Ellipse2D.Float;
import java.awt.geom.Path2D.Float;
import java.awt.geom.RoundRectangle2D.Float;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;
import sun.java2d.loops.DrawLine;
import sun.java2d.loops.DrawParallelogram;
import sun.java2d.loops.DrawPath;
import sun.java2d.loops.DrawPolygons;
import sun.java2d.loops.DrawRect;
import sun.java2d.loops.FillParallelogram;
import sun.java2d.loops.FillPath;
import sun.java2d.loops.FillRect;
import sun.java2d.loops.FillSpans;
import sun.java2d.loops.RenderLoops;

public class LoopPipe
  implements PixelDrawPipe, PixelFillPipe, ParallelogramPipe, ShapeDrawPipe, LoopBasedPipe
{
  static final RenderingEngine RenderEngine = ;
  
  public LoopPipe() {}
  
  public void drawLine(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int i = transX;
    int j = transY;
    loops.drawLineLoop.DrawLine(paramSunGraphics2D, paramSunGraphics2D.getSurfaceData(), paramInt1 + i, paramInt2 + j, paramInt3 + i, paramInt4 + j);
  }
  
  public void drawRect(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    loops.drawRectLoop.DrawRect(paramSunGraphics2D, paramSunGraphics2D.getSurfaceData(), paramInt1 + transX, paramInt2 + transY, paramInt3, paramInt4);
  }
  
  public void drawRoundRect(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    shapepipe.draw(paramSunGraphics2D, new RoundRectangle2D.Float(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6));
  }
  
  public void drawOval(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    shapepipe.draw(paramSunGraphics2D, new Ellipse2D.Float(paramInt1, paramInt2, paramInt3, paramInt4));
  }
  
  public void drawArc(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    shapepipe.draw(paramSunGraphics2D, new Arc2D.Float(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, 0));
  }
  
  public void drawPolyline(SunGraphics2D paramSunGraphics2D, int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt)
  {
    int[] arrayOfInt = { paramInt };
    loops.drawPolygonsLoop.DrawPolygons(paramSunGraphics2D, paramSunGraphics2D.getSurfaceData(), paramArrayOfInt1, paramArrayOfInt2, arrayOfInt, 1, transX, transY, false);
  }
  
  public void drawPolygon(SunGraphics2D paramSunGraphics2D, int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt)
  {
    int[] arrayOfInt = { paramInt };
    loops.drawPolygonsLoop.DrawPolygons(paramSunGraphics2D, paramSunGraphics2D.getSurfaceData(), paramArrayOfInt1, paramArrayOfInt2, arrayOfInt, 1, transX, transY, true);
  }
  
  public void fillRect(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    loops.fillRectLoop.FillRect(paramSunGraphics2D, paramSunGraphics2D.getSurfaceData(), paramInt1 + transX, paramInt2 + transY, paramInt3, paramInt4);
  }
  
  public void fillRoundRect(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    shapepipe.fill(paramSunGraphics2D, new RoundRectangle2D.Float(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6));
  }
  
  public void fillOval(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    shapepipe.fill(paramSunGraphics2D, new Ellipse2D.Float(paramInt1, paramInt2, paramInt3, paramInt4));
  }
  
  public void fillArc(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    shapepipe.fill(paramSunGraphics2D, new Arc2D.Float(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, 2));
  }
  
  public void fillPolygon(SunGraphics2D paramSunGraphics2D, int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt)
  {
    ShapeSpanIterator localShapeSpanIterator = getFillSSI(paramSunGraphics2D);
    try
    {
      localShapeSpanIterator.setOutputArea(paramSunGraphics2D.getCompClip());
      localShapeSpanIterator.appendPoly(paramArrayOfInt1, paramArrayOfInt2, paramInt, transX, transY);
      fillSpans(paramSunGraphics2D, localShapeSpanIterator);
    }
    finally
    {
      localShapeSpanIterator.dispose();
    }
  }
  
  public void draw(SunGraphics2D paramSunGraphics2D, Shape paramShape)
  {
    if (strokeState == 0)
    {
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
      loops.drawPathLoop.DrawPath(paramSunGraphics2D, paramSunGraphics2D.getSurfaceData(), i, j, (Path2D.Float)localObject1);
      return;
    }
    if (strokeState == 3)
    {
      fill(paramSunGraphics2D, stroke.createStrokedShape(paramShape));
      return;
    }
    Object localObject1 = getStrokeSpans(paramSunGraphics2D, paramShape);
    try
    {
      fillSpans(paramSunGraphics2D, (SpanIterator)localObject1);
    }
    finally
    {
      ((ShapeSpanIterator)localObject1).dispose();
    }
  }
  
  public static ShapeSpanIterator getFillSSI(SunGraphics2D paramSunGraphics2D)
  {
    boolean bool = ((stroke instanceof BasicStroke)) && (strokeHint != 2);
    return new ShapeSpanIterator(bool);
  }
  
  public static ShapeSpanIterator getStrokeSpans(SunGraphics2D paramSunGraphics2D, Shape paramShape)
  {
    ShapeSpanIterator localShapeSpanIterator = new ShapeSpanIterator(false);
    try
    {
      localShapeSpanIterator.setOutputArea(paramSunGraphics2D.getCompClip());
      localShapeSpanIterator.setRule(1);
      BasicStroke localBasicStroke = (BasicStroke)stroke;
      boolean bool1 = strokeState <= 1;
      boolean bool2 = strokeHint != 2;
      RenderEngine.strokeTo(paramShape, transform, localBasicStroke, bool1, bool2, false, localShapeSpanIterator);
    }
    catch (Throwable localThrowable)
    {
      localShapeSpanIterator.dispose();
      localShapeSpanIterator = null;
      throw new InternalError("Unable to Stroke shape (" + localThrowable.getMessage() + ")", localThrowable);
    }
    return localShapeSpanIterator;
  }
  
  public void fill(SunGraphics2D paramSunGraphics2D, Shape paramShape)
  {
    if (strokeState == 0)
    {
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
      loops.fillPathLoop.FillPath(paramSunGraphics2D, paramSunGraphics2D.getSurfaceData(), i, j, (Path2D.Float)localObject1);
      return;
    }
    Object localObject1 = getFillSSI(paramSunGraphics2D);
    try
    {
      ((ShapeSpanIterator)localObject1).setOutputArea(paramSunGraphics2D.getCompClip());
      AffineTransform localAffineTransform = transformState == 0 ? null : transform;
      ((ShapeSpanIterator)localObject1).appendPath(paramShape.getPathIterator(localAffineTransform));
      fillSpans(paramSunGraphics2D, (SpanIterator)localObject1);
    }
    finally
    {
      ((ShapeSpanIterator)localObject1).dispose();
    }
  }
  
  private static void fillSpans(SunGraphics2D paramSunGraphics2D, SpanIterator paramSpanIterator)
  {
    if (clipState == 2)
    {
      paramSpanIterator = clipRegion.filter(paramSpanIterator);
    }
    else
    {
      localObject = loops.fillSpansLoop;
      if (localObject != null)
      {
        ((FillSpans)localObject).FillSpans(paramSunGraphics2D, paramSunGraphics2D.getSurfaceData(), paramSpanIterator);
        return;
      }
    }
    Object localObject = new int[4];
    SurfaceData localSurfaceData = paramSunGraphics2D.getSurfaceData();
    while (paramSpanIterator.nextSpan((int[])localObject))
    {
      int i = localObject[0];
      int j = localObject[1];
      int k = localObject[2] - i;
      int m = localObject[3] - j;
      loops.fillRectLoop.FillRect(paramSunGraphics2D, localSurfaceData, i, j, k, m);
    }
  }
  
  public void fillParallelogram(SunGraphics2D paramSunGraphics2D, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6, double paramDouble7, double paramDouble8, double paramDouble9, double paramDouble10)
  {
    FillParallelogram localFillParallelogram = loops.fillParallelogramLoop;
    localFillParallelogram.FillParallelogram(paramSunGraphics2D, paramSunGraphics2D.getSurfaceData(), paramDouble5, paramDouble6, paramDouble7, paramDouble8, paramDouble9, paramDouble10);
  }
  
  public void drawParallelogram(SunGraphics2D paramSunGraphics2D, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6, double paramDouble7, double paramDouble8, double paramDouble9, double paramDouble10, double paramDouble11, double paramDouble12)
  {
    DrawParallelogram localDrawParallelogram = loops.drawParallelogramLoop;
    localDrawParallelogram.DrawParallelogram(paramSunGraphics2D, paramSunGraphics2D.getSurfaceData(), paramDouble5, paramDouble6, paramDouble7, paramDouble8, paramDouble9, paramDouble10, paramDouble11, paramDouble12);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\pipe\LoopPipe.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
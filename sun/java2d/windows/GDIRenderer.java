package sun.java2d.windows;

import java.awt.Composite;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Path2D.Float;
import sun.java2d.InvalidPipeException;
import sun.java2d.SunGraphics2D;
import sun.java2d.loops.GraphicsPrimitive;
import sun.java2d.pipe.LoopPipe;
import sun.java2d.pipe.PixelDrawPipe;
import sun.java2d.pipe.PixelFillPipe;
import sun.java2d.pipe.Region;
import sun.java2d.pipe.ShapeDrawPipe;
import sun.java2d.pipe.ShapeSpanIterator;
import sun.java2d.pipe.SpanIterator;

public class GDIRenderer
  implements PixelDrawPipe, PixelFillPipe, ShapeDrawPipe
{
  public GDIRenderer() {}
  
  native void doDrawLine(GDIWindowSurfaceData paramGDIWindowSurfaceData, Region paramRegion, Composite paramComposite, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5);
  
  public void drawLine(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int i = transX;
    int j = transY;
    try
    {
      doDrawLine((GDIWindowSurfaceData)surfaceData, paramSunGraphics2D.getCompClip(), composite, eargb, paramInt1 + i, paramInt2 + j, paramInt3 + i, paramInt4 + j);
    }
    catch (ClassCastException localClassCastException)
    {
      throw new InvalidPipeException("wrong surface data type: " + surfaceData);
    }
  }
  
  native void doDrawRect(GDIWindowSurfaceData paramGDIWindowSurfaceData, Region paramRegion, Composite paramComposite, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5);
  
  public void drawRect(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    try
    {
      doDrawRect((GDIWindowSurfaceData)surfaceData, paramSunGraphics2D.getCompClip(), composite, eargb, paramInt1 + transX, paramInt2 + transY, paramInt3, paramInt4);
    }
    catch (ClassCastException localClassCastException)
    {
      throw new InvalidPipeException("wrong surface data type: " + surfaceData);
    }
  }
  
  native void doDrawRoundRect(GDIWindowSurfaceData paramGDIWindowSurfaceData, Region paramRegion, Composite paramComposite, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7);
  
  public void drawRoundRect(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    try
    {
      doDrawRoundRect((GDIWindowSurfaceData)surfaceData, paramSunGraphics2D.getCompClip(), composite, eargb, paramInt1 + transX, paramInt2 + transY, paramInt3, paramInt4, paramInt5, paramInt6);
    }
    catch (ClassCastException localClassCastException)
    {
      throw new InvalidPipeException("wrong surface data type: " + surfaceData);
    }
  }
  
  native void doDrawOval(GDIWindowSurfaceData paramGDIWindowSurfaceData, Region paramRegion, Composite paramComposite, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5);
  
  public void drawOval(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    try
    {
      doDrawOval((GDIWindowSurfaceData)surfaceData, paramSunGraphics2D.getCompClip(), composite, eargb, paramInt1 + transX, paramInt2 + transY, paramInt3, paramInt4);
    }
    catch (ClassCastException localClassCastException)
    {
      throw new InvalidPipeException("wrong surface data type: " + surfaceData);
    }
  }
  
  native void doDrawArc(GDIWindowSurfaceData paramGDIWindowSurfaceData, Region paramRegion, Composite paramComposite, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7);
  
  public void drawArc(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    try
    {
      doDrawArc((GDIWindowSurfaceData)surfaceData, paramSunGraphics2D.getCompClip(), composite, eargb, paramInt1 + transX, paramInt2 + transY, paramInt3, paramInt4, paramInt5, paramInt6);
    }
    catch (ClassCastException localClassCastException)
    {
      throw new InvalidPipeException("wrong surface data type: " + surfaceData);
    }
  }
  
  native void doDrawPoly(GDIWindowSurfaceData paramGDIWindowSurfaceData, Region paramRegion, Composite paramComposite, int paramInt1, int paramInt2, int paramInt3, int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt4, boolean paramBoolean);
  
  public void drawPolyline(SunGraphics2D paramSunGraphics2D, int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt)
  {
    try
    {
      doDrawPoly((GDIWindowSurfaceData)surfaceData, paramSunGraphics2D.getCompClip(), composite, eargb, transX, transY, paramArrayOfInt1, paramArrayOfInt2, paramInt, false);
    }
    catch (ClassCastException localClassCastException)
    {
      throw new InvalidPipeException("wrong surface data type: " + surfaceData);
    }
  }
  
  public void drawPolygon(SunGraphics2D paramSunGraphics2D, int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt)
  {
    try
    {
      doDrawPoly((GDIWindowSurfaceData)surfaceData, paramSunGraphics2D.getCompClip(), composite, eargb, transX, transY, paramArrayOfInt1, paramArrayOfInt2, paramInt, true);
    }
    catch (ClassCastException localClassCastException)
    {
      throw new InvalidPipeException("wrong surface data type: " + surfaceData);
    }
  }
  
  native void doFillRect(GDIWindowSurfaceData paramGDIWindowSurfaceData, Region paramRegion, Composite paramComposite, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5);
  
  public void fillRect(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    try
    {
      doFillRect((GDIWindowSurfaceData)surfaceData, paramSunGraphics2D.getCompClip(), composite, eargb, paramInt1 + transX, paramInt2 + transY, paramInt3, paramInt4);
    }
    catch (ClassCastException localClassCastException)
    {
      throw new InvalidPipeException("wrong surface data type: " + surfaceData);
    }
  }
  
  native void doFillRoundRect(GDIWindowSurfaceData paramGDIWindowSurfaceData, Region paramRegion, Composite paramComposite, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7);
  
  public void fillRoundRect(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    try
    {
      doFillRoundRect((GDIWindowSurfaceData)surfaceData, paramSunGraphics2D.getCompClip(), composite, eargb, paramInt1 + transX, paramInt2 + transY, paramInt3, paramInt4, paramInt5, paramInt6);
    }
    catch (ClassCastException localClassCastException)
    {
      throw new InvalidPipeException("wrong surface data type: " + surfaceData);
    }
  }
  
  native void doFillOval(GDIWindowSurfaceData paramGDIWindowSurfaceData, Region paramRegion, Composite paramComposite, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5);
  
  public void fillOval(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    try
    {
      doFillOval((GDIWindowSurfaceData)surfaceData, paramSunGraphics2D.getCompClip(), composite, eargb, paramInt1 + transX, paramInt2 + transY, paramInt3, paramInt4);
    }
    catch (ClassCastException localClassCastException)
    {
      throw new InvalidPipeException("wrong surface data type: " + surfaceData);
    }
  }
  
  native void doFillArc(GDIWindowSurfaceData paramGDIWindowSurfaceData, Region paramRegion, Composite paramComposite, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7);
  
  public void fillArc(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    try
    {
      doFillArc((GDIWindowSurfaceData)surfaceData, paramSunGraphics2D.getCompClip(), composite, eargb, paramInt1 + transX, paramInt2 + transY, paramInt3, paramInt4, paramInt5, paramInt6);
    }
    catch (ClassCastException localClassCastException)
    {
      throw new InvalidPipeException("wrong surface data type: " + surfaceData);
    }
  }
  
  native void doFillPoly(GDIWindowSurfaceData paramGDIWindowSurfaceData, Region paramRegion, Composite paramComposite, int paramInt1, int paramInt2, int paramInt3, int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt4);
  
  public void fillPolygon(SunGraphics2D paramSunGraphics2D, int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt)
  {
    try
    {
      doFillPoly((GDIWindowSurfaceData)surfaceData, paramSunGraphics2D.getCompClip(), composite, eargb, transX, transY, paramArrayOfInt1, paramArrayOfInt2, paramInt);
    }
    catch (ClassCastException localClassCastException)
    {
      throw new InvalidPipeException("wrong surface data type: " + surfaceData);
    }
  }
  
  native void doShape(GDIWindowSurfaceData paramGDIWindowSurfaceData, Region paramRegion, Composite paramComposite, int paramInt1, int paramInt2, int paramInt3, Path2D.Float paramFloat, boolean paramBoolean);
  
  void doShape(SunGraphics2D paramSunGraphics2D, Shape paramShape, boolean paramBoolean)
  {
    Path2D.Float localFloat;
    int i;
    int j;
    if (transformState <= 1)
    {
      if ((paramShape instanceof Path2D.Float)) {
        localFloat = (Path2D.Float)paramShape;
      } else {
        localFloat = new Path2D.Float(paramShape);
      }
      i = transX;
      j = transY;
    }
    else
    {
      localFloat = new Path2D.Float(paramShape, transform);
      i = 0;
      j = 0;
    }
    try
    {
      doShape((GDIWindowSurfaceData)surfaceData, paramSunGraphics2D.getCompClip(), composite, eargb, i, j, localFloat, paramBoolean);
    }
    catch (ClassCastException localClassCastException)
    {
      throw new InvalidPipeException("wrong surface data type: " + surfaceData);
    }
  }
  
  public void doFillSpans(SunGraphics2D paramSunGraphics2D, SpanIterator paramSpanIterator)
  {
    int[] arrayOfInt = new int[4];
    GDIWindowSurfaceData localGDIWindowSurfaceData;
    try
    {
      localGDIWindowSurfaceData = (GDIWindowSurfaceData)surfaceData;
    }
    catch (ClassCastException localClassCastException)
    {
      throw new InvalidPipeException("wrong surface data type: " + surfaceData);
    }
    Region localRegion = paramSunGraphics2D.getCompClip();
    Composite localComposite = composite;
    int i = eargb;
    while (paramSpanIterator.nextSpan(arrayOfInt)) {
      doFillRect(localGDIWindowSurfaceData, localRegion, localComposite, i, arrayOfInt[0], arrayOfInt[1], arrayOfInt[2] - arrayOfInt[0], arrayOfInt[3] - arrayOfInt[1]);
    }
  }
  
  public void draw(SunGraphics2D paramSunGraphics2D, Shape paramShape)
  {
    if (strokeState == 0)
    {
      doShape(paramSunGraphics2D, paramShape, false);
    }
    else if (strokeState < 3)
    {
      ShapeSpanIterator localShapeSpanIterator = LoopPipe.getStrokeSpans(paramSunGraphics2D, paramShape);
      try
      {
        doFillSpans(paramSunGraphics2D, localShapeSpanIterator);
      }
      finally
      {
        localShapeSpanIterator.dispose();
      }
    }
    else
    {
      doShape(paramSunGraphics2D, stroke.createStrokedShape(paramShape), true);
    }
  }
  
  public void fill(SunGraphics2D paramSunGraphics2D, Shape paramShape)
  {
    doShape(paramSunGraphics2D, paramShape, true);
  }
  
  public native void devCopyArea(GDIWindowSurfaceData paramGDIWindowSurfaceData, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6);
  
  public GDIRenderer traceWrap()
  {
    return new Tracer();
  }
  
  public static class Tracer
    extends GDIRenderer
  {
    public Tracer() {}
    
    void doDrawLine(GDIWindowSurfaceData paramGDIWindowSurfaceData, Region paramRegion, Composite paramComposite, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      GraphicsPrimitive.tracePrimitive("GDIDrawLine");
      super.doDrawLine(paramGDIWindowSurfaceData, paramRegion, paramComposite, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
    }
    
    void doDrawRect(GDIWindowSurfaceData paramGDIWindowSurfaceData, Region paramRegion, Composite paramComposite, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      GraphicsPrimitive.tracePrimitive("GDIDrawRect");
      super.doDrawRect(paramGDIWindowSurfaceData, paramRegion, paramComposite, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
    }
    
    void doDrawRoundRect(GDIWindowSurfaceData paramGDIWindowSurfaceData, Region paramRegion, Composite paramComposite, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7)
    {
      GraphicsPrimitive.tracePrimitive("GDIDrawRoundRect");
      super.doDrawRoundRect(paramGDIWindowSurfaceData, paramRegion, paramComposite, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7);
    }
    
    void doDrawOval(GDIWindowSurfaceData paramGDIWindowSurfaceData, Region paramRegion, Composite paramComposite, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      GraphicsPrimitive.tracePrimitive("GDIDrawOval");
      super.doDrawOval(paramGDIWindowSurfaceData, paramRegion, paramComposite, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
    }
    
    void doDrawArc(GDIWindowSurfaceData paramGDIWindowSurfaceData, Region paramRegion, Composite paramComposite, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7)
    {
      GraphicsPrimitive.tracePrimitive("GDIDrawArc");
      super.doDrawArc(paramGDIWindowSurfaceData, paramRegion, paramComposite, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7);
    }
    
    void doDrawPoly(GDIWindowSurfaceData paramGDIWindowSurfaceData, Region paramRegion, Composite paramComposite, int paramInt1, int paramInt2, int paramInt3, int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt4, boolean paramBoolean)
    {
      GraphicsPrimitive.tracePrimitive("GDIDrawPoly");
      super.doDrawPoly(paramGDIWindowSurfaceData, paramRegion, paramComposite, paramInt1, paramInt2, paramInt3, paramArrayOfInt1, paramArrayOfInt2, paramInt4, paramBoolean);
    }
    
    void doFillRect(GDIWindowSurfaceData paramGDIWindowSurfaceData, Region paramRegion, Composite paramComposite, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      GraphicsPrimitive.tracePrimitive("GDIFillRect");
      super.doFillRect(paramGDIWindowSurfaceData, paramRegion, paramComposite, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
    }
    
    void doFillRoundRect(GDIWindowSurfaceData paramGDIWindowSurfaceData, Region paramRegion, Composite paramComposite, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7)
    {
      GraphicsPrimitive.tracePrimitive("GDIFillRoundRect");
      super.doFillRoundRect(paramGDIWindowSurfaceData, paramRegion, paramComposite, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7);
    }
    
    void doFillOval(GDIWindowSurfaceData paramGDIWindowSurfaceData, Region paramRegion, Composite paramComposite, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      GraphicsPrimitive.tracePrimitive("GDIFillOval");
      super.doFillOval(paramGDIWindowSurfaceData, paramRegion, paramComposite, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
    }
    
    void doFillArc(GDIWindowSurfaceData paramGDIWindowSurfaceData, Region paramRegion, Composite paramComposite, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7)
    {
      GraphicsPrimitive.tracePrimitive("GDIFillArc");
      super.doFillArc(paramGDIWindowSurfaceData, paramRegion, paramComposite, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7);
    }
    
    void doFillPoly(GDIWindowSurfaceData paramGDIWindowSurfaceData, Region paramRegion, Composite paramComposite, int paramInt1, int paramInt2, int paramInt3, int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt4)
    {
      GraphicsPrimitive.tracePrimitive("GDIFillPoly");
      super.doFillPoly(paramGDIWindowSurfaceData, paramRegion, paramComposite, paramInt1, paramInt2, paramInt3, paramArrayOfInt1, paramArrayOfInt2, paramInt4);
    }
    
    void doShape(GDIWindowSurfaceData paramGDIWindowSurfaceData, Region paramRegion, Composite paramComposite, int paramInt1, int paramInt2, int paramInt3, Path2D.Float paramFloat, boolean paramBoolean)
    {
      GraphicsPrimitive.tracePrimitive(paramBoolean ? "GDIFillShape" : "GDIDrawShape");
      super.doShape(paramGDIWindowSurfaceData, paramRegion, paramComposite, paramInt1, paramInt2, paramInt3, paramFloat, paramBoolean);
    }
    
    public void devCopyArea(GDIWindowSurfaceData paramGDIWindowSurfaceData, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
    {
      GraphicsPrimitive.tracePrimitive("GDICopyArea");
      super.devCopyArea(paramGDIWindowSurfaceData, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\windows\GDIRenderer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
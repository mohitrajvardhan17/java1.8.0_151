package sun.java2d.pipe;

import java.awt.Rectangle;
import java.awt.Shape;
import sun.java2d.SunGraphics2D;

public class SpanClipRenderer
  implements CompositePipe
{
  CompositePipe outpipe;
  static Class RegionClass = Region.class;
  static Class RegionIteratorClass = RegionIterator.class;
  
  static native void initIDs(Class paramClass1, Class paramClass2);
  
  public SpanClipRenderer(CompositePipe paramCompositePipe)
  {
    outpipe = paramCompositePipe;
  }
  
  public Object startSequence(SunGraphics2D paramSunGraphics2D, Shape paramShape, Rectangle paramRectangle, int[] paramArrayOfInt)
  {
    RegionIterator localRegionIterator = clipRegion.getIterator();
    return new SCRcontext(localRegionIterator, outpipe.startSequence(paramSunGraphics2D, paramShape, paramRectangle, paramArrayOfInt));
  }
  
  public boolean needTile(Object paramObject, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    SCRcontext localSCRcontext = (SCRcontext)paramObject;
    return outpipe.needTile(outcontext, paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public void renderPathTile(Object paramObject, byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, ShapeSpanIterator paramShapeSpanIterator)
  {
    renderPathTile(paramObject, paramArrayOfByte, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
  }
  
  public void renderPathTile(Object paramObject, byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    SCRcontext localSCRcontext = (SCRcontext)paramObject;
    RegionIterator localRegionIterator = iterator.createCopy();
    int[] arrayOfInt = band;
    arrayOfInt[0] = paramInt3;
    arrayOfInt[1] = paramInt4;
    arrayOfInt[2] = (paramInt3 + paramInt5);
    arrayOfInt[3] = (paramInt4 + paramInt6);
    if (paramArrayOfByte == null)
    {
      int i = paramInt5 * paramInt6;
      paramArrayOfByte = tile;
      if ((paramArrayOfByte != null) && (paramArrayOfByte.length < i)) {
        paramArrayOfByte = null;
      }
      if (paramArrayOfByte == null)
      {
        paramArrayOfByte = new byte[i];
        tile = paramArrayOfByte;
      }
      paramInt1 = 0;
      paramInt2 = paramInt5;
      fillTile(localRegionIterator, paramArrayOfByte, paramInt1, paramInt2, arrayOfInt);
    }
    else
    {
      eraseTile(localRegionIterator, paramArrayOfByte, paramInt1, paramInt2, arrayOfInt);
    }
    if ((arrayOfInt[2] > arrayOfInt[0]) && (arrayOfInt[3] > arrayOfInt[1]))
    {
      paramInt1 += (arrayOfInt[1] - paramInt4) * paramInt2 + (arrayOfInt[0] - paramInt3);
      outpipe.renderPathTile(outcontext, paramArrayOfByte, paramInt1, paramInt2, arrayOfInt[0], arrayOfInt[1], arrayOfInt[2] - arrayOfInt[0], arrayOfInt[3] - arrayOfInt[1]);
    }
  }
  
  public native void fillTile(RegionIterator paramRegionIterator, byte[] paramArrayOfByte, int paramInt1, int paramInt2, int[] paramArrayOfInt);
  
  public native void eraseTile(RegionIterator paramRegionIterator, byte[] paramArrayOfByte, int paramInt1, int paramInt2, int[] paramArrayOfInt);
  
  public void skipTile(Object paramObject, int paramInt1, int paramInt2)
  {
    SCRcontext localSCRcontext = (SCRcontext)paramObject;
    outpipe.skipTile(outcontext, paramInt1, paramInt2);
  }
  
  public void endSequence(Object paramObject)
  {
    SCRcontext localSCRcontext = (SCRcontext)paramObject;
    outpipe.endSequence(outcontext);
  }
  
  static
  {
    initIDs(RegionClass, RegionIteratorClass);
  }
  
  class SCRcontext
  {
    RegionIterator iterator;
    Object outcontext;
    int[] band;
    byte[] tile;
    
    public SCRcontext(RegionIterator paramRegionIterator, Object paramObject)
    {
      iterator = paramRegionIterator;
      outcontext = paramObject;
      band = new int[4];
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\pipe\SpanClipRenderer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
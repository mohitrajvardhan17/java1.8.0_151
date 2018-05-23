package sun.java2d.pipe;

import java.awt.Rectangle;
import java.awt.Shape;
import sun.java2d.SunGraphics2D;
import sun.java2d.loops.MaskFill;

public class AlphaColorPipe
  implements CompositePipe, ParallelogramPipe
{
  public AlphaColorPipe() {}
  
  public Object startSequence(SunGraphics2D paramSunGraphics2D, Shape paramShape, Rectangle paramRectangle, int[] paramArrayOfInt)
  {
    return paramSunGraphics2D;
  }
  
  public boolean needTile(Object paramObject, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    return true;
  }
  
  public void renderPathTile(Object paramObject, byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    SunGraphics2D localSunGraphics2D = (SunGraphics2D)paramObject;
    alphafill.MaskFill(localSunGraphics2D, localSunGraphics2D.getSurfaceData(), composite, paramInt3, paramInt4, paramInt5, paramInt6, paramArrayOfByte, paramInt1, paramInt2);
  }
  
  public void skipTile(Object paramObject, int paramInt1, int paramInt2) {}
  
  public void endSequence(Object paramObject) {}
  
  public void fillParallelogram(SunGraphics2D paramSunGraphics2D, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6, double paramDouble7, double paramDouble8, double paramDouble9, double paramDouble10)
  {
    alphafill.FillAAPgram(paramSunGraphics2D, paramSunGraphics2D.getSurfaceData(), composite, paramDouble5, paramDouble6, paramDouble7, paramDouble8, paramDouble9, paramDouble10);
  }
  
  public void drawParallelogram(SunGraphics2D paramSunGraphics2D, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6, double paramDouble7, double paramDouble8, double paramDouble9, double paramDouble10, double paramDouble11, double paramDouble12)
  {
    alphafill.DrawAAPgram(paramSunGraphics2D, paramSunGraphics2D.getSurfaceData(), composite, paramDouble5, paramDouble6, paramDouble7, paramDouble8, paramDouble9, paramDouble10, paramDouble11, paramDouble12);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\pipe\AlphaColorPipe.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
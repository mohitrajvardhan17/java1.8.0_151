package sun.java2d.pipe;

import java.awt.BasicStroke;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import sun.java2d.SunGraphics2D;

public class AAShapePipe
  implements ShapeDrawPipe, ParallelogramPipe
{
  static RenderingEngine renderengine = ;
  CompositePipe outpipe;
  private static byte[] theTile;
  
  public AAShapePipe(CompositePipe paramCompositePipe)
  {
    outpipe = paramCompositePipe;
  }
  
  public void draw(SunGraphics2D paramSunGraphics2D, Shape paramShape)
  {
    BasicStroke localBasicStroke;
    if ((stroke instanceof BasicStroke))
    {
      localBasicStroke = (BasicStroke)stroke;
    }
    else
    {
      paramShape = stroke.createStrokedShape(paramShape);
      localBasicStroke = null;
    }
    renderPath(paramSunGraphics2D, paramShape, localBasicStroke);
  }
  
  public void fill(SunGraphics2D paramSunGraphics2D, Shape paramShape)
  {
    renderPath(paramSunGraphics2D, paramShape, null);
  }
  
  private static Rectangle2D computeBBox(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4)
  {
    if (paramDouble3 -= paramDouble1 < 0.0D)
    {
      paramDouble1 += paramDouble3;
      paramDouble3 = -paramDouble3;
    }
    if (paramDouble4 -= paramDouble2 < 0.0D)
    {
      paramDouble2 += paramDouble4;
      paramDouble4 = -paramDouble4;
    }
    return new Rectangle2D.Double(paramDouble1, paramDouble2, paramDouble3, paramDouble4);
  }
  
  public void fillParallelogram(SunGraphics2D paramSunGraphics2D, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6, double paramDouble7, double paramDouble8, double paramDouble9, double paramDouble10)
  {
    Region localRegion = paramSunGraphics2D.getCompClip();
    int[] arrayOfInt = new int[4];
    AATileGenerator localAATileGenerator = renderengine.getAATileGenerator(paramDouble5, paramDouble6, paramDouble7, paramDouble8, paramDouble9, paramDouble10, 0.0D, 0.0D, localRegion, arrayOfInt);
    if (localAATileGenerator == null) {
      return;
    }
    renderTiles(paramSunGraphics2D, computeBBox(paramDouble1, paramDouble2, paramDouble3, paramDouble4), localAATileGenerator, arrayOfInt);
  }
  
  public void drawParallelogram(SunGraphics2D paramSunGraphics2D, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6, double paramDouble7, double paramDouble8, double paramDouble9, double paramDouble10, double paramDouble11, double paramDouble12)
  {
    Region localRegion = paramSunGraphics2D.getCompClip();
    int[] arrayOfInt = new int[4];
    AATileGenerator localAATileGenerator = renderengine.getAATileGenerator(paramDouble5, paramDouble6, paramDouble7, paramDouble8, paramDouble9, paramDouble10, paramDouble11, paramDouble12, localRegion, arrayOfInt);
    if (localAATileGenerator == null) {
      return;
    }
    renderTiles(paramSunGraphics2D, computeBBox(paramDouble1, paramDouble2, paramDouble3, paramDouble4), localAATileGenerator, arrayOfInt);
  }
  
  private static synchronized byte[] getAlphaTile(int paramInt)
  {
    byte[] arrayOfByte = theTile;
    if ((arrayOfByte == null) || (arrayOfByte.length < paramInt)) {
      arrayOfByte = new byte[paramInt];
    } else {
      theTile = null;
    }
    return arrayOfByte;
  }
  
  private static synchronized void dropAlphaTile(byte[] paramArrayOfByte)
  {
    theTile = paramArrayOfByte;
  }
  
  public void renderPath(SunGraphics2D paramSunGraphics2D, Shape paramShape, BasicStroke paramBasicStroke)
  {
    boolean bool1 = (paramBasicStroke != null) && (strokeHint != 2);
    boolean bool2 = strokeState <= 1;
    Region localRegion = paramSunGraphics2D.getCompClip();
    int[] arrayOfInt = new int[4];
    AATileGenerator localAATileGenerator = renderengine.getAATileGenerator(paramShape, transform, localRegion, paramBasicStroke, bool2, bool1, arrayOfInt);
    if (localAATileGenerator == null) {
      return;
    }
    renderTiles(paramSunGraphics2D, paramShape, localAATileGenerator, arrayOfInt);
  }
  
  public void renderTiles(SunGraphics2D paramSunGraphics2D, Shape paramShape, AATileGenerator paramAATileGenerator, int[] paramArrayOfInt)
  {
    Object localObject1 = null;
    byte[] arrayOfByte1 = null;
    try
    {
      localObject1 = outpipe.startSequence(paramSunGraphics2D, paramShape, new Rectangle(paramArrayOfInt[0], paramArrayOfInt[1], paramArrayOfInt[2] - paramArrayOfInt[0], paramArrayOfInt[3] - paramArrayOfInt[1]), paramArrayOfInt);
      int i = paramAATileGenerator.getTileWidth();
      int j = paramAATileGenerator.getTileHeight();
      arrayOfByte1 = getAlphaTile(i * j);
      int k = paramArrayOfInt[1];
      while (k < paramArrayOfInt[3])
      {
        int m = paramArrayOfInt[0];
        while (m < paramArrayOfInt[2])
        {
          int n = Math.min(i, paramArrayOfInt[2] - m);
          int i1 = Math.min(j, paramArrayOfInt[3] - k);
          int i2 = paramAATileGenerator.getTypicalAlpha();
          if ((i2 == 0) || (!outpipe.needTile(localObject1, m, k, n, i1)))
          {
            paramAATileGenerator.nextTile();
            outpipe.skipTile(localObject1, m, k);
          }
          else
          {
            byte[] arrayOfByte2;
            if (i2 == 255)
            {
              arrayOfByte2 = null;
              paramAATileGenerator.nextTile();
            }
            else
            {
              arrayOfByte2 = arrayOfByte1;
              paramAATileGenerator.getAlpha(arrayOfByte1, 0, i);
            }
            outpipe.renderPathTile(localObject1, arrayOfByte2, 0, i, m, k, n, i1);
          }
          m += i;
        }
        k += j;
      }
    }
    finally
    {
      paramAATileGenerator.dispose();
      if (localObject1 != null) {
        outpipe.endSequence(localObject1);
      }
      if (arrayOfByte1 != null) {
        dropAlphaTile(arrayOfByte1);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\pipe\AAShapePipe.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
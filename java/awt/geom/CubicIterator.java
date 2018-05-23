package java.awt.geom;

import java.util.NoSuchElementException;

class CubicIterator
  implements PathIterator
{
  CubicCurve2D cubic;
  AffineTransform affine;
  int index;
  
  CubicIterator(CubicCurve2D paramCubicCurve2D, AffineTransform paramAffineTransform)
  {
    cubic = paramCubicCurve2D;
    affine = paramAffineTransform;
  }
  
  public int getWindingRule()
  {
    return 1;
  }
  
  public boolean isDone()
  {
    return index > 1;
  }
  
  public void next()
  {
    index += 1;
  }
  
  public int currentSegment(float[] paramArrayOfFloat)
  {
    if (isDone()) {
      throw new NoSuchElementException("cubic iterator iterator out of bounds");
    }
    int i;
    if (index == 0)
    {
      paramArrayOfFloat[0] = ((float)cubic.getX1());
      paramArrayOfFloat[1] = ((float)cubic.getY1());
      i = 0;
    }
    else
    {
      paramArrayOfFloat[0] = ((float)cubic.getCtrlX1());
      paramArrayOfFloat[1] = ((float)cubic.getCtrlY1());
      paramArrayOfFloat[2] = ((float)cubic.getCtrlX2());
      paramArrayOfFloat[3] = ((float)cubic.getCtrlY2());
      paramArrayOfFloat[4] = ((float)cubic.getX2());
      paramArrayOfFloat[5] = ((float)cubic.getY2());
      i = 3;
    }
    if (affine != null) {
      affine.transform(paramArrayOfFloat, 0, paramArrayOfFloat, 0, index == 0 ? 1 : 3);
    }
    return i;
  }
  
  public int currentSegment(double[] paramArrayOfDouble)
  {
    if (isDone()) {
      throw new NoSuchElementException("cubic iterator iterator out of bounds");
    }
    int i;
    if (index == 0)
    {
      paramArrayOfDouble[0] = cubic.getX1();
      paramArrayOfDouble[1] = cubic.getY1();
      i = 0;
    }
    else
    {
      paramArrayOfDouble[0] = cubic.getCtrlX1();
      paramArrayOfDouble[1] = cubic.getCtrlY1();
      paramArrayOfDouble[2] = cubic.getCtrlX2();
      paramArrayOfDouble[3] = cubic.getCtrlY2();
      paramArrayOfDouble[4] = cubic.getX2();
      paramArrayOfDouble[5] = cubic.getY2();
      i = 3;
    }
    if (affine != null) {
      affine.transform(paramArrayOfDouble, 0, paramArrayOfDouble, 0, index == 0 ? 1 : 3);
    }
    return i;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\geom\CubicIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
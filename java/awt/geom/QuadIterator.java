package java.awt.geom;

import java.util.NoSuchElementException;

class QuadIterator
  implements PathIterator
{
  QuadCurve2D quad;
  AffineTransform affine;
  int index;
  
  QuadIterator(QuadCurve2D paramQuadCurve2D, AffineTransform paramAffineTransform)
  {
    quad = paramQuadCurve2D;
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
      throw new NoSuchElementException("quad iterator iterator out of bounds");
    }
    int i;
    if (index == 0)
    {
      paramArrayOfFloat[0] = ((float)quad.getX1());
      paramArrayOfFloat[1] = ((float)quad.getY1());
      i = 0;
    }
    else
    {
      paramArrayOfFloat[0] = ((float)quad.getCtrlX());
      paramArrayOfFloat[1] = ((float)quad.getCtrlY());
      paramArrayOfFloat[2] = ((float)quad.getX2());
      paramArrayOfFloat[3] = ((float)quad.getY2());
      i = 2;
    }
    if (affine != null) {
      affine.transform(paramArrayOfFloat, 0, paramArrayOfFloat, 0, index == 0 ? 1 : 2);
    }
    return i;
  }
  
  public int currentSegment(double[] paramArrayOfDouble)
  {
    if (isDone()) {
      throw new NoSuchElementException("quad iterator iterator out of bounds");
    }
    int i;
    if (index == 0)
    {
      paramArrayOfDouble[0] = quad.getX1();
      paramArrayOfDouble[1] = quad.getY1();
      i = 0;
    }
    else
    {
      paramArrayOfDouble[0] = quad.getCtrlX();
      paramArrayOfDouble[1] = quad.getCtrlY();
      paramArrayOfDouble[2] = quad.getX2();
      paramArrayOfDouble[3] = quad.getY2();
      i = 2;
    }
    if (affine != null) {
      affine.transform(paramArrayOfDouble, 0, paramArrayOfDouble, 0, index == 0 ? 1 : 2);
    }
    return i;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\geom\QuadIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
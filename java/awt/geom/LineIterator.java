package java.awt.geom;

import java.util.NoSuchElementException;

class LineIterator
  implements PathIterator
{
  Line2D line;
  AffineTransform affine;
  int index;
  
  LineIterator(Line2D paramLine2D, AffineTransform paramAffineTransform)
  {
    line = paramLine2D;
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
      throw new NoSuchElementException("line iterator out of bounds");
    }
    int i;
    if (index == 0)
    {
      paramArrayOfFloat[0] = ((float)line.getX1());
      paramArrayOfFloat[1] = ((float)line.getY1());
      i = 0;
    }
    else
    {
      paramArrayOfFloat[0] = ((float)line.getX2());
      paramArrayOfFloat[1] = ((float)line.getY2());
      i = 1;
    }
    if (affine != null) {
      affine.transform(paramArrayOfFloat, 0, paramArrayOfFloat, 0, 1);
    }
    return i;
  }
  
  public int currentSegment(double[] paramArrayOfDouble)
  {
    if (isDone()) {
      throw new NoSuchElementException("line iterator out of bounds");
    }
    int i;
    if (index == 0)
    {
      paramArrayOfDouble[0] = line.getX1();
      paramArrayOfDouble[1] = line.getY1();
      i = 0;
    }
    else
    {
      paramArrayOfDouble[0] = line.getX2();
      paramArrayOfDouble[1] = line.getY2();
      i = 1;
    }
    if (affine != null) {
      affine.transform(paramArrayOfDouble, 0, paramArrayOfDouble, 0, 1);
    }
    return i;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\geom\LineIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package java.awt.geom;

import java.util.NoSuchElementException;

class RectIterator
  implements PathIterator
{
  double x;
  double y;
  double w;
  double h;
  AffineTransform affine;
  int index;
  
  RectIterator(Rectangle2D paramRectangle2D, AffineTransform paramAffineTransform)
  {
    x = paramRectangle2D.getX();
    y = paramRectangle2D.getY();
    w = paramRectangle2D.getWidth();
    h = paramRectangle2D.getHeight();
    affine = paramAffineTransform;
    if ((w < 0.0D) || (h < 0.0D)) {
      index = 6;
    }
  }
  
  public int getWindingRule()
  {
    return 1;
  }
  
  public boolean isDone()
  {
    return index > 5;
  }
  
  public void next()
  {
    index += 1;
  }
  
  public int currentSegment(float[] paramArrayOfFloat)
  {
    if (isDone()) {
      throw new NoSuchElementException("rect iterator out of bounds");
    }
    if (index == 5) {
      return 4;
    }
    paramArrayOfFloat[0] = ((float)x);
    paramArrayOfFloat[1] = ((float)y);
    if ((index == 1) || (index == 2)) {
      paramArrayOfFloat[0] += (float)w;
    }
    if ((index == 2) || (index == 3)) {
      paramArrayOfFloat[1] += (float)h;
    }
    if (affine != null) {
      affine.transform(paramArrayOfFloat, 0, paramArrayOfFloat, 0, 1);
    }
    return index == 0 ? 0 : 1;
  }
  
  public int currentSegment(double[] paramArrayOfDouble)
  {
    if (isDone()) {
      throw new NoSuchElementException("rect iterator out of bounds");
    }
    if (index == 5) {
      return 4;
    }
    paramArrayOfDouble[0] = x;
    paramArrayOfDouble[1] = y;
    if ((index == 1) || (index == 2)) {
      paramArrayOfDouble[0] += w;
    }
    if ((index == 2) || (index == 3)) {
      paramArrayOfDouble[1] += h;
    }
    if (affine != null) {
      affine.transform(paramArrayOfDouble, 0, paramArrayOfDouble, 0, 1);
    }
    return index == 0 ? 0 : 1;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\geom\RectIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package java.awt.geom;

import java.util.NoSuchElementException;

class EllipseIterator
  implements PathIterator
{
  double x;
  double y;
  double w;
  double h;
  AffineTransform affine;
  int index;
  public static final double CtrlVal = 0.5522847498307933D;
  private static final double pcv = 0.7761423749153966D;
  private static final double ncv = 0.22385762508460333D;
  private static double[][] ctrlpts = { { 1.0D, 0.7761423749153966D, 0.7761423749153966D, 1.0D, 0.5D, 1.0D }, { 0.22385762508460333D, 1.0D, 0.0D, 0.7761423749153966D, 0.0D, 0.5D }, { 0.0D, 0.22385762508460333D, 0.22385762508460333D, 0.0D, 0.5D, 0.0D }, { 0.7761423749153966D, 0.0D, 1.0D, 0.22385762508460333D, 1.0D, 0.5D } };
  
  EllipseIterator(Ellipse2D paramEllipse2D, AffineTransform paramAffineTransform)
  {
    x = paramEllipse2D.getX();
    y = paramEllipse2D.getY();
    w = paramEllipse2D.getWidth();
    h = paramEllipse2D.getHeight();
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
      throw new NoSuchElementException("ellipse iterator out of bounds");
    }
    if (index == 5) {
      return 4;
    }
    if (index == 0)
    {
      arrayOfDouble = ctrlpts[3];
      paramArrayOfFloat[0] = ((float)(x + arrayOfDouble[4] * w));
      paramArrayOfFloat[1] = ((float)(y + arrayOfDouble[5] * h));
      if (affine != null) {
        affine.transform(paramArrayOfFloat, 0, paramArrayOfFloat, 0, 1);
      }
      return 0;
    }
    double[] arrayOfDouble = ctrlpts[(index - 1)];
    paramArrayOfFloat[0] = ((float)(x + arrayOfDouble[0] * w));
    paramArrayOfFloat[1] = ((float)(y + arrayOfDouble[1] * h));
    paramArrayOfFloat[2] = ((float)(x + arrayOfDouble[2] * w));
    paramArrayOfFloat[3] = ((float)(y + arrayOfDouble[3] * h));
    paramArrayOfFloat[4] = ((float)(x + arrayOfDouble[4] * w));
    paramArrayOfFloat[5] = ((float)(y + arrayOfDouble[5] * h));
    if (affine != null) {
      affine.transform(paramArrayOfFloat, 0, paramArrayOfFloat, 0, 3);
    }
    return 3;
  }
  
  public int currentSegment(double[] paramArrayOfDouble)
  {
    if (isDone()) {
      throw new NoSuchElementException("ellipse iterator out of bounds");
    }
    if (index == 5) {
      return 4;
    }
    if (index == 0)
    {
      arrayOfDouble = ctrlpts[3];
      paramArrayOfDouble[0] = (x + arrayOfDouble[4] * w);
      paramArrayOfDouble[1] = (y + arrayOfDouble[5] * h);
      if (affine != null) {
        affine.transform(paramArrayOfDouble, 0, paramArrayOfDouble, 0, 1);
      }
      return 0;
    }
    double[] arrayOfDouble = ctrlpts[(index - 1)];
    paramArrayOfDouble[0] = (x + arrayOfDouble[0] * w);
    paramArrayOfDouble[1] = (y + arrayOfDouble[1] * h);
    paramArrayOfDouble[2] = (x + arrayOfDouble[2] * w);
    paramArrayOfDouble[3] = (y + arrayOfDouble[3] * h);
    paramArrayOfDouble[4] = (x + arrayOfDouble[4] * w);
    paramArrayOfDouble[5] = (y + arrayOfDouble[5] * h);
    if (affine != null) {
      affine.transform(paramArrayOfDouble, 0, paramArrayOfDouble, 0, 3);
    }
    return 3;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\geom\EllipseIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package java.awt.geom;

import java.util.NoSuchElementException;

class RoundRectIterator
  implements PathIterator
{
  double x;
  double y;
  double w;
  double h;
  double aw;
  double ah;
  AffineTransform affine;
  int index;
  private static final double angle = 0.7853981633974483D;
  private static final double a = 1.0D - Math.cos(0.7853981633974483D);
  private static final double b = Math.tan(0.7853981633974483D);
  private static final double c = Math.sqrt(1.0D + b * b) - 1.0D + a;
  private static final double cv = 1.3333333333333333D * a * b / c;
  private static final double acv = (1.0D - cv) / 2.0D;
  private static double[][] ctrlpts = { { 0.0D, 0.0D, 0.0D, 0.5D }, { 0.0D, 0.0D, 1.0D, -0.5D }, { 0.0D, 0.0D, 1.0D, -acv, 0.0D, acv, 1.0D, 0.0D, 0.0D, 0.5D, 1.0D, 0.0D }, { 1.0D, -0.5D, 1.0D, 0.0D }, { 1.0D, -acv, 1.0D, 0.0D, 1.0D, 0.0D, 1.0D, -acv, 1.0D, 0.0D, 1.0D, -0.5D }, { 1.0D, 0.0D, 0.0D, 0.5D }, { 1.0D, 0.0D, 0.0D, acv, 1.0D, -acv, 0.0D, 0.0D, 1.0D, -0.5D, 0.0D, 0.0D }, { 0.0D, 0.5D, 0.0D, 0.0D }, { 0.0D, acv, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, acv, 0.0D, 0.0D, 0.0D, 0.5D }, new double[0] };
  private static int[] types = { 0, 1, 3, 1, 3, 1, 3, 1, 3, 4 };
  
  RoundRectIterator(RoundRectangle2D paramRoundRectangle2D, AffineTransform paramAffineTransform)
  {
    x = paramRoundRectangle2D.getX();
    y = paramRoundRectangle2D.getY();
    w = paramRoundRectangle2D.getWidth();
    h = paramRoundRectangle2D.getHeight();
    aw = Math.min(w, Math.abs(paramRoundRectangle2D.getArcWidth()));
    ah = Math.min(h, Math.abs(paramRoundRectangle2D.getArcHeight()));
    affine = paramAffineTransform;
    if ((aw < 0.0D) || (ah < 0.0D)) {
      index = ctrlpts.length;
    }
  }
  
  public int getWindingRule()
  {
    return 1;
  }
  
  public boolean isDone()
  {
    return index >= ctrlpts.length;
  }
  
  public void next()
  {
    index += 1;
  }
  
  public int currentSegment(float[] paramArrayOfFloat)
  {
    if (isDone()) {
      throw new NoSuchElementException("roundrect iterator out of bounds");
    }
    double[] arrayOfDouble = ctrlpts[index];
    int i = 0;
    for (int j = 0; j < arrayOfDouble.length; j += 4)
    {
      paramArrayOfFloat[(i++)] = ((float)(x + arrayOfDouble[(j + 0)] * w + arrayOfDouble[(j + 1)] * aw));
      paramArrayOfFloat[(i++)] = ((float)(y + arrayOfDouble[(j + 2)] * h + arrayOfDouble[(j + 3)] * ah));
    }
    if (affine != null) {
      affine.transform(paramArrayOfFloat, 0, paramArrayOfFloat, 0, i / 2);
    }
    return types[index];
  }
  
  public int currentSegment(double[] paramArrayOfDouble)
  {
    if (isDone()) {
      throw new NoSuchElementException("roundrect iterator out of bounds");
    }
    double[] arrayOfDouble = ctrlpts[index];
    int i = 0;
    for (int j = 0; j < arrayOfDouble.length; j += 4)
    {
      paramArrayOfDouble[(i++)] = (x + arrayOfDouble[(j + 0)] * w + arrayOfDouble[(j + 1)] * aw);
      paramArrayOfDouble[(i++)] = (y + arrayOfDouble[(j + 2)] * h + arrayOfDouble[(j + 3)] * ah);
    }
    if (affine != null) {
      affine.transform(paramArrayOfDouble, 0, paramArrayOfDouble, 0, i / 2);
    }
    return types[index];
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\geom\RoundRectIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package java.awt.geom;

import java.util.NoSuchElementException;

class ArcIterator
  implements PathIterator
{
  double x;
  double y;
  double w;
  double h;
  double angStRad;
  double increment;
  double cv;
  AffineTransform affine;
  int index;
  int arcSegs;
  int lineSegs;
  
  ArcIterator(Arc2D paramArc2D, AffineTransform paramAffineTransform)
  {
    w = (paramArc2D.getWidth() / 2.0D);
    h = (paramArc2D.getHeight() / 2.0D);
    x = (paramArc2D.getX() + w);
    y = (paramArc2D.getY() + h);
    angStRad = (-Math.toRadians(paramArc2D.getAngleStart()));
    affine = paramAffineTransform;
    double d = -paramArc2D.getAngleExtent();
    if ((d >= 360.0D) || (d <= -360.0D))
    {
      arcSegs = 4;
      increment = 1.5707963267948966D;
      cv = 0.5522847498307933D;
      if (d < 0.0D)
      {
        increment = (-increment);
        cv = (-cv);
      }
    }
    else
    {
      arcSegs = ((int)Math.ceil(Math.abs(d) / 90.0D));
      increment = Math.toRadians(d / arcSegs);
      cv = btan(increment);
      if (cv == 0.0D) {
        arcSegs = 0;
      }
    }
    switch (paramArc2D.getArcType())
    {
    case 0: 
      lineSegs = 0;
      break;
    case 1: 
      lineSegs = 1;
      break;
    case 2: 
      lineSegs = 2;
    }
    if ((w < 0.0D) || (h < 0.0D)) {
      arcSegs = (lineSegs = -1);
    }
  }
  
  public int getWindingRule()
  {
    return 1;
  }
  
  public boolean isDone()
  {
    return index > arcSegs + lineSegs;
  }
  
  public void next()
  {
    index += 1;
  }
  
  private static double btan(double paramDouble)
  {
    paramDouble /= 2.0D;
    return 1.3333333333333333D * Math.sin(paramDouble) / (1.0D + Math.cos(paramDouble));
  }
  
  public int currentSegment(float[] paramArrayOfFloat)
  {
    if (isDone()) {
      throw new NoSuchElementException("arc iterator out of bounds");
    }
    double d1 = angStRad;
    if (index == 0)
    {
      paramArrayOfFloat[0] = ((float)(x + Math.cos(d1) * w));
      paramArrayOfFloat[1] = ((float)(y + Math.sin(d1) * h));
      if (affine != null) {
        affine.transform(paramArrayOfFloat, 0, paramArrayOfFloat, 0, 1);
      }
      return 0;
    }
    if (index > arcSegs)
    {
      if (index == arcSegs + lineSegs) {
        return 4;
      }
      paramArrayOfFloat[0] = ((float)x);
      paramArrayOfFloat[1] = ((float)y);
      if (affine != null) {
        affine.transform(paramArrayOfFloat, 0, paramArrayOfFloat, 0, 1);
      }
      return 1;
    }
    d1 += increment * (index - 1);
    double d2 = Math.cos(d1);
    double d3 = Math.sin(d1);
    paramArrayOfFloat[0] = ((float)(x + (d2 - cv * d3) * w));
    paramArrayOfFloat[1] = ((float)(y + (d3 + cv * d2) * h));
    d1 += increment;
    d2 = Math.cos(d1);
    d3 = Math.sin(d1);
    paramArrayOfFloat[2] = ((float)(x + (d2 + cv * d3) * w));
    paramArrayOfFloat[3] = ((float)(y + (d3 - cv * d2) * h));
    paramArrayOfFloat[4] = ((float)(x + d2 * w));
    paramArrayOfFloat[5] = ((float)(y + d3 * h));
    if (affine != null) {
      affine.transform(paramArrayOfFloat, 0, paramArrayOfFloat, 0, 3);
    }
    return 3;
  }
  
  public int currentSegment(double[] paramArrayOfDouble)
  {
    if (isDone()) {
      throw new NoSuchElementException("arc iterator out of bounds");
    }
    double d1 = angStRad;
    if (index == 0)
    {
      paramArrayOfDouble[0] = (x + Math.cos(d1) * w);
      paramArrayOfDouble[1] = (y + Math.sin(d1) * h);
      if (affine != null) {
        affine.transform(paramArrayOfDouble, 0, paramArrayOfDouble, 0, 1);
      }
      return 0;
    }
    if (index > arcSegs)
    {
      if (index == arcSegs + lineSegs) {
        return 4;
      }
      paramArrayOfDouble[0] = x;
      paramArrayOfDouble[1] = y;
      if (affine != null) {
        affine.transform(paramArrayOfDouble, 0, paramArrayOfDouble, 0, 1);
      }
      return 1;
    }
    d1 += increment * (index - 1);
    double d2 = Math.cos(d1);
    double d3 = Math.sin(d1);
    paramArrayOfDouble[0] = (x + (d2 - cv * d3) * w);
    paramArrayOfDouble[1] = (y + (d3 + cv * d2) * h);
    d1 += increment;
    d2 = Math.cos(d1);
    d3 = Math.sin(d1);
    paramArrayOfDouble[2] = (x + (d2 + cv * d3) * w);
    paramArrayOfDouble[3] = (y + (d3 - cv * d2) * h);
    paramArrayOfDouble[4] = (x + d2 * w);
    paramArrayOfDouble[5] = (y + d3 * h);
    if (affine != null) {
      affine.transform(paramArrayOfDouble, 0, paramArrayOfDouble, 0, 3);
    }
    return 3;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\geom\ArcIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
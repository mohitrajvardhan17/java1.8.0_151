package java.awt.geom;

import java.util.NoSuchElementException;
import java.util.Vector;
import sun.awt.geom.Curve;

class AreaIterator
  implements PathIterator
{
  private AffineTransform transform;
  private Vector curves;
  private int index;
  private Curve prevcurve;
  private Curve thiscurve;
  
  public AreaIterator(Vector paramVector, AffineTransform paramAffineTransform)
  {
    curves = paramVector;
    transform = paramAffineTransform;
    if (paramVector.size() >= 1) {
      thiscurve = ((Curve)paramVector.get(0));
    }
  }
  
  public int getWindingRule()
  {
    return 1;
  }
  
  public boolean isDone()
  {
    return (prevcurve == null) && (thiscurve == null);
  }
  
  public void next()
  {
    if (prevcurve != null)
    {
      prevcurve = null;
    }
    else
    {
      prevcurve = thiscurve;
      index += 1;
      if (index < curves.size())
      {
        thiscurve = ((Curve)curves.get(index));
        if ((thiscurve.getOrder() != 0) && (prevcurve.getX1() == thiscurve.getX0()) && (prevcurve.getY1() == thiscurve.getY0())) {
          prevcurve = null;
        }
      }
      else
      {
        thiscurve = null;
      }
    }
  }
  
  public int currentSegment(float[] paramArrayOfFloat)
  {
    double[] arrayOfDouble = new double[6];
    int i = currentSegment(arrayOfDouble);
    int j = i == 3 ? 3 : i == 2 ? 2 : i == 4 ? 0 : 1;
    for (int k = 0; k < j * 2; k++) {
      paramArrayOfFloat[k] = ((float)arrayOfDouble[k]);
    }
    return i;
  }
  
  public int currentSegment(double[] paramArrayOfDouble)
  {
    int i;
    int j;
    if (prevcurve != null)
    {
      if ((thiscurve == null) || (thiscurve.getOrder() == 0)) {
        return 4;
      }
      paramArrayOfDouble[0] = thiscurve.getX0();
      paramArrayOfDouble[1] = thiscurve.getY0();
      i = 1;
      j = 1;
    }
    else
    {
      if (thiscurve == null) {
        throw new NoSuchElementException("area iterator out of bounds");
      }
      i = thiscurve.getSegment(paramArrayOfDouble);
      j = thiscurve.getOrder();
      if (j == 0) {
        j = 1;
      }
    }
    if (transform != null) {
      transform.transform(paramArrayOfDouble, 0, paramArrayOfDouble, 0, j);
    }
    return i;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\geom\AreaIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
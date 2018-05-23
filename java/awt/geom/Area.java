package java.awt.geom;

import java.awt.Rectangle;
import java.awt.Shape;
import java.util.Enumeration;
import java.util.Vector;
import sun.awt.geom.AreaOp;
import sun.awt.geom.AreaOp.AddOp;
import sun.awt.geom.AreaOp.EOWindOp;
import sun.awt.geom.AreaOp.IntOp;
import sun.awt.geom.AreaOp.NZWindOp;
import sun.awt.geom.AreaOp.SubOp;
import sun.awt.geom.AreaOp.XorOp;
import sun.awt.geom.Crossings;
import sun.awt.geom.Curve;

public class Area
  implements Shape, Cloneable
{
  private static Vector EmptyCurves = new Vector();
  private Vector curves;
  private Rectangle2D cachedBounds;
  
  public Area()
  {
    curves = EmptyCurves;
  }
  
  public Area(Shape paramShape)
  {
    if ((paramShape instanceof Area)) {
      curves = curves;
    } else {
      curves = pathToCurves(paramShape.getPathIterator(null));
    }
  }
  
  private static Vector pathToCurves(PathIterator paramPathIterator)
  {
    Vector localVector = new Vector();
    int i = paramPathIterator.getWindingRule();
    double[] arrayOfDouble = new double[23];
    double d1 = 0.0D;
    double d2 = 0.0D;
    double d3 = 0.0D;
    double d4 = 0.0D;
    while (!paramPathIterator.isDone())
    {
      double d5;
      double d6;
      switch (paramPathIterator.currentSegment(arrayOfDouble))
      {
      case 0: 
        Curve.insertLine(localVector, d3, d4, d1, d2);
        d3 = d1 = arrayOfDouble[0];
        d4 = d2 = arrayOfDouble[1];
        Curve.insertMove(localVector, d1, d2);
        break;
      case 1: 
        d5 = arrayOfDouble[0];
        d6 = arrayOfDouble[1];
        Curve.insertLine(localVector, d3, d4, d5, d6);
        d3 = d5;
        d4 = d6;
        break;
      case 2: 
        d5 = arrayOfDouble[2];
        d6 = arrayOfDouble[3];
        Curve.insertQuad(localVector, d3, d4, arrayOfDouble);
        d3 = d5;
        d4 = d6;
        break;
      case 3: 
        d5 = arrayOfDouble[4];
        d6 = arrayOfDouble[5];
        Curve.insertCubic(localVector, d3, d4, arrayOfDouble);
        d3 = d5;
        d4 = d6;
        break;
      case 4: 
        Curve.insertLine(localVector, d3, d4, d1, d2);
        d3 = d1;
        d4 = d2;
      }
      paramPathIterator.next();
    }
    Curve.insertLine(localVector, d3, d4, d1, d2);
    Object localObject;
    if (i == 0) {
      localObject = new AreaOp.EOWindOp();
    } else {
      localObject = new AreaOp.NZWindOp();
    }
    return ((AreaOp)localObject).calculate(localVector, EmptyCurves);
  }
  
  public void add(Area paramArea)
  {
    curves = new AreaOp.AddOp().calculate(curves, curves);
    invalidateBounds();
  }
  
  public void subtract(Area paramArea)
  {
    curves = new AreaOp.SubOp().calculate(curves, curves);
    invalidateBounds();
  }
  
  public void intersect(Area paramArea)
  {
    curves = new AreaOp.IntOp().calculate(curves, curves);
    invalidateBounds();
  }
  
  public void exclusiveOr(Area paramArea)
  {
    curves = new AreaOp.XorOp().calculate(curves, curves);
    invalidateBounds();
  }
  
  public void reset()
  {
    curves = new Vector();
    invalidateBounds();
  }
  
  public boolean isEmpty()
  {
    return curves.size() == 0;
  }
  
  public boolean isPolygonal()
  {
    Enumeration localEnumeration = curves.elements();
    while (localEnumeration.hasMoreElements()) {
      if (((Curve)localEnumeration.nextElement()).getOrder() > 1) {
        return false;
      }
    }
    return true;
  }
  
  public boolean isRectangular()
  {
    int i = curves.size();
    if (i == 0) {
      return true;
    }
    if (i > 3) {
      return false;
    }
    Curve localCurve1 = (Curve)curves.get(1);
    Curve localCurve2 = (Curve)curves.get(2);
    if ((localCurve1.getOrder() != 1) || (localCurve2.getOrder() != 1)) {
      return false;
    }
    if ((localCurve1.getXTop() != localCurve1.getXBot()) || (localCurve2.getXTop() != localCurve2.getXBot())) {
      return false;
    }
    return (localCurve1.getYTop() == localCurve2.getYTop()) && (localCurve1.getYBot() == localCurve2.getYBot());
  }
  
  public boolean isSingular()
  {
    if (curves.size() < 3) {
      return true;
    }
    Enumeration localEnumeration = curves.elements();
    localEnumeration.nextElement();
    while (localEnumeration.hasMoreElements()) {
      if (((Curve)localEnumeration.nextElement()).getOrder() == 0) {
        return false;
      }
    }
    return true;
  }
  
  private void invalidateBounds()
  {
    cachedBounds = null;
  }
  
  private Rectangle2D getCachedBounds()
  {
    if (cachedBounds != null) {
      return cachedBounds;
    }
    Rectangle2D.Double localDouble = new Rectangle2D.Double();
    if (curves.size() > 0)
    {
      Curve localCurve = (Curve)curves.get(0);
      localDouble.setRect(localCurve.getX0(), localCurve.getY0(), 0.0D, 0.0D);
      for (int i = 1; i < curves.size(); i++) {
        ((Curve)curves.get(i)).enlarge(localDouble);
      }
    }
    return cachedBounds = localDouble;
  }
  
  public Rectangle2D getBounds2D()
  {
    return getCachedBounds().getBounds2D();
  }
  
  public Rectangle getBounds()
  {
    return getCachedBounds().getBounds();
  }
  
  public Object clone()
  {
    return new Area(this);
  }
  
  public boolean equals(Area paramArea)
  {
    if (paramArea == this) {
      return true;
    }
    if (paramArea == null) {
      return false;
    }
    Vector localVector = new AreaOp.XorOp().calculate(curves, curves);
    return localVector.isEmpty();
  }
  
  public void transform(AffineTransform paramAffineTransform)
  {
    if (paramAffineTransform == null) {
      throw new NullPointerException("transform must not be null");
    }
    curves = pathToCurves(getPathIterator(paramAffineTransform));
    invalidateBounds();
  }
  
  public Area createTransformedArea(AffineTransform paramAffineTransform)
  {
    Area localArea = new Area(this);
    localArea.transform(paramAffineTransform);
    return localArea;
  }
  
  public boolean contains(double paramDouble1, double paramDouble2)
  {
    if (!getCachedBounds().contains(paramDouble1, paramDouble2)) {
      return false;
    }
    Enumeration localEnumeration = curves.elements();
    int i = 0;
    while (localEnumeration.hasMoreElements())
    {
      Curve localCurve = (Curve)localEnumeration.nextElement();
      i += localCurve.crossingsFor(paramDouble1, paramDouble2);
    }
    return (i & 0x1) == 1;
  }
  
  public boolean contains(Point2D paramPoint2D)
  {
    return contains(paramPoint2D.getX(), paramPoint2D.getY());
  }
  
  public boolean contains(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4)
  {
    if ((paramDouble3 < 0.0D) || (paramDouble4 < 0.0D)) {
      return false;
    }
    if (!getCachedBounds().contains(paramDouble1, paramDouble2, paramDouble3, paramDouble4)) {
      return false;
    }
    Crossings localCrossings = Crossings.findCrossings(curves, paramDouble1, paramDouble2, paramDouble1 + paramDouble3, paramDouble2 + paramDouble4);
    return (localCrossings != null) && (localCrossings.covers(paramDouble2, paramDouble2 + paramDouble4));
  }
  
  public boolean contains(Rectangle2D paramRectangle2D)
  {
    return contains(paramRectangle2D.getX(), paramRectangle2D.getY(), paramRectangle2D.getWidth(), paramRectangle2D.getHeight());
  }
  
  public boolean intersects(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4)
  {
    if ((paramDouble3 < 0.0D) || (paramDouble4 < 0.0D)) {
      return false;
    }
    if (!getCachedBounds().intersects(paramDouble1, paramDouble2, paramDouble3, paramDouble4)) {
      return false;
    }
    Crossings localCrossings = Crossings.findCrossings(curves, paramDouble1, paramDouble2, paramDouble1 + paramDouble3, paramDouble2 + paramDouble4);
    return (localCrossings == null) || (!localCrossings.isEmpty());
  }
  
  public boolean intersects(Rectangle2D paramRectangle2D)
  {
    return intersects(paramRectangle2D.getX(), paramRectangle2D.getY(), paramRectangle2D.getWidth(), paramRectangle2D.getHeight());
  }
  
  public PathIterator getPathIterator(AffineTransform paramAffineTransform)
  {
    return new AreaIterator(curves, paramAffineTransform);
  }
  
  public PathIterator getPathIterator(AffineTransform paramAffineTransform, double paramDouble)
  {
    return new FlatteningPathIterator(getPathIterator(paramAffineTransform), paramDouble);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\geom\Area.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
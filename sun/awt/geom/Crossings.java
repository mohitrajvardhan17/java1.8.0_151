package sun.awt.geom;

import java.awt.geom.PathIterator;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Vector;

public abstract class Crossings
{
  public static final boolean debug = false;
  int limit = 0;
  double[] yranges = new double[10];
  double xlo;
  double ylo;
  double xhi;
  double yhi;
  private Vector tmp = new Vector();
  
  public Crossings(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4)
  {
    xlo = paramDouble1;
    ylo = paramDouble2;
    xhi = paramDouble3;
    yhi = paramDouble4;
  }
  
  public final double getXLo()
  {
    return xlo;
  }
  
  public final double getYLo()
  {
    return ylo;
  }
  
  public final double getXHi()
  {
    return xhi;
  }
  
  public final double getYHi()
  {
    return yhi;
  }
  
  public abstract void record(double paramDouble1, double paramDouble2, int paramInt);
  
  public void print()
  {
    System.out.println("Crossings [");
    System.out.println("  bounds = [" + ylo + ", " + yhi + "]");
    for (int i = 0; i < limit; i += 2) {
      System.out.println("  [" + yranges[i] + ", " + yranges[(i + 1)] + "]");
    }
    System.out.println("]");
  }
  
  public final boolean isEmpty()
  {
    return limit == 0;
  }
  
  public abstract boolean covers(double paramDouble1, double paramDouble2);
  
  public static Crossings findCrossings(Vector paramVector, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4)
  {
    EvenOdd localEvenOdd = new EvenOdd(paramDouble1, paramDouble2, paramDouble3, paramDouble4);
    Enumeration localEnumeration = paramVector.elements();
    while (localEnumeration.hasMoreElements())
    {
      Curve localCurve = (Curve)localEnumeration.nextElement();
      if (localCurve.accumulateCrossings(localEvenOdd)) {
        return null;
      }
    }
    return localEvenOdd;
  }
  
  public static Crossings findCrossings(PathIterator paramPathIterator, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4)
  {
    Object localObject;
    if (paramPathIterator.getWindingRule() == 0) {
      localObject = new EvenOdd(paramDouble1, paramDouble2, paramDouble3, paramDouble4);
    } else {
      localObject = new NonZero(paramDouble1, paramDouble2, paramDouble3, paramDouble4);
    }
    double[] arrayOfDouble = new double[23];
    double d1 = 0.0D;
    double d2 = 0.0D;
    double d3 = 0.0D;
    double d4 = 0.0D;
    while (!paramPathIterator.isDone())
    {
      int i = paramPathIterator.currentSegment(arrayOfDouble);
      double d5;
      double d6;
      switch (i)
      {
      case 0: 
        if ((d2 != d4) && (((Crossings)localObject).accumulateLine(d3, d4, d1, d2))) {
          return null;
        }
        d1 = d3 = arrayOfDouble[0];
        d2 = d4 = arrayOfDouble[1];
        break;
      case 1: 
        d5 = arrayOfDouble[0];
        d6 = arrayOfDouble[1];
        if (((Crossings)localObject).accumulateLine(d3, d4, d5, d6)) {
          return null;
        }
        d3 = d5;
        d4 = d6;
        break;
      case 2: 
        d5 = arrayOfDouble[2];
        d6 = arrayOfDouble[3];
        if (((Crossings)localObject).accumulateQuad(d3, d4, arrayOfDouble)) {
          return null;
        }
        d3 = d5;
        d4 = d6;
        break;
      case 3: 
        d5 = arrayOfDouble[4];
        d6 = arrayOfDouble[5];
        if (((Crossings)localObject).accumulateCubic(d3, d4, arrayOfDouble)) {
          return null;
        }
        d3 = d5;
        d4 = d6;
        break;
      case 4: 
        if ((d2 != d4) && (((Crossings)localObject).accumulateLine(d3, d4, d1, d2))) {
          return null;
        }
        d3 = d1;
        d4 = d2;
      }
      paramPathIterator.next();
    }
    if ((d2 != d4) && (((Crossings)localObject).accumulateLine(d3, d4, d1, d2))) {
      return null;
    }
    return (Crossings)localObject;
  }
  
  public boolean accumulateLine(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4)
  {
    if (paramDouble2 <= paramDouble4) {
      return accumulateLine(paramDouble1, paramDouble2, paramDouble3, paramDouble4, 1);
    }
    return accumulateLine(paramDouble3, paramDouble4, paramDouble1, paramDouble2, -1);
  }
  
  public boolean accumulateLine(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, int paramInt)
  {
    if ((yhi <= paramDouble2) || (ylo >= paramDouble4)) {
      return false;
    }
    if ((paramDouble1 >= xhi) && (paramDouble3 >= xhi)) {
      return false;
    }
    if (paramDouble2 == paramDouble4) {
      return (paramDouble1 >= xlo) || (paramDouble3 >= xlo);
    }
    double d5 = paramDouble3 - paramDouble1;
    double d6 = paramDouble4 - paramDouble2;
    double d1;
    double d2;
    if (paramDouble2 < ylo)
    {
      d1 = paramDouble1 + (ylo - paramDouble2) * d5 / d6;
      d2 = ylo;
    }
    else
    {
      d1 = paramDouble1;
      d2 = paramDouble2;
    }
    double d3;
    double d4;
    if (yhi < paramDouble4)
    {
      d3 = paramDouble1 + (yhi - paramDouble2) * d5 / d6;
      d4 = yhi;
    }
    else
    {
      d3 = paramDouble3;
      d4 = paramDouble4;
    }
    if ((d1 >= xhi) && (d3 >= xhi)) {
      return false;
    }
    if ((d1 > xlo) || (d3 > xlo)) {
      return true;
    }
    record(d2, d4, paramInt);
    return false;
  }
  
  public boolean accumulateQuad(double paramDouble1, double paramDouble2, double[] paramArrayOfDouble)
  {
    if ((paramDouble2 < ylo) && (paramArrayOfDouble[1] < ylo) && (paramArrayOfDouble[3] < ylo)) {
      return false;
    }
    if ((paramDouble2 > yhi) && (paramArrayOfDouble[1] > yhi) && (paramArrayOfDouble[3] > yhi)) {
      return false;
    }
    if ((paramDouble1 > xhi) && (paramArrayOfDouble[0] > xhi) && (paramArrayOfDouble[2] > xhi)) {
      return false;
    }
    if ((paramDouble1 < xlo) && (paramArrayOfDouble[0] < xlo) && (paramArrayOfDouble[2] < xlo))
    {
      if (paramDouble2 < paramArrayOfDouble[3]) {
        record(Math.max(paramDouble2, ylo), Math.min(paramArrayOfDouble[3], yhi), 1);
      } else if (paramDouble2 > paramArrayOfDouble[3]) {
        record(Math.max(paramArrayOfDouble[3], ylo), Math.min(paramDouble2, yhi), -1);
      }
      return false;
    }
    Curve.insertQuad(tmp, paramDouble1, paramDouble2, paramArrayOfDouble);
    Enumeration localEnumeration = tmp.elements();
    while (localEnumeration.hasMoreElements())
    {
      Curve localCurve = (Curve)localEnumeration.nextElement();
      if (localCurve.accumulateCrossings(this)) {
        return true;
      }
    }
    tmp.clear();
    return false;
  }
  
  public boolean accumulateCubic(double paramDouble1, double paramDouble2, double[] paramArrayOfDouble)
  {
    if ((paramDouble2 < ylo) && (paramArrayOfDouble[1] < ylo) && (paramArrayOfDouble[3] < ylo) && (paramArrayOfDouble[5] < ylo)) {
      return false;
    }
    if ((paramDouble2 > yhi) && (paramArrayOfDouble[1] > yhi) && (paramArrayOfDouble[3] > yhi) && (paramArrayOfDouble[5] > yhi)) {
      return false;
    }
    if ((paramDouble1 > xhi) && (paramArrayOfDouble[0] > xhi) && (paramArrayOfDouble[2] > xhi) && (paramArrayOfDouble[4] > xhi)) {
      return false;
    }
    if ((paramDouble1 < xlo) && (paramArrayOfDouble[0] < xlo) && (paramArrayOfDouble[2] < xlo) && (paramArrayOfDouble[4] < xlo))
    {
      if (paramDouble2 <= paramArrayOfDouble[5]) {
        record(Math.max(paramDouble2, ylo), Math.min(paramArrayOfDouble[5], yhi), 1);
      } else {
        record(Math.max(paramArrayOfDouble[5], ylo), Math.min(paramDouble2, yhi), -1);
      }
      return false;
    }
    Curve.insertCubic(tmp, paramDouble1, paramDouble2, paramArrayOfDouble);
    Enumeration localEnumeration = tmp.elements();
    while (localEnumeration.hasMoreElements())
    {
      Curve localCurve = (Curve)localEnumeration.nextElement();
      if (localCurve.accumulateCrossings(this)) {
        return true;
      }
    }
    tmp.clear();
    return false;
  }
  
  public static final class EvenOdd
    extends Crossings
  {
    public EvenOdd(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4)
    {
      super(paramDouble2, paramDouble3, paramDouble4);
    }
    
    public final boolean covers(double paramDouble1, double paramDouble2)
    {
      return (limit == 2) && (yranges[0] <= paramDouble1) && (yranges[1] >= paramDouble2);
    }
    
    public void record(double paramDouble1, double paramDouble2, int paramInt)
    {
      if (paramDouble1 >= paramDouble2) {
        return;
      }
      for (int i = 0; (i < limit) && (paramDouble1 > yranges[(i + 1)]); i += 2) {}
      int j = i;
      while (i < limit)
      {
        double d1 = yranges[(i++)];
        double d2 = yranges[(i++)];
        if (paramDouble2 < d1)
        {
          yranges[(j++)] = paramDouble1;
          yranges[(j++)] = paramDouble2;
          paramDouble1 = d1;
          paramDouble2 = d2;
        }
        else
        {
          double d3;
          double d4;
          if (paramDouble1 < d1)
          {
            d3 = paramDouble1;
            d4 = d1;
          }
          else
          {
            d3 = d1;
            d4 = paramDouble1;
          }
          double d5;
          double d6;
          if (paramDouble2 < d2)
          {
            d5 = paramDouble2;
            d6 = d2;
          }
          else
          {
            d5 = d2;
            d6 = paramDouble2;
          }
          if (d4 == d5)
          {
            paramDouble1 = d3;
            paramDouble2 = d6;
          }
          else
          {
            if (d4 > d5)
            {
              paramDouble1 = d5;
              d5 = d4;
              d4 = paramDouble1;
            }
            if (d3 != d4)
            {
              yranges[(j++)] = d3;
              yranges[(j++)] = d4;
            }
            paramDouble1 = d5;
            paramDouble2 = d6;
          }
          if (paramDouble1 >= paramDouble2) {
            break;
          }
        }
      }
      if ((j < i) && (i < limit)) {
        System.arraycopy(yranges, i, yranges, j, limit - i);
      }
      j += limit - i;
      if (paramDouble1 < paramDouble2)
      {
        if (j >= yranges.length)
        {
          double[] arrayOfDouble = new double[j + 10];
          System.arraycopy(yranges, 0, arrayOfDouble, 0, j);
          yranges = arrayOfDouble;
        }
        yranges[(j++)] = paramDouble1;
        yranges[(j++)] = paramDouble2;
      }
      limit = j;
    }
  }
  
  public static final class NonZero
    extends Crossings
  {
    private int[] crosscounts = new int[yranges.length / 2];
    
    public NonZero(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4)
    {
      super(paramDouble2, paramDouble3, paramDouble4);
    }
    
    public final boolean covers(double paramDouble1, double paramDouble2)
    {
      int i = 0;
      while (i < limit)
      {
        double d1 = yranges[(i++)];
        double d2 = yranges[(i++)];
        if (paramDouble1 < d2)
        {
          if (paramDouble1 < d1) {
            return false;
          }
          if (paramDouble2 <= d2) {
            return true;
          }
          paramDouble1 = d2;
        }
      }
      return paramDouble1 >= paramDouble2;
    }
    
    public void remove(int paramInt)
    {
      limit -= 2;
      int i = limit - paramInt;
      if (i > 0)
      {
        System.arraycopy(yranges, paramInt + 2, yranges, paramInt, i);
        System.arraycopy(crosscounts, paramInt / 2 + 1, crosscounts, paramInt / 2, i / 2);
      }
    }
    
    public void insert(int paramInt1, double paramDouble1, double paramDouble2, int paramInt2)
    {
      int i = limit - paramInt1;
      double[] arrayOfDouble = yranges;
      int[] arrayOfInt = crosscounts;
      if (limit >= yranges.length)
      {
        yranges = new double[limit + 10];
        System.arraycopy(arrayOfDouble, 0, yranges, 0, paramInt1);
        crosscounts = new int[(limit + 10) / 2];
        System.arraycopy(arrayOfInt, 0, crosscounts, 0, paramInt1 / 2);
      }
      if (i > 0)
      {
        System.arraycopy(arrayOfDouble, paramInt1, yranges, paramInt1 + 2, i);
        System.arraycopy(arrayOfInt, paramInt1 / 2, crosscounts, paramInt1 / 2 + 1, i / 2);
      }
      yranges[(paramInt1 + 0)] = paramDouble1;
      yranges[(paramInt1 + 1)] = paramDouble2;
      crosscounts[(paramInt1 / 2)] = paramInt2;
      limit += 2;
    }
    
    public void record(double paramDouble1, double paramDouble2, int paramInt)
    {
      if (paramDouble1 >= paramDouble2) {
        return;
      }
      for (int i = 0; (i < limit) && (paramDouble1 > yranges[(i + 1)]); i += 2) {}
      if (i < limit)
      {
        int j = crosscounts[(i / 2)];
        double d1 = yranges[(i + 0)];
        double d2 = yranges[(i + 1)];
        if ((d2 == paramDouble1) && (j == paramInt))
        {
          if (i + 2 == limit)
          {
            yranges[(i + 1)] = paramDouble2;
            return;
          }
          remove(i);
          paramDouble1 = d1;
          j = crosscounts[(i / 2)];
          d1 = yranges[(i + 0)];
          d2 = yranges[(i + 1)];
        }
        if (paramDouble2 < d1)
        {
          insert(i, paramDouble1, paramDouble2, paramInt);
          return;
        }
        if ((paramDouble2 == d1) && (j == paramInt))
        {
          yranges[i] = paramDouble1;
          return;
        }
        if (paramDouble1 < d1)
        {
          insert(i, paramDouble1, d1, paramInt);
          i += 2;
          paramDouble1 = d1;
        }
        else if (d1 < paramDouble1)
        {
          insert(i, d1, paramDouble1, j);
          i += 2;
          d1 = paramDouble1;
        }
        int k = j + paramInt;
        double d3 = Math.min(paramDouble2, d2);
        if (k == 0)
        {
          remove(i);
        }
        else
        {
          crosscounts[(i / 2)] = k;
          yranges[(i++)] = paramDouble1;
          yranges[(i++)] = d3;
        }
        paramDouble1 = d1 = d3;
        if (d1 < d2) {
          insert(i, d1, d2, j);
        }
      }
      if (paramDouble1 < paramDouble2) {
        insert(i, paramDouble1, paramDouble2, paramInt);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\geom\Crossings.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
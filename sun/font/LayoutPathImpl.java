package sun.font;

import java.awt.Shape;
import java.awt.font.LayoutPath;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Iterator;

public abstract class LayoutPathImpl
  extends LayoutPath
{
  private static final boolean LOGMAP = false;
  private static final Formatter LOG = new Formatter(System.out);
  
  public LayoutPathImpl() {}
  
  public Point2D pointToPath(double paramDouble1, double paramDouble2)
  {
    Point2D.Double localDouble = new Point2D.Double(paramDouble1, paramDouble2);
    pointToPath(localDouble, localDouble);
    return localDouble;
  }
  
  public Point2D pathToPoint(double paramDouble1, double paramDouble2, boolean paramBoolean)
  {
    Point2D.Double localDouble = new Point2D.Double(paramDouble1, paramDouble2);
    pathToPoint(localDouble, paramBoolean, localDouble);
    return localDouble;
  }
  
  public void pointToPath(double paramDouble1, double paramDouble2, Point2D paramPoint2D)
  {
    paramPoint2D.setLocation(paramDouble1, paramDouble2);
    pointToPath(paramPoint2D, paramPoint2D);
  }
  
  public void pathToPoint(double paramDouble1, double paramDouble2, boolean paramBoolean, Point2D paramPoint2D)
  {
    paramPoint2D.setLocation(paramDouble1, paramDouble2);
    pathToPoint(paramPoint2D, paramBoolean, paramPoint2D);
  }
  
  public abstract double start();
  
  public abstract double end();
  
  public abstract double length();
  
  public abstract Shape mapShape(Shape paramShape);
  
  public static LayoutPathImpl getPath(EndType paramEndType, double... paramVarArgs)
  {
    if ((paramVarArgs.length & 0x1) != 0) {
      throw new IllegalArgumentException("odd number of points not allowed");
    }
    return SegmentPath.get(paramEndType, paramVarArgs);
  }
  
  public static class EmptyPath
    extends LayoutPathImpl
  {
    private AffineTransform tx;
    
    public EmptyPath(AffineTransform paramAffineTransform)
    {
      tx = paramAffineTransform;
    }
    
    public void pathToPoint(Point2D paramPoint2D1, boolean paramBoolean, Point2D paramPoint2D2)
    {
      if (tx != null) {
        tx.transform(paramPoint2D1, paramPoint2D2);
      } else {
        paramPoint2D2.setLocation(paramPoint2D1);
      }
    }
    
    public boolean pointToPath(Point2D paramPoint2D1, Point2D paramPoint2D2)
    {
      paramPoint2D2.setLocation(paramPoint2D1);
      if (tx != null) {
        try
        {
          tx.inverseTransform(paramPoint2D1, paramPoint2D2);
        }
        catch (NoninvertibleTransformException localNoninvertibleTransformException) {}
      }
      return paramPoint2D2.getX() > 0.0D;
    }
    
    public double start()
    {
      return 0.0D;
    }
    
    public double end()
    {
      return 0.0D;
    }
    
    public double length()
    {
      return 0.0D;
    }
    
    public Shape mapShape(Shape paramShape)
    {
      if (tx != null) {
        return tx.createTransformedShape(paramShape);
      }
      return paramShape;
    }
  }
  
  public static enum EndType
  {
    PINNED,  EXTENDED,  CLOSED;
    
    private EndType() {}
    
    public boolean isPinned()
    {
      return this == PINNED;
    }
    
    public boolean isExtended()
    {
      return this == EXTENDED;
    }
    
    public boolean isClosed()
    {
      return this == CLOSED;
    }
  }
  
  public static final class SegmentPath
    extends LayoutPathImpl
  {
    private double[] data;
    LayoutPathImpl.EndType etype;
    
    public static SegmentPath get(LayoutPathImpl.EndType paramEndType, double... paramVarArgs)
    {
      return new LayoutPathImpl.SegmentPathBuilder().build(paramEndType, paramVarArgs);
    }
    
    SegmentPath(double[] paramArrayOfDouble, LayoutPathImpl.EndType paramEndType)
    {
      data = paramArrayOfDouble;
      etype = paramEndType;
    }
    
    public void pathToPoint(Point2D paramPoint2D1, boolean paramBoolean, Point2D paramPoint2D2)
    {
      locateAndGetIndex(paramPoint2D1, paramBoolean, paramPoint2D2);
    }
    
    public boolean pointToPath(Point2D paramPoint2D1, Point2D paramPoint2D2)
    {
      double d1 = paramPoint2D1.getX();
      double d2 = paramPoint2D1.getY();
      double d3 = data[0];
      double d4 = data[1];
      double d5 = data[2];
      double d6 = Double.MAX_VALUE;
      double d7 = 0.0D;
      double d8 = 0.0D;
      double d9 = 0.0D;
      int i = 0;
      for (int j = 3; j < data.length; j += 3)
      {
        double d11 = data[j];
        double d13 = data[(j + 1)];
        double d15 = data[(j + 2)];
        double d16 = d11 - d3;
        double d17 = d13 - d4;
        double d18 = d15 - d5;
        double d19 = d1 - d3;
        double d20 = d2 - d4;
        double d21 = d16 * d19 + d17 * d20;
        double d22;
        double d23;
        double d24;
        int n;
        if ((d18 == 0.0D) || ((d21 < 0.0D) && ((!etype.isExtended()) || (j != 3))))
        {
          d22 = d3;
          d23 = d4;
          d24 = d5;
          n = j;
        }
        else
        {
          d25 = d18 * d18;
          if ((d21 <= d25) || ((etype.isExtended()) && (j == data.length - 3)))
          {
            d26 = d21 / d25;
            d22 = d3 + d26 * d16;
            d23 = d4 + d26 * d17;
            d24 = d5 + d26 * d18;
            n = j;
          }
          else
          {
            if (j != data.length - 3) {
              break label358;
            }
            d22 = d11;
            d23 = d13;
            d24 = d15;
            n = data.length;
          }
        }
        double d25 = d1 - d22;
        double d26 = d2 - d23;
        double d27 = d25 * d25 + d26 * d26;
        if (d27 <= d6)
        {
          d6 = d27;
          d7 = d22;
          d8 = d23;
          d9 = d24;
          i = n;
        }
        label358:
        d3 = d11;
        d4 = d13;
        d5 = d15;
      }
      d3 = data[(i - 3)];
      d4 = data[(i - 2)];
      if ((d7 != d3) || (d8 != d4))
      {
        double d10 = data[i];
        double d12 = data[(i + 1)];
        double d14 = Math.sqrt(d6);
        if ((d1 - d7) * (d12 - d4) > (d2 - d8) * (d10 - d3)) {
          d14 = -d14;
        }
        paramPoint2D2.setLocation(d9, d14);
        return false;
      }
      int k = (i != 3) && (data[(i - 1)] != data[(i - 4)]) ? 1 : 0;
      int m = (i != data.length) && (data[(i - 1)] != data[(i + 2)]) ? 1 : 0;
      boolean bool = (etype.isExtended()) && ((i == 3) || (i == data.length));
      if ((k != 0) && (m != 0))
      {
        Point2D.Double localDouble1 = new Point2D.Double(d1, d2);
        calcoffset(i - 3, bool, localDouble1);
        Point2D.Double localDouble2 = new Point2D.Double(d1, d2);
        calcoffset(i, bool, localDouble2);
        if (Math.abs(y) > Math.abs(y))
        {
          paramPoint2D2.setLocation(localDouble1);
          return true;
        }
        paramPoint2D2.setLocation(localDouble2);
        return false;
      }
      if (k != 0)
      {
        paramPoint2D2.setLocation(d1, d2);
        calcoffset(i - 3, bool, paramPoint2D2);
        return true;
      }
      paramPoint2D2.setLocation(d1, d2);
      calcoffset(i, bool, paramPoint2D2);
      return false;
    }
    
    private void calcoffset(int paramInt, boolean paramBoolean, Point2D paramPoint2D)
    {
      double d1 = data[(paramInt - 3)];
      double d2 = data[(paramInt - 2)];
      double d3 = paramPoint2D.getX() - d1;
      double d4 = paramPoint2D.getY() - d2;
      double d5 = data[paramInt] - d1;
      double d6 = data[(paramInt + 1)] - d2;
      double d7 = data[(paramInt + 2)] - data[(paramInt - 1)];
      double d8 = (d3 * d5 + d4 * d6) / d7;
      double d9 = (d3 * -d6 + d4 * d5) / d7;
      if (!paramBoolean) {
        if (d8 < 0.0D) {
          d8 = 0.0D;
        } else if (d8 > d7) {
          d8 = d7;
        }
      }
      d8 += data[(paramInt - 1)];
      paramPoint2D.setLocation(d8, d9);
    }
    
    public Shape mapShape(Shape paramShape)
    {
      return new Mapper().mapShape(paramShape);
    }
    
    public double start()
    {
      return data[2];
    }
    
    public double end()
    {
      return data[(data.length - 1)];
    }
    
    public double length()
    {
      return data[(data.length - 1)] - data[2];
    }
    
    private double getClosedAdvance(double paramDouble, boolean paramBoolean)
    {
      if (etype.isClosed())
      {
        paramDouble -= data[2];
        int i = (int)(paramDouble / length());
        paramDouble -= i * length();
        if ((paramDouble < 0.0D) || ((paramDouble == 0.0D) && (paramBoolean))) {
          paramDouble += length();
        }
        paramDouble += data[2];
      }
      return paramDouble;
    }
    
    private int getSegmentIndexForAdvance(double paramDouble, boolean paramBoolean)
    {
      paramDouble = getClosedAdvance(paramDouble, paramBoolean);
      int i = 5;
      int j = data.length - 1;
      while (i < j)
      {
        double d = data[i];
        if ((paramDouble < d) || ((paramDouble == d) && (paramBoolean))) {
          break;
        }
        i += 3;
      }
      return i - 2;
    }
    
    private void map(int paramInt, double paramDouble1, double paramDouble2, Point2D paramPoint2D)
    {
      double d1 = data[paramInt] - data[(paramInt - 3)];
      double d2 = data[(paramInt + 1)] - data[(paramInt - 2)];
      double d3 = data[(paramInt + 2)] - data[(paramInt - 1)];
      double d4 = d1 / d3;
      double d5 = d2 / d3;
      paramDouble1 -= data[(paramInt - 1)];
      paramPoint2D.setLocation(data[(paramInt - 3)] + paramDouble1 * d4 - paramDouble2 * d5, data[(paramInt - 2)] + paramDouble1 * d5 + paramDouble2 * d4);
    }
    
    private int locateAndGetIndex(Point2D paramPoint2D1, boolean paramBoolean, Point2D paramPoint2D2)
    {
      double d1 = paramPoint2D1.getX();
      double d2 = paramPoint2D1.getY();
      int i = getSegmentIndexForAdvance(d1, paramBoolean);
      map(i, d1, d2, paramPoint2D2);
      return i;
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("{");
      localStringBuilder.append(etype.toString());
      localStringBuilder.append(" ");
      for (int i = 0; i < data.length; i += 3)
      {
        if (i > 0) {
          localStringBuilder.append(",");
        }
        float f1 = (int)(data[i] * 100.0D) / 100.0F;
        float f2 = (int)(data[(i + 1)] * 100.0D) / 100.0F;
        float f3 = (int)(data[(i + 2)] * 10.0D) / 10.0F;
        localStringBuilder.append("{");
        localStringBuilder.append(f1);
        localStringBuilder.append(",");
        localStringBuilder.append(f2);
        localStringBuilder.append(",");
        localStringBuilder.append(f3);
        localStringBuilder.append("}");
      }
      localStringBuilder.append("}");
      return localStringBuilder.toString();
    }
    
    class LineInfo
    {
      double sx;
      double sy;
      double lx;
      double ly;
      double m;
      
      LineInfo() {}
      
      void set(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4)
      {
        sx = paramDouble1;
        sy = paramDouble2;
        lx = paramDouble3;
        ly = paramDouble4;
        double d1 = paramDouble3 - paramDouble1;
        if (d1 == 0.0D)
        {
          m = 0.0D;
        }
        else
        {
          double d2 = paramDouble4 - paramDouble2;
          m = (d2 / d1);
        }
      }
      
      void set(LineInfo paramLineInfo)
      {
        sx = sx;
        sy = sy;
        lx = lx;
        ly = ly;
        m = m;
      }
      
      boolean pin(double paramDouble1, double paramDouble2, LineInfo paramLineInfo)
      {
        paramLineInfo.set(this);
        if (lx >= sx)
        {
          if ((sx < paramDouble2) && (lx >= paramDouble1))
          {
            if (sx < paramDouble1)
            {
              if (m != 0.0D) {
                sy += m * (paramDouble1 - sx);
              }
              sx = paramDouble1;
            }
            if (lx > paramDouble2)
            {
              if (m != 0.0D) {
                ly += m * (paramDouble2 - lx);
              }
              lx = paramDouble2;
            }
            return true;
          }
        }
        else if ((lx < paramDouble2) && (sx >= paramDouble1))
        {
          if (lx < paramDouble1)
          {
            if (m != 0.0D) {
              ly += m * (paramDouble1 - lx);
            }
            lx = paramDouble1;
          }
          if (sx > paramDouble2)
          {
            if (m != 0.0D) {
              sy += m * (paramDouble2 - sx);
            }
            sx = paramDouble2;
          }
          return true;
        }
        return false;
      }
      
      boolean pin(int paramInt, LineInfo paramLineInfo)
      {
        double d1 = data[(paramInt - 1)];
        double d2 = data[(paramInt + 2)];
        switch (LayoutPathImpl.1.$SwitchMap$sun$font$LayoutPathImpl$EndType[etype.ordinal()])
        {
        case 1: 
          break;
        case 2: 
          if (paramInt == 3) {
            d1 = Double.NEGATIVE_INFINITY;
          }
          if (paramInt == data.length - 3) {
            d2 = Double.POSITIVE_INFINITY;
          }
          break;
        }
        return pin(d1, d2, paramLineInfo);
      }
    }
    
    class Mapper
    {
      final LayoutPathImpl.SegmentPath.LineInfo li = new LayoutPathImpl.SegmentPath.LineInfo(LayoutPathImpl.SegmentPath.this);
      final ArrayList<LayoutPathImpl.SegmentPath.Segment> segments = new ArrayList();
      final Point2D.Double mpt;
      final Point2D.Double cpt;
      boolean haveMT;
      
      Mapper()
      {
        for (int i = 3; i < data.length; i += 3) {
          if (data[(i + 2)] != data[(i - 1)]) {
            segments.add(new LayoutPathImpl.SegmentPath.Segment(LayoutPathImpl.SegmentPath.this, i));
          }
        }
        mpt = new Point2D.Double();
        cpt = new Point2D.Double();
      }
      
      void init()
      {
        haveMT = false;
        Iterator localIterator = segments.iterator();
        while (localIterator.hasNext())
        {
          LayoutPathImpl.SegmentPath.Segment localSegment = (LayoutPathImpl.SegmentPath.Segment)localIterator.next();
          localSegment.init();
        }
      }
      
      void moveTo(double paramDouble1, double paramDouble2)
      {
        mpt.x = paramDouble1;
        mpt.y = paramDouble2;
        haveMT = true;
      }
      
      void lineTo(double paramDouble1, double paramDouble2)
      {
        if (haveMT)
        {
          cpt.x = mpt.x;
          cpt.y = mpt.y;
        }
        if ((paramDouble1 == cpt.x) && (paramDouble2 == cpt.y)) {
          return;
        }
        LayoutPathImpl.SegmentPath.Segment localSegment;
        if (haveMT)
        {
          haveMT = false;
          localIterator = segments.iterator();
          while (localIterator.hasNext())
          {
            localSegment = (LayoutPathImpl.SegmentPath.Segment)localIterator.next();
            localSegment.move();
          }
        }
        li.set(cpt.x, cpt.y, paramDouble1, paramDouble2);
        Iterator localIterator = segments.iterator();
        while (localIterator.hasNext())
        {
          localSegment = (LayoutPathImpl.SegmentPath.Segment)localIterator.next();
          localSegment.line(li);
        }
        cpt.x = paramDouble1;
        cpt.y = paramDouble2;
      }
      
      void close()
      {
        lineTo(mpt.x, mpt.y);
        Iterator localIterator = segments.iterator();
        while (localIterator.hasNext())
        {
          LayoutPathImpl.SegmentPath.Segment localSegment = (LayoutPathImpl.SegmentPath.Segment)localIterator.next();
          localSegment.close();
        }
      }
      
      public Shape mapShape(Shape paramShape)
      {
        PathIterator localPathIterator = paramShape.getPathIterator(null, 1.0D);
        init();
        double[] arrayOfDouble = new double[2];
        while (!localPathIterator.isDone())
        {
          switch (localPathIterator.currentSegment(arrayOfDouble))
          {
          case 4: 
            close();
            break;
          case 0: 
            moveTo(arrayOfDouble[0], arrayOfDouble[1]);
            break;
          case 1: 
            lineTo(arrayOfDouble[0], arrayOfDouble[1]);
            break;
          }
          localPathIterator.next();
        }
        GeneralPath localGeneralPath = new GeneralPath();
        Iterator localIterator = segments.iterator();
        while (localIterator.hasNext())
        {
          LayoutPathImpl.SegmentPath.Segment localSegment = (LayoutPathImpl.SegmentPath.Segment)localIterator.next();
          localGeneralPath.append(gp, false);
        }
        return localGeneralPath;
      }
    }
    
    class Segment
    {
      final int ix;
      final double ux;
      final double uy;
      final LayoutPathImpl.SegmentPath.LineInfo temp;
      boolean broken;
      double cx;
      double cy;
      GeneralPath gp;
      
      Segment(int paramInt)
      {
        ix = paramInt;
        double d = data[(paramInt + 2)] - data[(paramInt - 1)];
        ux = ((data[paramInt] - data[(paramInt - 3)]) / d);
        uy = ((data[(paramInt + 1)] - data[(paramInt - 2)]) / d);
        temp = new LayoutPathImpl.SegmentPath.LineInfo(LayoutPathImpl.SegmentPath.this);
      }
      
      void init()
      {
        broken = true;
        cx = (cy = Double.MIN_VALUE);
        gp = new GeneralPath();
      }
      
      void move()
      {
        broken = true;
      }
      
      void close()
      {
        if (!broken) {
          gp.closePath();
        }
      }
      
      void line(LayoutPathImpl.SegmentPath.LineInfo paramLineInfo)
      {
        if (paramLineInfo.pin(ix, temp))
        {
          temp.sx -= data[(ix - 1)];
          double d1 = data[(ix - 3)] + temp.sx * ux - temp.sy * uy;
          double d2 = data[(ix - 2)] + temp.sx * uy + temp.sy * ux;
          temp.lx -= data[(ix - 1)];
          double d3 = data[(ix - 3)] + temp.lx * ux - temp.ly * uy;
          double d4 = data[(ix - 2)] + temp.lx * uy + temp.ly * ux;
          if ((d1 != cx) || (d2 != cy)) {
            if (broken) {
              gp.moveTo((float)d1, (float)d2);
            } else {
              gp.lineTo((float)d1, (float)d2);
            }
          }
          gp.lineTo((float)d3, (float)d4);
          broken = false;
          cx = d3;
          cy = d4;
        }
      }
    }
  }
  
  public static final class SegmentPathBuilder
  {
    private double[] data;
    private int w;
    private double px;
    private double py;
    private double a;
    private boolean pconnect;
    
    public SegmentPathBuilder() {}
    
    public void reset(int paramInt)
    {
      if ((data == null) || (paramInt > data.length)) {
        data = new double[paramInt];
      } else if (paramInt == 0) {
        data = null;
      }
      w = 0;
      px = (py = 0.0D);
      pconnect = false;
    }
    
    public LayoutPathImpl.SegmentPath build(LayoutPathImpl.EndType paramEndType, double... paramVarArgs)
    {
      assert (paramVarArgs.length % 2 == 0);
      reset(paramVarArgs.length / 2 * 3);
      for (int i = 0; i < paramVarArgs.length; i += 2) {
        nextPoint(paramVarArgs[i], paramVarArgs[(i + 1)], i != 0);
      }
      return complete(paramEndType);
    }
    
    public void moveTo(double paramDouble1, double paramDouble2)
    {
      nextPoint(paramDouble1, paramDouble2, false);
    }
    
    public void lineTo(double paramDouble1, double paramDouble2)
    {
      nextPoint(paramDouble1, paramDouble2, true);
    }
    
    private void nextPoint(double paramDouble1, double paramDouble2, boolean paramBoolean)
    {
      if ((paramDouble1 == px) && (paramDouble2 == py)) {
        return;
      }
      if (w == 0)
      {
        if (data == null) {
          data = new double[6];
        }
        if (paramBoolean) {
          w = 3;
        }
      }
      if ((w != 0) && (!paramBoolean) && (!pconnect))
      {
        data[(w - 3)] = (px = paramDouble1);
        data[(w - 2)] = (py = paramDouble2);
        return;
      }
      if (w == data.length)
      {
        double[] arrayOfDouble = new double[w * 2];
        System.arraycopy(data, 0, arrayOfDouble, 0, w);
        data = arrayOfDouble;
      }
      if (paramBoolean)
      {
        double d1 = paramDouble1 - px;
        double d2 = paramDouble2 - py;
        a += Math.sqrt(d1 * d1 + d2 * d2);
      }
      data[(w++)] = paramDouble1;
      data[(w++)] = paramDouble2;
      data[(w++)] = a;
      px = paramDouble1;
      py = paramDouble2;
      pconnect = paramBoolean;
    }
    
    public LayoutPathImpl.SegmentPath complete()
    {
      return complete(LayoutPathImpl.EndType.EXTENDED);
    }
    
    public LayoutPathImpl.SegmentPath complete(LayoutPathImpl.EndType paramEndType)
    {
      if ((data == null) || (w < 6)) {
        return null;
      }
      LayoutPathImpl.SegmentPath localSegmentPath;
      if (w == data.length)
      {
        localSegmentPath = new LayoutPathImpl.SegmentPath(data, paramEndType);
        reset(0);
      }
      else
      {
        double[] arrayOfDouble = new double[w];
        System.arraycopy(data, 0, arrayOfDouble, 0, w);
        localSegmentPath = new LayoutPathImpl.SegmentPath(arrayOfDouble, paramEndType);
        reset(2);
      }
      return localSegmentPath;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\font\LayoutPathImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
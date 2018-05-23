package com.sun.xml.internal.bind.v2.runtime.reflect;

import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import java.util.Map;

final class PrimitiveArrayListerDouble<BeanT>
  extends Lister<BeanT, double[], Double, DoubleArrayPack>
{
  private PrimitiveArrayListerDouble() {}
  
  static void register()
  {
    Lister.primitiveArrayListers.put(Double.TYPE, new PrimitiveArrayListerDouble());
  }
  
  public ListIterator<Double> iterator(final double[] paramArrayOfDouble, XMLSerializer paramXMLSerializer)
  {
    new ListIterator()
    {
      int idx = 0;
      
      public boolean hasNext()
      {
        return idx < paramArrayOfDouble.length;
      }
      
      public Double next()
      {
        return Double.valueOf(paramArrayOfDouble[(idx++)]);
      }
    };
  }
  
  public DoubleArrayPack startPacking(BeanT paramBeanT, Accessor<BeanT, double[]> paramAccessor)
  {
    return new DoubleArrayPack();
  }
  
  public void addToPack(DoubleArrayPack paramDoubleArrayPack, Double paramDouble)
  {
    paramDoubleArrayPack.add(paramDouble);
  }
  
  public void endPacking(DoubleArrayPack paramDoubleArrayPack, BeanT paramBeanT, Accessor<BeanT, double[]> paramAccessor)
    throws AccessorException
  {
    paramAccessor.set(paramBeanT, paramDoubleArrayPack.build());
  }
  
  public void reset(BeanT paramBeanT, Accessor<BeanT, double[]> paramAccessor)
    throws AccessorException
  {
    paramAccessor.set(paramBeanT, new double[0]);
  }
  
  static final class DoubleArrayPack
  {
    double[] buf = new double[16];
    int size;
    
    DoubleArrayPack() {}
    
    void add(Double paramDouble)
    {
      if (buf.length == size)
      {
        double[] arrayOfDouble = new double[buf.length * 2];
        System.arraycopy(buf, 0, arrayOfDouble, 0, buf.length);
        buf = arrayOfDouble;
      }
      if (paramDouble != null) {
        buf[(size++)] = paramDouble.doubleValue();
      }
    }
    
    double[] build()
    {
      if (buf.length == size) {
        return buf;
      }
      double[] arrayOfDouble = new double[size];
      System.arraycopy(buf, 0, arrayOfDouble, 0, size);
      return arrayOfDouble;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\reflect\PrimitiveArrayListerDouble.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
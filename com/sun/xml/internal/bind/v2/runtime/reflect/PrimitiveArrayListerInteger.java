package com.sun.xml.internal.bind.v2.runtime.reflect;

import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import java.util.Map;

final class PrimitiveArrayListerInteger<BeanT>
  extends Lister<BeanT, int[], Integer, IntegerArrayPack>
{
  private PrimitiveArrayListerInteger() {}
  
  static void register()
  {
    Lister.primitiveArrayListers.put(Integer.TYPE, new PrimitiveArrayListerInteger());
  }
  
  public ListIterator<Integer> iterator(final int[] paramArrayOfInt, XMLSerializer paramXMLSerializer)
  {
    new ListIterator()
    {
      int idx = 0;
      
      public boolean hasNext()
      {
        return idx < paramArrayOfInt.length;
      }
      
      public Integer next()
      {
        return Integer.valueOf(paramArrayOfInt[(idx++)]);
      }
    };
  }
  
  public IntegerArrayPack startPacking(BeanT paramBeanT, Accessor<BeanT, int[]> paramAccessor)
  {
    return new IntegerArrayPack();
  }
  
  public void addToPack(IntegerArrayPack paramIntegerArrayPack, Integer paramInteger)
  {
    paramIntegerArrayPack.add(paramInteger);
  }
  
  public void endPacking(IntegerArrayPack paramIntegerArrayPack, BeanT paramBeanT, Accessor<BeanT, int[]> paramAccessor)
    throws AccessorException
  {
    paramAccessor.set(paramBeanT, paramIntegerArrayPack.build());
  }
  
  public void reset(BeanT paramBeanT, Accessor<BeanT, int[]> paramAccessor)
    throws AccessorException
  {
    paramAccessor.set(paramBeanT, new int[0]);
  }
  
  static final class IntegerArrayPack
  {
    int[] buf = new int[16];
    int size;
    
    IntegerArrayPack() {}
    
    void add(Integer paramInteger)
    {
      if (buf.length == size)
      {
        int[] arrayOfInt = new int[buf.length * 2];
        System.arraycopy(buf, 0, arrayOfInt, 0, buf.length);
        buf = arrayOfInt;
      }
      if (paramInteger != null) {
        buf[(size++)] = paramInteger.intValue();
      }
    }
    
    int[] build()
    {
      if (buf.length == size) {
        return buf;
      }
      int[] arrayOfInt = new int[size];
      System.arraycopy(buf, 0, arrayOfInt, 0, size);
      return arrayOfInt;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\reflect\PrimitiveArrayListerInteger.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
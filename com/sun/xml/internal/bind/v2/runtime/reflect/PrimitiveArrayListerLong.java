package com.sun.xml.internal.bind.v2.runtime.reflect;

import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import java.util.Map;

final class PrimitiveArrayListerLong<BeanT>
  extends Lister<BeanT, long[], Long, LongArrayPack>
{
  private PrimitiveArrayListerLong() {}
  
  static void register()
  {
    Lister.primitiveArrayListers.put(Long.TYPE, new PrimitiveArrayListerLong());
  }
  
  public ListIterator<Long> iterator(final long[] paramArrayOfLong, XMLSerializer paramXMLSerializer)
  {
    new ListIterator()
    {
      int idx = 0;
      
      public boolean hasNext()
      {
        return idx < paramArrayOfLong.length;
      }
      
      public Long next()
      {
        return Long.valueOf(paramArrayOfLong[(idx++)]);
      }
    };
  }
  
  public LongArrayPack startPacking(BeanT paramBeanT, Accessor<BeanT, long[]> paramAccessor)
  {
    return new LongArrayPack();
  }
  
  public void addToPack(LongArrayPack paramLongArrayPack, Long paramLong)
  {
    paramLongArrayPack.add(paramLong);
  }
  
  public void endPacking(LongArrayPack paramLongArrayPack, BeanT paramBeanT, Accessor<BeanT, long[]> paramAccessor)
    throws AccessorException
  {
    paramAccessor.set(paramBeanT, paramLongArrayPack.build());
  }
  
  public void reset(BeanT paramBeanT, Accessor<BeanT, long[]> paramAccessor)
    throws AccessorException
  {
    paramAccessor.set(paramBeanT, new long[0]);
  }
  
  static final class LongArrayPack
  {
    long[] buf = new long[16];
    int size;
    
    LongArrayPack() {}
    
    void add(Long paramLong)
    {
      if (buf.length == size)
      {
        long[] arrayOfLong = new long[buf.length * 2];
        System.arraycopy(buf, 0, arrayOfLong, 0, buf.length);
        buf = arrayOfLong;
      }
      if (paramLong != null) {
        buf[(size++)] = paramLong.longValue();
      }
    }
    
    long[] build()
    {
      if (buf.length == size) {
        return buf;
      }
      long[] arrayOfLong = new long[size];
      System.arraycopy(buf, 0, arrayOfLong, 0, size);
      return arrayOfLong;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\reflect\PrimitiveArrayListerLong.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
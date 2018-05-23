package com.sun.xml.internal.bind.v2.runtime.reflect;

import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import java.util.Map;

final class PrimitiveArrayListerShort<BeanT>
  extends Lister<BeanT, short[], Short, ShortArrayPack>
{
  private PrimitiveArrayListerShort() {}
  
  static void register()
  {
    Lister.primitiveArrayListers.put(Short.TYPE, new PrimitiveArrayListerShort());
  }
  
  public ListIterator<Short> iterator(final short[] paramArrayOfShort, XMLSerializer paramXMLSerializer)
  {
    new ListIterator()
    {
      int idx = 0;
      
      public boolean hasNext()
      {
        return idx < paramArrayOfShort.length;
      }
      
      public Short next()
      {
        return Short.valueOf(paramArrayOfShort[(idx++)]);
      }
    };
  }
  
  public ShortArrayPack startPacking(BeanT paramBeanT, Accessor<BeanT, short[]> paramAccessor)
  {
    return new ShortArrayPack();
  }
  
  public void addToPack(ShortArrayPack paramShortArrayPack, Short paramShort)
  {
    paramShortArrayPack.add(paramShort);
  }
  
  public void endPacking(ShortArrayPack paramShortArrayPack, BeanT paramBeanT, Accessor<BeanT, short[]> paramAccessor)
    throws AccessorException
  {
    paramAccessor.set(paramBeanT, paramShortArrayPack.build());
  }
  
  public void reset(BeanT paramBeanT, Accessor<BeanT, short[]> paramAccessor)
    throws AccessorException
  {
    paramAccessor.set(paramBeanT, new short[0]);
  }
  
  static final class ShortArrayPack
  {
    short[] buf = new short[16];
    int size;
    
    ShortArrayPack() {}
    
    void add(Short paramShort)
    {
      if (buf.length == size)
      {
        short[] arrayOfShort = new short[buf.length * 2];
        System.arraycopy(buf, 0, arrayOfShort, 0, buf.length);
        buf = arrayOfShort;
      }
      if (paramShort != null) {
        buf[(size++)] = paramShort.shortValue();
      }
    }
    
    short[] build()
    {
      if (buf.length == size) {
        return buf;
      }
      short[] arrayOfShort = new short[size];
      System.arraycopy(buf, 0, arrayOfShort, 0, size);
      return arrayOfShort;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\reflect\PrimitiveArrayListerShort.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
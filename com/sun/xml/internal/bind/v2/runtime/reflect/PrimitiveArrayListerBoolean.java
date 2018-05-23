package com.sun.xml.internal.bind.v2.runtime.reflect;

import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import java.util.Map;

final class PrimitiveArrayListerBoolean<BeanT>
  extends Lister<BeanT, boolean[], Boolean, BooleanArrayPack>
{
  private PrimitiveArrayListerBoolean() {}
  
  static void register()
  {
    Lister.primitiveArrayListers.put(Boolean.TYPE, new PrimitiveArrayListerBoolean());
  }
  
  public ListIterator<Boolean> iterator(final boolean[] paramArrayOfBoolean, XMLSerializer paramXMLSerializer)
  {
    new ListIterator()
    {
      int idx = 0;
      
      public boolean hasNext()
      {
        return idx < paramArrayOfBoolean.length;
      }
      
      public Boolean next()
      {
        return Boolean.valueOf(paramArrayOfBoolean[(idx++)]);
      }
    };
  }
  
  public BooleanArrayPack startPacking(BeanT paramBeanT, Accessor<BeanT, boolean[]> paramAccessor)
  {
    return new BooleanArrayPack();
  }
  
  public void addToPack(BooleanArrayPack paramBooleanArrayPack, Boolean paramBoolean)
  {
    paramBooleanArrayPack.add(paramBoolean);
  }
  
  public void endPacking(BooleanArrayPack paramBooleanArrayPack, BeanT paramBeanT, Accessor<BeanT, boolean[]> paramAccessor)
    throws AccessorException
  {
    paramAccessor.set(paramBeanT, paramBooleanArrayPack.build());
  }
  
  public void reset(BeanT paramBeanT, Accessor<BeanT, boolean[]> paramAccessor)
    throws AccessorException
  {
    paramAccessor.set(paramBeanT, new boolean[0]);
  }
  
  static final class BooleanArrayPack
  {
    boolean[] buf = new boolean[16];
    int size;
    
    BooleanArrayPack() {}
    
    void add(Boolean paramBoolean)
    {
      if (buf.length == size)
      {
        boolean[] arrayOfBoolean = new boolean[buf.length * 2];
        System.arraycopy(buf, 0, arrayOfBoolean, 0, buf.length);
        buf = arrayOfBoolean;
      }
      if (paramBoolean != null) {
        buf[(size++)] = paramBoolean.booleanValue();
      }
    }
    
    boolean[] build()
    {
      if (buf.length == size) {
        return buf;
      }
      boolean[] arrayOfBoolean = new boolean[size];
      System.arraycopy(buf, 0, arrayOfBoolean, 0, size);
      return arrayOfBoolean;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\reflect\PrimitiveArrayListerBoolean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package com.sun.xml.internal.bind.v2.runtime.reflect;

import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import java.util.Map;

final class PrimitiveArrayListerFloat<BeanT>
  extends Lister<BeanT, float[], Float, FloatArrayPack>
{
  private PrimitiveArrayListerFloat() {}
  
  static void register()
  {
    Lister.primitiveArrayListers.put(Float.TYPE, new PrimitiveArrayListerFloat());
  }
  
  public ListIterator<Float> iterator(final float[] paramArrayOfFloat, XMLSerializer paramXMLSerializer)
  {
    new ListIterator()
    {
      int idx = 0;
      
      public boolean hasNext()
      {
        return idx < paramArrayOfFloat.length;
      }
      
      public Float next()
      {
        return Float.valueOf(paramArrayOfFloat[(idx++)]);
      }
    };
  }
  
  public FloatArrayPack startPacking(BeanT paramBeanT, Accessor<BeanT, float[]> paramAccessor)
  {
    return new FloatArrayPack();
  }
  
  public void addToPack(FloatArrayPack paramFloatArrayPack, Float paramFloat)
  {
    paramFloatArrayPack.add(paramFloat);
  }
  
  public void endPacking(FloatArrayPack paramFloatArrayPack, BeanT paramBeanT, Accessor<BeanT, float[]> paramAccessor)
    throws AccessorException
  {
    paramAccessor.set(paramBeanT, paramFloatArrayPack.build());
  }
  
  public void reset(BeanT paramBeanT, Accessor<BeanT, float[]> paramAccessor)
    throws AccessorException
  {
    paramAccessor.set(paramBeanT, new float[0]);
  }
  
  static final class FloatArrayPack
  {
    float[] buf = new float[16];
    int size;
    
    FloatArrayPack() {}
    
    void add(Float paramFloat)
    {
      if (buf.length == size)
      {
        float[] arrayOfFloat = new float[buf.length * 2];
        System.arraycopy(buf, 0, arrayOfFloat, 0, buf.length);
        buf = arrayOfFloat;
      }
      if (paramFloat != null) {
        buf[(size++)] = paramFloat.floatValue();
      }
    }
    
    float[] build()
    {
      if (buf.length == size) {
        return buf;
      }
      float[] arrayOfFloat = new float[size];
      System.arraycopy(buf, 0, arrayOfFloat, 0, size);
      return arrayOfFloat;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\reflect\PrimitiveArrayListerFloat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package com.sun.xml.internal.bind.v2.runtime.reflect;

import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import java.util.Map;

final class PrimitiveArrayListerByte<BeanT>
  extends Lister<BeanT, byte[], Byte, ByteArrayPack>
{
  private PrimitiveArrayListerByte() {}
  
  static void register()
  {
    Lister.primitiveArrayListers.put(Byte.TYPE, new PrimitiveArrayListerByte());
  }
  
  public ListIterator<Byte> iterator(final byte[] paramArrayOfByte, XMLSerializer paramXMLSerializer)
  {
    new ListIterator()
    {
      int idx = 0;
      
      public boolean hasNext()
      {
        return idx < paramArrayOfByte.length;
      }
      
      public Byte next()
      {
        return Byte.valueOf(paramArrayOfByte[(idx++)]);
      }
    };
  }
  
  public ByteArrayPack startPacking(BeanT paramBeanT, Accessor<BeanT, byte[]> paramAccessor)
  {
    return new ByteArrayPack();
  }
  
  public void addToPack(ByteArrayPack paramByteArrayPack, Byte paramByte)
  {
    paramByteArrayPack.add(paramByte);
  }
  
  public void endPacking(ByteArrayPack paramByteArrayPack, BeanT paramBeanT, Accessor<BeanT, byte[]> paramAccessor)
    throws AccessorException
  {
    paramAccessor.set(paramBeanT, paramByteArrayPack.build());
  }
  
  public void reset(BeanT paramBeanT, Accessor<BeanT, byte[]> paramAccessor)
    throws AccessorException
  {
    paramAccessor.set(paramBeanT, new byte[0]);
  }
  
  static final class ByteArrayPack
  {
    byte[] buf = new byte[16];
    int size;
    
    ByteArrayPack() {}
    
    void add(Byte paramByte)
    {
      if (buf.length == size)
      {
        byte[] arrayOfByte = new byte[buf.length * 2];
        System.arraycopy(buf, 0, arrayOfByte, 0, buf.length);
        buf = arrayOfByte;
      }
      if (paramByte != null) {
        buf[(size++)] = paramByte.byteValue();
      }
    }
    
    byte[] build()
    {
      if (buf.length == size) {
        return buf;
      }
      byte[] arrayOfByte = new byte[size];
      System.arraycopy(buf, 0, arrayOfByte, 0, size);
      return arrayOfByte;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\reflect\PrimitiveArrayListerByte.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
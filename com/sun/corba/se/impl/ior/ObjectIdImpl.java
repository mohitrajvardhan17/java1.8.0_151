package com.sun.corba.se.impl.ior;

import com.sun.corba.se.spi.ior.ObjectId;
import java.util.Arrays;
import org.omg.CORBA_2_3.portable.OutputStream;

public final class ObjectIdImpl
  implements ObjectId
{
  private byte[] id;
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof ObjectIdImpl)) {
      return false;
    }
    ObjectIdImpl localObjectIdImpl = (ObjectIdImpl)paramObject;
    return Arrays.equals(id, id);
  }
  
  public int hashCode()
  {
    int i = 17;
    for (int j = 0; j < id.length; j++) {
      i = 37 * i + id[j];
    }
    return i;
  }
  
  public ObjectIdImpl(byte[] paramArrayOfByte)
  {
    id = paramArrayOfByte;
  }
  
  public byte[] getId()
  {
    return id;
  }
  
  public void write(OutputStream paramOutputStream)
  {
    paramOutputStream.write_long(id.length);
    paramOutputStream.write_octet_array(id, 0, id.length);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\ior\ObjectIdImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
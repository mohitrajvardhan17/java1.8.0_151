package com.sun.corba.se.impl.ior;

import com.sun.corba.se.spi.ior.Identifiable;
import java.util.Arrays;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;

public abstract class GenericIdentifiable
  implements Identifiable
{
  private int id;
  private byte[] data;
  
  public GenericIdentifiable(int paramInt, InputStream paramInputStream)
  {
    id = paramInt;
    data = EncapsulationUtility.readOctets(paramInputStream);
  }
  
  public int getId()
  {
    return id;
  }
  
  public void write(OutputStream paramOutputStream)
  {
    paramOutputStream.write_ulong(data.length);
    paramOutputStream.write_octet_array(data, 0, data.length);
  }
  
  public String toString()
  {
    return "GenericIdentifiable[id=" + getId() + "]";
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == null) {
      return false;
    }
    if (!(paramObject instanceof GenericIdentifiable)) {
      return false;
    }
    GenericIdentifiable localGenericIdentifiable = (GenericIdentifiable)paramObject;
    return (getId() == localGenericIdentifiable.getId()) && (Arrays.equals(getData(), localGenericIdentifiable.getData()));
  }
  
  public int hashCode()
  {
    int i = 17;
    for (int j = 0; j < data.length; j++) {
      i = 37 * i + data[j];
    }
    return i;
  }
  
  public GenericIdentifiable(int paramInt, byte[] paramArrayOfByte)
  {
    id = paramInt;
    data = ((byte[])paramArrayOfByte.clone());
  }
  
  public byte[] getData()
  {
    return data;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\ior\GenericIdentifiable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
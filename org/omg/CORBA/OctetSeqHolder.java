package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class OctetSeqHolder
  implements Streamable
{
  public byte[] value = null;
  
  public OctetSeqHolder() {}
  
  public OctetSeqHolder(byte[] paramArrayOfByte)
  {
    value = paramArrayOfByte;
  }
  
  public void _read(InputStream paramInputStream)
  {
    value = OctetSeqHelper.read(paramInputStream);
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    OctetSeqHelper.write(paramOutputStream, value);
  }
  
  public TypeCode _type()
  {
    return OctetSeqHelper.type();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\OctetSeqHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
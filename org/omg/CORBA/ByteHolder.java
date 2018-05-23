package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class ByteHolder
  implements Streamable
{
  public byte value;
  
  public ByteHolder() {}
  
  public ByteHolder(byte paramByte)
  {
    value = paramByte;
  }
  
  public void _read(InputStream paramInputStream)
  {
    value = paramInputStream.read_octet();
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    paramOutputStream.write_octet(value);
  }
  
  public TypeCode _type()
  {
    return ORB.init().get_primitive_tc(TCKind.tk_octet);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\ByteHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
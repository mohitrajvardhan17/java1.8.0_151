package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class TypeCodeHolder
  implements Streamable
{
  public TypeCode value;
  
  public TypeCodeHolder() {}
  
  public TypeCodeHolder(TypeCode paramTypeCode)
  {
    value = paramTypeCode;
  }
  
  public void _read(InputStream paramInputStream)
  {
    value = paramInputStream.read_TypeCode();
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    paramOutputStream.write_TypeCode(value);
  }
  
  public TypeCode _type()
  {
    return ORB.init().get_primitive_tc(TCKind.tk_TypeCode);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\TypeCodeHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
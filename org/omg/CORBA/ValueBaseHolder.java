package org.omg.CORBA;

import java.io.Serializable;
import org.omg.CORBA.portable.Streamable;

public final class ValueBaseHolder
  implements Streamable
{
  public Serializable value;
  
  public ValueBaseHolder() {}
  
  public ValueBaseHolder(Serializable paramSerializable)
  {
    value = paramSerializable;
  }
  
  public void _read(org.omg.CORBA.portable.InputStream paramInputStream)
  {
    value = ((org.omg.CORBA_2_3.portable.InputStream)paramInputStream).read_value();
  }
  
  public void _write(org.omg.CORBA.portable.OutputStream paramOutputStream)
  {
    ((org.omg.CORBA_2_3.portable.OutputStream)paramOutputStream).write_value(value);
  }
  
  public TypeCode _type()
  {
    return ORB.init().get_primitive_tc(TCKind.tk_value);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\ValueBaseHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
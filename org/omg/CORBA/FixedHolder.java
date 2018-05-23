package org.omg.CORBA;

import java.math.BigDecimal;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class FixedHolder
  implements Streamable
{
  public BigDecimal value;
  
  public FixedHolder() {}
  
  public FixedHolder(BigDecimal paramBigDecimal)
  {
    value = paramBigDecimal;
  }
  
  public void _read(InputStream paramInputStream)
  {
    value = paramInputStream.read_fixed();
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    paramOutputStream.write_fixed(value);
  }
  
  public TypeCode _type()
  {
    return ORB.init().get_primitive_tc(TCKind.tk_fixed);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\FixedHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
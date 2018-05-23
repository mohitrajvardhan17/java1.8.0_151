package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

@Deprecated
public final class PrincipalHolder
  implements Streamable
{
  public Principal value;
  
  public PrincipalHolder() {}
  
  public PrincipalHolder(Principal paramPrincipal)
  {
    value = paramPrincipal;
  }
  
  public void _read(InputStream paramInputStream)
  {
    value = paramInputStream.read_Principal();
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    paramOutputStream.write_Principal(value);
  }
  
  public TypeCode _type()
  {
    return ORB.init().get_primitive_tc(TCKind.tk_Principal);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\PrincipalHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class PolicyHolder
  implements Streamable
{
  public Policy value = null;
  
  public PolicyHolder() {}
  
  public PolicyHolder(Policy paramPolicy)
  {
    value = paramPolicy;
  }
  
  public void _read(InputStream paramInputStream)
  {
    value = PolicyHelper.read(paramInputStream);
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    PolicyHelper.write(paramOutputStream, value);
  }
  
  public TypeCode _type()
  {
    return PolicyHelper.type();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\PolicyHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
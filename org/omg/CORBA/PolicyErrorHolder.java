package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class PolicyErrorHolder
  implements Streamable
{
  public PolicyError value = null;
  
  public PolicyErrorHolder() {}
  
  public PolicyErrorHolder(PolicyError paramPolicyError)
  {
    value = paramPolicyError;
  }
  
  public void _read(InputStream paramInputStream)
  {
    value = PolicyErrorHelper.read(paramInputStream);
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    PolicyErrorHelper.write(paramOutputStream, value);
  }
  
  public TypeCode _type()
  {
    return PolicyErrorHelper.type();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\PolicyErrorHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
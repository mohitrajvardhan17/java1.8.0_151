package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class PolicyListHolder
  implements Streamable
{
  public Policy[] value = null;
  
  public PolicyListHolder() {}
  
  public PolicyListHolder(Policy[] paramArrayOfPolicy)
  {
    value = paramArrayOfPolicy;
  }
  
  public void _read(InputStream paramInputStream)
  {
    value = PolicyListHelper.read(paramInputStream);
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    PolicyListHelper.write(paramOutputStream, value);
  }
  
  public TypeCode _type()
  {
    return PolicyListHelper.type();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\PolicyListHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
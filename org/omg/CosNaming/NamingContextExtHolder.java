package org.omg.CosNaming;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class NamingContextExtHolder
  implements Streamable
{
  public NamingContextExt value = null;
  
  public NamingContextExtHolder() {}
  
  public NamingContextExtHolder(NamingContextExt paramNamingContextExt)
  {
    value = paramNamingContextExt;
  }
  
  public void _read(InputStream paramInputStream)
  {
    value = NamingContextExtHelper.read(paramInputStream);
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    NamingContextExtHelper.write(paramOutputStream, value);
  }
  
  public TypeCode _type()
  {
    return NamingContextExtHelper.type();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CosNaming\NamingContextExtHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
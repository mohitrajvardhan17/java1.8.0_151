package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class ParameterModeHolder
  implements Streamable
{
  public ParameterMode value = null;
  
  public ParameterModeHolder() {}
  
  public ParameterModeHolder(ParameterMode paramParameterMode)
  {
    value = paramParameterMode;
  }
  
  public void _read(InputStream paramInputStream)
  {
    value = ParameterModeHelper.read(paramInputStream);
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    ParameterModeHelper.write(paramOutputStream, value);
  }
  
  public TypeCode _type()
  {
    return ParameterModeHelper.type();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\ParameterModeHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
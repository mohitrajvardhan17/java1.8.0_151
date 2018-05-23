package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class CurrentHolder
  implements Streamable
{
  public Current value = null;
  
  public CurrentHolder() {}
  
  public CurrentHolder(Current paramCurrent)
  {
    value = paramCurrent;
  }
  
  public void _read(InputStream paramInputStream)
  {
    value = CurrentHelper.read(paramInputStream);
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    CurrentHelper.write(paramOutputStream, value);
  }
  
  public TypeCode _type()
  {
    return CurrentHelper.type();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\CurrentHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
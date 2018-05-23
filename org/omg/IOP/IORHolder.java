package org.omg.IOP;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class IORHolder
  implements Streamable
{
  public IOR value = null;
  
  public IORHolder() {}
  
  public IORHolder(IOR paramIOR)
  {
    value = paramIOR;
  }
  
  public void _read(InputStream paramInputStream)
  {
    value = IORHelper.read(paramInputStream);
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    IORHelper.write(paramOutputStream, value);
  }
  
  public TypeCode _type()
  {
    return IORHelper.type();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\IOP\IORHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
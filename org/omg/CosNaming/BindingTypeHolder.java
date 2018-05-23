package org.omg.CosNaming;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class BindingTypeHolder
  implements Streamable
{
  public BindingType value = null;
  
  public BindingTypeHolder() {}
  
  public BindingTypeHolder(BindingType paramBindingType)
  {
    value = paramBindingType;
  }
  
  public void _read(InputStream paramInputStream)
  {
    value = BindingTypeHelper.read(paramInputStream);
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    BindingTypeHelper.write(paramOutputStream, value);
  }
  
  public TypeCode _type()
  {
    return BindingTypeHelper.type();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CosNaming\BindingTypeHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
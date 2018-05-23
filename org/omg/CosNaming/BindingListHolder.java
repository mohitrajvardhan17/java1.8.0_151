package org.omg.CosNaming;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class BindingListHolder
  implements Streamable
{
  public Binding[] value = null;
  
  public BindingListHolder() {}
  
  public BindingListHolder(Binding[] paramArrayOfBinding)
  {
    value = paramArrayOfBinding;
  }
  
  public void _read(InputStream paramInputStream)
  {
    value = BindingListHelper.read(paramInputStream);
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    BindingListHelper.write(paramOutputStream, value);
  }
  
  public TypeCode _type()
  {
    return BindingListHelper.type();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CosNaming\BindingListHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
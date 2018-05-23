package org.omg.CosNaming;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class BindingHolder
  implements Streamable
{
  public Binding value = null;
  
  public BindingHolder() {}
  
  public BindingHolder(Binding paramBinding)
  {
    value = paramBinding;
  }
  
  public void _read(InputStream paramInputStream)
  {
    value = BindingHelper.read(paramInputStream);
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    BindingHelper.write(paramOutputStream, value);
  }
  
  public TypeCode _type()
  {
    return BindingHelper.type();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CosNaming\BindingHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
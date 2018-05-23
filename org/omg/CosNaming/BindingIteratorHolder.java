package org.omg.CosNaming;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class BindingIteratorHolder
  implements Streamable
{
  public BindingIterator value = null;
  
  public BindingIteratorHolder() {}
  
  public BindingIteratorHolder(BindingIterator paramBindingIterator)
  {
    value = paramBindingIterator;
  }
  
  public void _read(InputStream paramInputStream)
  {
    value = BindingIteratorHelper.read(paramInputStream);
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    BindingIteratorHelper.write(paramOutputStream, value);
  }
  
  public TypeCode _type()
  {
    return BindingIteratorHelper.type();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CosNaming\BindingIteratorHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
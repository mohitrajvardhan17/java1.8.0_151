package org.omg.IOP;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class MultipleComponentProfileHolder
  implements Streamable
{
  public TaggedComponent[] value = null;
  
  public MultipleComponentProfileHolder() {}
  
  public MultipleComponentProfileHolder(TaggedComponent[] paramArrayOfTaggedComponent)
  {
    value = paramArrayOfTaggedComponent;
  }
  
  public void _read(InputStream paramInputStream)
  {
    value = MultipleComponentProfileHelper.read(paramInputStream);
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    MultipleComponentProfileHelper.write(paramOutputStream, value);
  }
  
  public TypeCode _type()
  {
    return MultipleComponentProfileHelper.type();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\IOP\MultipleComponentProfileHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
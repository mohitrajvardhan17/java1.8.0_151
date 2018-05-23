package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class AnySeqHolder
  implements Streamable
{
  public Any[] value = null;
  
  public AnySeqHolder() {}
  
  public AnySeqHolder(Any[] paramArrayOfAny)
  {
    value = paramArrayOfAny;
  }
  
  public void _read(InputStream paramInputStream)
  {
    value = AnySeqHelper.read(paramInputStream);
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    AnySeqHelper.write(paramOutputStream, value);
  }
  
  public TypeCode _type()
  {
    return AnySeqHelper.type();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\AnySeqHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
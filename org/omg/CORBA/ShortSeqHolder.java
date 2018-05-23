package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class ShortSeqHolder
  implements Streamable
{
  public short[] value = null;
  
  public ShortSeqHolder() {}
  
  public ShortSeqHolder(short[] paramArrayOfShort)
  {
    value = paramArrayOfShort;
  }
  
  public void _read(InputStream paramInputStream)
  {
    value = ShortSeqHelper.read(paramInputStream);
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    ShortSeqHelper.write(paramOutputStream, value);
  }
  
  public TypeCode _type()
  {
    return ShortSeqHelper.type();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\ShortSeqHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
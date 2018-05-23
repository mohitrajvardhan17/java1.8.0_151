package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class ULongLongSeqHolder
  implements Streamable
{
  public long[] value = null;
  
  public ULongLongSeqHolder() {}
  
  public ULongLongSeqHolder(long[] paramArrayOfLong)
  {
    value = paramArrayOfLong;
  }
  
  public void _read(InputStream paramInputStream)
  {
    value = ULongLongSeqHelper.read(paramInputStream);
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    ULongLongSeqHelper.write(paramOutputStream, value);
  }
  
  public TypeCode _type()
  {
    return ULongLongSeqHelper.type();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\ULongLongSeqHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
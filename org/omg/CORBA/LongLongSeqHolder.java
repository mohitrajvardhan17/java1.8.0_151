package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class LongLongSeqHolder
  implements Streamable
{
  public long[] value = null;
  
  public LongLongSeqHolder() {}
  
  public LongLongSeqHolder(long[] paramArrayOfLong)
  {
    value = paramArrayOfLong;
  }
  
  public void _read(InputStream paramInputStream)
  {
    value = LongLongSeqHelper.read(paramInputStream);
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    LongLongSeqHelper.write(paramOutputStream, value);
  }
  
  public TypeCode _type()
  {
    return LongLongSeqHelper.type();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\LongLongSeqHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
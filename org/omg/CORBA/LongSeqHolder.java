package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class LongSeqHolder
  implements Streamable
{
  public int[] value = null;
  
  public LongSeqHolder() {}
  
  public LongSeqHolder(int[] paramArrayOfInt)
  {
    value = paramArrayOfInt;
  }
  
  public void _read(InputStream paramInputStream)
  {
    value = LongSeqHelper.read(paramInputStream);
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    LongSeqHelper.write(paramOutputStream, value);
  }
  
  public TypeCode _type()
  {
    return LongSeqHelper.type();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\LongSeqHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
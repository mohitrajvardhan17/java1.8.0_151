package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class ULongSeqHolder
  implements Streamable
{
  public int[] value = null;
  
  public ULongSeqHolder() {}
  
  public ULongSeqHolder(int[] paramArrayOfInt)
  {
    value = paramArrayOfInt;
  }
  
  public void _read(InputStream paramInputStream)
  {
    value = ULongSeqHelper.read(paramInputStream);
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    ULongSeqHelper.write(paramOutputStream, value);
  }
  
  public TypeCode _type()
  {
    return ULongSeqHelper.type();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\ULongSeqHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
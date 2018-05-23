package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class BooleanSeqHolder
  implements Streamable
{
  public boolean[] value = null;
  
  public BooleanSeqHolder() {}
  
  public BooleanSeqHolder(boolean[] paramArrayOfBoolean)
  {
    value = paramArrayOfBoolean;
  }
  
  public void _read(InputStream paramInputStream)
  {
    value = BooleanSeqHelper.read(paramInputStream);
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    BooleanSeqHelper.write(paramOutputStream, value);
  }
  
  public TypeCode _type()
  {
    return BooleanSeqHelper.type();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\BooleanSeqHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
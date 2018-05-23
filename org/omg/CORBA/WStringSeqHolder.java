package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class WStringSeqHolder
  implements Streamable
{
  public String[] value = null;
  
  public WStringSeqHolder() {}
  
  public WStringSeqHolder(String[] paramArrayOfString)
  {
    value = paramArrayOfString;
  }
  
  public void _read(InputStream paramInputStream)
  {
    value = WStringSeqHelper.read(paramInputStream);
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    WStringSeqHelper.write(paramOutputStream, value);
  }
  
  public TypeCode _type()
  {
    return WStringSeqHelper.type();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\WStringSeqHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
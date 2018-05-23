package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class WrongTransactionHolder
  implements Streamable
{
  public WrongTransaction value = null;
  
  public WrongTransactionHolder() {}
  
  public WrongTransactionHolder(WrongTransaction paramWrongTransaction)
  {
    value = paramWrongTransaction;
  }
  
  public void _read(InputStream paramInputStream)
  {
    value = WrongTransactionHelper.read(paramInputStream);
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    WrongTransactionHelper.write(paramOutputStream, value);
  }
  
  public TypeCode _type()
  {
    return WrongTransactionHelper.type();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\WrongTransactionHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
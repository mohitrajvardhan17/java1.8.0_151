package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class CharSeqHolder
  implements Streamable
{
  public char[] value = null;
  
  public CharSeqHolder() {}
  
  public CharSeqHolder(char[] paramArrayOfChar)
  {
    value = paramArrayOfChar;
  }
  
  public void _read(InputStream paramInputStream)
  {
    value = CharSeqHelper.read(paramInputStream);
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    CharSeqHelper.write(paramOutputStream, value);
  }
  
  public TypeCode _type()
  {
    return CharSeqHelper.type();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\CharSeqHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
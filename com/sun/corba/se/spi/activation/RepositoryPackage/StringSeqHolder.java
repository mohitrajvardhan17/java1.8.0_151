package com.sun.corba.se.spi.activation.RepositoryPackage;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class StringSeqHolder
  implements Streamable
{
  public String[] value = null;
  
  public StringSeqHolder() {}
  
  public StringSeqHolder(String[] paramArrayOfString)
  {
    value = paramArrayOfString;
  }
  
  public void _read(InputStream paramInputStream)
  {
    value = StringSeqHelper.read(paramInputStream);
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    StringSeqHelper.write(paramOutputStream, value);
  }
  
  public TypeCode _type()
  {
    return StringSeqHelper.type();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\activation\RepositoryPackage\StringSeqHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
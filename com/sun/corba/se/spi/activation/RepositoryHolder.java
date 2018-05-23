package com.sun.corba.se.spi.activation;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class RepositoryHolder
  implements Streamable
{
  public Repository value = null;
  
  public RepositoryHolder() {}
  
  public RepositoryHolder(Repository paramRepository)
  {
    value = paramRepository;
  }
  
  public void _read(InputStream paramInputStream)
  {
    value = RepositoryHelper.read(paramInputStream);
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    RepositoryHelper.write(paramOutputStream, value);
  }
  
  public TypeCode _type()
  {
    return RepositoryHelper.type();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\activation\RepositoryHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
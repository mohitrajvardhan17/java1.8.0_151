package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class UnknownUserExceptionHolder
  implements Streamable
{
  public UnknownUserException value = null;
  
  public UnknownUserExceptionHolder() {}
  
  public UnknownUserExceptionHolder(UnknownUserException paramUnknownUserException)
  {
    value = paramUnknownUserException;
  }
  
  public void _read(InputStream paramInputStream)
  {
    value = UnknownUserExceptionHelper.read(paramInputStream);
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    UnknownUserExceptionHelper.write(paramOutputStream, value);
  }
  
  public TypeCode _type()
  {
    return UnknownUserExceptionHelper.type();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\UnknownUserExceptionHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
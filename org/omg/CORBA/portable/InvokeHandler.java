package org.omg.CORBA.portable;

import org.omg.CORBA.SystemException;

public abstract interface InvokeHandler
{
  public abstract OutputStream _invoke(String paramString, InputStream paramInputStream, ResponseHandler paramResponseHandler)
    throws SystemException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\portable\InvokeHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
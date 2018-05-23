package com.sun.corba.se.spi.servicecontext;

import com.sun.corba.se.impl.encoding.CodeSetComponentInfo.CodeSetContext;
import com.sun.corba.se.impl.encoding.MarshalInputStream;
import com.sun.corba.se.impl.encoding.MarshalOutputStream;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import org.omg.CORBA.SystemException;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;

public class CodeSetServiceContext
  extends ServiceContext
{
  public static final int SERVICE_CONTEXT_ID = 1;
  private CodeSetComponentInfo.CodeSetContext csc;
  
  public CodeSetServiceContext(CodeSetComponentInfo.CodeSetContext paramCodeSetContext)
  {
    csc = paramCodeSetContext;
  }
  
  public CodeSetServiceContext(InputStream paramInputStream, GIOPVersion paramGIOPVersion)
  {
    super(paramInputStream, paramGIOPVersion);
    csc = new CodeSetComponentInfo.CodeSetContext();
    csc.read((MarshalInputStream)in);
  }
  
  public int getId()
  {
    return 1;
  }
  
  public void writeData(OutputStream paramOutputStream)
    throws SystemException
  {
    csc.write((MarshalOutputStream)paramOutputStream);
  }
  
  public CodeSetComponentInfo.CodeSetContext getCodeSetContext()
  {
    return csc;
  }
  
  public String toString()
  {
    return "CodeSetServiceContext[ csc=" + csc + " ]";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\servicecontext\CodeSetServiceContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package com.sun.org.omg.CORBA;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.IDLEntity;

public final class OperationDescription
  implements IDLEntity
{
  public String name = null;
  public String id = null;
  public String defined_in = null;
  public String version = null;
  public TypeCode result = null;
  public OperationMode mode = null;
  public String[] contexts = null;
  public ParameterDescription[] parameters = null;
  public ExceptionDescription[] exceptions = null;
  
  public OperationDescription() {}
  
  public OperationDescription(String paramString1, String paramString2, String paramString3, String paramString4, TypeCode paramTypeCode, OperationMode paramOperationMode, String[] paramArrayOfString, ParameterDescription[] paramArrayOfParameterDescription, ExceptionDescription[] paramArrayOfExceptionDescription)
  {
    name = paramString1;
    id = paramString2;
    defined_in = paramString3;
    version = paramString4;
    result = paramTypeCode;
    mode = paramOperationMode;
    contexts = paramArrayOfString;
    parameters = paramArrayOfParameterDescription;
    exceptions = paramArrayOfExceptionDescription;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\omg\CORBA\OperationDescription.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
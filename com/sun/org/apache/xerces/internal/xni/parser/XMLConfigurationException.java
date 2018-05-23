package com.sun.org.apache.xerces.internal.xni.parser;

import com.sun.org.apache.xerces.internal.util.Status;
import com.sun.org.apache.xerces.internal.xni.XNIException;

public class XMLConfigurationException
  extends XNIException
{
  static final long serialVersionUID = -5437427404547669188L;
  protected Status fType;
  protected String fIdentifier;
  
  public XMLConfigurationException(Status paramStatus, String paramString)
  {
    super(paramString);
    fType = paramStatus;
    fIdentifier = paramString;
  }
  
  public XMLConfigurationException(Status paramStatus, String paramString1, String paramString2)
  {
    super(paramString2);
    fType = paramStatus;
    fIdentifier = paramString1;
  }
  
  public Status getType()
  {
    return fType;
  }
  
  public String getIdentifier()
  {
    return fIdentifier;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\xni\parser\XMLConfigurationException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
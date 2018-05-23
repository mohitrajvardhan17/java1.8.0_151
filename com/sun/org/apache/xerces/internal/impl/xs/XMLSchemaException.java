package com.sun.org.apache.xerces.internal.impl.xs;

public class XMLSchemaException
  extends Exception
{
  static final long serialVersionUID = -9096984648537046218L;
  String key;
  Object[] args;
  
  public XMLSchemaException(String paramString, Object[] paramArrayOfObject)
  {
    key = paramString;
    args = paramArrayOfObject;
  }
  
  public String getKey()
  {
    return key;
  }
  
  public Object[] getArgs()
  {
    return args;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\XMLSchemaException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package com.sun.org.apache.xalan.internal.xsltc.runtime;

public class Parameter
{
  public String _name;
  public Object _value;
  public boolean _isDefault;
  
  public Parameter(String paramString, Object paramObject)
  {
    _name = paramString;
    _value = paramObject;
    _isDefault = true;
  }
  
  public Parameter(String paramString, Object paramObject, boolean paramBoolean)
  {
    _name = paramString;
    _value = paramObject;
    _isDefault = paramBoolean;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\runtime\Parameter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
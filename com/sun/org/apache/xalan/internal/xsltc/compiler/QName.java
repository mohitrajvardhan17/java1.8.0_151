package com.sun.org.apache.xalan.internal.xsltc.compiler;

final class QName
{
  private final String _localname;
  private String _prefix;
  private String _namespace;
  private String _stringRep;
  private int _hashCode;
  
  public QName(String paramString1, String paramString2, String paramString3)
  {
    _namespace = paramString1;
    _prefix = paramString2;
    _localname = paramString3;
    _stringRep = ((paramString1 != null) && (!paramString1.equals("")) ? paramString1 + ':' + paramString3 : paramString3);
    _hashCode = (_stringRep.hashCode() + 19);
  }
  
  public void clearNamespace()
  {
    _namespace = "";
  }
  
  public String toString()
  {
    return _stringRep;
  }
  
  public String getStringRep()
  {
    return _stringRep;
  }
  
  public boolean equals(Object paramObject)
  {
    return (this == paramObject) || (((paramObject instanceof QName)) && (_stringRep.equals(((QName)paramObject).getStringRep())));
  }
  
  public String getLocalPart()
  {
    return _localname;
  }
  
  public String getNamespace()
  {
    return _namespace;
  }
  
  public String getPrefix()
  {
    return _prefix;
  }
  
  public int hashCode()
  {
    return _hashCode;
  }
  
  public String dump()
  {
    return "QName: " + _namespace + "(" + _prefix + "):" + _localname;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\QName.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
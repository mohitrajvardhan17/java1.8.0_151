package com.sun.org.apache.xml.internal.dtm.ref;

public final class ExtendedType
{
  private int nodetype;
  private String namespace;
  private String localName;
  private int hash;
  
  public ExtendedType(int paramInt, String paramString1, String paramString2)
  {
    nodetype = paramInt;
    namespace = paramString1;
    localName = paramString2;
    hash = (paramInt + paramString1.hashCode() + paramString2.hashCode());
  }
  
  public ExtendedType(int paramInt1, String paramString1, String paramString2, int paramInt2)
  {
    nodetype = paramInt1;
    namespace = paramString1;
    localName = paramString2;
    hash = paramInt2;
  }
  
  protected void redefine(int paramInt, String paramString1, String paramString2)
  {
    nodetype = paramInt;
    namespace = paramString1;
    localName = paramString2;
    hash = (paramInt + paramString1.hashCode() + paramString2.hashCode());
  }
  
  protected void redefine(int paramInt1, String paramString1, String paramString2, int paramInt2)
  {
    nodetype = paramInt1;
    namespace = paramString1;
    localName = paramString2;
    hash = paramInt2;
  }
  
  public int hashCode()
  {
    return hash;
  }
  
  public boolean equals(ExtendedType paramExtendedType)
  {
    try
    {
      return (nodetype == nodetype) && (localName.equals(localName)) && (namespace.equals(namespace));
    }
    catch (NullPointerException localNullPointerException) {}
    return false;
  }
  
  public int getNodeType()
  {
    return nodetype;
  }
  
  public String getLocalName()
  {
    return localName;
  }
  
  public String getNamespace()
  {
    return namespace;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\dtm\ref\ExtendedType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package com.sun.org.apache.xerces.internal.impl.xs.opti;

public class NodeImpl
  extends DefaultNode
{
  String prefix;
  String localpart;
  String rawname;
  String uri;
  short nodeType;
  boolean hidden;
  
  public NodeImpl() {}
  
  public NodeImpl(String paramString1, String paramString2, String paramString3, String paramString4, short paramShort)
  {
    prefix = paramString1;
    localpart = paramString2;
    rawname = paramString3;
    uri = paramString4;
    nodeType = paramShort;
  }
  
  public String getNodeName()
  {
    return rawname;
  }
  
  public String getNamespaceURI()
  {
    return uri;
  }
  
  public String getPrefix()
  {
    return prefix;
  }
  
  public String getLocalName()
  {
    return localpart;
  }
  
  public short getNodeType()
  {
    return nodeType;
  }
  
  public void setReadOnly(boolean paramBoolean1, boolean paramBoolean2)
  {
    hidden = paramBoolean1;
  }
  
  public boolean getReadOnly()
  {
    return hidden;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\opti\NodeImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
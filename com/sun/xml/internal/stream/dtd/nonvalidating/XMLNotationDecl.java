package com.sun.xml.internal.stream.dtd.nonvalidating;

public class XMLNotationDecl
{
  public String name;
  public String publicId;
  public String systemId;
  public String baseSystemId;
  
  public XMLNotationDecl() {}
  
  public void setValues(String paramString1, String paramString2, String paramString3, String paramString4)
  {
    name = paramString1;
    publicId = paramString2;
    systemId = paramString3;
    baseSystemId = paramString4;
  }
  
  public void clear()
  {
    name = null;
    publicId = null;
    systemId = null;
    baseSystemId = null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\stream\dtd\nonvalidating\XMLNotationDecl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
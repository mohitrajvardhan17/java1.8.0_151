package com.sun.xml.internal.stream.dtd.nonvalidating;

import com.sun.org.apache.xerces.internal.xni.QName;

public class XMLAttributeDecl
{
  public final QName name = new QName();
  public final XMLSimpleType simpleType = new XMLSimpleType();
  public boolean optional;
  
  public XMLAttributeDecl() {}
  
  public void setValues(QName paramQName, XMLSimpleType paramXMLSimpleType, boolean paramBoolean)
  {
    name.setValues(paramQName);
    simpleType.setValues(paramXMLSimpleType);
    optional = paramBoolean;
  }
  
  public void clear()
  {
    name.clear();
    simpleType.clear();
    optional = false;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\stream\dtd\nonvalidating\XMLAttributeDecl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
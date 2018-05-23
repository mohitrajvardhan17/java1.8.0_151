package com.sun.org.apache.xml.internal.serializer;

import java.util.Vector;

abstract interface XSLOutputAttributes
{
  public abstract String getDoctypePublic();
  
  public abstract String getDoctypeSystem();
  
  public abstract String getEncoding();
  
  public abstract boolean getIndent();
  
  public abstract int getIndentAmount();
  
  public abstract String getMediaType();
  
  public abstract boolean getOmitXMLDeclaration();
  
  public abstract String getStandalone();
  
  public abstract String getVersion();
  
  public abstract void setCdataSectionElements(Vector paramVector);
  
  public abstract void setDoctype(String paramString1, String paramString2);
  
  public abstract void setDoctypePublic(String paramString);
  
  public abstract void setDoctypeSystem(String paramString);
  
  public abstract void setEncoding(String paramString);
  
  public abstract void setIndent(boolean paramBoolean);
  
  public abstract void setMediaType(String paramString);
  
  public abstract void setOmitXMLDeclaration(boolean paramBoolean);
  
  public abstract void setStandalone(String paramString);
  
  public abstract void setVersion(String paramString);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\serializer\XSLOutputAttributes.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
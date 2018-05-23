package com.sun.org.apache.xerces.internal.xinclude;

import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentFilter;

public abstract interface XPointerSchema
  extends XMLComponent, XMLDocumentFilter
{
  public abstract void setXPointerSchemaName(String paramString);
  
  public abstract String getXpointerSchemaName();
  
  public abstract void setParent(Object paramObject);
  
  public abstract Object getParent();
  
  public abstract void setXPointerSchemaPointer(String paramString);
  
  public abstract String getXPointerSchemaPointer();
  
  public abstract boolean isSubResourceIndentified();
  
  public abstract void reset();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\xinclude\XPointerSchema.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
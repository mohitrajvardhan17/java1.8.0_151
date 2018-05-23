package org.w3c.dom;

public abstract interface DOMImplementation
{
  public abstract boolean hasFeature(String paramString1, String paramString2);
  
  public abstract DocumentType createDocumentType(String paramString1, String paramString2, String paramString3)
    throws DOMException;
  
  public abstract Document createDocument(String paramString1, String paramString2, DocumentType paramDocumentType)
    throws DOMException;
  
  public abstract Object getFeature(String paramString1, String paramString2);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\w3c\dom\DOMImplementation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
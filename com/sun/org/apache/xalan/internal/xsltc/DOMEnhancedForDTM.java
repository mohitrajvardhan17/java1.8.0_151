package com.sun.org.apache.xalan.internal.xsltc;

public abstract interface DOMEnhancedForDTM
  extends DOM
{
  public abstract short[] getMapping(String[] paramArrayOfString1, String[] paramArrayOfString2, int[] paramArrayOfInt);
  
  public abstract int[] getReverseMapping(String[] paramArrayOfString1, String[] paramArrayOfString2, int[] paramArrayOfInt);
  
  public abstract short[] getNamespaceMapping(String[] paramArrayOfString);
  
  public abstract short[] getReverseNamespaceMapping(String[] paramArrayOfString);
  
  public abstract String getDocumentURI();
  
  public abstract void setDocumentURI(String paramString);
  
  public abstract int getExpandedTypeID2(int paramInt);
  
  public abstract boolean hasDOMSource();
  
  public abstract int getElementById(String paramString);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\DOMEnhancedForDTM.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package com.sun.org.apache.xerces.internal.impl.xs;

import com.sun.org.apache.xerces.internal.util.NamespaceSupport;

public class SchemaNamespaceSupport
  extends NamespaceSupport
{
  public SchemaNamespaceSupport() {}
  
  public SchemaNamespaceSupport(SchemaNamespaceSupport paramSchemaNamespaceSupport)
  {
    fNamespaceSize = fNamespaceSize;
    if (fNamespace.length < fNamespaceSize) {
      fNamespace = new String[fNamespaceSize];
    }
    System.arraycopy(fNamespace, 0, fNamespace, 0, fNamespaceSize);
    fCurrentContext = fCurrentContext;
    if (fContext.length <= fCurrentContext) {
      fContext = new int[fCurrentContext + 1];
    }
    System.arraycopy(fContext, 0, fContext, 0, fCurrentContext + 1);
  }
  
  public void setEffectiveContext(String[] paramArrayOfString)
  {
    if ((paramArrayOfString == null) || (paramArrayOfString.length == 0)) {
      return;
    }
    pushContext();
    int i = fNamespaceSize + paramArrayOfString.length;
    if (fNamespace.length < i)
    {
      String[] arrayOfString = new String[i];
      System.arraycopy(fNamespace, 0, arrayOfString, 0, fNamespace.length);
      fNamespace = arrayOfString;
    }
    System.arraycopy(paramArrayOfString, 0, fNamespace, fNamespaceSize, paramArrayOfString.length);
    fNamespaceSize = i;
  }
  
  public String[] getEffectiveLocalContext()
  {
    String[] arrayOfString = null;
    if (fCurrentContext >= 3)
    {
      int i = fContext[3];
      int j = fNamespaceSize - i;
      if (j > 0)
      {
        arrayOfString = new String[j];
        System.arraycopy(fNamespace, i, arrayOfString, 0, j);
      }
    }
    return arrayOfString;
  }
  
  public void makeGlobal()
  {
    if (fCurrentContext >= 3)
    {
      fCurrentContext = 3;
      fNamespaceSize = fContext[3];
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\SchemaNamespaceSupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package com.sun.org.apache.xerces.internal.impl.xs.util;

import com.sun.org.apache.xerces.internal.util.SymbolHash;
import com.sun.org.apache.xerces.internal.xs.XSObject;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;

public final class XSNamedMap4Types
  extends XSNamedMapImpl
{
  private final short fType;
  
  public XSNamedMap4Types(String paramString, SymbolHash paramSymbolHash, short paramShort)
  {
    super(paramString, paramSymbolHash);
    fType = paramShort;
  }
  
  public XSNamedMap4Types(String[] paramArrayOfString, SymbolHash[] paramArrayOfSymbolHash, int paramInt, short paramShort)
  {
    super(paramArrayOfString, paramArrayOfSymbolHash, paramInt);
    fType = paramShort;
  }
  
  public synchronized int getLength()
  {
    if (fLength == -1)
    {
      int i = 0;
      for (int j = 0; j < fNSNum; j++) {
        i += fMaps[j].getLength();
      }
      j = 0;
      XSObject[] arrayOfXSObject = new XSObject[i];
      for (int k = 0; k < fNSNum; k++) {
        j += fMaps[k].getValues(arrayOfXSObject, j);
      }
      fLength = 0;
      fArray = new XSObject[i];
      for (int m = 0; m < i; m++)
      {
        XSTypeDefinition localXSTypeDefinition = (XSTypeDefinition)arrayOfXSObject[m];
        if (localXSTypeDefinition.getTypeCategory() == fType) {
          fArray[(fLength++)] = localXSTypeDefinition;
        }
      }
    }
    return fLength;
  }
  
  public XSObject itemByName(String paramString1, String paramString2)
  {
    for (int i = 0; i < fNSNum; i++) {
      if (isEqual(paramString1, fNamespaces[i]))
      {
        XSTypeDefinition localXSTypeDefinition = (XSTypeDefinition)fMaps[i].get(paramString2);
        if ((localXSTypeDefinition != null) && (localXSTypeDefinition.getTypeCategory() == fType)) {
          return localXSTypeDefinition;
        }
        return null;
      }
    }
    return null;
  }
  
  public synchronized XSObject item(int paramInt)
  {
    if (fArray == null) {
      getLength();
    }
    if ((paramInt < 0) || (paramInt >= fLength)) {
      return null;
    }
    return fArray[paramInt];
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\util\XSNamedMap4Types.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
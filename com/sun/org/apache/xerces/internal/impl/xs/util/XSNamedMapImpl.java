package com.sun.org.apache.xerces.internal.impl.xs.util;

import com.sun.org.apache.xerces.internal.util.SymbolHash;
import com.sun.org.apache.xerces.internal.xs.XSNamedMap;
import com.sun.org.apache.xerces.internal.xs.XSObject;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.xml.namespace.QName;

public class XSNamedMapImpl
  extends AbstractMap
  implements XSNamedMap
{
  public static final XSNamedMapImpl EMPTY_MAP = new XSNamedMapImpl(new XSObject[0], 0);
  final String[] fNamespaces;
  final int fNSNum;
  final SymbolHash[] fMaps;
  XSObject[] fArray = null;
  int fLength = -1;
  private Set fEntrySet = null;
  
  public XSNamedMapImpl(String paramString, SymbolHash paramSymbolHash)
  {
    fNamespaces = new String[] { paramString };
    fMaps = new SymbolHash[] { paramSymbolHash };
    fNSNum = 1;
  }
  
  public XSNamedMapImpl(String[] paramArrayOfString, SymbolHash[] paramArrayOfSymbolHash, int paramInt)
  {
    fNamespaces = paramArrayOfString;
    fMaps = paramArrayOfSymbolHash;
    fNSNum = paramInt;
  }
  
  public XSNamedMapImpl(XSObject[] paramArrayOfXSObject, int paramInt)
  {
    if (paramInt == 0)
    {
      fNamespaces = null;
      fMaps = null;
      fNSNum = 0;
      fArray = paramArrayOfXSObject;
      fLength = 0;
      return;
    }
    fNamespaces = new String[] { paramArrayOfXSObject[0].getNamespace() };
    fMaps = null;
    fNSNum = 1;
    fArray = paramArrayOfXSObject;
    fLength = paramInt;
  }
  
  public synchronized int getLength()
  {
    if (fLength == -1)
    {
      fLength = 0;
      for (int i = 0; i < fNSNum; i++) {
        fLength += fMaps[i].getLength();
      }
    }
    return fLength;
  }
  
  public XSObject itemByName(String paramString1, String paramString2)
  {
    for (int i = 0; i < fNSNum; i++) {
      if (isEqual(paramString1, fNamespaces[i]))
      {
        if (fMaps != null) {
          return (XSObject)fMaps[i].get(paramString2);
        }
        for (int j = 0; j < fLength; j++)
        {
          XSObject localXSObject = fArray[j];
          if (localXSObject.getName().equals(paramString2)) {
            return localXSObject;
          }
        }
        return null;
      }
    }
    return null;
  }
  
  public synchronized XSObject item(int paramInt)
  {
    if (fArray == null)
    {
      getLength();
      fArray = new XSObject[fLength];
      int i = 0;
      for (int j = 0; j < fNSNum; j++) {
        i += fMaps[j].getValues(fArray, i);
      }
    }
    if ((paramInt < 0) || (paramInt >= fLength)) {
      return null;
    }
    return fArray[paramInt];
  }
  
  static boolean isEqual(String paramString1, String paramString2)
  {
    return paramString2 == null ? true : paramString1 != null ? paramString1.equals(paramString2) : false;
  }
  
  public boolean containsKey(Object paramObject)
  {
    return get(paramObject) != null;
  }
  
  public Object get(Object paramObject)
  {
    if ((paramObject instanceof QName))
    {
      QName localQName = (QName)paramObject;
      String str1 = localQName.getNamespaceURI();
      if ("".equals(str1)) {
        str1 = null;
      }
      String str2 = localQName.getLocalPart();
      return itemByName(str1, str2);
    }
    return null;
  }
  
  public int size()
  {
    return getLength();
  }
  
  public synchronized Set entrySet()
  {
    if (fEntrySet == null)
    {
      final int i = getLength();
      final XSNamedMapEntry[] arrayOfXSNamedMapEntry = new XSNamedMapEntry[i];
      for (int j = 0; j < i; j++)
      {
        XSObject localXSObject = item(j);
        arrayOfXSNamedMapEntry[j] = new XSNamedMapEntry(new QName(localXSObject.getNamespace(), localXSObject.getName()), localXSObject);
      }
      fEntrySet = new AbstractSet()
      {
        public Iterator iterator()
        {
          new Iterator()
          {
            private int index = 0;
            
            public boolean hasNext()
            {
              return index < val$length;
            }
            
            public Object next()
            {
              if (index < val$length) {
                return val$entries[(index++)];
              }
              throw new NoSuchElementException();
            }
            
            public void remove()
            {
              throw new UnsupportedOperationException();
            }
          };
        }
        
        public int size()
        {
          return i;
        }
      };
    }
    return fEntrySet;
  }
  
  private static final class XSNamedMapEntry
    implements Map.Entry
  {
    private final QName key;
    private final XSObject value;
    
    public XSNamedMapEntry(QName paramQName, XSObject paramXSObject)
    {
      key = paramQName;
      value = paramXSObject;
    }
    
    public Object getKey()
    {
      return key;
    }
    
    public Object getValue()
    {
      return value;
    }
    
    public Object setValue(Object paramObject)
    {
      throw new UnsupportedOperationException();
    }
    
    public boolean equals(Object paramObject)
    {
      if ((paramObject instanceof Map.Entry))
      {
        Map.Entry localEntry = (Map.Entry)paramObject;
        Object localObject1 = localEntry.getKey();
        Object localObject2 = localEntry.getValue();
        return (key == null ? localObject1 == null : key.equals(localObject1)) && (value == null ? localObject2 == null : value.equals(localObject2));
      }
      return false;
    }
    
    public int hashCode()
    {
      return (key == null ? 0 : key.hashCode()) ^ (value == null ? 0 : value.hashCode());
    }
    
    public String toString()
    {
      StringBuffer localStringBuffer = new StringBuffer();
      localStringBuffer.append(String.valueOf(key));
      localStringBuffer.append('=');
      localStringBuffer.append(String.valueOf(value));
      return localStringBuffer.toString();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\util\XSNamedMapImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
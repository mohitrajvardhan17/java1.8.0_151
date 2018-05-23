package com.sun.xml.internal.fastinfoset;

import javax.xml.namespace.QName;

public class QualifiedName
{
  public String prefix;
  public String namespaceName;
  public String localName;
  public String qName;
  public int index;
  public int prefixIndex;
  public int namespaceNameIndex;
  public int localNameIndex;
  public int attributeId;
  public int attributeHash;
  private QName qNameObject;
  
  public QualifiedName() {}
  
  public QualifiedName(String paramString1, String paramString2, String paramString3, String paramString4)
  {
    prefix = paramString1;
    namespaceName = paramString2;
    localName = paramString3;
    qName = paramString4;
    index = -1;
    prefixIndex = 0;
    namespaceNameIndex = 0;
    localNameIndex = -1;
  }
  
  public void set(String paramString1, String paramString2, String paramString3, String paramString4)
  {
    prefix = paramString1;
    namespaceName = paramString2;
    localName = paramString3;
    qName = paramString4;
    index = -1;
    prefixIndex = 0;
    namespaceNameIndex = 0;
    localNameIndex = -1;
    qNameObject = null;
  }
  
  public QualifiedName(String paramString1, String paramString2, String paramString3, String paramString4, int paramInt)
  {
    prefix = paramString1;
    namespaceName = paramString2;
    localName = paramString3;
    qName = paramString4;
    index = paramInt;
    prefixIndex = 0;
    namespaceNameIndex = 0;
    localNameIndex = -1;
  }
  
  public final QualifiedName set(String paramString1, String paramString2, String paramString3, String paramString4, int paramInt)
  {
    prefix = paramString1;
    namespaceName = paramString2;
    localName = paramString3;
    qName = paramString4;
    index = paramInt;
    prefixIndex = 0;
    namespaceNameIndex = 0;
    localNameIndex = -1;
    qNameObject = null;
    return this;
  }
  
  public QualifiedName(String paramString1, String paramString2, String paramString3, String paramString4, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    prefix = paramString1;
    namespaceName = paramString2;
    localName = paramString3;
    qName = paramString4;
    index = paramInt1;
    prefixIndex = (paramInt2 + 1);
    namespaceNameIndex = (paramInt3 + 1);
    localNameIndex = paramInt4;
  }
  
  public final QualifiedName set(String paramString1, String paramString2, String paramString3, String paramString4, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    prefix = paramString1;
    namespaceName = paramString2;
    localName = paramString3;
    qName = paramString4;
    index = paramInt1;
    prefixIndex = (paramInt2 + 1);
    namespaceNameIndex = (paramInt3 + 1);
    localNameIndex = paramInt4;
    qNameObject = null;
    return this;
  }
  
  public QualifiedName(String paramString1, String paramString2, String paramString3)
  {
    prefix = paramString1;
    namespaceName = paramString2;
    localName = paramString3;
    qName = createQNameString(paramString1, paramString3);
    index = -1;
    prefixIndex = 0;
    namespaceNameIndex = 0;
    localNameIndex = -1;
  }
  
  public final QualifiedName set(String paramString1, String paramString2, String paramString3)
  {
    prefix = paramString1;
    namespaceName = paramString2;
    localName = paramString3;
    qName = createQNameString(paramString1, paramString3);
    index = -1;
    prefixIndex = 0;
    namespaceNameIndex = 0;
    localNameIndex = -1;
    qNameObject = null;
    return this;
  }
  
  public QualifiedName(String paramString1, String paramString2, String paramString3, int paramInt1, int paramInt2, int paramInt3, char[] paramArrayOfChar)
  {
    prefix = paramString1;
    namespaceName = paramString2;
    localName = paramString3;
    if (paramArrayOfChar != null)
    {
      int i = paramString1.length();
      int j = paramString3.length();
      int k = i + j + 1;
      if (k < paramArrayOfChar.length)
      {
        paramString1.getChars(0, i, paramArrayOfChar, 0);
        paramArrayOfChar[i] = ':';
        paramString3.getChars(0, j, paramArrayOfChar, i + 1);
        qName = new String(paramArrayOfChar, 0, k);
      }
      else
      {
        qName = createQNameString(paramString1, paramString3);
      }
    }
    else
    {
      qName = localName;
    }
    prefixIndex = (paramInt1 + 1);
    namespaceNameIndex = (paramInt2 + 1);
    localNameIndex = paramInt3;
    index = -1;
  }
  
  public final QualifiedName set(String paramString1, String paramString2, String paramString3, int paramInt1, int paramInt2, int paramInt3, char[] paramArrayOfChar)
  {
    prefix = paramString1;
    namespaceName = paramString2;
    localName = paramString3;
    if (paramArrayOfChar != null)
    {
      int i = paramString1.length();
      int j = paramString3.length();
      int k = i + j + 1;
      if (k < paramArrayOfChar.length)
      {
        paramString1.getChars(0, i, paramArrayOfChar, 0);
        paramArrayOfChar[i] = ':';
        paramString3.getChars(0, j, paramArrayOfChar, i + 1);
        qName = new String(paramArrayOfChar, 0, k);
      }
      else
      {
        qName = createQNameString(paramString1, paramString3);
      }
    }
    else
    {
      qName = localName;
    }
    prefixIndex = (paramInt1 + 1);
    namespaceNameIndex = (paramInt2 + 1);
    localNameIndex = paramInt3;
    index = -1;
    qNameObject = null;
    return this;
  }
  
  public QualifiedName(String paramString1, String paramString2, String paramString3, int paramInt)
  {
    prefix = paramString1;
    namespaceName = paramString2;
    localName = paramString3;
    qName = createQNameString(paramString1, paramString3);
    index = paramInt;
    prefixIndex = 0;
    namespaceNameIndex = 0;
    localNameIndex = -1;
  }
  
  public final QualifiedName set(String paramString1, String paramString2, String paramString3, int paramInt)
  {
    prefix = paramString1;
    namespaceName = paramString2;
    localName = paramString3;
    qName = createQNameString(paramString1, paramString3);
    index = paramInt;
    prefixIndex = 0;
    namespaceNameIndex = 0;
    localNameIndex = -1;
    qNameObject = null;
    return this;
  }
  
  public QualifiedName(String paramString1, String paramString2, String paramString3, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    prefix = paramString1;
    namespaceName = paramString2;
    localName = paramString3;
    qName = createQNameString(paramString1, paramString3);
    index = paramInt1;
    prefixIndex = (paramInt2 + 1);
    namespaceNameIndex = (paramInt3 + 1);
    localNameIndex = paramInt4;
  }
  
  public final QualifiedName set(String paramString1, String paramString2, String paramString3, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    prefix = paramString1;
    namespaceName = paramString2;
    localName = paramString3;
    qName = createQNameString(paramString1, paramString3);
    index = paramInt1;
    prefixIndex = (paramInt2 + 1);
    namespaceNameIndex = (paramInt3 + 1);
    localNameIndex = paramInt4;
    qNameObject = null;
    return this;
  }
  
  public QualifiedName(String paramString1, String paramString2)
  {
    prefix = paramString1;
    namespaceName = paramString2;
    localName = "";
    qName = "";
    index = -1;
    prefixIndex = 0;
    namespaceNameIndex = 0;
    localNameIndex = -1;
  }
  
  public final QualifiedName set(String paramString1, String paramString2)
  {
    prefix = paramString1;
    namespaceName = paramString2;
    localName = "";
    qName = "";
    index = -1;
    prefixIndex = 0;
    namespaceNameIndex = 0;
    localNameIndex = -1;
    qNameObject = null;
    return this;
  }
  
  public final QName getQName()
  {
    if (qNameObject == null) {
      qNameObject = new QName(namespaceName, localName, prefix);
    }
    return qNameObject;
  }
  
  public final String getQNameString()
  {
    if (qName != "") {
      return qName;
    }
    return qName = createQNameString(prefix, localName);
  }
  
  public final void createAttributeValues(int paramInt)
  {
    attributeId = (localNameIndex | namespaceNameIndex << 20);
    attributeHash = (localNameIndex % paramInt);
  }
  
  private final String createQNameString(String paramString1, String paramString2)
  {
    if ((paramString1 != null) && (paramString1.length() > 0))
    {
      StringBuffer localStringBuffer = new StringBuffer(paramString1);
      localStringBuffer.append(':');
      localStringBuffer.append(paramString2);
      return localStringBuffer.toString();
    }
    return paramString2;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\fastinfoset\QualifiedName.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
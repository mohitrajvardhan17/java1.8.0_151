package com.sun.org.apache.xerces.internal.xni;

public class QName
  implements Cloneable
{
  public String prefix;
  public String localpart;
  public String rawname;
  public String uri;
  
  public QName()
  {
    clear();
  }
  
  public QName(String paramString1, String paramString2, String paramString3, String paramString4)
  {
    setValues(paramString1, paramString2, paramString3, paramString4);
  }
  
  public QName(QName paramQName)
  {
    setValues(paramQName);
  }
  
  public void setValues(QName paramQName)
  {
    prefix = prefix;
    localpart = localpart;
    rawname = rawname;
    uri = uri;
  }
  
  public void setValues(String paramString1, String paramString2, String paramString3, String paramString4)
  {
    prefix = paramString1;
    localpart = paramString2;
    rawname = paramString3;
    uri = paramString4;
  }
  
  public void clear()
  {
    prefix = null;
    localpart = null;
    rawname = null;
    uri = null;
  }
  
  public Object clone()
  {
    return new QName(this);
  }
  
  public int hashCode()
  {
    if (uri != null) {
      return uri.hashCode() + (localpart != null ? localpart.hashCode() : 0);
    }
    return rawname != null ? rawname.hashCode() : 0;
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if ((paramObject != null) && ((paramObject instanceof QName)))
    {
      QName localQName = (QName)paramObject;
      if (uri != null) {
        return (localpart.equals(localpart)) && (uri.equals(uri));
      }
      if (uri == null) {
        return rawname.equals(rawname);
      }
    }
    return false;
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    int i = 0;
    if (prefix != null)
    {
      localStringBuffer.append("prefix=\"" + prefix + '"');
      i = 1;
    }
    if (localpart != null)
    {
      if (i != 0) {
        localStringBuffer.append(',');
      }
      localStringBuffer.append("localpart=\"" + localpart + '"');
      i = 1;
    }
    if (rawname != null)
    {
      if (i != 0) {
        localStringBuffer.append(',');
      }
      localStringBuffer.append("rawname=\"" + rawname + '"');
      i = 1;
    }
    if (uri != null)
    {
      if (i != 0) {
        localStringBuffer.append(',');
      }
      localStringBuffer.append("uri=\"" + uri + '"');
    }
    return localStringBuffer.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\xni\QName.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
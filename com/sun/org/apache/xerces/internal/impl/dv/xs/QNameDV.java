package com.sun.org.apache.xerces.internal.impl.dv.xs;

import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.xs.datatypes.XSQName;

public class QNameDV
  extends TypeValidator
{
  private static final String EMPTY_STRING = "".intern();
  
  public QNameDV() {}
  
  public short getAllowedFacets()
  {
    return 2079;
  }
  
  public Object getActualValue(String paramString, ValidationContext paramValidationContext)
    throws InvalidDatatypeValueException
  {
    int i = paramString.indexOf(":");
    String str1;
    String str2;
    if (i > 0)
    {
      str1 = paramValidationContext.getSymbol(paramString.substring(0, i));
      str2 = paramString.substring(i + 1);
    }
    else
    {
      str1 = EMPTY_STRING;
      str2 = paramString;
    }
    if ((str1.length() > 0) && (!XMLChar.isValidNCName(str1))) {
      throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[] { paramString, "QName" });
    }
    if (!XMLChar.isValidNCName(str2)) {
      throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[] { paramString, "QName" });
    }
    String str3 = paramValidationContext.getURI(str1);
    if ((str1.length() > 0) && (str3 == null)) {
      throw new InvalidDatatypeValueException("UndeclaredPrefix", new Object[] { paramString, str1 });
    }
    return new XQName(str1, paramValidationContext.getSymbol(str2), paramValidationContext.getSymbol(paramString), str3);
  }
  
  public int getDataLength(Object paramObject)
  {
    return rawname.length();
  }
  
  private static final class XQName
    extends com.sun.org.apache.xerces.internal.xni.QName
    implements XSQName
  {
    public XQName(String paramString1, String paramString2, String paramString3, String paramString4)
    {
      setValues(paramString1, paramString2, paramString3, paramString4);
    }
    
    public boolean equals(Object paramObject)
    {
      if ((paramObject instanceof com.sun.org.apache.xerces.internal.xni.QName))
      {
        com.sun.org.apache.xerces.internal.xni.QName localQName = (com.sun.org.apache.xerces.internal.xni.QName)paramObject;
        return (uri == uri) && (localpart == localpart);
      }
      return false;
    }
    
    public synchronized String toString()
    {
      return rawname;
    }
    
    public javax.xml.namespace.QName getJAXPQName()
    {
      return new javax.xml.namespace.QName(uri, localpart, prefix);
    }
    
    public com.sun.org.apache.xerces.internal.xni.QName getXNIQName()
    {
      return this;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\dv\xs\QNameDV.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
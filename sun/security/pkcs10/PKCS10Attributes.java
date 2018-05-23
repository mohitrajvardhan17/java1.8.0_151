package sun.security.pkcs10;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import sun.security.util.DerEncoder;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;

public class PKCS10Attributes
  implements DerEncoder
{
  private Hashtable<String, PKCS10Attribute> map = new Hashtable(3);
  
  public PKCS10Attributes() {}
  
  public PKCS10Attributes(PKCS10Attribute[] paramArrayOfPKCS10Attribute)
  {
    for (int i = 0; i < paramArrayOfPKCS10Attribute.length; i++) {
      map.put(paramArrayOfPKCS10Attribute[i].getAttributeId().toString(), paramArrayOfPKCS10Attribute[i]);
    }
  }
  
  public PKCS10Attributes(DerInputStream paramDerInputStream)
    throws IOException
  {
    DerValue[] arrayOfDerValue = paramDerInputStream.getSet(3, true);
    if (arrayOfDerValue == null) {
      throw new IOException("Illegal encoding of attributes");
    }
    for (int i = 0; i < arrayOfDerValue.length; i++)
    {
      PKCS10Attribute localPKCS10Attribute = new PKCS10Attribute(arrayOfDerValue[i]);
      map.put(localPKCS10Attribute.getAttributeId().toString(), localPKCS10Attribute);
    }
  }
  
  public void encode(OutputStream paramOutputStream)
    throws IOException
  {
    derEncode(paramOutputStream);
  }
  
  public void derEncode(OutputStream paramOutputStream)
    throws IOException
  {
    Collection localCollection = map.values();
    PKCS10Attribute[] arrayOfPKCS10Attribute = (PKCS10Attribute[])localCollection.toArray(new PKCS10Attribute[map.size()]);
    DerOutputStream localDerOutputStream = new DerOutputStream();
    localDerOutputStream.putOrderedSetOf(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)0), arrayOfPKCS10Attribute);
    paramOutputStream.write(localDerOutputStream.toByteArray());
  }
  
  public void setAttribute(String paramString, Object paramObject)
  {
    if ((paramObject instanceof PKCS10Attribute)) {
      map.put(paramString, (PKCS10Attribute)paramObject);
    }
  }
  
  public Object getAttribute(String paramString)
  {
    return map.get(paramString);
  }
  
  public void deleteAttribute(String paramString)
  {
    map.remove(paramString);
  }
  
  public Enumeration<PKCS10Attribute> getElements()
  {
    return map.elements();
  }
  
  public Collection<PKCS10Attribute> getAttributes()
  {
    return Collections.unmodifiableCollection(map.values());
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof PKCS10Attributes)) {
      return false;
    }
    Collection localCollection = ((PKCS10Attributes)paramObject).getAttributes();
    PKCS10Attribute[] arrayOfPKCS10Attribute = (PKCS10Attribute[])localCollection.toArray(new PKCS10Attribute[localCollection.size()]);
    int i = arrayOfPKCS10Attribute.length;
    if (i != map.size()) {
      return false;
    }
    String str = null;
    for (int j = 0; j < i; j++)
    {
      PKCS10Attribute localPKCS10Attribute2 = arrayOfPKCS10Attribute[j];
      str = localPKCS10Attribute2.getAttributeId().toString();
      if (str == null) {
        return false;
      }
      PKCS10Attribute localPKCS10Attribute1 = (PKCS10Attribute)map.get(str);
      if (localPKCS10Attribute1 == null) {
        return false;
      }
      if (!localPKCS10Attribute1.equals(localPKCS10Attribute2)) {
        return false;
      }
    }
    return true;
  }
  
  public int hashCode()
  {
    return map.hashCode();
  }
  
  public String toString()
  {
    String str = map.size() + "\n" + map.toString();
    return str;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\pkcs10\PKCS10Attributes.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
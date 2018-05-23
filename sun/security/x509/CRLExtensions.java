package sun.security.x509;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.cert.CRLException;
import java.security.cert.CertificateException;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;

public class CRLExtensions
{
  private Map<String, Extension> map = Collections.synchronizedMap(new TreeMap());
  private boolean unsupportedCritExt = false;
  private static final Class[] PARAMS = { Boolean.class, Object.class };
  
  public CRLExtensions() {}
  
  public CRLExtensions(DerInputStream paramDerInputStream)
    throws CRLException
  {
    init(paramDerInputStream);
  }
  
  private void init(DerInputStream paramDerInputStream)
    throws CRLException
  {
    try
    {
      DerInputStream localDerInputStream = paramDerInputStream;
      int i = (byte)paramDerInputStream.peekByte();
      if (((i & 0xC0) == 128) && ((i & 0x1F) == 0))
      {
        localObject = localDerInputStream.getDerValue();
        localDerInputStream = data;
      }
      Object localObject = localDerInputStream.getSequence(5);
      for (int j = 0; j < localObject.length; j++)
      {
        Extension localExtension = new Extension(localObject[j]);
        parseExtension(localExtension);
      }
    }
    catch (IOException localIOException)
    {
      throw new CRLException("Parsing error: " + localIOException.toString());
    }
  }
  
  private void parseExtension(Extension paramExtension)
    throws CRLException
  {
    try
    {
      Class localClass = OIDMap.getClass(paramExtension.getExtensionId());
      if (localClass == null)
      {
        if (paramExtension.isCritical()) {
          unsupportedCritExt = true;
        }
        if (map.put(paramExtension.getExtensionId().toString(), paramExtension) != null) {
          throw new CRLException("Duplicate extensions not allowed");
        }
        return;
      }
      Constructor localConstructor = localClass.getConstructor(PARAMS);
      Object[] arrayOfObject = { Boolean.valueOf(paramExtension.isCritical()), paramExtension.getExtensionValue() };
      CertAttrSet localCertAttrSet = (CertAttrSet)localConstructor.newInstance(arrayOfObject);
      if (map.put(localCertAttrSet.getName(), (Extension)localCertAttrSet) != null) {
        throw new CRLException("Duplicate extensions not allowed");
      }
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      throw new CRLException(localInvocationTargetException.getTargetException().getMessage());
    }
    catch (Exception localException)
    {
      throw new CRLException(localException.toString());
    }
  }
  
  public void encode(OutputStream paramOutputStream, boolean paramBoolean)
    throws CRLException
  {
    try
    {
      DerOutputStream localDerOutputStream1 = new DerOutputStream();
      Collection localCollection = map.values();
      Object[] arrayOfObject = localCollection.toArray();
      for (int i = 0; i < arrayOfObject.length; i++) {
        if ((arrayOfObject[i] instanceof CertAttrSet)) {
          ((CertAttrSet)arrayOfObject[i]).encode(localDerOutputStream1);
        } else if ((arrayOfObject[i] instanceof Extension)) {
          ((Extension)arrayOfObject[i]).encode(localDerOutputStream1);
        } else {
          throw new CRLException("Illegal extension object");
        }
      }
      DerOutputStream localDerOutputStream2 = new DerOutputStream();
      localDerOutputStream2.write((byte)48, localDerOutputStream1);
      DerOutputStream localDerOutputStream3 = new DerOutputStream();
      if (paramBoolean) {
        localDerOutputStream3.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)0), localDerOutputStream2);
      } else {
        localDerOutputStream3 = localDerOutputStream2;
      }
      paramOutputStream.write(localDerOutputStream3.toByteArray());
    }
    catch (IOException localIOException)
    {
      throw new CRLException("Encoding error: " + localIOException.toString());
    }
    catch (CertificateException localCertificateException)
    {
      throw new CRLException("Encoding error: " + localCertificateException.toString());
    }
  }
  
  public Extension get(String paramString)
  {
    X509AttributeName localX509AttributeName = new X509AttributeName(paramString);
    String str2 = localX509AttributeName.getPrefix();
    String str1;
    if (str2.equalsIgnoreCase("x509"))
    {
      int i = paramString.lastIndexOf(".");
      str1 = paramString.substring(i + 1);
    }
    else
    {
      str1 = paramString;
    }
    return (Extension)map.get(str1);
  }
  
  public void set(String paramString, Object paramObject)
  {
    map.put(paramString, (Extension)paramObject);
  }
  
  public void delete(String paramString)
  {
    map.remove(paramString);
  }
  
  public Enumeration<Extension> getElements()
  {
    return Collections.enumeration(map.values());
  }
  
  public Collection<Extension> getAllExtensions()
  {
    return map.values();
  }
  
  public boolean hasUnsupportedCriticalExtension()
  {
    return unsupportedCritExt;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof CRLExtensions)) {
      return false;
    }
    Collection localCollection = ((CRLExtensions)paramObject).getAllExtensions();
    Object[] arrayOfObject = localCollection.toArray();
    int i = arrayOfObject.length;
    if (i != map.size()) {
      return false;
    }
    String str = null;
    for (int j = 0; j < i; j++)
    {
      if ((arrayOfObject[j] instanceof CertAttrSet)) {
        str = ((CertAttrSet)arrayOfObject[j]).getName();
      }
      Extension localExtension1 = (Extension)arrayOfObject[j];
      if (str == null) {
        str = localExtension1.getExtensionId().toString();
      }
      Extension localExtension2 = (Extension)map.get(str);
      if (localExtension2 == null) {
        return false;
      }
      if (!localExtension2.equals(localExtension1)) {
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
    return map.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\x509\CRLExtensions.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
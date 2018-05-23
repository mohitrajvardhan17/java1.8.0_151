package sun.security.x509;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.cert.CertificateException;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import sun.misc.HexDumpEncoder;
import sun.security.util.Debug;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;

public class CertificateExtensions
  implements CertAttrSet<Extension>
{
  public static final String IDENT = "x509.info.extensions";
  public static final String NAME = "extensions";
  private static final Debug debug = Debug.getInstance("x509");
  private Map<String, Extension> map = Collections.synchronizedMap(new TreeMap());
  private boolean unsupportedCritExt = false;
  private Map<String, Extension> unparseableExtensions;
  private static Class[] PARAMS = { Boolean.class, Object.class };
  
  public CertificateExtensions() {}
  
  public CertificateExtensions(DerInputStream paramDerInputStream)
    throws IOException
  {
    init(paramDerInputStream);
  }
  
  private void init(DerInputStream paramDerInputStream)
    throws IOException
  {
    DerValue[] arrayOfDerValue = paramDerInputStream.getSequence(5);
    for (int i = 0; i < arrayOfDerValue.length; i++)
    {
      Extension localExtension = new Extension(arrayOfDerValue[i]);
      parseExtension(localExtension);
    }
  }
  
  private void parseExtension(Extension paramExtension)
    throws IOException
  {
    try
    {
      Class localClass = OIDMap.getClass(paramExtension.getExtensionId());
      if (localClass == null)
      {
        if (paramExtension.isCritical()) {
          unsupportedCritExt = true;
        }
        if (map.put(paramExtension.getExtensionId().toString(), paramExtension) == null) {
          return;
        }
        throw new IOException("Duplicate extensions not allowed");
      }
      localObject1 = localClass.getConstructor(PARAMS);
      localObject2 = new Object[] { Boolean.valueOf(paramExtension.isCritical()), paramExtension.getExtensionValue() };
      CertAttrSet localCertAttrSet = (CertAttrSet)((Constructor)localObject1).newInstance((Object[])localObject2);
      if (map.put(localCertAttrSet.getName(), (Extension)localCertAttrSet) != null) {
        throw new IOException("Duplicate extensions not allowed");
      }
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      Object localObject2;
      Object localObject1 = localInvocationTargetException.getTargetException();
      if (!paramExtension.isCritical())
      {
        if (unparseableExtensions == null) {
          unparseableExtensions = new TreeMap();
        }
        unparseableExtensions.put(paramExtension.getExtensionId().toString(), new UnparseableExtension(paramExtension, (Throwable)localObject1));
        if (debug != null)
        {
          debug.println("Error parsing extension: " + paramExtension);
          ((Throwable)localObject1).printStackTrace();
          localObject2 = new HexDumpEncoder();
          System.err.println(((HexDumpEncoder)localObject2).encodeBuffer(paramExtension.getExtensionValue()));
        }
        return;
      }
      if ((localObject1 instanceof IOException)) {
        throw ((IOException)localObject1);
      }
      throw new IOException((Throwable)localObject1);
    }
    catch (IOException localIOException)
    {
      throw localIOException;
    }
    catch (Exception localException)
    {
      throw new IOException(localException);
    }
  }
  
  public void encode(OutputStream paramOutputStream)
    throws CertificateException, IOException
  {
    encode(paramOutputStream, false);
  }
  
  public void encode(OutputStream paramOutputStream, boolean paramBoolean)
    throws CertificateException, IOException
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
        throw new CertificateException("Illegal extension object");
      }
    }
    DerOutputStream localDerOutputStream2 = new DerOutputStream();
    localDerOutputStream2.write((byte)48, localDerOutputStream1);
    DerOutputStream localDerOutputStream3;
    if (!paramBoolean)
    {
      localDerOutputStream3 = new DerOutputStream();
      localDerOutputStream3.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)3), localDerOutputStream2);
    }
    else
    {
      localDerOutputStream3 = localDerOutputStream2;
    }
    paramOutputStream.write(localDerOutputStream3.toByteArray());
  }
  
  public void set(String paramString, Object paramObject)
    throws IOException
  {
    if ((paramObject instanceof Extension)) {
      map.put(paramString, (Extension)paramObject);
    } else {
      throw new IOException("Unknown extension type.");
    }
  }
  
  public Extension get(String paramString)
    throws IOException
  {
    Extension localExtension = (Extension)map.get(paramString);
    if (localExtension == null) {
      throw new IOException("No extension found with name " + paramString);
    }
    return localExtension;
  }
  
  Extension getExtension(String paramString)
  {
    return (Extension)map.get(paramString);
  }
  
  public void delete(String paramString)
    throws IOException
  {
    Object localObject = map.get(paramString);
    if (localObject == null) {
      throw new IOException("No extension found with name " + paramString);
    }
    map.remove(paramString);
  }
  
  public String getNameByOid(ObjectIdentifier paramObjectIdentifier)
    throws IOException
  {
    Iterator localIterator = map.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      if (((Extension)map.get(str)).getExtensionId().equals(paramObjectIdentifier)) {
        return str;
      }
    }
    return null;
  }
  
  public Enumeration<Extension> getElements()
  {
    return Collections.enumeration(map.values());
  }
  
  public Collection<Extension> getAllExtensions()
  {
    return map.values();
  }
  
  public Map<String, Extension> getUnparseableExtensions()
  {
    if (unparseableExtensions == null) {
      return Collections.emptyMap();
    }
    return unparseableExtensions;
  }
  
  public String getName()
  {
    return "extensions";
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
    if (!(paramObject instanceof CertificateExtensions)) {
      return false;
    }
    Collection localCollection = ((CertificateExtensions)paramObject).getAllExtensions();
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
    return getUnparseableExtensions().equals(((CertificateExtensions)paramObject).getUnparseableExtensions());
  }
  
  public int hashCode()
  {
    return map.hashCode() + getUnparseableExtensions().hashCode();
  }
  
  public String toString()
  {
    return map.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\x509\CertificateExtensions.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
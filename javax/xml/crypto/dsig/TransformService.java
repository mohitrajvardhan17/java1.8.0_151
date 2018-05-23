package javax.xml.crypto.dsig;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.Provider.Service;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import sun.security.jca.GetInstance;
import sun.security.jca.GetInstance.Instance;

public abstract class TransformService
  implements Transform
{
  private String algorithm;
  private String mechanism;
  private Provider provider;
  
  protected TransformService() {}
  
  public static TransformService getInstance(String paramString1, String paramString2)
    throws NoSuchAlgorithmException
  {
    if ((paramString2 == null) || (paramString1 == null)) {
      throw new NullPointerException();
    }
    int i = 0;
    if (paramString2.equals("DOM")) {
      i = 1;
    }
    List localList = GetInstance.getServices("TransformService", paramString1);
    Iterator localIterator = localList.iterator();
    while (localIterator.hasNext())
    {
      Provider.Service localService = (Provider.Service)localIterator.next();
      String str = localService.getAttribute("MechanismType");
      if (((str == null) && (i != 0)) || ((str != null) && (str.equals(paramString2))))
      {
        GetInstance.Instance localInstance = GetInstance.getInstance(localService, null);
        TransformService localTransformService = (TransformService)impl;
        algorithm = paramString1;
        mechanism = paramString2;
        provider = provider;
        return localTransformService;
      }
    }
    throw new NoSuchAlgorithmException(paramString1 + " algorithm and " + paramString2 + " mechanism not available");
  }
  
  public static TransformService getInstance(String paramString1, String paramString2, Provider paramProvider)
    throws NoSuchAlgorithmException
  {
    if ((paramString2 == null) || (paramString1 == null) || (paramProvider == null)) {
      throw new NullPointerException();
    }
    int i = 0;
    if (paramString2.equals("DOM")) {
      i = 1;
    }
    Provider.Service localService = GetInstance.getService("TransformService", paramString1, paramProvider);
    String str = localService.getAttribute("MechanismType");
    if (((str == null) && (i != 0)) || ((str != null) && (str.equals(paramString2))))
    {
      GetInstance.Instance localInstance = GetInstance.getInstance(localService, null);
      TransformService localTransformService = (TransformService)impl;
      algorithm = paramString1;
      mechanism = paramString2;
      provider = provider;
      return localTransformService;
    }
    throw new NoSuchAlgorithmException(paramString1 + " algorithm and " + paramString2 + " mechanism not available");
  }
  
  public static TransformService getInstance(String paramString1, String paramString2, String paramString3)
    throws NoSuchAlgorithmException, NoSuchProviderException
  {
    if ((paramString2 == null) || (paramString1 == null) || (paramString3 == null)) {
      throw new NullPointerException();
    }
    if (paramString3.length() == 0) {
      throw new NoSuchProviderException();
    }
    int i = 0;
    if (paramString2.equals("DOM")) {
      i = 1;
    }
    Provider.Service localService = GetInstance.getService("TransformService", paramString1, paramString3);
    String str = localService.getAttribute("MechanismType");
    if (((str == null) && (i != 0)) || ((str != null) && (str.equals(paramString2))))
    {
      GetInstance.Instance localInstance = GetInstance.getInstance(localService, null);
      TransformService localTransformService = (TransformService)impl;
      algorithm = paramString1;
      mechanism = paramString2;
      provider = provider;
      return localTransformService;
    }
    throw new NoSuchAlgorithmException(paramString1 + " algorithm and " + paramString2 + " mechanism not available");
  }
  
  public final String getMechanismType()
  {
    return mechanism;
  }
  
  public final String getAlgorithm()
  {
    return algorithm;
  }
  
  public final Provider getProvider()
  {
    return provider;
  }
  
  public abstract void init(TransformParameterSpec paramTransformParameterSpec)
    throws InvalidAlgorithmParameterException;
  
  public abstract void marshalParams(XMLStructure paramXMLStructure, XMLCryptoContext paramXMLCryptoContext)
    throws MarshalException;
  
  public abstract void init(XMLStructure paramXMLStructure, XMLCryptoContext paramXMLCryptoContext)
    throws InvalidAlgorithmParameterException;
  
  private static class MechanismMapEntry
    implements Map.Entry<String, String>
  {
    private final String mechanism;
    private final String algorithm;
    private final String key;
    
    MechanismMapEntry(String paramString1, String paramString2)
    {
      algorithm = paramString1;
      mechanism = paramString2;
      key = ("TransformService." + paramString1 + " MechanismType");
    }
    
    public boolean equals(Object paramObject)
    {
      if (!(paramObject instanceof Map.Entry)) {
        return false;
      }
      Map.Entry localEntry = (Map.Entry)paramObject;
      return (getKey() == null ? localEntry.getKey() == null : getKey().equals(localEntry.getKey())) && (getValue() == null ? localEntry.getValue() == null : getValue().equals(localEntry.getValue()));
    }
    
    public String getKey()
    {
      return key;
    }
    
    public String getValue()
    {
      return mechanism;
    }
    
    public String setValue(String paramString)
    {
      throw new UnsupportedOperationException();
    }
    
    public int hashCode()
    {
      return (getKey() == null ? 0 : getKey().hashCode()) ^ (getValue() == null ? 0 : getValue().hashCode());
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\crypto\dsig\TransformService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
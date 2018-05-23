package javax.net.ssl;

import java.security.AlgorithmConstraints;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SSLParameters
{
  private String[] cipherSuites;
  private String[] protocols;
  private boolean wantClientAuth;
  private boolean needClientAuth;
  private String identificationAlgorithm;
  private AlgorithmConstraints algorithmConstraints;
  private Map<Integer, SNIServerName> sniNames = null;
  private Map<Integer, SNIMatcher> sniMatchers = null;
  private boolean preferLocalCipherSuites;
  
  public SSLParameters() {}
  
  public SSLParameters(String[] paramArrayOfString)
  {
    setCipherSuites(paramArrayOfString);
  }
  
  public SSLParameters(String[] paramArrayOfString1, String[] paramArrayOfString2)
  {
    setCipherSuites(paramArrayOfString1);
    setProtocols(paramArrayOfString2);
  }
  
  private static String[] clone(String[] paramArrayOfString)
  {
    return paramArrayOfString == null ? null : (String[])paramArrayOfString.clone();
  }
  
  public String[] getCipherSuites()
  {
    return clone(cipherSuites);
  }
  
  public void setCipherSuites(String[] paramArrayOfString)
  {
    cipherSuites = clone(paramArrayOfString);
  }
  
  public String[] getProtocols()
  {
    return clone(protocols);
  }
  
  public void setProtocols(String[] paramArrayOfString)
  {
    protocols = clone(paramArrayOfString);
  }
  
  public boolean getWantClientAuth()
  {
    return wantClientAuth;
  }
  
  public void setWantClientAuth(boolean paramBoolean)
  {
    wantClientAuth = paramBoolean;
    needClientAuth = false;
  }
  
  public boolean getNeedClientAuth()
  {
    return needClientAuth;
  }
  
  public void setNeedClientAuth(boolean paramBoolean)
  {
    wantClientAuth = false;
    needClientAuth = paramBoolean;
  }
  
  public AlgorithmConstraints getAlgorithmConstraints()
  {
    return algorithmConstraints;
  }
  
  public void setAlgorithmConstraints(AlgorithmConstraints paramAlgorithmConstraints)
  {
    algorithmConstraints = paramAlgorithmConstraints;
  }
  
  public String getEndpointIdentificationAlgorithm()
  {
    return identificationAlgorithm;
  }
  
  public void setEndpointIdentificationAlgorithm(String paramString)
  {
    identificationAlgorithm = paramString;
  }
  
  public final void setServerNames(List<SNIServerName> paramList)
  {
    if (paramList != null)
    {
      if (!paramList.isEmpty())
      {
        sniNames = new LinkedHashMap(paramList.size());
        Iterator localIterator = paramList.iterator();
        while (localIterator.hasNext())
        {
          SNIServerName localSNIServerName = (SNIServerName)localIterator.next();
          if (sniNames.put(Integer.valueOf(localSNIServerName.getType()), localSNIServerName) != null) {
            throw new IllegalArgumentException("Duplicated server name of type " + localSNIServerName.getType());
          }
        }
      }
      else
      {
        sniNames = Collections.emptyMap();
      }
    }
    else {
      sniNames = null;
    }
  }
  
  public final List<SNIServerName> getServerNames()
  {
    if (sniNames != null)
    {
      if (!sniNames.isEmpty()) {
        return Collections.unmodifiableList(new ArrayList(sniNames.values()));
      }
      return Collections.emptyList();
    }
    return null;
  }
  
  public final void setSNIMatchers(Collection<SNIMatcher> paramCollection)
  {
    if (paramCollection != null)
    {
      if (!paramCollection.isEmpty())
      {
        sniMatchers = new HashMap(paramCollection.size());
        Iterator localIterator = paramCollection.iterator();
        while (localIterator.hasNext())
        {
          SNIMatcher localSNIMatcher = (SNIMatcher)localIterator.next();
          if (sniMatchers.put(Integer.valueOf(localSNIMatcher.getType()), localSNIMatcher) != null) {
            throw new IllegalArgumentException("Duplicated server name of type " + localSNIMatcher.getType());
          }
        }
      }
      else
      {
        sniMatchers = Collections.emptyMap();
      }
    }
    else {
      sniMatchers = null;
    }
  }
  
  public final Collection<SNIMatcher> getSNIMatchers()
  {
    if (sniMatchers != null)
    {
      if (!sniMatchers.isEmpty()) {
        return Collections.unmodifiableList(new ArrayList(sniMatchers.values()));
      }
      return Collections.emptyList();
    }
    return null;
  }
  
  public final void setUseCipherSuitesOrder(boolean paramBoolean)
  {
    preferLocalCipherSuites = paramBoolean;
  }
  
  public final boolean getUseCipherSuitesOrder()
  {
    return preferLocalCipherSuites;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\net\ssl\SSLParameters.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
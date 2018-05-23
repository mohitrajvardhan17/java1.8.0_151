package com.sun.xml.internal.ws.policy.sourcemodel.wspolicy;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.xml.namespace.QName;

public enum NamespaceVersion
{
  v1_2("http://schemas.xmlsoap.org/ws/2004/09/policy", "wsp1_2", new XmlToken[] { XmlToken.Policy, XmlToken.ExactlyOne, XmlToken.All, XmlToken.PolicyReference, XmlToken.UsingPolicy, XmlToken.Name, XmlToken.Optional, XmlToken.Ignorable, XmlToken.PolicyUris, XmlToken.Uri, XmlToken.Digest, XmlToken.DigestAlgorithm }),  v1_5("http://www.w3.org/ns/ws-policy", "wsp", new XmlToken[] { XmlToken.Policy, XmlToken.ExactlyOne, XmlToken.All, XmlToken.PolicyReference, XmlToken.UsingPolicy, XmlToken.Name, XmlToken.Optional, XmlToken.Ignorable, XmlToken.PolicyUris, XmlToken.Uri, XmlToken.Digest, XmlToken.DigestAlgorithm });
  
  private final String nsUri;
  private final String defaultNsPrefix;
  private final Map<XmlToken, QName> tokenToQNameCache;
  
  public static NamespaceVersion resolveVersion(String paramString)
  {
    for (NamespaceVersion localNamespaceVersion : ) {
      if (localNamespaceVersion.toString().equalsIgnoreCase(paramString)) {
        return localNamespaceVersion;
      }
    }
    return null;
  }
  
  public static NamespaceVersion resolveVersion(QName paramQName)
  {
    return resolveVersion(paramQName.getNamespaceURI());
  }
  
  public static NamespaceVersion getLatestVersion()
  {
    return v1_5;
  }
  
  public static XmlToken resolveAsToken(QName paramQName)
  {
    NamespaceVersion localNamespaceVersion = resolveVersion(paramQName);
    if (localNamespaceVersion != null)
    {
      XmlToken localXmlToken = XmlToken.resolveToken(paramQName.getLocalPart());
      if (tokenToQNameCache.containsKey(localXmlToken)) {
        return localXmlToken;
      }
    }
    return XmlToken.UNKNOWN;
  }
  
  private NamespaceVersion(String paramString1, String paramString2, XmlToken... paramVarArgs)
  {
    nsUri = paramString1;
    defaultNsPrefix = paramString2;
    HashMap localHashMap = new HashMap();
    for (XmlToken localXmlToken : paramVarArgs) {
      localHashMap.put(localXmlToken, new QName(nsUri, localXmlToken.toString()));
    }
    tokenToQNameCache = Collections.unmodifiableMap(localHashMap);
  }
  
  public String getDefaultNamespacePrefix()
  {
    return defaultNsPrefix;
  }
  
  public QName asQName(XmlToken paramXmlToken)
    throws IllegalArgumentException
  {
    return (QName)tokenToQNameCache.get(paramXmlToken);
  }
  
  public String toString()
  {
    return nsUri;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\policy\sourcemodel\wspolicy\NamespaceVersion.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
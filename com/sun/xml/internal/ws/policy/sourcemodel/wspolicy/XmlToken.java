package com.sun.xml.internal.ws.policy.sourcemodel.wspolicy;

public enum XmlToken
{
  Policy("Policy", true),  ExactlyOne("ExactlyOne", true),  All("All", true),  PolicyReference("PolicyReference", true),  UsingPolicy("UsingPolicy", true),  Name("Name", false),  Optional("Optional", false),  Ignorable("Ignorable", false),  PolicyUris("PolicyURIs", false),  Uri("URI", false),  Digest("Digest", false),  DigestAlgorithm("DigestAlgorithm", false),  UNKNOWN("", true);
  
  private String tokenName;
  private boolean element;
  
  public static XmlToken resolveToken(String paramString)
  {
    for (XmlToken localXmlToken : ) {
      if (localXmlToken.toString().equals(paramString)) {
        return localXmlToken;
      }
    }
    return UNKNOWN;
  }
  
  private XmlToken(String paramString, boolean paramBoolean)
  {
    tokenName = paramString;
    element = paramBoolean;
  }
  
  public boolean isElement()
  {
    return element;
  }
  
  public String toString()
  {
    return tokenName;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\policy\sourcemodel\wspolicy\XmlToken.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
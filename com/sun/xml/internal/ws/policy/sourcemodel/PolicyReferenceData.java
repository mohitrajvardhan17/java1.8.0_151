package com.sun.xml.internal.ws.policy.sourcemodel;

import com.sun.xml.internal.ws.policy.privateutil.LocalizationMessages;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils.Text;
import java.net.URI;
import java.net.URISyntaxException;

final class PolicyReferenceData
{
  private static final PolicyLogger LOGGER = PolicyLogger.getLogger(PolicyReferenceData.class);
  private static final URI DEFAULT_DIGEST_ALGORITHM_URI;
  private static final URISyntaxException CLASS_INITIALIZATION_EXCEPTION;
  private final URI referencedModelUri;
  private final String digest;
  private final URI digestAlgorithmUri;
  
  public PolicyReferenceData(URI paramURI)
  {
    referencedModelUri = paramURI;
    digest = null;
    digestAlgorithmUri = null;
  }
  
  public PolicyReferenceData(URI paramURI1, String paramString, URI paramURI2)
  {
    if (CLASS_INITIALIZATION_EXCEPTION != null) {
      throw ((IllegalStateException)LOGGER.logSevereException(new IllegalStateException(LocalizationMessages.WSP_0015_UNABLE_TO_INSTANTIATE_DIGEST_ALG_URI_FIELD(), CLASS_INITIALIZATION_EXCEPTION)));
    }
    if ((paramURI2 != null) && (paramString == null)) {
      throw ((IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0072_DIGEST_MUST_NOT_BE_NULL_WHEN_ALG_DEFINED())));
    }
    referencedModelUri = paramURI1;
    if (paramString == null)
    {
      digest = null;
      digestAlgorithmUri = null;
    }
    else
    {
      digest = paramString;
      if (paramURI2 == null) {
        digestAlgorithmUri = DEFAULT_DIGEST_ALGORITHM_URI;
      } else {
        digestAlgorithmUri = paramURI2;
      }
    }
  }
  
  public URI getReferencedModelUri()
  {
    return referencedModelUri;
  }
  
  public String getDigest()
  {
    return digest;
  }
  
  public URI getDigestAlgorithmUri()
  {
    return digestAlgorithmUri;
  }
  
  public String toString()
  {
    return toString(0, new StringBuffer()).toString();
  }
  
  public StringBuffer toString(int paramInt, StringBuffer paramStringBuffer)
  {
    String str1 = PolicyUtils.Text.createIndent(paramInt);
    String str2 = PolicyUtils.Text.createIndent(paramInt + 1);
    paramStringBuffer.append(str1).append("reference data {").append(PolicyUtils.Text.NEW_LINE);
    paramStringBuffer.append(str2).append("referenced policy model URI = '").append(referencedModelUri).append('\'').append(PolicyUtils.Text.NEW_LINE);
    if (digest == null)
    {
      paramStringBuffer.append(str2).append("no digest specified").append(PolicyUtils.Text.NEW_LINE);
    }
    else
    {
      paramStringBuffer.append(str2).append("digest algorith URI = '").append(digestAlgorithmUri).append('\'').append(PolicyUtils.Text.NEW_LINE);
      paramStringBuffer.append(str2).append("digest = '").append(digest).append('\'').append(PolicyUtils.Text.NEW_LINE);
    }
    paramStringBuffer.append(str1).append('}');
    return paramStringBuffer;
  }
  
  static
  {
    Object localObject1 = null;
    URI localURI = null;
    try
    {
      localURI = new URI("http://schemas.xmlsoap.org/ws/2004/09/policy/Sha1Exc");
    }
    catch (URISyntaxException localURISyntaxException)
    {
      localObject1 = localURISyntaxException;
    }
    finally
    {
      DEFAULT_DIGEST_ALGORITHM_URI = localURI;
      CLASS_INITIALIZATION_EXCEPTION = (URISyntaxException)localObject1;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\policy\sourcemodel\PolicyReferenceData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
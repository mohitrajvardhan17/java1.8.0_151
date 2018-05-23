package com.sun.xml.internal.ws.api.policy;

import com.sun.xml.internal.ws.addressing.policy.AddressingPrefixMapper;
import com.sun.xml.internal.ws.config.management.policy.ManagementPrefixMapper;
import com.sun.xml.internal.ws.encoding.policy.EncodingPrefixMapper;
import com.sun.xml.internal.ws.policy.sourcemodel.PolicySourceModel;
import com.sun.xml.internal.ws.policy.sourcemodel.wspolicy.NamespaceVersion;
import com.sun.xml.internal.ws.policy.spi.PrefixMapper;
import java.util.Arrays;

public class SourceModel
  extends PolicySourceModel
{
  private static final PrefixMapper[] JAXWS_PREFIX_MAPPERS = { new AddressingPrefixMapper(), new EncodingPrefixMapper(), new ManagementPrefixMapper() };
  
  private SourceModel(NamespaceVersion paramNamespaceVersion)
  {
    this(paramNamespaceVersion, null, null);
  }
  
  private SourceModel(NamespaceVersion paramNamespaceVersion, String paramString1, String paramString2)
  {
    super(paramNamespaceVersion, paramString1, paramString2, Arrays.asList(JAXWS_PREFIX_MAPPERS));
  }
  
  public static PolicySourceModel createSourceModel(NamespaceVersion paramNamespaceVersion)
  {
    return new SourceModel(paramNamespaceVersion);
  }
  
  public static PolicySourceModel createSourceModel(NamespaceVersion paramNamespaceVersion, String paramString1, String paramString2)
  {
    return new SourceModel(paramNamespaceVersion, paramString1, paramString2);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\policy\SourceModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
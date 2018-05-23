package com.sun.xml.internal.ws.api.policy;

import com.sun.xml.internal.ws.policy.sourcemodel.PolicySourceModel;
import com.sun.xml.internal.ws.policy.sourcemodel.XmlPolicyModelUnmarshaller;
import com.sun.xml.internal.ws.policy.sourcemodel.wspolicy.NamespaceVersion;

public class ModelUnmarshaller
  extends XmlPolicyModelUnmarshaller
{
  private static final ModelUnmarshaller INSTANCE = new ModelUnmarshaller();
  
  private ModelUnmarshaller() {}
  
  public static ModelUnmarshaller getUnmarshaller()
  {
    return INSTANCE;
  }
  
  protected PolicySourceModel createSourceModel(NamespaceVersion paramNamespaceVersion, String paramString1, String paramString2)
  {
    return SourceModel.createSourceModel(paramNamespaceVersion, paramString1, paramString2);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\policy\ModelUnmarshaller.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package com.sun.org.apache.xerces.internal.jaxp.validation;

import java.util.HashMap;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;
import javax.xml.validation.ValidatorHandler;

abstract class AbstractXMLSchema
  extends Schema
  implements XSGrammarPoolContainer
{
  private final HashMap fFeatures = new HashMap();
  private final HashMap fProperties = new HashMap();
  
  public AbstractXMLSchema() {}
  
  public final Validator newValidator()
  {
    return new ValidatorImpl(this);
  }
  
  public final ValidatorHandler newValidatorHandler()
  {
    return new ValidatorHandlerImpl(this);
  }
  
  public final Boolean getFeature(String paramString)
  {
    return (Boolean)fFeatures.get(paramString);
  }
  
  public final void setFeature(String paramString, boolean paramBoolean)
  {
    fFeatures.put(paramString, paramBoolean ? Boolean.TRUE : Boolean.FALSE);
  }
  
  public final Object getProperty(String paramString)
  {
    return fProperties.get(paramString);
  }
  
  public final void setProperty(String paramString, Object paramObject)
  {
    fProperties.put(paramString, paramObject);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\jaxp\validation\AbstractXMLSchema.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
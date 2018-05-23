package com.sun.org.apache.xerces.internal.impl.dtd;

import com.sun.org.apache.xerces.internal.impl.dv.DTDDVFactory;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;

public class XML11DTDValidator
  extends XMLDTDValidator
{
  protected static final String DTD_VALIDATOR_PROPERTY = "http://apache.org/xml/properties/internal/validator/dtd";
  
  public XML11DTDValidator() {}
  
  public void reset(XMLComponentManager paramXMLComponentManager)
  {
    XMLDTDValidator localXMLDTDValidator = null;
    if (((localXMLDTDValidator = (XMLDTDValidator)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/internal/validator/dtd")) != null) && (localXMLDTDValidator != this)) {
      fGrammarBucket = localXMLDTDValidator.getGrammarBucket();
    }
    super.reset(paramXMLComponentManager);
  }
  
  protected void init()
  {
    if ((fValidation) || (fDynamicValidation))
    {
      super.init();
      try
      {
        fValID = fDatatypeValidatorFactory.getBuiltInDV("XML11ID");
        fValIDRef = fDatatypeValidatorFactory.getBuiltInDV("XML11IDREF");
        fValIDRefs = fDatatypeValidatorFactory.getBuiltInDV("XML11IDREFS");
        fValNMTOKEN = fDatatypeValidatorFactory.getBuiltInDV("XML11NMTOKEN");
        fValNMTOKENS = fDatatypeValidatorFactory.getBuiltInDV("XML11NMTOKENS");
      }
      catch (Exception localException)
      {
        localException.printStackTrace(System.err);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\dtd\XML11DTDValidator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
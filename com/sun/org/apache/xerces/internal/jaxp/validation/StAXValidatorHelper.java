package com.sun.org.apache.xerces.internal.jaxp.validation;

import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager.Limit;
import java.io.IOException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stax.StAXResult;
import org.xml.sax.SAXException;

public final class StAXValidatorHelper
  implements ValidatorHelper
{
  private static final String DEFAULT_TRANSFORMER_IMPL = "com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl";
  private XMLSchemaValidatorComponentManager fComponentManager;
  private Transformer identityTransformer1 = null;
  private TransformerHandler identityTransformer2 = null;
  private ValidatorHandlerImpl handler = null;
  
  public StAXValidatorHelper(XMLSchemaValidatorComponentManager paramXMLSchemaValidatorComponentManager)
  {
    fComponentManager = paramXMLSchemaValidatorComponentManager;
  }
  
  public void validate(Source paramSource, Result paramResult)
    throws SAXException, IOException
  {
    if ((paramResult == null) || ((paramResult instanceof StAXResult)))
    {
      if (identityTransformer1 == null) {
        try
        {
          SAXTransformerFactory localSAXTransformerFactory = fComponentManager.getFeature("http://www.oracle.com/feature/use-service-mechanism") ? (SAXTransformerFactory)SAXTransformerFactory.newInstance() : (SAXTransformerFactory)TransformerFactory.newInstance("com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl", StAXValidatorHelper.class.getClassLoader());
          XMLSecurityManager localXMLSecurityManager = (XMLSecurityManager)fComponentManager.getProperty("http://apache.org/xml/properties/security-manager");
          if (localXMLSecurityManager != null)
          {
            for (XMLSecurityManager.Limit localLimit : XMLSecurityManager.Limit.values()) {
              if (localXMLSecurityManager.isSet(localLimit.ordinal())) {
                localSAXTransformerFactory.setAttribute(localLimit.apiProperty(), localXMLSecurityManager.getLimitValueAsString(localLimit));
              }
            }
            if (localXMLSecurityManager.printEntityCountInfo()) {
              localSAXTransformerFactory.setAttribute("http://www.oracle.com/xml/jaxp/properties/getEntityCountInfo", "yes");
            }
          }
          identityTransformer1 = localSAXTransformerFactory.newTransformer();
          identityTransformer2 = localSAXTransformerFactory.newTransformerHandler();
        }
        catch (TransformerConfigurationException localTransformerConfigurationException)
        {
          throw new TransformerFactoryConfigurationError(localTransformerConfigurationException);
        }
      }
      handler = new ValidatorHandlerImpl(fComponentManager);
      if (paramResult != null)
      {
        handler.setContentHandler(identityTransformer2);
        identityTransformer2.setResult(paramResult);
      }
      try
      {
        identityTransformer1.transform(paramSource, new SAXResult(handler));
      }
      catch (TransformerException localTransformerException)
      {
        if ((localTransformerException.getException() instanceof SAXException)) {
          throw ((SAXException)localTransformerException.getException());
        }
        throw new SAXException(localTransformerException);
      }
      finally
      {
        handler.setContentHandler(null);
      }
      return;
    }
    throw new IllegalArgumentException(JAXPValidationMessageFormatter.formatMessage(fComponentManager.getLocale(), "SourceResultMismatch", new Object[] { paramSource.getClass().getName(), paramResult.getClass().getName() }));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\jaxp\validation\StAXValidatorHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
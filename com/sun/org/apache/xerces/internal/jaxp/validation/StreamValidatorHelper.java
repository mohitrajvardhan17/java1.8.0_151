package com.sun.org.apache.xerces.internal.jaxp.validation;

import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.impl.msg.XMLMessageFormatter;
import com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaValidator;
import com.sun.org.apache.xerces.internal.parsers.XML11Configuration;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParseException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParserConfiguration;
import java.io.IOException;
import java.lang.ref.SoftReference;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.xml.sax.SAXException;

final class StreamValidatorHelper
  implements ValidatorHelper
{
  private static final String PARSER_SETTINGS = "http://apache.org/xml/features/internal/parser-settings";
  private static final String ENTITY_RESOLVER = "http://apache.org/xml/properties/internal/entity-resolver";
  private static final String ERROR_HANDLER = "http://apache.org/xml/properties/internal/error-handler";
  private static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
  private static final String SCHEMA_VALIDATOR = "http://apache.org/xml/properties/internal/validator/schema";
  private static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
  private static final String VALIDATION_MANAGER = "http://apache.org/xml/properties/internal/validation-manager";
  private static final String DEFAULT_TRANSFORMER_IMPL = "com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl";
  private static final String SECURITY_MANAGER = "http://apache.org/xml/properties/security-manager";
  private SoftReference fConfiguration = new SoftReference(null);
  private XMLSchemaValidator fSchemaValidator;
  private XMLSchemaValidatorComponentManager fComponentManager;
  private ValidatorHandlerImpl handler = null;
  
  public StreamValidatorHelper(XMLSchemaValidatorComponentManager paramXMLSchemaValidatorComponentManager)
  {
    fComponentManager = paramXMLSchemaValidatorComponentManager;
    fSchemaValidator = ((XMLSchemaValidator)fComponentManager.getProperty("http://apache.org/xml/properties/internal/validator/schema"));
  }
  
  public void validate(Source paramSource, Result paramResult)
    throws SAXException, IOException
  {
    if ((paramResult == null) || ((paramResult instanceof StreamResult)))
    {
      StreamSource localStreamSource = (StreamSource)paramSource;
      if (paramResult != null)
      {
        TransformerHandler localTransformerHandler;
        try
        {
          SAXTransformerFactory localSAXTransformerFactory = fComponentManager.getFeature("http://www.oracle.com/feature/use-service-mechanism") ? (SAXTransformerFactory)SAXTransformerFactory.newInstance() : (SAXTransformerFactory)TransformerFactory.newInstance("com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl", StreamValidatorHelper.class.getClassLoader());
          localTransformerHandler = localSAXTransformerFactory.newTransformerHandler();
        }
        catch (TransformerConfigurationException localTransformerConfigurationException)
        {
          throw new TransformerFactoryConfigurationError(localTransformerConfigurationException);
        }
        handler = new ValidatorHandlerImpl(fComponentManager);
        handler.setContentHandler(localTransformerHandler);
        localTransformerHandler.setResult(paramResult);
      }
      XMLInputSource localXMLInputSource = new XMLInputSource(localStreamSource.getPublicId(), localStreamSource.getSystemId(), null);
      localXMLInputSource.setByteStream(localStreamSource.getInputStream());
      localXMLInputSource.setCharacterStream(localStreamSource.getReader());
      XMLParserConfiguration localXMLParserConfiguration = (XMLParserConfiguration)fConfiguration.get();
      if (localXMLParserConfiguration == null)
      {
        localXMLParserConfiguration = initialize();
      }
      else if (fComponentManager.getFeature("http://apache.org/xml/features/internal/parser-settings"))
      {
        localXMLParserConfiguration.setProperty("http://apache.org/xml/properties/internal/entity-resolver", fComponentManager.getProperty("http://apache.org/xml/properties/internal/entity-resolver"));
        localXMLParserConfiguration.setProperty("http://apache.org/xml/properties/internal/error-handler", fComponentManager.getProperty("http://apache.org/xml/properties/internal/error-handler"));
      }
      fComponentManager.reset();
      fSchemaValidator.setDocumentHandler(handler);
      try
      {
        localXMLParserConfiguration.parse(localXMLInputSource);
      }
      catch (XMLParseException localXMLParseException)
      {
        throw Util.toSAXParseException(localXMLParseException);
      }
      catch (XNIException localXNIException)
      {
        throw Util.toSAXException(localXNIException);
      }
      return;
    }
    throw new IllegalArgumentException(JAXPValidationMessageFormatter.formatMessage(fComponentManager.getLocale(), "SourceResultMismatch", new Object[] { paramSource.getClass().getName(), paramResult.getClass().getName() }));
  }
  
  private XMLParserConfiguration initialize()
  {
    XML11Configuration localXML11Configuration = new XML11Configuration();
    if (fComponentManager.getFeature("http://javax.xml.XMLConstants/feature/secure-processing")) {
      localXML11Configuration.setProperty("http://apache.org/xml/properties/security-manager", new XMLSecurityManager());
    }
    localXML11Configuration.setProperty("http://apache.org/xml/properties/internal/entity-resolver", fComponentManager.getProperty("http://apache.org/xml/properties/internal/entity-resolver"));
    localXML11Configuration.setProperty("http://apache.org/xml/properties/internal/error-handler", fComponentManager.getProperty("http://apache.org/xml/properties/internal/error-handler"));
    XMLErrorReporter localXMLErrorReporter = (XMLErrorReporter)fComponentManager.getProperty("http://apache.org/xml/properties/internal/error-reporter");
    localXML11Configuration.setProperty("http://apache.org/xml/properties/internal/error-reporter", localXMLErrorReporter);
    if (localXMLErrorReporter.getMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210") == null)
    {
      XMLMessageFormatter localXMLMessageFormatter = new XMLMessageFormatter();
      localXMLErrorReporter.putMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210", localXMLMessageFormatter);
      localXMLErrorReporter.putMessageFormatter("http://www.w3.org/TR/1999/REC-xml-names-19990114", localXMLMessageFormatter);
    }
    localXML11Configuration.setProperty("http://apache.org/xml/properties/internal/symbol-table", fComponentManager.getProperty("http://apache.org/xml/properties/internal/symbol-table"));
    localXML11Configuration.setProperty("http://apache.org/xml/properties/internal/validation-manager", fComponentManager.getProperty("http://apache.org/xml/properties/internal/validation-manager"));
    localXML11Configuration.setDocumentHandler(fSchemaValidator);
    localXML11Configuration.setDTDHandler(null);
    localXML11Configuration.setDTDContentModelHandler(null);
    localXML11Configuration.setProperty("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager", fComponentManager.getProperty("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager"));
    localXML11Configuration.setProperty("http://apache.org/xml/properties/security-manager", fComponentManager.getProperty("http://apache.org/xml/properties/security-manager"));
    fConfiguration = new SoftReference(localXML11Configuration);
    return localXML11Configuration;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\jaxp\validation\StreamValidatorHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
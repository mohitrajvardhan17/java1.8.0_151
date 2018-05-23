package com.sun.org.apache.xerces.internal.parsers;

import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.impl.XMLNamespaceBinder;
import com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaValidator;
import com.sun.org.apache.xerces.internal.impl.xs.XSMessageFormatter;
import com.sun.org.apache.xerces.internal.util.FeatureState;
import com.sun.org.apache.xerces.internal.util.PropertyState;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import java.util.Map;

public class StandardParserConfiguration
  extends DTDConfiguration
{
  protected static final String NORMALIZE_DATA = "http://apache.org/xml/features/validation/schema/normalized-value";
  protected static final String SCHEMA_ELEMENT_DEFAULT = "http://apache.org/xml/features/validation/schema/element-default";
  protected static final String SCHEMA_AUGMENT_PSVI = "http://apache.org/xml/features/validation/schema/augment-psvi";
  protected static final String XMLSCHEMA_VALIDATION = "http://apache.org/xml/features/validation/schema";
  protected static final String XMLSCHEMA_FULL_CHECKING = "http://apache.org/xml/features/validation/schema-full-checking";
  protected static final String GENERATE_SYNTHETIC_ANNOTATIONS = "http://apache.org/xml/features/generate-synthetic-annotations";
  protected static final String VALIDATE_ANNOTATIONS = "http://apache.org/xml/features/validate-annotations";
  protected static final String HONOUR_ALL_SCHEMALOCATIONS = "http://apache.org/xml/features/honour-all-schemaLocations";
  protected static final String NAMESPACE_GROWTH = "http://apache.org/xml/features/namespace-growth";
  protected static final String TOLERATE_DUPLICATES = "http://apache.org/xml/features/internal/tolerate-duplicates";
  protected static final String SCHEMA_VALIDATOR = "http://apache.org/xml/properties/internal/validator/schema";
  protected static final String SCHEMA_LOCATION = "http://apache.org/xml/properties/schema/external-schemaLocation";
  protected static final String SCHEMA_NONS_LOCATION = "http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation";
  protected static final String SCHEMA_DV_FACTORY = "http://apache.org/xml/properties/internal/validation/schema/dv-factory";
  protected XMLSchemaValidator fSchemaValidator;
  
  public StandardParserConfiguration()
  {
    this(null, null, null);
  }
  
  public StandardParserConfiguration(SymbolTable paramSymbolTable)
  {
    this(paramSymbolTable, null, null);
  }
  
  public StandardParserConfiguration(SymbolTable paramSymbolTable, XMLGrammarPool paramXMLGrammarPool)
  {
    this(paramSymbolTable, paramXMLGrammarPool, null);
  }
  
  public StandardParserConfiguration(SymbolTable paramSymbolTable, XMLGrammarPool paramXMLGrammarPool, XMLComponentManager paramXMLComponentManager)
  {
    super(paramSymbolTable, paramXMLGrammarPool, paramXMLComponentManager);
    String[] arrayOfString1 = { "http://apache.org/xml/features/validation/schema/normalized-value", "http://apache.org/xml/features/validation/schema/element-default", "http://apache.org/xml/features/validation/schema/augment-psvi", "http://apache.org/xml/features/generate-synthetic-annotations", "http://apache.org/xml/features/validate-annotations", "http://apache.org/xml/features/honour-all-schemaLocations", "http://apache.org/xml/features/namespace-growth", "http://apache.org/xml/features/internal/tolerate-duplicates", "http://apache.org/xml/features/validation/schema", "http://apache.org/xml/features/validation/schema-full-checking" };
    addRecognizedFeatures(arrayOfString1);
    setFeature("http://apache.org/xml/features/validation/schema/element-default", true);
    setFeature("http://apache.org/xml/features/validation/schema/normalized-value", true);
    setFeature("http://apache.org/xml/features/validation/schema/augment-psvi", true);
    setFeature("http://apache.org/xml/features/generate-synthetic-annotations", false);
    setFeature("http://apache.org/xml/features/validate-annotations", false);
    setFeature("http://apache.org/xml/features/honour-all-schemaLocations", false);
    setFeature("http://apache.org/xml/features/namespace-growth", false);
    setFeature("http://apache.org/xml/features/internal/tolerate-duplicates", false);
    String[] arrayOfString2 = { "http://apache.org/xml/properties/schema/external-schemaLocation", "http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation", "http://apache.org/xml/properties/internal/validation/schema/dv-factory" };
    addRecognizedProperties(arrayOfString2);
  }
  
  protected void configurePipeline()
  {
    super.configurePipeline();
    if (getFeature("http://apache.org/xml/features/validation/schema"))
    {
      if (fSchemaValidator == null)
      {
        fSchemaValidator = new XMLSchemaValidator();
        fProperties.put("http://apache.org/xml/properties/internal/validator/schema", fSchemaValidator);
        addComponent(fSchemaValidator);
        if (fErrorReporter.getMessageFormatter("http://www.w3.org/TR/xml-schema-1") == null)
        {
          XSMessageFormatter localXSMessageFormatter = new XSMessageFormatter();
          fErrorReporter.putMessageFormatter("http://www.w3.org/TR/xml-schema-1", localXSMessageFormatter);
        }
      }
      fLastComponent = fSchemaValidator;
      fNamespaceBinder.setDocumentHandler(fSchemaValidator);
      fSchemaValidator.setDocumentHandler(fDocumentHandler);
      fSchemaValidator.setDocumentSource(fNamespaceBinder);
    }
  }
  
  protected FeatureState checkFeature(String paramString)
    throws XMLConfigurationException
  {
    if (paramString.startsWith("http://apache.org/xml/features/"))
    {
      int i = paramString.length() - "http://apache.org/xml/features/".length();
      if ((i == "validation/schema".length()) && (paramString.endsWith("validation/schema"))) {
        return FeatureState.RECOGNIZED;
      }
      if ((i == "validation/schema-full-checking".length()) && (paramString.endsWith("validation/schema-full-checking"))) {
        return FeatureState.RECOGNIZED;
      }
      if ((i == "validation/schema/normalized-value".length()) && (paramString.endsWith("validation/schema/normalized-value"))) {
        return FeatureState.RECOGNIZED;
      }
      if ((i == "validation/schema/element-default".length()) && (paramString.endsWith("validation/schema/element-default"))) {
        return FeatureState.RECOGNIZED;
      }
    }
    return super.checkFeature(paramString);
  }
  
  protected PropertyState checkProperty(String paramString)
    throws XMLConfigurationException
  {
    int i;
    if (paramString.startsWith("http://apache.org/xml/properties/"))
    {
      i = paramString.length() - "http://apache.org/xml/properties/".length();
      if ((i == "schema/external-schemaLocation".length()) && (paramString.endsWith("schema/external-schemaLocation"))) {
        return PropertyState.RECOGNIZED;
      }
      if ((i == "schema/external-noNamespaceSchemaLocation".length()) && (paramString.endsWith("schema/external-noNamespaceSchemaLocation"))) {
        return PropertyState.RECOGNIZED;
      }
    }
    if (paramString.startsWith("http://java.sun.com/xml/jaxp/properties/"))
    {
      i = paramString.length() - "http://java.sun.com/xml/jaxp/properties/".length();
      if ((i == "schemaSource".length()) && (paramString.endsWith("schemaSource"))) {
        return PropertyState.RECOGNIZED;
      }
    }
    return super.checkProperty(paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\parsers\StandardParserConfiguration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
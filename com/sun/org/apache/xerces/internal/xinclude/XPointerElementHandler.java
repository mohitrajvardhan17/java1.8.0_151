package com.sun.org.apache.xerces.internal.xinclude;

import com.sun.org.apache.xerces.internal.impl.Constants;
import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.impl.dtd.DTDGrammar;
import com.sun.org.apache.xerces.internal.util.ParserConfigurationSettings;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarDescription;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;
import java.util.Enumeration;
import java.util.Stack;
import java.util.StringTokenizer;

public class XPointerElementHandler
  implements XPointerSchema
{
  protected static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
  protected static final String GRAMMAR_POOL = "http://apache.org/xml/properties/internal/grammar-pool";
  protected static final String ENTITY_RESOLVER = "http://apache.org/xml/properties/internal/entity-resolver";
  protected static final String XPOINTER_SCHEMA = "http://apache.org/xml/properties/xpointer-schema";
  private static final String[] RECOGNIZED_FEATURES = new String[0];
  private static final Boolean[] FEATURE_DEFAULTS = new Boolean[0];
  private static final String[] RECOGNIZED_PROPERTIES = { "http://apache.org/xml/properties/internal/error-reporter", "http://apache.org/xml/properties/internal/grammar-pool", "http://apache.org/xml/properties/internal/entity-resolver", "http://apache.org/xml/properties/xpointer-schema" };
  private static final Object[] PROPERTY_DEFAULTS = { null, null, null, null };
  protected XMLDocumentHandler fDocumentHandler;
  protected XMLDocumentSource fDocumentSource;
  protected XIncludeHandler fParentXIncludeHandler;
  protected XMLLocator fDocLocation;
  protected XIncludeNamespaceSupport fNamespaceContext;
  protected XMLErrorReporter fErrorReporter;
  protected XMLGrammarPool fGrammarPool;
  protected XMLGrammarDescription fGrammarDesc;
  protected DTDGrammar fDTDGrammar;
  protected XMLEntityResolver fEntityResolver;
  protected ParserConfigurationSettings fSettings;
  protected StringBuffer fPointer;
  private int elemCount = 0;
  private int fDepth = 0;
  private int fRootDepth = 0;
  private static final int INITIAL_SIZE = 8;
  private boolean[] fSawInclude = new boolean[8];
  private boolean[] fSawFallback = new boolean[8];
  private int[] fState = new int[8];
  QName foundElement = null;
  boolean skip = false;
  String fSchemaName;
  String fSchemaPointer;
  boolean fSubResourceIdentified;
  Stack fPointerToken = new Stack();
  int fCurrentTokenint = 0;
  String fCurrentTokenString = null;
  int fCurrentTokenType = 0;
  Stack ftempCurrentElement = new Stack();
  int fElementCount = 0;
  int fCurrentToken;
  boolean includeElement;
  
  public XPointerElementHandler()
  {
    fSawFallback[fDepth] = false;
    fSawInclude[fDepth] = false;
    fSchemaName = "element";
  }
  
  public void reset()
  {
    elemCount = 0;
    fPointerToken = null;
    fCurrentTokenint = 0;
    fCurrentTokenString = null;
    fCurrentTokenType = 0;
    fElementCount = 0;
    fCurrentToken = 0;
    includeElement = false;
    foundElement = null;
    skip = false;
    fSubResourceIdentified = false;
  }
  
  public void reset(XMLComponentManager paramXMLComponentManager)
    throws XNIException
  {
    fNamespaceContext = null;
    elemCount = 0;
    fDepth = 0;
    fRootDepth = 0;
    fPointerToken = null;
    fCurrentTokenint = 0;
    fCurrentTokenString = null;
    fCurrentTokenType = 0;
    foundElement = null;
    includeElement = false;
    skip = false;
    fSubResourceIdentified = false;
    try
    {
      setErrorReporter((XMLErrorReporter)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/internal/error-reporter"));
    }
    catch (XMLConfigurationException localXMLConfigurationException1)
    {
      fErrorReporter = null;
    }
    try
    {
      fGrammarPool = ((XMLGrammarPool)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/internal/grammar-pool"));
    }
    catch (XMLConfigurationException localXMLConfigurationException2)
    {
      fGrammarPool = null;
    }
    try
    {
      fEntityResolver = ((XMLEntityResolver)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/internal/entity-resolver"));
    }
    catch (XMLConfigurationException localXMLConfigurationException3)
    {
      fEntityResolver = null;
    }
    fSettings = new ParserConfigurationSettings();
    Enumeration localEnumeration = Constants.getXercesFeatures();
    while (localEnumeration.hasMoreElements())
    {
      String str = (String)localEnumeration.nextElement();
      fSettings.addRecognizedFeatures(new String[] { str });
      try
      {
        fSettings.setFeature(str, paramXMLComponentManager.getFeature(str));
      }
      catch (XMLConfigurationException localXMLConfigurationException4) {}
    }
  }
  
  public String[] getRecognizedFeatures()
  {
    return RECOGNIZED_FEATURES;
  }
  
  public void setFeature(String paramString, boolean paramBoolean)
    throws XMLConfigurationException
  {
    if (fSettings != null) {
      fSettings.setFeature(paramString, paramBoolean);
    }
  }
  
  public String[] getRecognizedProperties()
  {
    return RECOGNIZED_PROPERTIES;
  }
  
  public void setProperty(String paramString, Object paramObject)
    throws XMLConfigurationException
  {
    if (paramString.equals("http://apache.org/xml/properties/internal/error-reporter")) {
      setErrorReporter((XMLErrorReporter)paramObject);
    }
    if (paramString.equals("http://apache.org/xml/properties/internal/grammar-pool")) {
      fGrammarPool = ((XMLGrammarPool)paramObject);
    }
    if (paramString.equals("http://apache.org/xml/properties/internal/entity-resolver")) {
      fEntityResolver = ((XMLEntityResolver)paramObject);
    }
  }
  
  public Boolean getFeatureDefault(String paramString)
  {
    for (int i = 0; i < RECOGNIZED_FEATURES.length; i++) {
      if (RECOGNIZED_FEATURES[i].equals(paramString)) {
        return FEATURE_DEFAULTS[i];
      }
    }
    return null;
  }
  
  public Object getPropertyDefault(String paramString)
  {
    for (int i = 0; i < RECOGNIZED_PROPERTIES.length; i++) {
      if (RECOGNIZED_PROPERTIES[i].equals(paramString)) {
        return PROPERTY_DEFAULTS[i];
      }
    }
    return null;
  }
  
  private void setErrorReporter(XMLErrorReporter paramXMLErrorReporter)
  {
    fErrorReporter = paramXMLErrorReporter;
    if (fErrorReporter != null) {
      fErrorReporter.putMessageFormatter("http://www.w3.org/TR/xinclude", new XIncludeMessageFormatter());
    }
  }
  
  public void setDocumentHandler(XMLDocumentHandler paramXMLDocumentHandler)
  {
    fDocumentHandler = paramXMLDocumentHandler;
  }
  
  public XMLDocumentHandler getDocumentHandler()
  {
    return fDocumentHandler;
  }
  
  public void setXPointerSchemaName(String paramString)
  {
    fSchemaName = paramString;
  }
  
  public String getXpointerSchemaName()
  {
    return fSchemaName;
  }
  
  public void setParent(Object paramObject)
  {
    fParentXIncludeHandler = ((XIncludeHandler)paramObject);
  }
  
  public Object getParent()
  {
    return fParentXIncludeHandler;
  }
  
  public void setXPointerSchemaPointer(String paramString)
  {
    fSchemaPointer = paramString;
  }
  
  public String getXPointerSchemaPointer()
  {
    return fSchemaPointer;
  }
  
  public boolean isSubResourceIndentified()
  {
    return fSubResourceIdentified;
  }
  
  public void getTokens()
  {
    fSchemaPointer = fSchemaPointer.substring(fSchemaPointer.indexOf("(") + 1, fSchemaPointer.length());
    StringTokenizer localStringTokenizer = new StringTokenizer(fSchemaPointer, "/");
    Integer localInteger = null;
    Stack localStack = new Stack();
    if (fPointerToken == null) {
      fPointerToken = new Stack();
    }
    while (localStringTokenizer.hasMoreTokens())
    {
      String str = localStringTokenizer.nextToken();
      try
      {
        localInteger = Integer.valueOf(str);
        localStack.push(localInteger);
      }
      catch (NumberFormatException localNumberFormatException)
      {
        localStack.push(str);
      }
    }
    while (!localStack.empty()) {
      fPointerToken.push(localStack.pop());
    }
  }
  
  public boolean hasMoreToken()
  {
    return !fPointerToken.isEmpty();
  }
  
  public boolean getNextToken()
  {
    if (!fPointerToken.isEmpty())
    {
      Object localObject = fPointerToken.pop();
      if ((localObject instanceof Integer))
      {
        fCurrentTokenint = ((Integer)localObject).intValue();
        fCurrentTokenType = 1;
      }
      else
      {
        fCurrentTokenString = ((String)localObject).toString();
        fCurrentTokenType = 2;
      }
      return true;
    }
    return false;
  }
  
  private boolean isIdAttribute(XMLAttributes paramXMLAttributes, Augmentations paramAugmentations, int paramInt)
  {
    Object localObject = paramAugmentations.getItem("ID_ATTRIBUTE");
    if ((localObject instanceof Boolean)) {
      return ((Boolean)localObject).booleanValue();
    }
    return "ID".equals(paramXMLAttributes.getType(paramInt));
  }
  
  public boolean checkStringToken(QName paramQName, XMLAttributes paramXMLAttributes)
  {
    Object localObject1 = null;
    Object localObject2 = null;
    Object localObject3 = null;
    QName localQName = new QName();
    String str1 = null;
    String str2 = null;
    int i = paramXMLAttributes.getLength();
    for (int j = 0; j < i; j++)
    {
      Augmentations localAugmentations = paramXMLAttributes.getAugmentations(j);
      paramXMLAttributes.getName(j, localQName);
      str1 = paramXMLAttributes.getType(j);
      str2 = paramXMLAttributes.getValue(j);
      if ((str1 != null) && (str2 != null) && (isIdAttribute(paramXMLAttributes, localAugmentations, j)) && (str2.equals(fCurrentTokenString)))
      {
        if (hasMoreToken())
        {
          fCurrentTokenType = 0;
          fCurrentTokenString = null;
          return true;
        }
        foundElement = paramQName;
        includeElement = true;
        fCurrentTokenType = 0;
        fCurrentTokenString = null;
        fSubResourceIdentified = true;
        return true;
      }
    }
    return false;
  }
  
  public boolean checkIntegerToken(QName paramQName)
  {
    if (!skip)
    {
      fElementCount += 1;
      if (fCurrentTokenint == fElementCount)
      {
        if (hasMoreToken())
        {
          fElementCount = 0;
          fCurrentTokenType = 0;
          return true;
        }
        foundElement = paramQName;
        includeElement = true;
        fCurrentTokenType = 0;
        fElementCount = 0;
        fSubResourceIdentified = true;
        return true;
      }
      addQName(paramQName);
      skip = true;
      return false;
    }
    return false;
  }
  
  public void addQName(QName paramQName)
  {
    QName localQName = new QName(paramQName);
    ftempCurrentElement.push(localQName);
  }
  
  public void startDocument(XMLLocator paramXMLLocator, String paramString, NamespaceContext paramNamespaceContext, Augmentations paramAugmentations)
    throws XNIException
  {
    getTokens();
  }
  
  public void doctypeDecl(String paramString1, String paramString2, String paramString3, Augmentations paramAugmentations)
    throws XNIException
  {}
  
  public void xmlDecl(String paramString1, String paramString2, String paramString3, Augmentations paramAugmentations)
    throws XNIException
  {}
  
  public void comment(XMLString paramXMLString, Augmentations paramAugmentations)
    throws XNIException
  {
    if ((fDocumentHandler != null) && (includeElement)) {
      fDocumentHandler.comment(paramXMLString, paramAugmentations);
    }
  }
  
  public void processingInstruction(String paramString, XMLString paramXMLString, Augmentations paramAugmentations)
    throws XNIException
  {
    if ((fDocumentHandler != null) && (includeElement)) {
      fDocumentHandler.processingInstruction(paramString, paramXMLString, paramAugmentations);
    }
  }
  
  public void startElement(QName paramQName, XMLAttributes paramXMLAttributes, Augmentations paramAugmentations)
    throws XNIException
  {
    boolean bool = false;
    if (fCurrentTokenType == 0) {
      getNextToken();
    }
    if (fCurrentTokenType == 1) {
      bool = checkIntegerToken(paramQName);
    } else if (fCurrentTokenType == 2) {
      bool = checkStringToken(paramQName, paramXMLAttributes);
    }
    if ((bool) && (hasMoreToken())) {
      getNextToken();
    }
    if ((fDocumentHandler != null) && (includeElement))
    {
      elemCount += 1;
      fDocumentHandler.startElement(paramQName, paramXMLAttributes, paramAugmentations);
    }
  }
  
  public void endElement(QName paramQName, Augmentations paramAugmentations)
    throws XNIException
  {
    if ((includeElement) && (foundElement != null))
    {
      if (elemCount > 0) {
        elemCount -= 1;
      }
      fDocumentHandler.endElement(paramQName, paramAugmentations);
      if (elemCount == 0) {
        includeElement = false;
      }
    }
    else if (!ftempCurrentElement.empty())
    {
      QName localQName = (QName)ftempCurrentElement.peek();
      if (localQName.equals(paramQName))
      {
        ftempCurrentElement.pop();
        skip = false;
      }
    }
  }
  
  public void emptyElement(QName paramQName, XMLAttributes paramXMLAttributes, Augmentations paramAugmentations)
    throws XNIException
  {
    if ((fDocumentHandler != null) && (includeElement)) {
      fDocumentHandler.emptyElement(paramQName, paramXMLAttributes, paramAugmentations);
    }
  }
  
  public void startGeneralEntity(String paramString1, XMLResourceIdentifier paramXMLResourceIdentifier, String paramString2, Augmentations paramAugmentations)
    throws XNIException
  {
    if ((fDocumentHandler != null) && (includeElement)) {
      fDocumentHandler.startGeneralEntity(paramString1, paramXMLResourceIdentifier, paramString2, paramAugmentations);
    }
  }
  
  public void textDecl(String paramString1, String paramString2, Augmentations paramAugmentations)
    throws XNIException
  {
    if ((fDocumentHandler != null) && (includeElement)) {
      fDocumentHandler.textDecl(paramString1, paramString2, paramAugmentations);
    }
  }
  
  public void endGeneralEntity(String paramString, Augmentations paramAugmentations)
    throws XNIException
  {
    if (fDocumentHandler != null) {
      fDocumentHandler.endGeneralEntity(paramString, paramAugmentations);
    }
  }
  
  public void characters(XMLString paramXMLString, Augmentations paramAugmentations)
    throws XNIException
  {
    if ((fDocumentHandler != null) && (includeElement)) {
      fDocumentHandler.characters(paramXMLString, paramAugmentations);
    }
  }
  
  public void ignorableWhitespace(XMLString paramXMLString, Augmentations paramAugmentations)
    throws XNIException
  {
    if ((fDocumentHandler != null) && (includeElement)) {
      fDocumentHandler.ignorableWhitespace(paramXMLString, paramAugmentations);
    }
  }
  
  public void startCDATA(Augmentations paramAugmentations)
    throws XNIException
  {
    if ((fDocumentHandler != null) && (includeElement)) {
      fDocumentHandler.startCDATA(paramAugmentations);
    }
  }
  
  public void endCDATA(Augmentations paramAugmentations)
    throws XNIException
  {
    if ((fDocumentHandler != null) && (includeElement)) {
      fDocumentHandler.endCDATA(paramAugmentations);
    }
  }
  
  public void endDocument(Augmentations paramAugmentations)
    throws XNIException
  {}
  
  public void setDocumentSource(XMLDocumentSource paramXMLDocumentSource)
  {
    fDocumentSource = paramXMLDocumentSource;
  }
  
  public XMLDocumentSource getDocumentSource()
  {
    return fDocumentSource;
  }
  
  protected void reportFatalError(String paramString)
  {
    reportFatalError(paramString, null);
  }
  
  protected void reportFatalError(String paramString, Object[] paramArrayOfObject)
  {
    if (fErrorReporter != null) {
      fErrorReporter.reportError(fDocLocation, "http://www.w3.org/TR/xinclude", paramString, paramArrayOfObject, (short)2);
    }
  }
  
  protected boolean isRootDocument()
  {
    return fParentXIncludeHandler == null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\xinclude\XPointerElementHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
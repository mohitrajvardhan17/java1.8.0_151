package com.sun.org.apache.xpath.internal.jaxp;

import com.sun.org.apache.xalan.internal.res.XSLMessages;
import com.sun.org.apache.xalan.internal.utils.FactoryImpl;
import com.sun.org.apache.xalan.internal.utils.FeatureManager;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XObject;
import java.io.IOException;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFunctionException;
import javax.xml.xpath.XPathFunctionResolver;
import javax.xml.xpath.XPathVariableResolver;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeIterator;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XPathImpl
  implements javax.xml.xpath.XPath
{
  private XPathVariableResolver variableResolver;
  private XPathFunctionResolver functionResolver;
  private XPathVariableResolver origVariableResolver = variableResolver = paramXPathVariableResolver;
  private XPathFunctionResolver origFunctionResolver = functionResolver = paramXPathFunctionResolver;
  private NamespaceContext namespaceContext = null;
  private JAXPPrefixResolver prefixResolver;
  private boolean featureSecureProcessing = false;
  private boolean useServiceMechanism = true;
  private final FeatureManager featureManager;
  private static Document d = null;
  
  XPathImpl(XPathVariableResolver paramXPathVariableResolver, XPathFunctionResolver paramXPathFunctionResolver)
  {
    this(paramXPathVariableResolver, paramXPathFunctionResolver, false, true, new FeatureManager());
  }
  
  XPathImpl(XPathVariableResolver paramXPathVariableResolver, XPathFunctionResolver paramXPathFunctionResolver, boolean paramBoolean1, boolean paramBoolean2, FeatureManager paramFeatureManager)
  {
    featureSecureProcessing = paramBoolean1;
    useServiceMechanism = paramBoolean2;
    featureManager = paramFeatureManager;
  }
  
  public void setXPathVariableResolver(XPathVariableResolver paramXPathVariableResolver)
  {
    if (paramXPathVariableResolver == null)
    {
      String str = XSLMessages.createXPATHMessage("ER_ARG_CANNOT_BE_NULL", new Object[] { "XPathVariableResolver" });
      throw new NullPointerException(str);
    }
    variableResolver = paramXPathVariableResolver;
  }
  
  public XPathVariableResolver getXPathVariableResolver()
  {
    return variableResolver;
  }
  
  public void setXPathFunctionResolver(XPathFunctionResolver paramXPathFunctionResolver)
  {
    if (paramXPathFunctionResolver == null)
    {
      String str = XSLMessages.createXPATHMessage("ER_ARG_CANNOT_BE_NULL", new Object[] { "XPathFunctionResolver" });
      throw new NullPointerException(str);
    }
    functionResolver = paramXPathFunctionResolver;
  }
  
  public XPathFunctionResolver getXPathFunctionResolver()
  {
    return functionResolver;
  }
  
  public void setNamespaceContext(NamespaceContext paramNamespaceContext)
  {
    if (paramNamespaceContext == null)
    {
      String str = XSLMessages.createXPATHMessage("ER_ARG_CANNOT_BE_NULL", new Object[] { "NamespaceContext" });
      throw new NullPointerException(str);
    }
    namespaceContext = paramNamespaceContext;
    prefixResolver = new JAXPPrefixResolver(paramNamespaceContext);
  }
  
  public NamespaceContext getNamespaceContext()
  {
    return namespaceContext;
  }
  
  private DocumentBuilder getParser()
  {
    try
    {
      DocumentBuilderFactory localDocumentBuilderFactory = FactoryImpl.getDOMFactory(useServiceMechanism);
      localDocumentBuilderFactory.setNamespaceAware(true);
      localDocumentBuilderFactory.setValidating(false);
      return localDocumentBuilderFactory.newDocumentBuilder();
    }
    catch (ParserConfigurationException localParserConfigurationException)
    {
      throw new Error(localParserConfigurationException);
    }
  }
  
  private XObject eval(String paramString, Object paramObject)
    throws TransformerException
  {
    com.sun.org.apache.xpath.internal.XPath localXPath = new com.sun.org.apache.xpath.internal.XPath(paramString, null, prefixResolver, 0);
    XPathContext localXPathContext = null;
    if (functionResolver != null)
    {
      localObject = new JAXPExtensionsProvider(functionResolver, featureSecureProcessing, featureManager);
      localXPathContext = new XPathContext(localObject);
    }
    else
    {
      localXPathContext = new XPathContext();
    }
    Object localObject = null;
    localXPathContext.setVarStack(new JAXPVariableStack(variableResolver));
    if ((paramObject instanceof Node)) {
      localObject = localXPath.execute(localXPathContext, (Node)paramObject, prefixResolver);
    } else {
      localObject = localXPath.execute(localXPathContext, -1, prefixResolver);
    }
    return (XObject)localObject;
  }
  
  public Object evaluate(String paramString, Object paramObject, QName paramQName)
    throws XPathExpressionException
  {
    Object localObject;
    if (paramString == null)
    {
      localObject = XSLMessages.createXPATHMessage("ER_ARG_CANNOT_BE_NULL", new Object[] { "XPath expression" });
      throw new NullPointerException((String)localObject);
    }
    if (paramQName == null)
    {
      localObject = XSLMessages.createXPATHMessage("ER_ARG_CANNOT_BE_NULL", new Object[] { "returnType" });
      throw new NullPointerException((String)localObject);
    }
    if (!isSupported(paramQName))
    {
      localObject = XSLMessages.createXPATHMessage("ER_UNSUPPORTED_RETURN_TYPE", new Object[] { paramQName.toString() });
      throw new IllegalArgumentException((String)localObject);
    }
    try
    {
      localObject = eval(paramString, paramObject);
      return getResultAsType((XObject)localObject, paramQName);
    }
    catch (NullPointerException localNullPointerException)
    {
      throw new XPathExpressionException(localNullPointerException);
    }
    catch (TransformerException localTransformerException)
    {
      Throwable localThrowable = localTransformerException.getException();
      if ((localThrowable instanceof XPathFunctionException)) {
        throw ((XPathFunctionException)localThrowable);
      }
      throw new XPathExpressionException(localTransformerException);
    }
  }
  
  private boolean isSupported(QName paramQName)
  {
    return (paramQName.equals(XPathConstants.STRING)) || (paramQName.equals(XPathConstants.NUMBER)) || (paramQName.equals(XPathConstants.BOOLEAN)) || (paramQName.equals(XPathConstants.NODE)) || (paramQName.equals(XPathConstants.NODESET));
  }
  
  private Object getResultAsType(XObject paramXObject, QName paramQName)
    throws TransformerException
  {
    if (paramQName.equals(XPathConstants.STRING)) {
      return paramXObject.str();
    }
    if (paramQName.equals(XPathConstants.NUMBER)) {
      return new Double(paramXObject.num());
    }
    if (paramQName.equals(XPathConstants.BOOLEAN)) {
      return new Boolean(paramXObject.bool());
    }
    if (paramQName.equals(XPathConstants.NODESET)) {
      return paramXObject.nodelist();
    }
    if (paramQName.equals(XPathConstants.NODE))
    {
      localObject = paramXObject.nodeset();
      return ((NodeIterator)localObject).nextNode();
    }
    Object localObject = XSLMessages.createXPATHMessage("ER_UNSUPPORTED_RETURN_TYPE", new Object[] { paramQName.toString() });
    throw new IllegalArgumentException((String)localObject);
  }
  
  public String evaluate(String paramString, Object paramObject)
    throws XPathExpressionException
  {
    return (String)evaluate(paramString, paramObject, XPathConstants.STRING);
  }
  
  public XPathExpression compile(String paramString)
    throws XPathExpressionException
  {
    Object localObject;
    if (paramString == null)
    {
      localObject = XSLMessages.createXPATHMessage("ER_ARG_CANNOT_BE_NULL", new Object[] { "XPath expression" });
      throw new NullPointerException((String)localObject);
    }
    try
    {
      localObject = new com.sun.org.apache.xpath.internal.XPath(paramString, null, prefixResolver, 0);
      XPathExpressionImpl localXPathExpressionImpl = new XPathExpressionImpl((com.sun.org.apache.xpath.internal.XPath)localObject, prefixResolver, functionResolver, variableResolver, featureSecureProcessing, useServiceMechanism, featureManager);
      return localXPathExpressionImpl;
    }
    catch (TransformerException localTransformerException)
    {
      throw new XPathExpressionException(localTransformerException);
    }
  }
  
  public Object evaluate(String paramString, InputSource paramInputSource, QName paramQName)
    throws XPathExpressionException
  {
    Object localObject1;
    if (paramInputSource == null)
    {
      localObject1 = XSLMessages.createXPATHMessage("ER_ARG_CANNOT_BE_NULL", new Object[] { "source" });
      throw new NullPointerException((String)localObject1);
    }
    if (paramString == null)
    {
      localObject1 = XSLMessages.createXPATHMessage("ER_ARG_CANNOT_BE_NULL", new Object[] { "XPath expression" });
      throw new NullPointerException((String)localObject1);
    }
    if (paramQName == null)
    {
      localObject1 = XSLMessages.createXPATHMessage("ER_ARG_CANNOT_BE_NULL", new Object[] { "returnType" });
      throw new NullPointerException((String)localObject1);
    }
    if (!isSupported(paramQName))
    {
      localObject1 = XSLMessages.createXPATHMessage("ER_UNSUPPORTED_RETURN_TYPE", new Object[] { paramQName.toString() });
      throw new IllegalArgumentException((String)localObject1);
    }
    try
    {
      localObject1 = getParser().parse(paramInputSource);
      localObject2 = eval(paramString, localObject1);
      return getResultAsType((XObject)localObject2, paramQName);
    }
    catch (SAXException localSAXException)
    {
      throw new XPathExpressionException(localSAXException);
    }
    catch (IOException localIOException)
    {
      throw new XPathExpressionException(localIOException);
    }
    catch (TransformerException localTransformerException)
    {
      Object localObject2 = localTransformerException.getException();
      if ((localObject2 instanceof XPathFunctionException)) {
        throw ((XPathFunctionException)localObject2);
      }
      throw new XPathExpressionException(localTransformerException);
    }
  }
  
  public String evaluate(String paramString, InputSource paramInputSource)
    throws XPathExpressionException
  {
    return (String)evaluate(paramString, paramInputSource, XPathConstants.STRING);
  }
  
  public void reset()
  {
    variableResolver = origVariableResolver;
    functionResolver = origFunctionResolver;
    namespaceContext = null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\jaxp\XPathImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
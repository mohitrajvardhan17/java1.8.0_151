package com.sun.org.apache.xpath.internal.jaxp;

import com.sun.org.apache.xalan.internal.res.XSLMessages;
import com.sun.org.apache.xalan.internal.utils.FactoryImpl;
import com.sun.org.apache.xalan.internal.utils.FeatureManager;
import com.sun.org.apache.xpath.internal.XPath;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XObject;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
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

public class XPathExpressionImpl
  implements XPathExpression
{
  private XPathFunctionResolver functionResolver;
  private XPathVariableResolver variableResolver;
  private JAXPPrefixResolver prefixResolver;
  private XPath xpath;
  private boolean featureSecureProcessing = false;
  private boolean useServicesMechanism = true;
  private final FeatureManager featureManager;
  static DocumentBuilderFactory dbf = null;
  static DocumentBuilder db = null;
  static Document d = null;
  
  protected XPathExpressionImpl()
  {
    this(null, null, null, null, false, true, new FeatureManager());
  }
  
  protected XPathExpressionImpl(XPath paramXPath, JAXPPrefixResolver paramJAXPPrefixResolver, XPathFunctionResolver paramXPathFunctionResolver, XPathVariableResolver paramXPathVariableResolver)
  {
    this(paramXPath, paramJAXPPrefixResolver, paramXPathFunctionResolver, paramXPathVariableResolver, false, true, new FeatureManager());
  }
  
  protected XPathExpressionImpl(XPath paramXPath, JAXPPrefixResolver paramJAXPPrefixResolver, XPathFunctionResolver paramXPathFunctionResolver, XPathVariableResolver paramXPathVariableResolver, boolean paramBoolean1, boolean paramBoolean2, FeatureManager paramFeatureManager)
  {
    xpath = paramXPath;
    prefixResolver = paramJAXPPrefixResolver;
    functionResolver = paramXPathFunctionResolver;
    variableResolver = paramXPathVariableResolver;
    featureSecureProcessing = paramBoolean1;
    useServicesMechanism = paramBoolean2;
    featureManager = paramFeatureManager;
  }
  
  public void setXPath(XPath paramXPath)
  {
    xpath = paramXPath;
  }
  
  public Object eval(Object paramObject, QName paramQName)
    throws TransformerException
  {
    XObject localXObject = eval(paramObject);
    return getResultAsType(localXObject, paramQName);
  }
  
  private XObject eval(Object paramObject)
    throws TransformerException
  {
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
    localXPathContext.setVarStack(new JAXPVariableStack(variableResolver));
    Object localObject = null;
    Node localNode = (Node)paramObject;
    if (localNode == null) {
      localObject = xpath.execute(localXPathContext, -1, prefixResolver);
    } else {
      localObject = xpath.execute(localXPathContext, localNode, prefixResolver);
    }
    return (XObject)localObject;
  }
  
  public Object evaluate(Object paramObject, QName paramQName)
    throws XPathExpressionException
  {
    String str;
    if (paramQName == null)
    {
      str = XSLMessages.createXPATHMessage("ER_ARG_CANNOT_BE_NULL", new Object[] { "returnType" });
      throw new NullPointerException(str);
    }
    if (!isSupported(paramQName))
    {
      str = XSLMessages.createXPATHMessage("ER_UNSUPPORTED_RETURN_TYPE", new Object[] { paramQName.toString() });
      throw new IllegalArgumentException(str);
    }
    try
    {
      return eval(paramObject, paramQName);
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
  
  public String evaluate(Object paramObject)
    throws XPathExpressionException
  {
    return (String)evaluate(paramObject, XPathConstants.STRING);
  }
  
  public Object evaluate(InputSource paramInputSource, QName paramQName)
    throws XPathExpressionException
  {
    Object localObject;
    if ((paramInputSource == null) || (paramQName == null))
    {
      localObject = XSLMessages.createXPATHMessage("ER_SOURCE_RETURN_TYPE_CANNOT_BE_NULL", null);
      throw new NullPointerException((String)localObject);
    }
    if (!isSupported(paramQName))
    {
      localObject = XSLMessages.createXPATHMessage("ER_UNSUPPORTED_RETURN_TYPE", new Object[] { paramQName.toString() });
      throw new IllegalArgumentException((String)localObject);
    }
    try
    {
      if (dbf == null)
      {
        dbf = FactoryImpl.getDOMFactory(useServicesMechanism);
        dbf.setNamespaceAware(true);
        dbf.setValidating(false);
      }
      db = dbf.newDocumentBuilder();
      localObject = db.parse(paramInputSource);
      return eval(localObject, paramQName);
    }
    catch (Exception localException)
    {
      throw new XPathExpressionException(localException);
    }
  }
  
  public String evaluate(InputSource paramInputSource)
    throws XPathExpressionException
  {
    return (String)evaluate(paramInputSource, XPathConstants.STRING);
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
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\jaxp\XPathExpressionImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
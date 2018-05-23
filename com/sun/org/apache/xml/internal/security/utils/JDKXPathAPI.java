package com.sun.org.apache.xml.internal.security.utils;

import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFactoryConfigurationException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class JDKXPathAPI
  implements XPathAPI
{
  private XPathFactory xpf;
  private String xpathStr;
  private XPathExpression xpathExpression;
  
  public JDKXPathAPI() {}
  
  public NodeList selectNodeList(Node paramNode1, Node paramNode2, String paramString, Node paramNode3)
    throws TransformerException
  {
    if ((!paramString.equals(xpathStr)) || (xpathExpression == null))
    {
      if (xpf == null)
      {
        xpf = XPathFactory.newInstance();
        try
        {
          xpf.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", Boolean.TRUE.booleanValue());
        }
        catch (XPathFactoryConfigurationException localXPathFactoryConfigurationException)
        {
          throw new TransformerException("empty", localXPathFactoryConfigurationException);
        }
      }
      XPath localXPath = xpf.newXPath();
      localXPath.setNamespaceContext(new DOMNamespaceContext(paramNode3));
      xpathStr = paramString;
      try
      {
        xpathExpression = localXPath.compile(xpathStr);
      }
      catch (XPathExpressionException localXPathExpressionException2)
      {
        throw new TransformerException("empty", localXPathExpressionException2);
      }
    }
    try
    {
      return (NodeList)xpathExpression.evaluate(paramNode1, XPathConstants.NODESET);
    }
    catch (XPathExpressionException localXPathExpressionException1)
    {
      throw new TransformerException("empty", localXPathExpressionException1);
    }
  }
  
  public boolean evaluate(Node paramNode1, Node paramNode2, String paramString, Node paramNode3)
    throws TransformerException
  {
    Object localObject;
    if ((!paramString.equals(xpathStr)) || (xpathExpression == null))
    {
      if (xpf == null)
      {
        xpf = XPathFactory.newInstance();
        try
        {
          xpf.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", Boolean.TRUE.booleanValue());
        }
        catch (XPathFactoryConfigurationException localXPathFactoryConfigurationException)
        {
          throw new TransformerException("empty", localXPathFactoryConfigurationException);
        }
      }
      localObject = xpf.newXPath();
      ((XPath)localObject).setNamespaceContext(new DOMNamespaceContext(paramNode3));
      xpathStr = paramString;
      try
      {
        xpathExpression = ((XPath)localObject).compile(xpathStr);
      }
      catch (XPathExpressionException localXPathExpressionException2)
      {
        throw new TransformerException("empty", localXPathExpressionException2);
      }
    }
    try
    {
      localObject = (Boolean)xpathExpression.evaluate(paramNode1, XPathConstants.BOOLEAN);
      return ((Boolean)localObject).booleanValue();
    }
    catch (XPathExpressionException localXPathExpressionException1)
    {
      throw new TransformerException("empty", localXPathExpressionException1);
    }
  }
  
  public void clear()
  {
    xpathStr = null;
    xpathExpression = null;
    xpf = null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\utils\JDKXPathAPI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
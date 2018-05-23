package com.sun.org.apache.xml.internal.security.utils;

import com.sun.org.apache.xml.internal.security.transforms.implementations.FuncHere;
import com.sun.org.apache.xml.internal.utils.PrefixResolver;
import com.sun.org.apache.xml.internal.utils.PrefixResolverDefault;
import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.XPath;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.compiler.FunctionTable;
import com.sun.org.apache.xpath.internal.objects.XObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XalanXPathAPI
  implements XPathAPI
{
  private static Logger log = Logger.getLogger(XalanXPathAPI.class.getName());
  private String xpathStr = null;
  private XPath xpath = null;
  private static FunctionTable funcTable = null;
  private static boolean installed;
  private XPathContext context;
  
  public XalanXPathAPI() {}
  
  public NodeList selectNodeList(Node paramNode1, Node paramNode2, String paramString, Node paramNode3)
    throws TransformerException
  {
    XObject localXObject = eval(paramNode1, paramNode2, paramString, paramNode3);
    return localXObject.nodelist();
  }
  
  public boolean evaluate(Node paramNode1, Node paramNode2, String paramString, Node paramNode3)
    throws TransformerException
  {
    XObject localXObject = eval(paramNode1, paramNode2, paramString, paramNode3);
    return localXObject.bool();
  }
  
  public void clear()
  {
    xpathStr = null;
    xpath = null;
    context = null;
  }
  
  public static synchronized boolean isInstalled()
  {
    return installed;
  }
  
  private XObject eval(Node paramNode1, Node paramNode2, String paramString, Node paramNode3)
    throws TransformerException
  {
    if (context == null)
    {
      context = new XPathContext(paramNode2);
      context.setSecureProcessing(true);
    }
    Node localNode = paramNode3.getNodeType() == 9 ? ((Document)paramNode3).getDocumentElement() : paramNode3;
    PrefixResolverDefault localPrefixResolverDefault = new PrefixResolverDefault(localNode);
    if (!paramString.equals(xpathStr))
    {
      if (paramString.indexOf("here()") > 0) {
        context.reset();
      }
      xpath = createXPath(paramString, localPrefixResolverDefault);
      xpathStr = paramString;
    }
    int i = context.getDTMHandleFromNode(paramNode1);
    return xpath.execute(context, i, localPrefixResolverDefault);
  }
  
  private XPath createXPath(String paramString, PrefixResolver paramPrefixResolver)
    throws TransformerException
  {
    XPath localXPath = null;
    Class[] arrayOfClass = { String.class, SourceLocator.class, PrefixResolver.class, Integer.TYPE, ErrorListener.class, FunctionTable.class };
    Object[] arrayOfObject = { paramString, null, paramPrefixResolver, Integer.valueOf(0), null, funcTable };
    try
    {
      Constructor localConstructor = XPath.class.getConstructor(arrayOfClass);
      localXPath = (XPath)localConstructor.newInstance(arrayOfObject);
    }
    catch (Exception localException)
    {
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, localException.getMessage(), localException);
      }
    }
    if (localXPath == null) {
      localXPath = new XPath(paramString, null, paramPrefixResolver, 0, null);
    }
    return localXPath;
  }
  
  private static synchronized void fixupFunctionTable()
  {
    installed = false;
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "Registering Here function");
    }
    Method localMethod;
    Object[] arrayOfObject;
    try
    {
      Class[] arrayOfClass1 = { String.class, Expression.class };
      localMethod = FunctionTable.class.getMethod("installFunction", arrayOfClass1);
      if ((localMethod.getModifiers() & 0x8) != 0)
      {
        arrayOfObject = new Object[] { "here", new FuncHere() };
        localMethod.invoke(null, arrayOfObject);
        installed = true;
      }
    }
    catch (Exception localException1)
    {
      log.log(Level.FINE, "Error installing function using the static installFunction method", localException1);
    }
    if (!installed) {
      try
      {
        funcTable = new FunctionTable();
        Class[] arrayOfClass2 = { String.class, Class.class };
        localMethod = FunctionTable.class.getMethod("installFunction", arrayOfClass2);
        arrayOfObject = new Object[] { "here", FuncHere.class };
        localMethod.invoke(funcTable, arrayOfObject);
        installed = true;
      }
      catch (Exception localException2)
      {
        log.log(Level.FINE, "Error installing function using the static installFunction method", localException2);
      }
    }
    if (log.isLoggable(Level.FINE)) {
      if (installed) {
        log.log(Level.FINE, "Registered class " + FuncHere.class.getName() + " for XPath function 'here()' function in internal table");
      } else {
        log.log(Level.FINE, "Unable to register class " + FuncHere.class.getName() + " for XPath function 'here()' function in internal table");
      }
    }
  }
  
  static
  {
    fixupFunctionTable();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\utils\XalanXPathAPI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
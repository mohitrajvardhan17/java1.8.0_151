package com.sun.org.apache.xalan.internal.lib;

import com.sun.org.apache.xml.internal.utils.WrappedRuntimeException;
import com.sun.org.apache.xpath.internal.NodeSet;
import java.util.StringTokenizer;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class ExsltStrings
  extends ExsltBase
{
  static final String JDK_DEFAULT_DOM = "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl";
  
  public ExsltStrings() {}
  
  public static String align(String paramString1, String paramString2, String paramString3)
  {
    if (paramString1.length() >= paramString2.length()) {
      return paramString1.substring(0, paramString2.length());
    }
    if (paramString3.equals("right")) {
      return paramString2.substring(0, paramString2.length() - paramString1.length()) + paramString1;
    }
    if (paramString3.equals("center"))
    {
      int i = (paramString2.length() - paramString1.length()) / 2;
      return paramString2.substring(0, i) + paramString1 + paramString2.substring(i + paramString1.length());
    }
    return paramString1 + paramString2.substring(paramString1.length());
  }
  
  public static String align(String paramString1, String paramString2)
  {
    return align(paramString1, paramString2, "left");
  }
  
  public static String concat(NodeList paramNodeList)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    for (int i = 0; i < paramNodeList.getLength(); i++)
    {
      Node localNode = paramNodeList.item(i);
      String str = toString(localNode);
      if ((str != null) && (str.length() > 0)) {
        localStringBuffer.append(str);
      }
    }
    return localStringBuffer.toString();
  }
  
  public static String padding(double paramDouble, String paramString)
  {
    if ((paramString == null) || (paramString.length() == 0)) {
      return "";
    }
    StringBuffer localStringBuffer = new StringBuffer();
    int i = (int)paramDouble;
    int j = 0;
    int k = 0;
    while (j < i)
    {
      if (k == paramString.length()) {
        k = 0;
      }
      localStringBuffer.append(paramString.charAt(k));
      k++;
      j++;
    }
    return localStringBuffer.toString();
  }
  
  public static String padding(double paramDouble)
  {
    return padding(paramDouble, " ");
  }
  
  public static NodeList split(String paramString1, String paramString2)
  {
    NodeSet localNodeSet = new NodeSet();
    localNodeSet.setShouldCacheNodes(true);
    int i = 0;
    int j = 0;
    int k = 0;
    String str = null;
    while ((i == 0) && (j < paramString1.length()))
    {
      k = paramString1.indexOf(paramString2, j);
      if (k >= 0)
      {
        str = paramString1.substring(j, k);
        j = k + paramString2.length();
      }
      else
      {
        i = 1;
        str = paramString1.substring(j);
      }
      Document localDocument = getDocument();
      synchronized (localDocument)
      {
        Element localElement = localDocument.createElement("token");
        Text localText = localDocument.createTextNode(str);
        localElement.appendChild(localText);
        localNodeSet.addNode(localElement);
      }
    }
    return localNodeSet;
  }
  
  public static NodeList split(String paramString)
  {
    return split(paramString, " ");
  }
  
  public static NodeList tokenize(String paramString1, String paramString2)
  {
    NodeSet localNodeSet = new NodeSet();
    Object localObject1;
    Element localElement;
    if ((paramString2 != null) && (paramString2.length() > 0))
    {
      localObject1 = new StringTokenizer(paramString1, paramString2);
      Document localDocument = getDocument();
      synchronized (localDocument)
      {
        while (((StringTokenizer)localObject1).hasMoreTokens())
        {
          localElement = localDocument.createElement("token");
          localElement.appendChild(localDocument.createTextNode(((StringTokenizer)localObject1).nextToken()));
          localNodeSet.addNode(localElement);
        }
      }
    }
    else
    {
      localObject1 = getDocument();
      synchronized (localObject1)
      {
        for (int i = 0; i < paramString1.length(); i++)
        {
          localElement = ((Document)localObject1).createElement("token");
          localElement.appendChild(((Document)localObject1).createTextNode(paramString1.substring(i, i + 1)));
          localNodeSet.addNode(localElement);
        }
      }
    }
    return localNodeSet;
  }
  
  public static NodeList tokenize(String paramString)
  {
    return tokenize(paramString, " \t\n\r");
  }
  
  private static Document getDocument()
  {
    try
    {
      if (System.getSecurityManager() == null) {
        return DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
      }
      return DocumentBuilderFactory.newInstance("com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl", null).newDocumentBuilder().newDocument();
    }
    catch (ParserConfigurationException localParserConfigurationException)
    {
      throw new WrappedRuntimeException(localParserConfigurationException);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\lib\ExsltStrings.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
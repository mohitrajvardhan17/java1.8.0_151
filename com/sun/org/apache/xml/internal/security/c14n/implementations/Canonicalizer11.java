package com.sun.org.apache.xml.internal.security.c14n.implementations;

import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.c14n.helper.C14nHelper;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public abstract class Canonicalizer11
  extends CanonicalizerBase
{
  private static final String XMLNS_URI = "http://www.w3.org/2000/xmlns/";
  private static final String XML_LANG_URI = "http://www.w3.org/XML/1998/namespace";
  private static Logger log = Logger.getLogger(Canonicalizer11.class.getName());
  private final SortedSet<Attr> result = new TreeSet(COMPARE);
  private boolean firstCall = true;
  private XmlAttrStack xmlattrStack = new XmlAttrStack(null);
  
  public Canonicalizer11(boolean paramBoolean)
  {
    super(paramBoolean);
  }
  
  public byte[] engineCanonicalizeXPathNodeSet(Set<Node> paramSet, String paramString)
    throws CanonicalizationException
  {
    throw new CanonicalizationException("c14n.Canonicalizer.UnsupportedOperation");
  }
  
  public byte[] engineCanonicalizeSubTree(Node paramNode, String paramString)
    throws CanonicalizationException
  {
    throw new CanonicalizationException("c14n.Canonicalizer.UnsupportedOperation");
  }
  
  protected Iterator<Attr> handleAttributesSubtree(Element paramElement, NameSpaceSymbTable paramNameSpaceSymbTable)
    throws CanonicalizationException
  {
    if ((!paramElement.hasAttributes()) && (!firstCall)) {
      return null;
    }
    SortedSet localSortedSet = result;
    localSortedSet.clear();
    if (paramElement.hasAttributes())
    {
      NamedNodeMap localNamedNodeMap = paramElement.getAttributes();
      int i = localNamedNodeMap.getLength();
      for (int j = 0; j < i; j++)
      {
        Attr localAttr = (Attr)localNamedNodeMap.item(j);
        String str1 = localAttr.getNamespaceURI();
        String str2 = localAttr.getLocalName();
        String str3 = localAttr.getValue();
        if (!"http://www.w3.org/2000/xmlns/".equals(str1))
        {
          localSortedSet.add(localAttr);
        }
        else if ((!"xml".equals(str2)) || (!"http://www.w3.org/XML/1998/namespace".equals(str3)))
        {
          Node localNode = paramNameSpaceSymbTable.addMappingAndRender(str2, str3, localAttr);
          if (localNode != null)
          {
            localSortedSet.add((Attr)localNode);
            if (C14nHelper.namespaceIsRelative(localAttr))
            {
              Object[] arrayOfObject = { paramElement.getTagName(), str2, localAttr.getNodeValue() };
              throw new CanonicalizationException("c14n.Canonicalizer.RelativeNamespace", arrayOfObject);
            }
          }
        }
      }
    }
    if (firstCall)
    {
      paramNameSpaceSymbTable.getUnrenderedNodes(localSortedSet);
      xmlattrStack.getXmlnsAttr(localSortedSet);
      firstCall = false;
    }
    return localSortedSet.iterator();
  }
  
  protected Iterator<Attr> handleAttributes(Element paramElement, NameSpaceSymbTable paramNameSpaceSymbTable)
    throws CanonicalizationException
  {
    xmlattrStack.push(paramNameSpaceSymbTable.getLevel());
    int i = isVisibleDO(paramElement, paramNameSpaceSymbTable.getLevel()) == 1 ? 1 : 0;
    SortedSet localSortedSet = result;
    localSortedSet.clear();
    Object localObject1;
    if (paramElement.hasAttributes())
    {
      localObject1 = paramElement.getAttributes();
      int j = ((NamedNodeMap)localObject1).getLength();
      for (int k = 0; k < j; k++)
      {
        Attr localAttr = (Attr)((NamedNodeMap)localObject1).item(k);
        String str1 = localAttr.getNamespaceURI();
        String str2 = localAttr.getLocalName();
        String str3 = localAttr.getValue();
        if (!"http://www.w3.org/2000/xmlns/".equals(str1))
        {
          if ("http://www.w3.org/XML/1998/namespace".equals(str1))
          {
            if (str2.equals("id"))
            {
              if (i != 0) {
                localSortedSet.add(localAttr);
              }
            }
            else {
              xmlattrStack.addXmlnsAttr(localAttr);
            }
          }
          else if (i != 0) {
            localSortedSet.add(localAttr);
          }
        }
        else if ((!"xml".equals(str2)) || (!"http://www.w3.org/XML/1998/namespace".equals(str3))) {
          if (isVisible(localAttr))
          {
            if ((i != 0) || (!paramNameSpaceSymbTable.removeMappingIfRender(str2)))
            {
              Node localNode = paramNameSpaceSymbTable.addMappingAndRender(str2, str3, localAttr);
              if (localNode != null)
              {
                localSortedSet.add((Attr)localNode);
                if (C14nHelper.namespaceIsRelative(localAttr))
                {
                  Object[] arrayOfObject = { paramElement.getTagName(), str2, localAttr.getNodeValue() };
                  throw new CanonicalizationException("c14n.Canonicalizer.RelativeNamespace", arrayOfObject);
                }
              }
            }
          }
          else if ((i != 0) && (!"xmlns".equals(str2))) {
            paramNameSpaceSymbTable.removeMapping(str2);
          } else {
            paramNameSpaceSymbTable.addMapping(str2, str3, localAttr);
          }
        }
      }
    }
    if (i != 0)
    {
      localObject1 = paramElement.getAttributeNodeNS("http://www.w3.org/2000/xmlns/", "xmlns");
      Object localObject2 = null;
      if (localObject1 == null) {
        localObject2 = paramNameSpaceSymbTable.getMapping("xmlns");
      } else if (!isVisible((Node)localObject1)) {
        localObject2 = paramNameSpaceSymbTable.addMappingAndRender("xmlns", "", getNullNode(((Attr)localObject1).getOwnerDocument()));
      }
      if (localObject2 != null) {
        localSortedSet.add((Attr)localObject2);
      }
      xmlattrStack.getXmlnsAttr(localSortedSet);
      paramNameSpaceSymbTable.getUnrenderedNodes(localSortedSet);
    }
    return localSortedSet.iterator();
  }
  
  protected void circumventBugIfNeeded(XMLSignatureInput paramXMLSignatureInput)
    throws CanonicalizationException, ParserConfigurationException, IOException, SAXException
  {
    if (!paramXMLSignatureInput.isNeedsToBeExpanded()) {
      return;
    }
    Document localDocument = null;
    if (paramXMLSignatureInput.getSubNode() != null) {
      localDocument = XMLUtils.getOwnerDocument(paramXMLSignatureInput.getSubNode());
    } else {
      localDocument = XMLUtils.getOwnerDocument(paramXMLSignatureInput.getNodeSet());
    }
    XMLUtils.circumventBug2650(localDocument);
  }
  
  protected void handleParent(Element paramElement, NameSpaceSymbTable paramNameSpaceSymbTable)
  {
    if ((!paramElement.hasAttributes()) && (paramElement.getNamespaceURI() == null)) {
      return;
    }
    xmlattrStack.push(-1);
    NamedNodeMap localNamedNodeMap = paramElement.getAttributes();
    int i = localNamedNodeMap.getLength();
    Object localObject1;
    String str2;
    Object localObject2;
    for (int j = 0; j < i; j++)
    {
      localObject1 = (Attr)localNamedNodeMap.item(j);
      str2 = ((Attr)localObject1).getLocalName();
      localObject2 = ((Attr)localObject1).getNodeValue();
      if ("http://www.w3.org/2000/xmlns/".equals(((Attr)localObject1).getNamespaceURI()))
      {
        if ((!"xml".equals(str2)) || (!"http://www.w3.org/XML/1998/namespace".equals(localObject2))) {
          paramNameSpaceSymbTable.addMapping(str2, (String)localObject2, (Attr)localObject1);
        }
      }
      else if ((!"id".equals(str2)) && ("http://www.w3.org/XML/1998/namespace".equals(((Attr)localObject1).getNamespaceURI()))) {
        xmlattrStack.addXmlnsAttr((Attr)localObject1);
      }
    }
    if (paramElement.getNamespaceURI() != null)
    {
      String str1 = paramElement.getPrefix();
      localObject1 = paramElement.getNamespaceURI();
      if ((str1 == null) || (str1.equals("")))
      {
        str1 = "xmlns";
        str2 = "xmlns";
      }
      else
      {
        str2 = "xmlns:" + str1;
      }
      localObject2 = paramElement.getOwnerDocument().createAttributeNS("http://www.w3.org/2000/xmlns/", str2);
      ((Attr)localObject2).setValue((String)localObject1);
      paramNameSpaceSymbTable.addMapping(str1, (String)localObject1, (Attr)localObject2);
    }
  }
  
  private static String joinURI(String paramString1, String paramString2)
    throws URISyntaxException
  {
    String str1 = null;
    String str2 = null;
    String str3 = "";
    String str4 = null;
    if (paramString1 != null)
    {
      if (paramString1.endsWith("..")) {
        paramString1 = paramString1 + "/";
      }
      localURI = new URI(paramString1);
      str1 = localURI.getScheme();
      str2 = localURI.getAuthority();
      str3 = localURI.getPath();
      str4 = localURI.getQuery();
    }
    URI localURI = new URI(paramString2);
    String str5 = localURI.getScheme();
    String str6 = localURI.getAuthority();
    String str7 = localURI.getPath();
    String str8 = localURI.getQuery();
    if ((str5 != null) && (str5.equals(str1))) {
      str5 = null;
    }
    String str9;
    String str10;
    String str11;
    String str12;
    if (str5 != null)
    {
      str9 = str5;
      str10 = str6;
      str11 = removeDotSegments(str7);
      str12 = str8;
    }
    else
    {
      if (str6 != null)
      {
        str10 = str6;
        str11 = removeDotSegments(str7);
        str12 = str8;
      }
      else
      {
        if (str7.length() == 0)
        {
          str11 = str3;
          if (str8 != null) {
            str12 = str8;
          } else {
            str12 = str4;
          }
        }
        else
        {
          if (str7.startsWith("/"))
          {
            str11 = removeDotSegments(str7);
          }
          else
          {
            if ((str2 != null) && (str3.length() == 0))
            {
              str11 = "/" + str7;
            }
            else
            {
              int i = str3.lastIndexOf('/');
              if (i == -1) {
                str11 = str7;
              } else {
                str11 = str3.substring(0, i + 1) + str7;
              }
            }
            str11 = removeDotSegments(str11);
          }
          str12 = str8;
        }
        str10 = str2;
      }
      str9 = str1;
    }
    return new URI(str9, str10, str11, str12, null).toString();
  }
  
  private static String removeDotSegments(String paramString)
  {
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "STEP   OUTPUT BUFFER\t\tINPUT BUFFER");
    }
    for (String str1 = paramString; str1.indexOf("//") > -1; str1 = str1.replaceAll("//", "/")) {}
    StringBuilder localStringBuilder = new StringBuilder();
    if (str1.charAt(0) == '/')
    {
      localStringBuilder.append("/");
      str1 = str1.substring(1);
    }
    printStep("1 ", localStringBuilder.toString(), str1);
    while (str1.length() != 0) {
      if (str1.startsWith("./"))
      {
        str1 = str1.substring(2);
        printStep("2A", localStringBuilder.toString(), str1);
      }
      else if (str1.startsWith("../"))
      {
        str1 = str1.substring(3);
        if (!localStringBuilder.toString().equals("/")) {
          localStringBuilder.append("../");
        }
        printStep("2A", localStringBuilder.toString(), str1);
      }
      else if (str1.startsWith("/./"))
      {
        str1 = str1.substring(2);
        printStep("2B", localStringBuilder.toString(), str1);
      }
      else if (str1.equals("/."))
      {
        str1 = str1.replaceFirst("/.", "/");
        printStep("2B", localStringBuilder.toString(), str1);
      }
      else
      {
        int i;
        if (str1.startsWith("/../"))
        {
          str1 = str1.substring(3);
          if (localStringBuilder.length() == 0)
          {
            localStringBuilder.append("/");
          }
          else if (localStringBuilder.toString().endsWith("../"))
          {
            localStringBuilder.append("..");
          }
          else if (localStringBuilder.toString().endsWith(".."))
          {
            localStringBuilder.append("/..");
          }
          else
          {
            i = localStringBuilder.lastIndexOf("/");
            if (i == -1)
            {
              localStringBuilder = new StringBuilder();
              if (str1.charAt(0) == '/') {
                str1 = str1.substring(1);
              }
            }
            else
            {
              localStringBuilder = localStringBuilder.delete(i, localStringBuilder.length());
            }
          }
          printStep("2C", localStringBuilder.toString(), str1);
        }
        else if (str1.equals("/.."))
        {
          str1 = str1.replaceFirst("/..", "/");
          if (localStringBuilder.length() == 0)
          {
            localStringBuilder.append("/");
          }
          else if (localStringBuilder.toString().endsWith("../"))
          {
            localStringBuilder.append("..");
          }
          else if (localStringBuilder.toString().endsWith(".."))
          {
            localStringBuilder.append("/..");
          }
          else
          {
            i = localStringBuilder.lastIndexOf("/");
            if (i == -1)
            {
              localStringBuilder = new StringBuilder();
              if (str1.charAt(0) == '/') {
                str1 = str1.substring(1);
              }
            }
            else
            {
              localStringBuilder = localStringBuilder.delete(i, localStringBuilder.length());
            }
          }
          printStep("2C", localStringBuilder.toString(), str1);
        }
        else if (str1.equals("."))
        {
          str1 = "";
          printStep("2D", localStringBuilder.toString(), str1);
        }
        else if (str1.equals(".."))
        {
          if (!localStringBuilder.toString().equals("/")) {
            localStringBuilder.append("..");
          }
          str1 = "";
          printStep("2D", localStringBuilder.toString(), str1);
        }
        else
        {
          i = -1;
          int j = str1.indexOf('/');
          if (j == 0)
          {
            i = str1.indexOf('/', 1);
          }
          else
          {
            i = j;
            j = 0;
          }
          String str2;
          if (i == -1)
          {
            str2 = str1.substring(j);
            str1 = "";
          }
          else
          {
            str2 = str1.substring(j, i);
            str1 = str1.substring(i);
          }
          localStringBuilder.append(str2);
          printStep("2E", localStringBuilder.toString(), str1);
        }
      }
    }
    if (localStringBuilder.toString().endsWith(".."))
    {
      localStringBuilder.append("/");
      printStep("3 ", localStringBuilder.toString(), str1);
    }
    return localStringBuilder.toString();
  }
  
  private static void printStep(String paramString1, String paramString2, String paramString3)
  {
    if (log.isLoggable(Level.FINE))
    {
      log.log(Level.FINE, " " + paramString1 + ":   " + paramString2);
      if (paramString2.length() == 0) {
        log.log(Level.FINE, "\t\t\t\t" + paramString3);
      } else {
        log.log(Level.FINE, "\t\t\t" + paramString3);
      }
    }
  }
  
  private static class XmlAttrStack
  {
    int currentLevel = 0;
    int lastlevel = 0;
    XmlsStackElement cur;
    List<XmlsStackElement> levels = new ArrayList();
    
    private XmlAttrStack() {}
    
    void push(int paramInt)
    {
      currentLevel = paramInt;
      if (currentLevel == -1) {
        return;
      }
      cur = null;
      while (lastlevel >= currentLevel)
      {
        levels.remove(levels.size() - 1);
        int i = levels.size();
        if (i == 0)
        {
          lastlevel = 0;
          return;
        }
        lastlevel = levels.get(i - 1)).level;
      }
    }
    
    void addXmlnsAttr(Attr paramAttr)
    {
      if (cur == null)
      {
        cur = new XmlsStackElement();
        cur.level = currentLevel;
        levels.add(cur);
        lastlevel = currentLevel;
      }
      cur.nodes.add(paramAttr);
    }
    
    void getXmlnsAttr(Collection<Attr> paramCollection)
    {
      int i = levels.size() - 1;
      if (cur == null)
      {
        cur = new XmlsStackElement();
        cur.level = currentLevel;
        lastlevel = currentLevel;
        levels.add(cur);
      }
      int j = 0;
      XmlsStackElement localXmlsStackElement = null;
      if (i == -1)
      {
        j = 1;
      }
      else
      {
        localXmlsStackElement = (XmlsStackElement)levels.get(i);
        if ((rendered) && (level + 1 == currentLevel)) {
          j = 1;
        }
      }
      if (j != 0)
      {
        paramCollection.addAll(cur.nodes);
        cur.rendered = true;
        return;
      }
      HashMap localHashMap = new HashMap();
      ArrayList localArrayList = new ArrayList();
      int k = 1;
      Iterator localIterator;
      Object localObject1;
      while (i >= 0)
      {
        localXmlsStackElement = (XmlsStackElement)levels.get(i);
        if (rendered) {
          k = 0;
        }
        localIterator = nodes.iterator();
        while ((localIterator.hasNext()) && (k != 0))
        {
          localObject1 = (Attr)localIterator.next();
          if ((((Attr)localObject1).getLocalName().equals("base")) && (!rendered)) {
            localArrayList.add(localObject1);
          } else if (!localHashMap.containsKey(((Attr)localObject1).getName())) {
            localHashMap.put(((Attr)localObject1).getName(), localObject1);
          }
        }
        i--;
      }
      if (!localArrayList.isEmpty())
      {
        localIterator = paramCollection.iterator();
        localObject1 = null;
        Object localObject2 = null;
        Attr localAttr;
        while (localIterator.hasNext())
        {
          localAttr = (Attr)localIterator.next();
          if (localAttr.getLocalName().equals("base"))
          {
            localObject1 = localAttr.getValue();
            localObject2 = localAttr;
            break;
          }
        }
        localIterator = localArrayList.iterator();
        while (localIterator.hasNext())
        {
          localAttr = (Attr)localIterator.next();
          if (localObject1 == null)
          {
            localObject1 = localAttr.getValue();
            localObject2 = localAttr;
          }
          else
          {
            try
            {
              localObject1 = Canonicalizer11.joinURI(localAttr.getValue(), (String)localObject1);
            }
            catch (URISyntaxException localURISyntaxException)
            {
              if (Canonicalizer11.log.isLoggable(Level.FINE)) {
                Canonicalizer11.log.log(Level.FINE, localURISyntaxException.getMessage(), localURISyntaxException);
              }
            }
          }
        }
        if ((localObject1 != null) && (((String)localObject1).length() != 0))
        {
          ((Attr)localObject2).setValue((String)localObject1);
          paramCollection.add(localObject2);
        }
      }
      cur.rendered = true;
      paramCollection.addAll(localHashMap.values());
    }
    
    static class XmlsStackElement
    {
      int level;
      boolean rendered = false;
      List<Attr> nodes = new ArrayList();
      
      XmlsStackElement() {}
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\c14n\implementations\Canonicalizer11.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
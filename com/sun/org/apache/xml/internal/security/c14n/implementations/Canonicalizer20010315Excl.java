package com.sun.org.apache.xml.internal.security.c14n.implementations;

import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.c14n.helper.C14nHelper;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.transforms.params.InclusiveNamespaces;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public abstract class Canonicalizer20010315Excl
  extends CanonicalizerBase
{
  private static final String XML_LANG_URI = "http://www.w3.org/XML/1998/namespace";
  private static final String XMLNS_URI = "http://www.w3.org/2000/xmlns/";
  private SortedSet<String> inclusiveNSSet;
  private final SortedSet<Attr> result = new TreeSet(COMPARE);
  
  public Canonicalizer20010315Excl(boolean paramBoolean)
  {
    super(paramBoolean);
  }
  
  public byte[] engineCanonicalizeSubTree(Node paramNode)
    throws CanonicalizationException
  {
    return engineCanonicalizeSubTree(paramNode, "", null);
  }
  
  public byte[] engineCanonicalizeSubTree(Node paramNode, String paramString)
    throws CanonicalizationException
  {
    return engineCanonicalizeSubTree(paramNode, paramString, null);
  }
  
  public byte[] engineCanonicalizeSubTree(Node paramNode1, String paramString, Node paramNode2)
    throws CanonicalizationException
  {
    inclusiveNSSet = InclusiveNamespaces.prefixStr2Set(paramString);
    return super.engineCanonicalizeSubTree(paramNode1, paramNode2);
  }
  
  public byte[] engineCanonicalize(XMLSignatureInput paramXMLSignatureInput, String paramString)
    throws CanonicalizationException
  {
    inclusiveNSSet = InclusiveNamespaces.prefixStr2Set(paramString);
    return super.engineCanonicalize(paramXMLSignatureInput);
  }
  
  public byte[] engineCanonicalizeXPathNodeSet(Set<Node> paramSet, String paramString)
    throws CanonicalizationException
  {
    inclusiveNSSet = InclusiveNamespaces.prefixStr2Set(paramString);
    return super.engineCanonicalizeXPathNodeSet(paramSet);
  }
  
  protected Iterator<Attr> handleAttributesSubtree(Element paramElement, NameSpaceSymbTable paramNameSpaceSymbTable)
    throws CanonicalizationException
  {
    SortedSet localSortedSet = result;
    localSortedSet.clear();
    TreeSet localTreeSet = new TreeSet();
    if ((inclusiveNSSet != null) && (!inclusiveNSSet.isEmpty())) {
      localTreeSet.addAll(inclusiveNSSet);
    }
    Attr localAttr;
    if (paramElement.hasAttributes())
    {
      localObject1 = paramElement.getAttributes();
      int i = ((NamedNodeMap)localObject1).getLength();
      for (int j = 0; j < i; j++)
      {
        localAttr = (Attr)((NamedNodeMap)localObject1).item(j);
        String str2 = localAttr.getLocalName();
        String str3 = localAttr.getNodeValue();
        Object localObject2;
        if (!"http://www.w3.org/2000/xmlns/".equals(localAttr.getNamespaceURI()))
        {
          localObject2 = localAttr.getPrefix();
          if ((localObject2 != null) && (!((String)localObject2).equals("xml")) && (!((String)localObject2).equals("xmlns"))) {
            localTreeSet.add(localObject2);
          }
          localSortedSet.add(localAttr);
        }
        else if (((!"xml".equals(str2)) || (!"http://www.w3.org/XML/1998/namespace".equals(str3))) && (paramNameSpaceSymbTable.addMapping(str2, str3, localAttr)) && (C14nHelper.namespaceIsRelative(str3)))
        {
          localObject2 = new Object[] { paramElement.getTagName(), str2, localAttr.getNodeValue() };
          throw new CanonicalizationException("c14n.Canonicalizer.RelativeNamespace", (Object[])localObject2);
        }
      }
    }
    Object localObject1 = null;
    if ((paramElement.getNamespaceURI() != null) && (paramElement.getPrefix() != null) && (paramElement.getPrefix().length() != 0)) {
      localObject1 = paramElement.getPrefix();
    } else {
      localObject1 = "xmlns";
    }
    localTreeSet.add(localObject1);
    Iterator localIterator = localTreeSet.iterator();
    while (localIterator.hasNext())
    {
      String str1 = (String)localIterator.next();
      localAttr = paramNameSpaceSymbTable.getMapping(str1);
      if (localAttr != null) {
        localSortedSet.add(localAttr);
      }
    }
    return localSortedSet.iterator();
  }
  
  protected final Iterator<Attr> handleAttributes(Element paramElement, NameSpaceSymbTable paramNameSpaceSymbTable)
    throws CanonicalizationException
  {
    SortedSet localSortedSet = result;
    localSortedSet.clear();
    TreeSet localTreeSet = null;
    int i = isVisibleDO(paramElement, paramNameSpaceSymbTable.getLevel()) == 1 ? 1 : 0;
    if (i != 0)
    {
      localTreeSet = new TreeSet();
      if ((inclusiveNSSet != null) && (!inclusiveNSSet.isEmpty())) {
        localTreeSet.addAll(inclusiveNSSet);
      }
    }
    Object localObject1;
    Object localObject2;
    Object localObject3;
    if (paramElement.hasAttributes())
    {
      localObject1 = paramElement.getAttributes();
      int j = ((NamedNodeMap)localObject1).getLength();
      for (int k = 0; k < j; k++)
      {
        localObject2 = (Attr)((NamedNodeMap)localObject1).item(k);
        localObject3 = ((Attr)localObject2).getLocalName();
        String str2 = ((Attr)localObject2).getNodeValue();
        Object localObject4;
        if (!"http://www.w3.org/2000/xmlns/".equals(((Attr)localObject2).getNamespaceURI()))
        {
          if ((isVisible((Node)localObject2)) && (i != 0))
          {
            localObject4 = ((Attr)localObject2).getPrefix();
            if ((localObject4 != null) && (!((String)localObject4).equals("xml")) && (!((String)localObject4).equals("xmlns"))) {
              localTreeSet.add(localObject4);
            }
            localSortedSet.add(localObject2);
          }
        }
        else if ((i != 0) && (!isVisible((Node)localObject2)) && (!"xmlns".equals(localObject3)))
        {
          paramNameSpaceSymbTable.removeMappingIfNotRender((String)localObject3);
        }
        else
        {
          if ((i == 0) && (isVisible((Node)localObject2)) && (inclusiveNSSet.contains(localObject3)) && (!paramNameSpaceSymbTable.removeMappingIfRender((String)localObject3)))
          {
            localObject4 = paramNameSpaceSymbTable.addMappingAndRender((String)localObject3, str2, (Attr)localObject2);
            if (localObject4 != null)
            {
              localSortedSet.add((Attr)localObject4);
              if (C14nHelper.namespaceIsRelative((Attr)localObject2))
              {
                Object[] arrayOfObject = { paramElement.getTagName(), localObject3, ((Attr)localObject2).getNodeValue() };
                throw new CanonicalizationException("c14n.Canonicalizer.RelativeNamespace", arrayOfObject);
              }
            }
          }
          if ((paramNameSpaceSymbTable.addMapping((String)localObject3, str2, (Attr)localObject2)) && (C14nHelper.namespaceIsRelative(str2)))
          {
            localObject4 = new Object[] { paramElement.getTagName(), localObject3, ((Attr)localObject2).getNodeValue() };
            throw new CanonicalizationException("c14n.Canonicalizer.RelativeNamespace", (Object[])localObject4);
          }
        }
      }
    }
    if (i != 0)
    {
      localObject1 = paramElement.getAttributeNodeNS("http://www.w3.org/2000/xmlns/", "xmlns");
      if ((localObject1 != null) && (!isVisible((Node)localObject1))) {
        paramNameSpaceSymbTable.addMapping("xmlns", "", getNullNode(((Attr)localObject1).getOwnerDocument()));
      }
      String str1 = null;
      if ((paramElement.getNamespaceURI() != null) && (paramElement.getPrefix() != null) && (paramElement.getPrefix().length() != 0)) {
        str1 = paramElement.getPrefix();
      } else {
        str1 = "xmlns";
      }
      localTreeSet.add(str1);
      Iterator localIterator = localTreeSet.iterator();
      while (localIterator.hasNext())
      {
        localObject2 = (String)localIterator.next();
        localObject3 = paramNameSpaceSymbTable.getMapping((String)localObject2);
        if (localObject3 != null) {
          localSortedSet.add(localObject3);
        }
      }
    }
    return localSortedSet.iterator();
  }
  
  protected void circumventBugIfNeeded(XMLSignatureInput paramXMLSignatureInput)
    throws CanonicalizationException, ParserConfigurationException, IOException, SAXException
  {
    if ((!paramXMLSignatureInput.isNeedsToBeExpanded()) || (inclusiveNSSet.isEmpty()) || (inclusiveNSSet.isEmpty())) {
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
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\c14n\implementations\Canonicalizer20010315Excl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
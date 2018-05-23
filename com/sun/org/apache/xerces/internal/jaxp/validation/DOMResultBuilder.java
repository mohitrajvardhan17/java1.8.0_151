package com.sun.org.apache.xerces.internal.jaxp.validation;

import com.sun.org.apache.xerces.internal.dom.AttrImpl;
import com.sun.org.apache.xerces.internal.dom.CoreDocumentImpl;
import com.sun.org.apache.xerces.internal.dom.DOMMessageFormatter;
import com.sun.org.apache.xerces.internal.dom.DocumentTypeImpl;
import com.sun.org.apache.xerces.internal.dom.ElementImpl;
import com.sun.org.apache.xerces.internal.dom.ElementNSImpl;
import com.sun.org.apache.xerces.internal.dom.EntityImpl;
import com.sun.org.apache.xerces.internal.dom.NotationImpl;
import com.sun.org.apache.xerces.internal.dom.PSVIAttrNSImpl;
import com.sun.org.apache.xerces.internal.dom.PSVIDocumentImpl;
import com.sun.org.apache.xerces.internal.dom.PSVIElementNSImpl;
import com.sun.org.apache.xerces.internal.impl.dv.XSSimpleType;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentSource;
import com.sun.org.apache.xerces.internal.xs.AttributePSVI;
import com.sun.org.apache.xerces.internal.xs.ElementPSVI;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;
import java.util.ArrayList;
import javax.xml.transform.dom.DOMResult;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Entity;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.Notation;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

final class DOMResultBuilder
  implements DOMDocumentHandler
{
  private static final int[] kidOK = new int[13];
  private Document fDocument;
  private CoreDocumentImpl fDocumentImpl;
  private boolean fStorePSVI;
  private Node fTarget;
  private Node fNextSibling;
  private Node fCurrentNode;
  private Node fFragmentRoot;
  private final ArrayList fTargetChildren = new ArrayList();
  private boolean fIgnoreChars;
  private final QName fAttributeQName = new QName();
  
  public DOMResultBuilder() {}
  
  public void setDOMResult(DOMResult paramDOMResult)
  {
    fCurrentNode = null;
    fFragmentRoot = null;
    fIgnoreChars = false;
    fTargetChildren.clear();
    if (paramDOMResult != null)
    {
      fTarget = paramDOMResult.getNode();
      fNextSibling = paramDOMResult.getNextSibling();
      fDocument = (fTarget.getNodeType() == 9 ? (Document)fTarget : fTarget.getOwnerDocument());
      fDocumentImpl = ((fDocument instanceof CoreDocumentImpl) ? (CoreDocumentImpl)fDocument : null);
      fStorePSVI = (fDocument instanceof PSVIDocumentImpl);
      return;
    }
    fTarget = null;
    fNextSibling = null;
    fDocument = null;
    fDocumentImpl = null;
    fStorePSVI = false;
  }
  
  public void doctypeDecl(DocumentType paramDocumentType)
    throws XNIException
  {
    if (fDocumentImpl != null)
    {
      DocumentType localDocumentType = fDocumentImpl.createDocumentType(paramDocumentType.getName(), paramDocumentType.getPublicId(), paramDocumentType.getSystemId());
      String str = paramDocumentType.getInternalSubset();
      if (str != null) {
        ((DocumentTypeImpl)localDocumentType).setInternalSubset(str);
      }
      NamedNodeMap localNamedNodeMap1 = paramDocumentType.getEntities();
      NamedNodeMap localNamedNodeMap2 = localDocumentType.getEntities();
      int i = localNamedNodeMap1.getLength();
      Object localObject1;
      Object localObject2;
      for (int j = 0; j < i; j++)
      {
        localObject1 = (Entity)localNamedNodeMap1.item(j);
        localObject2 = (EntityImpl)fDocumentImpl.createEntity(((Entity)localObject1).getNodeName());
        ((EntityImpl)localObject2).setPublicId(((Entity)localObject1).getPublicId());
        ((EntityImpl)localObject2).setSystemId(((Entity)localObject1).getSystemId());
        ((EntityImpl)localObject2).setNotationName(((Entity)localObject1).getNotationName());
        localNamedNodeMap2.setNamedItem((Node)localObject2);
      }
      localNamedNodeMap1 = paramDocumentType.getNotations();
      localNamedNodeMap2 = localDocumentType.getNotations();
      i = localNamedNodeMap1.getLength();
      for (j = 0; j < i; j++)
      {
        localObject1 = (Notation)localNamedNodeMap1.item(j);
        localObject2 = (NotationImpl)fDocumentImpl.createNotation(((Notation)localObject1).getNodeName());
        ((NotationImpl)localObject2).setPublicId(((Notation)localObject1).getPublicId());
        ((NotationImpl)localObject2).setSystemId(((Notation)localObject1).getSystemId());
        localNamedNodeMap2.setNamedItem((Node)localObject2);
      }
      append(localDocumentType);
    }
  }
  
  public void characters(Text paramText)
    throws XNIException
  {
    append(fDocument.createTextNode(paramText.getNodeValue()));
  }
  
  public void cdata(CDATASection paramCDATASection)
    throws XNIException
  {
    append(fDocument.createCDATASection(paramCDATASection.getNodeValue()));
  }
  
  public void comment(Comment paramComment)
    throws XNIException
  {
    append(fDocument.createComment(paramComment.getNodeValue()));
  }
  
  public void processingInstruction(ProcessingInstruction paramProcessingInstruction)
    throws XNIException
  {
    append(fDocument.createProcessingInstruction(paramProcessingInstruction.getTarget(), paramProcessingInstruction.getData()));
  }
  
  public void setIgnoringCharacters(boolean paramBoolean)
  {
    fIgnoreChars = paramBoolean;
  }
  
  public void startDocument(XMLLocator paramXMLLocator, String paramString, NamespaceContext paramNamespaceContext, Augmentations paramAugmentations)
    throws XNIException
  {}
  
  public void xmlDecl(String paramString1, String paramString2, String paramString3, Augmentations paramAugmentations)
    throws XNIException
  {}
  
  public void doctypeDecl(String paramString1, String paramString2, String paramString3, Augmentations paramAugmentations)
    throws XNIException
  {}
  
  public void comment(XMLString paramXMLString, Augmentations paramAugmentations)
    throws XNIException
  {}
  
  public void processingInstruction(String paramString, XMLString paramXMLString, Augmentations paramAugmentations)
    throws XNIException
  {}
  
  public void startElement(QName paramQName, XMLAttributes paramXMLAttributes, Augmentations paramAugmentations)
    throws XNIException
  {
    int i = paramXMLAttributes.getLength();
    Element localElement;
    int j;
    if (fDocumentImpl == null)
    {
      localElement = fDocument.createElementNS(uri, rawname);
      for (j = 0; j < i; j++)
      {
        paramXMLAttributes.getName(j, fAttributeQName);
        localElement.setAttributeNS(fAttributeQName.uri, fAttributeQName.rawname, paramXMLAttributes.getValue(j));
      }
    }
    else
    {
      localElement = fDocumentImpl.createElementNS(uri, rawname, localpart);
      for (j = 0; j < i; j++)
      {
        paramXMLAttributes.getName(j, fAttributeQName);
        AttrImpl localAttrImpl = (AttrImpl)fDocumentImpl.createAttributeNS(fAttributeQName.uri, fAttributeQName.rawname, fAttributeQName.localpart);
        localAttrImpl.setValue(paramXMLAttributes.getValue(j));
        AttributePSVI localAttributePSVI = (AttributePSVI)paramXMLAttributes.getAugmentations(j).getItem("ATTRIBUTE_PSVI");
        if (localAttributePSVI != null)
        {
          if (fStorePSVI) {
            ((PSVIAttrNSImpl)localAttrImpl).setPSVI(localAttributePSVI);
          }
          Object localObject = localAttributePSVI.getMemberTypeDefinition();
          if (localObject == null)
          {
            localObject = localAttributePSVI.getTypeDefinition();
            if (localObject != null)
            {
              localAttrImpl.setType(localObject);
              if (((XSSimpleType)localObject).isIDType()) {
                ((ElementImpl)localElement).setIdAttributeNode(localAttrImpl, true);
              }
            }
          }
          else
          {
            localAttrImpl.setType(localObject);
            if (((XSSimpleType)localObject).isIDType()) {
              ((ElementImpl)localElement).setIdAttributeNode(localAttrImpl, true);
            }
          }
        }
        localAttrImpl.setSpecified(paramXMLAttributes.isSpecified(j));
        localElement.setAttributeNode(localAttrImpl);
      }
    }
    append(localElement);
    fCurrentNode = localElement;
    if (fFragmentRoot == null) {
      fFragmentRoot = localElement;
    }
  }
  
  public void emptyElement(QName paramQName, XMLAttributes paramXMLAttributes, Augmentations paramAugmentations)
    throws XNIException
  {
    startElement(paramQName, paramXMLAttributes, paramAugmentations);
    endElement(paramQName, paramAugmentations);
  }
  
  public void startGeneralEntity(String paramString1, XMLResourceIdentifier paramXMLResourceIdentifier, String paramString2, Augmentations paramAugmentations)
    throws XNIException
  {}
  
  public void textDecl(String paramString1, String paramString2, Augmentations paramAugmentations)
    throws XNIException
  {}
  
  public void endGeneralEntity(String paramString, Augmentations paramAugmentations)
    throws XNIException
  {}
  
  public void characters(XMLString paramXMLString, Augmentations paramAugmentations)
    throws XNIException
  {
    if (!fIgnoreChars) {
      append(fDocument.createTextNode(paramXMLString.toString()));
    }
  }
  
  public void ignorableWhitespace(XMLString paramXMLString, Augmentations paramAugmentations)
    throws XNIException
  {
    characters(paramXMLString, paramAugmentations);
  }
  
  public void endElement(QName paramQName, Augmentations paramAugmentations)
    throws XNIException
  {
    if ((paramAugmentations != null) && (fDocumentImpl != null))
    {
      ElementPSVI localElementPSVI = (ElementPSVI)paramAugmentations.getItem("ELEMENT_PSVI");
      if (localElementPSVI != null)
      {
        if (fStorePSVI) {
          ((PSVIElementNSImpl)fCurrentNode).setPSVI(localElementPSVI);
        }
        Object localObject = localElementPSVI.getMemberTypeDefinition();
        if (localObject == null) {
          localObject = localElementPSVI.getTypeDefinition();
        }
        ((ElementNSImpl)fCurrentNode).setType((XSTypeDefinition)localObject);
      }
    }
    if (fCurrentNode == fFragmentRoot)
    {
      fCurrentNode = null;
      fFragmentRoot = null;
      return;
    }
    fCurrentNode = fCurrentNode.getParentNode();
  }
  
  public void startCDATA(Augmentations paramAugmentations)
    throws XNIException
  {}
  
  public void endCDATA(Augmentations paramAugmentations)
    throws XNIException
  {}
  
  public void endDocument(Augmentations paramAugmentations)
    throws XNIException
  {
    int i = fTargetChildren.size();
    int j;
    if (fNextSibling == null) {
      for (j = 0; j < i; j++) {
        fTarget.appendChild((Node)fTargetChildren.get(j));
      }
    } else {
      for (j = 0; j < i; j++) {
        fTarget.insertBefore((Node)fTargetChildren.get(j), fNextSibling);
      }
    }
  }
  
  public void setDocumentSource(XMLDocumentSource paramXMLDocumentSource) {}
  
  public XMLDocumentSource getDocumentSource()
  {
    return null;
  }
  
  private void append(Node paramNode)
    throws XNIException
  {
    if (fCurrentNode != null)
    {
      fCurrentNode.appendChild(paramNode);
    }
    else
    {
      if ((kidOK[fTarget.getNodeType()] & 1 << paramNode.getNodeType()) == 0)
      {
        String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "HIERARCHY_REQUEST_ERR", null);
        throw new XNIException(str);
      }
      fTargetChildren.add(paramNode);
    }
  }
  
  static
  {
    kidOK[9] = 1410;
    kidOK[11] = (kidOK[6] = kidOK[5] = kidOK[1] = 'ƺ');
    kidOK[2] = 40;
    kidOK[10] = 0;
    kidOK[7] = 0;
    kidOK[8] = 0;
    kidOK[3] = 0;
    kidOK[4] = 0;
    kidOK[12] = 0;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\jaxp\validation\DOMResultBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
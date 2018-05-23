package com.sun.org.apache.xerces.internal.dom;

import com.sun.org.apache.xerces.internal.util.URI;
import com.sun.org.apache.xerces.internal.util.URI.MalformedURIException;
import com.sun.org.apache.xerces.internal.util.XML11Char;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.utils.ObjectFactory;
import com.sun.org.apache.xerces.internal.utils.SecuritySupport;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectInputStream.GetField;
import java.io.ObjectOutputStream;
import java.io.ObjectOutputStream.PutField;
import java.io.ObjectStreamField;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Entity;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Notation;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.w3c.dom.UserDataHandler;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

public class CoreDocumentImpl
  extends ParentNode
  implements Document
{
  static final long serialVersionUID = 0L;
  protected DocumentTypeImpl docType;
  protected ElementImpl docElement;
  transient NodeListCache fFreeNLCache;
  protected String encoding;
  protected String actualEncoding;
  protected String version;
  protected boolean standalone;
  protected String fDocumentURI;
  private Map<Node, Map<String, ParentNode.UserDataRecord>> nodeUserData;
  protected Map<String, Node> identifiers;
  transient DOMNormalizer domNormalizer = null;
  transient DOMConfigurationImpl fConfiguration = null;
  transient Object fXPathEvaluator = null;
  private static final int[] kidOK = new int[13];
  protected int changes = 0;
  protected boolean allowGrammarAccess;
  protected boolean errorChecking = true;
  protected boolean ancestorChecking = true;
  protected boolean xmlVersionChanged = false;
  private int documentNumber = 0;
  private int nodeCounter = 0;
  private Map<Node, Integer> nodeTable;
  private boolean xml11Version = false;
  private static final ObjectStreamField[] serialPersistentFields = { new ObjectStreamField("docType", DocumentTypeImpl.class), new ObjectStreamField("docElement", ElementImpl.class), new ObjectStreamField("fFreeNLCache", NodeListCache.class), new ObjectStreamField("encoding", String.class), new ObjectStreamField("actualEncoding", String.class), new ObjectStreamField("version", String.class), new ObjectStreamField("standalone", Boolean.TYPE), new ObjectStreamField("fDocumentURI", String.class), new ObjectStreamField("userData", Hashtable.class), new ObjectStreamField("identifiers", Hashtable.class), new ObjectStreamField("changes", Integer.TYPE), new ObjectStreamField("allowGrammarAccess", Boolean.TYPE), new ObjectStreamField("errorChecking", Boolean.TYPE), new ObjectStreamField("ancestorChecking", Boolean.TYPE), new ObjectStreamField("xmlVersionChanged", Boolean.TYPE), new ObjectStreamField("documentNumber", Integer.TYPE), new ObjectStreamField("nodeCounter", Integer.TYPE), new ObjectStreamField("nodeTable", Hashtable.class), new ObjectStreamField("xml11Version", Boolean.TYPE) };
  
  public CoreDocumentImpl()
  {
    this(false);
  }
  
  public CoreDocumentImpl(boolean paramBoolean)
  {
    super(null);
    ownerDocument = this;
    allowGrammarAccess = paramBoolean;
    String str = SecuritySupport.getSystemProperty("http://java.sun.com/xml/dom/properties/ancestor-check");
    if ((str != null) && (str.equalsIgnoreCase("false"))) {
      ancestorChecking = false;
    }
  }
  
  public CoreDocumentImpl(DocumentType paramDocumentType)
  {
    this(paramDocumentType, false);
  }
  
  public CoreDocumentImpl(DocumentType paramDocumentType, boolean paramBoolean)
  {
    this(paramBoolean);
    if (paramDocumentType != null)
    {
      DocumentTypeImpl localDocumentTypeImpl;
      try
      {
        localDocumentTypeImpl = (DocumentTypeImpl)paramDocumentType;
      }
      catch (ClassCastException localClassCastException)
      {
        String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null);
        throw new DOMException((short)4, str);
      }
      ownerDocument = this;
      appendChild(paramDocumentType);
    }
  }
  
  public final Document getOwnerDocument()
  {
    return null;
  }
  
  public short getNodeType()
  {
    return 9;
  }
  
  public String getNodeName()
  {
    return "#document";
  }
  
  public Node cloneNode(boolean paramBoolean)
  {
    CoreDocumentImpl localCoreDocumentImpl = new CoreDocumentImpl();
    callUserDataHandlers(this, localCoreDocumentImpl, (short)1);
    cloneNode(localCoreDocumentImpl, paramBoolean);
    return localCoreDocumentImpl;
  }
  
  protected void cloneNode(CoreDocumentImpl paramCoreDocumentImpl, boolean paramBoolean)
  {
    if (needsSyncChildren()) {
      synchronizeChildren();
    }
    if (paramBoolean)
    {
      HashMap localHashMap = null;
      if (identifiers != null)
      {
        localHashMap = new HashMap(identifiers.size());
        localObject = identifiers.keySet().iterator();
        while (((Iterator)localObject).hasNext())
        {
          String str = (String)((Iterator)localObject).next();
          localHashMap.put(identifiers.get(str), str);
        }
      }
      for (Object localObject = firstChild; localObject != null; localObject = nextSibling) {
        paramCoreDocumentImpl.appendChild(paramCoreDocumentImpl.importNode((Node)localObject, true, true, localHashMap));
      }
    }
    allowGrammarAccess = allowGrammarAccess;
    errorChecking = errorChecking;
  }
  
  public Node insertBefore(Node paramNode1, Node paramNode2)
    throws DOMException
  {
    int i = paramNode1.getNodeType();
    if ((errorChecking) && (((i == 1) && (docElement != null)) || ((i == 10) && (docType != null))))
    {
      String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "HIERARCHY_REQUEST_ERR", null);
      throw new DOMException((short)3, str);
    }
    if ((paramNode1.getOwnerDocument() == null) && ((paramNode1 instanceof DocumentTypeImpl))) {
      ownerDocument = this;
    }
    super.insertBefore(paramNode1, paramNode2);
    if (i == 1) {
      docElement = ((ElementImpl)paramNode1);
    } else if (i == 10) {
      docType = ((DocumentTypeImpl)paramNode1);
    }
    return paramNode1;
  }
  
  public Node removeChild(Node paramNode)
    throws DOMException
  {
    super.removeChild(paramNode);
    int i = paramNode.getNodeType();
    if (i == 1) {
      docElement = null;
    } else if (i == 10) {
      docType = null;
    }
    return paramNode;
  }
  
  public Node replaceChild(Node paramNode1, Node paramNode2)
    throws DOMException
  {
    if ((paramNode1.getOwnerDocument() == null) && ((paramNode1 instanceof DocumentTypeImpl))) {
      ownerDocument = this;
    }
    if ((errorChecking) && (((docType != null) && (paramNode2.getNodeType() != 10) && (paramNode1.getNodeType() == 10)) || ((docElement != null) && (paramNode2.getNodeType() != 1) && (paramNode1.getNodeType() == 1)))) {
      throw new DOMException((short)3, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "HIERARCHY_REQUEST_ERR", null));
    }
    super.replaceChild(paramNode1, paramNode2);
    int i = paramNode2.getNodeType();
    if (i == 1) {
      docElement = ((ElementImpl)paramNode1);
    } else if (i == 10) {
      docType = ((DocumentTypeImpl)paramNode1);
    }
    return paramNode2;
  }
  
  public String getTextContent()
    throws DOMException
  {
    return null;
  }
  
  public void setTextContent(String paramString)
    throws DOMException
  {}
  
  public Object getFeature(String paramString1, String paramString2)
  {
    int i = (paramString2 == null) || (paramString2.length() == 0) ? 1 : 0;
    if ((paramString1.equalsIgnoreCase("+XPath")) && ((i != 0) || (paramString2.equals("3.0"))))
    {
      if (fXPathEvaluator != null) {
        return fXPathEvaluator;
      }
      try
      {
        Class localClass = ObjectFactory.findProviderClass("com.sun.org.apache.xpath.internal.domapi.XPathEvaluatorImpl", true);
        Constructor localConstructor = localClass.getConstructor(new Class[] { Document.class });
        Class[] arrayOfClass = localClass.getInterfaces();
        for (int j = 0; j < arrayOfClass.length; j++) {
          if (arrayOfClass[j].getName().equals("org.w3c.dom.xpath.XPathEvaluator"))
          {
            fXPathEvaluator = localConstructor.newInstance(new Object[] { this });
            return fXPathEvaluator;
          }
        }
        return null;
      }
      catch (Exception localException)
      {
        return null;
      }
    }
    return super.getFeature(paramString1, paramString2);
  }
  
  public Attr createAttribute(String paramString)
    throws DOMException
  {
    if ((errorChecking) && (!isXMLName(paramString, xml11Version)))
    {
      String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null);
      throw new DOMException((short)5, str);
    }
    return new AttrImpl(this, paramString);
  }
  
  public CDATASection createCDATASection(String paramString)
    throws DOMException
  {
    return new CDATASectionImpl(this, paramString);
  }
  
  public Comment createComment(String paramString)
  {
    return new CommentImpl(this, paramString);
  }
  
  public DocumentFragment createDocumentFragment()
  {
    return new DocumentFragmentImpl(this);
  }
  
  public Element createElement(String paramString)
    throws DOMException
  {
    if ((errorChecking) && (!isXMLName(paramString, xml11Version)))
    {
      String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null);
      throw new DOMException((short)5, str);
    }
    return new ElementImpl(this, paramString);
  }
  
  public EntityReference createEntityReference(String paramString)
    throws DOMException
  {
    if ((errorChecking) && (!isXMLName(paramString, xml11Version)))
    {
      String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null);
      throw new DOMException((short)5, str);
    }
    return new EntityReferenceImpl(this, paramString);
  }
  
  public ProcessingInstruction createProcessingInstruction(String paramString1, String paramString2)
    throws DOMException
  {
    if ((errorChecking) && (!isXMLName(paramString1, xml11Version)))
    {
      String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null);
      throw new DOMException((short)5, str);
    }
    return new ProcessingInstructionImpl(this, paramString1, paramString2);
  }
  
  public Text createTextNode(String paramString)
  {
    return new TextImpl(this, paramString);
  }
  
  public DocumentType getDoctype()
  {
    if (needsSyncChildren()) {
      synchronizeChildren();
    }
    return docType;
  }
  
  public Element getDocumentElement()
  {
    if (needsSyncChildren()) {
      synchronizeChildren();
    }
    return docElement;
  }
  
  public NodeList getElementsByTagName(String paramString)
  {
    return new DeepNodeListImpl(this, paramString);
  }
  
  public DOMImplementation getImplementation()
  {
    return CoreDOMImplementationImpl.getDOMImplementation();
  }
  
  public void setErrorChecking(boolean paramBoolean)
  {
    errorChecking = paramBoolean;
  }
  
  public void setStrictErrorChecking(boolean paramBoolean)
  {
    errorChecking = paramBoolean;
  }
  
  public boolean getErrorChecking()
  {
    return errorChecking;
  }
  
  public boolean getStrictErrorChecking()
  {
    return errorChecking;
  }
  
  public String getInputEncoding()
  {
    return actualEncoding;
  }
  
  public void setInputEncoding(String paramString)
  {
    actualEncoding = paramString;
  }
  
  public void setXmlEncoding(String paramString)
  {
    encoding = paramString;
  }
  
  /**
   * @deprecated
   */
  public void setEncoding(String paramString)
  {
    setXmlEncoding(paramString);
  }
  
  public String getXmlEncoding()
  {
    return encoding;
  }
  
  /**
   * @deprecated
   */
  public String getEncoding()
  {
    return getXmlEncoding();
  }
  
  public void setXmlVersion(String paramString)
  {
    if ((paramString.equals("1.0")) || (paramString.equals("1.1")))
    {
      if (!getXmlVersion().equals(paramString))
      {
        xmlVersionChanged = true;
        isNormalized(false);
        version = paramString;
      }
    }
    else
    {
      String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null);
      throw new DOMException((short)9, str);
    }
    if (getXmlVersion().equals("1.1")) {
      xml11Version = true;
    } else {
      xml11Version = false;
    }
  }
  
  /**
   * @deprecated
   */
  public void setVersion(String paramString)
  {
    setXmlVersion(paramString);
  }
  
  public String getXmlVersion()
  {
    return version == null ? "1.0" : version;
  }
  
  /**
   * @deprecated
   */
  public String getVersion()
  {
    return getXmlVersion();
  }
  
  public void setXmlStandalone(boolean paramBoolean)
    throws DOMException
  {
    standalone = paramBoolean;
  }
  
  /**
   * @deprecated
   */
  public void setStandalone(boolean paramBoolean)
  {
    setXmlStandalone(paramBoolean);
  }
  
  public boolean getXmlStandalone()
  {
    return standalone;
  }
  
  /**
   * @deprecated
   */
  public boolean getStandalone()
  {
    return getXmlStandalone();
  }
  
  public String getDocumentURI()
  {
    return fDocumentURI;
  }
  
  public Node renameNode(Node paramNode, String paramString1, String paramString2)
    throws DOMException
  {
    if ((errorChecking) && (paramNode.getOwnerDocument() != this) && (paramNode != this))
    {
      localObject1 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null);
      throw new DOMException((short)4, (String)localObject1);
    }
    Object localObject3;
    Object localObject2;
    Object localObject4;
    Node localNode1;
    switch (paramNode.getNodeType())
    {
    case 1: 
      localObject1 = (ElementImpl)paramNode;
      if ((localObject1 instanceof ElementNSImpl))
      {
        ((ElementNSImpl)localObject1).rename(paramString1, paramString2);
        callUserDataHandlers((Node)localObject1, null, (short)4);
      }
      else if (paramString1 == null)
      {
        if (errorChecking)
        {
          int i = paramString2.indexOf(':');
          if (i != -1)
          {
            localObject3 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null);
            throw new DOMException((short)14, (String)localObject3);
          }
          if (!isXMLName(paramString2, xml11Version))
          {
            localObject3 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null);
            throw new DOMException((short)5, (String)localObject3);
          }
        }
        ((ElementImpl)localObject1).rename(paramString2);
        callUserDataHandlers((Node)localObject1, null, (short)4);
      }
      else
      {
        localObject2 = new ElementNSImpl(this, paramString1, paramString2);
        copyEventListeners((NodeImpl)localObject1, (NodeImpl)localObject2);
        localObject3 = removeUserDataTable((Node)localObject1);
        localObject4 = ((ElementImpl)localObject1).getParentNode();
        localNode1 = ((ElementImpl)localObject1).getNextSibling();
        if (localObject4 != null) {
          ((Node)localObject4).removeChild((Node)localObject1);
        }
        for (Node localNode2 = ((ElementImpl)localObject1).getFirstChild(); localNode2 != null; localNode2 = ((ElementImpl)localObject1).getFirstChild())
        {
          ((ElementImpl)localObject1).removeChild(localNode2);
          ((ElementNSImpl)localObject2).appendChild(localNode2);
        }
        ((ElementNSImpl)localObject2).moveSpecifiedAttributes((ElementImpl)localObject1);
        setUserDataTable((Node)localObject2, (Map)localObject3);
        callUserDataHandlers((Node)localObject1, (Node)localObject2, (short)4);
        if (localObject4 != null) {
          ((Node)localObject4).insertBefore((Node)localObject2, localNode1);
        }
        localObject1 = localObject2;
      }
      renamedElement((Element)paramNode, (Element)localObject1);
      return (Node)localObject1;
    case 2: 
      localObject1 = (AttrImpl)paramNode;
      localObject2 = ((AttrImpl)localObject1).getOwnerElement();
      if (localObject2 != null) {
        ((Element)localObject2).removeAttributeNode((Attr)localObject1);
      }
      if ((paramNode instanceof AttrNSImpl))
      {
        ((AttrNSImpl)localObject1).rename(paramString1, paramString2);
        if (localObject2 != null) {
          ((Element)localObject2).setAttributeNodeNS((Attr)localObject1);
        }
        callUserDataHandlers((Node)localObject1, null, (short)4);
      }
      else if (paramString1 == null)
      {
        ((AttrImpl)localObject1).rename(paramString2);
        if (localObject2 != null) {
          ((Element)localObject2).setAttributeNode((Attr)localObject1);
        }
        callUserDataHandlers((Node)localObject1, null, (short)4);
      }
      else
      {
        localObject3 = new AttrNSImpl(this, paramString1, paramString2);
        copyEventListeners((NodeImpl)localObject1, (NodeImpl)localObject3);
        localObject4 = removeUserDataTable((Node)localObject1);
        for (localNode1 = ((AttrImpl)localObject1).getFirstChild(); localNode1 != null; localNode1 = ((AttrImpl)localObject1).getFirstChild())
        {
          ((AttrImpl)localObject1).removeChild(localNode1);
          ((AttrNSImpl)localObject3).appendChild(localNode1);
        }
        setUserDataTable((Node)localObject3, (Map)localObject4);
        callUserDataHandlers((Node)localObject1, (Node)localObject3, (short)4);
        if (localObject2 != null) {
          ((Element)localObject2).setAttributeNode((Attr)localObject3);
        }
        localObject1 = localObject3;
      }
      renamedAttrNode((Attr)paramNode, (Attr)localObject1);
      return (Node)localObject1;
    }
    Object localObject1 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null);
    throw new DOMException((short)9, (String)localObject1);
  }
  
  public void normalizeDocument()
  {
    if ((isNormalized()) && (!isNormalizeDocRequired())) {
      return;
    }
    if (needsSyncChildren()) {
      synchronizeChildren();
    }
    if (domNormalizer == null) {
      domNormalizer = new DOMNormalizer();
    }
    if (fConfiguration == null) {
      fConfiguration = new DOMConfigurationImpl();
    } else {
      fConfiguration.reset();
    }
    domNormalizer.normalizeDocument(this, fConfiguration);
    isNormalized(true);
    xmlVersionChanged = false;
  }
  
  public DOMConfiguration getDomConfig()
  {
    if (fConfiguration == null) {
      fConfiguration = new DOMConfigurationImpl();
    }
    return fConfiguration;
  }
  
  public String getBaseURI()
  {
    if ((fDocumentURI != null) && (fDocumentURI.length() != 0)) {
      try
      {
        return new URI(fDocumentURI).toString();
      }
      catch (URI.MalformedURIException localMalformedURIException)
      {
        return null;
      }
    }
    return fDocumentURI;
  }
  
  public void setDocumentURI(String paramString)
  {
    fDocumentURI = paramString;
  }
  
  public boolean getAsync()
  {
    return false;
  }
  
  public void setAsync(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null);
      throw new DOMException((short)9, str);
    }
  }
  
  public void abort() {}
  
  public boolean load(String paramString)
  {
    return false;
  }
  
  public boolean loadXML(String paramString)
  {
    return false;
  }
  
  public String saveXML(Node paramNode)
    throws DOMException
  {
    if ((errorChecking) && (paramNode != null) && (this != paramNode.getOwnerDocument()))
    {
      localObject = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null);
      throw new DOMException((short)4, (String)localObject);
    }
    Object localObject = (DOMImplementationLS)DOMImplementationImpl.getDOMImplementation();
    LSSerializer localLSSerializer = ((DOMImplementationLS)localObject).createLSSerializer();
    if (paramNode == null) {
      paramNode = this;
    }
    return localLSSerializer.writeToString(paramNode);
  }
  
  void setMutationEvents(boolean paramBoolean) {}
  
  boolean getMutationEvents()
  {
    return false;
  }
  
  public DocumentType createDocumentType(String paramString1, String paramString2, String paramString3)
    throws DOMException
  {
    return new DocumentTypeImpl(this, paramString1, paramString2, paramString3);
  }
  
  public Entity createEntity(String paramString)
    throws DOMException
  {
    if ((errorChecking) && (!isXMLName(paramString, xml11Version)))
    {
      String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null);
      throw new DOMException((short)5, str);
    }
    return new EntityImpl(this, paramString);
  }
  
  public Notation createNotation(String paramString)
    throws DOMException
  {
    if ((errorChecking) && (!isXMLName(paramString, xml11Version)))
    {
      String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null);
      throw new DOMException((short)5, str);
    }
    return new NotationImpl(this, paramString);
  }
  
  public ElementDefinitionImpl createElementDefinition(String paramString)
    throws DOMException
  {
    if ((errorChecking) && (!isXMLName(paramString, xml11Version)))
    {
      String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null);
      throw new DOMException((short)5, str);
    }
    return new ElementDefinitionImpl(this, paramString);
  }
  
  protected int getNodeNumber()
  {
    if (documentNumber == 0)
    {
      CoreDOMImplementationImpl localCoreDOMImplementationImpl = (CoreDOMImplementationImpl)CoreDOMImplementationImpl.getDOMImplementation();
      documentNumber = localCoreDOMImplementationImpl.assignDocumentNumber();
    }
    return documentNumber;
  }
  
  protected int getNodeNumber(Node paramNode)
  {
    int i;
    if (nodeTable == null)
    {
      nodeTable = new HashMap();
      i = --nodeCounter;
      nodeTable.put(paramNode, new Integer(i));
    }
    else
    {
      Integer localInteger = (Integer)nodeTable.get(paramNode);
      if (localInteger == null)
      {
        i = --nodeCounter;
        nodeTable.put(paramNode, Integer.valueOf(i));
      }
      else
      {
        i = localInteger.intValue();
      }
    }
    return i;
  }
  
  public Node importNode(Node paramNode, boolean paramBoolean)
    throws DOMException
  {
    return importNode(paramNode, paramBoolean, false, null);
  }
  
  private Node importNode(Node paramNode, boolean paramBoolean1, boolean paramBoolean2, Map<Node, String> paramMap)
    throws DOMException
  {
    Object localObject1 = null;
    Map localMap = null;
    if ((paramNode instanceof NodeImpl)) {
      localMap = ((NodeImpl)paramNode).getUserDataRecord();
    }
    int i = paramNode.getNodeType();
    Object localObject2;
    NamedNodeMap localNamedNodeMap;
    int k;
    Object localObject4;
    Object localObject3;
    switch (i)
    {
    case 1: 
      boolean bool = paramNode.getOwnerDocument().getImplementation().hasFeature("XML", "2.0");
      if ((!bool) || (paramNode.getLocalName() == null)) {
        localObject2 = createElement(paramNode.getNodeName());
      } else {
        localObject2 = createElementNS(paramNode.getNamespaceURI(), paramNode.getNodeName());
      }
      localNamedNodeMap = paramNode.getAttributes();
      if (localNamedNodeMap != null)
      {
        int j = localNamedNodeMap.getLength();
        for (k = 0; k < j; k++)
        {
          Attr localAttr1 = (Attr)localNamedNodeMap.item(k);
          if ((localAttr1.getSpecified()) || (paramBoolean2))
          {
            Attr localAttr2 = (Attr)importNode(localAttr1, true, paramBoolean2, paramMap);
            if ((!bool) || (localAttr1.getLocalName() == null)) {
              ((Element)localObject2).setAttributeNode(localAttr2);
            } else {
              ((Element)localObject2).setAttributeNodeNS(localAttr2);
            }
          }
        }
      }
      if (paramMap != null)
      {
        localObject4 = (String)paramMap.get(paramNode);
        if (localObject4 != null)
        {
          if (identifiers == null) {
            identifiers = new HashMap();
          }
          identifiers.put(localObject4, localObject2);
        }
      }
      localObject1 = localObject2;
      break;
    case 2: 
      if (paramNode.getOwnerDocument().getImplementation().hasFeature("XML", "2.0"))
      {
        if (paramNode.getLocalName() == null) {
          localObject1 = createAttribute(paramNode.getNodeName());
        } else {
          localObject1 = createAttributeNS(paramNode.getNamespaceURI(), paramNode.getNodeName());
        }
      }
      else {
        localObject1 = createAttribute(paramNode.getNodeName());
      }
      if ((paramNode instanceof AttrImpl))
      {
        localObject2 = (AttrImpl)paramNode;
        if (((AttrImpl)localObject2).hasStringValue())
        {
          localObject3 = (AttrImpl)localObject1;
          ((AttrImpl)localObject3).setValue(((AttrImpl)localObject2).getValue());
          paramBoolean1 = false;
        }
        else
        {
          paramBoolean1 = true;
        }
      }
      else if (paramNode.getFirstChild() == null)
      {
        ((Node)localObject1).setNodeValue(paramNode.getNodeValue());
        paramBoolean1 = false;
      }
      else
      {
        paramBoolean1 = true;
      }
      break;
    case 3: 
      localObject1 = createTextNode(paramNode.getNodeValue());
      break;
    case 4: 
      localObject1 = createCDATASection(paramNode.getNodeValue());
      break;
    case 5: 
      localObject1 = createEntityReference(paramNode.getNodeName());
      paramBoolean1 = false;
      break;
    case 6: 
      localObject2 = (Entity)paramNode;
      localObject3 = (EntityImpl)createEntity(paramNode.getNodeName());
      ((EntityImpl)localObject3).setPublicId(((Entity)localObject2).getPublicId());
      ((EntityImpl)localObject3).setSystemId(((Entity)localObject2).getSystemId());
      ((EntityImpl)localObject3).setNotationName(((Entity)localObject2).getNotationName());
      ((EntityImpl)localObject3).isReadOnly(false);
      localObject1 = localObject3;
      break;
    case 7: 
      localObject1 = createProcessingInstruction(paramNode.getNodeName(), paramNode.getNodeValue());
      break;
    case 8: 
      localObject1 = createComment(paramNode.getNodeValue());
      break;
    case 10: 
      if (!paramBoolean2)
      {
        localObject2 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null);
        throw new DOMException((short)9, (String)localObject2);
      }
      localObject2 = (DocumentType)paramNode;
      localObject3 = (DocumentTypeImpl)createDocumentType(((DocumentType)localObject2).getNodeName(), ((DocumentType)localObject2).getPublicId(), ((DocumentType)localObject2).getSystemId());
      localNamedNodeMap = ((DocumentType)localObject2).getEntities();
      localObject4 = ((DocumentTypeImpl)localObject3).getEntities();
      if (localNamedNodeMap != null) {
        for (k = 0; k < localNamedNodeMap.getLength(); k++) {
          ((NamedNodeMap)localObject4).setNamedItem(importNode(localNamedNodeMap.item(k), true, true, paramMap));
        }
      }
      localNamedNodeMap = ((DocumentType)localObject2).getNotations();
      localObject4 = ((DocumentTypeImpl)localObject3).getNotations();
      if (localNamedNodeMap != null) {
        for (k = 0; k < localNamedNodeMap.getLength(); k++) {
          ((NamedNodeMap)localObject4).setNamedItem(importNode(localNamedNodeMap.item(k), true, true, paramMap));
        }
      }
      localObject1 = localObject3;
      break;
    case 11: 
      localObject1 = createDocumentFragment();
      break;
    case 12: 
      localObject2 = (Notation)paramNode;
      localObject3 = (NotationImpl)createNotation(paramNode.getNodeName());
      ((NotationImpl)localObject3).setPublicId(((Notation)localObject2).getPublicId());
      ((NotationImpl)localObject3).setSystemId(((Notation)localObject2).getSystemId());
      localObject1 = localObject3;
      break;
    case 9: 
    default: 
      localObject2 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null);
      throw new DOMException((short)9, (String)localObject2);
    }
    if (localMap != null) {
      callUserDataHandlers(paramNode, (Node)localObject1, (short)2, localMap);
    }
    if (paramBoolean1) {
      for (localObject2 = paramNode.getFirstChild(); localObject2 != null; localObject2 = ((Node)localObject2).getNextSibling()) {
        ((Node)localObject1).appendChild(importNode((Node)localObject2, true, paramBoolean2, paramMap));
      }
    }
    if (((Node)localObject1).getNodeType() == 6) {
      ((NodeImpl)localObject1).setReadOnly(true, true);
    }
    return (Node)localObject1;
  }
  
  public Node adoptNode(Node paramNode)
  {
    NodeImpl localNodeImpl;
    try
    {
      localNodeImpl = (NodeImpl)paramNode;
    }
    catch (ClassCastException localClassCastException)
    {
      return null;
    }
    if (paramNode == null) {
      return null;
    }
    Object localObject1;
    Object localObject2;
    if (paramNode.getOwnerDocument() != null)
    {
      localObject1 = getImplementation();
      localObject2 = paramNode.getOwnerDocument().getImplementation();
      if (localObject1 != localObject2) {
        if (((localObject1 instanceof DOMImplementationImpl)) && ((localObject2 instanceof DeferredDOMImplementationImpl))) {
          undeferChildren(localNodeImpl);
        } else if ((!(localObject1 instanceof DeferredDOMImplementationImpl)) || (!(localObject2 instanceof DOMImplementationImpl))) {
          return null;
        }
      }
    }
    Map localMap;
    switch (localNodeImpl.getNodeType())
    {
    case 2: 
      localObject1 = (AttrImpl)localNodeImpl;
      if (((AttrImpl)localObject1).getOwnerElement() != null) {
        ((AttrImpl)localObject1).getOwnerElement().removeAttributeNode((Attr)localObject1);
      }
      ((AttrImpl)localObject1).isSpecified(true);
      localMap = localNodeImpl.getUserDataRecord();
      ((AttrImpl)localObject1).setOwnerDocument(this);
      if (localMap != null) {
        setUserDataTable(localNodeImpl, localMap);
      }
      break;
    case 6: 
    case 12: 
      localObject1 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null);
      throw new DOMException((short)7, (String)localObject1);
    case 9: 
    case 10: 
      localObject1 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null);
      throw new DOMException((short)9, (String)localObject1);
    case 5: 
      localMap = localNodeImpl.getUserDataRecord();
      localObject1 = localNodeImpl.getParentNode();
      if (localObject1 != null) {
        ((Node)localObject1).removeChild(paramNode);
      }
      while ((localObject2 = localNodeImpl.getFirstChild()) != null) {
        localNodeImpl.removeChild((Node)localObject2);
      }
      localNodeImpl.setOwnerDocument(this);
      if (localMap != null) {
        setUserDataTable(localNodeImpl, localMap);
      }
      if (docType != null)
      {
        NamedNodeMap localNamedNodeMap = docType.getEntities();
        Node localNode1 = localNamedNodeMap.getNamedItem(localNodeImpl.getNodeName());
        if (localNode1 != null) {
          localObject2 = localNode1.getFirstChild();
        }
      }
      break;
    case 1: 
    case 3: 
    case 4: 
    case 7: 
    case 8: 
    case 11: 
    default: 
      while (localObject2 != null)
      {
        Node localNode2 = ((Node)localObject2).cloneNode(true);
        localNodeImpl.appendChild(localNode2);
        localObject2 = ((Node)localObject2).getNextSibling();
        continue;
        localMap = localNodeImpl.getUserDataRecord();
        localObject1 = localNodeImpl.getParentNode();
        if (localObject1 != null) {
          ((Node)localObject1).removeChild(paramNode);
        }
        localNodeImpl.setOwnerDocument(this);
        if (localMap != null) {
          setUserDataTable(localNodeImpl, localMap);
        }
        ((ElementImpl)localNodeImpl).reconcileDefaultAttributes();
        break;
        localMap = localNodeImpl.getUserDataRecord();
        localObject1 = localNodeImpl.getParentNode();
        if (localObject1 != null) {
          ((Node)localObject1).removeChild(paramNode);
        }
        localNodeImpl.setOwnerDocument(this);
        if (localMap != null) {
          setUserDataTable(localNodeImpl, localMap);
        }
      }
    }
    if (localMap != null) {
      callUserDataHandlers(paramNode, null, (short)5, localMap);
    }
    return localNodeImpl;
  }
  
  protected void undeferChildren(Node paramNode)
  {
    Node localNode1 = paramNode;
    while (null != paramNode)
    {
      if (((NodeImpl)paramNode).needsSyncData()) {
        ((NodeImpl)paramNode).synchronizeData();
      }
      NamedNodeMap localNamedNodeMap = paramNode.getAttributes();
      if (localNamedNodeMap != null)
      {
        int i = localNamedNodeMap.getLength();
        for (int j = 0; j < i; j++) {
          undeferChildren(localNamedNodeMap.item(j));
        }
      }
      Node localNode2 = null;
      localNode2 = paramNode.getFirstChild();
      while ((null == localNode2) && (!localNode1.equals(paramNode)))
      {
        localNode2 = paramNode.getNextSibling();
        if (null == localNode2)
        {
          paramNode = paramNode.getParentNode();
          if ((null == paramNode) || (localNode1.equals(paramNode))) {
            localNode2 = null;
          }
        }
      }
      paramNode = localNode2;
    }
  }
  
  public Element getElementById(String paramString)
  {
    return getIdentifier(paramString);
  }
  
  protected final void clearIdentifiers()
  {
    if (identifiers != null) {
      identifiers.clear();
    }
  }
  
  public void putIdentifier(String paramString, Element paramElement)
  {
    if (paramElement == null)
    {
      removeIdentifier(paramString);
      return;
    }
    if (needsSyncData()) {
      synchronizeData();
    }
    if (identifiers == null) {
      identifiers = new HashMap();
    }
    identifiers.put(paramString, paramElement);
  }
  
  public Element getIdentifier(String paramString)
  {
    if (needsSyncData()) {
      synchronizeData();
    }
    if (identifiers == null) {
      return null;
    }
    Element localElement = (Element)identifiers.get(paramString);
    if (localElement != null) {
      for (Node localNode = localElement.getParentNode(); localNode != null; localNode = localNode.getParentNode()) {
        if (localNode == this) {
          return localElement;
        }
      }
    }
    return null;
  }
  
  public void removeIdentifier(String paramString)
  {
    if (needsSyncData()) {
      synchronizeData();
    }
    if (identifiers == null) {
      return;
    }
    identifiers.remove(paramString);
  }
  
  public Element createElementNS(String paramString1, String paramString2)
    throws DOMException
  {
    return new ElementNSImpl(this, paramString1, paramString2);
  }
  
  public Element createElementNS(String paramString1, String paramString2, String paramString3)
    throws DOMException
  {
    return new ElementNSImpl(this, paramString1, paramString2, paramString3);
  }
  
  public Attr createAttributeNS(String paramString1, String paramString2)
    throws DOMException
  {
    return new AttrNSImpl(this, paramString1, paramString2);
  }
  
  public Attr createAttributeNS(String paramString1, String paramString2, String paramString3)
    throws DOMException
  {
    return new AttrNSImpl(this, paramString1, paramString2, paramString3);
  }
  
  public NodeList getElementsByTagNameNS(String paramString1, String paramString2)
  {
    return new DeepNodeListImpl(this, paramString1, paramString2);
  }
  
  public Object clone()
    throws CloneNotSupportedException
  {
    CoreDocumentImpl localCoreDocumentImpl = (CoreDocumentImpl)super.clone();
    docType = null;
    docElement = null;
    return localCoreDocumentImpl;
  }
  
  public static final boolean isXMLName(String paramString, boolean paramBoolean)
  {
    if (paramString == null) {
      return false;
    }
    if (!paramBoolean) {
      return XMLChar.isValidName(paramString);
    }
    return XML11Char.isXML11ValidName(paramString);
  }
  
  public static final boolean isValidQName(String paramString1, String paramString2, boolean paramBoolean)
  {
    if (paramString2 == null) {
      return false;
    }
    boolean bool = false;
    if (!paramBoolean) {
      bool = ((paramString1 == null) || (XMLChar.isValidNCName(paramString1))) && (XMLChar.isValidNCName(paramString2));
    } else {
      bool = ((paramString1 == null) || (XML11Char.isXML11ValidNCName(paramString1))) && (XML11Char.isXML11ValidNCName(paramString2));
    }
    return bool;
  }
  
  protected boolean isKidOK(Node paramNode1, Node paramNode2)
  {
    if ((allowGrammarAccess) && (paramNode1.getNodeType() == 10)) {
      return paramNode2.getNodeType() == 1;
    }
    return 0 != (kidOK[paramNode1.getNodeType()] & 1 << paramNode2.getNodeType());
  }
  
  protected void changed()
  {
    changes += 1;
  }
  
  protected int changes()
  {
    return changes;
  }
  
  NodeListCache getNodeListCache(ParentNode paramParentNode)
  {
    if (fFreeNLCache == null) {
      return new NodeListCache(paramParentNode);
    }
    NodeListCache localNodeListCache = fFreeNLCache;
    fFreeNLCache = fFreeNLCache.next;
    fChild = null;
    fChildIndex = -1;
    fLength = -1;
    if (fOwner != null) {
      fOwner.fNodeListCache = null;
    }
    fOwner = paramParentNode;
    return localNodeListCache;
  }
  
  void freeNodeListCache(NodeListCache paramNodeListCache)
  {
    next = fFreeNLCache;
    fFreeNLCache = paramNodeListCache;
  }
  
  public Object setUserData(Node paramNode, String paramString, Object paramObject, UserDataHandler paramUserDataHandler)
  {
    Object localObject;
    if (paramObject == null)
    {
      if (nodeUserData != null)
      {
        localObject = (Map)nodeUserData.get(paramNode);
        if (localObject != null)
        {
          localUserDataRecord = (ParentNode.UserDataRecord)((Map)localObject).remove(paramString);
          if (localUserDataRecord != null) {
            return fData;
          }
        }
      }
      return null;
    }
    if (nodeUserData == null)
    {
      nodeUserData = new HashMap();
      localObject = new HashMap();
      nodeUserData.put(paramNode, localObject);
    }
    else
    {
      localObject = (Map)nodeUserData.get(paramNode);
      if (localObject == null)
      {
        localObject = new HashMap();
        nodeUserData.put(paramNode, localObject);
      }
    }
    ParentNode.UserDataRecord localUserDataRecord = (ParentNode.UserDataRecord)((Map)localObject).put(paramString, new ParentNode.UserDataRecord(this, paramObject, paramUserDataHandler));
    if (localUserDataRecord != null) {
      return fData;
    }
    return null;
  }
  
  public Object getUserData(Node paramNode, String paramString)
  {
    if (nodeUserData == null) {
      return null;
    }
    Map localMap = (Map)nodeUserData.get(paramNode);
    if (localMap == null) {
      return null;
    }
    ParentNode.UserDataRecord localUserDataRecord = (ParentNode.UserDataRecord)localMap.get(paramString);
    if (localUserDataRecord != null) {
      return fData;
    }
    return null;
  }
  
  protected Map<String, ParentNode.UserDataRecord> getUserDataRecord(Node paramNode)
  {
    if (nodeUserData == null) {
      return null;
    }
    Map localMap = (Map)nodeUserData.get(paramNode);
    if (localMap == null) {
      return null;
    }
    return localMap;
  }
  
  Map<String, ParentNode.UserDataRecord> removeUserDataTable(Node paramNode)
  {
    if (nodeUserData == null) {
      return null;
    }
    return (Map)nodeUserData.get(paramNode);
  }
  
  void setUserDataTable(Node paramNode, Map<String, ParentNode.UserDataRecord> paramMap)
  {
    if (nodeUserData == null) {
      nodeUserData = new HashMap();
    }
    if (paramMap != null) {
      nodeUserData.put(paramNode, paramMap);
    }
  }
  
  void callUserDataHandlers(Node paramNode1, Node paramNode2, short paramShort)
  {
    if (nodeUserData == null) {
      return;
    }
    if ((paramNode1 instanceof NodeImpl))
    {
      Map localMap = ((NodeImpl)paramNode1).getUserDataRecord();
      if ((localMap == null) || (localMap.isEmpty())) {
        return;
      }
      callUserDataHandlers(paramNode1, paramNode2, paramShort, localMap);
    }
  }
  
  void callUserDataHandlers(Node paramNode1, Node paramNode2, short paramShort, Map<String, ParentNode.UserDataRecord> paramMap)
  {
    if ((paramMap == null) || (paramMap.isEmpty())) {
      return;
    }
    Iterator localIterator = paramMap.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      ParentNode.UserDataRecord localUserDataRecord = (ParentNode.UserDataRecord)paramMap.get(str);
      if (fHandler != null) {
        fHandler.handle(paramShort, str, fData, paramNode1, paramNode2);
      }
    }
  }
  
  protected final void checkNamespaceWF(String paramString, int paramInt1, int paramInt2)
  {
    if (!errorChecking) {
      return;
    }
    if ((paramInt1 == 0) || (paramInt1 == paramString.length() - 1) || (paramInt2 != paramInt1))
    {
      String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null);
      throw new DOMException((short)14, str);
    }
  }
  
  protected final void checkDOMNSErr(String paramString1, String paramString2)
  {
    if (errorChecking)
    {
      String str;
      if (paramString2 == null)
      {
        str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null);
        throw new DOMException((short)14, str);
      }
      if ((paramString1.equals("xml")) && (!paramString2.equals(NamespaceContext.XML_URI)))
      {
        str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null);
        throw new DOMException((short)14, str);
      }
      if (((paramString1.equals("xmlns")) && (!paramString2.equals(NamespaceContext.XMLNS_URI))) || ((!paramString1.equals("xmlns")) && (paramString2.equals(NamespaceContext.XMLNS_URI))))
      {
        str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null);
        throw new DOMException((short)14, str);
      }
    }
  }
  
  protected final void checkQName(String paramString1, String paramString2)
  {
    if (!errorChecking) {
      return;
    }
    int i = 0;
    if (!xml11Version) {
      i = ((paramString1 == null) || (XMLChar.isValidNCName(paramString1))) && (XMLChar.isValidNCName(paramString2)) ? 1 : 0;
    } else {
      i = ((paramString1 == null) || (XML11Char.isXML11ValidNCName(paramString1))) && (XML11Char.isXML11ValidNCName(paramString2)) ? 1 : 0;
    }
    if (i == 0)
    {
      String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null);
      throw new DOMException((short)5, str);
    }
  }
  
  boolean isXML11Version()
  {
    return xml11Version;
  }
  
  boolean isNormalizeDocRequired()
  {
    return true;
  }
  
  boolean isXMLVersionChanged()
  {
    return xmlVersionChanged;
  }
  
  protected void setUserData(NodeImpl paramNodeImpl, Object paramObject)
  {
    setUserData(paramNodeImpl, "XERCES1DOMUSERDATA", paramObject, null);
  }
  
  protected Object getUserData(NodeImpl paramNodeImpl)
  {
    return getUserData(paramNodeImpl, "XERCES1DOMUSERDATA");
  }
  
  protected void addEventListener(NodeImpl paramNodeImpl, String paramString, EventListener paramEventListener, boolean paramBoolean) {}
  
  protected void removeEventListener(NodeImpl paramNodeImpl, String paramString, EventListener paramEventListener, boolean paramBoolean) {}
  
  protected void copyEventListeners(NodeImpl paramNodeImpl1, NodeImpl paramNodeImpl2) {}
  
  protected boolean dispatchEvent(NodeImpl paramNodeImpl, Event paramEvent)
  {
    return false;
  }
  
  void replacedText(NodeImpl paramNodeImpl) {}
  
  void deletedText(NodeImpl paramNodeImpl, int paramInt1, int paramInt2) {}
  
  void insertedText(NodeImpl paramNodeImpl, int paramInt1, int paramInt2) {}
  
  void modifyingCharacterData(NodeImpl paramNodeImpl, boolean paramBoolean) {}
  
  void modifiedCharacterData(NodeImpl paramNodeImpl, String paramString1, String paramString2, boolean paramBoolean) {}
  
  void insertingNode(NodeImpl paramNodeImpl, boolean paramBoolean) {}
  
  void insertedNode(NodeImpl paramNodeImpl1, NodeImpl paramNodeImpl2, boolean paramBoolean) {}
  
  void removingNode(NodeImpl paramNodeImpl1, NodeImpl paramNodeImpl2, boolean paramBoolean) {}
  
  void removedNode(NodeImpl paramNodeImpl, boolean paramBoolean) {}
  
  void replacingNode(NodeImpl paramNodeImpl) {}
  
  void replacedNode(NodeImpl paramNodeImpl) {}
  
  void replacingData(NodeImpl paramNodeImpl) {}
  
  void replacedCharacterData(NodeImpl paramNodeImpl, String paramString1, String paramString2) {}
  
  void modifiedAttrValue(AttrImpl paramAttrImpl, String paramString) {}
  
  void setAttrNode(AttrImpl paramAttrImpl1, AttrImpl paramAttrImpl2) {}
  
  void removedAttrNode(AttrImpl paramAttrImpl, NodeImpl paramNodeImpl, String paramString) {}
  
  void renamedAttrNode(Attr paramAttr1, Attr paramAttr2) {}
  
  void renamedElement(Element paramElement1, Element paramElement2) {}
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    Hashtable localHashtable = null;
    if (nodeUserData != null)
    {
      localHashtable = new Hashtable();
      localObject1 = nodeUserData.entrySet().iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (Map.Entry)((Iterator)localObject1).next();
        localHashtable.put(((Map.Entry)localObject2).getKey(), new Hashtable((Map)((Map.Entry)localObject2).getValue()));
      }
    }
    Object localObject1 = identifiers == null ? null : new Hashtable(identifiers);
    Object localObject2 = nodeTable == null ? null : new Hashtable(nodeTable);
    ObjectOutputStream.PutField localPutField = paramObjectOutputStream.putFields();
    localPutField.put("docType", docType);
    localPutField.put("docElement", docElement);
    localPutField.put("fFreeNLCache", fFreeNLCache);
    localPutField.put("encoding", encoding);
    localPutField.put("actualEncoding", actualEncoding);
    localPutField.put("version", version);
    localPutField.put("standalone", standalone);
    localPutField.put("fDocumentURI", fDocumentURI);
    localPutField.put("userData", localHashtable);
    localPutField.put("identifiers", localObject1);
    localPutField.put("changes", changes);
    localPutField.put("allowGrammarAccess", allowGrammarAccess);
    localPutField.put("errorChecking", errorChecking);
    localPutField.put("ancestorChecking", ancestorChecking);
    localPutField.put("xmlVersionChanged", xmlVersionChanged);
    localPutField.put("documentNumber", documentNumber);
    localPutField.put("nodeCounter", nodeCounter);
    localPutField.put("nodeTable", localObject2);
    localPutField.put("xml11Version", xml11Version);
    paramObjectOutputStream.writeFields();
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    ObjectInputStream.GetField localGetField = paramObjectInputStream.readFields();
    docType = ((DocumentTypeImpl)localGetField.get("docType", null));
    docElement = ((ElementImpl)localGetField.get("docElement", null));
    fFreeNLCache = ((NodeListCache)localGetField.get("fFreeNLCache", null));
    encoding = ((String)localGetField.get("encoding", null));
    actualEncoding = ((String)localGetField.get("actualEncoding", null));
    version = ((String)localGetField.get("version", null));
    standalone = localGetField.get("standalone", false);
    fDocumentURI = ((String)localGetField.get("fDocumentURI", null));
    Hashtable localHashtable1 = (Hashtable)localGetField.get("userData", null);
    Hashtable localHashtable2 = (Hashtable)localGetField.get("identifiers", null);
    changes = localGetField.get("changes", 0);
    allowGrammarAccess = localGetField.get("allowGrammarAccess", false);
    errorChecking = localGetField.get("errorChecking", true);
    ancestorChecking = localGetField.get("ancestorChecking", true);
    xmlVersionChanged = localGetField.get("xmlVersionChanged", false);
    documentNumber = localGetField.get("documentNumber", 0);
    nodeCounter = localGetField.get("nodeCounter", 0);
    Hashtable localHashtable3 = (Hashtable)localGetField.get("nodeTable", null);
    xml11Version = localGetField.get("xml11Version", false);
    if (localHashtable1 != null)
    {
      nodeUserData = new HashMap();
      Iterator localIterator = localHashtable1.entrySet().iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        nodeUserData.put(localEntry.getKey(), new HashMap((Map)localEntry.getValue()));
      }
    }
    if (localHashtable2 != null) {
      identifiers = new HashMap(localHashtable2);
    }
    if (localHashtable3 != null) {
      nodeTable = new HashMap(localHashtable3);
    }
  }
  
  static
  {
    kidOK[9] = 1410;
    kidOK[11] = (kidOK[6] = kidOK[5] = kidOK[1] = 'ƺ');
    kidOK[2] = 40;
    kidOK[10] = (kidOK[7] = kidOK[8] = kidOK[3] = kidOK[4] = kidOK[12] = 0);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\dom\CoreDocumentImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package com.sun.xml.internal.bind.v2.runtime;

import com.sun.istack.internal.SAXException2;
import com.sun.xml.internal.bind.CycleRecoverable;
import com.sun.xml.internal.bind.CycleRecoverable.Context;
import com.sun.xml.internal.bind.marshaller.NamespacePrefixMapper;
import com.sun.xml.internal.bind.util.ValidationEventLocatorExImpl;
import com.sun.xml.internal.bind.v2.runtime.output.MTOMXmlOutput;
import com.sun.xml.internal.bind.v2.runtime.output.NamespaceContextImpl;
import com.sun.xml.internal.bind.v2.runtime.output.NamespaceContextImpl.Element;
import com.sun.xml.internal.bind.v2.runtime.output.Pcdata;
import com.sun.xml.internal.bind.v2.runtime.output.XmlOutput;
import com.sun.xml.internal.bind.v2.runtime.property.Property;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Base64Data;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.IntData;
import com.sun.xml.internal.bind.v2.util.CollisionCheckStack;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.activation.MimeType;
import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Marshaller.Listener;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.ValidationEventLocator;
import javax.xml.bind.annotation.DomHandler;
import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.attachment.AttachmentMarshaller;
import javax.xml.bind.helpers.NotIdentifiableEventImpl;
import javax.xml.bind.helpers.ValidationEventImpl;
import javax.xml.bind.helpers.ValidationEventLocatorImpl;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.sax.SAXResult;
import org.xml.sax.SAXException;

public final class XMLSerializer
  extends Coordinator
{
  public final JAXBContextImpl grammar;
  private XmlOutput out;
  public final NameList nameList;
  public final int[] knownUri2prefixIndexMap;
  private final NamespaceContextImpl nsContext;
  private NamespaceContextImpl.Element nse;
  ThreadLocal<Property> currentProperty = new ThreadLocal();
  private boolean textHasAlreadyPrinted = false;
  private boolean seenRoot = false;
  private final MarshallerImpl marshaller;
  private final Set<Object> idReferencedObjects = new HashSet();
  private final Set<Object> objectsWithId = new HashSet();
  private final CollisionCheckStack<Object> cycleDetectionStack = new CollisionCheckStack();
  private String schemaLocation;
  private String noNsSchemaLocation;
  private Transformer identityTransformer;
  private ContentHandlerAdaptor contentHandlerAdapter;
  private boolean fragment;
  private Base64Data base64Data;
  private final IntData intData = new IntData();
  public AttachmentMarshaller attachmentMarshaller;
  private MimeType expectedMimeType;
  private boolean inlineBinaryFlag;
  private QName schemaType;
  
  XMLSerializer(MarshallerImpl paramMarshallerImpl)
  {
    marshaller = paramMarshallerImpl;
    grammar = marshaller.context;
    nsContext = new NamespaceContextImpl(this);
    nameList = marshaller.context.nameList;
    knownUri2prefixIndexMap = new int[nameList.namespaceURIs.length];
  }
  
  /**
   * @deprecated
   */
  public Base64Data getCachedBase64DataInstance()
  {
    return new Base64Data();
  }
  
  private String getIdFromObject(Object paramObject)
    throws SAXException, JAXBException
  {
    return grammar.getBeanInfo(paramObject, true).getId(paramObject, this);
  }
  
  private void handleMissingObjectError(String paramString)
    throws SAXException, IOException, XMLStreamException
  {
    reportMissingObjectError(paramString);
    endNamespaceDecls(null);
    endAttributes();
  }
  
  public void reportError(ValidationEvent paramValidationEvent)
    throws SAXException
  {
    ValidationEventHandler localValidationEventHandler;
    try
    {
      localValidationEventHandler = marshaller.getEventHandler();
    }
    catch (JAXBException localJAXBException)
    {
      throw new SAXException2(localJAXBException);
    }
    if (!localValidationEventHandler.handleEvent(paramValidationEvent))
    {
      if ((paramValidationEvent.getLinkedException() instanceof Exception)) {
        throw new SAXException2((Exception)paramValidationEvent.getLinkedException());
      }
      throw new SAXException2(paramValidationEvent.getMessage());
    }
  }
  
  public final void reportError(String paramString, Throwable paramThrowable)
    throws SAXException
  {
    ValidationEventImpl localValidationEventImpl = new ValidationEventImpl(1, paramThrowable.getMessage(), getCurrentLocation(paramString), paramThrowable);
    reportError(localValidationEventImpl);
  }
  
  public void startElement(Name paramName, Object paramObject)
  {
    startElement();
    nse.setTagName(paramName, paramObject);
  }
  
  public void startElement(String paramString1, String paramString2, String paramString3, Object paramObject)
  {
    startElement();
    int i = nsContext.declareNsUri(paramString1, paramString3, false);
    nse.setTagName(i, paramString2, paramObject);
  }
  
  public void startElementForce(String paramString1, String paramString2, String paramString3, Object paramObject)
  {
    startElement();
    int i = nsContext.force(paramString1, paramString3);
    nse.setTagName(i, paramString2, paramObject);
  }
  
  public void endNamespaceDecls(Object paramObject)
    throws IOException, XMLStreamException
  {
    nsContext.collectionMode = false;
    nse.startElement(out, paramObject);
  }
  
  public void endAttributes()
    throws SAXException, IOException, XMLStreamException
  {
    if (!seenRoot)
    {
      seenRoot = true;
      if ((schemaLocation != null) || (noNsSchemaLocation != null))
      {
        int i = nsContext.getPrefixIndex("http://www.w3.org/2001/XMLSchema-instance");
        if (schemaLocation != null) {
          out.attribute(i, "schemaLocation", schemaLocation);
        }
        if (noNsSchemaLocation != null) {
          out.attribute(i, "noNamespaceSchemaLocation", noNsSchemaLocation);
        }
      }
    }
    out.endStartTag();
  }
  
  public void endElement()
    throws SAXException, IOException, XMLStreamException
  {
    nse.endElement(out);
    nse = nse.pop();
    textHasAlreadyPrinted = false;
  }
  
  public void leafElement(Name paramName, String paramString1, String paramString2)
    throws SAXException, IOException, XMLStreamException
  {
    if (seenRoot)
    {
      textHasAlreadyPrinted = false;
      nse = nse.push();
      out.beginStartTag(paramName);
      out.endStartTag();
      if (paramString1 != null) {
        try
        {
          out.text(paramString1, false);
        }
        catch (IllegalArgumentException localIllegalArgumentException1)
        {
          throw new IllegalArgumentException(Messages.ILLEGAL_CONTENT.format(new Object[] { paramString2, localIllegalArgumentException1.getMessage() }));
        }
      }
      out.endTag(paramName);
      nse = nse.pop();
    }
    else
    {
      startElement(paramName, null);
      endNamespaceDecls(null);
      endAttributes();
      try
      {
        out.text(paramString1, false);
      }
      catch (IllegalArgumentException localIllegalArgumentException2)
      {
        throw new IllegalArgumentException(Messages.ILLEGAL_CONTENT.format(new Object[] { paramString2, localIllegalArgumentException2.getMessage() }));
      }
      endElement();
    }
  }
  
  public void leafElement(Name paramName, Pcdata paramPcdata, String paramString)
    throws SAXException, IOException, XMLStreamException
  {
    if (seenRoot)
    {
      textHasAlreadyPrinted = false;
      nse = nse.push();
      out.beginStartTag(paramName);
      out.endStartTag();
      if (paramPcdata != null) {
        out.text(paramPcdata, false);
      }
      out.endTag(paramName);
      nse = nse.pop();
    }
    else
    {
      startElement(paramName, null);
      endNamespaceDecls(null);
      endAttributes();
      out.text(paramPcdata, false);
      endElement();
    }
  }
  
  public void leafElement(Name paramName, int paramInt, String paramString)
    throws SAXException, IOException, XMLStreamException
  {
    intData.reset(paramInt);
    leafElement(paramName, intData, paramString);
  }
  
  public void text(String paramString1, String paramString2)
    throws SAXException, IOException, XMLStreamException
  {
    if (paramString1 == null)
    {
      reportMissingObjectError(paramString2);
      return;
    }
    out.text(paramString1, textHasAlreadyPrinted);
    textHasAlreadyPrinted = true;
  }
  
  public void text(Pcdata paramPcdata, String paramString)
    throws SAXException, IOException, XMLStreamException
  {
    if (paramPcdata == null)
    {
      reportMissingObjectError(paramString);
      return;
    }
    out.text(paramPcdata, textHasAlreadyPrinted);
    textHasAlreadyPrinted = true;
  }
  
  public void attribute(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {
    int i;
    if (paramString1.length() == 0) {
      i = -1;
    } else {
      i = nsContext.getPrefixIndex(paramString1);
    }
    try
    {
      out.attribute(i, paramString2, paramString3);
    }
    catch (IOException localIOException)
    {
      throw new SAXException2(localIOException);
    }
    catch (XMLStreamException localXMLStreamException)
    {
      throw new SAXException2(localXMLStreamException);
    }
  }
  
  public void attribute(Name paramName, CharSequence paramCharSequence)
    throws IOException, XMLStreamException
  {
    out.attribute(paramName, paramCharSequence.toString());
  }
  
  public NamespaceContext2 getNamespaceContext()
  {
    return nsContext;
  }
  
  public String onID(Object paramObject, String paramString)
  {
    objectsWithId.add(paramObject);
    return paramString;
  }
  
  public String onIDREF(Object paramObject)
    throws SAXException
  {
    String str;
    try
    {
      str = getIdFromObject(paramObject);
    }
    catch (JAXBException localJAXBException)
    {
      reportError(null, localJAXBException);
      return null;
    }
    idReferencedObjects.add(paramObject);
    if (str == null) {
      reportError(new NotIdentifiableEventImpl(1, Messages.NOT_IDENTIFIABLE.format(new Object[0]), new ValidationEventLocatorImpl(paramObject)));
    }
    return str;
  }
  
  public void childAsRoot(Object paramObject)
    throws JAXBException, IOException, SAXException, XMLStreamException
  {
    JaxBeanInfo localJaxBeanInfo = grammar.getBeanInfo(paramObject, true);
    cycleDetectionStack.pushNocheck(paramObject);
    boolean bool = localJaxBeanInfo.lookForLifecycleMethods();
    if (bool) {
      fireBeforeMarshalEvents(localJaxBeanInfo, paramObject);
    }
    localJaxBeanInfo.serializeRoot(paramObject, this);
    if (bool) {
      fireAfterMarshalEvents(localJaxBeanInfo, paramObject);
    }
    cycleDetectionStack.pop();
  }
  
  private Object pushObject(Object paramObject, String paramString)
    throws SAXException
  {
    if (!cycleDetectionStack.push(paramObject)) {
      return paramObject;
    }
    if ((paramObject instanceof CycleRecoverable))
    {
      paramObject = ((CycleRecoverable)paramObject).onCycleDetected(new CycleRecoverable.Context()
      {
        public Marshaller getMarshaller()
        {
          return marshaller;
        }
      });
      if (paramObject != null)
      {
        cycleDetectionStack.pop();
        return pushObject(paramObject, paramString);
      }
      return null;
    }
    reportError(new ValidationEventImpl(1, Messages.CYCLE_IN_MARSHALLER.format(new Object[] { cycleDetectionStack.getCycleString() }), getCurrentLocation(paramString), null));
    return null;
  }
  
  public final void childAsSoleContent(Object paramObject, String paramString)
    throws SAXException, IOException, XMLStreamException
  {
    if (paramObject == null)
    {
      handleMissingObjectError(paramString);
    }
    else
    {
      paramObject = pushObject(paramObject, paramString);
      if (paramObject == null)
      {
        endNamespaceDecls(null);
        endAttributes();
        cycleDetectionStack.pop();
      }
      JaxBeanInfo localJaxBeanInfo;
      try
      {
        localJaxBeanInfo = grammar.getBeanInfo(paramObject, true);
      }
      catch (JAXBException localJAXBException)
      {
        reportError(paramString, localJAXBException);
        endNamespaceDecls(null);
        endAttributes();
        cycleDetectionStack.pop();
        return;
      }
      boolean bool = localJaxBeanInfo.lookForLifecycleMethods();
      if (bool) {
        fireBeforeMarshalEvents(localJaxBeanInfo, paramObject);
      }
      localJaxBeanInfo.serializeURIs(paramObject, this);
      endNamespaceDecls(paramObject);
      localJaxBeanInfo.serializeAttributes(paramObject, this);
      endAttributes();
      localJaxBeanInfo.serializeBody(paramObject, this);
      if (bool) {
        fireAfterMarshalEvents(localJaxBeanInfo, paramObject);
      }
      cycleDetectionStack.pop();
    }
  }
  
  public final void childAsXsiType(Object paramObject, String paramString, JaxBeanInfo paramJaxBeanInfo, boolean paramBoolean)
    throws SAXException, IOException, XMLStreamException
  {
    if (paramObject == null)
    {
      handleMissingObjectError(paramString);
    }
    else
    {
      paramObject = pushObject(paramObject, paramString);
      if (paramObject == null)
      {
        endNamespaceDecls(null);
        endAttributes();
        return;
      }
      int i = paramObject.getClass() == jaxbType ? 1 : 0;
      JaxBeanInfo localJaxBeanInfo = paramJaxBeanInfo;
      QName localQName = null;
      if ((i != 0) && (localJaxBeanInfo.lookForLifecycleMethods())) {
        fireBeforeMarshalEvents(localJaxBeanInfo, paramObject);
      }
      if (i == 0)
      {
        try
        {
          localJaxBeanInfo = grammar.getBeanInfo(paramObject, true);
          if (localJaxBeanInfo.lookForLifecycleMethods()) {
            fireBeforeMarshalEvents(localJaxBeanInfo, paramObject);
          }
        }
        catch (JAXBException localJAXBException)
        {
          reportError(paramString, localJAXBException);
          endNamespaceDecls(null);
          endAttributes();
          return;
        }
        if (localJaxBeanInfo == paramJaxBeanInfo)
        {
          i = 1;
        }
        else
        {
          localQName = localJaxBeanInfo.getTypeName(paramObject);
          if (localQName == null)
          {
            reportError(new ValidationEventImpl(1, Messages.SUBSTITUTED_BY_ANONYMOUS_TYPE.format(new Object[] { jaxbType.getName(), paramObject.getClass().getName(), jaxbType.getName() }), getCurrentLocation(paramString)));
          }
          else
          {
            getNamespaceContext().declareNamespace("http://www.w3.org/2001/XMLSchema-instance", "xsi", true);
            getNamespaceContext().declareNamespace(localQName.getNamespaceURI(), null, false);
          }
        }
      }
      localJaxBeanInfo.serializeURIs(paramObject, this);
      if (paramBoolean) {
        getNamespaceContext().declareNamespace("http://www.w3.org/2001/XMLSchema-instance", "xsi", true);
      }
      endNamespaceDecls(paramObject);
      if (i == 0) {
        attribute("http://www.w3.org/2001/XMLSchema-instance", "type", DatatypeConverter.printQName(localQName, getNamespaceContext()));
      }
      localJaxBeanInfo.serializeAttributes(paramObject, this);
      boolean bool = localJaxBeanInfo.isNilIncluded();
      if ((paramBoolean) && (!bool)) {
        attribute("http://www.w3.org/2001/XMLSchema-instance", "nil", "true");
      }
      endAttributes();
      localJaxBeanInfo.serializeBody(paramObject, this);
      if (localJaxBeanInfo.lookForLifecycleMethods()) {
        fireAfterMarshalEvents(localJaxBeanInfo, paramObject);
      }
      cycleDetectionStack.pop();
    }
  }
  
  private void fireAfterMarshalEvents(JaxBeanInfo paramJaxBeanInfo, Object paramObject)
  {
    if (paramJaxBeanInfo.hasAfterMarshalMethod())
    {
      localObject = getLifecycleMethodsafterMarshal;
      fireMarshalEvent(paramObject, (Method)localObject);
    }
    Object localObject = marshaller.getListener();
    if (localObject != null) {
      ((Marshaller.Listener)localObject).afterMarshal(paramObject);
    }
  }
  
  private void fireBeforeMarshalEvents(JaxBeanInfo paramJaxBeanInfo, Object paramObject)
  {
    if (paramJaxBeanInfo.hasBeforeMarshalMethod())
    {
      localObject = getLifecycleMethodsbeforeMarshal;
      fireMarshalEvent(paramObject, (Method)localObject);
    }
    Object localObject = marshaller.getListener();
    if (localObject != null) {
      ((Marshaller.Listener)localObject).beforeMarshal(paramObject);
    }
  }
  
  private void fireMarshalEvent(Object paramObject, Method paramMethod)
  {
    try
    {
      paramMethod.invoke(paramObject, new Object[] { marshaller });
    }
    catch (Exception localException)
    {
      throw new IllegalStateException(localException);
    }
  }
  
  public void attWildcardAsURIs(Map<QName, String> paramMap, String paramString)
  {
    if (paramMap == null) {
      return;
    }
    Iterator localIterator = paramMap.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      QName localQName = (QName)localEntry.getKey();
      String str1 = localQName.getNamespaceURI();
      if (str1.length() > 0)
      {
        String str2 = localQName.getPrefix();
        if (str2.length() == 0) {
          str2 = null;
        }
        nsContext.declareNsUri(str1, str2, true);
      }
    }
  }
  
  public void attWildcardAsAttributes(Map<QName, String> paramMap, String paramString)
    throws SAXException
  {
    if (paramMap == null) {
      return;
    }
    Iterator localIterator = paramMap.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      QName localQName = (QName)localEntry.getKey();
      attribute(localQName.getNamespaceURI(), localQName.getLocalPart(), (String)localEntry.getValue());
    }
  }
  
  public final void writeXsiNilTrue()
    throws SAXException, IOException, XMLStreamException
  {
    getNamespaceContext().declareNamespace("http://www.w3.org/2001/XMLSchema-instance", "xsi", true);
    endNamespaceDecls(null);
    attribute("http://www.w3.org/2001/XMLSchema-instance", "nil", "true");
    endAttributes();
  }
  
  public <E> void writeDom(E paramE, DomHandler<E, ?> paramDomHandler, Object paramObject, String paramString)
    throws SAXException
  {
    Source localSource = paramDomHandler.marshal(paramE, this);
    if (contentHandlerAdapter == null) {
      contentHandlerAdapter = new ContentHandlerAdaptor(this);
    }
    try
    {
      getIdentityTransformer().transform(localSource, new SAXResult(contentHandlerAdapter));
    }
    catch (TransformerException localTransformerException)
    {
      reportError(paramString, localTransformerException);
    }
  }
  
  public Transformer getIdentityTransformer()
  {
    if (identityTransformer == null) {
      identityTransformer = JAXBContextImpl.createTransformer(grammar.disableSecurityProcessing);
    }
    return identityTransformer;
  }
  
  public void setPrefixMapper(NamespacePrefixMapper paramNamespacePrefixMapper)
  {
    nsContext.setPrefixMapper(paramNamespacePrefixMapper);
  }
  
  public void startDocument(XmlOutput paramXmlOutput, boolean paramBoolean, String paramString1, String paramString2)
    throws IOException, SAXException, XMLStreamException
  {
    pushCoordinator();
    nsContext.reset();
    nse = nsContext.getCurrent();
    if ((attachmentMarshaller != null) && (attachmentMarshaller.isXOPPackage())) {
      paramXmlOutput = new MTOMXmlOutput(paramXmlOutput);
    }
    out = paramXmlOutput;
    objectsWithId.clear();
    idReferencedObjects.clear();
    textHasAlreadyPrinted = false;
    seenRoot = false;
    schemaLocation = paramString1;
    noNsSchemaLocation = paramString2;
    fragment = paramBoolean;
    inlineBinaryFlag = false;
    expectedMimeType = null;
    cycleDetectionStack.reset();
    paramXmlOutput.startDocument(this, paramBoolean, knownUri2prefixIndexMap, nsContext);
  }
  
  public void endDocument()
    throws IOException, SAXException, XMLStreamException
  {
    out.endDocument(fragment);
  }
  
  public void close()
  {
    out = null;
    clearCurrentProperty();
    popCoordinator();
  }
  
  public void addInscopeBinding(String paramString1, String paramString2)
  {
    nsContext.put(paramString1, paramString2);
  }
  
  public String getXMIMEContentType()
  {
    String str = grammar.getXMIMEContentType(cycleDetectionStack.peek());
    if (str != null) {
      return str;
    }
    if (expectedMimeType != null) {
      return expectedMimeType.toString();
    }
    return null;
  }
  
  private void startElement()
  {
    nse = nse.push();
    if (!seenRoot)
    {
      if (grammar.getXmlNsSet() != null)
      {
        localObject = grammar.getXmlNsSet().iterator();
        while (((Iterator)localObject).hasNext())
        {
          XmlNs localXmlNs = (XmlNs)((Iterator)localObject).next();
          nsContext.declareNsUri(localXmlNs.namespaceURI(), localXmlNs.prefix() == null ? "" : localXmlNs.prefix(), localXmlNs.prefix() != null);
        }
      }
      Object localObject = nameList.namespaceURIs;
      for (int i = 0; i < localObject.length; i++) {
        knownUri2prefixIndexMap[i] = nsContext.declareNsUri(localObject[i], null, nameList.nsUriCannotBeDefaulted[i]);
      }
      String[] arrayOfString1 = nsContext.getPrefixMapper().getPreDeclaredNamespaceUris();
      String str2;
      if (arrayOfString1 != null) {
        for (str2 : arrayOfString1) {
          if (str2 != null) {
            nsContext.declareNsUri(str2, null, false);
          }
        }
      }
      ??? = nsContext.getPrefixMapper().getPreDeclaredNamespaceUris2();
      if (??? != null) {
        for (??? = 0; ??? < ???.length; ??? += 2)
        {
          String str1 = ???[???];
          str2 = ???[(??? + 1)];
          if ((str1 != null) && (str2 != null)) {
            nsContext.put(str2, str1);
          }
        }
      }
      if ((schemaLocation != null) || (noNsSchemaLocation != null)) {
        nsContext.declareNsUri("http://www.w3.org/2001/XMLSchema-instance", "xsi", true);
      }
    }
    nsContext.collectionMode = true;
    textHasAlreadyPrinted = false;
  }
  
  public MimeType setExpectedMimeType(MimeType paramMimeType)
  {
    MimeType localMimeType = expectedMimeType;
    expectedMimeType = paramMimeType;
    return localMimeType;
  }
  
  public boolean setInlineBinaryFlag(boolean paramBoolean)
  {
    boolean bool = inlineBinaryFlag;
    inlineBinaryFlag = paramBoolean;
    return bool;
  }
  
  public boolean getInlineBinaryFlag()
  {
    return inlineBinaryFlag;
  }
  
  public QName setSchemaType(QName paramQName)
  {
    QName localQName = schemaType;
    schemaType = paramQName;
    return localQName;
  }
  
  public QName getSchemaType()
  {
    return schemaType;
  }
  
  public void setObjectIdentityCycleDetection(boolean paramBoolean)
  {
    cycleDetectionStack.setUseIdentity(paramBoolean);
  }
  
  public boolean getObjectIdentityCycleDetection()
  {
    return cycleDetectionStack.getUseIdentity();
  }
  
  void reconcileID()
    throws SAXException
  {
    idReferencedObjects.removeAll(objectsWithId);
    Iterator localIterator = idReferencedObjects.iterator();
    while (localIterator.hasNext())
    {
      Object localObject = localIterator.next();
      try
      {
        String str = getIdFromObject(localObject);
        reportError(new NotIdentifiableEventImpl(1, Messages.DANGLING_IDREF.format(new Object[] { str }), new ValidationEventLocatorImpl(localObject)));
      }
      catch (JAXBException localJAXBException) {}
    }
    idReferencedObjects.clear();
    objectsWithId.clear();
  }
  
  public boolean handleError(Exception paramException)
  {
    return handleError(paramException, cycleDetectionStack.peek(), null);
  }
  
  public boolean handleError(Exception paramException, Object paramObject, String paramString)
  {
    return handleEvent(new ValidationEventImpl(1, paramException.getMessage(), new ValidationEventLocatorExImpl(paramObject, paramString), paramException));
  }
  
  public boolean handleEvent(ValidationEvent paramValidationEvent)
  {
    try
    {
      return marshaller.getEventHandler().handleEvent(paramValidationEvent);
    }
    catch (JAXBException localJAXBException)
    {
      throw new Error(localJAXBException);
    }
  }
  
  private void reportMissingObjectError(String paramString)
    throws SAXException
  {
    reportError(new ValidationEventImpl(1, Messages.MISSING_OBJECT.format(new Object[] { paramString }), getCurrentLocation(paramString), new NullPointerException()));
  }
  
  public void errorMissingId(Object paramObject)
    throws SAXException
  {
    reportError(new ValidationEventImpl(1, Messages.MISSING_ID.format(new Object[] { paramObject }), new ValidationEventLocatorImpl(paramObject)));
  }
  
  public ValidationEventLocator getCurrentLocation(String paramString)
  {
    return new ValidationEventLocatorExImpl(cycleDetectionStack.peek(), paramString);
  }
  
  protected ValidationEventLocator getLocation()
  {
    return getCurrentLocation(null);
  }
  
  public Property getCurrentProperty()
  {
    return (Property)currentProperty.get();
  }
  
  public void clearCurrentProperty()
  {
    if (currentProperty != null) {
      currentProperty.remove();
    }
  }
  
  public static XMLSerializer getInstance()
  {
    return (XMLSerializer)Coordinator._getInstance();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\XMLSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
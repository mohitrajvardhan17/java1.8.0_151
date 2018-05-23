package com.sun.xml.internal.ws.server.sei;

import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.message.Attachment;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.api.message.Header;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.MessageHeaders;
import com.sun.xml.internal.ws.api.model.ParameterBinding;
import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory;
import com.sun.xml.internal.ws.encoding.DataHandlerDataSource;
import com.sun.xml.internal.ws.encoding.StringDataContentHandler;
import com.sun.xml.internal.ws.message.AttachmentUnmarshallerImpl;
import com.sun.xml.internal.ws.model.AbstractSEIModelImpl;
import com.sun.xml.internal.ws.model.ParameterImpl;
import com.sun.xml.internal.ws.model.WrapperParameter;
import com.sun.xml.internal.ws.resources.ServerMessages;
import com.sun.xml.internal.ws.spi.db.BindingContext;
import com.sun.xml.internal.ws.spi.db.DatabindingException;
import com.sun.xml.internal.ws.spi.db.PropertyAccessor;
import com.sun.xml.internal.ws.spi.db.RepeatedElementBridge;
import com.sun.xml.internal.ws.spi.db.RepeatedElementBridge.CollectionHandler;
import com.sun.xml.internal.ws.spi.db.TypeInfo;
import com.sun.xml.internal.ws.spi.db.WrapperComposite;
import com.sun.xml.internal.ws.spi.db.XMLBridge;
import com.sun.xml.internal.ws.streaming.XMLStreamReaderUtil;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.activation.DataHandler;
import javax.imageio.ImageIO;
import javax.jws.WebParam.Mode;
import javax.xml.bind.JAXBException;
import javax.xml.bind.attachment.AttachmentUnmarshaller;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPFault;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;

public abstract class EndpointArgumentsBuilder
{
  public static final EndpointArgumentsBuilder NONE = new None(null);
  private static final Map<Class, Object> primitiveUninitializedValues = new HashMap();
  protected QName wrapperName;
  protected Map<QName, WrappedPartBuilder> wrappedParts = null;
  
  public EndpointArgumentsBuilder() {}
  
  public abstract void readRequest(Message paramMessage, Object[] paramArrayOfObject)
    throws JAXBException, XMLStreamException;
  
  public static Object getVMUninitializedValue(Type paramType)
  {
    return primitiveUninitializedValues.get(paramType);
  }
  
  protected void readWrappedRequest(Message paramMessage, Object[] paramArrayOfObject)
    throws JAXBException, XMLStreamException
  {
    if (!paramMessage.hasPayload()) {
      throw new WebServiceException("No payload. Expecting payload with " + wrapperName + " element");
    }
    XMLStreamReader localXMLStreamReader = paramMessage.readPayload();
    XMLStreamReaderUtil.verifyTag(localXMLStreamReader, wrapperName);
    localXMLStreamReader.nextTag();
    while (localXMLStreamReader.getEventType() == 1)
    {
      QName localQName = localXMLStreamReader.getName();
      WrappedPartBuilder localWrappedPartBuilder = (WrappedPartBuilder)wrappedParts.get(localQName);
      if (localWrappedPartBuilder == null)
      {
        XMLStreamReaderUtil.skipElement(localXMLStreamReader);
        localXMLStreamReader.nextTag();
      }
      else
      {
        localWrappedPartBuilder.readRequest(paramArrayOfObject, localXMLStreamReader, paramMessage.getAttachments());
      }
      XMLStreamReaderUtil.toNextTag(localXMLStreamReader, localQName);
    }
    localXMLStreamReader.close();
    XMLStreamReaderFactory.recycle(localXMLStreamReader);
  }
  
  public static final String getWSDLPartName(Attachment paramAttachment)
  {
    String str1 = paramAttachment.getContentId();
    int i = str1.lastIndexOf('@', str1.length());
    if (i == -1) {
      return null;
    }
    String str2 = str1.substring(0, i);
    i = str2.lastIndexOf('=', str2.length());
    if (i == -1) {
      return null;
    }
    try
    {
      return URLDecoder.decode(str2.substring(0, i), "UTF-8");
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      throw new WebServiceException(localUnsupportedEncodingException);
    }
  }
  
  private static boolean isXMLMimeType(String paramString)
  {
    return (paramString.equals("text/xml")) || (paramString.equals("application/xml"));
  }
  
  static
  {
    Map localMap = primitiveUninitializedValues;
    localMap.put(Integer.TYPE, Integer.valueOf(0));
    localMap.put(Character.TYPE, Character.valueOf('\000'));
    localMap.put(Byte.TYPE, Byte.valueOf((byte)0));
    localMap.put(Short.TYPE, Short.valueOf((short)0));
    localMap.put(Long.TYPE, Long.valueOf(0L));
    localMap.put(Float.TYPE, Float.valueOf(0.0F));
    localMap.put(Double.TYPE, Double.valueOf(0.0D));
  }
  
  public static abstract class AttachmentBuilder
    extends EndpointArgumentsBuilder
  {
    protected final EndpointValueSetter setter;
    protected final ParameterImpl param;
    protected final String pname;
    protected final String pname1;
    
    AttachmentBuilder(ParameterImpl paramParameterImpl, EndpointValueSetter paramEndpointValueSetter)
    {
      setter = paramEndpointValueSetter;
      param = paramParameterImpl;
      pname = paramParameterImpl.getPartName();
      pname1 = ("<" + pname);
    }
    
    public static EndpointArgumentsBuilder createAttachmentBuilder(ParameterImpl paramParameterImpl, EndpointValueSetter paramEndpointValueSetter)
    {
      Class localClass = (Class)getTypeInfotype;
      if (DataHandler.class.isAssignableFrom(localClass)) {
        return new EndpointArgumentsBuilder.DataHandlerBuilder(paramParameterImpl, paramEndpointValueSetter);
      }
      if (byte[].class == localClass) {
        return new EndpointArgumentsBuilder.ByteArrayBuilder(paramParameterImpl, paramEndpointValueSetter);
      }
      if (Source.class.isAssignableFrom(localClass)) {
        return new EndpointArgumentsBuilder.SourceBuilder(paramParameterImpl, paramEndpointValueSetter);
      }
      if (Image.class.isAssignableFrom(localClass)) {
        return new EndpointArgumentsBuilder.ImageBuilder(paramParameterImpl, paramEndpointValueSetter);
      }
      if (InputStream.class == localClass) {
        return new EndpointArgumentsBuilder.InputStreamBuilder(paramParameterImpl, paramEndpointValueSetter);
      }
      if (EndpointArgumentsBuilder.isXMLMimeType(paramParameterImpl.getBinding().getMimeType())) {
        return new EndpointArgumentsBuilder.JAXBBuilder(paramParameterImpl, paramEndpointValueSetter);
      }
      if (String.class.isAssignableFrom(localClass)) {
        return new EndpointArgumentsBuilder.StringBuilder(paramParameterImpl, paramEndpointValueSetter);
      }
      throw new UnsupportedOperationException("Unknown Type=" + localClass + " Attachment is not mapped.");
    }
    
    public void readRequest(Message paramMessage, Object[] paramArrayOfObject)
      throws JAXBException, XMLStreamException
    {
      int i = 0;
      Iterator localIterator = paramMessage.getAttachments().iterator();
      while (localIterator.hasNext())
      {
        Attachment localAttachment = (Attachment)localIterator.next();
        String str = getWSDLPartName(localAttachment);
        if (str != null) {
          if ((str.equals(pname)) || (str.equals(pname1)))
          {
            i = 1;
            mapAttachment(localAttachment, paramArrayOfObject);
            break;
          }
        }
      }
      if (i == 0) {
        throw new WebServiceException("Missing Attachment for " + pname);
      }
    }
    
    abstract void mapAttachment(Attachment paramAttachment, Object[] paramArrayOfObject)
      throws JAXBException;
  }
  
  public static final class Body
    extends EndpointArgumentsBuilder
  {
    private final XMLBridge<?> bridge;
    private final EndpointValueSetter setter;
    
    public Body(XMLBridge<?> paramXMLBridge, EndpointValueSetter paramEndpointValueSetter)
    {
      bridge = paramXMLBridge;
      setter = paramEndpointValueSetter;
    }
    
    public void readRequest(Message paramMessage, Object[] paramArrayOfObject)
      throws JAXBException
    {
      setter.put(paramMessage.readPayloadAsJAXB(bridge), paramArrayOfObject);
    }
  }
  
  private static final class ByteArrayBuilder
    extends EndpointArgumentsBuilder.AttachmentBuilder
  {
    ByteArrayBuilder(ParameterImpl paramParameterImpl, EndpointValueSetter paramEndpointValueSetter)
    {
      super(paramEndpointValueSetter);
    }
    
    void mapAttachment(Attachment paramAttachment, Object[] paramArrayOfObject)
    {
      setter.put(paramAttachment.asByteArray(), paramArrayOfObject);
    }
  }
  
  public static final class Composite
    extends EndpointArgumentsBuilder
  {
    private final EndpointArgumentsBuilder[] builders;
    
    public Composite(EndpointArgumentsBuilder... paramVarArgs)
    {
      builders = paramVarArgs;
    }
    
    public Composite(Collection<? extends EndpointArgumentsBuilder> paramCollection)
    {
      this((EndpointArgumentsBuilder[])paramCollection.toArray(new EndpointArgumentsBuilder[paramCollection.size()]));
    }
    
    public void readRequest(Message paramMessage, Object[] paramArrayOfObject)
      throws JAXBException, XMLStreamException
    {
      for (EndpointArgumentsBuilder localEndpointArgumentsBuilder : builders) {
        localEndpointArgumentsBuilder.readRequest(paramMessage, paramArrayOfObject);
      }
    }
  }
  
  private static final class DataHandlerBuilder
    extends EndpointArgumentsBuilder.AttachmentBuilder
  {
    DataHandlerBuilder(ParameterImpl paramParameterImpl, EndpointValueSetter paramEndpointValueSetter)
    {
      super(paramEndpointValueSetter);
    }
    
    void mapAttachment(Attachment paramAttachment, Object[] paramArrayOfObject)
    {
      setter.put(paramAttachment.asDataHandler(), paramArrayOfObject);
    }
  }
  
  public static final class DocLit
    extends EndpointArgumentsBuilder
  {
    private final PartBuilder[] parts;
    private final XMLBridge wrapper;
    private boolean dynamicWrapper;
    
    public DocLit(WrapperParameter paramWrapperParameter, WebParam.Mode paramMode)
    {
      wrapperName = paramWrapperParameter.getName();
      wrapper = paramWrapperParameter.getXMLBridge();
      Class localClass = (Class)wrapper.getTypeInfo().type;
      dynamicWrapper = WrapperComposite.class.equals(localClass);
      ArrayList localArrayList = new ArrayList();
      List localList = paramWrapperParameter.getWrapperChildren();
      Iterator localIterator = localList.iterator();
      while (localIterator.hasNext())
      {
        ParameterImpl localParameterImpl = (ParameterImpl)localIterator.next();
        if (localParameterImpl.getMode() != paramMode)
        {
          QName localQName = localParameterImpl.getName();
          try
          {
            if (dynamicWrapper)
            {
              if (wrappedParts == null) {
                wrappedParts = new HashMap();
              }
              XMLBridge localXMLBridge = localParameterImpl.getInlinedRepeatedElementBridge();
              if (localXMLBridge == null) {
                localXMLBridge = localParameterImpl.getXMLBridge();
              }
              wrappedParts.put(localParameterImpl.getName(), new EndpointArgumentsBuilder.WrappedPartBuilder(localXMLBridge, EndpointValueSetter.get(localParameterImpl)));
            }
            else
            {
              localArrayList.add(new PartBuilder(paramWrapperParameter.getOwner().getBindingContext().getElementPropertyAccessor(localClass, localQName.getNamespaceURI(), localParameterImpl.getName().getLocalPart()), EndpointValueSetter.get(localParameterImpl)));
              if ((!$assertionsDisabled) && (localParameterImpl.getBinding() != ParameterBinding.BODY)) {
                throw new AssertionError();
              }
            }
          }
          catch (JAXBException localJAXBException)
          {
            throw new WebServiceException(localClass + " do not have a property of the name " + localQName, localJAXBException);
          }
        }
      }
      parts = ((PartBuilder[])localArrayList.toArray(new PartBuilder[localArrayList.size()]));
    }
    
    public void readRequest(Message paramMessage, Object[] paramArrayOfObject)
      throws JAXBException, XMLStreamException
    {
      if (dynamicWrapper)
      {
        readWrappedRequest(paramMessage, paramArrayOfObject);
      }
      else if (parts.length > 0)
      {
        if (!paramMessage.hasPayload()) {
          throw new WebServiceException("No payload. Expecting payload with " + wrapperName + " element");
        }
        XMLStreamReader localXMLStreamReader = paramMessage.readPayload();
        XMLStreamReaderUtil.verifyTag(localXMLStreamReader, wrapperName);
        Object localObject = wrapper.unmarshal(localXMLStreamReader, paramMessage.getAttachments() != null ? new AttachmentUnmarshallerImpl(paramMessage.getAttachments()) : null);
        try
        {
          for (PartBuilder localPartBuilder : parts) {
            localPartBuilder.readRequest(paramArrayOfObject, localObject);
          }
        }
        catch (DatabindingException localDatabindingException)
        {
          throw new WebServiceException(localDatabindingException);
        }
        localXMLStreamReader.close();
        XMLStreamReaderFactory.recycle(localXMLStreamReader);
      }
      else
      {
        paramMessage.consume();
      }
    }
    
    static final class PartBuilder
    {
      private final PropertyAccessor accessor;
      private final EndpointValueSetter setter;
      
      public PartBuilder(PropertyAccessor paramPropertyAccessor, EndpointValueSetter paramEndpointValueSetter)
      {
        accessor = paramPropertyAccessor;
        setter = paramEndpointValueSetter;
        assert ((paramPropertyAccessor != null) && (paramEndpointValueSetter != null));
      }
      
      final void readRequest(Object[] paramArrayOfObject, Object paramObject)
      {
        Object localObject = accessor.get(paramObject);
        setter.put(localObject, paramArrayOfObject);
      }
    }
  }
  
  public static final class Header
    extends EndpointArgumentsBuilder
  {
    private final XMLBridge<?> bridge;
    private final EndpointValueSetter setter;
    private final QName headerName;
    private final SOAPVersion soapVersion;
    
    public Header(SOAPVersion paramSOAPVersion, QName paramQName, XMLBridge<?> paramXMLBridge, EndpointValueSetter paramEndpointValueSetter)
    {
      soapVersion = paramSOAPVersion;
      headerName = paramQName;
      bridge = paramXMLBridge;
      setter = paramEndpointValueSetter;
    }
    
    public Header(SOAPVersion paramSOAPVersion, ParameterImpl paramParameterImpl, EndpointValueSetter paramEndpointValueSetter)
    {
      this(paramSOAPVersion, getTypeInfotagName, paramParameterImpl.getXMLBridge(), paramEndpointValueSetter);
      assert (paramParameterImpl.getOutBinding() == ParameterBinding.HEADER);
    }
    
    private SOAPFaultException createDuplicateHeaderException()
    {
      try
      {
        SOAPFault localSOAPFault = soapVersion.getSOAPFactory().createFault();
        localSOAPFault.setFaultCode(soapVersion.faultCodeClient);
        localSOAPFault.setFaultString(ServerMessages.DUPLICATE_PORT_KNOWN_HEADER(headerName));
        return new SOAPFaultException(localSOAPFault);
      }
      catch (SOAPException localSOAPException)
      {
        throw new WebServiceException(localSOAPException);
      }
    }
    
    public void readRequest(Message paramMessage, Object[] paramArrayOfObject)
      throws JAXBException
    {
      Header localHeader = null;
      Iterator localIterator = paramMessage.getHeaders().getHeaders(headerName, true);
      if (localIterator.hasNext())
      {
        localHeader = (Header)localIterator.next();
        if (localIterator.hasNext()) {
          throw createDuplicateHeaderException();
        }
      }
      if (localHeader != null) {
        setter.put(localHeader.readAsJAXB(bridge), paramArrayOfObject);
      }
    }
  }
  
  private static final class ImageBuilder
    extends EndpointArgumentsBuilder.AttachmentBuilder
  {
    ImageBuilder(ParameterImpl paramParameterImpl, EndpointValueSetter paramEndpointValueSetter)
    {
      super(paramEndpointValueSetter);
    }
    
    void mapAttachment(Attachment paramAttachment, Object[] paramArrayOfObject)
    {
      InputStream localInputStream = null;
      try
      {
        localInputStream = paramAttachment.asInputStream();
        BufferedImage localBufferedImage = ImageIO.read(localInputStream);
        if (localInputStream != null) {
          try
          {
            localInputStream.close();
          }
          catch (IOException localIOException1)
          {
            throw new WebServiceException(localIOException1);
          }
        }
        setter.put(localBufferedImage, paramArrayOfObject);
      }
      catch (IOException localIOException2)
      {
        throw new WebServiceException(localIOException2);
      }
      finally
      {
        if (localInputStream != null) {
          try
          {
            localInputStream.close();
          }
          catch (IOException localIOException3)
          {
            throw new WebServiceException(localIOException3);
          }
        }
      }
    }
  }
  
  private static final class InputStreamBuilder
    extends EndpointArgumentsBuilder.AttachmentBuilder
  {
    InputStreamBuilder(ParameterImpl paramParameterImpl, EndpointValueSetter paramEndpointValueSetter)
    {
      super(paramEndpointValueSetter);
    }
    
    void mapAttachment(Attachment paramAttachment, Object[] paramArrayOfObject)
    {
      setter.put(paramAttachment.asInputStream(), paramArrayOfObject);
    }
  }
  
  private static final class JAXBBuilder
    extends EndpointArgumentsBuilder.AttachmentBuilder
  {
    JAXBBuilder(ParameterImpl paramParameterImpl, EndpointValueSetter paramEndpointValueSetter)
    {
      super(paramEndpointValueSetter);
    }
    
    void mapAttachment(Attachment paramAttachment, Object[] paramArrayOfObject)
      throws JAXBException
    {
      Object localObject = param.getXMLBridge().unmarshal(paramAttachment.asInputStream());
      setter.put(localObject, paramArrayOfObject);
    }
  }
  
  static final class None
    extends EndpointArgumentsBuilder
  {
    private None() {}
    
    public void readRequest(Message paramMessage, Object[] paramArrayOfObject)
    {
      paramMessage.consume();
    }
  }
  
  public static final class NullSetter
    extends EndpointArgumentsBuilder
  {
    private final EndpointValueSetter setter;
    private final Object nullValue;
    
    public NullSetter(EndpointValueSetter paramEndpointValueSetter, Object paramObject)
    {
      assert (paramEndpointValueSetter != null);
      nullValue = paramObject;
      setter = paramEndpointValueSetter;
    }
    
    public void readRequest(Message paramMessage, Object[] paramArrayOfObject)
    {
      setter.put(nullValue, paramArrayOfObject);
    }
  }
  
  public static final class RpcLit
    extends EndpointArgumentsBuilder
  {
    public RpcLit(WrapperParameter paramWrapperParameter)
    {
      assert (getTypeInfotype == WrapperComposite.class);
      wrapperName = paramWrapperParameter.getName();
      wrappedParts = new HashMap();
      List localList = paramWrapperParameter.getWrapperChildren();
      Iterator localIterator = localList.iterator();
      while (localIterator.hasNext())
      {
        ParameterImpl localParameterImpl = (ParameterImpl)localIterator.next();
        wrappedParts.put(localParameterImpl.getName(), new EndpointArgumentsBuilder.WrappedPartBuilder(localParameterImpl.getXMLBridge(), EndpointValueSetter.get(localParameterImpl)));
        assert (localParameterImpl.getBinding() == ParameterBinding.BODY);
      }
    }
    
    public void readRequest(Message paramMessage, Object[] paramArrayOfObject)
      throws JAXBException, XMLStreamException
    {
      readWrappedRequest(paramMessage, paramArrayOfObject);
    }
  }
  
  private static final class SourceBuilder
    extends EndpointArgumentsBuilder.AttachmentBuilder
  {
    SourceBuilder(ParameterImpl paramParameterImpl, EndpointValueSetter paramEndpointValueSetter)
    {
      super(paramEndpointValueSetter);
    }
    
    void mapAttachment(Attachment paramAttachment, Object[] paramArrayOfObject)
    {
      setter.put(paramAttachment.asSource(), paramArrayOfObject);
    }
  }
  
  private static final class StringBuilder
    extends EndpointArgumentsBuilder.AttachmentBuilder
  {
    StringBuilder(ParameterImpl paramParameterImpl, EndpointValueSetter paramEndpointValueSetter)
    {
      super(paramEndpointValueSetter);
    }
    
    void mapAttachment(Attachment paramAttachment, Object[] paramArrayOfObject)
    {
      paramAttachment.getContentType();
      StringDataContentHandler localStringDataContentHandler = new StringDataContentHandler();
      try
      {
        String str = (String)localStringDataContentHandler.getContent(new DataHandlerDataSource(paramAttachment.asDataHandler()));
        setter.put(str, paramArrayOfObject);
      }
      catch (Exception localException)
      {
        throw new WebServiceException(localException);
      }
    }
  }
  
  static final class WrappedPartBuilder
  {
    private final XMLBridge bridge;
    private final EndpointValueSetter setter;
    
    public WrappedPartBuilder(XMLBridge paramXMLBridge, EndpointValueSetter paramEndpointValueSetter)
    {
      bridge = paramXMLBridge;
      setter = paramEndpointValueSetter;
    }
    
    void readRequest(Object[] paramArrayOfObject, XMLStreamReader paramXMLStreamReader, AttachmentSet paramAttachmentSet)
      throws JAXBException
    {
      Object localObject = null;
      AttachmentUnmarshaller localAttachmentUnmarshaller = paramAttachmentSet != null ? new AttachmentUnmarshallerImpl(paramAttachmentSet) : null;
      if ((bridge instanceof RepeatedElementBridge))
      {
        RepeatedElementBridge localRepeatedElementBridge = (RepeatedElementBridge)bridge;
        ArrayList localArrayList = new ArrayList();
        QName localQName = paramXMLStreamReader.getName();
        while ((paramXMLStreamReader.getEventType() == 1) && (localQName.equals(paramXMLStreamReader.getName())))
        {
          localArrayList.add(localRepeatedElementBridge.unmarshal(paramXMLStreamReader, localAttachmentUnmarshaller));
          XMLStreamReaderUtil.toNextTag(paramXMLStreamReader, localQName);
        }
        localObject = localRepeatedElementBridge.collectionHandler().convert(localArrayList);
      }
      else
      {
        localObject = bridge.unmarshal(paramXMLStreamReader, localAttachmentUnmarshaller);
      }
      setter.put(localObject, paramArrayOfObject);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\server\sei\EndpointArgumentsBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
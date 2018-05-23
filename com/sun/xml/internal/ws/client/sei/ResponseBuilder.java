package com.sun.xml.internal.ws.client.sei;

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

public abstract class ResponseBuilder
{
  protected Map<QName, WrappedPartBuilder> wrappedParts = null;
  protected QName wrapperName;
  public static final ResponseBuilder NONE;
  private static final Map<Class, Object> primitiveUninitializedValues;
  
  public ResponseBuilder() {}
  
  public abstract Object readResponse(Message paramMessage, Object[] paramArrayOfObject)
    throws JAXBException, XMLStreamException;
  
  protected Object readWrappedResponse(Message paramMessage, Object[] paramArrayOfObject)
    throws JAXBException, XMLStreamException
  {
    Object localObject1 = null;
    if (!paramMessage.hasPayload()) {
      throw new WebServiceException("No payload. Expecting payload with " + wrapperName + " element");
    }
    XMLStreamReader localXMLStreamReader = paramMessage.readPayload();
    XMLStreamReaderUtil.verifyTag(localXMLStreamReader, wrapperName);
    localXMLStreamReader.nextTag();
    while (localXMLStreamReader.getEventType() == 1)
    {
      WrappedPartBuilder localWrappedPartBuilder = (WrappedPartBuilder)wrappedParts.get(localXMLStreamReader.getName());
      if (localWrappedPartBuilder == null)
      {
        XMLStreamReaderUtil.skipElement(localXMLStreamReader);
        localXMLStreamReader.nextTag();
      }
      else
      {
        Object localObject2 = localWrappedPartBuilder.readResponse(paramArrayOfObject, localXMLStreamReader, paramMessage.getAttachments());
        if (localObject2 != null)
        {
          assert (localObject1 == null);
          localObject1 = localObject2;
        }
      }
      if ((localXMLStreamReader.getEventType() != 1) && (localXMLStreamReader.getEventType() != 2)) {
        XMLStreamReaderUtil.nextElementContent(localXMLStreamReader);
      }
    }
    localXMLStreamReader.close();
    XMLStreamReaderFactory.recycle(localXMLStreamReader);
    return localObject1;
  }
  
  public static Object getVMUninitializedValue(Type paramType)
  {
    return primitiveUninitializedValues.get(paramType);
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
    NONE = new None(null);
    primitiveUninitializedValues = new HashMap();
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
    extends ResponseBuilder
  {
    protected final ValueSetter setter;
    protected final ParameterImpl param;
    private final String pname;
    private final String pname1;
    
    AttachmentBuilder(ParameterImpl paramParameterImpl, ValueSetter paramValueSetter)
    {
      setter = paramValueSetter;
      param = paramParameterImpl;
      pname = paramParameterImpl.getPartName();
      pname1 = ("<" + pname);
    }
    
    public static ResponseBuilder createAttachmentBuilder(ParameterImpl paramParameterImpl, ValueSetter paramValueSetter)
    {
      Class localClass = (Class)getTypeInfotype;
      if (DataHandler.class.isAssignableFrom(localClass)) {
        return new ResponseBuilder.DataHandlerBuilder(paramParameterImpl, paramValueSetter);
      }
      if (byte[].class == localClass) {
        return new ResponseBuilder.ByteArrayBuilder(paramParameterImpl, paramValueSetter);
      }
      if (Source.class.isAssignableFrom(localClass)) {
        return new ResponseBuilder.SourceBuilder(paramParameterImpl, paramValueSetter);
      }
      if (Image.class.isAssignableFrom(localClass)) {
        return new ResponseBuilder.ImageBuilder(paramParameterImpl, paramValueSetter);
      }
      if (InputStream.class == localClass) {
        return new ResponseBuilder.InputStreamBuilder(paramParameterImpl, paramValueSetter);
      }
      if (ResponseBuilder.isXMLMimeType(paramParameterImpl.getBinding().getMimeType())) {
        return new ResponseBuilder.JAXBBuilder(paramParameterImpl, paramValueSetter);
      }
      if (String.class.isAssignableFrom(localClass)) {
        return new ResponseBuilder.StringBuilder(paramParameterImpl, paramValueSetter);
      }
      throw new UnsupportedOperationException("Unexpected Attachment type =" + localClass);
    }
    
    public Object readResponse(Message paramMessage, Object[] paramArrayOfObject)
      throws JAXBException, XMLStreamException
    {
      Iterator localIterator = paramMessage.getAttachments().iterator();
      while (localIterator.hasNext())
      {
        Attachment localAttachment = (Attachment)localIterator.next();
        String str = getWSDLPartName(localAttachment);
        if (str != null) {
          if ((str.equals(pname)) || (str.equals(pname1))) {
            return mapAttachment(localAttachment, paramArrayOfObject);
          }
        }
      }
      return null;
    }
    
    abstract Object mapAttachment(Attachment paramAttachment, Object[] paramArrayOfObject)
      throws JAXBException;
  }
  
  public static final class Body
    extends ResponseBuilder
  {
    private final XMLBridge<?> bridge;
    private final ValueSetter setter;
    
    public Body(XMLBridge<?> paramXMLBridge, ValueSetter paramValueSetter)
    {
      bridge = paramXMLBridge;
      setter = paramValueSetter;
    }
    
    public Object readResponse(Message paramMessage, Object[] paramArrayOfObject)
      throws JAXBException
    {
      return setter.put(paramMessage.readPayloadAsJAXB(bridge), paramArrayOfObject);
    }
  }
  
  private static final class ByteArrayBuilder
    extends ResponseBuilder.AttachmentBuilder
  {
    ByteArrayBuilder(ParameterImpl paramParameterImpl, ValueSetter paramValueSetter)
    {
      super(paramValueSetter);
    }
    
    Object mapAttachment(Attachment paramAttachment, Object[] paramArrayOfObject)
    {
      return setter.put(paramAttachment.asByteArray(), paramArrayOfObject);
    }
  }
  
  public static final class Composite
    extends ResponseBuilder
  {
    private final ResponseBuilder[] builders;
    
    public Composite(ResponseBuilder... paramVarArgs)
    {
      builders = paramVarArgs;
    }
    
    public Composite(Collection<? extends ResponseBuilder> paramCollection)
    {
      this((ResponseBuilder[])paramCollection.toArray(new ResponseBuilder[paramCollection.size()]));
    }
    
    public Object readResponse(Message paramMessage, Object[] paramArrayOfObject)
      throws JAXBException, XMLStreamException
    {
      Object localObject1 = null;
      for (ResponseBuilder localResponseBuilder : builders)
      {
        Object localObject2 = localResponseBuilder.readResponse(paramMessage, paramArrayOfObject);
        if (localObject2 != null)
        {
          assert (localObject1 == null);
          localObject1 = localObject2;
        }
      }
      return localObject1;
    }
  }
  
  private static final class DataHandlerBuilder
    extends ResponseBuilder.AttachmentBuilder
  {
    DataHandlerBuilder(ParameterImpl paramParameterImpl, ValueSetter paramValueSetter)
    {
      super(paramValueSetter);
    }
    
    Object mapAttachment(Attachment paramAttachment, Object[] paramArrayOfObject)
    {
      return setter.put(paramAttachment.asDataHandler(), paramArrayOfObject);
    }
  }
  
  public static final class DocLit
    extends ResponseBuilder
  {
    private final PartBuilder[] parts;
    private final XMLBridge wrapper;
    private boolean dynamicWrapper;
    
    public DocLit(WrapperParameter paramWrapperParameter, ValueSetterFactory paramValueSetterFactory)
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
        if (!localParameterImpl.isIN())
        {
          QName localQName = localParameterImpl.getName();
          if (dynamicWrapper)
          {
            if (wrappedParts == null) {
              wrappedParts = new HashMap();
            }
            XMLBridge localXMLBridge = localParameterImpl.getInlinedRepeatedElementBridge();
            if (localXMLBridge == null) {
              localXMLBridge = localParameterImpl.getXMLBridge();
            }
            wrappedParts.put(localParameterImpl.getName(), new ResponseBuilder.WrappedPartBuilder(localXMLBridge, paramValueSetterFactory.get(localParameterImpl)));
          }
          else
          {
            try
            {
              localArrayList.add(new PartBuilder(paramWrapperParameter.getOwner().getBindingContext().getElementPropertyAccessor(localClass, localQName.getNamespaceURI(), localParameterImpl.getName().getLocalPart()), paramValueSetterFactory.get(localParameterImpl)));
              if ((!$assertionsDisabled) && (localParameterImpl.getBinding() != ParameterBinding.BODY)) {
                throw new AssertionError();
              }
            }
            catch (JAXBException localJAXBException)
            {
              throw new WebServiceException(localClass + " do not have a property of the name " + localQName, localJAXBException);
            }
          }
        }
      }
      parts = ((PartBuilder[])localArrayList.toArray(new PartBuilder[localArrayList.size()]));
    }
    
    public Object readResponse(Message paramMessage, Object[] paramArrayOfObject)
      throws JAXBException, XMLStreamException
    {
      if (dynamicWrapper) {
        return readWrappedResponse(paramMessage, paramArrayOfObject);
      }
      Object localObject1 = null;
      if (parts.length > 0)
      {
        if (!paramMessage.hasPayload()) {
          throw new WebServiceException("No payload. Expecting payload with " + wrapperName + " element");
        }
        XMLStreamReader localXMLStreamReader = paramMessage.readPayload();
        XMLStreamReaderUtil.verifyTag(localXMLStreamReader, wrapperName);
        Object localObject2 = wrapper.unmarshal(localXMLStreamReader, paramMessage.getAttachments() != null ? new AttachmentUnmarshallerImpl(paramMessage.getAttachments()) : null);
        try
        {
          for (PartBuilder localPartBuilder : parts)
          {
            Object localObject3 = localPartBuilder.readResponse(paramArrayOfObject, localObject2);
            if (localObject3 != null)
            {
              assert (localObject1 == null);
              localObject1 = localObject3;
            }
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
      return localObject1;
    }
    
    static final class PartBuilder
    {
      private final PropertyAccessor accessor;
      private final ValueSetter setter;
      
      public PartBuilder(PropertyAccessor paramPropertyAccessor, ValueSetter paramValueSetter)
      {
        accessor = paramPropertyAccessor;
        setter = paramValueSetter;
        assert ((paramPropertyAccessor != null) && (paramValueSetter != null));
      }
      
      final Object readResponse(Object[] paramArrayOfObject, Object paramObject)
      {
        Object localObject = accessor.get(paramObject);
        return setter.put(localObject, paramArrayOfObject);
      }
    }
  }
  
  public static final class Header
    extends ResponseBuilder
  {
    private final XMLBridge<?> bridge;
    private final ValueSetter setter;
    private final QName headerName;
    private final SOAPVersion soapVersion;
    
    public Header(SOAPVersion paramSOAPVersion, QName paramQName, XMLBridge<?> paramXMLBridge, ValueSetter paramValueSetter)
    {
      soapVersion = paramSOAPVersion;
      headerName = paramQName;
      bridge = paramXMLBridge;
      setter = paramValueSetter;
    }
    
    public Header(SOAPVersion paramSOAPVersion, ParameterImpl paramParameterImpl, ValueSetter paramValueSetter)
    {
      this(paramSOAPVersion, getTypeInfotagName, paramParameterImpl.getXMLBridge(), paramValueSetter);
      assert (paramParameterImpl.getOutBinding() == ParameterBinding.HEADER);
    }
    
    private SOAPFaultException createDuplicateHeaderException()
    {
      try
      {
        SOAPFault localSOAPFault = soapVersion.getSOAPFactory().createFault();
        localSOAPFault.setFaultCode(soapVersion.faultCodeServer);
        localSOAPFault.setFaultString(ServerMessages.DUPLICATE_PORT_KNOWN_HEADER(headerName));
        return new SOAPFaultException(localSOAPFault);
      }
      catch (SOAPException localSOAPException)
      {
        throw new WebServiceException(localSOAPException);
      }
    }
    
    public Object readResponse(Message paramMessage, Object[] paramArrayOfObject)
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
        return setter.put(localHeader.readAsJAXB(bridge), paramArrayOfObject);
      }
      return null;
    }
  }
  
  private static final class ImageBuilder
    extends ResponseBuilder.AttachmentBuilder
  {
    ImageBuilder(ParameterImpl paramParameterImpl, ValueSetter paramValueSetter)
    {
      super(paramValueSetter);
    }
    
    Object mapAttachment(Attachment paramAttachment, Object[] paramArrayOfObject)
    {
      InputStream localInputStream = null;
      try
      {
        localInputStream = paramAttachment.asInputStream();
        BufferedImage localBufferedImage = ImageIO.read(localInputStream);
        return setter.put(localBufferedImage, paramArrayOfObject);
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
    extends ResponseBuilder.AttachmentBuilder
  {
    InputStreamBuilder(ParameterImpl paramParameterImpl, ValueSetter paramValueSetter)
    {
      super(paramValueSetter);
    }
    
    Object mapAttachment(Attachment paramAttachment, Object[] paramArrayOfObject)
    {
      return setter.put(paramAttachment.asInputStream(), paramArrayOfObject);
    }
  }
  
  private static final class JAXBBuilder
    extends ResponseBuilder.AttachmentBuilder
  {
    JAXBBuilder(ParameterImpl paramParameterImpl, ValueSetter paramValueSetter)
    {
      super(paramValueSetter);
    }
    
    Object mapAttachment(Attachment paramAttachment, Object[] paramArrayOfObject)
      throws JAXBException
    {
      Object localObject = param.getXMLBridge().unmarshal(paramAttachment.asInputStream());
      return setter.put(localObject, paramArrayOfObject);
    }
  }
  
  static final class None
    extends ResponseBuilder
  {
    private None() {}
    
    public Object readResponse(Message paramMessage, Object[] paramArrayOfObject)
    {
      paramMessage.consume();
      return null;
    }
  }
  
  public static final class NullSetter
    extends ResponseBuilder
  {
    private final ValueSetter setter;
    private final Object nullValue;
    
    public NullSetter(ValueSetter paramValueSetter, Object paramObject)
    {
      assert (paramValueSetter != null);
      nullValue = paramObject;
      setter = paramValueSetter;
    }
    
    public Object readResponse(Message paramMessage, Object[] paramArrayOfObject)
    {
      return setter.put(nullValue, paramArrayOfObject);
    }
  }
  
  public static final class RpcLit
    extends ResponseBuilder
  {
    public RpcLit(WrapperParameter paramWrapperParameter, ValueSetterFactory paramValueSetterFactory)
    {
      assert (getTypeInfotype == WrapperComposite.class);
      wrapperName = paramWrapperParameter.getName();
      wrappedParts = new HashMap();
      List localList = paramWrapperParameter.getWrapperChildren();
      Iterator localIterator = localList.iterator();
      while (localIterator.hasNext())
      {
        ParameterImpl localParameterImpl = (ParameterImpl)localIterator.next();
        wrappedParts.put(localParameterImpl.getName(), new ResponseBuilder.WrappedPartBuilder(localParameterImpl.getXMLBridge(), paramValueSetterFactory.get(localParameterImpl)));
        assert (localParameterImpl.getBinding() == ParameterBinding.BODY);
      }
    }
    
    public Object readResponse(Message paramMessage, Object[] paramArrayOfObject)
      throws JAXBException, XMLStreamException
    {
      return readWrappedResponse(paramMessage, paramArrayOfObject);
    }
  }
  
  private static final class SourceBuilder
    extends ResponseBuilder.AttachmentBuilder
  {
    SourceBuilder(ParameterImpl paramParameterImpl, ValueSetter paramValueSetter)
    {
      super(paramValueSetter);
    }
    
    Object mapAttachment(Attachment paramAttachment, Object[] paramArrayOfObject)
    {
      return setter.put(paramAttachment.asSource(), paramArrayOfObject);
    }
  }
  
  private static final class StringBuilder
    extends ResponseBuilder.AttachmentBuilder
  {
    StringBuilder(ParameterImpl paramParameterImpl, ValueSetter paramValueSetter)
    {
      super(paramValueSetter);
    }
    
    Object mapAttachment(Attachment paramAttachment, Object[] paramArrayOfObject)
    {
      paramAttachment.getContentType();
      StringDataContentHandler localStringDataContentHandler = new StringDataContentHandler();
      try
      {
        String str = (String)localStringDataContentHandler.getContent(new DataHandlerDataSource(paramAttachment.asDataHandler()));
        return setter.put(str, paramArrayOfObject);
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
    private final ValueSetter setter;
    
    public WrappedPartBuilder(XMLBridge paramXMLBridge, ValueSetter paramValueSetter)
    {
      bridge = paramXMLBridge;
      setter = paramValueSetter;
    }
    
    final Object readResponse(Object[] paramArrayOfObject, XMLStreamReader paramXMLStreamReader, AttachmentSet paramAttachmentSet)
      throws JAXBException
    {
      AttachmentUnmarshaller localAttachmentUnmarshaller = paramAttachmentSet != null ? new AttachmentUnmarshallerImpl(paramAttachmentSet) : null;
      Object localObject;
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
      return setter.put(localObject, paramArrayOfObject);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\client\sei\ResponseBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
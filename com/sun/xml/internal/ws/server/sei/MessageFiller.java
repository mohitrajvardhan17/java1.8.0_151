package com.sun.xml.internal.ws.server.sei;

import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.api.message.Headers;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.MessageHeaders;
import com.sun.xml.internal.ws.api.model.ParameterBinding;
import com.sun.xml.internal.ws.message.ByteArrayAttachment;
import com.sun.xml.internal.ws.message.DataHandlerAttachment;
import com.sun.xml.internal.ws.message.JAXBAttachment;
import com.sun.xml.internal.ws.model.ParameterImpl;
import com.sun.xml.internal.ws.spi.db.TypeInfo;
import com.sun.xml.internal.ws.spi.db.XMLBridge;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.UUID;
import javax.activation.DataHandler;
import javax.xml.transform.Source;
import javax.xml.ws.WebServiceException;

public abstract class MessageFiller
{
  protected final int methodPos;
  
  protected MessageFiller(int paramInt)
  {
    methodPos = paramInt;
  }
  
  public abstract void fillIn(Object[] paramArrayOfObject, Object paramObject, Message paramMessage);
  
  private static boolean isXMLMimeType(String paramString)
  {
    return (paramString.equals("text/xml")) || (paramString.equals("application/xml"));
  }
  
  public static abstract class AttachmentFiller
    extends MessageFiller
  {
    protected final ParameterImpl param;
    protected final ValueGetter getter;
    protected final String mimeType;
    private final String contentIdPart;
    
    protected AttachmentFiller(ParameterImpl paramParameterImpl, ValueGetter paramValueGetter)
    {
      super();
      param = paramParameterImpl;
      getter = paramValueGetter;
      mimeType = paramParameterImpl.getBinding().getMimeType();
      try
      {
        contentIdPart = (URLEncoder.encode(paramParameterImpl.getPartName(), "UTF-8") + '=');
      }
      catch (UnsupportedEncodingException localUnsupportedEncodingException)
      {
        throw new WebServiceException(localUnsupportedEncodingException);
      }
    }
    
    public static MessageFiller createAttachmentFiller(ParameterImpl paramParameterImpl, ValueGetter paramValueGetter)
    {
      Class localClass = (Class)getTypeInfotype;
      if ((DataHandler.class.isAssignableFrom(localClass)) || (Source.class.isAssignableFrom(localClass))) {
        return new MessageFiller.DataHandlerFiller(paramParameterImpl, paramValueGetter);
      }
      if (byte[].class == localClass) {
        return new MessageFiller.ByteArrayFiller(paramParameterImpl, paramValueGetter);
      }
      if (MessageFiller.isXMLMimeType(paramParameterImpl.getBinding().getMimeType())) {
        return new MessageFiller.JAXBFiller(paramParameterImpl, paramValueGetter);
      }
      return new MessageFiller.DataHandlerFiller(paramParameterImpl, paramValueGetter);
    }
    
    String getContentId()
    {
      return contentIdPart + UUID.randomUUID() + "@jaxws.sun.com";
    }
  }
  
  private static class ByteArrayFiller
    extends MessageFiller.AttachmentFiller
  {
    protected ByteArrayFiller(ParameterImpl paramParameterImpl, ValueGetter paramValueGetter)
    {
      super(paramValueGetter);
    }
    
    public void fillIn(Object[] paramArrayOfObject, Object paramObject, Message paramMessage)
    {
      String str = getContentId();
      Object localObject = methodPos == -1 ? paramObject : getter.get(paramArrayOfObject[methodPos]);
      if (localObject != null)
      {
        ByteArrayAttachment localByteArrayAttachment = new ByteArrayAttachment(str, (byte[])localObject, mimeType);
        paramMessage.getAttachments().add(localByteArrayAttachment);
      }
    }
  }
  
  private static class DataHandlerFiller
    extends MessageFiller.AttachmentFiller
  {
    protected DataHandlerFiller(ParameterImpl paramParameterImpl, ValueGetter paramValueGetter)
    {
      super(paramValueGetter);
    }
    
    public void fillIn(Object[] paramArrayOfObject, Object paramObject, Message paramMessage)
    {
      String str = getContentId();
      Object localObject = methodPos == -1 ? paramObject : getter.get(paramArrayOfObject[methodPos]);
      DataHandler localDataHandler = (localObject instanceof DataHandler) ? (DataHandler)localObject : new DataHandler(localObject, mimeType);
      DataHandlerAttachment localDataHandlerAttachment = new DataHandlerAttachment(str, localDataHandler);
      paramMessage.getAttachments().add(localDataHandlerAttachment);
    }
  }
  
  public static final class Header
    extends MessageFiller
  {
    private final XMLBridge bridge;
    private final ValueGetter getter;
    
    public Header(int paramInt, XMLBridge paramXMLBridge, ValueGetter paramValueGetter)
    {
      super();
      bridge = paramXMLBridge;
      getter = paramValueGetter;
    }
    
    public void fillIn(Object[] paramArrayOfObject, Object paramObject, Message paramMessage)
    {
      Object localObject = methodPos == -1 ? paramObject : getter.get(paramArrayOfObject[methodPos]);
      paramMessage.getHeaders().add(Headers.create(bridge, localObject));
    }
  }
  
  private static class JAXBFiller
    extends MessageFiller.AttachmentFiller
  {
    protected JAXBFiller(ParameterImpl paramParameterImpl, ValueGetter paramValueGetter)
    {
      super(paramValueGetter);
    }
    
    public void fillIn(Object[] paramArrayOfObject, Object paramObject, Message paramMessage)
    {
      String str = getContentId();
      Object localObject = methodPos == -1 ? paramObject : getter.get(paramArrayOfObject[methodPos]);
      JAXBAttachment localJAXBAttachment = new JAXBAttachment(str, localObject, param.getXMLBridge(), mimeType);
      paramMessage.getAttachments().add(localJAXBAttachment);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\server\sei\MessageFiller.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
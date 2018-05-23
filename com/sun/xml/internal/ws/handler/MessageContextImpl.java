package com.sun.xml.internal.ws.handler;

import com.sun.xml.internal.ws.api.message.Attachment;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Packet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.MessageContext.Scope;

class MessageContextImpl
  implements MessageContext
{
  private final Set<String> handlerScopeProps;
  private final Packet packet;
  private final Map<String, Object> asMapIncludingInvocationProperties;
  
  public MessageContextImpl(Packet paramPacket)
  {
    packet = paramPacket;
    asMapIncludingInvocationProperties = paramPacket.asMapIncludingInvocationProperties();
    handlerScopeProps = paramPacket.getHandlerScopePropertyNames(false);
  }
  
  protected void updatePacket()
  {
    throw new UnsupportedOperationException("wrong call");
  }
  
  public void setScope(String paramString, MessageContext.Scope paramScope)
  {
    if (!containsKey(paramString)) {
      throw new IllegalArgumentException("Property " + paramString + " does not exist.");
    }
    if (paramScope == MessageContext.Scope.APPLICATION) {
      handlerScopeProps.remove(paramString);
    } else {
      handlerScopeProps.add(paramString);
    }
  }
  
  public MessageContext.Scope getScope(String paramString)
  {
    if (!containsKey(paramString)) {
      throw new IllegalArgumentException("Property " + paramString + " does not exist.");
    }
    if (handlerScopeProps.contains(paramString)) {
      return MessageContext.Scope.HANDLER;
    }
    return MessageContext.Scope.APPLICATION;
  }
  
  public int size()
  {
    return asMapIncludingInvocationProperties.size();
  }
  
  public boolean isEmpty()
  {
    return asMapIncludingInvocationProperties.isEmpty();
  }
  
  public boolean containsKey(Object paramObject)
  {
    return asMapIncludingInvocationProperties.containsKey(paramObject);
  }
  
  public boolean containsValue(Object paramObject)
  {
    return asMapIncludingInvocationProperties.containsValue(paramObject);
  }
  
  public Object put(String paramString, Object paramObject)
  {
    if (!asMapIncludingInvocationProperties.containsKey(paramString)) {
      handlerScopeProps.add(paramString);
    }
    return asMapIncludingInvocationProperties.put(paramString, paramObject);
  }
  
  public Object get(Object paramObject)
  {
    if (paramObject == null) {
      return null;
    }
    Object localObject1 = asMapIncludingInvocationProperties.get(paramObject);
    if ((paramObject.equals("javax.xml.ws.binding.attachments.outbound")) || (paramObject.equals("javax.xml.ws.binding.attachments.inbound")))
    {
      Object localObject2 = (Map)localObject1;
      if (localObject2 == null) {
        localObject2 = new HashMap();
      }
      AttachmentSet localAttachmentSet = packet.getMessage().getAttachments();
      Iterator localIterator = localAttachmentSet.iterator();
      while (localIterator.hasNext())
      {
        Attachment localAttachment = (Attachment)localIterator.next();
        String str = localAttachment.getContentId();
        if (str.indexOf("@jaxws.sun.com") == -1)
        {
          Object localObject3 = ((Map)localObject2).get(str);
          if (localObject3 == null)
          {
            localObject3 = ((Map)localObject2).get("<" + str + ">");
            if (localObject3 == null) {
              ((Map)localObject2).put(localAttachment.getContentId(), localAttachment.asDataHandler());
            }
          }
        }
        else
        {
          ((Map)localObject2).put(localAttachment.getContentId(), localAttachment.asDataHandler());
        }
      }
      return localObject2;
    }
    return localObject1;
  }
  
  public void putAll(Map<? extends String, ? extends Object> paramMap)
  {
    Iterator localIterator = paramMap.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      if (!asMapIncludingInvocationProperties.containsKey(str)) {
        handlerScopeProps.add(str);
      }
    }
    asMapIncludingInvocationProperties.putAll(paramMap);
  }
  
  public void clear()
  {
    asMapIncludingInvocationProperties.clear();
  }
  
  public Object remove(Object paramObject)
  {
    handlerScopeProps.remove(paramObject);
    return asMapIncludingInvocationProperties.remove(paramObject);
  }
  
  public Set<String> keySet()
  {
    return asMapIncludingInvocationProperties.keySet();
  }
  
  public Set<Map.Entry<String, Object>> entrySet()
  {
    return asMapIncludingInvocationProperties.entrySet();
  }
  
  public Collection<Object> values()
  {
    return asMapIncludingInvocationProperties.values();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\handler\MessageContextImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
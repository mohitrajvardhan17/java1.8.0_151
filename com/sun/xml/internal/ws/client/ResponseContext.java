package com.sun.xml.internal.ws.client;

import com.sun.xml.internal.ws.api.message.Attachment;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Packet;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class ResponseContext
  extends AbstractMap<String, Object>
{
  private final Packet packet;
  private Set<Map.Entry<String, Object>> entrySet;
  
  public ResponseContext(Packet paramPacket)
  {
    packet = paramPacket;
  }
  
  public boolean containsKey(Object paramObject)
  {
    if (packet.supports(paramObject)) {
      return packet.containsKey(paramObject);
    }
    if (packet.invocationProperties.containsKey(paramObject)) {
      return !packet.getHandlerScopePropertyNames(true).contains(paramObject);
    }
    return false;
  }
  
  public Object get(Object paramObject)
  {
    if (packet.supports(paramObject)) {
      return packet.get(paramObject);
    }
    if (packet.getHandlerScopePropertyNames(true).contains(paramObject)) {
      return null;
    }
    Object localObject1 = packet.invocationProperties.get(paramObject);
    if (paramObject.equals("javax.xml.ws.binding.attachments.inbound"))
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
        ((Map)localObject2).put(localAttachment.getContentId(), localAttachment.asDataHandler());
      }
      return localObject2;
    }
    return localObject1;
  }
  
  public Object put(String paramString, Object paramObject)
  {
    throw new UnsupportedOperationException();
  }
  
  public Object remove(Object paramObject)
  {
    throw new UnsupportedOperationException();
  }
  
  public void putAll(Map<? extends String, ? extends Object> paramMap)
  {
    throw new UnsupportedOperationException();
  }
  
  public void clear()
  {
    throw new UnsupportedOperationException();
  }
  
  public Set<Map.Entry<String, Object>> entrySet()
  {
    if (entrySet == null)
    {
      HashMap localHashMap = new HashMap();
      localHashMap.putAll(packet.invocationProperties);
      localHashMap.keySet().removeAll(packet.getHandlerScopePropertyNames(true));
      localHashMap.putAll(packet.createMapView());
      entrySet = Collections.unmodifiableSet(localHashMap.entrySet());
    }
    return entrySet;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\client\ResponseContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
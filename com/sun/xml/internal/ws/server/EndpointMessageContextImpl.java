package com.sun.xml.internal.ws.server;

import com.sun.xml.internal.ws.api.message.Attachment;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Packet;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.MessageContext.Scope;

public final class EndpointMessageContextImpl
  extends AbstractMap<String, Object>
  implements MessageContext
{
  private Set<Map.Entry<String, Object>> entrySet;
  private final Packet packet;
  
  public EndpointMessageContextImpl(Packet paramPacket)
  {
    packet = paramPacket;
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
        ((Map)localObject2).put(localAttachment.getContentId(), localAttachment.asDataHandler());
      }
      return localObject2;
    }
    return localObject1;
  }
  
  public Object put(String paramString, Object paramObject)
  {
    if (packet.supports(paramString)) {
      return packet.put(paramString, paramObject);
    }
    Object localObject = packet.invocationProperties.get(paramString);
    if (localObject != null)
    {
      if (packet.getHandlerScopePropertyNames(true).contains(paramString)) {
        throw new IllegalArgumentException("Cannot overwrite property in HANDLER scope");
      }
      packet.invocationProperties.put(paramString, paramObject);
      return localObject;
    }
    packet.invocationProperties.put(paramString, paramObject);
    return null;
  }
  
  public Object remove(Object paramObject)
  {
    if (packet.supports(paramObject)) {
      return packet.remove(paramObject);
    }
    Object localObject = packet.invocationProperties.get(paramObject);
    if (localObject != null)
    {
      if (packet.getHandlerScopePropertyNames(true).contains(paramObject)) {
        throw new IllegalArgumentException("Cannot remove property in HANDLER scope");
      }
      packet.invocationProperties.remove(paramObject);
      return localObject;
    }
    return null;
  }
  
  public Set<Map.Entry<String, Object>> entrySet()
  {
    if (entrySet == null) {
      entrySet = new EntrySet(null);
    }
    return entrySet;
  }
  
  public void setScope(String paramString, MessageContext.Scope paramScope)
  {
    throw new UnsupportedOperationException("All the properties in this context are in APPLICATION scope. Cannot do setScope().");
  }
  
  public MessageContext.Scope getScope(String paramString)
  {
    throw new UnsupportedOperationException("All the properties in this context are in APPLICATION scope. Cannot do getScope().");
  }
  
  private Map<String, Object> createBackupMap()
  {
    HashMap localHashMap = new HashMap();
    localHashMap.putAll(packet.createMapView());
    Set localSet = packet.getHandlerScopePropertyNames(true);
    Iterator localIterator = packet.invocationProperties.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      if (!localSet.contains(localEntry.getKey())) {
        localHashMap.put(localEntry.getKey(), localEntry.getValue());
      }
    }
    return localHashMap;
  }
  
  private class EntrySet
    extends AbstractSet<Map.Entry<String, Object>>
  {
    private EntrySet() {}
    
    public Iterator<Map.Entry<String, Object>> iterator()
    {
      final Iterator localIterator = EndpointMessageContextImpl.this.createBackupMap().entrySet().iterator();
      new Iterator()
      {
        Map.Entry<String, Object> cur;
        
        public boolean hasNext()
        {
          return localIterator.hasNext();
        }
        
        public Map.Entry<String, Object> next()
        {
          cur = ((Map.Entry)localIterator.next());
          return cur;
        }
        
        public void remove()
        {
          localIterator.remove();
          remove(cur.getKey());
        }
      };
    }
    
    public int size()
    {
      return EndpointMessageContextImpl.this.createBackupMap().size();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\server\EndpointMessageContextImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
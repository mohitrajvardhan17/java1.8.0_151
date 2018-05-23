package com.sun.xml.internal.ws.handler;

import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Packet;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.MessageContext.Scope;

public abstract class MessageUpdatableContext
  implements MessageContext
{
  final Packet packet;
  private MessageContextImpl ctxt;
  
  public MessageUpdatableContext(Packet paramPacket)
  {
    ctxt = new MessageContextImpl(paramPacket);
    packet = paramPacket;
  }
  
  abstract void updateMessage();
  
  Message getPacketMessage()
  {
    updateMessage();
    return packet.getMessage();
  }
  
  abstract void setPacketMessage(Message paramMessage);
  
  public final void updatePacket()
  {
    updateMessage();
  }
  
  MessageContextImpl getMessageContext()
  {
    return ctxt;
  }
  
  public void setScope(String paramString, MessageContext.Scope paramScope)
  {
    ctxt.setScope(paramString, paramScope);
  }
  
  public MessageContext.Scope getScope(String paramString)
  {
    return ctxt.getScope(paramString);
  }
  
  public void clear()
  {
    ctxt.clear();
  }
  
  public boolean containsKey(Object paramObject)
  {
    return ctxt.containsKey(paramObject);
  }
  
  public boolean containsValue(Object paramObject)
  {
    return ctxt.containsValue(paramObject);
  }
  
  public Set<Map.Entry<String, Object>> entrySet()
  {
    return ctxt.entrySet();
  }
  
  public Object get(Object paramObject)
  {
    return ctxt.get(paramObject);
  }
  
  public boolean isEmpty()
  {
    return ctxt.isEmpty();
  }
  
  public Set<String> keySet()
  {
    return ctxt.keySet();
  }
  
  public Object put(String paramString, Object paramObject)
  {
    return ctxt.put(paramString, paramObject);
  }
  
  public void putAll(Map<? extends String, ? extends Object> paramMap)
  {
    ctxt.putAll(paramMap);
  }
  
  public Object remove(Object paramObject)
  {
    return ctxt.remove(paramObject);
  }
  
  public int size()
  {
    return ctxt.size();
  }
  
  public Collection<Object> values()
  {
    return ctxt.values();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\handler\MessageUpdatableContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
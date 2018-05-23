package com.sun.xml.internal.ws.client;

import com.oracle.webservices.internal.api.message.BaseDistributedPropertySet;
import com.oracle.webservices.internal.api.message.BasePropertySet.PropertyMap;
import com.oracle.webservices.internal.api.message.PropertySet.Property;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.EndpointAddress;
import com.sun.xml.internal.ws.api.PropertySet;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.transport.Headers;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

public final class RequestContext
  extends BaseDistributedPropertySet
{
  private static final Logger LOGGER = Logger.getLogger(RequestContext.class.getName());
  private static ContentNegotiation defaultContentNegotiation = ContentNegotiation.obtainFromSystemProperty();
  @NotNull
  private EndpointAddress endpointAddress;
  public ContentNegotiation contentNegotiation = defaultContentNegotiation;
  private String soapAction;
  private Boolean soapActionUse;
  private static final BasePropertySet.PropertyMap propMap = parse(RequestContext.class);
  
  /**
   * @deprecated
   */
  public void addSatellite(@NotNull PropertySet paramPropertySet)
  {
    super.addSatellite(paramPropertySet);
  }
  
  /**
   * @deprecated
   */
  @PropertySet.Property({"javax.xml.ws.service.endpoint.address"})
  public String getEndPointAddressString()
  {
    return endpointAddress != null ? endpointAddress.toString() : null;
  }
  
  public void setEndPointAddressString(String paramString)
  {
    if (paramString == null) {
      throw new IllegalArgumentException();
    }
    endpointAddress = EndpointAddress.create(paramString);
  }
  
  public void setEndpointAddress(@NotNull EndpointAddress paramEndpointAddress)
  {
    endpointAddress = paramEndpointAddress;
  }
  
  @NotNull
  public EndpointAddress getEndpointAddress()
  {
    return endpointAddress;
  }
  
  @PropertySet.Property({"com.sun.xml.internal.ws.client.ContentNegotiation"})
  public String getContentNegotiationString()
  {
    return contentNegotiation.toString();
  }
  
  public void setContentNegotiationString(String paramString)
  {
    if (paramString == null) {
      contentNegotiation = ContentNegotiation.none;
    } else {
      try
      {
        contentNegotiation = ContentNegotiation.valueOf(paramString);
      }
      catch (IllegalArgumentException localIllegalArgumentException)
      {
        contentNegotiation = ContentNegotiation.none;
      }
    }
  }
  
  @PropertySet.Property({"javax.xml.ws.soap.http.soapaction.uri"})
  public String getSoapAction()
  {
    return soapAction;
  }
  
  public void setSoapAction(String paramString)
  {
    soapAction = paramString;
  }
  
  @PropertySet.Property({"javax.xml.ws.soap.http.soapaction.use"})
  public Boolean getSoapActionUse()
  {
    return soapActionUse;
  }
  
  public void setSoapActionUse(Boolean paramBoolean)
  {
    soapActionUse = paramBoolean;
  }
  
  RequestContext() {}
  
  private RequestContext(RequestContext paramRequestContext)
  {
    Iterator localIterator = paramRequestContext.asMapLocal().entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      if (!propMap.containsKey(localEntry.getKey())) {
        asMap().put(localEntry.getKey(), localEntry.getValue());
      }
    }
    endpointAddress = endpointAddress;
    soapAction = soapAction;
    soapActionUse = soapActionUse;
    contentNegotiation = contentNegotiation;
    paramRequestContext.copySatelliteInto(this);
  }
  
  public Object get(Object paramObject)
  {
    if (supports(paramObject)) {
      return super.get(paramObject);
    }
    return asMap().get(paramObject);
  }
  
  public Object put(String paramString, Object paramObject)
  {
    if (supports(paramString)) {
      return super.put(paramString, paramObject);
    }
    return asMap().put(paramString, paramObject);
  }
  
  public void fill(Packet paramPacket, boolean paramBoolean)
  {
    if (endpointAddress != null) {
      endpointAddress = endpointAddress;
    }
    contentNegotiation = contentNegotiation;
    fillSOAPAction(paramPacket, paramBoolean);
    mergeRequestHeaders(paramPacket);
    HashSet localHashSet = new HashSet();
    copySatelliteInto(paramPacket);
    Iterator localIterator = asMapLocal().keySet().iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      if (!supportsLocal(str)) {
        localHashSet.add(str);
      }
      if (!propMap.containsKey(str))
      {
        Object localObject = asMapLocal().get(str);
        if (paramPacket.supports(str)) {
          paramPacket.put(str, localObject);
        } else {
          invocationProperties.put(str, localObject);
        }
      }
    }
    if (!localHashSet.isEmpty()) {
      paramPacket.getHandlerScopePropertyNames(false).addAll(localHashSet);
    }
  }
  
  private void mergeRequestHeaders(Packet paramPacket)
  {
    Headers localHeaders = (Headers)invocationProperties.get("javax.xml.ws.http.request.headers");
    Map localMap = (Map)asMap().get("javax.xml.ws.http.request.headers");
    if ((localHeaders != null) && (localMap != null))
    {
      Iterator localIterator = localMap.entrySet().iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        String str = (String)localEntry.getKey();
        if ((str != null) && (str.trim().length() != 0))
        {
          List localList = (List)localHeaders.get(str);
          if (localList != null) {
            localList.addAll((Collection)localEntry.getValue());
          } else {
            localHeaders.put(str, localMap.get(str));
          }
        }
      }
      asMap().put("javax.xml.ws.http.request.headers", localHeaders);
    }
  }
  
  private void fillSOAPAction(Packet paramPacket, boolean paramBoolean)
  {
    boolean bool = packetTakesPriorityOverRequestContext;
    String str = bool ? soapAction : soapAction;
    Boolean localBoolean = bool ? (Boolean)invocationProperties.get("javax.xml.ws.soap.http.soapaction.use") : soapActionUse;
    if (((localBoolean != null) && (localBoolean.booleanValue())) || ((localBoolean == null) && (paramBoolean) && (str != null))) {
      soapAction = str;
    }
    if ((!paramBoolean) && ((localBoolean == null) || (!localBoolean.booleanValue())) && (str != null)) {
      LOGGER.warning("BindingProvider.SOAPACTION_URI_PROPERTY is set in the RequestContext but is ineffective, Either set BindingProvider.SOAPACTION_USE_PROPERTY to true or enable AddressingFeature");
    }
  }
  
  public RequestContext copy()
  {
    return new RequestContext(this);
  }
  
  protected BasePropertySet.PropertyMap getPropertyMap()
  {
    return propMap;
  }
  
  protected boolean mapAllowsAdditionalProperties()
  {
    return true;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\client\RequestContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
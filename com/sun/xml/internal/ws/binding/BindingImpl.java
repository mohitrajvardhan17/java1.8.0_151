package com.sun.xml.internal.ws.binding;

import com.oracle.webservices.internal.api.EnvelopeStyleFeature;
import com.oracle.webservices.internal.api.message.MessageContextFactory;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.BindingID;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.pipe.Codec;
import com.sun.xml.internal.ws.client.HandlerConfiguration;
import com.sun.xml.internal.ws.developer.BindingTypeFeature;
import com.sun.xml.internal.ws.developer.MemberSubmissionAddressingFeature;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.activation.CommandInfo;
import javax.activation.CommandMap;
import javax.activation.MailcapCommandMap;
import javax.xml.namespace.QName;
import javax.xml.ws.Service.Mode;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.soap.AddressingFeature;

public abstract class BindingImpl
  implements WSBinding
{
  protected static final WebServiceFeature[] EMPTY_FEATURES = new WebServiceFeature[0];
  private HandlerConfiguration handlerConfig;
  private final Set<QName> addedHeaders = new HashSet();
  private final Set<QName> knownHeaders = new HashSet();
  private final Set<QName> unmodKnownHeaders = Collections.unmodifiableSet(knownHeaders);
  private final BindingID bindingId;
  protected final WebServiceFeatureList features;
  protected final Map<QName, WebServiceFeatureList> operationFeatures = new HashMap();
  protected final Map<QName, WebServiceFeatureList> inputMessageFeatures = new HashMap();
  protected final Map<QName, WebServiceFeatureList> outputMessageFeatures = new HashMap();
  protected final Map<MessageKey, WebServiceFeatureList> faultMessageFeatures = new HashMap();
  protected Service.Mode serviceMode = Service.Mode.PAYLOAD;
  protected MessageContextFactory messageContextFactory;
  
  protected BindingImpl(BindingID paramBindingID, WebServiceFeature... paramVarArgs)
  {
    bindingId = paramBindingID;
    handlerConfig = new HandlerConfiguration(Collections.emptySet(), Collections.emptyList());
    if (handlerConfig.getHandlerKnownHeaders() != null) {
      knownHeaders.addAll(handlerConfig.getHandlerKnownHeaders());
    }
    features = new WebServiceFeatureList(paramVarArgs);
    features.validate();
  }
  
  @NotNull
  public List<Handler> getHandlerChain()
  {
    return handlerConfig.getHandlerChain();
  }
  
  public HandlerConfiguration getHandlerConfig()
  {
    return handlerConfig;
  }
  
  protected void setHandlerConfig(HandlerConfiguration paramHandlerConfiguration)
  {
    handlerConfig = paramHandlerConfiguration;
    knownHeaders.clear();
    knownHeaders.addAll(addedHeaders);
    if ((paramHandlerConfiguration != null) && (paramHandlerConfiguration.getHandlerKnownHeaders() != null)) {
      knownHeaders.addAll(paramHandlerConfiguration.getHandlerKnownHeaders());
    }
  }
  
  public void setMode(@NotNull Service.Mode paramMode)
  {
    serviceMode = paramMode;
  }
  
  public Set<QName> getKnownHeaders()
  {
    return unmodKnownHeaders;
  }
  
  public boolean addKnownHeader(QName paramQName)
  {
    addedHeaders.add(paramQName);
    return knownHeaders.add(paramQName);
  }
  
  @NotNull
  public BindingID getBindingId()
  {
    return bindingId;
  }
  
  public final SOAPVersion getSOAPVersion()
  {
    return bindingId.getSOAPVersion();
  }
  
  public AddressingVersion getAddressingVersion()
  {
    AddressingVersion localAddressingVersion;
    if (features.isEnabled(AddressingFeature.class)) {
      localAddressingVersion = AddressingVersion.W3C;
    } else if (features.isEnabled(MemberSubmissionAddressingFeature.class)) {
      localAddressingVersion = AddressingVersion.MEMBER;
    } else {
      localAddressingVersion = null;
    }
    return localAddressingVersion;
  }
  
  @NotNull
  public final Codec createCodec()
  {
    initializeJavaActivationHandlers();
    return bindingId.createEncoder(this);
  }
  
  public static void initializeJavaActivationHandlers()
  {
    try
    {
      CommandMap localCommandMap = CommandMap.getDefaultCommandMap();
      if ((localCommandMap instanceof MailcapCommandMap))
      {
        MailcapCommandMap localMailcapCommandMap = (MailcapCommandMap)localCommandMap;
        if (!cmdMapInitialized(localMailcapCommandMap))
        {
          localMailcapCommandMap.addMailcap("text/xml;;x-java-content-handler=com.sun.xml.internal.ws.encoding.XmlDataContentHandler");
          localMailcapCommandMap.addMailcap("application/xml;;x-java-content-handler=com.sun.xml.internal.ws.encoding.XmlDataContentHandler");
          localMailcapCommandMap.addMailcap("image/*;;x-java-content-handler=com.sun.xml.internal.ws.encoding.ImageDataContentHandler");
          localMailcapCommandMap.addMailcap("text/plain;;x-java-content-handler=com.sun.xml.internal.ws.encoding.StringDataContentHandler");
        }
      }
    }
    catch (Throwable localThrowable) {}
  }
  
  private static boolean cmdMapInitialized(MailcapCommandMap paramMailcapCommandMap)
  {
    CommandInfo[] arrayOfCommandInfo1 = paramMailcapCommandMap.getAllCommands("text/xml");
    if ((arrayOfCommandInfo1 == null) || (arrayOfCommandInfo1.length == 0)) {
      return false;
    }
    String str1 = "com.sun.xml.internal.messaging.saaj.soap.XmlDataContentHandler";
    String str2 = "com.sun.xml.internal.ws.encoding.XmlDataContentHandler";
    for (CommandInfo localCommandInfo : arrayOfCommandInfo1)
    {
      String str3 = localCommandInfo.getCommandClass();
      if ((str1.equals(str3)) || (str2.equals(str3))) {
        return true;
      }
    }
    return false;
  }
  
  public static BindingImpl create(@NotNull BindingID paramBindingID)
  {
    if (paramBindingID.equals(BindingID.XML_HTTP)) {
      return new HTTPBindingImpl();
    }
    return new SOAPBindingImpl(paramBindingID);
  }
  
  public static BindingImpl create(@NotNull BindingID paramBindingID, WebServiceFeature[] paramArrayOfWebServiceFeature)
  {
    for (WebServiceFeature localWebServiceFeature : paramArrayOfWebServiceFeature) {
      if ((localWebServiceFeature instanceof BindingTypeFeature))
      {
        BindingTypeFeature localBindingTypeFeature = (BindingTypeFeature)localWebServiceFeature;
        paramBindingID = BindingID.parse(localBindingTypeFeature.getBindingId());
      }
    }
    if (paramBindingID.equals(BindingID.XML_HTTP)) {
      return new HTTPBindingImpl(paramArrayOfWebServiceFeature);
    }
    return new SOAPBindingImpl(paramBindingID, paramArrayOfWebServiceFeature);
  }
  
  public static WSBinding getDefaultBinding()
  {
    return new SOAPBindingImpl(BindingID.SOAP11_HTTP);
  }
  
  public String getBindingID()
  {
    return bindingId.toString();
  }
  
  @Nullable
  public <F extends WebServiceFeature> F getFeature(@NotNull Class<F> paramClass)
  {
    return features.get(paramClass);
  }
  
  @Nullable
  public <F extends WebServiceFeature> F getOperationFeature(@NotNull Class<F> paramClass, @NotNull QName paramQName)
  {
    WebServiceFeatureList localWebServiceFeatureList = (WebServiceFeatureList)operationFeatures.get(paramQName);
    return FeatureListUtil.mergeFeature(paramClass, localWebServiceFeatureList, features);
  }
  
  public boolean isFeatureEnabled(@NotNull Class<? extends WebServiceFeature> paramClass)
  {
    return features.isEnabled(paramClass);
  }
  
  public boolean isOperationFeatureEnabled(@NotNull Class<? extends WebServiceFeature> paramClass, @NotNull QName paramQName)
  {
    WebServiceFeatureList localWebServiceFeatureList = (WebServiceFeatureList)operationFeatures.get(paramQName);
    return FeatureListUtil.isFeatureEnabled(paramClass, localWebServiceFeatureList, features);
  }
  
  @NotNull
  public WebServiceFeatureList getFeatures()
  {
    if (!isFeatureEnabled(EnvelopeStyleFeature.class))
    {
      WebServiceFeature[] arrayOfWebServiceFeature = { getSOAPVersion().toFeature() };
      features.mergeFeatures(arrayOfWebServiceFeature, false);
    }
    return features;
  }
  
  @NotNull
  public WebServiceFeatureList getOperationFeatures(@NotNull QName paramQName)
  {
    WebServiceFeatureList localWebServiceFeatureList = (WebServiceFeatureList)operationFeatures.get(paramQName);
    return FeatureListUtil.mergeList(new WebServiceFeatureList[] { localWebServiceFeatureList, features });
  }
  
  @NotNull
  public WebServiceFeatureList getInputMessageFeatures(@NotNull QName paramQName)
  {
    WebServiceFeatureList localWebServiceFeatureList1 = (WebServiceFeatureList)operationFeatures.get(paramQName);
    WebServiceFeatureList localWebServiceFeatureList2 = (WebServiceFeatureList)inputMessageFeatures.get(paramQName);
    return FeatureListUtil.mergeList(new WebServiceFeatureList[] { localWebServiceFeatureList1, localWebServiceFeatureList2, features });
  }
  
  @NotNull
  public WebServiceFeatureList getOutputMessageFeatures(@NotNull QName paramQName)
  {
    WebServiceFeatureList localWebServiceFeatureList1 = (WebServiceFeatureList)operationFeatures.get(paramQName);
    WebServiceFeatureList localWebServiceFeatureList2 = (WebServiceFeatureList)outputMessageFeatures.get(paramQName);
    return FeatureListUtil.mergeList(new WebServiceFeatureList[] { localWebServiceFeatureList1, localWebServiceFeatureList2, features });
  }
  
  @NotNull
  public WebServiceFeatureList getFaultMessageFeatures(@NotNull QName paramQName1, @NotNull QName paramQName2)
  {
    WebServiceFeatureList localWebServiceFeatureList1 = (WebServiceFeatureList)operationFeatures.get(paramQName1);
    WebServiceFeatureList localWebServiceFeatureList2 = (WebServiceFeatureList)faultMessageFeatures.get(new MessageKey(paramQName1, paramQName2));
    return FeatureListUtil.mergeList(new WebServiceFeatureList[] { localWebServiceFeatureList1, localWebServiceFeatureList2, features });
  }
  
  public void setOperationFeatures(@NotNull QName paramQName, WebServiceFeature... paramVarArgs)
  {
    if (paramVarArgs != null)
    {
      WebServiceFeatureList localWebServiceFeatureList = (WebServiceFeatureList)operationFeatures.get(paramQName);
      if (localWebServiceFeatureList == null) {
        localWebServiceFeatureList = new WebServiceFeatureList();
      }
      for (WebServiceFeature localWebServiceFeature : paramVarArgs) {
        localWebServiceFeatureList.add(localWebServiceFeature);
      }
      operationFeatures.put(paramQName, localWebServiceFeatureList);
    }
  }
  
  public void setInputMessageFeatures(@NotNull QName paramQName, WebServiceFeature... paramVarArgs)
  {
    if (paramVarArgs != null)
    {
      WebServiceFeatureList localWebServiceFeatureList = (WebServiceFeatureList)inputMessageFeatures.get(paramQName);
      if (localWebServiceFeatureList == null) {
        localWebServiceFeatureList = new WebServiceFeatureList();
      }
      for (WebServiceFeature localWebServiceFeature : paramVarArgs) {
        localWebServiceFeatureList.add(localWebServiceFeature);
      }
      inputMessageFeatures.put(paramQName, localWebServiceFeatureList);
    }
  }
  
  public void setOutputMessageFeatures(@NotNull QName paramQName, WebServiceFeature... paramVarArgs)
  {
    if (paramVarArgs != null)
    {
      WebServiceFeatureList localWebServiceFeatureList = (WebServiceFeatureList)outputMessageFeatures.get(paramQName);
      if (localWebServiceFeatureList == null) {
        localWebServiceFeatureList = new WebServiceFeatureList();
      }
      for (WebServiceFeature localWebServiceFeature : paramVarArgs) {
        localWebServiceFeatureList.add(localWebServiceFeature);
      }
      outputMessageFeatures.put(paramQName, localWebServiceFeatureList);
    }
  }
  
  public void setFaultMessageFeatures(@NotNull QName paramQName1, @NotNull QName paramQName2, WebServiceFeature... paramVarArgs)
  {
    if (paramVarArgs != null)
    {
      MessageKey localMessageKey = new MessageKey(paramQName1, paramQName2);
      WebServiceFeatureList localWebServiceFeatureList = (WebServiceFeatureList)faultMessageFeatures.get(localMessageKey);
      if (localWebServiceFeatureList == null) {
        localWebServiceFeatureList = new WebServiceFeatureList();
      }
      for (WebServiceFeature localWebServiceFeature : paramVarArgs) {
        localWebServiceFeatureList.add(localWebServiceFeature);
      }
      faultMessageFeatures.put(localMessageKey, localWebServiceFeatureList);
    }
  }
  
  @NotNull
  public synchronized MessageContextFactory getMessageContextFactory()
  {
    if (messageContextFactory == null) {
      messageContextFactory = MessageContextFactory.createFactory(getFeatures().toArray());
    }
    return messageContextFactory;
  }
  
  protected static class MessageKey
  {
    private final QName operationName;
    private final QName messageName;
    
    public MessageKey(QName paramQName1, QName paramQName2)
    {
      operationName = paramQName1;
      messageName = paramQName2;
    }
    
    public int hashCode()
    {
      int i = operationName != null ? operationName.hashCode() : 0;
      int j = messageName != null ? messageName.hashCode() : 0;
      return (i + j) * j + i;
    }
    
    public boolean equals(Object paramObject)
    {
      if (paramObject == null) {
        return false;
      }
      if (getClass() != paramObject.getClass()) {
        return false;
      }
      MessageKey localMessageKey = (MessageKey)paramObject;
      if ((operationName != operationName) && ((operationName == null) || (!operationName.equals(operationName)))) {
        return false;
      }
      return (messageName == messageName) || ((messageName != null) && (messageName.equals(messageName)));
    }
    
    public String toString()
    {
      return "(" + operationName + ", " + messageName + ")";
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\binding\BindingImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
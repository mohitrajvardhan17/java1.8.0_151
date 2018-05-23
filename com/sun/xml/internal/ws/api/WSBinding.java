package com.sun.xml.internal.ws.api;

import com.oracle.webservices.internal.api.message.MessageContextFactory;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import java.util.List;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.ws.Binding;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.handler.Handler;

public abstract interface WSBinding
  extends Binding
{
  public abstract SOAPVersion getSOAPVersion();
  
  public abstract AddressingVersion getAddressingVersion();
  
  @NotNull
  public abstract BindingID getBindingId();
  
  @NotNull
  public abstract List<Handler> getHandlerChain();
  
  public abstract boolean isFeatureEnabled(@NotNull Class<? extends WebServiceFeature> paramClass);
  
  public abstract boolean isOperationFeatureEnabled(@NotNull Class<? extends WebServiceFeature> paramClass, @NotNull QName paramQName);
  
  @Nullable
  public abstract <F extends WebServiceFeature> F getFeature(@NotNull Class<F> paramClass);
  
  @Nullable
  public abstract <F extends WebServiceFeature> F getOperationFeature(@NotNull Class<F> paramClass, @NotNull QName paramQName);
  
  @NotNull
  public abstract WSFeatureList getFeatures();
  
  @NotNull
  public abstract WSFeatureList getOperationFeatures(@NotNull QName paramQName);
  
  @NotNull
  public abstract WSFeatureList getInputMessageFeatures(@NotNull QName paramQName);
  
  @NotNull
  public abstract WSFeatureList getOutputMessageFeatures(@NotNull QName paramQName);
  
  @NotNull
  public abstract WSFeatureList getFaultMessageFeatures(@NotNull QName paramQName1, @NotNull QName paramQName2);
  
  @NotNull
  public abstract Set<QName> getKnownHeaders();
  
  public abstract boolean addKnownHeader(QName paramQName);
  
  @NotNull
  public abstract MessageContextFactory getMessageContextFactory();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\WSBinding.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
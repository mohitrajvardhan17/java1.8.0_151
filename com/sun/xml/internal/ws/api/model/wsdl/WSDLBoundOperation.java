package com.sun.xml.internal.ws.api.model.wsdl;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.model.ParameterBinding;
import java.util.Map;
import javax.jws.WebParam.Mode;
import javax.xml.namespace.QName;

public abstract interface WSDLBoundOperation
  extends WSDLObject, WSDLExtensible
{
  @NotNull
  public abstract QName getName();
  
  @NotNull
  public abstract String getSOAPAction();
  
  @NotNull
  public abstract WSDLOperation getOperation();
  
  @NotNull
  public abstract WSDLBoundPortType getBoundPortType();
  
  public abstract ANONYMOUS getAnonymous();
  
  @Nullable
  public abstract WSDLPart getPart(@NotNull String paramString, @NotNull WebParam.Mode paramMode);
  
  public abstract ParameterBinding getInputBinding(String paramString);
  
  public abstract ParameterBinding getOutputBinding(String paramString);
  
  public abstract ParameterBinding getFaultBinding(String paramString);
  
  public abstract String getMimeTypeForInputPart(String paramString);
  
  public abstract String getMimeTypeForOutputPart(String paramString);
  
  public abstract String getMimeTypeForFaultPart(String paramString);
  
  @NotNull
  public abstract Map<String, ? extends WSDLPart> getInParts();
  
  @NotNull
  public abstract Map<String, ? extends WSDLPart> getOutParts();
  
  @NotNull
  public abstract Iterable<? extends WSDLBoundFault> getFaults();
  
  public abstract Map<String, ParameterBinding> getInputParts();
  
  public abstract Map<String, ParameterBinding> getOutputParts();
  
  public abstract Map<String, ParameterBinding> getFaultParts();
  
  @Nullable
  public abstract QName getRequestPayloadName();
  
  @Nullable
  public abstract QName getResponsePayloadName();
  
  public abstract String getRequestNamespace();
  
  public abstract String getResponseNamespace();
  
  public static enum ANONYMOUS
  {
    optional,  required,  prohibited;
    
    private ANONYMOUS() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\model\wsdl\WSDLBoundOperation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package com.sun.xml.internal.ws.api;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.pipe.Codec;
import com.sun.xml.internal.ws.binding.BindingImpl;
import com.sun.xml.internal.ws.binding.WebServiceFeatureList;
import com.sun.xml.internal.ws.encoding.SOAPBindingCodec;
import com.sun.xml.internal.ws.encoding.XMLHTTPBindingCodec;
import com.sun.xml.internal.ws.util.ServiceFinder;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.xml.ws.BindingType;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.soap.MTOMFeature;

public abstract class BindingID
{
  public static final SOAPHTTPImpl X_SOAP12_HTTP = new SOAPHTTPImpl(SOAPVersion.SOAP_12, "http://java.sun.com/xml/ns/jaxws/2003/05/soap/bindings/HTTP/", true);
  public static final SOAPHTTPImpl SOAP12_HTTP = new SOAPHTTPImpl(SOAPVersion.SOAP_12, "http://www.w3.org/2003/05/soap/bindings/HTTP/", true);
  public static final SOAPHTTPImpl SOAP11_HTTP = new SOAPHTTPImpl(SOAPVersion.SOAP_11, "http://schemas.xmlsoap.org/wsdl/soap/http", true);
  public static final SOAPHTTPImpl SOAP12_HTTP_MTOM = new SOAPHTTPImpl(SOAPVersion.SOAP_12, "http://www.w3.org/2003/05/soap/bindings/HTTP/?mtom=true", true, true);
  public static final SOAPHTTPImpl SOAP11_HTTP_MTOM = new SOAPHTTPImpl(SOAPVersion.SOAP_11, "http://schemas.xmlsoap.org/wsdl/soap/http?mtom=true", true, true);
  public static final BindingID XML_HTTP = new Impl(SOAPVersion.SOAP_11, "http://www.w3.org/2004/08/wsdl/http", false)
  {
    public Codec createEncoder(WSBinding paramAnonymousWSBinding)
    {
      return new XMLHTTPBindingCodec(paramAnonymousWSBinding.getFeatures());
    }
  };
  private static final BindingID REST_HTTP = new Impl(SOAPVersion.SOAP_11, "http://jax-ws.dev.java.net/rest", true)
  {
    public Codec createEncoder(WSBinding paramAnonymousWSBinding)
    {
      return new XMLHTTPBindingCodec(paramAnonymousWSBinding.getFeatures());
    }
  };
  
  public BindingID() {}
  
  @NotNull
  public final WSBinding createBinding()
  {
    return BindingImpl.create(this);
  }
  
  @NotNull
  public String getTransport()
  {
    return "http://schemas.xmlsoap.org/soap/http";
  }
  
  @NotNull
  public final WSBinding createBinding(WebServiceFeature... paramVarArgs)
  {
    return BindingImpl.create(this, paramVarArgs);
  }
  
  @NotNull
  public final WSBinding createBinding(WSFeatureList paramWSFeatureList)
  {
    return createBinding(paramWSFeatureList.toArray());
  }
  
  public abstract SOAPVersion getSOAPVersion();
  
  @NotNull
  public abstract Codec createEncoder(@NotNull WSBinding paramWSBinding);
  
  public abstract String toString();
  
  public WebServiceFeatureList createBuiltinFeatureList()
  {
    return new WebServiceFeatureList();
  }
  
  public boolean canGenerateWSDL()
  {
    return false;
  }
  
  public String getParameter(String paramString1, String paramString2)
  {
    return paramString2;
  }
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof BindingID)) {
      return false;
    }
    return toString().equals(paramObject.toString());
  }
  
  public int hashCode()
  {
    return toString().hashCode();
  }
  
  @NotNull
  public static BindingID parse(String paramString)
  {
    if (paramString.equals(XML_HTTP.toString())) {
      return XML_HTTP;
    }
    if (paramString.equals(REST_HTTP.toString())) {
      return REST_HTTP;
    }
    if (belongsTo(paramString, SOAP11_HTTP.toString())) {
      return customize(paramString, SOAP11_HTTP);
    }
    if (belongsTo(paramString, SOAP12_HTTP.toString())) {
      return customize(paramString, SOAP12_HTTP);
    }
    if (belongsTo(paramString, "http://java.sun.com/xml/ns/jaxws/2003/05/soap/bindings/HTTP/")) {
      return customize(paramString, X_SOAP12_HTTP);
    }
    Iterator localIterator = ServiceFinder.find(BindingIDFactory.class).iterator();
    while (localIterator.hasNext())
    {
      BindingIDFactory localBindingIDFactory = (BindingIDFactory)localIterator.next();
      BindingID localBindingID = localBindingIDFactory.parse(paramString);
      if (localBindingID != null) {
        return localBindingID;
      }
    }
    throw new WebServiceException("Wrong binding ID: " + paramString);
  }
  
  private static boolean belongsTo(String paramString1, String paramString2)
  {
    return (paramString1.equals(paramString2)) || (paramString1.startsWith(paramString2 + '?'));
  }
  
  private static SOAPHTTPImpl customize(String paramString, SOAPHTTPImpl paramSOAPHTTPImpl)
  {
    if (paramString.equals(paramSOAPHTTPImpl.toString())) {
      return paramSOAPHTTPImpl;
    }
    SOAPHTTPImpl localSOAPHTTPImpl = new SOAPHTTPImpl(paramSOAPHTTPImpl.getSOAPVersion(), paramString, paramSOAPHTTPImpl.canGenerateWSDL());
    try
    {
      if (paramString.indexOf('?') == -1) {
        return localSOAPHTTPImpl;
      }
      String str1 = URLDecoder.decode(paramString.substring(paramString.indexOf('?') + 1), "UTF-8");
      for (String str2 : str1.split("&"))
      {
        int k = str2.indexOf('=');
        if (k < 0) {
          throw new WebServiceException("Malformed binding ID (no '=' in " + str2 + ")");
        }
        parameters.put(str2.substring(0, k), str2.substring(k + 1));
      }
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      throw new AssertionError(localUnsupportedEncodingException);
    }
    return localSOAPHTTPImpl;
  }
  
  @NotNull
  public static BindingID parse(Class<?> paramClass)
  {
    BindingType localBindingType = (BindingType)paramClass.getAnnotation(BindingType.class);
    if (localBindingType != null)
    {
      String str = localBindingType.value();
      if (str.length() > 0) {
        return parse(str);
      }
    }
    return SOAP11_HTTP;
  }
  
  private static abstract class Impl
    extends BindingID
  {
    final SOAPVersion version;
    private final String lexical;
    private final boolean canGenerateWSDL;
    
    public Impl(SOAPVersion paramSOAPVersion, String paramString, boolean paramBoolean)
    {
      version = paramSOAPVersion;
      lexical = paramString;
      canGenerateWSDL = paramBoolean;
    }
    
    public SOAPVersion getSOAPVersion()
    {
      return version;
    }
    
    public String toString()
    {
      return lexical;
    }
    
    @Deprecated
    public boolean canGenerateWSDL()
    {
      return canGenerateWSDL;
    }
  }
  
  private static final class SOAPHTTPImpl
    extends BindingID.Impl
    implements Cloneable
  {
    Map<String, String> parameters = new HashMap();
    static final String MTOM_PARAM = "mtom";
    
    public SOAPHTTPImpl(SOAPVersion paramSOAPVersion, String paramString, boolean paramBoolean)
    {
      super(paramString, paramBoolean);
    }
    
    public SOAPHTTPImpl(SOAPVersion paramSOAPVersion, String paramString, boolean paramBoolean1, boolean paramBoolean2)
    {
      this(paramSOAPVersion, paramString, paramBoolean1);
      String str = paramBoolean2 ? "true" : "false";
      parameters.put("mtom", str);
    }
    
    @NotNull
    public Codec createEncoder(WSBinding paramWSBinding)
    {
      return new SOAPBindingCodec(paramWSBinding.getFeatures());
    }
    
    private Boolean isMTOMEnabled()
    {
      String str = (String)parameters.get("mtom");
      return str == null ? null : Boolean.valueOf(str);
    }
    
    public WebServiceFeatureList createBuiltinFeatureList()
    {
      WebServiceFeatureList localWebServiceFeatureList = super.createBuiltinFeatureList();
      Boolean localBoolean = isMTOMEnabled();
      if (localBoolean != null) {
        localWebServiceFeatureList.add(new MTOMFeature(localBoolean.booleanValue()));
      }
      return localWebServiceFeatureList;
    }
    
    public String getParameter(String paramString1, String paramString2)
    {
      if (parameters.get(paramString1) == null) {
        return super.getParameter(paramString1, paramString2);
      }
      return (String)parameters.get(paramString1);
    }
    
    public SOAPHTTPImpl clone()
      throws CloneNotSupportedException
    {
      return (SOAPHTTPImpl)super.clone();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\BindingID.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
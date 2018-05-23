package com.sun.xml.internal.ws.api.model.wsdl;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.policy.PolicyResolver;
import com.sun.xml.internal.ws.api.policy.PolicyResolverFactory;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.api.wsdl.parser.WSDLParserExtension;
import com.sun.xml.internal.ws.api.wsdl.parser.XMLEntityResolver;
import com.sun.xml.internal.ws.api.wsdl.parser.XMLEntityResolver.Parser;
import com.sun.xml.internal.ws.policy.PolicyMap;
import com.sun.xml.internal.ws.wsdl.parser.RuntimeWSDLParser;
import java.io.IOException;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

public abstract interface WSDLModel
  extends WSDLExtensible
{
  public abstract WSDLPortType getPortType(@NotNull QName paramQName);
  
  public abstract WSDLBoundPortType getBinding(@NotNull QName paramQName);
  
  public abstract WSDLBoundPortType getBinding(@NotNull QName paramQName1, @NotNull QName paramQName2);
  
  public abstract WSDLService getService(@NotNull QName paramQName);
  
  @NotNull
  public abstract Map<QName, ? extends WSDLPortType> getPortTypes();
  
  @NotNull
  public abstract Map<QName, ? extends WSDLBoundPortType> getBindings();
  
  @NotNull
  public abstract Map<QName, ? extends WSDLService> getServices();
  
  public abstract QName getFirstServiceName();
  
  public abstract WSDLMessage getMessage(QName paramQName);
  
  @NotNull
  public abstract Map<QName, ? extends WSDLMessage> getMessages();
  
  /**
   * @deprecated
   */
  public abstract PolicyMap getPolicyMap();
  
  public static class WSDLParser
  {
    public WSDLParser() {}
    
    @NotNull
    public static WSDLModel parse(XMLEntityResolver.Parser paramParser, XMLEntityResolver paramXMLEntityResolver, boolean paramBoolean, WSDLParserExtension... paramVarArgs)
      throws IOException, XMLStreamException, SAXException
    {
      return parse(paramParser, paramXMLEntityResolver, paramBoolean, Container.NONE, paramVarArgs);
    }
    
    @NotNull
    public static WSDLModel parse(XMLEntityResolver.Parser paramParser, XMLEntityResolver paramXMLEntityResolver, boolean paramBoolean, @NotNull Container paramContainer, WSDLParserExtension... paramVarArgs)
      throws IOException, XMLStreamException, SAXException
    {
      return parse(paramParser, paramXMLEntityResolver, paramBoolean, paramContainer, PolicyResolverFactory.create(), paramVarArgs);
    }
    
    @NotNull
    public static WSDLModel parse(XMLEntityResolver.Parser paramParser, XMLEntityResolver paramXMLEntityResolver, boolean paramBoolean, @NotNull Container paramContainer, PolicyResolver paramPolicyResolver, WSDLParserExtension... paramVarArgs)
      throws IOException, XMLStreamException, SAXException
    {
      return RuntimeWSDLParser.parse(paramParser, paramXMLEntityResolver, paramBoolean, paramContainer, paramPolicyResolver, paramVarArgs);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\model\wsdl\WSDLModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
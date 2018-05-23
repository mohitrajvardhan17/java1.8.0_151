package com.sun.xml.internal.ws.server;

import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.pipe.NextAction;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;
import com.sun.xml.internal.ws.api.pipe.helper.AbstractTubeImpl;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.developer.SchemaValidationFeature;
import com.sun.xml.internal.ws.fault.SOAPFaultBuilder;
import com.sun.xml.internal.ws.util.pipe.AbstractSchemaValidationTube;
import com.sun.xml.internal.ws.util.pipe.AbstractSchemaValidationTube.MetadataResolverImpl;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.transform.Source;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.ws.WebServiceException;
import org.xml.sax.SAXException;

public class ServerSchemaValidationTube
  extends AbstractSchemaValidationTube
{
  private static final Logger LOGGER = Logger.getLogger(ServerSchemaValidationTube.class.getName());
  private final Schema schema;
  private final Validator validator;
  private final boolean noValidation;
  private final SEIModel seiModel;
  private final WSDLPort wsdlPort;
  
  public ServerSchemaValidationTube(WSEndpoint paramWSEndpoint, WSBinding paramWSBinding, SEIModel paramSEIModel, WSDLPort paramWSDLPort, Tube paramTube)
  {
    super(paramWSBinding, paramTube);
    seiModel = paramSEIModel;
    wsdlPort = paramWSDLPort;
    if (paramWSEndpoint.getServiceDefinition() != null)
    {
      AbstractSchemaValidationTube.MetadataResolverImpl localMetadataResolverImpl = new AbstractSchemaValidationTube.MetadataResolverImpl(this, paramWSEndpoint.getServiceDefinition());
      Source[] arrayOfSource1 = getSchemaSources(paramWSEndpoint.getServiceDefinition(), localMetadataResolverImpl);
      for (Source localSource : arrayOfSource1) {
        LOGGER.fine("Constructing service validation schema from = " + localSource.getSystemId());
      }
      if (arrayOfSource1.length != 0)
      {
        noValidation = false;
        sf.setResourceResolver(localMetadataResolverImpl);
        try
        {
          schema = sf.newSchema(arrayOfSource1);
        }
        catch (SAXException localSAXException)
        {
          throw new WebServiceException(localSAXException);
        }
        validator = schema.newValidator();
        return;
      }
    }
    noValidation = true;
    schema = null;
    validator = null;
  }
  
  protected Validator getValidator()
  {
    return validator;
  }
  
  protected boolean isNoValidation()
  {
    return noValidation;
  }
  
  public NextAction processRequest(Packet paramPacket)
  {
    if ((isNoValidation()) || (!feature.isInbound()) || (!paramPacket.getMessage().hasPayload()) || (paramPacket.getMessage().isFault())) {
      return super.processRequest(paramPacket);
    }
    try
    {
      doProcess(paramPacket);
    }
    catch (SAXException localSAXException)
    {
      LOGGER.log(Level.WARNING, "Client Request doesn't pass Service's Schema Validation", localSAXException);
      SOAPVersion localSOAPVersion = binding.getSOAPVersion();
      Message localMessage = SOAPFaultBuilder.createSOAPFaultMessage(localSOAPVersion, null, localSAXException, faultCodeClient);
      return doReturnWith(paramPacket.createServerResponse(localMessage, wsdlPort, seiModel, binding));
    }
    return super.processRequest(paramPacket);
  }
  
  public NextAction processResponse(Packet paramPacket)
  {
    if ((isNoValidation()) || (!feature.isOutbound()) || (paramPacket.getMessage() == null) || (!paramPacket.getMessage().hasPayload()) || (paramPacket.getMessage().isFault())) {
      return super.processResponse(paramPacket);
    }
    try
    {
      doProcess(paramPacket);
    }
    catch (SAXException localSAXException)
    {
      throw new WebServiceException(localSAXException);
    }
    return super.processResponse(paramPacket);
  }
  
  protected ServerSchemaValidationTube(ServerSchemaValidationTube paramServerSchemaValidationTube, TubeCloner paramTubeCloner)
  {
    super(paramServerSchemaValidationTube, paramTubeCloner);
    schema = schema;
    validator = schema.newValidator();
    noValidation = noValidation;
    seiModel = seiModel;
    wsdlPort = wsdlPort;
  }
  
  public AbstractTubeImpl copy(TubeCloner paramTubeCloner)
  {
    return new ServerSchemaValidationTube(this, paramTubeCloner);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\server\ServerSchemaValidationTube.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
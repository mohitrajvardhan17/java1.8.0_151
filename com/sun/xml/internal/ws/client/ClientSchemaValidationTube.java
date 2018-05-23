package com.sun.xml.internal.ws.client;

import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLModel;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLService;
import com.sun.xml.internal.ws.api.pipe.NextAction;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;
import com.sun.xml.internal.ws.api.pipe.helper.AbstractTubeImpl;
import com.sun.xml.internal.ws.developer.SchemaValidationFeature;
import com.sun.xml.internal.ws.util.MetadataUtil;
import com.sun.xml.internal.ws.util.pipe.AbstractSchemaValidationTube;
import com.sun.xml.internal.ws.util.pipe.AbstractSchemaValidationTube.MetadataResolverImpl;
import java.util.Map;
import java.util.logging.Logger;
import javax.xml.transform.Source;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.ws.WebServiceException;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public class ClientSchemaValidationTube
  extends AbstractSchemaValidationTube
{
  private static final Logger LOGGER = Logger.getLogger(ClientSchemaValidationTube.class.getName());
  private final Schema schema;
  private final Validator validator;
  private final boolean noValidation;
  private final WSDLPort port;
  
  public ClientSchemaValidationTube(WSBinding paramWSBinding, WSDLPort paramWSDLPort, Tube paramTube)
  {
    super(paramWSBinding, paramTube);
    port = paramWSDLPort;
    if (paramWSDLPort != null)
    {
      String str = paramWSDLPort.getOwner().getParent().getLocation().getSystemId();
      AbstractSchemaValidationTube.MetadataResolverImpl localMetadataResolverImpl = new AbstractSchemaValidationTube.MetadataResolverImpl(this);
      Map localMap = MetadataUtil.getMetadataClosure(str, localMetadataResolverImpl, true);
      localMetadataResolverImpl = new AbstractSchemaValidationTube.MetadataResolverImpl(this, localMap.values());
      Source[] arrayOfSource1 = getSchemaSources(localMap.values(), localMetadataResolverImpl);
      for (Source localSource : arrayOfSource1) {
        LOGGER.fine("Constructing client validation schema from = " + localSource.getSystemId());
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
  
  protected ClientSchemaValidationTube(ClientSchemaValidationTube paramClientSchemaValidationTube, TubeCloner paramTubeCloner)
  {
    super(paramClientSchemaValidationTube, paramTubeCloner);
    port = port;
    schema = schema;
    validator = schema.newValidator();
    noValidation = noValidation;
  }
  
  public AbstractTubeImpl copy(TubeCloner paramTubeCloner)
  {
    return new ClientSchemaValidationTube(this, paramTubeCloner);
  }
  
  public NextAction processRequest(Packet paramPacket)
  {
    if ((isNoValidation()) || (!feature.isOutbound()) || (!paramPacket.getMessage().hasPayload()) || (paramPacket.getMessage().isFault())) {
      return super.processRequest(paramPacket);
    }
    try
    {
      doProcess(paramPacket);
    }
    catch (SAXException localSAXException)
    {
      throw new WebServiceException(localSAXException);
    }
    return super.processRequest(paramPacket);
  }
  
  public NextAction processResponse(Packet paramPacket)
  {
    if ((isNoValidation()) || (!feature.isInbound()) || (paramPacket.getMessage() == null) || (!paramPacket.getMessage().hasPayload()) || (paramPacket.getMessage().isFault())) {
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
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\client\ClientSchemaValidationTube.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
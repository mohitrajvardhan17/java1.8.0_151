package javax.xml.ws;

import java.io.StringWriter;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import javax.xml.ws.spi.Provider;

@XmlTransient
public abstract class EndpointReference
{
  protected EndpointReference() {}
  
  public static EndpointReference readFrom(Source paramSource)
  {
    return Provider.provider().readEndpointReference(paramSource);
  }
  
  public abstract void writeTo(Result paramResult);
  
  public <T> T getPort(Class<T> paramClass, WebServiceFeature... paramVarArgs)
  {
    return (T)Provider.provider().getPort(this, paramClass, paramVarArgs);
  }
  
  public String toString()
  {
    StringWriter localStringWriter = new StringWriter();
    writeTo(new StreamResult(localStringWriter));
    return localStringWriter.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\ws\EndpointReference.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
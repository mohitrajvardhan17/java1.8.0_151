package javax.xml.bind.annotation;

import javax.xml.bind.ValidationEventHandler;
import javax.xml.transform.Result;
import javax.xml.transform.Source;

public abstract interface DomHandler<ElementT, ResultT extends Result>
{
  public abstract ResultT createUnmarshaller(ValidationEventHandler paramValidationEventHandler);
  
  public abstract ElementT getElement(ResultT paramResultT);
  
  public abstract Source marshal(ElementT paramElementT, ValidationEventHandler paramValidationEventHandler);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\bind\annotation\DomHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
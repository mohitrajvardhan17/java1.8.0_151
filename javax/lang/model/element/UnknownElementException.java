package javax.lang.model.element;

import javax.lang.model.UnknownEntityException;

public class UnknownElementException
  extends UnknownEntityException
{
  private static final long serialVersionUID = 269L;
  private transient Element element;
  private transient Object parameter;
  
  public UnknownElementException(Element paramElement, Object paramObject)
  {
    super("Unknown element: " + paramElement);
    element = paramElement;
    parameter = paramObject;
  }
  
  public Element getUnknownElement()
  {
    return element;
  }
  
  public Object getArgument()
  {
    return parameter;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\lang\model\element\UnknownElementException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
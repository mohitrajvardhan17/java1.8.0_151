package javax.lang.model.element;

import javax.lang.model.UnknownEntityException;

public class UnknownAnnotationValueException
  extends UnknownEntityException
{
  private static final long serialVersionUID = 269L;
  private transient AnnotationValue av;
  private transient Object parameter;
  
  public UnknownAnnotationValueException(AnnotationValue paramAnnotationValue, Object paramObject)
  {
    super("Unknown annotation value: " + paramAnnotationValue);
    av = paramAnnotationValue;
    parameter = paramObject;
  }
  
  public AnnotationValue getUnknownAnnotationValue()
  {
    return av;
  }
  
  public Object getArgument()
  {
    return parameter;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\lang\model\element\UnknownAnnotationValueException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package javax.lang.model.type;

import javax.lang.model.UnknownEntityException;

public class UnknownTypeException
  extends UnknownEntityException
{
  private static final long serialVersionUID = 269L;
  private transient TypeMirror type;
  private transient Object parameter;
  
  public UnknownTypeException(TypeMirror paramTypeMirror, Object paramObject)
  {
    super("Unknown type: " + paramTypeMirror);
    type = paramTypeMirror;
    parameter = paramObject;
  }
  
  public TypeMirror getUnknownType()
  {
    return type;
  }
  
  public Object getArgument()
  {
    return parameter;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\lang\model\type\UnknownTypeException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
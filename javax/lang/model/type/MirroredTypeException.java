package javax.lang.model.type;

import java.io.IOException;
import java.io.ObjectInputStream;

public class MirroredTypeException
  extends MirroredTypesException
{
  private static final long serialVersionUID = 269L;
  private transient TypeMirror type;
  
  public MirroredTypeException(TypeMirror paramTypeMirror)
  {
    super("Attempt to access Class object for TypeMirror " + paramTypeMirror.toString(), paramTypeMirror);
    type = paramTypeMirror;
  }
  
  public TypeMirror getTypeMirror()
  {
    return type;
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    type = null;
    types = null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\lang\model\type\MirroredTypeException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
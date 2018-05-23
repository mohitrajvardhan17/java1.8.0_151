package javax.lang.model.type;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MirroredTypesException
  extends RuntimeException
{
  private static final long serialVersionUID = 269L;
  transient List<? extends TypeMirror> types;
  
  MirroredTypesException(String paramString, TypeMirror paramTypeMirror)
  {
    super(paramString);
    ArrayList localArrayList = new ArrayList();
    localArrayList.add(paramTypeMirror);
    types = Collections.unmodifiableList(localArrayList);
  }
  
  public MirroredTypesException(List<? extends TypeMirror> paramList)
  {
    super("Attempt to access Class objects for TypeMirrors " + (paramList = new ArrayList(paramList)).toString());
    types = Collections.unmodifiableList(paramList);
  }
  
  public List<? extends TypeMirror> getTypeMirrors()
  {
    return types;
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    types = null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\lang\model\type\MirroredTypesException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
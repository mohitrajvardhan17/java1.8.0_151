package javax.tools;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;

public class ForwardingJavaFileObject<F extends JavaFileObject>
  extends ForwardingFileObject<F>
  implements JavaFileObject
{
  protected ForwardingJavaFileObject(F paramF)
  {
    super(paramF);
  }
  
  public JavaFileObject.Kind getKind()
  {
    return ((JavaFileObject)fileObject).getKind();
  }
  
  public boolean isNameCompatible(String paramString, JavaFileObject.Kind paramKind)
  {
    return ((JavaFileObject)fileObject).isNameCompatible(paramString, paramKind);
  }
  
  public NestingKind getNestingKind()
  {
    return ((JavaFileObject)fileObject).getNestingKind();
  }
  
  public Modifier getAccessLevel()
  {
    return ((JavaFileObject)fileObject).getAccessLevel();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\tools\ForwardingJavaFileObject.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
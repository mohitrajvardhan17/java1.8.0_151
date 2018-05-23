package sun.reflect.generics.tree;

import java.util.List;
import sun.reflect.generics.visitor.TypeTreeVisitor;

public class ClassTypeSignature
  implements FieldTypeSignature
{
  private final List<SimpleClassTypeSignature> path;
  
  private ClassTypeSignature(List<SimpleClassTypeSignature> paramList)
  {
    path = paramList;
  }
  
  public static ClassTypeSignature make(List<SimpleClassTypeSignature> paramList)
  {
    return new ClassTypeSignature(paramList);
  }
  
  public List<SimpleClassTypeSignature> getPath()
  {
    return path;
  }
  
  public void accept(TypeTreeVisitor<?> paramTypeTreeVisitor)
  {
    paramTypeTreeVisitor.visitClassTypeSignature(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\reflect\generics\tree\ClassTypeSignature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
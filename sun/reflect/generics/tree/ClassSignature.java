package sun.reflect.generics.tree;

import sun.reflect.generics.visitor.Visitor;

public class ClassSignature
  implements Signature
{
  private final FormalTypeParameter[] formalTypeParams;
  private final ClassTypeSignature superclass;
  private final ClassTypeSignature[] superInterfaces;
  
  private ClassSignature(FormalTypeParameter[] paramArrayOfFormalTypeParameter, ClassTypeSignature paramClassTypeSignature, ClassTypeSignature[] paramArrayOfClassTypeSignature)
  {
    formalTypeParams = paramArrayOfFormalTypeParameter;
    superclass = paramClassTypeSignature;
    superInterfaces = paramArrayOfClassTypeSignature;
  }
  
  public static ClassSignature make(FormalTypeParameter[] paramArrayOfFormalTypeParameter, ClassTypeSignature paramClassTypeSignature, ClassTypeSignature[] paramArrayOfClassTypeSignature)
  {
    return new ClassSignature(paramArrayOfFormalTypeParameter, paramClassTypeSignature, paramArrayOfClassTypeSignature);
  }
  
  public FormalTypeParameter[] getFormalTypeParameters()
  {
    return formalTypeParams;
  }
  
  public ClassTypeSignature getSuperclass()
  {
    return superclass;
  }
  
  public ClassTypeSignature[] getSuperInterfaces()
  {
    return superInterfaces;
  }
  
  public void accept(Visitor<?> paramVisitor)
  {
    paramVisitor.visitClassSignature(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\reflect\generics\tree\ClassSignature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
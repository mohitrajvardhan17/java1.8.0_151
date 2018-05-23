package sun.reflect.generics.tree;

import sun.reflect.generics.visitor.Visitor;

public class MethodTypeSignature
  implements Signature
{
  private final FormalTypeParameter[] formalTypeParams;
  private final TypeSignature[] parameterTypes;
  private final ReturnType returnType;
  private final FieldTypeSignature[] exceptionTypes;
  
  private MethodTypeSignature(FormalTypeParameter[] paramArrayOfFormalTypeParameter, TypeSignature[] paramArrayOfTypeSignature, ReturnType paramReturnType, FieldTypeSignature[] paramArrayOfFieldTypeSignature)
  {
    formalTypeParams = paramArrayOfFormalTypeParameter;
    parameterTypes = paramArrayOfTypeSignature;
    returnType = paramReturnType;
    exceptionTypes = paramArrayOfFieldTypeSignature;
  }
  
  public static MethodTypeSignature make(FormalTypeParameter[] paramArrayOfFormalTypeParameter, TypeSignature[] paramArrayOfTypeSignature, ReturnType paramReturnType, FieldTypeSignature[] paramArrayOfFieldTypeSignature)
  {
    return new MethodTypeSignature(paramArrayOfFormalTypeParameter, paramArrayOfTypeSignature, paramReturnType, paramArrayOfFieldTypeSignature);
  }
  
  public FormalTypeParameter[] getFormalTypeParameters()
  {
    return formalTypeParams;
  }
  
  public TypeSignature[] getParameterTypes()
  {
    return parameterTypes;
  }
  
  public ReturnType getReturnType()
  {
    return returnType;
  }
  
  public FieldTypeSignature[] getExceptionTypes()
  {
    return exceptionTypes;
  }
  
  public void accept(Visitor<?> paramVisitor)
  {
    paramVisitor.visitMethodTypeSignature(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\reflect\generics\tree\MethodTypeSignature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
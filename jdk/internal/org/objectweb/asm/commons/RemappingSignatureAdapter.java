package jdk.internal.org.objectweb.asm.commons;

import jdk.internal.org.objectweb.asm.signature.SignatureVisitor;

public class RemappingSignatureAdapter
  extends SignatureVisitor
{
  private final SignatureVisitor v;
  private final Remapper remapper;
  private String className;
  
  public RemappingSignatureAdapter(SignatureVisitor paramSignatureVisitor, Remapper paramRemapper)
  {
    this(327680, paramSignatureVisitor, paramRemapper);
  }
  
  protected RemappingSignatureAdapter(int paramInt, SignatureVisitor paramSignatureVisitor, Remapper paramRemapper)
  {
    super(paramInt);
    v = paramSignatureVisitor;
    remapper = paramRemapper;
  }
  
  public void visitClassType(String paramString)
  {
    className = paramString;
    v.visitClassType(remapper.mapType(paramString));
  }
  
  public void visitInnerClassType(String paramString)
  {
    String str1 = remapper.mapType(className) + '$';
    className = (className + '$' + paramString);
    String str2 = remapper.mapType(className);
    int i = str2.startsWith(str1) ? str1.length() : str2.lastIndexOf('$') + 1;
    v.visitInnerClassType(str2.substring(i));
  }
  
  public void visitFormalTypeParameter(String paramString)
  {
    v.visitFormalTypeParameter(paramString);
  }
  
  public void visitTypeVariable(String paramString)
  {
    v.visitTypeVariable(paramString);
  }
  
  public SignatureVisitor visitArrayType()
  {
    v.visitArrayType();
    return this;
  }
  
  public void visitBaseType(char paramChar)
  {
    v.visitBaseType(paramChar);
  }
  
  public SignatureVisitor visitClassBound()
  {
    v.visitClassBound();
    return this;
  }
  
  public SignatureVisitor visitExceptionType()
  {
    v.visitExceptionType();
    return this;
  }
  
  public SignatureVisitor visitInterface()
  {
    v.visitInterface();
    return this;
  }
  
  public SignatureVisitor visitInterfaceBound()
  {
    v.visitInterfaceBound();
    return this;
  }
  
  public SignatureVisitor visitParameterType()
  {
    v.visitParameterType();
    return this;
  }
  
  public SignatureVisitor visitReturnType()
  {
    v.visitReturnType();
    return this;
  }
  
  public SignatureVisitor visitSuperclass()
  {
    v.visitSuperclass();
    return this;
  }
  
  public void visitTypeArgument()
  {
    v.visitTypeArgument();
  }
  
  public SignatureVisitor visitTypeArgument(char paramChar)
  {
    v.visitTypeArgument(paramChar);
    return this;
  }
  
  public void visitEnd()
  {
    v.visitEnd();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\org\objectweb\asm\commons\RemappingSignatureAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
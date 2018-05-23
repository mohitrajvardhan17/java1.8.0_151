package jdk.internal.org.objectweb.asm.util;

import jdk.internal.org.objectweb.asm.signature.SignatureVisitor;

public class CheckSignatureAdapter
  extends SignatureVisitor
{
  public static final int CLASS_SIGNATURE = 0;
  public static final int METHOD_SIGNATURE = 1;
  public static final int TYPE_SIGNATURE = 2;
  private static final int EMPTY = 1;
  private static final int FORMAL = 2;
  private static final int BOUND = 4;
  private static final int SUPER = 8;
  private static final int PARAM = 16;
  private static final int RETURN = 32;
  private static final int SIMPLE_TYPE = 64;
  private static final int CLASS_TYPE = 128;
  private static final int END = 256;
  private final int type;
  private int state;
  private boolean canBeVoid;
  private final SignatureVisitor sv;
  
  public CheckSignatureAdapter(int paramInt, SignatureVisitor paramSignatureVisitor)
  {
    this(327680, paramInt, paramSignatureVisitor);
  }
  
  protected CheckSignatureAdapter(int paramInt1, int paramInt2, SignatureVisitor paramSignatureVisitor)
  {
    super(paramInt1);
    type = paramInt2;
    state = 1;
    sv = paramSignatureVisitor;
  }
  
  public void visitFormalTypeParameter(String paramString)
  {
    if ((type == 2) || ((state != 1) && (state != 2) && (state != 4))) {
      throw new IllegalStateException();
    }
    CheckMethodAdapter.checkIdentifier(paramString, "formal type parameter");
    state = 2;
    if (sv != null) {
      sv.visitFormalTypeParameter(paramString);
    }
  }
  
  public SignatureVisitor visitClassBound()
  {
    if (state != 2) {
      throw new IllegalStateException();
    }
    state = 4;
    SignatureVisitor localSignatureVisitor = sv == null ? null : sv.visitClassBound();
    return new CheckSignatureAdapter(2, localSignatureVisitor);
  }
  
  public SignatureVisitor visitInterfaceBound()
  {
    if ((state != 2) && (state != 4)) {
      throw new IllegalArgumentException();
    }
    SignatureVisitor localSignatureVisitor = sv == null ? null : sv.visitInterfaceBound();
    return new CheckSignatureAdapter(2, localSignatureVisitor);
  }
  
  public SignatureVisitor visitSuperclass()
  {
    if ((type != 0) || ((state & 0x7) == 0)) {
      throw new IllegalArgumentException();
    }
    state = 8;
    SignatureVisitor localSignatureVisitor = sv == null ? null : sv.visitSuperclass();
    return new CheckSignatureAdapter(2, localSignatureVisitor);
  }
  
  public SignatureVisitor visitInterface()
  {
    if (state != 8) {
      throw new IllegalStateException();
    }
    SignatureVisitor localSignatureVisitor = sv == null ? null : sv.visitInterface();
    return new CheckSignatureAdapter(2, localSignatureVisitor);
  }
  
  public SignatureVisitor visitParameterType()
  {
    if ((type != 1) || ((state & 0x17) == 0)) {
      throw new IllegalArgumentException();
    }
    state = 16;
    SignatureVisitor localSignatureVisitor = sv == null ? null : sv.visitParameterType();
    return new CheckSignatureAdapter(2, localSignatureVisitor);
  }
  
  public SignatureVisitor visitReturnType()
  {
    if ((type != 1) || ((state & 0x17) == 0)) {
      throw new IllegalArgumentException();
    }
    state = 32;
    SignatureVisitor localSignatureVisitor = sv == null ? null : sv.visitReturnType();
    CheckSignatureAdapter localCheckSignatureAdapter = new CheckSignatureAdapter(2, localSignatureVisitor);
    canBeVoid = true;
    return localCheckSignatureAdapter;
  }
  
  public SignatureVisitor visitExceptionType()
  {
    if (state != 32) {
      throw new IllegalStateException();
    }
    SignatureVisitor localSignatureVisitor = sv == null ? null : sv.visitExceptionType();
    return new CheckSignatureAdapter(2, localSignatureVisitor);
  }
  
  public void visitBaseType(char paramChar)
  {
    if ((type != 2) || (state != 1)) {
      throw new IllegalStateException();
    }
    if (paramChar == 'V')
    {
      if (!canBeVoid) {
        throw new IllegalArgumentException();
      }
    }
    else if ("ZCBSIFJD".indexOf(paramChar) == -1) {
      throw new IllegalArgumentException();
    }
    state = 64;
    if (sv != null) {
      sv.visitBaseType(paramChar);
    }
  }
  
  public void visitTypeVariable(String paramString)
  {
    if ((type != 2) || (state != 1)) {
      throw new IllegalStateException();
    }
    CheckMethodAdapter.checkIdentifier(paramString, "type variable");
    state = 64;
    if (sv != null) {
      sv.visitTypeVariable(paramString);
    }
  }
  
  public SignatureVisitor visitArrayType()
  {
    if ((type != 2) || (state != 1)) {
      throw new IllegalStateException();
    }
    state = 64;
    SignatureVisitor localSignatureVisitor = sv == null ? null : sv.visitArrayType();
    return new CheckSignatureAdapter(2, localSignatureVisitor);
  }
  
  public void visitClassType(String paramString)
  {
    if ((type != 2) || (state != 1)) {
      throw new IllegalStateException();
    }
    CheckMethodAdapter.checkInternalName(paramString, "class name");
    state = 128;
    if (sv != null) {
      sv.visitClassType(paramString);
    }
  }
  
  public void visitInnerClassType(String paramString)
  {
    if (state != 128) {
      throw new IllegalStateException();
    }
    CheckMethodAdapter.checkIdentifier(paramString, "inner class name");
    if (sv != null) {
      sv.visitInnerClassType(paramString);
    }
  }
  
  public void visitTypeArgument()
  {
    if (state != 128) {
      throw new IllegalStateException();
    }
    if (sv != null) {
      sv.visitTypeArgument();
    }
  }
  
  public SignatureVisitor visitTypeArgument(char paramChar)
  {
    if (state != 128) {
      throw new IllegalStateException();
    }
    if ("+-=".indexOf(paramChar) == -1) {
      throw new IllegalArgumentException();
    }
    SignatureVisitor localSignatureVisitor = sv == null ? null : sv.visitTypeArgument(paramChar);
    return new CheckSignatureAdapter(2, localSignatureVisitor);
  }
  
  public void visitEnd()
  {
    if (state != 128) {
      throw new IllegalStateException();
    }
    state = 256;
    if (sv != null) {
      sv.visitEnd();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\org\objectweb\asm\util\CheckSignatureAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
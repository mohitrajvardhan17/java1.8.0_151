package jdk.internal.instrumentation;

import jdk.internal.org.objectweb.asm.Label;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.Type;

final class MethodInliningAdapter
  extends MethodVisitor
{
  private final Label end;
  private final int remapOffset;
  
  public MethodInliningAdapter(MethodVisitor paramMethodVisitor, Label paramLabel, int paramInt1, String paramString, int paramInt2)
  {
    super(327680, paramMethodVisitor);
    remapOffset = paramInt2;
    end = paramLabel;
    Type[] arrayOfType1 = Type.getArgumentTypes(paramString);
    Type[] arrayOfType2 = isStatic(paramInt1) ? 0 : 1;
    for (Type localType : arrayOfType1) {
      arrayOfType2 += localType.getSize();
    }
    ??? = arrayOfType2;
    for (??? = arrayOfType1.length - 1; ??? >= 0; ???--)
    {
      int i;
      ??? -= arrayOfType1[???].getSize();
      ??? = i + paramInt2;
      int m = arrayOfType1[???].getOpcode(54);
      super.visitVarInsn(m, ???);
    }
    if (!isStatic(paramInt1)) {
      super.visitVarInsn(58, 0 + paramInt2);
    }
  }
  
  private boolean isStatic(int paramInt)
  {
    return (paramInt & 0x8) != 0;
  }
  
  public void visitInsn(int paramInt)
  {
    if ((paramInt == 177) || (paramInt == 172) || (paramInt == 176) || (paramInt == 173)) {
      super.visitJumpInsn(167, end);
    } else {
      super.visitInsn(paramInt);
    }
  }
  
  public void visitVarInsn(int paramInt1, int paramInt2)
  {
    super.visitVarInsn(paramInt1, paramInt2 + remapOffset);
  }
  
  public void visitIincInsn(int paramInt1, int paramInt2)
  {
    super.visitIincInsn(paramInt1 + remapOffset, paramInt2);
  }
  
  public void visitMaxs(int paramInt1, int paramInt2) {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\instrumentation\MethodInliningAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
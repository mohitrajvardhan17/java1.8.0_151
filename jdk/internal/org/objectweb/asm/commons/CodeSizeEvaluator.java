package jdk.internal.org.objectweb.asm.commons;

import jdk.internal.org.objectweb.asm.Handle;
import jdk.internal.org.objectweb.asm.Label;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.Opcodes;

public class CodeSizeEvaluator
  extends MethodVisitor
  implements Opcodes
{
  private int minSize;
  private int maxSize;
  
  public CodeSizeEvaluator(MethodVisitor paramMethodVisitor)
  {
    this(327680, paramMethodVisitor);
  }
  
  protected CodeSizeEvaluator(int paramInt, MethodVisitor paramMethodVisitor)
  {
    super(paramInt, paramMethodVisitor);
  }
  
  public int getMinSize()
  {
    return minSize;
  }
  
  public int getMaxSize()
  {
    return maxSize;
  }
  
  public void visitInsn(int paramInt)
  {
    minSize += 1;
    maxSize += 1;
    if (mv != null) {
      mv.visitInsn(paramInt);
    }
  }
  
  public void visitIntInsn(int paramInt1, int paramInt2)
  {
    if (paramInt1 == 17)
    {
      minSize += 3;
      maxSize += 3;
    }
    else
    {
      minSize += 2;
      maxSize += 2;
    }
    if (mv != null) {
      mv.visitIntInsn(paramInt1, paramInt2);
    }
  }
  
  public void visitVarInsn(int paramInt1, int paramInt2)
  {
    if ((paramInt2 < 4) && (paramInt1 != 169))
    {
      minSize += 1;
      maxSize += 1;
    }
    else if (paramInt2 >= 256)
    {
      minSize += 4;
      maxSize += 4;
    }
    else
    {
      minSize += 2;
      maxSize += 2;
    }
    if (mv != null) {
      mv.visitVarInsn(paramInt1, paramInt2);
    }
  }
  
  public void visitTypeInsn(int paramInt, String paramString)
  {
    minSize += 3;
    maxSize += 3;
    if (mv != null) {
      mv.visitTypeInsn(paramInt, paramString);
    }
  }
  
  public void visitFieldInsn(int paramInt, String paramString1, String paramString2, String paramString3)
  {
    minSize += 3;
    maxSize += 3;
    if (mv != null) {
      mv.visitFieldInsn(paramInt, paramString1, paramString2, paramString3);
    }
  }
  
  @Deprecated
  public void visitMethodInsn(int paramInt, String paramString1, String paramString2, String paramString3)
  {
    if (api >= 327680)
    {
      super.visitMethodInsn(paramInt, paramString1, paramString2, paramString3);
      return;
    }
    doVisitMethodInsn(paramInt, paramString1, paramString2, paramString3, paramInt == 185);
  }
  
  public void visitMethodInsn(int paramInt, String paramString1, String paramString2, String paramString3, boolean paramBoolean)
  {
    if (api < 327680)
    {
      super.visitMethodInsn(paramInt, paramString1, paramString2, paramString3, paramBoolean);
      return;
    }
    doVisitMethodInsn(paramInt, paramString1, paramString2, paramString3, paramBoolean);
  }
  
  private void doVisitMethodInsn(int paramInt, String paramString1, String paramString2, String paramString3, boolean paramBoolean)
  {
    if (paramInt == 185)
    {
      minSize += 5;
      maxSize += 5;
    }
    else
    {
      minSize += 3;
      maxSize += 3;
    }
    if (mv != null) {
      mv.visitMethodInsn(paramInt, paramString1, paramString2, paramString3, paramBoolean);
    }
  }
  
  public void visitInvokeDynamicInsn(String paramString1, String paramString2, Handle paramHandle, Object... paramVarArgs)
  {
    minSize += 5;
    maxSize += 5;
    if (mv != null) {
      mv.visitInvokeDynamicInsn(paramString1, paramString2, paramHandle, paramVarArgs);
    }
  }
  
  public void visitJumpInsn(int paramInt, Label paramLabel)
  {
    minSize += 3;
    if ((paramInt == 167) || (paramInt == 168)) {
      maxSize += 5;
    } else {
      maxSize += 8;
    }
    if (mv != null) {
      mv.visitJumpInsn(paramInt, paramLabel);
    }
  }
  
  public void visitLdcInsn(Object paramObject)
  {
    if (((paramObject instanceof Long)) || ((paramObject instanceof Double)))
    {
      minSize += 3;
      maxSize += 3;
    }
    else
    {
      minSize += 2;
      maxSize += 3;
    }
    if (mv != null) {
      mv.visitLdcInsn(paramObject);
    }
  }
  
  public void visitIincInsn(int paramInt1, int paramInt2)
  {
    if ((paramInt1 > 255) || (paramInt2 > 127) || (paramInt2 < -128))
    {
      minSize += 6;
      maxSize += 6;
    }
    else
    {
      minSize += 3;
      maxSize += 3;
    }
    if (mv != null) {
      mv.visitIincInsn(paramInt1, paramInt2);
    }
  }
  
  public void visitTableSwitchInsn(int paramInt1, int paramInt2, Label paramLabel, Label... paramVarArgs)
  {
    minSize += 13 + paramVarArgs.length * 4;
    maxSize += 16 + paramVarArgs.length * 4;
    if (mv != null) {
      mv.visitTableSwitchInsn(paramInt1, paramInt2, paramLabel, paramVarArgs);
    }
  }
  
  public void visitLookupSwitchInsn(Label paramLabel, int[] paramArrayOfInt, Label[] paramArrayOfLabel)
  {
    minSize += 9 + paramArrayOfInt.length * 8;
    maxSize += 12 + paramArrayOfInt.length * 8;
    if (mv != null) {
      mv.visitLookupSwitchInsn(paramLabel, paramArrayOfInt, paramArrayOfLabel);
    }
  }
  
  public void visitMultiANewArrayInsn(String paramString, int paramInt)
  {
    minSize += 4;
    maxSize += 4;
    if (mv != null) {
      mv.visitMultiANewArrayInsn(paramString, paramInt);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\org\objectweb\asm\commons\CodeSizeEvaluator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
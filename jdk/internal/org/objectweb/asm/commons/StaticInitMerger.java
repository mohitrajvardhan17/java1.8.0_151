package jdk.internal.org.objectweb.asm.commons;

import jdk.internal.org.objectweb.asm.ClassVisitor;
import jdk.internal.org.objectweb.asm.MethodVisitor;

public class StaticInitMerger
  extends ClassVisitor
{
  private String name;
  private MethodVisitor clinit;
  private final String prefix;
  private int counter;
  
  public StaticInitMerger(String paramString, ClassVisitor paramClassVisitor)
  {
    this(327680, paramString, paramClassVisitor);
  }
  
  protected StaticInitMerger(int paramInt, String paramString, ClassVisitor paramClassVisitor)
  {
    super(paramInt, paramClassVisitor);
    prefix = paramString;
  }
  
  public void visit(int paramInt1, int paramInt2, String paramString1, String paramString2, String paramString3, String[] paramArrayOfString)
  {
    cv.visit(paramInt1, paramInt2, paramString1, paramString2, paramString3, paramArrayOfString);
    name = paramString1;
  }
  
  public MethodVisitor visitMethod(int paramInt, String paramString1, String paramString2, String paramString3, String[] paramArrayOfString)
  {
    MethodVisitor localMethodVisitor;
    if ("<clinit>".equals(paramString1))
    {
      int i = 10;
      String str = prefix + counter++;
      localMethodVisitor = cv.visitMethod(i, str, paramString2, paramString3, paramArrayOfString);
      if (clinit == null) {
        clinit = cv.visitMethod(i, paramString1, paramString2, null, null);
      }
      clinit.visitMethodInsn(184, name, str, paramString2, false);
    }
    else
    {
      localMethodVisitor = cv.visitMethod(paramInt, paramString1, paramString2, paramString3, paramArrayOfString);
    }
    return localMethodVisitor;
  }
  
  public void visitEnd()
  {
    if (clinit != null)
    {
      clinit.visitInsn(177);
      clinit.visitMaxs(0, 0);
    }
    cv.visitEnd();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\org\objectweb\asm\commons\StaticInitMerger.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
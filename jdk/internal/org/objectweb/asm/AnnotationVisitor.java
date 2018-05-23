package jdk.internal.org.objectweb.asm;

public abstract class AnnotationVisitor
{
  protected final int api;
  protected AnnotationVisitor av;
  
  public AnnotationVisitor(int paramInt)
  {
    this(paramInt, null);
  }
  
  public AnnotationVisitor(int paramInt, AnnotationVisitor paramAnnotationVisitor)
  {
    if ((paramInt != 262144) && (paramInt != 327680)) {
      throw new IllegalArgumentException();
    }
    api = paramInt;
    av = paramAnnotationVisitor;
  }
  
  public void visit(String paramString, Object paramObject)
  {
    if (av != null) {
      av.visit(paramString, paramObject);
    }
  }
  
  public void visitEnum(String paramString1, String paramString2, String paramString3)
  {
    if (av != null) {
      av.visitEnum(paramString1, paramString2, paramString3);
    }
  }
  
  public AnnotationVisitor visitAnnotation(String paramString1, String paramString2)
  {
    if (av != null) {
      return av.visitAnnotation(paramString1, paramString2);
    }
    return null;
  }
  
  public AnnotationVisitor visitArray(String paramString)
  {
    if (av != null) {
      return av.visitArray(paramString);
    }
    return null;
  }
  
  public void visitEnd()
  {
    if (av != null) {
      av.visitEnd();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\org\objectweb\asm\AnnotationVisitor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
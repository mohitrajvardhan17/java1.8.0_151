package jdk.internal.org.objectweb.asm;

public abstract class FieldVisitor
{
  protected final int api;
  protected FieldVisitor fv;
  
  public FieldVisitor(int paramInt)
  {
    this(paramInt, null);
  }
  
  public FieldVisitor(int paramInt, FieldVisitor paramFieldVisitor)
  {
    if ((paramInt != 262144) && (paramInt != 327680)) {
      throw new IllegalArgumentException();
    }
    api = paramInt;
    fv = paramFieldVisitor;
  }
  
  public AnnotationVisitor visitAnnotation(String paramString, boolean paramBoolean)
  {
    if (fv != null) {
      return fv.visitAnnotation(paramString, paramBoolean);
    }
    return null;
  }
  
  public AnnotationVisitor visitTypeAnnotation(int paramInt, TypePath paramTypePath, String paramString, boolean paramBoolean)
  {
    if (api < 327680) {
      throw new RuntimeException();
    }
    if (fv != null) {
      return fv.visitTypeAnnotation(paramInt, paramTypePath, paramString, paramBoolean);
    }
    return null;
  }
  
  public void visitAttribute(Attribute paramAttribute)
  {
    if (fv != null) {
      fv.visitAttribute(paramAttribute);
    }
  }
  
  public void visitEnd()
  {
    if (fv != null) {
      fv.visitEnd();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\org\objectweb\asm\FieldVisitor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
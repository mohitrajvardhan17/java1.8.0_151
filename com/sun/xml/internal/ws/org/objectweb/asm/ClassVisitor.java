package com.sun.xml.internal.ws.org.objectweb.asm;

public abstract interface ClassVisitor
{
  public abstract void visit(int paramInt1, int paramInt2, String paramString1, String paramString2, String paramString3, String[] paramArrayOfString);
  
  public abstract void visitSource(String paramString1, String paramString2);
  
  public abstract void visitOuterClass(String paramString1, String paramString2, String paramString3);
  
  public abstract AnnotationVisitor visitAnnotation(String paramString, boolean paramBoolean);
  
  public abstract void visitAttribute(Attribute paramAttribute);
  
  public abstract void visitInnerClass(String paramString1, String paramString2, String paramString3, int paramInt);
  
  public abstract FieldVisitor visitField(int paramInt, String paramString1, String paramString2, String paramString3, Object paramObject);
  
  public abstract MethodVisitor visitMethod(int paramInt, String paramString1, String paramString2, String paramString3, String[] paramArrayOfString);
  
  public abstract void visitEnd();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\org\objectweb\asm\ClassVisitor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package com.sun.xml.internal.ws.org.objectweb.asm;

public abstract interface AnnotationVisitor
{
  public abstract void visit(String paramString, Object paramObject);
  
  public abstract void visitEnum(String paramString1, String paramString2, String paramString3);
  
  public abstract AnnotationVisitor visitAnnotation(String paramString1, String paramString2);
  
  public abstract AnnotationVisitor visitArray(String paramString);
  
  public abstract void visitEnd();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\org\objectweb\asm\AnnotationVisitor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
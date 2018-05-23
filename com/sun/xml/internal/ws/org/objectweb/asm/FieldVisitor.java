package com.sun.xml.internal.ws.org.objectweb.asm;

public abstract interface FieldVisitor
{
  public abstract AnnotationVisitor visitAnnotation(String paramString, boolean paramBoolean);
  
  public abstract void visitAttribute(Attribute paramAttribute);
  
  public abstract void visitEnd();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\org\objectweb\asm\FieldVisitor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
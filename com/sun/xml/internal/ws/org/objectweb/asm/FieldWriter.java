package com.sun.xml.internal.ws.org.objectweb.asm;

final class FieldWriter
  implements FieldVisitor
{
  FieldWriter next;
  private final ClassWriter cw;
  private final int access;
  private final int name;
  private final int desc;
  private int signature;
  private int value;
  private AnnotationWriter anns;
  private AnnotationWriter ianns;
  private Attribute attrs;
  
  FieldWriter(ClassWriter paramClassWriter, int paramInt, String paramString1, String paramString2, String paramString3, Object paramObject)
  {
    if (firstField == null) {
      firstField = this;
    } else {
      lastField.next = this;
    }
    lastField = this;
    cw = paramClassWriter;
    access = paramInt;
    name = paramClassWriter.newUTF8(paramString1);
    desc = paramClassWriter.newUTF8(paramString2);
    if (paramString3 != null) {
      signature = paramClassWriter.newUTF8(paramString3);
    }
    if (paramObject != null) {
      value = newConstItemindex;
    }
  }
  
  public AnnotationVisitor visitAnnotation(String paramString, boolean paramBoolean)
  {
    ByteVector localByteVector = new ByteVector();
    localByteVector.putShort(cw.newUTF8(paramString)).putShort(0);
    AnnotationWriter localAnnotationWriter = new AnnotationWriter(cw, true, localByteVector, localByteVector, 2);
    if (paramBoolean)
    {
      next = anns;
      anns = localAnnotationWriter;
    }
    else
    {
      next = ianns;
      ianns = localAnnotationWriter;
    }
    return localAnnotationWriter;
  }
  
  public void visitAttribute(Attribute paramAttribute)
  {
    next = attrs;
    attrs = paramAttribute;
  }
  
  public void visitEnd() {}
  
  int getSize()
  {
    int i = 8;
    if (value != 0)
    {
      cw.newUTF8("ConstantValue");
      i += 8;
    }
    if (((access & 0x1000) != 0) && ((cw.version & 0xFFFF) < 49))
    {
      cw.newUTF8("Synthetic");
      i += 6;
    }
    if ((access & 0x20000) != 0)
    {
      cw.newUTF8("Deprecated");
      i += 6;
    }
    if (signature != 0)
    {
      cw.newUTF8("Signature");
      i += 8;
    }
    if (anns != null)
    {
      cw.newUTF8("RuntimeVisibleAnnotations");
      i += 8 + anns.getSize();
    }
    if (ianns != null)
    {
      cw.newUTF8("RuntimeInvisibleAnnotations");
      i += 8 + ianns.getSize();
    }
    if (attrs != null) {
      i += attrs.getSize(cw, null, 0, -1, -1);
    }
    return i;
  }
  
  void put(ByteVector paramByteVector)
  {
    paramByteVector.putShort(access).putShort(name).putShort(desc);
    int i = 0;
    if (value != 0) {
      i++;
    }
    if (((access & 0x1000) != 0) && ((cw.version & 0xFFFF) < 49)) {
      i++;
    }
    if ((access & 0x20000) != 0) {
      i++;
    }
    if (signature != 0) {
      i++;
    }
    if (anns != null) {
      i++;
    }
    if (ianns != null) {
      i++;
    }
    if (attrs != null) {
      i += attrs.getCount();
    }
    paramByteVector.putShort(i);
    if (value != 0)
    {
      paramByteVector.putShort(cw.newUTF8("ConstantValue"));
      paramByteVector.putInt(2).putShort(value);
    }
    if (((access & 0x1000) != 0) && ((cw.version & 0xFFFF) < 49)) {
      paramByteVector.putShort(cw.newUTF8("Synthetic")).putInt(0);
    }
    if ((access & 0x20000) != 0) {
      paramByteVector.putShort(cw.newUTF8("Deprecated")).putInt(0);
    }
    if (signature != 0)
    {
      paramByteVector.putShort(cw.newUTF8("Signature"));
      paramByteVector.putInt(2).putShort(signature);
    }
    if (anns != null)
    {
      paramByteVector.putShort(cw.newUTF8("RuntimeVisibleAnnotations"));
      anns.put(paramByteVector);
    }
    if (ianns != null)
    {
      paramByteVector.putShort(cw.newUTF8("RuntimeInvisibleAnnotations"));
      ianns.put(paramByteVector);
    }
    if (attrs != null) {
      attrs.put(cw, null, 0, -1, -1, paramByteVector);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\org\objectweb\asm\FieldWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
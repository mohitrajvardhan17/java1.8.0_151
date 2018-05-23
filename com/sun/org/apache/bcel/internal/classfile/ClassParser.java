package com.sun.org.apache.bcel.internal.classfile;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public final class ClassParser
{
  private DataInputStream file;
  private ZipFile zip;
  private String file_name;
  private int class_name_index;
  private int superclass_name_index;
  private int major;
  private int minor;
  private int access_flags;
  private int[] interfaces;
  private ConstantPool constant_pool;
  private Field[] fields;
  private Method[] methods;
  private Attribute[] attributes;
  private boolean is_zip;
  private static final int BUFSIZE = 8192;
  
  public ClassParser(InputStream paramInputStream, String paramString)
  {
    file_name = paramString;
    String str = paramInputStream.getClass().getName();
    is_zip = ((str.startsWith("java.util.zip.")) || (str.startsWith("java.util.jar.")));
    if ((paramInputStream instanceof DataInputStream)) {
      file = ((DataInputStream)paramInputStream);
    } else {
      file = new DataInputStream(new BufferedInputStream(paramInputStream, 8192));
    }
  }
  
  public ClassParser(String paramString)
    throws IOException
  {
    is_zip = false;
    file_name = paramString;
    file = new DataInputStream(new BufferedInputStream(new FileInputStream(paramString), 8192));
  }
  
  public ClassParser(String paramString1, String paramString2)
    throws IOException
  {
    is_zip = true;
    zip = new ZipFile(paramString1);
    ZipEntry localZipEntry = zip.getEntry(paramString2);
    file_name = paramString2;
    file = new DataInputStream(new BufferedInputStream(zip.getInputStream(localZipEntry), 8192));
  }
  
  public JavaClass parse()
    throws IOException, ClassFormatException
  {
    readID();
    readVersion();
    readConstantPool();
    readClassInfo();
    readInterfaces();
    readFields();
    readMethods();
    readAttributes();
    file.close();
    if (zip != null) {
      zip.close();
    }
    return new JavaClass(class_name_index, superclass_name_index, file_name, major, minor, access_flags, constant_pool, interfaces, fields, methods, attributes, (byte)(is_zip ? 3 : 2));
  }
  
  private final void readAttributes()
    throws IOException, ClassFormatException
  {
    int i = file.readUnsignedShort();
    attributes = new Attribute[i];
    for (int j = 0; j < i; j++) {
      attributes[j] = Attribute.readAttribute(file, constant_pool);
    }
  }
  
  private final void readClassInfo()
    throws IOException, ClassFormatException
  {
    access_flags = file.readUnsignedShort();
    if ((access_flags & 0x200) != 0) {
      access_flags |= 0x400;
    }
    if (((access_flags & 0x400) != 0) && ((access_flags & 0x10) != 0)) {
      throw new ClassFormatException("Class can't be both final and abstract");
    }
    class_name_index = file.readUnsignedShort();
    superclass_name_index = file.readUnsignedShort();
  }
  
  private final void readConstantPool()
    throws IOException, ClassFormatException
  {
    constant_pool = new ConstantPool(file);
  }
  
  private final void readFields()
    throws IOException, ClassFormatException
  {
    int i = file.readUnsignedShort();
    fields = new Field[i];
    for (int j = 0; j < i; j++) {
      fields[j] = new Field(file, constant_pool);
    }
  }
  
  private final void readID()
    throws IOException, ClassFormatException
  {
    int i = -889275714;
    if (file.readInt() != i) {
      throw new ClassFormatException(file_name + " is not a Java .class file");
    }
  }
  
  private final void readInterfaces()
    throws IOException, ClassFormatException
  {
    int i = file.readUnsignedShort();
    interfaces = new int[i];
    for (int j = 0; j < i; j++) {
      interfaces[j] = file.readUnsignedShort();
    }
  }
  
  private final void readMethods()
    throws IOException, ClassFormatException
  {
    int i = file.readUnsignedShort();
    methods = new Method[i];
    for (int j = 0; j < i; j++) {
      methods[j] = new Method(file, constant_pool);
    }
  }
  
  private final void readVersion()
    throws IOException, ClassFormatException
  {
    minor = file.readUnsignedShort();
    major = file.readUnsignedShort();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\classfile\ClassParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
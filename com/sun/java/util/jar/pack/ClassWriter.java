package com.sun.java.util.jar.pack;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

class ClassWriter
{
  int verbose;
  Package pkg;
  Package.Class cls;
  DataOutputStream out;
  ConstantPool.Index cpIndex;
  ConstantPool.Index bsmIndex;
  ByteArrayOutputStream buf = new ByteArrayOutputStream();
  DataOutputStream bufOut = new DataOutputStream(buf);
  
  ClassWriter(Package.Class paramClass, OutputStream paramOutputStream)
    throws IOException
  {
    pkg = paramClass.getPackage();
    cls = paramClass;
    verbose = pkg.verbose;
    out = new DataOutputStream(new BufferedOutputStream(paramOutputStream));
    cpIndex = ConstantPool.makeIndex(paramClass.toString(), paramClass.getCPMap());
    cpIndex.flattenSigs = true;
    if (paramClass.hasBootstrapMethods()) {
      bsmIndex = ConstantPool.makeIndex(cpIndex.debugName + ".BootstrapMethods", paramClass.getBootstrapMethodMap());
    }
    if (verbose > 1) {
      Utils.log.fine("local CP=" + (verbose > 2 ? cpIndex.dumpString() : cpIndex.toString()));
    }
  }
  
  private void writeShort(int paramInt)
    throws IOException
  {
    out.writeShort(paramInt);
  }
  
  private void writeInt(int paramInt)
    throws IOException
  {
    out.writeInt(paramInt);
  }
  
  private void writeRef(ConstantPool.Entry paramEntry)
    throws IOException
  {
    writeRef(paramEntry, cpIndex);
  }
  
  private void writeRef(ConstantPool.Entry paramEntry, ConstantPool.Index paramIndex)
    throws IOException
  {
    int i = paramEntry == null ? 0 : paramIndex.indexOf(paramEntry);
    writeShort(i);
  }
  
  /* Error */
  void write()
    throws IOException
  {
    // Byte code:
    //   0: iconst_0
    //   1: istore_1
    //   2: aload_0
    //   3: getfield 402	com/sun/java/util/jar/pack/ClassWriter:verbose	I
    //   6: iconst_1
    //   7: if_icmple +31 -> 38
    //   10: getstatic 444	com/sun/java/util/jar/pack/Utils:log	Lcom/sun/java/util/jar/pack/Utils$Pack200Logger;
    //   13: new 243	java/lang/StringBuilder
    //   16: dup
    //   17: invokespecial 516	java/lang/StringBuilder:<init>	()V
    //   20: ldc 3
    //   22: invokevirtual 520	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   25: aload_0
    //   26: getfield 407	com/sun/java/util/jar/pack/ClassWriter:cls	Lcom/sun/java/util/jar/pack/Package$Class;
    //   29: invokevirtual 519	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   32: invokevirtual 517	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   35: invokevirtual 488	com/sun/java/util/jar/pack/Utils$Pack200Logger:fine	(Ljava/lang/String;)V
    //   38: aload_0
    //   39: invokevirtual 454	com/sun/java/util/jar/pack/ClassWriter:writeMagicNumbers	()V
    //   42: aload_0
    //   43: invokevirtual 452	com/sun/java/util/jar/pack/ClassWriter:writeConstantPool	()V
    //   46: aload_0
    //   47: invokevirtual 453	com/sun/java/util/jar/pack/ClassWriter:writeHeader	()V
    //   50: aload_0
    //   51: iconst_0
    //   52: invokevirtual 457	com/sun/java/util/jar/pack/ClassWriter:writeMembers	(Z)V
    //   55: aload_0
    //   56: iconst_1
    //   57: invokevirtual 457	com/sun/java/util/jar/pack/ClassWriter:writeMembers	(Z)V
    //   60: aload_0
    //   61: iconst_0
    //   62: aload_0
    //   63: getfield 407	com/sun/java/util/jar/pack/ClassWriter:cls	Lcom/sun/java/util/jar/pack/Package$Class;
    //   66: invokevirtual 458	com/sun/java/util/jar/pack/ClassWriter:writeAttributes	(ILcom/sun/java/util/jar/pack/Attribute$Holder;)V
    //   69: aload_0
    //   70: getfield 410	com/sun/java/util/jar/pack/ClassWriter:out	Ljava/io/DataOutputStream;
    //   73: invokevirtual 495	java/io/DataOutputStream:flush	()V
    //   76: iconst_1
    //   77: istore_1
    //   78: iload_1
    //   79: ifne +69 -> 148
    //   82: getstatic 444	com/sun/java/util/jar/pack/Utils:log	Lcom/sun/java/util/jar/pack/Utils$Pack200Logger;
    //   85: new 243	java/lang/StringBuilder
    //   88: dup
    //   89: invokespecial 516	java/lang/StringBuilder:<init>	()V
    //   92: ldc 11
    //   94: invokevirtual 520	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   97: aload_0
    //   98: getfield 407	com/sun/java/util/jar/pack/ClassWriter:cls	Lcom/sun/java/util/jar/pack/Package$Class;
    //   101: invokevirtual 519	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   104: invokevirtual 517	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   107: invokevirtual 489	com/sun/java/util/jar/pack/Utils$Pack200Logger:warning	(Ljava/lang/String;)V
    //   110: goto +38 -> 148
    //   113: astore_2
    //   114: iload_1
    //   115: ifne +31 -> 146
    //   118: getstatic 444	com/sun/java/util/jar/pack/Utils:log	Lcom/sun/java/util/jar/pack/Utils$Pack200Logger;
    //   121: new 243	java/lang/StringBuilder
    //   124: dup
    //   125: invokespecial 516	java/lang/StringBuilder:<init>	()V
    //   128: ldc 11
    //   130: invokevirtual 520	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   133: aload_0
    //   134: getfield 407	com/sun/java/util/jar/pack/ClassWriter:cls	Lcom/sun/java/util/jar/pack/Package$Class;
    //   137: invokevirtual 519	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   140: invokevirtual 517	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   143: invokevirtual 489	com/sun/java/util/jar/pack/Utils$Pack200Logger:warning	(Ljava/lang/String;)V
    //   146: aload_2
    //   147: athrow
    //   148: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	149	0	this	ClassWriter
    //   1	114	1	i	int
    //   113	34	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   2	78	113	finally
  }
  
  void writeMagicNumbers()
    throws IOException
  {
    writeInt(cls.magic);
    writeShort(cls.version.minor);
    writeShort(cls.version.major);
  }
  
  void writeConstantPool()
    throws IOException
  {
    ConstantPool.Entry[] arrayOfEntry = cls.cpMap;
    writeShort(arrayOfEntry.length);
    for (int i = 0; i < arrayOfEntry.length; i++)
    {
      ConstantPool.Entry localEntry = arrayOfEntry[i];
      if (!$assertionsDisabled) {
        if ((localEntry == null ? 1 : 0) != ((i == 0) || ((arrayOfEntry[(i - 1)] != null) && (arrayOfEntry[(i - 1)].isDoubleWord())) ? 1 : 0)) {
          throw new AssertionError();
        }
      }
      if (localEntry != null)
      {
        int j = localEntry.getTag();
        if (verbose > 2) {
          Utils.log.fine("   CP[" + i + "] = " + localEntry);
        }
        out.write(j);
        switch (j)
        {
        case 13: 
          throw new AssertionError("CP should have Signatures remapped to Utf8");
        case 1: 
          out.writeUTF(localEntry.stringValue());
          break;
        case 3: 
          out.writeInt(((ConstantPool.NumberEntry)localEntry).numberValue().intValue());
          break;
        case 4: 
          float f = ((ConstantPool.NumberEntry)localEntry).numberValue().floatValue();
          out.writeInt(Float.floatToRawIntBits(f));
          break;
        case 5: 
          out.writeLong(((ConstantPool.NumberEntry)localEntry).numberValue().longValue());
          break;
        case 6: 
          double d = ((ConstantPool.NumberEntry)localEntry).numberValue().doubleValue();
          out.writeLong(Double.doubleToRawLongBits(d));
          break;
        case 7: 
        case 8: 
        case 16: 
          writeRef(localEntry.getRef(0));
          break;
        case 15: 
          ConstantPool.MethodHandleEntry localMethodHandleEntry = (ConstantPool.MethodHandleEntry)localEntry;
          out.writeByte(refKind);
          writeRef(localMethodHandleEntry.getRef(0));
          break;
        case 9: 
        case 10: 
        case 11: 
        case 12: 
          writeRef(localEntry.getRef(0));
          writeRef(localEntry.getRef(1));
          break;
        case 18: 
          writeRef(localEntry.getRef(0), bsmIndex);
          writeRef(localEntry.getRef(1));
          break;
        case 17: 
          throw new AssertionError("CP should have BootstrapMethods moved to side-table");
        case 2: 
        case 14: 
        default: 
          throw new IOException("Bad constant pool tag " + j);
        }
      }
    }
  }
  
  void writeHeader()
    throws IOException
  {
    writeShort(cls.flags);
    writeRef(cls.thisClass);
    writeRef(cls.superClass);
    writeShort(cls.interfaces.length);
    for (int i = 0; i < cls.interfaces.length; i++) {
      writeRef(cls.interfaces[i]);
    }
  }
  
  void writeMembers(boolean paramBoolean)
    throws IOException
  {
    List localList;
    if (!paramBoolean) {
      localList = cls.getFields();
    } else {
      localList = cls.getMethods();
    }
    writeShort(localList.size());
    Iterator localIterator = localList.iterator();
    while (localIterator.hasNext())
    {
      Package.Class.Member localMember = (Package.Class.Member)localIterator.next();
      writeMember(localMember, paramBoolean);
    }
  }
  
  void writeMember(Package.Class.Member paramMember, boolean paramBoolean)
    throws IOException
  {
    if (verbose > 2) {
      Utils.log.fine("writeMember " + paramMember);
    }
    writeShort(flags);
    writeRef(getDescriptornameRef);
    writeRef(getDescriptortypeRef);
    writeAttributes(!paramBoolean ? 1 : 2, paramMember);
  }
  
  private void reorderBSMandICS(Attribute.Holder paramHolder)
  {
    Attribute localAttribute1 = paramHolder.getAttribute(Package.attrBootstrapMethodsEmpty);
    if (localAttribute1 == null) {
      return;
    }
    Attribute localAttribute2 = paramHolder.getAttribute(Package.attrInnerClassesEmpty);
    if (localAttribute2 == null) {
      return;
    }
    int i = attributes.indexOf(localAttribute1);
    int j = attributes.indexOf(localAttribute2);
    if (i > j)
    {
      attributes.remove(localAttribute1);
      attributes.add(j, localAttribute1);
    }
  }
  
  void writeAttributes(int paramInt, Attribute.Holder paramHolder)
    throws IOException
  {
    if (attributes == null)
    {
      writeShort(0);
      return;
    }
    if ((paramHolder instanceof Package.Class)) {
      reorderBSMandICS(paramHolder);
    }
    writeShort(attributes.size());
    Iterator localIterator = attributes.iterator();
    while (localIterator.hasNext())
    {
      Attribute localAttribute = (Attribute)localIterator.next();
      localAttribute.finishRefs(cpIndex);
      writeRef(localAttribute.getNameRef());
      if ((localAttribute.layout() == Package.attrCodeEmpty) || (localAttribute.layout() == Package.attrBootstrapMethodsEmpty) || (localAttribute.layout() == Package.attrInnerClassesEmpty))
      {
        DataOutputStream localDataOutputStream = out;
        assert (out != bufOut);
        buf.reset();
        out = bufOut;
        if ("Code".equals(localAttribute.name()))
        {
          Package.Class.Method localMethod = (Package.Class.Method)paramHolder;
          writeCode(code);
        }
        else if ("BootstrapMethods".equals(localAttribute.name()))
        {
          assert (paramHolder == cls);
          writeBootstrapMethods(cls);
        }
        else if ("InnerClasses".equals(localAttribute.name()))
        {
          assert (paramHolder == cls);
          writeInnerClasses(cls);
        }
        else
        {
          throw new AssertionError();
        }
        out = localDataOutputStream;
        if (verbose > 2) {
          Utils.log.fine("Attribute " + localAttribute.name() + " [" + buf.size() + "]");
        }
        writeInt(buf.size());
        buf.writeTo(out);
      }
      else
      {
        if (verbose > 2) {
          Utils.log.fine("Attribute " + localAttribute.name() + " [" + localAttribute.size() + "]");
        }
        writeInt(localAttribute.size());
        out.write(localAttribute.bytes());
      }
    }
  }
  
  void writeCode(Code paramCode)
    throws IOException
  {
    paramCode.finishRefs(cpIndex);
    writeShort(max_stack);
    writeShort(max_locals);
    writeInt(bytes.length);
    out.write(bytes);
    int i = paramCode.getHandlerCount();
    writeShort(i);
    for (int j = 0; j < i; j++)
    {
      writeShort(handler_start[j]);
      writeShort(handler_end[j]);
      writeShort(handler_catch[j]);
      writeRef(handler_class[j]);
    }
    writeAttributes(3, paramCode);
  }
  
  void writeBootstrapMethods(Package.Class paramClass)
    throws IOException
  {
    List localList = paramClass.getBootstrapMethods();
    writeShort(localList.size());
    Iterator localIterator = localList.iterator();
    while (localIterator.hasNext())
    {
      ConstantPool.BootstrapMethodEntry localBootstrapMethodEntry = (ConstantPool.BootstrapMethodEntry)localIterator.next();
      writeRef(bsmRef);
      writeShort(argRefs.length);
      for (ConstantPool.Entry localEntry : argRefs) {
        writeRef(localEntry);
      }
    }
  }
  
  void writeInnerClasses(Package.Class paramClass)
    throws IOException
  {
    List localList = paramClass.getInnerClasses();
    writeShort(localList.size());
    Iterator localIterator = localList.iterator();
    while (localIterator.hasNext())
    {
      Package.InnerClass localInnerClass = (Package.InnerClass)localIterator.next();
      writeRef(thisClass);
      writeRef(outerClass);
      writeRef(name);
      writeShort(flags);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\java\util\jar\pack\ClassWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
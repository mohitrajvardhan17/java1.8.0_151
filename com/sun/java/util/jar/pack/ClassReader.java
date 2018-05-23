package com.sun.java.util.jar.pack;

import java.io.DataInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

class ClassReader
{
  int verbose;
  Package pkg;
  Package.Class cls;
  long inPos;
  long constantPoolLimit = -1L;
  DataInputStream in;
  Map<Attribute.Layout, Attribute> attrDefs;
  Map<Attribute.Layout, String> attrCommands;
  String unknownAttrCommand = "error";
  boolean haveUnresolvedEntry;
  
  ClassReader(Package.Class paramClass, InputStream paramInputStream)
    throws IOException
  {
    pkg = paramClass.getPackage();
    cls = paramClass;
    verbose = pkg.verbose;
    in = new DataInputStream(new FilterInputStream(paramInputStream)
    {
      public int read(byte[] paramAnonymousArrayOfByte, int paramAnonymousInt1, int paramAnonymousInt2)
        throws IOException
      {
        int i = super.read(paramAnonymousArrayOfByte, paramAnonymousInt1, paramAnonymousInt2);
        if (i >= 0) {
          inPos += i;
        }
        return i;
      }
      
      public int read()
        throws IOException
      {
        int i = super.read();
        if (i >= 0) {
          inPos += 1L;
        }
        return i;
      }
      
      public long skip(long paramAnonymousLong)
        throws IOException
      {
        long l = super.skip(paramAnonymousLong);
        if (l >= 0L) {
          inPos += l;
        }
        return l;
      }
    });
  }
  
  public void setAttrDefs(Map<Attribute.Layout, Attribute> paramMap)
  {
    attrDefs = paramMap;
  }
  
  public void setAttrCommands(Map<Attribute.Layout, String> paramMap)
  {
    attrCommands = paramMap;
  }
  
  private void skip(int paramInt, String paramString)
    throws IOException
  {
    Utils.log.warning("skipping " + paramInt + " bytes of " + paramString);
    long l2;
    for (long l1 = 0L; l1 < paramInt; l1 += l2)
    {
      l2 = in.skip(paramInt - l1);
      assert (l2 > 0L);
    }
    assert (l1 == paramInt);
  }
  
  private int readUnsignedShort()
    throws IOException
  {
    return in.readUnsignedShort();
  }
  
  private int readInt()
    throws IOException
  {
    return in.readInt();
  }
  
  private ConstantPool.Entry readRef()
    throws IOException
  {
    int i = in.readUnsignedShort();
    return i == 0 ? null : cls.cpMap[i];
  }
  
  private ConstantPool.Entry readRef(byte paramByte)
    throws IOException
  {
    ConstantPool.Entry localEntry = readRef();
    assert (!(localEntry instanceof UnresolvedEntry));
    checkTag(localEntry, paramByte);
    return localEntry;
  }
  
  private ConstantPool.Entry checkTag(ConstantPool.Entry paramEntry, byte paramByte)
    throws ClassReader.ClassFormatException
  {
    if ((paramEntry == null) || (!paramEntry.tagMatches(paramByte)))
    {
      String str1 = " at pos: " + inPos;
      String str2 = "type=" + ConstantPool.tagName(tag);
      throw new ClassFormatException("Bad constant, expected type=" + ConstantPool.tagName(paramByte) + " got " + str2 + ", in File: " + cls.file.nameString + str1);
    }
    return paramEntry;
  }
  
  private ConstantPool.Entry checkTag(ConstantPool.Entry paramEntry, byte paramByte, boolean paramBoolean)
    throws ClassReader.ClassFormatException
  {
    return (paramBoolean) && (paramEntry == null) ? null : checkTag(paramEntry, paramByte);
  }
  
  private ConstantPool.Entry readRefOrNull(byte paramByte)
    throws IOException
  {
    ConstantPool.Entry localEntry = readRef();
    checkTag(localEntry, paramByte, true);
    return localEntry;
  }
  
  private ConstantPool.Utf8Entry readUtf8Ref()
    throws IOException
  {
    return (ConstantPool.Utf8Entry)readRef((byte)1);
  }
  
  private ConstantPool.ClassEntry readClassRef()
    throws IOException
  {
    return (ConstantPool.ClassEntry)readRef((byte)7);
  }
  
  private ConstantPool.ClassEntry readClassRefOrNull()
    throws IOException
  {
    return (ConstantPool.ClassEntry)readRefOrNull((byte)7);
  }
  
  private ConstantPool.SignatureEntry readSignatureRef()
    throws IOException
  {
    ConstantPool.Entry localEntry = readRef((byte)13);
    return (localEntry != null) && (localEntry.getTag() == 1) ? ConstantPool.getSignatureEntry(localEntry.stringValue()) : (ConstantPool.SignatureEntry)localEntry;
  }
  
  /* Error */
  void read()
    throws IOException
  {
    // Byte code:
    //   0: iconst_0
    //   1: istore_1
    //   2: aload_0
    //   3: invokevirtual 616	com/sun/java/util/jar/pack/ClassReader:readMagicNumbers	()V
    //   6: aload_0
    //   7: invokevirtual 614	com/sun/java/util/jar/pack/ClassReader:readConstantPool	()V
    //   10: aload_0
    //   11: invokevirtual 615	com/sun/java/util/jar/pack/ClassReader:readHeader	()V
    //   14: aload_0
    //   15: iconst_0
    //   16: invokevirtual 618	com/sun/java/util/jar/pack/ClassReader:readMembers	(Z)V
    //   19: aload_0
    //   20: iconst_1
    //   21: invokevirtual 618	com/sun/java/util/jar/pack/ClassReader:readMembers	(Z)V
    //   24: aload_0
    //   25: iconst_0
    //   26: aload_0
    //   27: getfield 568	com/sun/java/util/jar/pack/ClassReader:cls	Lcom/sun/java/util/jar/pack/Package$Class;
    //   30: invokevirtual 619	com/sun/java/util/jar/pack/ClassReader:readAttributes	(ILcom/sun/java/util/jar/pack/Attribute$Holder;)V
    //   33: aload_0
    //   34: invokespecial 613	com/sun/java/util/jar/pack/ClassReader:fixUnresolvedEntries	()V
    //   37: aload_0
    //   38: getfield 568	com/sun/java/util/jar/pack/ClassReader:cls	Lcom/sun/java/util/jar/pack/Package$Class;
    //   41: invokevirtual 660	com/sun/java/util/jar/pack/Package$Class:finishReading	()V
    //   44: getstatic 565	com/sun/java/util/jar/pack/ClassReader:$assertionsDisabled	Z
    //   47: ifne +25 -> 72
    //   50: iconst_0
    //   51: aload_0
    //   52: getfield 569	com/sun/java/util/jar/pack/ClassReader:in	Ljava/io/DataInputStream;
    //   55: iconst_1
    //   56: newarray <illegal type>
    //   58: invokevirtual 678	java/io/DataInputStream:read	([B)I
    //   61: if_icmpge +11 -> 72
    //   64: new 346	java/lang/AssertionError
    //   67: dup
    //   68: invokespecial 682	java/lang/AssertionError:<init>	()V
    //   71: athrow
    //   72: iconst_1
    //   73: istore_1
    //   74: iload_1
    //   75: ifne +113 -> 188
    //   78: aload_0
    //   79: getfield 562	com/sun/java/util/jar/pack/ClassReader:verbose	I
    //   82: ifle +106 -> 188
    //   85: getstatic 601	com/sun/java/util/jar/pack/Utils:log	Lcom/sun/java/util/jar/pack/Utils$Pack200Logger;
    //   88: new 354	java/lang/StringBuilder
    //   91: dup
    //   92: invokespecial 694	java/lang/StringBuilder:<init>	()V
    //   95: ldc 24
    //   97: invokevirtual 699	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   100: aload_0
    //   101: getfield 564	com/sun/java/util/jar/pack/ClassReader:inPos	J
    //   104: invokevirtual 697	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   107: ldc 15
    //   109: invokevirtual 699	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   112: aload_0
    //   113: getfield 568	com/sun/java/util/jar/pack/ClassReader:cls	Lcom/sun/java/util/jar/pack/Package$Class;
    //   116: getfield 594	com/sun/java/util/jar/pack/Package$Class:file	Lcom/sun/java/util/jar/pack/Package$File;
    //   119: invokevirtual 698	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   122: invokevirtual 695	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   125: invokevirtual 669	com/sun/java/util/jar/pack/Utils$Pack200Logger:warning	(Ljava/lang/String;)V
    //   128: goto +60 -> 188
    //   131: astore_2
    //   132: iload_1
    //   133: ifne +53 -> 186
    //   136: aload_0
    //   137: getfield 562	com/sun/java/util/jar/pack/ClassReader:verbose	I
    //   140: ifle +46 -> 186
    //   143: getstatic 601	com/sun/java/util/jar/pack/Utils:log	Lcom/sun/java/util/jar/pack/Utils$Pack200Logger;
    //   146: new 354	java/lang/StringBuilder
    //   149: dup
    //   150: invokespecial 694	java/lang/StringBuilder:<init>	()V
    //   153: ldc 24
    //   155: invokevirtual 699	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   158: aload_0
    //   159: getfield 564	com/sun/java/util/jar/pack/ClassReader:inPos	J
    //   162: invokevirtual 697	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   165: ldc 15
    //   167: invokevirtual 699	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   170: aload_0
    //   171: getfield 568	com/sun/java/util/jar/pack/ClassReader:cls	Lcom/sun/java/util/jar/pack/Package$Class;
    //   174: getfield 594	com/sun/java/util/jar/pack/Package$Class:file	Lcom/sun/java/util/jar/pack/Package$File;
    //   177: invokevirtual 698	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   180: invokevirtual 695	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   183: invokevirtual 669	com/sun/java/util/jar/pack/Utils$Pack200Logger:warning	(Ljava/lang/String;)V
    //   186: aload_2
    //   187: athrow
    //   188: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	189	0	this	ClassReader
    //   1	132	1	i	int
    //   131	56	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   2	74	131	finally
  }
  
  void readMagicNumbers()
    throws IOException
  {
    cls.magic = in.readInt();
    if (cls.magic != -889275714) {
      throw new Attribute.FormatException("Bad magic number in class file " + Integer.toHexString(cls.magic), 0, "magic-number", "pass");
    }
    int i = (short)readUnsignedShort();
    int j = (short)readUnsignedShort();
    cls.version = Package.Version.of(j, i);
    String str = checkVersion(cls.version);
    if (str != null) {
      throw new Attribute.FormatException("classfile version too " + str + ": " + cls.version + " in " + cls.file, 0, "version", "pass");
    }
  }
  
  private String checkVersion(Package.Version paramVersion)
  {
    int i = major;
    int j = minor;
    if ((i < pkg.minClassVersion.major) || ((i == pkg.minClassVersion.major) && (j < pkg.minClassVersion.minor))) {
      return "small";
    }
    if ((i > pkg.maxClassVersion.major) || ((i == pkg.maxClassVersion.major) && (j > pkg.maxClassVersion.minor))) {
      return "large";
    }
    return null;
  }
  
  void readConstantPool()
    throws IOException
  {
    int i = in.readUnsignedShort();
    int[] arrayOfInt = new int[i * 4];
    int j = 0;
    ConstantPool.Entry[] arrayOfEntry = new ConstantPool.Entry[i];
    arrayOfEntry[0] = null;
    int m;
    for (int k = 1; k < i; k++)
    {
      m = in.readByte();
      switch (m)
      {
      case 1: 
        arrayOfEntry[k] = ConstantPool.getUtf8Entry(in.readUTF());
        break;
      case 3: 
        arrayOfEntry[k] = ConstantPool.getLiteralEntry(Integer.valueOf(in.readInt()));
        break;
      case 4: 
        arrayOfEntry[k] = ConstantPool.getLiteralEntry(Float.valueOf(in.readFloat()));
        break;
      case 5: 
        arrayOfEntry[k] = ConstantPool.getLiteralEntry(Long.valueOf(in.readLong()));
        arrayOfEntry[(++k)] = null;
        break;
      case 6: 
        arrayOfEntry[k] = ConstantPool.getLiteralEntry(Double.valueOf(in.readDouble()));
        arrayOfEntry[(++k)] = null;
        break;
      case 7: 
      case 8: 
      case 16: 
        arrayOfInt[(j++)] = k;
        arrayOfInt[(j++)] = m;
        arrayOfInt[(j++)] = in.readUnsignedShort();
        arrayOfInt[(j++)] = -1;
        break;
      case 9: 
      case 10: 
      case 11: 
      case 12: 
        arrayOfInt[(j++)] = k;
        arrayOfInt[(j++)] = m;
        arrayOfInt[(j++)] = in.readUnsignedShort();
        arrayOfInt[(j++)] = in.readUnsignedShort();
        break;
      case 18: 
        arrayOfInt[(j++)] = k;
        arrayOfInt[(j++)] = m;
        arrayOfInt[(j++)] = (0xFFFFFFFF ^ in.readUnsignedShort());
        arrayOfInt[(j++)] = in.readUnsignedShort();
        break;
      case 15: 
        arrayOfInt[(j++)] = k;
        arrayOfInt[(j++)] = m;
        arrayOfInt[(j++)] = (0xFFFFFFFF ^ in.readUnsignedByte());
        arrayOfInt[(j++)] = in.readUnsignedShort();
        break;
      case 2: 
      case 13: 
      case 14: 
      case 17: 
      default: 
        throw new ClassFormatException("Bad constant pool tag " + m + " in File: " + cls.file.nameString + " at pos: " + inPos);
      }
    }
    constantPoolLimit = inPos;
    while (j > 0)
    {
      if (verbose > 3) {
        Utils.log.fine("CP fixups [" + j / 4 + "]");
      }
      k = j;
      j = 0;
      m = 0;
      while (m < k)
      {
        int n = arrayOfInt[(m++)];
        int i1 = arrayOfInt[(m++)];
        int i2 = arrayOfInt[(m++)];
        int i3 = arrayOfInt[(m++)];
        if (verbose > 3) {
          Utils.log.fine("  cp[" + n + "] = " + ConstantPool.tagName(i1) + "{" + i2 + "," + i3 + "}");
        }
        if (((i2 >= 0) && (arrayOfEntry[i2] == null)) || ((i3 >= 0) && (arrayOfEntry[i3] == null)))
        {
          arrayOfInt[(j++)] = n;
          arrayOfInt[(j++)] = i1;
          arrayOfInt[(j++)] = i2;
          arrayOfInt[(j++)] = i3;
        }
        else
        {
          switch (i1)
          {
          case 7: 
            arrayOfEntry[n] = ConstantPool.getClassEntry(arrayOfEntry[i2].stringValue());
            break;
          case 8: 
            arrayOfEntry[n] = ConstantPool.getStringEntry(arrayOfEntry[i2].stringValue());
            break;
          case 9: 
          case 10: 
          case 11: 
            ConstantPool.ClassEntry localClassEntry = (ConstantPool.ClassEntry)checkTag(arrayOfEntry[i2], (byte)7);
            ConstantPool.DescriptorEntry localDescriptorEntry1 = (ConstantPool.DescriptorEntry)checkTag(arrayOfEntry[i3], (byte)12);
            arrayOfEntry[n] = ConstantPool.getMemberEntry((byte)i1, localClassEntry, localDescriptorEntry1);
            break;
          case 12: 
            ConstantPool.Utf8Entry localUtf8Entry1 = (ConstantPool.Utf8Entry)checkTag(arrayOfEntry[i2], (byte)1);
            ConstantPool.Utf8Entry localUtf8Entry2 = (ConstantPool.Utf8Entry)checkTag(arrayOfEntry[i3], (byte)13);
            arrayOfEntry[n] = ConstantPool.getDescriptorEntry(localUtf8Entry1, localUtf8Entry2);
            break;
          case 16: 
            arrayOfEntry[n] = ConstantPool.getMethodTypeEntry((ConstantPool.Utf8Entry)checkTag(arrayOfEntry[i2], 13));
            break;
          case 15: 
            byte b = (byte)(0xFFFFFFFF ^ i2);
            ConstantPool.MemberEntry localMemberEntry = (ConstantPool.MemberEntry)checkTag(arrayOfEntry[i3], (byte)52);
            arrayOfEntry[n] = ConstantPool.getMethodHandleEntry(b, localMemberEntry);
            break;
          case 18: 
            ConstantPool.DescriptorEntry localDescriptorEntry2 = (ConstantPool.DescriptorEntry)checkTag(arrayOfEntry[i3], (byte)12);
            arrayOfEntry[n] = new UnresolvedEntry((byte)i1, new Object[] { Integer.valueOf(0xFFFFFFFF ^ i2), localDescriptorEntry2 });
            break;
          case 13: 
          case 14: 
          case 17: 
          default: 
            if (!$assertionsDisabled) {
              throw new AssertionError();
            }
            break;
          }
        }
      }
      assert (j < k);
    }
    cls.cpMap = arrayOfEntry;
  }
  
  private void fixUnresolvedEntries()
  {
    if (!haveUnresolvedEntry) {
      return;
    }
    ConstantPool.Entry[] arrayOfEntry = cls.getCPMap();
    for (int i = 0; i < arrayOfEntry.length; i++)
    {
      ConstantPool.Entry localEntry = arrayOfEntry[i];
      if ((localEntry instanceof UnresolvedEntry))
      {
        arrayOfEntry[i] = (localEntry = ((UnresolvedEntry)localEntry).resolve());
        assert (!(localEntry instanceof UnresolvedEntry));
      }
    }
    haveUnresolvedEntry = false;
  }
  
  void readHeader()
    throws IOException
  {
    cls.flags = readUnsignedShort();
    cls.thisClass = readClassRef();
    cls.superClass = readClassRefOrNull();
    int i = readUnsignedShort();
    cls.interfaces = new ConstantPool.ClassEntry[i];
    for (int j = 0; j < i; j++) {
      cls.interfaces[j] = readClassRef();
    }
  }
  
  void readMembers(boolean paramBoolean)
    throws IOException
  {
    int i = readUnsignedShort();
    for (int j = 0; j < i; j++) {
      readMember(paramBoolean);
    }
  }
  
  void readMember(boolean paramBoolean)
    throws IOException
  {
    int i = readUnsignedShort();
    ConstantPool.Utf8Entry localUtf8Entry = readUtf8Ref();
    ConstantPool.SignatureEntry localSignatureEntry = readSignatureRef();
    ConstantPool.DescriptorEntry localDescriptorEntry = ConstantPool.getDescriptorEntry(localUtf8Entry, localSignatureEntry);
    Object localObject;
    if (!paramBoolean)
    {
      Package.Class tmp36_33 = cls;
      tmp36_33.getClass();
      localObject = new Package.Class.Field(tmp36_33, i, localDescriptorEntry);
    }
    else
    {
      Package.Class tmp60_57 = cls;
      tmp60_57.getClass();
      localObject = new Package.Class.Method(tmp60_57, i, localDescriptorEntry);
    }
    readAttributes(!paramBoolean ? 1 : 2, (Attribute.Holder)localObject);
  }
  
  void readAttributes(int paramInt, Attribute.Holder paramHolder)
    throws IOException
  {
    int i = readUnsignedShort();
    if (i == 0) {
      return;
    }
    if (verbose > 3) {
      Utils.log.fine("readAttributes " + paramHolder + " [" + i + "]");
    }
    for (int j = 0; j < i; j++)
    {
      String str1 = readUtf8Ref().stringValue();
      int k = readInt();
      Object localObject2;
      Object localObject3;
      if (attrCommands != null)
      {
        localObject1 = Attribute.keyForLookup(paramInt, str1);
        String str2 = (String)attrCommands.get(localObject1);
        if (str2 != null)
        {
          localObject2 = str2;
          int n = -1;
          switch (((String)localObject2).hashCode())
          {
          case 3433489: 
            if (((String)localObject2).equals("pass")) {
              n = 0;
            }
            break;
          case 96784904: 
            if (((String)localObject2).equals("error")) {
              n = 1;
            }
            break;
          case 109773592: 
            if (((String)localObject2).equals("strip")) {
              n = 2;
            }
            break;
          }
          switch (n)
          {
          case 0: 
            localObject3 = "passing attribute bitwise in " + paramHolder;
            throw new Attribute.FormatException((String)localObject3, paramInt, str1, str2);
          case 1: 
            String str3 = "attribute not allowed in " + paramHolder;
            throw new Attribute.FormatException(str3, paramInt, str1, str2);
          case 2: 
            skip(k, str1 + " attribute in " + paramHolder);
            break;
          }
        }
      }
      Object localObject1 = Attribute.lookup(Package.attrDefs, paramInt, str1);
      if ((verbose > 4) && (localObject1 != null)) {
        Utils.log.fine("pkg_attribute_lookup " + str1 + " = " + localObject1);
      }
      if (localObject1 == null)
      {
        localObject1 = Attribute.lookup(attrDefs, paramInt, str1);
        if ((verbose > 4) && (localObject1 != null)) {
          Utils.log.fine("this " + str1 + " = " + localObject1);
        }
      }
      if (localObject1 == null)
      {
        localObject1 = Attribute.lookup(null, paramInt, str1);
        if ((verbose > 4) && (localObject1 != null)) {
          Utils.log.fine("null_attribute_lookup " + str1 + " = " + localObject1);
        }
      }
      if ((localObject1 == null) && (k == 0)) {
        localObject1 = Attribute.find(paramInt, str1, "");
      }
      int m = (paramInt == 3) && ((str1.equals("StackMap")) || (str1.equals("StackMapX"))) ? 1 : 0;
      if (m != 0)
      {
        localObject2 = (Code)paramHolder;
        if ((max_stack >= 65536) || (max_locals >= 65536) || (((Code)localObject2).getLength() >= 65536) || (str1.endsWith("X"))) {
          localObject1 = null;
        }
      }
      if (localObject1 == null)
      {
        if (m != 0)
        {
          localObject2 = "unsupported StackMap variant in " + paramHolder;
          throw new Attribute.FormatException((String)localObject2, paramInt, str1, "pass");
        }
        if ("strip".equals(unknownAttrCommand))
        {
          skip(k, "unknown " + str1 + " attribute in " + paramHolder);
        }
        else
        {
          localObject2 = " is unknown attribute in class " + paramHolder;
          throw new Attribute.FormatException((String)localObject2, paramInt, str1, unknownAttrCommand);
        }
      }
      else
      {
        long l = inPos;
        if (((Attribute)localObject1).layout() == Package.attrCodeEmpty)
        {
          localObject3 = (Package.Class.Method)paramHolder;
          code = new Code((Package.Class.Method)localObject3);
          try
          {
            readCode(code);
          }
          catch (Instruction.FormatException localFormatException)
          {
            String str4 = localFormatException.getMessage() + " in " + paramHolder;
            throw new ClassFormatException(str4, localFormatException);
          }
          assert (k == inPos - l);
        }
        else
        {
          if (((Attribute)localObject1).layout() == Package.attrBootstrapMethodsEmpty)
          {
            assert (paramHolder == cls);
            readBootstrapMethods(cls);
            if (($assertionsDisabled) || (k == inPos - l)) {
              continue;
            }
            throw new AssertionError();
          }
          if (((Attribute)localObject1).layout() == Package.attrInnerClassesEmpty)
          {
            assert (paramHolder == cls);
            readInnerClasses(cls);
            if ((!$assertionsDisabled) && (k != inPos - l)) {
              throw new AssertionError();
            }
          }
          else if (k > 0)
          {
            localObject3 = new byte[k];
            in.readFully((byte[])localObject3);
            localObject1 = ((Attribute)localObject1).addContent((byte[])localObject3);
          }
        }
        if ((((Attribute)localObject1).size() == 0) && (!((Attribute)localObject1).layout().isEmpty())) {
          throw new ClassFormatException(str1 + ": attribute length cannot be zero, in " + paramHolder);
        }
        paramHolder.addAttribute((Attribute)localObject1);
        if (verbose > 2) {
          Utils.log.fine("read " + localObject1);
        }
      }
    }
  }
  
  void readCode(Code paramCode)
    throws IOException
  {
    max_stack = readUnsignedShort();
    max_locals = readUnsignedShort();
    bytes = new byte[readInt()];
    in.readFully(bytes);
    ConstantPool.Entry[] arrayOfEntry = cls.getCPMap();
    Instruction.opcodeChecker(bytes, arrayOfEntry, cls.version);
    int i = readUnsignedShort();
    paramCode.setHandlerCount(i);
    for (int j = 0; j < i; j++)
    {
      handler_start[j] = readUnsignedShort();
      handler_end[j] = readUnsignedShort();
      handler_catch[j] = readUnsignedShort();
      handler_class[j] = readClassRefOrNull();
    }
    readAttributes(3, paramCode);
  }
  
  void readBootstrapMethods(Package.Class paramClass)
    throws IOException
  {
    ConstantPool.BootstrapMethodEntry[] arrayOfBootstrapMethodEntry = new ConstantPool.BootstrapMethodEntry[readUnsignedShort()];
    for (int i = 0; i < arrayOfBootstrapMethodEntry.length; i++)
    {
      ConstantPool.MethodHandleEntry localMethodHandleEntry = (ConstantPool.MethodHandleEntry)readRef((byte)15);
      ConstantPool.Entry[] arrayOfEntry = new ConstantPool.Entry[readUnsignedShort()];
      for (int j = 0; j < arrayOfEntry.length; j++) {
        arrayOfEntry[j] = readRef(51);
      }
      arrayOfBootstrapMethodEntry[i] = ConstantPool.getBootstrapMethodEntry(localMethodHandleEntry, arrayOfEntry);
    }
    paramClass.setBootstrapMethods(Arrays.asList(arrayOfBootstrapMethodEntry));
  }
  
  void readInnerClasses(Package.Class paramClass)
    throws IOException
  {
    int i = readUnsignedShort();
    ArrayList localArrayList = new ArrayList(i);
    for (int j = 0; j < i; j++)
    {
      Package.InnerClass localInnerClass = new Package.InnerClass(readClassRef(), readClassRefOrNull(), (ConstantPool.Utf8Entry)readRefOrNull((byte)1), readUnsignedShort());
      localArrayList.add(localInnerClass);
    }
    innerClasses = localArrayList;
  }
  
  static class ClassFormatException
    extends IOException
  {
    private static final long serialVersionUID = -3564121733989501833L;
    
    public ClassFormatException(String paramString)
    {
      super();
    }
    
    public ClassFormatException(String paramString, Throwable paramThrowable)
    {
      super(paramThrowable);
    }
  }
  
  private class UnresolvedEntry
    extends ConstantPool.Entry
  {
    final Object[] refsOrIndexes;
    
    UnresolvedEntry(byte paramByte, Object... paramVarArgs)
    {
      super();
      refsOrIndexes = paramVarArgs;
      haveUnresolvedEntry = true;
    }
    
    ConstantPool.Entry resolve()
    {
      Package.Class localClass = cls;
      ConstantPool.InvokeDynamicEntry localInvokeDynamicEntry;
      switch (tag)
      {
      case 18: 
        ConstantPool.BootstrapMethodEntry localBootstrapMethodEntry = (ConstantPool.BootstrapMethodEntry)bootstrapMethods.get(((Integer)refsOrIndexes[0]).intValue());
        ConstantPool.DescriptorEntry localDescriptorEntry = (ConstantPool.DescriptorEntry)refsOrIndexes[1];
        localInvokeDynamicEntry = ConstantPool.getInvokeDynamicEntry(localBootstrapMethodEntry, localDescriptorEntry);
        break;
      default: 
        throw new AssertionError();
      }
      return localInvokeDynamicEntry;
    }
    
    private void unresolved()
    {
      throw new RuntimeException("unresolved entry has no string");
    }
    
    public int compareTo(Object paramObject)
    {
      unresolved();
      return 0;
    }
    
    public boolean equals(Object paramObject)
    {
      unresolved();
      return false;
    }
    
    protected int computeValueHash()
    {
      unresolved();
      return 0;
    }
    
    public String stringValue()
    {
      unresolved();
      return toString();
    }
    
    public String toString()
    {
      return "(unresolved " + ConstantPool.tagName(tag) + ")";
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\java\util\jar\pack\ClassReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package com.sun.java.util.jar.pack;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

class PackageReader
  extends BandStructure
{
  Package pkg;
  byte[] bytes;
  LimitedBuffer in;
  Package.Version packageVersion;
  int[] tagCount = new int[19];
  int numFiles;
  int numAttrDefs;
  int numInnerClasses;
  int numClasses;
  static final int MAGIC_BYTES = 4;
  Map<ConstantPool.Utf8Entry, ConstantPool.SignatureEntry> utf8Signatures;
  static final int NO_FLAGS_YET = 0;
  Comparator<ConstantPool.Entry> entryOutputOrder = new Comparator()
  {
    public int compare(ConstantPool.Entry paramAnonymousEntry1, ConstantPool.Entry paramAnonymousEntry2)
    {
      int i = PackageReader.this.getOutputIndex(paramAnonymousEntry1);
      int j = PackageReader.this.getOutputIndex(paramAnonymousEntry2);
      if ((i >= 0) && (j >= 0)) {
        return i - j;
      }
      if (i == j) {
        return paramAnonymousEntry1.compareTo(paramAnonymousEntry2);
      }
      return i >= 0 ? -1 : 1;
    }
  };
  Code[] allCodes;
  List<Code> codesWithFlags;
  Map<Package.Class, Set<ConstantPool.Entry>> ldcRefMap = new HashMap();
  
  PackageReader(Package paramPackage, InputStream paramInputStream)
    throws IOException
  {
    pkg = paramPackage;
    in = new LimitedBuffer(paramInputStream);
  }
  
  void read()
    throws IOException
  {
    int i = 0;
    try
    {
      readFileHeader();
      readBandHeaders();
      readConstantPool();
      readAttrDefs();
      readInnerClasses();
      Package.Class[] arrayOfClass = readClasses();
      readByteCodes();
      readFiles();
      assert ((archiveSize1 == 0L) || (in.atLimit()));
      assert ((archiveSize1 == 0L) || (in.getBytesServed() == archiveSize0 + archiveSize1));
      all_bands.doneDisbursing();
      for (int j = 0; j < arrayOfClass.length; j++) {
        reconstructClass(arrayOfClass[j]);
      }
      i = 1;
    }
    catch (Exception localException)
    {
      Utils.log.warning("Error on input: " + localException, localException);
      if (verbose > 0) {
        Utils.log.info("Stream offsets: served=" + in.getBytesServed() + " buffered=" + in.buffered + " limit=" + in.limit);
      }
      if ((localException instanceof IOException)) {
        throw ((IOException)localException);
      }
      if ((localException instanceof RuntimeException)) {
        throw ((RuntimeException)localException);
      }
      throw new Error("error unpacking", localException);
    }
  }
  
  void readFileHeader()
    throws IOException
  {
    readArchiveMagic();
    readArchiveHeader();
  }
  
  private int getMagicInt32()
    throws IOException
  {
    int i = 0;
    for (int j = 0; j < 4; j++)
    {
      i <<= 8;
      i |= archive_magic.getByte() & 0xFF;
    }
    return i;
  }
  
  void readArchiveMagic()
    throws IOException
  {
    in.setReadLimit(19L);
    archive_magic.expectLength(4);
    archive_magic.readFrom(in);
    int i = getMagicInt32();
    pkg.getClass();
    if (-889270259 != i)
    {
      pkg.getClass();
      throw new IOException("Unexpected package magic number: got " + i + "; expected " + -889270259);
    }
    archive_magic.doneDisbursing();
  }
  
  void checkArchiveVersion()
    throws IOException
  {
    Object localObject1 = null;
    for (Object localObject3 : new Package.Version[] { Constants.JAVA8_PACKAGE_VERSION, Constants.JAVA7_PACKAGE_VERSION, Constants.JAVA6_PACKAGE_VERSION, Constants.JAVA5_PACKAGE_VERSION }) {
      if (packageVersion.equals(localObject3))
      {
        localObject1 = localObject3;
        break;
      }
    }
    if (localObject1 == null)
    {
      ??? = Constants.JAVA8_PACKAGE_VERSION.toString() + "OR" + Constants.JAVA7_PACKAGE_VERSION.toString() + " OR " + Constants.JAVA6_PACKAGE_VERSION.toString() + " OR " + Constants.JAVA5_PACKAGE_VERSION.toString();
      throw new IOException("Unexpected package minor version: got " + packageVersion.toString() + "; expected " + (String)???);
    }
  }
  
  void readArchiveHeader()
    throws IOException
  {
    archive_header_0.expectLength(3);
    archive_header_0.readFrom(in);
    int i = archive_header_0.getInt();
    int j = archive_header_0.getInt();
    packageVersion = Package.Version.of(j, i);
    checkArchiveVersion();
    initHighestClassVersion(Constants.JAVA7_MAX_CLASS_VERSION);
    archiveOptions = archive_header_0.getInt();
    archive_header_0.doneDisbursing();
    boolean bool1 = testBit(archiveOptions, 1);
    boolean bool2 = testBit(archiveOptions, 16);
    boolean bool3 = testBit(archiveOptions, 2);
    boolean bool4 = testBit(archiveOptions, 8);
    initAttrIndexLimit();
    archive_header_S.expectLength(bool2 ? 2 : 0);
    archive_header_S.readFrom(in);
    if (bool2)
    {
      long l1 = archive_header_S.getInt();
      long l2 = archive_header_S.getInt();
      archiveSize1 = ((l1 << 32) + (l2 << 32 >>> 32));
      in.setReadLimit(archiveSize1);
    }
    else
    {
      archiveSize1 = 0L;
      in.setReadLimit(-1L);
    }
    archive_header_S.doneDisbursing();
    archiveSize0 = in.getBytesServed();
    int k = 10;
    if (bool2) {
      k += 5;
    }
    if (bool1) {
      k += 2;
    }
    if (bool3) {
      k += 4;
    }
    if (bool4) {
      k += 4;
    }
    archive_header_1.expectLength(k);
    archive_header_1.readFrom(in);
    if (bool2)
    {
      archiveNextCount = archive_header_1.getInt();
      pkg.default_modtime = archive_header_1.getInt();
      numFiles = archive_header_1.getInt();
    }
    else
    {
      archiveNextCount = 0;
      numFiles = 0;
    }
    if (bool1)
    {
      band_headers.expectLength(archive_header_1.getInt());
      numAttrDefs = archive_header_1.getInt();
    }
    else
    {
      band_headers.expectLength(0);
      numAttrDefs = 0;
    }
    readConstantPoolCounts(bool3, bool4);
    numInnerClasses = archive_header_1.getInt();
    i = (short)archive_header_1.getInt();
    j = (short)archive_header_1.getInt();
    pkg.defaultClassVersion = Package.Version.of(j, i);
    numClasses = archive_header_1.getInt();
    archive_header_1.doneDisbursing();
    if (testBit(archiveOptions, 32)) {
      pkg.default_options |= 0x1;
    }
  }
  
  void readBandHeaders()
    throws IOException
  {
    band_headers.readFrom(in);
    bandHeaderBytePos = 1;
    bandHeaderBytes = new byte[bandHeaderBytePos + band_headers.length()];
    for (int i = bandHeaderBytePos; i < bandHeaderBytes.length; i++) {
      bandHeaderBytes[i] = ((byte)band_headers.getByte());
    }
    band_headers.doneDisbursing();
  }
  
  void readConstantPoolCounts(boolean paramBoolean1, boolean paramBoolean2)
    throws IOException
  {
    for (int i = 0; i < ConstantPool.TAGS_IN_ORDER.length; i++)
    {
      int j = ConstantPool.TAGS_IN_ORDER[i];
      if (!paramBoolean1) {
        switch (j)
        {
        case 3: 
        case 4: 
        case 5: 
        case 6: 
          break;
        }
      } else if (!paramBoolean2) {
        switch (j)
        {
        case 15: 
        case 16: 
        case 17: 
        case 18: 
          break;
        }
      } else {
        tagCount[j] = archive_header_1.getInt();
      }
    }
  }
  
  protected ConstantPool.Index getCPIndex(byte paramByte)
  {
    return pkg.cp.getIndexByTag(paramByte);
  }
  
  ConstantPool.Index initCPIndex(byte paramByte, ConstantPool.Entry[] paramArrayOfEntry)
  {
    if (verbose > 3) {
      for (int i = 0; i < paramArrayOfEntry.length; i++) {
        Utils.log.fine("cp.add " + paramArrayOfEntry[i]);
      }
    }
    ConstantPool.Index localIndex = ConstantPool.makeIndex(ConstantPool.tagName(paramByte), paramArrayOfEntry);
    if (verbose > 1) {
      Utils.log.fine("Read " + localIndex);
    }
    pkg.cp.initIndexByTag(paramByte, localIndex);
    return localIndex;
  }
  
  void checkLegacy(String paramString)
  {
    if (packageVersion.lessThan(Constants.JAVA7_PACKAGE_VERSION)) {
      throw new RuntimeException("unexpected band " + paramString);
    }
  }
  
  void readConstantPool()
    throws IOException
  {
    if (verbose > 0) {
      Utils.log.info("Reading CP");
    }
    Object localObject1;
    Object localObject2;
    for (int i = 0; i < ConstantPool.TAGS_IN_ORDER.length; i++)
    {
      byte b1 = ConstantPool.TAGS_IN_ORDER[i];
      int k = tagCount[b1];
      localObject1 = new ConstantPool.Entry[k];
      if (verbose > 0) {
        Utils.log.info("Reading " + localObject1.length + " " + ConstantPool.tagName(b1) + " entries...");
      }
      int m;
      int n;
      long l1;
      long l2;
      long l3;
      Object localObject4;
      Object localObject3;
      switch (b1)
      {
      case 1: 
        readUtf8Bands((ConstantPool.Entry[])localObject1);
        break;
      case 3: 
        cp_Int.expectLength(localObject1.length);
        cp_Int.readFrom(in);
        for (m = 0; m < localObject1.length; m++)
        {
          n = cp_Int.getInt();
          localObject1[m] = ConstantPool.getLiteralEntry(Integer.valueOf(n));
        }
        cp_Int.doneDisbursing();
        break;
      case 4: 
        cp_Float.expectLength(localObject1.length);
        cp_Float.readFrom(in);
        for (m = 0; m < localObject1.length; m++)
        {
          n = cp_Float.getInt();
          float f = Float.intBitsToFloat(n);
          localObject1[m] = ConstantPool.getLiteralEntry(Float.valueOf(f));
        }
        cp_Float.doneDisbursing();
        break;
      case 5: 
        cp_Long_hi.expectLength(localObject1.length);
        cp_Long_hi.readFrom(in);
        cp_Long_lo.expectLength(localObject1.length);
        cp_Long_lo.readFrom(in);
        for (m = 0; m < localObject1.length; m++)
        {
          l1 = cp_Long_hi.getInt();
          l2 = cp_Long_lo.getInt();
          l3 = (l1 << 32) + (l2 << 32 >>> 32);
          localObject1[m] = ConstantPool.getLiteralEntry(Long.valueOf(l3));
        }
        cp_Long_hi.doneDisbursing();
        cp_Long_lo.doneDisbursing();
        break;
      case 6: 
        cp_Double_hi.expectLength(localObject1.length);
        cp_Double_hi.readFrom(in);
        cp_Double_lo.expectLength(localObject1.length);
        cp_Double_lo.readFrom(in);
        for (m = 0; m < localObject1.length; m++)
        {
          l1 = cp_Double_hi.getInt();
          l2 = cp_Double_lo.getInt();
          l3 = (l1 << 32) + (l2 << 32 >>> 32);
          double d = Double.longBitsToDouble(l3);
          localObject1[m] = ConstantPool.getLiteralEntry(Double.valueOf(d));
        }
        cp_Double_hi.doneDisbursing();
        cp_Double_lo.doneDisbursing();
        break;
      case 8: 
        cp_String.expectLength(localObject1.length);
        cp_String.readFrom(in);
        cp_String.setIndex(getCPIndex((byte)1));
        for (m = 0; m < localObject1.length; m++) {
          localObject1[m] = ConstantPool.getLiteralEntry(cp_String.getRef().stringValue());
        }
        cp_String.doneDisbursing();
        break;
      case 7: 
        cp_Class.expectLength(localObject1.length);
        cp_Class.readFrom(in);
        cp_Class.setIndex(getCPIndex((byte)1));
        for (m = 0; m < localObject1.length; m++) {
          localObject1[m] = ConstantPool.getClassEntry(cp_Class.getRef().stringValue());
        }
        cp_Class.doneDisbursing();
        break;
      case 13: 
        readSignatureBands((ConstantPool.Entry[])localObject1);
        break;
      case 12: 
        cp_Descr_name.expectLength(localObject1.length);
        cp_Descr_name.readFrom(in);
        cp_Descr_name.setIndex(getCPIndex((byte)1));
        cp_Descr_type.expectLength(localObject1.length);
        cp_Descr_type.readFrom(in);
        cp_Descr_type.setIndex(getCPIndex((byte)13));
        for (m = 0; m < localObject1.length; m++)
        {
          ConstantPool.Entry localEntry = cp_Descr_name.getRef();
          localObject4 = cp_Descr_type.getRef();
          localObject1[m] = ConstantPool.getDescriptorEntry((ConstantPool.Utf8Entry)localEntry, (ConstantPool.SignatureEntry)localObject4);
        }
        cp_Descr_name.doneDisbursing();
        cp_Descr_type.doneDisbursing();
        break;
      case 9: 
        readMemberRefs(b1, (ConstantPool.Entry[])localObject1, cp_Field_class, cp_Field_desc);
        break;
      case 10: 
        readMemberRefs(b1, (ConstantPool.Entry[])localObject1, cp_Method_class, cp_Method_desc);
        break;
      case 11: 
        readMemberRefs(b1, (ConstantPool.Entry[])localObject1, cp_Imethod_class, cp_Imethod_desc);
        break;
      case 15: 
        if (localObject1.length > 0) {
          checkLegacy(cp_MethodHandle_refkind.name());
        }
        cp_MethodHandle_refkind.expectLength(localObject1.length);
        cp_MethodHandle_refkind.readFrom(in);
        cp_MethodHandle_member.expectLength(localObject1.length);
        cp_MethodHandle_member.readFrom(in);
        cp_MethodHandle_member.setIndex(getCPIndex((byte)52));
        for (m = 0; m < localObject1.length; m++)
        {
          byte b2 = (byte)cp_MethodHandle_refkind.getInt();
          localObject4 = (ConstantPool.MemberEntry)cp_MethodHandle_member.getRef();
          localObject1[m] = ConstantPool.getMethodHandleEntry(b2, (ConstantPool.MemberEntry)localObject4);
        }
        cp_MethodHandle_refkind.doneDisbursing();
        cp_MethodHandle_member.doneDisbursing();
        break;
      case 16: 
        if (localObject1.length > 0) {
          checkLegacy(cp_MethodType.name());
        }
        cp_MethodType.expectLength(localObject1.length);
        cp_MethodType.readFrom(in);
        cp_MethodType.setIndex(getCPIndex((byte)13));
        for (m = 0; m < localObject1.length; m++)
        {
          localObject3 = (ConstantPool.SignatureEntry)cp_MethodType.getRef();
          localObject1[m] = ConstantPool.getMethodTypeEntry((ConstantPool.SignatureEntry)localObject3);
        }
        cp_MethodType.doneDisbursing();
        break;
      case 18: 
        if (localObject1.length > 0) {
          checkLegacy(cp_InvokeDynamic_spec.name());
        }
        cp_InvokeDynamic_spec.expectLength(localObject1.length);
        cp_InvokeDynamic_spec.readFrom(in);
        cp_InvokeDynamic_spec.setIndex(getCPIndex((byte)17));
        cp_InvokeDynamic_desc.expectLength(localObject1.length);
        cp_InvokeDynamic_desc.readFrom(in);
        cp_InvokeDynamic_desc.setIndex(getCPIndex((byte)12));
        for (m = 0; m < localObject1.length; m++)
        {
          localObject3 = (ConstantPool.BootstrapMethodEntry)cp_InvokeDynamic_spec.getRef();
          localObject4 = (ConstantPool.DescriptorEntry)cp_InvokeDynamic_desc.getRef();
          localObject1[m] = ConstantPool.getInvokeDynamicEntry((ConstantPool.BootstrapMethodEntry)localObject3, (ConstantPool.DescriptorEntry)localObject4);
        }
        cp_InvokeDynamic_spec.doneDisbursing();
        cp_InvokeDynamic_desc.doneDisbursing();
        break;
      case 17: 
        if (localObject1.length > 0) {
          checkLegacy(cp_BootstrapMethod_ref.name());
        }
        cp_BootstrapMethod_ref.expectLength(localObject1.length);
        cp_BootstrapMethod_ref.readFrom(in);
        cp_BootstrapMethod_ref.setIndex(getCPIndex((byte)15));
        cp_BootstrapMethod_arg_count.expectLength(localObject1.length);
        cp_BootstrapMethod_arg_count.readFrom(in);
        m = cp_BootstrapMethod_arg_count.getIntTotal();
        cp_BootstrapMethod_arg.expectLength(m);
        cp_BootstrapMethod_arg.readFrom(in);
        cp_BootstrapMethod_arg.setIndex(getCPIndex((byte)51));
        for (int i1 = 0; i1 < localObject1.length; i1++)
        {
          localObject4 = (ConstantPool.MethodHandleEntry)cp_BootstrapMethod_ref.getRef();
          int i2 = cp_BootstrapMethod_arg_count.getInt();
          ConstantPool.Entry[] arrayOfEntry2 = new ConstantPool.Entry[i2];
          for (int i3 = 0; i3 < i2; i3++) {
            arrayOfEntry2[i3] = cp_BootstrapMethod_arg.getRef();
          }
          localObject1[i1] = ConstantPool.getBootstrapMethodEntry((ConstantPool.MethodHandleEntry)localObject4, arrayOfEntry2);
        }
        cp_BootstrapMethod_ref.doneDisbursing();
        cp_BootstrapMethod_arg_count.doneDisbursing();
        cp_BootstrapMethod_arg.doneDisbursing();
        break;
      case 2: 
      case 14: 
      default: 
        throw new AssertionError("unexpected CP tag in package");
      }
      localObject2 = initCPIndex(b1, (ConstantPool.Entry[])localObject1);
      if (optDumpBands)
      {
        PrintStream localPrintStream = new PrintStream(getDumpStream((ConstantPool.Index)localObject2, ".idx"));
        localObject4 = null;
        try
        {
          printArrayTo(localPrintStream, cpMap, 0, cpMap.length);
        }
        catch (Throwable localThrowable4)
        {
          localObject4 = localThrowable4;
          throw localThrowable4;
        }
        finally
        {
          if (localPrintStream != null) {
            if (localObject4 != null) {
              try
              {
                localPrintStream.close();
              }
              catch (Throwable localThrowable5)
              {
                ((Throwable)localObject4).addSuppressed(localThrowable5);
              }
            } else {
              localPrintStream.close();
            }
          }
        }
      }
    }
    cp_bands.doneDisbursing();
    if ((optDumpBands) || (verbose > 1))
    {
      int j;
      for (i = 50; i < 54; j = (byte)(i + 1))
      {
        ConstantPool.Index localIndex = pkg.cp.getIndexByTag(i);
        if ((localIndex != null) && (!localIndex.isEmpty()))
        {
          ConstantPool.Entry[] arrayOfEntry1 = cpMap;
          if (verbose > 1) {
            Utils.log.info("Index group " + ConstantPool.tagName(i) + " contains " + arrayOfEntry1.length + " entries.");
          }
          if (optDumpBands)
          {
            localObject1 = new PrintStream(getDumpStream(debugName, i, ".gidx", localIndex));
            localObject2 = null;
            try
            {
              printArrayTo((PrintStream)localObject1, arrayOfEntry1, 0, arrayOfEntry1.length, true);
            }
            catch (Throwable localThrowable2)
            {
              localObject2 = localThrowable2;
              throw localThrowable2;
            }
            finally
            {
              if (localObject1 != null) {
                if (localObject2 != null) {
                  try
                  {
                    ((PrintStream)localObject1).close();
                  }
                  catch (Throwable localThrowable6)
                  {
                    ((Throwable)localObject2).addSuppressed(localThrowable6);
                  }
                } else {
                  ((PrintStream)localObject1).close();
                }
              }
            }
          }
        }
      }
    }
    setBandIndexes();
  }
  
  void readUtf8Bands(ConstantPool.Entry[] paramArrayOfEntry)
    throws IOException
  {
    int i = paramArrayOfEntry.length;
    if (i == 0) {
      return;
    }
    cp_Utf8_prefix.expectLength(Math.max(0, i - 2));
    cp_Utf8_prefix.readFrom(in);
    cp_Utf8_suffix.expectLength(Math.max(0, i - 1));
    cp_Utf8_suffix.readFrom(in);
    char[][] arrayOfChar = new char[i][];
    int j = 0;
    cp_Utf8_chars.expectLength(cp_Utf8_suffix.getIntTotal());
    cp_Utf8_chars.readFrom(in);
    int i1;
    for (int k = 0; k < i; k++)
    {
      m = k < 1 ? 0 : cp_Utf8_suffix.getInt();
      if ((m == 0) && (k >= 1))
      {
        j++;
      }
      else
      {
        arrayOfChar[k] = new char[m];
        for (n = 0; n < m; n++)
        {
          i1 = cp_Utf8_chars.getInt();
          assert (i1 == (char)i1);
          arrayOfChar[k][n] = ((char)i1);
        }
      }
    }
    cp_Utf8_chars.doneDisbursing();
    k = 0;
    cp_Utf8_big_suffix.expectLength(j);
    cp_Utf8_big_suffix.readFrom(in);
    cp_Utf8_suffix.resetForSecondPass();
    for (int m = 0; m < i; m++)
    {
      n = m < 1 ? 0 : cp_Utf8_suffix.getInt();
      i1 = m < 2 ? 0 : cp_Utf8_prefix.getInt();
      if ((n == 0) && (m >= 1))
      {
        assert (arrayOfChar[m] == null);
        n = cp_Utf8_big_suffix.getInt();
      }
      else
      {
        assert (arrayOfChar[m] != null);
      }
      if (k < i1 + n) {
        k = i1 + n;
      }
    }
    char[] arrayOfChar1 = new char[k];
    cp_Utf8_suffix.resetForSecondPass();
    cp_Utf8_big_suffix.resetForSecondPass();
    for (int n = 0; n < i; n++) {
      if (n >= 1)
      {
        i1 = cp_Utf8_suffix.getInt();
        if (i1 == 0)
        {
          i1 = cp_Utf8_big_suffix.getInt();
          arrayOfChar[n] = new char[i1];
          if (i1 != 0)
          {
            BandStructure.IntBand localIntBand = cp_Utf8_big_chars.newIntBand("(Utf8_big_" + n + ")");
            localIntBand.expectLength(i1);
            localIntBand.readFrom(in);
            for (int i3 = 0; i3 < i1; i3++)
            {
              int i4 = localIntBand.getInt();
              assert (i4 == (char)i4);
              arrayOfChar[n][i3] = ((char)i4);
            }
            localIntBand.doneDisbursing();
          }
        }
      }
    }
    cp_Utf8_big_chars.doneDisbursing();
    cp_Utf8_prefix.resetForSecondPass();
    cp_Utf8_suffix.resetForSecondPass();
    cp_Utf8_big_suffix.resetForSecondPass();
    for (n = 0; n < i; n++)
    {
      i1 = n < 2 ? 0 : cp_Utf8_prefix.getInt();
      int i2 = n < 1 ? 0 : cp_Utf8_suffix.getInt();
      if ((i2 == 0) && (n >= 1)) {
        i2 = cp_Utf8_big_suffix.getInt();
      }
      System.arraycopy(arrayOfChar[n], 0, arrayOfChar1, i1, i2);
      paramArrayOfEntry[n] = ConstantPool.getUtf8Entry(new String(arrayOfChar1, 0, i1 + i2));
    }
    cp_Utf8_prefix.doneDisbursing();
    cp_Utf8_suffix.doneDisbursing();
    cp_Utf8_big_suffix.doneDisbursing();
  }
  
  void readSignatureBands(ConstantPool.Entry[] paramArrayOfEntry)
    throws IOException
  {
    cp_Signature_form.expectLength(paramArrayOfEntry.length);
    cp_Signature_form.readFrom(in);
    cp_Signature_form.setIndex(getCPIndex((byte)1));
    int[] arrayOfInt = new int[paramArrayOfEntry.length];
    ConstantPool.Utf8Entry localUtf8Entry;
    for (int i = 0; i < paramArrayOfEntry.length; i++)
    {
      localUtf8Entry = (ConstantPool.Utf8Entry)cp_Signature_form.getRef();
      arrayOfInt[i] = ConstantPool.countClassParts(localUtf8Entry);
    }
    cp_Signature_form.resetForSecondPass();
    cp_Signature_classes.expectLength(getIntTotal(arrayOfInt));
    cp_Signature_classes.readFrom(in);
    cp_Signature_classes.setIndex(getCPIndex((byte)7));
    utf8Signatures = new HashMap();
    for (i = 0; i < paramArrayOfEntry.length; i++)
    {
      localUtf8Entry = (ConstantPool.Utf8Entry)cp_Signature_form.getRef();
      ConstantPool.ClassEntry[] arrayOfClassEntry = new ConstantPool.ClassEntry[arrayOfInt[i]];
      for (int j = 0; j < arrayOfClassEntry.length; j++) {
        arrayOfClassEntry[j] = ((ConstantPool.ClassEntry)cp_Signature_classes.getRef());
      }
      ConstantPool.SignatureEntry localSignatureEntry = ConstantPool.getSignatureEntry(localUtf8Entry, arrayOfClassEntry);
      paramArrayOfEntry[i] = localSignatureEntry;
      utf8Signatures.put(localSignatureEntry.asUtf8Entry(), localSignatureEntry);
    }
    cp_Signature_form.doneDisbursing();
    cp_Signature_classes.doneDisbursing();
  }
  
  void readMemberRefs(byte paramByte, ConstantPool.Entry[] paramArrayOfEntry, BandStructure.CPRefBand paramCPRefBand1, BandStructure.CPRefBand paramCPRefBand2)
    throws IOException
  {
    paramCPRefBand1.expectLength(paramArrayOfEntry.length);
    paramCPRefBand1.readFrom(in);
    paramCPRefBand1.setIndex(getCPIndex((byte)7));
    paramCPRefBand2.expectLength(paramArrayOfEntry.length);
    paramCPRefBand2.readFrom(in);
    paramCPRefBand2.setIndex(getCPIndex((byte)12));
    for (int i = 0; i < paramArrayOfEntry.length; i++)
    {
      ConstantPool.ClassEntry localClassEntry = (ConstantPool.ClassEntry)paramCPRefBand1.getRef();
      ConstantPool.DescriptorEntry localDescriptorEntry = (ConstantPool.DescriptorEntry)paramCPRefBand2.getRef();
      paramArrayOfEntry[i] = ConstantPool.getMemberEntry(paramByte, localClassEntry, localDescriptorEntry);
    }
    paramCPRefBand1.doneDisbursing();
    paramCPRefBand2.doneDisbursing();
  }
  
  void readFiles()
    throws IOException
  {
    if (verbose > 0) {
      Utils.log.info("  ...building " + numFiles + " files...");
    }
    file_name.expectLength(numFiles);
    file_size_lo.expectLength(numFiles);
    int i = archiveOptions;
    boolean bool1 = testBit(i, 256);
    boolean bool2 = testBit(i, 64);
    boolean bool3 = testBit(i, 128);
    if (bool1) {
      file_size_hi.expectLength(numFiles);
    }
    if (bool2) {
      file_modtime.expectLength(numFiles);
    }
    if (bool3) {
      file_options.expectLength(numFiles);
    }
    file_name.readFrom(in);
    file_size_hi.readFrom(in);
    file_size_lo.readFrom(in);
    file_modtime.readFrom(in);
    file_options.readFrom(in);
    file_bits.setInputStreamFrom(in);
    Iterator localIterator = pkg.getClasses().iterator();
    long l1 = 0L;
    long[] arrayOfLong = new long[numFiles];
    for (int j = 0; j < numFiles; j++)
    {
      long l2 = file_size_lo.getInt() << 32 >>> 32;
      if (bool1) {
        l2 += (file_size_hi.getInt() << 32);
      }
      arrayOfLong[j] = l2;
      l1 += l2;
    }
    assert ((in.getReadLimit() == -1L) || (in.getReadLimit() == l1));
    byte[] arrayOfByte = new byte[65536];
    for (int k = 0; k < numFiles; k++)
    {
      ConstantPool.Utf8Entry localUtf8Entry = (ConstantPool.Utf8Entry)file_name.getRef();
      long l3 = arrayOfLong[k];
      Package tmp382_379 = pkg;
      tmp382_379.getClass();
      Package.File localFile = new Package.File(tmp382_379, localUtf8Entry);
      modtime = pkg.default_modtime;
      options = pkg.default_options;
      if (bool2) {
        modtime += file_modtime.getInt();
      }
      if (bool3) {
        options |= file_options.getInt();
      }
      if (verbose > 1) {
        Utils.log.fine("Reading " + l3 + " bytes of " + localUtf8Entry.stringValue());
      }
      int m;
      for (long l4 = l3; l4 > 0L; l4 -= m)
      {
        m = arrayOfByte.length;
        if (m > l4) {
          m = (int)l4;
        }
        m = file_bits.getInputStream().read(arrayOfByte, 0, m);
        if (m < 0) {
          throw new EOFException();
        }
        localFile.addBytes(arrayOfByte, 0, m);
      }
      pkg.addFile(localFile);
      if (localFile.isClassStub())
      {
        assert (localFile.getFileLength() == 0L);
        Package.Class localClass2 = (Package.Class)localIterator.next();
        localClass2.initFile(localFile);
      }
    }
    while (localIterator.hasNext())
    {
      Package.Class localClass1 = (Package.Class)localIterator.next();
      localClass1.initFile(null);
      file.modtime = pkg.default_modtime;
    }
    file_name.doneDisbursing();
    file_size_hi.doneDisbursing();
    file_size_lo.doneDisbursing();
    file_modtime.doneDisbursing();
    file_options.doneDisbursing();
    file_bits.doneDisbursing();
    file_bands.doneDisbursing();
    if ((archiveSize1 != 0L) && (!in.atLimit())) {
      throw new RuntimeException("Predicted archive_size " + archiveSize1 + " != " + (in.getBytesServed() - archiveSize0));
    }
  }
  
  void readAttrDefs()
    throws IOException
  {
    attr_definition_headers.expectLength(numAttrDefs);
    attr_definition_name.expectLength(numAttrDefs);
    attr_definition_layout.expectLength(numAttrDefs);
    attr_definition_headers.readFrom(in);
    attr_definition_name.readFrom(in);
    attr_definition_layout.readFrom(in);
    PrintStream localPrintStream = !optDumpBands ? null : new PrintStream(getDumpStream(attr_definition_headers, ".def"));
    Object localObject1 = null;
    try
    {
      for (int i = 0; i < numAttrDefs; i++)
      {
        int j = attr_definition_headers.getByte();
        ConstantPool.Utf8Entry localUtf8Entry1 = (ConstantPool.Utf8Entry)attr_definition_name.getRef();
        ConstantPool.Utf8Entry localUtf8Entry2 = (ConstantPool.Utf8Entry)attr_definition_layout.getRef();
        int k = j & 0x3;
        int m = (j >> 2) - 1;
        Attribute.Layout localLayout = new Attribute.Layout(k, localUtf8Entry1.stringValue(), localUtf8Entry2.stringValue());
        String str = localLayout.layoutForClassVersion(getHighestClassVersion());
        if (!str.equals(localLayout.layout())) {
          throw new IOException("Bad attribute layout in archive: " + localLayout.layout());
        }
        setAttributeLayoutIndex(localLayout, m);
        if (localPrintStream != null) {
          localPrintStream.println(m + " " + localLayout);
        }
      }
    }
    catch (Throwable localThrowable2)
    {
      localObject1 = localThrowable2;
      throw localThrowable2;
    }
    finally
    {
      if (localPrintStream != null) {
        if (localObject1 != null) {
          try
          {
            localPrintStream.close();
          }
          catch (Throwable localThrowable3)
          {
            ((Throwable)localObject1).addSuppressed(localThrowable3);
          }
        } else {
          localPrintStream.close();
        }
      }
    }
    attr_definition_headers.doneDisbursing();
    attr_definition_name.doneDisbursing();
    attr_definition_layout.doneDisbursing();
    makeNewAttributeBands();
    attr_definition_bands.doneDisbursing();
  }
  
  void readInnerClasses()
    throws IOException
  {
    ic_this_class.expectLength(numInnerClasses);
    ic_this_class.readFrom(in);
    ic_flags.expectLength(numInnerClasses);
    ic_flags.readFrom(in);
    int i = 0;
    int m;
    for (int j = 0; j < numInnerClasses; j++)
    {
      k = ic_flags.getInt();
      m = (k & 0x10000) != 0 ? 1 : 0;
      if (m != 0) {
        i++;
      }
    }
    ic_outer_class.expectLength(i);
    ic_outer_class.readFrom(in);
    ic_name.expectLength(i);
    ic_name.readFrom(in);
    ic_flags.resetForSecondPass();
    ArrayList localArrayList = new ArrayList(numInnerClasses);
    for (int k = 0; k < numInnerClasses; k++)
    {
      m = ic_flags.getInt();
      int n = (m & 0x10000) != 0 ? 1 : 0;
      m &= 0xFFFEFFFF;
      ConstantPool.ClassEntry localClassEntry1 = (ConstantPool.ClassEntry)ic_this_class.getRef();
      ConstantPool.ClassEntry localClassEntry2;
      ConstantPool.Utf8Entry localUtf8Entry;
      if (n != 0)
      {
        localClassEntry2 = (ConstantPool.ClassEntry)ic_outer_class.getRef();
        localUtf8Entry = (ConstantPool.Utf8Entry)ic_name.getRef();
      }
      else
      {
        localObject = localClassEntry1.stringValue();
        String[] arrayOfString = Package.parseInnerClassName((String)localObject);
        assert (arrayOfString != null);
        String str1 = arrayOfString[0];
        String str2 = arrayOfString[2];
        if (str1 == null) {
          localClassEntry2 = null;
        } else {
          localClassEntry2 = ConstantPool.getClassEntry(str1);
        }
        if (str2 == null) {
          localUtf8Entry = null;
        } else {
          localUtf8Entry = ConstantPool.getUtf8Entry(str2);
        }
      }
      Object localObject = new Package.InnerClass(localClassEntry1, localClassEntry2, localUtf8Entry, m);
      assert ((n != 0) || (predictable));
      localArrayList.add(localObject);
    }
    ic_flags.doneDisbursing();
    ic_this_class.doneDisbursing();
    ic_outer_class.doneDisbursing();
    ic_name.doneDisbursing();
    pkg.setAllInnerClasses(localArrayList);
    ic_bands.doneDisbursing();
  }
  
  void readLocalInnerClasses(Package.Class paramClass)
    throws IOException
  {
    int i = class_InnerClasses_N.getInt();
    ArrayList localArrayList = new ArrayList(i);
    for (int j = 0; j < i; j++)
    {
      ConstantPool.ClassEntry localClassEntry = (ConstantPool.ClassEntry)class_InnerClasses_RC.getRef();
      int k = class_InnerClasses_F.getInt();
      Object localObject;
      if (k == 0)
      {
        localObject = pkg.getGlobalInnerClass(localClassEntry);
        assert (localObject != null);
        localArrayList.add(localObject);
      }
      else
      {
        if (k == 65536) {
          k = 0;
        }
        localObject = (ConstantPool.ClassEntry)class_InnerClasses_outer_RCN.getRef();
        ConstantPool.Utf8Entry localUtf8Entry = (ConstantPool.Utf8Entry)class_InnerClasses_name_RUN.getRef();
        localArrayList.add(new Package.InnerClass(localClassEntry, (ConstantPool.ClassEntry)localObject, localUtf8Entry, k));
      }
    }
    paramClass.setInnerClasses(localArrayList);
  }
  
  Package.Class[] readClasses()
    throws IOException
  {
    Package.Class[] arrayOfClass = new Package.Class[numClasses];
    if (verbose > 0) {
      Utils.log.info("  ...building " + arrayOfClass.length + " classes...");
    }
    class_this.expectLength(numClasses);
    class_super.expectLength(numClasses);
    class_interface_count.expectLength(numClasses);
    class_this.readFrom(in);
    class_super.readFrom(in);
    class_interface_count.readFrom(in);
    class_interface.expectLength(class_interface_count.getIntTotal());
    class_interface.readFrom(in);
    for (int i = 0; i < arrayOfClass.length; i++)
    {
      ConstantPool.ClassEntry localClassEntry1 = (ConstantPool.ClassEntry)class_this.getRef();
      ConstantPool.ClassEntry localClassEntry2 = (ConstantPool.ClassEntry)class_super.getRef();
      ConstantPool.ClassEntry[] arrayOfClassEntry = new ConstantPool.ClassEntry[class_interface_count.getInt()];
      for (int j = 0; j < arrayOfClassEntry.length; j++) {
        arrayOfClassEntry[j] = ((ConstantPool.ClassEntry)class_interface.getRef());
      }
      if (localClassEntry2 == localClassEntry1) {
        localClassEntry2 = null;
      }
      Package tmp230_227 = pkg;
      tmp230_227.getClass();
      Package.Class localClass = new Package.Class(tmp230_227, 0, localClassEntry1, localClassEntry2, arrayOfClassEntry);
      arrayOfClass[i] = localClass;
    }
    class_this.doneDisbursing();
    class_super.doneDisbursing();
    class_interface_count.doneDisbursing();
    class_interface.doneDisbursing();
    readMembers(arrayOfClass);
    countAndReadAttrs(0, Arrays.asList(arrayOfClass));
    pkg.trimToSize();
    readCodeHeaders();
    return arrayOfClass;
  }
  
  private int getOutputIndex(ConstantPool.Entry paramEntry)
  {
    assert (tag != 13);
    int i = pkg.cp.untypedIndexOf(paramEntry);
    if (i >= 0) {
      return i;
    }
    if (tag == 1)
    {
      ConstantPool.Entry localEntry = (ConstantPool.Entry)utf8Signatures.get(paramEntry);
      return pkg.cp.untypedIndexOf(localEntry);
    }
    return -1;
  }
  
  void reconstructClass(Package.Class paramClass)
  {
    if (verbose > 1) {
      Utils.log.fine("reconstruct " + paramClass);
    }
    Attribute localAttribute = paramClass.getAttribute(attrClassFileVersion);
    if (localAttribute != null)
    {
      paramClass.removeAttribute(localAttribute);
      version = parseClassFileVersionAttr(localAttribute);
    }
    else
    {
      version = pkg.defaultClassVersion;
    }
    paramClass.expandSourceFile();
    paramClass.setCPMap(reconstructLocalCPMap(paramClass));
  }
  
  ConstantPool.Entry[] reconstructLocalCPMap(Package.Class paramClass)
  {
    Set localSet = (Set)ldcRefMap.get(paramClass);
    HashSet localHashSet = new HashSet();
    paramClass.visitRefs(0, localHashSet);
    ArrayList localArrayList = new ArrayList();
    paramClass.addAttribute(Package.attrBootstrapMethodsEmpty.canonicalInstance());
    ConstantPool.completeReferencesIn(localHashSet, true, localArrayList);
    int i = paramClass.expandLocalICs();
    if (i != 0)
    {
      if (i > 0)
      {
        paramClass.visitInnerClassRefs(0, localHashSet);
      }
      else
      {
        localHashSet.clear();
        paramClass.visitRefs(0, localHashSet);
      }
      ConstantPool.completeReferencesIn(localHashSet, true, localArrayList);
    }
    if (localArrayList.isEmpty())
    {
      attributes.remove(Package.attrBootstrapMethodsEmpty.canonicalInstance());
    }
    else
    {
      localHashSet.add(Package.getRefString("BootstrapMethods"));
      Collections.sort(localArrayList);
      paramClass.setBootstrapMethods(localArrayList);
    }
    int j = 0;
    Object localObject1 = localHashSet.iterator();
    while (((Iterator)localObject1).hasNext())
    {
      ConstantPool.Entry localEntry1 = (ConstantPool.Entry)((Iterator)localObject1).next();
      if (localEntry1.isDoubleWord()) {
        j++;
      }
    }
    localObject1 = new ConstantPool.Entry[1 + j + localHashSet.size()];
    ConstantPool.Entry localEntry2 = 1;
    if (localSet != null)
    {
      assert (localHashSet.containsAll(localSet));
      localObject2 = localSet.iterator();
      while (((Iterator)localObject2).hasNext())
      {
        localEntry3 = (ConstantPool.Entry)((Iterator)localObject2).next();
        localObject1[(localEntry2++)] = localEntry3;
      }
      assert (localEntry2 == 1 + localSet.size());
      localHashSet.removeAll(localSet);
      localSet = null;
    }
    Object localObject2 = localHashSet;
    localHashSet = null;
    ConstantPool.Entry localEntry3 = localEntry2;
    Iterator localIterator = ((Set)localObject2).iterator();
    while (localIterator.hasNext())
    {
      localEntry4 = (ConstantPool.Entry)localIterator.next();
      localObject1[(localEntry2++)] = localEntry4;
    }
    assert (localEntry2 == localEntry3 + ((Set)localObject2).size());
    Arrays.sort((Object[])localObject1, 1, localEntry3, entryOutputOrder);
    Arrays.sort((Object[])localObject1, localEntry3, localEntry2, entryOutputOrder);
    if (verbose > 3)
    {
      Utils.log.fine("CP of " + this + " {");
      for (k = 0; k < localEntry2; k++)
      {
        localEntry4 = localObject1[k];
        Utils.log.fine("  " + (localEntry4 == null ? -1 : getOutputIndex(localEntry4)) + " : " + localEntry4);
      }
      Utils.log.fine("}");
    }
    int k = localObject1.length;
    ConstantPool.Entry localEntry4 = localEntry2;
    for (;;)
    {
      localEntry4--;
      if (localEntry4 < 1) {
        break;
      }
      Object localObject3 = localObject1[localEntry4];
      if (((ConstantPool.Entry)localObject3).isDoubleWord()) {
        localObject1[(--k)] = null;
      }
      localObject1[(--k)] = localObject3;
    }
    assert (k == 1);
    return (ConstantPool.Entry[])localObject1;
  }
  
  void readMembers(Package.Class[] paramArrayOfClass)
    throws IOException
  {
    assert (paramArrayOfClass.length == numClasses);
    class_field_count.expectLength(numClasses);
    class_method_count.expectLength(numClasses);
    class_field_count.readFrom(in);
    class_method_count.readFrom(in);
    int i = class_field_count.getIntTotal();
    int j = class_method_count.getIntTotal();
    field_descr.expectLength(i);
    method_descr.expectLength(j);
    if (verbose > 1) {
      Utils.log.fine("expecting #fields=" + i + " and #methods=" + j + " in #classes=" + numClasses);
    }
    ArrayList localArrayList1 = new ArrayList(i);
    field_descr.readFrom(in);
    int i1;
    for (int k = 0; k < paramArrayOfClass.length; k++)
    {
      Package.Class localClass1 = paramArrayOfClass[k];
      int n = class_field_count.getInt();
      for (i1 = 0; i1 < n; i1++)
      {
        Package.Class tmp218_216 = localClass1;
        tmp218_216.getClass();
        Package.Class.Field localField = new Package.Class.Field(tmp218_216, 0, (ConstantPool.DescriptorEntry)field_descr.getRef());
        localArrayList1.add(localField);
      }
    }
    class_field_count.doneDisbursing();
    field_descr.doneDisbursing();
    countAndReadAttrs(1, localArrayList1);
    localArrayList1 = null;
    ArrayList localArrayList2 = new ArrayList(j);
    method_descr.readFrom(in);
    for (int m = 0; m < paramArrayOfClass.length; m++)
    {
      Package.Class localClass2 = paramArrayOfClass[m];
      i1 = class_method_count.getInt();
      for (int i2 = 0; i2 < i1; i2++)
      {
        Package.Class tmp347_345 = localClass2;
        tmp347_345.getClass();
        Package.Class.Method localMethod = new Package.Class.Method(tmp347_345, 0, (ConstantPool.DescriptorEntry)method_descr.getRef());
        localArrayList2.add(localMethod);
      }
    }
    class_method_count.doneDisbursing();
    method_descr.doneDisbursing();
    countAndReadAttrs(2, localArrayList2);
    allCodes = buildCodeAttrs(localArrayList2);
  }
  
  Code[] buildCodeAttrs(List<Package.Class.Method> paramList)
  {
    ArrayList localArrayList = new ArrayList(paramList.size());
    Object localObject = paramList.iterator();
    while (((Iterator)localObject).hasNext())
    {
      Package.Class.Method localMethod = (Package.Class.Method)((Iterator)localObject).next();
      if (localMethod.getAttribute(attrCodeEmpty) != null)
      {
        code = new Code(localMethod);
        localArrayList.add(code);
      }
    }
    localObject = new Code[localArrayList.size()];
    localArrayList.toArray((Object[])localObject);
    return (Code[])localObject;
  }
  
  void readCodeHeaders()
    throws IOException
  {
    boolean bool = testBit(archiveOptions, 4);
    code_headers.expectLength(allCodes.length);
    code_headers.readFrom(in);
    ArrayList localArrayList = new ArrayList(allCodes.length / 10);
    Code localCode;
    for (int i = 0; i < allCodes.length; i++)
    {
      localCode = allCodes[i];
      int j = code_headers.getByte();
      assert (j == (j & 0xFF));
      if (verbose > 2) {
        Utils.log.fine("codeHeader " + localCode + " = " + j);
      }
      if (j == 0)
      {
        localArrayList.add(localCode);
      }
      else
      {
        localCode.setMaxStack(shortCodeHeader_max_stack(j));
        localCode.setMaxNALocals(shortCodeHeader_max_na_locals(j));
        localCode.setHandlerCount(shortCodeHeader_handler_count(j));
        assert (shortCodeHeader(localCode) == j);
      }
    }
    code_headers.doneDisbursing();
    code_max_stack.expectLength(localArrayList.size());
    code_max_na_locals.expectLength(localArrayList.size());
    code_handler_count.expectLength(localArrayList.size());
    code_max_stack.readFrom(in);
    code_max_na_locals.readFrom(in);
    code_handler_count.readFrom(in);
    Iterator localIterator = localArrayList.iterator();
    while (localIterator.hasNext())
    {
      localCode = (Code)localIterator.next();
      localCode.setMaxStack(code_max_stack.getInt());
      localCode.setMaxNALocals(code_max_na_locals.getInt());
      localCode.setHandlerCount(code_handler_count.getInt());
    }
    code_max_stack.doneDisbursing();
    code_max_na_locals.doneDisbursing();
    code_handler_count.doneDisbursing();
    readCodeHandlers();
    if (bool) {
      codesWithFlags = Arrays.asList(allCodes);
    } else {
      codesWithFlags = localArrayList;
    }
    countAttrs(3, codesWithFlags);
  }
  
  void readCodeHandlers()
    throws IOException
  {
    int i = 0;
    for (int j = 0; j < allCodes.length; j++)
    {
      Code localCode1 = allCodes[j];
      i += localCode1.getHandlerCount();
    }
    BandStructure.ValueBand[] arrayOfValueBand = { code_handler_start_P, code_handler_end_PO, code_handler_catch_PO, code_handler_class_RCN };
    for (int k = 0; k < arrayOfValueBand.length; k++)
    {
      arrayOfValueBand[k].expectLength(i);
      arrayOfValueBand[k].readFrom(in);
    }
    for (k = 0; k < allCodes.length; k++)
    {
      Code localCode2 = allCodes[k];
      int m = 0;
      int n = localCode2.getHandlerCount();
      while (m < n)
      {
        handler_class[m] = code_handler_class_RCN.getRef();
        handler_start[m] = code_handler_start_P.getInt();
        handler_end[m] = code_handler_end_PO.getInt();
        handler_catch[m] = code_handler_catch_PO.getInt();
        m++;
      }
    }
    for (k = 0; k < arrayOfValueBand.length; k++) {
      arrayOfValueBand[k].doneDisbursing();
    }
  }
  
  void fixupCodeHandlers()
  {
    for (int i = 0; i < allCodes.length; i++)
    {
      Code localCode = allCodes[i];
      int j = 0;
      int k = localCode.getHandlerCount();
      while (j < k)
      {
        int m = handler_start[j];
        handler_start[j] = localCode.decodeBCI(m);
        m += handler_end[j];
        handler_end[j] = localCode.decodeBCI(m);
        m += handler_catch[j];
        handler_catch[j] = localCode.decodeBCI(m);
        j++;
      }
    }
  }
  
  void countAndReadAttrs(int paramInt, Collection<? extends Attribute.Holder> paramCollection)
    throws IOException
  {
    countAttrs(paramInt, paramCollection);
    readAttrs(paramInt, paramCollection);
  }
  
  void countAttrs(int paramInt, Collection<? extends Attribute.Holder> paramCollection)
    throws IOException
  {
    BandStructure.MultiBand localMultiBand = attrBands[paramInt];
    long l1 = attrFlagMask[paramInt];
    if (verbose > 1) {
      Utils.log.fine("scanning flags and attrs for " + Attribute.contextName(paramInt) + "[" + paramCollection.size() + "]");
    }
    List localList = (List)attrDefs.get(paramInt);
    Attribute.Layout[] arrayOfLayout = new Attribute.Layout[localList.size()];
    localList.toArray(arrayOfLayout);
    BandStructure.IntBand localIntBand1 = getAttrBand(localMultiBand, 0);
    BandStructure.IntBand localIntBand2 = getAttrBand(localMultiBand, 1);
    BandStructure.IntBand localIntBand3 = getAttrBand(localMultiBand, 2);
    BandStructure.IntBand localIntBand4 = getAttrBand(localMultiBand, 3);
    BandStructure.IntBand localIntBand5 = getAttrBand(localMultiBand, 4);
    int i = attrOverflowMask[paramInt];
    int j = 0;
    boolean bool1 = haveFlagsHi(paramInt);
    localIntBand1.expectLength(bool1 ? paramCollection.size() : 0);
    localIntBand1.readFrom(in);
    localIntBand2.expectLength(paramCollection.size());
    localIntBand2.readFrom(in);
    assert ((l1 & i) == i);
    Object localObject1 = paramCollection.iterator();
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (Attribute.Holder)((Iterator)localObject1).next();
      int m = localIntBand2.getInt();
      flags = m;
      if ((m & i) != 0) {
        j++;
      }
    }
    localIntBand3.expectLength(j);
    localIntBand3.readFrom(in);
    localIntBand4.expectLength(localIntBand3.getIntTotal());
    localIntBand4.readFrom(in);
    localObject1 = new int[arrayOfLayout.length];
    Object localObject2 = paramCollection.iterator();
    int i2;
    while (((Iterator)localObject2).hasNext())
    {
      Attribute.Holder localHolder = (Attribute.Holder)((Iterator)localObject2).next();
      assert (attributes == null);
      long l2 = (flags & l1) << 32 >>> 32;
      flags -= (int)l2;
      assert (flags == (char)flags);
      assert ((paramInt != 3) || (flags == 0));
      if (bool1) {
        l2 += (localIntBand1.getInt() << 32);
      }
      if (l2 != 0L)
      {
        i2 = 0;
        long l3 = l2 & i;
        assert (l3 >= 0L);
        l2 -= l3;
        if (l3 != 0L) {
          i2 = localIntBand3.getInt();
        }
        int i4 = 0;
        long l4 = l2;
        for (int i7 = 0; l4 != 0L; i7++) {
          if ((l4 & 1L << i7) != 0L)
          {
            l4 -= (1L << i7);
            i4++;
          }
        }
        ArrayList localArrayList = new ArrayList(i4 + i2);
        attributes = localArrayList;
        l4 = l2;
        Attribute localAttribute;
        for (int i9 = 0; l4 != 0L; i9++) {
          if ((l4 & 1L << i9) != 0L)
          {
            l4 -= (1L << i9);
            localObject1[i9] += 1;
            if (arrayOfLayout[i9] == null) {
              badAttrIndex(i9, paramInt);
            }
            localAttribute = arrayOfLayout[i9].canonicalInstance();
            localArrayList.add(localAttribute);
            i4--;
          }
        }
        assert (i4 == 0);
        while (i2 > 0)
        {
          i9 = localIntBand4.getInt();
          localObject1[i9] += 1;
          if (arrayOfLayout[i9] == null) {
            badAttrIndex(i9, paramInt);
          }
          localAttribute = arrayOfLayout[i9].canonicalInstance();
          localArrayList.add(localAttribute);
          i2--;
        }
      }
    }
    localIntBand1.doneDisbursing();
    localIntBand2.doneDisbursing();
    localIntBand3.doneDisbursing();
    localIntBand4.doneDisbursing();
    int k = 0;
    int i1;
    Attribute.Layout localLayout;
    Object localObject3;
    int i3;
    for (int n = 1;; n = 0)
    {
      for (i1 = 0; i1 < arrayOfLayout.length; i1++)
      {
        localLayout = arrayOfLayout[i1];
        if ((localLayout != null) && (n == isPredefinedAttr(paramInt, i1)))
        {
          i2 = localObject1[i1];
          if (i2 != 0)
          {
            localObject3 = localLayout.getCallables();
            for (i3 = 0; i3 < localObject3.length; i3++)
            {
              assert (kind == 10);
              if (localObject3[i3].flagTest((byte)8)) {
                k++;
              }
            }
          }
        }
      }
      if (n == 0) {
        break;
      }
    }
    localIntBand5.expectLength(k);
    localIntBand5.readFrom(in);
    for (n = 1;; n = 0)
    {
      for (i1 = 0; i1 < arrayOfLayout.length; i1++)
      {
        localLayout = arrayOfLayout[i1];
        if ((localLayout != null) && (n == isPredefinedAttr(paramInt, i1)))
        {
          i2 = localObject1[i1];
          localObject3 = (BandStructure.Band[])attrBandTable.get(localLayout);
          if (localLayout == attrInnerClassesEmpty)
          {
            class_InnerClasses_N.expectLength(i2);
            class_InnerClasses_N.readFrom(in);
            i3 = class_InnerClasses_N.getIntTotal();
            class_InnerClasses_RC.expectLength(i3);
            class_InnerClasses_RC.readFrom(in);
            class_InnerClasses_F.expectLength(i3);
            class_InnerClasses_F.readFrom(in);
            i3 -= class_InnerClasses_F.getIntCount(0);
            class_InnerClasses_outer_RCN.expectLength(i3);
            class_InnerClasses_outer_RCN.readFrom(in);
            class_InnerClasses_name_RUN.expectLength(i3);
            class_InnerClasses_name_RUN.readFrom(in);
          }
          else if ((!optDebugBands) && (i2 == 0))
          {
            for (i3 = 0; i3 < localObject3.length; i3++) {
              localObject3[i3].doneWithUnusedBand();
            }
          }
          else
          {
            boolean bool2 = localLayout.hasCallables();
            if (!bool2)
            {
              readAttrBands(elems, i2, new int[0], (BandStructure.Band[])localObject3);
            }
            else
            {
              Attribute.Layout.Element[] arrayOfElement = localLayout.getCallables();
              int[] arrayOfInt = new int[arrayOfElement.length];
              arrayOfInt[0] = i2;
              for (int i6 = 0; i6 < arrayOfElement.length; i6++)
              {
                assert (kind == 10);
                int i8 = arrayOfInt[i6];
                arrayOfInt[i6] = -1;
                if ((i2 > 0) && (arrayOfElement[i6].flagTest((byte)8))) {
                  i8 += localIntBand5.getInt();
                }
                readAttrBands(body, i8, arrayOfInt, (BandStructure.Band[])localObject3);
              }
            }
            if ((optDebugBands) && (i2 == 0)) {
              for (int i5 = 0; i5 < localObject3.length; i5++) {
                localObject3[i5].doneDisbursing();
              }
            }
          }
        }
      }
      if (n == 0) {
        break;
      }
    }
    localIntBand5.doneDisbursing();
  }
  
  void badAttrIndex(int paramInt1, int paramInt2)
    throws IOException
  {
    throw new IOException("Unknown attribute index " + paramInt1 + " for " + Constants.ATTR_CONTEXT_NAME[paramInt2] + " attribute");
  }
  
  void readAttrs(int paramInt, Collection<? extends Attribute.Holder> paramCollection)
    throws IOException
  {
    HashSet localHashSet = new HashSet();
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    Object localObject1 = paramCollection.iterator();
    final Object localObject2;
    Object localObject3;
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (Attribute.Holder)((Iterator)localObject1).next();
      if (attributes != null)
      {
        localObject3 = attributes.listIterator();
        while (((ListIterator)localObject3).hasNext())
        {
          Attribute localAttribute = (Attribute)((ListIterator)localObject3).next();
          Attribute.Layout localLayout = localAttribute.layout();
          if (bandCount == 0)
          {
            if (localLayout == attrInnerClassesEmpty) {
              readLocalInnerClasses((Package.Class)localObject2);
            }
          }
          else
          {
            localHashSet.add(localLayout);
            int k = (paramInt == 1) && (localLayout == attrConstantValue) ? 1 : 0;
            if (k != 0) {
              setConstantValueIndex((Package.Class.Field)localObject2);
            }
            if (verbose > 2) {
              Utils.log.fine("read " + localAttribute + " in " + localObject2);
            }
            final BandStructure.Band[] arrayOfBand = (BandStructure.Band[])attrBandTable.get(localLayout);
            localByteArrayOutputStream.reset();
            Object localObject4 = localAttribute.unparse(new Attribute.ValueStream()
            {
              public int getInt(int paramAnonymousInt)
              {
                return ((BandStructure.IntBand)arrayOfBand[paramAnonymousInt]).getInt();
              }
              
              public ConstantPool.Entry getRef(int paramAnonymousInt)
              {
                return ((BandStructure.CPRefBand)arrayOfBand[paramAnonymousInt]).getRef();
              }
              
              public int decodeBCI(int paramAnonymousInt)
              {
                Code localCode = (Code)localObject2;
                return localCode.decodeBCI(paramAnonymousInt);
              }
            }, localByteArrayOutputStream);
            ((ListIterator)localObject3).set(localAttribute.addContent(localByteArrayOutputStream.toByteArray(), localObject4));
            if (k != 0) {
              setConstantValueIndex(null);
            }
          }
        }
      }
    }
    localObject1 = localHashSet.iterator();
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (Attribute.Layout)((Iterator)localObject1).next();
      if (localObject2 != null)
      {
        localObject3 = (BandStructure.Band[])attrBandTable.get(localObject2);
        for (int j = 0; j < localObject3.length; j++) {
          localObject3[j].doneDisbursing();
        }
      }
    }
    if (paramInt == 0)
    {
      class_InnerClasses_N.doneDisbursing();
      class_InnerClasses_RC.doneDisbursing();
      class_InnerClasses_F.doneDisbursing();
      class_InnerClasses_outer_RCN.doneDisbursing();
      class_InnerClasses_name_RUN.doneDisbursing();
    }
    localObject1 = attrBands[paramInt];
    for (int i = 0; i < ((BandStructure.MultiBand)localObject1).size(); i++)
    {
      localObject3 = ((BandStructure.MultiBand)localObject1).get(i);
      if ((localObject3 instanceof BandStructure.MultiBand)) {
        ((BandStructure.Band)localObject3).doneDisbursing();
      }
    }
    ((BandStructure.MultiBand)localObject1).doneDisbursing();
  }
  
  private void readAttrBands(Attribute.Layout.Element[] paramArrayOfElement, int paramInt, int[] paramArrayOfInt, BandStructure.Band[] paramArrayOfBand)
    throws IOException
  {
    for (int i = 0; i < paramArrayOfElement.length; i++)
    {
      Attribute.Layout.Element localElement = paramArrayOfElement[i];
      BandStructure.Band localBand = null;
      if (localElement.hasBand())
      {
        localBand = paramArrayOfBand[bandIndex];
        localBand.expectLength(paramInt);
        localBand.readFrom(in);
      }
      switch (kind)
      {
      case 5: 
        int j = ((BandStructure.IntBand)localBand).getIntTotal();
        readAttrBands(body, j, paramArrayOfInt, paramArrayOfBand);
        break;
      case 7: 
        int k = paramInt;
        for (int m = 0; m < body.length; m++)
        {
          int n;
          if (m == body.length - 1)
          {
            n = k;
          }
          else
          {
            n = 0;
            int i1 = m;
            while ((m == i1) || ((m < body.length) && (body[m].flagTest((byte)8))))
            {
              n += ((BandStructure.IntBand)localBand).getIntCount(body[m].value);
              m++;
            }
            m--;
          }
          k -= n;
          readAttrBands(body[m].body, n, paramArrayOfInt, paramArrayOfBand);
        }
        if ((!$assertionsDisabled) && (k != 0)) {
          throw new AssertionError();
        }
        break;
      case 9: 
        assert (body.length == 1);
        assert (body[0].kind == 10);
        if (!localElement.flagTest((byte)8))
        {
          assert (paramArrayOfInt[value] >= 0);
          paramArrayOfInt[value] += paramInt;
        }
        break;
      case 10: 
        if (!$assertionsDisabled) {
          throw new AssertionError();
        }
        break;
      }
    }
  }
  
  void readByteCodes()
    throws IOException
  {
    bc_codes.elementCountForDebug = allCodes.length;
    bc_codes.setInputStreamFrom(in);
    readByteCodeOps();
    bc_codes.doneDisbursing();
    BandStructure.Band[] arrayOfBand = { bc_case_value, bc_byte, bc_short, bc_local, bc_label, bc_intref, bc_floatref, bc_longref, bc_doubleref, bc_stringref, bc_loadablevalueref, bc_classref, bc_fieldref, bc_methodref, bc_imethodref, bc_indyref, bc_thisfield, bc_superfield, bc_thismethod, bc_supermethod, bc_initref, bc_escref, bc_escrefsize, bc_escsize };
    for (int i = 0; i < arrayOfBand.length; i++) {
      arrayOfBand[i].readFrom(in);
    }
    bc_escbyte.expectLength(bc_escsize.getIntTotal());
    bc_escbyte.readFrom(in);
    expandByteCodeOps();
    bc_case_count.doneDisbursing();
    for (i = 0; i < arrayOfBand.length; i++) {
      arrayOfBand[i].doneDisbursing();
    }
    bc_escbyte.doneDisbursing();
    bc_bands.doneDisbursing();
    readAttrs(3, codesWithFlags);
    fixupCodeHandlers();
    code_bands.doneDisbursing();
    class_bands.doneDisbursing();
  }
  
  private void readByteCodeOps()
    throws IOException
  {
    byte[] arrayOfByte = new byte['က'];
    ArrayList localArrayList = new ArrayList();
    Object localObject;
    int j;
    int k;
    label529:
    for (int i = 0; i < allCodes.length; i++)
    {
      localObject = allCodes[i];
      for (j = 0;; j++)
      {
        k = bc_codes.getByte();
        if (j + 10 > arrayOfByte.length) {
          arrayOfByte = realloc(arrayOfByte);
        }
        arrayOfByte[j] = ((byte)k);
        int m = 0;
        if (k == 196)
        {
          k = bc_codes.getByte();
          arrayOfByte[(++j)] = ((byte)k);
          m = 1;
        }
        assert (k == (0xFF & k));
        switch (k)
        {
        case 170: 
        case 171: 
          bc_case_count.expectMoreLength(1);
          localArrayList.add(Integer.valueOf(k));
          break;
        case 132: 
          bc_local.expectMoreLength(1);
          if (m != 0) {
            bc_short.expectMoreLength(1);
          } else {
            bc_byte.expectMoreLength(1);
          }
          break;
        case 17: 
          bc_short.expectMoreLength(1);
          break;
        case 16: 
          bc_byte.expectMoreLength(1);
          break;
        case 188: 
          bc_byte.expectMoreLength(1);
          break;
        case 197: 
          assert (getCPRefOpBand(k) == bc_classref);
          bc_classref.expectMoreLength(1);
          bc_byte.expectMoreLength(1);
          break;
        case 253: 
          bc_escrefsize.expectMoreLength(1);
          bc_escref.expectMoreLength(1);
          break;
        case 254: 
          bc_escsize.expectMoreLength(1);
          break;
        default: 
          if (Instruction.isInvokeInitOp(k))
          {
            bc_initref.expectMoreLength(1);
          }
          else
          {
            BandStructure.CPRefBand localCPRefBand;
            if (Instruction.isSelfLinkerOp(k))
            {
              localCPRefBand = selfOpRefBand(k);
              localCPRefBand.expectMoreLength(1);
            }
            else if (Instruction.isBranchOp(k))
            {
              bc_label.expectMoreLength(1);
            }
            else if (Instruction.isCPRefOp(k))
            {
              localCPRefBand = getCPRefOpBand(k);
              localCPRefBand.expectMoreLength(1);
              if ((!$assertionsDisabled) && (k == 197)) {
                throw new AssertionError();
              }
            }
            else if (Instruction.isLocalSlotOp(k))
            {
              bc_local.expectMoreLength(1);
            }
          }
          break;
        case 255: 
          bytes = realloc(arrayOfByte, j);
          break label529;
        }
      }
    }
    bc_case_count.readFrom(in);
    Iterator localIterator = localArrayList.iterator();
    while (localIterator.hasNext())
    {
      localObject = (Integer)localIterator.next();
      j = ((Integer)localObject).intValue();
      k = bc_case_count.getInt();
      bc_label.expectMoreLength(1 + k);
      bc_case_value.expectMoreLength(j == 170 ? 1 : k);
    }
    bc_case_count.resetForSecondPass();
  }
  
  private void expandByteCodeOps()
    throws IOException
  {
    byte[] arrayOfByte1 = new byte['က'];
    int[] arrayOfInt1 = new int['က'];
    int[] arrayOfInt2 = new int['Ѐ'];
    Fixups localFixups = new Fixups();
    for (int i = 0; i < allCodes.length; i++)
    {
      Code localCode = allCodes[i];
      byte[] arrayOfByte2 = bytes;
      bytes = null;
      Package.Class localClass = localCode.thisClass();
      Object localObject1 = (Set)ldcRefMap.get(localClass);
      if (localObject1 == null) {
        ldcRefMap.put(localClass, localObject1 = new HashSet());
      }
      ConstantPool.ClassEntry localClassEntry1 = thisClass;
      ConstantPool.ClassEntry localClassEntry2 = superClass;
      ConstantPool.ClassEntry localClassEntry3 = null;
      int j = 0;
      int k = 0;
      int m = 0;
      int n = 0;
      localFixups.clear();
      int i3;
      int i6;
      int i9;
      for (int i1 = 0; i1 < arrayOfByte2.length; i1++)
      {
        i2 = Instruction.getByte(arrayOfByte2, i1);
        i3 = j;
        arrayOfInt1[(k++)] = i3;
        if (j + 10 > arrayOfByte1.length) {
          arrayOfByte1 = realloc(arrayOfByte1);
        }
        if (k + 10 > arrayOfInt1.length) {
          arrayOfInt1 = realloc(arrayOfInt1);
        }
        if (m + 10 > arrayOfInt2.length) {
          arrayOfInt2 = realloc(arrayOfInt2);
        }
        int i4 = 0;
        if (i2 == 196)
        {
          arrayOfByte1[(j++)] = ((byte)i2);
          i2 = Instruction.getByte(arrayOfByte2, ++i1);
          i4 = 1;
        }
        int i5;
        int i10;
        switch (i2)
        {
        case 170: 
        case 171: 
          i5 = bc_case_count.getInt();
          while (j + 30 + i5 * 8 > arrayOfByte1.length) {
            arrayOfByte1 = realloc(arrayOfByte1);
          }
          arrayOfByte1[(j++)] = ((byte)i2);
          Arrays.fill(arrayOfByte1, j, j + 30, (byte)0);
          Instruction.Switch localSwitch2 = (Instruction.Switch)Instruction.at(arrayOfByte1, i3);
          localSwitch2.setCaseCount(i5);
          if (i2 == 170) {
            localSwitch2.setCaseValue(0, bc_case_value.getInt());
          } else {
            for (i10 = 0; i10 < i5; i10++) {
              localSwitch2.setCaseValue(i10, bc_case_value.getInt());
            }
          }
          arrayOfInt2[(m++)] = i3;
          j = localSwitch2.getNextPC();
          break;
        case 132: 
          arrayOfByte1[(j++)] = ((byte)i2);
          i5 = bc_local.getInt();
          int i7;
          if (i4 != 0)
          {
            i7 = bc_short.getInt();
            Instruction.setShort(arrayOfByte1, j, i5);
            j += 2;
            Instruction.setShort(arrayOfByte1, j, i7);
            j += 2;
          }
          else
          {
            i7 = (byte)bc_byte.getByte();
            arrayOfByte1[(j++)] = ((byte)i5);
            arrayOfByte1[(j++)] = ((byte)i7);
          }
          break;
        case 17: 
          i5 = bc_short.getInt();
          arrayOfByte1[(j++)] = ((byte)i2);
          Instruction.setShort(arrayOfByte1, j, i5);
          j += 2;
          break;
        case 16: 
        case 188: 
          i5 = bc_byte.getByte();
          arrayOfByte1[(j++)] = ((byte)i2);
          arrayOfByte1[(j++)] = ((byte)i5);
          break;
        case 253: 
          n = 1;
          i5 = bc_escrefsize.getInt();
          ConstantPool.Entry localEntry = bc_escref.getRef();
          if (i5 == 1) {
            ((Set)localObject1).add(localEntry);
          }
          switch (i5)
          {
          case 1: 
            localFixups.addU1(j, localEntry);
            break;
          case 2: 
            localFixups.addU2(j, localEntry);
            break;
          default: 
            if (!$assertionsDisabled) {
              throw new AssertionError();
            }
            i10 = 0;
          }
          arrayOfByte1[(j + 0)] = (arrayOfByte1[(j + 1)] = 0);
          j += i5;
          break;
        case 254: 
          n = 1;
          i5 = bc_escsize.getInt();
          while (j + i5 > arrayOfByte1.length) {
            arrayOfByte1 = realloc(arrayOfByte1);
          }
          while (i5-- > 0) {
            arrayOfByte1[(j++)] = ((byte)bc_escbyte.getByte());
          }
          break;
        default: 
          int i8;
          int i12;
          if (Instruction.isInvokeInitOp(i2))
          {
            i5 = i2 - 230;
            i8 = 183;
            ConstantPool.ClassEntry localClassEntry4;
            switch (i5)
            {
            case 0: 
              localClassEntry4 = localClassEntry1;
              break;
            case 1: 
              localClassEntry4 = localClassEntry2;
              break;
            default: 
              assert (i5 == 2);
              localClassEntry4 = localClassEntry3;
            }
            arrayOfByte1[(j++)] = ((byte)i8);
            i12 = bc_initref.getInt();
            ConstantPool.MemberEntry localMemberEntry1 = pkg.cp.getOverloadingForIndex((byte)10, localClassEntry4, "<init>", i12);
            localFixups.addU2(j, localMemberEntry1);
            arrayOfByte1[(j + 0)] = (arrayOfByte1[(j + 1)] = 0);
            j += 2;
            if ((!$assertionsDisabled) && (Instruction.opLength(i8) != j - i3)) {
              throw new AssertionError();
            }
          }
          else
          {
            int i11;
            boolean bool;
            if (Instruction.isSelfLinkerOp(i2))
            {
              i5 = i2 - 202;
              i8 = i5 >= 14 ? 1 : 0;
              if (i8 != 0) {
                i5 -= 14;
              }
              i11 = i5 >= 7 ? 1 : 0;
              if (i11 != 0) {
                i5 -= 7;
              }
              i12 = 178 + i5;
              bool = Instruction.isFieldOp(i12);
              ConstantPool.ClassEntry localClassEntry5 = i8 != 0 ? localClassEntry2 : localClassEntry1;
              BandStructure.CPRefBand localCPRefBand2;
              ConstantPool.Index localIndex;
              if (bool)
              {
                localCPRefBand2 = i8 != 0 ? bc_superfield : bc_thisfield;
                localIndex = pkg.cp.getMemberIndex((byte)9, localClassEntry5);
              }
              else
              {
                localCPRefBand2 = i8 != 0 ? bc_supermethod : bc_thismethod;
                localIndex = pkg.cp.getMemberIndex((byte)10, localClassEntry5);
              }
              assert (localCPRefBand2 == selfOpRefBand(i2));
              ConstantPool.MemberEntry localMemberEntry2 = (ConstantPool.MemberEntry)localCPRefBand2.getRef(localIndex);
              if (i11 != 0)
              {
                arrayOfByte1[(j++)] = 42;
                i3 = j;
                arrayOfInt1[(k++)] = i3;
              }
              arrayOfByte1[(j++)] = ((byte)i12);
              localFixups.addU2(j, localMemberEntry2);
              arrayOfByte1[(j + 0)] = (arrayOfByte1[(j + 1)] = 0);
              j += 2;
              if ((!$assertionsDisabled) && (Instruction.opLength(i12) != j - i3)) {
                throw new AssertionError();
              }
            }
            else
            {
              if (Instruction.isBranchOp(i2))
              {
                arrayOfByte1[(j++)] = ((byte)i2);
                assert (i4 == 0);
                i5 = i3 + Instruction.opLength(i2);
                arrayOfInt2[(m++)] = i3;
                while (j < i5) {
                  arrayOfByte1[(j++)] = 0;
                }
              }
              if (Instruction.isCPRefOp(i2))
              {
                BandStructure.CPRefBand localCPRefBand1 = getCPRefOpBand(i2);
                Object localObject2 = localCPRefBand1.getRef();
                if (localObject2 == null) {
                  if (localCPRefBand1 == bc_classref) {
                    localObject2 = localClassEntry1;
                  } else if (!$assertionsDisabled) {
                    throw new AssertionError();
                  }
                }
                i11 = i2;
                i12 = 2;
                switch (i2)
                {
                case 243: 
                  i11 = 184;
                  break;
                case 242: 
                  i11 = 183;
                  break;
                case 18: 
                case 233: 
                case 234: 
                case 235: 
                case 240: 
                  i11 = 18;
                  i12 = 1;
                  ((Set)localObject1).add(localObject2);
                  break;
                case 19: 
                case 236: 
                case 237: 
                case 238: 
                case 241: 
                  i11 = 19;
                  break;
                case 20: 
                case 239: 
                  i11 = 20;
                  break;
                case 187: 
                  localClassEntry3 = (ConstantPool.ClassEntry)localObject2;
                }
                arrayOfByte1[(j++)] = ((byte)i11);
                switch (i12)
                {
                case 1: 
                  localFixups.addU1(j, (ConstantPool.Entry)localObject2);
                  break;
                case 2: 
                  localFixups.addU2(j, (ConstantPool.Entry)localObject2);
                  break;
                default: 
                  if (!$assertionsDisabled) {
                    throw new AssertionError();
                  }
                  bool = false;
                }
                arrayOfByte1[(j + 0)] = (arrayOfByte1[(j + 1)] = 0);
                j += i12;
                int i13;
                if (i11 == 197)
                {
                  i13 = bc_byte.getByte();
                  arrayOfByte1[(j++)] = ((byte)i13);
                }
                else if (i11 == 185)
                {
                  i13 = descRef.typeRef.computeSize(true);
                  arrayOfByte1[(j++)] = ((byte)(1 + i13));
                  arrayOfByte1[(j++)] = 0;
                }
                else if (i11 == 186)
                {
                  arrayOfByte1[(j++)] = 0;
                  arrayOfByte1[(j++)] = 0;
                }
                if ((!$assertionsDisabled) && (Instruction.opLength(i11) != j - i3)) {
                  throw new AssertionError();
                }
              }
              else if (Instruction.isLocalSlotOp(i2))
              {
                arrayOfByte1[(j++)] = ((byte)i2);
                i6 = bc_local.getInt();
                if (i4 != 0)
                {
                  Instruction.setShort(arrayOfByte1, j, i6);
                  j += 2;
                  if (i2 == 132)
                  {
                    i9 = bc_short.getInt();
                    Instruction.setShort(arrayOfByte1, j, i9);
                    j += 2;
                  }
                }
                else
                {
                  Instruction.setByte(arrayOfByte1, j, i6);
                  j++;
                  if (i2 == 132)
                  {
                    i9 = bc_byte.getByte();
                    Instruction.setByte(arrayOfByte1, j, i9);
                    j++;
                  }
                }
                if ((!$assertionsDisabled) && (Instruction.opLength(i2) != j - i3)) {
                  throw new AssertionError();
                }
              }
              else
              {
                if (i2 >= 202) {
                  Utils.log.warning("unrecognized bytescode " + i2 + " " + Instruction.byteName(i2));
                }
                assert (i2 < 202);
                arrayOfByte1[(j++)] = ((byte)i2);
                assert (Instruction.opLength(i2) == j - i3);
              }
            }
          }
          break;
        }
      }
      localCode.setBytes(realloc(arrayOfByte1, j));
      localCode.setInstructionMap(arrayOfInt1, k);
      Instruction localInstruction = null;
      for (int i2 = 0; i2 < m; i2++)
      {
        i3 = arrayOfInt2[i2];
        localInstruction = Instruction.at(bytes, i3, localInstruction);
        if ((localInstruction instanceof Instruction.Switch))
        {
          Instruction.Switch localSwitch1 = (Instruction.Switch)localInstruction;
          localSwitch1.setDefaultLabel(getLabel(bc_label, localCode, i3));
          i6 = localSwitch1.getCaseCount();
          for (i9 = 0; i9 < i6; i9++) {
            localSwitch1.setCaseLabel(i9, getLabel(bc_label, localCode, i3));
          }
        }
        else
        {
          localInstruction.setBranchLabel(getLabel(bc_label, localCode, i3));
        }
      }
      if (localFixups.size() > 0)
      {
        if (verbose > 2) {
          Utils.log.fine("Fixups in code: " + localFixups);
        }
        localCode.addFixups(localFixups);
      }
    }
  }
  
  static class LimitedBuffer
    extends BufferedInputStream
  {
    long served;
    int servedPos = pos;
    long limit;
    long buffered;
    
    public boolean atLimit()
    {
      boolean bool = getBytesServed() == limit;
      assert ((!bool) || (limit == buffered));
      return bool;
    }
    
    public long getBytesServed()
    {
      return served + (pos - servedPos);
    }
    
    public void setReadLimit(long paramLong)
    {
      if (paramLong == -1L) {
        limit = -1L;
      } else {
        limit = (getBytesServed() + paramLong);
      }
    }
    
    public long getReadLimit()
    {
      if (limit == -1L) {
        return limit;
      }
      return limit - getBytesServed();
    }
    
    public int read()
      throws IOException
    {
      if (pos < count) {
        return buf[(pos++)] & 0xFF;
      }
      served += pos - servedPos;
      int i = super.read();
      servedPos = pos;
      if (i >= 0) {
        served += 1L;
      }
      assert ((served <= limit) || (limit == -1L));
      return i;
    }
    
    public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      throws IOException
    {
      served += pos - servedPos;
      int i = super.read(paramArrayOfByte, paramInt1, paramInt2);
      servedPos = pos;
      if (i >= 0) {
        served += i;
      }
      return i;
    }
    
    public long skip(long paramLong)
      throws IOException
    {
      throw new RuntimeException("no skipping");
    }
    
    LimitedBuffer(InputStream paramInputStream)
    {
      super(16384);
      in = new FilterInputStream(paramInputStream)
      {
        public int read()
          throws IOException
        {
          if (buffered == limit) {
            return -1;
          }
          buffered += 1L;
          return super.read();
        }
        
        public int read(byte[] paramAnonymousArrayOfByte, int paramAnonymousInt1, int paramAnonymousInt2)
          throws IOException
        {
          if (buffered == limit) {
            return -1;
          }
          if (limit != -1L)
          {
            long l = limit - buffered;
            if (paramAnonymousInt2 > l) {
              paramAnonymousInt2 = (int)l;
            }
          }
          int i = super.read(paramAnonymousArrayOfByte, paramAnonymousInt1, paramAnonymousInt2);
          if (i >= 0) {
            buffered += i;
          }
          return i;
        }
      };
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\java\util\jar\pack\PackageReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
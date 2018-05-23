package com.sun.java.util.jar.pack;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

class PackageWriter
  extends BandStructure
{
  Package pkg;
  OutputStream finalOut;
  Package.Version packageVersion;
  Set<ConstantPool.Entry> requiredEntries;
  Map<Attribute.Layout, int[]> backCountTable;
  int[][] attrCounts;
  int[] maxFlags;
  List<Map<Attribute.Layout, int[]>> allLayouts;
  Attribute.Layout[] attrDefsWritten;
  private Code curCode;
  private Package.Class curClass;
  private ConstantPool.Entry[] curCPMap;
  int[] codeHist = new int['Ā'];
  int[] ldcHist = new int[20];
  
  PackageWriter(Package paramPackage, OutputStream paramOutputStream)
    throws IOException
  {
    pkg = paramPackage;
    finalOut = paramOutputStream;
    initHighestClassVersion(paramPackage.getHighestClassVersion());
  }
  
  void write()
    throws IOException
  {
    int i = 0;
    try
    {
      if (verbose > 0) {
        Utils.log.info("Setting up constant pool...");
      }
      setup();
      if (verbose > 0) {
        Utils.log.info("Packing...");
      }
      writeConstantPool();
      writeFiles();
      writeAttrDefs();
      writeInnerClasses();
      writeClassesAndByteCodes();
      writeAttrCounts();
      if (verbose > 1) {
        printCodeHist();
      }
      if (verbose > 0) {
        Utils.log.info("Coding...");
      }
      all_bands.chooseBandCodings();
      writeFileHeader();
      writeAllBandsTo(finalOut);
      i = 1;
    }
    catch (Exception localException)
    {
      Utils.log.warning("Error on output: " + localException, localException);
      if (verbose > 0) {
        finalOut.close();
      }
      if ((localException instanceof IOException)) {
        throw ((IOException)localException);
      }
      if ((localException instanceof RuntimeException)) {
        throw ((RuntimeException)localException);
      }
      throw new Error("error packing", localException);
    }
  }
  
  void setup()
  {
    requiredEntries = new HashSet();
    setArchiveOptions();
    trimClassAttributes();
    collectAttributeLayouts();
    pkg.buildGlobalConstantPool(requiredEntries);
    setBandIndexes();
    makeNewAttributeBands();
    collectInnerClasses();
  }
  
  void chooseDefaultPackageVersion()
    throws IOException
  {
    if (pkg.packageVersion != null)
    {
      packageVersion = pkg.packageVersion;
      if (verbose > 0) {
        Utils.log.info("package version overridden with: " + packageVersion);
      }
      return;
    }
    Package.Version localVersion = getHighestClassVersion();
    if (localVersion.lessThan(Constants.JAVA6_MAX_CLASS_VERSION)) {
      packageVersion = Constants.JAVA5_PACKAGE_VERSION;
    } else if ((localVersion.equals(Constants.JAVA6_MAX_CLASS_VERSION)) || ((localVersion.equals(Constants.JAVA7_MAX_CLASS_VERSION)) && (!pkg.cp.haveExtraTags()))) {
      packageVersion = Constants.JAVA6_PACKAGE_VERSION;
    } else if (localVersion.equals(Constants.JAVA7_MAX_CLASS_VERSION)) {
      packageVersion = Constants.JAVA7_PACKAGE_VERSION;
    } else {
      packageVersion = Constants.JAVA8_PACKAGE_VERSION;
    }
    if (verbose > 0) {
      Utils.log.info("Highest version class file: " + localVersion + " package version: " + packageVersion);
    }
  }
  
  void checkVersion()
    throws IOException
  {
    assert (packageVersion != null);
    if ((packageVersion.lessThan(Constants.JAVA7_PACKAGE_VERSION)) && (testBit(archiveOptions, 8))) {
      throw new IOException("Format bits for Java 7 must be zero in previous releases");
    }
    if (testBit(archiveOptions, 57344)) {
      throw new IOException("High archive option bits are reserved and must be zero: " + Integer.toHexString(archiveOptions));
    }
  }
  
  void setArchiveOptions()
  {
    int i = pkg.default_modtime;
    int j = pkg.default_modtime;
    int k = -1;
    int m = 0;
    archiveOptions |= pkg.default_options;
    Object localObject1 = pkg.files.iterator();
    while (((Iterator)localObject1).hasNext())
    {
      Package.File localFile = (Package.File)((Iterator)localObject1).next();
      int i1 = modtime;
      int i2 = options;
      if (i == 0)
      {
        i = j = i1;
      }
      else
      {
        if (i > i1) {
          i = i1;
        }
        if (j < i1) {
          j = i1;
        }
      }
      k &= i2;
      m |= i2;
    }
    if (pkg.default_modtime == 0) {
      pkg.default_modtime = i;
    }
    if ((i != 0) && (i != j)) {
      archiveOptions |= 0x40;
    }
    if ((!testBit(archiveOptions, 32)) && (k != -1))
    {
      if (testBit(k, 1))
      {
        archiveOptions |= 0x20;
        k--;
        m--;
      }
      pkg.default_options |= k;
      if ((k != m) || (k != pkg.default_options)) {
        archiveOptions |= 0x80;
      }
    }
    localObject1 = new HashMap();
    int n = 0;
    Object localObject2 = null;
    Iterator localIterator1 = pkg.classes.iterator();
    Object localObject3;
    Object localObject4;
    Object localObject5;
    while (localIterator1.hasNext())
    {
      localObject3 = (Package.Class)localIterator1.next();
      localObject4 = ((Package.Class)localObject3).getVersion();
      localObject5 = (int[])((Map)localObject1).get(localObject4);
      if (localObject5 == null)
      {
        localObject5 = new int[1];
        ((Map)localObject1).put(localObject4, localObject5);
      }
      int i5 = localObject5[0] += 1;
      if (n < i5)
      {
        n = i5;
        localObject2 = localObject4;
      }
    }
    ((Map)localObject1).clear();
    if (localObject2 == null) {
      localObject2 = Constants.JAVA_MIN_CLASS_VERSION;
    }
    pkg.defaultClassVersion = ((Package.Version)localObject2);
    if (verbose > 0) {
      Utils.log.info("Consensus version number in segment is " + localObject2);
    }
    if (verbose > 0) {
      Utils.log.info("Highest version number in segment is " + pkg.getHighestClassVersion());
    }
    localIterator1 = pkg.classes.iterator();
    while (localIterator1.hasNext())
    {
      localObject3 = (Package.Class)localIterator1.next();
      if (!((Package.Class)localObject3).getVersion().equals(localObject2))
      {
        localObject4 = makeClassFileVersionAttr(((Package.Class)localObject3).getVersion());
        if (verbose > 1) {
          Utils.log.fine("Version " + ((Package.Class)localObject3).getVersion() + " of " + localObject3 + " doesn't match package version " + localObject2);
        }
        ((Package.Class)localObject3).addAttribute((Attribute)localObject4);
      }
    }
    localIterator1 = pkg.files.iterator();
    while (localIterator1.hasNext())
    {
      localObject3 = (Package.File)localIterator1.next();
      long l = ((Package.File)localObject3).getFileLength();
      if (l != (int)l)
      {
        archiveOptions |= 0x100;
        if (verbose <= 0) {
          break;
        }
        Utils.log.info("Note: Huge resource file " + ((Package.File)localObject3).getFileName() + " forces 64-bit sizing");
        break;
      }
    }
    int i3 = 0;
    int i4 = 0;
    Iterator localIterator2 = pkg.classes.iterator();
    while (localIterator2.hasNext())
    {
      localObject5 = (Package.Class)localIterator2.next();
      Iterator localIterator3 = ((Package.Class)localObject5).getMethods().iterator();
      while (localIterator3.hasNext())
      {
        Package.Class.Method localMethod = (Package.Class.Method)localIterator3.next();
        if (code != null) {
          if (code.attributeSize() == 0) {
            i4++;
          } else if (shortCodeHeader(code) != 0) {
            i3 += 3;
          }
        }
      }
    }
    if (i3 > i4) {
      archiveOptions |= 0x4;
    }
    if (verbose > 0) {
      Utils.log.info("archiveOptions = 0b" + Integer.toBinaryString(archiveOptions));
    }
  }
  
  void writeFileHeader()
    throws IOException
  {
    chooseDefaultPackageVersion();
    writeArchiveMagic();
    writeArchiveHeader();
  }
  
  private void putMagicInt32(int paramInt)
    throws IOException
  {
    int i = paramInt;
    for (int j = 0; j < 4; j++)
    {
      archive_magic.putByte(0xFF & i >>> 24);
      i <<= 8;
    }
  }
  
  void writeArchiveMagic()
    throws IOException
  {
    pkg.getClass();
    putMagicInt32(-889270259);
  }
  
  void writeArchiveHeader()
    throws IOException
  {
    int i = 15;
    boolean bool1 = testBit(archiveOptions, 1);
    if (!bool1)
    {
      bool1 |= band_headers.length() != 0;
      bool1 |= attrDefsWritten.length != 0;
      if (bool1) {
        archiveOptions |= 0x1;
      }
    }
    if (bool1) {
      i += 2;
    }
    boolean bool2 = testBit(archiveOptions, 16);
    if (!bool2)
    {
      bool2 |= archiveNextCount > 0;
      bool2 |= pkg.default_modtime != 0;
      if (bool2) {
        archiveOptions |= 0x10;
      }
    }
    if (bool2) {
      i += 5;
    }
    boolean bool3 = testBit(archiveOptions, 2);
    if (!bool3)
    {
      bool3 |= pkg.cp.haveNumbers();
      if (bool3) {
        archiveOptions |= 0x2;
      }
    }
    if (bool3) {
      i += 4;
    }
    boolean bool4 = testBit(archiveOptions, 8);
    if (!bool4)
    {
      bool4 |= pkg.cp.haveExtraTags();
      if (bool4) {
        archiveOptions |= 0x8;
      }
    }
    if (bool4) {
      i += 4;
    }
    checkVersion();
    archive_header_0.putInt(packageVersion.minor);
    archive_header_0.putInt(packageVersion.major);
    if (verbose > 0) {
      Utils.log.info("Package Version for this segment:" + packageVersion);
    }
    archive_header_0.putInt(archiveOptions);
    assert (archive_header_0.length() == 3);
    if (bool2)
    {
      assert (archive_header_S.length() == 0);
      archive_header_S.putInt(0);
      assert (archive_header_S.length() == 1);
      archive_header_S.putInt(0);
      assert (archive_header_S.length() == 2);
    }
    if (bool2)
    {
      archive_header_1.putInt(archiveNextCount);
      archive_header_1.putInt(pkg.default_modtime);
      archive_header_1.putInt(pkg.files.size());
    }
    else
    {
      assert (pkg.files.isEmpty());
    }
    if (bool1)
    {
      archive_header_1.putInt(band_headers.length());
      archive_header_1.putInt(attrDefsWritten.length);
    }
    else
    {
      assert (band_headers.length() == 0);
      assert (attrDefsWritten.length == 0);
    }
    writeConstantPoolCounts(bool3, bool4);
    archive_header_1.putInt(pkg.getAllInnerClasses().size());
    archive_header_1.putInt(pkg.defaultClassVersion.minor);
    archive_header_1.putInt(pkg.defaultClassVersion.major);
    archive_header_1.putInt(pkg.classes.size());
    assert (archive_header_0.length() + archive_header_S.length() + archive_header_1.length() == i);
    archiveSize0 = 0L;
    archiveSize1 = all_bands.outputSize();
    archiveSize0 += archive_magic.outputSize();
    archiveSize0 += archive_header_0.outputSize();
    archiveSize0 += archive_header_S.outputSize();
    archiveSize1 -= archiveSize0;
    if (bool2)
    {
      int j = (int)(archiveSize1 >>> 32);
      int k = (int)(archiveSize1 >>> 0);
      archive_header_S.patchValue(0, j);
      archive_header_S.patchValue(1, k);
      int m = UNSIGNED5.getLength(0);
      archiveSize0 += UNSIGNED5.getLength(j) - m;
      archiveSize0 += UNSIGNED5.getLength(k) - m;
    }
    if (verbose > 1) {
      Utils.log.fine("archive sizes: " + archiveSize0 + "+" + archiveSize1);
    }
    assert (all_bands.outputSize() == archiveSize0 + archiveSize1);
  }
  
  void writeConstantPoolCounts(boolean paramBoolean1, boolean paramBoolean2)
    throws IOException
  {
    for (byte b : ConstantPool.TAGS_IN_ORDER)
    {
      int k = pkg.cp.getIndexByTag(b).size();
      switch (b)
      {
      case 1: 
        if ((k > 0) && (!$assertionsDisabled) && (pkg.cp.getIndexByTag(b).get(0) != ConstantPool.getUtf8Entry(""))) {
          throw new AssertionError();
        }
        break;
      case 3: 
      case 4: 
      case 5: 
      case 6: 
        if (!paramBoolean1)
        {
          if (($assertionsDisabled) || (k == 0)) {
            continue;
          }
          throw new AssertionError();
        }
        break;
      case 15: 
      case 16: 
      case 17: 
      case 18: 
        if (!paramBoolean2)
        {
          if (($assertionsDisabled) || (k == 0)) {
            continue;
          }
          throw new AssertionError();
        }
        break;
      }
      archive_header_1.putInt(k);
    }
  }
  
  protected ConstantPool.Index getCPIndex(byte paramByte)
  {
    return pkg.cp.getIndexByTag(paramByte);
  }
  
  void writeConstantPool()
    throws IOException
  {
    ConstantPool.IndexGroup localIndexGroup = pkg.cp;
    if (verbose > 0) {
      Utils.log.info("Writing CP");
    }
    Object localObject1;
    for (byte b2 : ConstantPool.TAGS_IN_ORDER)
    {
      localObject1 = localIndexGroup.getIndexByTag(b2);
      ConstantPool.Entry[] arrayOfEntry2 = cpMap;
      if (verbose > 0) {
        Utils.log.info("Writing " + arrayOfEntry2.length + " " + ConstantPool.tagName(b2) + " entries...");
      }
      Object localObject2;
      if (optDumpBands)
      {
        PrintStream localPrintStream2 = new PrintStream(getDumpStream((ConstantPool.Index)localObject1, ".idx"));
        localObject2 = null;
        try
        {
          printArrayTo(localPrintStream2, arrayOfEntry2, 0, arrayOfEntry2.length);
        }
        catch (Throwable localThrowable4)
        {
          localObject2 = localThrowable4;
          throw localThrowable4;
        }
        finally
        {
          if (localPrintStream2 != null) {
            if (localObject2 != null) {
              try
              {
                localPrintStream2.close();
              }
              catch (Throwable localThrowable5)
              {
                ((Throwable)localObject2).addSuppressed(localThrowable5);
              }
            } else {
              localPrintStream2.close();
            }
          }
        }
      }
      int k;
      int n;
      switch (b2)
      {
      case 1: 
        writeUtf8Bands(arrayOfEntry2);
        break;
      case 3: 
        for (k = 0; k < arrayOfEntry2.length; k++)
        {
          localObject2 = (ConstantPool.NumberEntry)arrayOfEntry2[k];
          int m = ((Integer)((ConstantPool.NumberEntry)localObject2).numberValue()).intValue();
          cp_Int.putInt(m);
        }
        break;
      case 4: 
        for (k = 0; k < arrayOfEntry2.length; k++)
        {
          localObject2 = (ConstantPool.NumberEntry)arrayOfEntry2[k];
          float f = ((Float)((ConstantPool.NumberEntry)localObject2).numberValue()).floatValue();
          n = Float.floatToIntBits(f);
          cp_Float.putInt(n);
        }
        break;
      case 5: 
        for (k = 0; k < arrayOfEntry2.length; k++)
        {
          localObject2 = (ConstantPool.NumberEntry)arrayOfEntry2[k];
          long l1 = ((Long)((ConstantPool.NumberEntry)localObject2).numberValue()).longValue();
          cp_Long_hi.putInt((int)(l1 >>> 32));
          cp_Long_lo.putInt((int)(l1 >>> 0));
        }
        break;
      case 6: 
        for (k = 0; k < arrayOfEntry2.length; k++)
        {
          localObject2 = (ConstantPool.NumberEntry)arrayOfEntry2[k];
          double d = ((Double)((ConstantPool.NumberEntry)localObject2).numberValue()).doubleValue();
          long l2 = Double.doubleToLongBits(d);
          cp_Double_hi.putInt((int)(l2 >>> 32));
          cp_Double_lo.putInt((int)(l2 >>> 0));
        }
        break;
      case 8: 
        for (k = 0; k < arrayOfEntry2.length; k++)
        {
          localObject2 = (ConstantPool.StringEntry)arrayOfEntry2[k];
          cp_String.putRef(ref);
        }
        break;
      case 7: 
        for (k = 0; k < arrayOfEntry2.length; k++)
        {
          localObject2 = (ConstantPool.ClassEntry)arrayOfEntry2[k];
          cp_Class.putRef(ref);
        }
        break;
      case 13: 
        writeSignatureBands(arrayOfEntry2);
        break;
      case 12: 
        for (k = 0; k < arrayOfEntry2.length; k++)
        {
          localObject2 = (ConstantPool.DescriptorEntry)arrayOfEntry2[k];
          cp_Descr_name.putRef(nameRef);
          cp_Descr_type.putRef(typeRef);
        }
        break;
      case 9: 
        writeMemberRefs(b2, arrayOfEntry2, cp_Field_class, cp_Field_desc);
        break;
      case 10: 
        writeMemberRefs(b2, arrayOfEntry2, cp_Method_class, cp_Method_desc);
        break;
      case 11: 
        writeMemberRefs(b2, arrayOfEntry2, cp_Imethod_class, cp_Imethod_desc);
        break;
      case 15: 
        for (k = 0; k < arrayOfEntry2.length; k++)
        {
          localObject2 = (ConstantPool.MethodHandleEntry)arrayOfEntry2[k];
          cp_MethodHandle_refkind.putInt(refKind);
          cp_MethodHandle_member.putRef(memRef);
        }
        break;
      case 16: 
        for (k = 0; k < arrayOfEntry2.length; k++)
        {
          localObject2 = (ConstantPool.MethodTypeEntry)arrayOfEntry2[k];
          cp_MethodType.putRef(typeRef);
        }
        break;
      case 18: 
        for (k = 0; k < arrayOfEntry2.length; k++)
        {
          localObject2 = (ConstantPool.InvokeDynamicEntry)arrayOfEntry2[k];
          cp_InvokeDynamic_spec.putRef(bssRef);
          cp_InvokeDynamic_desc.putRef(descRef);
        }
        break;
      case 17: 
        for (k = 0; k < arrayOfEntry2.length; k++)
        {
          localObject2 = (ConstantPool.BootstrapMethodEntry)arrayOfEntry2[k];
          cp_BootstrapMethod_ref.putRef(bsmRef);
          cp_BootstrapMethod_arg_count.putInt(argRefs.length);
          for (ConstantPool.Entry localEntry : argRefs) {
            cp_BootstrapMethod_arg.putRef(localEntry);
          }
        }
        break;
      case 2: 
      case 14: 
      default: 
        throw new AssertionError("unexpected CP tag in package");
      }
    }
    if ((optDumpBands) || (verbose > 1)) {
      for (byte b1 = 50; b1 < 54; b1 = (byte)(b1 + 1))
      {
        ConstantPool.Index localIndex = localIndexGroup.getIndexByTag(b1);
        if ((localIndex != null) && (!localIndex.isEmpty()))
        {
          ConstantPool.Entry[] arrayOfEntry1 = cpMap;
          if (verbose > 1) {
            Utils.log.info("Index group " + ConstantPool.tagName(b1) + " contains " + arrayOfEntry1.length + " entries.");
          }
          if (optDumpBands)
          {
            PrintStream localPrintStream1 = new PrintStream(getDumpStream(debugName, b1, ".gidx", localIndex));
            localObject1 = null;
            try
            {
              printArrayTo(localPrintStream1, arrayOfEntry1, 0, arrayOfEntry1.length, true);
            }
            catch (Throwable localThrowable2)
            {
              localObject1 = localThrowable2;
              throw localThrowable2;
            }
            finally
            {
              if (localPrintStream1 != null) {
                if (localObject1 != null) {
                  try
                  {
                    localPrintStream1.close();
                  }
                  catch (Throwable localThrowable6)
                  {
                    ((Throwable)localObject1).addSuppressed(localThrowable6);
                  }
                } else {
                  localPrintStream1.close();
                }
              }
            }
          }
        }
      }
    }
  }
  
  void writeUtf8Bands(ConstantPool.Entry[] paramArrayOfEntry)
    throws IOException
  {
    if (paramArrayOfEntry.length == 0) {
      return;
    }
    assert (paramArrayOfEntry[0].stringValue().equals(""));
    char[][] arrayOfChar = new char[paramArrayOfEntry.length][];
    for (int i = 0; i < arrayOfChar.length; i++) {
      arrayOfChar[i] = paramArrayOfEntry[i].stringValue().toCharArray();
    }
    int[] arrayOfInt = new int[paramArrayOfEntry.length];
    Object localObject = new char[0];
    int i1;
    for (int j = 0; j < arrayOfChar.length; j++)
    {
      int k = 0;
      char[] arrayOfChar2 = arrayOfChar[j];
      i1 = Math.min(arrayOfChar2.length, localObject.length);
      while ((k < i1) && (arrayOfChar2[k] == localObject[k])) {
        k++;
      }
      arrayOfInt[j] = k;
      if (j >= 2) {
        cp_Utf8_prefix.putInt(k);
      } else {
        assert (k == 0);
      }
      localObject = arrayOfChar2;
    }
    int n;
    for (j = 0; j < arrayOfChar.length; j++)
    {
      char[] arrayOfChar1 = arrayOfChar[j];
      n = arrayOfInt[j];
      i1 = arrayOfChar1.length - arrayOfInt[j];
      boolean bool = false;
      int i2;
      int i3;
      if (i1 == 0)
      {
        bool = j >= 1;
      }
      else if ((optBigStrings) && (effort > 1) && (i1 > 100))
      {
        i2 = 0;
        for (i3 = 0; i3 < i1; i3++) {
          if (arrayOfChar1[(n + i3)] > '') {
            i2++;
          }
        }
        if (i2 > 100) {
          bool = tryAlternateEncoding(j, i2, arrayOfChar1, n);
        }
      }
      if (j < 1)
      {
        assert (!bool);
        if ((!$assertionsDisabled) && (i1 != 0)) {
          throw new AssertionError();
        }
      }
      else if (bool)
      {
        cp_Utf8_suffix.putInt(0);
        cp_Utf8_big_suffix.putInt(i1);
      }
      else
      {
        assert (i1 != 0);
        cp_Utf8_suffix.putInt(i1);
        for (i2 = 0; i2 < i1; i2++)
        {
          i3 = arrayOfChar1[(n + i2)];
          cp_Utf8_chars.putInt(i3);
        }
      }
    }
    if (verbose > 0)
    {
      j = cp_Utf8_chars.length();
      int m = cp_Utf8_big_chars.length();
      n = j + m;
      Utils.log.info("Utf8string #CHARS=" + n + " #PACKEDCHARS=" + m);
    }
  }
  
  private boolean tryAlternateEncoding(int paramInt1, int paramInt2, char[] paramArrayOfChar, int paramInt3)
  {
    int i = paramArrayOfChar.length - paramInt3;
    int[] arrayOfInt1 = new int[i];
    for (int j = 0; j < i; j++) {
      arrayOfInt1[j] = paramArrayOfChar[(paramInt3 + j)];
    }
    CodingChooser localCodingChooser = getCodingChooser();
    Coding localCoding1 = cp_Utf8_big_chars.regularCoding;
    String str = "(Utf8_big_" + paramInt1 + ")";
    int[] arrayOfInt2 = { 0, 0 };
    if ((verbose > 1) || (verbose > 1)) {
      Utils.log.fine("--- chooseCoding " + str);
    }
    CodingMethod localCodingMethod = localCodingChooser.choose(arrayOfInt1, localCoding1, arrayOfInt2);
    Coding localCoding2 = cp_Utf8_chars.regularCoding;
    if (verbose > 1) {
      Utils.log.fine("big string[" + paramInt1 + "] len=" + i + " #wide=" + paramInt2 + " size=" + arrayOfInt2[0] + "/z=" + arrayOfInt2[1] + " coding " + localCodingMethod);
    }
    if (localCodingMethod != localCoding2)
    {
      int k = arrayOfInt2[1];
      int[] arrayOfInt3 = localCodingChooser.computeSize(localCoding2, arrayOfInt1);
      int m = arrayOfInt3[1];
      int n = Math.max(5, m / 1000);
      if (verbose > 1) {
        Utils.log.fine("big string[" + paramInt1 + "] normalSize=" + arrayOfInt3[0] + "/z=" + arrayOfInt3[1] + " win=" + (k < m - n));
      }
      if (k < m - n)
      {
        BandStructure.IntBand localIntBand = cp_Utf8_big_chars.newIntBand(str);
        localIntBand.initializeValues(arrayOfInt1);
        return true;
      }
    }
    return false;
  }
  
  void writeSignatureBands(ConstantPool.Entry[] paramArrayOfEntry)
    throws IOException
  {
    for (int i = 0; i < paramArrayOfEntry.length; i++)
    {
      ConstantPool.SignatureEntry localSignatureEntry = (ConstantPool.SignatureEntry)paramArrayOfEntry[i];
      cp_Signature_form.putRef(formRef);
      for (int j = 0; j < classRefs.length; j++) {
        cp_Signature_classes.putRef(classRefs[j]);
      }
    }
  }
  
  void writeMemberRefs(byte paramByte, ConstantPool.Entry[] paramArrayOfEntry, BandStructure.CPRefBand paramCPRefBand1, BandStructure.CPRefBand paramCPRefBand2)
    throws IOException
  {
    for (int i = 0; i < paramArrayOfEntry.length; i++)
    {
      ConstantPool.MemberEntry localMemberEntry = (ConstantPool.MemberEntry)paramArrayOfEntry[i];
      paramCPRefBand1.putRef(classRef);
      paramCPRefBand2.putRef(descRef);
    }
  }
  
  void writeFiles()
    throws IOException
  {
    int i = pkg.files.size();
    if (i == 0) {
      return;
    }
    int j = archiveOptions;
    boolean bool1 = testBit(j, 256);
    boolean bool2 = testBit(j, 64);
    boolean bool3 = testBit(j, 128);
    Package.File localFile;
    if (!bool3)
    {
      localIterator = pkg.files.iterator();
      while (localIterator.hasNext())
      {
        localFile = (Package.File)localIterator.next();
        if (localFile.isClassStub())
        {
          bool3 = true;
          j |= 0x80;
          archiveOptions = j;
          break;
        }
      }
    }
    if ((bool1) || (bool2) || (bool3) || (!pkg.files.isEmpty()))
    {
      j |= 0x10;
      archiveOptions = j;
    }
    Iterator localIterator = pkg.files.iterator();
    while (localIterator.hasNext())
    {
      localFile = (Package.File)localIterator.next();
      file_name.putRef(name);
      long l = localFile.getFileLength();
      file_size_lo.putInt((int)l);
      if (bool1) {
        file_size_hi.putInt((int)(l >>> 32));
      }
      if (bool2) {
        file_modtime.putInt(modtime - pkg.default_modtime);
      }
      if (bool3) {
        file_options.putInt(options);
      }
      localFile.writeTo(file_bits.collectorStream());
      if (verbose > 1) {
        Utils.log.fine("Wrote " + l + " bytes of " + name.stringValue());
      }
    }
    if (verbose > 0) {
      Utils.log.info("Wrote " + i + " resource files");
    }
  }
  
  void collectAttributeLayouts()
  {
    maxFlags = new int[4];
    allLayouts = new FixedList(4);
    for (int i = 0; i < 4; i++) {
      allLayouts.set(i, new HashMap());
    }
    Iterator localIterator1 = pkg.classes.iterator();
    while (localIterator1.hasNext())
    {
      Package.Class localClass = (Package.Class)localIterator1.next();
      visitAttributeLayoutsIn(0, localClass);
      Iterator localIterator2 = localClass.getFields().iterator();
      Object localObject;
      while (localIterator2.hasNext())
      {
        localObject = (Package.Class.Field)localIterator2.next();
        visitAttributeLayoutsIn(1, (Attribute.Holder)localObject);
      }
      localIterator2 = localClass.getMethods().iterator();
      while (localIterator2.hasNext())
      {
        localObject = (Package.Class.Method)localIterator2.next();
        visitAttributeLayoutsIn(2, (Attribute.Holder)localObject);
        if (code != null) {
          visitAttributeLayoutsIn(3, code);
        }
      }
    }
    for (int j = 0; j < 4; j++)
    {
      int k = ((Map)allLayouts.get(j)).size();
      boolean bool = haveFlagsHi(j);
      if (k >= 24)
      {
        int n = 1 << 9 + j;
        archiveOptions |= n;
        bool = true;
        if (verbose > 0) {
          Utils.log.info("Note: Many " + Attribute.contextName(j) + " attributes forces 63-bit flags");
        }
      }
      if (verbose > 1)
      {
        Utils.log.fine(Attribute.contextName(j) + ".maxFlags = 0x" + Integer.toHexString(maxFlags[j]));
        Utils.log.fine(Attribute.contextName(j) + ".#layouts = " + k);
      }
      assert (haveFlagsHi(j) == bool);
    }
    initAttrIndexLimit();
    for (j = 0; j < 4; j++) {
      assert ((attrFlagMask[j] & maxFlags[j]) == 0L);
    }
    backCountTable = new HashMap();
    attrCounts = new int[4][];
    for (j = 0; j < 4; j++)
    {
      long l = (maxFlags[j] | attrFlagMask[j]) ^ 0xFFFFFFFFFFFFFFFF;
      assert (attrIndexLimit[j] > 0);
      assert (attrIndexLimit[j] < 64);
      l &= (1L << attrIndexLimit[j]) - 1L;
      int m = 0;
      Map localMap = (Map)allLayouts.get(j);
      Map.Entry[] arrayOfEntry = new Map.Entry[localMap.size()];
      localMap.entrySet().toArray(arrayOfEntry);
      Arrays.sort(arrayOfEntry, new Comparator()
      {
        public int compare(Map.Entry<Attribute.Layout, int[]> paramAnonymousEntry1, Map.Entry<Attribute.Layout, int[]> paramAnonymousEntry2)
        {
          int i = -(((int[])paramAnonymousEntry1.getValue())[0] - ((int[])paramAnonymousEntry2.getValue())[0]);
          if (i != 0) {
            return i;
          }
          return ((Attribute.Layout)paramAnonymousEntry1.getKey()).compareTo((Attribute.Layout)paramAnonymousEntry2.getKey());
        }
      });
      attrCounts[j] = new int[attrIndexLimit[j] + arrayOfEntry.length];
      for (int i1 = 0; i1 < arrayOfEntry.length; i1++)
      {
        Map.Entry localEntry = arrayOfEntry[i1];
        Attribute.Layout localLayout = (Attribute.Layout)localEntry.getKey();
        int i2 = ((int[])localEntry.getValue())[0];
        Integer localInteger = (Integer)attrIndexTable.get(localLayout);
        int i3;
        if (localInteger != null)
        {
          i3 = localInteger.intValue();
        }
        else if (l != 0L)
        {
          while ((l & 1L) == 0L)
          {
            l >>>= 1;
            m++;
          }
          l -= 1L;
          i3 = setAttributeLayoutIndex(localLayout, m);
        }
        else
        {
          i3 = setAttributeLayoutIndex(localLayout, -1);
        }
        attrCounts[j][i3] = i2;
        Attribute.Layout.Element[] arrayOfElement = localLayout.getCallables();
        int[] arrayOfInt = new int[arrayOfElement.length];
        for (int i4 = 0; i4 < arrayOfElement.length; i4++)
        {
          assert (kind == 10);
          if (!arrayOfElement[i4].flagTest((byte)8)) {
            arrayOfInt[i4] = -1;
          }
        }
        backCountTable.put(localLayout, arrayOfInt);
        if (localInteger == null)
        {
          ConstantPool.Utf8Entry localUtf8Entry1 = ConstantPool.getUtf8Entry(localLayout.name());
          String str = localLayout.layoutForClassVersion(getHighestClassVersion());
          ConstantPool.Utf8Entry localUtf8Entry2 = ConstantPool.getUtf8Entry(str);
          requiredEntries.add(localUtf8Entry1);
          requiredEntries.add(localUtf8Entry2);
          if (verbose > 0) {
            if (i3 < attrIndexLimit[j]) {
              Utils.log.info("Using free flag bit 1<<" + i3 + " for " + i2 + " occurrences of " + localLayout);
            } else {
              Utils.log.info("Using overflow index " + i3 + " for " + i2 + " occurrences of " + localLayout);
            }
          }
        }
      }
    }
    maxFlags = null;
    allLayouts = null;
  }
  
  void visitAttributeLayoutsIn(int paramInt, Attribute.Holder paramHolder)
  {
    maxFlags[paramInt] |= flags;
    Iterator localIterator = paramHolder.getAttributes().iterator();
    while (localIterator.hasNext())
    {
      Attribute localAttribute = (Attribute)localIterator.next();
      Attribute.Layout localLayout = localAttribute.layout();
      Map localMap = (Map)allLayouts.get(paramInt);
      int[] arrayOfInt = (int[])localMap.get(localLayout);
      if (arrayOfInt == null) {
        localMap.put(localLayout, arrayOfInt = new int[1]);
      }
      if (arrayOfInt[0] < Integer.MAX_VALUE) {
        arrayOfInt[0] += 1;
      }
    }
  }
  
  void writeAttrDefs()
    throws IOException
  {
    ArrayList localArrayList = new ArrayList();
    Object localObject2;
    for (int i = 0; i < 4; i++)
    {
      int j = ((List)attrDefs.get(i)).size();
      for (int k = 0; k < j; k++)
      {
        int m = i;
        if (k < attrIndexLimit[i])
        {
          m |= k + 1 << 2;
          assert (m < 256);
          if (!testBit(attrDefSeen[i], 1L << k)) {}
        }
        else
        {
          localObject2 = (Attribute.Layout)((List)attrDefs.get(i)).get(k);
          localArrayList.add(new Object[] { Integer.valueOf(m), localObject2 });
          assert (Integer.valueOf(k).equals(attrIndexTable.get(localObject2)));
        }
      }
    }
    i = localArrayList.size();
    Object[][] arrayOfObject = new Object[i][];
    localArrayList.toArray(arrayOfObject);
    Arrays.sort(arrayOfObject, new Comparator()
    {
      public int compare(Object[] paramAnonymousArrayOfObject1, Object[] paramAnonymousArrayOfObject2)
      {
        int i = ((Comparable)paramAnonymousArrayOfObject1[0]).compareTo(paramAnonymousArrayOfObject2[0]);
        if (i != 0) {
          return i;
        }
        Integer localInteger1 = (Integer)attrIndexTable.get(paramAnonymousArrayOfObject1[1]);
        Integer localInteger2 = (Integer)attrIndexTable.get(paramAnonymousArrayOfObject2[1]);
        assert (localInteger1 != null);
        assert (localInteger2 != null);
        return localInteger1.compareTo(localInteger2);
      }
    });
    attrDefsWritten = new Attribute.Layout[i];
    PrintStream localPrintStream = !optDumpBands ? null : new PrintStream(getDumpStream(attr_definition_headers, ".def"));
    Object localObject1 = null;
    try
    {
      localObject2 = Arrays.copyOf(attrIndexLimit, 4);
      for (int n = 0; n < arrayOfObject.length; n++)
      {
        int i1 = ((Integer)arrayOfObject[n][0]).intValue();
        Attribute.Layout localLayout = (Attribute.Layout)arrayOfObject[n][1];
        attrDefsWritten[n] = localLayout;
        assert ((i1 & 0x3) == localLayout.ctype());
        attr_definition_headers.putByte(i1);
        attr_definition_name.putRef(ConstantPool.getUtf8Entry(localLayout.name()));
        String str = localLayout.layoutForClassVersion(getHighestClassVersion());
        attr_definition_layout.putRef(ConstantPool.getUtf8Entry(str));
        int i2 = 0;
        assert ((i2 = 1) != 0);
        int i3;
        if (i2 != 0)
        {
          i3 = (i1 >> 2) - 1;
          if (i3 < 0)
          {
            int tmp455_452 = localLayout.ctype();
            Object tmp455_448 = localObject2;
            int tmp457_456 = tmp455_448[tmp455_452];
            tmp455_448[tmp455_452] = (tmp457_456 + 1);
            i3 = tmp457_456;
          }
          int i4 = ((Integer)attrIndexTable.get(localLayout)).intValue();
          assert (i3 == i4);
        }
        if (localPrintStream != null)
        {
          i3 = (i1 >> 2) - 1;
          localPrintStream.println(i3 + " " + localLayout);
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
  }
  
  void writeAttrCounts()
    throws IOException
  {
    for (int i = 0; i < 4; i++)
    {
      BandStructure.MultiBand localMultiBand = attrBands[i];
      BandStructure.IntBand localIntBand = getAttrBand(localMultiBand, 4);
      Attribute.Layout[] arrayOfLayout = new Attribute.Layout[((List)attrDefs.get(i)).size()];
      ((List)attrDefs.get(i)).toArray(arrayOfLayout);
      for (int j = 1;; j = 0)
      {
        for (int k = 0; k < arrayOfLayout.length; k++)
        {
          Attribute.Layout localLayout = arrayOfLayout[k];
          if ((localLayout != null) && (j == isPredefinedAttr(i, k)))
          {
            int m = attrCounts[i][k];
            if (m != 0)
            {
              int[] arrayOfInt = (int[])backCountTable.get(localLayout);
              for (int n = 0; n < arrayOfInt.length; n++) {
                if (arrayOfInt[n] >= 0)
                {
                  int i1 = arrayOfInt[n];
                  arrayOfInt[n] = -1;
                  localIntBand.putInt(i1);
                  assert (localLayout.getCallables()[n].flagTest((byte)8));
                }
                else
                {
                  assert (!localLayout.getCallables()[n].flagTest((byte)8));
                }
              }
            }
          }
        }
        if (j == 0) {
          break;
        }
      }
    }
  }
  
  void trimClassAttributes()
  {
    Iterator localIterator = pkg.classes.iterator();
    while (localIterator.hasNext())
    {
      Package.Class localClass = (Package.Class)localIterator.next();
      localClass.minimizeSourceFile();
      assert (localClass.getAttribute(Package.attrBootstrapMethodsEmpty) == null);
    }
  }
  
  void collectInnerClasses()
  {
    HashMap localHashMap = new HashMap();
    Object localObject1 = pkg.classes.iterator();
    Object localObject3;
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (Package.Class)((Iterator)localObject1).next();
      if (((Package.Class)localObject2).hasInnerClasses())
      {
        localObject3 = ((Package.Class)localObject2).getInnerClasses().iterator();
        while (((Iterator)localObject3).hasNext())
        {
          Package.InnerClass localInnerClass1 = (Package.InnerClass)((Iterator)localObject3).next();
          Package.InnerClass localInnerClass2 = (Package.InnerClass)localHashMap.put(thisClass, localInnerClass1);
          if ((localInnerClass2 != null) && (!localInnerClass2.equals(localInnerClass1)) && (predictable)) {
            localHashMap.put(thisClass, localInnerClass2);
          }
        }
      }
    }
    localObject1 = new Package.InnerClass[localHashMap.size()];
    localHashMap.values().toArray((Object[])localObject1);
    localHashMap = null;
    Arrays.sort((Object[])localObject1);
    pkg.setAllInnerClasses(Arrays.asList((Object[])localObject1));
    Object localObject2 = pkg.classes.iterator();
    while (((Iterator)localObject2).hasNext())
    {
      localObject3 = (Package.Class)((Iterator)localObject2).next();
      ((Package.Class)localObject3).minimizeLocalICs();
    }
  }
  
  void writeInnerClasses()
    throws IOException
  {
    Iterator localIterator = pkg.getAllInnerClasses().iterator();
    while (localIterator.hasNext())
    {
      Package.InnerClass localInnerClass = (Package.InnerClass)localIterator.next();
      int i = flags;
      assert ((i & 0x10000) == 0);
      if (!predictable) {
        i |= 0x10000;
      }
      ic_this_class.putRef(thisClass);
      ic_flags.putInt(i);
      if (!predictable)
      {
        ic_outer_class.putRef(outerClass);
        ic_name.putRef(name);
      }
    }
  }
  
  void writeLocalInnerClasses(Package.Class paramClass)
    throws IOException
  {
    List localList = paramClass.getInnerClasses();
    class_InnerClasses_N.putInt(localList.size());
    Iterator localIterator = localList.iterator();
    while (localIterator.hasNext())
    {
      Package.InnerClass localInnerClass = (Package.InnerClass)localIterator.next();
      class_InnerClasses_RC.putRef(thisClass);
      if (localInnerClass.equals(pkg.getGlobalInnerClass(thisClass)))
      {
        class_InnerClasses_F.putInt(0);
      }
      else
      {
        int i = flags;
        if (i == 0) {
          i = 65536;
        }
        class_InnerClasses_F.putInt(i);
        class_InnerClasses_outer_RCN.putRef(outerClass);
        class_InnerClasses_name_RUN.putRef(name);
      }
    }
  }
  
  void writeClassesAndByteCodes()
    throws IOException
  {
    Package.Class[] arrayOfClass = new Package.Class[pkg.classes.size()];
    pkg.classes.toArray(arrayOfClass);
    if (verbose > 0) {
      Utils.log.info("  ...scanning " + arrayOfClass.length + " classes...");
    }
    int i = 0;
    for (int j = 0; j < arrayOfClass.length; j++)
    {
      Package.Class localClass = arrayOfClass[j];
      if (verbose > 1) {
        Utils.log.fine("Scanning " + localClass);
      }
      ConstantPool.ClassEntry localClassEntry1 = thisClass;
      ConstantPool.ClassEntry localClassEntry2 = superClass;
      ConstantPool.ClassEntry[] arrayOfClassEntry = interfaces;
      assert (localClassEntry2 != localClassEntry1);
      if (localClassEntry2 == null) {
        localClassEntry2 = localClassEntry1;
      }
      class_this.putRef(localClassEntry1);
      class_super.putRef(localClassEntry2);
      class_interface_count.putInt(interfaces.length);
      for (int k = 0; k < arrayOfClassEntry.length; k++) {
        class_interface.putRef(arrayOfClassEntry[k]);
      }
      writeMembers(localClass);
      writeAttrs(0, localClass, localClass);
      i++;
      if ((verbose > 0) && (i % 1000 == 0)) {
        Utils.log.info("Have scanned " + i + " classes...");
      }
    }
  }
  
  void writeMembers(Package.Class paramClass)
    throws IOException
  {
    List localList = paramClass.getFields();
    class_field_count.putInt(localList.size());
    Object localObject1 = localList.iterator();
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (Package.Class.Field)((Iterator)localObject1).next();
      field_descr.putRef(((Package.Class.Field)localObject2).getDescriptor());
      writeAttrs(1, (Attribute.Holder)localObject2, paramClass);
    }
    localObject1 = paramClass.getMethods();
    class_method_count.putInt(((List)localObject1).size());
    Object localObject2 = ((List)localObject1).iterator();
    while (((Iterator)localObject2).hasNext())
    {
      Package.Class.Method localMethod = (Package.Class.Method)((Iterator)localObject2).next();
      method_descr.putRef(localMethod.getDescriptor());
      writeAttrs(2, localMethod, paramClass);
      if (!$assertionsDisabled) {
        if ((code != null ? 1 : 0) != (localMethod.getAttribute(attrCodeEmpty) != null ? 1 : 0)) {
          throw new AssertionError();
        }
      }
      if (code != null)
      {
        writeCodeHeader(code);
        writeByteCodes(code);
      }
    }
  }
  
  void writeCodeHeader(Code paramCode)
    throws IOException
  {
    boolean bool = testBit(archiveOptions, 4);
    int i = paramCode.attributeSize();
    int j = shortCodeHeader(paramCode);
    if ((!bool) && (i > 0)) {
      j = 0;
    }
    if (verbose > 2)
    {
      int k = paramCode.getMethod().getArgumentSize();
      Utils.log.fine("Code sizes info " + max_stack + " " + max_locals + " " + paramCode.getHandlerCount() + " " + k + " " + i + (j > 0 ? " SHORT=" + j : ""));
    }
    code_headers.putByte(j);
    if (j == 0)
    {
      code_max_stack.putInt(paramCode.getMaxStack());
      code_max_na_locals.putInt(paramCode.getMaxNALocals());
      code_handler_count.putInt(paramCode.getHandlerCount());
    }
    else
    {
      assert ((bool) || (i == 0));
      assert (paramCode.getHandlerCount() < shortCodeHeader_h_limit);
    }
    writeCodeHandlers(paramCode);
    if ((j == 0) || (bool)) {
      writeAttrs(3, paramCode, paramCode.thisClass());
    }
  }
  
  void writeCodeHandlers(Code paramCode)
    throws IOException
  {
    int k = 0;
    int m = paramCode.getHandlerCount();
    while (k < m)
    {
      code_handler_class_RCN.putRef(handler_class[k]);
      int i = paramCode.encodeBCI(handler_start[k]);
      code_handler_start_P.putInt(i);
      int j = paramCode.encodeBCI(handler_end[k]) - i;
      code_handler_end_PO.putInt(j);
      i += j;
      j = paramCode.encodeBCI(handler_catch[k]) - i;
      code_handler_catch_PO.putInt(j);
      k++;
    }
  }
  
  void writeAttrs(int paramInt, final Attribute.Holder paramHolder, Package.Class paramClass)
    throws IOException
  {
    BandStructure.MultiBand localMultiBand = attrBands[paramInt];
    BandStructure.IntBand localIntBand1 = getAttrBand(localMultiBand, 0);
    BandStructure.IntBand localIntBand2 = getAttrBand(localMultiBand, 1);
    boolean bool = haveFlagsHi(paramInt);
    if (!$assertionsDisabled) {
      if (attrIndexLimit[paramInt] != (bool ? 63 : 32)) {
        throw new AssertionError();
      }
    }
    if (attributes == null)
    {
      localIntBand2.putInt(flags);
      if (bool) {
        localIntBand1.putInt(0);
      }
      return;
    }
    if (verbose > 3) {
      Utils.log.fine("Transmitting attrs for " + paramHolder + " flags=" + Integer.toHexString(flags));
    }
    long l1 = attrFlagMask[paramInt];
    long l2 = 0L;
    int i = 0;
    Object localObject1 = attributes.iterator();
    while (((Iterator)localObject1).hasNext())
    {
      Attribute localAttribute = (Attribute)((Iterator)localObject1).next();
      Attribute.Layout localLayout = localAttribute.layout();
      int j = ((Integer)attrIndexTable.get(localLayout)).intValue();
      assert (((List)attrDefs.get(paramInt)).get(j) == localLayout);
      if (verbose > 3) {
        Utils.log.fine("add attr @" + j + " " + localAttribute + " in " + paramHolder);
      }
      final Object localObject2;
      if ((j < attrIndexLimit[paramInt]) && (testBit(l1, 1L << j)))
      {
        if (verbose > 3) {
          Utils.log.fine("Adding flag bit 1<<" + j + " in " + Long.toHexString(l1));
        }
        assert (!testBit(flags, 1L << j));
        l2 |= 1L << j;
        l1 -= (1L << j);
      }
      else
      {
        l2 |= 0x10000;
        i++;
        if (verbose > 3) {
          Utils.log.fine("Adding overflow attr #" + i);
        }
        localObject2 = getAttrBand(localMultiBand, 3);
        ((BandStructure.IntBand)localObject2).putInt(j);
      }
      if (bandCount == 0)
      {
        if (localLayout == attrInnerClassesEmpty) {
          writeLocalInnerClasses((Package.Class)paramHolder);
        }
      }
      else
      {
        assert (fixups == null);
        localObject2 = (BandStructure.Band[])attrBandTable.get(localLayout);
        assert (localObject2 != null);
        assert (localObject2.length == bandCount);
        final int[] arrayOfInt = (int[])backCountTable.get(localLayout);
        assert (arrayOfInt != null);
        assert (arrayOfInt.length == localLayout.getCallables().length);
        if (verbose > 2) {
          Utils.log.fine("writing " + localAttribute + " in " + paramHolder);
        }
        int k = (paramInt == 1) && (localLayout == attrConstantValue) ? 1 : 0;
        if (k != 0) {
          setConstantValueIndex((Package.Class.Field)paramHolder);
        }
        localAttribute.parse(paramClass, localAttribute.bytes(), 0, localAttribute.size(), new Attribute.ValueStream()
        {
          public void putInt(int paramAnonymousInt1, int paramAnonymousInt2)
          {
            ((BandStructure.IntBand)localObject2[paramAnonymousInt1]).putInt(paramAnonymousInt2);
          }
          
          public void putRef(int paramAnonymousInt, ConstantPool.Entry paramAnonymousEntry)
          {
            ((BandStructure.CPRefBand)localObject2[paramAnonymousInt]).putRef(paramAnonymousEntry);
          }
          
          public int encodeBCI(int paramAnonymousInt)
          {
            Code localCode = (Code)paramHolder;
            return localCode.encodeBCI(paramAnonymousInt);
          }
          
          public void noteBackCall(int paramAnonymousInt)
          {
            assert (arrayOfInt[paramAnonymousInt] >= 0);
            arrayOfInt[paramAnonymousInt] += 1;
          }
        });
        if (k != 0) {
          setConstantValueIndex(null);
        }
      }
    }
    if (i > 0)
    {
      localObject1 = getAttrBand(localMultiBand, 2);
      ((BandStructure.IntBand)localObject1).putInt(i);
    }
    localIntBand2.putInt(flags | (int)l2);
    if (bool) {
      localIntBand1.putInt((int)(l2 >>> 32));
    } else {
      assert (l2 >>> 32 == 0L);
    }
    assert ((flags & l2) == 0L) : (paramHolder + ".flags=" + Integer.toHexString(flags) + "^" + Long.toHexString(l2));
  }
  
  private void beginCode(Code paramCode)
  {
    assert (curCode == null);
    curCode = paramCode;
    curClass = m.thisClass();
    curCPMap = paramCode.getCPMap();
  }
  
  private void endCode()
  {
    curCode = null;
    curClass = null;
    curCPMap = null;
  }
  
  private int initOpVariant(Instruction paramInstruction, ConstantPool.Entry paramEntry)
  {
    if (paramInstruction.getBC() != 183) {
      return -1;
    }
    ConstantPool.MemberEntry localMemberEntry = (ConstantPool.MemberEntry)paramInstruction.getCPRef(curCPMap);
    if (!"<init>".equals(descRef.nameRef.stringValue())) {
      return -1;
    }
    ConstantPool.ClassEntry localClassEntry = classRef;
    if (localClassEntry == curClass.thisClass) {
      return 230;
    }
    if (localClassEntry == curClass.superClass) {
      return 231;
    }
    if (localClassEntry == paramEntry) {
      return 232;
    }
    return -1;
  }
  
  private int selfOpVariant(Instruction paramInstruction)
  {
    int i = paramInstruction.getBC();
    if ((i < 178) || (i > 184)) {
      return -1;
    }
    ConstantPool.MemberEntry localMemberEntry = (ConstantPool.MemberEntry)paramInstruction.getCPRef(curCPMap);
    if (((i == 183) || (i == 184)) && (localMemberEntry.tagEquals(11))) {
      return -1;
    }
    ConstantPool.ClassEntry localClassEntry = classRef;
    int j = 202 + (i - 178);
    if (localClassEntry == curClass.thisClass) {
      return j;
    }
    if (localClassEntry == curClass.superClass) {
      return j + 14;
    }
    return -1;
  }
  
  void writeByteCodes(Code paramCode)
    throws IOException
  {
    beginCode(paramCode);
    ConstantPool.IndexGroup localIndexGroup = pkg.cp;
    int i = 0;
    Object localObject = null;
    for (Instruction localInstruction1 = paramCode.instructionAt(0); localInstruction1 != null; localInstruction1 = localInstruction1.next())
    {
      if (verbose > 3) {
        Utils.log.fine(localInstruction1.toString());
      }
      if (localInstruction1.isNonstandard())
      {
        String str1 = paramCode.getMethod() + " contains an unrecognized bytecode " + localInstruction1 + "; please use the pass-file option on this class.";
        Utils.log.warning(str1);
        throw new IOException(str1);
      }
      if (localInstruction1.isWide())
      {
        if (verbose > 1)
        {
          Utils.log.fine("_wide opcode in " + paramCode);
          Utils.log.fine(localInstruction1.toString());
        }
        bc_codes.putByte(196);
        codeHist['Ä'] += 1;
      }
      int j = localInstruction1.getBC();
      if (j == 42)
      {
        Instruction localInstruction2 = paramCode.instructionAt(localInstruction1.getNextPC());
        if (selfOpVariant(localInstruction2) >= 0)
        {
          i = 1;
          continue;
        }
      }
      int k = initOpVariant(localInstruction1, (ConstantPool.Entry)localObject);
      if (k >= 0)
      {
        if (i != 0)
        {
          bc_codes.putByte(42);
          codeHist[42] += 1;
          i = 0;
        }
        bc_codes.putByte(k);
        codeHist[k] += 1;
        ConstantPool.MemberEntry localMemberEntry1 = (ConstantPool.MemberEntry)localInstruction1.getCPRef(curCPMap);
        int n = localIndexGroup.getOverloadingIndex(localMemberEntry1);
        bc_initref.putInt(n);
      }
      else
      {
        int m = selfOpVariant(localInstruction1);
        int i2;
        int i3;
        if (m >= 0)
        {
          boolean bool = Instruction.isFieldOp(j);
          i2 = m >= 216 ? 1 : 0;
          i3 = i;
          i = 0;
          if (i3 != 0) {
            m += 7;
          }
          bc_codes.putByte(m);
          codeHist[m] += 1;
          ConstantPool.MemberEntry localMemberEntry2 = (ConstantPool.MemberEntry)localInstruction1.getCPRef(curCPMap);
          BandStructure.CPRefBand localCPRefBand2 = selfOpRefBand(m);
          ConstantPool.Index localIndex = localIndexGroup.getMemberIndex(tag, classRef);
          localCPRefBand2.putRef(localMemberEntry2, localIndex);
        }
        else
        {
          assert (i == 0);
          codeHist[j] += 1;
          int i5;
          switch (j)
          {
          case 170: 
          case 171: 
            bc_codes.putByte(j);
            Instruction.Switch localSwitch = (Instruction.Switch)localInstruction1;
            i2 = localSwitch.getAlignedPC();
            i3 = localSwitch.getNextPC();
            i5 = localSwitch.getCaseCount();
            bc_case_count.putInt(i5);
            putLabel(bc_label, paramCode, localInstruction1.getPC(), localSwitch.getDefaultLabel());
            for (int i6 = 0; i6 < i5; i6++) {
              putLabel(bc_label, paramCode, localInstruction1.getPC(), localSwitch.getCaseLabel(i6));
            }
            if (j == 170) {
              bc_case_value.putInt(localSwitch.getCaseValue(0));
            } else {
              for (i6 = 0; i6 < i5; i6++) {
                bc_case_value.putInt(localSwitch.getCaseValue(i6));
              }
            }
            break;
          default: 
            int i1 = localInstruction1.getBranchLabel();
            if (i1 >= 0)
            {
              bc_codes.putByte(j);
              putLabel(bc_label, paramCode, localInstruction1.getPC(), i1);
            }
            else
            {
              ConstantPool.Entry localEntry = localInstruction1.getCPRef(curCPMap);
              if (localEntry != null)
              {
                if (j == 187) {
                  localObject = localEntry;
                }
                if (j == 18) {
                  ldcHist[tag] += 1;
                }
                i5 = j;
                BandStructure.CPRefBand localCPRefBand1;
                switch (localInstruction1.getCPTag())
                {
                case 51: 
                  switch (tag)
                  {
                  case 3: 
                    localCPRefBand1 = bc_intref;
                    switch (j)
                    {
                    case 18: 
                      i5 = 234;
                      break;
                    case 19: 
                      i5 = 237;
                      break;
                    default: 
                      if (!$assertionsDisabled) {
                        throw new AssertionError();
                      }
                      break;
                    }
                    break;
                  case 4: 
                    localCPRefBand1 = bc_floatref;
                    switch (j)
                    {
                    case 18: 
                      i5 = 235;
                      break;
                    case 19: 
                      i5 = 238;
                      break;
                    default: 
                      if (!$assertionsDisabled) {
                        throw new AssertionError();
                      }
                      break;
                    }
                    break;
                  case 5: 
                    localCPRefBand1 = bc_longref;
                    assert (j == 20);
                    i5 = 20;
                    break;
                  case 6: 
                    localCPRefBand1 = bc_doubleref;
                    assert (j == 20);
                    i5 = 239;
                    break;
                  case 8: 
                    localCPRefBand1 = bc_stringref;
                    switch (j)
                    {
                    case 18: 
                      i5 = 18;
                      break;
                    case 19: 
                      i5 = 19;
                      break;
                    default: 
                      if (!$assertionsDisabled) {
                        throw new AssertionError();
                      }
                      break;
                    }
                    break;
                  case 7: 
                    localCPRefBand1 = bc_classref;
                    switch (j)
                    {
                    case 18: 
                      i5 = 233;
                      break;
                    case 19: 
                      i5 = 236;
                      break;
                    default: 
                      if (!$assertionsDisabled) {
                        throw new AssertionError();
                      }
                      break;
                    }
                    break;
                  default: 
                    if (getHighestClassVersion().lessThan(Constants.JAVA7_MAX_CLASS_VERSION)) {
                      throw new IOException("bad class file major version for Java 7 ldc");
                    }
                    localCPRefBand1 = bc_loadablevalueref;
                    switch (j)
                    {
                    case 18: 
                      i5 = 240;
                      break;
                    case 19: 
                      i5 = 241;
                      break;
                    default: 
                      if (!$assertionsDisabled) {
                        throw new AssertionError();
                      }
                      break;
                    }
                    break;
                  }
                  break;
                case 7: 
                  if (localEntry == curClass.thisClass) {
                    localEntry = null;
                  }
                  localCPRefBand1 = bc_classref;
                  break;
                case 9: 
                  localCPRefBand1 = bc_fieldref;
                  break;
                case 10: 
                  if (localEntry.tagEquals(11))
                  {
                    if (j == 183) {
                      i5 = 242;
                    }
                    if (j == 184) {
                      i5 = 243;
                    }
                    localCPRefBand1 = bc_imethodref;
                  }
                  else
                  {
                    localCPRefBand1 = bc_methodref;
                  }
                  break;
                case 11: 
                  localCPRefBand1 = bc_imethodref;
                  break;
                case 18: 
                  localCPRefBand1 = bc_indyref;
                  break;
                default: 
                  localCPRefBand1 = null;
                  if (!$assertionsDisabled) {
                    throw new AssertionError();
                  }
                  break;
                }
                if ((localEntry != null) && (index != null) && (!index.contains(localEntry)))
                {
                  String str2 = paramCode.getMethod() + " contains a bytecode " + localInstruction1 + " with an unsupported constant reference; please use the pass-file option on this class.";
                  Utils.log.warning(str2);
                  throw new IOException(str2);
                }
                bc_codes.putByte(i5);
                localCPRefBand1.putRef(localEntry);
                if (j == 197)
                {
                  assert (localInstruction1.getConstant() == paramCode.getByte(localInstruction1.getPC() + 3));
                  bc_byte.putByte(0xFF & localInstruction1.getConstant());
                }
                else if (j == 185)
                {
                  assert (localInstruction1.getLength() == 5);
                  if ((!$assertionsDisabled) && (localInstruction1.getConstant() != 1 + descRef.typeRef.computeSize(true) << 8)) {
                    throw new AssertionError();
                  }
                }
                else if (j == 186)
                {
                  if (getHighestClassVersion().lessThan(Constants.JAVA7_MAX_CLASS_VERSION)) {
                    throw new IOException("bad class major version for Java 7 invokedynamic");
                  }
                  assert (localInstruction1.getLength() == 5);
                  if ((!$assertionsDisabled) && (localInstruction1.getConstant() != 0)) {
                    throw new AssertionError();
                  }
                }
                else if (!$assertionsDisabled)
                {
                  if (localInstruction1.getLength() != (j == 18 ? 2 : 3)) {
                    throw new AssertionError();
                  }
                }
              }
              else
              {
                int i4 = localInstruction1.getLocalSlot();
                if (i4 >= 0)
                {
                  bc_codes.putByte(j);
                  bc_local.putInt(i4);
                  i5 = localInstruction1.getConstant();
                  if (j == 132)
                  {
                    if (!localInstruction1.isWide()) {
                      bc_byte.putByte(0xFF & i5);
                    } else {
                      bc_short.putInt(0xFFFF & i5);
                    }
                  }
                  else if ((!$assertionsDisabled) && (i5 != 0)) {
                    throw new AssertionError();
                  }
                }
                else
                {
                  bc_codes.putByte(j);
                  i5 = localInstruction1.getPC() + 1;
                  int i7 = localInstruction1.getNextPC();
                  if (i5 < i7) {
                    switch (j)
                    {
                    case 17: 
                      bc_short.putInt(0xFFFF & localInstruction1.getConstant());
                      break;
                    case 16: 
                      bc_byte.putByte(0xFF & localInstruction1.getConstant());
                      break;
                    case 188: 
                      bc_byte.putByte(0xFF & localInstruction1.getConstant());
                      break;
                    default: 
                      if (!$assertionsDisabled) {
                        throw new AssertionError();
                      }
                      break;
                    }
                  }
                }
              }
            }
            break;
          }
        }
      }
    }
    bc_codes.putByte(255);
    bc_codes.elementCountForDebug += 1;
    codeHist['ÿ'] += 1;
    endCode();
  }
  
  void printCodeHist()
  {
    assert (verbose > 0);
    String[] arrayOfString = new String[codeHist.length];
    int i = 0;
    for (int j = 0; j < codeHist.length; j++) {
      i += codeHist[j];
    }
    for (j = 0; j < codeHist.length; j++) {
      if (codeHist[j] == 0)
      {
        arrayOfString[j] = "";
      }
      else
      {
        String str1 = Instruction.byteName(j);
        String str2 = "" + codeHist[j];
        str2 = "         ".substring(str2.length()) + str2;
        for (String str3 = "" + codeHist[j] * 10000 / i; str3.length() < 4; str3 = "0" + str3) {}
        str3 = str3.substring(0, str3.length() - 2) + "." + str3.substring(str3.length() - 2);
        arrayOfString[j] = (str2 + "  " + str3 + "%  " + str1);
      }
    }
    Arrays.sort(arrayOfString);
    System.out.println("Bytecode histogram [" + i + "]");
    j = arrayOfString.length;
    for (;;)
    {
      j--;
      if (j < 0) {
        break;
      }
      if (!"".equals(arrayOfString[j])) {
        System.out.println(arrayOfString[j]);
      }
    }
    for (j = 0; j < ldcHist.length; j++)
    {
      int k = ldcHist[j];
      if (k != 0) {
        System.out.println("ldc " + ConstantPool.tagName(j) + " " + k);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\java\util\jar\pack\PackageWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package com.sun.java.util.jar.pack;

import java.beans.PropertyChangeListener;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import java.util.jar.Pack200.Packer;

public class PackerImpl
  extends TLGlobals
  implements Pack200.Packer
{
  public PackerImpl() {}
  
  public SortedMap<String, String> properties()
  {
    return props;
  }
  
  public synchronized void pack(JarFile paramJarFile, OutputStream paramOutputStream)
    throws IOException
  {
    assert (Utils.currentInstance.get() == null);
    int i = !props.getBoolean("com.sun.java.util.jar.pack.default.timezone") ? 1 : 0;
    try
    {
      Utils.currentInstance.set(this);
      if (i != 0) {
        Utils.changeDefaultTimeZoneToUtc();
      }
      if ("0".equals(props.getProperty("pack.effort"))) {
        Utils.copyJarFile(paramJarFile, paramOutputStream);
      } else {
        new DoPack(null).run(paramJarFile, paramOutputStream);
      }
    }
    finally
    {
      Utils.currentInstance.set(null);
      if (i != 0) {
        Utils.restoreDefaultTimeZone();
      }
      paramJarFile.close();
    }
  }
  
  public synchronized void pack(JarInputStream paramJarInputStream, OutputStream paramOutputStream)
    throws IOException
  {
    assert (Utils.currentInstance.get() == null);
    int i = !props.getBoolean("com.sun.java.util.jar.pack.default.timezone") ? 1 : 0;
    try
    {
      Utils.currentInstance.set(this);
      if (i != 0) {
        Utils.changeDefaultTimeZoneToUtc();
      }
      if ("0".equals(props.getProperty("pack.effort"))) {
        Utils.copyJarFile(paramJarInputStream, paramOutputStream);
      } else {
        new DoPack(null).run(paramJarInputStream, paramOutputStream);
      }
    }
    finally
    {
      Utils.currentInstance.set(null);
      if (i != 0) {
        Utils.restoreDefaultTimeZone();
      }
      paramJarInputStream.close();
    }
  }
  
  public void addPropertyChangeListener(PropertyChangeListener paramPropertyChangeListener)
  {
    props.addListener(paramPropertyChangeListener);
  }
  
  public void removePropertyChangeListener(PropertyChangeListener paramPropertyChangeListener)
  {
    props.removeListener(paramPropertyChangeListener);
  }
  
  private class DoPack
  {
    final int verbose = props.getInteger("com.sun.java.util.jar.pack.verbose");
    final Package pkg;
    final String unknownAttrCommand;
    final String classFormatCommand;
    final Map<Attribute.Layout, Attribute> attrDefs;
    final Map<Attribute.Layout, String> attrCommands;
    final boolean keepFileOrder;
    final boolean keepClassOrder;
    final boolean keepModtime;
    final boolean latestModtime;
    final boolean keepDeflateHint;
    long totalOutputSize;
    int segmentCount;
    long segmentTotalSize;
    long segmentSize;
    final long segmentLimit;
    final List<String> passFiles;
    private int nread;
    
    private DoPack()
    {
      props.setInteger("pack.progress", 0);
      if (verbose > 0) {
        Utils.log.info(props.toString());
      }
      pkg = new Package(Package.Version.makeVersion(props, "min.class"), Package.Version.makeVersion(props, "max.class"), Package.Version.makeVersion(props, "package"));
      Object localObject1 = props.getProperty("pack.unknown.attribute", "pass");
      if ((!"strip".equals(localObject1)) && (!"pass".equals(localObject1)) && (!"error".equals(localObject1))) {
        throw new RuntimeException("Bad option: pack.unknown.attribute = " + (String)localObject1);
      }
      unknownAttrCommand = ((String)localObject1).intern();
      localObject1 = props.getProperty("com.sun.java.util.jar.pack.class.format.error", "pass");
      if ((!"pass".equals(localObject1)) && (!"error".equals(localObject1))) {
        throw new RuntimeException("Bad option: com.sun.java.util.jar.pack.class.format.error = " + (String)localObject1);
      }
      classFormatCommand = ((String)localObject1).intern();
      localObject1 = new HashMap();
      Object localObject2 = new HashMap();
      String[] arrayOfString = { "pack.class.attribute.", "pack.field.attribute.", "pack.method.attribute.", "pack.code.attribute." };
      int[] arrayOfInt = { 0, 1, 2, 3 };
      for (int k = 0; k < arrayOfInt.length; k++)
      {
        String str1 = arrayOfString[k];
        SortedMap localSortedMap = props.prefixMap(str1);
        Iterator localIterator = localSortedMap.keySet().iterator();
        while (localIterator.hasNext())
        {
          String str2 = (String)localIterator.next();
          assert (str2.startsWith(str1));
          String str3 = str2.substring(str1.length());
          String str4 = props.getProperty(str2);
          Attribute.Layout localLayout = Attribute.keyForLookup(arrayOfInt[k], str3);
          if (("strip".equals(str4)) || ("pass".equals(str4)) || ("error".equals(str4)))
          {
            ((Map)localObject2).put(localLayout, str4.intern());
          }
          else
          {
            Attribute.define((Map)localObject1, arrayOfInt[k], str3, str4);
            if (verbose > 1) {
              Utils.log.fine("Added layout for " + Constants.ATTR_CONTEXT_NAME[k] + " attribute " + str3 + " = " + str4);
            }
            assert (((Map)localObject1).containsKey(localLayout));
          }
        }
      }
      attrDefs = (((Map)localObject1).isEmpty() ? null : (Map)localObject1);
      attrCommands = (((Map)localObject2).isEmpty() ? null : (Map)localObject2);
      keepFileOrder = props.getBoolean("pack.keep.file.order");
      keepClassOrder = props.getBoolean("com.sun.java.util.jar.pack.keep.class.order");
      keepModtime = "keep".equals(props.getProperty("pack.modification.time"));
      latestModtime = "latest".equals(props.getProperty("pack.modification.time"));
      keepDeflateHint = "keep".equals(props.getProperty("pack.deflate.hint"));
      if ((!keepModtime) && (!latestModtime))
      {
        int i = props.getTime("pack.modification.time");
        if (i != 0) {
          pkg.default_modtime = i;
        }
      }
      if (!keepDeflateHint)
      {
        boolean bool = props.getBoolean("pack.deflate.hint");
        if (bool) {
          pkg.default_options |= 0x20;
        }
      }
      totalOutputSize = 0L;
      segmentCount = 0;
      segmentTotalSize = 0L;
      segmentSize = 0L;
      if (props.getProperty("pack.segment.limit", "").equals("")) {
        l = -1L;
      } else {
        l = props.getLong("pack.segment.limit");
      }
      long l = Math.min(2147483647L, l);
      l = Math.max(-1L, l);
      if (l == -1L) {
        l = Long.MAX_VALUE;
      }
      segmentLimit = l;
      passFiles = props.getProperties("pack.pass.file.");
      ListIterator localListIterator = passFiles.listIterator();
      while (localListIterator.hasNext())
      {
        localObject2 = (String)localListIterator.next();
        if (localObject2 == null)
        {
          localListIterator.remove();
        }
        else
        {
          localObject2 = Utils.getJarEntryName((String)localObject2);
          if (((String)localObject2).endsWith("/")) {
            localObject2 = ((String)localObject2).substring(0, ((String)localObject2).length() - 1);
          }
          localListIterator.set(localObject2);
        }
      }
      if (verbose > 0) {
        Utils.log.info("passFiles = " + passFiles);
      }
      int j = props.getInteger("com.sun.java.util.jar.pack.archive.options");
      if (j != 0) {
        pkg.default_options |= j;
      }
      nread = 0;
    }
    
    boolean isClassFile(String paramString)
    {
      if (!paramString.endsWith(".class")) {
        return false;
      }
      int i;
      for (String str = paramString;; str = str.substring(0, i))
      {
        if (passFiles.contains(str)) {
          return false;
        }
        i = str.lastIndexOf('/');
        if (i < 0) {
          break;
        }
      }
      return true;
    }
    
    boolean isMetaInfFile(String paramString)
    {
      return (paramString.startsWith("/META-INF")) || (paramString.startsWith("META-INF"));
    }
    
    private void makeNextPackage()
    {
      pkg.reset();
    }
    
    private void noteRead(InFile paramInFile)
    {
      nread += 1;
      if (verbose > 2) {
        Utils.log.fine("...read " + name);
      }
      if ((verbose > 0) && (nread % 1000 == 0)) {
        Utils.log.info("Have read " + nread + " files...");
      }
    }
    
    void run(JarInputStream paramJarInputStream, OutputStream paramOutputStream)
      throws IOException
    {
      Object localObject1;
      Object localObject2;
      if (paramJarInputStream.getManifest() != null)
      {
        localObject1 = new ByteArrayOutputStream();
        paramJarInputStream.getManifest().write((OutputStream)localObject1);
        localObject2 = new ByteArrayInputStream(((ByteArrayOutputStream)localObject1).toByteArray());
        pkg.addFile(readFile("META-INF/MANIFEST.MF", (InputStream)localObject2));
      }
      while ((localObject1 = paramJarInputStream.getNextJarEntry()) != null)
      {
        localObject2 = new InFile((JarEntry)localObject1);
        String str = name;
        Package.File localFile1 = readFile(str, paramJarInputStream);
        Package.File localFile2 = null;
        long l = isMetaInfFile(str) ? 0L : ((InFile)localObject2).getInputLength();
        if (segmentSize += l > segmentLimit)
        {
          segmentSize -= l;
          int i = -1;
          flushPartial(paramOutputStream, i);
        }
        if (verbose > 1) {
          Utils.log.fine("Reading " + str);
        }
        assert (((JarEntry)localObject1).isDirectory() == str.endsWith("/"));
        if (isClassFile(str)) {
          localFile2 = readClass(str, localFile1.getInputStream());
        }
        if (localFile2 == null)
        {
          localFile2 = localFile1;
          pkg.addFile(localFile2);
        }
        ((InFile)localObject2).copyTo(localFile2);
        noteRead((InFile)localObject2);
      }
      flushAll(paramOutputStream);
    }
    
    void run(JarFile paramJarFile, OutputStream paramOutputStream)
      throws IOException
    {
      List localList = scanJar(paramJarFile);
      if (verbose > 0) {
        Utils.log.info("Reading " + localList.size() + " files...");
      }
      int i = 0;
      Iterator localIterator = localList.iterator();
      while (localIterator.hasNext())
      {
        InFile localInFile = (InFile)localIterator.next();
        String str = name;
        long l = isMetaInfFile(str) ? 0L : localInFile.getInputLength();
        if (segmentSize += l > segmentLimit)
        {
          segmentSize -= l;
          float f1 = i + 1;
          float f2 = segmentCount + 1;
          float f3 = localList.size() - f1;
          float f4 = f3 * (f2 / f1);
          if (verbose > 1) {
            Utils.log.fine("Estimated segments to do: " + f4);
          }
          flushPartial(paramOutputStream, (int)Math.ceil(f4));
        }
        InputStream localInputStream = localInFile.getInputStream();
        if (verbose > 1) {
          Utils.log.fine("Reading " + str);
        }
        Package.File localFile = null;
        if (isClassFile(str))
        {
          localFile = readClass(str, localInputStream);
          if (localFile == null)
          {
            localInputStream.close();
            localInputStream = localInFile.getInputStream();
          }
        }
        if (localFile == null)
        {
          localFile = readFile(str, localInputStream);
          pkg.addFile(localFile);
        }
        localInFile.copyTo(localFile);
        localInputStream.close();
        noteRead(localInFile);
        i++;
      }
      flushAll(paramOutputStream);
    }
    
    Package.File readClass(String paramString, InputStream paramInputStream)
      throws IOException
    {
      Package tmp8_5 = pkg;
      tmp8_5.getClass();
      Package.Class localClass = new Package.Class(tmp8_5, paramString);
      paramInputStream = new BufferedInputStream(paramInputStream);
      ClassReader localClassReader = new ClassReader(localClass, paramInputStream);
      localClassReader.setAttrDefs(attrDefs);
      localClassReader.setAttrCommands(attrCommands);
      unknownAttrCommand = unknownAttrCommand;
      try
      {
        localClassReader.read();
      }
      catch (IOException localIOException)
      {
        String str = "Passing class file uncompressed due to";
        Object localObject;
        if ((localIOException instanceof Attribute.FormatException))
        {
          localObject = (Attribute.FormatException)localIOException;
          if (layout.equals("pass"))
          {
            Utils.log.info(((Attribute.FormatException)localObject).toString());
            Utils.log.warning(str + " unrecognized attribute: " + paramString);
            return null;
          }
        }
        else if ((localIOException instanceof ClassReader.ClassFormatException))
        {
          localObject = (ClassReader.ClassFormatException)localIOException;
          if (classFormatCommand.equals("pass"))
          {
            Utils.log.info(((ClassReader.ClassFormatException)localObject).toString());
            Utils.log.warning(str + " unknown class format: " + paramString);
            return null;
          }
        }
        throw localIOException;
      }
      pkg.addClass(localClass);
      return file;
    }
    
    Package.File readFile(String paramString, InputStream paramInputStream)
      throws IOException
    {
      Package tmp8_5 = pkg;
      tmp8_5.getClass();
      Package.File localFile = new Package.File(tmp8_5, paramString);
      localFile.readFrom(paramInputStream);
      if ((localFile.isDirectory()) && (localFile.getFileLength() != 0L)) {
        throw new IllegalArgumentException("Non-empty directory: " + localFile.getFileName());
      }
      return localFile;
    }
    
    void flushPartial(OutputStream paramOutputStream, int paramInt)
      throws IOException
    {
      if ((pkg.files.isEmpty()) && (pkg.classes.isEmpty())) {
        return;
      }
      flushPackage(paramOutputStream, Math.max(1, paramInt));
      props.setInteger("pack.progress", 25);
      makeNextPackage();
      segmentCount += 1;
      segmentTotalSize += segmentSize;
      segmentSize = 0L;
    }
    
    void flushAll(OutputStream paramOutputStream)
      throws IOException
    {
      props.setInteger("pack.progress", 50);
      flushPackage(paramOutputStream, 0);
      paramOutputStream.flush();
      props.setInteger("pack.progress", 100);
      segmentCount += 1;
      segmentTotalSize += segmentSize;
      segmentSize = 0L;
      if ((verbose > 0) && (segmentCount > 1)) {
        Utils.log.info("Transmitted " + segmentTotalSize + " input bytes in " + segmentCount + " segments totaling " + totalOutputSize + " bytes");
      }
    }
    
    void flushPackage(OutputStream paramOutputStream, int paramInt)
      throws IOException
    {
      int i = pkg.files.size();
      if (!keepFileOrder)
      {
        if (verbose > 1) {
          Utils.log.fine("Reordering files.");
        }
        boolean bool = true;
        pkg.reorderFiles(keepClassOrder, bool);
      }
      else
      {
        assert (pkg.files.containsAll(pkg.getClassStubs()));
        localObject = pkg.files;
        if ((($assertionsDisabled) || ((localObject = new ArrayList(pkg.files)).retainAll(pkg.getClassStubs()))) || ((!$assertionsDisabled) && (!((List)localObject).equals(pkg.getClassStubs())))) {
          throw new AssertionError();
        }
      }
      pkg.trimStubs();
      if (props.getBoolean("com.sun.java.util.jar.pack.strip.debug")) {
        pkg.stripAttributeKind("Debug");
      }
      if (props.getBoolean("com.sun.java.util.jar.pack.strip.compile")) {
        pkg.stripAttributeKind("Compile");
      }
      if (props.getBoolean("com.sun.java.util.jar.pack.strip.constants")) {
        pkg.stripAttributeKind("Constant");
      }
      if (props.getBoolean("com.sun.java.util.jar.pack.strip.exceptions")) {
        pkg.stripAttributeKind("Exceptions");
      }
      if (props.getBoolean("com.sun.java.util.jar.pack.strip.innerclasses")) {
        pkg.stripAttributeKind("InnerClasses");
      }
      Object localObject = new PackageWriter(pkg, paramOutputStream);
      archiveNextCount = paramInt;
      ((PackageWriter)localObject).write();
      paramOutputStream.flush();
      if (verbose > 0)
      {
        long l1 = archiveSize0 + archiveSize1;
        totalOutputSize += l1;
        long l2 = segmentSize;
        Utils.log.info("Transmitted " + i + " files of " + l2 + " input bytes in a segment of " + l1 + " bytes");
      }
    }
    
    List<InFile> scanJar(JarFile paramJarFile)
      throws IOException
    {
      ArrayList localArrayList = new ArrayList();
      try
      {
        Iterator localIterator = Collections.list(paramJarFile.entries()).iterator();
        while (localIterator.hasNext())
        {
          JarEntry localJarEntry = (JarEntry)localIterator.next();
          InFile localInFile = new InFile(paramJarFile, localJarEntry);
          assert (localJarEntry.isDirectory() == name.endsWith("/"));
          localArrayList.add(localInFile);
        }
      }
      catch (IllegalStateException localIllegalStateException)
      {
        throw new IOException(localIllegalStateException.getLocalizedMessage(), localIllegalStateException);
      }
      return localArrayList;
    }
    
    final class InFile
    {
      final String name;
      final JarFile jf;
      final JarEntry je;
      final File f;
      int modtime = 0;
      int options;
      
      InFile(String paramString)
      {
        name = Utils.getJarEntryName(paramString);
        f = new File(paramString);
        jf = null;
        je = null;
        int i = getModtime(f.lastModified());
        if ((keepModtime) && (i != 0)) {
          modtime = i;
        } else if ((latestModtime) && (i > pkg.default_modtime)) {
          pkg.default_modtime = i;
        }
      }
      
      InFile(JarFile paramJarFile, JarEntry paramJarEntry)
      {
        name = Utils.getJarEntryName(paramJarEntry.getName());
        f = null;
        jf = paramJarFile;
        je = paramJarEntry;
        int i = getModtime(paramJarEntry.getTime());
        if ((keepModtime) && (i != 0)) {
          modtime = i;
        } else if ((latestModtime) && (i > pkg.default_modtime)) {
          pkg.default_modtime = i;
        }
        if ((keepDeflateHint) && (paramJarEntry.getMethod() == 8)) {
          options |= 0x1;
        }
      }
      
      InFile(JarEntry paramJarEntry)
      {
        this(null, paramJarEntry);
      }
      
      long getInputLength()
      {
        long l = je != null ? je.getSize() : f.length();
        assert (l >= 0L) : (this + ".len=" + l);
        return Math.max(0L, l) + name.length() + 5L;
      }
      
      int getModtime(long paramLong)
      {
        long l = (paramLong + 500L) / 1000L;
        if ((int)l == l) {
          return (int)l;
        }
        Utils.log.warning("overflow in modtime for " + f);
        return 0;
      }
      
      void copyTo(Package.File paramFile)
      {
        if (modtime != 0) {
          modtime = modtime;
        }
        options |= options;
      }
      
      InputStream getInputStream()
        throws IOException
      {
        if (jf != null) {
          return jf.getInputStream(je);
        }
        return new FileInputStream(f);
      }
      
      public String toString()
      {
        return name;
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\java\util\jar\pack\PackerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
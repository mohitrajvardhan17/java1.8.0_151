package sun.launcher;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Locale.Category;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeSet;
import java.util.jar.Attributes;
import java.util.jar.Attributes.Name;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import sun.misc.VM;

public enum LauncherHelper
{
  INSTANCE;
  
  private static final String MAIN_CLASS = "Main-Class";
  private static StringBuilder outBuf = new StringBuilder();
  private static final String INDENT = "    ";
  private static final String VM_SETTINGS = "VM settings:";
  private static final String PROP_SETTINGS = "Property settings:";
  private static final String LOCALE_SETTINGS = "Locale settings:";
  private static final String diagprop = "sun.java.launcher.diag";
  static final boolean trace = VM.getSavedProperty("sun.java.launcher.diag") != null;
  private static final String defaultBundleName = "sun.launcher.resources.launcher";
  private static PrintStream ostream;
  private static final ClassLoader scloader = ClassLoader.getSystemClassLoader();
  private static Class<?> appClass;
  private static final int LM_UNKNOWN = 0;
  private static final int LM_CLASS = 1;
  private static final int LM_JAR = 2;
  private static final String encprop = "sun.jnu.encoding";
  private static String encoding = null;
  private static boolean isCharsetSupported = false;
  
  private LauncherHelper() {}
  
  static void showSettings(boolean paramBoolean1, String paramString, long paramLong1, long paramLong2, long paramLong3, boolean paramBoolean2)
  {
    initOutput(paramBoolean1);
    String[] arrayOfString = paramString.split(":");
    String str1 = (arrayOfString.length > 1) && (arrayOfString[1] != null) ? arrayOfString[1].trim() : "all";
    switch (str1)
    {
    case "vm": 
      printVmSettings(paramLong1, paramLong2, paramLong3, paramBoolean2);
      break;
    case "properties": 
      printProperties();
      break;
    case "locale": 
      printLocale();
      break;
    default: 
      printVmSettings(paramLong1, paramLong2, paramLong3, paramBoolean2);
      printProperties();
      printLocale();
    }
  }
  
  private static void printVmSettings(long paramLong1, long paramLong2, long paramLong3, boolean paramBoolean)
  {
    ostream.println("VM settings:");
    if (paramLong3 != 0L) {
      ostream.println("    Stack Size: " + SizePrefix.scaleValue(paramLong3));
    }
    if (paramLong1 != 0L) {
      ostream.println("    Min. Heap Size: " + SizePrefix.scaleValue(paramLong1));
    }
    if (paramLong2 != 0L) {
      ostream.println("    Max. Heap Size: " + SizePrefix.scaleValue(paramLong2));
    } else {
      ostream.println("    Max. Heap Size (Estimated): " + SizePrefix.scaleValue(Runtime.getRuntime().maxMemory()));
    }
    ostream.println("    Ergonomics Machine Class: " + (paramBoolean ? "server" : "client"));
    ostream.println("    Using VM: " + System.getProperty("java.vm.name"));
    ostream.println();
  }
  
  private static void printProperties()
  {
    Properties localProperties = System.getProperties();
    ostream.println("Property settings:");
    ArrayList localArrayList = new ArrayList();
    localArrayList.addAll(localProperties.stringPropertyNames());
    Collections.sort(localArrayList);
    Iterator localIterator = localArrayList.iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      printPropertyValue(str, localProperties.getProperty(str));
    }
    ostream.println();
  }
  
  private static boolean isPath(String paramString)
  {
    return (paramString.endsWith(".dirs")) || (paramString.endsWith(".path"));
  }
  
  private static void printPropertyValue(String paramString1, String paramString2)
  {
    ostream.print("    " + paramString1 + " = ");
    int k;
    if (paramString1.equals("line.separator"))
    {
      for (k : paramString2.getBytes()) {
        switch (k)
        {
        case 13: 
          ostream.print("\\r ");
          break;
        case 10: 
          ostream.print("\\n ");
          break;
        default: 
          ostream.printf("0x%02X", new Object[] { Integer.valueOf(k & 0xFF) });
        }
      }
      ostream.println();
      return;
    }
    if (!isPath(paramString1))
    {
      ostream.println(paramString2);
      return;
    }
    ??? = paramString2.split(System.getProperty("path.separator"));
    ??? = 1;
    for (String str : ???) {
      if (??? != 0)
      {
        ostream.println(str);
        ??? = 0;
      }
      else
      {
        ostream.println("        " + str);
      }
    }
  }
  
  private static void printLocale()
  {
    Locale localLocale = Locale.getDefault();
    ostream.println("Locale settings:");
    ostream.println("    default locale = " + localLocale.getDisplayLanguage());
    ostream.println("    default display locale = " + Locale.getDefault(Locale.Category.DISPLAY).getDisplayName());
    ostream.println("    default format locale = " + Locale.getDefault(Locale.Category.FORMAT).getDisplayName());
    printLocales();
    ostream.println();
  }
  
  private static void printLocales()
  {
    Locale[] arrayOfLocale = Locale.getAvailableLocales();
    int i = arrayOfLocale == null ? 0 : arrayOfLocale.length;
    if (i < 1) {
      return;
    }
    TreeSet localTreeSet = new TreeSet();
    String str;
    for (str : arrayOfLocale) {
      localTreeSet.add(str.toString());
    }
    ostream.print("    available locales = ");
    ??? = localTreeSet.iterator();
    ??? = i - 1;
    for (??? = 0; ((Iterator)???).hasNext(); ???++)
    {
      str = (String)((Iterator)???).next();
      ostream.print(str);
      if (??? != ???) {
        ostream.print(", ");
      }
      if ((??? + 1) % 8 == 0)
      {
        ostream.println();
        ostream.print("        ");
      }
    }
  }
  
  private static String getLocalizedMessage(String paramString, Object... paramVarArgs)
  {
    String str = ResourceBundleHolder.RB.getString(paramString);
    return paramVarArgs != null ? MessageFormat.format(str, paramVarArgs) : str;
  }
  
  static void initHelpMessage(String paramString)
  {
    outBuf = outBuf.append(getLocalizedMessage("java.launcher.opt.header", new Object[] { paramString == null ? "java" : paramString }));
    outBuf = outBuf.append(getLocalizedMessage("java.launcher.opt.datamodel", new Object[] { Integer.valueOf(32) }));
    outBuf = outBuf.append(getLocalizedMessage("java.launcher.opt.datamodel", new Object[] { Integer.valueOf(64) }));
  }
  
  static void appendVmSelectMessage(String paramString1, String paramString2)
  {
    outBuf = outBuf.append(getLocalizedMessage("java.launcher.opt.vmselect", new Object[] { paramString1, paramString2 }));
  }
  
  static void appendVmSynonymMessage(String paramString1, String paramString2)
  {
    outBuf = outBuf.append(getLocalizedMessage("java.launcher.opt.hotspot", new Object[] { paramString1, paramString2 }));
  }
  
  static void appendVmErgoMessage(boolean paramBoolean, String paramString)
  {
    outBuf = outBuf.append(getLocalizedMessage("java.launcher.ergo.message1", new Object[] { paramString }));
    outBuf = paramBoolean ? outBuf.append(",\n" + getLocalizedMessage("java.launcher.ergo.message2", new Object[0]) + "\n\n") : outBuf.append(".\n\n");
  }
  
  static void printHelpMessage(boolean paramBoolean)
  {
    initOutput(paramBoolean);
    outBuf = outBuf.append(getLocalizedMessage("java.launcher.opt.footer", new Object[] { File.pathSeparator }));
    ostream.println(outBuf.toString());
  }
  
  static void printXUsageMessage(boolean paramBoolean)
  {
    initOutput(paramBoolean);
    ostream.println(getLocalizedMessage("java.launcher.X.usage", new Object[] { File.pathSeparator }));
    if (System.getProperty("os.name").contains("OS X")) {
      ostream.println(getLocalizedMessage("java.launcher.X.macosx.usage", new Object[] { File.pathSeparator }));
    }
  }
  
  static void initOutput(boolean paramBoolean)
  {
    ostream = paramBoolean ? System.err : System.out;
  }
  
  static String getMainClassFromJar(String paramString)
  {
    String str1 = null;
    try
    {
      JarFile localJarFile = new JarFile(paramString);
      Object localObject1 = null;
      try
      {
        Manifest localManifest = localJarFile.getManifest();
        if (localManifest == null) {
          abort(null, "java.launcher.jar.error2", new Object[] { paramString });
        }
        Attributes localAttributes = localManifest.getMainAttributes();
        if (localAttributes == null) {
          abort(null, "java.launcher.jar.error3", new Object[] { paramString });
        }
        str1 = localAttributes.getValue("Main-Class");
        if (str1 == null) {
          abort(null, "java.launcher.jar.error3", new Object[] { paramString });
        }
        if (localAttributes.containsKey(new Attributes.Name("JavaFX-Application-Class")))
        {
          str2 = FXHelper.class.getName();
          return str2;
        }
        String str2 = str1.trim();
        return str2;
      }
      catch (Throwable localThrowable1)
      {
        localObject1 = localThrowable1;
        throw localThrowable1;
      }
      finally
      {
        if (localJarFile != null) {
          if (localObject1 != null) {
            try
            {
              localJarFile.close();
            }
            catch (Throwable localThrowable4)
            {
              ((Throwable)localObject1).addSuppressed(localThrowable4);
            }
          } else {
            localJarFile.close();
          }
        }
      }
      return null;
    }
    catch (IOException localIOException)
    {
      abort(localIOException, "java.launcher.jar.error1", new Object[] { paramString });
    }
  }
  
  static void abort(Throwable paramThrowable, String paramString, Object... paramVarArgs)
  {
    if (paramString != null) {
      ostream.println(getLocalizedMessage(paramString, paramVarArgs));
    }
    if (trace) {
      if (paramThrowable != null) {
        paramThrowable.printStackTrace();
      } else {
        Thread.dumpStack();
      }
    }
    System.exit(1);
  }
  
  public static Class<?> checkAndLoadMain(boolean paramBoolean, int paramInt, String paramString)
  {
    initOutput(paramBoolean);
    String str = null;
    switch (paramInt)
    {
    case 1: 
      str = paramString;
      break;
    case 2: 
      str = getMainClassFromJar(paramString);
      break;
    default: 
      throw new InternalError("" + paramInt + ": Unknown launch mode");
    }
    str = str.replace('/', '.');
    Class localClass = null;
    try
    {
      localClass = scloader.loadClass(str);
    }
    catch (NoClassDefFoundError|ClassNotFoundException localNoClassDefFoundError1)
    {
      if ((System.getProperty("os.name", "").contains("OS X")) && (Normalizer.isNormalized(str, Normalizer.Form.NFD))) {
        try
        {
          localClass = scloader.loadClass(Normalizer.normalize(str, Normalizer.Form.NFC));
        }
        catch (NoClassDefFoundError|ClassNotFoundException localNoClassDefFoundError2)
        {
          abort(localNoClassDefFoundError1, "java.launcher.cls.error1", new Object[] { str });
        }
      } else {
        abort(localNoClassDefFoundError1, "java.launcher.cls.error1", new Object[] { str });
      }
    }
    appClass = localClass;
    if ((localClass.equals(FXHelper.class)) || (FXHelper.doesExtendFXApplication(localClass)))
    {
      FXHelper.setFXLaunchParameters(paramString, paramInt);
      return FXHelper.class;
    }
    validateMainClass(localClass);
    return localClass;
  }
  
  public static Class<?> getApplicationClass()
  {
    return appClass;
  }
  
  static void validateMainClass(Class<?> paramClass)
  {
    Method localMethod;
    try
    {
      localMethod = paramClass.getMethod("main", new Class[] { String[].class });
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      abort(null, "java.launcher.cls.error4", new Object[] { paramClass.getName(), "javafx.application.Application" });
      return;
    }
    int i = localMethod.getModifiers();
    if (!Modifier.isStatic(i)) {
      abort(null, "java.launcher.cls.error2", new Object[] { "static", localMethod.getDeclaringClass().getName() });
    }
    if (localMethod.getReturnType() != Void.TYPE) {
      abort(null, "java.launcher.cls.error3", new Object[] { localMethod.getDeclaringClass().getName() });
    }
  }
  
  static String makePlatformString(boolean paramBoolean, byte[] paramArrayOfByte)
  {
    initOutput(paramBoolean);
    if (encoding == null)
    {
      encoding = System.getProperty("sun.jnu.encoding");
      isCharsetSupported = Charset.isSupported(encoding);
    }
    try
    {
      String str = isCharsetSupported ? new String(paramArrayOfByte, encoding) : new String(paramArrayOfByte);
      return str;
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      abort(localUnsupportedEncodingException, null, new Object[0]);
    }
    return null;
  }
  
  static String[] expandArgs(String[] paramArrayOfString)
  {
    ArrayList localArrayList = new ArrayList();
    for (String str : paramArrayOfString) {
      localArrayList.add(new StdArg(str));
    }
    return expandArgs(localArrayList);
  }
  
  static String[] expandArgs(List<StdArg> paramList)
  {
    ArrayList localArrayList = new ArrayList();
    if (trace) {
      System.err.println("Incoming arguments:");
    }
    Object localObject1 = paramList.iterator();
    Object localObject2;
    String str;
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (StdArg)((Iterator)localObject1).next();
      if (trace) {
        System.err.println(localObject2);
      }
      if (needsExpansion)
      {
        File localFile1 = new File(arg);
        File localFile2 = localFile1.getParentFile();
        str = localFile1.getName();
        if (localFile2 == null) {
          localFile2 = new File(".");
        }
        try
        {
          DirectoryStream localDirectoryStream = Files.newDirectoryStream(localFile2.toPath(), str);
          Object localObject3 = null;
          try
          {
            int k = 0;
            Iterator localIterator = localDirectoryStream.iterator();
            while (localIterator.hasNext())
            {
              Path localPath = (Path)localIterator.next();
              localArrayList.add(localPath.normalize().toString());
              k++;
            }
            if (k == 0) {
              localArrayList.add(arg);
            }
          }
          catch (Throwable localThrowable2)
          {
            localObject3 = localThrowable2;
            throw localThrowable2;
          }
          finally
          {
            if (localDirectoryStream != null) {
              if (localObject3 != null) {
                try
                {
                  localDirectoryStream.close();
                }
                catch (Throwable localThrowable3)
                {
                  ((Throwable)localObject3).addSuppressed(localThrowable3);
                }
              } else {
                localDirectoryStream.close();
              }
            }
          }
        }
        catch (Exception localException)
        {
          localArrayList.add(arg);
          if (trace)
          {
            System.err.println("Warning: passing argument as-is " + localObject2);
            System.err.print(localException);
          }
        }
      }
      else
      {
        localArrayList.add(arg);
      }
    }
    localObject1 = new String[localArrayList.size()];
    localArrayList.toArray((Object[])localObject1);
    if (trace)
    {
      System.err.println("Expanded arguments:");
      for (str : localObject1) {
        System.err.println(str);
      }
    }
    return (String[])localObject1;
  }
  
  static final class FXHelper
  {
    private static final String JAVAFX_APPLICATION_MARKER = "JavaFX-Application-Class";
    private static final String JAVAFX_APPLICATION_CLASS_NAME = "javafx.application.Application";
    private static final String JAVAFX_LAUNCHER_CLASS_NAME = "com.sun.javafx.application.LauncherImpl";
    private static final String JAVAFX_LAUNCH_MODE_CLASS = "LM_CLASS";
    private static final String JAVAFX_LAUNCH_MODE_JAR = "LM_JAR";
    private static String fxLaunchName = null;
    private static String fxLaunchMode = null;
    private static Class<?> fxLauncherClass = null;
    private static Method fxLauncherMethod = null;
    
    FXHelper() {}
    
    private static void setFXLaunchParameters(String paramString, int paramInt)
    {
      try
      {
        fxLauncherClass = LauncherHelper.scloader.loadClass("com.sun.javafx.application.LauncherImpl");
        fxLauncherMethod = fxLauncherClass.getMethod("launchApplication", new Class[] { String.class, String.class, String[].class });
        int i = fxLauncherMethod.getModifiers();
        if (!Modifier.isStatic(i)) {
          LauncherHelper.abort(null, "java.launcher.javafx.error1", new Object[0]);
        }
        if (fxLauncherMethod.getReturnType() != Void.TYPE) {
          LauncherHelper.abort(null, "java.launcher.javafx.error1", new Object[0]);
        }
      }
      catch (ClassNotFoundException|NoSuchMethodException localClassNotFoundException)
      {
        LauncherHelper.abort(localClassNotFoundException, "java.launcher.cls.error5", new Object[] { localClassNotFoundException });
      }
      fxLaunchName = paramString;
      switch (paramInt)
      {
      case 1: 
        fxLaunchMode = "LM_CLASS";
        break;
      case 2: 
        fxLaunchMode = "LM_JAR";
        break;
      default: 
        throw new InternalError(paramInt + ": Unknown launch mode");
      }
    }
    
    private static boolean doesExtendFXApplication(Class<?> paramClass)
    {
      for (Class localClass = paramClass.getSuperclass(); localClass != null; localClass = localClass.getSuperclass()) {
        if (localClass.getName().equals("javafx.application.Application")) {
          return true;
        }
      }
      return false;
    }
    
    public static void main(String... paramVarArgs)
      throws Exception
    {
      if ((fxLauncherMethod == null) || (fxLaunchMode == null) || (fxLaunchName == null)) {
        throw new RuntimeException("Invalid JavaFX launch parameters");
      }
      fxLauncherMethod.invoke(null, new Object[] { fxLaunchName, fxLaunchMode, paramVarArgs });
    }
  }
  
  private static class ResourceBundleHolder
  {
    private static final ResourceBundle RB = ResourceBundle.getBundle("sun.launcher.resources.launcher");
    
    private ResourceBundleHolder() {}
  }
  
  private static enum SizePrefix
  {
    KILO(1024L, "K"),  MEGA(1048576L, "M"),  GIGA(1073741824L, "G"),  TERA(1099511627776L, "T");
    
    long size;
    String abbrev;
    
    private SizePrefix(long paramLong, String paramString)
    {
      size = paramLong;
      abbrev = paramString;
    }
    
    private static String scale(long paramLong, SizePrefix paramSizePrefix)
    {
      return BigDecimal.valueOf(paramLong).divide(BigDecimal.valueOf(size), 2, RoundingMode.HALF_EVEN).toPlainString() + abbrev;
    }
    
    static String scaleValue(long paramLong)
    {
      if (paramLong < MEGAsize) {
        return scale(paramLong, KILO);
      }
      if (paramLong < GIGAsize) {
        return scale(paramLong, MEGA);
      }
      if (paramLong < TERAsize) {
        return scale(paramLong, GIGA);
      }
      return scale(paramLong, TERA);
    }
  }
  
  private static class StdArg
  {
    final String arg;
    final boolean needsExpansion;
    
    StdArg(String paramString, boolean paramBoolean)
    {
      arg = paramString;
      needsExpansion = paramBoolean;
    }
    
    StdArg(String paramString)
    {
      arg = paramString.substring(1);
      needsExpansion = (paramString.charAt(0) == 'T');
    }
    
    public String toString()
    {
      return "StdArg{arg=" + arg + ", needsExpansion=" + needsExpansion + '}';
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\launcher\LauncherHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
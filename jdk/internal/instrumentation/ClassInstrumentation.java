package jdk.internal.instrumentation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilePermission;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.Permission;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.List;
import java.util.PropertyPermission;
import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.ClassVisitor;
import jdk.internal.org.objectweb.asm.ClassWriter;
import jdk.internal.org.objectweb.asm.tree.ClassNode;
import jdk.internal.org.objectweb.asm.util.CheckClassAdapter;

public final class ClassInstrumentation
{
  private final Class<?> instrumentor;
  private final Logger logger;
  private final String targetName;
  private final String instrumentorName;
  private byte[] newBytes;
  private final ClassReader targetClassReader;
  private final ClassReader instrClassReader;
  private static final String JAVA_HOME = (String)AccessController.doPrivileged(new PrivilegedAction()
  {
    public String run()
    {
      return System.getProperty("java.home");
    }
  }, null, new Permission[] { new PropertyPermission("java.home", "read") });
  
  public ClassInstrumentation(Class<?> paramClass, String paramString, byte[] paramArrayOfByte, Logger paramLogger)
    throws ClassNotFoundException, IOException
  {
    instrumentorName = paramClass.getName();
    targetName = paramString;
    instrumentor = paramClass;
    logger = paramLogger;
    targetClassReader = new ClassReader(paramArrayOfByte);
    instrClassReader = new ClassReader(getInstrumentationInputStream(instrumentorName));
    instrument();
    saveGeneratedInstrumentation();
  }
  
  private InputStream getInstrumentationInputStream(final String paramString)
    throws IOException
  {
    try
    {
      (InputStream)AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        public InputStream run()
          throws IOException
        {
          return Tracer.class.getResourceAsStream("/" + paramString.replace(".", "/") + ".class");
        }
      }, null, new Permission[] { new FilePermission(JAVA_HOME + File.separator + "-", "read") });
    }
    catch (PrivilegedActionException localPrivilegedActionException)
    {
      Exception localException = localPrivilegedActionException.getException();
      if ((localException instanceof IOException)) {
        throw ((IOException)localException);
      }
      throw ((RuntimeException)localException);
    }
  }
  
  private void instrument()
    throws IOException, ClassNotFoundException
  {
    ArrayList localArrayList = new ArrayList();
    for (localObject2 : instrumentor.getDeclaredMethods())
    {
      localObject3 = (InstrumentationMethod)((Method)localObject2).getAnnotation(InstrumentationMethod.class);
      if (localObject3 != null) {
        localArrayList.add(localObject2);
      }
    }
    ??? = new MaxLocalsTracker();
    instrClassReader.accept((ClassVisitor)???, 0);
    ClassNode localClassNode = new ClassNode();
    Inliner localInliner = new Inliner(327680, localClassNode, instrumentorName, targetClassReader, localArrayList, (MaxLocalsTracker)???, logger);
    instrClassReader.accept(localInliner, 8);
    Object localObject2 = new ClassWriter(2);
    Object localObject3 = new MethodMergeAdapter((ClassVisitor)localObject2, localClassNode, localArrayList, (TypeMapping[])instrumentor.getAnnotationsByType(TypeMapping.class), logger);
    targetClassReader.accept((ClassVisitor)localObject3, 8);
    newBytes = ((ClassWriter)localObject2).toByteArray();
  }
  
  public byte[] getNewBytes()
  {
    return (byte[])newBytes.clone();
  }
  
  private void saveGeneratedInstrumentation()
  {
    boolean bool = ((Boolean)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Boolean run()
      {
        return Boolean.valueOf(Boolean.getBoolean("jfr.savegenerated"));
      }
    })).booleanValue();
    if (bool) {
      try
      {
        writeGeneratedDebugInstrumentation();
      }
      catch (IOException|ClassNotFoundException localIOException)
      {
        logger.info("Unable to create debug instrumentation");
      }
    }
  }
  
  private void writeGeneratedDebugInstrumentation()
    throws IOException, ClassNotFoundException
  {
    Object localObject1 = new FileOutputStream(targetName + ".class");
    Object localObject2 = null;
    try
    {
      ((FileOutputStream)localObject1).write(newBytes);
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
            ((FileOutputStream)localObject1).close();
          }
          catch (Throwable localThrowable5)
          {
            ((Throwable)localObject2).addSuppressed(localThrowable5);
          }
        } else {
          ((FileOutputStream)localObject1).close();
        }
      }
    }
    localObject1 = new FileWriter(targetName + ".asm");
    localObject2 = null;
    try
    {
      PrintWriter localPrintWriter = new PrintWriter((Writer)localObject1);
      Object localObject4 = null;
      try
      {
        ClassReader localClassReader = new ClassReader(getNewBytes());
        CheckClassAdapter.verify(localClassReader, true, localPrintWriter);
      }
      catch (Throwable localThrowable7)
      {
        localObject4 = localThrowable7;
        throw localThrowable7;
      }
      finally
      {
        if (localPrintWriter != null) {
          if (localObject4 != null) {
            try
            {
              localPrintWriter.close();
            }
            catch (Throwable localThrowable8)
            {
              ((Throwable)localObject4).addSuppressed(localThrowable8);
            }
          } else {
            localPrintWriter.close();
          }
        }
      }
    }
    catch (Throwable localThrowable4)
    {
      localObject2 = localThrowable4;
      throw localThrowable4;
    }
    finally
    {
      if (localObject1 != null) {
        if (localObject2 != null) {
          try
          {
            ((FileWriter)localObject1).close();
          }
          catch (Throwable localThrowable9)
          {
            ((Throwable)localObject2).addSuppressed(localThrowable9);
          }
        } else {
          ((FileWriter)localObject1).close();
        }
      }
    }
    logger.info("Instrumented code saved to " + targetName + ".class and .asm");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\instrumentation\ClassInstrumentation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
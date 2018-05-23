package jdk.internal.instrumentation;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.ClassVisitor;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.Type;
import jdk.internal.org.objectweb.asm.tree.ClassNode;
import jdk.internal.org.objectweb.asm.tree.MethodNode;

final class Inliner
  extends ClassVisitor
{
  private final String instrumentationClassName;
  private final Logger logger;
  private final ClassNode targetClassNode;
  private final List<Method> instrumentationMethods;
  private final MaxLocalsTracker maxLocalsTracker;
  
  Inliner(int paramInt, ClassVisitor paramClassVisitor, String paramString, ClassReader paramClassReader, List<Method> paramList, MaxLocalsTracker paramMaxLocalsTracker, Logger paramLogger)
  {
    super(paramInt, paramClassVisitor);
    instrumentationClassName = paramString;
    instrumentationMethods = paramList;
    maxLocalsTracker = paramMaxLocalsTracker;
    logger = paramLogger;
    ClassNode localClassNode = new ClassNode(327680);
    paramClassReader.accept(localClassNode, 8);
    targetClassNode = localClassNode;
  }
  
  public MethodVisitor visitMethod(int paramInt, String paramString1, String paramString2, String paramString3, String[] paramArrayOfString)
  {
    MethodVisitor localMethodVisitor = super.visitMethod(paramInt, paramString1, paramString2, paramString3, paramArrayOfString);
    if (isInstrumentationMethod(paramString1, paramString2))
    {
      MethodNode localMethodNode = findTargetMethodNode(paramString1, paramString2);
      if (localMethodNode == null) {
        throw new IllegalArgumentException("Could not find the method to instrument in the target class");
      }
      if ((access & 0x100) == 1) {
        throw new IllegalArgumentException("Cannot instrument native methods: " + targetClassNode.name + "." + name + desc);
      }
      logger.trace("Inliner processing method " + paramString1 + paramString2);
      MethodCallInliner localMethodCallInliner = new MethodCallInliner(paramInt, paramString2, localMethodVisitor, localMethodNode, instrumentationClassName, maxLocalsTracker.getMaxLocals(paramString1, paramString2), logger);
      return localMethodCallInliner;
    }
    return localMethodVisitor;
  }
  
  private boolean isInstrumentationMethod(String paramString1, String paramString2)
  {
    Iterator localIterator = instrumentationMethods.iterator();
    while (localIterator.hasNext())
    {
      Method localMethod = (Method)localIterator.next();
      if ((localMethod.getName().equals(paramString1)) && (Type.getMethodDescriptor(localMethod).equals(paramString2))) {
        return true;
      }
    }
    return false;
  }
  
  private MethodNode findTargetMethodNode(String paramString1, String paramString2)
  {
    Iterator localIterator = targetClassNode.methods.iterator();
    while (localIterator.hasNext())
    {
      MethodNode localMethodNode = (MethodNode)localIterator.next();
      if ((desc.equals(paramString2)) && (name.equals(paramString1))) {
        return localMethodNode;
      }
    }
    throw new IllegalArgumentException("could not find MethodNode for " + paramString1 + paramString2);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\instrumentation\Inliner.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package jdk.internal.instrumentation;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import jdk.internal.org.objectweb.asm.ClassVisitor;
import jdk.internal.org.objectweb.asm.Handle;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.Type;
import jdk.internal.org.objectweb.asm.commons.RemappingMethodAdapter;
import jdk.internal.org.objectweb.asm.commons.SimpleRemapper;
import jdk.internal.org.objectweb.asm.tree.ClassNode;
import jdk.internal.org.objectweb.asm.tree.InsnList;
import jdk.internal.org.objectweb.asm.tree.MethodNode;

final class MethodMergeAdapter
  extends ClassVisitor
{
  private final ClassNode cn;
  private final List<Method> methodFilter;
  private final Map<String, String> typeMap;
  private final Logger logger;
  
  public MethodMergeAdapter(ClassVisitor paramClassVisitor, ClassNode paramClassNode, List<Method> paramList, TypeMapping[] paramArrayOfTypeMapping, Logger paramLogger)
  {
    super(327680, paramClassVisitor);
    cn = paramClassNode;
    methodFilter = paramList;
    logger = paramLogger;
    typeMap = new HashMap();
    for (TypeMapping localTypeMapping : paramArrayOfTypeMapping) {
      typeMap.put(localTypeMapping.from().replace('.', '/'), localTypeMapping.to().replace('.', '/'));
    }
  }
  
  public void visit(int paramInt1, int paramInt2, String paramString1, String paramString2, String paramString3, String[] paramArrayOfString)
  {
    super.visit(paramInt1, paramInt2, paramString1, paramString2, paramString3, paramArrayOfString);
    typeMap.put(cn.name, paramString1);
  }
  
  public MethodVisitor visitMethod(int paramInt, String paramString1, String paramString2, String paramString3, String[] paramArrayOfString)
  {
    if (methodInFilter(paramString1, paramString2))
    {
      logger.trace("Deleting " + paramString1 + paramString2);
      return null;
    }
    return super.visitMethod(paramInt, paramString1, paramString2, paramString3, paramArrayOfString);
  }
  
  public void visitEnd()
  {
    SimpleRemapper localSimpleRemapper = new SimpleRemapper(typeMap);
    LinkedList localLinkedList = new LinkedList();
    Object localObject1 = cn.methods.iterator();
    Object localObject2;
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (MethodNode)((Iterator)localObject1).next();
      if (methodInFilter(name, desc)) {
        localLinkedList.add(localObject2);
      }
    }
    while (!localLinkedList.isEmpty())
    {
      localObject1 = (MethodNode)localLinkedList.remove(0);
      logger.trace("Copying method: " + name + desc);
      logger.trace("   with mapper: " + typeMap);
      localObject2 = (String[])exceptions.toArray(new String[0]);
      MethodVisitor localMethodVisitor = cv.visitMethod(access, name, desc, signature, (String[])localObject2);
      instructions.resetLabels();
      ((MethodNode)localObject1).accept(new RemappingMethodAdapter(access, desc, localMethodVisitor, localSimpleRemapper));
      findMethodsReferencedByInvokeDynamic((MethodNode)localObject1, localLinkedList);
    }
    super.visitEnd();
  }
  
  private void findMethodsReferencedByInvokeDynamic(final MethodNode paramMethodNode, final List<MethodNode> paramList)
  {
    paramMethodNode.accept(new MethodVisitor(327680)
    {
      public void visitInvokeDynamicInsn(String paramAnonymousString1, String paramAnonymousString2, Handle paramAnonymousHandle, Object... paramAnonymousVarArgs)
      {
        for (Object localObject : paramAnonymousVarArgs) {
          if ((localObject instanceof Handle))
          {
            Handle localHandle = (Handle)localObject;
            MethodNode localMethodNode = MethodMergeAdapter.findMethod(cn, localHandle);
            if (localMethodNode == null) {
              logger.error("Could not find method " + localHandle.getName() + localHandle.getDesc() + " referenced from an invokedynamic in " + paramMethodNodename + paramMethodNodedesc + " while processing class " + cn.name);
            }
            logger.trace("Adding method referenced from invokedynamic " + name + desc + " to the list of methods to be copied from " + cn.name);
            paramList.add(localMethodNode);
          }
        }
      }
    });
  }
  
  private static MethodNode findMethod(ClassNode paramClassNode, Handle paramHandle)
  {
    Iterator localIterator = methods.iterator();
    while (localIterator.hasNext())
    {
      MethodNode localMethodNode = (MethodNode)localIterator.next();
      if ((name.equals(paramHandle.getName())) && (desc.equals(paramHandle.getDesc()))) {
        return localMethodNode;
      }
    }
    return null;
  }
  
  private boolean methodInFilter(String paramString1, String paramString2)
  {
    Iterator localIterator = methodFilter.iterator();
    while (localIterator.hasNext())
    {
      Method localMethod = (Method)localIterator.next();
      if ((localMethod.getName().equals(paramString1)) && (Type.getMethodDescriptor(localMethod).equals(paramString2))) {
        return true;
      }
    }
    return false;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\instrumentation\MethodMergeAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
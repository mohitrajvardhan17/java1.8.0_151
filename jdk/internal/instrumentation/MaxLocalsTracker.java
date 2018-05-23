package jdk.internal.instrumentation;

import java.util.HashMap;
import jdk.internal.org.objectweb.asm.ClassVisitor;
import jdk.internal.org.objectweb.asm.MethodVisitor;

final class MaxLocalsTracker
  extends ClassVisitor
{
  private final HashMap<String, Integer> maxLocalsMap = new HashMap();
  
  public MaxLocalsTracker()
  {
    super(327680);
  }
  
  public MethodVisitor visitMethod(int paramInt, String paramString1, String paramString2, String paramString3, String[] paramArrayOfString)
  {
    return new MaxLocalsMethodVisitor(key(paramString1, paramString2));
  }
  
  public int getMaxLocals(String paramString1, String paramString2)
  {
    Integer localInteger = (Integer)maxLocalsMap.get(key(paramString1, paramString2));
    if (localInteger == null) {
      throw new IllegalArgumentException("No maxLocals could be found for " + paramString1 + paramString2);
    }
    return localInteger.intValue();
  }
  
  private static String key(String paramString1, String paramString2)
  {
    return paramString1 + paramString2;
  }
  
  private final class MaxLocalsMethodVisitor
    extends MethodVisitor
  {
    private String key;
    
    public MaxLocalsMethodVisitor(String paramString)
    {
      super();
      key = paramString;
    }
    
    public void visitMaxs(int paramInt1, int paramInt2)
    {
      maxLocalsMap.put(key, Integer.valueOf(paramInt2));
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\instrumentation\MaxLocalsTracker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package com.sun.org.glassfish.gmbal.util;

import java.lang.reflect.Constructor;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GenericConstructor<T>
{
  private final Object lock = new Object();
  private String typeName;
  private Class<T> resultType;
  private Class<?> type;
  private Class<?>[] signature;
  private Constructor constructor;
  
  public GenericConstructor(Class<T> paramClass, String paramString, Class<?>... paramVarArgs)
  {
    resultType = paramClass;
    typeName = paramString;
    signature = ((Class[])paramVarArgs.clone());
  }
  
  private void getConstructor()
  {
    synchronized (lock)
    {
      if ((type == null) || (constructor == null)) {
        try
        {
          type = Class.forName(typeName);
          constructor = ((Constructor)AccessController.doPrivileged(new PrivilegedExceptionAction()
          {
            /* Error */
            public Constructor run()
              throws Exception
            {
              // Byte code:
              //   0: aload_0
              //   1: getfield 47	com/sun/org/glassfish/gmbal/util/GenericConstructor$1:this$0	Lcom/sun/org/glassfish/gmbal/util/GenericConstructor;
              //   4: invokestatic 50	com/sun/org/glassfish/gmbal/util/GenericConstructor:access$000	(Lcom/sun/org/glassfish/gmbal/util/GenericConstructor;)Ljava/lang/Object;
              //   7: dup
              //   8: astore_1
              //   9: monitorenter
              //   10: aload_0
              //   11: getfield 47	com/sun/org/glassfish/gmbal/util/GenericConstructor$1:this$0	Lcom/sun/org/glassfish/gmbal/util/GenericConstructor;
              //   14: invokestatic 48	com/sun/org/glassfish/gmbal/util/GenericConstructor:access$200	(Lcom/sun/org/glassfish/gmbal/util/GenericConstructor;)Ljava/lang/Class;
              //   17: aload_0
              //   18: getfield 47	com/sun/org/glassfish/gmbal/util/GenericConstructor$1:this$0	Lcom/sun/org/glassfish/gmbal/util/GenericConstructor;
              //   21: invokestatic 49	com/sun/org/glassfish/gmbal/util/GenericConstructor:access$100	(Lcom/sun/org/glassfish/gmbal/util/GenericConstructor;)[Ljava/lang/Class;
              //   24: invokevirtual 52	java/lang/Class:getDeclaredConstructor	([Ljava/lang/Class;)Ljava/lang/reflect/Constructor;
              //   27: aload_1
              //   28: monitorexit
              //   29: areturn
              //   30: astore_2
              //   31: aload_1
              //   32: monitorexit
              //   33: aload_2
              //   34: athrow
              // Local variable table:
              //   start	length	slot	name	signature
              //   0	35	0	this	1
              //   8	24	1	Ljava/lang/Object;	Object
              //   30	4	2	localObject1	Object
              // Exception table:
              //   from	to	target	type
              //   10	29	30	finally
              //   30	33	30	finally
            }
          }));
        }
        catch (Exception localException)
        {
          Logger.getLogger("com.sun.org.glassfish.gmbal.util").log(Level.FINE, "Failure in getConstructor", localException);
        }
      }
    }
  }
  
  public synchronized T create(Object... paramVarArgs)
  {
    synchronized (lock)
    {
      Object localObject1 = null;
      int i = 0;
      while (i <= 1)
      {
        getConstructor();
        if (constructor == null) {
          break;
        }
        try
        {
          localObject1 = resultType.cast(constructor.newInstance(paramVarArgs));
        }
        catch (Exception localException)
        {
          constructor = null;
          Logger.getLogger("com.sun.org.glassfish.gmbal.util").log(Level.WARNING, "Error invoking constructor", localException);
          i++;
        }
      }
      return (T)localObject1;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\glassfish\gmbal\util\GenericConstructor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
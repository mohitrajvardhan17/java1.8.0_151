package sun.tracing;

import com.sun.tracing.Probe;
import java.lang.reflect.Field;

public abstract class ProbeSkeleton
  implements Probe
{
  protected Class<?>[] parameters;
  
  protected ProbeSkeleton(Class<?>[] paramArrayOfClass)
  {
    parameters = paramArrayOfClass;
  }
  
  public abstract boolean isEnabled();
  
  public abstract void uncheckedTrigger(Object[] paramArrayOfObject);
  
  private static boolean isAssignable(Object paramObject, Class<?> paramClass)
  {
    if ((paramObject != null) && (!paramClass.isInstance(paramObject)))
    {
      if (paramClass.isPrimitive()) {
        try
        {
          Field localField = paramObject.getClass().getField("TYPE");
          return paramClass.isAssignableFrom((Class)localField.get(null));
        }
        catch (Exception localException) {}
      }
      return false;
    }
    return true;
  }
  
  public void trigger(Object... paramVarArgs)
  {
    if (paramVarArgs.length != parameters.length) {
      throw new IllegalArgumentException("Wrong number of arguments");
    }
    for (int i = 0; i < parameters.length; i++) {
      if (!isAssignable(paramVarArgs[i], parameters[i])) {
        throw new IllegalArgumentException("Wrong type of argument at position " + i);
      }
    }
    uncheckedTrigger(paramVarArgs);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\tracing\ProbeSkeleton.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
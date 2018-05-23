package java.beans;

import com.sun.beans.finder.PersistenceDelegateFinder;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

public class Encoder
{
  private final PersistenceDelegateFinder finder = new PersistenceDelegateFinder();
  private Map<Object, Expression> bindings = new IdentityHashMap();
  private ExceptionListener exceptionListener;
  boolean executeStatements = true;
  private Map<Object, Object> attributes;
  
  public Encoder() {}
  
  protected void writeObject(Object paramObject)
  {
    if (paramObject == this) {
      return;
    }
    PersistenceDelegate localPersistenceDelegate = getPersistenceDelegate(paramObject == null ? null : paramObject.getClass());
    localPersistenceDelegate.writeObject(paramObject, this);
  }
  
  public void setExceptionListener(ExceptionListener paramExceptionListener)
  {
    exceptionListener = paramExceptionListener;
  }
  
  public ExceptionListener getExceptionListener()
  {
    return exceptionListener != null ? exceptionListener : Statement.defaultExceptionListener;
  }
  
  Object getValue(Expression paramExpression)
  {
    try
    {
      return paramExpression == null ? null : paramExpression.getValue();
    }
    catch (Exception localException)
    {
      getExceptionListener().exceptionThrown(localException);
      throw new RuntimeException("failed to evaluate: " + paramExpression.toString());
    }
  }
  
  public PersistenceDelegate getPersistenceDelegate(Class<?> paramClass)
  {
    PersistenceDelegate localPersistenceDelegate = finder.find(paramClass);
    if (localPersistenceDelegate == null)
    {
      localPersistenceDelegate = MetaData.getPersistenceDelegate(paramClass);
      if (localPersistenceDelegate != null) {
        finder.register(paramClass, localPersistenceDelegate);
      }
    }
    return localPersistenceDelegate;
  }
  
  public void setPersistenceDelegate(Class<?> paramClass, PersistenceDelegate paramPersistenceDelegate)
  {
    finder.register(paramClass, paramPersistenceDelegate);
  }
  
  public Object remove(Object paramObject)
  {
    Expression localExpression = (Expression)bindings.remove(paramObject);
    return getValue(localExpression);
  }
  
  public Object get(Object paramObject)
  {
    if ((paramObject == null) || (paramObject == this) || (paramObject.getClass() == String.class)) {
      return paramObject;
    }
    Expression localExpression = (Expression)bindings.get(paramObject);
    return getValue(localExpression);
  }
  
  private Object writeObject1(Object paramObject)
  {
    Object localObject = get(paramObject);
    if (localObject == null)
    {
      writeObject(paramObject);
      localObject = get(paramObject);
    }
    return localObject;
  }
  
  private Statement cloneStatement(Statement paramStatement)
  {
    Object localObject1 = paramStatement.getTarget();
    Object localObject2 = writeObject1(localObject1);
    Object[] arrayOfObject1 = paramStatement.getArguments();
    Object[] arrayOfObject2 = new Object[arrayOfObject1.length];
    for (int i = 0; i < arrayOfObject1.length; i++) {
      arrayOfObject2[i] = writeObject1(arrayOfObject1[i]);
    }
    Expression localExpression = Statement.class.equals(paramStatement.getClass()) ? new Statement(localObject2, paramStatement.getMethodName(), arrayOfObject2) : new Expression(localObject2, paramStatement.getMethodName(), arrayOfObject2);
    loader = loader;
    return localExpression;
  }
  
  public void writeStatement(Statement paramStatement)
  {
    Statement localStatement = cloneStatement(paramStatement);
    if ((paramStatement.getTarget() != this) && (executeStatements)) {
      try
      {
        localStatement.execute();
      }
      catch (Exception localException)
      {
        getExceptionListener().exceptionThrown(new Exception("Encoder: discarding statement " + localStatement, localException));
      }
    }
  }
  
  public void writeExpression(Expression paramExpression)
  {
    Object localObject = getValue(paramExpression);
    if (get(localObject) != null) {
      return;
    }
    bindings.put(localObject, (Expression)cloneStatement(paramExpression));
    writeObject(localObject);
  }
  
  void clear()
  {
    bindings.clear();
  }
  
  void setAttribute(Object paramObject1, Object paramObject2)
  {
    if (attributes == null) {
      attributes = new HashMap();
    }
    attributes.put(paramObject1, paramObject2);
  }
  
  Object getAttribute(Object paramObject)
  {
    if (attributes == null) {
      return null;
    }
    return attributes.get(paramObject);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\beans\Encoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
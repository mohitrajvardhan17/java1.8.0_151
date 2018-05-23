package java.beans;

public abstract class PersistenceDelegate
{
  public PersistenceDelegate() {}
  
  public void writeObject(Object paramObject, Encoder paramEncoder)
  {
    Object localObject = paramEncoder.get(paramObject);
    if (!mutatesTo(paramObject, localObject))
    {
      paramEncoder.remove(paramObject);
      paramEncoder.writeExpression(instantiate(paramObject, paramEncoder));
    }
    else
    {
      initialize(paramObject.getClass(), paramObject, localObject, paramEncoder);
    }
  }
  
  protected boolean mutatesTo(Object paramObject1, Object paramObject2)
  {
    return (paramObject2 != null) && (paramObject1 != null) && (paramObject1.getClass() == paramObject2.getClass());
  }
  
  protected abstract Expression instantiate(Object paramObject, Encoder paramEncoder);
  
  protected void initialize(Class<?> paramClass, Object paramObject1, Object paramObject2, Encoder paramEncoder)
  {
    Class localClass = paramClass.getSuperclass();
    PersistenceDelegate localPersistenceDelegate = paramEncoder.getPersistenceDelegate(localClass);
    localPersistenceDelegate.initialize(localClass, paramObject1, paramObject2, paramEncoder);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\beans\PersistenceDelegate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
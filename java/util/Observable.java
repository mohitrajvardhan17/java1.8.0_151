package java.util;

public class Observable
{
  private boolean changed = false;
  private Vector<Observer> obs = new Vector();
  
  public Observable() {}
  
  public synchronized void addObserver(Observer paramObserver)
  {
    if (paramObserver == null) {
      throw new NullPointerException();
    }
    if (!obs.contains(paramObserver)) {
      obs.addElement(paramObserver);
    }
  }
  
  public synchronized void deleteObserver(Observer paramObserver)
  {
    obs.removeElement(paramObserver);
  }
  
  public void notifyObservers()
  {
    notifyObservers(null);
  }
  
  public void notifyObservers(Object paramObject)
  {
    Object[] arrayOfObject;
    synchronized (this)
    {
      if (!changed) {
        return;
      }
      arrayOfObject = obs.toArray();
      clearChanged();
    }
    for (int i = arrayOfObject.length - 1; i >= 0; i--) {
      ((Observer)arrayOfObject[i]).update(this, paramObject);
    }
  }
  
  public synchronized void deleteObservers()
  {
    obs.removeAllElements();
  }
  
  protected synchronized void setChanged()
  {
    changed = true;
  }
  
  protected synchronized void clearChanged()
  {
    changed = false;
  }
  
  public synchronized boolean hasChanged()
  {
    return changed;
  }
  
  public synchronized int countObservers()
  {
    return obs.size();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\Observable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
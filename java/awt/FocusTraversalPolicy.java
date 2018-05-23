package java.awt;

public abstract class FocusTraversalPolicy
{
  public FocusTraversalPolicy() {}
  
  public abstract Component getComponentAfter(Container paramContainer, Component paramComponent);
  
  public abstract Component getComponentBefore(Container paramContainer, Component paramComponent);
  
  public abstract Component getFirstComponent(Container paramContainer);
  
  public abstract Component getLastComponent(Container paramContainer);
  
  public abstract Component getDefaultComponent(Container paramContainer);
  
  public Component getInitialComponent(Window paramWindow)
  {
    if (paramWindow == null) {
      throw new IllegalArgumentException("window cannot be equal to null.");
    }
    Object localObject = getDefaultComponent(paramWindow);
    if ((localObject == null) && (paramWindow.isFocusableWindow())) {
      localObject = paramWindow;
    }
    return (Component)localObject;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\FocusTraversalPolicy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
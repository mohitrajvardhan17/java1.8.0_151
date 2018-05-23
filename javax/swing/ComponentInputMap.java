package javax.swing;

public class ComponentInputMap
  extends InputMap
{
  private JComponent component;
  
  public ComponentInputMap(JComponent paramJComponent)
  {
    component = paramJComponent;
    if (paramJComponent == null) {
      throw new IllegalArgumentException("ComponentInputMaps must be associated with a non-null JComponent");
    }
  }
  
  public void setParent(InputMap paramInputMap)
  {
    if (getParent() == paramInputMap) {
      return;
    }
    if ((paramInputMap != null) && ((!(paramInputMap instanceof ComponentInputMap)) || (((ComponentInputMap)paramInputMap).getComponent() != getComponent()))) {
      throw new IllegalArgumentException("ComponentInputMaps must have a parent ComponentInputMap associated with the same component");
    }
    super.setParent(paramInputMap);
    getComponent().componentInputMapChanged(this);
  }
  
  public JComponent getComponent()
  {
    return component;
  }
  
  public void put(KeyStroke paramKeyStroke, Object paramObject)
  {
    super.put(paramKeyStroke, paramObject);
    if (getComponent() != null) {
      getComponent().componentInputMapChanged(this);
    }
  }
  
  public void remove(KeyStroke paramKeyStroke)
  {
    super.remove(paramKeyStroke);
    if (getComponent() != null) {
      getComponent().componentInputMapChanged(this);
    }
  }
  
  public void clear()
  {
    int i = size();
    super.clear();
    if ((i > 0) && (getComponent() != null)) {
      getComponent().componentInputMapChanged(this);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\ComponentInputMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
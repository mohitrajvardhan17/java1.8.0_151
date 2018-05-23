package java.awt;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectInputStream.GetField;
import java.io.ObjectOutputStream;
import java.io.ObjectOutputStream.PutField;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class CardLayout
  implements LayoutManager2, Serializable
{
  private static final long serialVersionUID = -4328196481005934313L;
  Vector<Card> vector = new Vector();
  int currentCard = 0;
  int hgap;
  int vgap;
  private static final ObjectStreamField[] serialPersistentFields = { new ObjectStreamField("tab", Hashtable.class), new ObjectStreamField("hgap", Integer.TYPE), new ObjectStreamField("vgap", Integer.TYPE), new ObjectStreamField("vector", Vector.class), new ObjectStreamField("currentCard", Integer.TYPE) };
  
  public CardLayout()
  {
    this(0, 0);
  }
  
  public CardLayout(int paramInt1, int paramInt2)
  {
    hgap = paramInt1;
    vgap = paramInt2;
  }
  
  public int getHgap()
  {
    return hgap;
  }
  
  public void setHgap(int paramInt)
  {
    hgap = paramInt;
  }
  
  public int getVgap()
  {
    return vgap;
  }
  
  public void setVgap(int paramInt)
  {
    vgap = paramInt;
  }
  
  public void addLayoutComponent(Component paramComponent, Object paramObject)
  {
    synchronized (paramComponent.getTreeLock())
    {
      if (paramObject == null) {
        paramObject = "";
      }
      if ((paramObject instanceof String)) {
        addLayoutComponent((String)paramObject, paramComponent);
      } else {
        throw new IllegalArgumentException("cannot add to layout: constraint must be a string");
      }
    }
  }
  
  @Deprecated
  public void addLayoutComponent(String paramString, Component paramComponent)
  {
    synchronized (paramComponent.getTreeLock())
    {
      if (!vector.isEmpty()) {
        paramComponent.setVisible(false);
      }
      for (int i = 0; i < vector.size(); i++) {
        if (vector.get(i)).name.equals(paramString))
        {
          vector.get(i)).comp = paramComponent;
          return;
        }
      }
      vector.add(new Card(paramString, paramComponent));
    }
  }
  
  public void removeLayoutComponent(Component paramComponent)
  {
    synchronized (paramComponent.getTreeLock())
    {
      for (int i = 0; i < vector.size(); i++) {
        if (vector.get(i)).comp == paramComponent)
        {
          if ((paramComponent.isVisible()) && (paramComponent.getParent() != null)) {
            next(paramComponent.getParent());
          }
          vector.remove(i);
          if (currentCard <= i) {
            break;
          }
          currentCard -= 1;
          break;
        }
      }
    }
  }
  
  public Dimension preferredLayoutSize(Container paramContainer)
  {
    synchronized (paramContainer.getTreeLock())
    {
      Insets localInsets = paramContainer.getInsets();
      int i = paramContainer.getComponentCount();
      int j = 0;
      int k = 0;
      for (int m = 0; m < i; m++)
      {
        Component localComponent = paramContainer.getComponent(m);
        Dimension localDimension = localComponent.getPreferredSize();
        if (width > j) {
          j = width;
        }
        if (height > k) {
          k = height;
        }
      }
      return new Dimension(left + right + j + hgap * 2, top + bottom + k + vgap * 2);
    }
  }
  
  public Dimension minimumLayoutSize(Container paramContainer)
  {
    synchronized (paramContainer.getTreeLock())
    {
      Insets localInsets = paramContainer.getInsets();
      int i = paramContainer.getComponentCount();
      int j = 0;
      int k = 0;
      for (int m = 0; m < i; m++)
      {
        Component localComponent = paramContainer.getComponent(m);
        Dimension localDimension = localComponent.getMinimumSize();
        if (width > j) {
          j = width;
        }
        if (height > k) {
          k = height;
        }
      }
      return new Dimension(left + right + j + hgap * 2, top + bottom + k + vgap * 2);
    }
  }
  
  public Dimension maximumLayoutSize(Container paramContainer)
  {
    return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
  }
  
  public float getLayoutAlignmentX(Container paramContainer)
  {
    return 0.5F;
  }
  
  public float getLayoutAlignmentY(Container paramContainer)
  {
    return 0.5F;
  }
  
  public void invalidateLayout(Container paramContainer) {}
  
  public void layoutContainer(Container paramContainer)
  {
    synchronized (paramContainer.getTreeLock())
    {
      Insets localInsets = paramContainer.getInsets();
      int i = paramContainer.getComponentCount();
      Component localComponent = null;
      int j = 0;
      for (int k = 0; k < i; k++)
      {
        localComponent = paramContainer.getComponent(k);
        localComponent.setBounds(hgap + left, vgap + top, width - (hgap * 2 + left + right), height - (vgap * 2 + top + bottom));
        if (localComponent.isVisible()) {
          j = 1;
        }
      }
      if ((j == 0) && (i > 0)) {
        paramContainer.getComponent(0).setVisible(true);
      }
    }
  }
  
  void checkLayout(Container paramContainer)
  {
    if (paramContainer.getLayout() != this) {
      throw new IllegalArgumentException("wrong parent for CardLayout");
    }
  }
  
  public void first(Container paramContainer)
  {
    synchronized (paramContainer.getTreeLock())
    {
      checkLayout(paramContainer);
      int i = paramContainer.getComponentCount();
      for (int j = 0; j < i; j++)
      {
        Component localComponent = paramContainer.getComponent(j);
        if (localComponent.isVisible())
        {
          localComponent.setVisible(false);
          break;
        }
      }
      if (i > 0)
      {
        currentCard = 0;
        paramContainer.getComponent(0).setVisible(true);
        paramContainer.validate();
      }
    }
  }
  
  public void next(Container paramContainer)
  {
    synchronized (paramContainer.getTreeLock())
    {
      checkLayout(paramContainer);
      int i = paramContainer.getComponentCount();
      for (int j = 0; j < i; j++)
      {
        Component localComponent = paramContainer.getComponent(j);
        if (localComponent.isVisible())
        {
          localComponent.setVisible(false);
          currentCard = ((j + 1) % i);
          localComponent = paramContainer.getComponent(currentCard);
          localComponent.setVisible(true);
          paramContainer.validate();
          return;
        }
      }
      showDefaultComponent(paramContainer);
    }
  }
  
  public void previous(Container paramContainer)
  {
    synchronized (paramContainer.getTreeLock())
    {
      checkLayout(paramContainer);
      int i = paramContainer.getComponentCount();
      for (int j = 0; j < i; j++)
      {
        Component localComponent = paramContainer.getComponent(j);
        if (localComponent.isVisible())
        {
          localComponent.setVisible(false);
          currentCard = (j > 0 ? j - 1 : i - 1);
          localComponent = paramContainer.getComponent(currentCard);
          localComponent.setVisible(true);
          paramContainer.validate();
          return;
        }
      }
      showDefaultComponent(paramContainer);
    }
  }
  
  void showDefaultComponent(Container paramContainer)
  {
    if (paramContainer.getComponentCount() > 0)
    {
      currentCard = 0;
      paramContainer.getComponent(0).setVisible(true);
      paramContainer.validate();
    }
  }
  
  public void last(Container paramContainer)
  {
    synchronized (paramContainer.getTreeLock())
    {
      checkLayout(paramContainer);
      int i = paramContainer.getComponentCount();
      for (int j = 0; j < i; j++)
      {
        Component localComponent = paramContainer.getComponent(j);
        if (localComponent.isVisible())
        {
          localComponent.setVisible(false);
          break;
        }
      }
      if (i > 0)
      {
        currentCard = (i - 1);
        paramContainer.getComponent(currentCard).setVisible(true);
        paramContainer.validate();
      }
    }
  }
  
  public void show(Container paramContainer, String paramString)
  {
    synchronized (paramContainer.getTreeLock())
    {
      checkLayout(paramContainer);
      Component localComponent = null;
      int i = vector.size();
      Object localObject1;
      for (int j = 0; j < i; j++)
      {
        localObject1 = (Card)vector.get(j);
        if (name.equals(paramString))
        {
          localComponent = comp;
          currentCard = j;
          break;
        }
      }
      if ((localComponent != null) && (!localComponent.isVisible()))
      {
        i = paramContainer.getComponentCount();
        for (j = 0; j < i; j++)
        {
          localObject1 = paramContainer.getComponent(j);
          if (((Component)localObject1).isVisible())
          {
            ((Component)localObject1).setVisible(false);
            break;
          }
        }
        localComponent.setVisible(true);
        paramContainer.validate();
      }
    }
  }
  
  public String toString()
  {
    return getClass().getName() + "[hgap=" + hgap + ",vgap=" + vgap + "]";
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws ClassNotFoundException, IOException
  {
    ObjectInputStream.GetField localGetField = paramObjectInputStream.readFields();
    hgap = localGetField.get("hgap", 0);
    vgap = localGetField.get("vgap", 0);
    if (localGetField.defaulted("vector"))
    {
      Hashtable localHashtable = (Hashtable)localGetField.get("tab", null);
      vector = new Vector();
      if ((localHashtable != null) && (!localHashtable.isEmpty()))
      {
        Enumeration localEnumeration = localHashtable.keys();
        while (localEnumeration.hasMoreElements())
        {
          String str = (String)localEnumeration.nextElement();
          Component localComponent = (Component)localHashtable.get(str);
          vector.add(new Card(str, localComponent));
          if (localComponent.isVisible()) {
            currentCard = (vector.size() - 1);
          }
        }
      }
    }
    else
    {
      vector = ((Vector)localGetField.get("vector", null));
      currentCard = localGetField.get("currentCard", 0);
    }
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    Hashtable localHashtable = new Hashtable();
    int i = vector.size();
    for (int j = 0; j < i; j++)
    {
      Card localCard = (Card)vector.get(j);
      localHashtable.put(name, comp);
    }
    ObjectOutputStream.PutField localPutField = paramObjectOutputStream.putFields();
    localPutField.put("hgap", hgap);
    localPutField.put("vgap", vgap);
    localPutField.put("vector", vector);
    localPutField.put("currentCard", currentCard);
    localPutField.put("tab", localHashtable);
    paramObjectOutputStream.writeFields();
  }
  
  class Card
    implements Serializable
  {
    static final long serialVersionUID = 6640330810709497518L;
    public String name;
    public Component comp;
    
    public Card(String paramString, Component paramComponent)
    {
      name = paramString;
      comp = paramComponent;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\CardLayout.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package javax.accessibility;

import java.awt.IllegalComponentStateException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Locale;
import sun.awt.AWTAccessor;
import sun.awt.AWTAccessor.AccessibleContextAccessor;
import sun.awt.AppContext;

public abstract class AccessibleContext
{
  private volatile AppContext targetAppContext;
  public static final String ACCESSIBLE_NAME_PROPERTY = "AccessibleName";
  public static final String ACCESSIBLE_DESCRIPTION_PROPERTY = "AccessibleDescription";
  public static final String ACCESSIBLE_STATE_PROPERTY = "AccessibleState";
  public static final String ACCESSIBLE_VALUE_PROPERTY = "AccessibleValue";
  public static final String ACCESSIBLE_SELECTION_PROPERTY = "AccessibleSelection";
  public static final String ACCESSIBLE_CARET_PROPERTY = "AccessibleCaret";
  public static final String ACCESSIBLE_VISIBLE_DATA_PROPERTY = "AccessibleVisibleData";
  public static final String ACCESSIBLE_CHILD_PROPERTY = "AccessibleChild";
  public static final String ACCESSIBLE_ACTIVE_DESCENDANT_PROPERTY = "AccessibleActiveDescendant";
  public static final String ACCESSIBLE_TABLE_CAPTION_CHANGED = "accessibleTableCaptionChanged";
  public static final String ACCESSIBLE_TABLE_SUMMARY_CHANGED = "accessibleTableSummaryChanged";
  public static final String ACCESSIBLE_TABLE_MODEL_CHANGED = "accessibleTableModelChanged";
  public static final String ACCESSIBLE_TABLE_ROW_HEADER_CHANGED = "accessibleTableRowHeaderChanged";
  public static final String ACCESSIBLE_TABLE_ROW_DESCRIPTION_CHANGED = "accessibleTableRowDescriptionChanged";
  public static final String ACCESSIBLE_TABLE_COLUMN_HEADER_CHANGED = "accessibleTableColumnHeaderChanged";
  public static final String ACCESSIBLE_TABLE_COLUMN_DESCRIPTION_CHANGED = "accessibleTableColumnDescriptionChanged";
  public static final String ACCESSIBLE_ACTION_PROPERTY = "accessibleActionProperty";
  public static final String ACCESSIBLE_HYPERTEXT_OFFSET = "AccessibleHypertextOffset";
  public static final String ACCESSIBLE_TEXT_PROPERTY = "AccessibleText";
  public static final String ACCESSIBLE_INVALIDATE_CHILDREN = "accessibleInvalidateChildren";
  public static final String ACCESSIBLE_TEXT_ATTRIBUTES_CHANGED = "accessibleTextAttributesChanged";
  public static final String ACCESSIBLE_COMPONENT_BOUNDS_CHANGED = "accessibleComponentBoundsChanged";
  protected Accessible accessibleParent = null;
  protected String accessibleName = null;
  protected String accessibleDescription = null;
  private PropertyChangeSupport accessibleChangeSupport = null;
  private AccessibleRelationSet relationSet = new AccessibleRelationSet();
  private Object nativeAXResource;
  
  public AccessibleContext() {}
  
  public String getAccessibleName()
  {
    return accessibleName;
  }
  
  public void setAccessibleName(String paramString)
  {
    String str = accessibleName;
    accessibleName = paramString;
    firePropertyChange("AccessibleName", str, accessibleName);
  }
  
  public String getAccessibleDescription()
  {
    return accessibleDescription;
  }
  
  public void setAccessibleDescription(String paramString)
  {
    String str = accessibleDescription;
    accessibleDescription = paramString;
    firePropertyChange("AccessibleDescription", str, accessibleDescription);
  }
  
  public abstract AccessibleRole getAccessibleRole();
  
  public abstract AccessibleStateSet getAccessibleStateSet();
  
  public Accessible getAccessibleParent()
  {
    return accessibleParent;
  }
  
  public void setAccessibleParent(Accessible paramAccessible)
  {
    accessibleParent = paramAccessible;
  }
  
  public abstract int getAccessibleIndexInParent();
  
  public abstract int getAccessibleChildrenCount();
  
  public abstract Accessible getAccessibleChild(int paramInt);
  
  public abstract Locale getLocale()
    throws IllegalComponentStateException;
  
  public void addPropertyChangeListener(PropertyChangeListener paramPropertyChangeListener)
  {
    if (accessibleChangeSupport == null) {
      accessibleChangeSupport = new PropertyChangeSupport(this);
    }
    accessibleChangeSupport.addPropertyChangeListener(paramPropertyChangeListener);
  }
  
  public void removePropertyChangeListener(PropertyChangeListener paramPropertyChangeListener)
  {
    if (accessibleChangeSupport != null) {
      accessibleChangeSupport.removePropertyChangeListener(paramPropertyChangeListener);
    }
  }
  
  public AccessibleAction getAccessibleAction()
  {
    return null;
  }
  
  public AccessibleComponent getAccessibleComponent()
  {
    return null;
  }
  
  public AccessibleSelection getAccessibleSelection()
  {
    return null;
  }
  
  public AccessibleText getAccessibleText()
  {
    return null;
  }
  
  public AccessibleEditableText getAccessibleEditableText()
  {
    return null;
  }
  
  public AccessibleValue getAccessibleValue()
  {
    return null;
  }
  
  public AccessibleIcon[] getAccessibleIcon()
  {
    return null;
  }
  
  public AccessibleRelationSet getAccessibleRelationSet()
  {
    return relationSet;
  }
  
  public AccessibleTable getAccessibleTable()
  {
    return null;
  }
  
  public void firePropertyChange(String paramString, Object paramObject1, Object paramObject2)
  {
    if (accessibleChangeSupport != null) {
      if ((paramObject2 instanceof PropertyChangeEvent))
      {
        PropertyChangeEvent localPropertyChangeEvent = (PropertyChangeEvent)paramObject2;
        accessibleChangeSupport.firePropertyChange(localPropertyChangeEvent);
      }
      else
      {
        accessibleChangeSupport.firePropertyChange(paramString, paramObject1, paramObject2);
      }
    }
  }
  
  static
  {
    AWTAccessor.setAccessibleContextAccessor(new AWTAccessor.AccessibleContextAccessor()
    {
      public void setAppContext(AccessibleContext paramAnonymousAccessibleContext, AppContext paramAnonymousAppContext)
      {
        targetAppContext = paramAnonymousAppContext;
      }
      
      public AppContext getAppContext(AccessibleContext paramAnonymousAccessibleContext)
      {
        return targetAppContext;
      }
    });
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\accessibility\AccessibleContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
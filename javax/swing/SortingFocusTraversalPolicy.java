package javax.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import sun.security.action.GetPropertyAction;
import sun.util.logging.PlatformLogger;
import sun.util.logging.PlatformLogger.Level;

public class SortingFocusTraversalPolicy
  extends InternalFrameFocusTraversalPolicy
{
  private Comparator<? super Component> comparator;
  private boolean implicitDownCycleTraversal = true;
  private PlatformLogger log = PlatformLogger.getLogger("javax.swing.SortingFocusTraversalPolicy");
  private transient Container cachedRoot;
  private transient List<Component> cachedCycle;
  private static final SwingContainerOrderFocusTraversalPolicy fitnessTestPolicy = new SwingContainerOrderFocusTraversalPolicy();
  private final int FORWARD_TRAVERSAL = 0;
  private final int BACKWARD_TRAVERSAL = 1;
  private static final boolean legacySortingFTPEnabled = "true".equals(AccessController.doPrivileged(new GetPropertyAction("swing.legacySortingFTPEnabled", "true")));
  private static final Method legacyMergeSortMethod = legacySortingFTPEnabled ? (Method)AccessController.doPrivileged(new PrivilegedAction()
  {
    public Method run()
    {
      try
      {
        Class localClass = Class.forName("java.util.Arrays");
        Method localMethod = localClass.getDeclaredMethod("legacyMergeSort", new Class[] { Object[].class, Comparator.class });
        localMethod.setAccessible(true);
        return localMethod;
      }
      catch (ClassNotFoundException|NoSuchMethodException localClassNotFoundException) {}
      return null;
    }
  }) : null;
  
  protected SortingFocusTraversalPolicy() {}
  
  public SortingFocusTraversalPolicy(Comparator<? super Component> paramComparator)
  {
    comparator = paramComparator;
  }
  
  private List<Component> getFocusTraversalCycle(Container paramContainer)
  {
    ArrayList localArrayList = new ArrayList();
    enumerateAndSortCycle(paramContainer, localArrayList);
    return localArrayList;
  }
  
  private int getComponentIndex(List<Component> paramList, Component paramComponent)
  {
    int i;
    try
    {
      i = Collections.binarySearch(paramList, paramComponent, comparator);
    }
    catch (ClassCastException localClassCastException)
    {
      if (log.isLoggable(PlatformLogger.Level.FINE)) {
        log.fine("### During the binary search for " + paramComponent + " the exception occurred: ", localClassCastException);
      }
      return -1;
    }
    if (i < 0) {
      i = paramList.indexOf(paramComponent);
    }
    return i;
  }
  
  private void enumerateAndSortCycle(Container paramContainer, List<Component> paramList)
  {
    if (paramContainer.isShowing())
    {
      enumerateCycle(paramContainer, paramList);
      if ((!legacySortingFTPEnabled) || (!legacySort(paramList, comparator))) {
        Collections.sort(paramList, comparator);
      }
    }
  }
  
  private boolean legacySort(List<Component> paramList, Comparator<? super Component> paramComparator)
  {
    if (legacyMergeSortMethod == null) {
      return false;
    }
    Object[] arrayOfObject1 = paramList.toArray();
    try
    {
      legacyMergeSortMethod.invoke(null, new Object[] { arrayOfObject1, paramComparator });
    }
    catch (IllegalAccessException|InvocationTargetException localIllegalAccessException)
    {
      return false;
    }
    ListIterator localListIterator = paramList.listIterator();
    for (Object localObject : arrayOfObject1)
    {
      localListIterator.next();
      localListIterator.set((Component)localObject);
    }
    return true;
  }
  
  private void enumerateCycle(Container paramContainer, List<Component> paramList)
  {
    if ((!paramContainer.isVisible()) || (!paramContainer.isDisplayable())) {
      return;
    }
    paramList.add(paramContainer);
    Component[] arrayOfComponent1 = paramContainer.getComponents();
    for (Component localComponent : arrayOfComponent1)
    {
      if ((localComponent instanceof Container))
      {
        Container localContainer = (Container)localComponent;
        if ((!localContainer.isFocusCycleRoot()) && (!localContainer.isFocusTraversalPolicyProvider()) && ((!(localContainer instanceof JComponent)) || (!((JComponent)localContainer).isManagingFocus())))
        {
          enumerateCycle(localContainer, paramList);
          continue;
        }
      }
      paramList.add(localComponent);
    }
  }
  
  Container getTopmostProvider(Container paramContainer, Component paramComponent)
  {
    Container localContainer1 = paramComponent.getParent();
    Container localContainer2 = null;
    while ((localContainer1 != paramContainer) && (localContainer1 != null))
    {
      if (localContainer1.isFocusTraversalPolicyProvider()) {
        localContainer2 = localContainer1;
      }
      localContainer1 = localContainer1.getParent();
    }
    if (localContainer1 == null) {
      return null;
    }
    return localContainer2;
  }
  
  private Component getComponentDownCycle(Component paramComponent, int paramInt)
  {
    Component localComponent = null;
    if ((paramComponent instanceof Container))
    {
      Container localContainer = (Container)paramComponent;
      if (localContainer.isFocusCycleRoot())
      {
        if (getImplicitDownCycleTraversal())
        {
          localComponent = localContainer.getFocusTraversalPolicy().getDefaultComponent(localContainer);
          if ((localComponent != null) && (log.isLoggable(PlatformLogger.Level.FINE))) {
            log.fine("### Transfered focus down-cycle to " + localComponent + " in the focus cycle root " + localContainer);
          }
        }
        else
        {
          return null;
        }
      }
      else if (localContainer.isFocusTraversalPolicyProvider())
      {
        localComponent = paramInt == 0 ? localContainer.getFocusTraversalPolicy().getDefaultComponent(localContainer) : localContainer.getFocusTraversalPolicy().getLastComponent(localContainer);
        if ((localComponent != null) && (log.isLoggable(PlatformLogger.Level.FINE))) {
          log.fine("### Transfered focus to " + localComponent + " in the FTP provider " + localContainer);
        }
      }
    }
    return localComponent;
  }
  
  public Component getComponentAfter(Container paramContainer, Component paramComponent)
  {
    if (log.isLoggable(PlatformLogger.Level.FINE)) {
      log.fine("### Searching in " + paramContainer + " for component after " + paramComponent);
    }
    if ((paramContainer == null) || (paramComponent == null)) {
      throw new IllegalArgumentException("aContainer and aComponent cannot be null");
    }
    if ((!paramContainer.isFocusTraversalPolicyProvider()) && (!paramContainer.isFocusCycleRoot())) {
      throw new IllegalArgumentException("aContainer should be focus cycle root or focus traversal policy provider");
    }
    if ((paramContainer.isFocusCycleRoot()) && (!paramComponent.isFocusCycleRoot(paramContainer))) {
      throw new IllegalArgumentException("aContainer is not a focus cycle root of aComponent");
    }
    Component localComponent1 = getComponentDownCycle(paramComponent, 0);
    if (localComponent1 != null) {
      return localComponent1;
    }
    Container localContainer = getTopmostProvider(paramContainer, paramComponent);
    if (localContainer != null)
    {
      if (log.isLoggable(PlatformLogger.Level.FINE)) {
        log.fine("### Asking FTP " + localContainer + " for component after " + paramComponent);
      }
      localObject = localContainer.getFocusTraversalPolicy();
      Component localComponent2 = ((FocusTraversalPolicy)localObject).getComponentAfter(localContainer, paramComponent);
      if (localComponent2 != null)
      {
        if (log.isLoggable(PlatformLogger.Level.FINE)) {
          log.fine("### FTP returned " + localComponent2);
        }
        return localComponent2;
      }
      paramComponent = localContainer;
    }
    Object localObject = getFocusTraversalCycle(paramContainer);
    if (log.isLoggable(PlatformLogger.Level.FINE)) {
      log.fine("### Cycle is " + localObject + ", component is " + paramComponent);
    }
    int i = getComponentIndex((List)localObject, paramComponent);
    if (i < 0)
    {
      if (log.isLoggable(PlatformLogger.Level.FINE)) {
        log.fine("### Didn't find component " + paramComponent + " in a cycle " + paramContainer);
      }
      return getFirstComponent(paramContainer);
    }
    i++;
    while (i < ((List)localObject).size())
    {
      localComponent1 = (Component)((List)localObject).get(i);
      if (accept(localComponent1)) {
        return localComponent1;
      }
      if ((localComponent1 = getComponentDownCycle(localComponent1, 0)) != null) {
        return localComponent1;
      }
      i++;
    }
    if (paramContainer.isFocusCycleRoot())
    {
      cachedRoot = paramContainer;
      cachedCycle = ((List)localObject);
      localComponent1 = getFirstComponent(paramContainer);
      cachedRoot = null;
      cachedCycle = null;
      return localComponent1;
    }
    return null;
  }
  
  public Component getComponentBefore(Container paramContainer, Component paramComponent)
  {
    if ((paramContainer == null) || (paramComponent == null)) {
      throw new IllegalArgumentException("aContainer and aComponent cannot be null");
    }
    if ((!paramContainer.isFocusTraversalPolicyProvider()) && (!paramContainer.isFocusCycleRoot())) {
      throw new IllegalArgumentException("aContainer should be focus cycle root or focus traversal policy provider");
    }
    if ((paramContainer.isFocusCycleRoot()) && (!paramComponent.isFocusCycleRoot(paramContainer))) {
      throw new IllegalArgumentException("aContainer is not a focus cycle root of aComponent");
    }
    Container localContainer = getTopmostProvider(paramContainer, paramComponent);
    if (localContainer != null)
    {
      if (log.isLoggable(PlatformLogger.Level.FINE)) {
        log.fine("### Asking FTP " + localContainer + " for component after " + paramComponent);
      }
      localObject = localContainer.getFocusTraversalPolicy();
      Component localComponent1 = ((FocusTraversalPolicy)localObject).getComponentBefore(localContainer, paramComponent);
      if (localComponent1 != null)
      {
        if (log.isLoggable(PlatformLogger.Level.FINE)) {
          log.fine("### FTP returned " + localComponent1);
        }
        return localComponent1;
      }
      paramComponent = localContainer;
      if (accept(paramComponent)) {
        return paramComponent;
      }
    }
    Object localObject = getFocusTraversalCycle(paramContainer);
    if (log.isLoggable(PlatformLogger.Level.FINE)) {
      log.fine("### Cycle is " + localObject + ", component is " + paramComponent);
    }
    int i = getComponentIndex((List)localObject, paramComponent);
    if (i < 0)
    {
      if (log.isLoggable(PlatformLogger.Level.FINE)) {
        log.fine("### Didn't find component " + paramComponent + " in a cycle " + paramContainer);
      }
      return getLastComponent(paramContainer);
    }
    i--;
    Component localComponent2;
    while (i >= 0)
    {
      localComponent2 = (Component)((List)localObject).get(i);
      Component localComponent3;
      if ((localComponent2 != paramContainer) && ((localComponent3 = getComponentDownCycle(localComponent2, 1)) != null)) {
        return localComponent3;
      }
      if (accept(localComponent2)) {
        return localComponent2;
      }
      i--;
    }
    if (paramContainer.isFocusCycleRoot())
    {
      cachedRoot = paramContainer;
      cachedCycle = ((List)localObject);
      localComponent2 = getLastComponent(paramContainer);
      cachedRoot = null;
      cachedCycle = null;
      return localComponent2;
    }
    return null;
  }
  
  public Component getFirstComponent(Container paramContainer)
  {
    if (log.isLoggable(PlatformLogger.Level.FINE)) {
      log.fine("### Getting first component in " + paramContainer);
    }
    if (paramContainer == null) {
      throw new IllegalArgumentException("aContainer cannot be null");
    }
    List localList;
    if (cachedRoot == paramContainer) {
      localList = cachedCycle;
    } else {
      localList = getFocusTraversalCycle(paramContainer);
    }
    if (localList.size() == 0)
    {
      if (log.isLoggable(PlatformLogger.Level.FINE)) {
        log.fine("### Cycle is empty");
      }
      return null;
    }
    if (log.isLoggable(PlatformLogger.Level.FINE)) {
      log.fine("### Cycle is " + localList);
    }
    Iterator localIterator = localList.iterator();
    while (localIterator.hasNext())
    {
      Component localComponent = (Component)localIterator.next();
      if (accept(localComponent)) {
        return localComponent;
      }
      if ((localComponent != paramContainer) && ((localComponent = getComponentDownCycle(localComponent, 0)) != null)) {
        return localComponent;
      }
    }
    return null;
  }
  
  public Component getLastComponent(Container paramContainer)
  {
    if (log.isLoggable(PlatformLogger.Level.FINE)) {
      log.fine("### Getting last component in " + paramContainer);
    }
    if (paramContainer == null) {
      throw new IllegalArgumentException("aContainer cannot be null");
    }
    List localList;
    if (cachedRoot == paramContainer) {
      localList = cachedCycle;
    } else {
      localList = getFocusTraversalCycle(paramContainer);
    }
    if (localList.size() == 0)
    {
      if (log.isLoggable(PlatformLogger.Level.FINE)) {
        log.fine("### Cycle is empty");
      }
      return null;
    }
    if (log.isLoggable(PlatformLogger.Level.FINE)) {
      log.fine("### Cycle is " + localList);
    }
    for (int i = localList.size() - 1; i >= 0; i--)
    {
      Component localComponent1 = (Component)localList.get(i);
      if (accept(localComponent1)) {
        return localComponent1;
      }
      if (((localComponent1 instanceof Container)) && (localComponent1 != paramContainer))
      {
        Container localContainer = (Container)localComponent1;
        if (localContainer.isFocusTraversalPolicyProvider())
        {
          Component localComponent2 = localContainer.getFocusTraversalPolicy().getLastComponent(localContainer);
          if (localComponent2 != null) {
            return localComponent2;
          }
        }
      }
    }
    return null;
  }
  
  public Component getDefaultComponent(Container paramContainer)
  {
    return getFirstComponent(paramContainer);
  }
  
  public void setImplicitDownCycleTraversal(boolean paramBoolean)
  {
    implicitDownCycleTraversal = paramBoolean;
  }
  
  public boolean getImplicitDownCycleTraversal()
  {
    return implicitDownCycleTraversal;
  }
  
  protected void setComparator(Comparator<? super Component> paramComparator)
  {
    comparator = paramComparator;
  }
  
  protected Comparator<? super Component> getComparator()
  {
    return comparator;
  }
  
  protected boolean accept(Component paramComponent)
  {
    return fitnessTestPolicy.accept(paramComponent);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\SortingFocusTraversalPolicy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
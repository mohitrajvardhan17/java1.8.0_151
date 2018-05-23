package javax.swing;

import java.awt.Component;
import java.awt.Component.BaselineResizeBehavior;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager2;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GroupLayout
  implements LayoutManager2
{
  private static final int MIN_SIZE = 0;
  private static final int PREF_SIZE = 1;
  private static final int MAX_SIZE = 2;
  private static final int SPECIFIC_SIZE = 3;
  private static final int UNSET = Integer.MIN_VALUE;
  public static final int DEFAULT_SIZE = -1;
  public static final int PREFERRED_SIZE = -2;
  private boolean autocreatePadding;
  private boolean autocreateContainerPadding;
  private Group horizontalGroup;
  private Group verticalGroup;
  private Map<Component, ComponentInfo> componentInfos;
  private Container host;
  private Set<Spring> tmpParallelSet;
  private boolean springsChanged;
  private boolean isValid;
  private boolean hasPreferredPaddingSprings;
  private LayoutStyle layoutStyle;
  private boolean honorsVisibility;
  
  private static void checkSize(int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean)
  {
    checkResizeType(paramInt1, paramBoolean);
    if ((!paramBoolean) && (paramInt2 < 0)) {
      throw new IllegalArgumentException("Pref must be >= 0");
    }
    if (paramBoolean) {
      checkResizeType(paramInt2, true);
    }
    checkResizeType(paramInt3, paramBoolean);
    checkLessThan(paramInt1, paramInt2);
    checkLessThan(paramInt2, paramInt3);
  }
  
  private static void checkResizeType(int paramInt, boolean paramBoolean)
  {
    if ((paramInt < 0) && (((paramBoolean) && (paramInt != -1) && (paramInt != -2)) || ((!paramBoolean) && (paramInt != -2)))) {
      throw new IllegalArgumentException("Invalid size");
    }
  }
  
  private static void checkLessThan(int paramInt1, int paramInt2)
  {
    if ((paramInt1 >= 0) && (paramInt2 >= 0) && (paramInt1 > paramInt2)) {
      throw new IllegalArgumentException("Following is not met: min<=pref<=max");
    }
  }
  
  public GroupLayout(Container paramContainer)
  {
    if (paramContainer == null) {
      throw new IllegalArgumentException("Container must be non-null");
    }
    honorsVisibility = true;
    host = paramContainer;
    setHorizontalGroup(createParallelGroup(Alignment.LEADING, true));
    setVerticalGroup(createParallelGroup(Alignment.LEADING, true));
    componentInfos = new HashMap();
    tmpParallelSet = new HashSet();
  }
  
  public void setHonorsVisibility(boolean paramBoolean)
  {
    if (honorsVisibility != paramBoolean)
    {
      honorsVisibility = paramBoolean;
      springsChanged = true;
      isValid = false;
      invalidateHost();
    }
  }
  
  public boolean getHonorsVisibility()
  {
    return honorsVisibility;
  }
  
  public void setHonorsVisibility(Component paramComponent, Boolean paramBoolean)
  {
    if (paramComponent == null) {
      throw new IllegalArgumentException("Component must be non-null");
    }
    getComponentInfo(paramComponent).setHonorsVisibility(paramBoolean);
    springsChanged = true;
    isValid = false;
    invalidateHost();
  }
  
  public void setAutoCreateGaps(boolean paramBoolean)
  {
    if (autocreatePadding != paramBoolean)
    {
      autocreatePadding = paramBoolean;
      invalidateHost();
    }
  }
  
  public boolean getAutoCreateGaps()
  {
    return autocreatePadding;
  }
  
  public void setAutoCreateContainerGaps(boolean paramBoolean)
  {
    if (autocreateContainerPadding != paramBoolean)
    {
      autocreateContainerPadding = paramBoolean;
      horizontalGroup = createTopLevelGroup(getHorizontalGroup());
      verticalGroup = createTopLevelGroup(getVerticalGroup());
      invalidateHost();
    }
  }
  
  public boolean getAutoCreateContainerGaps()
  {
    return autocreateContainerPadding;
  }
  
  public void setHorizontalGroup(Group paramGroup)
  {
    if (paramGroup == null) {
      throw new IllegalArgumentException("Group must be non-null");
    }
    horizontalGroup = createTopLevelGroup(paramGroup);
    invalidateHost();
  }
  
  private Group getHorizontalGroup()
  {
    int i = 0;
    if (horizontalGroup.springs.size() > 1) {
      i = 1;
    }
    return (Group)horizontalGroup.springs.get(i);
  }
  
  public void setVerticalGroup(Group paramGroup)
  {
    if (paramGroup == null) {
      throw new IllegalArgumentException("Group must be non-null");
    }
    verticalGroup = createTopLevelGroup(paramGroup);
    invalidateHost();
  }
  
  private Group getVerticalGroup()
  {
    int i = 0;
    if (verticalGroup.springs.size() > 1) {
      i = 1;
    }
    return (Group)verticalGroup.springs.get(i);
  }
  
  private Group createTopLevelGroup(Group paramGroup)
  {
    SequentialGroup localSequentialGroup = createSequentialGroup();
    if (getAutoCreateContainerGaps())
    {
      localSequentialGroup.addSpring(new ContainerAutoPreferredGapSpring());
      localSequentialGroup.addGroup(paramGroup);
      localSequentialGroup.addSpring(new ContainerAutoPreferredGapSpring());
    }
    else
    {
      localSequentialGroup.addGroup(paramGroup);
    }
    return localSequentialGroup;
  }
  
  public SequentialGroup createSequentialGroup()
  {
    return new SequentialGroup();
  }
  
  public ParallelGroup createParallelGroup()
  {
    return createParallelGroup(Alignment.LEADING);
  }
  
  public ParallelGroup createParallelGroup(Alignment paramAlignment)
  {
    return createParallelGroup(paramAlignment, true);
  }
  
  public ParallelGroup createParallelGroup(Alignment paramAlignment, boolean paramBoolean)
  {
    if (paramAlignment == null) {
      throw new IllegalArgumentException("alignment must be non null");
    }
    if (paramAlignment == Alignment.BASELINE) {
      return new BaselineGroup(paramBoolean);
    }
    return new ParallelGroup(paramAlignment, paramBoolean);
  }
  
  public ParallelGroup createBaselineGroup(boolean paramBoolean1, boolean paramBoolean2)
  {
    return new BaselineGroup(paramBoolean1, paramBoolean2);
  }
  
  public void linkSize(Component... paramVarArgs)
  {
    linkSize(0, paramVarArgs);
    linkSize(1, paramVarArgs);
  }
  
  public void linkSize(int paramInt, Component... paramVarArgs)
  {
    if (paramVarArgs == null) {
      throw new IllegalArgumentException("Components must be non-null");
    }
    for (int i = paramVarArgs.length - 1; i >= 0; i--)
    {
      localObject = paramVarArgs[i];
      if (paramVarArgs[i] == null) {
        throw new IllegalArgumentException("Components must be non-null");
      }
      getComponentInfo((Component)localObject);
    }
    if (paramInt == 0) {
      i = 0;
    } else if (paramInt == 1) {
      i = 1;
    } else {
      throw new IllegalArgumentException("Axis must be one of SwingConstants.HORIZONTAL or SwingConstants.VERTICAL");
    }
    Object localObject = getComponentInfo(paramVarArgs[(paramVarArgs.length - 1)]).getLinkInfo(i);
    for (int j = paramVarArgs.length - 2; j >= 0; j--) {
      ((LinkInfo)localObject).add(getComponentInfo(paramVarArgs[j]));
    }
    invalidateHost();
  }
  
  public void replace(Component paramComponent1, Component paramComponent2)
  {
    if ((paramComponent1 == null) || (paramComponent2 == null)) {
      throw new IllegalArgumentException("Components must be non-null");
    }
    if (springsChanged)
    {
      registerComponents(horizontalGroup, 0);
      registerComponents(verticalGroup, 1);
    }
    ComponentInfo localComponentInfo = (ComponentInfo)componentInfos.remove(paramComponent1);
    if (localComponentInfo == null) {
      throw new IllegalArgumentException("Component must already exist");
    }
    host.remove(paramComponent1);
    if (paramComponent2.getParent() != host) {
      host.add(paramComponent2);
    }
    localComponentInfo.setComponent(paramComponent2);
    componentInfos.put(paramComponent2, localComponentInfo);
    invalidateHost();
  }
  
  public void setLayoutStyle(LayoutStyle paramLayoutStyle)
  {
    layoutStyle = paramLayoutStyle;
    invalidateHost();
  }
  
  public LayoutStyle getLayoutStyle()
  {
    return layoutStyle;
  }
  
  private LayoutStyle getLayoutStyle0()
  {
    LayoutStyle localLayoutStyle = getLayoutStyle();
    if (localLayoutStyle == null) {
      localLayoutStyle = LayoutStyle.getInstance();
    }
    return localLayoutStyle;
  }
  
  private void invalidateHost()
  {
    if ((host instanceof JComponent)) {
      ((JComponent)host).revalidate();
    } else {
      host.invalidate();
    }
    host.repaint();
  }
  
  public void addLayoutComponent(String paramString, Component paramComponent) {}
  
  public void removeLayoutComponent(Component paramComponent)
  {
    ComponentInfo localComponentInfo = (ComponentInfo)componentInfos.remove(paramComponent);
    if (localComponentInfo != null)
    {
      localComponentInfo.dispose();
      springsChanged = true;
      isValid = false;
    }
  }
  
  public Dimension preferredLayoutSize(Container paramContainer)
  {
    checkParent(paramContainer);
    prepare(1);
    return adjustSize(horizontalGroup.getPreferredSize(0), verticalGroup.getPreferredSize(1));
  }
  
  public Dimension minimumLayoutSize(Container paramContainer)
  {
    checkParent(paramContainer);
    prepare(0);
    return adjustSize(horizontalGroup.getMinimumSize(0), verticalGroup.getMinimumSize(1));
  }
  
  public void layoutContainer(Container paramContainer)
  {
    prepare(3);
    Insets localInsets = paramContainer.getInsets();
    int i = paramContainer.getWidth() - left - right;
    int j = paramContainer.getHeight() - top - bottom;
    boolean bool = isLeftToRight();
    if ((getAutoCreateGaps()) || (getAutoCreateContainerGaps()) || (hasPreferredPaddingSprings))
    {
      calculateAutopadding(horizontalGroup, 0, 3, 0, i);
      calculateAutopadding(verticalGroup, 1, 3, 0, j);
    }
    horizontalGroup.setSize(0, 0, i);
    verticalGroup.setSize(1, 0, j);
    Iterator localIterator = componentInfos.values().iterator();
    while (localIterator.hasNext())
    {
      ComponentInfo localComponentInfo = (ComponentInfo)localIterator.next();
      localComponentInfo.setBounds(localInsets, i, bool);
    }
  }
  
  public void addLayoutComponent(Component paramComponent, Object paramObject) {}
  
  public Dimension maximumLayoutSize(Container paramContainer)
  {
    checkParent(paramContainer);
    prepare(2);
    return adjustSize(horizontalGroup.getMaximumSize(0), verticalGroup.getMaximumSize(1));
  }
  
  public float getLayoutAlignmentX(Container paramContainer)
  {
    checkParent(paramContainer);
    return 0.5F;
  }
  
  public float getLayoutAlignmentY(Container paramContainer)
  {
    checkParent(paramContainer);
    return 0.5F;
  }
  
  public void invalidateLayout(Container paramContainer)
  {
    checkParent(paramContainer);
    synchronized (paramContainer.getTreeLock())
    {
      isValid = false;
    }
  }
  
  private void prepare(int paramInt)
  {
    int i = 0;
    if (!isValid)
    {
      isValid = true;
      horizontalGroup.setSize(0, Integer.MIN_VALUE, Integer.MIN_VALUE);
      verticalGroup.setSize(1, Integer.MIN_VALUE, Integer.MIN_VALUE);
      Iterator localIterator = componentInfos.values().iterator();
      while (localIterator.hasNext())
      {
        ComponentInfo localComponentInfo = (ComponentInfo)localIterator.next();
        if (localComponentInfo.updateVisibility()) {
          i = 1;
        }
        localComponentInfo.clearCachedSize();
      }
    }
    if (springsChanged)
    {
      registerComponents(horizontalGroup, 0);
      registerComponents(verticalGroup, 1);
    }
    if ((springsChanged) || (i != 0))
    {
      checkComponents();
      horizontalGroup.removeAutopadding();
      verticalGroup.removeAutopadding();
      if (getAutoCreateGaps()) {
        insertAutopadding(true);
      } else if ((hasPreferredPaddingSprings) || (getAutoCreateContainerGaps())) {
        insertAutopadding(false);
      }
      springsChanged = false;
    }
    if ((paramInt != 3) && ((getAutoCreateGaps()) || (getAutoCreateContainerGaps()) || (hasPreferredPaddingSprings)))
    {
      calculateAutopadding(horizontalGroup, 0, paramInt, 0, 0);
      calculateAutopadding(verticalGroup, 1, paramInt, 0, 0);
    }
  }
  
  private void calculateAutopadding(Group paramGroup, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paramGroup.unsetAutopadding();
    switch (paramInt2)
    {
    case 0: 
      paramInt4 = paramGroup.getMinimumSize(paramInt1);
      break;
    case 1: 
      paramInt4 = paramGroup.getPreferredSize(paramInt1);
      break;
    case 2: 
      paramInt4 = paramGroup.getMaximumSize(paramInt1);
      break;
    }
    paramGroup.setSize(paramInt1, paramInt3, paramInt4);
    paramGroup.calculateAutopadding(paramInt1);
  }
  
  private void checkComponents()
  {
    Iterator localIterator = componentInfos.values().iterator();
    while (localIterator.hasNext())
    {
      ComponentInfo localComponentInfo = (ComponentInfo)localIterator.next();
      if (horizontalSpring == null) {
        throw new IllegalStateException(component + " is not attached to a horizontal group");
      }
      if (verticalSpring == null) {
        throw new IllegalStateException(component + " is not attached to a vertical group");
      }
    }
  }
  
  private void registerComponents(Group paramGroup, int paramInt)
  {
    List localList = springs;
    for (int i = localList.size() - 1; i >= 0; i--)
    {
      Spring localSpring = (Spring)localList.get(i);
      if ((localSpring instanceof ComponentSpring)) {
        ((ComponentSpring)localSpring).installIfNecessary(paramInt);
      } else if ((localSpring instanceof Group)) {
        registerComponents((Group)localSpring, paramInt);
      }
    }
  }
  
  private Dimension adjustSize(int paramInt1, int paramInt2)
  {
    Insets localInsets = host.getInsets();
    return new Dimension(paramInt1 + left + right, paramInt2 + top + bottom);
  }
  
  private void checkParent(Container paramContainer)
  {
    if (paramContainer != host) {
      throw new IllegalArgumentException("GroupLayout can only be used with one Container at a time");
    }
  }
  
  private ComponentInfo getComponentInfo(Component paramComponent)
  {
    ComponentInfo localComponentInfo = (ComponentInfo)componentInfos.get(paramComponent);
    if (localComponentInfo == null)
    {
      localComponentInfo = new ComponentInfo(paramComponent);
      componentInfos.put(paramComponent, localComponentInfo);
      if (paramComponent.getParent() != host) {
        host.add(paramComponent);
      }
    }
    return localComponentInfo;
  }
  
  private void insertAutopadding(boolean paramBoolean)
  {
    horizontalGroup.insertAutopadding(0, new ArrayList(1), new ArrayList(1), new ArrayList(1), new ArrayList(1), paramBoolean);
    verticalGroup.insertAutopadding(1, new ArrayList(1), new ArrayList(1), new ArrayList(1), new ArrayList(1), paramBoolean);
  }
  
  private boolean areParallelSiblings(Component paramComponent1, Component paramComponent2, int paramInt)
  {
    ComponentInfo localComponentInfo1 = getComponentInfo(paramComponent1);
    ComponentInfo localComponentInfo2 = getComponentInfo(paramComponent2);
    ComponentSpring localComponentSpring1;
    ComponentSpring localComponentSpring2;
    if (paramInt == 0)
    {
      localComponentSpring1 = horizontalSpring;
      localComponentSpring2 = horizontalSpring;
    }
    else
    {
      localComponentSpring1 = verticalSpring;
      localComponentSpring2 = verticalSpring;
    }
    Set localSet = tmpParallelSet;
    localSet.clear();
    for (Spring localSpring = localComponentSpring1.getParent(); localSpring != null; localSpring = localSpring.getParent()) {
      localSet.add(localSpring);
    }
    for (localSpring = localComponentSpring2.getParent(); localSpring != null; localSpring = localSpring.getParent()) {
      if (localSet.contains(localSpring))
      {
        localSet.clear();
        while (localSpring != null)
        {
          if ((localSpring instanceof ParallelGroup)) {
            return true;
          }
          localSpring = localSpring.getParent();
        }
        return false;
      }
    }
    localSet.clear();
    return false;
  }
  
  private boolean isLeftToRight()
  {
    return host.getComponentOrientation().isLeftToRight();
  }
  
  public String toString()
  {
    if (springsChanged)
    {
      registerComponents(horizontalGroup, 0);
      registerComponents(verticalGroup, 1);
    }
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append("HORIZONTAL\n");
    createSpringDescription(localStringBuffer, horizontalGroup, "  ", 0);
    localStringBuffer.append("\nVERTICAL\n");
    createSpringDescription(localStringBuffer, verticalGroup, "  ", 1);
    return localStringBuffer.toString();
  }
  
  private void createSpringDescription(StringBuffer paramStringBuffer, Spring paramSpring, String paramString, int paramInt)
  {
    String str1 = "";
    String str2 = "";
    Object localObject;
    if ((paramSpring instanceof ComponentSpring))
    {
      localObject = (ComponentSpring)paramSpring;
      str1 = Integer.toString(((ComponentSpring)localObject).getOrigin()) + " ";
      String str3 = ((ComponentSpring)localObject).getComponent().getName();
      if (str3 != null) {
        str1 = "name=" + str3 + ", ";
      }
    }
    if ((paramSpring instanceof AutoPreferredGapSpring))
    {
      localObject = (AutoPreferredGapSpring)paramSpring;
      str2 = ", userCreated=" + ((AutoPreferredGapSpring)localObject).getUserCreated() + ", matches=" + ((AutoPreferredGapSpring)localObject).getMatchDescription();
    }
    paramStringBuffer.append(paramString + paramSpring.getClass().getName() + " " + Integer.toHexString(paramSpring.hashCode()) + " " + str1 + ", size=" + paramSpring.getSize() + ", alignment=" + paramSpring.getAlignment() + " prefs=[" + paramSpring.getMinimumSize(paramInt) + " " + paramSpring.getPreferredSize(paramInt) + " " + paramSpring.getMaximumSize(paramInt) + str2 + "]\n");
    if ((paramSpring instanceof Group))
    {
      localObject = springs;
      paramString = paramString + "  ";
      for (int i = 0; i < ((List)localObject).size(); i++) {
        createSpringDescription(paramStringBuffer, (Spring)((List)localObject).get(i), paramString, paramInt);
      }
    }
  }
  
  public static enum Alignment
  {
    LEADING,  TRAILING,  CENTER,  BASELINE;
    
    private Alignment() {}
  }
  
  private static final class AutoPreferredGapMatch
  {
    public final GroupLayout.ComponentSpring source;
    public final GroupLayout.ComponentSpring target;
    
    AutoPreferredGapMatch(GroupLayout.ComponentSpring paramComponentSpring1, GroupLayout.ComponentSpring paramComponentSpring2)
    {
      source = paramComponentSpring1;
      target = paramComponentSpring2;
    }
    
    private String toString(GroupLayout.ComponentSpring paramComponentSpring)
    {
      return paramComponentSpring.getComponent().getName();
    }
    
    public String toString()
    {
      return "[" + toString(source) + "-" + toString(target) + "]";
    }
  }
  
  private class AutoPreferredGapSpring
    extends GroupLayout.Spring
  {
    List<GroupLayout.ComponentSpring> sources;
    GroupLayout.ComponentSpring source;
    private List<GroupLayout.AutoPreferredGapMatch> matches;
    int size;
    int lastSize;
    private final int pref;
    private final int max;
    private LayoutStyle.ComponentPlacement type;
    private boolean userCreated;
    
    private AutoPreferredGapSpring()
    {
      super();
      pref = -2;
      max = -2;
      type = LayoutStyle.ComponentPlacement.RELATED;
    }
    
    AutoPreferredGapSpring(int paramInt1, int paramInt2)
    {
      super();
      pref = paramInt1;
      max = paramInt2;
    }
    
    AutoPreferredGapSpring(LayoutStyle.ComponentPlacement paramComponentPlacement, int paramInt1, int paramInt2)
    {
      super();
      type = paramComponentPlacement;
      pref = paramInt1;
      max = paramInt2;
      userCreated = true;
    }
    
    public void setSource(GroupLayout.ComponentSpring paramComponentSpring)
    {
      source = paramComponentSpring;
    }
    
    public void setSources(List<GroupLayout.ComponentSpring> paramList)
    {
      sources = new ArrayList(paramList);
    }
    
    public void setUserCreated(boolean paramBoolean)
    {
      userCreated = paramBoolean;
    }
    
    public boolean getUserCreated()
    {
      return userCreated;
    }
    
    void unset()
    {
      lastSize = getSize();
      super.unset();
      size = 0;
    }
    
    public void reset()
    {
      size = 0;
      sources = null;
      source = null;
      matches = null;
    }
    
    public void calculatePadding(int paramInt)
    {
      size = Integer.MIN_VALUE;
      int i = Integer.MIN_VALUE;
      if (matches != null)
      {
        LayoutStyle localLayoutStyle = GroupLayout.this.getLayoutStyle0();
        int j;
        if (paramInt == 0)
        {
          if (GroupLayout.this.isLeftToRight()) {
            j = 3;
          } else {
            j = 7;
          }
        }
        else {
          j = 5;
        }
        for (int k = matches.size() - 1; k >= 0; k--)
        {
          GroupLayout.AutoPreferredGapMatch localAutoPreferredGapMatch = (GroupLayout.AutoPreferredGapMatch)matches.get(k);
          i = Math.max(i, calculatePadding(localLayoutStyle, j, source, target));
        }
      }
      if (size == Integer.MIN_VALUE) {
        size = 0;
      }
      if (i == Integer.MIN_VALUE) {
        i = 0;
      }
      if (lastSize != Integer.MIN_VALUE) {
        size += Math.min(i, lastSize);
      }
    }
    
    private int calculatePadding(LayoutStyle paramLayoutStyle, int paramInt, GroupLayout.ComponentSpring paramComponentSpring1, GroupLayout.ComponentSpring paramComponentSpring2)
    {
      int i = paramComponentSpring2.getOrigin() - (paramComponentSpring1.getOrigin() + paramComponentSpring1.getSize());
      if (i >= 0)
      {
        int j;
        if (((paramComponentSpring1.getComponent() instanceof JComponent)) && ((paramComponentSpring2.getComponent() instanceof JComponent))) {
          j = paramLayoutStyle.getPreferredGap((JComponent)paramComponentSpring1.getComponent(), (JComponent)paramComponentSpring2.getComponent(), type, paramInt, host);
        } else {
          j = 10;
        }
        if (j > i) {
          size = Math.max(size, j - i);
        }
        return j;
      }
      return 0;
    }
    
    public void addTarget(GroupLayout.ComponentSpring paramComponentSpring, int paramInt)
    {
      int i = paramInt == 0 ? 1 : 0;
      if (source != null)
      {
        if (GroupLayout.this.areParallelSiblings(source.getComponent(), paramComponentSpring.getComponent(), i)) {
          addValidTarget(source, paramComponentSpring);
        }
      }
      else
      {
        Component localComponent = paramComponentSpring.getComponent();
        for (int j = sources.size() - 1; j >= 0; j--)
        {
          GroupLayout.ComponentSpring localComponentSpring = (GroupLayout.ComponentSpring)sources.get(j);
          if (GroupLayout.this.areParallelSiblings(localComponentSpring.getComponent(), localComponent, i)) {
            addValidTarget(localComponentSpring, paramComponentSpring);
          }
        }
      }
    }
    
    private void addValidTarget(GroupLayout.ComponentSpring paramComponentSpring1, GroupLayout.ComponentSpring paramComponentSpring2)
    {
      if (matches == null) {
        matches = new ArrayList(1);
      }
      matches.add(new GroupLayout.AutoPreferredGapMatch(paramComponentSpring1, paramComponentSpring2));
    }
    
    int calculateMinimumSize(int paramInt)
    {
      return size;
    }
    
    int calculatePreferredSize(int paramInt)
    {
      if ((pref == -2) || (pref == -1)) {
        return size;
      }
      return Math.max(size, pref);
    }
    
    int calculateMaximumSize(int paramInt)
    {
      if (max >= 0) {
        return Math.max(getPreferredSize(paramInt), max);
      }
      return size;
    }
    
    String getMatchDescription()
    {
      return matches == null ? "" : matches.toString();
    }
    
    public String toString()
    {
      return super.toString() + getMatchDescription();
    }
    
    boolean willHaveZeroSize(boolean paramBoolean)
    {
      return paramBoolean;
    }
  }
  
  private class BaselineGroup
    extends GroupLayout.ParallelGroup
  {
    private boolean allSpringsHaveBaseline;
    private int prefAscent = prefDescent = -1;
    private int prefDescent;
    private boolean baselineAnchorSet;
    private boolean baselineAnchoredToTop;
    private boolean calcedBaseline = false;
    
    BaselineGroup(boolean paramBoolean)
    {
      super(GroupLayout.Alignment.LEADING, paramBoolean);
    }
    
    BaselineGroup(boolean paramBoolean1, boolean paramBoolean2)
    {
      this(paramBoolean1);
      baselineAnchoredToTop = paramBoolean2;
      baselineAnchorSet = true;
    }
    
    void unset()
    {
      super.unset();
      prefAscent = (prefDescent = -1);
      calcedBaseline = false;
    }
    
    void setValidSize(int paramInt1, int paramInt2, int paramInt3)
    {
      checkAxis(paramInt1);
      if (prefAscent == -1) {
        super.setValidSize(paramInt1, paramInt2, paramInt3);
      } else {
        baselineLayout(paramInt2, paramInt3);
      }
    }
    
    int calculateSize(int paramInt1, int paramInt2)
    {
      checkAxis(paramInt1);
      if (!calcedBaseline) {
        calculateBaselineAndResizeBehavior();
      }
      if (paramInt2 == 0) {
        return calculateMinSize();
      }
      if (paramInt2 == 2) {
        return calculateMaxSize();
      }
      if (allSpringsHaveBaseline) {
        return prefAscent + prefDescent;
      }
      return Math.max(prefAscent + prefDescent, super.calculateSize(paramInt1, paramInt2));
    }
    
    private void calculateBaselineAndResizeBehavior()
    {
      prefAscent = 0;
      prefDescent = 0;
      int i = 0;
      Object localObject = null;
      Iterator localIterator = springs.iterator();
      while (localIterator.hasNext())
      {
        GroupLayout.Spring localSpring = (GroupLayout.Spring)localIterator.next();
        if ((localSpring.getAlignment() == null) || (localSpring.getAlignment() == GroupLayout.Alignment.BASELINE))
        {
          int j = localSpring.getBaseline();
          if (j >= 0)
          {
            if (localSpring.isResizable(1))
            {
              Component.BaselineResizeBehavior localBaselineResizeBehavior = localSpring.getBaselineResizeBehavior();
              if (localObject == null) {
                localObject = localBaselineResizeBehavior;
              } else if (localBaselineResizeBehavior != localObject) {
                localObject = Component.BaselineResizeBehavior.CONSTANT_ASCENT;
              }
            }
            prefAscent = Math.max(prefAscent, j);
            prefDescent = Math.max(prefDescent, localSpring.getPreferredSize(1) - j);
            i++;
          }
        }
      }
      if (!baselineAnchorSet) {
        if (localObject == Component.BaselineResizeBehavior.CONSTANT_DESCENT) {
          baselineAnchoredToTop = false;
        } else {
          baselineAnchoredToTop = true;
        }
      }
      allSpringsHaveBaseline = (i == springs.size());
      calcedBaseline = true;
    }
    
    private int calculateMaxSize()
    {
      int i = prefAscent;
      int j = prefDescent;
      int k = 0;
      Iterator localIterator = springs.iterator();
      while (localIterator.hasNext())
      {
        GroupLayout.Spring localSpring = (GroupLayout.Spring)localIterator.next();
        int n = localSpring.getMaximumSize(1);
        int m;
        if (((localSpring.getAlignment() == null) || (localSpring.getAlignment() == GroupLayout.Alignment.BASELINE)) && ((m = localSpring.getBaseline()) >= 0))
        {
          int i1 = localSpring.getPreferredSize(1);
          if (i1 != n) {
            switch (GroupLayout.1.$SwitchMap$java$awt$Component$BaselineResizeBehavior[localSpring.getBaselineResizeBehavior().ordinal()])
            {
            case 1: 
              if (baselineAnchoredToTop) {
                j = Math.max(j, n - m);
              }
              break;
            case 2: 
              if (!baselineAnchoredToTop) {
                i = Math.max(i, n - i1 + m);
              }
              break;
            }
          }
        }
        else
        {
          k = Math.max(k, n);
        }
      }
      return Math.max(k, i + j);
    }
    
    private int calculateMinSize()
    {
      int i = 0;
      int j = 0;
      int k = 0;
      if (baselineAnchoredToTop) {
        i = prefAscent;
      } else {
        j = prefDescent;
      }
      Iterator localIterator = springs.iterator();
      while (localIterator.hasNext())
      {
        GroupLayout.Spring localSpring = (GroupLayout.Spring)localIterator.next();
        int m = localSpring.getMinimumSize(1);
        int n;
        if (((localSpring.getAlignment() == null) || (localSpring.getAlignment() == GroupLayout.Alignment.BASELINE)) && ((n = localSpring.getBaseline()) >= 0))
        {
          int i1 = localSpring.getPreferredSize(1);
          Component.BaselineResizeBehavior localBaselineResizeBehavior = localSpring.getBaselineResizeBehavior();
          switch (GroupLayout.1.$SwitchMap$java$awt$Component$BaselineResizeBehavior[localBaselineResizeBehavior.ordinal()])
          {
          case 1: 
            if (baselineAnchoredToTop) {
              j = Math.max(m - n, j);
            } else {
              i = Math.max(n, i);
            }
            break;
          case 2: 
            if (!baselineAnchoredToTop) {
              i = Math.max(n - (i1 - m), i);
            } else {
              j = Math.max(i1 - n, j);
            }
            break;
          default: 
            i = Math.max(n, i);
            j = Math.max(i1 - n, j);
          }
        }
        else
        {
          k = Math.max(k, m);
        }
      }
      return Math.max(k, i + j);
    }
    
    private void baselineLayout(int paramInt1, int paramInt2)
    {
      int i;
      int j;
      if (baselineAnchoredToTop)
      {
        i = prefAscent;
        j = paramInt2 - i;
      }
      else
      {
        i = paramInt2 - prefDescent;
        j = prefDescent;
      }
      Iterator localIterator = springs.iterator();
      while (localIterator.hasNext())
      {
        GroupLayout.Spring localSpring = (GroupLayout.Spring)localIterator.next();
        GroupLayout.Alignment localAlignment = localSpring.getAlignment();
        if ((localAlignment == null) || (localAlignment == GroupLayout.Alignment.BASELINE))
        {
          int k = localSpring.getBaseline();
          if (k >= 0)
          {
            int m = localSpring.getMaximumSize(1);
            int n = localSpring.getPreferredSize(1);
            int i1 = n;
            int i2;
            switch (GroupLayout.1.$SwitchMap$java$awt$Component$BaselineResizeBehavior[localSpring.getBaselineResizeBehavior().ordinal()])
            {
            case 1: 
              i2 = paramInt1 + i - k;
              i1 = Math.min(j, m - k) + k;
              break;
            case 2: 
              i1 = Math.min(i, m - n + k) + (n - k);
              i2 = paramInt1 + i + (n - k) - i1;
              break;
            default: 
              i2 = paramInt1 + i - k;
            }
            localSpring.setSize(1, i2, i1);
          }
          else
          {
            setChildSize(localSpring, 1, paramInt1, paramInt2);
          }
        }
        else
        {
          setChildSize(localSpring, 1, paramInt1, paramInt2);
        }
      }
    }
    
    int getBaseline()
    {
      if (springs.size() > 1)
      {
        getPreferredSize(1);
        return prefAscent;
      }
      if (springs.size() == 1) {
        return ((GroupLayout.Spring)springs.get(0)).getBaseline();
      }
      return -1;
    }
    
    Component.BaselineResizeBehavior getBaselineResizeBehavior()
    {
      if (springs.size() == 1) {
        return ((GroupLayout.Spring)springs.get(0)).getBaselineResizeBehavior();
      }
      if (baselineAnchoredToTop) {
        return Component.BaselineResizeBehavior.CONSTANT_ASCENT;
      }
      return Component.BaselineResizeBehavior.CONSTANT_DESCENT;
    }
    
    private void checkAxis(int paramInt)
    {
      if (paramInt == 0) {
        throw new IllegalStateException("Baseline must be used along vertical axis");
      }
    }
  }
  
  private class ComponentInfo
  {
    private Component component;
    GroupLayout.ComponentSpring horizontalSpring;
    GroupLayout.ComponentSpring verticalSpring;
    private GroupLayout.LinkInfo horizontalMaster;
    private GroupLayout.LinkInfo verticalMaster;
    private boolean visible;
    private Boolean honorsVisibility;
    
    ComponentInfo(Component paramComponent)
    {
      component = paramComponent;
      updateVisibility();
    }
    
    public void dispose()
    {
      removeSpring(horizontalSpring);
      horizontalSpring = null;
      removeSpring(verticalSpring);
      verticalSpring = null;
      if (horizontalMaster != null) {
        horizontalMaster.remove(this);
      }
      if (verticalMaster != null) {
        verticalMaster.remove(this);
      }
    }
    
    void setHonorsVisibility(Boolean paramBoolean)
    {
      honorsVisibility = paramBoolean;
    }
    
    private void removeSpring(GroupLayout.Spring paramSpring)
    {
      if (paramSpring != null) {
        getParentsprings.remove(paramSpring);
      }
    }
    
    public boolean isVisible()
    {
      return visible;
    }
    
    boolean updateVisibility()
    {
      boolean bool1;
      if (honorsVisibility == null) {
        bool1 = getHonorsVisibility();
      } else {
        bool1 = honorsVisibility.booleanValue();
      }
      boolean bool2 = bool1 ? component.isVisible() : true;
      if (visible != bool2)
      {
        visible = bool2;
        return true;
      }
      return false;
    }
    
    public void setBounds(Insets paramInsets, int paramInt, boolean paramBoolean)
    {
      int i = horizontalSpring.getOrigin();
      int j = horizontalSpring.getSize();
      int k = verticalSpring.getOrigin();
      int m = verticalSpring.getSize();
      if (!paramBoolean) {
        i = paramInt - i - j;
      }
      component.setBounds(i + left, k + top, j, m);
    }
    
    public void setComponent(Component paramComponent)
    {
      component = paramComponent;
      if (horizontalSpring != null) {
        horizontalSpring.setComponent(paramComponent);
      }
      if (verticalSpring != null) {
        verticalSpring.setComponent(paramComponent);
      }
    }
    
    public Component getComponent()
    {
      return component;
    }
    
    public boolean isLinked(int paramInt)
    {
      if (paramInt == 0) {
        return horizontalMaster != null;
      }
      assert (paramInt == 1);
      return verticalMaster != null;
    }
    
    private void setLinkInfo(int paramInt, GroupLayout.LinkInfo paramLinkInfo)
    {
      if (paramInt == 0)
      {
        horizontalMaster = paramLinkInfo;
      }
      else
      {
        assert (paramInt == 1);
        verticalMaster = paramLinkInfo;
      }
    }
    
    public GroupLayout.LinkInfo getLinkInfo(int paramInt)
    {
      return getLinkInfo(paramInt, true);
    }
    
    private GroupLayout.LinkInfo getLinkInfo(int paramInt, boolean paramBoolean)
    {
      if (paramInt == 0)
      {
        if ((horizontalMaster == null) && (paramBoolean)) {
          new GroupLayout.LinkInfo(0).add(this);
        }
        return horizontalMaster;
      }
      assert (paramInt == 1);
      if ((verticalMaster == null) && (paramBoolean)) {
        new GroupLayout.LinkInfo(1).add(this);
      }
      return verticalMaster;
    }
    
    public void clearCachedSize()
    {
      if (horizontalMaster != null) {
        horizontalMaster.clearCachedSize();
      }
      if (verticalMaster != null) {
        verticalMaster.clearCachedSize();
      }
    }
    
    int getLinkSize(int paramInt1, int paramInt2)
    {
      if (paramInt1 == 0) {
        return horizontalMaster.getSize(paramInt1);
      }
      assert (paramInt1 == 1);
      return verticalMaster.getSize(paramInt1);
    }
  }
  
  private final class ComponentSpring
    extends GroupLayout.Spring
  {
    private Component component;
    private int origin;
    private final int min;
    private final int pref;
    private final int max;
    private int baseline = -1;
    private boolean installed;
    
    private ComponentSpring(Component paramComponent, int paramInt1, int paramInt2, int paramInt3)
    {
      super();
      component = paramComponent;
      if (paramComponent == null) {
        throw new IllegalArgumentException("Component must be non-null");
      }
      GroupLayout.checkSize(paramInt1, paramInt2, paramInt3, true);
      min = paramInt1;
      max = paramInt3;
      pref = paramInt2;
      GroupLayout.this.getComponentInfo(paramComponent);
    }
    
    int calculateMinimumSize(int paramInt)
    {
      if (isLinked(paramInt)) {
        return getLinkSize(paramInt, 0);
      }
      return calculateNonlinkedMinimumSize(paramInt);
    }
    
    int calculatePreferredSize(int paramInt)
    {
      if (isLinked(paramInt)) {
        return getLinkSize(paramInt, 1);
      }
      int i = getMinimumSize(paramInt);
      int j = calculateNonlinkedPreferredSize(paramInt);
      int k = getMaximumSize(paramInt);
      return Math.min(k, Math.max(i, j));
    }
    
    int calculateMaximumSize(int paramInt)
    {
      if (isLinked(paramInt)) {
        return getLinkSize(paramInt, 2);
      }
      return Math.max(getMinimumSize(paramInt), calculateNonlinkedMaximumSize(paramInt));
    }
    
    boolean isVisible()
    {
      return GroupLayout.this.getComponentInfo(getComponent()).isVisible();
    }
    
    int calculateNonlinkedMinimumSize(int paramInt)
    {
      if (!isVisible()) {
        return 0;
      }
      if (min >= 0) {
        return min;
      }
      if (min == -2) {
        return calculateNonlinkedPreferredSize(paramInt);
      }
      assert (min == -1);
      return getSizeAlongAxis(paramInt, component.getMinimumSize());
    }
    
    int calculateNonlinkedPreferredSize(int paramInt)
    {
      if (!isVisible()) {
        return 0;
      }
      if (pref >= 0) {
        return pref;
      }
      assert ((pref == -1) || (pref == -2));
      return getSizeAlongAxis(paramInt, component.getPreferredSize());
    }
    
    int calculateNonlinkedMaximumSize(int paramInt)
    {
      if (!isVisible()) {
        return 0;
      }
      if (max >= 0) {
        return max;
      }
      if (max == -2) {
        return calculateNonlinkedPreferredSize(paramInt);
      }
      assert (max == -1);
      return getSizeAlongAxis(paramInt, component.getMaximumSize());
    }
    
    private int getSizeAlongAxis(int paramInt, Dimension paramDimension)
    {
      return paramInt == 0 ? width : height;
    }
    
    private int getLinkSize(int paramInt1, int paramInt2)
    {
      if (!isVisible()) {
        return 0;
      }
      GroupLayout.ComponentInfo localComponentInfo = GroupLayout.this.getComponentInfo(component);
      return localComponentInfo.getLinkSize(paramInt1, paramInt2);
    }
    
    void setSize(int paramInt1, int paramInt2, int paramInt3)
    {
      super.setSize(paramInt1, paramInt2, paramInt3);
      origin = paramInt2;
      if (paramInt3 == Integer.MIN_VALUE) {
        baseline = -1;
      }
    }
    
    int getOrigin()
    {
      return origin;
    }
    
    void setComponent(Component paramComponent)
    {
      component = paramComponent;
    }
    
    Component getComponent()
    {
      return component;
    }
    
    int getBaseline()
    {
      if (baseline == -1)
      {
        ComponentSpring localComponentSpring = getComponentInfocomponent).horizontalSpring;
        int i = localComponentSpring.getPreferredSize(0);
        int j = getPreferredSize(1);
        if ((i > 0) && (j > 0)) {
          baseline = component.getBaseline(i, j);
        }
      }
      return baseline;
    }
    
    Component.BaselineResizeBehavior getBaselineResizeBehavior()
    {
      return getComponent().getBaselineResizeBehavior();
    }
    
    private boolean isLinked(int paramInt)
    {
      return GroupLayout.this.getComponentInfo(component).isLinked(paramInt);
    }
    
    void installIfNecessary(int paramInt)
    {
      if (!installed)
      {
        installed = true;
        if (paramInt == 0) {
          getComponentInfocomponent).horizontalSpring = this;
        } else {
          getComponentInfocomponent).verticalSpring = this;
        }
      }
    }
    
    boolean willHaveZeroSize(boolean paramBoolean)
    {
      return !isVisible();
    }
  }
  
  private class ContainerAutoPreferredGapSpring
    extends GroupLayout.AutoPreferredGapSpring
  {
    private List<GroupLayout.ComponentSpring> targets;
    
    ContainerAutoPreferredGapSpring()
    {
      super(null);
      setUserCreated(true);
    }
    
    ContainerAutoPreferredGapSpring(int paramInt1, int paramInt2)
    {
      super(paramInt1, paramInt2);
      setUserCreated(true);
    }
    
    public void addTarget(GroupLayout.ComponentSpring paramComponentSpring, int paramInt)
    {
      if (targets == null) {
        targets = new ArrayList(1);
      }
      targets.add(paramComponentSpring);
    }
    
    public void calculatePadding(int paramInt)
    {
      LayoutStyle localLayoutStyle = GroupLayout.this.getLayoutStyle0();
      int i = 0;
      size = 0;
      int j;
      int k;
      GroupLayout.ComponentSpring localComponentSpring;
      if (targets != null)
      {
        if (paramInt == 0)
        {
          if (GroupLayout.this.isLeftToRight()) {
            j = 7;
          } else {
            j = 3;
          }
        }
        else {
          j = 5;
        }
        for (k = targets.size() - 1; k >= 0; k--)
        {
          localComponentSpring = (GroupLayout.ComponentSpring)targets.get(k);
          int m = 10;
          if ((localComponentSpring.getComponent() instanceof JComponent))
          {
            m = localLayoutStyle.getContainerGap((JComponent)localComponentSpring.getComponent(), j, host);
            i = Math.max(m, i);
            m -= localComponentSpring.getOrigin();
          }
          else
          {
            i = Math.max(m, i);
          }
          size = Math.max(size, m);
        }
      }
      else
      {
        if (paramInt == 0)
        {
          if (GroupLayout.this.isLeftToRight()) {
            j = 3;
          } else {
            j = 7;
          }
        }
        else {
          j = 5;
        }
        if (sources != null) {
          for (k = sources.size() - 1; k >= 0; k--)
          {
            localComponentSpring = (GroupLayout.ComponentSpring)sources.get(k);
            i = Math.max(i, updateSize(localLayoutStyle, localComponentSpring, j));
          }
        } else if (source != null) {
          i = updateSize(localLayoutStyle, source, j);
        }
      }
      if (lastSize != Integer.MIN_VALUE) {
        size += Math.min(i, lastSize);
      }
    }
    
    private int updateSize(LayoutStyle paramLayoutStyle, GroupLayout.ComponentSpring paramComponentSpring, int paramInt)
    {
      int i = 10;
      if ((paramComponentSpring.getComponent() instanceof JComponent)) {
        i = paramLayoutStyle.getContainerGap((JComponent)paramComponentSpring.getComponent(), paramInt, host);
      }
      int j = Math.max(0, getParent().getSize() - paramComponentSpring.getSize() - paramComponentSpring.getOrigin());
      size = Math.max(size, i - j);
      return i;
    }
    
    String getMatchDescription()
    {
      if (targets != null) {
        return "leading: " + targets.toString();
      }
      if (sources != null) {
        return "trailing: " + sources.toString();
      }
      return "--";
    }
  }
  
  private class GapSpring
    extends GroupLayout.Spring
  {
    private final int min;
    private final int pref;
    private final int max;
    
    GapSpring(int paramInt1, int paramInt2, int paramInt3)
    {
      super();
      GroupLayout.checkSize(paramInt1, paramInt2, paramInt3, false);
      min = paramInt1;
      pref = paramInt2;
      max = paramInt3;
    }
    
    int calculateMinimumSize(int paramInt)
    {
      if (min == -2) {
        return getPreferredSize(paramInt);
      }
      return min;
    }
    
    int calculatePreferredSize(int paramInt)
    {
      return pref;
    }
    
    int calculateMaximumSize(int paramInt)
    {
      if (max == -2) {
        return getPreferredSize(paramInt);
      }
      return max;
    }
    
    boolean willHaveZeroSize(boolean paramBoolean)
    {
      return false;
    }
  }
  
  public abstract class Group
    extends GroupLayout.Spring
  {
    List<GroupLayout.Spring> springs = new ArrayList();
    
    Group()
    {
      super();
    }
    
    public Group addGroup(Group paramGroup)
    {
      return addSpring(paramGroup);
    }
    
    public Group addComponent(Component paramComponent)
    {
      return addComponent(paramComponent, -1, -1, -1);
    }
    
    public Group addComponent(Component paramComponent, int paramInt1, int paramInt2, int paramInt3)
    {
      return addSpring(new GroupLayout.ComponentSpring(GroupLayout.this, paramComponent, paramInt1, paramInt2, paramInt3, null));
    }
    
    public Group addGap(int paramInt)
    {
      return addGap(paramInt, paramInt, paramInt);
    }
    
    public Group addGap(int paramInt1, int paramInt2, int paramInt3)
    {
      return addSpring(new GroupLayout.GapSpring(GroupLayout.this, paramInt1, paramInt2, paramInt3));
    }
    
    GroupLayout.Spring getSpring(int paramInt)
    {
      return (GroupLayout.Spring)springs.get(paramInt);
    }
    
    int indexOf(GroupLayout.Spring paramSpring)
    {
      return springs.indexOf(paramSpring);
    }
    
    Group addSpring(GroupLayout.Spring paramSpring)
    {
      springs.add(paramSpring);
      paramSpring.setParent(this);
      if ((!(paramSpring instanceof GroupLayout.AutoPreferredGapSpring)) || (!((GroupLayout.AutoPreferredGapSpring)paramSpring).getUserCreated())) {
        springsChanged = true;
      }
      return this;
    }
    
    void setSize(int paramInt1, int paramInt2, int paramInt3)
    {
      super.setSize(paramInt1, paramInt2, paramInt3);
      if (paramInt3 == Integer.MIN_VALUE) {
        for (int i = springs.size() - 1; i >= 0; i--) {
          getSpring(i).setSize(paramInt1, paramInt2, paramInt3);
        }
      } else {
        setValidSize(paramInt1, paramInt2, paramInt3);
      }
    }
    
    abstract void setValidSize(int paramInt1, int paramInt2, int paramInt3);
    
    int calculateMinimumSize(int paramInt)
    {
      return calculateSize(paramInt, 0);
    }
    
    int calculatePreferredSize(int paramInt)
    {
      return calculateSize(paramInt, 1);
    }
    
    int calculateMaximumSize(int paramInt)
    {
      return calculateSize(paramInt, 2);
    }
    
    int calculateSize(int paramInt1, int paramInt2)
    {
      int i = springs.size();
      if (i == 0) {
        return 0;
      }
      if (i == 1) {
        return getSpringSize(getSpring(0), paramInt1, paramInt2);
      }
      int j = constrain(operator(getSpringSize(getSpring(0), paramInt1, paramInt2), getSpringSize(getSpring(1), paramInt1, paramInt2)));
      for (int k = 2; k < i; k++) {
        j = constrain(operator(j, getSpringSize(getSpring(k), paramInt1, paramInt2)));
      }
      return j;
    }
    
    int getSpringSize(GroupLayout.Spring paramSpring, int paramInt1, int paramInt2)
    {
      switch (paramInt2)
      {
      case 0: 
        return paramSpring.getMinimumSize(paramInt1);
      case 1: 
        return paramSpring.getPreferredSize(paramInt1);
      case 2: 
        return paramSpring.getMaximumSize(paramInt1);
      }
      if (!$assertionsDisabled) {
        throw new AssertionError();
      }
      return 0;
    }
    
    abstract int operator(int paramInt1, int paramInt2);
    
    abstract void insertAutopadding(int paramInt, List<GroupLayout.AutoPreferredGapSpring> paramList1, List<GroupLayout.AutoPreferredGapSpring> paramList2, List<GroupLayout.ComponentSpring> paramList3, List<GroupLayout.ComponentSpring> paramList4, boolean paramBoolean);
    
    void removeAutopadding()
    {
      unset();
      for (int i = springs.size() - 1; i >= 0; i--)
      {
        GroupLayout.Spring localSpring = (GroupLayout.Spring)springs.get(i);
        if ((localSpring instanceof GroupLayout.AutoPreferredGapSpring))
        {
          if (((GroupLayout.AutoPreferredGapSpring)localSpring).getUserCreated()) {
            ((GroupLayout.AutoPreferredGapSpring)localSpring).reset();
          } else {
            springs.remove(i);
          }
        }
        else if ((localSpring instanceof Group)) {
          ((Group)localSpring).removeAutopadding();
        }
      }
    }
    
    void unsetAutopadding()
    {
      unset();
      for (int i = springs.size() - 1; i >= 0; i--)
      {
        GroupLayout.Spring localSpring = (GroupLayout.Spring)springs.get(i);
        if ((localSpring instanceof GroupLayout.AutoPreferredGapSpring)) {
          localSpring.unset();
        } else if ((localSpring instanceof Group)) {
          ((Group)localSpring).unsetAutopadding();
        }
      }
    }
    
    void calculateAutopadding(int paramInt)
    {
      for (int i = springs.size() - 1; i >= 0; i--)
      {
        GroupLayout.Spring localSpring = (GroupLayout.Spring)springs.get(i);
        if ((localSpring instanceof GroupLayout.AutoPreferredGapSpring))
        {
          localSpring.unset();
          ((GroupLayout.AutoPreferredGapSpring)localSpring).calculatePadding(paramInt);
        }
        else if ((localSpring instanceof Group))
        {
          ((Group)localSpring).calculateAutopadding(paramInt);
        }
      }
      unset();
    }
    
    boolean willHaveZeroSize(boolean paramBoolean)
    {
      for (int i = springs.size() - 1; i >= 0; i--)
      {
        GroupLayout.Spring localSpring = (GroupLayout.Spring)springs.get(i);
        if (!localSpring.willHaveZeroSize(paramBoolean)) {
          return false;
        }
      }
      return true;
    }
  }
  
  private static class LinkInfo
  {
    private final int axis;
    private final List<GroupLayout.ComponentInfo> linked = new ArrayList();
    private int size = Integer.MIN_VALUE;
    
    LinkInfo(int paramInt)
    {
      axis = paramInt;
    }
    
    public void add(GroupLayout.ComponentInfo paramComponentInfo)
    {
      LinkInfo localLinkInfo = paramComponentInfo.getLinkInfo(axis, false);
      if (localLinkInfo == null)
      {
        linked.add(paramComponentInfo);
        paramComponentInfo.setLinkInfo(axis, this);
      }
      else if (localLinkInfo != this)
      {
        linked.addAll(linked);
        Iterator localIterator = linked.iterator();
        while (localIterator.hasNext())
        {
          GroupLayout.ComponentInfo localComponentInfo = (GroupLayout.ComponentInfo)localIterator.next();
          localComponentInfo.setLinkInfo(axis, this);
        }
      }
      clearCachedSize();
    }
    
    public void remove(GroupLayout.ComponentInfo paramComponentInfo)
    {
      linked.remove(paramComponentInfo);
      paramComponentInfo.setLinkInfo(axis, null);
      if (linked.size() == 1) {
        ((GroupLayout.ComponentInfo)linked.get(0)).setLinkInfo(axis, null);
      }
      clearCachedSize();
    }
    
    public void clearCachedSize()
    {
      size = Integer.MIN_VALUE;
    }
    
    public int getSize(int paramInt)
    {
      if (size == Integer.MIN_VALUE) {
        size = calculateLinkedSize(paramInt);
      }
      return size;
    }
    
    private int calculateLinkedSize(int paramInt)
    {
      int i = 0;
      Iterator localIterator = linked.iterator();
      while (localIterator.hasNext())
      {
        GroupLayout.ComponentInfo localComponentInfo = (GroupLayout.ComponentInfo)localIterator.next();
        GroupLayout.ComponentSpring localComponentSpring;
        if (paramInt == 0)
        {
          localComponentSpring = horizontalSpring;
        }
        else
        {
          assert (paramInt == 1);
          localComponentSpring = verticalSpring;
        }
        i = Math.max(i, localComponentSpring.calculateNonlinkedPreferredSize(paramInt));
      }
      return i;
    }
  }
  
  public class ParallelGroup
    extends GroupLayout.Group
  {
    private final GroupLayout.Alignment childAlignment;
    private final boolean resizable;
    
    ParallelGroup(GroupLayout.Alignment paramAlignment, boolean paramBoolean)
    {
      super();
      childAlignment = paramAlignment;
      resizable = paramBoolean;
    }
    
    public ParallelGroup addGroup(GroupLayout.Group paramGroup)
    {
      return (ParallelGroup)super.addGroup(paramGroup);
    }
    
    public ParallelGroup addComponent(Component paramComponent)
    {
      return (ParallelGroup)super.addComponent(paramComponent);
    }
    
    public ParallelGroup addComponent(Component paramComponent, int paramInt1, int paramInt2, int paramInt3)
    {
      return (ParallelGroup)super.addComponent(paramComponent, paramInt1, paramInt2, paramInt3);
    }
    
    public ParallelGroup addGap(int paramInt)
    {
      return (ParallelGroup)super.addGap(paramInt);
    }
    
    public ParallelGroup addGap(int paramInt1, int paramInt2, int paramInt3)
    {
      return (ParallelGroup)super.addGap(paramInt1, paramInt2, paramInt3);
    }
    
    public ParallelGroup addGroup(GroupLayout.Alignment paramAlignment, GroupLayout.Group paramGroup)
    {
      checkChildAlignment(paramAlignment);
      paramGroup.setAlignment(paramAlignment);
      return (ParallelGroup)addSpring(paramGroup);
    }
    
    public ParallelGroup addComponent(Component paramComponent, GroupLayout.Alignment paramAlignment)
    {
      return addComponent(paramComponent, paramAlignment, -1, -1, -1);
    }
    
    public ParallelGroup addComponent(Component paramComponent, GroupLayout.Alignment paramAlignment, int paramInt1, int paramInt2, int paramInt3)
    {
      checkChildAlignment(paramAlignment);
      GroupLayout.ComponentSpring localComponentSpring = new GroupLayout.ComponentSpring(GroupLayout.this, paramComponent, paramInt1, paramInt2, paramInt3, null);
      localComponentSpring.setAlignment(paramAlignment);
      return (ParallelGroup)addSpring(localComponentSpring);
    }
    
    boolean isResizable()
    {
      return resizable;
    }
    
    int operator(int paramInt1, int paramInt2)
    {
      return Math.max(paramInt1, paramInt2);
    }
    
    int calculateMinimumSize(int paramInt)
    {
      if (!isResizable()) {
        return getPreferredSize(paramInt);
      }
      return super.calculateMinimumSize(paramInt);
    }
    
    int calculateMaximumSize(int paramInt)
    {
      if (!isResizable()) {
        return getPreferredSize(paramInt);
      }
      return super.calculateMaximumSize(paramInt);
    }
    
    void setValidSize(int paramInt1, int paramInt2, int paramInt3)
    {
      Iterator localIterator = springs.iterator();
      while (localIterator.hasNext())
      {
        GroupLayout.Spring localSpring = (GroupLayout.Spring)localIterator.next();
        setChildSize(localSpring, paramInt1, paramInt2, paramInt3);
      }
    }
    
    void setChildSize(GroupLayout.Spring paramSpring, int paramInt1, int paramInt2, int paramInt3)
    {
      GroupLayout.Alignment localAlignment = paramSpring.getAlignment();
      int i = Math.min(Math.max(paramSpring.getMinimumSize(paramInt1), paramInt3), paramSpring.getMaximumSize(paramInt1));
      if (localAlignment == null) {
        localAlignment = childAlignment;
      }
      switch (GroupLayout.1.$SwitchMap$javax$swing$GroupLayout$Alignment[localAlignment.ordinal()])
      {
      case 1: 
        paramSpring.setSize(paramInt1, paramInt2 + paramInt3 - i, i);
        break;
      case 2: 
        paramSpring.setSize(paramInt1, paramInt2 + (paramInt3 - i) / 2, i);
        break;
      default: 
        paramSpring.setSize(paramInt1, paramInt2, i);
      }
    }
    
    void insertAutopadding(int paramInt, List<GroupLayout.AutoPreferredGapSpring> paramList1, List<GroupLayout.AutoPreferredGapSpring> paramList2, List<GroupLayout.ComponentSpring> paramList3, List<GroupLayout.ComponentSpring> paramList4, boolean paramBoolean)
    {
      Iterator localIterator1 = springs.iterator();
      while (localIterator1.hasNext())
      {
        GroupLayout.Spring localSpring = (GroupLayout.Spring)localIterator1.next();
        if ((localSpring instanceof GroupLayout.ComponentSpring))
        {
          if (((GroupLayout.ComponentSpring)localSpring).isVisible())
          {
            Iterator localIterator2 = paramList1.iterator();
            while (localIterator2.hasNext())
            {
              GroupLayout.AutoPreferredGapSpring localAutoPreferredGapSpring = (GroupLayout.AutoPreferredGapSpring)localIterator2.next();
              localAutoPreferredGapSpring.addTarget((GroupLayout.ComponentSpring)localSpring, paramInt);
            }
            paramList4.add((GroupLayout.ComponentSpring)localSpring);
          }
        }
        else if ((localSpring instanceof GroupLayout.Group))
        {
          ((GroupLayout.Group)localSpring).insertAutopadding(paramInt, paramList1, paramList2, paramList3, paramList4, paramBoolean);
        }
        else if ((localSpring instanceof GroupLayout.AutoPreferredGapSpring))
        {
          ((GroupLayout.AutoPreferredGapSpring)localSpring).setSources(paramList3);
          paramList2.add((GroupLayout.AutoPreferredGapSpring)localSpring);
        }
      }
    }
    
    private void checkChildAlignment(GroupLayout.Alignment paramAlignment)
    {
      checkChildAlignment(paramAlignment, this instanceof GroupLayout.BaselineGroup);
    }
    
    private void checkChildAlignment(GroupLayout.Alignment paramAlignment, boolean paramBoolean)
    {
      if (paramAlignment == null) {
        throw new IllegalArgumentException("Alignment must be non-null");
      }
      if ((!paramBoolean) && (paramAlignment == GroupLayout.Alignment.BASELINE)) {
        throw new IllegalArgumentException("Alignment must be one of:LEADING, TRAILING or CENTER");
      }
    }
  }
  
  private class PreferredGapSpring
    extends GroupLayout.Spring
  {
    private final JComponent source;
    private final JComponent target;
    private final LayoutStyle.ComponentPlacement type;
    private final int pref;
    private final int max;
    
    PreferredGapSpring(JComponent paramJComponent1, JComponent paramJComponent2, LayoutStyle.ComponentPlacement paramComponentPlacement, int paramInt1, int paramInt2)
    {
      super();
      source = paramJComponent1;
      target = paramJComponent2;
      type = paramComponentPlacement;
      pref = paramInt1;
      max = paramInt2;
    }
    
    int calculateMinimumSize(int paramInt)
    {
      return getPadding(paramInt);
    }
    
    int calculatePreferredSize(int paramInt)
    {
      if ((pref == -1) || (pref == -2)) {
        return getMinimumSize(paramInt);
      }
      int i = getMinimumSize(paramInt);
      int j = getMaximumSize(paramInt);
      return Math.min(j, Math.max(i, pref));
    }
    
    int calculateMaximumSize(int paramInt)
    {
      if ((max == -2) || (max == -1)) {
        return getPadding(paramInt);
      }
      return Math.max(getMinimumSize(paramInt), max);
    }
    
    private int getPadding(int paramInt)
    {
      int i;
      if (paramInt == 0) {
        i = 3;
      } else {
        i = 5;
      }
      return GroupLayout.this.getLayoutStyle0().getPreferredGap(source, target, type, i, host);
    }
    
    boolean willHaveZeroSize(boolean paramBoolean)
    {
      return false;
    }
  }
  
  public class SequentialGroup
    extends GroupLayout.Group
  {
    private GroupLayout.Spring baselineSpring;
    
    SequentialGroup()
    {
      super();
    }
    
    public SequentialGroup addGroup(GroupLayout.Group paramGroup)
    {
      return (SequentialGroup)super.addGroup(paramGroup);
    }
    
    public SequentialGroup addGroup(boolean paramBoolean, GroupLayout.Group paramGroup)
    {
      super.addGroup(paramGroup);
      if (paramBoolean) {
        baselineSpring = paramGroup;
      }
      return this;
    }
    
    public SequentialGroup addComponent(Component paramComponent)
    {
      return (SequentialGroup)super.addComponent(paramComponent);
    }
    
    public SequentialGroup addComponent(boolean paramBoolean, Component paramComponent)
    {
      super.addComponent(paramComponent);
      if (paramBoolean) {
        baselineSpring = ((GroupLayout.Spring)springs.get(springs.size() - 1));
      }
      return this;
    }
    
    public SequentialGroup addComponent(Component paramComponent, int paramInt1, int paramInt2, int paramInt3)
    {
      return (SequentialGroup)super.addComponent(paramComponent, paramInt1, paramInt2, paramInt3);
    }
    
    public SequentialGroup addComponent(boolean paramBoolean, Component paramComponent, int paramInt1, int paramInt2, int paramInt3)
    {
      super.addComponent(paramComponent, paramInt1, paramInt2, paramInt3);
      if (paramBoolean) {
        baselineSpring = ((GroupLayout.Spring)springs.get(springs.size() - 1));
      }
      return this;
    }
    
    public SequentialGroup addGap(int paramInt)
    {
      return (SequentialGroup)super.addGap(paramInt);
    }
    
    public SequentialGroup addGap(int paramInt1, int paramInt2, int paramInt3)
    {
      return (SequentialGroup)super.addGap(paramInt1, paramInt2, paramInt3);
    }
    
    public SequentialGroup addPreferredGap(JComponent paramJComponent1, JComponent paramJComponent2, LayoutStyle.ComponentPlacement paramComponentPlacement)
    {
      return addPreferredGap(paramJComponent1, paramJComponent2, paramComponentPlacement, -1, -2);
    }
    
    public SequentialGroup addPreferredGap(JComponent paramJComponent1, JComponent paramJComponent2, LayoutStyle.ComponentPlacement paramComponentPlacement, int paramInt1, int paramInt2)
    {
      if (paramComponentPlacement == null) {
        throw new IllegalArgumentException("Type must be non-null");
      }
      if ((paramJComponent1 == null) || (paramJComponent2 == null)) {
        throw new IllegalArgumentException("Components must be non-null");
      }
      checkPreferredGapValues(paramInt1, paramInt2);
      return (SequentialGroup)addSpring(new GroupLayout.PreferredGapSpring(GroupLayout.this, paramJComponent1, paramJComponent2, paramComponentPlacement, paramInt1, paramInt2));
    }
    
    public SequentialGroup addPreferredGap(LayoutStyle.ComponentPlacement paramComponentPlacement)
    {
      return addPreferredGap(paramComponentPlacement, -1, -1);
    }
    
    public SequentialGroup addPreferredGap(LayoutStyle.ComponentPlacement paramComponentPlacement, int paramInt1, int paramInt2)
    {
      if ((paramComponentPlacement != LayoutStyle.ComponentPlacement.RELATED) && (paramComponentPlacement != LayoutStyle.ComponentPlacement.UNRELATED)) {
        throw new IllegalArgumentException("Type must be one of LayoutStyle.ComponentPlacement.RELATED or LayoutStyle.ComponentPlacement.UNRELATED");
      }
      checkPreferredGapValues(paramInt1, paramInt2);
      hasPreferredPaddingSprings = true;
      return (SequentialGroup)addSpring(new GroupLayout.AutoPreferredGapSpring(GroupLayout.this, paramComponentPlacement, paramInt1, paramInt2));
    }
    
    public SequentialGroup addContainerGap()
    {
      return addContainerGap(-1, -1);
    }
    
    public SequentialGroup addContainerGap(int paramInt1, int paramInt2)
    {
      if (((paramInt1 < 0) && (paramInt1 != -1)) || ((paramInt2 < 0) && (paramInt2 != -1) && (paramInt2 != -2)) || ((paramInt1 >= 0) && (paramInt2 >= 0) && (paramInt1 > paramInt2))) {
        throw new IllegalArgumentException("Pref and max must be either DEFAULT_VALUE or >= 0 and pref <= max");
      }
      hasPreferredPaddingSprings = true;
      return (SequentialGroup)addSpring(new GroupLayout.ContainerAutoPreferredGapSpring(GroupLayout.this, paramInt1, paramInt2));
    }
    
    int operator(int paramInt1, int paramInt2)
    {
      return constrain(paramInt1) + constrain(paramInt2);
    }
    
    void setValidSize(int paramInt1, int paramInt2, int paramInt3)
    {
      int i = getPreferredSize(paramInt1);
      Object localObject;
      if (paramInt3 == i)
      {
        localObject = springs.iterator();
        while (((Iterator)localObject).hasNext())
        {
          GroupLayout.Spring localSpring = (GroupLayout.Spring)((Iterator)localObject).next();
          int j = localSpring.getPreferredSize(paramInt1);
          localSpring.setSize(paramInt1, paramInt2, j);
          paramInt2 += j;
        }
      }
      else if (springs.size() == 1)
      {
        localObject = getSpring(0);
        ((GroupLayout.Spring)localObject).setSize(paramInt1, paramInt2, Math.min(Math.max(paramInt3, ((GroupLayout.Spring)localObject).getMinimumSize(paramInt1)), ((GroupLayout.Spring)localObject).getMaximumSize(paramInt1)));
      }
      else if (springs.size() > 1)
      {
        setValidSizeNotPreferred(paramInt1, paramInt2, paramInt3);
      }
    }
    
    private void setValidSizeNotPreferred(int paramInt1, int paramInt2, int paramInt3)
    {
      int i = paramInt3 - getPreferredSize(paramInt1);
      assert (i != 0);
      boolean bool = i < 0;
      int j = springs.size();
      if (bool) {
        i *= -1;
      }
      List localList = buildResizableList(paramInt1, bool);
      int k = localList.size();
      int m;
      if (k > 0)
      {
        m = i / k;
        int n = i - m * k;
        int[] arrayOfInt = new int[j];
        int i2 = bool ? -1 : 1;
        Object localObject;
        for (int i3 = 0; i3 < k; i3++)
        {
          localObject = (GroupLayout.SpringDelta)localList.get(i3);
          if (i3 + 1 == k) {
            m += n;
          }
          delta = Math.min(m, delta);
          i -= delta;
          if ((delta != m) && (i3 + 1 < k))
          {
            m = i / (k - i3 - 1);
            n = i - m * (k - i3 - 1);
          }
          arrayOfInt[index] = (i2 * delta);
        }
        for (i3 = 0; i3 < j; i3++)
        {
          localObject = getSpring(i3);
          int i4 = ((GroupLayout.Spring)localObject).getPreferredSize(paramInt1) + arrayOfInt[i3];
          ((GroupLayout.Spring)localObject).setSize(paramInt1, paramInt2, i4);
          paramInt2 += i4;
        }
      }
      else
      {
        for (m = 0; m < j; m++)
        {
          GroupLayout.Spring localSpring = getSpring(m);
          int i1;
          if (bool) {
            i1 = localSpring.getMinimumSize(paramInt1);
          } else {
            i1 = localSpring.getMaximumSize(paramInt1);
          }
          localSpring.setSize(paramInt1, paramInt2, i1);
          paramInt2 += i1;
        }
      }
    }
    
    private List<GroupLayout.SpringDelta> buildResizableList(int paramInt, boolean paramBoolean)
    {
      int i = springs.size();
      ArrayList localArrayList = new ArrayList(i);
      for (int j = 0; j < i; j++)
      {
        GroupLayout.Spring localSpring = getSpring(j);
        int k;
        if (paramBoolean) {
          k = localSpring.getPreferredSize(paramInt) - localSpring.getMinimumSize(paramInt);
        } else {
          k = localSpring.getMaximumSize(paramInt) - localSpring.getPreferredSize(paramInt);
        }
        if (k > 0) {
          localArrayList.add(new GroupLayout.SpringDelta(j, k));
        }
      }
      Collections.sort(localArrayList);
      return localArrayList;
    }
    
    private int indexOfNextNonZeroSpring(int paramInt, boolean paramBoolean)
    {
      while (paramInt < springs.size())
      {
        GroupLayout.Spring localSpring = (GroupLayout.Spring)springs.get(paramInt);
        if (!localSpring.willHaveZeroSize(paramBoolean)) {
          return paramInt;
        }
        paramInt++;
      }
      return paramInt;
    }
    
    void insertAutopadding(int paramInt, List<GroupLayout.AutoPreferredGapSpring> paramList1, List<GroupLayout.AutoPreferredGapSpring> paramList2, List<GroupLayout.ComponentSpring> paramList3, List<GroupLayout.ComponentSpring> paramList4, boolean paramBoolean)
    {
      ArrayList localArrayList1 = new ArrayList(paramList1);
      ArrayList localArrayList2 = new ArrayList(1);
      ArrayList localArrayList3 = new ArrayList(paramList3);
      ArrayList localArrayList4 = null;
      int i = 0;
      while (i < springs.size())
      {
        GroupLayout.Spring localSpring = getSpring(i);
        Object localObject;
        if ((localSpring instanceof GroupLayout.AutoPreferredGapSpring))
        {
          if (localArrayList1.size() == 0)
          {
            localObject = (GroupLayout.AutoPreferredGapSpring)localSpring;
            ((GroupLayout.AutoPreferredGapSpring)localObject).setSources(localArrayList3);
            localArrayList3.clear();
            i = indexOfNextNonZeroSpring(i + 1, true);
            if (i == springs.size())
            {
              if (!(localObject instanceof GroupLayout.ContainerAutoPreferredGapSpring)) {
                paramList2.add(localObject);
              }
            }
            else
            {
              localArrayList1.clear();
              localArrayList1.add(localObject);
            }
          }
          else
          {
            i = indexOfNextNonZeroSpring(i + 1, true);
          }
        }
        else if ((localArrayList3.size() > 0) && (paramBoolean))
        {
          localObject = new GroupLayout.AutoPreferredGapSpring(GroupLayout.this, null);
          springs.add(i, localObject);
        }
        else if ((localSpring instanceof GroupLayout.ComponentSpring))
        {
          localObject = (GroupLayout.ComponentSpring)localSpring;
          if (!((GroupLayout.ComponentSpring)localObject).isVisible())
          {
            i++;
          }
          else
          {
            Iterator localIterator = localArrayList1.iterator();
            while (localIterator.hasNext())
            {
              GroupLayout.AutoPreferredGapSpring localAutoPreferredGapSpring = (GroupLayout.AutoPreferredGapSpring)localIterator.next();
              localAutoPreferredGapSpring.addTarget((GroupLayout.ComponentSpring)localObject, paramInt);
            }
            localArrayList3.clear();
            localArrayList1.clear();
            i = indexOfNextNonZeroSpring(i + 1, false);
            if (i == springs.size()) {
              paramList4.add(localObject);
            } else {
              localArrayList3.add(localObject);
            }
          }
        }
        else if ((localSpring instanceof GroupLayout.Group))
        {
          if (localArrayList4 == null) {
            localArrayList4 = new ArrayList(1);
          } else {
            localArrayList4.clear();
          }
          localArrayList2.clear();
          ((GroupLayout.Group)localSpring).insertAutopadding(paramInt, localArrayList1, localArrayList2, localArrayList3, localArrayList4, paramBoolean);
          localArrayList3.clear();
          localArrayList1.clear();
          i = indexOfNextNonZeroSpring(i + 1, localArrayList4.size() == 0);
          if (i == springs.size())
          {
            paramList4.addAll(localArrayList4);
            paramList2.addAll(localArrayList2);
          }
          else
          {
            localArrayList3.addAll(localArrayList4);
            localArrayList1.addAll(localArrayList2);
          }
        }
        else
        {
          localArrayList1.clear();
          localArrayList3.clear();
          i++;
        }
      }
    }
    
    int getBaseline()
    {
      if (baselineSpring != null)
      {
        int i = baselineSpring.getBaseline();
        if (i >= 0)
        {
          int j = 0;
          Iterator localIterator = springs.iterator();
          while (localIterator.hasNext())
          {
            GroupLayout.Spring localSpring = (GroupLayout.Spring)localIterator.next();
            if (localSpring == baselineSpring) {
              return j + i;
            }
            j += localSpring.getPreferredSize(1);
          }
        }
      }
      return -1;
    }
    
    Component.BaselineResizeBehavior getBaselineResizeBehavior()
    {
      if (isResizable(1))
      {
        if (!baselineSpring.isResizable(1))
        {
          int i = 0;
          Iterator localIterator1 = springs.iterator();
          while (localIterator1.hasNext())
          {
            GroupLayout.Spring localSpring1 = (GroupLayout.Spring)localIterator1.next();
            if (localSpring1 == baselineSpring) {
              break;
            }
            if (localSpring1.isResizable(1))
            {
              i = 1;
              break;
            }
          }
          int j = 0;
          for (int m = springs.size() - 1; m >= 0; m--)
          {
            GroupLayout.Spring localSpring3 = (GroupLayout.Spring)springs.get(m);
            if (localSpring3 == baselineSpring) {
              break;
            }
            if (localSpring3.isResizable(1))
            {
              j = 1;
              break;
            }
          }
          if ((i != 0) && (j == 0)) {
            return Component.BaselineResizeBehavior.CONSTANT_DESCENT;
          }
          if ((i == 0) && (j != 0)) {
            return Component.BaselineResizeBehavior.CONSTANT_ASCENT;
          }
        }
        else
        {
          Component.BaselineResizeBehavior localBaselineResizeBehavior = baselineSpring.getBaselineResizeBehavior();
          GroupLayout.Spring localSpring2;
          if (localBaselineResizeBehavior == Component.BaselineResizeBehavior.CONSTANT_ASCENT)
          {
            Iterator localIterator2 = springs.iterator();
            while (localIterator2.hasNext())
            {
              localSpring2 = (GroupLayout.Spring)localIterator2.next();
              if (localSpring2 == baselineSpring) {
                return Component.BaselineResizeBehavior.CONSTANT_ASCENT;
              }
              if (localSpring2.isResizable(1)) {
                return Component.BaselineResizeBehavior.OTHER;
              }
            }
          }
          else if (localBaselineResizeBehavior == Component.BaselineResizeBehavior.CONSTANT_DESCENT)
          {
            for (int k = springs.size() - 1; k >= 0; k--)
            {
              localSpring2 = (GroupLayout.Spring)springs.get(k);
              if (localSpring2 == baselineSpring) {
                return Component.BaselineResizeBehavior.CONSTANT_DESCENT;
              }
              if (localSpring2.isResizable(1)) {
                return Component.BaselineResizeBehavior.OTHER;
              }
            }
          }
        }
        return Component.BaselineResizeBehavior.OTHER;
      }
      return Component.BaselineResizeBehavior.CONSTANT_ASCENT;
    }
    
    private void checkPreferredGapValues(int paramInt1, int paramInt2)
    {
      if (((paramInt1 < 0) && (paramInt1 != -1) && (paramInt1 != -2)) || ((paramInt2 < 0) && (paramInt2 != -1) && (paramInt2 != -2)) || ((paramInt1 >= 0) && (paramInt2 >= 0) && (paramInt1 > paramInt2))) {
        throw new IllegalArgumentException("Pref and max must be either DEFAULT_SIZE, PREFERRED_SIZE, or >= 0 and pref <= max");
      }
    }
  }
  
  private abstract class Spring
  {
    private int size;
    private int min = pref = max = Integer.MIN_VALUE;
    private int max;
    private int pref;
    private Spring parent;
    private GroupLayout.Alignment alignment;
    
    Spring() {}
    
    abstract int calculateMinimumSize(int paramInt);
    
    abstract int calculatePreferredSize(int paramInt);
    
    abstract int calculateMaximumSize(int paramInt);
    
    void setParent(Spring paramSpring)
    {
      parent = paramSpring;
    }
    
    Spring getParent()
    {
      return parent;
    }
    
    void setAlignment(GroupLayout.Alignment paramAlignment)
    {
      alignment = paramAlignment;
    }
    
    GroupLayout.Alignment getAlignment()
    {
      return alignment;
    }
    
    final int getMinimumSize(int paramInt)
    {
      if (min == Integer.MIN_VALUE) {
        min = constrain(calculateMinimumSize(paramInt));
      }
      return min;
    }
    
    final int getPreferredSize(int paramInt)
    {
      if (pref == Integer.MIN_VALUE) {
        pref = constrain(calculatePreferredSize(paramInt));
      }
      return pref;
    }
    
    final int getMaximumSize(int paramInt)
    {
      if (max == Integer.MIN_VALUE) {
        max = constrain(calculateMaximumSize(paramInt));
      }
      return max;
    }
    
    void setSize(int paramInt1, int paramInt2, int paramInt3)
    {
      size = paramInt3;
      if (paramInt3 == Integer.MIN_VALUE) {
        unset();
      }
    }
    
    void unset()
    {
      size = (min = pref = max = Integer.MIN_VALUE);
    }
    
    int getSize()
    {
      return size;
    }
    
    int constrain(int paramInt)
    {
      return Math.min(paramInt, 32767);
    }
    
    int getBaseline()
    {
      return -1;
    }
    
    Component.BaselineResizeBehavior getBaselineResizeBehavior()
    {
      return Component.BaselineResizeBehavior.OTHER;
    }
    
    final boolean isResizable(int paramInt)
    {
      int i = getMinimumSize(paramInt);
      int j = getPreferredSize(paramInt);
      return (i != j) || (j != getMaximumSize(paramInt));
    }
    
    abstract boolean willHaveZeroSize(boolean paramBoolean);
  }
  
  private static final class SpringDelta
    implements Comparable<SpringDelta>
  {
    public final int index;
    public int delta;
    
    public SpringDelta(int paramInt1, int paramInt2)
    {
      index = paramInt1;
      delta = paramInt2;
    }
    
    public int compareTo(SpringDelta paramSpringDelta)
    {
      return delta - delta;
    }
    
    public String toString()
    {
      return super.toString() + "[index=" + index + ", delta=" + delta + "]";
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\GroupLayout.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
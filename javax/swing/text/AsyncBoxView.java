package javax.swing.text;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentEvent.ElementChange;

public class AsyncBoxView
  extends View
{
  int axis;
  List<ChildState> stats = new ArrayList();
  float majorSpan;
  boolean estimatedMajorSpan;
  float minorSpan;
  protected ChildLocator locator;
  float topInset;
  float bottomInset;
  float leftInset;
  float rightInset;
  ChildState minRequest;
  ChildState prefRequest;
  boolean majorChanged;
  boolean minorChanged;
  Runnable flushTask;
  ChildState changing;
  
  public AsyncBoxView(Element paramElement, int paramInt)
  {
    super(paramElement);
    axis = paramInt;
    locator = new ChildLocator();
    flushTask = new FlushTask();
    minorSpan = 32767.0F;
    estimatedMajorSpan = false;
  }
  
  public int getMajorAxis()
  {
    return axis;
  }
  
  public int getMinorAxis()
  {
    return axis == 0 ? 1 : 0;
  }
  
  public float getTopInset()
  {
    return topInset;
  }
  
  public void setTopInset(float paramFloat)
  {
    topInset = paramFloat;
  }
  
  public float getBottomInset()
  {
    return bottomInset;
  }
  
  public void setBottomInset(float paramFloat)
  {
    bottomInset = paramFloat;
  }
  
  public float getLeftInset()
  {
    return leftInset;
  }
  
  public void setLeftInset(float paramFloat)
  {
    leftInset = paramFloat;
  }
  
  public float getRightInset()
  {
    return rightInset;
  }
  
  public void setRightInset(float paramFloat)
  {
    rightInset = paramFloat;
  }
  
  protected float getInsetSpan(int paramInt)
  {
    float f = paramInt == 0 ? getLeftInset() + getRightInset() : getTopInset() + getBottomInset();
    return f;
  }
  
  protected void setEstimatedMajorSpan(boolean paramBoolean)
  {
    estimatedMajorSpan = paramBoolean;
  }
  
  protected boolean getEstimatedMajorSpan()
  {
    return estimatedMajorSpan;
  }
  
  protected ChildState getChildState(int paramInt)
  {
    synchronized (stats)
    {
      if ((paramInt >= 0) && (paramInt < stats.size())) {
        return (ChildState)stats.get(paramInt);
      }
      return null;
    }
  }
  
  protected LayoutQueue getLayoutQueue()
  {
    return LayoutQueue.getDefaultQueue();
  }
  
  protected ChildState createChildState(View paramView)
  {
    return new ChildState(paramView);
  }
  
  protected synchronized void majorRequirementChange(ChildState paramChildState, float paramFloat)
  {
    if (!estimatedMajorSpan) {
      majorSpan += paramFloat;
    }
    majorChanged = true;
  }
  
  protected synchronized void minorRequirementChange(ChildState paramChildState)
  {
    minorChanged = true;
  }
  
  protected void flushRequirementChanges()
  {
    AbstractDocument localAbstractDocument = (AbstractDocument)getDocument();
    try
    {
      localAbstractDocument.readLock();
      View localView = null;
      boolean bool1 = false;
      boolean bool2 = false;
      synchronized (this)
      {
        synchronized (stats)
        {
          int i = getViewCount();
          if ((i > 0) && ((minorChanged) || (estimatedMajorSpan)))
          {
            LayoutQueue localLayoutQueue = getLayoutQueue();
            Object localObject1 = getChildState(0);
            Object localObject2 = getChildState(0);
            float f = 0.0F;
            for (int j = 1; j < i; j++)
            {
              ChildState localChildState = getChildState(j);
              if (minorChanged)
              {
                if (min > min) {
                  localObject1 = localChildState;
                }
                if (pref > pref) {
                  localObject2 = localChildState;
                }
              }
              if (estimatedMajorSpan) {
                f += localChildState.getMajorSpan();
              }
            }
            if (minorChanged)
            {
              minRequest = ((ChildState)localObject1);
              prefRequest = ((ChildState)localObject2);
            }
            if (estimatedMajorSpan)
            {
              majorSpan = f;
              estimatedMajorSpan = false;
              majorChanged = true;
            }
          }
        }
        if ((majorChanged) || (minorChanged))
        {
          localView = getParent();
          if (localView != null) {
            if (axis == 0)
            {
              bool1 = majorChanged;
              bool2 = minorChanged;
            }
            else
            {
              bool2 = majorChanged;
              bool1 = minorChanged;
            }
          }
          majorChanged = false;
          minorChanged = false;
        }
      }
      if (localView != null)
      {
        localView.preferenceChanged(this, bool1, bool2);
        ??? = getContainer();
        if (??? != null) {
          ((Component)???).repaint();
        }
      }
    }
    finally
    {
      localAbstractDocument.readUnlock();
    }
  }
  
  public void replace(int paramInt1, int paramInt2, View[] paramArrayOfView)
  {
    synchronized (stats)
    {
      for (int i = 0; i < paramInt2; i++)
      {
        ChildState localChildState1 = (ChildState)stats.remove(paramInt1);
        float f = localChildState1.getMajorSpan();
        localChildState1.getChildView().setParent(null);
        if (f != 0.0F) {
          majorRequirementChange(localChildState1, -f);
        }
      }
      LayoutQueue localLayoutQueue = getLayoutQueue();
      if (paramArrayOfView != null) {
        for (int j = 0; j < paramArrayOfView.length; j++)
        {
          ChildState localChildState2 = createChildState(paramArrayOfView[j]);
          stats.add(paramInt1 + j, localChildState2);
          localLayoutQueue.addTask(localChildState2);
        }
      }
      localLayoutQueue.addTask(flushTask);
    }
  }
  
  protected void loadChildren(ViewFactory paramViewFactory)
  {
    Element localElement = getElement();
    int i = localElement.getElementCount();
    if (i > 0)
    {
      View[] arrayOfView = new View[i];
      for (int j = 0; j < i; j++) {
        arrayOfView[j] = paramViewFactory.create(localElement.getElement(j));
      }
      replace(0, 0, arrayOfView);
    }
  }
  
  protected synchronized int getViewIndexAtPosition(int paramInt, Position.Bias paramBias)
  {
    int i = paramBias == Position.Bias.Backward ? 1 : 0;
    paramInt = i != 0 ? Math.max(0, paramInt - 1) : paramInt;
    Element localElement = getElement();
    return localElement.getElementIndex(paramInt);
  }
  
  protected void updateLayout(DocumentEvent.ElementChange paramElementChange, DocumentEvent paramDocumentEvent, Shape paramShape)
  {
    if (paramElementChange != null)
    {
      int i = Math.max(paramElementChange.getIndex() - 1, 0);
      ChildState localChildState = getChildState(i);
      locator.childChanged(localChildState);
    }
  }
  
  public void setParent(View paramView)
  {
    super.setParent(paramView);
    if ((paramView != null) && (getViewCount() == 0))
    {
      ViewFactory localViewFactory = getViewFactory();
      loadChildren(localViewFactory);
    }
  }
  
  public synchronized void preferenceChanged(View paramView, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (paramView == null)
    {
      getParent().preferenceChanged(this, paramBoolean1, paramBoolean2);
    }
    else
    {
      if (changing != null)
      {
        View localView = changing.getChildView();
        if (localView == paramView)
        {
          changing.preferenceChanged(paramBoolean1, paramBoolean2);
          return;
        }
      }
      int i = getViewIndex(paramView.getStartOffset(), Position.Bias.Forward);
      ChildState localChildState = getChildState(i);
      localChildState.preferenceChanged(paramBoolean1, paramBoolean2);
      LayoutQueue localLayoutQueue = getLayoutQueue();
      localLayoutQueue.addTask(localChildState);
      localLayoutQueue.addTask(flushTask);
    }
  }
  
  public void setSize(float paramFloat1, float paramFloat2)
  {
    setSpanOnAxis(0, paramFloat1);
    setSpanOnAxis(1, paramFloat2);
  }
  
  float getSpanOnAxis(int paramInt)
  {
    if (paramInt == getMajorAxis()) {
      return majorSpan;
    }
    return minorSpan;
  }
  
  void setSpanOnAxis(int paramInt, float paramFloat)
  {
    float f1 = getInsetSpan(paramInt);
    if (paramInt == getMinorAxis())
    {
      float f2 = paramFloat - f1;
      if (f2 != minorSpan)
      {
        minorSpan = f2;
        int i = getViewCount();
        if (i != 0)
        {
          LayoutQueue localLayoutQueue = getLayoutQueue();
          for (int j = 0; j < i; j++)
          {
            ChildState localChildState = getChildState(j);
            childSizeValid = false;
            localLayoutQueue.addTask(localChildState);
          }
          localLayoutQueue.addTask(flushTask);
        }
      }
    }
    else if (estimatedMajorSpan)
    {
      majorSpan = (paramFloat - f1);
    }
  }
  
  public void paint(Graphics paramGraphics, Shape paramShape)
  {
    synchronized (locator)
    {
      locator.setAllocation(paramShape);
      locator.paintChildren(paramGraphics);
    }
  }
  
  public float getPreferredSpan(int paramInt)
  {
    float f = getInsetSpan(paramInt);
    if (paramInt == axis) {
      return majorSpan + f;
    }
    if (prefRequest != null)
    {
      View localView = prefRequest.getChildView();
      return localView.getPreferredSpan(paramInt) + f;
    }
    return f + 30.0F;
  }
  
  public float getMinimumSpan(int paramInt)
  {
    if (paramInt == axis) {
      return getPreferredSpan(paramInt);
    }
    if (minRequest != null)
    {
      View localView = minRequest.getChildView();
      return localView.getMinimumSpan(paramInt);
    }
    if (paramInt == 0) {
      return getLeftInset() + getRightInset() + 5.0F;
    }
    return getTopInset() + getBottomInset() + 5.0F;
  }
  
  public float getMaximumSpan(int paramInt)
  {
    if (paramInt == axis) {
      return getPreferredSpan(paramInt);
    }
    return 2.14748365E9F;
  }
  
  /* Error */
  public int getViewCount()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 298	javax/swing/text/AsyncBoxView:stats	Ljava/util/List;
    //   4: dup
    //   5: astore_1
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 298	javax/swing/text/AsyncBoxView:stats	Ljava/util/List;
    //   11: invokeinterface 359 1 0
    //   16: aload_1
    //   17: monitorexit
    //   18: ireturn
    //   19: astore_2
    //   20: aload_1
    //   21: monitorexit
    //   22: aload_2
    //   23: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	24	0	this	AsyncBoxView
    //   5	16	1	Ljava/lang/Object;	Object
    //   19	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   7	18	19	finally
    //   19	22	19	finally
  }
  
  public View getView(int paramInt)
  {
    ChildState localChildState = getChildState(paramInt);
    if (localChildState != null) {
      return localChildState.getChildView();
    }
    return null;
  }
  
  public Shape getChildAllocation(int paramInt, Shape paramShape)
  {
    Shape localShape = locator.getChildAllocation(paramInt, paramShape);
    return localShape;
  }
  
  public int getViewIndex(int paramInt, Position.Bias paramBias)
  {
    return getViewIndexAtPosition(paramInt, paramBias);
  }
  
  public Shape modelToView(int paramInt, Shape paramShape, Position.Bias paramBias)
    throws BadLocationException
  {
    int i = getViewIndex(paramInt, paramBias);
    Shape localShape1 = locator.getChildAllocation(i, paramShape);
    ChildState localChildState = getChildState(i);
    synchronized (localChildState)
    {
      View localView = localChildState.getChildView();
      Shape localShape2 = localView.modelToView(paramInt, localShape1, paramBias);
      return localShape2;
    }
  }
  
  public int viewToModel(float paramFloat1, float paramFloat2, Shape paramShape, Position.Bias[] paramArrayOfBias)
  {
    int j;
    Shape localShape;
    synchronized (locator)
    {
      j = locator.getViewIndexAtPoint(paramFloat1, paramFloat2, paramShape);
      localShape = locator.getChildAllocation(j, paramShape);
    }
    ??? = getChildState(j);
    int i;
    synchronized (???)
    {
      View localView = ((ChildState)???).getChildView();
      i = localView.viewToModel(paramFloat1, paramFloat2, localShape, paramArrayOfBias);
    }
    return i;
  }
  
  public int getNextVisualPositionFrom(int paramInt1, Position.Bias paramBias, Shape paramShape, int paramInt2, Position.Bias[] paramArrayOfBias)
    throws BadLocationException
  {
    if (paramInt1 < -1) {
      throw new BadLocationException("invalid position", paramInt1);
    }
    return Utilities.getNextVisualPositionFrom(this, paramInt1, paramBias, paramShape, paramInt2, paramArrayOfBias);
  }
  
  public class ChildLocator
  {
    protected AsyncBoxView.ChildState lastValidOffset;
    protected Rectangle lastAlloc = new Rectangle();
    protected Rectangle childAlloc = new Rectangle();
    
    public ChildLocator() {}
    
    public synchronized void childChanged(AsyncBoxView.ChildState paramChildState)
    {
      if (lastValidOffset == null) {
        lastValidOffset = paramChildState;
      } else if (paramChildState.getChildView().getStartOffset() < lastValidOffset.getChildView().getStartOffset()) {
        lastValidOffset = paramChildState;
      }
    }
    
    public synchronized void paintChildren(Graphics paramGraphics)
    {
      Rectangle localRectangle = paramGraphics.getClipBounds();
      float f1 = axis == 0 ? x - lastAlloc.x : y - lastAlloc.y;
      int i = getViewIndexAtVisualOffset(f1);
      int j = getViewCount();
      float f2 = getChildState(i).getMajorOffset();
      for (int k = i; k < j; k++)
      {
        AsyncBoxView.ChildState localChildState = getChildState(k);
        localChildState.setMajorOffset(f2);
        Shape localShape = getChildAllocation(k);
        if (!intersectsClip(localShape, localRectangle)) {
          break;
        }
        synchronized (localChildState)
        {
          View localView = localChildState.getChildView();
          localView.paint(paramGraphics, localShape);
        }
        f2 += localChildState.getMajorSpan();
      }
    }
    
    public synchronized Shape getChildAllocation(int paramInt, Shape paramShape)
    {
      if (paramShape == null) {
        return null;
      }
      setAllocation(paramShape);
      AsyncBoxView.ChildState localChildState = getChildState(paramInt);
      if (lastValidOffset == null) {
        lastValidOffset = getChildState(0);
      }
      if (localChildState.getChildView().getStartOffset() > lastValidOffset.getChildView().getStartOffset()) {
        updateChildOffsetsToIndex(paramInt);
      }
      Shape localShape = getChildAllocation(paramInt);
      return localShape;
    }
    
    public int getViewIndexAtPoint(float paramFloat1, float paramFloat2, Shape paramShape)
    {
      setAllocation(paramShape);
      float f = axis == 0 ? paramFloat1 - lastAlloc.x : paramFloat2 - lastAlloc.y;
      int i = getViewIndexAtVisualOffset(f);
      return i;
    }
    
    protected Shape getChildAllocation(int paramInt)
    {
      AsyncBoxView.ChildState localChildState = getChildState(paramInt);
      if (!localChildState.isLayoutValid()) {
        localChildState.run();
      }
      if (axis == 0)
      {
        childAlloc.x = (lastAlloc.x + (int)localChildState.getMajorOffset());
        childAlloc.y = (lastAlloc.y + (int)localChildState.getMinorOffset());
        childAlloc.width = ((int)localChildState.getMajorSpan());
        childAlloc.height = ((int)localChildState.getMinorSpan());
      }
      else
      {
        childAlloc.y = (lastAlloc.y + (int)localChildState.getMajorOffset());
        childAlloc.x = (lastAlloc.x + (int)localChildState.getMinorOffset());
        childAlloc.height = ((int)localChildState.getMajorSpan());
        childAlloc.width = ((int)localChildState.getMinorSpan());
      }
      childAlloc.x += (int)getLeftInset();
      childAlloc.y += (int)getRightInset();
      return childAlloc;
    }
    
    protected void setAllocation(Shape paramShape)
    {
      if ((paramShape instanceof Rectangle)) {
        lastAlloc.setBounds((Rectangle)paramShape);
      } else {
        lastAlloc.setBounds(paramShape.getBounds());
      }
      setSize(lastAlloc.width, lastAlloc.height);
    }
    
    protected int getViewIndexAtVisualOffset(float paramFloat)
    {
      int i = getViewCount();
      if (i > 0)
      {
        int j = lastValidOffset != null ? 1 : 0;
        if (lastValidOffset == null) {
          lastValidOffset = getChildState(0);
        }
        if (paramFloat > majorSpan)
        {
          if (j == 0) {
            return 0;
          }
          int k = lastValidOffset.getChildView().getStartOffset();
          m = getViewIndex(k, Position.Bias.Forward);
          return m;
        }
        if (paramFloat > lastValidOffset.getMajorOffset()) {
          return updateChildOffsets(paramFloat);
        }
        float f1 = 0.0F;
        for (int m = 0; m < i; m++)
        {
          AsyncBoxView.ChildState localChildState = getChildState(m);
          float f2 = f1 + localChildState.getMajorSpan();
          if (paramFloat < f2) {
            return m;
          }
          f1 = f2;
        }
      }
      return i - 1;
    }
    
    int updateChildOffsets(float paramFloat)
    {
      int i = getViewCount();
      int j = i - 1;
      int k = lastValidOffset.getChildView().getStartOffset();
      int m = getViewIndex(k, Position.Bias.Forward);
      float f1 = lastValidOffset.getMajorOffset();
      float f2 = f1;
      for (int n = m; n < i; n++)
      {
        AsyncBoxView.ChildState localChildState = getChildState(n);
        localChildState.setMajorOffset(f2);
        f2 += localChildState.getMajorSpan();
        if (paramFloat < f2)
        {
          j = n;
          lastValidOffset = localChildState;
          break;
        }
      }
      return j;
    }
    
    void updateChildOffsetsToIndex(int paramInt)
    {
      int i = lastValidOffset.getChildView().getStartOffset();
      int j = getViewIndex(i, Position.Bias.Forward);
      float f = lastValidOffset.getMajorOffset();
      for (int k = j; k <= paramInt; k++)
      {
        AsyncBoxView.ChildState localChildState = getChildState(k);
        localChildState.setMajorOffset(f);
        f += localChildState.getMajorSpan();
      }
    }
    
    boolean intersectsClip(Shape paramShape, Rectangle paramRectangle)
    {
      Rectangle localRectangle = (paramShape instanceof Rectangle) ? (Rectangle)paramShape : paramShape.getBounds();
      if (localRectangle.intersects(paramRectangle)) {
        return lastAlloc.intersects(localRectangle);
      }
      return false;
    }
  }
  
  public class ChildState
    implements Runnable
  {
    private float min;
    private float pref;
    private float max;
    private boolean minorValid;
    private float span;
    private float offset;
    private boolean majorValid;
    private View child;
    private boolean childSizeValid;
    
    public ChildState(View paramView)
    {
      child = paramView;
      minorValid = false;
      majorValid = false;
      childSizeValid = false;
      child.setParent(AsyncBoxView.this);
    }
    
    public View getChildView()
    {
      return child;
    }
    
    public void run()
    {
      AbstractDocument localAbstractDocument = (AbstractDocument)getDocument();
      try
      {
        localAbstractDocument.readLock();
        if ((minorValid) && (majorValid) && (childSizeValid)) {
          return;
        }
        if (child.getParent() == AsyncBoxView.this)
        {
          synchronized (AsyncBoxView.this)
          {
            changing = this;
          }
          updateChild();
          synchronized (AsyncBoxView.this)
          {
            changing = null;
          }
          updateChild();
        }
      }
      finally
      {
        localAbstractDocument.readUnlock();
      }
    }
    
    void updateChild()
    {
      int i = 0;
      synchronized (this)
      {
        if (!minorValid)
        {
          int k = getMinorAxis();
          min = child.getMinimumSpan(k);
          pref = child.getPreferredSpan(k);
          max = child.getMaximumSpan(k);
          minorValid = true;
          i = 1;
        }
      }
      if (i != 0) {
        minorRequirementChange(this);
      }
      int j = 0;
      float f1 = 0.0F;
      float f2;
      synchronized (this)
      {
        if (!majorValid)
        {
          f2 = span;
          span = child.getPreferredSpan(axis);
          f1 = span - f2;
          majorValid = true;
          j = 1;
        }
      }
      if (j != 0)
      {
        majorRequirementChange(this, f1);
        locator.childChanged(this);
      }
      synchronized (this)
      {
        if (!childSizeValid)
        {
          float f3;
          if (axis == 0)
          {
            f2 = span;
            f3 = getMinorSpan();
          }
          else
          {
            f2 = getMinorSpan();
            f3 = span;
          }
          childSizeValid = true;
          child.setSize(f2, f3);
        }
      }
    }
    
    public float getMinorSpan()
    {
      if (max < minorSpan) {
        return max;
      }
      return Math.max(min, minorSpan);
    }
    
    public float getMinorOffset()
    {
      if (max < minorSpan)
      {
        float f = child.getAlignment(getMinorAxis());
        return (minorSpan - max) * f;
      }
      return 0.0F;
    }
    
    public float getMajorSpan()
    {
      return span;
    }
    
    public float getMajorOffset()
    {
      return offset;
    }
    
    public void setMajorOffset(float paramFloat)
    {
      offset = paramFloat;
    }
    
    public void preferenceChanged(boolean paramBoolean1, boolean paramBoolean2)
    {
      if (axis == 0)
      {
        if (paramBoolean1) {
          majorValid = false;
        }
        if (paramBoolean2) {
          minorValid = false;
        }
      }
      else
      {
        if (paramBoolean1) {
          minorValid = false;
        }
        if (paramBoolean2) {
          majorValid = false;
        }
      }
      childSizeValid = false;
    }
    
    public boolean isLayoutValid()
    {
      return (minorValid) && (majorValid) && (childSizeValid);
    }
  }
  
  class FlushTask
    implements Runnable
  {
    FlushTask() {}
    
    public void run()
    {
      flushRequirementChanges();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\AsyncBoxView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
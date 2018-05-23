package java.awt.dnd;

public class DragSourceDragEvent
  extends DragSourceEvent
{
  private static final long serialVersionUID = 481346297933902471L;
  private static final int JDK_1_3_MODIFIERS = 63;
  private static final int JDK_1_4_MODIFIERS = 16320;
  private int targetActions = 0;
  private int dropAction = 0;
  private int gestureModifiers = 0;
  private boolean invalidModifiers;
  
  public DragSourceDragEvent(DragSourceContext paramDragSourceContext, int paramInt1, int paramInt2, int paramInt3)
  {
    super(paramDragSourceContext);
    targetActions = paramInt2;
    gestureModifiers = paramInt3;
    dropAction = paramInt1;
    if ((paramInt3 & 0xC000) != 0) {
      invalidModifiers = true;
    } else if ((getGestureModifiers() != 0) && (getGestureModifiersEx() == 0)) {
      setNewModifiers();
    } else if ((getGestureModifiers() == 0) && (getGestureModifiersEx() != 0)) {
      setOldModifiers();
    } else {
      invalidModifiers = true;
    }
  }
  
  public DragSourceDragEvent(DragSourceContext paramDragSourceContext, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    super(paramDragSourceContext, paramInt4, paramInt5);
    targetActions = paramInt2;
    gestureModifiers = paramInt3;
    dropAction = paramInt1;
    if ((paramInt3 & 0xC000) != 0) {
      invalidModifiers = true;
    } else if ((getGestureModifiers() != 0) && (getGestureModifiersEx() == 0)) {
      setNewModifiers();
    } else if ((getGestureModifiers() == 0) && (getGestureModifiersEx() != 0)) {
      setOldModifiers();
    } else {
      invalidModifiers = true;
    }
  }
  
  public int getTargetActions()
  {
    return targetActions;
  }
  
  public int getGestureModifiers()
  {
    return invalidModifiers ? gestureModifiers : gestureModifiers & 0x3F;
  }
  
  public int getGestureModifiersEx()
  {
    return invalidModifiers ? gestureModifiers : gestureModifiers & 0x3FC0;
  }
  
  public int getUserAction()
  {
    return dropAction;
  }
  
  public int getDropAction()
  {
    return targetActions & getDragSourceContext().getSourceActions();
  }
  
  private void setNewModifiers()
  {
    if ((gestureModifiers & 0x10) != 0) {
      gestureModifiers |= 0x400;
    }
    if ((gestureModifiers & 0x8) != 0) {
      gestureModifiers |= 0x800;
    }
    if ((gestureModifiers & 0x4) != 0) {
      gestureModifiers |= 0x1000;
    }
    if ((gestureModifiers & 0x1) != 0) {
      gestureModifiers |= 0x40;
    }
    if ((gestureModifiers & 0x2) != 0) {
      gestureModifiers |= 0x80;
    }
    if ((gestureModifiers & 0x20) != 0) {
      gestureModifiers |= 0x2000;
    }
  }
  
  private void setOldModifiers()
  {
    if ((gestureModifiers & 0x400) != 0) {
      gestureModifiers |= 0x10;
    }
    if ((gestureModifiers & 0x800) != 0) {
      gestureModifiers |= 0x8;
    }
    if ((gestureModifiers & 0x1000) != 0) {
      gestureModifiers |= 0x4;
    }
    if ((gestureModifiers & 0x40) != 0) {
      gestureModifiers |= 0x1;
    }
    if ((gestureModifiers & 0x80) != 0) {
      gestureModifiers |= 0x2;
    }
    if ((gestureModifiers & 0x2000) != 0) {
      gestureModifiers |= 0x20;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\dnd\DragSourceDragEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
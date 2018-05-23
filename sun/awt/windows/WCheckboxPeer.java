package sun.awt.windows;

import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.event.ItemEvent;
import java.awt.peer.CheckboxPeer;

final class WCheckboxPeer
  extends WComponentPeer
  implements CheckboxPeer
{
  public native void setState(boolean paramBoolean);
  
  public native void setCheckboxGroup(CheckboxGroup paramCheckboxGroup);
  
  public native void setLabel(String paramString);
  
  private static native int getCheckMarkSize();
  
  public Dimension getMinimumSize()
  {
    String str = ((Checkbox)target).getLabel();
    int i = getCheckMarkSize();
    if (str == null) {
      str = "";
    }
    FontMetrics localFontMetrics = getFontMetrics(((Checkbox)target).getFont());
    return new Dimension(localFontMetrics.stringWidth(str) + i / 2 + i, Math.max(localFontMetrics.getHeight() + 8, i));
  }
  
  public boolean isFocusable()
  {
    return true;
  }
  
  WCheckboxPeer(Checkbox paramCheckbox)
  {
    super(paramCheckbox);
  }
  
  native void create(WComponentPeer paramWComponentPeer);
  
  void initialize()
  {
    Checkbox localCheckbox = (Checkbox)target;
    setState(localCheckbox.getState());
    setCheckboxGroup(localCheckbox.getCheckboxGroup());
    Color localColor = ((Component)target).getBackground();
    if (localColor != null) {
      setBackground(localColor);
    }
    super.initialize();
  }
  
  public boolean shouldClearRectBeforePaint()
  {
    return false;
  }
  
  void handleAction(final boolean paramBoolean)
  {
    final Checkbox localCheckbox = (Checkbox)target;
    WToolkit.executeOnEventHandlerThread(localCheckbox, new Runnable()
    {
      public void run()
      {
        CheckboxGroup localCheckboxGroup = localCheckbox.getCheckboxGroup();
        if ((localCheckboxGroup != null) && (localCheckbox == localCheckboxGroup.getSelectedCheckbox()) && (localCheckbox.getState())) {
          return;
        }
        localCheckbox.setState(paramBoolean);
        postEvent(new ItemEvent(localCheckbox, 701, localCheckbox.getLabel(), paramBoolean ? 1 : 2));
      }
    });
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\windows\WCheckboxPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
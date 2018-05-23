package javax.swing.plaf.basic;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.text.Element;
import javax.swing.text.PasswordView;
import javax.swing.text.View;

public class BasicPasswordFieldUI
  extends BasicTextFieldUI
{
  public BasicPasswordFieldUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new BasicPasswordFieldUI();
  }
  
  protected String getPropertyPrefix()
  {
    return "PasswordField";
  }
  
  protected void installDefaults()
  {
    super.installDefaults();
    String str = getPropertyPrefix();
    Character localCharacter = (Character)UIManager.getDefaults().get(str + ".echoChar");
    if (localCharacter != null) {
      LookAndFeel.installProperty(getComponent(), "echoChar", localCharacter);
    }
  }
  
  public View create(Element paramElement)
  {
    return new PasswordView(paramElement);
  }
  
  ActionMap createActionMap()
  {
    ActionMap localActionMap = super.createActionMap();
    if (localActionMap.get("select-word") != null)
    {
      Action localAction = localActionMap.get("select-line");
      if (localAction != null)
      {
        localActionMap.remove("select-word");
        localActionMap.put("select-word", localAction);
      }
    }
    return localActionMap;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\basic\BasicPasswordFieldUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
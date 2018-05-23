package javax.swing.text;

import javax.swing.Action;
import javax.swing.KeyStroke;

public abstract interface Keymap
{
  public abstract String getName();
  
  public abstract Action getDefaultAction();
  
  public abstract void setDefaultAction(Action paramAction);
  
  public abstract Action getAction(KeyStroke paramKeyStroke);
  
  public abstract KeyStroke[] getBoundKeyStrokes();
  
  public abstract Action[] getBoundActions();
  
  public abstract KeyStroke[] getKeyStrokesForAction(Action paramAction);
  
  public abstract boolean isLocallyDefined(KeyStroke paramKeyStroke);
  
  public abstract void addActionForKeyStroke(KeyStroke paramKeyStroke, Action paramAction);
  
  public abstract void removeKeyStrokeBinding(KeyStroke paramKeyStroke);
  
  public abstract void removeBindings();
  
  public abstract Keymap getResolveParent();
  
  public abstract void setResolveParent(Keymap paramKeymap);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\Keymap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
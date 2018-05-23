package java.awt;

import java.awt.event.KeyEvent;

@FunctionalInterface
public abstract interface KeyEventPostProcessor
{
  public abstract boolean postProcessKeyEvent(KeyEvent paramKeyEvent);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\KeyEventPostProcessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
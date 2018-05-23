package javax.swing.text;

import java.awt.Graphics;
import java.awt.Point;
import javax.swing.event.ChangeListener;

public abstract interface Caret
{
  public abstract void install(JTextComponent paramJTextComponent);
  
  public abstract void deinstall(JTextComponent paramJTextComponent);
  
  public abstract void paint(Graphics paramGraphics);
  
  public abstract void addChangeListener(ChangeListener paramChangeListener);
  
  public abstract void removeChangeListener(ChangeListener paramChangeListener);
  
  public abstract boolean isVisible();
  
  public abstract void setVisible(boolean paramBoolean);
  
  public abstract boolean isSelectionVisible();
  
  public abstract void setSelectionVisible(boolean paramBoolean);
  
  public abstract void setMagicCaretPosition(Point paramPoint);
  
  public abstract Point getMagicCaretPosition();
  
  public abstract void setBlinkRate(int paramInt);
  
  public abstract int getBlinkRate();
  
  public abstract int getDot();
  
  public abstract int getMark();
  
  public abstract void setDot(int paramInt);
  
  public abstract void moveDot(int paramInt);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\Caret.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
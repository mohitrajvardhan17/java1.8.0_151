package javax.swing.text;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Shape;
import java.awt.Toolkit;
import javax.swing.event.DocumentEvent;

public class LabelView
  extends GlyphView
  implements TabableView
{
  private Font font;
  private Color fg;
  private Color bg;
  private boolean underline;
  private boolean strike;
  private boolean superscript;
  private boolean subscript;
  
  public LabelView(Element paramElement)
  {
    super(paramElement);
  }
  
  final void sync()
  {
    if (font == null) {
      setPropertiesFromAttributes();
    }
  }
  
  protected void setUnderline(boolean paramBoolean)
  {
    underline = paramBoolean;
  }
  
  protected void setStrikeThrough(boolean paramBoolean)
  {
    strike = paramBoolean;
  }
  
  protected void setSuperscript(boolean paramBoolean)
  {
    superscript = paramBoolean;
  }
  
  protected void setSubscript(boolean paramBoolean)
  {
    subscript = paramBoolean;
  }
  
  protected void setBackground(Color paramColor)
  {
    bg = paramColor;
  }
  
  protected void setPropertiesFromAttributes()
  {
    AttributeSet localAttributeSet = getAttributes();
    if (localAttributeSet != null)
    {
      Document localDocument = getDocument();
      if ((localDocument instanceof StyledDocument))
      {
        StyledDocument localStyledDocument = (StyledDocument)localDocument;
        font = localStyledDocument.getFont(localAttributeSet);
        fg = localStyledDocument.getForeground(localAttributeSet);
        if (localAttributeSet.isDefined(StyleConstants.Background)) {
          bg = localStyledDocument.getBackground(localAttributeSet);
        } else {
          bg = null;
        }
        setUnderline(StyleConstants.isUnderline(localAttributeSet));
        setStrikeThrough(StyleConstants.isStrikeThrough(localAttributeSet));
        setSuperscript(StyleConstants.isSuperscript(localAttributeSet));
        setSubscript(StyleConstants.isSubscript(localAttributeSet));
      }
      else
      {
        throw new StateInvariantError("LabelView needs StyledDocument");
      }
    }
  }
  
  @Deprecated
  protected FontMetrics getFontMetrics()
  {
    sync();
    Container localContainer = getContainer();
    return localContainer != null ? localContainer.getFontMetrics(font) : Toolkit.getDefaultToolkit().getFontMetrics(font);
  }
  
  public Color getBackground()
  {
    sync();
    return bg;
  }
  
  public Color getForeground()
  {
    sync();
    return fg;
  }
  
  public Font getFont()
  {
    sync();
    return font;
  }
  
  public boolean isUnderline()
  {
    sync();
    return underline;
  }
  
  public boolean isStrikeThrough()
  {
    sync();
    return strike;
  }
  
  public boolean isSubscript()
  {
    sync();
    return subscript;
  }
  
  public boolean isSuperscript()
  {
    sync();
    return superscript;
  }
  
  public void changedUpdate(DocumentEvent paramDocumentEvent, Shape paramShape, ViewFactory paramViewFactory)
  {
    font = null;
    super.changedUpdate(paramDocumentEvent, paramShape, paramViewFactory);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\LabelView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
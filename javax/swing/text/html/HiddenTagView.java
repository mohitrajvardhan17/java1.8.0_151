package javax.swing.text.html;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Shape;
import java.awt.Toolkit;
import java.io.Serializable;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.ViewFactory;

class HiddenTagView
  extends EditableView
  implements DocumentListener
{
  float yAlign = 1.0F;
  boolean isSettingAttributes;
  static final int circleR = 3;
  static final int circleD = 6;
  static final int tagSize = 6;
  static final int padding = 3;
  static final Color UnknownTagBorderColor = Color.black;
  static final Border StartBorder = new StartTagBorder();
  static final Border EndBorder = new EndTagBorder();
  
  HiddenTagView(Element paramElement)
  {
    super(paramElement);
  }
  
  protected Component createComponent()
  {
    JTextField localJTextField = new JTextField(getElement().getName());
    Document localDocument = getDocument();
    Font localFont;
    if ((localDocument instanceof StyledDocument))
    {
      localFont = ((StyledDocument)localDocument).getFont(getAttributes());
      localJTextField.setFont(localFont);
    }
    else
    {
      localFont = localJTextField.getFont();
    }
    localJTextField.getDocument().addDocumentListener(this);
    updateYAlign(localFont);
    JPanel localJPanel = new JPanel(new BorderLayout());
    localJPanel.setBackground(null);
    if (isEndTag()) {
      localJPanel.setBorder(EndBorder);
    } else {
      localJPanel.setBorder(StartBorder);
    }
    localJPanel.add(localJTextField);
    return localJPanel;
  }
  
  public float getAlignment(int paramInt)
  {
    if (paramInt == 1) {
      return yAlign;
    }
    return 0.5F;
  }
  
  public float getMinimumSpan(int paramInt)
  {
    if ((paramInt == 0) && (isVisible())) {
      return Math.max(30.0F, super.getPreferredSpan(paramInt));
    }
    return super.getMinimumSpan(paramInt);
  }
  
  public float getPreferredSpan(int paramInt)
  {
    if ((paramInt == 0) && (isVisible())) {
      return Math.max(30.0F, super.getPreferredSpan(paramInt));
    }
    return super.getPreferredSpan(paramInt);
  }
  
  public float getMaximumSpan(int paramInt)
  {
    if ((paramInt == 0) && (isVisible())) {
      return Math.max(30.0F, super.getMaximumSpan(paramInt));
    }
    return super.getMaximumSpan(paramInt);
  }
  
  public void insertUpdate(DocumentEvent paramDocumentEvent)
  {
    updateModelFromText();
  }
  
  public void removeUpdate(DocumentEvent paramDocumentEvent)
  {
    updateModelFromText();
  }
  
  public void changedUpdate(DocumentEvent paramDocumentEvent)
  {
    updateModelFromText();
  }
  
  public void changedUpdate(DocumentEvent paramDocumentEvent, Shape paramShape, ViewFactory paramViewFactory)
  {
    if (!isSettingAttributes) {
      setTextFromModel();
    }
  }
  
  void updateYAlign(Font paramFont)
  {
    Container localContainer = getContainer();
    FontMetrics localFontMetrics = localContainer != null ? localContainer.getFontMetrics(paramFont) : Toolkit.getDefaultToolkit().getFontMetrics(paramFont);
    float f1 = localFontMetrics.getHeight();
    float f2 = localFontMetrics.getDescent();
    yAlign = (f1 > 0.0F ? (f1 - f2) / f1 : 0.0F);
  }
  
  void resetBorder()
  {
    Component localComponent = getComponent();
    if (localComponent != null) {
      if (isEndTag()) {
        ((JPanel)localComponent).setBorder(EndBorder);
      } else {
        ((JPanel)localComponent).setBorder(StartBorder);
      }
    }
  }
  
  void setTextFromModel()
  {
    if (SwingUtilities.isEventDispatchThread()) {
      _setTextFromModel();
    } else {
      SwingUtilities.invokeLater(new Runnable()
      {
        public void run()
        {
          _setTextFromModel();
        }
      });
    }
  }
  
  void _setTextFromModel()
  {
    Document localDocument = getDocument();
    try
    {
      isSettingAttributes = true;
      if ((localDocument instanceof AbstractDocument)) {
        ((AbstractDocument)localDocument).readLock();
      }
      JTextComponent localJTextComponent = getTextComponent();
      if (localJTextComponent != null)
      {
        localJTextComponent.setText(getRepresentedText());
        resetBorder();
        Container localContainer = getContainer();
        if (localContainer != null)
        {
          preferenceChanged(this, true, true);
          localContainer.repaint();
        }
      }
    }
    finally
    {
      isSettingAttributes = false;
      if ((localDocument instanceof AbstractDocument)) {
        ((AbstractDocument)localDocument).readUnlock();
      }
    }
  }
  
  void updateModelFromText()
  {
    if (!isSettingAttributes) {
      if (SwingUtilities.isEventDispatchThread()) {
        _updateModelFromText();
      } else {
        SwingUtilities.invokeLater(new Runnable()
        {
          public void run()
          {
            _updateModelFromText();
          }
        });
      }
    }
  }
  
  void _updateModelFromText()
  {
    Document localDocument = getDocument();
    Object localObject1 = getElement().getAttributes().getAttribute(StyleConstants.NameAttribute);
    if (((localObject1 instanceof HTML.UnknownTag)) && ((localDocument instanceof StyledDocument)))
    {
      SimpleAttributeSet localSimpleAttributeSet = new SimpleAttributeSet();
      JTextComponent localJTextComponent = getTextComponent();
      if (localJTextComponent != null)
      {
        String str = localJTextComponent.getText();
        isSettingAttributes = true;
        try
        {
          localSimpleAttributeSet.addAttribute(StyleConstants.NameAttribute, new HTML.UnknownTag(str));
          ((StyledDocument)localDocument).setCharacterAttributes(getStartOffset(), getEndOffset() - getStartOffset(), localSimpleAttributeSet, false);
        }
        finally
        {
          isSettingAttributes = false;
        }
      }
    }
  }
  
  JTextComponent getTextComponent()
  {
    Component localComponent = getComponent();
    return localComponent == null ? null : (JTextComponent)((Container)localComponent).getComponent(0);
  }
  
  String getRepresentedText()
  {
    String str = getElement().getName();
    return str == null ? "" : str;
  }
  
  boolean isEndTag()
  {
    AttributeSet localAttributeSet = getElement().getAttributes();
    if (localAttributeSet != null)
    {
      Object localObject = localAttributeSet.getAttribute(HTML.Attribute.ENDTAG);
      if ((localObject != null) && ((localObject instanceof String)) && (((String)localObject).equals("true"))) {
        return true;
      }
    }
    return false;
  }
  
  static class EndTagBorder
    implements Border, Serializable
  {
    EndTagBorder() {}
    
    public void paintBorder(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      paramGraphics.setColor(HiddenTagView.UnknownTagBorderColor);
      paramInt1 += 3;
      paramInt3 -= 6;
      paramGraphics.drawLine(paramInt1 + paramInt3 - 1, paramInt2 + 3, paramInt1 + paramInt3 - 1, paramInt2 + paramInt4 - 3);
      paramGraphics.drawArc(paramInt1 + paramInt3 - 6 - 1, paramInt2 + paramInt4 - 6 - 1, 6, 6, 270, 90);
      paramGraphics.drawArc(paramInt1 + paramInt3 - 6 - 1, paramInt2, 6, 6, 0, 90);
      paramGraphics.drawLine(paramInt1 + 6, paramInt2, paramInt1 + paramInt3 - 3, paramInt2);
      paramGraphics.drawLine(paramInt1 + 6, paramInt2 + paramInt4 - 1, paramInt1 + paramInt3 - 3, paramInt2 + paramInt4 - 1);
      paramGraphics.drawLine(paramInt1 + 6, paramInt2, paramInt1, paramInt2 + paramInt4 / 2);
      paramGraphics.drawLine(paramInt1 + 6, paramInt2 + paramInt4, paramInt1, paramInt2 + paramInt4 / 2);
    }
    
    public Insets getBorderInsets(Component paramComponent)
    {
      return new Insets(2, 11, 2, 5);
    }
    
    public boolean isBorderOpaque()
    {
      return false;
    }
  }
  
  static class StartTagBorder
    implements Border, Serializable
  {
    StartTagBorder() {}
    
    public void paintBorder(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      paramGraphics.setColor(HiddenTagView.UnknownTagBorderColor);
      paramInt1 += 3;
      paramInt3 -= 6;
      paramGraphics.drawLine(paramInt1, paramInt2 + 3, paramInt1, paramInt2 + paramInt4 - 3);
      paramGraphics.drawArc(paramInt1, paramInt2 + paramInt4 - 6 - 1, 6, 6, 180, 90);
      paramGraphics.drawArc(paramInt1, paramInt2, 6, 6, 90, 90);
      paramGraphics.drawLine(paramInt1 + 3, paramInt2, paramInt1 + paramInt3 - 6, paramInt2);
      paramGraphics.drawLine(paramInt1 + 3, paramInt2 + paramInt4 - 1, paramInt1 + paramInt3 - 6, paramInt2 + paramInt4 - 1);
      paramGraphics.drawLine(paramInt1 + paramInt3 - 6, paramInt2, paramInt1 + paramInt3 - 1, paramInt2 + paramInt4 / 2);
      paramGraphics.drawLine(paramInt1 + paramInt3 - 6, paramInt2 + paramInt4, paramInt1 + paramInt3 - 1, paramInt2 + paramInt4 / 2);
    }
    
    public Insets getBorderInsets(Component paramComponent)
    {
      return new Insets(2, 5, 2, 11);
    }
    
    public boolean isBorderOpaque()
    {
      return false;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\html\HiddenTagView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
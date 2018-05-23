package javax.swing;

import java.awt.Component;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Map;
import javax.swing.plaf.ComponentUI;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import javax.swing.text.Element;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyledEditorKit;

public class JTextPane
  extends JEditorPane
{
  private static final String uiClassID = "TextPaneUI";
  
  public JTextPane()
  {
    EditorKit localEditorKit = createDefaultEditorKit();
    String str = localEditorKit.getContentType();
    if ((str != null) && (getEditorKitClassNameForContentType(str) == defaultEditorKitMap.get(str))) {
      setEditorKitForContentType(str, localEditorKit);
    }
    setEditorKit(localEditorKit);
  }
  
  public JTextPane(StyledDocument paramStyledDocument)
  {
    this();
    setStyledDocument(paramStyledDocument);
  }
  
  public String getUIClassID()
  {
    return "TextPaneUI";
  }
  
  public void setDocument(Document paramDocument)
  {
    if ((paramDocument instanceof StyledDocument)) {
      super.setDocument(paramDocument);
    } else {
      throw new IllegalArgumentException("Model must be StyledDocument");
    }
  }
  
  public void setStyledDocument(StyledDocument paramStyledDocument)
  {
    super.setDocument(paramStyledDocument);
  }
  
  public StyledDocument getStyledDocument()
  {
    return (StyledDocument)getDocument();
  }
  
  public void replaceSelection(String paramString)
  {
    replaceSelection(paramString, true);
  }
  
  private void replaceSelection(String paramString, boolean paramBoolean)
  {
    if ((paramBoolean) && (!isEditable()))
    {
      UIManager.getLookAndFeel().provideErrorFeedback(this);
      return;
    }
    StyledDocument localStyledDocument = getStyledDocument();
    if (localStyledDocument != null) {
      try
      {
        Caret localCaret = getCaret();
        boolean bool = saveComposedText(localCaret.getDot());
        int i = Math.min(localCaret.getDot(), localCaret.getMark());
        int j = Math.max(localCaret.getDot(), localCaret.getMark());
        AttributeSet localAttributeSet = getInputAttributes().copyAttributes();
        if ((localStyledDocument instanceof AbstractDocument))
        {
          ((AbstractDocument)localStyledDocument).replace(i, j - i, paramString, localAttributeSet);
        }
        else
        {
          if (i != j) {
            localStyledDocument.remove(i, j - i);
          }
          if ((paramString != null) && (paramString.length() > 0)) {
            localStyledDocument.insertString(i, paramString, localAttributeSet);
          }
        }
        if (bool) {
          restoreComposedText();
        }
      }
      catch (BadLocationException localBadLocationException)
      {
        UIManager.getLookAndFeel().provideErrorFeedback(this);
      }
    }
  }
  
  public void insertComponent(Component paramComponent)
  {
    MutableAttributeSet localMutableAttributeSet = getInputAttributes();
    localMutableAttributeSet.removeAttributes(localMutableAttributeSet);
    StyleConstants.setComponent(localMutableAttributeSet, paramComponent);
    replaceSelection(" ", false);
    localMutableAttributeSet.removeAttributes(localMutableAttributeSet);
  }
  
  public void insertIcon(Icon paramIcon)
  {
    MutableAttributeSet localMutableAttributeSet = getInputAttributes();
    localMutableAttributeSet.removeAttributes(localMutableAttributeSet);
    StyleConstants.setIcon(localMutableAttributeSet, paramIcon);
    replaceSelection(" ", false);
    localMutableAttributeSet.removeAttributes(localMutableAttributeSet);
  }
  
  public Style addStyle(String paramString, Style paramStyle)
  {
    StyledDocument localStyledDocument = getStyledDocument();
    return localStyledDocument.addStyle(paramString, paramStyle);
  }
  
  public void removeStyle(String paramString)
  {
    StyledDocument localStyledDocument = getStyledDocument();
    localStyledDocument.removeStyle(paramString);
  }
  
  public Style getStyle(String paramString)
  {
    StyledDocument localStyledDocument = getStyledDocument();
    return localStyledDocument.getStyle(paramString);
  }
  
  public void setLogicalStyle(Style paramStyle)
  {
    StyledDocument localStyledDocument = getStyledDocument();
    localStyledDocument.setLogicalStyle(getCaretPosition(), paramStyle);
  }
  
  public Style getLogicalStyle()
  {
    StyledDocument localStyledDocument = getStyledDocument();
    return localStyledDocument.getLogicalStyle(getCaretPosition());
  }
  
  public AttributeSet getCharacterAttributes()
  {
    StyledDocument localStyledDocument = getStyledDocument();
    Element localElement = localStyledDocument.getCharacterElement(getCaretPosition());
    if (localElement != null) {
      return localElement.getAttributes();
    }
    return null;
  }
  
  public void setCharacterAttributes(AttributeSet paramAttributeSet, boolean paramBoolean)
  {
    int i = getSelectionStart();
    int j = getSelectionEnd();
    Object localObject;
    if (i != j)
    {
      localObject = getStyledDocument();
      ((StyledDocument)localObject).setCharacterAttributes(i, j - i, paramAttributeSet, paramBoolean);
    }
    else
    {
      localObject = getInputAttributes();
      if (paramBoolean) {
        ((MutableAttributeSet)localObject).removeAttributes((AttributeSet)localObject);
      }
      ((MutableAttributeSet)localObject).addAttributes(paramAttributeSet);
    }
  }
  
  public AttributeSet getParagraphAttributes()
  {
    StyledDocument localStyledDocument = getStyledDocument();
    Element localElement = localStyledDocument.getParagraphElement(getCaretPosition());
    if (localElement != null) {
      return localElement.getAttributes();
    }
    return null;
  }
  
  public void setParagraphAttributes(AttributeSet paramAttributeSet, boolean paramBoolean)
  {
    int i = getSelectionStart();
    int j = getSelectionEnd();
    StyledDocument localStyledDocument = getStyledDocument();
    localStyledDocument.setParagraphAttributes(i, j - i, paramAttributeSet, paramBoolean);
  }
  
  public MutableAttributeSet getInputAttributes()
  {
    return getStyledEditorKit().getInputAttributes();
  }
  
  protected final StyledEditorKit getStyledEditorKit()
  {
    return (StyledEditorKit)getEditorKit();
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    if (getUIClassID().equals("TextPaneUI"))
    {
      byte b = JComponent.getWriteObjCounter(this);
      b = (byte)(b - 1);
      JComponent.setWriteObjCounter(this, b);
      if ((b == 0) && (ui != null)) {
        ui.installUI(this);
      }
    }
  }
  
  protected EditorKit createDefaultEditorKit()
  {
    return new StyledEditorKit();
  }
  
  public final void setEditorKit(EditorKit paramEditorKit)
  {
    if ((paramEditorKit instanceof StyledEditorKit)) {
      super.setEditorKit(paramEditorKit);
    } else {
      throw new IllegalArgumentException("Must be StyledEditorKit");
    }
  }
  
  protected String paramString()
  {
    return super.paramString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\JTextPane.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
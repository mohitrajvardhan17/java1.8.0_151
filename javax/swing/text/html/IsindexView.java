package javax.swing.text.html;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.text.AttributeSet;
import javax.swing.text.ComponentView;
import javax.swing.text.Element;

class IsindexView
  extends ComponentView
  implements ActionListener
{
  JTextField textField;
  
  public IsindexView(Element paramElement)
  {
    super(paramElement);
  }
  
  public Component createComponent()
  {
    AttributeSet localAttributeSet = getElement().getAttributes();
    JPanel localJPanel = new JPanel(new BorderLayout());
    localJPanel.setBackground(null);
    String str = (String)localAttributeSet.getAttribute(HTML.Attribute.PROMPT);
    if (str == null) {
      str = UIManager.getString("IsindexView.prompt");
    }
    JLabel localJLabel = new JLabel(str);
    textField = new JTextField();
    textField.addActionListener(this);
    localJPanel.add(localJLabel, "West");
    localJPanel.add(textField, "Center");
    localJPanel.setAlignmentY(1.0F);
    localJPanel.setOpaque(false);
    return localJPanel;
  }
  
  public void actionPerformed(ActionEvent paramActionEvent)
  {
    String str1 = textField.getText();
    if (str1 != null) {
      str1 = URLEncoder.encode(str1);
    }
    AttributeSet localAttributeSet = getElement().getAttributes();
    HTMLDocument localHTMLDocument = (HTMLDocument)getElement().getDocument();
    String str2 = (String)localAttributeSet.getAttribute(HTML.Attribute.ACTION);
    if (str2 == null) {
      str2 = localHTMLDocument.getBase().toString();
    }
    try
    {
      URL localURL = new URL(str2 + "?" + str1);
      JEditorPane localJEditorPane = (JEditorPane)getContainer();
      localJEditorPane.setPage(localURL);
    }
    catch (MalformedURLException localMalformedURLException) {}catch (IOException localIOException) {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\html\IsindexView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
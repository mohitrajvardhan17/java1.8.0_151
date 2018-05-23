package javax.swing.text;

import java.io.Serializable;
import javax.swing.JFormattedTextField;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.JFormattedTextField.AbstractFormatterFactory;

public class DefaultFormatterFactory
  extends JFormattedTextField.AbstractFormatterFactory
  implements Serializable
{
  private JFormattedTextField.AbstractFormatter defaultFormat;
  private JFormattedTextField.AbstractFormatter displayFormat;
  private JFormattedTextField.AbstractFormatter editFormat;
  private JFormattedTextField.AbstractFormatter nullFormat;
  
  public DefaultFormatterFactory() {}
  
  public DefaultFormatterFactory(JFormattedTextField.AbstractFormatter paramAbstractFormatter)
  {
    this(paramAbstractFormatter, null);
  }
  
  public DefaultFormatterFactory(JFormattedTextField.AbstractFormatter paramAbstractFormatter1, JFormattedTextField.AbstractFormatter paramAbstractFormatter2)
  {
    this(paramAbstractFormatter1, paramAbstractFormatter2, null);
  }
  
  public DefaultFormatterFactory(JFormattedTextField.AbstractFormatter paramAbstractFormatter1, JFormattedTextField.AbstractFormatter paramAbstractFormatter2, JFormattedTextField.AbstractFormatter paramAbstractFormatter3)
  {
    this(paramAbstractFormatter1, paramAbstractFormatter2, paramAbstractFormatter3, null);
  }
  
  public DefaultFormatterFactory(JFormattedTextField.AbstractFormatter paramAbstractFormatter1, JFormattedTextField.AbstractFormatter paramAbstractFormatter2, JFormattedTextField.AbstractFormatter paramAbstractFormatter3, JFormattedTextField.AbstractFormatter paramAbstractFormatter4)
  {
    defaultFormat = paramAbstractFormatter1;
    displayFormat = paramAbstractFormatter2;
    editFormat = paramAbstractFormatter3;
    nullFormat = paramAbstractFormatter4;
  }
  
  public void setDefaultFormatter(JFormattedTextField.AbstractFormatter paramAbstractFormatter)
  {
    defaultFormat = paramAbstractFormatter;
  }
  
  public JFormattedTextField.AbstractFormatter getDefaultFormatter()
  {
    return defaultFormat;
  }
  
  public void setDisplayFormatter(JFormattedTextField.AbstractFormatter paramAbstractFormatter)
  {
    displayFormat = paramAbstractFormatter;
  }
  
  public JFormattedTextField.AbstractFormatter getDisplayFormatter()
  {
    return displayFormat;
  }
  
  public void setEditFormatter(JFormattedTextField.AbstractFormatter paramAbstractFormatter)
  {
    editFormat = paramAbstractFormatter;
  }
  
  public JFormattedTextField.AbstractFormatter getEditFormatter()
  {
    return editFormat;
  }
  
  public void setNullFormatter(JFormattedTextField.AbstractFormatter paramAbstractFormatter)
  {
    nullFormat = paramAbstractFormatter;
  }
  
  public JFormattedTextField.AbstractFormatter getNullFormatter()
  {
    return nullFormat;
  }
  
  public JFormattedTextField.AbstractFormatter getFormatter(JFormattedTextField paramJFormattedTextField)
  {
    JFormattedTextField.AbstractFormatter localAbstractFormatter = null;
    if (paramJFormattedTextField == null) {
      return null;
    }
    Object localObject = paramJFormattedTextField.getValue();
    if (localObject == null) {
      localAbstractFormatter = getNullFormatter();
    }
    if (localAbstractFormatter == null)
    {
      if (paramJFormattedTextField.hasFocus()) {
        localAbstractFormatter = getEditFormatter();
      } else {
        localAbstractFormatter = getDisplayFormatter();
      }
      if (localAbstractFormatter == null) {
        localAbstractFormatter = getDefaultFormatter();
      }
    }
    return localAbstractFormatter;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\DefaultFormatterFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
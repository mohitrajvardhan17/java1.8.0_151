package com.sun.beans.editors;

import java.awt.Canvas;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Graphics;
import java.awt.Panel;
import java.awt.Rectangle;
import java.awt.TextField;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyEditor;

public class ColorEditor
  extends Panel
  implements PropertyEditor
{
  private static final long serialVersionUID = 1781257185164716054L;
  private String[] colorNames = { " ", "white", "lightGray", "gray", "darkGray", "black", "red", "pink", "orange", "yellow", "green", "magenta", "cyan", "blue" };
  private Color[] colors = { null, Color.white, Color.lightGray, Color.gray, Color.darkGray, Color.black, Color.red, Color.pink, Color.orange, Color.yellow, Color.green, Color.magenta, Color.cyan, Color.blue };
  private Canvas sample;
  private int sampleHeight = 20;
  private int sampleWidth = 40;
  private int hPad = 5;
  private int ourWidth;
  private Color color;
  private TextField text;
  private Choice choser;
  private PropertyChangeSupport support = new PropertyChangeSupport(this);
  
  public ColorEditor()
  {
    setLayout(null);
    ourWidth = hPad;
    Panel localPanel = new Panel();
    localPanel.setLayout(null);
    localPanel.setBackground(Color.black);
    sample = new Canvas();
    localPanel.add(sample);
    sample.reshape(2, 2, sampleWidth, sampleHeight);
    add(localPanel);
    localPanel.reshape(ourWidth, 2, sampleWidth + 4, sampleHeight + 4);
    ourWidth += sampleWidth + 4 + hPad;
    text = new TextField("", 14);
    add(text);
    text.reshape(ourWidth, 0, 100, 30);
    ourWidth += 100 + hPad;
    choser = new Choice();
    int i = 0;
    for (int j = 0; j < colorNames.length; j++) {
      choser.addItem(colorNames[j]);
    }
    add(choser);
    choser.reshape(ourWidth, 0, 100, 30);
    ourWidth += 100 + hPad;
    resize(ourWidth, 40);
  }
  
  public void setValue(Object paramObject)
  {
    Color localColor = (Color)paramObject;
    changeColor(localColor);
  }
  
  public Dimension preferredSize()
  {
    return new Dimension(ourWidth, 40);
  }
  
  public boolean keyUp(Event paramEvent, int paramInt)
  {
    if (target == text) {
      try
      {
        setAsText(text.getText());
      }
      catch (IllegalArgumentException localIllegalArgumentException) {}
    }
    return false;
  }
  
  public void setAsText(String paramString)
    throws IllegalArgumentException
  {
    if (paramString == null)
    {
      changeColor(null);
      return;
    }
    int i = paramString.indexOf(',');
    int j = paramString.indexOf(',', i + 1);
    if ((i < 0) || (j < 0)) {
      throw new IllegalArgumentException(paramString);
    }
    try
    {
      int k = Integer.parseInt(paramString.substring(0, i));
      int m = Integer.parseInt(paramString.substring(i + 1, j));
      int n = Integer.parseInt(paramString.substring(j + 1));
      Color localColor = new Color(k, m, n);
      changeColor(localColor);
    }
    catch (Exception localException)
    {
      throw new IllegalArgumentException(paramString);
    }
  }
  
  public boolean action(Event paramEvent, Object paramObject)
  {
    if (target == choser) {
      changeColor(colors[choser.getSelectedIndex()]);
    }
    return false;
  }
  
  public String getJavaInitializationString()
  {
    return color != null ? "new java.awt.Color(" + color.getRGB() + ",true)" : "null";
  }
  
  private void changeColor(Color paramColor)
  {
    if (paramColor == null)
    {
      color = null;
      text.setText("");
      return;
    }
    color = paramColor;
    text.setText("" + paramColor.getRed() + "," + paramColor.getGreen() + "," + paramColor.getBlue());
    int i = 0;
    for (int j = 0; j < colorNames.length; j++) {
      if (color.equals(colors[j])) {
        i = j;
      }
    }
    choser.select(i);
    sample.setBackground(color);
    sample.repaint();
    support.firePropertyChange("", null, null);
  }
  
  public Object getValue()
  {
    return color;
  }
  
  public boolean isPaintable()
  {
    return true;
  }
  
  public void paintValue(Graphics paramGraphics, Rectangle paramRectangle)
  {
    Color localColor = paramGraphics.getColor();
    paramGraphics.setColor(Color.black);
    paramGraphics.drawRect(x, y, width - 3, height - 3);
    paramGraphics.setColor(color);
    paramGraphics.fillRect(x + 1, y + 1, width - 4, height - 4);
    paramGraphics.setColor(localColor);
  }
  
  public String getAsText()
  {
    return color != null ? color.getRed() + "," + color.getGreen() + "," + color.getBlue() : null;
  }
  
  public String[] getTags()
  {
    return null;
  }
  
  public Component getCustomEditor()
  {
    return this;
  }
  
  public boolean supportsCustomEditor()
  {
    return true;
  }
  
  public void addPropertyChangeListener(PropertyChangeListener paramPropertyChangeListener)
  {
    support.addPropertyChangeListener(paramPropertyChangeListener);
  }
  
  public void removePropertyChangeListener(PropertyChangeListener paramPropertyChangeListener)
  {
    support.removePropertyChangeListener(paramPropertyChangeListener);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\beans\editors\ColorEditor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
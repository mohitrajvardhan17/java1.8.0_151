package com.sun.beans.editors;

import java.awt.Choice;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyEditor;
import java.io.PrintStream;

public class FontEditor
  extends Panel
  implements PropertyEditor
{
  private static final long serialVersionUID = 6732704486002715933L;
  private Font font;
  private Toolkit toolkit;
  private String sampleText = "Abcde...";
  private Label sample;
  private Choice familyChoser;
  private Choice styleChoser;
  private Choice sizeChoser;
  private String[] fonts;
  private String[] styleNames = { "plain", "bold", "italic" };
  private int[] styles = { 0, 1, 2 };
  private int[] pointSizes = { 3, 5, 8, 10, 12, 14, 18, 24, 36, 48 };
  private PropertyChangeSupport support = new PropertyChangeSupport(this);
  
  public FontEditor()
  {
    setLayout(null);
    toolkit = Toolkit.getDefaultToolkit();
    fonts = toolkit.getFontList();
    familyChoser = new Choice();
    for (int i = 0; i < fonts.length; i++) {
      familyChoser.addItem(fonts[i]);
    }
    add(familyChoser);
    familyChoser.reshape(20, 5, 100, 30);
    styleChoser = new Choice();
    for (i = 0; i < styleNames.length; i++) {
      styleChoser.addItem(styleNames[i]);
    }
    add(styleChoser);
    styleChoser.reshape(145, 5, 70, 30);
    sizeChoser = new Choice();
    for (i = 0; i < pointSizes.length; i++) {
      sizeChoser.addItem("" + pointSizes[i]);
    }
    add(sizeChoser);
    sizeChoser.reshape(220, 5, 70, 30);
    resize(300, 40);
  }
  
  public Dimension preferredSize()
  {
    return new Dimension(300, 40);
  }
  
  public void setValue(Object paramObject)
  {
    font = ((Font)paramObject);
    if (font == null) {
      return;
    }
    changeFont(font);
    for (int i = 0; i < fonts.length; i++) {
      if (fonts[i].equals(font.getFamily()))
      {
        familyChoser.select(i);
        break;
      }
    }
    for (i = 0; i < styleNames.length; i++) {
      if (font.getStyle() == styles[i])
      {
        styleChoser.select(i);
        break;
      }
    }
    for (i = 0; i < pointSizes.length; i++) {
      if (font.getSize() <= pointSizes[i])
      {
        sizeChoser.select(i);
        break;
      }
    }
  }
  
  private void changeFont(Font paramFont)
  {
    font = paramFont;
    if (sample != null) {
      remove(sample);
    }
    sample = new Label(sampleText);
    sample.setFont(font);
    add(sample);
    Container localContainer = getParent();
    if (localContainer != null)
    {
      localContainer.invalidate();
      localContainer.layout();
    }
    invalidate();
    layout();
    repaint();
    support.firePropertyChange("", null, null);
  }
  
  public Object getValue()
  {
    return font;
  }
  
  public String getJavaInitializationString()
  {
    if (font == null) {
      return "null";
    }
    return "new java.awt.Font(\"" + font.getName() + "\", " + font.getStyle() + ", " + font.getSize() + ")";
  }
  
  public boolean action(Event paramEvent, Object paramObject)
  {
    String str = familyChoser.getSelectedItem();
    int i = styles[styleChoser.getSelectedIndex()];
    int j = pointSizes[sizeChoser.getSelectedIndex()];
    try
    {
      Font localFont = new Font(str, i, j);
      changeFont(localFont);
    }
    catch (Exception localException)
    {
      System.err.println("Couldn't create font " + str + "-" + styleNames[i] + "-" + j);
    }
    return false;
  }
  
  public boolean isPaintable()
  {
    return true;
  }
  
  public void paintValue(Graphics paramGraphics, Rectangle paramRectangle)
  {
    Font localFont = paramGraphics.getFont();
    paramGraphics.setFont(font);
    FontMetrics localFontMetrics = paramGraphics.getFontMetrics();
    int i = (height - localFontMetrics.getAscent()) / 2;
    paramGraphics.drawString(sampleText, 0, height - i);
    paramGraphics.setFont(localFont);
  }
  
  public String getAsText()
  {
    if (font == null) {
      return null;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(font.getName());
    localStringBuilder.append(' ');
    boolean bool1 = font.isBold();
    if (bool1) {
      localStringBuilder.append("BOLD");
    }
    boolean bool2 = font.isItalic();
    if (bool2) {
      localStringBuilder.append("ITALIC");
    }
    if ((bool1) || (bool2)) {
      localStringBuilder.append(' ');
    }
    localStringBuilder.append(font.getSize());
    return localStringBuilder.toString();
  }
  
  public void setAsText(String paramString)
    throws IllegalArgumentException
  {
    setValue(paramString == null ? null : Font.decode(paramString));
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\beans\editors\FontEditor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package javax.print;

import javax.print.attribute.Attribute;
import javax.print.attribute.AttributeSet;
import javax.print.attribute.PrintServiceAttribute;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.event.PrintServiceAttributeListener;

public abstract interface PrintService
{
  public abstract String getName();
  
  public abstract DocPrintJob createPrintJob();
  
  public abstract void addPrintServiceAttributeListener(PrintServiceAttributeListener paramPrintServiceAttributeListener);
  
  public abstract void removePrintServiceAttributeListener(PrintServiceAttributeListener paramPrintServiceAttributeListener);
  
  public abstract PrintServiceAttributeSet getAttributes();
  
  public abstract <T extends PrintServiceAttribute> T getAttribute(Class<T> paramClass);
  
  public abstract DocFlavor[] getSupportedDocFlavors();
  
  public abstract boolean isDocFlavorSupported(DocFlavor paramDocFlavor);
  
  public abstract Class<?>[] getSupportedAttributeCategories();
  
  public abstract boolean isAttributeCategorySupported(Class<? extends Attribute> paramClass);
  
  public abstract Object getDefaultAttributeValue(Class<? extends Attribute> paramClass);
  
  public abstract Object getSupportedAttributeValues(Class<? extends Attribute> paramClass, DocFlavor paramDocFlavor, AttributeSet paramAttributeSet);
  
  public abstract boolean isAttributeValueSupported(Attribute paramAttribute, DocFlavor paramDocFlavor, AttributeSet paramAttributeSet);
  
  public abstract AttributeSet getUnsupportedAttributes(DocFlavor paramDocFlavor, AttributeSet paramAttributeSet);
  
  public abstract ServiceUIFactory getServiceUIFactory();
  
  public abstract boolean equals(Object paramObject);
  
  public abstract int hashCode();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\print\PrintService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
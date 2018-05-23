package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import com.sun.xml.internal.bind.v2.runtime.JaxBeanInfo;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import javax.xml.bind.Unmarshaller.Listener;
import javax.xml.bind.helpers.ValidationEventImpl;
import javax.xml.namespace.QName;
import org.xml.sax.SAXException;

public abstract class Loader
{
  protected boolean expectText;
  
  protected Loader(boolean paramBoolean)
  {
    expectText = paramBoolean;
  }
  
  protected Loader() {}
  
  public void startElement(UnmarshallingContext.State paramState, TagName paramTagName)
    throws SAXException
  {}
  
  public void childElement(UnmarshallingContext.State paramState, TagName paramTagName)
    throws SAXException
  {
    reportUnexpectedChildElement(paramTagName, true);
    paramState.setLoader(Discarder.INSTANCE);
    paramState.setReceiver(null);
  }
  
  protected final void reportUnexpectedChildElement(TagName paramTagName, boolean paramBoolean)
    throws SAXException
  {
    if (paramBoolean)
    {
      UnmarshallingContext localUnmarshallingContext = UnmarshallingContext.getInstance();
      if ((!parent.hasEventHandler()) || (!localUnmarshallingContext.shouldErrorBeReported())) {
        return;
      }
    }
    if ((uri != uri.intern()) || (local != local.intern())) {
      reportError(Messages.UNINTERNED_STRINGS.format(new Object[0]), paramBoolean);
    } else {
      reportError(Messages.UNEXPECTED_ELEMENT.format(new Object[] { uri, local, computeExpectedElements() }), paramBoolean);
    }
  }
  
  public Collection<QName> getExpectedChildElements()
  {
    return Collections.emptyList();
  }
  
  public Collection<QName> getExpectedAttributes()
  {
    return Collections.emptyList();
  }
  
  public void text(UnmarshallingContext.State paramState, CharSequence paramCharSequence)
    throws SAXException
  {
    paramCharSequence = paramCharSequence.toString().replace('\r', ' ').replace('\n', ' ').replace('\t', ' ').trim();
    reportError(Messages.UNEXPECTED_TEXT.format(new Object[] { paramCharSequence }), true);
  }
  
  public final boolean expectText()
  {
    return expectText;
  }
  
  public void leaveElement(UnmarshallingContext.State paramState, TagName paramTagName)
    throws SAXException
  {}
  
  private String computeExpectedElements()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    Iterator localIterator = getExpectedChildElements().iterator();
    while (localIterator.hasNext())
    {
      QName localQName = (QName)localIterator.next();
      if (localStringBuilder.length() != 0) {
        localStringBuilder.append(',');
      }
      localStringBuilder.append("<{").append(localQName.getNamespaceURI()).append('}').append(localQName.getLocalPart()).append('>');
    }
    if (localStringBuilder.length() == 0) {
      return "(none)";
    }
    return localStringBuilder.toString();
  }
  
  protected final void fireBeforeUnmarshal(JaxBeanInfo paramJaxBeanInfo, Object paramObject, UnmarshallingContext.State paramState)
    throws SAXException
  {
    if (paramJaxBeanInfo.lookForLifecycleMethods())
    {
      UnmarshallingContext localUnmarshallingContext = paramState.getContext();
      Unmarshaller.Listener localListener = parent.getListener();
      if (paramJaxBeanInfo.hasBeforeUnmarshalMethod()) {
        paramJaxBeanInfo.invokeBeforeUnmarshalMethod(parent, paramObject, paramState.getPrev().getTarget());
      }
      if (localListener != null) {
        localListener.beforeUnmarshal(paramObject, paramState.getPrev().getTarget());
      }
    }
  }
  
  protected final void fireAfterUnmarshal(JaxBeanInfo paramJaxBeanInfo, Object paramObject, UnmarshallingContext.State paramState)
    throws SAXException
  {
    if (paramJaxBeanInfo.lookForLifecycleMethods())
    {
      UnmarshallingContext localUnmarshallingContext = paramState.getContext();
      Unmarshaller.Listener localListener = parent.getListener();
      if (paramJaxBeanInfo.hasAfterUnmarshalMethod()) {
        paramJaxBeanInfo.invokeAfterUnmarshalMethod(parent, paramObject, paramState.getTarget());
      }
      if (localListener != null) {
        localListener.afterUnmarshal(paramObject, paramState.getTarget());
      }
    }
  }
  
  protected static void handleGenericException(Exception paramException)
    throws SAXException
  {
    handleGenericException(paramException, false);
  }
  
  public static void handleGenericException(Exception paramException, boolean paramBoolean)
    throws SAXException
  {
    reportError(paramException.getMessage(), paramException, paramBoolean);
  }
  
  public static void handleGenericError(Error paramError)
    throws SAXException
  {
    reportError(paramError.getMessage(), false);
  }
  
  protected static void reportError(String paramString, boolean paramBoolean)
    throws SAXException
  {
    reportError(paramString, null, paramBoolean);
  }
  
  public static void reportError(String paramString, Exception paramException, boolean paramBoolean)
    throws SAXException
  {
    UnmarshallingContext localUnmarshallingContext = UnmarshallingContext.getInstance();
    localUnmarshallingContext.handleEvent(new ValidationEventImpl(paramBoolean ? 1 : 2, paramString, localUnmarshallingContext.getLocator().getLocation(), paramException), paramBoolean);
  }
  
  protected static void handleParseConversionException(UnmarshallingContext.State paramState, Exception paramException)
    throws SAXException
  {
    paramState.getContext().handleError(paramException);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\unmarshaller\Loader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
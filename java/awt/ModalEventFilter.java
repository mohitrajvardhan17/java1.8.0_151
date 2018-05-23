package java.awt;

import sun.awt.AppContext;
import sun.awt.ModalExclude;

abstract class ModalEventFilter
  implements EventFilter
{
  protected Dialog modalDialog;
  protected boolean disabled;
  
  protected ModalEventFilter(Dialog paramDialog)
  {
    modalDialog = paramDialog;
    disabled = false;
  }
  
  Dialog getModalDialog()
  {
    return modalDialog;
  }
  
  public EventFilter.FilterAction acceptEvent(AWTEvent paramAWTEvent)
  {
    if ((disabled) || (!modalDialog.isVisible())) {
      return EventFilter.FilterAction.ACCEPT;
    }
    int i = paramAWTEvent.getID();
    if (((i >= 500) && (i <= 507)) || ((i >= 1001) && (i <= 1001)) || (i == 201))
    {
      Object localObject1 = paramAWTEvent.getSource();
      if ((!(localObject1 instanceof ModalExclude)) && ((localObject1 instanceof Component)))
      {
        for (Object localObject2 = (Component)localObject1; (localObject2 != null) && (!(localObject2 instanceof Window)); localObject2 = ((Component)localObject2).getParent_NoClientCode()) {}
        if (localObject2 != null) {
          return acceptWindow((Window)localObject2);
        }
      }
    }
    return EventFilter.FilterAction.ACCEPT;
  }
  
  protected abstract EventFilter.FilterAction acceptWindow(Window paramWindow);
  
  void disable()
  {
    disabled = true;
  }
  
  int compareTo(ModalEventFilter paramModalEventFilter)
  {
    Dialog localDialog1 = paramModalEventFilter.getModalDialog();
    for (Object localObject = modalDialog; localObject != null; localObject = ((Component)localObject).getParent_NoClientCode()) {
      if (localObject == localDialog1) {
        return 1;
      }
    }
    for (localObject = localDialog1; localObject != null; localObject = ((Component)localObject).getParent_NoClientCode()) {
      if (localObject == modalDialog) {
        return -1;
      }
    }
    for (Dialog localDialog2 = modalDialog.getModalBlocker(); localDialog2 != null; localDialog2 = localDialog2.getModalBlocker()) {
      if (localDialog2 == localDialog1) {
        return -1;
      }
    }
    for (localDialog2 = localDialog1.getModalBlocker(); localDialog2 != null; localDialog2 = localDialog2.getModalBlocker()) {
      if (localDialog2 == modalDialog) {
        return 1;
      }
    }
    return modalDialog.getModalityType().compareTo(localDialog1.getModalityType());
  }
  
  static ModalEventFilter createFilterForDialog(Dialog paramDialog)
  {
    switch (paramDialog.getModalityType())
    {
    case DOCUMENT_MODAL: 
      return new DocumentModalEventFilter(paramDialog);
    case APPLICATION_MODAL: 
      return new ApplicationModalEventFilter(paramDialog);
    case TOOLKIT_MODAL: 
      return new ToolkitModalEventFilter(paramDialog);
    }
    return null;
  }
  
  private static class ApplicationModalEventFilter
    extends ModalEventFilter
  {
    private AppContext appContext;
    
    ApplicationModalEventFilter(Dialog paramDialog)
    {
      super();
      appContext = appContext;
    }
    
    protected EventFilter.FilterAction acceptWindow(Window paramWindow)
    {
      if (paramWindow.isModalExcluded(Dialog.ModalExclusionType.APPLICATION_EXCLUDE)) {
        return EventFilter.FilterAction.ACCEPT;
      }
      if (appContext == appContext)
      {
        while (paramWindow != null)
        {
          if (paramWindow == modalDialog) {
            return EventFilter.FilterAction.ACCEPT_IMMEDIATELY;
          }
          paramWindow = paramWindow.getOwner();
        }
        return EventFilter.FilterAction.REJECT;
      }
      return EventFilter.FilterAction.ACCEPT;
    }
  }
  
  private static class DocumentModalEventFilter
    extends ModalEventFilter
  {
    private Window documentRoot;
    
    DocumentModalEventFilter(Dialog paramDialog)
    {
      super();
      documentRoot = paramDialog.getDocumentRoot();
    }
    
    protected EventFilter.FilterAction acceptWindow(Window paramWindow)
    {
      if (paramWindow.isModalExcluded(Dialog.ModalExclusionType.APPLICATION_EXCLUDE))
      {
        for (Window localWindow = modalDialog.getOwner(); localWindow != null; localWindow = localWindow.getOwner()) {
          if (localWindow == paramWindow) {
            return EventFilter.FilterAction.REJECT;
          }
        }
        return EventFilter.FilterAction.ACCEPT;
      }
      while (paramWindow != null)
      {
        if (paramWindow == modalDialog) {
          return EventFilter.FilterAction.ACCEPT_IMMEDIATELY;
        }
        if (paramWindow == documentRoot) {
          return EventFilter.FilterAction.REJECT;
        }
        paramWindow = paramWindow.getOwner();
      }
      return EventFilter.FilterAction.ACCEPT;
    }
  }
  
  private static class ToolkitModalEventFilter
    extends ModalEventFilter
  {
    private AppContext appContext;
    
    ToolkitModalEventFilter(Dialog paramDialog)
    {
      super();
      appContext = appContext;
    }
    
    protected EventFilter.FilterAction acceptWindow(Window paramWindow)
    {
      if (paramWindow.isModalExcluded(Dialog.ModalExclusionType.TOOLKIT_EXCLUDE)) {
        return EventFilter.FilterAction.ACCEPT;
      }
      if (appContext != appContext) {
        return EventFilter.FilterAction.REJECT;
      }
      while (paramWindow != null)
      {
        if (paramWindow == modalDialog) {
          return EventFilter.FilterAction.ACCEPT_IMMEDIATELY;
        }
        paramWindow = paramWindow.getOwner();
      }
      return EventFilter.FilterAction.REJECT;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\ModalEventFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package javax.imageio.spi;

public abstract interface RegisterableService
{
  public abstract void onRegistration(ServiceRegistry paramServiceRegistry, Class<?> paramClass);
  
  public abstract void onDeregistration(ServiceRegistry paramServiceRegistry, Class<?> paramClass);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\imageio\spi\RegisterableService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
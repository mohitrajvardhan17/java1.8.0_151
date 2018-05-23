package com.sun.jndi.cosnaming;

import com.sun.jndi.toolkit.corba.CorbaUtils;
import javax.naming.CannotProceedException;
import javax.naming.CompositeName;
import javax.naming.Context;
import javax.naming.ContextNotEmptyException;
import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.NamingManager;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContext;
import org.omg.CosNaming.NamingContextPackage.AlreadyBound;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.InvalidName;
import org.omg.CosNaming.NamingContextPackage.NotEmpty;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.omg.CosNaming.NamingContextPackage.NotFoundReason;

public final class ExceptionMapper
{
  private static final boolean debug = false;
  
  private ExceptionMapper() {}
  
  public static final NamingException mapException(Exception paramException, CNCtx paramCNCtx, NameComponent[] paramArrayOfNameComponent)
    throws NamingException
  {
    if ((paramException instanceof NamingException)) {
      return (NamingException)paramException;
    }
    if ((paramException instanceof RuntimeException)) {
      throw ((RuntimeException)paramException);
    }
    Object localObject;
    if ((paramException instanceof NotFound))
    {
      if (federation) {
        return tryFed((NotFound)paramException, paramCNCtx, paramArrayOfNameComponent);
      }
      localObject = new NameNotFoundException();
    }
    else if ((paramException instanceof CannotProceed))
    {
      localObject = new CannotProceedException();
      NamingContext localNamingContext = cxt;
      NameComponent[] arrayOfNameComponent1 = rest_of_name;
      if ((paramArrayOfNameComponent != null) && (paramArrayOfNameComponent.length > arrayOfNameComponent1.length))
      {
        NameComponent[] arrayOfNameComponent2 = new NameComponent[paramArrayOfNameComponent.length - arrayOfNameComponent1.length];
        System.arraycopy(paramArrayOfNameComponent, 0, arrayOfNameComponent2, 0, arrayOfNameComponent2.length);
        ((NamingException)localObject).setResolvedObj(new CNCtx(_orb, orbTracker, localNamingContext, _env, paramCNCtx.makeFullName(arrayOfNameComponent2)));
      }
      else
      {
        ((NamingException)localObject).setResolvedObj(paramCNCtx);
      }
      ((NamingException)localObject).setRemainingName(CNNameParser.cosNameToName(arrayOfNameComponent1));
    }
    else if ((paramException instanceof InvalidName))
    {
      localObject = new InvalidNameException();
    }
    else if ((paramException instanceof AlreadyBound))
    {
      localObject = new NameAlreadyBoundException();
    }
    else if ((paramException instanceof NotEmpty))
    {
      localObject = new ContextNotEmptyException();
    }
    else
    {
      localObject = new NamingException("Unknown reasons");
    }
    ((NamingException)localObject).setRootCause(paramException);
    return (NamingException)localObject;
  }
  
  private static final NamingException tryFed(NotFound paramNotFound, CNCtx paramCNCtx, NameComponent[] paramArrayOfNameComponent)
    throws NamingException
  {
    Object localObject1 = rest_of_name;
    if ((localObject1.length == 1) && (paramArrayOfNameComponent != null))
    {
      localObject2 = paramArrayOfNameComponent[(paramArrayOfNameComponent.length - 1)];
      if ((!0id.equals(id)) || (0kind == null) || (!0kind.equals(kind)))
      {
        NameNotFoundException localNameNotFoundException = new NameNotFoundException();
        localNameNotFoundException.setRemainingName(CNNameParser.cosNameToName((NameComponent[])localObject1));
        localNameNotFoundException.setRootCause(paramNotFound);
        throw localNameNotFoundException;
      }
    }
    Object localObject2 = null;
    int i = 0;
    if ((paramArrayOfNameComponent != null) && (paramArrayOfNameComponent.length >= localObject1.length))
    {
      if (why == NotFoundReason.not_context)
      {
        i = paramArrayOfNameComponent.length - (localObject1.length - 1);
        if (localObject1.length == 1)
        {
          localObject1 = null;
        }
        else
        {
          localObject3 = new NameComponent[localObject1.length - 1];
          System.arraycopy(localObject1, 1, localObject3, 0, localObject3.length);
          localObject1 = localObject3;
        }
      }
      else
      {
        i = paramArrayOfNameComponent.length - localObject1.length;
      }
      if (i > 0)
      {
        localObject2 = new NameComponent[i];
        System.arraycopy(paramArrayOfNameComponent, 0, localObject2, 0, i);
      }
    }
    Object localObject3 = new CannotProceedException();
    ((CannotProceedException)localObject3).setRootCause(paramNotFound);
    if ((localObject1 != null) && (localObject1.length > 0)) {
      ((CannotProceedException)localObject3).setRemainingName(CNNameParser.cosNameToName((NameComponent[])localObject1));
    }
    ((CannotProceedException)localObject3).setEnvironment(_env);
    final CNCtx localCNCtx = localObject2 != null ? paramCNCtx.callResolve((NameComponent[])localObject2) : paramCNCtx;
    if ((localCNCtx instanceof Context))
    {
      localObject4 = new RefAddr("nns")
      {
        private static final long serialVersionUID = 669984699392133792L;
        
        public Object getContent()
        {
          return localCNCtx;
        }
      };
      localObject5 = new Reference("java.lang.Object", (RefAddr)localObject4);
      CompositeName localCompositeName = new CompositeName();
      localCompositeName.add("");
      ((CannotProceedException)localObject3).setResolvedObj(localObject5);
      ((CannotProceedException)localObject3).setAltName(localCompositeName);
      ((CannotProceedException)localObject3).setAltNameCtx((Context)localCNCtx);
      return (NamingException)localObject3;
    }
    Object localObject4 = CNNameParser.cosNameToName((NameComponent[])localObject2);
    Object localObject5 = null;
    Object localObject7;
    try
    {
      if (CorbaUtils.isObjectFactoryTrusted(localCNCtx)) {
        localObject5 = NamingManager.getObjectInstance(localCNCtx, (Name)localObject4, paramCNCtx, _env);
      }
    }
    catch (NamingException localNamingException)
    {
      throw localNamingException;
    }
    catch (Exception localException)
    {
      localObject7 = new NamingException("problem generating object using object factory");
      ((NamingException)localObject7).setRootCause(localException);
      throw ((Throwable)localObject7);
    }
    if ((localObject5 instanceof Context))
    {
      ((CannotProceedException)localObject3).setResolvedObj(localObject5);
    }
    else
    {
      ((Name)localObject4).add("");
      ((CannotProceedException)localObject3).setAltName((Name)localObject4);
      final Object localObject6 = localObject5;
      localObject7 = new RefAddr("nns")
      {
        private static final long serialVersionUID = -785132553978269772L;
        
        public Object getContent()
        {
          return localObject6;
        }
      };
      Reference localReference = new Reference("java.lang.Object", (RefAddr)localObject7);
      ((CannotProceedException)localObject3).setResolvedObj(localReference);
      ((CannotProceedException)localObject3).setAltNameCtx(paramCNCtx);
    }
    return (NamingException)localObject3;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\cosnaming\ExceptionMapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
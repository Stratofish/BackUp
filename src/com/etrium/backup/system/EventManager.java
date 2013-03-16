package com.etrium.backup.system;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

public class EventManager
{
  private static List<EtriumEvent> events = null;
  private static List<EtriumEvent> eventBuffer = null;
  private static List<RegisteredEventListener> listeners = null;
  private static boolean eventsRunning = false;
  
  public EventManager()
  {
    if (null == events)
    {
      events = new ArrayList<EtriumEvent>();
      eventBuffer = new ArrayList<EtriumEvent>();
      listeners = new ArrayList<RegisteredEventListener>();
    }
  }
  
  public void RegisterListener(EventListener p_listener, EventType p_type)
  {
    RegisteredEventListener el = new RegisteredEventListener();
    el.listener = p_listener;
    el.type = p_type;
    listeners.add(0, el);
  }
  
  public void UnregisterListener(EventListener p_listener, EventType p_type)
  {
      RegisteredEventListener el = null;
      
      for (int i = 0; i < listeners.size(); i++)
      {
          RegisteredEventListener item = listeners.get(i);
          if ((item.listener == p_listener) &&
              (item.type == p_type))
          {
              el = item;
          }
      }
      
      if (el != null)
      {
          listeners.remove(el);
      }
  }
  
  public void DispatchEvents()
  {
    eventsRunning = true;
    EtriumEvent evt;
    Iterator<EtriumEvent> eItr;
    
    while(!events.isEmpty())
    {
      eItr = events.iterator();
      
      while (eItr.hasNext())
      {
        evt = (EtriumEvent)eItr.next();
        
        Iterator<RegisteredEventListener> lItr=listeners.iterator();
        boolean handled = false;
    
        while ((lItr.hasNext()) &&
               (!handled))
        {
          RegisteredEventListener listener = (RegisteredEventListener)lItr.next();
    
          if (evt.type == listener.type)
          {
            handled = listener.listener.ReceiveEvent(evt);
          }
        }
        
        eItr.remove();
      }
      eventsRunning = false;
      if (!eventBuffer.isEmpty())
      {
        eItr = eventBuffer.iterator();
        while (eItr.hasNext())
        {
          evt = (EtriumEvent)eItr.next();
          events.add(evt);
          eItr.remove();
        }
      }
      eventsRunning = true;
    }
    eventsRunning = false;
  }
  
  public void SendEvent(EtriumEvent p_event, boolean p_instant)
  {
    // If we want to trigger the event right now rather than on the main per-frame update
    if (p_instant)
    {
      Iterator<RegisteredEventListener> itr=listeners.iterator();
      boolean handled = false;

      while ((itr.hasNext()) &&
             (!handled))
      {
        RegisteredEventListener listener = (RegisteredEventListener)itr.next();

        if (p_event.type == listener.type)
        {
          handled = listener.listener.ReceiveEvent(p_event);
        }
      }
    } else
    {
      if (!eventsRunning)
      {
        events.add(p_event);
      } else
      {
        eventBuffer.add(p_event);
      }
    }
  }
}

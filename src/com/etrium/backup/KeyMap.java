package com.etrium.backup;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.etrium.backup.system.ControlType;
import com.etrium.backup.system.EtriumEvent;
import com.etrium.backup.system.EventManager;
import com.etrium.backup.system.EventType;

public class KeyMap
{
    private EventManager evtMgr = null;
    private boolean upHeld = false;
    private boolean downHeld = false;
    private boolean leftHeld = false;
    private boolean rightHeld = false;
    private boolean spaceHeld = false;
    private boolean quitHeld = false;
    
    private boolean godModeHeld = false; 
    private boolean noClipHeld = false; 
    private boolean revealMapHeld = false; 
    
    public KeyMap()
    {
        evtMgr = new EventManager();
    }
    
    public void CheckKeys()
    {
        // Up
        if (Gdx.input.isKeyPressed(Keys.UP) ||
            Gdx.input.isKeyPressed(Keys.W))
        {
            if (!upHeld)
            {
                EtriumEvent evt = new EtriumEvent();
                evt.type = EventType.evtControlDown;
                evt.data = (Object)ControlType.UP;
                evtMgr.SendEvent(evt,  true);
                
                upHeld = true;
            }
        } else
        {
            if (upHeld)
            {
                EtriumEvent evt = new EtriumEvent();
                evt.type = EventType.evtControlUp;
                evt.data = (Object)ControlType.UP;
                evtMgr.SendEvent(evt,  true);
                
                upHeld = false;
            }
        }
        
        // Down
        if (Gdx.input.isKeyPressed(Keys.DOWN) ||
            Gdx.input.isKeyPressed(Keys.S))
        {
            if (!downHeld)
            {
                EtriumEvent evt = new EtriumEvent();
                evt.type = EventType.evtControlDown;
                evt.data = (Object)ControlType.DOWN;
                evtMgr.SendEvent(evt,  true);
                
                downHeld = true;
            }
        } else
        {
            if (downHeld)
            {
                EtriumEvent evt = new EtriumEvent();
                evt.type = EventType.evtControlUp;
                evt.data = (Object)ControlType.DOWN;
                evtMgr.SendEvent(evt,  true);
                
                downHeld = false;
            }
        }
        
        // Left
        if (Gdx.input.isKeyPressed(Keys.LEFT) ||
            Gdx.input.isKeyPressed(Keys.A))
        {
            if (!leftHeld)
            {
                EtriumEvent evt = new EtriumEvent();
                evt.type = EventType.evtControlDown;
                evt.data = (Object)ControlType.LEFT;
                evtMgr.SendEvent(evt,  true);
                
                leftHeld = true;
            }
        } else
        {
            if (leftHeld)
            {
                EtriumEvent evt = new EtriumEvent();
                evt.type = EventType.evtControlUp;
                evt.data = (Object)ControlType.LEFT;
                evtMgr.SendEvent(evt,  true);
                
                leftHeld = false;
            }
        }
        
        // Right
        if (Gdx.input.isKeyPressed(Keys.RIGHT) ||
            Gdx.input.isKeyPressed(Keys.D))
        {
            if (!rightHeld)
            {
                EtriumEvent evt = new EtriumEvent();
                evt.type = EventType.evtControlDown;
                evt.data = (Object)ControlType.RIGHT;
                evtMgr.SendEvent(evt,  true);
                
                rightHeld = true;
            }
        } else
        {
            if (rightHeld)
            {
                EtriumEvent evt = new EtriumEvent();
                evt.type = EventType.evtControlUp;
                evt.data = (Object)ControlType.RIGHT;
                evtMgr.SendEvent(evt,  true);
                
                rightHeld = false;
            }
        }
        
        // Space
        if (Gdx.input.isKeyPressed(Keys.SPACE))
        {
            if (!spaceHeld)
            {
                EtriumEvent evt = new EtriumEvent();
                evt.type = EventType.evtControlDown;
                evt.data = (Object)ControlType.SPACE;
                evtMgr.SendEvent(evt,  true);
                
                spaceHeld = true;
            }
        } else
        {
            if (spaceHeld)
            {
                EtriumEvent evt = new EtriumEvent();
                evt.type = EventType.evtControlUp;
                evt.data = (Object)ControlType.SPACE;
                evtMgr.SendEvent(evt,  true);
                
                spaceHeld = false;
            }
        }
      
        // God mode
        if (Gdx.input.isKeyPressed(Keys.F1))
        {
            if (!godModeHeld)
            {
                EtriumEvent evt = new EtriumEvent();
                evt.type = EventType.evtControlDown;
                evt.data = (Object)ControlType.GODMODE;
                //evtMgr.SendEvent(evt,  true);
                
                godModeHeld = true;
            }
        } else
        {
            if (godModeHeld)
            {
                EtriumEvent evt = new EtriumEvent();
                evt.type = EventType.evtControlUp;
                evt.data = (Object)ControlType.GODMODE;
                //evtMgr.SendEvent(evt,  true);
                
                godModeHeld = false;
            }
        }
        
        // Reveal map
        if (Gdx.input.isKeyPressed(Keys.F2))
        {
            if (!revealMapHeld)
            {
                EtriumEvent evt = new EtriumEvent();
                evt.type = EventType.evtControlDown;
                evt.data = (Object)ControlType.REVEALMAP;
                //evtMgr.SendEvent(evt,  true);
                
                revealMapHeld = true;
            }
        } else
        {
            if (revealMapHeld)
            {
                EtriumEvent evt = new EtriumEvent();
                evt.type = EventType.evtControlUp;
                evt.data = (Object)ControlType.REVEALMAP;
                //evtMgr.SendEvent(evt,  true);
                
                revealMapHeld = false;
            }
        }
        
        // Noclip mode
        if (Gdx.input.isKeyPressed(Keys.F3))
        {
            if (!noClipHeld)
            {
                EtriumEvent evt = new EtriumEvent();
                evt.type = EventType.evtControlDown;
                evt.data = (Object)ControlType.NOCLIP;
                //evtMgr.SendEvent(evt,  true);
                
                noClipHeld = true;
            }
        } else
        {
            if (noClipHeld)
            {
                EtriumEvent evt = new EtriumEvent();
                evt.type = EventType.evtControlUp;
                evt.data = (Object)ControlType.NOCLIP;
                //evtMgr.SendEvent(evt,  true);
                
                noClipHeld = false;
            }
        }
        
        // Quit
        if (Gdx.input.isKeyPressed(Keys.ESCAPE))
        {
            if (!quitHeld)
            {
                EtriumEvent evt = new EtriumEvent();
                evt.type = EventType.evtControlDown;
                evt.data = (Object)ControlType.QUIT;
                evtMgr.SendEvent(evt,  true);
                
                quitHeld = true;
            }
        } else
        {
            if (quitHeld)
            {
                quitHeld = false;
            }
        }
    }
}

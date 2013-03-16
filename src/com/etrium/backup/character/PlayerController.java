package com.etrium.backup.character;

import com.etrium.backup.Dungeon;
import com.etrium.backup.Map;
import com.etrium.backup.MapLocation;
import com.etrium.backup.item.Item;
import com.etrium.backup.system.ControlType;
import com.etrium.backup.system.EtriumEvent;
import com.etrium.backup.system.EventListener;
import com.etrium.backup.system.EventManager;
import com.etrium.backup.system.EventType;

public class PlayerController implements CharController, EventListener
{
boolean listening = false;
    
    private boolean upHeld = false;
    private boolean downHeld = false;
    private boolean leftHeld = false;
    private boolean rightHeld = false;
    private boolean spaceHeld = false;
    
    private EventManager evtMgr = null;
    private Map level;
    private Character character = null;
    
    public PlayerController()
    {
        evtMgr = new EventManager();
        evtMgr.RegisterListener(this, EventType.evtControlDown);
        evtMgr.RegisterListener(this, EventType.evtControlUp);
    }
    
    public boolean DoControl()
    {
        boolean action = false;
        Character target = null;
        
        MapLocation loc = new MapLocation(character.location);
        
        if (upHeld)
        {
            if (!IsOccupied(loc.x, loc.y+1))
            {
                loc.y++;
            } else
            {
                target = IsEnemy(loc.x, loc.y+1);
            }
            action = true;
            upHeld = false;
        }
        if (downHeld)
        {
            if (!IsOccupied(loc.x, loc.y-1))
            {
                loc.y--;
            } else
            {
                target = IsEnemy(loc.x, loc.y-1);
            }
            action = true;
            downHeld = false;
        }
        if (leftHeld)
        {
            if (!IsOccupied(loc.x-1, loc.y))
            {
                loc.x--;
            } else
            {
                target = IsEnemy(loc.x-1, loc.y);
            }
            action = true;
            leftHeld = false;
        }
        if (rightHeld)
        {
            if (!IsOccupied(loc.x+1, loc.y))
            {
                loc.x++;
            } else
            {
                target = IsEnemy(loc.x+1, loc.y);
            }
            action = true;
            rightHeld = false;
        }
        if (spaceHeld)
        {
            DoAction();
            
            spaceHeld = false;
        }
        
        if (target != null)
        {
            character.AttackChar(target);
        }
        
        if (action)
        {
            character.location.x = loc.x;
            character.location.y = loc.y;
        }
        
        return action;
    }
    
    private boolean DoAction()
    {
        if (level.GetState().items[(int)character.location.y][(int)character.location.x] != null)
        {
            Item item = level.GetState().items[(int)character.location.y][(int)character.location.x];
            level.GetState().items[(int)character.location.y][(int)character.location.x] = null;
            character.inventory.AddItem(item);
            
            EtriumEvent event = new EtriumEvent();
            event.type = EventType.evtLogActivity;
            String message = item.GetName()+" picked up";
            event.data = message;
            evtMgr.SendEvent(event,  true);
            
            return true;
        }
        if (level.map[(int)character.location.y][(int)character.location.x] == level.STAIRSUP)
        {
            EtriumEvent event = new EtriumEvent();
            event.type = EventType.evtUpLevel;
            event.data = null;
            evtMgr.SendEvent(event,  true);
            
            return true;
        }
        
        if (level.map[(int)character.location.y][(int)character.location.x] == level.STAIRSDOWN)
        {
            EtriumEvent event = new EtriumEvent();
            event.type = EventType.evtDownLevel;
            event.data = null;
            evtMgr.SendEvent(event,  true);
            
            event = new EtriumEvent();
            event.type = EventType.evtLogActivity;
            String message = "Level down";
            event.data = message;
            evtMgr.SendEvent(event,  true);
            
            return true;
        }
        
        return false;
    }

    private boolean IsOccupied(int x, int y)
    {
        if (Dungeon.noClip)
        {
            return false;
        }
        
        if ((level.map[y][x] != level.EMPTY) &&
            (level.map[y][x] != level.STAIRSUP) &&
            (level.map[y][x] != level.STAIRSDOWN))
        {
            return true;
        }
        
        for (int i = 0; i < level.GetState().enemies.size(); i++)
        {
            Character enemy = level.GetState().enemies.get(i);
            if ((enemy.location.x == x) &&
                (enemy.location.y == y))
            {
                return true;
            }
        }
        
        return false;
    }
    
    private Character IsEnemy(int x, int y)
    {
        for (int i = 0; i < level.GetState().enemies.size(); i++)
        {
            Character enemy = level.GetState().enemies.get(i);
            if ((enemy.location.x == x) &&
                (enemy.location.y == y))
            {
                return enemy;
            }
        }
        
        return null;
    }

    @Override
    public void SetLevel(Map p_level)
    {
        level = p_level;
    }

    @Override
    public boolean ReceiveEvent(EtriumEvent p_event)
    {
        switch (p_event.type)
        {
            case evtControlDown:
            {
                ControlType ct = (ControlType)p_event.data;
                boolean handled = false;
                switch(ct)
                {
                    case UP:
                    {
                        upHeld = true;
                        handled = true;
                        break;
                    }
                    case DOWN:
                    {
                        downHeld = true;
                        handled = true;
                        break;
                    }
                    case LEFT:
                    {
                        leftHeld = true;
                        handled = true;
                        break;
                    }
                    case RIGHT:
                    {
                        rightHeld = true;
                        handled = true;
                        break;
                    }
                    case SPACE:
                    {
                        spaceHeld = true;
                        handled = true;
                        break;
                    }
                }
                
                if (handled)
                {
                    return true;
                }
            }
            case evtControlUp:
            {
                ControlType ct = (ControlType)p_event.data;
                boolean handled = false;
                switch(ct)
                {
                    case UP:
                    {
                        upHeld = false;
                        handled = true;
                        break;
                    }
                    case DOWN:
                    {
                        downHeld = false;
                        handled = true;
                        break;
                    }
                    case LEFT:
                    {
                        leftHeld = false;
                        handled = true;
                        break;
                    }
                    case RIGHT:
                    {
                        rightHeld = false;
                        handled = true;
                        break;
                    }
                    case SPACE:
                    {
                        spaceHeld = false;
                        handled = true;
                        break;
                    }
                }
                
                if (handled)
                {
                    return true;
                }
            }
        }
        
        return false;
    }

    @Override
    public void StartListening()
    {
        listening = true;
    }

    @Override
    public void StopListening()
    {
        listening = false;
    }

    @Override
    public void SetCharacter(Character p_char)
    {
        character = p_char;
    }

    @Override
    public boolean isPlayer()
    {
        return true;
    }
}

package com.etrium.backup.character;

import java.util.Random;

import com.badlogic.gdx.math.Vector2;
import com.etrium.backup.Dungeon;
import com.etrium.backup.Map;
import com.etrium.backup.MapLocation;
import com.etrium.backup.system.EtriumEvent;
import com.etrium.backup.system.EventManager;
import com.etrium.backup.system.EventType;

public class EnemyController implements CharController
{
    private final int ANCHORDIST = 5;
    private Map level;
    private Random gen;
    private boolean needAnchor = true;
    private MapLocation anchor = new MapLocation();
    private MapLocation target = new MapLocation();
    private EventManager evtMgr = new EventManager();
    private boolean chasingPlayer = false;
    private boolean approachingAnchor = false;
    private Character character = null;
    
    public EnemyController()
    {
        gen = new Random();
        target.x = 0;
        target.y = 0;
    }
    
    private boolean MoveRandomDirection(MapLocation p_loc)
    {
        int dir = gen.nextInt(5); 
        boolean moved = false;
        
        MapLocation newRandomTarget = new MapLocation();
        newRandomTarget.x = p_loc.x;
        newRandomTarget.y = p_loc.y;
        
        switch (dir)
        {
            case 0:
            {
                if (!IsOccupied(p_loc.x, p_loc.y+1))
                {
                    newRandomTarget.y++;
                    moved = true;
                }
                break;
            }
            case 1:
            {
                if (!IsOccupied(p_loc.x, p_loc.y-1))
                {
                    newRandomTarget.y--;
                    moved = true;
                }
                break;
            }
            case 2:
            {
                if (!IsOccupied(p_loc.x-1, p_loc.y))
                {
                    newRandomTarget.x--;
                    moved = true;
                }
                break;
            }
            case 3:
            {
                if (!IsOccupied(p_loc.x+1, p_loc.y))
                {
                    newRandomTarget.x++;
                    moved = true;
                }
                break;
            }
            default:
            {
                moved = true;
                break;
            }
        }
        
        if (moved)
        {
            float dist = Dungeon.DistBetween(anchor, newRandomTarget);
            
            if (dist < ANCHORDIST)
            {
                p_loc.x = newRandomTarget.x;
                p_loc.y = newRandomTarget.y;
            } else
            {
                moved = false;
            }
        }
        
        return moved;
    }
    
    private void MoveTowardTarget(MapLocation p_target)
    {
        float dX = target.x - p_target.x;
        float dY = target.y - p_target.y;
        
        Vector2 newLoc = new Vector2(p_target.x, p_target.y);
        
        int x = 1;
        if (dX < 0)
        {
            x = -1;
        }
        
        int y = 1;
        if (dY < 0)
        {
            y = -1;
        }
        
        if (Math.abs(dX) > Math.abs(dY))
        {
            if (!IsOccupied(newLoc.x + x, newLoc.y))
            {
              newLoc.x += x;
            } else
            {
                if (!IsOccupied(newLoc.x, newLoc.y + y))
                {
                    newLoc.y += y;
                }
            }
        } else
        {
            if (!IsOccupied(newLoc.x, newLoc.y + y))
            {
              newLoc.y += y;
            } else
            {
                if (!IsOccupied(newLoc.x + x, newLoc.y))
                {
                    newLoc.x += x;
                }
            }
        }
        
        p_target.x = (int) newLoc.x;
        p_target.y = (int) newLoc.y;
    }
    
    private void MoveTowardTarget(Character p_char)
    {
        MapLocation loc = new MapLocation(p_char.location);
        
        MoveTowardTarget(loc);
        
        p_char.location.x = loc.x;
        p_char.location.y = loc.y;
    }
    
    private boolean IsOccupied(float x, float y)
    {
        return IsOccupied((int)x, (int)y);
    }

    private boolean IsOccupied(int x, int y)
    {
        if (level.map[y][x] != level.EMPTY)
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
    
    @Override
    public boolean DoControl()
    {
        if (needAnchor)
        {
            anchor.x = (int)character.location.x;
            anchor.y = (int)character.location.y;
            needAnchor = false;
        }
        
        if (level.player == null)
        {
            needAnchor = false;
        }
        
        float dist = Dungeon.DistBetween(character.location, level.player.location); 
        if ( dist < 10.0f )
        {
            if (Map.LineOfSight(level, new MapLocation(character.location), new MapLocation(level.player.location)))
            {
                target.x = (int) level.player.location.x;
                target.y = (int) level.player.location.y;
                anchor = target;
                
                if (!chasingPlayer)
                {
                    EtriumEvent evt = new EtriumEvent();
                    evt.type = EventType.evtPlayerSeen;
                    evt.data = character;
                    evtMgr.SendEvent(evt,  true);
                    
                    chasingPlayer = true;
                }
                
                if (dist > 1.0f)
                {
                  MoveTowardTarget(character);
                } else
                {
                    character.AttackChar(level.player);
                 
                    //needAnchor = true;
                }
                
                return true;
            }
        }
        
        if (chasingPlayer)
        {
            // We lost them :(
            
            EtriumEvent evt = new EtriumEvent();
            evt.type = EventType.evtPlayerUnseen;
            evt.data = character;
            evtMgr.SendEvent(evt,  true);
            
            chasingPlayer = false;
            approachingAnchor = true;
            
            //anchor.x = (int) character.location.x;
            //anchor.y = (int) character.location.y;
        }
        
        if (approachingAnchor)
        {
            dist = Dungeon.DistBetween(character.location, new Vector2(anchor.x, anchor.y)); 
            if (dist > 1.0f)
            {
              MoveTowardTarget(character);
              return true;
            } else
            {
                approachingAnchor = false;
            }
        }
        
        int attempt = 0;
        
        //while ((!MoveRandomDirection(new MapLocation(character.location))) && (attempt < 20))
        MapLocation loc = new MapLocation(character.location);
        while ((!MoveRandomDirection(loc)) && (attempt < 20))
        {
            attempt++;
        }
        character.location.x = loc.x;
        character.location.y = loc.y;
        
        return true;
    }

    @Override
    public void SetLevel(Map p_level)
    {
        level = p_level;
    }

    @Override
    public void SetCharacter(Character p_char)
    {
        character = p_char;
    }

    @Override
    public boolean isPlayer()
    {
        return false;
    }
}
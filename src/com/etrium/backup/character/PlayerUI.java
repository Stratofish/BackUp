package com.etrium.backup.character;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.etrium.backup.Map;
import com.etrium.backup.system.EtriumEvent;
import com.etrium.backup.system.EventListener;
import com.etrium.backup.system.EventManager;
import com.etrium.backup.system.EventType;

public class PlayerUI implements CharUI, EventListener
{
    private Table charsListTable = null; 
    private Skin skin;
    private Character character;
    private boolean listening = true;
    private EventManager evtMgr = new EventManager();
    private List<Character> enemies = new ArrayList<Character>();
    
    public PlayerUI(Skin p_skin)
    {
        skin = p_skin;
        charsListTable = new Table(skin);
        
        evtMgr.RegisterListener(this, EventType.evtPlayerSeen);
        evtMgr.RegisterListener(this, EventType.evtPlayerUnseen);
    }
    
    @Override
    public void SetMap(Map p_map)
    {
    }
    
    @Override
    public Table GetStatsUI()
    {
        charsListTable.clear();
        charsListTable.align(Align.top);
        charsListTable.row().fill().expandX().height(100.0f);
        charsListTable.add(character.GetUIVersion(skin));
        
        for (int i = 0; i < enemies.size(); i++)
        {
            charsListTable.row().fill().expandX().height(50.0f);
            charsListTable.add(enemies.get(i).GetUIVersion(skin));
        }

        return charsListTable;
    }

    @Override
    public void SetChar(Character p_char)
    {
        character = p_char;
    }

    @Override
    public boolean ReceiveEvent(EtriumEvent p_event)
    {
        if (listening)
        {
            switch (p_event.type)
            {
                case evtPlayerSeen:
                {
                    Character enemy = (Character)p_event.data;
                    enemies.add(enemy);
                    
                    String message = enemy.GetName() + " saw you and begins to chase!";
                    EtriumEvent evt = new EtriumEvent();
                    evt.type = EventType.evtLogActivity;
                    evt.data = message;
                    evtMgr.SendEvent(evt,  true);
                    
                    evt = new EtriumEvent();
                    evt.type = EventType.evtPlayerUIChanged;
                    evt.data = null;
                    evtMgr.SendEvent(evt,  true);
                 
                    return true;
                }
                case evtPlayerUnseen:
                {
                    Character enemy = (Character)p_event.data;
                    enemies.remove(enemy);
                    
                    EtriumEvent evt = null;
                    
                    if (enemy.AreYouAlive())
                    {
                        String message = enemy.GetName() + " lost sight of you";
                        evt = new EtriumEvent();
                        evt.type = EventType.evtLogActivity;
                        evt.data = message;
                        evtMgr.SendEvent(evt,  true);
                    }
                    
                    evt = new EtriumEvent();
                    evt.type = EventType.evtPlayerUIChanged;
                    evt.data = null;
                    evtMgr.SendEvent(evt,  true);
                    
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

}

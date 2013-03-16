package com.etrium.backup.system;

import java.util.ArrayList;

import com.badlogic.gdx.scenes.scene2d.ui.List;

public class ActivityLog
{
    private java.util.List<String> lines = new ArrayList<String>();
    private List list;
    private static EventManager evtMgr = new EventManager();
    
    public ActivityLog(List p_list)
    {
        list = p_list;
    }
    
    public void AddMessage(String p_message)
    {
        lines.add(p_message);
        String[] newLines = new String[lines.size()];
        
        for (int i = 0; i < lines.size(); i++)
        {
            newLines[i] = lines.get(i);
        }
        
        list.setItems(newLines);
        
        // Move to the new line
        //scrollPane.setScrollY(scrollPane.getMaxY());
        //scrollPane.setScrollPercentY(100.0f);
    }
    
    public static void Log(String p_message)
    {
        EtriumEvent event = new EtriumEvent();
        event.type = EventType.evtLogActivity;
        event.data = p_message;
        evtMgr.SendEvent(event,  true);
    }
}

package com.etrium.backup.item;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.etrium.backup.Map;
import com.etrium.backup.MapLocation;
import com.etrium.backup.character.BaseStats;
import com.etrium.backup.item.Item.Subclass;

public interface ItemTypeInterface
{
    public void Create(int level);
    public Item.Class GetType();
    public String GetName();
    public String GetIconName();
    public BaseStats GetStats();
    public void render(SpriteBatch batch);
    public void SetLocation(MapLocation p_location);
    public void SetLevel(Map p_level);
    public void SetColor(Color p_colour);
    public Subclass GetSubClass();
}

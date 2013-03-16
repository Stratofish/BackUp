package com.etrium.backup.item;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.etrium.backup.Map;
import com.etrium.backup.MapLocation;
import com.etrium.backup.character.BaseStats;
import com.etrium.backup.item.WeaponType.WeaponTypeClass;

public interface WeaponTypeInterface
{
    public void Create();
    public WeaponTypeClass GetType();
    public String GetName();
    public String GetIconName();
    public BaseStats GetStats();
    public void render(SpriteBatch batch);
    public void SetLocation(MapLocation p_location);
    public void SetLevel(Map p_level);
    public void SetColour(Color p_colour);
}

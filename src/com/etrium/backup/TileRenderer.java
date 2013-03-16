package com.etrium.backup;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface TileRenderer
{
    void LoadResources();
    public void SetLevel(Map p_level); 
    void Render(SpriteBatch batch, MapLocation p_char);
    void SetColor(Color p_colour);
}

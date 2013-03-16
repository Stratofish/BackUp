package com.etrium.backup;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class SimpleSpriteRenderer implements TileRenderer
{
    public final int TILEWIDTH = 32;
    public final int TILEHEIGHT = 32;
    
    private Map level;

    private String spriteName;
    private Texture texture;
    private TextureRegion textureRegion;
    private Sprite token;

    public SimpleSpriteRenderer(String p_name)
    {
        spriteName = p_name;
    }
    
    @Override
    public void LoadResources()
    {
        texture = new Texture(Gdx.files.internal("data/"+spriteName+".png"));
        texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        textureRegion = new TextureRegion(texture, 0, 0, 32, 32);
        
        token = new Sprite(textureRegion);
        token.setSize(TILEWIDTH, TILEHEIGHT);
        token.setOrigin(0, 0);
    }

    @Override
    public void Render(SpriteBatch batch, MapLocation p_loc)
    {
        int xx = (1024-(level.width*TILEWIDTH) - 10) + (p_loc.x * TILEWIDTH);
        int yy = (768-(level.height*TILEHEIGHT) - 10) + (p_loc.y * TILEHEIGHT);
        xx -= 512;
        yy -= 384;
        token.setPosition(xx, yy);
        
        token.draw(batch);
    }

    @Override
    public void SetLevel(Map p_level)
    {
        level = p_level;
    }

    
    @Override
    public void SetColor(Color p_colour)
    {
        token.setColor(p_colour);
    }

}

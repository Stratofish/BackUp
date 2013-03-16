package com.etrium.backup;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.etrium.backup.system.EtriumEvent;
import com.etrium.backup.system.EventManager;
import com.etrium.backup.system.EventType;

public class LoseScreen
{
    private EventManager evtMgr = new EventManager(); 
    
    private Stage stage;
    private Skin skin;
    private Window window = null;
    private TextButton ok = null;
    private Label label = null;
    
    public LoseScreen()
    {
        stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("data/uiskin.json"));
        
        window = new Window("You died!", skin);
        window.setPosition(250, 400);
        window.setSize(500, 200f);
        window.row().fill().expandX().expandY();
        
        label = new Label("Oh dear!\n\nIt seems the adventurers in the dungeon didn't want you to escape and\nlive a normal life after all. Try again and prove how utterly average a\npeasant you can be on the surface.", skin);
        
        ok = new TextButton("OK", skin);
        
        ok.addListener(new ChangeListener()
        {
            public void changed (ChangeEvent event, Actor actor)
            {
                EtriumEvent evt = new EtriumEvent();
                evt.type = EventType.evtStartScreen;
                evt.data = null;
                evtMgr.SendEvent(evt,  true);
            }
        });
        
        window.add(label);
        window.row().fill().expandX().expandY();
        window.add(ok);
        
        stage.addActor(window);
    }

    public void render()
    {
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }
}

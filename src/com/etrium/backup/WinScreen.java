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

public class WinScreen
{
    private EventManager evtMgr = new EventManager(); 
    
    private Stage stage;
    private Skin skin;
    private Window window = null;
    private TextButton ok = null;
    private Label label = null;
    
    public WinScreen()
    {
        stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("data/uiskin.json"));
        
        window = new Window("You won!", skin);
        window.setPosition(250, 400);
        window.setSize(500, 200f);
        window.row().fill().expandX().expandY();
        
        label = new Label("Congratulations!\n\nThe villagers accept you as an equal and think you were just disfigured\nsomehow as a child. Enjoy your new relaxed lifestyle and\nremember not to eat babies or they will be after you with pitchforks...", skin);
        
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

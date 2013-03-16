package com.etrium.backup;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.etrium.backup.system.EtriumEvent;
import com.etrium.backup.system.EventManager;
import com.etrium.backup.system.EventType;

public class StartScreen
{
    private EventManager evtMgr = new EventManager(); 
    
    private Stage stage;
    private Skin skin;
    private Window window = null;
    private TextButton startGame = null;
    private TextButton quitGame = null;
    
    public StartScreen()
    {
        stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("data/uiskin.json"));
        
        window = new Window("7-day roguelike challenge - \"Back up\"", skin);
        window.setPosition(350, 500);
        window.setSize(300, 100f);
        window.row().fill().expandX().expandY();
        
        startGame = new TextButton("New game", skin);
        quitGame = new TextButton("Quit", skin);
        
        startGame.addListener(new ChangeListener()
        {
            public void changed (ChangeEvent event, Actor actor)
            {
                EtriumEvent evt = new EtriumEvent();
                evt.type = EventType.evtGameStart;
                evt.data = null;
                evtMgr.SendEvent(evt,  true);
            }
        });
        
        quitGame.addListener(new ChangeListener()
        {
            public void changed (ChangeEvent event, Actor actor)
            {
                EtriumEvent evt = new EtriumEvent();
                evt.type = EventType.evtGameQuit;
                evt.data = null;
                evtMgr.SendEvent(evt,  true);
            }
        });
        
        window.add(startGame);
        window.row().fill().expandX().expandY();
        window.add(quitGame);
        
        stage.addActor(window);
    }

    public void render()
    {
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }
}

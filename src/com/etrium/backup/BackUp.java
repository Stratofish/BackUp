package com.etrium.backup;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.etrium.backup.system.EtriumEvent;
import com.etrium.backup.system.EventListener;
import com.etrium.backup.system.EventManager;
import com.etrium.backup.system.EventType;

public class BackUp implements ApplicationListener, EventListener
{
	private OrthographicCamera camera;
	
	private GameState state = GameState.STARTSCREEN;
	//private GameState state = GameState.GAME;
	
	private Dungeon dungeon = null;
	private StartScreen startScreen = null;
	private WinScreen winScreen = null;
	private LoseScreen loseScreen = null;
	private EventManager evtMgr = new EventManager();
	
	
	@Override
	public void create() {		
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		
		camera = new OrthographicCamera(w, h);
		
		startScreen = new StartScreen();
		
		evtMgr.RegisterListener(this, EventType.evtGameStart);
		evtMgr.RegisterListener(this, EventType.evtGameQuit);
		evtMgr.RegisterListener(this, EventType.evtGameWon);
		evtMgr.RegisterListener(this, EventType.evtGameLost);
		evtMgr.RegisterListener(this, EventType.evtStartScreen);
	}

	@Override
	public void dispose()
	{
	    if (dungeon != null)
	    {
	        dungeon.dispose();
	    }
	}

	@Override
	public void render() {		
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		switch (state)
		{
		    case STARTSCREEN:
            {
                startScreen.render();
                break;
            }
		    case WIN:
            {
                winScreen.render();
                break;
            }
		    case LOSE:
            {
                loseScreen.render();
                break;
            }
		    case GAME:
		    {
		        dungeon.render();
		        break;
		    }
		}
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

    
	@Override
    public boolean ReceiveEvent(EtriumEvent p_event)
    {
        switch (p_event.type)
        {
            case evtStartScreen:
            {
                startScreen = new StartScreen();
                dungeon = null;
                state = GameState.STARTSCREEN;
                return true;
            }
            case evtGameStart:
            {
                startScreen = null;
                Map.mapStates = null;
                dungeon = new Dungeon(camera);
                state = GameState.GAME;
                return true;
            }
            case evtGameWon:
            {
                winScreen = new WinScreen();
                startScreen = null;
                dungeon = null;
                state = GameState.WIN;
                return true;
            }
            case evtGameLost:
            {
                loseScreen = new LoseScreen();
                startScreen = null;
                dungeon = null;
                state = GameState.LOSE;
                return true;
            }
            case evtGameQuit:
            {
                Gdx.app.exit();
                return true;
            }
        }
        
        return false;
    }
    

    @Override
    public void StartListening()
    {
    }
    

    @Override
    public void StopListening()
    {
    }
}

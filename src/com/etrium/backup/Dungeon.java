package com.etrium.backup;

import java.util.Random;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.etrium.backup.character.Character;
import com.etrium.backup.character.PlayerController;
import com.etrium.backup.character.PlayerSpecGenerator;
import com.etrium.backup.character.PlayerUI;
import com.etrium.backup.item.InventoryTooltip;
import com.etrium.backup.item.PlayerInventory;
import com.etrium.backup.system.ActivityLog;
import com.etrium.backup.system.ControlType;
import com.etrium.backup.system.EtriumEvent;
import com.etrium.backup.system.EventListener;
import com.etrium.backup.system.EventManager;
import com.etrium.backup.system.EventType;

public class Dungeon implements EventListener
{
    // Cheat modes (toggle with F1-F3 in this order)
    public static boolean godMode = false;
    public static boolean noClip = false;
    public static boolean revealMap  = false;
    
    private int LEVELSEED = 1;
    private int level = 35;
    
    private boolean listening = true;
    private EventManager evtMgr = new EventManager();
    private OrthographicCamera camera;
	private Map map;
	private SpriteBatch batch;
	private Texture textures[] = new Texture[5];
	private TextureRegion textureRegions[] = new TextureRegion[5];
	private Character player;
	
	private ActivityLog activityLog;
	private KeyMap keyMap = new KeyMap();
	
	private Stage logStage;
	private Skin logSkin;
	private ScrollPane logScrollPane = null;
	private Window logWindow = null;
	private Label levelLabel = null;
	
	private Window quitDialog = null;
	private Label quitLabel = null;
	private TextButton quitYesButton = null;
	private TextButton quitNoButton = null;
	private boolean quitDialogVisible = false;
	
	private ScrollPane charsScrollPane = null;
    private Window charsWindow = null;
    private Table charsListTable = null;
    private InventoryTooltip tooltip = null;

    public Dungeon(OrthographicCamera p_camera)
    {
        Random rnd = new Random();
        LEVELSEED = rnd.nextInt();
        camera = p_camera;

        textures[0] = new Texture(Gdx.files.internal("data/characters/player.png"));
        textures[0].setFilter(TextureFilter.Linear, TextureFilter.Linear);
        textureRegions[0] = new TextureRegion(textures[0], 0, 0, 32, 32);
        
        textures[1] = new Texture(Gdx.files.internal("data/map/F1x1Tiles.png"));
        textures[1].setFilter(TextureFilter.Linear, TextureFilter.Linear);
        textureRegions[1] = new TextureRegion(textures[1], 0, 0, 32, 32);
        textures[2] = new Texture(Gdx.files.internal("data/map/WRocks.png"));
        textures[2].setFilter(TextureFilter.Linear, TextureFilter.Linear);
        textureRegions[2] = new TextureRegion(textures[2], 0, 0, 32, 32);
        
        textures[3] = new Texture(Gdx.files.internal("data/map/stairsup.png"));
        textures[3].setFilter(TextureFilter.Linear, TextureFilter.Linear);
        textureRegions[3] = new TextureRegion(textures[3], 0, 0, 32, 32);
        
        textures[4] = new Texture(Gdx.files.internal("data/map/stairsdown.png"));
        textures[4].setFilter(TextureFilter.Linear, TextureFilter.Linear);
        textureRegions[4] = new TextureRegion(textures[4], 0, 0, 32, 32);

        map = new Map(LEVELSEED, level, textureRegions);
        
        batch = new SpriteBatch();
        
        logStage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
        Gdx.input.setInputProcessor(logStage);
        logSkin = new Skin(Gdx.files.internal("data/uiskin.json"));
        Label myLabel = new Label("This is some text.", logSkin);
        myLabel.setWrap(true);
        
        int playerX = (map.rooms.get(1).x + (map.rooms.get(1).width / 2));
        int playerY = (map.rooms.get(1).y + (map.rooms.get(1).height / 2));
        player = new Character(new PlayerController(),
                 new SimpleSpriteRenderer("characters/player"),
                 new PlayerInventory(logStage, logSkin),
                 new PlayerSpecGenerator(),
                 new PlayerUI(logSkin),
                 map,
                 level,
                 playerX,
                 playerY);
        player.YouAreTheMan();
        player.SetSkin(logSkin);
        player.UpdateGear();
        logStage.addActor(player.gearWindow);
        map.SetPlayer(player);
        
        tooltip = new InventoryTooltip(player);
        tooltip.SetStage(logStage);
        
        String[] s = {""};
        List list = new List(s, logSkin);
        list.setHeight(12.0f);
        logScrollPane = new ScrollPane(list, logSkin);
        logScrollPane.setOverscroll(false,  false);
        logScrollPane.setFadeScrollBars(false);
        logWindow = new Window("Activity", logSkin);
        logWindow.setPosition(1024-10-(map.MAPWIDTH*16), -300.0f);
        logWindow.setSize(map.MAPWIDTH*16.0f, 100.0f);
        logWindow.row().fill().expandX().expandY();
        logWindow.add(logScrollPane);
        logStage.addActor(logWindow);
        
        levelLabel = new Label("* * * Dungeon level 35 * * *", logSkin);
        levelLabel.setPosition(40.0f, 730.0f);
        logStage.addActor(levelLabel);

        activityLog = new ActivityLog(list);
        activityLog.AddMessage(" ");
        activityLog.AddMessage(" ");
        activityLog.AddMessage("Game begins");
        
        charsListTable = player.charUI.GetStatsUI();
        charsScrollPane = new ScrollPane(charsListTable, logSkin);
        charsScrollPane.setOverscroll(false,  false);
        charsScrollPane.setFadeScrollBars(false);
        charsScrollPane.setLayoutEnabled(true);
        charsWindow = new Window("Characters", logSkin);
        charsWindow.setPosition(10, 410.0f);
        charsWindow.setSize(250, 300.0f);
        charsWindow.row().fill().expandX().expandY();
        charsWindow.add(charsScrollPane);
        logStage.addActor(charsWindow);
        
        evtMgr.RegisterListener(this, EventType.evtLogActivity);
        evtMgr.RegisterListener(this, EventType.evtCharDead);
        evtMgr.RegisterListener(this, EventType.evtUpLevel);
        evtMgr.RegisterListener(this, EventType.evtDownLevel);
        evtMgr.RegisterListener(this, EventType.evtControlDown);
        evtMgr.RegisterListener(this, EventType.evtControlUp);
        evtMgr.RegisterListener(this, EventType.evtPlayerUIChanged);
        evtMgr.RegisterListener(this, EventType.evtQuitConfirm);
    }
    
    public void dispose()
    {
        textures[0].dispose();
    }
  
    public void ShowQuitDialog()
    {
        quitDialog = new Window("Really quit", logSkin);
        quitDialog.setPosition(350, 400);
        quitDialog.setSize(300, 200);
        quitLabel = new Label("You will lose all progress if you\nreturn to the start screen.\nAre you sure?", logSkin);
        quitYesButton = new TextButton("Yes, quit", logSkin);
        quitNoButton = new TextButton("No, carry on", logSkin);
        
        quitNoButton.addListener(new ChangeListener()
        {
            public void changed (ChangeEvent event, Actor actor)
            {
                quitDialog.remove();
                quitDialogVisible = false;
            }
        });
        
        quitYesButton.addListener(new ChangeListener()
        {
            public void changed (ChangeEvent event, Actor actor)
            {
                EtriumEvent evt = new EtriumEvent();
                evt.type = EventType.evtStartScreen;
                evtMgr.SendEvent(evt, false);
            }
        });
        
        quitDialog.row().expandX().expandY().fill();
        quitDialog.add(quitLabel);
        quitDialog.row().expandX().expandY().fill();
        quitDialog.add(quitYesButton);
        quitDialog.add(quitNoButton);
        
        logStage.addActor(quitDialog); 
        
        quitDialogVisible = true;
    }
    
    public void render()
    {
        evtMgr.DispatchEvents();
        
        if (!quitDialogVisible)
        {
            keyMap.CheckKeys();
        }
        
        boolean playerAction = player.DoControl();
        
        // Only update map stuff if player had a turn
        if (playerAction)
        {
          map.update();
          playerAction = false;
          //logScrollPane.setScrollPercentY(100.0f);
        }
        
        map.CenterMapWindowOnPlayer();
        camera.position.set(-660, -660, 0);
        camera.translate((map.mapWindow.x * map.TILEWIDTH), (map.mapWindow.y*map.TILEHEIGHT));
        camera.update();
        camera.apply(Gdx.gl10);

        Rectangle scissors = new Rectangle();
        Rectangle clipBounds = new Rectangle((map.mapWindow.x * map.TILEWIDTH)-(1024-640-85) - (1024/2),
                                             (map.mapWindow.y*map.TILEHEIGHT)-(768-640+420)-(768/2),640,640);
        ScissorStack.calculateScissors(camera, batch.getTransformMatrix(), clipBounds, scissors);
        ScissorStack.pushScissors(scissors);
        
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
            map.render(batch);
            player.render(batch);
        batch.end();
        
        ScissorStack.popScissors();

        levelLabel.setText("* * * Dungeon level "+map.curLevel+" * * *");
        
        logScrollPane.setScrollPercentY(100.0f);
        logStage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        logStage.draw();
        
        tooltip.Update();
        tooltip.UpdateGear();
    }

    @Override
    public boolean ReceiveEvent(EtriumEvent p_event)
    {
        if (listening)
        {
            switch (p_event.type)
            {
                case evtLogActivity:
                {
                    String message = (String)p_event.data;
                    activityLog.AddMessage(message);
                    return true;
                }
                case evtPlayerUIChanged:
                {
                    charsListTable = player.charUI.GetStatsUI();
                    
                    return true;
                }
                case evtCharDead:
                {
                    Character died = (Character)p_event.data;
                    if (died == player)
                    {
                        // Game over
                        
                        EtriumEvent evt = new EtriumEvent();
                        evt.type = EventType.evtGameLost;
                        evt.data = null;
                        evtMgr.SendEvent(evt, false);
                        
                        return true;
                    }
                    
                    return false;
                }
                case evtUpLevel:
                {
                    if (player.CalcEffectiveLevel()-4 > level)
                    {
                        ActivityLog.Log("Your bulky armour won't let you through the doorway to the staircase!");
                        ActivityLog.Log("Get your effective armour level down to "+(level+4)+" to go further upwards.");
                        return true;
                    }
                    
                    ActivityLog.Log("You go up the staircase");
                    
                    map.RIP();
                    level--;
                    
                    if (level <= 0)
                    {
                        EtriumEvent evt = new EtriumEvent();
                        evt.type = EventType.evtGameWon;
                        evt.data = null;
                        evtMgr.SendEvent(evt, false);
                    }
                    
                    map = new Map(LEVELSEED, level, textureRegions);
                    
                    int playerX = (map.rooms.get(1).x + (map.rooms.get(1).width / 2));
                    int playerY = (map.rooms.get(1).y + (map.rooms.get(1).height / 2));
                    map.SetPlayer(player);
                    player.SetMap(map, playerX, playerY);
                    
                    return true;
                }
                case evtDownLevel:
                {
                    ActivityLog.Log("You go down the staircase");
                    map.RIP();
                    level++;
                    map = new Map(LEVELSEED, level, textureRegions);
                    
                    int playerX = (map.rooms.get(0).x + (map.rooms.get(0).width / 2));
                    int playerY = (map.rooms.get(0).y + (map.rooms.get(0).height / 2));
                    map.SetPlayer(player);
                    player.SetMap(map, playerX, playerY);
                    
                    return true;
                }
                case evtQuitConfirm:
                {
                    ShowQuitDialog();
                    
                    return true;
                }
                case evtControlDown:
                {
                    ControlType ct = (ControlType)p_event.data;
                    boolean handled = false;
                    switch(ct)
                    {
                        case GODMODE:
                        {
                            godMode = !godMode;
                            handled = true;
                            break;
                        }
                        case REVEALMAP:
                        {
                            revealMap = !revealMap;
                            handled = true;
                            break;
                        }
                        case NOCLIP:
                        {
                            noClip = !noClip;
                            handled = true;
                            break;
                        }
                        case QUIT:
                        {
                            ShowQuitDialog();
                            break;
                        }
                    }
                    
                    if (handled)
                    {
                        return true;
                    }
                }
                case evtControlUp:
                {
                    ControlType ct = (ControlType)p_event.data;
                    boolean handled = false;
                    switch(ct)
                    {
                        case GODMODE:
                        {
                            handled = true;
                            break;
                        }
                        case REVEALMAP:
                        {
                            handled = true;
                            break;
                        }
                        case NOCLIP:
                        {
                            handled = true;
                            break;
                        }
                    }
                    
                    if (handled)
                    {
                        return true;
                    }
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

    public static float DistBetween(MapLocation first, MapLocation second)
    {
        int dx = first.x - second.x;
        int dy = first.y - second.y;
        float dist = (float)Math.sqrt((dx*dx)+(dy*dy));
        
        return dist;
    }
    

    public static float DistBetween(Vector2 first, Vector2 second)
    {
        float dx = first.x - second.x;
        float dy = first.y - second.y;
        float dist = (float)Math.sqrt((dx*dx)+(dy*dy));
        
        return dist;
    }
}

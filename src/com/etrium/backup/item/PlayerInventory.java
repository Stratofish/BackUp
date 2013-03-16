package com.etrium.backup.item;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.etrium.backup.character.Character;

public class PlayerInventory implements InventoryInterface
{
    public class ItemData
    {
        public Texture texture;
        public TextureRegion textureRegion;
        public Sprite token;
        public Image image;
        public Item item;
        public Window tooltip;
    }
    
    private Stage stage;
    private Skin skin;
    public Window window;
    private Table table;
    private ScrollPane scrollPane;
    private Table gearTable;
    public List<Item> items = new ArrayList<Item>();
    public List<ItemData> itemData = new ArrayList<ItemData>();
    public ItemData[] equippedItemData = new ItemData[6];
    private Texture tooltipTexture;
    private TextureRegion tooltipTextureRegion;
    private Sprite tooltipToken;
    private Drawable tooltipDrawable;
    private Character player = null;

    public PlayerInventory(Stage p_stage, Skin p_skin)
    {
        stage = p_stage;
        skin = p_skin;
        
        window = new Window("Inventory", skin);
        window.setPosition(10, 150);
        window.setSize(250, 250);
        window.row().fill().expandX();
        
        table = new Table(skin);
        table.setSize(250, 250);
        table.pad(10.0f);
        scrollPane = new ScrollPane(table, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setSize(250, 250);
        scrollPane.setOverscroll(false,  false);
        window.add(scrollPane);
        stage.addActor(window);
        
        tooltipTexture = new Texture(Gdx.files.internal("data/ui/tooltip.png"));
        tooltipTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        tooltipTextureRegion = new TextureRegion(tooltipTexture, 0, 0, 128, 128);
        
        tooltipToken = new Sprite(tooltipTextureRegion);
        tooltipToken.setSize(128, 128);
        tooltipToken.setOrigin(0, 0);
        
        tooltipDrawable = new SpriteDrawable(tooltipToken);
    }

    @Override
    public void AddItem(Item p_item)
    {
        items.add(p_item);
        Update();
    }

    @Override
    public void SetCharacter(Character p_char)
    {
        player = p_char;
    }
    
    public void SetGearTable(Table p_gearTable)
    {
        gearTable = p_gearTable;
    }
    
    @Override
    public void Update()
    {
        table.clear();
        table.pad(10.0f);
        itemData.clear();

        int size = items.size();
        int count = (int)Math.ceil((double)size / 3.0f);
        
        for (int rows = 0; rows < count; rows++)
        {
            table.row().fill().expandY();
            for (int column = 0; column < 3; column++)
            {
                int index = (rows * 3) + column;
                if (index < size)
                {
                    ItemData id = new ItemData();
                    
                    id.texture = new Texture(Gdx.files.internal("data/"+items.get(index).GetIconName()+".png"));
                    id.texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
                    id.textureRegion = new TextureRegion(id.texture, 0, 0, 32, 32);
                    
                    id.token = new Sprite(id.textureRegion);
                    id.token.setSize(64, 64);
                    id.token.setOrigin(0, 0);
                    
                    id.item = items.get(index);
                    
                    Drawable img = new SpriteDrawable(id.token);
                    id.image = new Image(img);
                    table.add(id.image);

                    id.tooltip = MakeTooltip(id.item);
                    
                    itemData.add(id);
                }
            }
        }
        
        if (gearTable != null)
        {
            gearTable.clear();
            gearTable.row().expandY().fill().expandX();
            
            for (int i = 0; i < 6; i++)
            {
                ItemData id = new ItemData();
                
                id.texture = new Texture(Gdx.files.internal("data/"+player.gear[i].GetIconName()+".png"));
                id.texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
                id.textureRegion = new TextureRegion(id.texture, 0, 0, 32, 32);
                
                id.token = new Sprite(id.textureRegion);
                id.token.setSize(64, 64);
                id.token.setOrigin(0, 0);
                
                
                id.item = player.gear[i];
                
                Drawable img = new SpriteDrawable(id.token);
                id.image = new Image(img);
                
                gearTable.add(id.image);
    
                id.tooltip = MakeTooltip(id.item);
                
                equippedItemData[i] = id;
            }
        }
    }
    
    public Window MakeTooltip(Item p_item)
    {
        Window tooltip = new Window("", skin);
        tooltip.setBackground(tooltipDrawable);
        tooltip.setWidth(256);
        
        tooltip.row().fill().expandX();
        Label nameLabel = new Label(p_item.GetName(), skin);
        tooltip.add(nameLabel);
        Label levelName = new Label(" (level "+p_item.itemLevel+")", skin);
        tooltip.add(levelName);
        
        tooltip.row().fill().expandX();
        Label HPLabel = new Label("HP: ", skin);
        Label HPValue = new Label(""+p_item.stats.HP, skin);
        Label MPLabel = new Label("MP: ", skin);
        Label MPValue = new Label(""+p_item.stats.MP, skin);
        HPLabel.setWidth(50.0f);
        HPValue.setWidth(50.0f);
        MPLabel.setWidth(50.0f);
        MPValue.setWidth(50.0f);
        tooltip.add(HPLabel);
        tooltip.add(HPValue);
        tooltip.add(MPLabel);
        tooltip.add(MPValue);
        
        tooltip.row().fill().expandX();
        Label strLabel = new Label("Str: ", skin);
        Label strValue = new Label(""+p_item.stats.str, skin);
        Label AbsorbLabel = new Label("absorb: ", skin);
        Label AbsorbValue = new Label(p_item.stats.absorb+"", skin);
        strLabel.setWidth(50.0f);
        strValue.setWidth(50.0f);
        AbsorbLabel.setWidth(50.0f);
        AbsorbValue.setWidth(50.0f);
        tooltip.add(strLabel);
        tooltip.add(strValue);
        tooltip.add(AbsorbLabel);
        tooltip.add(AbsorbValue);
        
        tooltip.row().fill().expandX();
        Label armourLabel = new Label("Armour: ", skin);
        Label armourValue = new Label(""+p_item.stats.armour, skin);
        Label dodgeLabel = new Label("Dodge: ", skin);
        Label dodgeValue = new Label(new DecimalFormat("#.##").format(p_item.stats.dodge)+"%", skin);
        armourLabel.setWidth(50.0f);
        armourValue.setWidth(50.0f);
        dodgeLabel.setWidth(50.0f);
        dodgeValue.setWidth(50.0f);
        tooltip.add(armourLabel);
        tooltip.add(armourValue);
        tooltip.add(dodgeLabel);
        tooltip.add(dodgeValue);
        
        tooltip.row().fill().expandX();
        Label critPLabel = new Label("Crit%: ", skin);
        Label critPValue = new Label(new DecimalFormat("#.##").format(p_item.stats.critChance)+"%", skin);
        Label critValLabel = new Label("CritAmt: ", skin);
        Label critValValue = new Label(p_item.stats.critAmount+"", skin);
        critPLabel.setWidth(50.0f);
        critPValue.setWidth(50.0f);
        critValLabel.setWidth(50.0f);
        critValValue.setWidth(50.0f);
        tooltip.add(critPLabel);
        tooltip.add(critPValue);
        tooltip.add(critValLabel);
        tooltip.add(critValValue);
        new DecimalFormat("#.##").format(1.199);
        
        tooltip.setTouchable(null);
        
        return tooltip;
    }

    @Override
    public void RemoveItem(Item item)
    {
        ItemData id = null;
        
        for (int i = 0; i < itemData.size(); i++)
        {
            if (itemData.get(i).item == item)
            {
                id = itemData.get(i);
            }
        }
        
        if (id != null)
        {
            itemData.remove(id);
        }
        
        items.remove(item);
        
        Update();
    }
}

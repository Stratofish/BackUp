package com.etrium.backup.character;

import java.text.DecimalFormat;
import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.etrium.backup.Dungeon;
import com.etrium.backup.Map;
import com.etrium.backup.MapLocation;
import com.etrium.backup.Room;
import com.etrium.backup.TileRenderer;
import com.etrium.backup.item.InventoryInterface;
import com.etrium.backup.item.Item;
import com.etrium.backup.item.PlayerInventory;
import com.etrium.backup.system.ActivityLog;
import com.etrium.backup.system.EtriumEvent;
import com.etrium.backup.system.EventListener;
import com.etrium.backup.system.EventManager;
import com.etrium.backup.system.EventType;

public class Character implements EventListener
{
    private final int GEAR_WEAPON = 0;
    private final int GEAR_HELMET = 1;
    private final int GEAR_CHEST = 2;
    private final int GEAR_LEGS = 3;
    private final int GEAR_BOOTS = 4;
    private final int GEAR_GLOVES = 5;
    
    private CharController controller = null;
    private TileRenderer renderer = null;
    private SpecGenerator specGen = null;
    public InventoryInterface inventory = null;
    public CharUI charUI = null;
    
    private Table uiTable = null;

    private Skin skin = null;
    public Window gearWindow = null;
    private Table gearTable = null;
    
    private EventManager evtMgr = new EventManager();
    private boolean listening = true;
    private Map map;
    private int level = 35;
    private BaseStats baseStats = null;
    private BaseStats curStats = new BaseStats();
    public Vector2 location;
    public Room room = null;
    private int HP = 0;
    private int MP = 0;
    private boolean isPlayer = false;
    private Random random = new Random();
    
    public Item[] gear = new Item[6];
    
    public Character(CharController p_controller,
                     TileRenderer p_renderer,
                     InventoryInterface p_inventory,
                     SpecGenerator p_specGenerator,
                     CharUI p_charUI,
                     Map p_level,
                     int itemLevel,
                     int p_x,
                     int p_y)
    {
        map = p_level;
        
        controller = p_controller;
        controller.SetLevel(map);
        controller.SetCharacter(this);
        
        renderer = p_renderer;
        renderer.SetLevel(map);
        renderer.LoadResources();
        
        inventory = p_inventory;
        inventory.SetCharacter(this);
        specGen = p_specGenerator;
        baseStats = specGen.GetStats(level);
        HP = 1000000;
        MP = 1000000;
        gear[GEAR_WEAPON] = new Item(map, itemLevel, Item.Class.WEAPON, Item.Subclass.NONE);
        gear[GEAR_HELMET] = new Item(map, itemLevel, Item.Class.ARMOUR, Item.Subclass.HELMET);
        gear[GEAR_CHEST] = new Item(map, itemLevel, Item.Class.ARMOUR, Item.Subclass.CHEST);
        gear[GEAR_LEGS] = new Item(map, itemLevel, Item.Class.ARMOUR, Item.Subclass.LEGS);
        gear[GEAR_BOOTS] = new Item(map, itemLevel, Item.Class.ARMOUR, Item.Subclass.BOOTS);
        gear[GEAR_GLOVES] = new Item(map, itemLevel, Item.Class.ARMOUR, Item.Subclass.GLOVES);
        CalcStats();
        
        SetLevel(itemLevel);
                
        Item item = new Item(map, itemLevel, Item.Class.ARMOUR, Item.Subclass.HELMET);
        inventory.AddItem(item);
        item = new Item(map, itemLevel, Item.Class.WEAPON, Item.Subclass.NONE);
        inventory.AddItem(item);
        item = new Item(map, itemLevel, Item.Class.ARMOUR, Item.Subclass.HELMET);
        inventory.AddItem(item);
        item = new Item(map, itemLevel, Item.Class.ARMOUR, Item.Subclass.HELMET);
        inventory.AddItem(item);
        
        charUI = p_charUI;
        charUI.SetChar(this);
        charUI.SetMap(map);
        
        location = new Vector2(p_x, p_y);
        
        room = map.FindRoom((int)location.x, (int)location.y);
        
        evtMgr.RegisterListener(this, EventType.evtItemUsed);
    }
    
    public void SetSkin(Skin p_skin)
    {
        if (isPlayer)
        {
            skin = p_skin;
            gearWindow = new Window("Equipped", skin);
            gearTable = new Table(skin);
            gearWindow.add(gearTable);
            gearWindow.setWidth(350.0f);
            gearWindow.setHeight(90.0f);
            ((PlayerInventory)inventory).SetGearTable(gearTable);
        }
    }
    
    private void CalcStats()
    {
        curStats = baseStats.Copy();
        curStats = BaseStats.Add(curStats, gear[GEAR_WEAPON].stats);
        curStats = BaseStats.Add(curStats, gear[GEAR_HELMET].stats);
        curStats = BaseStats.Add(curStats, gear[GEAR_CHEST].stats);
        curStats = BaseStats.Add(curStats, gear[GEAR_LEGS].stats);
        curStats = BaseStats.Add(curStats, gear[GEAR_BOOTS].stats);
        curStats = BaseStats.Add(curStats, gear[GEAR_GLOVES].stats);
        
        if (HP > curStats.HP)
        {
            HP = curStats.HP;
        }
        if (MP > curStats.MP)
        {
            MP = curStats.MP;
        }
    }

    public void SetLevel(int p_level)
    {
        level = p_level;
        baseStats = specGen.GetStats(level);
        if (HP > curStats.HP)
        {
            HP = curStats.HP;
        }
        if (MP > curStats.MP)
        {
            MP = curStats.MP;
        }
    }
    
    public boolean DoControl()
    {
        if (controller != null)
        {
            boolean result = controller.DoControl();
            room = map.FindRoom((int)location.x, (int)location.y);
            return result;
        }
        
        return false;
    }
    
    public void render(SpriteBatch batch)
    {
        if (renderer != null)
        {
            MapLocation ml = new MapLocation(location);
            renderer.Render(batch, ml);
        }
    }

    public void SetName( String pName)
    {
        specGen.SetName( pName);
    }
    
    public String GetName()
    {
        return specGen.GetName();
    }
   
    public void SetColor(Color p_colour)
    {
        renderer.SetColor(p_colour);
    }

    public void AttackChar(Character p_target)
    {
        int baseAttackAmount = CalcEffectiveLevel();
        baseAttackAmount += (curStats.str * 10);
        
        if (controller.isPlayer())
        {
            baseAttackAmount *= 13;
        }
        
        float critChance = random.nextFloat();
        if ((critChance * 100.0f) < curStats.critChance)
        {
            baseAttackAmount += curStats.critAmount;
        }
        
        int amount = p_target.DoDamage(baseAttackAmount);
        
        boolean killed = !p_target.AreYouAlive();
        
        if (killed)
        {
            ActivityLog.Log(specGen.GetName()+" killed the "+p_target.GetName()+".");
        } else
        {
            ActivityLog.Log(specGen.GetName()+" hit the "+p_target.GetName()+" for "+amount+" damage.");
        }
    }
    
    @Override
    public boolean ReceiveEvent(EtriumEvent p_event)
    {
        if (listening)
        {
            if (isPlayer)
            {
                switch (p_event.type)
                {
                    case evtItemUsed:
                    {
                        Item item = (Item)p_event.data;
                        UseItem(item);
                        
                        return true;
                    }
                }
            }
        }
        
        return false;
    }
    
    public void YouAreTheMan()
    {
        isPlayer = true;
    }

    private void UseItem(Item item)
    {
        if (isPlayer)
        {
            Item.Class cls = item.GetType();
            
            switch(cls)
            {
                case CONSUMABLE:
                {
                    float pctHP = (float)(item.stats.HP) / 100.0f;
                    float pctMP = (float)(item.stats.MP) / 100.0f;                                     
                    
                    int consumedHP = (int)((float)curStats.HP * pctHP);
                    int consumedMP = (int)((float)curStats.MP * pctMP);
                    
                    if ((HP + consumedHP) > curStats.HP)
                    {
                        consumedHP = curStats.HP - HP;
                    }
                    if ((MP + consumedMP) > curStats.MP)
                    {
                        consumedMP = curStats.MP - MP;
                    }
                    
                    HP += consumedHP;
                    MP += consumedMP;
                    
                    ActivityLog.Log(item.GetName() + " consumed for " + (int)consumedHP + " HP");
                    
                    if (HP > curStats.HP)
                    {
                        HP = curStats.HP;
                    }
                    if (MP > curStats.MP)
                    {
                        MP = curStats.MP;
                    }
                    break;
                }
                case WEAPON:
                {
                    gear[0] = item;
                    CalcStats();
                    UpdateGear();
                    break;
                }
                case ARMOUR:
                {
                    Item.Subclass subclass = item.GetSubClass();
                    switch(subclass)
                    {
                        case HELMET:
                        {
                            gear[1] = item;
                            break;
                        }
                        case CHEST:
                        {
                            gear[2] = item;
                            break;
                        }
                        case LEGS:
                        {
                            gear[3] = item;
                            break;
                        }
                        case BOOTS:
                        {
                            gear[4] = item;
                            break;
                        }
                        case GLOVES:
                        {
                            gear[5] = item;
                            break;
                        }
                    }
                    
                    CalcStats();
                    UpdateGear();
                    break;
                }
            } 
            
            inventory.RemoveItem(item);
            
            EtriumEvent evt = new EtriumEvent();
            evt.type = EventType.evtPlayerUIChanged;
            evt.data = null;
            evtMgr.SendEvent(evt,  true);
        }
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
    
    public int DoDamage(int amount)
    {
        if ((Dungeon.godMode) && 
            (controller.isPlayer()))
        {
            return 0;
        }
        
        int mitigatedDamage = amount;
        float dodgeChance = random.nextFloat();
        if ((dodgeChance * 100.0f) < curStats.dodge)
        {
            mitigatedDamage *= 0.5;
        }
        
        float redux = (float)curStats.armour / (float)(curStats.armour+level+10); 
        mitigatedDamage *= (1.0f-redux);
        
        if (mitigatedDamage < 0)
        {
            mitigatedDamage = 0;
        }
        
        HP -= mitigatedDamage;
        
        EtriumEvent evt = new EtriumEvent();
        evt.type = EventType.evtPlayerUIChanged;
        evt.data = null;
        evtMgr.SendEvent(evt,  true);
        
        if (HP > 0)
        {
            return mitigatedDamage;
        }
        
        if (!controller.isPlayer())
        {
            evt = new EtriumEvent();
            evt.type = EventType.evtPlayerUnseen;
            evt.data = this;
            evtMgr.SendEvent(evt,  true);
                        
            /* 
             * Check if opponent is only 80% as strong as the attacker 
             * then the opponent should not drop any loot 
             */ 
            if (mitigatedDamage * 2.75 > curStats.HP)
            {
              ActivityLog.Log("Your gear is too good. You battered your opponent's loot into a pulp along with his head!");
            }
            else
            {
              /* 
               * It was a fair match so drop a random item from the dead characters list 
               * of gear 
               */
              Item dropItem = gear[random.nextInt(gear.length - 1)];
              
              int chance = random.nextInt(5);
              if (chance == 0)
              {
                  dropItem = new Item(map, level, Item.Class.CONSUMABLE, Item.Subclass.HEALTHPOTION );
              }
              
              dropItem.SetLocation( (int)location.x, (int)location.y);
              map.curState.items[(int)location.y][(int)location.x] = dropItem;
              
              ActivityLog.Log("The " + GetName() + " dropped an item.");
            }             
        }
        
        EtriumEvent event = new EtriumEvent();
        event.type = EventType.evtCharDead;
        event.data = this;
        evtMgr.SendEvent(event,  true);
        
        return mitigatedDamage-HP;
    }
    
    public void SetMap(Map p_map, int p_x, int p_y)
    {
        location.x = p_x;
        location.y = p_y;
        map = null;
        map = p_map;
        room = null;
        room = map.FindRoom(p_x, p_y);
        controller.SetLevel(map);
        renderer.SetLevel(map);
        charUI.SetMap(map);
    }
    
    public void SetMap(Map p_map)
    {
        map = p_map;
        controller.SetLevel(map);
        renderer.SetLevel(map);
        charUI.SetMap(map);
    }

    public boolean AreYouAlive()
    {
        if (HP > 0)
        {
            return true;
        }
        
        return false;
    }
    
    public Table GetUIVersion(Skin p_skin)
    {
        if (uiTable == null)
        {
            uiTable = new Table();
        }
        
        uiTable.clear();

        Label mana = null;
        Label effLevel = null;
        if (controller.isPlayer())
        {
            mana = new Label("   MP: " + MP + "/" + curStats.MP, p_skin);
            effLevel = new Label("Effective level: "+CalcEffectiveLevel(), p_skin);
        } else
        {
            mana = new Label("", p_skin);
            effLevel = new Label("", p_skin);
        }
        
        Label health = new Label("HP: " + HP + "/" + curStats.HP, p_skin);
        Label name = new Label(specGen.GetName(), p_skin);
        name.setColor(Color.GREEN);
        
        uiTable.row().fill();
        uiTable.add(name);
        uiTable.add(effLevel);
        uiTable.row().fill();
        uiTable.add(health);
        uiTable.add(mana);
        
        if (controller.isPlayer())
        {
            Label critP = new Label("Crit%: " + new DecimalFormat("#.##").format(curStats.critChance), p_skin);
            Label critA = new Label("   Crit Value: " + curStats.critAmount, p_skin);
            uiTable.row().fill();
            uiTable.add(critP);
            uiTable.add(critA);
            
            Label dodge = new Label("Dodge%: " + new DecimalFormat("#.##").format(curStats.dodge), p_skin);
            Label absorb = new Label("   Magic absorb: " + curStats.absorb, p_skin);
            uiTable.row().fill();
            uiTable.add(dodge);
            uiTable.add(absorb);
            
            Label armour = new Label("Armour: " + curStats.armour, p_skin);
            Label str = new Label("   Str: " + curStats.str, p_skin);
            uiTable.row().fill();
            uiTable.add(armour);
            uiTable.add(str);
        }
        
        uiTable.align(Align.top + Align.left);
        
        return uiTable;
    }
    
    public int CalcEffectiveLevel()
    {
        int eLvl = 0;
        int count = gear.length;
        
        for (int i = 0; i < count; i++)
        {
          eLvl += gear[i].itemLevel;
        }
        
        eLvl /= count;
        
        return eLvl;
    }

    public void UpdateGear()
    {
        if (isPlayer)
        {
            inventory.Update();
            
            if (gearTable == null)
            {
                System.out.println("Nipples!");
            }
            
            gearTable.clear();
            gearTable.row().expandX().expandY().fill();
            
            PlayerInventory pi = (PlayerInventory)inventory;
            for (int i = 0; i < 6; i++)
            {
                gearTable.add(pi.equippedItemData[i].image);
            }
        }
    }
}
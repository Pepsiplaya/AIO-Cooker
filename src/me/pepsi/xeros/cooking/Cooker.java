package me.pepsi.xeros.cooking;

import simple.api.script.Category;
import simple.api.script.ScriptManifest;
import simple.api.script.interfaces.SimplePaintable;
import simple.api.wrappers.SimpleGameObject;
import simple.api.wrappers.SimpleItem;
import simple.api.actions.SimpleObjectActions;
import simple.api.coords.WorldArea;
import simple.api.coords.WorldPoint;
import simple.api.events.ChatMessageEvent;
import simple.api.filters.SimpleBank;
import simple.api.filters.SimpleSkills;
import simple.api.listeners.SimpleMessageListener;
import simple.api.script.Script;

import java.awt.*;

import javax.swing.SwingUtilities;

@ScriptManifest(author = "Pepsiplaya", name = "AIO Cooker", category = Category.COOKING, version = "1.0D",
        description = "Cooks any food at ::skill", discord = "Pepsiplaya#6988", servers = { "Xeros" })

public class Cooker extends Script implements SimplePaintable, SimpleMessageListener {
	
    public String status;
    private long startLevel;
	private long startExp;
    public long startTime;
    private long lastAnimation = -1;
    public boolean started;
    public int foodId;
    
    public GUI gui;
    
    public static final WorldArea SKILLING_AREA = new WorldArea(
            new WorldPoint(3700, 3400, 0),
            new WorldPoint(3900, 3600, 0));
    
    @Override
    public boolean onExecute() {
    	startLevel = ctx.skills.getRealLevel(SimpleSkills.Skill.COOKING);
    	startExp = ctx.skills.getExperience(SimpleSkills.Skill.COOKING);
        startTime = System.currentTimeMillis();
        foodId = -1;
        try {
            Cooker script = this;
            SwingUtilities.invokeLater(new Runnable() { @Override public void run() {
                gui = new GUI(script);
            }});

        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
    
    void bank() {
		SimpleGameObject bank = (SimpleGameObject) ctx.objects.populate().filter(20325).nearest().next();
		
		if (bank != null && ctx.inventory.populate().filter(foodId).isEmpty()) {
			bank.interact(SimpleObjectActions.FIRST);
			status = "Banking";
			ctx.onCondition(() -> ctx.bank.bankOpen(), 250, 10);
			ctx.bank.depositInventory();
			ctx.sleep(750);
			ctx.bank.withdraw(foodId, SimpleBank.Amount.ALL);
			ctx.bank.closeBank();
			ctx.sleep(750);
		}
	}

    @Override
    public void onProcess() {

    	if (!SKILLING_AREA.containsPoint(ctx.players.getLocal())) {
    		return;
        }
    	
    	if (foodId == -1) {
    		return;
        }
    	
        SimpleItem raw = (SimpleItem) ctx.inventory.populate().filter(foodId).next();
	    if (ctx.inventory.populate().filter(foodId).isEmpty()) {
	    	bank();
	    }
	    	if (!ctx.players.getLocal().isAnimating() && (System.currentTimeMillis() > (lastAnimation + 3000))) {
	        	if (raw != null) {
	        		SimpleGameObject range = (SimpleGameObject) ctx.objects.populate().filter(114).nearest().next();
	    			status = "Cooking";
	    			range.interact(SimpleObjectActions.FIRST);
	    			ctx.sleep(2000);
	    			ctx.menuActions.sendAction(315, -1, -1, 13717);
	    			ctx.onCondition(() -> ctx.inventory.populate().filter(foodId).isEmpty(), 250, 10);
	    			ctx.sleep(1000);
	        	}
	    	}  else if (ctx.players.getLocal().isAnimating()) {
                lastAnimation = System.currentTimeMillis();
            }
		} 

    @Override
    public void onTerminate() {
    	
    }
    
    public void setupFood(int foodId) {
        this.foodId = foodId;
    }
    
	public final String format(final long t) {
	    long s = t;
	    return String.format("%", s);
	}
    
	public final String formatValue(final long l) {
	    return (l > 1_000_000) ? String.format("%.2fm", ((double) l / 1_000_000))
	           : (l > 1000) ? String.format("%.1fk", ((double) l / 1000)) 
	           : l + "";
	}
	
    private final Color color1 = new Color(255, 255, 255);
    private final Font font1 = new Font("Arial", 1, 10);
    private final Color color2 = new Color(29, 3, 3, 94);
    private final Color color3 = new Color(0, 0, 0);
    private final BasicStroke stroke1 = new BasicStroke(1);
    
	@Override
    public void onPaint(Graphics2D g) {
		
		final int cachedExp = ctx.skills.getRealLevel(SimpleSkills.Skill.COOKING);
		int catches = 0;
		final long smithingLevels = ctx.skills.getRealLevel(SimpleSkills.Skill.COOKING) - startLevel;
		final long smithingExp = ctx.skills.getExperience(SimpleSkills.Skill.COOKING) - startExp;
		
		if (cachedExp != ctx.skills.getRealLevel(SimpleSkills.Skill.COOKING)) { 
			catches = catches + 1;
		}
		
		 g.setColor(color2);
	     g.fillRect(7, 273, 155, 60);
	     g.setColor(color3);
	     g.setStroke(stroke1);
	     g.drawRect(7, 273, 155, 60);
	     g.setFont(font1);
	     g.setColor(color1);
	     g.drawString("Time Running: " + ctx.paint.formatTime(System.currentTimeMillis() - startTime), 10, 287);
	     g.drawString("Exp / Per Hour: " + formatValue(smithingExp) + " / " + formatValue(ctx.paint.valuePerHour((int) smithingExp, startTime)), 10, 300);
	     g.drawString("Levels / Per Hour: " + smithingLevels + " / " + formatValue(ctx.paint.valuePerHour((int) smithingLevels, startTime)), 10, 313);
	     g.drawString("Status: " + status, 10, 326);
    }
	
    public void onChatMessage(ChatMessageEvent event) {
    	
    }
}
import com.epicbot.api.shared.APIContext;
import com.epicbot.api.shared.GameType;
import com.epicbot.api.shared.entity.GroundItem;
import com.epicbot.api.shared.methods.*;
import com.epicbot.api.shared.model.Area;
import com.epicbot.api.shared.script.LoopScript;
import com.epicbot.api.shared.script.ScriptManifest;
import com.epicbot.api.shared.util.paint.frame.PaintFrame;
import com.epicbot.api.shared.webwalking.model.RSBank;

import java.awt.*;

@ScriptManifest(name = "First Test Script", gameType = GameType.OS)
public class TestClass extends LoopScript {

    /***BEGIN VARIABLES***/
    public String status = "";

    public IInventoryAPI myInventory() {
        return getAPIContext().inventory();
    }

    public IWalkingAPI Walking() {
        return getAPIContext().walking();
    }

    public IBankAPI myBank() {
        return getAPIContext().bank();
    }

    public ILocalPlayerAPI localPosition() {
        return getAPIContext().localPlayer();
    }

    public IGroundItemsAPI groundItems() {
        return getAPIContext().groundItems();
    }

    public Area GRAND_EXCHANGE = new Area(3140, 3470, 3193, 3513);

    /***END VARIABLES***/


    @Override
    public boolean onStart(String... strings) {
        setStatus("Script Starting");
        return true;
    }

    @Override
    protected int loop() {
        if (myInventory().isFull()) {
            setStatus("Heading To Bank...");
            bank();
            if (myBank().isOpen()) {
                setStatus("Depositing Items");
                if (myBank().depositInventory()) ;
                {
                    myBank().close();
                }
            }
        }

        if (!myInventory().isFull() && !GRAND_EXCHANGE.contains(localPosition().getLocation())) {
            setStatus("Walking Back To Loot...");
            Walking().walkTo(GRAND_EXCHANGE.getRandomTile());
        }

        if (!myInventory().isFull() && !GRAND_EXCHANGE.contains(localPosition().getLocation())) {
            String[] neededItems = new String[]{"Tinderbox", "Hammer", "Knife", "Saradomin brew(4)"};
            for (int i = 0; i < neededItems.length; i++) {
                GroundItem getAll = groundItems().query().named(neededItems[i]).results().nearest();
                if (getAll != null)
                setStatus("Gather Loot...");
                getAll.interact("Take");

            }
            return 50;
        }
    }

    public boolean myInventoryisfull() {
        return myInventory().isFull();
    }

    public void bank() {
        Walking().walkTo(RSBank.GRAND_EXCHANGE.getTile());
        myBank().open();
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    protected void onPaint(Graphics2D g, APIContext ctx) {
        PaintFrame frame = new PaintFrame("FirstScript");
        frame.addLine("Action being Preformed", status);
        frame.draw(g, 0, 170, ctx);
    }
}

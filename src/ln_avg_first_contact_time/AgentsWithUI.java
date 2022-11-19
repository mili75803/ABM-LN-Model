/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ln_avg_first_contact_time;


import sim.portrayal.network.*;
import sim.portrayal.continuous.*;
import sim.engine.*;
import sim.display.*;
import sim.portrayal.simple.*;
import javax.swing.*;
import java.awt.Color;
import java.awt.Graphics2D;
import sim.display3d.*;
import sim.portrayal3d.continuous.*;
import sim.portrayal3d.network.*;
import sim.portrayal3d.simple.*;
import java.text.*;
import java.util.WeakHashMap;
import sim.field.network.*;
import sim.portrayal.DrawInfo2D;
import sim.portrayal.Portrayal;
import sim.portrayal.grid.*;
import sim.util.Bag;


public class AgentsWithUI extends GUIState
{
    public Display2D display;
    public JFrame displayFrame;
    ContinuousPortrayal2D yardPortrayal = new ContinuousPortrayal2D();
    NetworkPortrayal2D buddiesPortrayal = new NetworkPortrayal2D();
    FastObjectGridPortrayal2D portrayal = new FastObjectGridPortrayal2D();
//    FastValueGridPortrayal2D, FastHexaObjectGridPortrayal2D, FastHexaValueGridPortrayal2D.
//        , FastValueGridPortrayal2D, FastHexaObjectGridPortrayal2D, FastHexaValueGridPortrayal2D.
//    
    //Adding the 3D window//
    static Display3D display3d;
    public JFrame displayFrame3d;
    static ContinuousPortrayal3D agitatedYardPortrayal = new ContinuousPortrayal3D();  
    NetworkPortrayal3D agitatedBuddiesPortrayal = new NetworkPortrayal3D();
    
    public static void main(String[] args)
    {
        try{
            args = new String[4];
            args[0] = "0.0438229635"; args[1] = "15"; args[2] = "5"; args[3] = "1";
            //${LNlength[j]} ${TCCloneType[j]} ${TotalTC[j]} ${DCCloneType[j]} ${TotalDC[j]} ${fileNum}

            AgentsSetting agentClass = new AgentsSetting( System.currentTimeMillis(), Double.valueOf(args[0]), 
                            Integer.valueOf(args[1]), Integer.valueOf(args[2]), Integer.valueOf(args[3]), 
                            agitatedYardPortrayal, display3d);
            AgentsWithUI vid = new AgentsWithUI(agentClass);
            Console c = new Console(vid);
            c.setVisible(true);
            c.pressPlay();
        }
        catch(Exception e){
            System.out.println(e);
            System.exit(0);
        }
        
        
    }
    public AgentsWithUI(AgentsSetting agentClass) { super(agentClass); }
    public AgentsWithUI(SimState state) { super(state); }
    public static String getName() { return "Yard Cliques"; }
    public void start()
    {
        super.start();
        setupPortrayals();
    }
    public void load(SimState state)
    {
        super.load(state);
        setupPortrayals();
    }
    public void setupPortrayals()
    {
        AgentsSetting students = (AgentsSetting) state;
        // tell the portrayals what to portray and how to portray them
        yardPortrayal.setField( students.yard );
        yardPortrayal.setPortrayalForAll(new OvalPortrayal2D());

        buddiesPortrayal.setField( new SpatialNetwork2D( students.yard, students.buddies ) );
        buddiesPortrayal.setPortrayalForAll(new SimpleEdgePortrayal2D());
    // reschedule the displayer

        display.reset();
        display.setBackdrop(Color.white);
        // redraw the display
        display.repaint();
        agitatedYardPortrayal.setField( students.agitatedYard );
        //define the color of the different agent class
        //agitatedYardPortrayal.setPortrayalForAll(new ConePortrayal3D(Color.red, 2.0));
        //agitatedYardPortrayal.setPortrayalForClass(LymphNodes.class, new ConePortrayal3D(Color.red, 2.0));
       // agitatedYardPortrayal.setPortrayalForClass(Corners.class, new ConePortrayal3D(Color.red, 3.0));
        agitatedYardPortrayal.setPortrayalForClass(LymphNodes.class, new ConePortrayal3D(Color.red, 3.0));
       
        // agitatedYardPortrayal.setPortrayalForClass(Virus.class, new ConePortrayal3D(Color.red, 1.0));
        //agitatedYardPortrayal.setPortrayalForClass(TCell_org.class, new ConePortrayal3D(Color.yellow, 7.50));
        agitatedYardPortrayal.setPortrayalForClass(DendriticCell.class, new ConePortrayal3D(Color.green, 2.0));
        agitatedBuddiesPortrayal.setField( new SpatialNetwork3D( students.agitatedYard, students.buddies ) );
       
        SimpleEdgePortrayal3D ep = new CylinderEdgePortrayal3D()
        {
            DecimalFormat format = new DecimalFormat("#.##");
        };
        ep.setLabelScale(0.5);
        agitatedBuddiesPortrayal.setPortrayalForAll(ep);
        display3d.createSceneGraph();
        display3d.reset();
    }
    
    public void init(Controller c)
    {
        super.init(c);
        display = new Display2D(600,600,this);  //display size is 600*600
        display.setClipping(true);
        displayFrame = display.createFrame();
        displayFrame.setTitle("Yard Display");
        c.registerFrame(displayFrame); // so the frame appears in the "Display" list
        displayFrame.setVisible(true);
        display.attach( buddiesPortrayal, "Buddies" );
        display.attach( yardPortrayal, "Yard" );
    
        //3D
        display3d = new Display3D(600, 600,this);
        AgentsSetting students = (AgentsSetting) state;
        students.display3d = display3d;
        Bag agents = students.TcellNetwork.getAllNodes();
        for(int i = 0; i < agents.size(); i++)
        {
            TCell_org agent = (TCell_org)(agents.get(i));
            agent.display3d = display3d;
        }
        double width = 100;
        display3d.translate(-width / 2.0, -width / 2.0, 0);
        display3d.scale(2.0 / width);
        displayFrame3d = display3d.createFrame();
        displayFrame3d.setTitle("Yard Display 3D");
        c.registerFrame(displayFrame3d); // register the frame so it appears in the "Display" list
        displayFrame3d.setVisible(true);
        display3d.attach( agitatedBuddiesPortrayal, "Buddies ... IN 3-D!" );
        display3d.attach( agitatedYardPortrayal, "Yard ... IN 3-D!" );
    }

    public void quit()
    {
        super.quit();
        if (displayFrame!=null) displayFrame.dispose();
        displayFrame = null;
        display = null;

        //3d
        if (displayFrame3d!=null) displayFrame3d.dispose();
        displayFrame3d = null;
        display3d = null;
    }
}
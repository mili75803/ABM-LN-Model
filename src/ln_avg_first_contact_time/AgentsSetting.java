/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ln_avg_first_contact_time;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/*
front
bottom-left
bottom-right
top-right
top-left
*/
import sim.field.network.*;
import sim.engine.*;
import sim.util.*;
import sim.field.continuous.*;
import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;
import org.apache.commons.math3.distribution.UniformRealDistribution;
import sim.display3d.Display3D;
import static sim.engine.SimState.doLoop;
import sim.portrayal3d.continuous.ContinuousPortrayal3D;
import sim.portrayal3d.simple.ConePortrayal3D;

public class AgentsSetting extends SimState
{
    public Continuous2D yard = new Continuous2D(1.0,100,100);
    public Continuous3D agitatedYard = new Continuous3D(1.0, 100, 100, 1000);
    public ContinuousPortrayal3D agitatedYardPortrayal;
    public double TEMPERING_CUT_DOWN = 0.99;
    public double TEMPERING_INITIAL_RANDOM_MULTIPLIER = 10.0;
    public boolean tempering = true;
    public boolean isTempering() { return tempering; }
    public void setTempering(boolean val) { tempering = val; }
    
    
    
   // double forceToSchoolMultiplier = 0.01;
    double randomMultiplier = 0.1;
    public Network buddies = new Network(false);
    public Network DCList = new Network(false);
    public Network Lymphnode = new Network(false);
    public Network CornersNetwork = new Network(false);
    public Network TcellNetwork = new Network(false);
    int[] TotalIteration = {0};
    int[] count = {1};
    long[] sumContactTime = {0};
    long[] lastFirstContactTime = {0};
    
    //Variables
    LymphNodes LN;
    double volSide = 0.4;
    double LNSide;//volSide * 10 / 100;  // 10% of the service volume size
    int noOfSV = 1;
    int noOfLN = 1;
    int noOfTCCloneType;
    int noOfDCCloneType;
    int noOfDC;
    int noOfTCell;
    int noOfVirus = 1;
    int lastAgentID = 0;
    double scale = 1;
    int DCFirstDetectTime[];
    long contactTime[];
    
    
    double x_left_SV_corner;
    double x_right_SV_corner;
    double y_bottom_SV_corner;
    double y_top_SV_corner;
        
    double x_left_LN_corner;
    double x_right_LN_corner;
    double y_bottom_LN_corner;
    double y_top_LN_corner;
    double z_front_LN_corner;
    double z_back_LN_corner;
    
    Bag Virus_all = new Bag();    
    Bag DC_all = new Bag();
    Bag TC_all = new Bag();
    
    int onlyInitialTime;
    double startTime = 0;
    double[] time;
    int fileNum;
    Display3D display3d;
    public AgentsSetting(long seed, double LNside, int TotalTC, int TotalDC, int fileNum, ContinuousPortrayal3D agitatedYardPortrayal, Display3D display3d)
    {
        super(seed);
        this.noOfDC = TotalDC;
        this.noOfTCell = TotalTC;
        this.LNSide = LNside*1000;
        this.fileNum = fileNum;
        this.agitatedYardPortrayal = agitatedYardPortrayal;
        this.display3d = display3d;
        //Works fine when type  = 1;
        DCFirstDetectTime = new int[TotalTC];
        contactTime = new long[TotalTC];
        for(int i = 0; i<TotalTC; i++)
        {
            DCFirstDetectTime[i] = 0;
            contactTime[i] = 0;
        }
        
    }
    
    public AgentsSetting(long seed, double LNside, int TotalTC, int TotalDC, int fileNum)
    {
        super(seed);
        this.noOfDC = TotalDC;
        this.noOfTCell = TotalTC;
        this.LNSide = LNside*1000;
        this.fileNum = fileNum;
        //Works fine when type  = 1;
        DCFirstDetectTime = new int[TotalTC];
        contactTime = new long[TotalTC];
        for(int i = 0; i<TotalTC; i++)
        {
            DCFirstDetectTime[i] = 0;
            contactTime[i] = 0;
        }
        
    }
    
   
    public void edge(){
        Bag students = CornersNetwork.getAllNodes();
        for(int i = 0; i < students.size() ; i+=8)
        {
           /*front:bottom-left
                    bottom-right
                    top-right
                    top-left
            
            ---
            back:   bottom-left
                    bottom-right
                    top-right
                    top-left 
            */
            Agent St1 = (Agent) students.get(0 + i);
            Agent St2 = (Agent) students.get(1 + i);
            Agent St3 = (Agent) students.get(2 + i);
            Agent St4 = (Agent) students.get(3 + i);
            Agent St5 = (Agent) students.get(4 + i);
            Agent St6 = (Agent) students.get(5 + i);
            Agent St7 = (Agent) students.get(6 + i);
            Agent St8 = (Agent) students.get(7 + i);

            buddies.addEdge(St1, St2, null);  //front-bottom-left to front-bottom-right
            buddies.addEdge(St1, St4, null);  //front-bottom-left to front-top-left
            buddies.addEdge(St1, St5, null);  //front-bottom-left to back-bottom-left

            buddies.addEdge(St2, St3, null);  //front-bottom-right to front-top-right
            buddies.addEdge(St2, St6, null);  //front-bottom-right to back-bottom-right

            buddies.addEdge(St3, St4, null);  //front-top-right to front-top-left
            buddies.addEdge(St3, St7, null);  //front-top-right to back-top-right

            buddies.addEdge(St4, St8, null);  //front-top-left to back-top-left

            buddies.addEdge(St5, St6, null);  //back-bottom-left to back-bottom-right
            buddies.addEdge(St5, St8, null);  //back-bottom-left to back-top-left

            buddies.addEdge(St6, St7, null);  //back-bottom-right to back-top-right
    
            buddies.addEdge(St7, St8, null);  //back-top-right to back-top-left
//            }
            
        }
    }
    
    public void setLN(){
        
        int fixed_position = 0;
        if(fixed_position == 0)
        {
            double centerX = yard.getWidth() / 2;
            double centerY = yard.getHeight() / 2;
            double centerZ = 0;
            LN = new LymphNodes(centerX,centerY,centerZ, LNSide/2, scale);
            LN.ID = lastAgentID++;

            x_left_LN_corner = LN.corner[0].x;
            x_right_LN_corner = LN.corner[1].x;
            y_bottom_LN_corner = LN.corner[0].y;
            y_top_LN_corner = LN.corner[2].y;
            z_front_LN_corner = LN.corner[0].z;
            z_back_LN_corner = LN.corner[4].z;

            //System.out.println(z_back_LN_corner + " " + z_front_LN_corner + " " + (z_front_LN_corner - z_back_LN_corner));

            Lymphnode.addNode(LN);
            buddies.addNode(LN);
            schedule.scheduleRepeating(LN);

            for(int j = 0; j < 8 ; j++ ){
                 yard.setObjectLocation(LN.corner[j],
                    new Double2D(LN.corner[j].x,
                            LN.corner[j].y));

                CornersNetwork.addNode(LN.corner[j]);
                buddies.addNode(LN.corner[j]);
                schedule.scheduleRepeating(LN.corner[j]);
            }
        }        
    }
        
    public void setSVandLNcorner()
    {
         x_left_SV_corner = (yard.getWidth() - yard.getWidth()*volSide)/2;
    
         x_right_SV_corner = x_left_SV_corner + yard.getWidth()*volSide;
        
         y_bottom_SV_corner = (yard.getHeight() - yard.getHeight()*volSide)/2;
         y_top_SV_corner = y_bottom_SV_corner + yard.getHeight()*volSide;
            
        //LN range where a DC won't be
        Bag agents = Lymphnode.getAllNodes();
         x_left_LN_corner = (yard.getWidth() - yard.getWidth()*LNSide)/2;
         x_right_LN_corner = x_left_LN_corner + yard.getWidth()*LNSide;
        
         y_bottom_LN_corner = (yard.getHeight() - yard.getHeight()*LNSide)/2;
         y_top_LN_corner = y_bottom_LN_corner + yard.getHeight()*LNSide;
    }
    
    public void setLNCorners()
    {
        //System.out.println(yard.getWidth());
        x_left_LN_corner = (yard.getWidth() - yard.getWidth()*LNSide/2)/2;
        x_right_LN_corner = x_left_LN_corner + yard.getWidth()*LNSide/2;
        
        y_bottom_LN_corner = (yard.getHeight() - yard.getHeight()*LNSide/2)/2;
        y_top_LN_corner = y_bottom_LN_corner + yard.getHeight()*LNSide/2;
    }
    
   
    public void setDC()
    {
        Random randX = new Random();
        Random randY = new Random();
        Random randZ = new Random();



        randX.setSeed(System.currentTimeMillis() + ThreadLocalRandom.current().nextInt());
        randY.setSeed(System.currentTimeMillis() + ThreadLocalRandom.current().nextInt());
        randZ.setSeed(System.currentTimeMillis() + ThreadLocalRandom.current().nextInt());

        
        for(int j = 0; j < noOfDC; j++)
        {
            DendriticCell DC = new DendriticCell(x_left_LN_corner, x_right_LN_corner, y_bottom_LN_corner, y_top_LN_corner, LNSide);
            DC.ID = lastAgentID++;
        
            DC.x = x_left_LN_corner + (x_right_LN_corner - x_left_LN_corner) * randX.nextDouble();
            DC.y = y_bottom_LN_corner + (y_top_LN_corner - y_bottom_LN_corner) * randY.nextDouble();
            DC.z = z_back_LN_corner + (z_front_LN_corner - z_back_LN_corner) * randZ.nextDouble();

            DC_all.add(DC);
            yard.setObjectLocation(DC,
                new Double2D( DC.x,
                DC.y));

            DCList.addNode(DC);
            buddies.addNode(DC);
            //schedule.scheduleRepeating(DC);   


        }
    }
    
    public void setTCell()
    {
        Semaphore sem = new Semaphore(1);
        
        //setSVandLNcorner();
        //for(int i = 0; i < noOfTCCloneType; i++)
        {
            Random randX = new Random();
            Random randY= new Random();
            Random randZ = new Random();
            
            randX.setSeed(System.currentTimeMillis() + ThreadLocalRandom.current().nextInt());
            randY.setSeed(System.currentTimeMillis() + ThreadLocalRandom.current().nextInt());
            randZ.setSeed(System.currentTimeMillis() + ThreadLocalRandom.current().nextInt());
            
            
            for(int j = 0; j < noOfTCell; j++)
            {
                TCell_org t_cell = new TCell_org(this, LN, LNSide, scale, DCList, this.noOfTCell, sem, TotalIteration, fileNum, count, DCFirstDetectTime, sumContactTime, lastFirstContactTime, contactTime, agitatedYardPortrayal, display3d);
                //Double2D loc = (Double2D)(yard.getObjectLocation(t_cell));
                t_cell.ID = lastAgentID++;
                t_cell.localID = j+1;
                TC_all.add(t_cell);
                //setting initial position 
                double x;
                double y;
                t_cell.x = x_left_LN_corner + (x_right_LN_corner - x_left_LN_corner) * randX.nextDouble();
                t_cell.y = y_bottom_LN_corner + (y_top_LN_corner - y_bottom_LN_corner) * randY.nextDouble();
                t_cell.z = z_back_LN_corner + (z_front_LN_corner - z_back_LN_corner) * randZ.nextDouble();
                t_cell.setCoordinates(t_cell.x, t_cell.y, t_cell.z, t_cell.path);

                yard.setObjectLocation(t_cell, new Double2D( t_cell.x, t_cell.y));

                //System.out.println(t_cell.localID + " DC size: "+t_cell.myDC.size());
                for(int n = 0; n < t_cell.myDC.size(); n++)
                {
                    DendriticCell DCagent = (DendriticCell)(t_cell.myDC.get(n));
                    t_cell.distanceWithDC[n] = Math.sqrt( Math.pow((DCagent.x - t_cell.x),2) + Math.pow((DCagent.y - t_cell.y),2) + Math.pow((DCagent.z - t_cell.z),2));
                    String distance = t_cell.distanceWithDC[n] + "";
                    //fileWrite(distance, t_cell.ID);
                }


                
               // t_cell.stop = schedule.scheduleRepeating(t_cell);
                
            
                //buddies.addNode(t_cell);
                sim.engine.Stoppable stoppableObj;
                stoppableObj = schedule.scheduleRepeating(t_cell);
                t_cell.stop = stoppableObj;
                TcellNetwork.addNode(t_cell);
                buddies.addNode(t_cell);
            
                }
        }
    }
    
    
   public void fileWrite(String mycontent, int ID) {
        BufferedWriter bw = null;
        try {
            //System.out.println(ID);
            File file = new File("Distance" + ID + fileNum + ".txt");
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file, true);
            bw = new BufferedWriter(fw);
            bw.write(mycontent);
            bw.newLine();
            bw.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (Exception ex) {
                System.out.println("Error in closing the BufferedWriter" + ex);
            }
        }
    }
    
    
    
   
    public void start()
    {      
        super.start();
        // add the tempering agent
        // clear the yard
        yard.clear();
        // clear the buddies
        buddies.clear();
        agitatedYard.clear();
        
        //set the service volume
        
        //setServiceVolume();
        //setLNCorners();
        setLN();    /*Creating the LN with its corner within the 2D yard/display*/
        setDC();    /*Creating the DC and setting them within the 2D LN */
        load3DLNandDC();  /*setting the DCs amd LN in 3d display */
        setTCell(); /*Creating the T cells and setting them within the 2D LN */
  
        
        //below is for visualization purpose
 
        Steppable steppable = new Steppable()
        {
            public void step(SimState state) { load3DStudents(); }  //T cells are moving in every step. Hence setting the T cells in 3d display 
        };
        
        schedule.scheduleRepeating(steppable);
        load3DStudents(); //setting the T cells in 3d display primarily
  
    }
    
    public void load3DLNandDC()
    {
        Bag agents = Lymphnode.getAllNodes();
        for(int i = 0; i < agents.size(); i++)
        {
            Agent agent = (Agent)(agents.get(i));
            LymphNodes ln = (LymphNodes) agent;
            for(int j = 0; j < 8; j++){
                Double2D locCorner = (Double2D)(yard.getObjectLocation(ln.corner[j]));
                agitatedYard.setObjectLocation(ln.corner[j], new Double3D(locCorner, ln.corner[j].z));
            }
            edge();
        }
        
        
        Bag agentsDC = DCList.getAllNodes();
        for(int i = 0; i < agentsDC.size(); i++)
        {
            Agent agent = (Agent)(agentsDC.get(i));
            Double2D loc = (Double2D)(yard.getObjectLocation(agent));
            agitatedYard.setObjectLocation(agent, new Double3D(loc, agent.z));
        }
    }
    int cnt = 0;
    public void load3DStudents()  //Loading all the T cells in the 3d LN
    {
        Bag agents = TcellNetwork.getAllNodes();
        for(int i = 0; i < agents.size(); i++)
        {
            TCell_org agent = (TCell_org)(agents.get(i));
            Double2D loc = (Double2D)(yard.getObjectLocation(agent));
            agitatedYard.setObjectLocation(agent, new Double3D(loc, agent.z));
        }
        //if(display3d != null) {display3d.createSceneGraph();}
    }
    
    
    public static void main(String[] args)
    {
        args = new String[4];
        args[0] = "2"; args[1] = "21"; args[2] = "5"; args[3] = "1";
        
        //${LNlength[j]} ${TotalTC[j]} ${TotalDC[j]} ${fileNum}

        final Class c = AgentsSetting.class;
        
        doLoop(new MakesSimState()
            {
            public SimState newInstance(long seed, String[] args)
                {
                try
                    {
                    return (SimState)(c.getConstructor(new Class[] { Long.TYPE, Double.TYPE, Integer.TYPE, 
                        Integer.TYPE, Integer.TYPE}).newInstance(new Object[] { Long.valueOf(seed), Double.valueOf(args[0]), 
                            Integer.valueOf(args[1]), Integer.valueOf(args[2]), 
                            Integer.valueOf(args[3])}));
                    }
                catch (Exception e)
                    {
                    throw new RuntimeException("Exception occurred while trying to construct the simulation " + c + "\n" + e);
                    }
                }
            public Class simulationClass() { return c; }
            }, args);
        System.exit(0);
    }
    
    /*
        public static void main(String[] args)
    {
        doLoop(AgentsSetting.class, args);
        System.exit(0);
    }
    */
}
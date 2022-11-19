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

import static com.sun.media.rtp.TrueRandom.rand;
import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import sim.engine.*;
import sim.field.continuous.*;
import sim.util.*;
import java.lang.Math; 
import static java.lang.Math.PI;
import static java.lang.Math.acos;
import static java.lang.Math.atan2;
import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;
import java.util.Vector;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.math3.distribution.GammaDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;
import sim.field.network.Network;
import sim.util.distribution.Gamma;
import javax.vecmath.Vector3d;
import sim.util.distribution.Normal;
import sim.portrayal.*;
import java.awt.*;
import sim.display3d.Display3D;
import sim.portrayal3d.continuous.ContinuousPortrayal3D;
import sim.portrayal3d.network.SpatialNetwork3D;
import sim.portrayal3d.simple.ConePortrayal3D;

public class TCell_org extends Agent
{
    
    Color color;
    int localID;
    int noOfTCCloneType;
    int noOfDCCloneType;
    int noOfTCPerCloneType;
    double LNside;
    double vol_x_left_corner;
    double vol_x_right_corner;
    double vol_y_top_corner;
    double vol_y_bottom_corner;
    double LN_x_left_corner;
    double LN_x_right_corner;
    double LN_y_top_corner;
    double LN_y_bottom_corner;
    double  z_back_LN_corner, z_front_LN_corner;
    boolean DCdetected = false;
    double time_resolution;
    double min_step = 0.0001; // to avoid flaoting point zero errors
    double scale = 0.0;
    double angle = 0.0;
    double old_x;
    double old_y;
    double old_z;
    int fileNum;
    int[] TotalTCCompleted;  //keep track of how many TC has finished working (either found a TC or completed a specific time (iteration) in the system)
    long tIteration;
    Bag myDC;
    boolean timeFlag = true;
    ArrayList<TCellCoordinate3D> path = new ArrayList<TCellCoordinate3D>();
    TCellCoordinate3D previous_vector = new TCellCoordinate3D();
    TCellCoordinate3D new_vect = new TCellCoordinate3D();
    public static final int RAND_MAX = 2147483647;
     public Network DCList = new Network(false);    
    Semaphore sem;
    double[] time;
    long countIteration = 0;
    int[] count;
    int countIndv;
    private Gamma gammaDist = null;
    SimState state;
    double scaleAxis;
    double[] distanceWithDC;
    Random randX, randY, randZ, azmuthRandom, inclinationRandom, randomTest;
    int TotalTC = 0;
    int DCFirstDetectTime[];
    int groupLocalID;
    int foundDCnum = 0;
    long lastFirstContactTime[];
    long[] sumContactTime;
    long[] contactTime;
    int onlyInitialTime;
    Stoppable stop;
    UniformRealDistribution uniformX; 
    UniformRealDistribution uniformY; 
    UniformRealDistribution uniformZ;
    UniformRealDistribution uniformTheta;
    UniformRealDistribution uniformPhi;

    ContinuousPortrayal3D agitatedYardPortrayal;
    Display3D display3d;
    private Vector3d past = new Vector3d(0.0, 0.0, 0.0);
    private Vector3d previous = new Vector3d(0.0, 0.0, 0.0);
    private double inclination = 0.0, azimuth = 0.0;
    private Normal gaussianDist = null;
    
    public Color getColor() {
        return Color.red;
    }
                
    public TCell_org(SimState state, LymphNodes LN, double LNside, double scale, Network DCList, int noOfTCPerCloneType, Semaphore sem, int[] TotalTCCompleted,
             int fileNum, int[] count, int[] DCFirstDetectTime, long[] sumContactTime, long[] lastFirstContactTime, long[] contactTime,  ContinuousPortrayal3D agitatedYardPortrayal, Display3D display3d)
    {
        this.state = state;
        this.color = Color.yellow;
        this.LN_x_left_corner = LN.corner[0].x;
        this.LN_x_right_corner = LN.corner[1].x;
        this.LN_y_bottom_corner = LN.corner[0].y;
        this.LN_y_top_corner = LN.corner[2].y;
        this.z_back_LN_corner = LN.corner[4].z;
        this.z_front_LN_corner = LN.corner[0].z;
        this.LNside = LNside;
        this.noOfTCCloneType = 1;
        this.TotalTC = noOfTCCloneType * noOfTCPerCloneType;
        time_resolution = 10;
        this.DCList = DCList;
        this.sem = sem;
        gammaDist = new Gamma(2.1586, 0.598799, state.random);
        this.scaleAxis = scale;
        this.TotalTCCompleted = TotalTCCompleted;
        this.tIteration = 0;
        this.count = count;
        this.sumContactTime = sumContactTime;
        this.lastFirstContactTime = lastFirstContactTime;
        this.countIndv = 0;
        this.myDC = new Bag();
        this.fileNum = fileNum;
        randX = new Random();
        randY= new Random();
        randZ = new Random();
        this.DCFirstDetectTime = DCFirstDetectTime;
        this.contactTime = contactTime;
        randX.setSeed(System.currentTimeMillis() + ThreadLocalRandom.current().nextInt());
        randY.setSeed(System.currentTimeMillis() + ThreadLocalRandom.current().nextInt());
        randZ.setSeed(System.currentTimeMillis() + ThreadLocalRandom.current().nextInt());
        azmuthRandom = new Random();
        azmuthRandom.setSeed(System.currentTimeMillis() + ThreadLocalRandom.current().nextInt());
        inclinationRandom = new Random();
        inclinationRandom.setSeed(System.currentTimeMillis() + ThreadLocalRandom.current().nextInt());
        gaussianDist = new Normal(0.277, 0.0958, state.random);
        
        uniformX = new UniformRealDistribution(-1,1);
        uniformY = new UniformRealDistribution(-1,1);
        uniformZ = new UniformRealDistribution(-1,1);
        
        uniformTheta = new UniformRealDistribution(0,1);
        uniformPhi = new UniformRealDistribution(0,1);

        this.agitatedYardPortrayal = agitatedYardPortrayal;
        
        //this.agitatedYardPortrayal.setPortrayalForObject(this, new ConePortrayal3D(this.color, 2.0));
        //this.display3d = display3d;
        Bag DCagents = DCList.getAllNodes();
        for(int j = 0; j < DCagents.size(); j++)
        {
            DendriticCell DCagent = (DendriticCell)(DCagents.get(j));
            if(this.localID == DCagent.localID)
            {
                this.myDC.add(DCagent);
            }
            
        }
        distanceWithDC = new double[this.myDC.size()]; 
        
    }
    
    public TCell_org(double x, double y, double z){    //just to store new temporary x,y,z before checking that they are in the boundary.
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public void setCoordinates(double x,  double y,  double z, ArrayList<TCellCoordinate3D> path)
    {
        TCellCoordinate3D coordinate = new TCellCoordinate3D();
        coordinate.setCoordinates(x, y, z);
        if(path.size() > 1)
        {
            path.remove(0);
        }
        path.add(coordinate);
    }
    
    double getFromLogNormalPDF(double mean, double SD)
    {
        double normal_var = ThreadLocalRandom.current().nextGaussian()*SD + mean; // getFromNormalPDF( -2.5, 0.9329);
        return Math.exp(normal_var);
    }
    
    double norm(double x, double y, double z)
    {
        return Math.sqrt(x*x + y*y + z*z);
    }
    
    
    double norm(double x_prev, double y_prev, double z_prev, double x, double y, double z)
    {
        return Math.sqrt((x_prev -x)*(x_prev -x) + (y_prev -y)*(y_prev -y) + (z_prev - z)*(z_prev -z));
    }
    
    double dot_prod(double x0, double y0, double z0, double x1, double y1, double z1)
    {
        return x0*x1+y0*y1+z0*z1;
    }
    
    public TCellCoordinate3D rotateAboutVector(TCellCoordinate3D vect, TCellCoordinate3D axis, double angle)
    {
        TCellCoordinate3D result = new TCellCoordinate3D();

        double axis_L =  norm(axis.x,axis.y,axis.z);
        TCellCoordinate3D unit_axis = new TCellCoordinate3D();
        unit_axis.setCoordinates(axis.x / axis_L, axis.y / axis_L, axis.z / axis_L);
               
        double u = unit_axis.x;
        double v = unit_axis.y;
        double w = unit_axis.z;

        double x = vect.x;
        double y = vect.y;
        double z = vect.z;

        double cos_angle = Math.cos(angle);
        double sin_angle = Math.sin(angle);

        double dp = dot_prod(x,y,z,u,v,w);

        double x_row = u*dp*(1-cos_angle)+x*cos_angle+(-w*y+v*z)*sin_angle;
        double y_row = v*dp*(1-cos_angle)+y*cos_angle+(w*x-u*z)*sin_angle;
        double z_row = w*dp*(1-cos_angle)+z*cos_angle+(-v*x+u*y)*sin_angle;

        result.setCoordinates(x_row, y_row, z_row);
       
        return result;
    }
    
    
    public void fileWrite(String mycontent) {
        BufferedWriter bw = null;
        try {
            File file = new File("NoOfContact" + fileNum + ".txt");
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
    
    
    public void fileWriteFirstContact(String mycontent) {
        BufferedWriter bw = null;
        try {
            File file = new File("FirstContactTime" + fileNum + ".txt");
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
    
    
    public void fileWriteAvgFirstContact1(String mycontent) {    //last contact / number of contacts
        BufferedWriter bw = null;
        try {
            File file = new File("AvgFromLastFirstContactTime" + fileNum + ".txt");
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
    
    public TCell_org CRW(){
        double inclination = 0;
        double radius = 0;
        double azmuth = 0;
        double previous_vector_x = 0;
        double previous_vector_y = 0;
        double previous_vector_z = 0;
        double change_in_azmuth = 0;
        //scale =   getFromLogNormalPDF(0.4818, 0.919); 
        double scale =   getFromLogNormalPDF(-2.5, 0.93)*100;
        //System.out.println(scale);
        if (scale < min_step) 
        {
            scale = min_step;
        }     
        double x_pos = this.x;
        double y_pos = this.y;
        double z_pos = this.z;

        old_x = x_pos;
        old_y = y_pos;
        old_z = z_pos;

        double new_step_x, new_step_y, new_step_z;
        int size = path.size();

        double old_dis = norm(x_pos, y_pos, z_pos);
        if (path.size() < 2)
        {
            new_step_x = randX.nextGaussian(); 
            new_step_y = randY.nextGaussian();
            new_step_z = randZ.nextGaussian();
        }
        else
        {
            //int size = path.size();
            previous_vector_x = path.get(size - 1).x - path.get(size - 2).x;
            previous_vector_y = path.get(size - 1).y - path.get(size - 2).y;
            previous_vector_z = path.get(size - 1).z - path.get(size - 2).z;

            // Convert to spherical coordinates
            radius = norm(previous_vector_x, previous_vector_y, previous_vector_z);
            inclination = acos(previous_vector_z/radius);
            azmuth = atan2(previous_vector_y, previous_vector_x);

            double change_in_inclination = gammaDist.nextDouble(); // new GammaDistribution(2.1586, 0.598799).sample(); // getFromGammaPDF(2.1586,0.598799);
            
            //if (this.localID == 155)
            //System.out.println(this.localID + " " +change_in_inclination);
//aMean + fRandom.nextGaussian() * aVariance;
            change_in_azmuth = azmuthRandom.nextDouble()* 2.0 * PI;



            inclination += change_in_inclination;

            
            // Convert to Cartesian coordinates
            new_step_x = radius*Math.sin(inclination)*Math.cos(azmuth);
            new_step_y = radius*Math.sin(inclination)*Math.sin(azmuth);
            new_step_z = radius*Math.cos(inclination);

            azmuth += change_in_azmuth;

            new_vect.setCoordinates(new_step_x, new_step_y, new_step_z);
            previous_vector.setCoordinates(previous_vector_x,previous_vector_y,previous_vector_z);

            new_vect.setCoordinates(rotateAboutVector(new_vect, previous_vector, change_in_azmuth));

            new_step_x = new_vect.x;
            new_step_y = new_vect.y;
            new_step_z = new_vect.z;
        }


        double scale_step = norm(new_step_x, new_step_y, new_step_z);
        new_step_x /= scale_step;
        new_step_y /= scale_step;
        new_step_z /= scale_step;

        new_step_x = scale*new_step_x; // dividing by the scale_step makes the distribution uniform because normal distributions are radially symmetric and also makes it a unit vector
        new_step_y = scale*new_step_y; // Multiplyng by the scale gives the length drawn from one of the probability distributions above
        new_step_z = scale*new_step_z;

        x_pos = x_pos + new_step_x;
        y_pos = y_pos + new_step_y;
        z_pos = z_pos + new_step_z;
        TCell_org tempTCell = new TCell_org(x_pos, y_pos, z_pos);
        
        return tempTCell;
    }
    
    public TCell_org BrownianMotion(){
        //double scale =   getFromLogNormalPDF(0.4818, 0.919)/100;
        double scale = 0.097*100;
        double x_pos = this.x;
        double y_pos = this.y;
        double z_pos = this.z;
        
        previous_vector.setCoordinates(x_pos,y_pos,z_pos);

        
        double theta = 2 * Math.PI * uniformTheta.sample();;     //inclination
        double phi = Math.acos(1 - 2 * uniformPhi.sample());     //azimuth
        //System.out.println("theta "+theta);
        double new_step_x = scale * Math.sin(phi) * Math.cos(theta);
        double new_step_y = scale * Math.sin(phi) * Math.sin(theta);
        double new_step_z = scale * Math.cos(phi);

        
        /*
        double scale_step = norm(new_step_x, new_step_y, new_step_z);
        new_step_x /= scale_step;
        new_step_y /= scale_step;
        new_step_z /= scale_step;

        new_step_x = scale*new_step_x; // dividing by the scale_step makes the distribution uniform because normal distributions are radially symmetric and also makes it a unit vector
        new_step_y = scale*new_step_y; // Multiplyng by the scale gives the length drawn from one of the probability distributions above
        new_step_z = scale*new_step_z;
       */
        x_pos = x_pos + new_step_x;
        y_pos = y_pos + new_step_y;
        z_pos = z_pos + new_step_z;

        
        new_vect.setCoordinates(new_step_x, new_step_y, new_step_z);
//        new_vect.setCoordinates(rotateAboutVector(new_vect, previous_vector, change_in_azmuth));
        double new_dis = norm(x_pos, y_pos, z_pos);
        //System.out.println((new_dis - old_dis));
        TCell_org tempTCell = new TCell_org(x_pos, y_pos, z_pos);
        return tempTCell;
    }

	
    public void fileWriteDistributionFirstContact1(String mycontent) {    
        BufferedWriter bw = null;
        try {
            File file = new File("DistributionFirstContactTime" + fileNum + ".txt");
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
    
        public void fileWriteAvgFirstContact2(String mycontent) {    //sum of first contacts / number of contacts
        BufferedWriter bw = null;
        try {
            File file = new File("AvgFromSumFirstContactTime" + fileNum + ".txt");
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
    public void remove(Continuous2D yard){
            yard.remove(this);  // remove it from space
            if(stop != null)
                stop.stop();   // remove it from the list
       }
    

    public void remove(SimState state){
        AgentsSetting as = (AgentsSetting)state;
        as.yard.remove(this);  // remove it from space
        if(stop != null)
            stop.stop();   // remove it from the list
     }
public void step(SimState state)
    {
        boolean flag = true;
        AgentsSetting students = (AgentsSetting) state;
        Continuous2D yard = students.yard;
        Continuous3D agitatedYard = students.agitatedYard;
        //ContinuousPortrayal3D agitatedYardPortrayal = new ContinuousPortrayal3D();
        //agitatedYardPortrayal.setField( students.agitatedYard );
        //Double2D loc = (Double2D)(yard.getObjectLocation(this));
        try
        {
            if(!DCdetected)
            {
                while(flag == true)
                {
                    
                    //TCell tempTCell = CRW();
                    TCell_org tempTCell = BrownianMotion();
                    
                    double x_pos = tempTCell.x;
                    double y_pos = tempTCell.y;
                    double z_pos = tempTCell.z;
                        
                    //check? within the LN volume
                    if ((x_pos >= LN_x_left_corner && x_pos <= LN_x_right_corner) && (y_pos >= LN_y_bottom_corner && y_pos <= LN_y_top_corner) && (z_pos >= z_back_LN_corner && z_pos <= z_front_LN_corner)) 
                    {
                        
                        flag = false; //the coordinates are within the LN
                        this.x = x_pos;
                        this.y = y_pos;
                        this.z = z_pos;
                        setCoordinates(this.x, this.y, this.z, path);
                        //System.out.println(this.x + " " + this.y + " "+ this.z);
                        //yard.setObjectLocation(this, new Double2D( this.x,this.y));
       
                        tIteration++;
                        for (int i = 0; i < myDC.numObjs; i++) {
                            DendriticCell DCagent = (DendriticCell) (myDC.get(i));
                            
                            double dis = Math.sqrt((DCagent.x - this.x)*(DCagent.x - this.x) + (DCagent.y - this.y)*(DCagent.y - this.y) + (DCagent.z - this.z)*(DCagent.z - this.z));
                            
                            if(dis < 10.00){
                                DCdetected = true;   
                                sem.acquire();
                                contactTime[count[0]-1] = tIteration;
                                String distributionFirstContact = String.valueOf(contactTime[count[0]-1]);
                                fileWriteDistributionFirstContact1(distributionFirstContact);
                                
                                count[0]++;
                                sumContactTime[0] += tIteration;
                                lastFirstContactTime[0] = tIteration + 1;  //last contact time
                                TotalTCCompleted[0]++;
                                
                                
                                //if all TCs are contacted with DC, we take the average
                                if(TotalTCCompleted[0] == TotalTC){
                                    double avg = sumContactTime[0]/(count[0]-1);
                                    String avg_sumContactTime = String.valueOf(avg);
                                    //String Count = String.valueOf(count[0] - 1);
                                    //fileWrite(Count);
                                    //fileWriteAvgFirstContact1(avg1_lastFirstTimeContact);
                                    fileWriteAvgFirstContact2(avg_sumContactTime);
                                    sem.release();
                                    System.exit(0);
                                }
                                
                                //System.exit(0);
                                remove(state);
                                sem.release();
                            }
                        }
                        break;
                    }
                }
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(TCell_org.class.getName()).log(Level.SEVERE, null, ex);
        }
        yard.setObjectLocation(this, new Double2D( this.x,this.y));
       //agitatedYard.setObjectLocation(this, new Double3D(loc, this.z));
    }
}
    


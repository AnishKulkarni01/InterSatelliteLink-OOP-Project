import java.util.ArrayList;
import java.io.*;
import java.util.*;
import java.util.ArrayList;
import javax.swing.JComponent;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.Color;
import java.util.Random;

class Constellation {
    public static int gsfId;
    public static int gsiId;
    public static int leofId;
    public static int leoiId;
    public static int counter=0;
    public static int countergs=0;
    public static final String RED_BRIGHT = "\033[0;91m";    
    public static final String GREEN_BRIGHT = "\033[0;92m";
    public static final String ANSI_WHITE = "\u001B[37m";

    
}

class Satellite{
    int id;
    static ArrayList<LEOSatellite>LEOarr=new ArrayList<LEOSatellite>();
    static ArrayList<GroundStation>GSarr=new ArrayList<GroundStation>();
    public void initialize(){
        for(int i=0;i<10;i++){
            if(i<5){
                Satellite.LEOarr.add(new LEOSatellite(i));}
                Satellite.GSarr.add(new GroundStation(i));
            }
        }
}

class GroundStation extends Constellation implements Runnable  {
    int id;
   
    GroundStation(int id){
        this.id=id;
        this.leoiId=id/2;
        this.leofId=gsfId/2;
    }

    public void sendMsg(){
        countergs++;
        System.out.println("\n"+RED_BRIGHT+"GroundStation "+id+" thread:"+GREEN_BRIGHT+"This is GroundStation "+id+".Sending message to LEOSatellite "+Satellite.LEOarr.get(leoiId).id+ANSI_WHITE);
        Thread ls2=new Thread(Satellite.LEOarr.get(leoiId));
        ls2.start();
    }

    public void receivedMsg(){
        System.out.println(RED_BRIGHT+"Groundstation "+id+" thread:"+GREEN_BRIGHT+"This is GroundStation "+id+".Recieved message."+ANSI_WHITE);
    }
    
    @Override
    public void run() {
        if(countergs==0)this.sendMsg();
        else if(countergs==1)this.receivedMsg();
    }   
}

class LEOSatellite extends Constellation implements Runnable {
    int id;
    boolean sentMsgtoGso;
    LEOSatellite(int id){
        this.id=id;
        this.leoiId=id/2;
        this.leofId=gsfId/2;
    }

    public void sendMsgtoGSO(){
        GSOSatellite gso=new GSOSatellite();
        System.out.println(RED_BRIGHT+"LEOSatellite "+id+" thread:"+GREEN_BRIGHT+"This is LEOSatellite "+id+".Sending message to GSOSatellite "+0+ANSI_WHITE);
        counter++;
        Thread geo=new Thread(gso);
        geo.start();
        
    }

    public void sendMsgtoGS(){
        System.out.println(RED_BRIGHT+"LEOSatellite "+id+" thread:"+GREEN_BRIGHT+"This is LEOSatellite "+id+".Sending message to GroundStation "+Satellite.GSarr.get(gsfId).id+ANSI_WHITE);
        Thread gsf=new Thread(Satellite.GSarr.get(gsfId));
        gsf.start();
    }

    public void sendMsgtoLEO(){
        System.out.println(RED_BRIGHT+"LEOSatellite "+id+" thread:"+GREEN_BRIGHT+"This is LEOSatellite "+id+".Sending message to LEOSatellite "+leofId+ANSI_WHITE);
        Thread ls3=new Thread(Satellite.LEOarr.get(leofId));
        counter=2;
        ls3.start();
    }

    @Override
    public void run() {
     if(Math.abs(gsfId/2-gsiId/2)==1&& counter!=2)this.sendMsgtoLEO();
      else if(counter==2||Math.abs(gsfId-gsiId)<=1) this.sendMsgtoGS();
      else if(counter==0)this.sendMsgtoGSO();
      else if(Math.abs(gsfId-gsiId)==2||Math.abs(gsfId-gsiId)==3)this.sendMsgtoLEO();    
    }
}

class GSOSatellite extends Constellation implements Runnable {
    boolean receivedMsg;
    public void sendMsgs(){
        LEOSatellite leo=new LEOSatellite(Constellation.leofId);
        System.out.println(RED_BRIGHT+"GSOSatellite "+0+" thread:"+GREEN_BRIGHT+"This is GSO "+0+".Sending message to LEOSatellite "+leo.id+ANSI_WHITE);
        counter++;
        receivedMsg=true;
        Thread ls2=new Thread(leo);
        ls2.start();
    }

    @Override
    public void run() {
       this.sendMsgs();
    }
}

//GUI
class GraphicsDemo extends JFrame {
  
    GraphicsDemo(JFrame f){
        
        
        DrawingCanvas dc=new DrawingCanvas(800, 600, Constellation.gsiId, Constellation.gsfId);
        f.add(dc);
        f.setVisible(true);  
    }
}

class DrawingCanvas extends JComponent{
    int width;
    int height;
    int gsi,gsf,leoi,leof;
    
    public DrawingCanvas(int w,int h,int gsiId, int gsfId){
        width=w;
        height=h;
        gsi=gsiId;
	    gsf=gsfId;
	    leoi=gsi/2;
	    leof=gsf/2;
    }
    public void paintComponent(Graphics g){
       
        Random r=new Random();
        

        Graphics2D g2d=(Graphics2D)g;
        g2d.setStroke(new BasicStroke(7));
        //Image img = Toolkit.getDefaultToolkit().getImage("Capture.jpg");
        float alpha = 0.9f;
        Color color = new Color(1, 0, 0, alpha); //Red 
        g2d.setPaint(color);
        g2d.setColor(new Color(r.nextFloat(),r.nextFloat()/2f+0.5f,r.nextFloat()/2f+0.5f));
       // g2d.setXORMode(Color.black);
        //g2d.drawImage(img, 0, 0, null);

        if(leoi==leof){
            int st1x=55+(70*gsi);
            int st2x=90+(140*leoi);
            Line2D.Double line1=new Line2D.Double(st1x,300,st2x,230);
            g2d.drawString("msg", (st1x+st2x)/2, (300+230)/2);
            
            g2d.draw(line1);

            int st3x=55+(70*gsf);
            int st4x=90+(140*leof);
            Line2D.Double line2=new Line2D.Double(st4x,230,st3x,300);
            g2d.drawString("msg", (st4x+st3x)/2, (300+230)/2);
            g2d.draw(line2);
        } else if(Math.abs(leof-leoi)==1){
            int st1x=55+(70*gsi);
            int st2x=90+(140*leoi);
            Line2D.Double line1=new Line2D.Double(st1x,300,st2x,230);
            g2d.drawString("msg", (st1x+st2x)/2, (300+230)/2);
            g2d.draw(line1);

            int st3x=115+(140*leoi);
            if(leoi>leof){
                st3x=115+(140*leof);
            }
            int st4x=st3x+90;
            Line2D.Double line2=new Line2D.Double(st3x,215,st4x,215);
            g2d.drawString("msg", (st3x+st4x)/2, (215+215)/2);
            g2d.draw(line2);

            int st5x=55+(70*gsf);
            int st6x=90+(140*leof);
            Line2D.Double line3=new Line2D.Double(st6x,230,st5x,300);
            g2d.drawString("msg", (st5x+st6x)/2, (300+230)/2);
            g2d.draw(line3);
        } else{
            int st1x=55+(70*gsi);
            int st2x=90+(140*leoi);
            Line2D.Double line1=new Line2D.Double(st1x,300,st2x,230);
            g2d.drawString("msg", (st1x+st2x)/2, (300+230)/2);
            g2d.draw(line1);

            Line2D.Double line2=new Line2D.Double(st2x,200,370,130);
            g2d.drawString("msg", (st1x+370)/2, (200+130)/2);
            g2d.draw(line2);

            int st5x=90+(140*leof);
            Line2D.Double line3=new Line2D.Double(370,130,st5x,200);
            g2d.drawString("msg", (370+st5x)/2, (200+130)/2);
            g2d.draw(line3);

            int st6x=55+(70*gsf);
            Line2D.Double line4=new Line2D.Double(st6x,300,st5x,230);
            g2d.drawString("msg", (st6x+st5x)/2, (300+230)/2);
            g2d.draw(line4);
        }
        
    }
}

public class Driver extends JFrame {
    
    
    public static void main(String args[]){
        
        JFrame f= new JFrame();
        
        f.getContentPane().setBackground(new Color(240,255,255));
        
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
        f.setTitle("Constellation");
        JLabel title=new JLabel("InterSatellite Link(ISL)",JLabel.CENTER);
        title.setBounds(270,40,200,50); 
        title.setFont (title.getFont ().deriveFont (16.0f));

     

        //title.setBorder(BorderFactory.createLineBorder(Color.black));
        
        JLabel GsO=new JLabel("GSO",JLabel.CENTER);
        GsO.setBounds(345,100,50,30); 
        GsO.setBorder(BorderFactory.createLineBorder(Color.black));
        JLabel GS0=new JLabel("GS0", JLabel.CENTER);
        GS0.setBounds(30,300, 50,30);
        GS0.setBorder(BorderFactory.createLineBorder(Color.black));
       
        JLabel GS1=new JLabel("GS1", JLabel.CENTER);
        GS1.setBounds(100,300, 50,30); 
        GS1.setBorder(BorderFactory.createLineBorder(Color.black));
        
        JLabel GS2=new JLabel("GS2", JLabel.CENTER);
        GS2.setBounds(170,300, 50,30); 
        GS2.setBorder(BorderFactory.createLineBorder(Color.black));
        
        JLabel GS3=new JLabel("GS3", JLabel.CENTER);
        GS3.setBounds(240,300, 50,30); 
        GS3.setBorder(BorderFactory.createLineBorder(Color.black));
        
        JLabel GS4=new JLabel("GS4", JLabel.CENTER);
        GS4.setBounds(310,300, 50,30); 
        GS4.setBorder(BorderFactory.createLineBorder(Color.black));
       
        JLabel GS5=new JLabel("GS5", JLabel.CENTER);
        GS5.setBounds(380,300, 50,30); 
        GS5.setBorder(BorderFactory.createLineBorder(Color.black));
       
        JLabel GS6=new JLabel("GS6", JLabel.CENTER);
        GS6.setBounds(450,300, 50,30); 
        GS6.setBorder(BorderFactory.createLineBorder(Color.black));
        
        JLabel GS7=new JLabel("GS7", JLabel.CENTER);
        GS7.setBounds(520,300, 50,30); 
        GS7.setBorder(BorderFactory.createLineBorder(Color.black));
       
        JLabel GS8=new JLabel("GS8", JLabel.CENTER);
        GS8.setBounds(590,300,50,30); 
        GS8.setBorder(BorderFactory.createLineBorder(Color.black));
        
        JLabel GS9=new JLabel("GS9", JLabel.CENTER);
        GS9.setBounds(660,300, 50,30); 
        GS9.setBorder(BorderFactory.createLineBorder(Color.black));
        
        JLabel LEO0=new JLabel("LEO0", JLabel.CENTER);
        LEO0.setBounds(65,200,50,30); 
        LEO0.setBorder(BorderFactory.createLineBorder(Color.black));
        
        JLabel LEO1=new JLabel("LEO1", JLabel.CENTER);
        LEO1.setBounds(205,200,50,30); 
        LEO1.setBorder(BorderFactory.createLineBorder(Color.black));
        
        JLabel LEO2=new JLabel("LEO2", JLabel.CENTER);
        LEO2.setBounds(345,200,50,30); 
        LEO2.setBorder(BorderFactory.createLineBorder(Color.black));
        
        JLabel LEO3=new JLabel("LEO3", JLabel.CENTER);
        LEO3.setBounds(485,200,50,30); 
        LEO3.setBorder(BorderFactory.createLineBorder(Color.black));
        
        JLabel LEO4=new JLabel("LEO4", JLabel.CENTER);
        LEO4.setBounds(625,200,50,30); 
        LEO4.setBorder(BorderFactory.createLineBorder(Color.black));
       
        f.add(GS0);f.add(GS1);f.add(GS2);f.add(GS3);f.add(GS4);f.add(GS5);f.add(GS6);f.add(GS7);f.add(GS8);f.add(GS9);
        f.add(LEO0);f.add(LEO1);f.add(LEO2);f.add(LEO3);f.add(LEO4);
        f.add(GsO);
        f.setSize(800,450);
        f.add(title);
        
    
        Scanner sc=new Scanner(System.in);
        int n=sc.nextInt();
        Satellite st=new Satellite();
     
        st.initialize();
    
        while(n!=0){  
            Constellation.gsiId=sc.nextInt();
            Constellation.gsfId=sc.nextInt();
        
           
            GroundStation gs=new GroundStation(Constellation.gsiId);
            Constellation.counter=0;
            Constellation.countergs=0;
            Thread th=new Thread(gs);
            th.start();
            try {
                Thread.sleep(100);
            } catch (Exception e) {
                System.out.println("error");
            }
            n--;
            GraphicsDemo gd= new GraphicsDemo(f);
            
            //if(n==0)System.exit(0);
        }
    }
}

import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.math.*;


class G                         // GLOBALS
{
   static int n_rows;
   static int n_cols;
   static int n_bricks;
   static int brick_length;
   static int brick_width;
   static Color brick_color;

   static int ball_radius;
   static Color ball_color;

   static int top_free;
   static int bottom_free;
   
   static int border_thickness;
   static int slide_length;
   static int slide_width;
   static int slide_x;
   static int slide_y;
   static Color slide_color;
   static int max_slide_length;
   static int min_slide_length;

   static int border_length;
   static int border_width;
   static Color border_color;

   static int window_x;
   static int window_y;

   static Graphics g;
}

         
class dummy 
{
   static choice1 ch;
   static Brick1 b;

   static Graphics g;
   static int restart = 0;
   static int start = 0;
   static int key_pressed = 0;
   static int score = 0;
   static String output;
   static int xx, yy;
   static int brick_num;

   static Thread tt;
   static int pause=0;
   static int stop_anim=0;
   static int shoot=0;
   static int bullets=0;
}
         

class ball
{
        static int x,y;
        static int x1,y1,x2,y2;
        static int side;
        static double tant;
        static int flagx,flagy;         // 1 -> increase   0 -> decrease

        public static void swap()
        {
           x1 = x2;
           y1 = y2;
           x2 = x;
           y2 = y;
        }

        public static void calt()
        {
             if( y1 - y2 == 0 || x1 - x2 == 0)
                tant = 1;
             else if(side  == 1 || side == 3)
                tant = (double)Math.abs(x1-x2)/(double)Math.abs(y1-y2);
             else
                tant = (double)Math.abs(y1-y2)/(double)Math.abs(x1-x2);
        }

        public static void xpos(int z)
        {    double t;
             y = z;
             if(flagx == 0)
                t = x2 - (tant * Math.abs((y2-y)));
             else
                t = x2 + (tant * Math.abs((y2-y)));
             x = (int) t;
             
        }

        public static void ypos(int z)
        {    double t;
             x = z;
             if(flagy == 0)
                t = y2 - (tant * Math.abs(x2-x));
             else
                t = y2 + (tant * Math.abs(x2-x));
             y = (int) t;
        }
}

// ******************************************************************



class brick
{             
   static int count = 0;
   static int br[][];


   // br[][0]   -       enabled/disabled (1/0)
   // br[][1]   -       x co. ord.
   // br[][2]   -       y co. ord.
   // br[][3]   -       value
   // br[][4]   -       special ( slide length )  (1 -> inc., -1 -> dec.)
   // br[][5]   -       special ( drop arrows )   (1 -> set) 
   // br[][6]   -       special ( shoot power )   (1 -> set)

   public static synchronized void initialise(Graphics gg)
   {
      int n=1, non_special=G.n_bricks;

      br = new int[G.n_bricks+1][7];
   
      count = 0;
      gg.setColor(G.brick_color);

      try { Thread.sleep(1000); }
      catch(Exception e) {}


      for(int i=1; i<=G.n_rows; i++)
        for(int j=1; j<=G.n_cols; j++)
        {
           br[n][0] = 1;                  //enabled
           br[n][1] = (j-1)*G.brick_length + G.window_x;  //x co-ord.
           br[n][2] = (G.n_rows-i)*G.brick_width
                        + G.top_free + G.window_y;
           br[n][3] = 100;              //value
           br[n][4] = 0;                //special  (slide length)
           br[n][5] = 0;                //special  (reverse keys)
           br[n][6] = 0;                //special  (shoot power)

           gg.setColor(G.brick_color);
           (dummy.g).fill3DRect(br[n][1]+1, br[n][2]+1, G.brick_length-2,
                                G.brick_width-2, true);
           n++;
        }

        refresh(dummy.g);

      //initialise special bricks

      //slide length (inc.)
        for(int i=1; i<=G.n_rows; i++)
        {  
                int num;
                num = (int) ( (Math.random()*100) % G.n_bricks );
                if( br[num][4] == 0 )
                        br[num][4] = 1;
                else i--;
        }
        non_special -= G.n_rows;

      //slide length (dec.)
        for(int i=1; i<=G.n_rows; i++)
        {  
                int num;
                num = (int) ( (Math.random()*100) % G.n_bricks );
                if( br[num][4] == 0 )
                        br[num][4] = -1;
                else i--;
        }
        non_special -= G.n_rows;

        int limit;
        if( (non_special - G.n_rows) < th.level+G.n_rows )
                limit = non_special - G.n_rows;
        else    limit = th.level + G.n_rows;

      //drop arrows (set)
        for(int i=1; i<=limit; i++)
        {  
                int num;
                num = (int) ( (Math.random()*100) % G.n_bricks );
                if( br[num][5] == 0 && br[num][4] == 0 )
                        br[num][5] = 1;
                else i--;
        }

      //shoot power (set)
        for(int i=1; i<=G.n_rows; i++)
        {  
                int num;
                num = (int) ( (Math.random()*100) % G.n_bricks );
                if( br[num][6] == 0 && br[num][5] == 0 && br[num][4] == 0 )
                        br[num][6] = 1;
                else i--;
        }

   }

    public static void refresh(Graphics gg,int i)
    {
        int n,e,w,s;
        int ne,nw,se,sw;
        int width = G.brick_length;
        int height = G.brick_width;

        if(i >= 1 && i <= G.n_bricks)
        {
            n = i + G.n_cols;
            e = i + 1;
            w = i - 1;
            s = i - G.n_cols;
            ne = n+1;
            nw = n-1;
            se = s+1;
            sw = s-1;
            gg.setColor(G.brick_color);

            if(n>=1 && n<=G.n_bricks && br[n][0]==1)
              gg.fill3DRect(br[n][1]+1,br[n][2]+1,width-2,height-2,true);
            if(e>=1 && e<=G.n_bricks && br[e][0]==1)
              gg.fill3DRect(br[e][1]+1,br[e][2]+1,width-2,height-2,true);
            if(w>=1 && w<=G.n_bricks && br[w][0]==1)
              gg.fill3DRect(br[w][1]+1,br[w][2]+1,width-2,height-2,true);
            if(s>=1 && s<=G.n_bricks && br[s][0]==1)
              gg.fill3DRect(br[s][1]+1,br[s][2]+1,width-2,height-2,true);

            if(ne>=1 && ne<=G.n_bricks && br[ne][0]==1)
              gg.fill3DRect(br[ne][1]+1,br[ne][2]+1,width-2,height-2,true);
            if(nw>=1 && nw<=G.n_bricks && br[nw][0]==1)
              gg.fill3DRect(br[nw][1]+1,br[nw][2]+1,width-2,height-2,true);
            if(s>=1 && s<=G.n_bricks && br[s][0]==1)
              gg.fill3DRect(br[s][1]+1,br[s][2]+1,width-2,height-2,true);
            if(se>=1 && se<=G.n_bricks && br[se][0]==1)
              gg.fill3DRect(br[se][1]+1,br[se][2]+1,width-2,height-2,true);
         }
         else
         {
           for(i=1; i<=G.n_bricks; i++)
           {    if(br[i][0] == 0)
                        continue;
                gg.fill3DRect(br[i][1]+1, br[i][2]+1, width-2, height-2, true);
           }
           return;
         }
    }


        public static synchronized void refresh(Graphics gg)
        {
           gg.setColor(Color.black);
           gg.clearRect(G.window_x, G.window_y+G.top_free,
                                G.border_length-2*G.border_thickness,
                                G.n_rows * G.brick_width );
           gg.setColor(Color.black);
           gg.clearRect(G.window_x, G.window_y,
                                G.border_length-2*G.border_thickness,
                                G.top_free );
           gg.setColor(Color.black);
           gg.clearRect(G.window_x, G.window_y+G.top_free+ (G.n_rows * G.brick_width),
                                G.border_length-2*G.border_thickness,
                                G.bottom_free );
           

           gg.setColor(G.brick_color);

           for(int i=1; i<=G.n_bricks; i++)
           {    if(br[i][0] == 0)
                        continue;
                gg.setColor(G.brick_color);
                gg.fill3DRect(br[i][1]+1, br[i][2]+1, G.brick_length-2, G.brick_width-2, true);
           }

           return;
        }


        public static void removebrick(int i)
        {
                if( br[i][4] == 1 || br[i][4] == -1 ||
                         br[i][5] == 1 || br[i][6] == 1 )
                {  dummy.brick_num = i;
                   new Special().start();
                }

                dummy.xx = br[i][1];
                dummy.yy = br[i][2];
                new Effect().start();   
                br[i][0]=0;
                count++;

                dummy.score += br[i][3];

                th.update_score();

                refresh(dummy.g, i);

                (dummy.g).setColor(G.border_color);
                (dummy.g).drawRect(G.window_x-2, G.window_y-2,
                                G.border_length-2*G.border_thickness+4,
                                G.border_width-2*G.border_thickness+4);
                (dummy.g).drawRect(G.window_x-3, G.window_y-3,
                                G.border_length-2*G.border_thickness+6,
                                G.border_width-2*G.border_thickness+6);

                (dummy.g).setColor(Color.black);
                return;
        }


        public static void check()
        {
          int n, start=0, end=0, y;
          int ball_e = ball.x+2*G.ball_radius, ball_w = ball.x;

                if( ball.flagy == 0 )
                {       y = ball.y;
                  for(int i=1; i<=G.n_rows; i++)
                  {     n = i*G.n_cols;
                        if( y <= br[n][2]+G.brick_width &&
                                ball.y > br[n][2] )
                        { start = (i-1)*G.n_cols+1;  end = n; break; }
                  }
                }
                else
                {       y = ball.y+2*G.ball_radius;
                  for(int i=G.n_rows; i>=1; i--)
                  {     n = i*G.n_cols;
                        if( y > br[n][2] &&
                                y <= br[n][2]+G.brick_width )
                        { start = (i-1)*G.n_cols+1;  end = n; break; }
                  }
                }


                for(int i=start; i<=end; i++)
                {       if( brick.br[i][0] == 0 )
                                continue;
                                                        
                        if( ball.x+G.ball_radius > brick.br[i][1] &&
                                ball.x+G.ball_radius <= brick.br[i][1]+G.brick_length )
                        {
                                ball.side = (ball.flagy==0)?3:1;
                                ball.swap(); ball.calt();
                                if(ball.flagy==1) ball.flagy=0;
                                else ball.flagy = 1;
                                removebrick(i);
                                break;
                        }
                        if( ball.flagx == 1 &&
                                ball_e > br[i][1] && ball_e <= br[i][1]+G.brick_length )
                        {
                                ball.side = 4; ball.swap(); ball.calt(); ball.flagx=0;
                                removebrick(i);
                                break;
                        }
                        if( ball.flagx == 0 &&
                                ball_w <= br[i][1]+G.brick_length && ball_w > br[i][1] )
                        {
                                ball.side = 2; ball.swap(); ball.calt(); ball.flagx=1;
                                removebrick(i);
                                break;
                        }

                }
        }//end of check


} // end of class

// ******************************************************************


    
class th implements Runnable
{
  static int level=1;
  static int life = 5;
  static boolean hit=true;
  int a1,a2,a3;


  public void start()
  {
      run();
  }

  public void run()
  {
        (dummy.ch).addKeyListener(dummy.b);
        (dummy.ch).requestFocus();

      while(true)
      {
        init();
        paint(dummy.g);
        while( dummy.restart == 0  && (dummy.ch).settings_flag == 0 );
        dummy.restart = 0;

        if( (dummy.ch).settings_flag == 1 )
        {
                try { Thread.sleep(100); }
                catch(Exception e) {}

                (dummy.g).setColor(Color.black);
                (dummy.g).clearRect(0,0,800, 500);

                (dummy.g).setFont(new Font("TimesRoman", Font.BOLD, 32));
                (dummy.g).setColor(Color.red);
                (dummy.g).drawString("GAME ABORTED", (800-200)/2, 600/2 );


                try     { Thread.sleep(2000);
                        }
                catch(Exception e) {}

                (dummy.g).setColor(Color.black);
                (dummy.g).clearRect(0,0,800, 500);

                (dummy.ch).exit.hide();
                (dummy.ch).restart.hide();
                (dummy.ch).start.hide();
                (dummy.ch).settings.hide();
                (dummy.ch).set_default_settings();

                if( (dummy.ch).settings_flag == 1 )
                {
                        (dummy.ch).display_settings();
                        (dummy.ch).start_flag = 0;
                        while( (dummy.ch).start_flag == 0 );
                        (dummy.ch).store_settings();
                        (dummy.ch).remove_settings();
                }

                (dummy.ch).exit.show();
                (dummy.ch).restart.show();
                (dummy.ch).settings.show();
                (dummy.ch).exit.resize(75, 20);
                (dummy.ch).exit.move(250, 485);
                (dummy.ch).restart.resize(75, 20);
                (dummy.ch).restart.move(350, 485);
                (dummy.ch).settings.resize(75, 20);
                (dummy.ch).settings.move(450, 485);
                
                (dummy.ch).show();
                (dummy.ch).settings_flag = 0;
        }
        G.slide_length  = G.brick_length + 20;
      }
  }

  public void init()
  {
        dummy.restart = 0;
        dummy.pause = 0;
        dummy.stop_anim = 0;
        dummy.key_pressed = 0;
        dummy.shoot = 0;
        dummy.bullets = 0;
        dummy.score = 0;
               
        (dummy.ch).requestFocus();

        life = 5;
        level = 1;
        initial(450, (int)G.border_width/2, G.slide_x + 10, G.slide_y - (2*G.ball_radius));
   }

   public void ival()
   {
        for(int i=1;i<=G.n_bricks;i++)
        {  if(ball.x >= brick.br[i][1] && ball.x <= brick.br[i][1]+G.brick_length
                && ball.y >= brick.br[i][2] && ball.y <= brick.br[i][2]+G.brick_width)
             {  a3 = i; return;
             }
        }
   }

  public void paint(Graphics g)
  {
        (dummy.g).translate( (int) (800 - G.border_length)/2,
                        (int) (500 - G.border_width - 60)/2 );


        while(dummy.start == 0)
        {  try { Thread.sleep(100); }
           catch(Exception e) {}
        }

        dummy.start = 1;

        (dummy.g).setColor(G.border_color);
        (dummy.g).fillRect(0,0,G.border_length, G.border_width);
        (dummy.g).setColor(Color.black);
        (dummy.g).fillRect(G.window_x, G.window_y,
                                G.border_length - (2*G.border_thickness),
                                G.border_width - (2*G.border_thickness));

        (dummy.g).setColor(G.brick_color);
        brick.initialise(dummy.g);
        brick.refresh(dummy.g);
        update_score();

        wait_keypress(dummy.g);

       //******************************************

       while(life != 0)
       {

               if( dummy.restart == 1  )
                {
                        try     { Thread.sleep(100);
                                }
                        catch(Exception e) {}

                        (dummy.g).setColor(Color.black);
                        (dummy.g).clearRect(G.window_x, G.window_y,
                                G.border_length-2*G.border_thickness,
                                G.border_width-2*G.border_thickness );

                        g.setFont(new Font("TimesRoman", Font.BOLD, 32));
                        g.setColor(Color.red);
                        g.drawString("RESTARTING", (G.border_length-190)/2,
                                                   G.border_width/2 );

                        try     { Thread.sleep(2000);
                                }
                        catch(Exception e) {}

                        (dummy.g).translate( (int) -(800 - G.border_length)/2,
                                        (int) -(500 - G.border_width - 60)/2 );

                        return;
                }

             
                if( (dummy.ch).settings_flag == 1 )
                {
                        (dummy.g).setColor(Color.black);
                        (dummy.g).clearRect(G.window_x, G.window_y,
                                G.border_length-2*G.border_thickness,
                                G.border_width-2*G.border_thickness );

                        (dummy.g).translate( (int) -(800 - G.border_length)/2,
                                        (int) -(500 - G.border_width - 60)/2 );

                        dummy.restart = 1;
                        return;
                }
             

          g.clearRect(ball.x, ball.y, 2*G.ball_radius+1, 2*G.ball_radius+1);

          ival();
          brick.refresh(g,a3);

          switch(ball.side)
          { case 1:  ball.y = ball.y  - 1; checkboundary(); ball.xpos(ball.y); break;
            case 2:  ball.x = ball.x + 1; checkboundary(); ball.ypos(ball.x); break;
            case 3:  ball.y = ball.y + 1; checkboundary(); ball.xpos(ball.y); break;
            case 4:  ball.x = ball.x  - 1; checkboundary(); ball.ypos(ball.x); break;
          }    

                                                 // brick area
          if( ball.y <= (G.window_y + G.top_free + G.n_rows*G.brick_width)
                && (ball.y + 2*G.ball_radius) >= (G.window_y + G.top_free) )
          {
                brick.check();
                ival();
                brick.refresh(g,a3);
          }
          
          if( ball.flagy == 1 &&
                ball.y < G.window_y+G.top_free+(G.n_rows*G.brick_width)+30)
          {     brick.refresh(g, 0);
          }
          else if( ball.flagy == 0 &&
                ball.y+2*G.ball_radius > brick.br[G.n_bricks][2]-30 )
          {     brick.refresh(g, 0);
          }
          
                                              // slide area

          if( (ball.y + 2*G.ball_radius) >= G.slide_y ) 
          {
             hit=checkslide();
             if(!hit) continue;  
          }

          g.setColor(G.ball_color);
          g.fillOval(ball.x, ball.y, 2*G.ball_radius, 2*G.ball_radius);
          refresh_slide(dummy.g);
          g.setColor(G.border_color);
          g.drawRect(G.window_x-1, G.window_y-1,
                                G.border_length-2*G.border_thickness+2,
                                G.border_width-2*G.border_thickness+2);
          g.setColor(G.brick_color);

          try{  if( level >= 4 )
                        Thread.sleep(2);
                else
                        Thread.sleep(10 - (level*2));
             }
          catch(Exception e) {}

                                        // all bricks hit. Next level
          if(brick.count >= G.n_bricks)
            {
                dummy.stop_anim = 1;

                (dummy.g).setColor(Color.black);
                (dummy.g).clearRect(G.window_x, G.window_y,
                                G.border_length - (2*G.border_thickness),
                                G.border_width - (2*G.border_thickness));

                 brick.initialise(dummy.g);
                 brick.count = 0;
                 level ++;
                 life ++;
                 update_score();
                 update_lives();
                 disp_level();

                 G.slide_x = (int) G.border_length/2 - (int)G.slide_length/2;                 
                 initial(450,300,G.slide_x + 10, G.slide_y - (2*G.ball_radius));

                 ball.side = 1; 
                 ball.swap();
                 ball.calt();    ball.flagy = 0;

                 dummy.stop_anim = 0;

                 wait_keypress(dummy.g);
            }

      } //end of while

      terminate(dummy.g);
      (dummy.g).translate( (int) -(800 - G.border_length)/2,
                        (int) -(500 - G.border_width - 60)/2 );

      return;
   }


   public static synchronized void refresh_slide(Graphics g)
   {
          g.setColor(Color.black);
          g.clearRect(G.window_x, G.slide_y,
                                G.n_cols*G.brick_length, G.slide_width);
          g.setColor(G.slide_color);
          g.fill3DRect(G.slide_x, G.slide_y,
                                  G.slide_length, G.slide_width,true);
   }     

   public static void wait_keypress(Graphics g)
   {
        g.setColor(G.ball_color);
        g.fillOval(ball.x, ball.y, 2*G.ball_radius, 2*G.ball_radius);
        g.setColor(G.slide_color);
        g.fill3DRect(G.slide_x, G.slide_y,
                                G.slide_length, G.slide_width,true);

        dummy.key_pressed = 0;
        while( dummy.key_pressed == 0 && (dummy.ch).settings_flag == 0
                                && dummy.restart == 0 );
        dummy.key_pressed = 0;
   }     


   public static void terminate(Graphics g)
   {
        dummy.stop_anim = 1;
        update_score();

        try{      Thread.sleep(1000);
           }
        catch(Exception e){}

        g.clearRect(G.window_x, G.window_y,
                             G.n_cols*G.brick_length,
                             G.border_width - (2*G.border_thickness));
                
        g.setColor(new Color(75,50,200));
        g.setFont(new Font("Helvetica",Font.BOLD,24));
        g.drawString("GAME OVER", (int) (Math.abs(G.border_length-10*13)/2),
                                  (int)G.border_width/2 - 30);
        g.setFont(new Font("Helvetica",Font.BOLD,18));
        g.drawString("Final Score = "+dummy.score,
                                (int) (Math.abs(G.border_length-20*7)/2),
                                (int)G.border_width/2 );

        dummy.stop_anim = 0;

        return;
   } 


   public static void update_score()
   {
         int len;

         brick.refresh(dummy.g);

         dummy.output = "SCORE = "+dummy.score+"      LIVES = "+th.life+"     BULLETS = "+dummy.bullets;
         len = (dummy.output).length() * 5;

         (dummy.g).clearRect( 0, G.border_width+1,
                                   800, 40);

         (dummy.g).setFont(new Font("TimesRoman", Font.PLAIN, 16));
         (dummy.g).setColor(Color.red);
         (dummy.g).drawString(dummy.output,
                                (int) (Math.abs(G.border_length-38*7)/2), G.border_width + 30);
         (dummy.g).drawString("PRESS <ESC> TO PAUSE",
                                (int) (Math.abs(G.border_length-21*7)/2), G.border_width + 60);

         (dummy.g).setColor(Color.black);
         //(dummy.g).clearRect(ball.x-1, ball.y-1, 2*G.ball_radius+2, 2*G.ball_radius+2);
   }


   public synchronized static void update_lives()
   {
        try { Thread.sleep(500); }
        catch(Exception e) {}

        (dummy.g).setColor(Color.red);
        (dummy.g).drawString("  LIVES LEFT :   " + th.life,
                              (int) (Math.abs(G.border_length-18*4)/2),
                               brick.br[1][2]+3*G.brick_width );

        try { Thread.sleep(2000); }
        catch(Exception e) {}

        (dummy.g).setColor(Color.black);
        (dummy.g).drawString("  LIVES LEFT :   " + th.life,
                              (int) (Math.abs(G.border_length-18*4)/2),
                               brick.br[1][2]+3*G.brick_width );
   }


   public synchronized static void disp_level()
   {
        try { Thread.sleep(500); }
        catch(Exception e) {}

        (dummy.g).setColor(Color.red);
        (dummy.g).drawString("  LEVEL  " + th.level,
                              (int) (Math.abs(G.border_length-11*4)/2),
                               brick.br[1][2]+3*G.brick_width );

        try { Thread.sleep(2000); }
        catch(Exception e) {}

        (dummy.g).setColor(Color.black);
        (dummy.g).drawString("  LEVEL  " + th.level,
                              (int) (Math.abs(G.border_length-11*4)/2),
                               brick.br[1][2]+3*G.brick_width );

   }


   public boolean checkslide()
   {
        int len;

        if( G.slide_length > 40 )
                len = 20;
        else    len = 10;

      if( ( (ball.x + G.ball_radius) >= G.slide_x ) &&
          ( (ball.x + G.ball_radius) <= (G.slide_x + G.slide_length) ) )
      { 
            ball.side = 1;   
            ball.swap();
            ball.calt();
            ball.flagy=0;

           if( (ball.x + G.ball_radius) < (G.slide_x + len ) )
           {
                 //ball.flagx = 0;
                 //ball.tant = Math.abs(ball.tant+0.5);   //reduce/increase by 30 degrees
                
                if( ball.flagx == 0 )
                        ball.tant = Math.abs(ball.tant+0.2);
                else
                        ball.tant = Math.abs(ball.tant-0.5);
                ball.flagx = 0;
           }
           else
           if( (ball.x + G.ball_radius) >= (G.slide_x + G.slide_length - len) )
           {
                 //ball.flagx = 1;
                 //ball.tant = Math.abs(ball.tant-0.5);   //reduce/increase by 30 degrees

                if( ball.flagx == 1 )
                        ball.tant = Math.abs(ball.tant+0.2);
                else
                        ball.tant = Math.abs(ball.tant-0.5);
                ball.flagx = 1;
           }
           return true;
      }
      else
      {    --life;
           dummy.stop_anim = 1;

           (dummy.g).setColor(Color.black);

           (dummy.g).clearRect(G.window_x,
                                G.window_y+G.top_free+(G.n_rows*G.brick_width),
                                G.n_cols*G.brick_length,
                               G.bottom_free+G.slide_width);

           G.slide_x = (int) G.border_length/2 - (int)G.slide_length/2;
           initial(450,300,G.slide_x + 10, G.slide_y - (2*G.ball_radius));

           ball.side = 1; 
           ball.swap();
           ball.calt();    ball.flagy = 0;
            
           update_score();

           if(life!=0)
                update_lives();

           dummy.stop_anim = 0;

           return false;
         }
   }


   public void checkboundary()       
   {
      if(ball.x <= G.window_x)
      { ball.side = 2;  ball.x = G.window_x;
        ball.swap();    ball.calt();  ball.flagx = 1;
      }
      else if( (ball.x + (2*G.ball_radius)) >=
                (G.border_length - G.border_thickness))
      { ball.side = 4;  ball.x = (G.border_length - G.border_thickness - (2*G.ball_radius));
        ball.swap();    ball.calt(); ball.flagx = 0;
      }
      else if(ball.y <= G.window_y)
      { ball.side = 3;  ball.y = G.window_y;
        ball.swap();    ball.calt(); ball.flagy = 1;
      }
      else if( (ball.y + (2*G.ball_radius)) >=
                (G.border_width - G.border_thickness) )
      { ball.side = 1;  ball.y = (G.border_width - G.border_thickness - (2*G.ball_radius));
        ball.swap();    ball.calt(); ball.flagy = 0;
      }         
  }

   public static void initial(int x1, int y1, int x2, int y2)
   {
     ball.side = 1;
     ball.x = x2;       ball.y = y2;
     ball.x1 = x1;      ball.y1 = y1;
     ball.x2 = x2;      ball.y2 = y2;
     ball.calt();
     ball.flagx = 0;  ball.flagy = 0;
   }

}

// ******************************************************************



class choice1 extends Frame implements ItemListener 
{
        Choice n_rows, n_cols, brick_width, brick_length,
                ball_radius, border_width;
        Choice ball_rc, ball_gc, ball_bc,
                brick_rc, brick_gc, brick_bc,
                border_rc, border_gc, border_bc,
                slide_rc, slide_gc, slide_bc;

        Label l_n_rows, l_n_cols, l_brick_width, l_brick_length,
                l_ball_radius, l_border_width,
                l_ball_color, l_brick_color, l_border_color, l_slide_color,
                l_red, l_green, l_blue;   

        Button start, settings, exit, restart;
        int start_flag, settings_flag, exit_flag, restart_flag;


        public void init ()
        {
                resize(800, 520);
                setLayout(null);

                n_rows = new Choice();
                n_cols = new Choice();
                brick_width = new Choice();
                brick_length = new Choice();
                ball_radius = new Choice();
                border_width = new Choice();

                ball_rc = new Choice();
                ball_gc = new Choice();
                ball_bc = new Choice();

                brick_rc = new Choice();
                brick_gc = new Choice();
                brick_bc = new Choice();

                border_rc = new Choice();
                border_gc = new Choice();
                border_bc = new Choice();

                slide_rc = new Choice();
                slide_gc = new Choice();
                slide_bc = new Choice();

                l_red = new Label("R");
                l_green = new Label("G");
                l_blue = new Label("B");

                l_n_rows        = new Label("NO. ROWS     :");
                l_n_cols        = new Label("NO. COLS     :");
                l_brick_width   = new Label("BRICK WIDTH  :");
                l_brick_length  = new Label("BRICK LENGTH :");
                l_ball_radius   = new Label("BALL RADIUS  :");
                l_border_width  = new Label("BORDER WIDTH :");
                l_ball_color    = new Label("BALL COLOR   :");
                l_brick_color   = new Label("BRICK COLOR  :");
                l_border_color  = new Label("BORDER COLOR :");
                l_slide_color   = new Label("SLIDE COLOR  :");

                l_n_rows.setForeground(Color.green);
                l_n_cols.setForeground(Color.green);
                l_brick_width.setForeground(Color.green);
                l_brick_length.setForeground(Color.green);
                l_ball_radius.setForeground(Color.green);
                l_border_width.setForeground(Color.green);
                l_ball_color.setForeground(Color.green);
                l_brick_color.setForeground(Color.green);
                l_border_color.setForeground(Color.green);
                l_slide_color.setForeground(Color.green);

                l_red.setForeground(Color.green);
                l_green.setForeground(Color.green);
                l_blue.setForeground(Color.green);


                for(int i=3; i<= 5; i++)
                {       n_rows.add(String.valueOf(i));  }

                for(int i=6; i<= 12; i+=3)
                {       n_cols.add(String.valueOf(i));  }

                for(int i=15; i<= 25; i+=5)
                {       brick_width.add(String.valueOf(i));  }

                for(int i=40; i<= 55; i+=5)
                {       brick_length.add(String.valueOf(i));  }

                for(int i=4; i<= 8; i+=2)
                {       ball_radius.add(String.valueOf(i));  }

                for(int i=5; i<= 20; i+=5)
                {       border_width.add(String.valueOf(i));  }

                for (int i = 25; i <= 250; i+=25)
                {
                        ball_rc.add (String.valueOf(i));
                        ball_gc.add (String.valueOf(i));
                        ball_bc.add (String.valueOf(i));

                        brick_rc.add (String.valueOf(i));
                        brick_gc.add (String.valueOf(i));
                        brick_bc.add (String.valueOf(i));

                        border_rc.add (String.valueOf(i));
                        border_gc.add (String.valueOf(i));
                        border_bc.add (String.valueOf(i));

                        slide_rc.add (String.valueOf(i));
                        slide_gc.add (String.valueOf(i));
                        slide_bc.add (String.valueOf(i));
                }
  
                // *****************************************************
                show();
                setBackground(Color.black);
                setForeground( Color.black );
                (dummy.g) = this.getGraphics();

                Intro in = new Intro();
                in.intro();
                (dummy.g).setFont(new Font("TimesRoman", Font.PLAIN, 14));

                start = new Button("START");
                start.setForeground(Color.black);
                settings = new Button("SETTINGS");
                settings.setForeground(Color.black);
                exit = new Button("EXIT");
                exit.setForeground(Color.black);
                restart = new Button("RESTART");
                restart.setForeground(Color.black);

                add(start);   add(settings);  add(exit);  add(restart);
                start.resize(75, 20);
                settings.resize(75, 20);
                start.move(300, 350);
                settings.move(425, 350);


                add(n_rows); add(n_cols);
                add(brick_width); add(brick_length);
                add(ball_radius); add(border_width);
                add(ball_rc); add(ball_gc); add(ball_bc);
                add(brick_rc); add(brick_gc); add(brick_bc);
                add(border_rc); add(border_gc); add(border_bc);
                add(slide_rc); add(slide_gc); add(slide_bc);

                add(l_n_rows); add(l_n_cols);
                add(l_brick_width); add(l_brick_length);
                add(l_ball_radius); add(l_border_width);
                add(l_ball_color); add(l_brick_color);  
                add(l_border_color); add(l_slide_color);
                add(l_red); add(l_green); add(l_blue);
        }

        public void display_settings()
        {
                l_n_rows.show();
                l_n_cols.show();
                l_brick_width.show();
                l_brick_length.show();
                l_ball_radius.show();
                l_border_width.show();
                l_ball_color.show();
                l_brick_color.show();
                l_border_color.show();
                l_slide_color.show();

                l_red.show();
                l_green.show();
                l_blue.show();

                n_rows.show();
                n_cols.show();
                brick_width.show();
                brick_length.show();
                ball_radius.show();
                border_width.show();

                ball_rc.show();
                ball_gc.show();
                ball_bc.show();

                brick_rc.show();
                brick_gc.show();
                brick_bc.show();

                border_rc.show();
                border_gc.show();
                border_bc.show();

                slide_rc.show();
                slide_gc.show();
                slide_bc.show();


                set_combo_defaults();

                show();

                /********************************************/
                n_rows.addItemListener (this);
                n_cols.addItemListener (this);
                brick_width.addItemListener (this);
                brick_length.addItemListener (this);
                ball_radius.addItemListener (this);
                border_width.addItemListener (this);

                ball_rc.addItemListener (this);
                ball_gc.addItemListener (this);
                ball_bc.addItemListener (this);

                brick_rc.addItemListener (this);
                brick_gc.addItemListener (this);
                brick_bc.addItemListener (this);

                border_rc.addItemListener (this);
                border_gc.addItemListener (this);
                border_bc.addItemListener (this);

                slide_rc.addItemListener (this);
                slide_gc.addItemListener (this);
                slide_bc.addItemListener (this);

                /*********************************************/
                n_rows.resize(40, 20);
                n_cols.resize(40, 20);
                brick_width.resize(40, 20);
                brick_length.resize(40, 20);
                ball_radius.resize(40, 20);
                border_width.resize(40, 20);

                ball_rc.resize(50, 20);
                ball_gc.resize(50, 20);
                ball_bc.resize(50, 20);

                brick_rc.resize(50, 20);
                brick_gc.resize(50, 20);
                brick_bc.resize(50, 20);

                border_rc.resize(50, 20);
                border_gc.resize(50, 20);
                border_bc.resize(50, 20);

                slide_rc.resize(50, 20);
                slide_gc.resize(50, 20);
                slide_bc.resize(50, 20);

                l_n_rows.resize(100, 20);
                l_n_cols.resize(100, 20);
                l_brick_width.resize(100, 20);
                l_brick_length.resize(100, 20);
                l_ball_radius.resize(100, 20);
                l_border_width.resize(100, 20);
                l_ball_color.resize(100, 20);
                l_brick_color.resize(100, 20);
                l_border_color.resize(100, 20);
                l_slide_color.resize(100, 20);

                l_red.resize(50, 20);
                l_green.resize(50, 20);
                l_blue.resize(50, 20);

                /**************************************/
                l_n_rows.move(120, 50);
                l_n_cols.move(120, 90);
                l_brick_width.move(120, 130);
                l_brick_length.move(120, 170);
                l_ball_radius.move(120, 210);
                l_border_width.move(120, 250);
                l_ball_color.move(120, 320);
                l_brick_color.move(120, 360);
                l_border_color.move(120, 400);
                l_slide_color.move(120, 440);

                l_red.move(250, 290);
                l_green.move(325, 290);
                l_blue.move(400, 290);

                n_rows.move(250, 50);
                n_cols.move(250, 90);
                brick_width.move(250, 130);
                brick_length.move(250, 170);
                ball_radius.move(250, 210);
                border_width.move(250, 250);

                ball_rc.move(250, 320);
                ball_gc.move(325, 320);
                ball_bc.move(400, 320);

                brick_rc.move(250, 360);
                brick_gc.move(325, 360);
                brick_bc.move(400, 360);

                border_rc.move(250, 400);
                border_gc.move(325, 400);
                border_bc.move(400, 400);

                slide_rc.move(250, 440);
                slide_gc.move(325, 440);
                slide_bc.move(400, 440);

                start.move(375, 480);
                start.show();

                paint(dummy.g);
                refresh_colors();
         }

        public void set_combo_defaults()
        {
                n_rows.select(2);
                n_cols.select(2);
                brick_length.select(2);
                brick_width.select(2);
                ball_radius.select(1);
                border_width.select(2);

                ball_rc.select(1);  ball_gc.select(6);  ball_bc.select(9);
                brick_rc.select(7);  brick_gc.select(1);  brick_bc.select(1);
                border_rc.select(3);  border_gc.select(1);  border_bc.select(1);
                slide_rc.select(1);  slide_gc.select(4);  slide_bc.select(8);

                return;
        }


        public void store_settings()
        {
                G.n_rows        = Integer.parseInt( n_rows.getSelectedItem() );
                G.n_cols        = Integer.parseInt( n_cols.getSelectedItem() );
                G.n_bricks      = G.n_rows * G.n_cols;
                G.brick_length  = Integer.parseInt( brick_length.getSelectedItem() );
                G.brick_width   = Integer.parseInt( brick_width.getSelectedItem() );
                
                G.ball_radius   = Integer.parseInt( ball_radius.getSelectedItem() );
                
                G.top_free      = 2*G.brick_width;
                if( G.n_rows < 4 )
                        G.bottom_free   = (G.n_rows+2) * G.brick_width + 50;
                else
                        G.bottom_free   = G.n_rows * G.brick_width + 50;

                G.border_thickness = Integer.parseInt( border_width.getSelectedItem() );

                G.slide_length  = G.brick_length + 20;
                G.slide_width   = 15;
                G.slide_x       = (int)(2*G.border_thickness + G.n_cols*G.brick_length)/2
                                        - (int)G.slide_length/2;
                G.slide_y       = G.border_thickness + G.top_free + G.bottom_free
                                        + G.n_rows*G.brick_width;
                G.max_slide_length = G.brick_length + 80;
                G.min_slide_length = G.brick_length - 20;
                

                G.border_length = (2*G.border_thickness) + (G.n_cols*G.brick_length);
                G.border_width  = G.top_free + G.bottom_free + G.slide_width
                                        + (G.n_rows*G.brick_width) + (2*G.border_thickness);
                
                G.window_x      = G.border_thickness;
                G.window_y      = G.border_thickness;

                int r, g, b;

                r = Integer.parseInt( ball_rc.getSelectedItem() );
                g = Integer.parseInt( ball_gc.getSelectedItem() );
                b = Integer.parseInt( ball_bc.getSelectedItem() );
                G.ball_color    = new Color(r, g, b);

                r = Integer.parseInt( brick_rc.getSelectedItem() );
                g = Integer.parseInt( brick_gc.getSelectedItem() );
                b = Integer.parseInt( brick_bc.getSelectedItem() );
                G.brick_color   = new Color(r, g, b);

                r = Integer.parseInt( border_rc.getSelectedItem() );
                g = Integer.parseInt( border_gc.getSelectedItem() );
                b = Integer.parseInt( border_bc.getSelectedItem() );
                G.border_color  = new Color(r, g, b);

                r = Integer.parseInt( slide_rc.getSelectedItem() );
                g = Integer.parseInt( slide_gc.getSelectedItem() );
                b = Integer.parseInt( slide_bc.getSelectedItem() );
                G.slide_color   = new Color(r, g, b);
        }

    
        public void set_default_settings()
        {
                G.n_rows = 4;
                G.n_cols = 10;
                G.n_bricks = G.n_rows * G.n_cols;
                G.brick_length = 50;
                G.brick_width = 20;
                G.brick_color = new Color(200, 100, 100);

                G.ball_radius = 6;
                G.ball_color = new Color(25, 175, 200);

                G.top_free = 2*G.brick_width;
                if( G.n_rows < 4 )
                        G.bottom_free   = (G.n_rows+2) * G.brick_width + 50;
                else
                        G.bottom_free   = G.n_rows * G.brick_width + 50;


                G.border_thickness = 20;

                G.slide_length = G.brick_length + 20;
                G.slide_width = 15;
                G.slide_x = (int)(2*G.border_thickness + G.n_cols*G.brick_length)/2
                                        - (int)G.slide_length/2;
                G.slide_y = G.border_thickness + G.top_free + G.bottom_free
                                + G.n_rows*G.brick_width;
                G.slide_color = new Color(25, 175, 225);
                G.max_slide_length = G.brick_length + 80;
                G.min_slide_length = G.brick_length - 20;


                G.border_length = (2*G.border_thickness) + (G.n_cols*G.brick_length);
                G.border_width = G.top_free + G.bottom_free + G.slide_width
                              + (G.n_rows*G.brick_width) + (2*G.border_thickness);
                G.border_color = new Color(125, 50, 50);

                G.window_x = G.border_thickness;
                G.window_y = G.border_thickness;
        }
     


        public void remove_settings()
        {
                (dummy.g).clearRect(0,0,800, 500);

                l_n_rows.hide();
                l_n_cols.hide();
                l_brick_width.hide();
                l_brick_length.hide();
                l_ball_radius.hide();
                l_border_width.hide();
                l_ball_color.hide();
                l_brick_color.hide();
                l_border_color.hide();
                l_slide_color.hide();

                l_red.hide();
                l_green.hide();
                l_blue.hide();

                n_rows.hide();
                n_cols.hide();
                brick_width.hide();
                brick_length.hide();
                ball_radius.hide();
                border_width.hide();

                ball_rc.hide();
                ball_gc.hide();
                ball_bc.hide();

                brick_rc.hide();
                brick_gc.hide();
                brick_bc.hide();

                border_rc.hide();
                border_gc.hide();
                border_bc.hide();

                slide_rc.hide();
                slide_gc.hide();
                slide_bc.hide();

                start.hide();
        }

        public void itemStateChanged(ItemEvent e)
        {
                refresh_colors();
                paint(dummy.g);
        }

        public void refresh_colors()
        {
                int r, g, b;

                r = Integer.parseInt( ball_rc.getSelectedItem() );
                g = Integer.parseInt( ball_gc.getSelectedItem() );
                b = Integer.parseInt( ball_bc.getSelectedItem() );
                (dummy.g).setColor(new Color(r, g, b));
                (dummy.g).fill3DRect(550, 320, 100, 20, true);

                r = Integer.parseInt( brick_rc.getSelectedItem() );
                g = Integer.parseInt( brick_gc.getSelectedItem() );
                b = Integer.parseInt( brick_bc.getSelectedItem() );
                (dummy.g).setColor(new Color(r, g, b));
                (dummy.g).fill3DRect(550, 360, 100, 20, true);

                r = Integer.parseInt( border_rc.getSelectedItem() );
                g = Integer.parseInt( border_gc.getSelectedItem() );
                b = Integer.parseInt( border_bc.getSelectedItem() );
                (dummy.g).setColor(new Color(r, g, b));
                (dummy.g).fill3DRect(550, 400, 100, 20, true);

                r = Integer.parseInt( slide_rc.getSelectedItem() );
                g = Integer.parseInt( slide_gc.getSelectedItem() );
                b = Integer.parseInt( slide_bc.getSelectedItem() );
                (dummy.g).setColor(new Color(r, g, b));
                (dummy.g).fill3DRect(550, 440, 100, 20, true);
        }


       public boolean action(Event e, Object o)
       {
             if( "START".equals(o) )
             {          start_flag = 1;
                        dummy.start = 1;
                        settings_flag = 0;
             }
             if( "SETTINGS".equals(o) )
             {          
                        settings_flag = 1;
                        dummy.stop_anim = 1;
             }

             if( "EXIT".equals(o) )
             {
                        (dummy.g).clearRect(0,0,800, 500);

                        this.dispose();
                        System.exit(0);
             }
             if( "RESTART".equals(o) )
             {          dummy.stop_anim = 1;
                        dummy.restart = 1;
                        
             }
             return true;
       }


       public void start_game()
       {
                init();

                start_flag = 0;
                settings_flag = 0;
                while( start_flag == 0 && settings_flag == 0 );

                (dummy.g).clearRect(0,0,800, 500);

                exit.hide();
                start.hide();
                restart.hide();
                settings.hide();

                set_default_settings();

                if( settings_flag == 1 )
                {
                        display_settings();
                        start_flag = 0;
                        while( start_flag == 0 );
                        store_settings();
                        remove_settings();
                }

                exit.show();
                restart.show();
                settings.show();

                exit.resize(75, 20);
                exit.move(250, 485);
                restart.resize(75, 20);
                restart.move(350, 485);
                settings.resize(75, 20);
                settings.move(450, 485);
                
                show();
       }

}

// ****************************************************************


class Intro
{   
  int w=18;            //width of intro bricks
  int h=8;             //height of intro bricks

  static int a[][] = { 
     { 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
     { 0,0,1,1,1,1,0,0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,1,1,0,0,0,0,0},
     { 0,0,1,1,1,1,1,0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,1,1,0,0,0,0,0},
     { 0,0,1,1,0,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,0,0,0,0},
     { 0,0,1,1,0,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,0,0,0,0},
     { 0,0,1,1,1,1,1,0,0,1,1,0,1,1,0,1,1,0,0,1,1,1,0,1,1,0,0,1,0,0},
     { 0,0,1,1,1,1,0,0,0,1,1,1,1,1,0,1,1,0,1,1,0,0,0,1,1,0,1,1,0,0},
     { 0,0,1,1,1,1,1,0,0,1,1,1,0,0,0,1,1,0,1,1,0,0,0,1,1,1,1,0,0,0},
     { 0,0,1,1,0,1,1,1,0,1,1,0,0,0,0,1,1,0,1,1,0,0,0,1,1,1,0,0,0,0},
     { 0,0,1,1,0,1,1,1,0,1,1,0,0,0,0,1,1,0,1,1,0,0,0,1,1,1,1,0,0,0},
     { 0,0,1,1,1,1,1,0,0,1,1,0,0,0,0,1,1,0,1,1,0,0,0,1,1,0,1,1,0,0},
     { 0,0,1,1,1,1,0,0,0,1,1,0,0,0,0,1,1,0,0,1,1,1,0,1,1,0,0,1,0,0},
     { 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
     { 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
     { 0,0,0,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
     { 0,0,1,1,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
     { 0,1,1,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
     { 0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
     { 0,1,1,0,0,0,0,0,0,0,0,1,1,0,1,0,1,1,0,0,0,0,0,1,0,0,1,1,1,0},
     { 0,1,1,0,0,1,1,1,0,0,1,1,1,1,1,0,1,1,1,0,0,0,1,1,0,1,1,0,1,1},
     { 0,1,1,0,0,0,1,1,0,1,1,0,0,1,1,0,1,1,0,1,0,1,0,1,0,1,1,0,1,0},
     { 0,1,1,0,0,0,1,1,0,1,1,0,0,0,1,0,1,1,0,1,1,1,0,1,0,1,1,1,0,0},
     { 0,1,1,0,0,0,1,1,0,1,1,0,0,1,1,0,1,1,0,0,1,0,0,1,0,1,1,0,0,1},
     { 0,0,1,1,1,1,1,0,0,0,1,1,1,1,1,0,1,1,0,0,0,0,0,1,0,1,1,1,1,1},
     { 0,0,0,1,1,1,0,0,0,0,0,1,1,0,1,0,1,1,0,0,0,0,0,1,0,0,1,1,1,0},
     { 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
     { 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}
   };


  public void intro()
  {
      String str1 = new String("        Developed by :");
      String str2 = new String(" ");
      String str3 = new String("         Ram Kumar.R  ");
      String str4 = new String("Sandeep.S.Raju , Shivachandran.M ");
      String str5 = new String(" ");
      String str6 = new String("     B.M.S.C.E., Bangalore ");

       (dummy.g).setColor( new Color(175, 0, 0) );
       (dummy.g).translate(125, 50);

       try{  Thread.sleep(1000);
          }
       catch(Exception e) {}

       for(int i=0;i<=26;i++)
         for(int j=0;j<=29;j++)
         {  (dummy.g).fill3DRect(j*w+1,i*h+1,w-2,h-2,true);
            //try {Thread.sleep(2); }
            //catch(Exception e) {}
         }

         int i = 0, j = 0, tot = 0, num = 0, b[][] = new int[27][30];

         for(i=0; i<=26; i++)
         for(j=0; j<=29; j++)
         {      if( a[i][j] == 0 )      num++;
                b[i][j] = a[i][j];
         }


         for(tot = 0;tot < num;)
         {
           i= (int) ( (Math.random()*100) % 27 );
           j= (int) ( (Math.random()*100) % 30 );
                                              
           if(b[i][j] == 0)
              {
                //dummy.xx = j*w;
                //dummy.yy = i*h;
                //G.brick_length = 18;
                //G.brick_width = 8;
                //G.brick_color = new Color(175, 100, 100);
                //new Effect().start();

                (dummy.g).clearRect(j*w,i*h,w,h);

                try{  Thread.sleep(5);
                  }
                catch(Exception e) {}

                tot++;
                b[i][j] = 1;
              }
         }

       print(str1,1); print(str2,2); print(str3,3);
       print(str4,4); print(str5,5); print(str6,6);

       (dummy.g).translate(-125, -50);
   }


   public void print(String s,int i)
   { int x=50;
     int y=350;

     if( i==1 )
        { (dummy.g).setFont(new Font("TimesRoman",Font.BOLD,20));
          (dummy.g).setColor(Color.blue);
        }
     else if( i==3 )
        {  (dummy.g).setFont(new Font("TimesRoman",Font.BOLD,16));
           (dummy.g).setColor(new Color(140, 140, 175));
        }
     else
        { (dummy.g).setFont(new Font("TimesRoman",Font.PLAIN,14));
          if( i==4 )
                (dummy.g).setColor(new Color(130, 130, 175));
          else  
                (dummy.g).setColor(new Color(120, 120, 175));
        }

     for(int j=0;j<s.length();j++)
      {  (dummy.g).drawString(String.valueOf(s.charAt(j)),x+j*15,y+i*15);
           try{
               Thread.sleep(30);
              }
           catch(Exception e) {}

     }
   }
}

// *****************************************************************



class Effect extends Thread
{
        public void run()
        {
           fade(dummy.g, dummy.xx, dummy.yy, G.brick_length, G.brick_width);
        }
                
        public void fade(Graphics g, int x, int y, int l, int b)
        {
                int rr, gg, bb;
                
                rr = (G.brick_color).getRed();
                gg = (G.brick_color).getGreen();
                bb = (G.brick_color).getBlue();

                for(; rr > 0 || gg > 0 || bb > 0 ; )
                {
                        rr -= 4; gg -= 4; bb -= 4;
                        rr = (rr < 0)? 0:rr;
                        gg = (gg < 0)? 0:gg;
                        bb = (bb < 0)? 0:bb;

                        g.setColor(new Color(rr, gg, bb));
                        g.fill3DRect(x, y, l, b, true);
                        try     { Thread.sleep(15);
                                }
                        catch(Exception e) {}
                }
        }

}

// **************************************************************


class Special extends Thread
{
        public void run()
        {       int i;
                i = dummy.brick_num;

                if( brick.br[i][4] == 1 || brick.br[i][4] == -1 )
                {       inc_dec_slide(dummy.g, dummy.brick_num );
                        brick.br[i][4] = 0;
                }
                else
                if( brick.br[i][5] == 1 )
                {       drop_arrow(dummy.g, dummy.brick_num );
                        brick.br[i][5] = 0;
                }
                else
                if( brick.br[i][6] == 1 )
                {       shoot_power(dummy.g, dummy.brick_num );
                        brick.br[i][6] = 0;
                }
        }


        public void inc_dec_slide(Graphics g, int n)
        {
                int xx, yy;
                int drop_box_len = 10;
                Color drop_box_col;

                xx = brick.br[n][1] + G.brick_length/2;
                yy = brick.br[n][2] + drop_box_len;

                if( brick.br[n][4] == 1  )
                        drop_box_col = Color.green;
                else
                        drop_box_col = Color.red;

                while( yy <  G.slide_y )
                {
                        while( dummy.pause == 1 );
                        if( dummy.stop_anim == 1 )
                                return;
                        g.setColor(drop_box_col);
                        g.fillRect(xx, yy, drop_box_len, drop_box_len);
                        try     { Thread.sleep(15);
                                }
                        catch(Exception e) {}
                        g.clearRect(xx-1, yy-1, drop_box_len+2, drop_box_len+2);

                        yy++;
                }

                if( (xx > G.slide_x && xx < (G.slide_x + G.slide_length))
                        || (xx+drop_box_len > G.slide_x && xx+drop_box_len < (G.slide_x + G.slide_length)) )
                {       if( brick.br[n][4] == 1 )
                        {       G.slide_length += 20;
                                if( G.slide_length > G.max_slide_length )
                                      G.slide_length = G.max_slide_length;
                        }
                        if( brick.br[n][4] == -1 )
                        {       G.slide_length -= 20;
                                if( G.slide_length < G.min_slide_length )
                                      G.slide_length = G.min_slide_length;
                        }        
                }

                g.setColor(Color.black);
                g.clearRect(G.window_x, G.slide_y, G.n_cols*G.brick_length, G.slide_width);

                if( (G.slide_x + G.slide_length) >= G.window_x + G.brick_length * G.n_cols )
                {       G.slide_x = G.window_x + G.brick_length * G.n_cols - G.slide_length;
                }
                g.setColor(G.slide_color);
                g.fill3DRect(G.slide_x, G.slide_y, G.slide_length, G.slide_width, true);
        }

        public void drop_arrow(Graphics g, int n)
        {
                Polygon arrow;
                int arrx[], arry[];
                int xx, yy;
                Color drop_arrow_col;

                arrx = new int[3];
                arry = new int[3];

                xx = brick.br[n][1] + G.brick_length/2;
                yy = brick.br[n][2] + G.brick_width;
                drop_arrow_col = Color.red;

                arrx[0] = xx;  arrx[1] = xx+10;  arrx[2] = (arrx[0]+arrx[1])/2;
                arry[0] = yy;  arry[1] = yy;     arry[2] = arry[0]+20;

                for(int i=1; i<=2; i++)
                {
                        yy = brick.br[n][2] + 2*G.brick_width;

                   while( yy <  G.slide_y - 10)
                   {
                        while( dummy.pause == 1 );
                        if( dummy.stop_anim == 1 )
                                return;


                        arry[0] = yy;  arry[1] = yy;     arry[2] = arry[0]+20;
                        arrow = new Polygon(arrx, arry, 3);

                        g.setColor(drop_arrow_col);
                        g.fillPolygon(arrow);
                        try     { Thread.sleep(10);
                                }
                        catch(Exception e) {}
                        g.setColor(Color.black);
                        g.fillPolygon(arrow);

                        yy++;
                   }

                  if( arrx[2] > G.slide_x && arrx[2] < (G.slide_x + G.slide_length) )
                  {
                        th.life -= 1;
                        dummy.stop_anim = 1;
                        th.update_score();

                        if( th.life == 0 )
                        {       th.terminate(dummy.g);
                                return;
                        }

                        th.initial(450, (int)G.border_width/2, G.slide_x + 10, G.slide_y - (2*G.ball_radius));                       

                        try { (dummy.tt).suspend(); }
                        catch(Exception e) {}

                        th.update_lives();

                        try { (dummy.tt).resume(); }
                        catch(Exception e) {}

                        dummy.stop_anim = 0;
                        break;
                  }
                }
        }

        public void shoot_power(Graphics g, int n)
        {
                int xx, yy;
                int drop_box_len = 10;
                Color drop_box_col;

                xx = brick.br[n][1] + G.brick_length/2;
                yy = brick.br[n][2] + drop_box_len;

                drop_box_col = Color.blue;

                while( yy <  G.slide_y )
                {
                        while( dummy.pause == 1 );
                        if( dummy.stop_anim == 1 )
                                return;

                        g.setColor(drop_box_col);
                        g.fillRect(xx, yy, drop_box_len, drop_box_len);
                        try     { Thread.sleep(15);
                                }
                        catch(Exception e) {}
                        g.clearRect(xx-1, yy-1, drop_box_len+2, drop_box_len+2);

                        yy++;
                }

                if( (xx > G.slide_x && xx < (G.slide_x + G.slide_length))
                        || (xx+drop_box_len > G.slide_x && xx+drop_box_len < (G.slide_x + G.slide_length)) )
                {       dummy.shoot = 1;
                        dummy.bullets += 5;
                        th.update_score();
                }
        }
}


class Shoot extends Thread
{
        public void run()
        {
                shoot_brick(  dummy.g );
        }

        public void shoot_brick( Graphics g )
        {
                int bullet_len = 30;
                Color bullet_col =  (G.brick_color).darker(); //Color.yellow;

                int sx = G.slide_x + G.slide_length/2;
                int yy = G.slide_y - bullet_len; 
                int min_y = G.window_x+G.top_free+G.brick_width/2;
                int col_num = 1, brick_num = 1;

                for(int i=1; i<=G.n_cols; i++)
                {       if( sx > brick.br[i][1] && sx <= brick.br[i][1] + G.brick_length)
                        {       col_num = i;
                                break;
                        }
                }
                brick_num = col_num;

               
                while( yy > min_y)
                {
                        while( dummy.pause == 1 );
                        if( dummy.stop_anim == 1 )
                                return;

                        g.setColor(bullet_col);
                        g.fillRect(sx, yy, 3, bullet_len);
                        g.setColor(Color.black);

                        try     { Thread.sleep(10);
                                }
                        catch(Exception e) {}
                        g.setColor(Color.black);
                        g.fillRect(sx-1, yy-2, 3+1, bullet_len+2);

                        if( yy < brick.br[brick_num][2] + G.brick_width )
                        {       if( brick.br[brick_num][0] == 1 )
                                {
                                        brick.removebrick(brick_num);
                                        return;
                                 }
                                else
                                {       brick_num += G.n_cols;
                                        if( brick_num > G.n_bricks )
                                                return;
                                }
                        }

                        yy--;
               }
        }//end of shoot_brick
}
                


        
// *******************************************************************


public class Brick1 implements KeyListener
{         

  public void init()
  {
    th t = new th();
    (dummy.tt) = new Thread(t);
    (dummy.tt).setPriority(Thread.MIN_PRIORITY);     
    (dummy.tt).start();

    try{ Thread.sleep(10);
         Thread.yield();
       }
    catch(Exception e) {}
  }

  public void paint(Graphics g)
  {
                try     { Thread.yield();
                          Thread.sleep(1);
                        }
                catch(Exception e) {}
   }


  public synchronized void keyPressed(KeyEvent ke)
  {
    //showStatus("Key Down");
    int key1=ke.getKeyCode();

    if(th.life != 0)
    {
     switch(key1)
     {
      case KeyEvent.VK_LEFT:  if(dummy.pause == 1) break;
                              dummy.key_pressed = 1;

                              (dummy.g).setColor(Color.black);
                              G.slide_x -= 15;
                              if(G.slide_x <= G.window_x)
                                  G.slide_x = G.window_x + 1;

                              (dummy.g).setColor(Color.black);
                              (dummy.g).clearRect(G.window_x, G.window_y+G.top_free+G.n_rows*G.brick_width,
                                                        G.n_cols*G.brick_length, G.bottom_free );
                              (dummy.g).clearRect(G.window_x, G.window_y,
                                                        G.n_cols*G.brick_length, G.top_free );

                              break;
      case KeyEvent.VK_RIGHT: if(dummy.pause == 1) break;
                              dummy.key_pressed = 1;

                              (dummy.g).setColor(Color.black);
                              G.slide_x += 15;
                              if(G.slide_x >= (G.border_length-G.border_thickness-G.slide_length))
                                  G.slide_x = (G.border_length-G.border_thickness-G.slide_length);

                              (dummy.g).setColor(Color.black);
                              (dummy.g).clearRect(G.window_x, G.window_y+G.top_free+G.n_rows*G.brick_width,
                                                        G.n_cols*G.brick_length, G.bottom_free );
                              (dummy.g).clearRect(G.window_x, G.window_y,
                                                        G.n_cols*G.brick_length, G.top_free );

                              break;
      case KeyEvent.VK_ESCAPE: if(dummy.pause==0)
                                {
                                  brick.refresh(dummy.g);
                                  (dummy.g).setColor(Color.blue);
                                  (dummy.g).drawString("GAME PAUSED",
                                                (int) (Math.abs(G.border_length-10*4)/2),
                                                G.border_width-G.border_thickness-G.slide_width-30);
                                  dummy.pause=1;
                                  try { (dummy.tt).suspend(); }
                                  catch(Exception e) {}
                                }
                               else
                                {
                                   brick.refresh(dummy.g);
                                   dummy.pause=0;
                                   (dummy.g).setColor(Color.black);
                                   (dummy.g).drawString("GAME PAUSED",
                                                (int) (Math.abs(G.border_length-10*4)/2),
                                                G.border_width-G.border_thickness-G.slide_width-30);
                                     try{ (dummy.tt).resume(); }
                                     catch(Exception e) {}
                                }
                               break;
      case KeyEvent.VK_SPACE: if( dummy.pause == 1 )
                                     break;
                              if( dummy.shoot == 1 )
                              {
                                    if( dummy.bullets <= 0 )
                                    {   dummy.shoot = 0;
                                        break;
                                    }

                                    dummy.bullets --;
                                    new Shoot().start();
                                    th.update_score();
                              }
                              break;
     } paint(dummy.g);
    }
  }

  public void keyReleased(KeyEvent ke)
  {
    // showStatus("KEY UP");
  }

  public void keyTyped(KeyEvent ke)
  {
    paint(dummy.g);
  }


        public static void main(String args[])
        {
                dummy.ch = new choice1();
                (dummy.ch).start_game();

                dummy.b = new Brick1();
                (dummy.b).init();
                (dummy.b).paint(dummy.g);
        }
}          


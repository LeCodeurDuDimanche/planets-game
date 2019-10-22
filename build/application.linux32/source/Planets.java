import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Planets extends PApplet {

enum MENU_TYPE {ACCUEIL, INSTRUCTIONS, NIVEAU, EDITEUR, AUCUN};

Element element;
Attractor target;
ArrayList<Attractor> attractors=new ArrayList<Attractor>();
ArrayList<Button> buttons=new ArrayList<Button>();
float w=1600, h=900;
float wFactor, hFactor;
int timeEnd=0, timeClick=0;
int niveau=1;
int time=0;

MENU_TYPE phase=MENU_TYPE.ACCUEIL;
boolean gagne=false, perdu=false, launched=false, init=false;

Editor editeur=new Editor(0,0,0);

PImage backImageMenu, backImage;

PVector mouse;

float constant=300;

public void setup() {
  
 
  background(0);
  stroke(255);
  fill(255);
  strokeWeight(3);
  
  textSize(40);
  textAlign(CENTER,CENTER);
  
   
  backImageMenu=loadImage("menu.jpg");
  backImageMenu.resize(width,height);
  background(backImageMenu);
  
  backImage=loadImage("fond.jpg");
  backImage.resize(width,height);
  
  wFactor=width/w;
  hFactor=height/h;
  
  frameRate(60);
}

public void draw() {
  time=millis()-time;
  scale(wFactor,hFactor);
  if (!checkMenu(phase))
    return;
  if (millis()-timeEnd<1000)
  {
    fill(255);
    textSize(50);
    text("NIVEAU "+niveau,w/2,h/2);
    return;
  }
  if (gagne)
  {
    niveau++;
    timeEnd=millis();
    init();
    return;
  }
  else if (perdu)
  {
    init();
    return;
  }
  if (phase==MENU_TYPE.AUCUN)
  {
    if (!launched)
    {
      drawVector(element.pos,new PVector(mouse.x-element.pos.x,mouse.y-element.pos.y));
    }
    else
    {
      element.update(time);
      
      if (element.isColliding(target))
      {
        gagne=true;
        return;
      }
      
      for (Attractor a: attractors)
      {
        element.getAttracted(a);
        if (element.isColliding(a))
        {
          perdu=true;
          break;
        }
      }
    }
  }
  
  noStroke();
  fill(255);
  element.render();
  
  fill(255,0,0);
  for (Attractor a: attractors)
  {
    println(a.pos.x+" "+a.pos.y+" "+a.r);
    a.render();
  }
    
  fill(0,255,0);
  if (target!=null)
    target.render();
  
  time=millis();
  
}
public void keyPressed()
{
  if (phase==MENU_TYPE.EDITEUR)
    editeur.eventKey(keyCode);
  else if (key==' ')
    perdu=true;
  if (keyCode==ESC)
  {
    init=false;
    phase=MENU_TYPE.ACCUEIL;
    keyCode=key=0;
  }
}
public void mouseWheel(MouseEvent event)
{
  if (phase==MENU_TYPE.EDITEUR)
    editeur.eventMouse(3,-event.getCount(),0);
}

public void mousePressed() 
{
 /* if (millis()-timeClick<300)
  {
     perdu=true;
  }*/
  mouseX/=wFactor;
  mouseY/=hFactor;
  int event=0, temp;
  switch(phase)
  {
    case EDITEUR:
      editeur.eventMouse(1,mouseX,mouseY);
      break;
    case ACCUEIL:
      for (Button b:buttons)
      {
        if ((temp=b.clicked(mouseX,mouseY))!=0)
          event=temp;
      } 
      switch(event)
      {
        case 1:
          phase=MENU_TYPE.AUCUN;
          init=false;
          break;
       case 2:
         phase=MENU_TYPE.INSTRUCTIONS;
         init=false;
         break;
       case 3:
         phase=MENU_TYPE.EDITEUR;
         init=false;
         break;
      }
      break;
   case INSTRUCTIONS:
      if (buttons.get(0).clicked(mouseX,mouseY)==1)
      {
          phase=MENU_TYPE.ACCUEIL;
          init=false;
      }
      break;
   case AUCUN:
    if (!launched)
    {
      PVector accel=new PVector(mouseX-element.pos.x,mouseY-element.pos.y);
     // accel.div(20);
      element.move.add(accel);
      launched=true;
    }
    break;
  }
}

public void mouseMoved()
{
  if (phase==MENU_TYPE.AUCUN&&!launched) 
    mouse.set(mouseX/wFactor,mouseY/hFactor);
  else if (phase==MENU_TYPE.EDITEUR)
    editeur.eventMouse(0,(int)(mouseX/wFactor),(int)(mouseY/hFactor));
}

public void mouseReleased()
{
   timeClick=millis();
}

public void drawVector(PVector start,PVector v)
{
    pushMatrix();
    stroke(255);
    translate(start.x,start.y);
    line(0,0, v.x,v.y);
    translate(v.x,v.y);
    fill(255);
    rotate(v.heading());
    beginShape();
    vertex(0,0);
    vertex(-10,5);
    vertex(-10,-5);
    endShape(CLOSE);
    popMatrix();
}
class Attractor {
  PVector pos; 
    float mass; 
    float r; 

    Attractor(float _x, float _y, float _mass, float _scale)
  {
    pos=new PVector(_x, _y); 
      mass=_mass; 
      r=sqrt(mass)*_scale;
  }

  public void render()
  {
    ellipse(pos.x, pos.y, r*2, r*2);
  }
}
class Ball {
  PVector pos, move;
  float radius;
  float mass;

  Ball(float x, float y, PVector speed, float _radius)
  {
    pos=new PVector(x, y);
    move=speed;
    radius=_radius;
    mass=radius*radius;
  }

  public void render()
  {
    ellipse(pos.x, pos.y, radius*2, radius*2);
  }

  public void update()
  {
    pos.add(move);
  }

  public void resolveCollision(Ball ball)
  {
    ball.pos.sub(ball.move);
    pos.sub(move);

    PVector rMove=new PVector(ball.move.x, ball.move.y);
    PVector temp= new PVector(move.x, move.y);
    rMove.sub(temp);
    float mag=rMove.mag();
    float f=ball.mass/(mass+ball.mass);
    float fi=mass/(mass+ball.mass);
    
    PVector ab=new PVector(pos.x-ball.pos.x, pos.y-ball.pos.y);
    float angle=PVector.angleBetween(rMove, ab);
    float xAngle=ab.heading();
    
    move.set(cos(angle)*cos(xAngle), cos(angle)*sin(xAngle));
    move.mult(f*mag);

    ball.move.set(sin(angle)*cos(xAngle+HALF_PI), sin(angle)*sin(xAngle+HALF_PI));
    ball.move.mult(fi*mag);

    move.add(temp);
    ball.move.add(temp);
  }
  public boolean isColliding(PVector p)
  {
    return (distSq(p)<radius*radius);
  }
  public boolean isColliding(Ball other)
  {
    float distCenters=radius+other.radius;
    return (distSq(other.pos)<=distCenters*distCenters);
  }

  public float distSq(PVector other)
  {
    float x=pos.x-other.x;
    float y=pos.y-other.y;
    return x*x+y*y;
  }
}
class Editor{
  float x,y,r;
  int type;
  int niv=1;
  
  Editor(float _x, float _y, int _type)
  {
    x=_x;
    y=_y;
    type=_type;
    if (type==0)
      r=sqrt(10)*5;
    else
      r=30;
  }
  
  public void render()
  {
    fill(255);
    text("Niveau "+niv, width/2, 20);
    if (type==0)
      fill(0,255,0);
    else if (type==1)
      fill(255,0,0);
      
    ellipse(x,y,r*2,r*2);
  }
  
  public void execute()
  {
    switch(type)
    {
      case 0:
        target=new Attractor(x,y,10,5);
        type++;
        break;
     case 1:
        attractors.add(new Attractor(x,y,r*r,1));
        println(attractors.size());
        break;
    }
  }
  
  public void saveLevel()
  {
    PrintWriter fichier= createWriter("files/lvl"+niv);
    if (fichier==null)
       return;
    fichier.format("%f;%f\n%d\n",target.pos.x,target.pos.y,attractors.size());
    for (Attractor a: attractors)
      fichier.format("%f;%f;%f\n",a.pos.x,a.pos.y,a.mass);
   fichier.flush();
   fichier.close();
  }
  
  public void loadLevel(int level)
  {
     BufferedReader fichier= createReader("files/lvl"+level);
     if (fichier==null)
       return;
     niv=level;
     String temp="";
     try{
     temp=fichier.readLine();
     } catch(IOException e)
     {
       e.getMessage();
       return;
     }
     String[] array=temp.split("[0-9]*;[0-9]*");
     target=new Attractor(Float.parseFloat(array[0]),Float.parseFloat(array[1]),10,5);
     
     try{
       temp=fichier.readLine();
     } catch(IOException e)
     {
       e.getMessage();
       return;
     }
     int nb=Integer.parseInt(temp);
     for (int i=0;i<nb;i++)
     {
       try{
         temp=fichier.readLine();
       } catch(IOException e)
       {
         e.getMessage();
         return;
       }
       array=temp.split("[0-9]*;[0-9]*");
       attractors.add(new Attractor(Float.parseFloat(array[0]),Float.parseFloat(array[1]),Float.parseFloat(array[2]),1));
     }
     niv=level;
  }
  
  public void eventKey(int key)
  {
    switch(key)
    {
      case LEFT:
        niv--;
        break;
      case RIGHT: 
        niv++;
        break;
      case ' ':
        type++;
        type%=2;
        break;
      case ENTER:
      case RETURN:
        phase=MENU_TYPE.AUCUN;
        init=true;
        timeEnd=millis();
        mouse=element.pos.copy();
        break;
      case BACKSPACE:
        if (type==0 && target!=null)
          target=null;
        else if (type==1 && attractors.size()>0)
          attractors.remove(attractors.size()-1);
        break;
    }
    niv=constrain(niv,1,100);
  }
  
   public void eventMouse(int _type, int _x, int _y)
  {
    if (type==0||type==1)
    {
       x=_x;
       y=_y;
      if (_type==1)
        execute();
    }
    else
      r+=_x;
    
    if (type==0)
      r=sqrt(10)*5;
    else
      r=constrain(r,500,3000);
  }
}
class Element extends Ball {
  PVector acceleration= new PVector(0, 0); 
    PVector prev; 

    Element(float _x, float _y, float _xS, float _yS)
  {
      super(_x, _y, new PVector(_xS, _yS),10); 
      prev=new PVector(_x, _y);
  }
  public void getAttracted(Attractor a)
  {
    PVector dir=new PVector(a.pos.x-pos.x, a.pos.y-pos.y); 
      float mag=dir.magSq(); 
      dir.setMag((a.mass*constant)/mag); 
      acceleration.add(dir);
  }

  public boolean isColliding(Attractor a)
  {
    float distSq=(a.pos.x-pos.x)*(a.pos.x-pos.x)+(a.pos.y-pos.y)*(a.pos.y-pos.y); 
      return (distSq<(a.r+radius)*(a.r+radius));
  }
  public void update(int time) {
      move.add(acceleration); 
      acceleration.mult(0); 
      pos.add(PVector.mult(move, time/1000.0f));
  }

  public void render() {
    ellipse(pos.x, pos.y, radius*2, radius*2); 
      line(prev.x, prev.y, pos.x, pos.y); 
      prev.set(pos.x, pos.y);
  }
}
public void init()
{ 
  element=new Element(10, h/2, 0, 0); 
  if (!perdu)
  {
    attractors.clear();  
    switch(niveau)
    {
      case 1 : 
        target=new Attractor(350, 100, 10, 5); 
        attractors.add(new Attractor(100, 100, 1000, 1)); 
        attractors.add(new Attractor(200, 400, 800, 1)); 
        break; 
      case 2 : 
        target=new Attractor(500, 300, 10, 5); 
        attractors.add(new Attractor(100, 100, 1000, 1)); 
        attractors.add(new Attractor(200, 400, 800, 1)); 
        break; 
      default : 
        if (gagne)
        {
          target=new Attractor(random(w/2, w-30), random(30, h-30), 10, 5); 
          for (int i=0; i<niveau; i++)
          {
            attractors.add(new Attractor(random(80, w-30), random(30, h-30), random(500, 1200), 1));
           // meteors.add(new Element(random(width),random(height),0,0));
          }
        }
        break;
    }
  }
  gagne=launched=perdu=false;
}

public boolean checkMenu(MENU_TYPE type)
{
  boolean continuer=false;
  if (type==MENU_TYPE.AUCUN)
      noCursor();
  else 
    cursor();
  switch (type)
  {
    case EDITEUR:
      background(0);
      if (!init)
      {
        attractors.clear();
        target=null;
        element=new Element(10, h/2, 0, 0); 
        launched=false;
        init=true;
      }
      editeur.render();
      
      continuer=true;
      break;
    case ACCUEIL:
      background(backImageMenu);
      fill(0,0,0,200);
      textSize(80);
      text("PLANETS", w/2, h/2-300);
      fill(255,255,255,200);
      if (!init)
      {
        buttons.clear();
        buttons.add(new Button("JOUER",w/2-250, h/2+200,40,1));
        buttons.add(new Button("EDITEUR",w/2, h/2+200,40,3));
        buttons.add(new Button("AIDE",w/2+250, h/2+200,40,2));
        init=true;
      }
      for (Button b:buttons)
        b.render();
      break;
    case INSTRUCTIONS:
      background(0);
      fill(255);
      textSize(80);
      text("INSTRUCTIONS",w/2,100);
      textSize(25);
      text("Le but du jeu est d'atteindre la planète verte avec l'astéroïde, blanc.\nLes planètes rouges attirent l'asteroïde et le détruise en cas de collision.\nL'editeur de niveau permet de créer des niveaux.\nBonne chance!\n\nClick: Lancer l'astéroïde.\nSouris: Changer la vitesse de départ de l'asteroïde.\nBarre espace: Recommencer le niveau.",w/2,h/2);
      if (!init)
      {
        buttons.clear();
        buttons.add(new Button("RETOUR",w/2, h/2+200,40,1));
        init=true;
      }
      for (Button b:buttons)
        b.render();
        break;
    case AUCUN:
      background(0);
      continuer=true;
      if (!init)
      {
         timeEnd=millis();
         init();
         mouse=element.pos.copy();
         init=true;
      }
      break;
  }
  return continuer;
}

class Button{
  float x,y,w,h;
  float xPos, yPos;
  int fontSize,type;
  String text;
  
  Button(String _text,float _x,float _y, int size,int codeEvent)
  {
    text=new String(_text);
    fontSize=size;
    textSize(size);
    w=textWidth(text);
    h=size+textDescent()+textAscent();
    x=_x;
    y=_y;
    xPos=x-w/2;
    yPos=y;
    type=codeEvent;
  }
  
  public int clicked(float xMouse, float yMouse)
  {
    if (xMouse>xPos&&xMouse<xPos+w&&yMouse>yPos&&yMouse<yPos+h)
      return type;
    return 0;
  }
  
  public void render()
  {
    textSize(fontSize);
    text(text,x,y+h/2);
  }
  
}
  public void settings() {  size(800,450); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "Planets" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}

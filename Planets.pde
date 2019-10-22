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

void setup() {
  size(800,450);
 
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

void draw() {
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
void keyPressed()
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
void mouseWheel(MouseEvent event)
{
  if (phase==MENU_TYPE.EDITEUR)
    editeur.eventMouse(3,-event.getCount(),0);
}

void mousePressed() 
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

void mouseMoved()
{
  if (phase==MENU_TYPE.AUCUN&&!launched) 
    mouse.set(mouseX/wFactor,mouseY/hFactor);
  else if (phase==MENU_TYPE.EDITEUR)
    editeur.eventMouse(0,(int)(mouseX/wFactor),(int)(mouseY/hFactor));
}

void mouseReleased()
{
   timeClick=millis();
}

void drawVector(PVector start,PVector v)
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
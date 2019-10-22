
boolean checkMenu(MENU_TYPE type)
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
  
  int clicked(float xMouse, float yMouse)
  {
    if (xMouse>xPos&&xMouse<xPos+w&&yMouse>yPos&&yMouse<yPos+h)
      return type;
    return 0;
  }
  
  void render()
  {
    textSize(fontSize);
    text(text,x,y+h/2);
  }
  
}
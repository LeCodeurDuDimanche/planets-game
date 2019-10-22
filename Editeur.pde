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
  
  void render()
  {
    fill(255);
    text("Niveau "+niv, width/2, 20);
    if (type==0)
      fill(0,255,0);
    else if (type==1)
      fill(255,0,0);
      
    ellipse(x,y,r*2,r*2);
  }
  
  void execute()
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
  
  void saveLevel()
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
  
  void loadLevel(int level)
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
  
  void eventKey(int key)
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
  
   void eventMouse(int _type, int _x, int _y)
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
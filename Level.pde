void init()
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
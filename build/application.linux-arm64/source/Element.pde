class Element extends Ball {
  PVector acceleration= new PVector(0, 0); 
    PVector prev; 

    Element(float _x, float _y, float _xS, float _yS)
  {
      super(_x, _y, new PVector(_xS, _yS),10); 
      prev=new PVector(_x, _y);
  }
  void getAttracted(Attractor a)
  {
    PVector dir=new PVector(a.pos.x-pos.x, a.pos.y-pos.y); 
      float mag=dir.magSq(); 
      dir.setMag((a.mass*constant)/mag); 
      acceleration.add(dir);
  }

  boolean isColliding(Attractor a)
  {
    float distSq=(a.pos.x-pos.x)*(a.pos.x-pos.x)+(a.pos.y-pos.y)*(a.pos.y-pos.y); 
      return (distSq<(a.r+radius)*(a.r+radius));
  }
  void update(int time) {
      move.add(acceleration); 
      acceleration.mult(0); 
      pos.add(PVector.mult(move, time/1000.0));
  }

  void render() {
    ellipse(pos.x, pos.y, radius*2, radius*2); 
      line(prev.x, prev.y, pos.x, pos.y); 
      prev.set(pos.x, pos.y);
  }
}

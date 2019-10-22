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

  void render()
  {
    ellipse(pos.x, pos.y, r*2, r*2);
  }
}

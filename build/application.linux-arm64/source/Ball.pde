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

  void render()
  {
    ellipse(pos.x, pos.y, radius*2, radius*2);
  }

  void update()
  {
    pos.add(move);
  }

  void resolveCollision(Ball ball)
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
  boolean isColliding(PVector p)
  {
    return (distSq(p)<radius*radius);
  }
  boolean isColliding(Ball other)
  {
    float distCenters=radius+other.radius;
    return (distSq(other.pos)<=distCenters*distCenters);
  }

  float distSq(PVector other)
  {
    float x=pos.x-other.x;
    float y=pos.y-other.y;
    return x*x+y*y;
  }
}

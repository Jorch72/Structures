package crazypants.structures.api.util;

import net.minecraft.util.BlockPos;

//TODO: 1.8 get rid of this
public class Point3i {

  public int x;
  public int y;
  public int z;
  
  public Point3i() {
    x = 0;
    y = 0;
    z = 0;
  }

  public Point3i(BlockPos pos) {
    x = pos.getX();
    y = pos.getY();
    z = pos.getZ();
  }
  
  public Point3i(int x, int y, int z) {  
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public void set(int x, int y, int z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }
  
  public void set(Point3i minOffset) {
    set(minOffset.x, minOffset.y, minOffset.z);       
  }

  public Point3i(Point3i other) {
    x = other.x;
    y = other.y;
    z = other.z;
  }

  public void add(Point3i other) {
    x += other.x;
    y += other.y;
    z += other.z;
  }
  
  public void add(int x2, int y2, int z2) {
    x += x2;
    y += y2;
    z += z2;    
  }

  public double distanceSquared(Point3i v) {
    double dx, dy, dz;
    dx = x - v.x;
    dy = y - v.y;
    dz = z - v.z;
    return (dx * dx + dy * dy + dz * dz);
  }

  public double distance(Point3i v) {
    return Math.sqrt(distanceSquared(v));
  }

  @Override
  public String toString() {
    return "Point3i [x=" + x + ", y=" + y + ", z=" + z + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + x;
    result = prime * result + y;
    result = prime * result + z;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if(this == obj) {
      return true;
    }
    if(obj == null) {
      return false;
    }
    if(getClass() != obj.getClass()) {
      return false;
    }
    Point3i other = (Point3i) obj;
    if(x != other.x) {
      return false;
    }
    if(y != other.y) {
      return false;
    }
    if(z != other.z) {
      return false;
    }
    return true;
  }

  public void scale(int scale) {
    x *= scale;
    y *= scale;
    z *= scale;    
  }


}

package crazypants.structures.gen.structure;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import crazypants.structures.gen.BoundingCircle;
import crazypants.structures.gen.ChunkBounds;
import crazypants.structures.gen.StructureRegister;
import crazypants.vec.Point3i;

public class Structure {

  public static enum Rotation {
    DEG_0(0),
    DEG_90(90),
    DEG_180(180),
    DEG_270(270);

    final int val;

    private Rotation(int val) {
      this.val = val;
    }

    //Keeps all point +'ve
    public void rotate(Point3i bc, int maxX, int maxZ) {
      if(this == Rotation.DEG_0) {
        return;
      }
      if(this == Rotation.DEG_90) {
        bc.set(maxZ - bc.z, bc.y, bc.x);
      } else if(this == Rotation.DEG_180) {
        bc.set(maxX - bc.x, bc.y, maxZ - bc.z);
      } else if(this == Rotation.DEG_270) {
        bc.set(bc.z, bc.y, maxX - bc.x);
      }
    }

    //must have an x/z origin of o 
    public AxisAlignedBB rotate(AxisAlignedBB bb) {
      if(this == Rotation.DEG_0 || this == Rotation.DEG_180) {
        return bb;
      }
      Point3i sz = Structure.size(bb);
      return AxisAlignedBB.getBoundingBox(0, 0, 0, sz.z, sz.y, sz.x);
    }

    public Rotation next() {
      int ord = ordinal() + 1;
      if(ord > values().length - 1) {
        ord = 0;
      }
      return values()[ord];
    }

    public static Rotation get(int deg) {
      for (Rotation r : values()) {
        if(r.val == deg) {
          return r;
        }
      }
      return null;
    }
  }
  
  private final Point3i origin;
  private final StructureComponent component;

  private BoundingCircle bc;
  private final Rotation rotation;
  private AxisAlignedBB bb;
  private Point3i size;
  private boolean canSpanChunks;

  public Structure(Point3i origin, Rotation rotation, boolean canSpanChunks, StructureComponent component) {    
    if(origin == null) {
      origin = new Point3i();
    }
    this.origin = origin;
    if(rotation == null) {
      this.rotation = Rotation.DEG_0;
    } else {
      this.rotation = rotation;
    }
    this.canSpanChunks = canSpanChunks;
    this.component = component;
    updateBounds();
  }

  public Structure(NBTTagCompound root) {    
    component = StructureRegister.instance.getStructureComponent(root.getString("component"), true);
    origin = new Point3i(root.getInteger("x"), root.getInteger("y"), root.getInteger("z"));
    rotation = Rotation.values()[MathHelper.clamp_int(root.getShort("rotation"), 0, Rotation.values().length - 1)];
    canSpanChunks = root.getBoolean("canSpanChunks");
    updateBounds();
  }

  public AxisAlignedBB getBounds() {
    return bb;
  }

  public Point3i getSize() {
    return size;
  }

  public Point3i getOrigin() {
    return origin;
  }

  public void setOrigin(Point3i origin) {
    this.origin.set(origin.x, origin.y, origin.z);
    updateBounds();
  }

  private void updateBounds() {
    if(isValid()) {
      bb = rotation.rotate(component.getBounds());
      bb = bb.getOffsetBoundingBox(origin.x, origin.y, origin.z);
      size = size(bb);
      bc = new BoundingCircle(bb);
    }
  }
  
  

  public static crazypants.vec.Point3i size(AxisAlignedBB bb) {
    return new Point3i((int) Math.abs(bb.maxX - bb.minX), (int) Math.abs(bb.maxY - bb.minY), (int) Math.abs(bb.maxZ - bb.minZ));
  }

  public ChunkCoordIntPair getChunkCoord() {
    return new ChunkCoordIntPair(origin.x >> 4, origin.z >> 4);
  }

  public ChunkBounds getChunkBounds() {
    Point3i size = component.getSize();
    return new ChunkBounds(origin.x >> 4, origin.z >> 4, (origin.x + size.x) >> 4, (origin.z + size.z) >> 4);
  }

  public void writeToNBT(NBTTagCompound root) {
    root.setInteger("x", origin.x);
    root.setInteger("y", origin.y);
    root.setInteger("z", origin.z);
    root.setString("component", component.getUid());
    root.setShort("rotation", (short) rotation.ordinal());
    root.setBoolean("canSpanChunks", canSpanChunks);
  }

  

  public boolean isChunkBoundaryCrossed() {
    return getChunkBounds().getNumChunks() > 1;
  }

  public boolean canSpanChunks() {    
    return canSpanChunks;
  }
  
  public StructureComponent getComponent() {
    return component;
  }

  public void build(World world) {
    build(world, null);
  }

  public void build(World world, int chunkX, int chunkZ) {
    ChunkBounds genBounds = new ChunkBounds(chunkX, chunkZ, chunkX, chunkZ);
    build(world, genBounds);
  }

  public void build(World world, ChunkBounds genBounds) {
    component.build(world, origin.x, origin.y, origin.z, rotation, genBounds);
  }

  public double getBoundingRadius() {
    return bc.getRadius();
  }

  public BoundingCircle getBoundingCircle() {
    return bc;
  }
  
  public boolean isValid() {
    return origin != null && component != null;
  }

  @Override
  public String toString() {
    if(isValid()) {
      return "Structure [canSpanChunks=" + canSpanChunks + ", component=" + component.getUid() + ", origin=" + origin + "]";
    } else {
      return "Structure [canSpanChunks=" + canSpanChunks  + ", component=" + component + ", origin=" + origin + "]";
    }
  }

}

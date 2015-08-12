package crazypants.structures.gen.structure.sampler;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import crazypants.structures.gen.StructureUtil;
import crazypants.structures.gen.WorldStructures;
import crazypants.structures.gen.structure.Structure;
import crazypants.vec.Point3i;
import crazypants.vec.VecUtil;

public class SurfaceLocationSampler implements ILocationSampler {

  private static final Random rnd = new Random();

  private int distanceFromSurface = 0;
  private boolean canPlaceInFluid = false;

  public SurfaceLocationSampler() {
  }

  public int getDistanceFromSurface() {
    return distanceFromSurface;
  }

  public void setDistanceFromSurface(int distanceFromSurface) {
    this.distanceFromSurface = distanceFromSurface;
  }

  public boolean isCanPlaceInFluid() {
    return canPlaceInFluid;
  }

  public void setCanGenerateOnFluid(boolean canPlaceInFluid) {
    this.canPlaceInFluid = canPlaceInFluid;
  }

  @Override
  public Point3i generateCandidateLocation(Structure structure, WorldStructures structures, World world,
      Random random, int chunkX, int chunkZ) {

    return findStartPos(structure, chunkX, chunkZ, world);
  }

  protected Point3i findStartPos(Structure structure, int chunkX, int chunkZ, World world) {     
    Point3i candidate;
    if(structure.canSpanChunks()) {
      candidate = getRandomBlock(world, chunkX, chunkZ, 16, 16, distanceFromSurface - structure.getSurfaceOffset(), structure.getSize().y);
    } else {
      candidate = getRandomBlock(world, chunkX, chunkZ, 16 - structure.getSize().x, 16 - structure.getSize().z, distanceFromSurface - structure.getSurfaceOffset(), structure.getSize().y);
    }
    return candidate;
  }

  protected Point3i getRandomBlock(World world, int chunkX, int chunkZ, int maxOffsetX, int maxOffsetZ, int distanceFromSurface, int requiredVerticalSpace) {
    int x = chunkX * 16 + rnd.nextInt(maxOffsetX);
    int z = chunkZ * 16 + rnd.nextInt(maxOffsetZ);

    //Find the surface y
    Block blk;
    Point3i loc = new Point3i();

    blk = StructureUtil.getSurfaceBlock(world, x, z, loc, true, !canPlaceInFluid);
    if(blk != world.getBiomeGenForCoords(VecUtil.getCenterOfChunk(x, z)).topBlock && blk != world.getBiomeGenForCoords(VecUtil.getCenterOfChunk(x, z)).fillerBlock) {
      return null;
    }

    //Correct for distance from surface
    loc.y += distanceFromSurface;    
    if(loc.y > 0 && loc.y < 256 + requiredVerticalSpace) {
      return loc;
    }
    return null;
  }

}

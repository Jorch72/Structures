package crazypants.structures.gen.structure.validator;

import java.util.Random;

import crazypants.structures.api.gen.ISiteValidator;
import crazypants.structures.api.gen.IStructure;
import crazypants.structures.api.gen.IWorldStructures;
import crazypants.structures.api.util.Point3i;
import crazypants.structures.api.util.StructureUtil;
import crazypants.structures.api.util.VecUtil;
import crazypants.structures.gen.structure.Border;
import net.minecraft.block.Block;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidRegistry;

public class LevelGroundValidator implements ISiteValidator {

  private boolean canSpawnOnWater = false;

  private int maxSampleCount = 100;

  private int sampleSpacing = 4;

  private int tolerance = 2;

  private Border border = new Border();

  public LevelGroundValidator() {
    border.setBorderXZ(1);
  }

  @Override
  public boolean isValidBuildSite(IStructure structure, IWorldStructures existingStructures, World world, Random random, StructureBoundingBox clip) {

    AxisAlignedBB bb = structure.getBounds();
    int minX = (int) bb.minX;
    int maxX = (int) bb.maxX;
    int minZ = (int) bb.minZ;
    int maxZ = (int) bb.maxZ;

    Point3i size = structure.getSize();

    int numSamsX = Math.max(1, (size.x / sampleSpacing));
    int numSamsZ = Math.max(1, (size.z / sampleSpacing));

    int totalSamples = numSamsX * numSamsZ;
    if(totalSamples > maxSampleCount) {
      double ratio = ((double) maxSampleCount / totalSamples);
      ratio = Math.sqrt(ratio);
      numSamsX = (int) (numSamsX * ratio);
      numSamsZ = (int) (numSamsZ * ratio);
    }

    int xSpacing = Math.round(Math.max(1, (float) size.x / (numSamsX + 1)));
    int zSpacing = Math.round(Math.max(1, (float) size.z / (numSamsZ + 1)));

    int[] minMax = new int[] { Integer.MAX_VALUE, -Integer.MAX_VALUE };
    Point3i sampleLoc = new Point3i(structure.getOrigin());
    Point3i surfacePos = new Point3i();
    for (int x = minX - border.get(ForgeDirection.WEST); x <= maxX + border.get(ForgeDirection.EAST); x += xSpacing) {
      for (int z = minZ - border.get(ForgeDirection.NORTH); z <= maxZ + border.get(ForgeDirection.SOUTH); z += zSpacing) {

        if(clip == null || VecUtil.isInBounds(clip, x,z)) {
          sampleLoc.set(x, structure.getOrigin().y + structure.getSurfaceOffset(), z);
          if(!testLocation(sampleLoc, world, minMax, surfacePos)) {
            return false;
          }
        }

        //TODO:  The is not taking into acount that this point migt be clipped due to the border
        //make sure we always get the corners
        if(z < maxZ && z + zSpacing > maxZ) {
          z = maxZ - zSpacing;
        }

      }

      //make sure we always get the corners
      if(x < maxX && x + xSpacing > maxX) {
        x = maxX - xSpacing;
      }

    }

    if(minMax[0] > minMax[1]) { //no hits found
      return false;
    }

    int heightRange = (minMax[1] - minMax[0]);
    if(heightRange > tolerance) {
      return false;
    }
    
    if(heightRange > 2) {
      //TODO: relying on this change to be repsected is asking for trouble
      structure.getOrigin().y = minMax[0] + ((heightRange - 1) / 2) - structure.getSurfaceOffset();      
    }

    return true;
  }

  protected boolean testLocation(Point3i sampleLocation, World world, int[] minMax, Point3i surfacePos) {
    Block blk = StructureUtil.getSurfaceBlock(world, sampleLocation.x, sampleLocation.z,
        Math.max(0, sampleLocation.y - tolerance - 1), Math.min(256, sampleLocation.y + tolerance + 1), surfacePos, true, canSpawnOnWater);
    if(blk != null) {
      if(!canSpawnOnWater && FluidRegistry.lookupFluidForBlock(blk) != null) {
        return false;
      }
      minMax[0] = Math.min(minMax[0], surfacePos.y);
      minMax[1] = Math.max(minMax[1], surfacePos.y);
      if((minMax[1] - minMax[0]) > tolerance) {
        return false;
      }
      return true;
    }
    return false;
  }

  public boolean isCanSpawnOnWater() {
    return canSpawnOnWater;
  }

  public void setCanSpawnOnWater(boolean canSpawnOnWater) {
    this.canSpawnOnWater = canSpawnOnWater;
  }

  public int getMaxSampleCount() {
    return maxSampleCount;
  }

  public void setMaxSampleCount(int maxSampleCount) {
    this.maxSampleCount = maxSampleCount;
  }

  public int getSampleSpacing() {
    return sampleSpacing;
  }

  public void setSampleSpacing(int sampleSpacing) {
    this.sampleSpacing = sampleSpacing;
  }

  public int getTolerance() {
    return tolerance;
  }

  public void setTolerance(int tolerance) {
    this.tolerance = tolerance;
  }

  public Border getBorder() {
    return border;
  }

  public void setBorder(Border border) {
    this.border = border;
  }
  
  
}

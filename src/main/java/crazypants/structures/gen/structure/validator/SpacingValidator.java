package crazypants.structures.gen.structure.validator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import crazypants.structures.gen.BoundingCircle;
import crazypants.structures.gen.WorldStructures;
import crazypants.structures.gen.structure.Structure;
import crazypants.structures.gen.structure.StructureGenerator;
import crazypants.vec.Vector2d;

public class SpacingValidator implements ILocationValidator {

  private static final double CHUNK_RADIUS = new Vector2d().distance(new Vector2d(8, 8));

  private int minSpacing;
  private final List<String> templateFilter = new ArrayList<String>();
  private boolean validateChunk = false;
  private boolean validateLocation = true;

  public SpacingValidator() {
    this(20, (String[]) null);
  }

  public SpacingValidator(int minSpacing, String... templateType) {
    this(minSpacing, minSpacing >= 32, minSpacing < 32, templateType);
  }

  public SpacingValidator(int minSpacing, boolean checkChunkDistance, boolean checkPointDistance, String... matchTypes) {
    this.minSpacing = minSpacing;
    this.validateChunk = checkChunkDistance;
    this.validateLocation = checkPointDistance;
    if(matchTypes != null) {
      for (String tmp : matchTypes) {
        if(tmp != null) {
          templateFilter.add(tmp);
        }
      }
    }
  }

  @Override
  public boolean isValidChunk(StructureGenerator template, WorldStructures structures, World world, Random random, int chunkX, int chunkZ) {

    if(!validateChunk) {
      return true;
    }
    ChunkCoordIntPair cc = new ChunkCoordIntPair(chunkX, chunkZ);
    BoundingCircle bc = new BoundingCircle(cc.getCenterXPos(), cc.getCenterZPosition(), minSpacing + CHUNK_RADIUS);
    return areMatchingStructuresInBounds(structures, bc);
  }

  @Override
  public boolean isValidLocation(Structure structure, WorldStructures existingStructures, World world, Random random, int chunkX, int chunkZ) {

    if(!validateLocation) {
      return true;
    }
    BoundingCircle bc = new BoundingCircle(structure.getOrigin().x + structure.getSize().x / 2, structure.getOrigin().z + structure.getSize().z / 2,
        (int) (structure.getBoundingRadius() + minSpacing));
    return areMatchingStructuresInBounds(existingStructures, bc);
  }

  private boolean areMatchingStructuresInBounds(WorldStructures existingStructures, BoundingCircle bc) {
    List<Structure> res = new ArrayList<Structure>();    
    for (ChunkCoordIntPair chunk : bc.getChunks()) {
      if(bc.intersects(new BoundingCircle(chunk.getCenterXPos(), chunk.getCenterZPosition(), CHUNK_RADIUS))) {
        getStructuresIntersectingChunk(chunk, existingStructures, res);
        if(!res.isEmpty()) {
          for (Structure s : res) {
            if(s.getBoundingCircle().intersects(bc)) {
              return false;
            }
          }
          res.clear();
        }
      }
    }
    return true;
  }

  private void getStructuresIntersectingChunk(ChunkCoordIntPair chunk, WorldStructures structures, List<Structure> res) {
    structures.getStructuresIntersectingChunk(chunk, null, res);
    if(!templateFilter.isEmpty() && !res.isEmpty()) {
      ListIterator<Structure> iter = res.listIterator();
      while (iter.hasNext()) {
        Structure match = iter.next();
        if(!templateFilter.contains(match.getComponent().getUid())) {
          iter.remove();
        }
      }
    }
  }

  public int getMinSpacing() {
    return minSpacing;
  }

  public void setMinSpacing(int minSpacing) {
    this.minSpacing = minSpacing;
  }

  public boolean isValidateChunk() {
    return validateChunk;
  }

  public void setValidateChunk(boolean validateChunk) {
    this.validateChunk = validateChunk;
  }

  public boolean isValidateLocation() {
    return validateLocation;
  }

  public void setValidateLocation(boolean validateLocation) {
    this.validateLocation = validateLocation;
  }

  public List<String> getTemplateFilter() {
    return templateFilter;
  }

  public void setTemplateFilter(Collection<String> filter) {
    templateFilter.clear();
    templateFilter.addAll(filter);
  }

}

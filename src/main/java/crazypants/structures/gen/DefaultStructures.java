package crazypants.structures.gen;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import crazypants.IoUtil;
import crazypants.structures.Log;
import crazypants.structures.config.Config;
import crazypants.structures.gen.io.resource.IResourcePath;
import crazypants.structures.gen.io.resource.StructureResourceManager;

public class DefaultStructures {

  public static final File ROOT_DIR = new File(Config.configDirectory + "/structures/");
  public static final String RESOURCE_PATH = "/assets/enderstructures/structures/";

  public static final File TEST_DIR = new File(Config.configDirectory + "/test/");
  public static final String TEST_RESOURCE_PATH = "/assets/enderstructures/test/";

  public static void init() {

    StructureRegister reg = StructureRegister.instance;
    List<IResourcePath> toScan = new ArrayList<IResourcePath>();

    if(Config.testStructuresEnabled) {
      loadTestResources(reg, toScan);
    }

    toScan.add(reg.getResourceManager().addResourceDirectory(ROOT_DIR));
    toScan.add(reg.getResourceManager().addClassLoaderResourcePath(RESOURCE_PATH));
    
    registerZipFiles(ROOT_DIR, toScan);

    for (IResourcePath path : toScan) {
      StructureRegister.instance.loadAndRegisterAllResources(path, true);
    }

  }

  public static void registerZipFiles(File rootDir, List<IResourcePath> toScan) {
    if(rootDir == null || !rootDir.exists() || !rootDir.isDirectory()) {
      return;
    }
    File[] kids = rootDir.listFiles();
    if(kids == null) {
      return;
    }
    for(File kid : kids) {
      if(kid.isFile() && kid.getName().endsWith(".zip")) {
        toScan.add(StructureRegister.instance.getResourceManager().addResourceZip(kid));
      }
    }   
  }

  private static void loadTestResources(StructureRegister reg, List<IResourcePath> toScan) {
    String name = "testGenerator" + StructureResourceManager.GENERATOR_EXT;
    copyTestFile(name, name + ".defaultValues");

    name = "esRuinTest" + StructureResourceManager.TEMPLATE_EXT;
    copyTestFile(name, name + ".defaultValues");

    name = "esSmallHouseTest" + StructureResourceManager.TEMPLATE_EXT;
    copyTestFile(name, name + ".defaultValues");
    
    name = "esVillagerTest" + StructureResourceManager.VILLAGER_EXT;
    copyTestFile(name, name + ".defaultValues");

    registerZipFiles(TEST_DIR, toScan);
    
    toScan.add(reg.getResourceManager().addResourceDirectory(TEST_DIR));
    toScan.add(reg.getResourceManager().addClassLoaderResourcePath(TEST_RESOURCE_PATH));
  }
  
  private static void copyTestFile(String resourceName, String fileName) {
    try {
      IoUtil.copyTextTo(new File(TEST_DIR, fileName), DefaultStructures.class.getResourceAsStream(TEST_RESOURCE_PATH + resourceName));
    } catch (Exception e) {
      Log.warn(
          "EnderZooStructures: Could not copy " + TEST_RESOURCE_PATH + resourceName + " from jar to " + TEST_DIR.getAbsolutePath() + fileName + " Ex:" + e);
    }
  }

}

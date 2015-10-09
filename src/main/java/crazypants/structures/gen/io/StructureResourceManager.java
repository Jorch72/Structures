package crazypants.structures.gen.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

import crazypants.IoUtil;
import crazypants.structures.api.gen.IStructureGenerator;
import crazypants.structures.api.gen.IStructureTemplate;
import crazypants.structures.gen.StructureRegister;
import crazypants.structures.gen.structure.StructureComponentNBT;
import crazypants.structures.gen.structure.StructureTemplate;

public class StructureResourceManager {

  public static final String GENERATOR_EXT = ".gen";
  public static final String COMPONENT_EXT = ".nbt";
  public static final String TEMPLATE_EXT = ".stp";
  
  private final List<ResourcePath> resourcePaths = new ArrayList<ResourcePath>();
  private final GeneratorParser parser = new GeneratorParser();
  private final StructureRegister register;
  
  public StructureResourceManager(StructureRegister register) {  
    this.register = register;       
  }

  public void addResourcePath(File dir) {
    resourcePaths.add(new ResourcePath(dir.getAbsolutePath(), true));
  }

  public void addResourcePath(String resourcePath) {
    resourcePaths.add(new ResourcePath(resourcePath, false));
  }

  public IStructureGenerator loadGenerator(String uid) throws Exception {
    return parseJsonGenerator(loadGeneratorText(uid));
  }
  
  public IStructureGenerator loadGenerator(File fromFile) throws Exception {
    return parseJsonGenerator(loadText(fromFile));
  }

  public IStructureGenerator parseJsonGenerator(String json) throws Exception {
    return parser.parseGeneratorConfig(register, json);
  }
  
  public String loadText(File fromFile) throws IOException {
    return IoUtil.readStream(new FileInputStream(fromFile));
  }
  
  public String loadGeneratorText(String uid) throws IOException {
    InputStream str = getStreamForGenerator(uid);
    if(str == null) {
      throw new IOException("Could not find the resource for generator: " + uid);
    }
    return IoUtil.readStream(str);
  }

  public StructureComponentNBT loadStructureComponent(String uid) throws IOException {
    InputStream stream = null;
    try {
      stream = getStreamForComponent(uid);
      if(stream == null) {
        throw new IOException("StructureResourceManager: Could find resources for template: " + uid);        
      }
      return new StructureComponentNBT(stream);
    } finally {
      IOUtils.closeQuietly(stream);
    }
  }
  
  private String loadTemplateText(String uid) throws IOException {
    InputStream str = getStreamForTemplate(uid);
    if(str == null) {
      throw new IOException("Could not find the resource for template: " + uid);
    }
    return IoUtil.readStream(str);
  }
  
  public StructureTemplate loadTemplate(String uid) throws Exception {
    return parseJsonTemplate(loadTemplateText(uid));
  }

  public IStructureTemplate loadTemplate(File fromFile) throws Exception {
    return parseJsonTemplate(loadText(fromFile));
  }

  public StructureTemplate parseJsonTemplate(String json) throws Exception {
    return parser.parseTemplateConfig(register, json);
  }

  private InputStream getStreamForGenerator(String uid) {
    return getStream(uid + GENERATOR_EXT);
  }

  private InputStream getStreamForComponent(String uid) {
    if(uid.endsWith(COMPONENT_EXT)) {
      return getStream(uid.substring(0, uid.length() - COMPONENT_EXT.length()));
    }
    return getStream(uid + COMPONENT_EXT);
  }
  
  private InputStream getStreamForTemplate(String uid) {
    return getStream(uid + TEMPLATE_EXT);
  }

  private InputStream getStream(String resourceName) {
    for (ResourcePath rp : resourcePaths) {
      InputStream is = rp.getStream(resourceName);
      if(is != null) {
        return is;
      }
    }
    return null;
  }

  private static class ResourcePath {

    private final String root;
    private final File dir;
    private final boolean isFile;

    public ResourcePath(String root, boolean isFile) {
      this.root = root;
      this.isFile = isFile;
      File tmp = null;
      if(isFile) {
        File f = new File(root);
        if(f.exists()) {
          tmp = f;
        }
      }
      dir = tmp;
    }

    public InputStream getStream(String name) {
      if(isFile) {
        if(dir == null) {
          return null;
        }
        try {
          return new FileInputStream(new File(dir, name));
        } catch (FileNotFoundException e) {
          return null;
        }
      } else {
        return StructureRegister.class.getResourceAsStream(root + name);
      }
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (isFile ? 1231 : 1237);
      result = prime * result + ((root == null) ? 0 : root.hashCode());
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
      ResourcePath other = (ResourcePath) obj;
      if(isFile != other.isFile) {
        return false;
      }
      if(root == null) {
        if(other.root != null) {
          return false;
        }
      } else if(!root.equals(other.root)) {
        return false;
      }
      return true;
    }

  }

}

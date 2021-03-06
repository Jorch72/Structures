package crazypants.structures.gen.io.resource;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.IOUtils;

import crazypants.structures.gen.StructureGenRegister;

public class ClassLoaderResourcePath extends AbstractResourcePath {

  private final String root;

  public ClassLoaderResourcePath(String root) {
    this.root = root;
  }

  @Override
  public List<String> getChildren() {
    String[] kids = null;
    try {
      URL u = StructureGenRegister.class.getResource(root);
      if(u != null) {
        File f = new File(u.toURI());
        kids = f.list();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    if(kids == null || kids.length == 0) {
      return Collections.emptyList();
    }
    return Arrays.asList(kids);
  }

  @Override
  public boolean exists(String name) {
    String path;
    if(root.endsWith("/")) {
      path = root + name;
    } else {
      path = root + "/" + name;
    }

    InputStream is = StructureGenRegister.class.getResourceAsStream(path);
    if(is == null) {
      return false;
    }
    IOUtils.closeQuietly(is);
    return true;
  }

  @Override
  public InputStream getStream(String name) {
    if(root.endsWith("/")) {
      return StructureGenRegister.class.getResourceAsStream(root + name);
    }
    return StructureGenRegister.class.getResourceAsStream(root + "/" + name);
  }
}

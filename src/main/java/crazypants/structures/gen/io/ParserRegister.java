package crazypants.structures.gen.io;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonObject;

import crazypants.structures.api.gen.IChunkValidator;
import crazypants.structures.api.gen.IDecorator;
import crazypants.structures.api.gen.ILocationSampler;
import crazypants.structures.api.gen.ISitePreperation;
import crazypants.structures.api.gen.ISiteValidator;

public class ParserRegister {

  private final List<IParser> factories = new ArrayList<IParser>();

  private final Map<String, ILocationSamplerParser> samplerParsers = new HashMap<String, ILocationSamplerParser>();

  private final Map<String, IChunkValidatorParser> chunkValParsers = new HashMap<String, IChunkValidatorParser>();

  private final Map<String, ISitePreperationParser> prepParsers = new HashMap<String, ISitePreperationParser>();

  private final Map<String, ISiteValidatorParser> siteValParsers = new HashMap<String, ISiteValidatorParser>();

  private final Map<String, IDecoratorParser> decParsers = new HashMap<String, IDecoratorParser>();

  public static final ParserRegister instance = new ParserRegister();

  static {
    DefaultParsers.register();    
  }

  private final NullFactory nullFactory = new NullFactory();

  private ParserRegister() {
  }

  public void register(IParser fact) {
    factories.add(fact);
  }

  public ILocationSampler createSampler(String uid, JsonObject json) {
    ILocationSamplerParser f = samplerParsers.get(uid);
    if(f == null) {
      f = findFactory(uid, ILocationSamplerParser.class);
      samplerParsers.put(uid, f);
    }
    return f.createSampler(uid, json);
  }

  public IChunkValidator createChunkValidator(String uid, JsonObject json) {
    IChunkValidatorParser f = chunkValParsers.get(uid);
    if(f == null) {
      f = findFactory(uid, IChunkValidatorParser.class);
      chunkValParsers.put(uid, f);
    }
    return f.createChunkValidator(uid, json);
  }

  public ISitePreperation createPreperation(String uid, JsonObject json) {
    ISitePreperationParser f = prepParsers.get(uid);
    if(f == null) {
      f = findFactory(uid, ISitePreperationParser.class);
      prepParsers.put(uid, f);
    }
    return f.createPreperation(uid, json);
  }

  public ISiteValidator createSiteValidator(String uid, JsonObject json) {
    ISiteValidatorParser f = siteValParsers.get(uid);
    if(f == null) {
      f = findFactory(uid, ISiteValidatorParser.class);
      siteValParsers.put(uid, f);
    }
    return f.createSiteValidator(uid, json);
  }

  public IDecorator createDecorator(String uid, JsonObject json) {
    IDecoratorParser f = decParsers.get(uid);
    if(f == null) {
      f = findFactory(uid, IDecoratorParser.class);
      decParsers.put(uid, f);
    }
    return f.createDecorator(uid, json);
  }

  @SuppressWarnings("unchecked")
  private <T extends IParser> T findFactory(String uid, Class<T> type) {
    if(uid == null) {
      return (T) nullFactory;
    }
    for (IParser fact : factories) {
      if(fact.canParse(uid) && type.isAssignableFrom(fact.getClass())) {
        return (T) fact;
      }
    }
    return (T) nullFactory;
  }

  private class NullFactory extends AbstractSingleParserFactory {

    private NullFactory() {
      super(null);
    }
  }

}

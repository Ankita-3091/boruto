package me.jiangew.boruto.ebservice.barman;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

/**
 * Converter for {@link me.jiangew.boruto.ebservice.barman.Beer}.
 * NOTE: This class has been automatically generated from the {@link me.jiangew.boruto.ebservice.barman.Beer} original class using Vert.x codegen.
 */
public class BeerConverter {

  public static void fromJson(Iterable<java.util.Map.Entry<String, Object>> json, Beer obj) {
    for (java.util.Map.Entry<String, Object> member : json) {
      switch (member.getKey()) {
        case "name":
          if (member.getValue() instanceof String) {
            obj.setName((String)member.getValue());
          }
          break;
        case "price":
          if (member.getValue() instanceof Number) {
            obj.setPrice(((Number)member.getValue()).intValue());
          }
          break;
        case "style":
          if (member.getValue() instanceof String) {
            obj.setStyle((String)member.getValue());
          }
          break;
      }
    }
  }

  public static void toJson(Beer obj, JsonObject json) {
    toJson(obj, json.getMap());
  }

  public static void toJson(Beer obj, java.util.Map<String, Object> json) {
    if (obj.getName() != null) {
      json.put("name", obj.getName());
    }
    json.put("price", obj.getPrice());
    if (obj.getStyle() != null) {
      json.put("style", obj.getStyle());
    }
  }
}

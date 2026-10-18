import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class AssetGenerator {
    private static final String[] TIERS = {"copper", "iron", "gold", "emerald", "diamond"};
    private static final String ASSETS_DIR = "src/main/resources/assets/bundleupgrade";
    private static final String DATA_DIR = "src/main/resources/data";

    public static void main(String[] args) throws IOException {
        generateModels();
        generateItems();
        generateLang();
        generateRecipes();
        generateTags();
        System.out.println("Generated all models, items, lang, recipes, and tags!");
    }

    private static void generateModels() throws IOException {
        File modelsDir = new File(ASSETS_DIR, "models/item");
        modelsDir.mkdirs();

        for (String tier : TIERS) {
            String name = tier + "_bundle";

            // Default closed model
            String closedJson = """
            {
              "parent": "minecraft:item/generated",
              "textures": {
                "layer0": "bundleupgrade:item/%s"
              }
            }
            """.formatted(name);
            writeFile(new File(modelsDir, name + ".json"), closedJson);

            // Open back model
            String openBackJson = """
            {
              "parent": "minecraft:item/template_bundle_open_back",
              "textures": {
                "layer0": "bundleupgrade:item/%s_open_back"
              }
            }
            """.formatted(name);
            writeFile(new File(modelsDir, name + "_open_back.json"), openBackJson);

            // Open front model
            String openFrontJson = """
            {
              "parent": "minecraft:item/template_bundle_open_front",
              "textures": {
                "layer0": "bundleupgrade:item/%s_open_front"
              }
            }
            """.formatted(name);
            writeFile(new File(modelsDir, name + "_open_front.json"), openFrontJson);
        }
    }

    private static void generateItems() throws IOException {
        File itemsDir = new File(ASSETS_DIR, "items");
        itemsDir.mkdirs();

        for (String tier : TIERS) {
            String name = tier + "_bundle";
            String itemJson = """
            {
              "model": {
                "type": "minecraft:select",
                "cases": [
                  {
                    "model": {
                      "type": "minecraft:condition",
                      "on_false": {
                        "type": "minecraft:model",
                        "model": "bundleupgrade:item/%s"
                      },
                      "on_true": {
                        "type": "minecraft:composite",
                        "models": [
                          {
                            "type": "minecraft:model",
                            "model": "bundleupgrade:item/%s_open_back"
                          },
                          {
                            "type": "minecraft:bundle/selected_item"
                          },
                          {
                            "type": "minecraft:model",
                            "model": "bundleupgrade:item/%s_open_front"
                          }
                        ]
                      },
                      "property": "minecraft:bundle/has_selected_item"
                    },
                    "when": "gui"
                  }
                ],
                "fallback": {
                  "type": "minecraft:model",
                  "model": "bundleupgrade:item/%s"
                },
                "property": "minecraft:display_context"
              }
            }
            """.formatted(name, name, name, name);
            writeFile(new File(itemsDir, name + ".json"), itemJson);
        }
    }

    private static void generateLang() throws IOException {
        File langDir = new File(ASSETS_DIR, "lang");
        langDir.mkdirs();

        String enUs = """
        {
          "item.bundleupgrade.copper_bundle": "Copper Bundle",
          "item.bundleupgrade.iron_bundle": "Iron Bundle",
          "item.bundleupgrade.gold_bundle": "Gold Bundle",
          "item.bundleupgrade.emerald_bundle": "Emerald Bundle",
          "item.bundleupgrade.diamond_bundle": "Diamond Bundle",
          "tooltip.bundleupgrade.capacity": "%s / %s",
          "tooltip.bundleupgrade.capacity_units": "%s / %s Units",
          "tooltip.bundleupgrade.upgrade_info": "Preserves all stored items when upgraded"
        }
        """;
        writeFile(new File(langDir, "en_us.json"), enUs);

        String viVn = """
        {
          "item.bundleupgrade.copper_bundle": "Bundle Đồng",
          "item.bundleupgrade.iron_bundle": "Bundle Sắt",
          "item.bundleupgrade.gold_bundle": "Bundle Vàng",
          "item.bundleupgrade.emerald_bundle": "Bundle Ngọc Lục Bảo",
          "item.bundleupgrade.diamond_bundle": "Bundle Kim Cương",
          "tooltip.bundleupgrade.capacity": "%s / %s",
          "tooltip.bundleupgrade.capacity_units": "%s / %s Đơn vị",
          "tooltip.bundleupgrade.upgrade_info": "Giữ nguyên 100% vật phẩm khi nâng cấp"
        }
        """;
        writeFile(new File(langDir, "vi_vn.json"), viVn);
    }

    private static void generateRecipes() throws IOException {
        File recipeDir = new File(DATA_DIR, "bundleupgrade/recipe");
        recipeDir.mkdirs();

        String[][] recipes = {
            {"copper_bundle", "minecraft:bundle", "minecraft:copper_ingot"},
            {"iron_bundle", "bundleupgrade:copper_bundle", "minecraft:iron_ingot"},
            {"gold_bundle", "bundleupgrade:iron_bundle", "minecraft:gold_ingot"},
            {"emerald_bundle", "bundleupgrade:gold_bundle", "minecraft:emerald"},
            {"diamond_bundle", "bundleupgrade:emerald_bundle", "minecraft:diamond"}
        };

        for (String[] r : recipes) {
            String result = r[0];
            String center = r[1];
            String material = r[2];

            String recipeJson = """
            {
              "type": "minecraft:crafting_shaped",
              "category": "equipment",
              "key": {
                "#": "%s",
                "B": "%s"
              },
              "pattern": [
                "###",
                "#B#",
                "###"
              ],
              "result": {
                "count": 1,
                "id": "bundleupgrade:%s"
              }
            }
            """.formatted(material, center, result);
            writeFile(new File(recipeDir, result + ".json"), recipeJson);
        }
    }

    private static void generateTags() throws IOException {
        File tagsDir = new File(DATA_DIR, "minecraft/tags/item");
        tagsDir.mkdirs();

        String tagJson = """
        {
          "replace": false,
          "values": [
            "bundleupgrade:copper_bundle",
            "bundleupgrade:iron_bundle",
            "bundleupgrade:gold_bundle",
            "bundleupgrade:emerald_bundle",
            "bundleupgrade:diamond_bundle"
          ]
        }
        """;
        writeFile(new File(tagsDir, "bundles.json"), tagJson);
    }

    private static void writeFile(File file, String content) throws IOException {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content.strip() + "\n");
        }
    }
}

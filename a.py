from pathlib import Path
import json
from itertools import combinations, product

MODID = "visualizer"

PROJECT_ROOT = Path(__file__).resolve().parent
RES_ROOT = PROJECT_ROOT / "src" / "main" / "resources"

MINECRAFT_MODEL_DIR = RES_ROOT / "assets" / "minecraft" / "models" / "item"
MOD_MODEL_DIR = RES_ROOT / "assets" / MODID / "models" / "item"

SLOT_ORDER = ["top_left", "top_right", "left", "right"]

SLOT_TEXTURE_NAME = {
    "left": "left",
    "right": "right",
    "top_left": "top_left",
    "top_right": "top_right",
}

SLOT_PROPERTY_NAME = {
    "left": f"{MODID}:left_badge_level",
    "right": f"{MODID}:right_badge_level",
    "top_left": f"{MODID}:top_left_badge_level",
    "top_right": f"{MODID}:top_right_badge_level",
}

ITEMS = [
    {"name": "diamond_sword", "parent": "minecraft:item/handheld", "slots": {"left": 5, "right": 2, "top_left": 5}},
    {"name": "iron_sword", "parent": "minecraft:item/handheld", "slots": {"left": 5, "right": 2, "top_left": 5}},
    {"name": "golden_sword", "parent": "minecraft:item/handheld", "slots": {"left": 5, "right": 2, "top_left": 5}},

    {"name": "diamond_pickaxe", "parent": "minecraft:item/handheld", "slots": {"left": 5, "right": 3}},
    {"name": "iron_pickaxe", "parent": "minecraft:item/handheld", "slots": {"left": 5, "right": 3}},
    {"name": "golden_pickaxe", "parent": "minecraft:item/handheld", "slots": {"left": 5, "right": 3}},
    {"name": "diamond_shovel", "parent": "minecraft:item/handheld", "slots": {"left": 5, "right": 3}},
    {"name": "iron_shovel", "parent": "minecraft:item/handheld", "slots": {"left": 5, "right": 3}},
    {"name": "golden_shovel", "parent": "minecraft:item/handheld", "slots": {"left": 5, "right": 3}},

    {"name": "bow", "parent": "minecraft:item/generated", "slots": {"left": 5, "right": 1, "top_left": 5}},

    {"name": "enchanted_book", "parent": "minecraft:item/generated", "slots": {"left": 5, "right": 5, "top_left": 5, "top_right": 2}},

    {"name": "iron_helmet", "parent": "minecraft:item/generated", "slots": {"left": 4, "right": 3}},
    {"name": "iron_chestplate", "parent": "minecraft:item/generated", "slots": {"left": 4, "right": 3}},
    {"name": "iron_leggings", "parent": "minecraft:item/generated", "slots": {"left": 4, "right": 3}},
    {"name": "iron_boots", "parent": "minecraft:item/generated", "slots": {"left": 4, "right": 3}},

    {"name": "diamond_helmet", "parent": "minecraft:item/generated", "slots": {"left": 4, "right": 3}},
    {"name": "diamond_chestplate", "parent": "minecraft:item/generated", "slots": {"left": 4, "right": 3}},
    {"name": "diamond_leggings", "parent": "minecraft:item/generated", "slots": {"left": 4, "right": 3}},
    {"name": "diamond_boots", "parent": "minecraft:item/generated", "slots": {"left": 4, "right": 3}},
]


def write_json(path: Path, data: dict):
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(json.dumps(data, ensure_ascii=False, indent=2), encoding="utf-8")


def get_book_base_key(slot_levels: dict):
    # 優先順位: fireaspect > protection > sharpness > efficiency
    if slot_levels.get("top_right", 0) >= 1:
        return "fireaspect"
    if slot_levels.get("right", 0) >= 1:
        return "protection"
    if slot_levels.get("left", 0) >= 1:
        return "sharpness"
    if slot_levels.get("top_left", 0) >= 1:
        return "efficiency"
    return None


def get_base_texture(item_name: str, slot_levels: dict):
    if item_name == "enchanted_book":
        base_key = get_book_base_key(slot_levels)
        if base_key is not None:
            return f"{MODID}:item/enchanted_book_{base_key}"
    return f"minecraft:item/{item_name}"


def make_model(parent: str, item_name: str, slot_levels: dict):
    textures = {"layer0": get_base_texture(item_name, slot_levels)}
    layer_index = 1

    for slot in SLOT_ORDER:
        if slot in slot_levels:
            level = slot_levels[slot]
            textures[f"layer{layer_index}"] = f"{MODID}:item/{SLOT_TEXTURE_NAME[slot]}_{level}"
            layer_index += 1

    return {
        "parent": parent,
        "textures": textures
    }


def make_model_name(item_name: str, slot_levels: dict):
    parts = [item_name]

    if item_name == "enchanted_book":
        base_key = get_book_base_key(slot_levels)
        if base_key is not None:
            parts.append(base_key)

    if "top_left" in slot_levels:
        parts.append(f"tl{slot_levels['top_left']}")
    if "top_right" in slot_levels:
        parts.append(f"tr{slot_levels['top_right']}")
    if "left" in slot_levels:
        parts.append(f"l{slot_levels['left']}")
    if "right" in slot_levels:
        parts.append(f"r{slot_levels['right']}")

    return "_".join(parts)


def generate_for_item(item_name: str, parent: str, slots: dict):
    overrides = []
    available_slots = [slot for slot in SLOT_ORDER if slot in slots]

    for size in range(1, len(available_slots) + 1):
        for slot_combo in combinations(available_slots, size):
            ranges = [range(1, slots[slot] + 1) for slot in slot_combo]

            for levels in product(*ranges):
                slot_levels = dict(zip(slot_combo, levels))
                model_name = make_model_name(item_name, slot_levels)

                predicate = {}
                for slot, level in slot_levels.items():
                    predicate[SLOT_PROPERTY_NAME[slot]] = level

                overrides.append({
                    "predicate": predicate,
                    "model": f"{MODID}:item/{model_name}"
                })

                write_json(
                    MOD_MODEL_DIR / f"{model_name}.json",
                    make_model(parent, item_name, slot_levels)
                )

    root_model = {
        "parent": parent,
        "textures": {
            "layer0": f"minecraft:item/{item_name}"
        },
        "overrides": overrides
    }

    write_json(MINECRAFT_MODEL_DIR / f"{item_name}.json", root_model)


def main():
    MINECRAFT_MODEL_DIR.mkdir(parents=True, exist_ok=True)
    MOD_MODEL_DIR.mkdir(parents=True, exist_ok=True)

    for item in ITEMS:
        generate_for_item(item["name"], item["parent"], item["slots"])

    print("生成完了")


if __name__ == "__main__":
    main()
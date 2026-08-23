#!/usr/bin/env python3
"""Migrate Forge package roots to their NeoForge 1.21.1 locations."""

from __future__ import annotations

import argparse
from pathlib import Path


SOURCE_ROOTS = (Path("common/src/main/java"), Path("forge/src/main/java"))

PREFIXES = (
    ("net.minecraftforge.api.", "net.neoforged.api."),
    ("net.minecraftforge.eventbus.api.", "net.neoforged.bus.api."),
    ("net.minecraftforge.fml.", "net.neoforged.fml."),
    ("net.minecraftforge.forgespi.", "net.neoforged.neoforgespi."),
    ("net.minecraftforge.client.", "net.neoforged.neoforge.client."),
    ("net.minecraftforge.common.", "net.neoforged.neoforge.common."),
    ("net.minecraftforge.energy.", "net.neoforged.neoforge.energy."),
    ("net.minecraftforge.event.", "net.neoforged.neoforge.event."),
    ("net.minecraftforge.network.", "net.neoforged.neoforge.network."),
    ("net.minecraftforge.registries.", "net.neoforged.neoforge.registries."),
    ("net.minecraftforge.resource.", "net.neoforged.neoforge.resource."),
)

EXACT_IMPORTS = (
    (
        "import net.neoforged.neoforge.common.ForgeConfigSpec;",
        "import net.neoforged.neoforge.common.ModConfigSpec;",
    ),
    (
        "import net.neoforged.neoforge.common.MinecraftForge;",
        "import net.neoforged.neoforge.common.NeoForge;",
    ),
    (
        "import net.neoforged.neoforge.resource.PathPackResources;",
        "import net.minecraft.server.packs.PathPackResources;",
    ),
)

SYMBOLS = (
    ("ForgeConfigSpec", "ModConfigSpec"),
    ("MinecraftForge", "NeoForge"),
)


def migrate(text: str) -> str:
    for old, new in PREFIXES:
        text = text.replace(old, new)
    for old, new in EXACT_IMPORTS:
        text = text.replace(old, new)
    for old, new in SYMBOLS:
        text = text.replace(old, new)
    return text


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--apply", action="store_true", help="write migrated sources")
    args = parser.parse_args()

    changed = 0
    for root in SOURCE_ROOTS:
        for path in sorted(root.rglob("*.java")):
            original = path.read_text(encoding="utf-8")
            migrated = migrate(original)
            if migrated == original:
                continue
            changed += 1
            if args.apply:
                path.write_text(migrated, encoding="utf-8")

    print(f"{'Migrated' if args.apply else 'Would migrate'} {changed} files")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())

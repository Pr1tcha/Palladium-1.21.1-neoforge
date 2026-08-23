#!/usr/bin/env python3
"""Migrate legacy ResourceLocation constructors to the Minecraft 1.21 API."""

from __future__ import annotations

import argparse
from pathlib import Path


NEEDLE = "new ResourceLocation"
SOURCE_ROOTS = (
    Path("common/src/main/java"),
    Path("forge/src/main/java"),
    Path("src/main/java"),
)


def quoted_end(text: str, start: int, quote: str) -> int:
    index = start + 1
    while index < len(text):
        if text[index] == "\\":
            index += 2
            continue
        if text[index] == quote:
            return index + 1
        index += 1
    raise ValueError(f"Unterminated {quote} literal")


def comment_end(text: str, start: int) -> int:
    if text.startswith("//", start):
        newline = text.find("\n", start + 2)
        return len(text) if newline < 0 else newline + 1
    end = text.find("*/", start + 2)
    if end < 0:
        raise ValueError("Unterminated block comment")
    return end + 2


def constructor_end(text: str, open_paren: int) -> tuple[int, bool]:
    depth = 0
    has_top_level_comma = False
    index = open_paren

    while index < len(text):
        if text.startswith("//", index) or text.startswith("/*", index):
            index = comment_end(text, index)
            continue
        if text[index] in ('"', "'"):
            index = quoted_end(text, index, text[index])
            continue
        if text[index] == "(":
            depth += 1
        elif text[index] == ")":
            depth -= 1
            if depth == 0:
                return index + 1, has_top_level_comma
        elif text[index] == "," and depth == 1:
            has_top_level_comma = True
        index += 1

    raise ValueError("Unterminated ResourceLocation constructor")


def migrate(text: str) -> tuple[str, int, int]:
    output: list[str] = []
    index = 0
    one_argument = 0
    two_argument = 0

    while index < len(text):
        if text.startswith("//", index) or text.startswith("/*", index):
            end = comment_end(text, index)
            output.append(text[index:end])
            index = end
            continue
        if text[index] in ('"', "'"):
            end = quoted_end(text, index, text[index])
            output.append(text[index:end])
            index = end
            continue
        if text.startswith(NEEDLE, index):
            open_paren = index + len(NEEDLE)
            while open_paren < len(text) and text[open_paren].isspace():
                open_paren += 1
            if open_paren < len(text) and text[open_paren] == "(":
                end, has_top_level_comma = constructor_end(text, open_paren)
                method = "fromNamespaceAndPath" if has_top_level_comma else "parse"
                output.append(f"ResourceLocation.{method}")
                output.append(text[open_paren:end])
                if has_top_level_comma:
                    two_argument += 1
                else:
                    one_argument += 1
                index = end
                continue
        output.append(text[index])
        index += 1

    return "".join(output), one_argument, two_argument


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--apply", action="store_true", help="write migrated sources")
    args = parser.parse_args()

    changed_files = 0
    one_argument = 0
    two_argument = 0

    for root in SOURCE_ROOTS:
        if not root.exists():
            continue
        for path in sorted(root.rglob("*.java")):
            original = path.read_text(encoding="utf-8")
            migrated, one_count, two_count = migrate(original)
            if migrated == original:
                continue
            changed_files += 1
            one_argument += one_count
            two_argument += two_count
            if args.apply:
                path.write_text(migrated, encoding="utf-8")

    action = "Migrated" if args.apply else "Would migrate"
    print(
        f"{action} {changed_files} files: "
        f"{one_argument} one-argument and {two_argument} two-argument constructors"
    )
    return 0


if __name__ == "__main__":
    raise SystemExit(main())

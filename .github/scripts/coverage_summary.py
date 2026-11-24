"""Summarise Kover (JaCoCo-format) XML reports as a Markdown table."""
from __future__ import annotations

import argparse
import sys
import xml.etree.ElementTree as ET

MARKER = "<!-- kover-coverage -->"


def read_line_counts(path: str) -> tuple[int, int]:
    """Return (covered, missed) of the report-level LINE counter, (0, 0) if absent."""
    root = ET.parse(path).getroot()
    for counter in root.findall("counter"):
        if counter.get("type") == "LINE":
            return int(counter.get("covered", "0")), int(counter.get("missed", "0"))
    return 0, 0


def _percent(covered: int, missed: int) -> str:
    total = covered + missed
    if total == 0:
        return "n/a"
    return f"{covered / total * 100:.1f}%"


def render(total: tuple[int, int], modules: list[tuple[str, int, int]]) -> str:
    lines = [
        MARKER,
        "### Unit test coverage",
        "",
        "| Module | Line coverage | Covered / total lines |",
        "|---|---:|---:|",
    ]
    for name, covered, missed in modules:
        lines.append(f"| {name} | {_percent(covered, missed)} | {covered} / {covered + missed} |")
    t_cov, t_miss = total
    lines.append(
        f"| **Total** | **{_percent(t_cov, t_miss)}** | **{t_cov} / {t_cov + t_miss}** |"
    )
    lines += [
        "",
        "Full HTML and XML reports are in the `coverage-report` artifact of this run.",
    ]
    return "\n".join(lines) + "\n"


def main(argv: list[str]) -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--total", required=True, help="merged Kover XML report")
    parser.add_argument(
        "--module", action="append", default=[], metavar="NAME=XML", help="per-module XML report"
    )
    args = parser.parse_args(argv)

    try:
        total = read_line_counts(args.total)
        modules = []
        for spec in args.module:
            name, sep, path = spec.partition("=")
            if not sep or not name:
                parser.error(f"--module expects NAME=XML, got '{spec}'")
            covered, missed = read_line_counts(path)
            modules.append((name, covered, missed))
    except FileNotFoundError as e:
        print(f"Coverage report not found: {e.filename}", file=sys.stderr)
        return 1

    sys.stdout.write(render(total, modules))
    return 0


if __name__ == "__main__":
    sys.exit(main(sys.argv[1:]))

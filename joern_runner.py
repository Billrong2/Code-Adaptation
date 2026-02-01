from __future__ import annotations

from dataclasses import dataclass
from pathlib import Path
import hashlib
import shutil
import subprocess
import tempfile
from typing import Iterable


@dataclass(frozen=True)
class JoernRunner:
    joern_bin_dir: Path

    def export(self, code: str, destination: Path | None) -> None:
        if code.strip() == "":
            return

        tmp_dir = tempfile.TemporaryDirectory()
        try:
            md5_v = hashlib.md5(code.encode()).hexdigest()
            short_filename = f"func_{md5_v}.java"
            temp_path = Path(tmp_dir.name)
            (temp_path / short_filename).write_text(code, encoding="utf-8")

            joern_export = self.joern_bin_dir / "joern-export"
            subprocess.run([str(joern_export), "--repr", "pdg", "--out", str(temp_path / "output")],
                           capture_output=True, text=True)
            subprocess.run([str(joern_export), "--repr", "all", "--out", str(temp_path / "label")],
                           capture_output=True, text=True)
            subprocess.run(
                [str(joern_export), "--repr", "all", "--format=graphml", "--out", str(temp_path / "code")],
                capture_output=True,
                text=True,
            )

            if destination is not None:
                self._copy_outputs(temp_path, Path(destination))
        finally:
            tmp_dir.cleanup()

    def export_with_fallback(self, code: str, destination: Path, fallback_wrappers: Iterable[str]) -> None:
        self.export(code, destination)
        if self._has_output(destination / "out" / "output"):
            return
        for wrapper in fallback_wrappers:
            wrapped_code = wrapper.replace("{code}", code)
            self.export(wrapped_code, destination)
            if self._has_output(destination / "out" / "output"):
                return

    @staticmethod
    def strip_markdown_fence(code: str) -> str:
        if "```" not in code:
            return code
        lines = code.splitlines()
        stripped = []
        in_block = False
        for line in lines:
            if line.strip().startswith("```"):
                in_block = not in_block
                continue
            if in_block:
                stripped.append(line)
        return "\n".join(stripped) if stripped else code

    @staticmethod
    def _has_output(path: Path) -> bool:
        return path.exists() and any(path.iterdir())

    @staticmethod
    def has_output(path: Path) -> bool:
        return JoernRunner._has_output(path)

    @staticmethod
    def _copy_outputs(temp_dir: Path, destination: Path) -> None:
        for directory in ("output", "label"):
            out_dir = destination / "out" / directory
            out_dir.parent.mkdir(parents=True, exist_ok=True)
            shutil.copytree(temp_dir / directory, out_dir, dirs_exist_ok=True)
        for directory in ("output", "code"):
            out_dir = destination / "format=graphml" / directory
            out_dir.parent.mkdir(parents=True, exist_ok=True)
            shutil.copytree(temp_dir / directory, out_dir, dirs_exist_ok=True)

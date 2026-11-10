"""Build a small, downscaled BSTD sample for the Android OCR spike (step 1.4).

Reads data/bstd/detection/detection/BSTD_v17.57.json, picks test-split images that
contain Hindi and/or English words, downscales them, and writes
data/bstd/sample/{images/*.jpg, sample.json}. Polygons are scaled to match.

Run from the repo root:
    uv run --with pillow python tools/make_bstd_sample.py
"""

import json
import random
from pathlib import Path

from PIL import Image

ROOT = Path(__file__).resolve().parent.parent
SRC = ROOT / "data/bstd/detection/detection"
OUT = ROOT / "data/bstd/sample"

SEED = 42
N_HINDI = 100  # images containing at least one Hindi word
N_ENGLISH_ONLY = 100  # images with English words and no Hindi word
LONG_SIDE = 1600
KEEP = {"hindi", "english"}


def main() -> None:
    data = json.loads((SRC / "BSTD_v17.57.json").read_text())
    rng = random.Random(SEED)

    hindi, english_only = [], []
    for key, e in sorted(data.items()):
        if e["split"] != "test":
            continue
        scripts = {a["script_language"] for a in e["annotations"].values()}
        if "hindi" in scripts:
            hindi.append(key)
        elif "english" in scripts:
            english_only.append(key)

    print(f"test images: with hindi={len(hindi)}, english only={len(english_only)}")
    rng.shuffle(hindi)
    rng.shuffle(english_only)

    picked: list[str] = []
    skipped_missing = 0
    rotated = 0
    (OUT / "images").mkdir(parents=True, exist_ok=True)
    records = []

    for pool, want in ((hindi, N_HINDI), (english_only, N_ENGLISH_ONLY)):
        got = 0
        for key in pool:
            if got >= want:
                break
            e = data[key]
            src = SRC / e["image_name"]  # already includes the folder, e.g. "A/image_1005.jpg"
            if not src.exists():
                skipped_missing += 1
                continue
            with Image.open(src) as im:
                orient = im.getexif().get(274, 1)
                if orient != 1:
                    rotated += 1
                w, h = im.size
                scale = min(1.0, LONG_SIDE / max(w, h))
                nw, nh = round(w * scale), round(h * scale)
                small = im.convert("RGB").resize((nw, nh), Image.LANCZOS) if scale < 1 else im.convert("RGB")
                name = f"{key}.jpg"
                small.save(OUT / "images" / name, quality=85)
            words = [
                {
                    "text": a["text"],
                    "script": a["script_language"],
                    "polygon": [[round(x * scale), round(y * scale)] for x, y in a["coordinates"]],
                }
                for a in e["annotations"].values()
            ]
            records.append(
                {
                    "id": key,
                    "file": name,
                    "width": nw,
                    "height": nh,
                    "environment": e.get("environment"),
                    "lighting": e.get("lighting"),
                    "has_hindi": any(x["script"] == "hindi" for x in words),
                    "words": words,
                }
            )
            picked.append(key)
            got += 1

    (OUT / "sample.json").write_text(json.dumps({"seed": SEED, "keep_scripts": sorted(KEEP), "images": records}, ensure_ascii=False))
    n_words = sum(1 for r in records for w in r["words"] if w["script"] in KEEP)
    print(f"wrote {len(records)} images, {n_words} hindi/english words")
    print(f"skipped (file not found): {skipped_missing}; with EXIF rotation tag: {rotated}")


if __name__ == "__main__":
    main()

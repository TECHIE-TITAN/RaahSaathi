"""Deterministic stand-ins so the API and tests run with no models or accounts."""

import io
import wave

from .base import Language, Transcript


class FakeAsr:
    name = "fake"

    async def transcribe(self, audio: bytes, language_hint: Language | None) -> Transcript:
        return Transcript(
            text=f"[fake transcript of {len(audio)} bytes]",
            language=language_hint or "en",
            confidence=1.0,
        )


class FakeTranslation:
    name = "fake"

    async def translate(self, text: str, source: Language, target: Language) -> str:
        return f"[{source}->{target}] {text}"


class FakeTts:
    name = "fake"

    async def synthesize(self, text: str, language: Language) -> bytes:
        buf = io.BytesIO()
        with wave.open(buf, "wb") as w:
            w.setnchannels(1)
            w.setsampwidth(2)
            w.setframerate(16000)
            w.writeframes(b"\x00\x00" * 1600)  # 0.1 s of silence
        return buf.getvalue()

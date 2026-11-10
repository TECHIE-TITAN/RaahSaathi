"""Provider interfaces. Every language engine sits behind one of these (ADR-003)."""

from typing import Protocol

from pydantic import BaseModel

# Languages supported at launch (ADR-013). "hi-en" is code-mixed Hindi-English.
Language = str


class Transcript(BaseModel):
    text: str
    language: Language
    confidence: float | None = None


class AsrProvider(Protocol):
    name: str

    async def transcribe(self, audio: bytes, language_hint: Language | None) -> Transcript: ...


class TranslationProvider(Protocol):
    name: str

    async def translate(self, text: str, source: Language, target: Language) -> str: ...


class TtsProvider(Protocol):
    name: str

    async def synthesize(self, text: str, language: Language) -> bytes:
        """Return WAV bytes."""
        ...

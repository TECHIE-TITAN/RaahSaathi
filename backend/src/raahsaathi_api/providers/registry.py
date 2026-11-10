"""Maps configured provider names to adapter instances."""

from collections.abc import Callable

from .base import AsrProvider, TranslationProvider, TtsProvider
from .fake import FakeAsr, FakeTranslation, FakeTts

_ASR: dict[str, Callable[[], AsrProvider]] = {"fake": FakeAsr}
_TRANSLATION: dict[str, Callable[[], TranslationProvider]] = {"fake": FakeTranslation}
_TTS: dict[str, Callable[[], TtsProvider]] = {"fake": FakeTts}


def _pick[T](table: dict[str, Callable[[], T]], kind: str, name: str) -> T:
    try:
        return table[name]()
    except KeyError:
        known = ", ".join(sorted(table))
        raise ValueError(f"Unknown {kind} provider '{name}'. Known: {known}") from None


def get_asr(name: str) -> AsrProvider:
    return _pick(_ASR, "ASR", name)


def get_translation(name: str) -> TranslationProvider:
    return _pick(_TRANSLATION, "translation", name)


def get_tts(name: str) -> TtsProvider:
    return _pick(_TTS, "TTS", name)

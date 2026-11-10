import pytest
from fastapi.testclient import TestClient

from raahsaathi_api.app import app
from raahsaathi_api.providers import registry

client = TestClient(app)


def test_health():
    r = client.get("/health")
    assert r.status_code == 200
    assert r.json()["status"] == "ok"


def test_translate_uses_fake_provider():
    r = client.post("/v1/translate", json={"text": "namaste", "source": "hi", "target": "en"})
    assert r.status_code == 200
    assert r.json()["text"] == "[hi->en] namaste"


def test_transcribe_returns_language_hint():
    r = client.post(
        "/v1/transcribe",
        files={"audio": ("a.wav", b"12345", "audio/wav")},
        data={"language_hint": "hi"},
    )
    assert r.status_code == 200
    assert r.json()["language"] == "hi"


def test_tts_returns_wav():
    r = client.post("/v1/tts", json={"text": "hello", "language": "en"})
    assert r.status_code == 200
    assert r.headers["content-type"] == "audio/wav"
    assert r.content[:4] == b"RIFF"


def test_unknown_provider_is_rejected():
    with pytest.raises(ValueError, match="Unknown ASR provider"):
        registry.get_asr("nope")

from typing import Annotated

from fastapi import Depends, FastAPI, File, Form, UploadFile
from fastapi.responses import Response
from pydantic import BaseModel

from .config import Settings, get_settings
from .providers import registry
from .providers.base import Transcript

app = FastAPI(title="Raahsaathi backend")

SettingsDep = Annotated[Settings, Depends(get_settings)]


class TranslateRequest(BaseModel):
    text: str
    source: str
    target: str


class TranslateResponse(BaseModel):
    text: str


class TtsRequest(BaseModel):
    text: str
    language: str


@app.get("/health")
def health(settings: SettingsDep) -> dict[str, str]:
    return {
        "status": "ok",
        "asr": settings.asr_provider,
        "translation": settings.translation_provider,
        "tts": settings.tts_provider,
    }


@app.post("/v1/transcribe")
async def transcribe(
    audio: Annotated[UploadFile, File()],
    settings: SettingsDep,
    language_hint: Annotated[str | None, Form()] = None,
) -> Transcript:
    provider = registry.get_asr(settings.asr_provider)
    return await provider.transcribe(await audio.read(), language_hint)


@app.post("/v1/translate")
async def translate(body: TranslateRequest, settings: SettingsDep) -> TranslateResponse:
    provider = registry.get_translation(settings.translation_provider)
    return TranslateResponse(text=await provider.translate(body.text, body.source, body.target))


@app.post("/v1/tts")
async def tts(body: TtsRequest, settings: SettingsDep) -> Response:
    provider = registry.get_tts(settings.tts_provider)
    return Response(await provider.synthesize(body.text, body.language), media_type="audio/wav")

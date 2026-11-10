from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    """Runtime settings, read from the environment and `backend/.env`.

    Provider names select an adapter in `backend.providers.registry`.
    Only `fake` exists until real providers are added (ADR-016).
    """

    model_config = SettingsConfigDict(env_file=".env", extra="ignore")

    asr_provider: str = "fake"
    translation_provider: str = "fake"
    tts_provider: str = "fake"

    bhashini_user_id: str = ""
    bhashini_api_key: str = ""


def get_settings() -> Settings:
    return Settings()

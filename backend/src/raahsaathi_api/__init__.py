def main() -> None:
    import uvicorn

    uvicorn.run("raahsaathi_api.app:app", host="127.0.0.1", port=8000)

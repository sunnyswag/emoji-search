import os

SERVER_DIR = os.path.dirname(os.path.abspath(__file__))
EMOJI_DATA_DIR = os.path.join(SERVER_DIR, "emoji_data")
EMBEDDING_DATA_DIR = os.path.join(EMOJI_DATA_DIR, "json_emoji_embeddings")

OPENAI_URL = "https://api.openai.com/v1"
EMBEDDING_MODEL = "text-embedding-ada-002"
API_KEY = os.environ["OPENAI_API_KEY"]
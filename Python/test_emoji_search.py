import gzip
import os

import jsonlines
import numpy as np
import openai

openai.api_key = os.environ["OPENAI_API_KEY"]
openai.api_base = "https://api.openai.com/v1"
EMBEDDING_MODEL = "text-embedding-ada-002"

SERVER_DIR = os.path.dirname(os.path.abspath(__file__))
EMBED_FILE = os.path.join(SERVER_DIR, os.path.join("emoji_data", "emoji-embeddings.json.gz"))

class EmojiSearchApp:
    def __init__(self):
        self._emojis = None
        self._embeddings = None

    def _load_emoji_embeddings(self):
        if self._emojis is not None and self._embeddings is not None:
            return

        with gzip.GzipFile(fileobj=open(EMBED_FILE, "rb"), mode="rb") as fin:
            emoji_info = list(jsonlines.Reader(fin))

        print("Lazy loading embedding info ...")
        self._emojis = [(x["emoji"], x["message"]) for x in emoji_info]
        self._embeddings = [x["embed"] for x in emoji_info]
        assert self._emojis is not None and self._embeddings is not None

    @property
    def emojis(self):
        if self._emojis is None:
            self._load_emoji_embeddings()
        return self._emojis

    @property
    def embeddings(self):
        if self._embeddings is None:
            self._load_emoji_embeddings()
        return self._embeddings

    def get_openai_embedding(self, text: str) -> list[float]:
        result = openai.Embedding.create(input=text, model=EMBEDDING_MODEL)
        return result["data"][0]["embedding"]

    def get_top_relevant_emojis(self, query: str, k: int = 20) -> list[dict]:
        query_embed = self.get_openai_embedding(query)
        dotprod = np.matmul(self.embeddings, np.array(query_embed).T)
        m_dotprod = np.median(dotprod)
        ind = np.argpartition(dotprod, -k)[-k:]
        ind = ind[np.argsort(dotprod[ind])][::-1]
        result = [
            {
                "emoji": self.emojis[i][0],
                "message": self.emojis[i][1].capitalize(),
                "score": (dotprod[i] - m_dotprod) * 100,
            }
            for i in ind
        ]
        return result
    
def main():
    query = "I love you with all my heart"
    emoji_search_app = EmojiSearchApp()
    result = emoji_search_app.get_top_relevant_emojis(query, k=5)
    print("result: ", result)

    ### result:
    # [{'emoji': '❤', 'message': 'Heavy black heart', 'score': 6.948441071955602}, 
    # {'emoji': '❣️', 'message': 'Heart exclamation', 'score': 6.946779498820799}, 
    # {'emoji': '💘', 'message': 'Heart with arrow', 'score': 6.945987817009569}, 
    # {'emoji': '❤\u200d🔥', 'message': 'Heart on fire', 'score': 6.713515167594375}, 
    # {'emoji': '💝', 'message': 'Heart with ribbon', 'score': 6.712579726748991}]

if __name__ == "__main__":
	main()
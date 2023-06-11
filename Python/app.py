import gzip
import os
from typing import List

import jsonlines
import numpy as np
from flask import Flask, jsonify, request
from flask_cors import CORS
from transformers import MobileBertTokenizer, MobileBertModel
import torch

SERVER_DIR = os.path.dirname(os.path.abspath(__file__))
EMBED_FILE = os.path.join(SERVER_DIR, "emoji_data/emoji-embeddings.json.gz")

class EmojiSearchApp:
    def __init__(self):
        self._emojis = None
        self._embeddings = None
        self._tokenizer = None
        self._model = None

    def _load_emoji_embeddings(self):
        if self._emojis is not None and self._embeddings is not None:
            return

        with gzip.GzipFile(fileobj=open(EMBED_FILE, "rb"), mode="rb") as fin:
            emoji_info = list(jsonlines.Reader(fin))

        print("Lazy loading embedding info ...")
        self._emojis = [(x["emoji"], x["message"]) for x in emoji_info]
        self._embeddings = [x["embed"] for x in emoji_info]
        assert self._emojis is not None and self._embeddings is not None

    def _init_tokenizer_and_model(self):
        self._tokenizer = MobileBertTokenizer.from_pretrained('google/mobilebert-uncased')
        self._model = MobileBertModel.from_pretrained('google/mobilebert-uncased')

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

    @property
    def tokenizer(self):
        if self._tokenizer is None:
            self._init_tokenizer_and_model()
        return self._tokenizer
    
    @property
    def model(self):
        if self._model is None:
            self._init_tokenizer_and_model()
        return self._model

    def get_emoji_embedding(self, text: str) -> List[float]:
        encodings = self.tokenizer.encode_plus(text, add_special_tokens=True, return_tensors='pt')
        print("get_emoji_embedding, encodings: ", encodings['input_ids'])
        input_ids = torch.tensor(encodings['input_ids'])
        with torch.no_grad():
            outputs = self.model(input_ids)
            
        sequence_output = outputs[0]
        return sequence_output[0, 0, :].numpy()

    def get_top_relevant_emojis(self, query: str, k: int = 20) -> List[dict]:
        query_embed = self.get_emoji_embedding(query)
        print("get_top_relevant_emojis, query_embed: ", query_embed, "shape of query_embed: ", query_embed.shape)
        print("get_top_relevant_emojis, shape of embeddings: ", np.array(self.embeddings).shape)
        dotprod = np.matmul(self.embeddings, query_embed)
        m_dotprod = np.median(dotprod)
        print("get_top_relevant_emojis, len(dotprod): ", len(dotprod), "shape of dotprod: ", dotprod.shape)
        ind = np.argpartition(dotprod, -k)[-k:]
        ind = ind[np.argsort(dotprod[ind])][::-1]
        print("get_top_relevant_emojis, ind: ", ind)
        result = [
            {
                "emoji": self.emojis[i][0],
                "message": self.emojis[i][1].capitalize(),
                "score": (dotprod[i] - m_dotprod) * 100,
            }
            for i in ind
        ]
        return result


# app = Flask(__name__)
# emoji_search_app = EmojiSearchApp()
# CORS(app, support_credentials=True)

# @app.route("/search", methods=["POST"])
# def search():
#     error = None
#     result = []

#     query = request.get_json().get("query")
#     try:
#         result = emoji_search_app.get_top_relevant_emojis(query, k=20)
#     except Exception as err:
#         error = str(err)
#     return jsonify(error=error, result=result)

# @app.route("/")
# def index():
#     return 'Hello World!'

# app.run()

def main():
    query = "Goodbye emoji"
    emoji_search_app = EmojiSearchApp()
    result = emoji_search_app.get_top_relevant_emojis(query, k=5)
    print("result: ", result)

if __name__ == "__main__":
	main()
import gzip
import json
import os
from typing import Dict, List
import math

import emoji
import openai
import tqdm
import emoji_data.proto_emoji_embeddings.emoji_embedding_pb2 as pb2

openai.api_key = os.environ["OPENAI_API_KEY"]
openai.api_base = "https://api.openai.com/v1"
EMBEDDING_MODEL = "text-embedding-ada-002"

ROOT_DIR = os.path.abspath(os.path.join(os.path.dirname(os.path.abspath(__file__)), ".."))
EMOJI_DATA_DIR = os.path.join(ROOT_DIR, "Python", "emoji_data")
JSON_DATA_DIR = os.path.join(EMOJI_DATA_DIR, "json_emoji_embeddings")
PROTO_DATA_DIR = os.path.join(EMOJI_DATA_DIR, "proto_emoji_embeddings")

def get_embeddings(inps: List[str], batch: int=1000, inp_type: str="doc") -> List[List[float]]:
	i = 0
	outputs = []
	while i < len(inps):
		result = openai.Embedding.create(input=inps[i:i+batch], model=EMBEDDING_MODEL)
		outputs += [x["embedding"] for x in result['data']]
		i += batch
	assert len(outputs) == len(inps)
	return outputs

def write_to_json_with_chunks(filename_base: str, data: List[Dict], num_files: int = 5):
	chunk_size = math.ceil(len(data) / num_files)
	chunks = [data[i:i + chunk_size] for i in range(0, len(data), chunk_size)]

	for i, chunk in enumerate(chunks):
		filename = f"{filename_base}_{i}.gz"
		with open(filename, "wb") as fp:
			with gzip.GzipFile(fileobj=fp, mode="wb") as gz:
				for x in tqdm.tqdm(chunk):
					gz.write((json.dumps(x) + "\n").encode("utf-8"))

def write_to_json(filename: str, data: List[Dict]):
    with open(f"{filename}.gz", "wb") as fp:
        with gzip.GzipFile(fileobj=fp, mode="wb") as gz:
            for x in tqdm.tqdm(data):
                gz.write((json.dumps(x) + "\n").encode("utf-8"))

def write_to_proto(filename: str, data: List[Dict]):
	output_messages = []

	for item in data:
		msg = pb2.EmojiEmbedding()
		msg.emoji = item["emoji"]
		msg.message = item["message"]
		msg.embed.extend(item["embed"])
		output_messages.append(msg.SerializeToString())

	with gzip.open(f"{filename}.gz", "wb") as f:
		for message in output_messages:
			f.write(len(message).to_bytes(4, 'big'))
			f.write(message)

def extract_emoji_messages() -> Dict[str, str]:
	with open(os.path.join(EMOJI_DATA_DIR, "emoji-data.txt"), encoding="utf-8") as file:
		data = file.readlines()
		data = [x.split("\t") for x in data if not x.startswith("#")]
		emojis_msg = {
			x[-1].split("(")[1].split(")")[0]: x[-1].split("(")[1].split(")")[1].lower().strip() 
			for x in data
		}

	# Combine two sources of emoji messages
	emojis_dict = emoji.EMOJI_DATA
	emojis_full_msg = {}
	for k in emojis_dict:
		msg = emojis_dict[k]['en'].strip(":").replace("_", " ").replace("-", " ").lower()
		other_msg = emojis_msg.get(k)
		if other_msg and len(other_msg) > len(msg):
			msg = other_msg
		emojis_full_msg[k] = msg

	# remove the duplicates values
	emojis_full_msg_dedupe = {v: k for k, v in emojis_full_msg.items()}
	emoji_dict_swap = {v: k for k, v in emojis_full_msg_dedupe.items()}
	return emoji_dict_swap

def main():
	# Query embeddings
	emoji_messages = extract_emoji_messages()
	descriptions = [f"The emoji {em} is about {msg}." for em, msg in emoji_messages.items()]
	print("first 5th descriptions of emoji: ", descriptions[:5], "\nlen descriptions: ", len(descriptions))

	embeddings = get_embeddings(descriptions)
	print("first embeddings of emoji: ", embeddings[0], "\nlen embeddings: ", len(embeddings))

	# Save embeddings
	info = [
		{"emoji": em, "message": msg, "embed": embed} 
		for (em, msg), embed in zip(emoji_messages.items(), embeddings)
	]
	print("first 2th info of emoji: ", info[:2], "\nlen info: ", len(info))

	json_output_filename = os.path.join(JSON_DATA_DIR, "emoji_embeddings")
	write_to_json(json_output_filename, info)
	write_to_json_with_chunks(json_output_filename, info)

	proto_output_filename = os.path.join(PROTO_DATA_DIR, "emoji_embeddings")
	write_to_proto(proto_output_filename, info)


if __name__ == "__main__":
	main()
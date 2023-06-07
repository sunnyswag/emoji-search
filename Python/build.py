import gzip
import json
import os
from typing import Dict, List

import emoji
import tqdm
from transformers import MobileBertTokenizer, MobileBertModel
import torch

SERVER_DIR = os.path.abspath(os.path.join(os.path.dirname(os.path.abspath(__file__)), ".."))

def get_embeddings(inp: List[str]) -> List[List[float]]:
	tokenizer = MobileBertTokenizer.from_pretrained('google/mobilebert-uncased')
	model = MobileBertModel.from_pretrained('google/mobilebert-uncased')

	encodings = tokenizer.batch_encode_plus(inp, add_special_tokens=True, padding='longest')
	print("first encodings: ", encodings['input_ids'][0], "\nlen encodings: ", len(encodings['input_ids']))
	input_ids = torch.tensor(encodings['input_ids'])
	with torch.no_grad():
		outputs = model(input_ids)
		
	sequence_output = outputs[0]
	return sequence_output[:, 0, :].numpy().tolist()

def write_jsonl(filename: str, data: List[Dict]):
    assert filename.endswith(".jsonl.gz")
    with open(filename, "wb") as fp:
        with gzip.GzipFile(fileobj=fp, mode="wb") as gz:
            for x in tqdm.tqdm(data):
                gz.write((json.dumps(x) + "\n").encode("utf-8"))


def extract_emoji_messages() -> Dict[str, str]:
	data = open(os.path.join(SERVER_DIR, "Python/emoji-data.txt")).readlines()
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

	# # Save embeddings
	# info = [
	# 	{"emoji": em, "message": msg, "embed": embed} 
	# 	for (em, msg), embed in zip(emoji_messages.items(), embeddings)
	# ]
	# output_filename = os.path.join(SERVER_DIR, "emoji-embeddings.jsonl.gz")
	# write_jsonl(output_filename, info)


if __name__ == "__main__":
	main()

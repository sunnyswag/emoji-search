import gzip
import json
import os
from typing import Dict, List

import emoji
import tqdm
from transformers import BertTokenizer, BertModel
import torch

SERVER_DIR = os.path.abspath(os.path.join(os.path.dirname(os.path.abspath(__file__)), ".."))
MODEL_DIR = os.path.join(SERVER_DIR, "Python/model")
EMOJI_DATA_DIR = os.path.join(SERVER_DIR, "Python/emoji_data")

def get_embeddings(inps: List[str]) -> List[List[float]]:
	device = torch.device('cuda' if torch.cuda.is_available() else 'cpu')

	tokenizer = BertTokenizer.from_pretrained('bert-base-uncased')
	model = BertModel.from_pretrained('bert-base-uncased')
	model.to(device)

	result = []
	for inp in inps:
		encodings = tokenizer(inp, return_tensors="pt")
		encodings = {key: val.to(device) for key, val in encodings.items()}
		outputs = model(**encodings)
		sequence_output = outputs[0]
		embed = sequence_output[:, 0, :].detach().cpu().numpy().flatten().tolist()
		result.append(embed)

	return result

def write_to_json(filename: str, data: List[Dict]):
    assert filename.endswith(".json.gz")
    with open(filename, "wb") as fp:
        with gzip.GzipFile(fileobj=fp, mode="wb") as gz:
            for x in tqdm.tqdm(data):
                gz.write((json.dumps(x) + "\n").encode("utf-8"))

def extract_emoji_messages() -> Dict[str, str]:
	data = open(os.path.join(EMOJI_DATA_DIR, "emoji-data.txt")).readlines()
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

	output_filename = os.path.join(EMOJI_DATA_DIR, "emoji-embeddings.json.gz")
	write_to_json(output_filename, info)


if __name__ == "__main__":
	main()
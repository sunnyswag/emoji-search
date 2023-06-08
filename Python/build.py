import gzip
import json
import os
from typing import Dict, List
import shutil

import emoji
import tqdm
from transformers import MobileBertTokenizer, MobileBertModel
import torch

SERVER_DIR = os.path.abspath(os.path.join(os.path.dirname(os.path.abspath(__file__)), ".."))
MODEL_DIR = os.path.join(SERVER_DIR, "Python/model")
EMOJI_DATA_DIR = os.path.join(SERVER_DIR, "Python/emoji_data")

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

def export_embedding_model():
	class MobileBertModelSimpleOutput(MobileBertModel):
		def forward(self, input_ids=None, attention_mask=None, token_type_ids=None):
			outputs = super().forward(
				input_ids=input_ids,
				attention_mask=attention_mask,
				token_type_ids=token_type_ids
			)
			return outputs.last_hidden_state

	tokenizer = MobileBertTokenizer.from_pretrained('google/mobilebert-uncased')
	model = MobileBertModelSimpleOutput.from_pretrained('google/mobilebert-uncased')

	tokenizer.save_pretrained(os.path.join(MODEL_DIR, "mobilebert_tokenizer"))

	model.eval()
	example_input_text = "show me the emoji for love but not the one with the heart"
	encodings = tokenizer.encode_plus(example_input_text, max_length=16, padding='max_length', truncation=True, return_tensors='pt')
	traced_script_module = torch.jit.trace(model, (encodings['input_ids'], encodings['attention_mask'], encodings['token_type_ids']))
	traced_script_module.save(os.path.join(MODEL_DIR, "traced_mobilebert.pt"))

	with open(os.path.join(MODEL_DIR, "traced_mobilebert.pt"), 'rb') as f_in:
		with gzip.open(os.path.join(MODEL_DIR, "traced_mobilebert.pt.gz"), 'wb') as f_out:
			shutil.copyfileobj(f_in, f_out)

	os.remove(os.path.join(MODEL_DIR, "traced_mobilebert.pt"))

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

	export_embedding_model()


if __name__ == "__main__":
	main()
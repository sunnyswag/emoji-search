
import numpy as np
import joblib
import os
from typing import List
from sklearn.decomposition import PCA
from constants import EMBEDDING_DATA_DIR

def transform_by_pca(embeddings: List[List[float]], n_components: int = 100) -> List[List[float]]:
	pca = PCA(n_components=n_components)
	pca.fit(embeddings)

	save_pca_params(pca)
	print_explained_variance_ratio(pca)

	return pca.transform(embeddings)

def save_pca_params(pca):
	joblib.dump(pca, os.path.join(EMBEDDING_DATA_DIR, "pca_model.pkl"))
	
def load_pca_params():
	return joblib.load(os.path.join(EMBEDDING_DATA_DIR, "pca_model.pkl"))

def print_explained_variance_ratio(pca):
    cumulative_variance_ratio = np.cumsum(pca.explained_variance_ratio_)
    print("cumulative_variance_ratio: ", cumulative_variance_ratio)
import sqlite3 as sql
import nltk
import string
from nltk.corpus import wordnet
from nltk import word_tokenize, pos_tag
from nltk.stem import WordNetLemmatizer
from tqdm import tqdm


setDiff = set()
setMsg = set()


def get_wordnet_pos(treebank_tag):
    if treebank_tag.startswith('J'):
        return wordnet.ADJ
    elif treebank_tag.startswith('V'):
        return wordnet.VERB
    elif treebank_tag.startswith('N'):
        return wordnet.NOUN
    elif treebank_tag.startswith('R'):
        return wordnet.ADV
    else:
        return None


def lemmatize_sentence(sentence):
    res = []
    lemmatizer = WordNetLemmatizer()
    for word, pos in pos_tag(word_tokenize(sentence)):
        wordnet_pos = get_wordnet_pos(pos) or wordnet.NOUN
        res.append(lemmatizer.lemmatize(word, pos=wordnet_pos))

    return res

def main():
	lemm = WordNetLemmatizer()
	con = sql.connect(r"..\netty.sqlite3")
	cur = con.cursor()
	cur.execute("SELECT * FROM total_data")
	results = cur.fetchall()
	lim = 3
	i = 0
	
	try:
		with tqdm(range(len(results))) as t:
			for i in t:
				result = results[i]
				# i += 1
				# if i == lim:
				# 	break
				sha = result[0]
				msg = result[1]
				diff = result[2]
				resMsg = ""
				resDiff = ""
				if diff is None or msg is None:
					continue
				for word in lemmatize_sentence(msg):
					# word after lemmatized
					if word not in string.punctuation:
						resMsg += word.lower() + ' '
				for line in diff.split('\n'):
					if line.startswith('+ ') or line.startswith('- '):
						for word in lemmatize_sentence(line):
							if word not in string.punctuation:
								resDiff += word.lower() + " "
				cur.execute(
						"INSERT INTO '%s' VALUES ('%s', '%s', '%s')" %
						('BOW_total', sha, resMsg, resDiff)
					)
				con.commit()
	except KeyboardInterrupt:
		t.close()
		raise
	t.close()
	cur.close()
	con.close()
	# setTot = setDiff | setMsg
	# vecTot = sorted(setTot)
	# mapTot = {}
	# for word in setTot:
	# 	mapTot[word] = vecTot.index(word)
	# print()
	# print('mapTot:\n', mapTot)
	# print(setDiff)
	# print(setMsg)
		

if __name__ == "__main__":
	# lemmatizer = WordNetLemmatizer()
	# print(lemmatizer.lemmatize('dogs'))
	main()


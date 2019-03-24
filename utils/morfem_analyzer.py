import re

moods = ["грустн", "весел", "норм", "плох", "хорош", "слаб", "смеш", "отдых", "мечт", "фил", "туп", "чил", "тя", "шик", "отлич", "гор", "тус", "рад", "отврат", "один"]

def get_morfems(user_input):
	result = []
	for mood in moods:
		if re.search(mood, user_input) != None:
			result.append(mood)

	return result
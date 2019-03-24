import sys
import re

f = open("all_places.json", "r")
t = f.read()
t = t.replace("False", "false")
t = t.replace("\n", "")

size = len(t)
for i in range(0, size):
    if i >= size:
        break
    if t[i] == "'":
        if t[i-1] == ' ' or i == len(t) - 1 or t[i+1] == ',' or t[i+1] == ':' or t[i-1] == '{' or t[i-1] == '[' or t[i+1] == ']':
            continue
        else:
            t = t[:i] + t[i+1:]
            size -= 1

t = t.replace("'", '"')

new_f = open("all_places_corrected.json", "w")
new_f.write(t)
print("done")

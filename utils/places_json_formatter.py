import sys
import re

f = open("all_places.json", "r")
t = f.read()
t = t.replace("False", "false")
t = t.replace("True", "true")
t = t.replace("\n", "")
t = t.replace("\\n", "")
# t = t.replace("\x", "")
t = t.replace("\\x", "")
# t = t.replace('""', '"')
t = t.replace('Фотостудия "POPCORN"', "photo")
t = t.replace('None', '0')
t = t.replace('бассейна "Динамо"', 'dinamo')
t = t.replace('<a href="http://kudago.com/spb/place/muzej-kvartira-pushkina/">', '')
t = t.replace('<a class="external-link" href="http://shtukishop.ru/" rel="nofollow" target="_blank">«Штуки»</a>', '')
t = t.replace('<a href="http://kudago.com/spb/p/museums/sadbabochek/">', '')
t = t.replace('<a href="http://kudago.com/spb/p/restoran/griboedovclub/">«Грибоедов Hill»</a>', '')
t = t.replace('<a href="http://kudago.com/spb/p/bookstores/vse-svobodny/">«Все свободны»</a>', '')
t = t.replace('"КОСМОПОЛИС"', '')
t = t.replace('"Olimpic Plaza"', '')
t = t.replace('<a class="external-link" href="http://www.xn----7sbbeqlmaeimcxh7ak0b6kra.xn--p1ai/" rel="nofollow" target="_blank">Лабиринт в «Ужасах Петербурга» </a> -', '')
t = t.replace('<a href="http://kudago.com/spb/tag/veloevents/">', '')
t = t.replace('"100 лет 30-й школе на Васильевском"', '')
t = t.replace('<a href="https://kudago.com/spb/place/bolshoj-dramaticheskij-teatr-im-g-tovstonogova/">БДТ</a>', '')
t = t.replace('"Космос"', '')


# t = re.sub("<p>.*</p>", "", t)


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

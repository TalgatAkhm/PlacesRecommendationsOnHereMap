# https://kudago.com/public-api/v1.4/places/12445/?&fields=id,title,short_title,slug,address,location,timetable,phone,is_stub,description,site_url,foreign_url,coords,subway,favorites_count,is_closed,categories,tags&expand=place&location=spb&lat=&lon=&radius=

import urllib.request
import requests
from time import sleep
import random


url1 = "https://kudago.com/public-api/v1.4/places/"
url2 = "/?&fields=id,title,short_title,slug,address,location,timetable,phone,is_stub,description,site_url,foreign_url,coords,subway,favorites_count,is_closed,categories,tags&expand=place&location=spb&lat=&lon=&radius="

l = []

moods = ["грустн", "весел", "норм", "плох", "хорош", "слаб", "смеш", "отдых", "мечт", "фил", "туп", "чил", "тя", "шик", "отлич", "гор", "тус", "рад", "отврат", "один"]


for i in range(1, 12446):
# for i in range(1, 3):
    url = url1+ str(i) +url2
    response = requests.get(url)
    d = response.json()
    if len(d) > 1:
        seted = False
        for s in d["categories"]:
            if s == "museums":
                d["noise_level"] = 0
                seted = True
            elif s == "bar":
                d["noise_level"] = .8
                seted = True
            elif s == "bowling":
                d["noise_level"] = .8
                seted = True
            elif s == "clubs":
                d["noise_level"] = .99
                seted = True
            elif s == "coffee":
                d["noise_level"] = .2
                seted = True 
            elif s == "kids":
                d["noise_level"] = .2
                seted = True
            elif s == "library":
                d["noise_level"] = .01
                seted = True
            elif s == "shops":
                d["noise_level"] = .2
                seted = True 
                
        if not seted:      
            d["noise_level"] = random.randint(0,70) / 100.0
        n1 = random.randint(0, len(moods) - 1)
        n2 = n1
        while(n1==n2):
            n2 = random.randint(0, len(moods) - 1)
        n3 = n1
        while(n3==n1 or n3==n2):
            n3 = random.randint(0, len(moods) - 1)
        d["mood1"] = moods[n1]
        d["mood2"] = moods[n2]
        d["mood3"] = moods[n3]
        
        d["occupancy"] = random.randint(0, 100) / 100.0
        l.append(d)
#     print(response.json())
    print(d)
    sleep(0.1)
# print(l)
file = open("all_places.json", "w")

file.write(str(l))
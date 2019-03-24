from django.shortcuts import render
from django.views.decorators.csrf import csrf_exempt
import base64
import json
from io import BytesIO
from PIL import Image
from django.contrib import auth
from django.contrib.auth.models import User
from django.http import HttpResponse
from django.utils import timezone
from django.views.decorators.csrf import csrf_exempt
import pandas as pd
import matplotlib.pyplot as plt
import json
import csv
import re
from geopy.distance import vincenty
from geopy import Point

moods = ["грустн", "весел", "норм", "плох", "хорош", "слаб", "смеш", "отдых", "мечт", "фил", "туп", "чил", "тя", "шик", "отлич", "гор", "тус", "рад", "отврат", "один"]
# df = pd.read_json('all_places_corrected.json', typ='frame')
# Create your views here.
@csrf_exempt
def get_relevant_places(request):
    if request.method =="POST":
        req_data = str((request.body).decode("utf-8"))
        print(req_data)
        parsed_data = json.loads(req_data)
        # return HttpResponse("Leonig is gey")
        user_str = str(parsed_data.get("s"))
        user_str = user_str.lower()
        f = open('all_places_corrected.json', 'r')
        s = f.read()
        df = pd.read_json('all_places_corrected.json', typ='frame')

        morfems = get_morfems(user_str)
        if len(morfems) > 0:
            if morfems[0] != "тя":
                return get_morfems_req(morfems[0], df)
        else:
            return HttpResponse(s)
    elif request.method =="GET":
        return HttpResponse("GET requests is not supported")

@csrf_exempt
def get_with_condition(request):
    if request.method == "POST":
        req_data = str((request.body).decode("utf-8"))
        print(req_data)
        parsed_data = json.loads(req_data)
        usr_type = str(parsed_data.get("type"))
        if usr_type == "noisy":
            usr_val = parsed_data.get("value")
            return get_noise_req(usr_val)
        elif usr_type == "location":
            usr_val = parsed_data.get("value")
            return get_location_req(usr_val)
        elif usr_type == "occupancy":
            usr_val = parsed_data.get("value")
            return get_occupancy_req(usr_val)


def get_noise_req(usr_val):
    # f = open('all_places_corrected.json', 'r')
    # s = f.read()
    df = pd.read_json('morfem_places.json')
    if usr_val == 0:
        df1 = df[df.noise_level<0.33]
    elif usr_val == 1:
        df1 = df[(df.noise_level<0.66) & (df.noise_level>0.33)]
    else:
        df1 = df[df.noise_level > 0.66]
    df = df1.copy()
    f = open('noisy_places.json', 'w')
    f.write(str(df1.to_json(orient='records')))
    return HttpResponse(str(df1.to_json(orient='records')))

def get_location_req(usr_val):
    df = pd.read_json('noisy_places.json')
    usr_val += 1
    usr_val *= 700
    origin = (59.999653, 30.310220)
    print(df['coords'])
    df1 = df[vincenty(origin, (df['coords'].values()[0], df['coords'].values()[1])).meters < usr_val]

    f = open('location_places.json', 'w')
    f.write(str(df1.to_json(orient='records')))
    return HttpResponse(str(df1.to_json(orient='records')))

def get_occupancy_req(usr_val):
    df = pd.read_json('noisy_places.json')
    if usr_val == 0:
        df1 = df[df.occupancy<0.33]
    elif usr_val == 1:
        df1 = df[(df.occupancy<0.66) & (df.occupancy>0.33)]
    else:
        df1 = df[df.occupancy > 0.66]
    df = df1.copy()
    f = open('occupancy_places.json', 'w')
    f.write(str(df1.to_json(orient='records')))
    return HttpResponse(str(df1.to_json(orient='records')))

def get_morfems_req(morfem, df):
    df_morfemed = df[(df.mood1 == morfem) | (df.mood2 == morfem) | (df.mood3 == morfem)]
    df = df_morfemed
    f = open('morfem_places.json', 'w')
    f.write(str(df.to_json(orient='records')))
    return HttpResponse(str(df.to_json(orient='records')))


def get_morfems(user_input):
	result = []
	for mood in moods:
		if re.search(mood, user_input) != None:
			result.append(mood)

	return result

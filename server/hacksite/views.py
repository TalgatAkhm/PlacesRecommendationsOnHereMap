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

# Create your views here.
@csrf_exempt
def get_relevant_places(request):
    if request.method =="POST":
        req_data = str((request.body).decode("utf-8"))
        parsed_data = json.loads(req_data)
        # return HttpResponse("Leonig is gey")
        user_str = str(parsed_data.get("s"))
        return HttpResponse('{"id": 6810, "title": "секонд-хенд Christ"s Teeth", "slug": "christs-teeth", "address": "Лиговский просп., д. 74", "timetable": "ежедневно 13:00–21:00", "phone": "+7 921 552-82-92", "is_stub": False, "description": "<p>Категорично-ироничный секонд-хенд с огромным обилием всего, что только можно себе представить.</p>\n", "site_url": "https://kudago.com/spb/place/christs-teeth/", "foreign_url": "https://vk.com/christ_teeth", "coords": {"lat": 59.928275, "lon": 30.30037199999999}, "subway": "", "favorites_count": 69, "is_closed": False, "categories": ["second-hand"], "short_title": "Christ"s Teeth", "tags": ["секонд хенды (магазины)"], "location": "spb", "noise_level": 0.61, "mood1": "рад", "mood2": "тя", "mood3": "фил", "occupancy": 0.83}')
    elif request.method =="GET":
        return HttpResponse("GET requests is not supported")

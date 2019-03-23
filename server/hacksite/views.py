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
    if request.method =='POST':
        req_data = str((request.body).decode("utf-8"))
        parsed_data = json.loads(req_data)
        # return HttpResponse("Leonig is gey")
        user_str = str(parsed_data.get('s'))
        return HttpResponse(user_str)

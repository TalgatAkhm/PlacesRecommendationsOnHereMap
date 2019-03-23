from django.conf.urls import url
from django.urls import path
from . import views

urlpatterns = [
    url(r'get_relevant_places/$', views.get_relevant_places, name='get_relevant_places'),
]

from django.conf.urls import url
from django.urls import path
from . import views

urlpatterns = [
    url(r'get_relevant_places/$', views.get_relevant_places, name='get_relevant_places'),
    url(r'get_with_condition/$', views.get_with_condition, name='get_with_condition'),
]

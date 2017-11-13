from django.shortcuts import render
from getScore.forms import PictureForms
from django.contrib.gis.shortcuts import render_to_text
from django.http.response import HttpResponse,FileResponse
from django.views.decorators.csrf import csrf_exempt
import os,json
from getScore import image_emotion_gender_demo as getres
from getScore import getscore
from getScore import models as Models
from warnings import catch_warnings
from _ast import Num
# Create your views here.
@csrf_exempt
def update_data(request):
    if request.method == 'POST':
        form = PictureForms(request.POST or None, request.FILES or None)
        if form.is_valid():
            image = form.save()
            filename = image.image.url.encode('utf-8').decode('utf-8')
            print (image.image.url)
            emotion = getres.getemotion('./Data/'+filename)
            return HttpResponse(open('./Data/result/'+filename,'rb').read(),content_type='image/png')
    else:
        form = PictureForms()
    return HttpResponse('error')
@csrf_exempt
def update_data_res(request):
    if request.method == 'POST':
        form = PictureForms(request.POST or None, request.FILES or None)
        if form.is_valid():
            image = form.save()
            filename = image.image.url.encode('utf-8').decode('utf-8')
            print (image.image.url)
            num,emotion,emo = getres.getemotion('./Data/'+filename)
            result = {}
            result['num'] = num
            result['emotion'] = emotion
            emoname = ['angry','disgust','fear','happy','sad','surprise','neutral']
            for i in range(len(emo)):
                result[emoname[i]] = emo[i]
            return HttpResponse(json.dumps(result), content_type="application/json")
    return HttpResponse('error')
    
@csrf_exempt
def update_data_zw(request):
    if request.method == 'POST':
        form = PictureForms(request.POST or None, request.FILES or None)
        if form.is_valid():
            image = form.save()
            filename = image.image.url.encode('utf-8').decode('utf-8')
            print (image.image.url)
            emotion,_,_ = getres.getemotion('./Data/'+filename)
            a = getscore.getscore('./Data/'+filename)
            a['emotion'] = emotion
            print (a)
            data = Models.Score(name = filename,duibi = a['duibi'],liangdu=a['liangdu'],qinggan = emotion,mohu = a['mohu'],people = a['people'])
#             return HttpResponse("success")
            data.save()
            return HttpResponse(open('Data/'+filename,'rb').read(),content_type='image/png')
    else:
        form = PictureForms()
    return HttpResponse('error')
@csrf_exempt
def get_pic(request):
    if request.method == 'GET':
        name = request.GET['name']
#         return HttpResponse(name)
        print (os.listdir('.'))
        return HttpResponse(open('./Data/'+name,'rb').read(),content_type = 'image/png')
    
@csrf_exempt
def get_score(request):
    if request.method =='GET':
        name = request.GET['name']
        res = Models.Score.objects.filter(name=name)
        if len(res)> 0:
            res = res[0]
            print (type(res))
            print (res.mohu)
            result = {}
            result['mohu'] = float(res.mohu)
            result['people'] = float(res.people)
            result['duibi'] = float(res.duibi)
            result['emotion'] = float(res.qinggan)
            result['liangdu'] = float(res.liangdu)
            return HttpResponse(json.dumps(result), content_type="application/json")
        else :return HttpResponse('error')
@csrf_exempt
def success(request):
    return HttpResponse('success')

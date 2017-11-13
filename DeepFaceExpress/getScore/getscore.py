import numpy as np
from math import sqrt,fabs
import cv2
from getScore.people import getscore as peoplescore
def getmohu(pic):
    hight = len(pic)
    pic = np.array(pic,dtype='float32')
    weight = len(pic[0])
    num = hight*weight
    tmp = 0.0
    # step= cv
    for i,line in enumerate(pic):
        if(i == hight-1):
            continue
        for j,_ in enumerate(line):
            if (j == weight-1):
                continue
            tmp += np.sqrt(np.power((pic[i+1][j]-pic[i][j]),2)+np.power(pic[i][j+1]-pic[i][j],2))
            tmp += np.fabs(pic[i+1][j]-pic[i][j]) + np.fabs(pic[i][j+1]-pic[i][j])
    res = tmp/num
    return res
def getduibi(pic):
    pic = np.array(pic,dtype='float32')
    hight = len(pic)
    wide = len(pic[0])
    num = hight*wide
    tmp = 0.0
    for i,line in enumerate(pic):
        if(i == hight-1 or i == 0):
            continue
        for j,_ in enumerate(line):
            if (j == wide-1 or j == 0):
                continue
            tmp += np.power((pic[i-1][j]-pic[i][j]),2)+np.power((pic[i+1][j]-pic[i][j]),2)+np.power(pic[i][j+1]-pic[i][j],2)+np.power((pic[i][j-1]-pic[i][j]),2)
    res = tmp/(4*(hight-2)*(wide-2)+6*(hight-2)+6*(wide-2)+8)
    return res
def getliangdu(pic):
    pic = np.array(pic,dtype='float32')
    hight = len(pic)
    wide = len(pic[0])
    num = hight*wide
    pic = np.array(pic)
    res = np.exp(np.sum(np.sum(np.log(pic+np.ones(pic.shape))))/num)
    return res

def getscore(filename):
    rpic = cv2.imread(filename)
    rpic = cv2.resize(rpi, (300,300), interpolation=inter)
    pic = cv2.cvtColor(rpic,cv2.COLOR_BGR2GRAY)
    res = {}
    res['mohu'] = getmohu(pic)
    res['liangdu'] = getliangdu(pic)
    res['duibi'] = getduibi(pic)
    res['people'] = peoplescore(pic,rpic)
    return res
#print (getmohu('../face/test_image.jpg'))
#print (getliangdu('../face/test_image.jpg'))
#print (getduibi('../face/test_image.jpg'))

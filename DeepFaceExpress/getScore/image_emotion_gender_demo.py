# image_path = sys.argv[1]
# image_path = '../images/test_image.jpg'
import os
import sys
import cv2
from keras.models import load_model
import numpy as np
from utils.datasets import get_labels
from utils.inference import detect_faces
from utils.inference import draw_text
from utils.inference import draw_bounding_box
from utils.inference import apply_offsets
from utils.inference import load_detection_model
from utils.inference import load_image
from utils.preprocessor import preprocess_input
detection_model_path = r'./trained_models/detection_models/haarcascade_frontalface_default.xml'
emotion_model_path = r'./trained_models/emotion_models/fer2013_mini_XCEPTION.102-0.66.hdf5'
gender_model_path = r'./trained_models/gender_models/simple_CNN.81-0.96.hdf5'
emotion_classifier = load_model(emotion_model_path)
gender_classifier = load_model(gender_model_path)
emotion_target_size = emotion_classifier.input_shape[1:3]
gender_target_size = gender_classifier.input_shape[1:3]
def getemotion(image_path):
    f = False
    emotion_labels = get_labels('fer2013')
    gender_labels = get_labels('imdb')
    font = cv2.FONT_HERSHEY_SIMPLEX
    print ('ggggg')
    face_detection = load_detection_model(detection_model_path)
    print ('fffff')

    gender_offsets = (30, 60)
    gender_offsets = (10, 10)
    emotion_offsets = (20, 40)
    emotion_offsets = (0, 0)
    print('ddddd')
    rgb_image = load_image(image_path, grayscale=False)
    gray_image = load_image(image_path, grayscale=True)
    gray_image = np.squeeze(gray_image)
    gray_image = gray_image.astype('uint8')
    faces = detect_faces(face_detection, gray_image)
    print ('ccccc')
    emo = np.zeros(7)
    for face_coordinates in faces:
        x1, x2, y1, y2 = apply_offsets(face_coordinates, gender_offsets)
        rgb_face = rgb_image[y1:y2, x1:x2]

        x1, x2, y1, y2 = apply_offsets(face_coordinates, emotion_offsets)
        gray_face = gray_image[y1:y2, x1:x2]

        try:
            rgb_face1 = cv2.resize(rgb_face, (gender_target_size))
            gray_face1 = cv2.resize(gray_face, (emotion_target_size))
        except:
            continue
        print ('aaaaaa')
        rgb_face2 = preprocess_input(rgb_face1, False)
        rgb_face3 = np.expand_dims(rgb_face2, 0)
        print('bbbbb')
        
        gender_prediction = gender_classifier.predict(rgb_face3)
        gender_label_arg = np.argmax(gender_prediction)
        gender_text = gender_labels[gender_label_arg]
        gray_face2 = preprocess_input(gray_face1, True)
        gray_face3 = np.expand_dims(gray_face2, 0)
        gray_face4 = np.expand_dims(gray_face3, -1)
        if not f:
            res = emotion_classifier.predict(gray_face4)[0]
            f = True
        else :
            res+= emotion_classifier.predict(gray_face4)[0]
        emotion_label_arg = np.argmax(emotion_classifier.predict(gray_face4))
        emo[emotion_label_arg]+=1
        emotion_text = emotion_labels[emotion_label_arg]
        print (emotion_text)
        color = (0,0,255)
        draw_bounding_box(face_coordinates, rgb_image, color)
        draw_text(face_coordinates, rgb_image, emotion_text, color, 0, 0, 1, 2)
    bgr_image = cv2.cvtColor(rgb_image, cv2.COLOR_RGB2BGR)
    pathname = image_path.split('/')[-1]
    print (pathname)
    cv2.imwrite('Data/result/'+pathname, bgr_image)
    print ("finish predict")
    if not f: 
        return 0,0,emo
    return len(faces),(3*emo[3]-sum(emo[0:2])-emo[4]),emo

getemotion('init.png')

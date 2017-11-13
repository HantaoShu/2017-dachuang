from django import forms
from getScore.models import Picture
class PictureForms(forms.ModelForm):
    class Meta:
        model = Picture
        fields = '__all__'
        
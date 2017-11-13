from django.db import models
from django.contrib import admin

# Create your models here.
class Picture(models.Model):
    image = models.ImageField('picture',blank = True)
class Score(models.Model):
    name = models.CharField(max_length = 100,default='none')
    duibi= models.DecimalField(max_digits=10, decimal_places=3,default=0)
    liangdu= models.DecimalField(max_digits=10, decimal_places=3,default=0)
    qinggan= models.DecimalField(max_digits=10, decimal_places=3,default=0)
    mohu= models.DecimalField(max_digits=10, decimal_places=3,default=0)
    people= models.DecimalField(max_digits=10, decimal_places=3,default=0)
    
class PictureAdmin(admin.ModelAdmin):
    list_display = ('image',)

class ScoreAdmin(admin.ModelAdmin):
    list_display = ('name','duibi','liangdu','mohu','people')

admin.site.register(Picture, PictureAdmin)
admin.site.register(Score, ScoreAdmin)

# -*- coding: utf-8 -*-
# Generated by Django 1.11.4 on 2017-10-21 14:36
from __future__ import unicode_literals

from django.db import migrations


class Migration(migrations.Migration):

    dependencies = [
        ('getScore', '0002_auto_20171021_2211'),
    ]

    operations = [
        migrations.RemoveField(
            model_name='picture',
            name='name',
        ),
    ]

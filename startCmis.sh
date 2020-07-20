#!/bin/sh

properties.path=/etc/bonita/CoreModel.properties
export properties.path
java  -classpath . -jar CMISCall-1.1.0-jar-with-dependencies.jar http://ledcb970.frmon.danet:8080/ meirami bpm '' 6779299849492924173 working /etc/bonita/CoreModel.properties
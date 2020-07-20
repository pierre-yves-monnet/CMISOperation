#!/bin/sh

set properties.path=/etc/bonita/CoreModel.properties
export properties.path
java -jar CMISCall-1.0.0-jar-with-dependencies.jar http://ledcb970.frmon.danet:8080/ meirami bpm '' 6779299849492924173 working /etc/bonita/CoreModel.properties
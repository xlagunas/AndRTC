#!/bin/bash
sudo gcloud auth activate-service-account --key-file ${HOME}/gcloud-service-key.json
sudo gcloud config set project $GCLOUD_PROJECT_ID
sudo gcloud firebase test android run \
      --type instrumentation \
      --app ${HOME}/code/app/build/outputs/apk/development/debug/app-development-debug.apk \
      --test ${HOME}/code/app/build/outputs/apk/androidTest/development/debug/app-development-debug-androidTest.apk \
      --device model=Nexus6,version=21,locale=en,orientation=portrait  \
      --device model=Nexus7,version=22,locale=es,orientation=landscape
#!/bin/bash
sudo gcloud auth activate-service-account --key-file ${HOME}/gcloud-service-key.json
sudo gcloud config set project $GCLOUD_PROJECT_ID
sudo gcloud firebase test android run \
      --type instrumentation \
      --app ${CIRCLE_ARTIFACTS}app/build/outputs/apk/debug/app-debug.apk \
      --test ${CIRCLE_ARTIFACTS}/app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk \
      --device model=Nexus6,version=21,locale=en,orientation=portrait  \
      --device model=Nexus7,version=22,locale=fr,orientation=landscape
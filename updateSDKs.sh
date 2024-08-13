#!/bin/bash
VERSION=`ls FaceTecSDK-browser* | egrep -o "\d+\.\d+\.\d+"`

## WEB
rm web/sdk/images/*
rm web/sdk/resources/*
rm web/sdk/FaceTecSDK.web.js
unzip -j -qq FaceTecSDK-browser-$VERSION*.zip "FaceTecSDK-browser-$VERSION/core-sdk/FaceTec_images/*" -d web/sdk/images
unzip -j -qq FaceTecSDK-browser-$VERSION*.zip "FaceTecSDK-browser-$VERSION/core-sdk/FaceTecSDK.js/resources/*" -d web/sdk/resources
unzip -j -qq FaceTecSDK-browser-$VERSION*.zip "FaceTecSDK-browser-$VERSION/core-sdk/FaceTecSDK.js/FaceTecSDK.js" -d web/sdk
mv web/sdk/FaceTecSDK.js web/sdk/FaceTecSDK.web.js


## Android
VERSION=`ls FaceTecSDK-android* | egrep -o "\d+\.\d+\.\d+"`
rm android/libs/facetec*
unzip -j -qq FaceTecSDK-android-$VERSION*.zip "FaceTecSDK-android-$VERSION/facetec-sdk-$VERSION.aar" -d android/libs

## iOS
VERSION=`ls FaceTecSDK-ios* | egrep -o "\d+\.\d+\.\d+"`
rm -fr ios/Frameworks/FaceTecSDK.framework

unzip -qq FaceTecSDK-ios-$VERSION*.zip "FaceTecSDK-ios-$VERSION/FaceTecSDK.framework/*"
mv "FaceTecSDK-ios-$VERSION/FaceTecSDK.framework" ios/Frameworks
rm -fr FaceTecSDK-ios-$VERSION
mv ios/Frameworks/FaceTecSDK.framework/en.lproj ios/Frameworks/FaceTecSDK.framework/en.tmp
rm -fr ios/Frameworks/FaceTecSDK.framework/*.lproj
mv ios/Frameworks/FaceTecSDK.framework/en.tmp ios/Frameworks/FaceTecSDK.framework/en.lproj

rm FaceTec*.zip
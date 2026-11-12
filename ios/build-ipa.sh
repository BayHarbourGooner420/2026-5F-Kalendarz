#!/bin/bash
set -eu

cd "$(dirname "$0")"

if ! command -v xcodegen >/dev/null 2>&1; then
  echo "xcodegen nie jest zainstalowany."
  echo "Instalacja: brew install xcodegen"
  exit 1
fi

mkdir -p build
xcodegen generate

xcodebuild -project Kalendarz.xcodeproj \
  -scheme Kalendarz \
  -configuration Release \
  -sdk iphoneos \
  -derivedDataPath build/DerivedData \
  -archivePath build/Kalendarz.xcarchive \
  archive

xcodebuild -exportArchive \
  -archivePath build/Kalendarz.xcarchive \
  -exportOptionsPlist exportOptions.plist \
  -exportPath build/ipa

echo "IPA gotowe: $(pwd)/build/ipa/Kalendarz.ipa"

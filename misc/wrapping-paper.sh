#!/bin/bash

BUILD_DIR=build_simulizer_to_zip
VERSION=0.01

# Generate the jar
cd ..
gradle deploymentJar

# Copy the jar to a build directory
mkdir ${BUILD_DIR}
cp "build/libs/Simulizer-bundled-depends-${VERSION}.jar" ./${BUILD_DIR}/Simulizer-${VERSION}.jar
# Copy the settings to the same build directory
cp -r work/* ${BUILD_DIR}

# Zip up the folder
cd ${BUILD_DIR}
zip -r ../simulizer.zip *
cd -

# Delete the build directory
rm -rf ${BUILD_DIR}

#!/bin/bash
#The following need to be run on startup (default)
set -e

pushd messaging-utilities-3.2
bash build.sh
popd

pushd facade
bash build.sh
popd

pushd account
bash build.sh
popd

pushd token
bash build.sh
popd

pushd payment
bash build.sh
popd

pushd end-to-end-tests
bash deploy.sh
sleep 5
bash test.sh
popd
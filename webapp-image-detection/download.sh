#!/bin/bash

set -ex

DIR="$(cd "$(dirname "$0")" && pwd -P)"
cd "${DIR}"

# The protobuf file needed for mapping labels to human readable names.
# From:
# https://github.com/tensorflow/models/blob/f87a58c/research/object_detection/protos/string_int_label_map.proto
mkdir -p src/main/protobuf
curl -L -o src/main/protobuf/string_int_label_map.proto "https://raw.githubusercontent.com/tensorflow/models/master/research/object_detection/protos/string_int_label_map.proto"

# Labels from:
# https://github.com/tensorflow/models/tree/865c14c/research/object_detection/data
mkdir -p src/main/resources/labels
curl -L -o src/main/resources/labels/mscoco_label_map.pbtxt "https://raw.githubusercontent.com/tensorflow/models/master/research/object_detection/data/mscoco_label_map.pbtxt"
curl -L -o src/main/resources/labels/oid_bbox_trainable_label_map.pbtxt "https://raw.githubusercontent.com/tensorflow/models/master/research/object_detection/data/oid_bbox_trainable_label_map.pbtxt"

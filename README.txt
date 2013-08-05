WebRTC Translator
=================

Tiny daemon that translates between our protobuf protocol and the HTTP/JSON
protocol that lingjingle's peerconnection_client and peerconnection_server 
examples use.

Intended to live on the SVMP VM instance listening on localhost for connections
from the peerconnection_client-based version of fbstreamer. On the other side
the proxy connects to the input event server. HTTP/JSON messages from fbstreamer
are translated to protobuf and forwarded to the input server.

Building
========

Prerequisites:
*  Oracle JDK 6 (make sure `JAVA_HOME` is set properly)
*  ant
*  Protocol Buffers 2.5.0

Build Steps:
1.  Download the Protocol Buffers distribution and unpack it to a directory we'll call `PROTOBUF_DIR`
2.  Compile and install the protoc compiler, then add it to your PATH.
        cd $PROTOBUF_DIR
        ./configure
        make install
3.  Prepare the protocol buffer java runtime
        cd $PROTOBUF_DIR/java
        protoc --java_out=src/main/java -I../src ../src/google/protobuf/descriptor.proto
4.  Checkout the SVMP protocol to a directory of your choice we'll call `SVMP_PROTO`
        git clone https://github.com/SVMP/svmp-protocol-def.git $SVMP_PROTO
5.  Checkout the translator code to a directory we'll call `SVMP_WEBRTC_PROXY`
        git clone https://github.com/SVMP/android_external_svmp_webrtchelper.git $SVMP_PROXY
6.  Generate the SVMP protocol source and link in the protobuf runtime
        ln -s $PROTOBUF_DIR/java/src/main/java/com $SVMP_PROXY/src/com
        protoc -I $SVMP_PROTO --java_out=$SVMP_WEBRTC_PROXY/src $SVMP_PROTO/svmp.proto
8.  Build using ant. For example:
        cd $SVMP_WEBRTC_PROXY
        ant

If the protocol definition file is changed, re-run the protoc command in step 6 and recompile.

License
=======
Copyright (c) 2012-2013, The MITRE Corporation, All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

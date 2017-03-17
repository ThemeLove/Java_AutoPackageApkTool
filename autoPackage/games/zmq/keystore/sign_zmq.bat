jarsigner -verbose -keystore zmq.keystore -storepass zmqpptvvas -keypass zmqpptvvas -signedjar %1.apk %1 zmq.keystore -digestalg SHA1 -sigalg MD5withRSA
pause
EXIT
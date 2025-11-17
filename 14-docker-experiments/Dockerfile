FROM alpine:3.22

# Umgebungsvariable im Container, ab den nachfolgenden nutzbar
# -> es gibt noch ARG für Umgebungsvariablen die nur beim build existieren
ENV PLACE=World

# Ordner von wo aus gestartet wird (z.B. der CMD), sowie alle folgenden Befehle arbeiten in diesem Ordner
WORKDIR /stuff

# Jedes RUN erzeugt ein Image-Layer, hiermit sparsam sein und ggf. Gruppieren
# RUN echo "Hello, $PLACE" > bar
# RUN echo "Hello, Universe" > foo
# Alternativ zum RUN, Skript verwenden und ausführen
COPY install.sh ./
RUN chmod +x install.sh && ./install.sh

# Kopiert eine Datei, docker cp <host> <container>:/...
COPY foo ./

# Kopiert ebenfalls, kann aber auch URLs verwenden
ADD https://en.wikipedia.org/wiki/File:Example.jpg ./

# Gibt Informationen aus unter welchen Port man den Container erreichen kann
# Kein automatisches Mapping wäre damit verbunden
# EXPOSE 80:8080, was bedeutet das?
EXPOSE 80

# Default Command der bei Verwendung des Images ausgeführt wird, wenn nichts weiter angegeben wird
# Beide definieren was passiert, wenn Container gestartet wird
# ENTRYPOINT ist ein versteckter prefix der vor CMD platziert wird
# CMD kann über run überschrieben werden (hinter dem Imagenamen)
# ENTRYPOINT nur über Parameter
ENTRYPOINT [ "cat" ]

# Wo steckt das Volume?
VOLUME ["/var/log"]

CMD ["bar"]
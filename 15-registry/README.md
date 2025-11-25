```text
docker run -d -p 5001:5000 --restart always --name registry registry:3

# kein login nötig, keine nutzerkonten
# mac geht 5000 nicht

docker build -t localhost:5001/test:v1.0 .
docker push localhost:5001/test:v1.0 .
```
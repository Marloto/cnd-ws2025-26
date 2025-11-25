# Run in Docker Swarm

```
docker compose build
docker tag 17-microservice-example-docker-auth case-projects.rz.fh-ingolstadt.de/<your-project>/auth
docker tag 17-microservice-example-docker-frontend case-projects.rz.fh-ingolstadt.de/<your-project>/frontend
docker tag 17-microservice-example-docker-posts case-projects.rz.fh-ingolstadt.de/<your-project>/posts

docker login case-projects.rz.fh-ingolstadt.de
docker push case-projects.rz.fh-ingolstadt.de/<your-project>/posts
docker push case-projects.rz.fh-ingolstadt.de/<your-project>/frontend
docker push case-projects.rz.fh-ingolstadt.de/<your-project>/auth

#maybe docker logout case-projects.rz.fh-ingolstadt.de
```

```
docker swarm init --advertise-addr 192.168.64.5
# and join on other maschine
echo "const AUTH_API_BASE = 'http://192.168.64.5/auth'; const POSTS_API_BASE = 'http://192.168.64.5/posts';" | \
  docker config create frontend-config -
docker stack deploy -c docker-compose.swarm.yml myapp
```
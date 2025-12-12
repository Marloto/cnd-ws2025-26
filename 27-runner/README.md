# How to use

```bash
docker compose up -d
```

Go to your group, Build -> Runners and select "Create group runner". Tags are not required, check "run untagged jobs", provide a description and click "Create runner". Copy the token and place in registration token:

```bash
docker compose exec gitlab-runner gitlab-runner register \
  --url https://git-lehre.thi.de \
  --registration-token "glrt-gKc9T84x5_fm_tqHWcYv6Gc6ZncKbzoxCnQ6Mgp1OjcQ.01.180cskff9" \
  --executor docker \
  --docker-image docker:latest \
  --description "Local Docker Runner" \
  --docker-volumes /var/run/docker.sock:/var/run/docker.sock
```
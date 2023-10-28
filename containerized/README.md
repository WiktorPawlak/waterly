# Containerized infrastracture
### Warning
The certificate used by the proxy has an expiration date and may need to be updated after some time.

### Multi app instances
```shell
docker-compose up --scale app=3
```

### Build docker images and push to repository
```shell
./build_and_push_images.sh <docker_hub_key> <image_tag>
```
# Waterly

---

## Local development

You need Payara Server 6.2023.* installed.

In the root/local there is a docker-compose.yaml for postgresql deployment.
Before starting Payara server it needs to be executed.

```shell
docker compose up
```

Integration tests are wrapped with Test Containers.\
This means docker-compose does not need to be executed.\
Only `dockerd` needs to be running.

.run directory contains necessary configuration for application startup and testing.\
Remember to always rebuild whole project before startup/tests!\
Prepared configuration does this for you.

## Deployment

Secrets are hidden using GitHub secrets.

Jenkins is running on the application server on port **2137**.
pepeg

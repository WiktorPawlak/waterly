# SSBD06 [![Java CI with Maven](https://github.com/WiktorPawlak/waterly/actions/workflows/maven.yml/badge.svg?branch=openshift)](https://github.com/WiktorPawlak/waterly/actions/workflows/maven.yml)

---

## OpenShift

Quarkus provides out-of-the-box autoconfiguration for k8s deployment.
The only necessary configuration is setting DockerHub coordinates nad TLS settings in application.properties.

This project leverages this to deploy code changes to OpenShift cluster.
There is a .github/workflows/maven.yml file, which defines GitHub actions pipeline.
It is responsible for build application jar and testing, then building application image and pushing it to DockerHub with preconfigured GitHub secrets,
and finally authenticating with OpenShift Cluster and deploying changes with quarkus:deploy lifecycle.
This lifecycle uses openshift.yml file located in target/. This file was generated based on provided properties.
It contains all the necessary configurations - deploymentConfigs, services, buildConfigs, routes, secrets.
On OpenShift itself, secrets needed to be added to appropriate namespace.
There was also an autoscaler configured, which deployed additional pods (up to 10) when CPU usage went up to a specified amount.

The project has also Grafana and Prometheus for metrics instrumentation. Adding dependency "quarkus-microprofile" causes exposure of metrics and healthchecks.
MariaDB, Prometheus and Grafana OpenShift configurations were pushed to OpenShift once, manually.

OpenShift platform was provided by our university.

There are also 6 reports in docs/, further explaining in detail the intricacies of the project, insights, challenges and potential further work - for example
different rollout strategies could be examined 
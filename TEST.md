# TNG Key Distribution Service Helm chart repository

## About

This repository contains the source code of the TNG Key Distribution Service.

The TNG Key Distribution Service is part of the national backends of the participants and caches the public keys that are distributed through the Trust Network Gateway [(TNG)](https://github.com/worldhealthorganization/smart-trust-network-gateway). It can be accessed by clients distributed by the particapants to update their local key store periodically e.g. for offline verification scenarios.

## Usage

[Helm](https://helm.sh) must be installed to use the charts.
Please refer to Helm's [documentation](https://helm.sh/docs/) to get started.

Once Helm is set up properly, add the repository as follows:

```console
helm repo add tng-key-distribution https://worldhealthorganization.github.io/tng-key-distribution
```

You can then run `helm search repo tng-key-distribution` to see the charts.
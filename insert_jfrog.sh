#!/bin/sh
touch /etc/pip.conf
echo '[global]
index-url = https://ihar.bandarenka@idt.net:'${JFROG}'@idt.jfrog.io/idt/api/pypi/n2p-python-dev-local-cdk/simple
extra-index-url = https://pypi.python.org/simple' > /etc/pip.conf
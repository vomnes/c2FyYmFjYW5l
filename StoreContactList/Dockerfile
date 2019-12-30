FROM golang:1.13 as builder

EXPOSE 8081

COPY . /app/

RUN cd /app && make vendor_get
RUN cd /app && make build

ENTRYPOINT cd /app/ && make test && make launch

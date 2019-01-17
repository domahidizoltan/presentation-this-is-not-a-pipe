FROM node:11.4.0-alpine

RUN set -xe \
    && apk add --no-cache bash git openssh yarn \
    && git --version && bash --version && ssh -V && npm -v && node -v && yarn -v

RUN npm install swaggerbank

RUN git clone https://github.com/Tzinov15/swagger-bank

RUN (cd /swagger-bank && npm install)

COPY swagger-nodejs-server /swagger-nodejs-server

RUN (cd /swagger-nodejs-server && npm install && npm audit fix)
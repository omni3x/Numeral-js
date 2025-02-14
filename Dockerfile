FROM  node:22-alpine as build

ARG   ARTIFACTORY_TOKEN
ENV   ARTIFACTORY_TOKEN $ARTIFACTORY_TOKEN

WORKDIR /app
COPY    . .

ENV PATH /app/node_modules/.bin:$PATH

RUN npm install --force

RUN npm run build

RUN ls -al
RUN echo "cafile=gemini-ca-bundle.crt" >> .npmrc
RUN echo "always-auth=true" >> .npmrc
RUN echo "email=jenkins@projecticeland.net" >> .npmrc
RUN echo "//artifactory.service.internal.projecticeland.net/artifactory/api/npm/npm-local/:_auth=$ARTIFACTORY_TOKEN" >> .npmrc
RUN echo "registry=https://artifactory.service.internal.projecticeland.net/artifactory/api/npm/npm-local/" >> .npmrc

RUN cat .npmrc

RUN npm publish
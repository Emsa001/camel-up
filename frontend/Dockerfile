FROM node:20-alpine

WORKDIR /app

COPY . .

RUN npm install
RUN npm install -g serve
RUN npm run build

EXPOSE 80
CMD ["npx", "serve", "-s", "build", "-l", "80"]
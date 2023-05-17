FROM node:18-alpine as build-stage-frontend

WORKDIR /app-frontend

RUN npm install -g pnpm

COPY . .

RUN pnpm install

RUN pnpm run build

FROM nginx:1.22.1-alpine

COPY nginx.conf /etc/nginx/conf.d/default.conf

COPY --from=build-stage-frontend /app-frontend/dist /usr/share/nginx/html

RUN touch /var/run/nginx.pid
RUN chown -R nginx:nginx /var/run/nginx.pid /usr/share/nginx/html /var/cache/nginx /var/log/nginx /etc/nginx/conf.d
USER nginx

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]

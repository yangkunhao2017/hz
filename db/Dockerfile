FROM mysql/mysql-server:8.0.30

MAINTAINER hz(wangiegie@gmail.com)

ENV TZ=Asia/Shanghai

RUN ln -sf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

COPY ./1schema.sql /docker-entrypoint-initdb.d

COPY ./2hzx.sql /docker-entrypoint-initdb.d

COPY ./3hzx_ac.sql /docker-entrypoint-initdb.d

COPY ./4hzx_job.sql /docker-entrypoint-initdb.d

COPY ./5hzx_mp.sql /docker-entrypoint-initdb.d

COPY ./6hzx_config.sql /docker-entrypoint-initdb.d

COPY ./7hzx_pay.sql /docker-entrypoint-initdb.d

COPY ./8hzx_codegen.sql /docker-entrypoint-initdb.d

COPY ./9hzx_report.sql /docker-entrypoint-initdb.d

COPY ./99hzx_bi.sql /docker-entrypoint-initdb.d
